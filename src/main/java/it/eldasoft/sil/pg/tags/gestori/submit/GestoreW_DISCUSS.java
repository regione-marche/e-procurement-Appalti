/*
 * Created on 29/03/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit dell'entita' W_DISCUSS
 * 
 */
public class GestoreW_DISCUSS extends AbstractGestoreChiaveNumerica {

  public String getCampoNumericoChiave() {
    return "DISCID";
  }

  public String[] getAltriCampiChiave() {
    return new String[] { "DISCID_P" };
  }

  public String getEntita() {
    return "W_DISCUSS";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    Long discid_p = datiForm.getLong("W_DISCUSS.DISCID_P");
    Long discid = datiForm.getLong("W_DISCUSS.DISCID");

    this.getGeneManager().deleteTabelle(new String[] { "W_DISCDEST", "W_DISCALL", "W_DISCREAD" }, "DISCID_P = ? AND DISCID = ?",
        new Object[] { discid_p, discid });

  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    super.preInsert(status, datiForm);
    datiForm.setValue("W_DISCUSS.DISCMESSINS", new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));

    // Inserimento dello stato letto del messaggio per l'operatore che lo ha
    // creato
    Long discid_p = datiForm.getLong("W_DISCUSS.DISCID_P");
    Long discid = datiForm.getLong("W_DISCUSS.DISCID");
    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long operatore = new Long(profilo.getId());

    DataColumnContainer dccW_DISCREAD = new DataColumnContainer(new DataColumn[] { new DataColumn("W_DISCREAD.DISCID_P", new JdbcParametro(
        JdbcParametro.TIPO_NUMERICO, discid_p)) });
    dccW_DISCREAD.addColumn("W_DISCREAD.DISCID", JdbcParametro.TIPO_NUMERICO, discid);
    dccW_DISCREAD.addColumn("W_DISCREAD.DISCMESSOPE", JdbcParametro.TIPO_NUMERICO, operatore);
    this.inserisci(status, dccW_DISCREAD, new GestoreW_DISCREAD());

    // Inserimento di tutti gli operatori presenti nella G_PERMESSI
    // Tutti gli operatori diventano automaticamente destinatari del nuovo
    // messaggio
    try {
      String codgar = (String) this.sqlManager.getObject("select disckey1 from w_discuss_p where discid_p = ?", new Object[] { discid_p });

      String selectG_PERMESSI = "select syscon from g_permessi where (codgar = ? or codgar = ?) and syscon <> ?";
      List<?> datiG_PERMESSI = this.sqlManager.getListVector(selectG_PERMESSI, new Object[] { codgar, "$" + codgar, operatore });
      for (int p = 0; p < datiG_PERMESSI.size(); p++) {

        Long syscon = (Long) SqlManager.getValueFromVectorParam(datiG_PERMESSI.get(p), 0).getValue();
        String operatoreNome = (String) this.sqlManager.getObject("select sysute from usrsys where syscon = ?", new Object[] { syscon });
        String operatoreEMail = (String) this.sqlManager.getObject("select email from usrsys where syscon = ?", new Object[] { syscon });

        if (operatoreNome != null && !"".equals(operatoreNome.trim()) && operatoreEMail != null && !"".equals(operatoreEMail)) {
          DataColumnContainer dccW_DISCDEST = new DataColumnContainer(new DataColumn[] { new DataColumn("W_DISCDEST.DISCID_P",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, discid_p)) });
          dccW_DISCDEST.addColumn("W_DISCDEST.DISCID", JdbcParametro.TIPO_NUMERICO, discid);
          dccW_DISCDEST.addColumn("W_DISCDEST.DESTNUM", JdbcParametro.TIPO_NUMERICO, new Long(p + 1));
          dccW_DISCDEST.addColumn("W_DISCDEST.DESTID", JdbcParametro.TIPO_NUMERICO, syscon);
          dccW_DISCDEST.addColumn("W_DISCDEST.DESTNAME", JdbcParametro.TIPO_TESTO, operatoreNome);
          dccW_DISCDEST.addColumn("W_DISCDEST.DESTMAIL", JdbcParametro.TIPO_TESTO, operatoreEMail);
          this.inserisci(status, dccW_DISCDEST, new GestoreW_DISCDEST());
        }

      }

    } catch (SQLException e) {

    }
  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
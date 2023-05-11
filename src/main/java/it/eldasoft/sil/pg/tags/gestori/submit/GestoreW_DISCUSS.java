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

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

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

  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
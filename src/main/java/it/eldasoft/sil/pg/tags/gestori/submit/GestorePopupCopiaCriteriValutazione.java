/*
 * Created on 28/04/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.CopiaCriteriManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore non standard che effettua la copia dei criteri
 * dal lotto sorgente ai lotti selezionati nella popup
 * gare-popup-copia-criteriValutazione.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupCopiaCriteriValutazione extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestorePopupCopiaCriteriValutazione() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupCopiaCriteriValutazione(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {


  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

    CopiaCriteriManager copiaCriteriManager = (CopiaCriteriManager) UtilitySpring.getBean("copiaCriteriManager",
        this.getServletContext(), CopiaCriteriManager.class);

    String  lottoSorgente = UtilityStruts.getParametroString(this.getRequest(),"lottoSorgente");
    String  codiceGara = UtilityStruts.getParametroString(this.getRequest(),"codiceGara");
    String[] listaLottiSelezionati = this.getRequest().getParameterValues("keys");

    this.getRequest().setAttribute("codiceGara", codiceGara);
    this.getRequest().setAttribute("lottoSorgente", lottoSorgente);

    try {

      //Lista criteri da copiare
      List listaCriteri = sqlManager.getListVector(
          "select G.NECVAN,G.NORPAR,G.TIPPAR,G.MAXPUN,G.DESPAR,G.LIVPAR,G.NECVAN1," +
          "G.NORPAR1,G.MINPUN, G1.ID, G1.DESCRI,G1.MAXPUN,G1.FORMATO,G1.MODPUNTI,G1.MODMANU,G.ISNOPRZ,G1.NUMDECI,G1.FORMULA,G.TIPCAL, G.SEZTEC, G1.ESPONENTE from GOEV G LEFT JOIN G1CRIDEF G1 "
            + "ON G.NGARA = G1.NGARA and G.NECVAN = G1.NECVAN where G.NGARA = ? ORDER BY G.NECVAN, G1.ID",new Object[] { lottoSorgente });


      Vector datiGare1 = sqlManager.getVector("select mintec, mineco, riptec, ripeco, ripcritec, ripcrieco from gare1 where ngara = ?", new Object[]{lottoSorgente});
      Double mintec = null;
      Double mineco = null;
      Long riptec = null;
      Long ripeco=null;
      Long ripcritec = null;
      Long ripcrieco = null;
      if(datiGare1!=null && datiGare1.size()>0){
        mintec = (Double)((JdbcParametro) datiGare1.get(0)).getValue();
        mineco = (Double)((JdbcParametro) datiGare1.get(1)).getValue();
        riptec = (Long)((JdbcParametro) datiGare1.get(2)).getValue();
        ripeco = (Long)((JdbcParametro) datiGare1.get(3)).getValue();
        ripcritec = (Long)((JdbcParametro) datiGare1.get(4)).getValue();
        ripcrieco = (Long)((JdbcParametro) datiGare1.get(5)).getValue();
      }

      for (int i = 0; i < listaLottiSelezionati.length; i++) {
        int pos = listaLottiSelezionati[i].indexOf(':') + 1;
        String codiceLottoSelezionato = listaLottiSelezionati[i].substring(pos);
        //Si devono cancellare gli eventuali crrteri presenti nel lotto di destinazione

        //Cancellazione delle figlie di goev
        this.getSqlManager().update(
            "delete from DPUN where NGARA = ? and NECVAN in (select necvan from goev where NGARA = ?)",
            new Object[] { codiceLottoSelezionato,codiceLottoSelezionato });

        this.getSqlManager().update(
            "delete from GOEV where NGARA = ?",
            new Object[] { codiceLottoSelezionato });

        //Aggiornamento di GARE1 con mintec, mineco ed i valori relativi alla riparametrazione
          this.getSqlManager().update("update gare1 set mintec=?, mineco=?, riptec=?, ripeco=?, ripcritec=?, ripcrieco=? where ngara=?", new Object[] {mintec, mineco, riptec, ripeco, ripcritec, ripcrieco, codiceLottoSelezionato});

          copiaCriteriManager.copiaCriteri(codiceGara, listaCriteri, codiceLottoSelezionato, "lotto");

      }

      this.getRequest().setAttribute("RISULTATO", "COPIAESEGUITA");
    }  catch (SQLException e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw new GestoreException("Errore nella copia dei criteri di valutazione", null, e);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

   @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

}
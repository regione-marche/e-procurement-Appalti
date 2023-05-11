/*
 * Created on: 30-mar-2016
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;


import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

/**
 * Gestore di submit dell'entita' CATPUB
 * 
 * @author Francesco.DiMattei
 */
public class GestoreCATPUB extends AbstractGestoreChiaveNumerica {

  public String getCampoNumericoChiave() {
    return "CODTAB";
  }

  public String[] getAltriCampiChiave() {
    return null;
  }
  
  public String getEntita() {
    return "CATPUB";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
      Long codtab = datiForm.getLong("CATPUB.CODTAB");
      try {
        this.sqlManager.update("DELETE FROM TABPUB WHERE CODTAB=? ", new Object[] {codtab});
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'eliminazione delle figlie" +
            " della pubblicazione " + codtab, null, e);
      }
  }
  
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }
  
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
      super.preInsert(status, datiForm);
      try {
        // Gestione delle sezioni 'Pubblicazioni'
            AbstractGestoreChiaveNumerica gestoreTABPUB = new DefaultGestoreEntitaChiaveNumerica(
                "TABPUB", "CODPUB", new String[] { "CODTAB" }, this.getRequest());
            this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
                gestoreTABPUB, "TABPUB",
                new DataColumn[] { datiForm.getColumn("CATPUB.CODTAB") }, null);
      } catch (Exception e) {
        throw new GestoreException("Errore nell'inserimento" +
            " della pubblicazione ", null, e);
      }
  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
      try {
        // Gestione delle sezioni 'Pubblicazioni'
            AbstractGestoreChiaveNumerica gestoreTABPUB = new DefaultGestoreEntitaChiaveNumerica(
                "TABPUB", "CODPUB", new String[] { "CODTAB" }, this.getRequest());
            this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
                gestoreTABPUB, "TABPUB",
                new DataColumn[] { datiForm.getColumn("CATPUB.CODTAB") }, null);
      } catch (Exception e) {
        throw new GestoreException("Errore nell'aggiornamento" +
            " della pubblicazione ", null, e);
      }
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
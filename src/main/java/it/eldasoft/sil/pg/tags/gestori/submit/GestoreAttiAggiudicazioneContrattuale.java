/*
 * Created on 02-dic-2009
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
import java.sql.Timestamp;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per update dei dati della prima fase della pagina Atti aggiudicazione e contrattuale.
 * La pagina anche se contiene dati di GARE è definità sull'entità TORN
 * perchè altrimenti al salvataggio si perderebbe la chiave di TORN
 * 
 * @author Marcello caminiti
 */
public class GestoreAttiAggiudicazioneContrattuale extends AbstractGestoreEntita {

  public String getEntita() {
    return "TORN";
  }
  
  public GestoreAttiAggiudicazioneContrattuale() {
    super(false);
  }

	  
  public GestoreAttiAggiudicazioneContrattuale(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }
  
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    DefaultGestoreEntita gestoreGARE = new DefaultGestoreEntita("GARE",
        this.getRequest());
    
    // Aggiornamento dei campi di gare  
    gestoreGARE.update(status, datiForm);
    
    //Aggiornamento del campo DATTOA di tutti i lotti
    if(datiForm.isModifiedColumn("GARE.DATTOA")){
      String codgar1 = datiForm.getString("GARE.CODGAR1");
      Timestamp dattoa = datiForm.getData("GARE.DATTOA");
      String update= "update gare set dattoa = ? where codgar1= ? and ngara<>codgar1";
      try {
        this.sqlManager.update(update, new Object[]{dattoa,codgar1});
      } catch (SQLException e) {
        this.getRequest().setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA,"1");
        throw new GestoreException(
            "Errore durante l'aggiornamento della Data dell'atto di aggiudicazione della gara dei lotti", null, e);
      }
    }
    
    
  }

  
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
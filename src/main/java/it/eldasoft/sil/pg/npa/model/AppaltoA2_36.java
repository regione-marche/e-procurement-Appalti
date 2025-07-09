/*
 * Created on 16-nov-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

/**
 * Bean description:    
 * Oggetto che riporta i dati inerenti la fase di aggiudicazione.
 *
 * @author Alex.Mancini
 */
public class AppaltoA2_36 {
  
  private DatiBaseProcedura2 datiBaseProcedura;
  
  
  public DatiBaseProcedura2 getDatiBaseProcedura() {
    return datiBaseProcedura;
  }

  
  public void setDatiBaseProcedura(DatiBaseProcedura2 datiBaseProcedura) {
    this.datiBaseProcedura = datiBaseProcedura;
  }

  public AppaltoA2_36() {
    // TODO document why this constructor is empty
  }
  
  public AppaltoA2_36(DatiBaseProcedura2 datiBaseProcedura) {
    this.datiBaseProcedura = datiBaseProcedura;
  }
}
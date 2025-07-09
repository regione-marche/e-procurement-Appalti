/*
 * Created on 20-nov-2023
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
 * Oggetto che riporta i dati dell'appalto all'interno della fase di pubblicazione
 *
 * @author Alex.Mancini
 */
public class AppaltoPL2_8 {

  private DatiBaseAppaltoOptional datiBase;
  private DatiBaseProcedura2 datiBaseProcedura;
  private DatiBaseStrumentiProcedura datiBaseStrumentiProcedura;

  
  public DatiBaseAppaltoOptional getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseAppaltoOptional datiBase) {
    this.datiBase = datiBase;
  }

  
  public DatiBaseProcedura2 getDatiBaseProcedura() {
    return datiBaseProcedura;
  }

  
  public void setDatiBaseProcedura(DatiBaseProcedura2 datiBaseProcedura) {
    this.datiBaseProcedura = datiBaseProcedura;
  }

  
  public DatiBaseStrumentiProcedura getDatiBaseStrumentiProcedura() {
    return datiBaseStrumentiProcedura;
  }

  
  public void setDatiBaseStrumentiProcedura(DatiBaseStrumentiProcedura datiBaseStrumentiProcedura) {
    this.datiBaseStrumentiProcedura = datiBaseStrumentiProcedura;
  }

  public AppaltoPL2_8() {
    // TODO document why this constructor is empty
  }
  
}
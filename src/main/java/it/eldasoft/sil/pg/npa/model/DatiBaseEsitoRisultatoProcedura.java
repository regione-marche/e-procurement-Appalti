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
 * Dato in comune con il Business Group Procedure Lot Result delle eforms
 *
 * @author Alex.Mancini
 * 
 * @property
 * private EnumNPA esitoProcedura;
 */
public class DatiBaseEsitoRisultatoProcedura {

  private EnumNPA esitoProcedura;
  
  
  public EnumNPA getEsitoProcedura() {
    return esitoProcedura;
  }

  
  public void setEsitoProcedura(EnumNPA esitoProcedura) {
    this.esitoProcedura = esitoProcedura;
  }

  public DatiBaseEsitoRisultatoProcedura() {
    // TODO document why this constructor is empty
  }

  public DatiBaseEsitoRisultatoProcedura(EnumNPA esitoProcedura) {
    this.esitoProcedura = esitoProcedura;
  }
  
  
}
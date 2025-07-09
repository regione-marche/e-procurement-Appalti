/*
 * Created on 05-ott-2023
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
 * Dato in comune con il Business Group Subcontracting delle eforms
 *
 * @author Alex.Mancini
 * 
 * @property
 * private EnumNPA subappalto;
 */
public class DatiBaseSubappalti {

  private EnumNPA subappalto;
  
  
  public EnumNPA getSubappalto() {
    return subappalto;
  }

  
  public void setSubappalto(EnumNPA subappalto) {
    this.subappalto = subappalto;
  }

  public DatiBaseSubappalti() {
    // TODO document why this constructor is empty
  }

  public DatiBaseSubappalti(EnumNPA subappalto) {
    this.subappalto = subappalto;
  }
  
}
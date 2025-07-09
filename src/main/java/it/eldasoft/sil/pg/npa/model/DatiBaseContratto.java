/*
 * Created on 13-nov-2023
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
 * Dato in comune con il Business Group Place of Performance delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseContratto {

  private EnumNPA codNUTS;
  
  
  public EnumNPA getCodNUTS() {
    return codNUTS;
  }

  
  public void setCodNUTS(EnumNPA codNUTS) {
    this.codNUTS = codNUTS;
  }

  public DatiBaseContratto() {
    // TODO document why this constructor is empty
  }

  public DatiBaseContratto(EnumNPA codNUTS) {
    this.codNUTS = codNUTS;
  }
  
}
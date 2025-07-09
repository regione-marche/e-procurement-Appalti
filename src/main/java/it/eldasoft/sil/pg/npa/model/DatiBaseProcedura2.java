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
 * Dato in comune con il Business Group Procedure delle eforms
 *
 * @author Alex.Mancini
 * 
 * @property
 * private EnumNPA tipoProcedura;
 */
public class DatiBaseProcedura2 {

  private EnumNPA tipoProcedura;
  
  public EnumNPA getTipoProcedura() {
    return tipoProcedura;
  }

  public void setTipoProcedura(EnumNPA tipoProcedura) {
    this.tipoProcedura = tipoProcedura;
  }

  public DatiBaseProcedura2() {
    // TODO document why this constructor is empty
  }

  public DatiBaseProcedura2(EnumNPA tipoProcedura) {
    this.tipoProcedura = tipoProcedura;
  }
  
  
  
}
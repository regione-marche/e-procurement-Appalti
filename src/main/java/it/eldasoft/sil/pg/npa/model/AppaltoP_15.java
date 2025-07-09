/*
 * Created on 14-mar-2024
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
public class AppaltoP_15 {

  private String codiceAppalto;
  private EnumNPA strumentiSvolgimentoProcedure;
  
  
  public String getCodiceAppalto() {
    return codiceAppalto;
  }

  
  public void setCodiceAppalto(String codiceAppalto) {
    this.codiceAppalto = codiceAppalto;
  }

  
  public EnumNPA getStrumentiSvolgimentoProcedure() {
    return strumentiSvolgimentoProcedure;
  }

  
  public void setStrumentiSvolgimentoProcedure(EnumNPA strumentiSvolgimentoProcedure) {
    this.strumentiSvolgimentoProcedure = strumentiSvolgimentoProcedure;
  }


  public AppaltoP_15() {
    // TODO document why this constructor is empty
  }
  
}
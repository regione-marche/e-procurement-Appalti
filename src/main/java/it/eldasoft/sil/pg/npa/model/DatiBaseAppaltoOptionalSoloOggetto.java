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
 * Dato in comune con il Business Group Purpose delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseAppaltoOptionalSoloOggetto {

  private String oggetto;
  private String oggettoMl;

  
  public String getOggetto() {
    return oggetto;
  }

  
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }

  
  public String getOggettoMl() {
    return oggettoMl;
  }

  
  public void setOggettoMl(String oggettoMl) {
    this.oggettoMl = oggettoMl;
  }

  public DatiBaseAppaltoOptionalSoloOggetto() {
    // TODO document why this constructor is empty
  }


  public DatiBaseAppaltoOptionalSoloOggetto(String oggetto, String oggettoMl) {
    this.oggetto = oggetto;
    this.oggettoMl = oggettoMl;
  }
  
}
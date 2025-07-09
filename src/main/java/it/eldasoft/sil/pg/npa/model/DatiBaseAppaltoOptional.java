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
 * Dato in comune con il Business Group Purpose delle eforms
 *
 * @author Alex.Mancini
 * 
 * @property
 * private String oggetto;
 * private String oggettoMl;
 * private Double importo;
 */
public class DatiBaseAppaltoOptional {

  private String oggetto;
  private String oggettoMl;
  private Double importo;
  
  
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


  public Double getImporto() {
    return importo;
  }

  
  public void setImporto(Double importo) {
    this.importo = importo;
  }

  public DatiBaseAppaltoOptional() {
    // TODO document why this constructor is empty
  }

  public DatiBaseAppaltoOptional(String oggetto, String oggettoMl, Double importo) {
    this.oggetto = oggetto;
    this.oggettoMl = oggettoMl;
    this.importo = importo;
  }
  
  
}
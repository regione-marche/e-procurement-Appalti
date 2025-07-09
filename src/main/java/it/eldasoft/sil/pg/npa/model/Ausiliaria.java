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
 * Dati del soggetto
 *
 * @author Alex.Mancini
 */
public class Ausiliaria {

  private String        codiceFiscale;
  private String        denominazione;
  
  
  public String getCodiceFiscale() {
    return codiceFiscale;
  }

  
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }

  
  public String getDenominazione() {
    return denominazione;
  }

  
  public void setDenominazione(String denominazione) {
    this.denominazione = denominazione;
  }


  public Ausiliaria() {
    // TODO document why this constructor is empty
  }

}
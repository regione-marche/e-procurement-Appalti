/*
 * Created on 14-nov-2023
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
 * Dato in comune con il Business Group Submission Terms delle eforms
 *
 * @author Alex.Mancini
 *
 * @property
 * private String scadenzaPresentazioneInvito;
 * private String oraScadenzaPresentazioneOfferte;
 */
public class DatiBaseTerminiInvio {

  private String scadenzaPresentazioneInvito;
  private String oraScadenzaPresentazioneOfferte;
  
  
  public String getScadenzaPresentazioneInvito() {
    return scadenzaPresentazioneInvito;
  }

  
  public void setScadenzaPresentazioneInvito(String scadenzaPresentazioneInvito) {
    this.scadenzaPresentazioneInvito = scadenzaPresentazioneInvito;
  }

  
  public String getOraScadenzaPresentazioneOfferte() {
    return oraScadenzaPresentazioneOfferte;
  }

  
  public void setOraScadenzaPresentazioneOfferte(String oraScadenzaPresentazioneOfferte) {
    this.oraScadenzaPresentazioneOfferte = oraScadenzaPresentazioneOfferte;
  }

  public DatiBaseTerminiInvio() {
    // TODO document why this constructor is empty
  }

  public DatiBaseTerminiInvio(String scadenzaPresentazioneInvito, String oraScadenzaPresentazioneOfferte) {
    this.scadenzaPresentazioneInvito = scadenzaPresentazioneInvito;
    this.oraScadenzaPresentazioneOfferte = oraScadenzaPresentazioneOfferte;
  }
  
}
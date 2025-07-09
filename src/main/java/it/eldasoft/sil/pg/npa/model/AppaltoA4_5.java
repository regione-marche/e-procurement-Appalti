/*
 * Created on 09-gen-2024
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
 * Oggetto che riporta i dati inerenti l'appalto nella fase di aggiudicazione
 *
 * @author Alex.Mancini
 */
public class AppaltoA4_5 {

  private Boolean prelazionePromotore;
  private String codiceFiscalePromotore;
  private String denominazionePromotore;
  private EnumNPA modalitaRiaggiudicazioneAffidamento;
  
  
  public Boolean getPrelazionePromotore() {
    return prelazionePromotore;
  }

  
  public void setPrelazionePromotore(Boolean prelazionePromotore) {
    this.prelazionePromotore = prelazionePromotore;
  }

  
  public String getCodiceFiscalePromotore() {
    return codiceFiscalePromotore;
  }

  
  public void setCodiceFiscalePromotore(String codiceFiscalePromotore) {
    this.codiceFiscalePromotore = codiceFiscalePromotore;
  }

  
  public String getDenominazionePromotore() {
    return denominazionePromotore;
  }

  
  public void setDenominazionePromotore(String denominazionePromotore) {
    this.denominazionePromotore = denominazionePromotore;
  }

  
  public EnumNPA getModalitaRiaggiudicazioneAffidamento() {
    return modalitaRiaggiudicazioneAffidamento;
  }

  
  public void setModalitaRiaggiudicazioneAffidamento(EnumNPA modalitaRiaggiudicazioneAffidamento) {
    this.modalitaRiaggiudicazioneAffidamento = modalitaRiaggiudicazioneAffidamento;
  }

  public AppaltoA4_5() {
    // TODO document why this constructor is empty
  }
  
}
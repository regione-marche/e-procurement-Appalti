/*
 * Created on 08-gen-2024
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
 * Oggetto che riporta i dati inerenti la fase di aggiudicazione
 *
 * @author Alex.Mancini
 */
public class AppaltoA1_32 {

  private EnumNPA modalitaRiaggiudicazioneAffidamento;

  
  public EnumNPA getModalitaRiaggiudicazioneAffidamento() {
    return modalitaRiaggiudicazioneAffidamento;
  }

  
  public void setModalitaRiaggiudicazioneAffidamento(EnumNPA modalitaRiaggiudicazioneAffidamento) {
    this.modalitaRiaggiudicazioneAffidamento = modalitaRiaggiudicazioneAffidamento;
  }

  
  public AppaltoA1_32() {
    // TODO document why this constructor is empty
  }
  
}
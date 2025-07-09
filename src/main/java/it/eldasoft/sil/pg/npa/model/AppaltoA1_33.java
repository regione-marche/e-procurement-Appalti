/*
 * Created on 29-gen-2024
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
 * Oggetto che riporta i dati inerenti la fase di aggiudicazione.
 *
 * @author Alex.Mancini
 */
public class AppaltoA1_33 {
  
  private EnumNPA motivoUrgenza;
  private String linkDocumenti;
  private EnumNPA modalitaRiaggiudicazioneAffidamento;
  private Boolean relazioneUnicaSulleProcedure;
  
  
  public EnumNPA getMotivoUrgenza() {
    return motivoUrgenza;
  }

  
  public void setMotivoUrgenza(EnumNPA motivoUrgenza) {
    this.motivoUrgenza = motivoUrgenza;
  }

  
  public String getLinkDocumenti() {
    return linkDocumenti;
  }

  
  public void setLinkDocumenti(String linkDocumenti) {
    this.linkDocumenti = linkDocumenti;
  }

  
  public EnumNPA getModalitaRiaggiudicazioneAffidamento() {
    return modalitaRiaggiudicazioneAffidamento;
  }

  
  public void setModalitaRiaggiudicazioneAffidamento(EnumNPA modalitaRiaggiudicazioneAffidamento) {
    this.modalitaRiaggiudicazioneAffidamento = modalitaRiaggiudicazioneAffidamento;
  }

  
  public Boolean getRelazioneUnicaSulleProcedure() {
    return relazioneUnicaSulleProcedure;
  }

  
  public void setRelazioneUnicaSulleProcedure(Boolean relazioneUnicaSulleProcedure) {
    this.relazioneUnicaSulleProcedure = relazioneUnicaSulleProcedure;
  }

  public AppaltoA1_33() {
    // TODO document why this constructor is empty
  }
  
}
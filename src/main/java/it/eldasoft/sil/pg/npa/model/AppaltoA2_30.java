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
 * Oggetto che riporta i dati inerenti la fase di aggiudicazione.
 *
 * @author Alex.Mancini
 */
public class AppaltoA2_30 {
  
  private String motivoUrgenza;
  private String linkDocumenti;
  private EnumNPA modalitaRiaggiudicazioneAffidamento;
  private Boolean relazioneUnicaSulleProcedure;
  private Boolean opereUrbanizzazioneScomputo;
  private DatiBaseSubappalti datiBaseSubappalti;
  private DatiBaseProceduraCompleto datiBaseProcedura;
  private DatiBaseStrumentiProcedura datiBaseStrumentiProcedura;
  
  
  public String getMotivoUrgenza() {
    return motivoUrgenza;
  }

  
  public void setMotivoUrgenza(String motivoUrgenza) {
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

  
  public Boolean getOpereUrbanizzazioneScomputo() {
    return opereUrbanizzazioneScomputo;
  }

  
  public void setOpereUrbanizzazioneScomputo(Boolean opereUrbanizzazioneScomputo) {
    this.opereUrbanizzazioneScomputo = opereUrbanizzazioneScomputo;
  }

  
  public DatiBaseProceduraCompleto getDatiBaseProcedura() {
    return datiBaseProcedura;
  }

  
  public void setDatiBaseProcedura(DatiBaseProceduraCompleto datiBaseProcedura) {
    this.datiBaseProcedura = datiBaseProcedura;
  }

  
  public DatiBaseStrumentiProcedura getDatiBaseStrumentiProcedura() {
    return datiBaseStrumentiProcedura;
  }

  
  public void setDatiBaseStrumentiProcedura(DatiBaseStrumentiProcedura datiBaseStrumentiProcedura) {
    this.datiBaseStrumentiProcedura = datiBaseStrumentiProcedura;
  }

  
  public DatiBaseSubappalti getDatiBaseSubappalti() {
    return datiBaseSubappalti;
  }

  
  public void setDatiBaseSubappalti(DatiBaseSubappalti datiBaseSubappalti) {
    this.datiBaseSubappalti = datiBaseSubappalti;
  }

  public AppaltoA2_30() {
    // TODO document why this constructor is empty
  }
  
}
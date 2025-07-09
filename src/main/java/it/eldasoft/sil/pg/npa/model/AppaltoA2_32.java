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
 * Oggetto che riporta i dati inerenti la fase di aggiudicazione.
 *
 * @author Alex.Mancini
 */
public class AppaltoA2_32 {
  
  private EnumNPA modalitaRiaggiudicazioneAffidamento;
  private DatiBaseStrumentiProceduraOnlyAsta datiBaseStrumentiProcedura;
  private DatiBaseSubappalti datiBaseSubappalti;
  private DatiBaseProcedura datiBaseProcedura;

  
  public EnumNPA getModalitaRiaggiudicazioneAffidamento() {
    return modalitaRiaggiudicazioneAffidamento;
  }

  
  public void setModalitaRiaggiudicazioneAffidamento(EnumNPA modalitaRiaggiudicazioneAffidamento) {
    this.modalitaRiaggiudicazioneAffidamento = modalitaRiaggiudicazioneAffidamento;
  }

  
  public DatiBaseProcedura getDatiBaseProcedura() {
    return datiBaseProcedura;
  }

  
  public void setDatiBaseProcedura(DatiBaseProcedura datiBaseProcedura) {
    this.datiBaseProcedura = datiBaseProcedura;
  }

  
  public DatiBaseStrumentiProceduraOnlyAsta getDatiBaseStrumentiProcedura() {
    return datiBaseStrumentiProcedura;
  }

  
  public void setDatiBaseStrumentiProcedura(DatiBaseStrumentiProceduraOnlyAsta datiBaseStrumentiProcedura) {
    this.datiBaseStrumentiProcedura = datiBaseStrumentiProcedura;
  }

  
  public DatiBaseSubappalti getDatiBaseSubappalti() {
    return datiBaseSubappalti;
  }

  
  public void setDatiBaseSubappalti(DatiBaseSubappalti datiBaseSubappalti) {
    this.datiBaseSubappalti = datiBaseSubappalti;
  }

  public AppaltoA2_32() {
    // TODO document why this constructor is empty
  }
  
}
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
 * Oggetto che riporta i dati inerenti l'affidamento diretto soprasoglia e sottosoglia con pubblicazione su TED - 
 * Avviso volontario per la trasparenza ex ante — direttiva generale - Articolo 3 bis della direttiva 89/665/CEE
 *
 * @author Alex.Mancini
 */
public class AppaltoAD2_25 {

  private String codiceAppalto;
  private EnumNPA motivoUrgenza;
  private String linkDocumenti;
  private Boolean relazioneUnicaSulleProcedure;
  private Boolean opereUrbanizzazioneScomputo;
  private DatiBaseAppaltoOptionalSoloOggetto datiBase;
  private DatiBaseProcedura datiBaseProcedura;
  private DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico datiBaseStrumentiProcedura;
  private DatiBaseSubappalti datiBaseSubappalti;
  
  
  public String getCodiceAppalto() {
    return codiceAppalto;
  }

  
  public void setCodiceAppalto(String codiceAppalto) {
    this.codiceAppalto = codiceAppalto;
  }

  
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

  
  public DatiBaseAppaltoOptionalSoloOggetto getDatiBase() {
    return datiBase;
  }


  public void setDatiBase(DatiBaseAppaltoOptionalSoloOggetto datiBase) {
    this.datiBase = datiBase;
  }


  public DatiBaseProcedura getDatiBaseProcedura() {
    return datiBaseProcedura;
  }

  
  public void setDatiBaseProcedura(DatiBaseProcedura datiBaseProcedura) {
    this.datiBaseProcedura = datiBaseProcedura;
  }

  
  public DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico getDatiBaseStrumentiProcedura() {
    return datiBaseStrumentiProcedura;
  }

  
  public void setDatiBaseStrumentiProcedura(DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico datiBaseStrumentiProcedura) {
    this.datiBaseStrumentiProcedura = datiBaseStrumentiProcedura;
  }

  
  public DatiBaseSubappalti getDatiBaseSubappalti() {
    return datiBaseSubappalti;
  }

  
  public void setDatiBaseSubappalti(DatiBaseSubappalti datiBaseSubappalti) {
    this.datiBaseSubappalti = datiBaseSubappalti;
  }

  public AppaltoAD2_25() {
    // TODO document why this constructor is empty
  }
  
}
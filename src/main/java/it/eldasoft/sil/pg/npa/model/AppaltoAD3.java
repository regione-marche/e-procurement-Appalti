/*
 * Created on 16-nov-2023
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
 * Descrizione non presente nella scheda anac
 *
 * @author Alex.Mancini
 */
public class AppaltoAD3 {

  private String codiceAppalto;
  private Boolean contrattoDifesa;
  private EnumNPA settore;
  private Boolean accordoQuadro;
  private EnumNPA motivoUrgenza;
  private String linkDocumenti;
  private Boolean relazioneUnicaSulleProcedure;
  private Boolean opereUrbanizzazioneScomputo;
  private DatiBaseAppaltoOptionalSoloOggetto datiBase;
  private DatiBaseSubappalti datiBaseSubappalti;
  
  
  public String getCodiceAppalto() {
    return codiceAppalto;
  }

  
  public void setCodiceAppalto(String codiceAppalto) {
    this.codiceAppalto = codiceAppalto;
  }


  public Boolean getContrattoDifesa() {
    return contrattoDifesa;
  }
  public void setContrattoDifesa(Boolean contrattoDifesa) {
    this.contrattoDifesa = contrattoDifesa;
  }

  public EnumNPA getSettore() {
    return settore;
  }
  public void setSettore(EnumNPA settore) {
    this.settore = settore;
  }

  public Boolean getAccordoQuadro() {
    return accordoQuadro;
  }
  public void setAccordoQuadro(Boolean accordoQuadro) {
    this.accordoQuadro = accordoQuadro;
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


  public DatiBaseSubappalti getDatiBaseSubappalti() {
    return datiBaseSubappalti;
  }

  
  public void setDatiBaseSubappalti(DatiBaseSubappalti datiBaseSubappalti) {
    this.datiBaseSubappalti = datiBaseSubappalti;
  }

  public AppaltoAD3() {
    // TODO document why this constructor is empty
  }
  
}
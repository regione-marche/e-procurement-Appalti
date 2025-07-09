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

import java.util.Date;

/**
 * Bean description:    
 * Descrizione non presente nella scheda anac
 *
 * @author Alex.Mancini
 */
public class AppaltoAD4 {

  private String codiceAppalto;
  private String cig;
  private String dataAdesione;
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

  
  public String getCig() {
    return cig;
  }

  
  public void setCig(String cig) {
    this.cig = cig;
  }

  
  public String getDataAdesione() {
    return dataAdesione;
  }

  
  public void setDataAdesione(String dataAdesione) {
    this.dataAdesione = dataAdesione;
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

  public AppaltoAD4() {
    // TODO document why this constructor is empty
  }
  
}
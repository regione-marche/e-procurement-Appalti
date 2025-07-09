/*
 * Created on 14-mar-2024
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
public class AppaltoA3_6 {
  
  private String codiceAppalto;
  private EnumNPA settore;
  private Boolean accordoQuadro;
  private Boolean relazioneUnicaSulleProcedure;
  private DatiBaseAppaltoOptionalSoloOggetto datiBase;
  
  
  public String getCodiceAppalto() {
    return codiceAppalto;
  }

  
  public void setCodiceAppalto(String codiceAppalto) {
    this.codiceAppalto = codiceAppalto;
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
  
  public Boolean getRelazioneUnicaSulleProcedure() {
    return relazioneUnicaSulleProcedure;
  }

  
  public void setRelazioneUnicaSulleProcedure(Boolean relazioneUnicaSulleProcedure) {
    this.relazioneUnicaSulleProcedure = relazioneUnicaSulleProcedure;
  }

  
  public DatiBaseAppaltoOptionalSoloOggetto getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseAppaltoOptionalSoloOggetto datiBase) {
    this.datiBase = datiBase;
  }

  public AppaltoA3_6() {
    // TODO document why this constructor is empty
  }
  
}
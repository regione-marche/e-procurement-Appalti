/*
 * Created on 04-mar-2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

import java.util.List;

/**
 * Bean description:    
 * Affidamento diretto sopra la soglia minima e sotto la soglia comunitaria, generale. 
 * Articolo 50 comma 1 lettere c) d) e) decreto legislativo 36/2023. 
 * Articolo 50 comma 9 decreto legislativo 36/2023.
 *
 * @author Alex.Mancini
 */
public class AnacFormAD2_28 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoAD2_28 appalto;
  private List<AggiudicazioneAD2_28> aggiudicazioni;

  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  
  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }
  
  
  public AppaltoAD2_28 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoAD2_28 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneAD2_28> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneAD2_28> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormAD2_28() {
    // TODO document why this constructor is empty
  }
  
}
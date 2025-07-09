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

import java.util.List;

/**
 * Bean description:    
 * Contiene tutti i dati necessari per l'affidamento diretto < limite massimo dell'affidamento diretto, senza invio al TED.
 *
 * @author Alex.Mancini
 */
public class AnacFormAD3 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoAD3 appalto;
  private List<AggiudicazioneAD3> aggiudicazioni;

  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  
  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  
  public AppaltoAD3 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoAD3 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneAD3> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneAD3> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormAD3() {
    // TODO document why this constructor is empty
  }
  
}
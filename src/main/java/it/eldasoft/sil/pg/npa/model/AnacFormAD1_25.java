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

import java.util.List;

/**
 * Bean description:    
 * Contiene tutti i dati necessari per l'affidamento diretto
 *
 * @author Alex.Mancini
 */
public class AnacFormAD1_25 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoAD1_25 appalto;
  private List<AggiudicazioneAD1_25> aggiudicazioni;

  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  
  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }
  
  
  public AppaltoAD1_25 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoAD1_25 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneAD1_25> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneAD1_25> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormAD1_25() {
    // TODO document why this constructor is empty
  }
  
}
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

import java.util.List;

/**
 * Bean description:    
 * Contiene tutti i dati relativi all'aggiudicazione
 *
 * @author Alex.Mancini
 */
public class AnacFormA3_6 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoA3_6 appalto;
  private List<AggiudicazioneA3_6> aggiudicazioni;

  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  
  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  
  public AppaltoA3_6 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA3_6 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA3_6> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA3_6> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA3_6() {
    // TODO document why this constructor is empty
  }
  
}
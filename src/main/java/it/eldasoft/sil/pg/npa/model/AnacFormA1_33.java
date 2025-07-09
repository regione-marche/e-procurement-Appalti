/*
 * Created on 29-gen-2024
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
 * Contiene tutti i dati relativi all'aggiudicazione.
 *
 * @author Alex.Mancini
 */
public class AnacFormA1_33 {

  private AppaltoA1_33 appalto;
  private List<AggiudicazioneA1_33> aggiudicazioni;

  
  public AppaltoA1_33 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA1_33 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA1_33> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA1_33> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA1_33() {
    // TODO document why this constructor is empty
  }
  
}
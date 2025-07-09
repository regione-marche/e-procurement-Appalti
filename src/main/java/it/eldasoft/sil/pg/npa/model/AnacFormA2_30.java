/*
 * Created on 20-nov-2023
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
public class AnacFormA2_30 {

  private AppaltoA2_30 appalto;
  private List<AggiudicazioneA2_30> aggiudicazioni;

  
  public AppaltoA2_30 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA2_30 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA2_30> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA2_30> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA2_30() {
    // TODO document why this constructor is empty
  }
  
}
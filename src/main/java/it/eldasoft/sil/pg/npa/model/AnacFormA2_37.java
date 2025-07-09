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
 * Contiene tutti i dati relativi all'aggiudicazione.
 *
 * @author Alex.Mancini
 */
public class AnacFormA2_37 {

  private AppaltoA2_37 appalto;
  private List<AggiudicazioneA2_37> aggiudicazioni;

  
  public AppaltoA2_37 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA2_37 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA2_37> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA2_37> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA2_37() {
    // TODO document why this constructor is empty
  }
  
}
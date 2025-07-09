/*
 * Created on 09-gen-2024
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
public class AnacFormA4_5 {

  private AppaltoA4_5 appalto;
  private List<AggiudicazioneA4_5> aggiudicazioni;

  
  public AppaltoA4_5 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA4_5 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA4_5> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA4_5> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA4_5() {
    // TODO document why this constructor is empty
  }
  
}
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
 * Contiene tutti i dati relativi all'aggiudicazione.
 *
 * @author Alex.Mancini
 */
public class AnacFormA1_32 {

  private AppaltoA1_32 appalto;
  private List<AggiudicazioneA1_32> aggiudicazioni;

  
  public AppaltoA1_32 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA1_32 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA1_32> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA1_32> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA1_32() {
    // TODO document why this constructor is empty
  }
  
}
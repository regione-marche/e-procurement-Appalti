/*
 * Created on 05-ott-2023
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
 * Contiene tutti i dati relativi all'aggiudicazione soprasoglia e sottosoglia con invio al 
 * TED - Avviso di aggiudicazione di un appalto — direttiva settoriale, regime ordinario. 
 * Articolo 70 della direttiva 2014/25/UE
 *
 * @author Alex.Mancini
 */
public class AnacFormA1_30 {

  private AppaltoA1_30 appalto;
  private List<AggiudicazioneA1_30> aggiudicazioni;

  
  public AppaltoA1_30 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA1_30 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA1_30> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA1_30> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA1_30() {
    // TODO document why this constructor is empty
  }
  
}
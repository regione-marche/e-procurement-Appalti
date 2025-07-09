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
 * Contiene tutti i dati relativi all'aggiudicazione soprasoglia e sottosoglia con invio al TED 
 * - Avviso relativo all’appalto aggiudicato — direttiva generale, regime ordinario. 
 * Articolo 50 della direttiva 2014/24/UE
 *
 * @author Alex.Mancini
 */
public class AnacFormA1_29 {

  private AppaltoA1_29 appalto;
  private List<AggiudicazioneA1_29> aggiudicazioni;

  
  public AppaltoA1_29 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA1_29 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA1_29> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA1_29> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA1_29() {
    // TODO document why this constructor is empty
  }
  
}
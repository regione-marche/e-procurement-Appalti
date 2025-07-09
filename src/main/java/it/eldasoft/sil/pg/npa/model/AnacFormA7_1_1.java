/*
 * Created on 06-feb-2024
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
 * Contiene tutti i dati necessari per procedura negoziata senza bando pari o sopra soglia con invio al TED
 *
 * @author Alex.Mancini
 */
public class AnacFormA7_1_1 {

  private AppaltoA7_1_1 appalto;
  private List<AggiudicazioneA7_1_1> aggiudicazioni;

  
  public AppaltoA7_1_1 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA7_1_1 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA7_1_1> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA7_1_1> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA7_1_1() {
    // TODO document why this constructor is empty
  }
  
}
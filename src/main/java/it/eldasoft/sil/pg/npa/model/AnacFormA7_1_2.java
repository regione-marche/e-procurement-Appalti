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
 * Contiene tutti i dati necessari per procedura negoziata senza bando pari o sopra soglia
 *
 * @author Alex.Mancini
 */
public class AnacFormA7_1_2 {

  private AppaltoA7_1_2 appalto;
  private List<AggiudicazioneA7_1_2> aggiudicazioni;

  
  public AppaltoA7_1_2 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA7_1_2 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA7_1_2> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA7_1_2> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA7_1_2() {
    // TODO document why this constructor is empty
  }
  
}
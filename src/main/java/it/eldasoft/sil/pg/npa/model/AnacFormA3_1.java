/*
 * Created on 18-dic-2023
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
public class AnacFormA3_1 {

  private AppaltoA3_1 appalto;
  private List<AggiudicazioneA3_1> aggiudicazioni;

  
  public AppaltoA3_1 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA3_1 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA3_1> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA3_1> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA3_1() {
    // TODO document why this constructor is empty
  }
  
}
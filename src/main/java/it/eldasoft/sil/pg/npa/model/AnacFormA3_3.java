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
public class AnacFormA3_3 {

  private AppaltoA3_3 appalto;
  private List<AggiudicazioneA3_3> aggiudicazioni;

  
  public AppaltoA3_3 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoA3_3 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneA3_3> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneA3_3> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormA3_3() {
    // TODO document why this constructor is empty
  }
  
}
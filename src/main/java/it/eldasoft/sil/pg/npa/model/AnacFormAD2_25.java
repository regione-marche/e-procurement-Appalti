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
 * Contiene tutti i dati necessari per l'affidamento diretto sottoglia > 5K, senza invio al TED. 
 * – Avviso volontario per la trasparenza ex ante — direttiva generale. 
 * Articolo 3 bis della direttiva 89/665/CEE
 *
 * @author Alex.Mancini
 */
public class AnacFormAD2_25 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoAD2_25 appalto;
  private List<AggiudicazioneAD2_25> aggiudicazioni;

  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  
  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }
  
  
  public AppaltoAD2_25 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoAD2_25 appalto) {
    this.appalto = appalto;
  }

  
  public List<AggiudicazioneAD2_25> getAggiudicazioni() {
    return aggiudicazioni;
  }

  
  public void setAggiudicazioni(List<AggiudicazioneAD2_25> aggiudicazioni) {
    this.aggiudicazioni = aggiudicazioni;
  }

  public AnacFormAD2_25() {
    // TODO document why this constructor is empty
  }
  
}
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

/**
 * Bean description:    
 * Dato in comune con il Business Group Contract delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseAggiudicazioneAppalto {

  private String dataAggiudicazione;

  public String getDataAggiudicazione() {
    return dataAggiudicazione;
  }

  public void setDataAggiudicazione(String dataAggiudicazione) {
    this.dataAggiudicazione = dataAggiudicazione;
  }
  
  public DatiBaseAggiudicazioneAppalto() {
    // TODO document why this constructor is empty
  }

  public DatiBaseAggiudicazioneAppalto(String dataAggiudicazione) {
    this.dataAggiudicazione = dataAggiudicazione;
  }
  
  
  
}
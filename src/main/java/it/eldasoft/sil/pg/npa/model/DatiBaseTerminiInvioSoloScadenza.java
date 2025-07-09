/*
 * Created on 13-nov-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

import java.util.Date;

/**
 * Bean description:    
 * Dato in comune con il Business Group Submission Terms delle eforms
 *
 * @author Alex.Mancini
 *
 * @property
 * private String scadenzaPresentazioneInvito;
 */
public class DatiBaseTerminiInvioSoloScadenza {

  private String scadenzaPresentazioneInvito;
  
  
  public String getScadenzaPresentazioneInvito() {
    return scadenzaPresentazioneInvito;
  }

  
  public void setScadenzaPresentazioneInvito(String scadenzaPresentazioneInvito) {
    this.scadenzaPresentazioneInvito = scadenzaPresentazioneInvito;
  }

  public DatiBaseTerminiInvioSoloScadenza() {
    // TODO document why this constructor is empty
  }

  public DatiBaseTerminiInvioSoloScadenza(String scadenzaPresentazioneInvito) {
    this.scadenzaPresentazioneInvito = scadenzaPresentazioneInvito;
  }
  
}
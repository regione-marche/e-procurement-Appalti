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
 * Scheda di comunicazione modifica dati della procedura per appalti sotto i 5K euro
 *
 * @author Manuel.Bridda
 */
public class AnacFormCM2 {

  private String cig;

  private Comunicazione comunicazione;

  public String getCig() {
    return cig;
  }

  public void setCig(String cig) {
    this.cig = cig;
  }

  public Comunicazione getComunicazione() {
    return comunicazione;
  }

  public void setComunicazione(Comunicazione comunicazione) {
    this.comunicazione = comunicazione;
  }
}
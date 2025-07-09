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
 * Codice CIG da annullare.
 *
 * @author Manuel.Bridda
 */
public class AnacFormANN {

  private String cig;

  private Annullamento annullamento;

  public String getCig() {
    return cig;
  }

  public void setCig(String cig) {
    this.cig = cig;
  }

  public Annullamento getAnnullamento() {
    return annullamento;
  }

  public void setAnnullamento(Annullamento annullamento) {
    this.annullamento = annullamento;
  }
}
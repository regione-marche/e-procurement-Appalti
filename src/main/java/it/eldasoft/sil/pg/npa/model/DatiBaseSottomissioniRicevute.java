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

/**
 * Bean description:    
 * Dato in comune con il Business Group Received Submissions delle eforms
 *
 * @author Alex.Mancini
 * 
 * @property
 * private Double offertaMassimoRibasso;
 */
public class DatiBaseSottomissioniRicevute {

  private Double offertaMinimoRibasso;
  private Double offertaMassimoRibasso;

  public Double getOffertaMinimoRibasso() {
    return offertaMinimoRibasso;
  }
  public Double getOffertaMassimoRibasso() {
    return offertaMassimoRibasso;
  }

  public void setOffertaMinimoRibasso(Double offertaMinimoRibasso) {
    this.offertaMinimoRibasso = offertaMinimoRibasso;
  }
  public void setOffertaMassimoRibasso(Double offertaMassimoRibasso) {
    this.offertaMassimoRibasso = offertaMassimoRibasso;
  }

  public DatiBaseSottomissioniRicevute() {
    // TODO document why this constructor is empty
  }
  
  public DatiBaseSottomissioniRicevute(Double offertaMinimoRibasso, Double offertaMassimoRibasso) {
    this.offertaMinimoRibasso = offertaMinimoRibasso;
    this.offertaMassimoRibasso = offertaMassimoRibasso;
  }
  
}
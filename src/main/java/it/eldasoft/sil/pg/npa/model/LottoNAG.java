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
 * dati relativi ai lotti
 *
 * @author Alex.Mancini
 */
public class LottoNAG {

  private String cig;
  private DatiBaseRisultatoProcedura2 datiBaseRisultatoProcedura2;
  private EnumNPA esitoProceduraAnnullata;

  public String getCig() {
    return cig;
  }

  public void setCig(String cig) {
    this.cig = cig;
  }

  public DatiBaseRisultatoProcedura2 getDatiBaseRisultatoProcedura() {
    return datiBaseRisultatoProcedura2;
  }

  public void setDatiBaseRisultatoProcedura(DatiBaseRisultatoProcedura2 datiBaseRisultatoProcedura2) {
    this.datiBaseRisultatoProcedura2 = datiBaseRisultatoProcedura2;
  }
  
  public EnumNPA getEsitoProceduraAnnullata() {
    return esitoProceduraAnnullata;
  }
  
  public void setEsitoProceduraAnnullata(EnumNPA esitoProceduraAnnullata) {
    this.esitoProceduraAnnullata = esitoProceduraAnnullata;
  }
  
  public LottoNAG() {
    // TODO document why this constructor is empty
  }

}
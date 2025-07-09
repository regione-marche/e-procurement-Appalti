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
 * Contiene tutti i dati necessari per la pubblicazione nazionale 
 * di un bando di gara soprasoglia — direttiva settoriale, regime ordinario. 
 * Articolo 67, paragrafo 2, della direttiva 2014/25/UE
 *
 * @author Alex.Mancini
 */
public class AnacFormP1_11 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP_11 appalto;
  private List<LottoP_11> lotti;
  
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  
  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  
  public AppaltoP_11 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoP_11 appalto) {
    this.appalto = appalto;
  }

  
  public List<LottoP_11> getLotti() {
    return lotti;
  }

  
  public void setLotti(List<LottoP_11> lotti) {
    this.lotti = lotti;
  }

  public AnacFormP1_11() {
    // TODO document why this constructor is empty
  }
  
}
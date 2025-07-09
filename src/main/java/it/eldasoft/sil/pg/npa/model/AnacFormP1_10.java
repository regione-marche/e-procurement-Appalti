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
 * di un bando di gara soprasoglia – direttiva generale, regime ordinario. 
 * Articolo 48, paragrafo 2, della direttiva 2014/24/UE
 *
 * @author Alex.Mancini
 */
public class AnacFormP1_10 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP_10 appalto;
  private List<LottoP_10> lotti;
  
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  
  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  
  public AppaltoP_10 getAppalto() {
    return appalto;
  }

  
  public void setAppalto(AppaltoP_10 appalto) {
    this.appalto = appalto;
  }

  
  public List<LottoP_10> getLotti() {
    return lotti;
  }

  
  public void setLotti(List<LottoP_10> lotti) {
    this.lotti = lotti;
  }

  public AnacFormP1_10() {
    // TODO document why this constructor is empty
  }
  
}
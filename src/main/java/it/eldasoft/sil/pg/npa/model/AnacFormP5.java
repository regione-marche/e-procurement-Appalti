/*
 * Created on 14-mar-2024
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
 * Scheda di tracciabilita
 *
 * @author Alex.Mancini
 */
public class AnacFormP5 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP5 appalto;
  private List<LottoP5> lotti;
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  public AppaltoP5 getAppalto() {
    return appalto;
  }

  public void setAppalto(AppaltoP5 appalto) {
    this.appalto = appalto;
  }

  public List<LottoP5> getLotti() {
    return lotti;
  }

  public void setLotti(List<LottoP5> lotti) {
    this.lotti = lotti;
  }
  
  public AnacFormP5() {
    // TODO document why this constructor is empty
  }
  
}
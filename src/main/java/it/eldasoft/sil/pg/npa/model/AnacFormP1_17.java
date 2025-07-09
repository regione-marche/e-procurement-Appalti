/*
 * Created on 20-ott-2023
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
 * Contiene tutti i dati necessari per la pubblicazione nazionale.
 *
 * @author Alex.Mancini
 */
public class AnacFormP1_17 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP_17 appalto;
  private List<LottoP_17> lotti;
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  public AppaltoP_17 getAppalto() {
    return appalto;
  }

  public void setAppalto(AppaltoP_17 appalto) {
    this.appalto = appalto;
  }

  public List<LottoP_17> getLotti() {
    return lotti;
  }

  public void setLotti(List<LottoP_17> lotti) {
    this.lotti = lotti;
  }
  
  public AnacFormP1_17() {
    // TODO document why this constructor is empty
  }
  
}
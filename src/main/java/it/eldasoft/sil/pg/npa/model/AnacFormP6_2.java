/*
 * Created on 17-nov-2023
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
public class AnacFormP6_2 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP6_2 appalto;
  private List<LottoP6_2> lotti;
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  public AppaltoP6_2 getAppalto() {
    return appalto;
  }

  public void setAppalto(AppaltoP6_2 appalto) {
    this.appalto = appalto;
  }

  public List<LottoP6_2> getLotti() {
    return lotti;
  }

  public void setLotti(List<LottoP6_2> lotti) {
    this.lotti = lotti;
  }
  
  public AnacFormP6_2() {
    // TODO document why this constructor is empty
  }
  
}
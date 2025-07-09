/*
 * Created on 18-dic-2023
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
 * Contiene i dati relativi ai contratti esclusi - adesione con confronto competitivo
 *
 * @author Alex.Mancini
 */
public class AnacFormP3_3 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP3_3 appalto;
  private List<LottoP3_3> lotti;
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  public AppaltoP3_3 getAppalto() {
    return appalto;
  }

  public void setAppalto(AppaltoP3_3 appalto) {
    this.appalto = appalto;
  }

  public List<LottoP3_3> getLotti() {
    return lotti;
  }

  public void setLotti(List<LottoP3_3> lotti) {
    this.lotti = lotti;
  }
  
  public AnacFormP3_3() {
    // TODO document why this constructor is empty
  }
  
}
/*
 * Created on 11-gen-2024
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
 * Contiene i dati per la scheda di Concessione di servizi e costruzione e gestione
 *
 * @author Alex.Mancini
 */
public class AnacFormP4_5 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP4_5 appalto;
  private List<LottoP4_5> lotti;
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  public AppaltoP4_5 getAppalto() {
    return appalto;
  }

  public void setAppalto(AppaltoP4_5 appalto) {
    this.appalto = appalto;
  }

  public List<LottoP4_5> getLotti() {
    return lotti;
  }

  public void setLotti(List<LottoP4_5> lotti) {
    this.lotti = lotti;
  }
  
  public AnacFormP4_5() {
    // TODO document why this constructor is empty
  }
  
}
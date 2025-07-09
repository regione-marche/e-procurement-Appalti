/*
 * Created on 20-nov-2023
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
 * Contiene i dati per la scheda di avvio dell'indagine di mercato.
 *
 * @author Alex.Mancini
 */
public class AnacFormP7_1_1 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP7Base appalto;
  private List<LottoP7_1_1> lotti;
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  public AppaltoP7Base getAppalto() {
    return appalto;
  }

  public void setAppalto(AppaltoP7Base appalto) {
    this.appalto = appalto;
  }

  public List<LottoP7_1_1> getLotti() {
    return lotti;
  }

  public void setLotti(List<LottoP7_1_1> lotti) {
    this.lotti = lotti;
  }
  
  public AnacFormP7_1_1() {
    // TODO document why this constructor is empty
  }
  
}
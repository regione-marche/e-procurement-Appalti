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
 * Contiene i dati relativi ai contratti esclusi in Concessione o Contratti 
 * esclusi in Concessione con la tecnica dell'AQ
 *
 * @author Alex.Mancini
 */
public class AnacFormP3_2 {

  private List<StazioneAppaltante> stazioniAppaltanti;
  private AppaltoP3_2 appalto;
  private List<LottoP3_2> lotti;
  
  public List<StazioneAppaltante> getStazioniAppaltanti() {
    return stazioniAppaltanti;
  }

  public void setStazioniAppaltanti(List<StazioneAppaltante> stazioniAppaltanti) {
    this.stazioniAppaltanti = stazioniAppaltanti;
  }

  public AppaltoP3_2 getAppalto() {
    return appalto;
  }

  public void setAppalto(AppaltoP3_2 appalto) {
    this.appalto = appalto;
  }

  public List<LottoP3_2> getLotti() {
    return lotti;
  }

  public void setLotti(List<LottoP3_2> lotti) {
    this.lotti = lotti;
  }
  
  public AnacFormP3_2() {
    // TODO document why this constructor is empty
  }
  
}
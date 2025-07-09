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
 * Contiene tutti i dati relativi alla mancata aggiudicazione della gara - sotto soglia europea
 *
 * @author Alex.Mancini
 */
public class AnacFormNAG {

  private List<LottoNAG> lotti;
  
  
  public List<LottoNAG> getLotti() {
    return lotti;
  }

  
  public void setLotti(List<LottoNAG> lotti) {
    this.lotti = lotti;
  }

  public AnacFormNAG() {
    // TODO document why this constructor is empty
  }
  
}
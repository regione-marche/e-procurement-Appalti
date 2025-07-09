/*
 * Created on 16-nov-2023
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
 * Dati relativi agli incarichi tecnici e alle prestazioni progettuali.
 *
 * @author Alex.Mancini
 */
public class AnacFormS3 {

  private List<ElencoIncarichi> elencoIncarichi;
  
  
  public List<ElencoIncarichi> getElencoIncarichi() {
    return elencoIncarichi;
  }

  
  public void setElencoIncarichi(List<ElencoIncarichi> elencoIncarichi) {
    this.elencoIncarichi = elencoIncarichi;
  }

  public AnacFormS3() {
    // TODO document why this constructor is empty
  }
  
}
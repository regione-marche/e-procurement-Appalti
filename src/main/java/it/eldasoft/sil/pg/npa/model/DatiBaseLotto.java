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

/**
 * Bean description:    
 * Dato in comune con il Business Group Purpose delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseLotto {

  private EnumNPA oggettoContratto;
  private String oggetto;
  private Multilingua oggettoMl;
  
  
  public EnumNPA getOggettoContratto() {
    return oggettoContratto;
  }

  
  public void setOggettoContratto(EnumNPA oggettoContratto) {
    this.oggettoContratto = oggettoContratto;
  }

  
  public String getOggetto() {
    return oggetto;
  }

  
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }


  public Multilingua getOggettoMl() {
    return oggettoMl;
  }


  public void setOggettoMl(Multilingua oggettoMl) {
    this.oggettoMl = oggettoMl;
  }


  public DatiBaseLotto() {
    // TODO document why this constructor is empty
  }

  public DatiBaseLotto(EnumNPA oggettoContratto, String oggetto, Multilingua oggettoMl) {
    this.oggettoContratto = oggettoContratto;
    this.oggetto = oggetto;
    this.oggettoMl = oggettoMl;
  }
  
  
}
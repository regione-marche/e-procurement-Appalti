/*
 * Created on 13-nov-2023
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
 * 
 * @property
 * private EnumNPA oggettoContratto;
 * private String oggetto;
 * private Multilingua oggettoMl;
 * private Double importo;
 */
public class DatiBaseLottoOptional {

  private EnumNPA oggettoContratto;
  private String oggetto;
  private Multilingua oggettoMl;
  private Double importo;
  
  
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


  public Double getImporto() {
    return importo;
  }

  
  public void setImporto(Double importo) {
    this.importo = importo;
  }

  public DatiBaseLottoOptional() {
    // TODO document why this constructor is empty
  }

  public DatiBaseLottoOptional(EnumNPA oggettoContratto, String oggetto, Double importo) {
    this.oggettoContratto = oggettoContratto;
    this.oggetto = oggetto;
    this.importo = importo;
  }
  
  
}
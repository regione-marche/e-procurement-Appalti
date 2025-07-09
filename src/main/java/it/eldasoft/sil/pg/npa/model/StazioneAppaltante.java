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
 * Stazione Appaltante e ruoli ricoperti nell'appalto
 *
 * @author Alex.Mancini
 */
public class StazioneAppaltante {

  private String codiceFiscale;
  private String codiceAusa;
  private String codiceCentroCosto;
  private List<EnumNPA> funzioniSvolte;
  private Boolean saTitolare;
  
  
  public String getCodiceFiscale() {
    return codiceFiscale;
  }

  
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }

  
  public String getCodiceAusa() {
    return codiceAusa;
  }

  
  public void setCodiceAusa(String codiceAusa) {
    this.codiceAusa = codiceAusa;
  }

  
  public String getCodiceCentroCosto() {
    return codiceCentroCosto;
  }

  
  public void setCodiceCentroCosto(String codiceCentroCosto) {
    this.codiceCentroCosto = codiceCentroCosto;
  }

  
  public List<EnumNPA> getFunzioniSvolte() {
    return funzioniSvolte;
  }

  
  public void setFunzioniSvolte(List<EnumNPA> funzioniSvolte) {
    this.funzioniSvolte = funzioniSvolte;
  }

  
  public Boolean getSaTitolare() {
    return saTitolare;
  }

  
  public void setSaTitolare(Boolean saTitolare) {
    this.saTitolare = saTitolare;
  }

  public StazioneAppaltante() {
    // TODO document why this constructor is empty
  }
  
}
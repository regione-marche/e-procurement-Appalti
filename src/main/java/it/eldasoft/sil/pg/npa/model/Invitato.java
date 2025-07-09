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

/**
 * Bean description:    
 * Dati del soggetto
 *
 * @author Alex.Mancini
 */
public class Invitato {

  private String        codiceFiscale;
  private String        denominazione;
  private EnumNPA       ruoloOE;
  private EnumNPA       tipoOE;
  
  
  public String getCodiceFiscale() {
    return codiceFiscale;
  }

  
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }

  
  public String getDenominazione() {
    return denominazione;
  }

  
  public void setDenominazione(String denominazione) {
    this.denominazione = denominazione;
  }

  
  public EnumNPA getRuoloOE() {
    return ruoloOE;
  }

  
  public void setRuoloOE(EnumNPA ruoloOE) {
    this.ruoloOE = ruoloOE;
  }

  
  public EnumNPA getTipoOE() {
    return tipoOE;
  }

  
  public void setTipoOE(EnumNPA tipoOE) {
    this.tipoOE = tipoOE;
  }

  public Invitato() {
    // TODO document why this constructor is empty
  }

}
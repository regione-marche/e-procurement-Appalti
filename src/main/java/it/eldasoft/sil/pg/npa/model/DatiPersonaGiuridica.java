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

/**
 * Bean description:    
 * Dati relativi alla persona giuridica.
 *
 * @author Alex.Mancini
 */
public class DatiPersonaGiuridica {

  private String codiceFiscale;
  private String denominazione;
  private EnumNPA ruoloOE;
  private EnumNPA tipoOE;
  private Integer idPartecipante;

  
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

  
  public Integer getIdPartecipante() {
    return idPartecipante;
  }


  public void setIdPartecipante(Integer idPartecipante) {
    this.idPartecipante = idPartecipante;
  }
  

  public DatiPersonaGiuridica() {
    // TODO document why this constructor is empty
  }
  
}
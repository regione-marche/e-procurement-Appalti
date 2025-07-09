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
 * Dati del partecipante in caso di aggiudicazione diretta
 *
 * @author Alex.Mancini
 */
public class PartecipanteAD {

  private String codiceFiscale;
  private String denominazione;
  private EnumNPA ruoloOE;
  private EnumNPA tipoOE;
  private String idPartecipante;
  private String paeseOperatoreEconomico;
  private Multilingua paeseOperatoreEconomicoMl;
  private Boolean avvalimento;
  private Double importo;
  private EnumNPA tipologiaAvvalimento;
  
  
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

  
  public String getIdPartecipante() {
    return idPartecipante;
  }

  
  public void setIdPartecipante(String idPartecipante) {
    this.idPartecipante = idPartecipante;
  }


  public String getPaeseOperatoreEconomico() {
    return paeseOperatoreEconomico;
  }

  
  public void setPaeseOperatoreEconomico(String paeseOperatoreEconomico) {
    this.paeseOperatoreEconomico = paeseOperatoreEconomico;
  }

  
  public Multilingua getPaeseOperatoreEconomicoMl() {
    return paeseOperatoreEconomicoMl;
  }


  public void setPaeseOperatoreEconomicoMl(Multilingua paeseOperatoreEconomicoMl) {
    this.paeseOperatoreEconomicoMl = paeseOperatoreEconomicoMl;
  }


  public Boolean getAvvalimento() {
    return avvalimento;
  }

  
  public void setAvvalimento(Boolean avvalimento) {
    this.avvalimento = avvalimento;
  }

  
  public EnumNPA getTipologiaAvvalimento() {
    return tipologiaAvvalimento;
  }

  
  public void setTipologiaAvvalimento(EnumNPA tipologiaAvvalimento) {
    this.tipologiaAvvalimento = tipologiaAvvalimento;
  }

  
  public Double getImporto() {
    return importo;
  }


  public void setImporto(Double importo) {
    this.importo = importo;
  }
  
  
  public PartecipanteAD() {
    // TODO document why this constructor is empty
  }

}
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
 * Oggetto che riporta i dati inerenti la fase di aggiudicazione sopra soglia e sotto soglia
 *
 * @author Alex.Mancini
 */
public class AggiudicazioneA1_29 {

  private String cig;
  private Double valoreSogliaAnomalia;
  private QuadroEconomico quadroEconomicoStandard;
  private List<Offerte> offertePresentate;
  private Long numeroOfferteAmmesse;
  private EnumNPA esitoProceduraAnnullata;

  
  public String getCig() {
    return cig;
  }

  
  public void setCig(String cig) {
    this.cig = cig;
  }

  
  public Double getValoreSogliaAnomalia() {
    return valoreSogliaAnomalia;
  }

  
  public void setValoreSogliaAnomalia(Double valoreSogliaAnomalia) {
    this.valoreSogliaAnomalia = valoreSogliaAnomalia;
  }

  
  public QuadroEconomico getQuadroEconomicoStandard() {
    return quadroEconomicoStandard;
  }

  
  public void setQuadroEconomicoStandard(QuadroEconomico quadroEconomicoStandard) {
    this.quadroEconomicoStandard = quadroEconomicoStandard;
  }

  
  public List<Offerte> getOffertePresentate() {
    return offertePresentate;
  }

  
  public void setOffertePresentate(List<Offerte> offertePresentate) {
    this.offertePresentate = offertePresentate;
  }

  
  public Long getNumeroOfferteAmmesse() {
    return numeroOfferteAmmesse;
  }

  
  public void setNumeroOfferteAmmesse(Long numeroOfferteAmmesse) {
    this.numeroOfferteAmmesse = numeroOfferteAmmesse;
  }

  
  public EnumNPA getEsitoProceduraAnnullata() {
    return esitoProceduraAnnullata;
  }

  
  public void setEsitoProceduraAnnullata(EnumNPA esitoProceduraAnnullata) {
    this.esitoProceduraAnnullata = esitoProceduraAnnullata;
  }

  public AggiudicazioneA1_29() {
    // TODO document why this constructor is empty
  }
  
}
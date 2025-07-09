/*
 * Created on 09-gen-2024
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
 * Oggetto che riporta i dati del lotto
 *
 * @author Alex.Mancini
 */
public class AggiudicazioneA4_5 {

  private String cig;
  private List<Offerte> offertePresentate;
  private Long numeroOfferteAmmesse;
  private QuadroEconomicoConcessioni quadroEconomicoConcessioni;
  private DatiBaseRisultatoProcedura datiBaseRisultatoProcedura;
  private DatiBaseSubappalti datiBaseSubappalti;
  private DatiBaseAggiudicazioneAppalto datiBaseAggiudicazioneAppalto;
  private DatiBaseAccessibilita datiBaseAccessibilita;
  
  
  public String getCig() {
    return cig;
  }

  
  public void setCig(String cig) {
    this.cig = cig;
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

  
  public QuadroEconomicoConcessioni getQuadroEconomicoConcessioni() {
    return quadroEconomicoConcessioni;
  }

  
  public void setQuadroEconomicoConcessioni(QuadroEconomicoConcessioni quadroEconomicoConcessioni) {
    this.quadroEconomicoConcessioni = quadroEconomicoConcessioni;
  }

  
  public DatiBaseRisultatoProcedura getDatiBaseRisultatoProcedura() {
    return datiBaseRisultatoProcedura;
  }

  
  public void setDatiBaseRisultatoProcedura(DatiBaseRisultatoProcedura datiBaseRisultatoProcedura) {
    this.datiBaseRisultatoProcedura = datiBaseRisultatoProcedura;
  }

  
  public DatiBaseSubappalti getDatiBaseSubappalti() {
    return datiBaseSubappalti;
  }

  
  public void setDatiBaseSubappalti(DatiBaseSubappalti datiBaseSubappalti) {
    this.datiBaseSubappalti = datiBaseSubappalti;
  }

  
  public DatiBaseAggiudicazioneAppalto getDatiBaseAggiudicazioneAppalto() {
    return datiBaseAggiudicazioneAppalto;
  }

  
  public void setDatiBaseAggiudicazioneAppalto(DatiBaseAggiudicazioneAppalto datiBaseAggiudicazioneAppalto) {
    this.datiBaseAggiudicazioneAppalto = datiBaseAggiudicazioneAppalto;
  }

  
  public DatiBaseAccessibilita getDatiBaseAccessibilita() {
    return datiBaseAccessibilita;
  }

  
  public void setDatiBaseAccessibilita(DatiBaseAccessibilita datiBaseAccessibilita) {
    this.datiBaseAccessibilita = datiBaseAccessibilita;
  }

  public AggiudicazioneA4_5() {
    // TODO document why this constructor is empty
  }
  
}
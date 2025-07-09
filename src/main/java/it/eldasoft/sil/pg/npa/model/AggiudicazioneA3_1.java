/*
 * Created on 18-dic-2023
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
 * Oggetto che riporta i dati inerenti la fase di aggiudicazione
 *
 * @author Alex.Mancini
 */
public class AggiudicazioneA3_1 {

  private String cig;
  private Double valoreSogliaAnomalia;
  private QuadroEconomico quadroEconomicoStandard;
  private List<Offerte> offertePresentate;
  private DatiBaseAggiudicazioneAppalto datiBaseAggiudicazioneAppalto;
  private DatiBaseEsitoRisultatoProcedura datiBaseRisultatoProcedura;
  private DatiBaseSottomissioniRicevute datiBaseSottomissioniRicevute;
  
  
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

  
  public DatiBaseEsitoRisultatoProcedura getDatiBaseRisultatoProcedura() {
    return datiBaseRisultatoProcedura;
  }

  
  public void setDatiBaseRisultatoProcedura(DatiBaseEsitoRisultatoProcedura datiBaseRisultatoProcedura) {
    this.datiBaseRisultatoProcedura = datiBaseRisultatoProcedura;
  }

  
  public DatiBaseAggiudicazioneAppalto getDatiBaseAggiudicazioneAppalto() {
    return datiBaseAggiudicazioneAppalto;
  }

  
  public void setDatiBaseAggiudicazioneAppalto(DatiBaseAggiudicazioneAppalto datiBaseAggiudicazioneAppalto) {
    this.datiBaseAggiudicazioneAppalto = datiBaseAggiudicazioneAppalto;
  }

  
  public DatiBaseSottomissioniRicevute getDatiBaseSottomissioniRicevute() {
    return datiBaseSottomissioniRicevute;
  }

  
  public void setDatiBaseSottomissioniRicevute(DatiBaseSottomissioniRicevute datiBaseSottomissioniRicevute) {
    this.datiBaseSottomissioniRicevute = datiBaseSottomissioniRicevute;
  }

  public AggiudicazioneA3_1() {
    // TODO document why this constructor is empty
  }
  
}
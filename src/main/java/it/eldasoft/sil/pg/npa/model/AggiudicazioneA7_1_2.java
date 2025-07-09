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
 * Oggetto che riporta i dati di aggiudicazione per procedura negoziata senza bando pari o sopra soglia.
 *
 * @author Alex.Mancini
 */
public class AggiudicazioneA7_1_2 {

  private String cig;
  private Boolean afferenteInvestimentiPNRR;
  private Boolean acquisizioneCup;
  private List<String> cupLotto;
  private ParitaDiGenereGenerazionale paritaDiGenereGenerazionale;
  private QuadroEconomico quadroEconomicoStandard;
  private QuadroEconomicoConcessioni quadroEconomicoConcessioni;
  private List<PartecipanteAD> partecipanti;
  private List<Finanziamento> finanziamenti;
  private DatiBaseAggiudicazioneAppalto datiBaseAggiudicazioneAppalto;
  private DatiBaseAccessibilita datiBaseAccessibilita;
  private Double valoreSogliaAnomalia;
  private List<Offerte> offertePresentate;
  private Long numeroOfferteAmmesse;
  private DatiBaseEsitoRisultatoProcedura datiBaseRisultatoProcedura;
  private DatiBaseSottomissioniRicevute datiBaseSottomissioniRicevute;
  
  
  public String getCig() {
    return cig;
  }

  
  public void setCig(String cig) {
    this.cig = cig;
  }

  
  public Boolean getAfferenteInvestimentiPNRR() {
    return afferenteInvestimentiPNRR;
  }

  
  public void setAfferenteInvestimentiPNRR(Boolean afferenteInvestimentiPNRR) {
    this.afferenteInvestimentiPNRR = afferenteInvestimentiPNRR;
  }

  
  public Boolean getAcquisizioneCup() {
    return acquisizioneCup;
  }

  
  public void setAcquisizioneCup(Boolean acquisizioneCup) {
    this.acquisizioneCup = acquisizioneCup;
  }

  
  public List<String> getCupLotto() {
    return cupLotto;
  }

  
  public void setCupLotto(List<String> cupLotto) {
    this.cupLotto = cupLotto;
  }

  
  public ParitaDiGenereGenerazionale getParitaDiGenereGenerazionale() {
    return paritaDiGenereGenerazionale;
  }

  
  public void setParitaDiGenereGenerazionale(ParitaDiGenereGenerazionale paritaDiGenereGenerazionale) {
    this.paritaDiGenereGenerazionale = paritaDiGenereGenerazionale;
  }

  
  public QuadroEconomico getQuadroEconomicoStandard() {
    return quadroEconomicoStandard;
  }

  
  public void setQuadroEconomicoStandard(QuadroEconomico quadroEconomicoStandard) {
    this.quadroEconomicoStandard = quadroEconomicoStandard;
  }

  
  public QuadroEconomicoConcessioni getQuadroEconomicoConcessioni() {
    return quadroEconomicoConcessioni;
  }

  
  public void setQuadroEconomicoConcessioni(QuadroEconomicoConcessioni quadroEconomicoConcessioni) {
    this.quadroEconomicoConcessioni = quadroEconomicoConcessioni;
  }

  
  public List<PartecipanteAD> getPartecipanti() {
    return partecipanti;
  }

  
  public void setPartecipanti(List<PartecipanteAD> partecipanti) {
    this.partecipanti = partecipanti;
  }

  
  public List<Finanziamento> getFinanziamenti() {
    return finanziamenti;
  }

  
  public void setFinanziamenti(List<Finanziamento> finanziamenti) {
    this.finanziamenti = finanziamenti;
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

  public AggiudicazioneA7_1_2() {
    // TODO document why this constructor is empty
  }

    public Double getValoreSogliaAnomalia() {
        return valoreSogliaAnomalia;
    }

    public void setValoreSogliaAnomalia(Double valoreSogliaAnomalia) {
        this.valoreSogliaAnomalia = valoreSogliaAnomalia;
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

    public DatiBaseEsitoRisultatoProcedura getDatiBaseRisultatoProcedura() {
        return datiBaseRisultatoProcedura;
    }

    public void setDatiBaseRisultatoProcedura(DatiBaseEsitoRisultatoProcedura datiBaseRisultatoProcedura) {
        this.datiBaseRisultatoProcedura = datiBaseRisultatoProcedura;
    }

    public DatiBaseSottomissioniRicevute getDatiBaseSottomissioniRicevute() {
        return datiBaseSottomissioniRicevute;
    }

    public void setDatiBaseSottomissioniRicevute(DatiBaseSottomissioniRicevute datiBaseSottomissioniRicevute) {
        this.datiBaseSottomissioniRicevute = datiBaseSottomissioniRicevute;
    }
}
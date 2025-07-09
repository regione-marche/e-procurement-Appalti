/*
 * Created on 14-mar-2024
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
 * Oggetto che riporta i dati del lotto all'interno della fase di pubblicazione
 *
 * @author Alex.Mancini
 */
public class LottoP_15 {

  private String lotIdentifier;
  private Boolean strumentiElettroniciSpecifici;
  private EnumNPA codIstat;
  private Boolean servizioPubblicoLocale;
  private Boolean lavoroOAcquistoPrevistoInProgrammazione;
  private String cui;
  private Boolean ripetizioniEConsegneComplementari;
  private IpotesiCollegamento ipotesiCollegamento;
  private Boolean opzioniRinnovi;
  private Boolean afferenteInvestimentiPNRR;
  private Boolean acquisizioneCup;
  private List<String> cupLotto;
  private ParitaDiGenereGenerazionale paritaDiGenereGenerazionale;
  private String ccnl;
  private EnumNPA tipologiaLavoro;
  private EnumNPA modalitaAcquisizione;
  private EnumNPA categoria;
  private List<EnumNPA> categoriaScorporabile;
  private EnumNPA prestazioniComprese;
  private List<Finanziamento> finanziamenti;
  private QuadroEconomico quadroEconomicoStandard;
  
  
  public String getLotIdentifier() {
    return lotIdentifier;
  }

  
  public void setLotIdentifier(String lotIdentifier) {
    this.lotIdentifier = lotIdentifier;
  }

  
  public Boolean getStrumentiElettroniciSpecifici() {
    return strumentiElettroniciSpecifici;
  }

  
  public void setStrumentiElettroniciSpecifici(Boolean strumentiElettroniciSpecifici) {
    this.strumentiElettroniciSpecifici = strumentiElettroniciSpecifici;
  }

  
  public EnumNPA getCodIstat() {
    return codIstat;
  }

  
  public void setCodIstat(EnumNPA codIstat) {
    this.codIstat = codIstat;
  }

  
  public Boolean getServizioPubblicoLocale() {
    return servizioPubblicoLocale;
  }

  
  public void setServizioPubblicoLocale(Boolean servizioPubblicoLocale) {
    this.servizioPubblicoLocale = servizioPubblicoLocale;
  }

  
  public Boolean getLavoroOAcquistoPrevistoInProgrammazione() {
    return lavoroOAcquistoPrevistoInProgrammazione;
  }

  
  public void setLavoroOAcquistoPrevistoInProgrammazione(Boolean lavoroOAcquistoPrevistoInProgrammazione) {
    this.lavoroOAcquistoPrevistoInProgrammazione = lavoroOAcquistoPrevistoInProgrammazione;
  }

  
  public String getCui() {
    return cui;
  }

  
  public void setCui(String cui) {
    this.cui = cui;
  }

  
  public Boolean getRipetizioniEConsegneComplementari() {
    return ripetizioniEConsegneComplementari;
  }

  
  public void setRipetizioniEConsegneComplementari(Boolean ripetizioniEConsegneComplementari) {
    this.ripetizioniEConsegneComplementari = ripetizioniEConsegneComplementari;
  }

  
  public IpotesiCollegamento getIpotesiCollegamento() {
    return ipotesiCollegamento;
  }

  
  public void setIpotesiCollegamento(IpotesiCollegamento ipotesiCollegamento) {
    this.ipotesiCollegamento = ipotesiCollegamento;
  }

  
  public Boolean getOpzioniRinnovi() {
    return opzioniRinnovi;
  }

  
  public void setOpzioniRinnovi(Boolean opzioniRinnovi) {
    this.opzioniRinnovi = opzioniRinnovi;
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

  
  public String getCcnl() {
    return ccnl;
  }

  
  public void setCcnl(String ccnl) {
    this.ccnl = ccnl;
  }

  
  public EnumNPA getTipologiaLavoro() {
    return tipologiaLavoro;
  }

  
  public void setTipologiaLavoro(EnumNPA tipologiaLavoro) {
    this.tipologiaLavoro = tipologiaLavoro;
  }

  
  public EnumNPA getModalitaAcquisizione() {
    return modalitaAcquisizione;
  }

  
  public void setModalitaAcquisizione(EnumNPA modalitaAcquisizione) {
    this.modalitaAcquisizione = modalitaAcquisizione;
  }

  
  public EnumNPA getCategoria() {
    return categoria;
  }

  
  public void setCategoria(EnumNPA categoria) {
    this.categoria = categoria;
  }

  
  public List<EnumNPA> getCategoriaScorporabile() {
    return categoriaScorporabile;
  }

  
  public void setCategoriaScorporabile(List<EnumNPA> categoriaScorporabile) {
    this.categoriaScorporabile = categoriaScorporabile;
  }

  
  public EnumNPA getPrestazioniComprese() {
    return prestazioniComprese;
  }

  
  public void setPrestazioniComprese(EnumNPA prestazioniComprese) {
    this.prestazioniComprese = prestazioniComprese;
  }

  
  public List<Finanziamento> getFinanziamenti() {
    return finanziamenti;
  }

  
  public void setFinanziamenti(List<Finanziamento> finanziamenti) {
    this.finanziamenti = finanziamenti;
  }

  
  public QuadroEconomico getQuadroEconomicoStandard() {
    return quadroEconomicoStandard;
  }

  
  public void setQuadroEconomicoStandard(QuadroEconomico quadroEconomicoStandard) {
    this.quadroEconomicoStandard = quadroEconomicoStandard;
  }

  public LottoP_15() {
    // TODO document why this constructor is empty
  }
  
}
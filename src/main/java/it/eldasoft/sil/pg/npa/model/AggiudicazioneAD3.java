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
 * Contiene tutti i dati necessari per l'affidamento diretto < limite massimo dell'affidamento diretto, senza invio al TED.
 *
 * @author Alex.Mancini
 */
public class AggiudicazioneAD3 {

  private String lotIdentifier;
  private Boolean afferenteInvestimentiPNRR;
  private Boolean acquisizioneCup;
  private List<String> cupLotto;
  private String ccnl;
  private EnumNPA categoria;
  private List<EnumNPA> categoriaScorporabile;
  private EnumNPA codIstat;
  private ParitaDiGenereGenerazionale paritaDiGenereGenerazionale;
  private QuadroEconomico quadroEconomicoStandard;
  private QuadroEconomicoConcessioni quadroEconomicoConcessioni;
  private List<PartecipanteAD> partecipanti;
  private DatiBaseLotto datiBase;
  private DatiBaseCPV datiBaseCPV;
  private DatiBaseContratto datiBaseContratto;
  private DatiBaseAggiudicazioneAppalto datiBaseAggiudicazioneAppalto;
  private DatiBaseAccessibilita datiBaseAccessibilita;
  private DatiBaseDocumentiOptional datiBaseDocumenti;
  private Boolean strumentiElettroniciSpecifici;

  
  public String getLotIdentifier() {
    return lotIdentifier;
  }

  
  public void setLotIdentifier(String lotIdentifier) {
    this.lotIdentifier = lotIdentifier;
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

  
  public String getCcnl() {
    return ccnl;
  }

  
  public void setCcnl(String ccnl) {
    this.ccnl = ccnl;
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

  
  public EnumNPA getCodIstat() {
    return codIstat;
  }

  
  public void setCodIstat(EnumNPA codIstat) {
    this.codIstat = codIstat;
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

  
  public DatiBaseLotto getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseLotto datiBase) {
    this.datiBase = datiBase;
  }

  
  public DatiBaseCPV getDatiBaseCPV() {
    return datiBaseCPV;
  }

  
  public void setDatiBaseCPV(DatiBaseCPV datiBaseCPV) {
    this.datiBaseCPV = datiBaseCPV;
  }

  
  public DatiBaseContratto getDatiBaseContratto() {
    return datiBaseContratto;
  }

  
  public void setDatiBaseContratto(DatiBaseContratto datiBaseContratto) {
    this.datiBaseContratto = datiBaseContratto;
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

  public DatiBaseDocumentiOptional getDatiBaseDocumenti() {
    return datiBaseDocumenti;
  }


  public void setDatiBaseDocumenti(DatiBaseDocumentiOptional datiBaseDocumenti) {
    this.datiBaseDocumenti = datiBaseDocumenti;
  }


  public AggiudicazioneAD3() {
    // TODO document why this constructor is empty
  }


  public Boolean getStrumentiElettroniciSpecifici() {
    return strumentiElettroniciSpecifici;
  }


  public void setStrumentiElettroniciSpecifici(Boolean strumentiElettroniciSpecifici) {
    this.strumentiElettroniciSpecifici = strumentiElettroniciSpecifici;
  }
  
}
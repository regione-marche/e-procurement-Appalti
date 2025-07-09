/*
 * Created on 26-gen-2024
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
 * Oggetto che riporta i dati del lotto all'interno della fase di pubblicazione sopra soglia e sotto soglia
 *
 * @author Alex.Mancini
 */
public class LottoP_12 {

  private String lotIdentifier;
  private List<EnumNPA> categorieMerceologiche;
  private List<EnumNPA> condizioniNegoziata;
  private EnumNPA contrattiDisposizioniParticolari;
  private EnumNPA codIstat;
  private Boolean afferenteInvestimentiPNRR;
  private Boolean acquisizioneCup;
  private List<String> cupLotto;
  private List<Finanziamento> finanziamenti;
  private EnumNPA derogaQualificazione;
  private Boolean servizioPubblicoLocale;
  private Boolean ripetizioniEConsegneComplementari;
  private Boolean lavoroOAcquistoPrevistoInProgrammazione;
  private String cui;
  private Boolean saNonSoggettaObblighi24Dicembre2015;
  private Boolean iniziativeNonSoddisfacenti;
  private String ccnl;
  private EnumNPA modalitaAcquisizione;
  private Boolean opzioniRinnovi;
  private IpotesiCollegamento ipotesiCollegamento;
  private EnumNPA categoria;
  private List<EnumNPA> categoriaScorporabile;
  private ParitaDiGenereGenerazionale paritaDiGenereGenerazionale;
  private QuadroEconomico quadroEconomicoStandard;
  private Boolean strumentiElettroniciSpecifici;
  
  
  public String getLotIdentifier() {
    return lotIdentifier;
  }

  
  public void setLotIdentifier(String lotIdentifier) {
    this.lotIdentifier = lotIdentifier;
  }

  
  public List<EnumNPA> getCategorieMerceologiche() {
    return categorieMerceologiche;
  }

  
  public void setCategorieMerceologiche(List<EnumNPA> categorieMerceologiche) {
    this.categorieMerceologiche = categorieMerceologiche;
  }

  
  public List<EnumNPA> getCondizioniNegoziata() {
    return condizioniNegoziata;
  }

  
  public void setCondizioniNegoziata(List<EnumNPA> condizioniNegoziata) {
    this.condizioniNegoziata = condizioniNegoziata;
  }

  
  public EnumNPA getContrattiDisposizioniParticolari() {
    return contrattiDisposizioniParticolari;
  }

  
  public void setContrattiDisposizioniParticolari(EnumNPA contrattiDisposizioniParticolari) {
    this.contrattiDisposizioniParticolari = contrattiDisposizioniParticolari;
  }

  
  public EnumNPA getCodIstat() {
    return codIstat;
  }

  
  public void setCodIstat(EnumNPA codIstat) {
    this.codIstat = codIstat;
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

  
  public List<Finanziamento> getFinanziamenti() {
    return finanziamenti;
  }

  
  public void setFinanziamenti(List<Finanziamento> finanziamenti) {
    this.finanziamenti = finanziamenti;
  }


  public EnumNPA getDerogaQualificazione() {
    return derogaQualificazione;
  }
  public void setDerogaQualificazione(EnumNPA derogaQualificazione) {
    this.derogaQualificazione = derogaQualificazione;
  }

  public Boolean getServizioPubblicoLocale() {
    return servizioPubblicoLocale;
  }

  
  public void setServizioPubblicoLocale(Boolean servizioPubblicoLocale) {
    this.servizioPubblicoLocale = servizioPubblicoLocale;
  }

  
  public Boolean getRipetizioniEConsegneComplementari() {
    return ripetizioniEConsegneComplementari;
  }

  
  public void setRipetizioniEConsegneComplementari(Boolean ripetizioniEConsegneComplementari) {
    this.ripetizioniEConsegneComplementari = ripetizioniEConsegneComplementari;
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

  
  public Boolean getSaNonSoggettaObblighi24Dicembre2015() {
    return saNonSoggettaObblighi24Dicembre2015;
  }

  
  public void setSaNonSoggettaObblighi24Dicembre2015(Boolean saNonSoggettaObblighi24Dicembre2015) {
    this.saNonSoggettaObblighi24Dicembre2015 = saNonSoggettaObblighi24Dicembre2015;
  }

  
  public Boolean getIniziativeNonSoddisfacenti() {
    return iniziativeNonSoddisfacenti;
  }

  
  public void setIniziativeNonSoddisfacenti(Boolean iniziativeNonSoddisfacenti) {
    this.iniziativeNonSoddisfacenti = iniziativeNonSoddisfacenti;
  }

  
  public String getCcnl() {
    return ccnl;
  }

  
  public void setCcnl(String ccnl) {
    this.ccnl = ccnl;
  }

  
  public EnumNPA getModalitaAcquisizione() {
    return modalitaAcquisizione;
  }

  
  public void setModalitaAcquisizione(EnumNPA modalitaAcquisizione) {
    this.modalitaAcquisizione = modalitaAcquisizione;
  }

  
  public Boolean getOpzioniRinnovi() {
    return opzioniRinnovi;
  }

  
  public void setOpzioniRinnovi(Boolean opzioniRinnovi) {
    this.opzioniRinnovi = opzioniRinnovi;
  }

  
  public IpotesiCollegamento getIpotesiCollegamento() {
    return ipotesiCollegamento;
  }

  
  public void setIpotesiCollegamento(IpotesiCollegamento ipotesiCollegamento) {
    this.ipotesiCollegamento = ipotesiCollegamento;
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

  public LottoP_12() {
    // TODO document why this constructor is empty
  }


  public Boolean getStrumentiElettroniciSpecifici() {
    return strumentiElettroniciSpecifici;
  }


  public void setStrumentiElettroniciSpecifici(Boolean strumentiElettroniciSpecifici) {
    this.strumentiElettroniciSpecifici = strumentiElettroniciSpecifici;
  }
  
}
/*
 * Created on 11-gen-2024
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
public class LottoP4_5 {

  private Boolean servizioPubblicoLocale;
  private String lotIdentifier;
  private String ccnl;
  private List<EnumNPA> condizioniNegoziata;
  private EnumNPA codIstat;
  private EnumNPA contrattiDisposizioniParticolari;
  private Boolean lavoroOAcquistoPrevistoInProgrammazione;
  private String cui;
  private IpotesiCollegamento ipotesiCollegamento;
  private Boolean opzioniRinnovi;
  private Boolean afferenteInvestimentiPNRR;
  private Boolean acquisizioneCup;
  private List<String> cupLotto;
  private ParitaDiGenereGenerazionale paritaDiGenereGenerazionale;
  private EnumNPA modalitaAcquisizione;
  private List<EnumNPA> tipologiaLavoro;
  private EnumNPA categoria;
  private List<EnumNPA> categoriaScorporabile;
  private List<Finanziamento> finanziamenti;
  private EnumNPA derogaQualificazione;
  private DatiBaseLottoOptional datiBase;
  private DatiBaseCPV datiBaseCPV;
  private DatiBaseContratto datiBaseContratto;
  private DatiBaseAggiudicazioneOptional datiBaseAggiudicazione;
  private DatiBaseAggiuntivi datiBaseAggiuntivi;
  private DatiBaseTerminiInvio datiBaseTerminiInvio;
  private DatiBaseAccessibilita datiBaseAccessibilita;
  private DatiBaseDocumentiOptional datiBaseDocumenti;
  private Boolean strumentiElettroniciSpecifici;
  
  
  public Boolean getServizioPubblicoLocale() {
    return servizioPubblicoLocale;
  }

  
  public void setServizioPubblicoLocale(Boolean servizioPubblicoLocale) {
    this.servizioPubblicoLocale = servizioPubblicoLocale;
  }

  
  public String getLotIdentifier() {
    return lotIdentifier;
  }

  
  public void setLotIdentifier(String lotIdentifier) {
    this.lotIdentifier = lotIdentifier;
  }

  public String getCcnl() {
    return ccnl;
  }
  public void setCcnl(String ccnl) {
    this.ccnl = ccnl;
  }
  
  public List<EnumNPA> getCondizioniNegoziata() {
    return condizioniNegoziata;
  }

  
  public void setCondizioniNegoziata(List<EnumNPA> condizioniNegoziata) {
    this.condizioniNegoziata = condizioniNegoziata;
  }

  
  public EnumNPA getCodIstat() {
    return codIstat;
  }

  
  public void setCodIstat(EnumNPA codIstat) {
    this.codIstat = codIstat;
  }

  
  public EnumNPA getContrattiDisposizioniParticolari() {
    return contrattiDisposizioniParticolari;
  }

  
  public void setContrattiDisposizioniParticolari(EnumNPA contrattiDisposizioniParticolari) {
    this.contrattiDisposizioniParticolari = contrattiDisposizioniParticolari;
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

  
  public EnumNPA getModalitaAcquisizione() {
    return modalitaAcquisizione;
  }

  
  public void setModalitaAcquisizione(EnumNPA modalitaAcquisizione) {
    this.modalitaAcquisizione = modalitaAcquisizione;
  }

  
  public List<EnumNPA> getTipologiaLavoro() {
    return tipologiaLavoro;
  }

  
  public void setTipologiaLavoro(List<EnumNPA> tipologiaLavoro) {
    this.tipologiaLavoro = tipologiaLavoro;
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
  
  public DatiBaseLottoOptional getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseLottoOptional datiBase) {
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

  
  public DatiBaseAggiudicazioneOptional getDatiBaseAggiudicazione() {
    return datiBaseAggiudicazione;
  }

  
  public void setDatiBaseAggiudicazione(DatiBaseAggiudicazioneOptional datiBaseAggiudicazione) {
    this.datiBaseAggiudicazione = datiBaseAggiudicazione;
  }

  
  public DatiBaseAggiuntivi getDatiBaseAggiuntivi() {
    return datiBaseAggiuntivi;
  }

  
  public void setDatiBaseAggiuntivi(DatiBaseAggiuntivi datiBaseAggiuntivi) {
    this.datiBaseAggiuntivi = datiBaseAggiuntivi;
  }

  
  public DatiBaseTerminiInvio getDatiBaseTerminiInvio() {
    return datiBaseTerminiInvio;
  }

  
  public void setDatiBaseTerminiInvio(DatiBaseTerminiInvio datiBaseTerminiInvio) {
    this.datiBaseTerminiInvio = datiBaseTerminiInvio;
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

  
  public Boolean getStrumentiElettroniciSpecifici() {
    return strumentiElettroniciSpecifici;
  }

  
  public void setStrumentiElettroniciSpecifici(Boolean strumentiElettroniciSpecifici) {
    this.strumentiElettroniciSpecifici = strumentiElettroniciSpecifici;
  }

  public LottoP4_5() {
    // TODO document why this constructor is empty
  }

}
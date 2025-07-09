/*
 * Created on 20-nov-2023
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
public class LottoP7_1_3 {
  
  private String lotIdentifier;
  private List<EnumNPA> categorieMerceologiche;
  private Boolean saNonSoggettaObblighi24Dicembre2015;
  private Boolean iniziativeNonSoddisfacenti;
  private Boolean strumentiElettroniciSpecifici;
  private EnumNPA motivoEsclusioneOrdinarioSpeciale;
  private EnumNPA motivoEsclusioneConcessione;
  private List<EnumNPA> condizioniNegoziata;
  private EnumNPA contrattiDisposizioniParticolari;
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
  private EnumNPA modalitaAcquisizione;
  private List<EnumNPA> tipologiaLavoro;
  private EnumNPA categoria;
  private List<EnumNPA> categoriaScorporabile;
  private EnumNPA prestazioniComprese;
  private List<Finanziamento> finanziamenti;
  private EnumNPA tipoRealizzazione;
  private DatiBaseLottoOptional datiBase;
  private DatiBaseCPV datiBaseCPV;
  private DatiBaseContratto datiBaseContratto;
  private DatiBaseAggiudicazioneOptional datiBaseAggiudicazione;
  private DatiBaseAggiuntivi datiBaseAggiuntivi;
  private DatiBaseAccessibilita datiBaseAccessibilita;
  private DatiBaseComunicazione datiBaseComunicazione;
  private DatiBaseDocumentiOptional datiBaseDocumenti;
  private QuadroEconomico quadroEconomicoStandard;
  private DatiBaseTerminiInvioSoloScadenza datiBaseTerminiInvio;
  
  
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

  
  public Boolean getStrumentiElettroniciSpecifici() {
    return strumentiElettroniciSpecifici;
  }

  
  public void setStrumentiElettroniciSpecifici(Boolean strumentiElettroniciSpecifici) {
    this.strumentiElettroniciSpecifici = strumentiElettroniciSpecifici;
  }

  
  public EnumNPA getMotivoEsclusioneOrdinarioSpeciale() {
    return motivoEsclusioneOrdinarioSpeciale;
  }

  
  public void setMotivoEsclusioneOrdinarioSpeciale(EnumNPA motivoEsclusioneOrdinarioSpeciale) {
    this.motivoEsclusioneOrdinarioSpeciale = motivoEsclusioneOrdinarioSpeciale;
  }

  
  public EnumNPA getMotivoEsclusioneConcessione() {
    return motivoEsclusioneConcessione;
  }

  
  public void setMotivoEsclusioneConcessione(EnumNPA motivoEsclusioneConcessione) {
    this.motivoEsclusioneConcessione = motivoEsclusioneConcessione;
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

  
  public EnumNPA getTipoRealizzazione() {
    return tipoRealizzazione;
  }

  
  public void setTipoRealizzazione(EnumNPA tipoRealizzazione) {
    this.tipoRealizzazione = tipoRealizzazione;
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

  
  public DatiBaseAccessibilita getDatiBaseAccessibilita() {
    return datiBaseAccessibilita;
  }

  
  public void setDatiBaseAccessibilita(DatiBaseAccessibilita datiBaseAccessibilita) {
    this.datiBaseAccessibilita = datiBaseAccessibilita;
  }

  
  public DatiBaseComunicazione getDatiBaseComunicazione() {
    return datiBaseComunicazione;
  }

  
  public void setDatiBaseComunicazione(DatiBaseComunicazione datiBaseComunicazione) {
    this.datiBaseComunicazione = datiBaseComunicazione;
  }

  
  public DatiBaseDocumentiOptional getDatiBaseDocumenti() {
    return datiBaseDocumenti;
  }

  
  public void setDatiBaseDocumenti(DatiBaseDocumentiOptional datiBaseDocumenti) {
    this.datiBaseDocumenti = datiBaseDocumenti;
  }

  
  public QuadroEconomico getQuadroEconomicoStandard() {
    return quadroEconomicoStandard;
  }

  
  public void setQuadroEconomicoStandard(QuadroEconomico quadroEconomicoStandard) {
    this.quadroEconomicoStandard = quadroEconomicoStandard;
  }

  
  public DatiBaseTerminiInvioSoloScadenza getDatiBaseTerminiInvio() {
    return datiBaseTerminiInvio;
  }

  
  public void setDatiBaseTerminiInvio(DatiBaseTerminiInvioSoloScadenza datiBaseTerminiInvio) {
    this.datiBaseTerminiInvio = datiBaseTerminiInvio;
  }

  public LottoP7_1_3() {
    // TODO document why this constructor is empty
  }

}
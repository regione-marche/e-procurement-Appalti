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

/**
 * Bean description:    
 * Oggetto che riporta i dati del lotto all'interno della fase di pianificazione
 *
 * @author Alex.Mancini
 */
public class LottoPL2_8 {
  
  private DatiBaseLottoOptional datiBase;
  private DatiBaseCPV datiBaseCPV;
  private DatiBaseContratto datiBaseContratto;
  private DatiBaseAggiudicazioneOptional datiBaseAggiudicazione;
  private DatiBaseAggiuntivi datiBaseAggiuntivi;
  private DatiBaseAccessibilita datiBaseAccessibilita;
  private DatiBaseDocumentiOptional datiBaseDocumenti;
  private String lotIdentifier;
  private DatiBaseTerminiInvioSoloOra datiBaseTerminiInvio;
  private DatiBaseComunicazione datiBaseComunicazione;
  
  
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

  
  public DatiBaseDocumentiOptional getDatiBaseDocumenti() {
    return datiBaseDocumenti;
  }

  
  public void setDatiBaseDocumenti(DatiBaseDocumentiOptional datiBaseDocumenti) {
    this.datiBaseDocumenti = datiBaseDocumenti;
  }

  
  public String getLotIdentifier() {
    return lotIdentifier;
  }

  
  public void setLotIdentifier(String lotIdentifier) {
    this.lotIdentifier = lotIdentifier;
  }

  
  public DatiBaseTerminiInvioSoloOra getDatiBaseTerminiInvio() {
    return datiBaseTerminiInvio;
  }

  
  public void setDatiBaseTerminiInvio(DatiBaseTerminiInvioSoloOra datiBaseTerminiInvio) {
    this.datiBaseTerminiInvio = datiBaseTerminiInvio;
  }

  
  public DatiBaseComunicazione getDatiBaseComunicazione() {
    return datiBaseComunicazione;
  }

  
  public void setDatiBaseComunicazione(DatiBaseComunicazione datiBaseComunicazione) {
    this.datiBaseComunicazione = datiBaseComunicazione;
  }

  public LottoPL2_8() {
    // TODO document why this constructor is empty
  }

}
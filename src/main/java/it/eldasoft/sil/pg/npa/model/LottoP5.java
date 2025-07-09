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

/**
 * Bean description:    
 * Oggetto che riporta i dati del lotto
 *
 * @author Alex.Mancini
 */
public class LottoP5 <TipoCup>{

  private String lotIdentifier;
  private EnumNPA fattispecieTracciabilita;
  private DatiBaseLottoOptional datiBase;
  private EnumNPA sceltaContraente;
  private TipoCup cup;

  
  public String getLotIdentifier() {
    return lotIdentifier;
  }

  
  public void setLotIdentifier(String lotIdentifier) {
    this.lotIdentifier = lotIdentifier;
  }

  
  public EnumNPA getFattispecieTracciabilita() {
    return fattispecieTracciabilita;
  }

  
  public void setFattispecieTracciabilita(EnumNPA fattispecieTracciabilita) {
    this.fattispecieTracciabilita = fattispecieTracciabilita;
  }

  
  public DatiBaseLottoOptional getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseLottoOptional datiBase) {
    this.datiBase = datiBase;
  }

  
  public EnumNPA getSceltaContraente() {
    return sceltaContraente;
  }

  
  public void setSceltaContraente(EnumNPA sceltaContraente) {
    this.sceltaContraente = sceltaContraente;
  }

  
  public TipoCup getCup() {
    return cup;
  }

  
  public void setCup(TipoCup cup) {
    this.cup = cup;
  }

  public LottoP5() {
    // TODO document why this constructor is empty
  }

}
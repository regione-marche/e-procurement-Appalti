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
 * Oggetto che riporta i dati del lotto all'interno della fase di pubblicazione sopra soglia e sotto soglia
 *
 * @author Alex.Mancini
 */
public class LottoP_24 {
  
  private String lotIdentifier;
  private EnumNPA codIstat;
  private Boolean lavoroOAcquistoPrevistoInProgrammazione;
  private String cui;
  private Boolean afferenteInvestimentiPNRR;
  private EnumNPA motivoEsclusioneOrdinarioSpeciale;
  private Boolean acquisizioneCup;
  private List<String> cupLotto;
  private EnumNPA contrattiDisposizioniParticolari;
  private List<Finanziamento> finanziamenti;
  private QuadroEconomicoConcorsiProgettazione quadroEconomicoConcorsiProgettazione;
  private Boolean strumentiElettroniciSpecifici;
  
  
  public String getLotIdentifier() {
    return lotIdentifier;
  }

  
  public void setLotIdentifier(String lotIdentifier) {
    this.lotIdentifier = lotIdentifier;
  }

  
  public EnumNPA getCodIstat() {
    return codIstat;
  }

  
  public void setCodIstat(EnumNPA codIstat) {
    this.codIstat = codIstat;
  }

  
  public Boolean isLavoroOAcquistoPrevistoInProgrammazione() {
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

  
  public Boolean isAfferenteInvestimentiPNRR() {
    return afferenteInvestimentiPNRR;
  }

  
  public void setAfferenteInvestimentiPNRR(Boolean afferenteInvestimentiPNRR) {
    this.afferenteInvestimentiPNRR = afferenteInvestimentiPNRR;
  }

  
  public Boolean isAcquisizioneCup() {
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

  
  public EnumNPA getContrattiDisposizioniParticolari() {
    return contrattiDisposizioniParticolari;
  }


  public void setContrattiDisposizioniParticolari(EnumNPA contrattiDisposizioniParticolari) {
    this.contrattiDisposizioniParticolari = contrattiDisposizioniParticolari;
  }


  public EnumNPA getMotivoEsclusioneOrdinarioSpeciale() {
    return motivoEsclusioneOrdinarioSpeciale;
  }

  
  public void setMotivoEsclusioneOrdinarioSpeciale(EnumNPA motivoEsclusioneOrdinarioSpeciale) {
    this.motivoEsclusioneOrdinarioSpeciale = motivoEsclusioneOrdinarioSpeciale;
  }

  
  public List<Finanziamento> getFinanziamenti() {
    return finanziamenti;
  }

  
  public void setFinanziamenti(List<Finanziamento> finanziamenti) {
    this.finanziamenti = finanziamenti;
  }

  
  public QuadroEconomicoConcorsiProgettazione getQuadroEconomicoConcorsiProgettazione() {
    return quadroEconomicoConcorsiProgettazione;
  }

  
  public void setQuadroEconomicoConcorsiProgettazione(QuadroEconomicoConcorsiProgettazione quadroEconomicoConcorsiProgettazione) {
    this.quadroEconomicoConcorsiProgettazione = quadroEconomicoConcorsiProgettazione;
  }

  public LottoP_24() {
    // TODO document why this constructor is empty
  }


  public Boolean getStrumentiElettroniciSpecifici() {
    return strumentiElettroniciSpecifici;
  }


  public void setStrumentiElettroniciSpecifici(Boolean strumentiElettroniciSpecifici) {
    this.strumentiElettroniciSpecifici = strumentiElettroniciSpecifici;
  }

}
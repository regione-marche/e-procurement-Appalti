/*
 * Created on 09-gen-2024
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
public class AggiudicazioneA2_32 {

  private String cig;
  private List<Offerte> offertePresentate;
  private Long numeroOfferteAmmesse;
  private Long nImpreseEscluseInsufficientiGiustificazioni;
  private QuadroEconomicoConcessioni quadroEconomicoConcessioni;
  private DatiBaseEsitoRisultatoProcedura datiBaseRisultatoProcedura;
  private DatiBaseAggiudicazioneAppalto datiBaseAggiudicazioneAppalto;
  private DatiBaseAccessibilita datiBaseAccessibilita;
  
  
  public String getCig() {
    return cig;
  }

  
  public void setCig(String cig) {
    this.cig = cig;
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

  
  public Long getnImpreseEscluseInsufficientiGiustificazioni() {
    return nImpreseEscluseInsufficientiGiustificazioni;
  }

  
  public void setnImpreseEscluseInsufficientiGiustificazioni(Long nImpreseEscluseInsufficientiGiustificazioni) {
    this.nImpreseEscluseInsufficientiGiustificazioni = nImpreseEscluseInsufficientiGiustificazioni;
  }

  
  public QuadroEconomicoConcessioni getQuadroEconomicoConcessioni() {
    return quadroEconomicoConcessioni;
  }

  
  public void setQuadroEconomicoConcessioni(QuadroEconomicoConcessioni quadroEconomicoConcessioni) {
    this.quadroEconomicoConcessioni = quadroEconomicoConcessioni;
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

  
  public DatiBaseAccessibilita getDatiBaseAccessibilita() {
    return datiBaseAccessibilita;
  }

  
  public void setDatiBaseAccessibilita(DatiBaseAccessibilita datiBaseAccessibilita) {
    this.datiBaseAccessibilita = datiBaseAccessibilita;
  }

  public AggiudicazioneA2_32() {
    // TODO document why this constructor is empty
  }
  
}
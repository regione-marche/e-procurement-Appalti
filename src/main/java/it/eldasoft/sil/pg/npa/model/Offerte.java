/*
 * Created on 05-ott-2023
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
 * Dati delle offerte
 *
 * @author Alex.Mancini
 */
public class Offerte {

  private String idPartecipante;
  private Double importo;
  private Boolean aggiudicatario;
  private String ccnl;
  private Integer posizioneGraduatoria;
  private Double offertaEconomica;
  private Double offertaQualitativa;
  private Double offertaInAumento;
  private Boolean offertaMaggioreSogliaAnomalia;
  private Boolean impresaEsclusaAutomaticamente;
  private Boolean offertaAnomala;

  
  public String getIdPartecipante() {
    return idPartecipante;
  }

  
  public void setIdPartecipante(String idPartecipante) {
    this.idPartecipante = idPartecipante;
  }

  
  public Double getImporto() {
    return importo;
  }

  
  public void setImporto(Double importo) {
    this.importo = importo;
  }

  
  public Boolean getAggiudicatario() {
    return aggiudicatario;
  }

  
  public void setAggiudicatario(Boolean aggiudicatario) {
    this.aggiudicatario = aggiudicatario;
  }

  
  public String getCcnl() {
    return ccnl;
  }

  
  public void setCcnl(String ccnl) {
    this.ccnl = ccnl;
  }

  
  public Integer getPosizioneGraduatoria() {
    return posizioneGraduatoria;
  }

  
  public void setPosizioneGraduatoria(Integer posizioneGraduatoria) {
    this.posizioneGraduatoria = posizioneGraduatoria;
  }

  
  public Double getOffertaEconomica() {
    return offertaEconomica;
  }

  
  public void setOffertaEconomica(Double offertaEconomica) {
    this.offertaEconomica = offertaEconomica;
  }

  
  public Double getOffertaQualitativa() {
    return offertaQualitativa;
  }

  
  public void setOffertaQualitativa(Double offertaQualitativa) {
    this.offertaQualitativa = offertaQualitativa;
  }

  
  public Double getOffertaInAumento() {
    return offertaInAumento;
  }

  
  public void setOffertaInAumento(Double offertaInAumento) {
    this.offertaInAumento = offertaInAumento;
  }

  
  public Boolean getOffertaMaggioreSogliaAnomalia() {
    return offertaMaggioreSogliaAnomalia;
  }

  
  public void setOffertaMaggioreSogliaAnomalia(Boolean offertaMaggioreSogliaAnomalia) {
    this.offertaMaggioreSogliaAnomalia = offertaMaggioreSogliaAnomalia;
  }

  
  public Boolean getImpresaEsclusaAutomaticamente() {
    return impresaEsclusaAutomaticamente;
  }

  
  public void setImpresaEsclusaAutomaticamente(Boolean impresaEsclusaAutomaticamente) {
    this.impresaEsclusaAutomaticamente = impresaEsclusaAutomaticamente;
  }

  
  public Boolean getOffertaAnomala() {
    return offertaAnomala;
  }

  
  public void setOffertaAnomala(Boolean offertaAnomala) {
    this.offertaAnomala = offertaAnomala;
  }

  public Offerte() {
    // TODO document why this constructor is empty
  }
  
}
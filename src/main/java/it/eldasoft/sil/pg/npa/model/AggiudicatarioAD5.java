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

/**
 * Bean description:    
 * Dati dell'aggiudicatario
 *
 * @author Alex.Mancini
 */
public class AggiudicatarioAD5 {

  private String idPartecipante;
  private String codiceFiscale;
  private String denominazione;
  private Double importo;
  
  
  public String getIdPartecipante() {
    return idPartecipante;
  }

  
  public void setIdPartecipante(String idPartecipante) {
    this.idPartecipante = idPartecipante;
  }

  
  public String getCodiceFiscale() {
    return codiceFiscale;
  }

  
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }

  
  public String getDenominazione() {
    return denominazione;
  }

  
  public void setDenominazione(String denominazione) {
    this.denominazione = denominazione;
  }

  
  public Double getImporto() {
    return importo;
  }

  
  public void setImporto(Double importo) {
    this.importo = importo;
  }

  public AggiudicatarioAD5() {
    // TODO document why this constructor is empty
  }

}
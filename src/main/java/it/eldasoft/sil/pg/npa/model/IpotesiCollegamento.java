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

import java.util.List;

/**
 * Bean description:    
 * ipotesi di collegamento
 *
 * @author Alex.Mancini
 */
public class IpotesiCollegamento {

  private List<String> cigCollegato;
  private EnumNPA motivoCollegamento;
  
  
  public List<String> getCigCollegato() {
    return cigCollegato;
  }

  
  public void setCigCollegato(List<String> cigCollegato) {
    this.cigCollegato = cigCollegato;
  }

  
  public EnumNPA getMotivoCollegamento() {
    return motivoCollegamento;
  }

  
  public void setMotivoCollegamento(EnumNPA motivoCollegamento) {
    this.motivoCollegamento = motivoCollegamento;
  }

  public IpotesiCollegamento() {
    // TODO document why this constructor is empty
  }


  public IpotesiCollegamento(List<String> cigCollegato, EnumNPA motivoCollegamento) {
    this.cigCollegato = cigCollegato;
    this.motivoCollegamento = motivoCollegamento;
  }
  
}
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

import java.util.List;

/**
 * Bean description:    
 * Scheda relativa alla Costituzione della societa' di scopo.
 *
 * @author Alex.Mancini
 */
public class AnacFormS4 {

  private String cig;
  private List<Partecipante> partecipante;
  private EnumNPA motivoVariazione;
  
  
  public String getCig() {
    return cig;
  }

  
  public void setCig(String cig) {
    this.cig = cig;
  }

  
  public List<Partecipante> getPartecipante() {
    return partecipante;
  }

  
  public void setPartecipante(List<Partecipante> partecipante) {
    this.partecipante = partecipante;
  }

  
  public EnumNPA getMotivoVariazione() {
    return motivoVariazione;
  }

  
  public void setMotivoVariazione(EnumNPA motivoVariazione) {
    this.motivoVariazione = motivoVariazione;
  }

  public AnacFormS4() {
    // TODO document why this constructor is empty
  }
  
}
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

import java.util.List;

/**
 * Bean description:    
 * Dati relativi agli incarichi.
 *
 * @author Alex.Mancini
 */
public class ElencoIncarichi {

  private String cig;
  private List<Prestazione> prestazioni;
  private List<Incarico> incarichi;
  
  
  public String getCig() {
    return cig;
  }

  
  public void setCig(String cig) {
    this.cig = cig;
  }

  
  public List<Prestazione> getPrestazioni() {
    return prestazioni;
  }

  
  public void setPrestazioni(List<Prestazione> prestazioni) {
    this.prestazioni = prestazioni;
  }

  
  public List<Incarico> getIncarichi() {
    return incarichi;
  }

  
  public void setIncarichi(List<Incarico> incarichi) {
    this.incarichi = incarichi;
  }

  public ElencoIncarichi() {
    // TODO document why this constructor is empty
  }
  
}
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
 * Dato in comune con il Business Group Duration delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseDurata {

  private String dataInizio;
  private Integer durata;
  private String dataFine; 
  private EnumNPA altraDurata;
  
  
  public String getDataInizio() {
    return dataInizio;
  }

  
  public void setDataInizio(String dataInizio) {
    this.dataInizio = dataInizio;
  }

  
  public Integer getDurata() {
    return durata;
  }

  
  public void setDurata(Integer durata) {
    this.durata = durata;
  }

  
  public String getDataFine() {
    return dataFine;
  }

  
  public void setDataFine(String dataFine) {
    this.dataFine = dataFine;
  }

  
  public EnumNPA getAltraDurata() {
    return altraDurata;
  }

  
  public void setAltraDurata(EnumNPA altraDurata) {
    this.altraDurata = altraDurata;
  }

  public DatiBaseDurata() {
    // TODO document why this constructor is empty
  }
  
}
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
 * Oggetto che riporta tutti i dati del finanziamento
 *
 * @author Alex.Mancini
 */
public class Finanziamento {

  private EnumNPA tipoFinanziamento;
  private Double importo;
  
  
  public EnumNPA getTipoFinanziamento() {
    return tipoFinanziamento;
  }

  
  public void setTipoFinanziamento(EnumNPA tipoFinanziamento) {
    this.tipoFinanziamento = tipoFinanziamento;
  }

  
  public Double getImporto() {
    return importo;
  }

  
  public void setImporto(Double importo) {
    this.importo = importo;
  }

  public Finanziamento() {
    // TODO document why this constructor is empty
  }


  public Finanziamento(EnumNPA tipoFinanziamento, Double importo) {
    this.tipoFinanziamento = tipoFinanziamento;
    this.importo = importo;
  }
  

}
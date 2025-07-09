/*
 * Created on 09-nov-2023
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
 * Quadro economico concorsi progettazione.
 *
 * @author Alex.Mancini
 */
public class QuadroEconomicoConcorsiProgettazione {

  private Double impTotalePremi;
  private Double impStimatoProgSuccessiva;

  
  public Double getImpTotalePremi() {
    return impTotalePremi;
  }

  
  public void setImpTotalePremi(Double impTotalePremi) {
    this.impTotalePremi = impTotalePremi;
  }

  
  public Double getImpStimatoProgSuccessiva() {
    return impStimatoProgSuccessiva;
  }

  
  public void setImpStimatoProgSuccessiva(Double impStimatoProgSuccessiva) {
    this.impStimatoProgSuccessiva = impStimatoProgSuccessiva;
  }

  public QuadroEconomicoConcorsiProgettazione() {
    // TODO document why this constructor is empty
  }


  public QuadroEconomicoConcorsiProgettazione(Double impTotalePremi, Double impStimatoProgSuccessiva) {
    this.impTotalePremi = impTotalePremi;
    this.impStimatoProgSuccessiva = impStimatoProgSuccessiva;
  }
  

}
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

import java.math.BigDecimal;

/**
 * Bean description:    
 * Oggetto che riporta tutti i dati del quadro economico standard
 *
 * @author Alex.Mancini
 */
public class QuadroEconomico {

  private Double impLavori;
  private Double impServizi;
  private Double impForniture;
  private Double impTotaleSicurezza;
  private Double ulterioriSommeNoRibasso;
  private Double impProgettazione;
  private Double sommeOpzioniRinnovi;
  private Double sommeRipetizioni;
  private Double sommeADisposizione;
  
  
  public Double getImpLavori() {
    return impLavori;
  }

  
  public void setImpLavori(Double impLavori) {
    this.impLavori = impLavori;
  }

  
  public Double getImpServizi() {
    return impServizi;
  }

  
  public void setImpServizi(Double impServizi) {
    this.impServizi = impServizi;
  }

  
  public Double getImpForniture() {
    return impForniture;
  }

  
  public void setImpForniture(Double impForniture) {
    this.impForniture = impForniture;
  }

  
  public Double getImpTotaleSicurezza() {
    return impTotaleSicurezza;
  }

  
  public void setImpTotaleSicurezza(Double impTotaleSicurezza) {
    this.impTotaleSicurezza = impTotaleSicurezza;
  }

  
  public Double getUlterioriSommeNoRibasso() {
    return ulterioriSommeNoRibasso;
  }

  
  public void setUlterioriSommeNoRibasso(Double ulterioriSommeNoRibasso) {
    this.ulterioriSommeNoRibasso = ulterioriSommeNoRibasso;
  }

  
  public Double getImpProgettazione() {
    return impProgettazione;
  }

  
  public void setImpProgettazione(Double impProgettazione) {
    this.impProgettazione = impProgettazione;
  }

  
  public Double getSommeOpzioniRinnovi() {
    return sommeOpzioniRinnovi;
  }

  
  public void setSommeOpzioniRinnovi(Double sommeOpzioniRinnovi) {
    this.sommeOpzioniRinnovi = sommeOpzioniRinnovi;
  }

  
  public Double getSommeRipetizioni() {
    return sommeRipetizioni;
  }

  
  public void setSommeRipetizioni(Double sommeRipetizioni) {
    this.sommeRipetizioni = sommeRipetizioni;
  }

  
  public Double getSommeADisposizione() {
    return sommeADisposizione;
  }

  
  public void setSommeADisposizione(Double sommeADisposizione) {
    this.sommeADisposizione = sommeADisposizione;
  }

  public QuadroEconomico() {
    // TODO document why this constructor is empty
  }


  public QuadroEconomico(Long tipgen, Double impapp, Double impsic, Double onprge, Double impnrl, 
      Double imprin, Double impserv, Double imppror, Double impaltro, Double impsomme, String codiceScheda) {
    
    if(tipgen != null) {
      BigDecimal ris = null;
      if(impapp != null) {
        ris = BigDecimal.valueOf(impapp);
        if(impsic != null) {
          ris = ris.subtract(BigDecimal.valueOf(impsic));
        }
        if(onprge != null) {
          ris = ris.subtract(BigDecimal.valueOf(onprge));
        }
        if(impnrl != null) {
          ris = ris.subtract(BigDecimal.valueOf(impnrl));
        }

        switch (String.valueOf(tipgen)) {
        case "1":
          this.impLavori = ris.doubleValue();
          this.impForniture = Double.valueOf(0);
          this.impServizi = Double.valueOf(0);
          break;
        case "2":
      	this.impLavori = Double.valueOf(0);
          this.impForniture = ris.doubleValue();
          this.impServizi = Double.valueOf(0);
          break;
        case "3":
        	this.impLavori = Double.valueOf(0);
          this.impForniture = Double.valueOf(0);
          this.impServizi = ris.doubleValue();
          break;
        default:
          break;
        }
      }
    }

    this.impTotaleSicurezza = impsic;
    this.ulterioriSommeNoRibasso = impnrl;
    this.impProgettazione = onprge;
    
    BigDecimal sommaOpzioniRinnovi = BigDecimal.valueOf(0);
    if(imprin != null) {
      sommaOpzioniRinnovi = sommaOpzioniRinnovi.add(BigDecimal.valueOf(imprin));
    }
    if(imppror != null) {
      sommaOpzioniRinnovi = sommaOpzioniRinnovi.add(BigDecimal.valueOf(imppror));
    }
    if(impaltro != null) {
      sommaOpzioniRinnovi = sommaOpzioniRinnovi.add(BigDecimal.valueOf(impaltro));
    }
    
    if(!sommaOpzioniRinnovi.equals(BigDecimal.valueOf(0)) || "P7_2".equals(codiceScheda) ) {
      this.sommeOpzioniRinnovi = sommaOpzioniRinnovi.doubleValue();
    }

    if(impserv!=null) {
      this.sommeRipetizioni = impserv;
    } else if("P7_2".equals(codiceScheda)){
      this.sommeRipetizioni = Double.valueOf(0);
    }
    
    this.sommeADisposizione = impsomme; 
  }

  
  
}
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
 * Oggetto che riporta tutti i dati del quadro economico per le concessioni.
 *
 * @author Alex.Mancini
 */
public class QuadroEconomicoConcessioni {

  private Double impLavori;
  private Double impServizi;
  private Double impForniture;
  private Double finanziamentiCanoniPA;
  private Double entrateUtenza;
  private Double introitoAttivo;
  private Double impTotaleSicurezza;
  private Double ulterioriSommeNoRibasso;
  private Double sommeOpzioniRinnovi;
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

  
  public Double getFinanziamentiCanoniPA() {
    return finanziamentiCanoniPA;
  }

  
  public void setFinanziamentiCanoniPA(Double finanziamentiCanoniPA) {
    this.finanziamentiCanoniPA = finanziamentiCanoniPA;
  }

  
  public Double getEntrateUtenza() {
    return entrateUtenza;
  }

  
  public void setEntrateUtenza(Double entrateUtenza) {
    this.entrateUtenza = entrateUtenza;
  }

  
  public Double getIntroitoAttivo() {
    return introitoAttivo;
  }

  
  public void setIntroitoAttivo(Double introitoAttivo) {
    this.introitoAttivo = introitoAttivo;
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

  
  public Double getSommeOpzioniRinnovi() {
    return sommeOpzioniRinnovi;
  }

  
  public void setSommeOpzioniRinnovi(Double sommeOpzioniRinnovi) {
    this.sommeOpzioniRinnovi = sommeOpzioniRinnovi;
  }

  
  public Double getSommeADisposizione() {
    return sommeADisposizione;
  }

  
  public void setSommeADisposizione(Double sommeADisposizione) {
    this.sommeADisposizione = sommeADisposizione;
  }

  public QuadroEconomicoConcessioni() {
    // TODO document why this constructor is empty
  }


  public QuadroEconomicoConcessioni(Long tipgen, Double impapp, Double impsic, Double onprge, Double impnrl,
                                    Double impcanoni, Double imputenza, Double impintro, Double imprin, 
                                    Double imppror, Double impaltro, Double impsomme, Boolean isConcessione) 
  {
    if(Boolean.TRUE.equals(isConcessione)) {
      
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
      
      this.finanziamentiCanoniPA = impcanoni;
      this.entrateUtenza = imputenza;
      this.introitoAttivo = impintro;
      this.impTotaleSicurezza = impsic;
      this.ulterioriSommeNoRibasso = impnrl;
      

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

      if(!sommaOpzioniRinnovi.equals(BigDecimal.valueOf(0))) {
        this.sommeOpzioniRinnovi = sommaOpzioniRinnovi.doubleValue();
      }

      this.sommeADisposizione = impsomme; 
    }
  }

}
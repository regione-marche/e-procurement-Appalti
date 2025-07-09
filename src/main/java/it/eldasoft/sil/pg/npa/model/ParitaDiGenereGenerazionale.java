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
 * Previsione nel bando di gara, nell’avviso o nell’invito, dell’obbligo di assicurare, 
 * in caso di aggiudicazione del contratto, una quota pari almeno al 30 per cento, 
 * delle assunzioni necessarie per l'esecuzione del contratto o per la realizzazione 
 * di attività ad esso connesse o strumentali, sia all'occupazione giovanile sia all'occupazione 
 * femminile (articolo 47, comma 4, D.L. 77/2021)?
 *
 * @author Alex.Mancini
 */
public class ParitaDiGenereGenerazionale {

  private Boolean derogaAQ;
  private String quota30PerCento;
  private Double quotaInferioreOccupFemminile;
  private Double quotaInferioreOccupGiovanile;
  private List<EnumNPA> motivoDeroga;
  private List<EnumNPA> misurePremiali;
  private Boolean ulterioriMisurePremiali;
  
  
  public Boolean getDerogaAQ() {
    return derogaAQ;
  }

  
  public void setDerogaAQ(Boolean derogaAQ) {
    this.derogaAQ = derogaAQ;
  }

  
  public String getQuota30PerCento() {
    return quota30PerCento;
  }

  
  public void setQuota30PerCento(String quota30PerCento) {
    this.quota30PerCento = quota30PerCento;
  }

  
  public Double getQuotaInferioreOccupFemminile() {
    return quotaInferioreOccupFemminile;
  }

  
  public void setQuotaInferioreOccupFemminile(Double quotaInferioreOccupFemminile) {
    this.quotaInferioreOccupFemminile = quotaInferioreOccupFemminile;
  }

  
  public Double getQuotaInferioreOccupGiovanile() {
    return quotaInferioreOccupGiovanile;
  }

  
  public void setQuotaInferioreOccupGiovanile(Double quotaInferioreOccupGiovanile) {
    this.quotaInferioreOccupGiovanile = quotaInferioreOccupGiovanile;
  }

  
  public List<EnumNPA> getMotivoDeroga() {
    return motivoDeroga;
  }

  
  public void setMotivoDeroga(List<EnumNPA> motivoDeroga) {
    this.motivoDeroga = motivoDeroga;
  }

  
  public List<EnumNPA> getMisurePremiali() {
    return misurePremiali;
  }

  
  public void setMisurePremiali(List<EnumNPA> misurePremiali) {
    this.misurePremiali = misurePremiali;
  }

  public Boolean getUlterioriMisurePremiali() {
    return ulterioriMisurePremiali;
  }

  public void setUlterioriMisurePremiali(Boolean ulterioriMisurePremiali) {
    this.ulterioriMisurePremiali = ulterioriMisurePremiali;
  }

  public ParitaDiGenereGenerazionale() {
    // TODO document why this constructor is empty
  }


  public ParitaDiGenereGenerazionale(Boolean derogaAQ, Long quota30PerCento, Double quotaInferioreOccupFemminile,
      Double quotaInferioreOccupGiovanile, List<EnumNPA> motivoDeroga, List<EnumNPA> misurePremiali, Boolean ispnrr, Boolean isadesione, Boolean ulterioriMisurePremiali) {
    if(ispnrr != null && Boolean.TRUE.equals(ispnrr)) {
      if(isadesione != null && Boolean.TRUE.equals(isadesione) && derogaAQ != null) {
          this.derogaAQ = derogaAQ;
      }
      if(derogaAQ == null || Boolean.FALSE.equals(derogaAQ)) {
        if(quota30PerCento != null) {
          switch (String.valueOf(quota30PerCento)) {
          case "1":
            this.quota30PerCento = "NO";
            break;
          case "2":
            this.quota30PerCento = "SI";
            break;
          case "3":
            this.quota30PerCento = "SI_QUOTA_INFERIORE";
            break;
          default:
            break;
          }
        }
        if(misurePremiali != null)
          this.misurePremiali = misurePremiali;
      }
      if(quota30PerCento != null && Long.valueOf(3).equals(quota30PerCento)) {
        if(quotaInferioreOccupFemminile != null) {
          quotaInferioreOccupFemminile = Math.min(quotaInferioreOccupFemminile, 100.0); //limite massimo, se lo supera viene settato al massimo
          quotaInferioreOccupFemminile = Math.max(quotaInferioreOccupFemminile, 0.0); //limite minimo, se lo supera viene settato al minimo
          this.quotaInferioreOccupFemminile = quotaInferioreOccupFemminile;          
        }
        if(quotaInferioreOccupGiovanile != null) {
          quotaInferioreOccupGiovanile = Math.min(quotaInferioreOccupGiovanile, 100.0); //limite massimo, se lo supera viene settato al massimo
          quotaInferioreOccupGiovanile = Math.max(quotaInferioreOccupGiovanile, 0.0); //limite minimo, se lo supera viene settato al minimo
          this.quotaInferioreOccupGiovanile = quotaInferioreOccupGiovanile;          
        }
      }
      if(quota30PerCento != null && (Long.valueOf(1).equals(quota30PerCento) || Long.valueOf(3).equals(quota30PerCento)) && motivoDeroga != null) {
        this.motivoDeroga = motivoDeroga;
      }
      if (ulterioriMisurePremiali != null) {
        this.ulterioriMisurePremiali = ulterioriMisurePremiali;
      }
    }
  }
  
  

}
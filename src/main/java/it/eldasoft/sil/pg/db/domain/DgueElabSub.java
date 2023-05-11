package it.eldasoft.sil.pg.db.domain;

/**
 * Questa classe mappa la tabella DGUE_ELABSUB
 * @author Manuel.Bridda
 *
 */
public class DgueElabSub {
//chiave possibilmente autogenerato
  public static final String TABELLA = "DGUE_ELABSUB";
  private Long id;
  private String denominazione;
  private String cf;
  private Long ruolo;
  private String attivita;
  private String prestazione;
  private Double quota;
  private DgueElaborazione dgueElaborazione;
  
  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }
  
  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * @return the denominazione
   */
  public String getDenominazione() {
    return denominazione;
  }
  
  /**
   * @param denominazione the denominazione to set
   */
  public void setDenominazione(String denominazione) {
    this.denominazione = denominazione;
  }
  
  /**
   * @return the cf
   */
  public String getCf() {
    return cf;
  }
  /**
   * @param cf the cf to set
   */
  public void setCf(String cf) {
    this.cf = cf;
  }
  
  /**
   * @return the ruolo
   */
  public Long getRuolo() {
    return ruolo;
  }
  
  /**
   * @param ruolo the ruolo to set
   */
  public void setRuolo(Long ruolo) {
    this.ruolo = ruolo;
  }
  
  /**
   * @return the attivita
   */
  public String getAttivita() {
    return attivita;
  }
  
  /**
   * @param attivita the attivita to set
   */
  public void setAttivita(String attivita) {
    this.attivita = attivita;
  }
  
  /**
   * @return the prestazione
   */
  public String getPrestazione() {
    return prestazione;
  }
  
  /**
   * @param prestazione the prestazione to set
   */
  public void setPrestazione(String prestazione) {
    this.prestazione = prestazione;
  }
  
  /**
   * @return the quota
   */
  public Double getQuota() {
    return quota;
  }
  
  /**
   * @param quota the quota to set
   */
  public void setQuota(Double quota) {
    this.quota = quota;
  }

  /**
   * @return the dgueElaborazione
   */
  public DgueElaborazione getDgueElaborazione() {
    return dgueElaborazione;
  }
  
  /**
   * @param dgueElaborazione the dgueElaborazione to set
   */
  public void setDgueElaborazione(DgueElaborazione dgueElaborazione) {
    this.dgueElaborazione = dgueElaborazione;
  }

  @Override
  public String toString() {
    return "DgueElabSub ["
        + (id != null ? "id=" + id + ", " : "")
        + (dgueElaborazione != null ? "dgueElaborazione.id=" + dgueElaborazione.getId() + ", " : "")
        + (denominazione != null ? "denominazione=" + denominazione + ", " : "")
        + (cf != null ? "cf=" + cf + ", " : "")
        + "]";
  } 
}

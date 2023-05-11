package it.eldasoft.sil.pg.db.domain;

import java.util.Date;
import java.util.List;

/**
 * Questa classe mappa la tabella DGUE_BATCH
 * @author gabriele.nencini
 *
 */
public class DgueBatch {
  //chiave possibilmente autogenerato
  private Long id;
  private String codgar;
  private String ngara;
  private String codimp;
  private Integer busta;
  private Date datainserimento;
  private DgueBatchStatus stato;
  //property di collegamento con le entities figlie
  private List<DgueBatchDoc> batchDocList;
  
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
   * @return the codgar
   */
  public String getCodgar() {
    return codgar;
  }
  
  /**
   * @param codgar the codgar to set
   */
  public void setCodgar(String codgar) {
    this.codgar = codgar;
  }
  
  /**
   * @return the ngara
   */
  public String getNgara() {
    return ngara;
  }
  
  /**
   * @param ngara the ngara to set
   */
  public void setNgara(String ngara) {
    this.ngara = ngara;
  }
  
  /**
   * @return the codimp
   */
  public String getCodimp() {
    return codimp;
  }
  
  /**
   * @param codimp the codimp to set
   */
  public void setCodimp(String codimp) {
    this.codimp = codimp;
  }
  
  /**
   * @return the busta
   */
  public Integer getBusta() {
    return busta;
  }
  
  /**
   * @param busta the busta to set
   */
  public void setBusta(Integer busta) {
    this.busta = busta;
  }
  
  /**
   * @return the datainserimento
   */
  public Date getDatainserimento() {
    return datainserimento;
  }
  
  /**
   * @param datainserimento the datainserimento to set
   */
  public void setDatainserimento(Date datainserimento) {
    this.datainserimento = datainserimento;
  }
  
  /**
   * @return the stato
   */
  public DgueBatchStatus getStato() {
    return stato;
  }
  
  /**
   * @param stato the stato to set
   */
  public void setStato(DgueBatchStatus stato) {
    this.stato = stato;
  }
  
  /**
   * @return the batchDocList
   */
  public List<DgueBatchDoc> getBatchDocList() {
    return batchDocList;
  }
  
  /**
   * @param batchDocList the batchDocList to set
   */
  public void setBatchDocList(List<DgueBatchDoc> batchDocList) {
    this.batchDocList = batchDocList;
  }

  @Override
  public String toString() {
    return "DgueBatch ["
        + (id != null ? "id=" + id + ", " : "")
        + (codgar != null ? "codgar=" + codgar + ", " : "")
        + (ngara != null ? "ngara=" + ngara + ", " : "")
        + (codimp != null ? "codimp=" + codimp + ", " : "")
        + (busta != null ? "busta=" + busta + ", " : "")
        + (stato != null ? "stato=" + stato : "")
        + "]";
  }
  
}

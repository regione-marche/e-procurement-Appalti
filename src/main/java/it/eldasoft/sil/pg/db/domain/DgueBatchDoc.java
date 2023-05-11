package it.eldasoft.sil.pg.db.domain;

/**
 * Questa classe mappa la tabella DGUE_BATCH
 * @author gabriele.nencini
 *
 */
public class DgueBatchDoc {
  //chiave possibilmente autogenerato
  private Long id;
  private DgueBatch idBatch;
  private String dignomdoc;
  private String idprg;
  private Long iddocdig;
  private DgueBatchStatus stato;
  
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
   * @return the idBatch
   */
  public DgueBatch getIdBatch() {
    return idBatch;
  }
  
  /**
   * @param idBatch the idBatch to set
   */
  public void setIdBatch(DgueBatch idBatch) {
    this.idBatch = idBatch;
  }
  
  /**
   * @return the dignomdoc
   */
  public String getDignomdoc() {
    return dignomdoc;
  }
  
  /**
   * @param dignomdoc the dignomdoc to set
   */
  public void setDignomdoc(String dignomdoc) {
    this.dignomdoc = dignomdoc;
  }
  
  /**
   * @return the idprg
   */
  public String getIdprg() {
    return idprg;
  }
  
  /**
   * @param idprg the idprg to set
   */
  public void setIdprg(String idprg) {
    this.idprg = idprg;
  }
  
  /**
   * @return the iddocdig
   */
  public Long getIddocdig() {
    return iddocdig;
  }
  
  /**
   * @param iddocdig the iddocdig to set
   */
  public void setIddocdig(Long iddocdig) {
    this.iddocdig = iddocdig;
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

  @Override
  public String toString() {
    return "DgueBatchDoc ["
        + (id != null ? "id=" + id + ", " : "")
        + (idBatch != null ? "idBatch=" + idBatch + ", " : "")
        + (stato != null ? "stato=" + stato : "")
        + "]";
  }
  
  
}

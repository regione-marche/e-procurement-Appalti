package it.eldasoft.sil.pg.db.domain;

/**
 * Classse che mappa la tabella GARCPV
 * 
 * PK composta: ngara + numcpv
 * @author gabriele.nencini
 *
 */
public class Garcpv {
  private String ngara;
  private Integer numcpv;
  private String codcpv;
  private String tipcpv;
  
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
   * @return the numcpv
   */
  public Integer getNumcpv() {
    return numcpv;
  }
  
  /**
   * @param numcpv the numcpv to set
   */
  public void setNumcpv(Integer numcpv) {
    this.numcpv = numcpv;
  }
  
  /**
   * @return the codcpv
   */
  public String getCodcpv() {
    return codcpv;
  }
  
  /**
   * @param codcpv the codcpv to set
   */
  public void setCodcpv(String codcpv) {
    this.codcpv = codcpv;
  }
  
  /**
   * @return the tipcpv
   */
  public String getTipcpv() {
    return tipcpv;
  }
  
  /**
   * @param tipcpv the tipcpv to set
   */
  public void setTipcpv(String tipcpv) {
    this.tipcpv = tipcpv;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ngara == null) ? 0 : ngara.hashCode());
    result = prime * result + ((numcpv == null) ? 0 : numcpv.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Garcpv other = (Garcpv) obj;
    if (ngara == null) {
      if (other.ngara != null) return false;
    } else if (!ngara.equals(other.ngara)) return false;
    if (numcpv == null) {
      if (other.numcpv != null) return false;
    } else if (!numcpv.equals(other.numcpv)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Garcpv ["
        + (ngara != null ? "ngara=" + ngara + ", " : "")
        + (numcpv != null ? "numcpv=" + numcpv + ", " : "")
        + (codcpv != null ? "codcpv=" + codcpv + ", " : "")
        + (tipcpv != null ? "tipcpv=" + tipcpv : "")
        + "]";
  }
  
}

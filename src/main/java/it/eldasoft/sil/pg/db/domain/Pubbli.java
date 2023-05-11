package it.eldasoft.sil.pg.db.domain;

import java.util.Date;

/**
 * Classe che rappresenta l'entity pubbli
 * @author gabriele.nencini
 *
 */
public class Pubbli {
  private String codgar9;
  private Integer numpub;
  private String nprpub;
  private String tespub;
  private Date dinpub;
  private Date datpub;
  private String intpub;
  private Integer tippub;
  private Double imppub;
  private Date datfipub;
  private String titpub;
  private String navpub;
  private String navnum;
  private String urlpub;
  
  
  @Override
  public String toString() {
    return "Pubbli ["
        + (codgar9 != null ? "codgar9=" + codgar9 + ", " : "")
        + (numpub != null ? "numpub=" + numpub + ", " : "")
        + (tippub != null ? "tippub=" + tippub : "")
        + "]";
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((codgar9 == null) ? 0 : codgar9.hashCode());
    result = prime * result + ((numpub == null) ? 0 : numpub.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Pubbli other = (Pubbli) obj;
    if (codgar9 == null) {
      if (other.codgar9 != null) return false;
    } else if (!codgar9.equals(other.codgar9)) return false;
    if (numpub == null) {
      if (other.numpub != null) return false;
    } else if (!numpub.equals(other.numpub)) return false;
    return true;
  }

  /**
   * @return the codgar9
   */
  public String getCodgar9() {
    return codgar9;
  }
  
  /**
   * @param codgar9 the codgar9 to set
   */
  public void setCodgar9(String codgar9) {
    this.codgar9 = codgar9;
  }
  
  /**
   * @return the numpub
   */
  public Integer getNumpub() {
    return numpub;
  }
  
  /**
   * @param numpub the numpub to set
   */
  public void setNumpub(Integer numpub) {
    this.numpub = numpub;
  }
  
  /**
   * @return the nprpub
   */
  public String getNprpub() {
    return nprpub;
  }
  
  /**
   * @param nprpub the nprpub to set
   */
  public void setNprpub(String nprpub) {
    this.nprpub = nprpub;
  }
  
  /**
   * @return the tespub
   */
  public String getTespub() {
    return tespub;
  }
  
  /**
   * @param tespub the tespub to set
   */
  public void setTespub(String tespub) {
    this.tespub = tespub;
  }
  
  /**
   * @return the dinpub
   */
  public Date getDinpub() {
    return dinpub;
  }
  
  /**
   * @param dinpub the dinpub to set
   */
  public void setDinpub(Date dinpub) {
    this.dinpub = dinpub;
  }
  
  /**
   * @return the datpub
   */
  public Date getDatpub() {
    return datpub;
  }
  
  /**
   * @param datpub the datpub to set
   */
  public void setDatpub(Date datpub) {
    this.datpub = datpub;
  }
  
  /**
   * @return the intpub
   */
  public String getIntpub() {
    return intpub;
  }
  
  /**
   * @param intpub the intpub to set
   */
  public void setIntpub(String intpub) {
    this.intpub = intpub;
  }
  
  /**
   * @return the tippub
   */
  public Integer getTippub() {
    return tippub;
  }
  
  /**
   * @param tippub the tippub to set
   */
  public void setTippub(Integer tippub) {
    this.tippub = tippub;
  }
  
  /**
   * @return the imppub
   */
  public Double getImppub() {
    return imppub;
  }
  
  /**
   * @param imppub the imppub to set
   */
  public void setImppub(Double imppub) {
    this.imppub = imppub;
  }
  
  /**
   * @return the datfipub
   */
  public Date getDatfipub() {
    return datfipub;
  }
  
  /**
   * @param datfipub the datfipub to set
   */
  public void setDatfipub(Date datfipub) {
    this.datfipub = datfipub;
  }
  
  /**
   * @return the titpub
   */
  public String getTitpub() {
    return titpub;
  }

  /**
   * @param titpub the titpub to set
   */
  public void setTitpub(String titpub) {
    this.titpub = titpub;
  }

  /**
   * @return the navpub
   */
  public String getNavpub() {
    return navpub;
  }

  /**
   * @param navpub the navpub to set
   */
  public void setNavpub(String navpub) {
    this.navpub = navpub;
  }

  /**
   * @return the navnum
   */
  public String getNavnum() {
    return navnum;
  }

  /**
   * @param navnum the navnum to set
   */
  public void setNavnum(String navnum) {
    this.navnum = navnum;
  }

  /**
   * @return the urlpub
   */
  public String getUrlpub() {
    return urlpub;
  }

  /**
   * @param urlpub the urlpub to set
   */
  public void setUrlpub(String urlpub) {
    this.urlpub = urlpub;
  }
}

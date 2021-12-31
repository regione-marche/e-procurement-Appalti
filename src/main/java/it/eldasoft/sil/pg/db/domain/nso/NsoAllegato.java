package it.eldasoft.sil.pg.db.domain.nso;

import java.util.Date;

public class NsoAllegato {
  private Long id;
  private Long nso_ordini_id;
  private Integer nprogr;
  private Integer tipodoc;
  private String descrizione;
  private String idprg;
  private Long iddocdig;
  private Date datarilascio;
  private Integer statodoc;
  private Integer modfirma;
  private String urldoc;
  private String isarchi;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Long getNso_ordini_id() {
    return nso_ordini_id;
  }
  
  public void setNso_ordini_id(Long nso_ordini_id) {
    this.nso_ordini_id = nso_ordini_id;
  }
  
  public Integer getNprogr() {
    return nprogr;
  }
  
  public void setNprogr(Integer nprogr) {
    this.nprogr = nprogr;
  }
  
  public Integer getTipodoc() {
    return tipodoc;
  }
  
  public void setTipodoc(Integer tipodoc) {
    this.tipodoc = tipodoc;
  }
  
  public String getDescrizione() {
    return descrizione;
  }
  
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }
  
  public String getIdprg() {
    return idprg;
  }
  
  public void setIdprg(String idprg) {
    this.idprg = idprg;
  }
  
  public Long getIddocdig() {
    return iddocdig;
  }
  
  public void setIddocdig(Long iddocdig) {
    this.iddocdig = iddocdig;
  }
  
  public Date getDatarilascio() {
    return datarilascio;
  }
  
  public void setDatarilascio(Date datarilascio) {
    this.datarilascio = datarilascio;
  }
  
  public Integer getStatodoc() {
    return statodoc;
  }
  
  public void setStatodoc(Integer statodoc) {
    this.statodoc = statodoc;
  }
  
  public Integer getModfirma() {
    return modfirma;
  }
  
  public void setModfirma(Integer modfirma) {
    this.modfirma = modfirma;
  }
  
  public String getUrldoc() {
    return urldoc;
  }
  
  public void setUrldoc(String urldoc) {
    this.urldoc = urldoc;
  }
  
  public String getIsarchi() {
    return isarchi;
  }
  
  public void setIsarchi(String isarchi) {
    this.isarchi = isarchi;
  }

  @Override
  public String toString() {
    return "NsoAllegato ["
        + (id != null ? "id=" + id + ", " : "")
        + (nso_ordini_id != null ? "nso_ordini_id=" + nso_ordini_id + ", " : "")
        + (idprg != null ? "idprg=" + idprg + ", " : "")
        + (iddocdig != null ? "iddocdig=" + iddocdig : "")
        + "]";
  }
  
  
}

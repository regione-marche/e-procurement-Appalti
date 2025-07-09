package it.eldasoft.sil.pg.nadc.model;

import java.util.Date;

public class Aggiornamento {
  private Long idevento;
  private Date dataora;
  private String oggevento;
  private String codevento;
  private String syslogin;
  private String sysute;
  private Long syscon;
  
  public Long getIdevento() {
    return idevento;
  }
  
  public void setIdevento(Long idevento) {
    this.idevento = idevento;
  }
  
  public Date getDataora() {
    return dataora;
  }
  
  public void setDataora(Date dataora) {
    this.dataora = dataora;
  }
  
  public String getOggevento() {
    return oggevento;
  }
  
  public void setOggevento(String oggevento) {
    this.oggevento = oggevento;
  }
  
  public String getCodevento() {
    return codevento;
  }
  
  public void setCodevento(String codevento) {
    this.codevento = codevento;
  }
  
  public String getSyslogin() {
    return syslogin;
  }
  
  public void setSyslogin(String syslogin) {
    this.syslogin = syslogin;
  }
  
  public String getSysute() {
    return sysute;
  }
  
  public void setSysute(String sysute) {
    this.sysute = sysute;
  }
  
  public Long getSyscon() {
    return syscon;
  }
  
  public void setSyscon(Long syscon) {
    this.syscon = syscon;
  }

  public Aggiornamento() {
    
  }
  
  
}

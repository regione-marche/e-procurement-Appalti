package it.eldasoft.sil.pg.nadc.model;

import java.util.Date;

public class Stipula {
  private Long id;
  private String codice_gara_eproc;
  private Long ncont;
  private String cod_stipula;
  private String oggetto_contratto;
  private Double imp_contratto;
  private String creato_da;
  private String contract_manager;
  private String stato;
  private String tipo_contratto;
  private String num_contratto;
  private Date dt_contratto;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getCodice_gara_eproc() {
    return codice_gara_eproc;
  }
  
  public void setCodice_gara_eproc(String codice_gara_eproc) {
    this.codice_gara_eproc = codice_gara_eproc;
  }
  
  public Long getNcont() {
    return ncont;
  }
  
  public void setNcont(Long ncont) {
    this.ncont = ncont;
  }
  
  public String getCod_stipula() {
    return cod_stipula;
  }
  
  public void setCod_stipula(String cod_stipula) {
    this.cod_stipula = cod_stipula;
  }
  
  public String getOggetto_contratto() {
    return oggetto_contratto;
  }
  
  public void setOggetto_contratto(String oggetto_contratto) {
    this.oggetto_contratto = oggetto_contratto;
  }
  
  public Double getImp_contratto() {
    return imp_contratto;
  }
  
  public void setImp_contratto(Double imp_contratto) {
    this.imp_contratto = imp_contratto;
  }
  
  public String getCreato_da() {
    return creato_da;
  }
  
  public void setCreato_da(String creato_da) {
    this.creato_da = creato_da;
  }
  
  public String getContract_manager() {
    return contract_manager;
  }
  
  public void setContract_manager(String contract_manager) {
    this.contract_manager = contract_manager;
  }
  
  public String getStato() {
    return stato;
  }
  
  public void setStato(String stato) {
    this.stato = stato;
  }
  
  public String getTipo_contratto() {
    return tipo_contratto;
  }
  
  public void setTipo_contratto(String tipo_contratto) {
    this.tipo_contratto = tipo_contratto;
  }
  
  public String getNum_contratto() {
    return num_contratto;
  }
  
  public void setNum_contratto(String num_contratto) {
    this.num_contratto = num_contratto;
  }
  
  public Date getDt_contratto() {
    return dt_contratto;
  }
  
  public void setDt_contratto(Date dt_contratto) {
    this.dt_contratto = dt_contratto;
  }

  public Stipula() {
    
  }
  
  
}

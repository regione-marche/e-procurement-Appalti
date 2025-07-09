package it.eldasoft.sil.pg.nadc.model;

import java.util.Date;
import java.util.List;

public class Appalto {
  private String codice_gara_eproc;
  private String proc_telematica;
  private String cig;
  private Date data_acquisizione_cig;
  private String tipologia_cig;
  private String tipo_appalto;
  private String oggetto_lotto;
  private String cf_amministrazione;
  private String nome_amministrazione;
  private Long id_stato_cig;
  private String stato_cig;
  private String stato_cig_dettagliato;
  private Date dt_perfez_cig;
  private Double imp_base_gara;
  private Double valore_massimo_stimato;
  private Date dt_pubbl_piattaf;
  private Date dt_termine_pres_off;
  private String hh_termine_pres_off;
  private Long id_tipo_procedura_eproc;
  private String tipo_procedura_eproc;
  private Integer id_scelta_contraente_simog;
  private String scelta_contraente_simog;
  private Integer id_tipo_procedura_l190;
  private String tipo_procedura_l190;
  private String cond_proc_negoz;
  private Long num_lotti;
  private String rup_nome;
  private String rup_cognome;
  private String rup_cf;
  private String cpv;
  private Long id_criterio_aggiud;
  private String crtierio_aggiud;
  private Date dt_aggiudic;
  private Double imp_aggiudic;
  private List<Stipula> stipule;
  
  public String getCodice_gara_eproc() {
    return codice_gara_eproc;
  }
  
  public void setCodice_gara_eproc(String codice_gara_eproc) {
    this.codice_gara_eproc = codice_gara_eproc;
  }
  
  public String getProc_telematica() {
    return proc_telematica;
  }
  
  public void setProc_telematica(String proc_telematica) {
    this.proc_telematica = proc_telematica;
  }
  
  public String getCig() {
    return cig;
  }
  
  public void setCig(String cig) {
    this.cig = cig;
  }
  
  public Date getData_acquisizione_cig() {
    return data_acquisizione_cig;
  }
  
  public void setData_acquisizione_cig(Date data_acquisizione_cig) {
    this.data_acquisizione_cig = data_acquisizione_cig;
  }
  
  public String getTipologia_cig() {
    return tipologia_cig;
  }
  
  public void setTipologia_cig(String tipologia_cig) {
    this.tipologia_cig = tipologia_cig;
  }
  
  public String getTipo_appalto() {
    return tipo_appalto;
  }
  
  public void setTipo_appalto(String tipo_appalto) {
    this.tipo_appalto = tipo_appalto;
  }
  
  public String getOggetto_lotto() {
    return oggetto_lotto;
  }
  
  public void setOggetto_lotto(String oggetto_lotto) {
    this.oggetto_lotto = oggetto_lotto;
  }
  
  public String getCf_amministrazione() {
    return cf_amministrazione;
  }
  
  public void setCf_amministrazione(String cf_amministrazione) {
    this.cf_amministrazione = cf_amministrazione;
  }
  
  public String getNome_amministrazione() {
    return nome_amministrazione;
  }
  
  public void setNome_amministrazione(String nome_amministrazione) {
    this.nome_amministrazione = nome_amministrazione;
  }
  
  public Long getId_stato_cig() {
    return id_stato_cig;
  }
  
  public void setId_stato_cig(Long id_stato_cig) {
    this.id_stato_cig = id_stato_cig;
  }
  
  public String getStato_cig() {
    return stato_cig;
  }
  
  public void setStato_cig(String stato_cig) {
    this.stato_cig = stato_cig;
  }
  
  public String getStato_cig_dettagliato() {
    return stato_cig_dettagliato;
  }
  
  public void setStato_cig_dettagliato(String stato_cig_dettagliato) {
    this.stato_cig_dettagliato = stato_cig_dettagliato;
  }
  
  public Date getDt_perfez_cig() {
    return dt_perfez_cig;
  }
  
  public void setDt_perfez_cig(Date dt_perfez_cig) {
    this.dt_perfez_cig = dt_perfez_cig;
  }
  
  public Double getImp_base_gara() {
    return imp_base_gara;
  }
  
  public void setImp_base_gara(Double imp_base_gara) {
    this.imp_base_gara = imp_base_gara;
  }
  
  public Double getValore_massimo_stimato() {
    return valore_massimo_stimato;
  }
  
  public void setValore_massimo_stimato(Double valore_massimo_stimato) {
    this.valore_massimo_stimato = valore_massimo_stimato;
  }
  
  public Date getDt_pubbl_piattaf() {
    return dt_pubbl_piattaf;
  }
  
  public void setDt_pubbl_piattaf(Date dt_pubbl_piattaf) {
    this.dt_pubbl_piattaf = dt_pubbl_piattaf;
  }
  
  public Date getDt_termine_pres_off() {
    return dt_termine_pres_off;
  }
  
  public void setDt_termine_pres_off(Date dt_termine_pres_off) {
    this.dt_termine_pres_off = dt_termine_pres_off;
  }
  
  public String getHh_termine_pres_off() {
    return hh_termine_pres_off;
  }
  
  public void setHh_termine_pres_off(String hh_termine_pres_off) {
    this.hh_termine_pres_off = hh_termine_pres_off;
  }
  
  public Long getId_tipo_procedura_eproc() {
    return id_tipo_procedura_eproc;
  }
  
  public void setId_tipo_procedura_eproc(Long id_tipo_procedura_eproc) {
    this.id_tipo_procedura_eproc = id_tipo_procedura_eproc;
  }
  
  public String getTipo_procedura_eproc() {
    return tipo_procedura_eproc;
  }
  
  public void setTipo_procedura_eproc(String tipo_procedura_eproc) {
    this.tipo_procedura_eproc = tipo_procedura_eproc;
  }
  
  public Integer getId_scelta_contraente_simog() {
    return id_scelta_contraente_simog;
  }
  
  public void setId_scelta_contraente_simog(Integer id_scelta_contraente_simog) {
    this.id_scelta_contraente_simog = id_scelta_contraente_simog;
  }
  
  public String getScelta_contraente_simog() {
    return scelta_contraente_simog;
  }
  
  public void setScelta_contraente_simog(String scelta_contraente_simog) {
    this.scelta_contraente_simog = scelta_contraente_simog;
  }
  
  public Integer getId_tipo_procedura_l190() {
    return id_tipo_procedura_l190;
  }
  
  public void setId_tipo_procedura_l190(Integer id_tipo_procedura_l190) {
    this.id_tipo_procedura_l190 = id_tipo_procedura_l190;
  }
  
  public String getTipo_procedura_l190() {
    return tipo_procedura_l190;
  }
  
  public void setTipo_procedura_l190(String tipo_procedura_l190) {
    this.tipo_procedura_l190 = tipo_procedura_l190;
  }
  
  public String getCond_proc_negoz() {
    return cond_proc_negoz;
  }
  
  public void setCond_proc_negoz(String cond_proc_negoz) {
    this.cond_proc_negoz = cond_proc_negoz;
  }
  
  public Long getNum_lotti() {
    return num_lotti;
  }
  
  public void setNum_lotti(Long num_lotti) {
    this.num_lotti = num_lotti;
  }
  
  public String getRup_nome() {
    return rup_nome;
  }
  
  public void setRup_nome(String rup_nome) {
    this.rup_nome = rup_nome;
  }
  
  public String getRup_cognome() {
    return rup_cognome;
  }
  
  public void setRup_cognome(String rup_cognome) {
    this.rup_cognome = rup_cognome;
  }
  
  public String getRup_cf() {
    return rup_cf;
  }
  
  public void setRup_cf(String rup_cf) {
    this.rup_cf = rup_cf;
  }
  
  public String getCpv() {
    return cpv;
  }
  
  public void setCpv(String cpv) {
    this.cpv = cpv;
  }
  
  public Long getId_criterio_aggiud() {
    return id_criterio_aggiud;
  }
  
  public void setId_criterio_aggiud(Long id_criterio_aggiud) {
    this.id_criterio_aggiud = id_criterio_aggiud;
  }
  
  public String getCrtierio_aggiud() {
    return crtierio_aggiud;
  }
  
  public void setCrtierio_aggiud(String crtierio_aggiud) {
    this.crtierio_aggiud = crtierio_aggiud;
  }
  
  public Date getDt_aggiudic() {
    return dt_aggiudic;
  }
  
  public void setDt_aggiudic(Date dt_aggiudic) {
    this.dt_aggiudic = dt_aggiudic;
  }
  
  public Double getImp_aggiudic() {
    return imp_aggiudic;
  }
  
  public void setImp_aggiudic(Double imp_aggiudic) {
    this.imp_aggiudic = imp_aggiudic;
  }
  
  public List<Stipula> getStipule() {
    return stipule;
  }
  
  public void setStipule(List<Stipula> stipule) {
    this.stipule = stipule;
  }

  public Appalto() {
    
  }
  
  
}

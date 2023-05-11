package it.eldasoft.sil.pg.db.domain.nso;

import java.io.Serializable;


public class Fornitore implements Comparable<Fornitore>, Serializable {
  private static final long serialVersionUID = 1L;
  private Integer id;
  private Integer nso_ordini_id;
  private String codimp;
  private String nomimp;
  private String endpoint;
  private String cfimp;
  private String via;
  private String citta;
  private String cap;
  private String codnaz;
  private String persona_rif;
  private String type;
  
  public Integer getId() {
    return id;
  }
  
  public void setId(Integer id) {
    this.id = id;
  }
  
  public Integer getNso_ordini_id() {
    return nso_ordini_id;
  }
  
  public void setNso_ordini_id(Integer nso_ordini_id) {
    this.nso_ordini_id = nso_ordini_id;
  }
  
  public String getCodimp() {
    return codimp;
  }
  
  public void setCodimp(String codimp) {
    this.codimp = codimp;
  }
  
  public String getNomimp() {
    return nomimp;
  }
  
  public void setNomimp(String nomimp) {
    this.nomimp = nomimp;
  }
  
  public String getEndpoint() {
    return endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }
  
  public String getCfimp() {
    return cfimp;
  }
  
  public void setCfimp(String cfimp) {
    this.cfimp = cfimp;
  }
  
  public String getVia() {
    return via;
  }
  
  public void setVia(String via) {
    this.via = via;
  }
  
  public String getCitta() {
    return citta;
  }
  
  public void setCitta(String citta) {
    this.citta = citta;
  }
  
  public String getCap() {
    return cap;
  }
  
  public void setCap(String cap) {
    this.cap = cap;
  }
  
  public String getCodnaz() {
    return codnaz;
  }
  
  public void setCodnaz(String codnaz) {
    this.codnaz = codnaz;
  }
  
  public String getPersona_rif() {
    return persona_rif;
  }
  
  public void setPersona_rif(String persona_rif) {
    this.persona_rif = persona_rif;
  }
  
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Fornitore ["
        + (id != null ? "id=" + id + ", " : "")
        + (nso_ordini_id != null ? "nso_ordini_id=" + nso_ordini_id + ", " : "")
        + (codimp != null ? "codimp=" + codimp : "")
        + "]";
  }

  @Override
  public int compareTo(Fornitore o) {
    if(o==null) return 1;
    return this.getCodimp().compareTo(o.getCodimp());
  }
  
}

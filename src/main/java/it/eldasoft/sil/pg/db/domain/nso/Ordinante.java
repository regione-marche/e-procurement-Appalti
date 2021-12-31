package it.eldasoft.sil.pg.db.domain.nso;

import java.io.Serializable;

public class Ordinante implements Serializable, Comparable<Ordinante> {
  private static final long serialVersionUID = 1L;
  private Integer id;
  private Integer nso_ordini_id;
  private Integer tipo;
  private String codein;
  private String nomein;
  private String endpoint;
  private String via;
  private String citta;
  private String cap;
  private String codnaz;
  private String piva;
  private String note;
  private String codipa;
  
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
  
  public Integer getTipo() {
    return tipo;
  }
  
  public void setTipo(Integer tipo) {
    this.tipo = tipo;
  }
  
  public String getCodein() {
    return codein;
  }
  
  public void setCodein(String codein) {
    this.codein = codein;
  }
  
  public String getNomein() {
    return nomein;
  }
  
  public void setNomein(String nomein) {
    this.nomein = nomein;
  }
  
  public String getEndpoint() {
    return endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
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
  
  public String getPiva() {
    return piva;
  }
  
  public void setPiva(String piva) {
    this.piva = piva;
  }
  
  public String getNote() {
    return note;
  }
  
  public void setNote(String note) {
    this.note = note;
  }
  
  public String getCodipa() {
    return codipa;
  }

  public void setCodipa(String codipa) {
    this.codipa = codipa;
  }

  @Override
  public int compareTo(Ordinante o) {
    if(o==null) return 1;
    return this.getNso_ordini_id().compareTo(o.getNso_ordini_id());
  }

  @Override
  public String toString() {
    return "Ordinante ["
        + (id != null ? "id=" + id + ", " : "")
        + (nso_ordini_id != null ? "nso_ordini_id=" + nso_ordini_id + ", " : "")
        + (tipo != null ? "tipo=" + tipo + ", " : "")
        + (codein != null ? "codein=" + codein : "")
        + "]";
  }
}

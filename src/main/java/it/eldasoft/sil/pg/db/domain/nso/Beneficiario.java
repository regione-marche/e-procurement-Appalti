package it.eldasoft.sil.pg.db.domain.nso;

import java.io.Serializable;


public class Beneficiario implements Comparable<Beneficiario>, Serializable {
  private Integer id; 
  private Integer nso_ordini_id;
  private String denominazione;
  private String contatto_rif;
  private String indirizzo;
  private String localita; 
  private String cap;
  private String citta;
  private String codnaz;
  
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
  
  public String getDenominazione() {
    return denominazione;
  }
  
  public void setDenominazione(String denominazione) {
    this.denominazione = denominazione;
  }
  
  public String getContatto_rif() {
    return contatto_rif;
  }
  
  public void setContatto_rif(String contatto_rif) {
    this.contatto_rif = contatto_rif;
  }
  
  public String getIndirizzo() {
    return indirizzo;
  }
  
  public void setIndirizzo(String indirizzo) {
    this.indirizzo = indirizzo;
  }
  
  public String getLocalita() {
    return localita;
  }
  
  public void setLocalita(String localita) {
    this.localita = localita;
  }
  
  public String getCap() {
    return cap;
  }
  
  public void setCap(String cap) {
    this.cap = cap;
  }
  
  public String getCitta() {
    return citta;
  }
  
  public void setCitta(String citta) {
    this.citta = citta;
  }
  
  public String getCodnaz() {
    return codnaz;
  }
  
  public void setCodnaz(String codnaz) {
    this.codnaz = codnaz;
  }
  
  @Override
  public String toString() {
    return "Beneficiario ["
        + (id != null ? "id=" + id + ", " : "")
        + (nso_ordini_id != null ? "nso_ordini_id=" + nso_ordini_id + ", " : "")
        + (denominazione != null ? "denominazione=" + denominazione : "")
        + "]";
  }

  @Override
  public int compareTo(Beneficiario arg0) {
    if(arg0 == null) return 1;
    return this.getNso_ordini_id().compareTo(arg0.getNso_ordini_id());
  }
  
}

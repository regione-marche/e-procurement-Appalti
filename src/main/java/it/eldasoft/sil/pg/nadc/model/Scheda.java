package it.eldasoft.sil.pg.nadc.model;

import com.fasterxml.jackson.databind.JsonNode;

public class Scheda {
  private String codice;
  private String idSchedaNpa;
  private RupScheda rup;
  private String data_inserimento;
  private String data_invio;
  private String data_conferma;
  private JsonNode dati;
  private String codice_eform;
  private String codice_espd;
  
  public String getCodice() {
    return codice;
  }
  
  public void setCodice(String codice) {
    this.codice = codice;
  }
  
  public String getIdSchedaNpa() {
    return idSchedaNpa;
  }
  
  public void setIdSchedaNpa(String idSchedaNpa) {
    this.idSchedaNpa = idSchedaNpa;
  }
  
  public RupScheda getRup() {
    return rup;
  }
  
  public void setRup(RupScheda rup) {
    this.rup = rup;
  }
  
  public String getData_inserimento() {
    return data_inserimento;
  }
  
  public void setData_inserimento(String data_inserimento) {
    this.data_inserimento = data_inserimento;
  }
  
  public String getData_invio() {
    return data_invio;
  }
  
  public void setData_invio(String data_invio) {
    this.data_invio = data_invio;
  }
  
  public String getData_conferma() {
    return data_conferma;
  }
  
  public void setData_conferma(String data_conferma) {
    this.data_conferma = data_conferma;
  }
  
  public JsonNode getDati() {
    return dati;
  }
  
  public void setDati(JsonNode jsonNode) {
    this.dati = jsonNode;
  }
  
  public String getCodice_eform() {
    return codice_eform;
  }
  
  public void setCodice_eform(String codice_eform) {
    this.codice_eform = codice_eform;
  }
  
  public String getCodice_espd() {
    return codice_espd;
  }
  
  public void setCodice_espd(String codice_espd) {
    this.codice_espd = codice_espd;
  }

  public Scheda() {
    
  }
  
  public Scheda deepCopy() {
    Scheda copy = new Scheda();
    copy.setCodice(this.codice);
    copy.setIdSchedaNpa(this.idSchedaNpa);
    copy.setRup(this.rup != null ? this.rup.deepCopy() : null); // Assumendo che RupScheda abbia un metodo deepCopy
    copy.setData_inserimento(this.data_inserimento);
    copy.setData_invio(this.data_invio);
    copy.setData_conferma(this.data_conferma);
    copy.setDati(this.dati != null ? this.dati.deepCopy() : null); // deepCopy di JsonNode
    copy.setCodice_eform(this.codice_eform);
    copy.setCodice_espd(this.codice_espd);
    return copy;
  }
  
  
}

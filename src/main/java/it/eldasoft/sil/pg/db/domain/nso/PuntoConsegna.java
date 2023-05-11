package it.eldasoft.sil.pg.db.domain.nso;

import java.io.Serializable;

public class PuntoConsegna implements Serializable, Comparable<PuntoConsegna> {
  private static final long serialVersionUID = 1L;
  public static final String CONSEGNA_DOMICILIARE = "Consegna domiciliare";

  private Integer id;
  private Integer ordine;
  private String codein;  
  private String cod_punto_cons;
  private String indirizzo;
  private String localita;
  private String cap;
  private String citta;
  private String codnaz;
  private String altre_indic;
  private String altro_punto_cons;
  private String cons_domicilio;
  
  public Integer getId() {
    return id;
  }
  
  public void setPid(Integer id) {
    this.id = id;
  }
  
  public Integer getOrdine() {
    return ordine;
  }

  public void setOrdine(Integer ordine) {
    this.ordine = ordine;
  }
  
  public String getCodein() {
    return codein;
  }
  
  public void setCodein(String codein) {
    this.codein = codein;
  }

  public String getCod_punto_cons() {
    return cod_punto_cons;
  }

  public void setCod_punto_cons(String cod_punto_cons) {
    this.cod_punto_cons = cod_punto_cons;
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
  
  public String getAltre_indic() {
    return altre_indic;
  }
  
  public void setAltre_indic(String altre_indic) {
    this.altre_indic = altre_indic;
  }
  
  public String getAltro_punto_cons() {
    return altro_punto_cons;
  }
  
  public void setAltro_punto_cons(String altro_punto_cons) {
    this.altro_punto_cons = altro_punto_cons;
  }
  
  public String getCons_domicilio() {
    return cons_domicilio;
  }
  
  public void setCons_domicilio(String cons_domicilio) {
    this.cons_domicilio = cons_domicilio;
  }

  @Override
  public int compareTo(PuntoConsegna arg0) {
    if(arg0==null) return 1;
    return this.id.compareTo(arg0.getId());
  }

  @Override
  public String toString() {
    return "PuntoConsegna ["
        + (id != null ? "id=" + id + ", " : "")
        + (ordine != null ? "ordine=" + ordine + ", " : "")
        + (codein != null ? "codein=" + codein + ", " : "")
        + (cod_punto_cons != null ? "cod_punto_cons=" + cod_punto_cons : "")
        + "]";
  }

}

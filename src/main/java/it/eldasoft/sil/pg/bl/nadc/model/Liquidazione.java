package it.eldasoft.sil.pg.bl.nadc.model;


public class Liquidazione {
  private String id;
  private String numero_documento;
  private String tipo_documento;
  private String divisa_originaria;
  private String numero_CIG;
  private Double importo_Contratto;
  private Double importo_Ordine;
  private Double importo_EM;
  private Double importo_Liquidato;

  // Getter & Setter
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNumero_documento() {
    return numero_documento;
  }

  public void setNumero_documento(String numero_documento) {
    this.numero_documento = numero_documento;
  }

  public String getTipo_documento() {
    return tipo_documento;
  }

  public void setTipo_documento(String tipo_documento) {
    this.tipo_documento = tipo_documento;
  }

  public String getDivisa_originaria() {
    return divisa_originaria;
  }

  public void setDivisa_originaria(String divisa_originaria) {
    this.divisa_originaria = divisa_originaria;
  }

  public String getNumero_CIG() {
    return numero_CIG;
  }

  public void setNumero_CIG(String numero_CIG) {
    this.numero_CIG = numero_CIG;
  }

  public Double getImporto_Contratto() {
    return importo_Contratto;
  }

  public void setImporto_Contratto(Double importo_Contratto) {
    this.importo_Contratto = importo_Contratto;
  }

  public Double getImporto_Ordine() {
    return importo_Ordine;
  }

  public void setImporto_Ordine(Double importo_Ordine) {
    this.importo_Ordine = importo_Ordine;
  }

  public Double getImporto_EM() {
    return importo_EM;
  }

  public void setImporto_EM(Double importo_EM) {
    this.importo_EM = importo_EM;
  }

  public Double getImporto_Liquidato() {
    return importo_Liquidato;
  }

  public void setImporto_Liquidato(Double importo_Liquidato) {
    this.importo_Liquidato = importo_Liquidato;
  }
}

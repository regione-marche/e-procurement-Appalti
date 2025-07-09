package it.eldasoft.sil.pg.ws.rest.durc.richiestaDURC.entity;

import java.util.Date;

public class RispostaCfRichiestaDurc {
  private Integer id;
  private String codiceFiscale;
  private String esitoRichiestaDurc;
  private String dataEsito;
  private String protocollo;
  private String denominazione;
  private String sedeLegale;
  private String dataRichiesta;
  private String dataFineValidita;
  private byte[] documentoPDF;
  private Boolean annullato;
  private String dataAnnullamento;
  private String motivoAnnullamento;


  public Integer getId() {
    return id;
  }


  public void setId(Integer id) {
    this.id = id;
  }


  public String getCodiceFiscale() {
    return codiceFiscale;
  }


  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }


  public String getEsitoRichiestaDurc() {
    return esitoRichiestaDurc;
  }


  public void setEsitoRichiestaDurc(String esitoRichiestaDurc) {
    this.esitoRichiestaDurc = esitoRichiestaDurc;
  }


  public String getDataEsito() {
    return dataEsito;
  }


  public void setDataEsito(String dataEsito) {
    this.dataEsito = dataEsito;
  }


  public String getProtocollo() {
    return protocollo;
  }


  public void setProtocollo(String protocollo) {
    this.protocollo = protocollo;
  }


  public String getDenominazione() {
    return denominazione;
  }


  public void setDenominazione(String denominazione) {
    this.denominazione = denominazione;
  }


  public String getSedeLegale() {
    return sedeLegale;
  }


  public void setSedeLegale(String sedeLegale) {
    this.sedeLegale = sedeLegale;
  }


  public String getDataRichiesta() {
    return dataRichiesta;
  }


  public void setDataRichiesta(String dataRichiesta) {
    this.dataRichiesta = dataRichiesta;
  }


  public String getDataFineValidita() {
    return dataFineValidita;
  }


  public void setDataFineValidita(String dataFineValidita) {
    this.dataFineValidita = dataFineValidita;
  }


  public byte[] getDocumentoPDF() {
    return documentoPDF;
  }


  public void setDocumentoPDF(byte[] documentoPDF) {
    this.documentoPDF = documentoPDF;
  }


  public Boolean isAnnullato() {
    return annullato;
  }


  public void setAnnullato(Boolean annullato) {
    this.annullato = annullato;
  }


  public String getDataAnnullamento() {
    return dataAnnullamento;
  }


  public void setDataAnnullamento(String dataAnnullamento) {
    this.dataAnnullamento = dataAnnullamento;
  }


  public String getMotivoAnnullamento() {
    return motivoAnnullamento;
  }


  public void setMotivoAnnullamento(String motivoAnnullamento) {
    this.motivoAnnullamento = motivoAnnullamento;
  }

  public RispostaCfRichiestaDurc() {

  }


}

/*
 * Created on 16-nov-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

/**
 * Bean description:    
 * Dati relativi alla persona fisica.
 *
 * @author Alex.Mancini
 */
public class DatiPersonaFisica {

  private String codiceFiscale;
  private String cognome;
  private String nome;
  private String telefono;
  private String fax;
  private String email;
  private String indirizzo;
  private String cap;
  private EnumNPA codIstat;
  private Boolean incaricatoEstero;
  
  
  public String getCodiceFiscale() {
    return codiceFiscale;
  }

  
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }

  
  public String getCognome() {
    return cognome;
  }

  
  public void setCognome(String cognome) {
    this.cognome = cognome;
  }

  
  public String getNome() {
    return nome;
  }

  
  public void setNome(String nome) {
    this.nome = nome;
  }

  
  public String getTelefono() {
    return telefono;
  }

  
  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  
  public String getFax() {
    return fax;
  }

  
  public void setFax(String fax) {
    this.fax = fax;
  }

  
  public String getEmail() {
    return email;
  }

  
  public void setEmail(String email) {
    this.email = email;
  }

  
  public String getIndirizzo() {
    return indirizzo;
  }

  
  public void setIndirizzo(String indirizzo) {
    this.indirizzo = indirizzo;
  }

  
  public String getCap() {
    return cap;
  }

  
  public void setCap(String cap) {
    this.cap = cap;
  }

  
  public EnumNPA getCodIstat() {
    return codIstat;
  }

  
  public void setCodIstat(EnumNPA codIstat) {
    this.codIstat = codIstat;
  }

  
  public Boolean getIncaricatoEstero() {
    return incaricatoEstero;
  }

  
  public void setIncaricatoEstero(Boolean incaricatoEstero) {
    this.incaricatoEstero = incaricatoEstero;
  }

  public DatiPersonaFisica() {
    // TODO document why this constructor is empty
  }
  
}
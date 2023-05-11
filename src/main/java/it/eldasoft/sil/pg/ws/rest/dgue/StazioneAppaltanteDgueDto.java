package it.eldasoft.sil.pg.ws.rest.dgue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class StazioneAppaltanteDgueDto {
  private String partitaIva;
  private String sitoWeb;
  private String ragioneSociale;
  //indirizzo
  private String via;
  private String citta;
  private String cap;
  
  private String nazione;
  private String indrizzoProfiloCommittente;
  private String email;

  //responsabile procedimento
  
  /**
   * @return the partitaIva
   */
  public String getPartitaIva() {
    return partitaIva;
  }

  
  /**
   * @param partitaIva the partitaIva to set
   */
  public void setPartitaIva(String partitaIva) {
    this.partitaIva = partitaIva;
  }

  
  /**
   * @return the sitoWeb
   */
  public String getSitoWeb() {
    return sitoWeb;
  }

  
  /**
   * @param sitoWeb the sitoWeb to set
   */
  public void setSitoWeb(String sitoWeb) {
    this.sitoWeb = sitoWeb;
  }

  
  /**
   * @return the ragioneSociale
   */
  public String getRagioneSociale() {
    return ragioneSociale;
  }

  
  /**
   * @param ragioneSociale the ragioneSociale to set
   */
  public void setRagioneSociale(String ragioneSociale) {
    this.ragioneSociale = ragioneSociale;
  }

  
  /**
   * @return the via
   */
  public String getVia() {
    return via;
  }

  
  /**
   * @param via the via to set
   */
  public void setVia(String via) {
    this.via = via;
  }

  
  /**
   * @return the citta
   */
  public String getCitta() {
    return citta;
  }

  
  /**
   * @param citta the citta to set
   */
  public void setCitta(String citta) {
    this.citta = citta;
  }

  
  /**
   * @return the cap
   */
  public String getCap() {
    return cap;
  }

  
  /**
   * @param cap the cap to set
   */
  public void setCap(String cap) {
    this.cap = cap;
  }

  
  /**
   * @return the nazione
   */
  public String getNazione() {
    return nazione;
  }

  
  /**
   * @param nazione the nazione to set
   */
  public void setNazione(String nazione) {
    this.nazione = nazione;
  }


  /**
   * @return the indrizzoProfiloCommittente
   */
  public String getIndrizzoProfiloCommittente() {
    return indrizzoProfiloCommittente;
  }


  /**
   * @param indrizzoProfiloCommittente the indrizzoProfiloCommittente to set
   */
  public void setIndrizzoProfiloCommittente(String indrizzoProfiloCommittente) {
    this.indrizzoProfiloCommittente = indrizzoProfiloCommittente;
  }


  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }


  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
  
  
  
  
  
}

/*
 * Created on 01/giu/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.scp;

import java.io.Serializable;

/**
 * Dati generali di una impresa.
 *
 * @author Mirco.Franzoni
 */
public class ImpresaEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -6611269573839884401L;

  private String ragioneSociale;

  private Long formaGiuridica;
  
  //private String indirizzo;

  //private String numeroCivico;

  //private String localita;

  private String provincia;

  //private String cap;

  //private String telefono;

  //private String fax;

  private String codiceFiscale;

  private String partitaIva;

  //private String numeroCCIAA;
  
  private String nazione;

  public void setRagioneSociale(String ragioneSociale) {
      this.ragioneSociale = ragioneSociale;
  }

  public String getRagioneSociale() {
      return ragioneSociale;
  }

  public void setProvincia(String provincia) {
      this.provincia = provincia;
  }

  public String getProvincia() {
      return provincia;
  }
  
  public void setCodiceFiscale(String codiceFiscale) {
      this.codiceFiscale = codiceFiscale;
  }

  public String getCodiceFiscale() {
      return codiceFiscale;
  }

  public void setPartitaIva(String partitaIva) {
      this.partitaIva = partitaIva;
  }

  public String getPartitaIva() {
      return partitaIva;
  }

  public void setFormaGiuridica(Long formaGiuridica) {
      this.formaGiuridica = formaGiuridica;
  }

  public Long getFormaGiuridica() {
      return formaGiuridica;
  }

  public void setNazione(String nazione) {
    this.nazione = nazione;
  }

  public String getNazione() {
    return nazione;
  }
}

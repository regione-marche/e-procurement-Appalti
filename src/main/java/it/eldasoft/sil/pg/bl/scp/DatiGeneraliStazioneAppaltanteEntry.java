package it.eldasoft.sil.pg.bl.scp;
/*
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */

import java.io.Serializable;

/**
 * Dati generali di una stazione appaltante.
 * 
 * @author Mirco.Franzoni
 */

public class DatiGeneraliStazioneAppaltanteEntry implements Serializable {
    /**
     * UID.
     */
    private static final long serialVersionUID = -6600269573839884401L;
    
    private String denominazione;
    
    private String indirizzo;
    
    private String civico;
    
    private String codiceIstat;
    
    private String localita;
    
    private String provincia;  //PROV. - Agx15
    
    private String cap;

    private Integer codiceNazione;  
    
    private String telefono;
    
    private String fax;
    
    private String codiceFiscale;
    
    private Long tipoAmministrazione; // Tipo G_044 
    
    private String email;
    
    private String pec;
    
    private String cfAnac;
    
    private String iscuc;
    
    /**
   * @return the iscuc
   */
  public String getIscuc() {
    return iscuc;
  }

  /**
   * @param iscuc the iscuc to set
   */
  public void setIscuc(String isCuc) {
    this.iscuc = isCuc;
  }

  public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getCodiceIstat() {
        return codiceIstat;
    }
    
    public void setCodiceIstat(String codiceIstat) {
        this.codiceIstat = codiceIstat;
    }
    
    public Integer getCodiceNazione() {
        return codiceNazione;
    }
    
    public void setCodiceNazione(Integer codiceNazione) {
        this.codiceNazione = codiceNazione;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCivico() {
        return civico;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    public String getLocalita() {
        return localita;
    }

    public void setLocalita(String localita) {
        this.localita = localita;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
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

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public Long getTipoAmministrazione() {
        return tipoAmministrazione;
    }

    public void setTipoAmministrazione(Long tipoAmministrazione) {
        this.tipoAmministrazione = tipoAmministrazione;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPec() {
        return pec;
    }

    public void setPec(String pec) {
        this.pec = pec;
    }

    public void setCfAnac(String cfAnac) {
        this.cfAnac = cfAnac;
    }

    public String getCfAnac() {
        return cfAnac;
    }
    
}


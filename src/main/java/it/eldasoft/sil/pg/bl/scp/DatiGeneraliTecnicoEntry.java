/*
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
 * Dati generali di un tecnico.
 * 
 * @author Mirco.Franzoni
 */

public class DatiGeneraliTecnicoEntry implements Serializable {
    /**
     * UID.
     */
    private static final long serialVersionUID = -6611269573839884401L;

    private String cognome;
    
    private String nome;
    
    private String nomeCognome;
    
    private String indirizzo;
    
    private String civico;
    
    private String localita;
    
    private String provincia; //PROV. - Agx15
    
    private String cap;
    
    private String luogoIstat;
    
    private String telefono;
    
    private String fax;
    
    private String cfPiva;
    
    private String mail;

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

    public String getNomeCognome() {
        return nomeCognome;
    }

    public void setNomeCognome(String nomeCognome) {
        this.nomeCognome = nomeCognome;
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

    public String getCfPiva() {
        return cfPiva;
    }

    public void setCfPiva(String cfPiva) {
        this.cfPiva = cfPiva;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setLuogoIstat(String luogoIstat) {
        this.luogoIstat = luogoIstat;
    }

    public String getLuogoIstat() {
        return luogoIstat;
    }



}
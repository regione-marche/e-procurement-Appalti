/*
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.scp;

import it.eldasoft.sil.pg.bl.scp.AllegatoEntry;
import it.eldasoft.sil.pg.bl.scp.DatiGeneraliTecnicoEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Dati generali di un avviso.
 * 
 * @author Mirco.Franzoni
 */

public class PubblicaAvvisoEntry implements Serializable {
    /**
     * UID.
     */
    private static final long serialVersionUID = -6611269573839884401L;

    private String codiceFiscaleSA;

    private String ufficio;
    //private Long codiceSistema;

    private Long tipologia;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String data;

    private String descrizione;

    private String cig;
    
    private String cup;

    private String cui;
    
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String scadenza;
    
    private List<AllegatoEntry> documenti = new ArrayList<AllegatoEntry>();
    
    private DatiGeneraliTecnicoEntry rup;
    
    private Long idRicevuto;
    
    private String primaPubblicazioneSCP;

    public Long getTipologia() {
        return tipologia;
    }

    public void setTipologia(Long tipologia) {
        this.tipologia = tipologia;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCig() {
        return cig;
    }

    public void setCig(String cig) {
        this.cig = cig;
    }

    public void setCup(String cup) {
        this.cup = cup;
    }

    public String getCup() {
        return cup;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getCui() {
        return cui;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setDocumenti(List<AllegatoEntry> documenti) {
        this.documenti = documenti;
    }

    public List<AllegatoEntry> getDocumenti() {
        return documenti;
    }

    public void setIdRicevuto(Long idRicevuto) {
        this.idRicevuto = idRicevuto;
    }

    public Long getIdRicevuto() {
        return idRicevuto;
    }

    public void setRup(DatiGeneraliTecnicoEntry rup) {
        this.rup = rup;
    }

    public DatiGeneraliTecnicoEntry getRup() {
        return rup;
    }

    public void setCodiceFiscaleSA(String codiceFiscaleSA) {
        this.codiceFiscaleSA = codiceFiscaleSA;
    }

    public String getCodiceFiscaleSA() {
        return codiceFiscaleSA;
    }

    public void setUfficio(String ufficio) {
        this.ufficio = ufficio;
    }

    public String getUfficio() {
        return ufficio;
    }

    public void setPrimaPubblicazioneSCP(String primaPubblicazioneSCP) {
      this.primaPubblicazioneSCP = primaPubblicazioneSCP;
    }

    public String getPrimaPubblicazioneSCP() {
      return primaPubblicazioneSCP;
    }
}
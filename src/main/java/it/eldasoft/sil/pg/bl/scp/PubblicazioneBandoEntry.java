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

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Pubblicazione Bando.
 * 
 * @author Mirco.Franzoni
 */


public class PubblicazioneBandoEntry implements Serializable {
    /**
     * UID.
     */
    private static final long serialVersionUID = -6611269573839884401L;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataGuce;
    
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataGuri;
    
    ////@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataAlbo;
    
    private Long quotidianiNazionali;
    
    private Long quotidianiLocali;
    
    private String profiloCommittente;
    
    private String profiloInfTrasp;
    
    private String profiloOsservatorio;
    
    ////@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataBore;
    
    private Long periodici;

    public void setDataGuce(String dataGuce) {
        this.dataGuce = dataGuce;
    }

    public String getDataGuce() {
        return dataGuce;
    }

    public void setDataGuri(String dataGuri) {
        this.dataGuri = dataGuri;
    }

    public String getDataGuri() {
        return dataGuri;
    }

    public void setDataAlbo(String dataAlbo) {
        this.dataAlbo = dataAlbo;
    }

    public String getDataAlbo() {
        return dataAlbo;
    }

    public void setQuotidianiNazionali(Long quotidianiNazionali) {
        this.quotidianiNazionali = quotidianiNazionali;
    }

    public Long getQuotidianiNazionali() {
        return quotidianiNazionali;
    }

    public void setQuotidianiLocali(Long quotidianiLocali) {
        this.quotidianiLocali = quotidianiLocali;
    }

    public Long getQuotidianiLocali() {
        return quotidianiLocali;
    }

    public void setProfiloCommittente(String profiloCommittente) {
        this.profiloCommittente = profiloCommittente;
    }

    public String getProfiloCommittente() {
        return profiloCommittente;
    }

    public void setProfiloInfTrasp(String profiloInfTrasp) {
        this.profiloInfTrasp = profiloInfTrasp;
    }

    public String getProfiloInfTrasp() {
        return profiloInfTrasp;
    }

    public void setProfiloOsservatorio(String profiloOsservatorio) {
        this.profiloOsservatorio = profiloOsservatorio;
    }

    public String getProfiloOsservatorio() {
        return profiloOsservatorio;
    }

    public void setDataBore(String dataBore) {
        this.dataBore = dataBore;
    }

    public String getDataBore() {
        return dataBore;
    }

    public void setPeriodici(Long periodici) {
        this.periodici = periodici;
    }

    public Long getPeriodici() {
        return periodici;
    }

}
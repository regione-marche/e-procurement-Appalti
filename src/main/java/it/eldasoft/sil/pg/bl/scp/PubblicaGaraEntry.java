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

import it.eldasoft.sil.pg.bl.scp.DatiGeneraliTecnicoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicaLottoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicazioneBandoEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Dati di pubblicazione di una gara.
 *
 * @author Mirco.Franzoni
 */
public class PubblicaGaraEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -6611269573839884401L;
  
  private String oggetto;
  
  //private int situazione;
  
  private String idAnac;
  
  private String codiceFiscaleSA;
    
  private String indirizzo;
  
  private String comune;
  
  private String provincia;
  
  private String ufficio;
  
  private String cigAccQuadro;
  
  private Long tipoSA;
  
  private String nomeSA;
  
  private String cfAgente;
  
  private String altreSA;
  
  private Long tipoProcedura;
  
  private String centraleCommittenza;
  
  private Double importoGara;
  
  private String settore;   //FLAG_ENTE_SPECIALE - W3z08
  
  private Long realizzazione;   //TIPO_APP - W3999
  
  private String saAgente;
  
  ////@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") 
  private String dataPubblicazione;
  
  ////@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") 
  //private String dataScadenza;
  
  //private String sommaUrgenza;
  
  //private Long durataAccordoQuadro;
  
  ////@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") 
  private String dataPerfezionamentoBando;
  
  private Long versioneSimog;
  
  //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")  
  private String primaPubblicazioneSCP;
  
  //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")  
  private String ultimaModificaSCP;
  
  private DatiGeneraliTecnicoEntry tecnicoRup;

  private PubblicazioneBandoEntry pubblicazioneBando;
  
  private List<PubblicaLottoEntry> lotti = new ArrayList<PubblicaLottoEntry>();

  private Long idRicevuto;

  public void setIdRicevuto(Long idRicevuto) {
    this.idRicevuto = idRicevuto;
  }

  public Long getIdRicevuto() {
    return idRicevuto;
  }
  
public void setTecnicoRup(DatiGeneraliTecnicoEntry tecnicoRup) {
    this.tecnicoRup = tecnicoRup;
}

public DatiGeneraliTecnicoEntry getTecnicoRup() {
    return tecnicoRup;
}

public void setLotti(List<PubblicaLottoEntry> lotti) {
    this.lotti = lotti;
}

public List<PubblicaLottoEntry> getLotti() {
    return lotti;
}

public void setSettore(String settore) {
    this.settore = settore;
}

public String getSettore() {
    return settore;
}

public void setSaAgente(String saAgente) {
    this.saAgente = saAgente;
}

public String getSaAgente() {
    return saAgente;
}

public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
}

public String getOggetto() {
    return oggetto;
}

/*
public void setSituazione(int situazione) {
    this.situazione = situazione;
}

public int getSituazione() {
    return situazione;
}

public void setProvenienzaDato(int provenienzaDato) {
    this.provenienzaDato = provenienzaDato;
}

public int getProvenienzaDato() {
    return provenienzaDato;
}
 */
public void setIdAnac(String idAnac) {
    this.idAnac = idAnac;
}

public String getIdAnac() {
    return idAnac;
}

public void setIndirizzo(String indirizzo) {
    this.indirizzo = indirizzo;
}

public String getIndirizzo() {
    return indirizzo;
}

public void setComune(String comune) {
    this.comune = comune;
}

public String getComune() {
    return comune;
}

public void setProvincia(String provincia) {
    this.provincia = provincia;
}

public String getProvincia() {
    return provincia;
}
/*
public void setCodiceCentroCosto(String codiceCentroCosto) {
    this.codiceCentroCosto = codiceCentroCosto;
}

public String getCodiceCentroCosto() {
    return codiceCentroCosto;
}

public void setCentroCosto(String centroCosto) {
    this.centroCosto = centroCosto;
}

public String getCentroCosto() {
    return centroCosto;
}

public void setUfficio(String ufficio) {
    this.ufficio = ufficio;
}

public String getUfficio() {
    return ufficio;
}

public void setModoIndizione(Long modoIndizione) {
    this.modoIndizione = modoIndizione;
}

public Long getModoIndizione() {
    return modoIndizione;
}
*/
public void setCigAccQuadro(String cigAccQuadro) {
    this.cigAccQuadro = cigAccQuadro;
}

public String getCigAccQuadro() {
    return cigAccQuadro;
}

public void setTipoSA(Long tipoSA) {
    this.tipoSA = tipoSA;
}

public Long getTipoSA() {
    return tipoSA;
}

public void setNomeSA(String nomeSA) {
    this.nomeSA = nomeSA;
}

public String getNomeSA() {
    return nomeSA;
}

public void setCfAgente(String cfAgente) {
    this.cfAgente = cfAgente;
}

public String getCfAgente() {
    return cfAgente;
}

public void setTipoProcedura(Long tipoProcedura) {
    this.tipoProcedura = tipoProcedura;
}

public Long getTipoProcedura() {
    return tipoProcedura;
}

public void setCentraleCommittenza(String centraleCommittenza) {
    this.centraleCommittenza = centraleCommittenza;
}

public String getCentraleCommittenza() {
    return centraleCommittenza;
}
/*
public void setRicostruzioneAlluvione(String ricostruzioneAlluvione) {
    this.ricostruzioneAlluvione = ricostruzioneAlluvione;
}

public String getRicostruzioneAlluvione() {
    return ricostruzioneAlluvione;
}

public void setCriteriAmbientali(String criteriAmbientali) {
    this.criteriAmbientali = criteriAmbientali;
}

public String getCriteriAmbientali() {
    return criteriAmbientali;
}

public void setSisma(String sisma) {
    this.sisma = sisma;
}

public String getSisma() {
    return sisma;
}
*/
public void setImportoGara(Double importoGara) {
    this.importoGara = importoGara;
}

public Double getImportoGara() {
    return importoGara;
}

public void setRealizzazione(Long realizzazione) {
    this.realizzazione = realizzazione;
}

public Long getRealizzazione() {
    return realizzazione;
}

public void setAltreSA(String altreSA) {
    this.altreSA = altreSA;
}

public String getAltreSA() {
    return altreSA;
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

public void setDataPubblicazione(String dataPubblicazione) {
    this.dataPubblicazione = dataPubblicazione;
}

public String getDataPubblicazione() {
    return dataPubblicazione;
}

public void setPubblicazioneBando(PubblicazioneBandoEntry pubblicazioneBando) {
    this.pubblicazioneBando = pubblicazioneBando;
}

public PubblicazioneBandoEntry getPubblicazioneBando() {
    return pubblicazioneBando;
}

public void setDataPerfezionamentoBando(String dataPerfezionamentoBando) {
    this.dataPerfezionamentoBando = dataPerfezionamentoBando;
}

public String getDataPerfezionamentoBando() {
    return dataPerfezionamentoBando;
}

public void setVersioneSimog(Long versioneSimog) {
    this.versioneSimog = versioneSimog;
}

public Long getVersioneSimog() {
    return versioneSimog;
}

public void setPrimaPubblicazioneSCP(String primaPubblicazioneSCP) {
this.primaPubblicazioneSCP = primaPubblicazioneSCP;
}

public String getPrimaPubblicazioneSCP() {
return primaPubblicazioneSCP;
}

public void setUltimaModificaSCP(String ultimaModificaSCP) {
this.ultimaModificaSCP = ultimaModificaSCP;
}

public String getUltimaModificaSCP() {
return ultimaModificaSCP;
}
}

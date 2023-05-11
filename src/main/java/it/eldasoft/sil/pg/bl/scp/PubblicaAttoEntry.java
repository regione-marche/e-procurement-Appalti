/*
 * Created on 16/ott/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.scp;

import it.eldasoft.sil.pg.bl.scp.AggiudicatarioEntry;
import it.eldasoft.sil.pg.bl.scp.AllegatoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicaGaraEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PubblicaAttoEntry implements Serializable {

  /**
     * UID.
     */
    private static final long serialVersionUID = -4433185026855332865L;

    private Long tipoDocumento;  
    
    private String eventualeSpecificazione;  
    
    ////@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataPubblicazione;
    
    ////@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataScadenza;
    
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")   
    private String dataDecreto;  
    
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataProvvedimento;
    
    private String numeroProvvedimento;  
    
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataStipula;  
    
    private String numeroRepertorio;

    private Double ribassoAggiudicazione;

    private Double offertaAumento;

    private Double importoAggiudicazione;
    
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")  
    private String dataAggiudicazione;
    
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")  
    private String primaPubblicazioneSCP;
    
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")  
    private String ultimaModificaSCP;
   
    private String urlCommittente;
    
    private String urlEProcurement;
    
    private List<AllegatoEntry> documenti = new ArrayList<AllegatoEntry>();
    
    private PubblicaGaraEntry gara;

    private List<AggiudicatarioEntry> aggiudicatari = new ArrayList<AggiudicatarioEntry>();
    
    private Long idRicevuto;
   

  public void setGara(PubblicaGaraEntry gara) {
      this.gara = gara;
  }


  public PubblicaGaraEntry getGara() {
      return gara;
  }

  public void setTipoDocumento(Long tipoDocumento) {
      this.tipoDocumento = tipoDocumento;
  }
  public Long getTipoDocumento() {
      return tipoDocumento;
  }
  public void setDataPubblicazione(String dataPubblicazione) {
      this.dataPubblicazione = dataPubblicazione;
  }
  public String getDataPubblicazione() {
      return dataPubblicazione;
  }
  public void setDataScadenza(String dataScadenza) {
      this.dataScadenza = dataScadenza;
  }
  public String getDataScadenza() {
      return dataScadenza;
  }
  public void setDataDecreto(String dataDecreto) {
      this.dataDecreto = dataDecreto;
  }
  public String getDataDecreto() {
      return dataDecreto;
  }
  public String getDataProvvedimento() {
      return dataProvvedimento;
  }
  public void setDataProvvedimento(String dataProvvedimento) {
      this.dataProvvedimento = dataProvvedimento;
  }
  public String getNumeroProvvedimento() {
      return numeroProvvedimento;
  }
  public void setNumeroProvvedimento(String numeroProvvedimento) {
      this.numeroProvvedimento = numeroProvvedimento;
  }
  public String getDataStipula() {
      return dataStipula;
  }
  public void setDataStipula(String dataStipula) {
      this.dataStipula = dataStipula;
  }
  public String getNumeroRepertorio() {
      return numeroRepertorio;
  }
  public void setNumeroRepertorio(String numeroRepertorio) {
      this.numeroRepertorio = numeroRepertorio;
  }
  public void setDocumenti(List<AllegatoEntry> documenti) {
      this.documenti = documenti;
  }
  public List<AllegatoEntry> getDocumenti() {
      return documenti;
  }
  public void setRibassoAggiudicazione(Double ribassoAggiudicazione) {
      this.ribassoAggiudicazione = ribassoAggiudicazione;
  }
  public Double getRibassoAggiudicazione() {
      return ribassoAggiudicazione;
  }
  public void setOffertaAumento(Double offertaAumento) {
      this.offertaAumento = offertaAumento;
  }
  public Double getOffertaAumento() {
      return offertaAumento;
  }
  public void setImportoAggiudicazione(Double importoAggiudicazione) {
      this.importoAggiudicazione = importoAggiudicazione;
  }
  public Double getImportoAggiudicazione() {
      return importoAggiudicazione;
  }
  public void setDataAggiudicazione(String dataAggiudicazione) {
      this.dataAggiudicazione = dataAggiudicazione;
  }
  public String getDataAggiudicazione() {
      return dataAggiudicazione;
  }


  public void setIdRicevuto(Long idRicevuto) {
      this.idRicevuto = idRicevuto;
  }


  public Long getIdRicevuto() {
      return idRicevuto;
  }


  public void setAggiudicatari(List<AggiudicatarioEntry> aggiudicatari) {
      this.aggiudicatari = aggiudicatari;
  }


  public List<AggiudicatarioEntry> getAggiudicatari() {
      return aggiudicatari;
  }


  public void setEventualeSpecificazione(String eventualeSpecificazione) {
      this.eventualeSpecificazione = eventualeSpecificazione;
  }


  public String getEventualeSpecificazione() {
      return eventualeSpecificazione;
  }


  public void setUrlCommittente(String urlCommittente) {
      this.urlCommittente = urlCommittente;
  }


  public String getUrlCommittente() {
      return urlCommittente;
  }


  public void setUrlEProcurement(String urlEProcurement) {
      this.urlEProcurement = urlEProcurement;
  }


  public String getUrlEProcurement() {
      return urlEProcurement;
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

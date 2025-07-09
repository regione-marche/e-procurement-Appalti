package it.eldasoft.sil.pg.nadc.model;

import java.util.List;

public class Anagrafica {
  private Appalto appalto;
  private List<Documento> documenti;
  private Creazione creazione;
  private Aggiornamento aggiornamento;
  
  public Appalto getAppalto() {
    return appalto;
  }
  
  public void setAppalto(Appalto appalto) {
    this.appalto = appalto;
  }
  
  public List<Documento> getDocumenti() {
    return documenti;
  }
  
  public void setDocumenti(List<Documento> documenti) {
    this.documenti = documenti;
  }
  
  public Creazione getCreazione() {
    return creazione;
  }
  
  public void setCreazione(Creazione creazione) {
    this.creazione = creazione;
  }
  
  public Aggiornamento getAggiornamento() {
    return aggiornamento;
  }
  
  public void setAggiornamento(Aggiornamento aggiornamento) {
    this.aggiornamento = aggiornamento;
  }

  public Anagrafica() {
    
  }
  
  
}

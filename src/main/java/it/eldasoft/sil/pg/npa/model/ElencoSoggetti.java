/*
 * Created on 05-ott-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

import java.util.Date;
import java.util.List;

/**
 * Bean description:    
 * Elenco di soggetti che hanno presentato manifestazione di interesse o domanda di partecipazione. 
 *
 * @author Alex.Mancini
 */
public class ElencoSoggetti {

  private String                cig;
  private List<Invitato>        invitatiCheNonHannoPresentatoOfferta;
  private List<Partecipante>    partecipanti;
  private String                  dataInvito;
  private String                  dataScadenzaPresentazioneOfferta;
  
  
  public String getCig() {
    return cig;
  }
  
  
  public void setCig(String cig) {
    this.cig = cig;
  }

  
  public List<Invitato> getInvitatiCheNonHannoPresentatoOfferta() {
    return invitatiCheNonHannoPresentatoOfferta;
  }

  
  public void setInvitatiCheNonHannoPresentatoOfferta(List<Invitato> invitatiCheNonHannoPresentatoOfferta) {
    this.invitatiCheNonHannoPresentatoOfferta = invitatiCheNonHannoPresentatoOfferta;
  }

  
  public List<Partecipante> getPartecipanti() {
    return partecipanti;
  }

  
  public void setPartecipanti(List<Partecipante> partecipanti) {
    this.partecipanti = partecipanti;
  }

  
  public String getDataInvito() {
    return dataInvito;
  }

  
  public void setDataInvito(String dataInvito) {
    this.dataInvito = dataInvito;
  }

  
  public String getDataScadenzaPresentazioneOfferta() {
    return dataScadenzaPresentazioneOfferta;
  }

  
  public void setDataScadenzaPresentazioneOfferta(String dataScadenzaPresentazioneOfferta) {
    this.dataScadenzaPresentazioneOfferta = dataScadenzaPresentazioneOfferta;
  }

  
  public ElencoSoggetti() {
    // TODO document why this constructor is empty
  }
  
}
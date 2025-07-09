/*
 * Created on 16-nov-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

import java.util.List;

/**
 * Bean description:    
 * Dati relativi alle prestazioni progettuali.
 *
 * @author Alex.Mancini
 */
public class Prestazione {

  private String progettazioneInternaEsterna;
  private EnumNPA tipoSoggetto;
  private DatiPersonaFisica datiPersonaFisica;
  private List<DatiPersonaGiuridica> datiPersonaGiuridica;
  private EnumNPA tipoProgettazione;
  private String cig;
  private String dataAffidamentoIncarico;
  private String dataConsegna;
  
  
  public String getProgettazioneInternaEsterna() {
    return progettazioneInternaEsterna;
  }

  
  public void setProgettazioneInternaEsterna(String progettazioneInternaEsterna) {
    this.progettazioneInternaEsterna = progettazioneInternaEsterna;
  }

  
  public EnumNPA getTipoSoggetto() {
    return tipoSoggetto;
  }

  
  public void setTipoSoggetto(EnumNPA tipoSoggetto) {
    this.tipoSoggetto = tipoSoggetto;
  }

  
  public DatiPersonaFisica getDatiPersonaFisica() {
    return datiPersonaFisica;
  }

  
  public void setDatiPersonaFisica(DatiPersonaFisica datiPersonaFisica) {
    this.datiPersonaFisica = datiPersonaFisica;
  }

  
  public List<DatiPersonaGiuridica> getDatiPersonaGiuridica() {
    return datiPersonaGiuridica;
  }

  
  public void setDatiPersonaGiuridica(List<DatiPersonaGiuridica> datiPersonaGiuridica) {
    this.datiPersonaGiuridica = datiPersonaGiuridica;
  }

  
  public EnumNPA getTipoProgettazione() {
    return tipoProgettazione;
  }

  
  public void setTipoProgettazione(EnumNPA tipoProgettazione) {
    this.tipoProgettazione = tipoProgettazione;
  }

  
  public String getCig() {
    return cig;
  }

  
  public void setCig(String cig) {
    this.cig = cig;
  }

  
  public String getDataAffidamentoIncarico() {
    return dataAffidamentoIncarico;
  }

  
  public void setDataAffidamentoIncarico(String dataAffidamentoIncarico) {
    this.dataAffidamentoIncarico = dataAffidamentoIncarico;
  }

  
  public String getDataConsegna() {
    return dataConsegna;
  }

  
  public void setDataConsegna(String dataConsegna) {
    this.dataConsegna = dataConsegna;
  }

  public Prestazione() {
    // TODO document why this constructor is empty
  }
  
}
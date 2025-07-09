/*
 * Created on 20-nov-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

/**
 * Bean description:    
 * Dato in comune con il Business Group Classification delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseCPVPLBase {

  private EnumNPA tipoClassificazione;
  private EnumNPA cpvPrevalente;
  
  
  public EnumNPA getTipoClassificazione() {
    return tipoClassificazione;
  }

  
  public void setTipoClassificazione(EnumNPA tipoClassificazione) {
    this.tipoClassificazione = tipoClassificazione;
  }

  
  public EnumNPA getCpvPrevalente() {
    return cpvPrevalente;
  }

  
  public void setCpvPrevalente(EnumNPA cpvPrevalente) {
    this.cpvPrevalente = cpvPrevalente;
  }

  
  public DatiBaseCPVPLBase() {
    // TODO document why this constructor is empty
  }

  public DatiBaseCPVPLBase(EnumNPA tipoClassificazione, EnumNPA cpvPrevalente) {
    this.tipoClassificazione = tipoClassificazione;
    this.cpvPrevalente = cpvPrevalente;
  }
  
}
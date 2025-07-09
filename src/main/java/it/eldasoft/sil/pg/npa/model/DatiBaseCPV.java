/*
 * Created on 13-nov-2023
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
 * Dato in comune con il Business Group Classification delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseCPV {

  private EnumNPA tipoClassificazione;
  private EnumNPA cpvPrevalente;
  private List<EnumNPA> cpvSecondarie;
  
  
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

  
  public List<EnumNPA> getCpvSecondarie() {
    return cpvSecondarie;
  }

  
  public void setCpvSecondarie(List<EnumNPA> cpvSecondarie) {
    this.cpvSecondarie = cpvSecondarie;
  }

  public DatiBaseCPV() {
    // TODO document why this constructor is empty
  }

  public DatiBaseCPV(EnumNPA tipoClassificazione, EnumNPA cpvPrevalente, List<EnumNPA> cpvSecondarie) {
    this.tipoClassificazione = tipoClassificazione;
    this.cpvPrevalente = cpvPrevalente;
    this.cpvSecondarie = cpvSecondarie;
  }
  
}
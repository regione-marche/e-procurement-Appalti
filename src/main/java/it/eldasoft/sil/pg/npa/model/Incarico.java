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
 * Dati relativi agli incarichi tecnici.
 *
 * @author Alex.Mancini
 */
public class Incarico {

  private EnumNPA tipoIncarico;
  private DatiPersonaFisica datiPersonaFisica;
  private List<DatiPersonaGiuridica> datiPersonaGiuridica;
  
  
  public EnumNPA getTipoIncarico() {
    return tipoIncarico;
  }

  
  public void setTipoIncarico(EnumNPA tipoIncarico) {
    this.tipoIncarico = tipoIncarico;
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

  public Incarico() {
    // TODO document why this constructor is empty
  }
  
}
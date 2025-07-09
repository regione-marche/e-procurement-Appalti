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

/**
 * Bean description:    
 * Motivo annullamento di un affidamento diretto
 *
 * @author Manuel.Bridda
 * 
 * @property
 * private EnumNPA motivoAnnullamento;
 */
public class Annullamento {

  private EnumNPA motivoAnnullamento;


  public EnumNPA getMotivoAnnullamento() {
    return motivoAnnullamento;
  }

  public void setMotivoAnnullamento(EnumNPA motivoAnnullamento) {
    this.motivoAnnullamento = motivoAnnullamento;
  }
}
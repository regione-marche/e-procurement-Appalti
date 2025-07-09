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

/**
 * Bean description:    
 * Dato in comune con il Business Group Prize delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseImporto {

  private Boolean contrattiSuccessivi;

  public Boolean getContrattiSuccessivi() {
    return contrattiSuccessivi;
  }

  public void setContrattiSuccessivi(Boolean contrattiSuccessivi) {
    this.contrattiSuccessivi = contrattiSuccessivi;
  }
  
  public DatiBaseImporto() {
    // TODO document why this constructor is empty
  }

  public DatiBaseImporto(Boolean contrattiSuccessivi) {
    this.contrattiSuccessivi = contrattiSuccessivi;
  }
  
}
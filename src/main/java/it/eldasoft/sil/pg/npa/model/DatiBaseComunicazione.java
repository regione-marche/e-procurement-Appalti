/*
 * Created on 17-nov-2023
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

/**
 * Bean description:    
 * Dato in comune con il Business Group Communication delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseComunicazione {

  private String dataInvioInviti;
    
  public String getDataInvioInviti() {
    return dataInvioInviti;
  }
  
  public void setDataInvioInviti(String dataInvioInviti) {
    this.dataInvioInviti = dataInvioInviti;
  }

  public DatiBaseComunicazione() {
    // TODO document why this constructor is empty
  }

  public DatiBaseComunicazione(String dataInvioInviti) {
    this.dataInvioInviti = dataInvioInviti;
  }
  
}
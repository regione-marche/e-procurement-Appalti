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

import java.util.ArrayList;
import java.util.List;

/**
 * Bean description:    
 * Dato in comune con il Business Group Other Requirements delle eforms
 *
 * @author Alex.Mancini
 *
 * @property
 * private List<EnumNPA> affidamentiRiservati;
 */
public class DatiBaseAggiuntivi {

  private List<EnumNPA> affidamentiRiservati;
  
  
  public List<EnumNPA> getAffidamentiRiservati() {
    return affidamentiRiservati;
  }

  
  public void setAffidamentiRiservati(List<EnumNPA> affidamentiRiservati) {
    this.affidamentiRiservati = affidamentiRiservati;
  }

  public DatiBaseAggiuntivi() {
    // TODO document why this constructor is empty
  }

  public DatiBaseAggiuntivi(String affidamentiRiservati) {
    List<EnumNPA> listaDatiAggiuntivi = null;
    if(affidamentiRiservati != null) {
      listaDatiAggiuntivi = new ArrayList<EnumNPA>();
      listaDatiAggiuntivi.add(new EnumNPA("affidamentiRiservati", affidamentiRiservati));
    }
    this.affidamentiRiservati = listaDatiAggiuntivi;
  }
  
  
}
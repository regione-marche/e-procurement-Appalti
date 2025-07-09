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
 * Dato in comune con il Business Group Strategic Procurement delle eforms
 *
 * @author Alex.Mancini
 *
 * @property
 * private EnumNPA accessibilita;
 * private String giustificazione;
 * private String giustificazioneMl;
 */
public class DatiBaseAccessibilita {

  private EnumNPA accessibilita;
  private String giustificazione;
  private String giustificazioneMl;
  
  
  public EnumNPA getAccessibilita() {
    return accessibilita;
  }

  
  public void setAccessibilita(EnumNPA accessibilita) {
    this.accessibilita = accessibilita;
  }

  
  public String getGiustificazione() {
    return giustificazione;
  }

  
  public void setGiustificazione(String giustificazione) {
    this.giustificazione = giustificazione;
  }

  public String getGiustificazioneMl() {
    return giustificazioneMl;
  }


  public void setGiustificazioneMl(String giustificazioneMl) {
    this.giustificazioneMl = giustificazioneMl;
  }


  public DatiBaseAccessibilita() {
    // TODO document why this constructor is empty
  }


  public DatiBaseAccessibilita(EnumNPA accessibilita, String giustificazione) {
    this.accessibilita = accessibilita;
    this.giustificazione = giustificazione;
  }
  
  
  
}
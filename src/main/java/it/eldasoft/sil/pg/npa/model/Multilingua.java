/*
 * Created on 29-nov-2023
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
 * Oggetto utilizzato per la comunicazione dei dati in multilingua
 *
 * @author Alex.Mancini
 * 
 * @property
 * private String de;
 * private String en;
 */
public class Multilingua {

  private String de;
  private String en;
  
  public String getDe() {
    return de;
  }

  public void setDe(String de) {
    this.de = de;
  }

  public String getEn() {
    return en;
  }

  public void setEn(String en) {
    this.en = en;
  }
  
  public Multilingua() {
    // TODO document why this constructor is empty
  }

}
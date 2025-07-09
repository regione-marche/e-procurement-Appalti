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
 * Dato in comune con il Business Group Procurement Documents delle eforms
 *
 * @author Alex.Mancini
 *
 * @property
 * private String url;
 * private List<EnumNPA> lingue;
 */
public class DatiBaseDocumentiOptional {

  private String url;
  private List<EnumNPA> lingue;
  
  
  public String getUrl() {
    return url;
  }

  
  public void setUrl(String url) {
    this.url = url;
  }

  
  public List<EnumNPA> getLingue() {
    return lingue;
  }

  
  public void setLingue(List<EnumNPA> lingue) {
    this.lingue = lingue;
  }

  public DatiBaseDocumentiOptional() {
    // TODO document why this constructor is empty
  }


  public DatiBaseDocumentiOptional(String url, List<EnumNPA> lingue) {
    this.url = url;
    this.lingue = lingue;
  }
  
}
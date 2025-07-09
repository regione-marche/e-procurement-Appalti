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
 * Dati del soggetto
 *
 * @author Alex.Mancini
 */
public class EnumNPA {

  private String        idTipologica;
  private String        codice;
  
  public String getIdTipologica() {
    return idTipologica;
  }
  
  public void setIdTipologica(String idTipologica) {
    this.idTipologica = idTipologica;
  }
  
  public String getCodice() {
    return codice;
  }
  
  public void setCodice(String codice) {
    this.codice = codice;
  }
  
  
  public EnumNPA() {
    // TODO Auto-generated constructor stub
  }

  public EnumNPA(String idTipologica, String codice) {
    super();
    this.idTipologica = idTipologica;
    this.codice = codice;
  }

  
}

/*
 * Created on 14-mar-2024
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
 * Oggetto che riporta i dati dell'appalto.
 *
 * @author Alex.Mancini
 */
public class AppaltoP5 {
  
  private String codiceAppalto;
  private EnumNPA settore;
  private String cigAccordoQuadro;
  private DatiBaseAppaltoOptional datiBase;

  
  public String getCodiceAppalto() {
    return codiceAppalto;
  }

  
  public void setCodiceAppalto(String codiceAppalto) {
    this.codiceAppalto = codiceAppalto;
  }

  public EnumNPA getSettore() {
    return settore;
  }
  public void setSettore(EnumNPA settore) {
    this.settore = settore;
  }
  
  public String getCigAccordoQuadro() {
    return cigAccordoQuadro;
  }

  
  public void setCigAccordoQuadro(String cigAccordoQuadro) {
    this.cigAccordoQuadro = cigAccordoQuadro;
  }

  
  public DatiBaseAppaltoOptional getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseAppaltoOptional datiBase) {
    this.datiBase = datiBase;
  }

  public AppaltoP5() {
    // TODO document why this constructor is empty
  }
  
}
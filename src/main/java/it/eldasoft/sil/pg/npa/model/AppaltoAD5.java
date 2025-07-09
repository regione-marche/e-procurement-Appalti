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
 * Descrizione non presente nella scheda anac
 *
 * @author Alex.Mancini
 */
public class AppaltoAD5 {

  private String codiceAppalto;
  private EnumNPA settore;
  private DatiBaseAppaltoOptionalSoloOggetto datiBase;

  
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

  public DatiBaseAppaltoOptionalSoloOggetto getDatiBase() {
    return datiBase;
  }


  public void setDatiBase(DatiBaseAppaltoOptionalSoloOggetto datiBase) {
    this.datiBase = datiBase;
  }


  public AppaltoAD5() {
    // TODO document why this constructor is empty
  }
  
}
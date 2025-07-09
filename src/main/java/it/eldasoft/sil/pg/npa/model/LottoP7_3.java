/*
 * Created on 20-nov-2023
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
 * Oggetto che riporta i dati del lotto
 *
 * @author Alex.Mancini
 */
public class LottoP7_3 {
  
  private String lotIdentifier;
  private DatiBaseLotto datiBase;
  private DatiBaseCPV datiBaseCPV;
  private DatiBaseDocumentiOptional datiBaseDocumenti;
  private DatiBaseDurata datiBaseDurata;
  
  
  public String getLotIdentifier() {
    return lotIdentifier;
  }

  
  public void setLotIdentifier(String lotIdentifier) {
    this.lotIdentifier = lotIdentifier;
  }

  
  public DatiBaseLotto getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseLotto datiBase) {
    this.datiBase = datiBase;
  }

  
  public DatiBaseCPV getDatiBaseCPV() {
    return datiBaseCPV;
  }

  
  public void setDatiBaseCPV(DatiBaseCPV datiBaseCPV) {
    this.datiBaseCPV = datiBaseCPV;
  }

  
  public DatiBaseDocumentiOptional getDatiBaseDocumenti() {
    return datiBaseDocumenti;
  }

  
  public void setDatiBaseDocumenti(DatiBaseDocumentiOptional datiBaseDocumenti) {
    this.datiBaseDocumenti = datiBaseDocumenti;
  }

  
  public DatiBaseDurata getDatiBaseDurata() {
    return datiBaseDurata;
  }

  
  public void setDatiBaseDurata(DatiBaseDurata datiBaseDurata) {
    this.datiBaseDurata = datiBaseDurata;
  }

  public LottoP7_3() {
    // TODO document why this constructor is empty
  }

}
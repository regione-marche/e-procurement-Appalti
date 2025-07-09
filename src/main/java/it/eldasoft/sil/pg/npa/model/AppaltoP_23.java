/*
 * Created on 09-nov-2023
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
 * Oggetto che riporta i dati dell'appalto all'interno della fase di pubblicazione sopra soglia e sotto soglia
 *
 * @author Alex.Mancini
 */
public class AppaltoP_23 {
  
  private String codiceAppalto;
  private EnumNPA strumentiSvolgimentoProcedure;
  private EnumNPA contrattiDisposizioniParticolari;
  private String idPianificazione;
  private Boolean termineRidottoAvvisoPreinformazione;

  
  public String getCodiceAppalto() {
    return codiceAppalto;
  }

  
  public void setCodiceAppalto(String codiceAppalto) {
    this.codiceAppalto = codiceAppalto;
  }

  
  public EnumNPA getStrumentiSvolgimentoProcedure() {
    return strumentiSvolgimentoProcedure;
  }

  
  public void setStrumentiSvolgimentoProcedure(EnumNPA strumentiSvolgimentoProcedure) {
    this.strumentiSvolgimentoProcedure = strumentiSvolgimentoProcedure;
  }

  
  public EnumNPA getContrattiDisposizioniParticolari() {
    return contrattiDisposizioniParticolari;
  }

  
  public void setContrattiDisposizioniParticolari(EnumNPA contrattiDisposizioniParticolari) {
    this.contrattiDisposizioniParticolari = contrattiDisposizioniParticolari;
  }

  
  public String getIdPianificazione() {
    return idPianificazione;
  }

  
  public void setIdPianificazione(String idPianificazione) {
    this.idPianificazione = idPianificazione;
  }

  
  public Boolean isTermineRidottoAvvisoPreinformazione() {
    return termineRidottoAvvisoPreinformazione;
  }

  
  public void setTermineRidottoAvvisoPreinformazione(Boolean termineRidottoAvvisoPreinformazione) {
    this.termineRidottoAvvisoPreinformazione = termineRidottoAvvisoPreinformazione;
  }

  public AppaltoP_23() {
    // TODO document why this constructor is empty
  }
  
}
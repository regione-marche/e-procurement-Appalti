/*
 * Created on 18-dic-2023
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
public class AppaltoP3_3 {
  
  private String codiceAppalto;
  private EnumNPA settore;
  private EnumNPA strumentiSvolgimentoProcedure;
  private String idPianificazione;
  private Boolean termineRidottoAvvisoPreinformazione;
  private String cigAccordoQuadroConvenzione;
  private DatiBaseAppaltoOptional datiBase;
  private DatiBaseProcedura2 datiBaseProcedura;
  private DatiBaseStrumentiProceduraCompleto datiBaseStrumentiProcedura;
  
  
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
  
  public EnumNPA getStrumentiSvolgimentoProcedure() {
    return strumentiSvolgimentoProcedure;
  }

  
  public void setStrumentiSvolgimentoProcedure(EnumNPA strumentiSvolgimentoProcedure) {
    this.strumentiSvolgimentoProcedure = strumentiSvolgimentoProcedure;
  }

  
  public String getIdPianificazione() {
    return idPianificazione;
  }

  
  public void setIdPianificazione(String idPianificazione) {
    this.idPianificazione = idPianificazione;
  }

  
  public Boolean getTermineRidottoAvvisoPreinformazione() {
    return termineRidottoAvvisoPreinformazione;
  }

  
  public void setTermineRidottoAvvisoPreinformazione(Boolean termineRidottoAvvisoPreinformazione) {
    this.termineRidottoAvvisoPreinformazione = termineRidottoAvvisoPreinformazione;
  }

  
  public String getCigAccordoQuadroConvenzione() {
    return cigAccordoQuadroConvenzione;
  }


  public void setCigAccordoQuadroConvenzione(String cigAccordoQuadroConvenzione) {
    this.cigAccordoQuadroConvenzione = cigAccordoQuadroConvenzione;
  }


  public DatiBaseAppaltoOptional getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseAppaltoOptional datiBase) {
    this.datiBase = datiBase;
  }

  
  public DatiBaseProcedura2 getDatiBaseProcedura() {
    return datiBaseProcedura;
  }

  
  public void setDatiBaseProcedura(DatiBaseProcedura2 datiBaseProcedura) {
    this.datiBaseProcedura = datiBaseProcedura;
  }

  
  public DatiBaseStrumentiProceduraCompleto getDatiBaseStrumentiProcedura() {
    return datiBaseStrumentiProcedura;
  }

  
  public void setDatiBaseStrumentiProcedura(DatiBaseStrumentiProceduraCompleto datiBaseStrumentiProcedura) {
    this.datiBaseStrumentiProcedura = datiBaseStrumentiProcedura;
  }

  public AppaltoP3_3() {
    // TODO document why this constructor is empty
  }
  
}
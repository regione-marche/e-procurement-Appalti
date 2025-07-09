/*
 * Created on 11-gen-2024
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
public class AppaltoP6_1 {

  private String codiceAppalto;
  private String cigAccordoQuadro;
  private EnumNPA strumentiSvolgimentoProcedure;
  private String idPianificazione;
  private Boolean termineRidottoAvvisoPreinformazione;
  private DatiBaseProceduraCompleto datiBaseProcedura;
  private DatiBaseStrumentiProceduraOptional datiBaseStrumentiProcedura;
  private DatiBaseAppaltoOptional datiBase;

  
  public String getCodiceAppalto() {
    return codiceAppalto;
  }

  
  public void setCodiceAppalto(String codiceAppalto) {
    this.codiceAppalto = codiceAppalto;
  }

  
  public String getCigAccordoQuadro() {
    return cigAccordoQuadro;
  }

  
  public void setCigAccordoQuadro(String cigAccordoQuadro) {
    this.cigAccordoQuadro = cigAccordoQuadro;
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

  
  public DatiBaseProceduraCompleto getDatiBaseProcedura() {
    return datiBaseProcedura;
  }

  
  public void setDatiBaseProcedura(DatiBaseProceduraCompleto datiBaseProcedura) {
    this.datiBaseProcedura = datiBaseProcedura;
  }

  
  public DatiBaseStrumentiProceduraOptional getDatiBaseStrumentiProcedura() {
    return datiBaseStrumentiProcedura;
  }

  
  public void setDatiBaseStrumentiProcedura(DatiBaseStrumentiProceduraOptional datiBaseStrumentiProcedura) {
    this.datiBaseStrumentiProcedura = datiBaseStrumentiProcedura;
  }

  
  public DatiBaseAppaltoOptional getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseAppaltoOptional datiBase) {
    this.datiBase = datiBase;
  }

  public AppaltoP6_1() {
    // TODO document why this constructor is empty
  }
  
}
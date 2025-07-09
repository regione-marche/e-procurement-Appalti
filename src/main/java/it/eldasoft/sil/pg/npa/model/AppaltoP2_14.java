/*
 * Created on 09-gen-2024
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
 * Oggetto che riporta i dati dell'appalto all'interno della fase di pubblicazione.
 *
 * @author Alex.Mancini
 */
public class AppaltoP2_14 {

  private String codiceAppalto;
  private EnumNPA settore;
  private List<EnumNPA> categorieMerceologiche;
  private EnumNPA motivazioneCIG;
  private EnumNPA strumentiSvolgimentoProcedure;
  private String idPianificazione;
  private Boolean termineRidottoAvvisoPreinformazione;
  private DatiBaseAppaltoOptional datiBase;
  private DatiBaseProcedura2 datiBaseProcedura;
  private DatiBaseStrumentiProceduraOptional datiBaseStrumentiProcedura;
  private Boolean costituzioneSocietaDiScopo;
  
  
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
  
  public List<EnumNPA> getCategorieMerceologiche() {
    return categorieMerceologiche;
  }

  
  public void setCategorieMerceologiche(List<EnumNPA> categorieMerceologiche) {
    this.categorieMerceologiche = categorieMerceologiche;
  }

  
  public EnumNPA getMotivazioneCIG() {
    return motivazioneCIG;
  }

  
  public void setMotivazioneCIG(EnumNPA motivazioneCIG) {
    this.motivazioneCIG = motivazioneCIG;
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

  
  public DatiBaseStrumentiProceduraOptional getDatiBaseStrumentiProcedura() {
    return datiBaseStrumentiProcedura;
  }

  
  public void setDatiBaseStrumentiProcedura(DatiBaseStrumentiProceduraOptional datiBaseStrumentiProcedura) {
    this.datiBaseStrumentiProcedura = datiBaseStrumentiProcedura;
  }

  public Boolean getCostituzioneSocietaDiScopo() {
    return costituzioneSocietaDiScopo;
  }
  public void setCostituzioneSocietaDiScopo(Boolean costituzioneSocietaDiScopo) {
    this.costituzioneSocietaDiScopo = costituzioneSocietaDiScopo;
  }

  public AppaltoP2_14() {
    // TODO document why this constructor is empty
  }
  
}
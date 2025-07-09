/*
 * Created on 20-ott-2023
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
 * Oggetto che riporta i dati dell'appalto all'interno della fase di pubblicazione sopra soglia e sotto soglia
 *
 * @author Alex.Mancini
 */
public class AppaltoP_17 {
  
  private String codiceAppalto;
  private List<EnumNPA> categorieMerceologiche;
  private EnumNPA motivazioneCIG;
  private EnumNPA strumentiSvolgimentoProcedure;
  private String idPianificazione;
  private Boolean termineRidottoAvvisoPreinformazione;
  private EnumNPA motivoUrgenza;
  private String linkDocumenti;
  

  public String getCodiceAppalto() {
    return codiceAppalto;
  }

  public void setCodiceAppalto(String codiceAppalto) {
    this.codiceAppalto = codiceAppalto;
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

  public EnumNPA getMotivoUrgenza() {
    return motivoUrgenza;
  }

  public void setMotivoUrgenza(EnumNPA motivoUrgenza) {
    this.motivoUrgenza = motivoUrgenza;
  }

  public String getLinkDocumenti() {
    return linkDocumenti;
  }

  public void setLinkDocumenti(String linkDocumenti) {
    this.linkDocumenti = linkDocumenti;
  }
  
  public AppaltoP_17() {
    // TODO document why this constructor is empty
  }
  
}
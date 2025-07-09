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

import java.util.List;

/**
 * Bean description:    
 * Dato in comune con il Business Group Procedure delle eforms
 *
 * @author Alex.Mancini
 * 
 * @property
 * private EnumNPA tipoProcedura;
 * private List<EnumNPA> giustificazioniAggiudicazioneDiretta;
 */
public class DatiBaseProcedura {

  private EnumNPA tipoProcedura;
  private List<EnumNPA> giustificazioniAggiudicazioneDiretta;
  
  
  public EnumNPA getTipoProcedura() {
    return tipoProcedura;
  }

  
  public void setTipoProcedura(EnumNPA tipoProcedura) {
    this.tipoProcedura = tipoProcedura;
  }

  
  public List<EnumNPA> getGiustificazioniAggiudicazioneDiretta() {
    return giustificazioniAggiudicazioneDiretta;
  }

  
  public void setGiustificazioniAggiudicazioneDiretta(List<EnumNPA> giustificazioniAggiudicazioneDiretta) {
    this.giustificazioniAggiudicazioneDiretta = giustificazioniAggiudicazioneDiretta;
  }

  public DatiBaseProcedura() {
    // TODO document why this constructor is empty
  }
  
}
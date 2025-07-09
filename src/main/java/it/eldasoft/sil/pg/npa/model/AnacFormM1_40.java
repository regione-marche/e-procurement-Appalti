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
 * Contiene tutti i dati relativi alle modifiche contrattuali
 *
 * @author Alex.Mancini
 */
public class AnacFormM1_40 {

  private String idContratto;
  private List<ModificaContrattuale_40> modificaContrattuale;

  
  public String getIdContratto() {
    return idContratto;
  }

  
  public void setIdContratto(String idContratto) {
    this.idContratto = idContratto;
  }

  
  public List<ModificaContrattuale_40> getModificaContrattuale() {
    return modificaContrattuale;
  }

  
  public void setModificaContrattuale(List<ModificaContrattuale_40> modificaContrattuale) {
    this.modificaContrattuale = modificaContrattuale;
  }

  public AnacFormM1_40() {
    // TODO document why this constructor is empty
  }
  
}
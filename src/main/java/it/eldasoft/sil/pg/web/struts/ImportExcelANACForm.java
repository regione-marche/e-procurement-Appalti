/*
 * Created on 09/Dic/21
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.struts.UploadFileForm;

/**
 * BeanForm per import gare da excel ANAC
 * 
 * @author Peruzzo Riccardo
 */
public class ImportExcelANACForm extends UploadFileForm {
  
  /**
   * 
   */
  
  /**   UID   */
  private static final long serialVersionUID = 0L;
  
  private String tipoElenco;  // Tipo elenco CIG o SmartCIG
  private String syscon;    // Codice Utente

  
  
  public ImportExcelANACForm(){
    super();
    this.inizializza();
  }
  
  private void inizializza(){
    this.tipoElenco = null;
    this.syscon = null;
    
  }
  
  
  public String getTipoElenco() {
    return tipoElenco;
  }
  
  public void setTipoElenco(String tipoElenco) {
    this.tipoElenco = tipoElenco;
  }
    
  public String getSyscon() {
    return this.syscon;
  }
  
  public void setSyscon(String syscon) {
    this.syscon = syscon;
  }
  
}

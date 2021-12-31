/*
 * Created on 18/lug/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import org.apache.struts.action.ActionForm;

/**
 * BeanForm per la ricerca delle ditte SAP
 * 
 * @author Marcello Caminiti
 */
public class TrovaConcorrentiAURForm extends ActionForm {
  
  /**
   * 
   */
  
  /**   UID   */
  private static final long serialVersionUID = 0L;
  
  private String i_NAME1;  // Ragione sociale
  private String i_STCD1;    // Codice Fiscale
  private String i_STCD2;    // Partita IVA

  
  
  public TrovaConcorrentiAURForm(){
    super();
    this.inizializza();
  }
  
  private void inizializza(){
    this.i_NAME1 = null;
    this.i_STCD1 = null;
    this.i_STCD2 = null;
    
  }
  
  
  
  
  
  public String getI_NAME1() {
    return i_NAME1;
  }
  
  public void setI_NAME1(String i_name1) {
    i_NAME1 = i_name1;
  }
    
  public String getI_STCD1() {
    return this.i_STCD1;
  }
  
  public void setI_STCD1(String STCD1) {
    this.i_STCD1 = STCD1;
  }
  
  
  
  public String getI_STCD2() {
    return this.i_STCD2;
  }

  public void setI_STCD2(String sTCD2) {
    this.i_STCD2 = sTCD2;
  }
}

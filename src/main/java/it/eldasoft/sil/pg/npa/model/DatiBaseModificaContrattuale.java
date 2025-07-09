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

/**
 * Bean description:    
 * Dato in comune con i Business Group Contract e Modification delle eforms
 *
 * @author Alex.Mancini
 */
public class DatiBaseModificaContrattuale {
  
  private String dataSottoscrizione;
  private EnumNPA motiviModifica;
  private String causaModifica;
  private Multilingua causaModificaMl;

  
  public String getDataSottoscrizione() {
    return dataSottoscrizione;
  }

  
  public void setDataSottoscrizione(String dataSottoscrizione) {
    this.dataSottoscrizione = dataSottoscrizione;
  }

  
  public EnumNPA getMotiviModifica() {
    return motiviModifica;
  }

  
  public void setMotiviModifica(EnumNPA motiviModifica) {
    this.motiviModifica = motiviModifica;
  }

  
  public String getCausaModifica() {
    return causaModifica;
  }

  
  public void setCausaModifica(String causaModifica) {
    this.causaModifica = causaModifica;
  }

  
  public Multilingua getCausaModificaMl() {
    return causaModificaMl;
  }

  
  public void setCausaModificaMl(Multilingua causaModificaMl) {
    this.causaModificaMl = causaModificaMl;
  }

  public DatiBaseModificaContrattuale() {
    // TODO document why this constructor is empty
  }
  
}
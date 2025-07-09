/*
 * Created on 20-nov-2023
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
 * Oggetto che riporta i dati dell'appalto
 *
 * @author Alex.Mancini
 */
public class AppaltoP7_3 {
  
  private DatiBaseAppaltoOptionalSoloOggetto datiBase;
  private Boolean contrattoDifesa;
  private EnumNPA settore;
  
  public DatiBaseAppaltoOptionalSoloOggetto getDatiBase() {
    return datiBase;
  }

  
  public void setDatiBase(DatiBaseAppaltoOptionalSoloOggetto datiBase) {
    this.datiBase = datiBase;
  }

  public Boolean getContrattoDifesa() {
    return contrattoDifesa;
  }
  public void setContrattoDifesa(Boolean contrattoDifesa) {
    this.contrattoDifesa = contrattoDifesa;
  }

  public EnumNPA getSettore() {
    return settore;
  }
  public void setSettore(EnumNPA settore) {
    this.settore = settore;
  }

  public AppaltoP7_3() {
    // TODO document why this constructor is empty
  }
  
}
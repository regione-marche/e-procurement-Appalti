/*
 * Created on 14-nov-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.util.Map;

/**
 * Bean description:    
 * Dato in comune con il Business Group Procedure delle eforms
 *
 * @author Alex.Mancini
 * 
 * @property
 * private EnumNPA tipoProcedura;
 * private Boolean proceduraAccelerata;
 * private String giustificazioneProceduraAccelerata;
 * private Multilingua giustificazioneProceduraAccelerataMl;
 */
public class DatiBaseProceduraCompleto {

  private EnumNPA tipoProcedura;
  private Boolean proceduraAccelerata;
  private String giustificazioneProceduraAccelerata;
  private Multilingua giustificazioneProceduraAccelerataMl;

  public EnumNPA getTipoProcedura() {
    return tipoProcedura;
  }

  
  public void setTipoProcedura(EnumNPA tipoProcedura) {
    this.tipoProcedura = tipoProcedura;
  }

  
  public Boolean getProceduraAccelerata() {
    return proceduraAccelerata;
  }


  public void setProceduraAccelerata(Boolean proceduraAccelerata) {
    this.proceduraAccelerata = proceduraAccelerata;
  }
  
  public String getGiustificazioneProceduraAccelerata() {
    return giustificazioneProceduraAccelerata;
  }


  public void setGiustificazioneProceduraAccelerata(String giustificazioneProceduraAccelerata) {
    this.giustificazioneProceduraAccelerata = giustificazioneProceduraAccelerata;
  }


  public Multilingua getGiustificazioneProceduraAccelerataMl() {
    return giustificazioneProceduraAccelerataMl;
  }


  public void setGiustificazioneProceduraAccelerataMl(Multilingua giustificazioneProceduraAccelerataMl) {
    this.giustificazioneProceduraAccelerataMl = giustificazioneProceduraAccelerataMl;
  }


  public DatiBaseProceduraCompleto() {
    // TODO document why this constructor is empty
  }

  public DatiBaseProceduraCompleto(Map parameters) throws Exception {

    NpaFunzioniManager npaFunzioniManager = (NpaFunzioniManager) parameters.get("npaFunzioniManager");
    Long iterga = (Long) NpaFunzioniManager.getValoreParametro(parameters,"ITERGA");
    String codgar = (String) NpaFunzioniManager.getValoreParametro(parameters,"CODGAR");
    this.tipoProcedura = npaFunzioniManager.getTipoProceduraByIterga(iterga,codgar);

    Boolean prourg = (Boolean) NpaFunzioniManager.getValoreParametro(parameters,"PROURG");
    Multilingua motacc = (Multilingua) NpaFunzioniManager.getValoreParametro(parameters,"MOTACC");

    this.proceduraAccelerata = Boolean.TRUE.equals(prourg);
    if(motacc != null) {
      this.giustificazioneProceduraAccelerataMl = motacc;
    }
  }

}

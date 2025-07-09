package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.util.Map;

public class DatiBaseStrumentiProceduraOnlyAsta {

  private Boolean astaElettronica;

  public Boolean getAstaElettronica() {
    return astaElettronica;
  }

  public void setAstaElettronica(Boolean astaElettronica) {
    this.astaElettronica = astaElettronica;
  }

  public DatiBaseStrumentiProceduraOnlyAsta(Map parameters) throws Exception {
    Boolean ricastae = NpaFunzioniManager.getBooleanByString((String) NpaFunzioniManager.getValoreParametro(parameters, "RICASTAE"));
    this.astaElettronica = Boolean.TRUE.equals(ricastae);
  }

}

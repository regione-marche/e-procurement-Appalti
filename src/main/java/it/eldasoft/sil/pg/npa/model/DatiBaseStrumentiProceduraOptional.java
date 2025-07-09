package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.util.Map;

public class DatiBaseStrumentiProceduraOptional extends DatiBaseStrumentiProceduraOnlyAsta {

  private EnumNPA accordoQuadro;

  public EnumNPA getAccordoQuadro() {
    return accordoQuadro;
  }

  public void setAccordoQuadro(EnumNPA accordoQuadro) {
    this.accordoQuadro = accordoQuadro;
  }

  public DatiBaseStrumentiProceduraOptional(Map parameters) throws Exception {
    super(parameters);
    Boolean accqua = NpaFunzioniManager.getBooleanByString((String) NpaFunzioniManager.getValoreParametro(parameters, "ACCQUA"));
    if(accqua != null && Boolean.TRUE.equals(accqua)) {
      this.accordoQuadro = new EnumNPA("accordoQuadro", "fa-mix");
    } else {
      this.accordoQuadro = new EnumNPA("accordoQuadro", "none");
    }
  }

}

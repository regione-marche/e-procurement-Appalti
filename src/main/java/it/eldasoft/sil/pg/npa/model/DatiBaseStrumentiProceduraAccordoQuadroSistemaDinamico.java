package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.util.Map;

public class DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico {

  private EnumNPA accordoQuadro;
  private EnumNPA sistemaDinamicoAcquisizione;

  public EnumNPA getAccordoQuadro() {
    return accordoQuadro;
  }

  public void setAccordoQuadro(EnumNPA accordoQuadro) {
    this.accordoQuadro = accordoQuadro;
  }

  public EnumNPA getSistemaDinamicoAcquisizione() {
    return sistemaDinamicoAcquisizione;
  }

  public void setSistemaDinamicoAcquisizione(EnumNPA sistemaDinamicoAcquisizione) {
    this.sistemaDinamicoAcquisizione = sistemaDinamicoAcquisizione;
  }

  public DatiBaseStrumentiProceduraAccordoQuadroSistemaDinamico(Map parameters) throws Exception {
    NpaFunzioniManager npaFunzioniManager = (NpaFunzioniManager) parameters.get("npaFunzioniManager");
    String issda = (String) NpaFunzioniManager.getValoreParametro(parameters, "ISSDA");
    if (issda != null)
      this.sistemaDinamicoAcquisizione = new EnumNPA("sistemaDinamicoAcquisizione", npaFunzioniManager.getTab2d1ByTab2tip(issda, "A1z32"));
    else
      this.sistemaDinamicoAcquisizione = new EnumNPA("sistemaDinamicoAcquisizione", "none");

    Boolean accqua = NpaFunzioniManager.getBooleanByString((String) NpaFunzioniManager.getValoreParametro(parameters, "ACCQUA"));
    if(accqua != null && Boolean.TRUE.equals(accqua)) {
      this.accordoQuadro = new EnumNPA("accordoQuadro", "fa-mix");
    } else {
      this.accordoQuadro = new EnumNPA("accordoQuadro", "none");
    }
  }
}

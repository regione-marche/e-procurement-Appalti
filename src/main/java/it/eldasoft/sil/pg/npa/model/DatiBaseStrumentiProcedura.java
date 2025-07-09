package it.eldasoft.sil.pg.npa.model;

import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;

import java.util.Map;

public class DatiBaseStrumentiProcedura extends DatiBaseStrumentiProceduraOptional {

  private EnumNPA sistemaDinamicoAcquisizione;

  public EnumNPA getSistemaDinamicoAcquisizione() {
    return sistemaDinamicoAcquisizione;
  }

  public void setSistemaDinamicoAcquisizione(EnumNPA sistemaDinamicoAcquisizione) {
    this.sistemaDinamicoAcquisizione = sistemaDinamicoAcquisizione;
  }

  public DatiBaseStrumentiProcedura(Map parameters) throws Exception {
    super(parameters);
    NpaFunzioniManager npaFunzioniManager = (NpaFunzioniManager) parameters.get("npaFunzioniManager");
    String issda = (String) NpaFunzioniManager.getValoreParametro(parameters, "ISSDA");
    if (issda != null)
      this.sistemaDinamicoAcquisizione = new EnumNPA("sistemaDinamicoAcquisizione", npaFunzioniManager.getTab2d1ByTab2tip(issda, "A1z32"));
    else
      this.sistemaDinamicoAcquisizione = new EnumNPA("sistemaDinamicoAcquisizione", "none");
  }

}

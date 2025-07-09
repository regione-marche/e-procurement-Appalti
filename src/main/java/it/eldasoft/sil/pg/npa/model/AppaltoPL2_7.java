package it.eldasoft.sil.pg.npa.model;

import java.util.Map;

public class AppaltoPL2_7 extends AppaltoPL {
  private DatiBaseProceduraCompleto datiBaseProcedura;

  public DatiBaseProceduraCompleto getDatiBaseProcedura() {
    return datiBaseProcedura;
  }

  public AppaltoPL2_7(Map parameters) throws Exception {
    super(parameters);
    this.datiBaseProcedura = new DatiBaseProceduraCompleto(parameters);
  }

}

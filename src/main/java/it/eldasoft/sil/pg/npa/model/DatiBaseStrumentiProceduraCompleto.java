package it.eldasoft.sil.pg.npa.model;

import java.util.Map;

public class DatiBaseStrumentiProceduraCompleto extends DatiBaseStrumentiProcedura {

  private Boolean fineSistemaDinamicoAcquisizione;

  public DatiBaseStrumentiProceduraCompleto(Map parameters) throws Exception {
    super(parameters);

    this.fineSistemaDinamicoAcquisizione = null;
  }
}

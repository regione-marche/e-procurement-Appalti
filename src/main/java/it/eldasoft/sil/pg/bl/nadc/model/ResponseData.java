package it.eldasoft.sil.pg.bl.nadc.model;


public class ResponseData {
  private Contratto contratto;
  private Liquidazione liq; // prima era Object

  public Contratto getContratto() {
    return contratto;
  }

  public void setContratto(Contratto contratto) {
    this.contratto = contratto;
  }

  public Liquidazione getLiq() {
    return liq;
  }

  public void setLiq(Liquidazione liq) {
    this.liq = liq;
  }
}

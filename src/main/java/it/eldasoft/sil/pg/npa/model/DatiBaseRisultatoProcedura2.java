package it.eldasoft.sil.pg.npa.model;

public class DatiBaseRisultatoProcedura2 {

  private EnumNPA giustificazione;

  public EnumNPA getGiustificazione() {
    return giustificazione;
  }

  public void setGiustificazione(EnumNPA giustificazione) {
    this.giustificazione = giustificazione;
  }

  public DatiBaseRisultatoProcedura2() {
  }

  public DatiBaseRisultatoProcedura2(EnumNPA giustificazione) {
    this.giustificazione = giustificazione;
  }
}
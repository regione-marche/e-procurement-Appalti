package it.eldasoft.sil.pg.ws.rest.eforms.model;


public class EntityItem {
  private String tipo;
  private String entita;
  private String chiave;
  private String valore;

  public EntityItem(String tipo, String entita, String chiave, String valore) {
    this.tipo = tipo;
    this.entita = entita;
    this.chiave = chiave;
    this.valore = valore;
  }

  // Getters & Setters
  public String getTipo() {
    return tipo;
  }
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }
  public String getEntita() {
    return entita;
  }
  public void setEntita(String entita) {
    this.entita = entita;
  }
  public String getChiave() {
    return chiave;
  }
  public void setChiave(String chiave) {
    this.chiave = chiave;
  }
  public String getValore() {
    return valore;
  }
  public void setValore(String valore) {
    this.valore = valore;
  }

  @Override
  public String toString() {
    return "EntityItem [tipo=" + tipo + ", entita=" + entita 
        + ", chiave=" + chiave + ", valore=" + valore + "]";
  }
}

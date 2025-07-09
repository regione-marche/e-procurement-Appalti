package it.eldasoft.sil.pg.nadc.model;

import java.util.List;

public class ImportazioneContrattiPCP {
  private String cig;
  private String idAppalto;
  private Rup rup;
  private DataEproc dataEproc;
  private List<Scheda> schede;
  
  public String getCig() {
    return cig;
  }
  
  public void setCig(String cig) {
    this.cig = cig;
  }
  
  public String getIdAppalto() {
    return idAppalto;
  }
  
  public void setIdAppalto(String idAppalto) {
    this.idAppalto = idAppalto;
  }
  
  public Rup getRup() {
    return rup;
  }
  
  public void setRup(Rup rup) {
    this.rup = rup;
  }
  
  public DataEproc getDataEproc() {
    return dataEproc;
  }
  
  public void setDataEproc(DataEproc dataEproc) {
    this.dataEproc = dataEproc;
  }
  
  public List<Scheda> getSchede() {
    return schede;
  }
  
  public void setSchede(List<Scheda> schede) {
    this.schede = schede;
  }

  public ImportazioneContrattiPCP() {
    
  }
  
  
}

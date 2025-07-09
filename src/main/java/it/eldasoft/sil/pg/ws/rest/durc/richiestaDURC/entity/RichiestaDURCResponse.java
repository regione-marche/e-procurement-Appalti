package it.eldasoft.sil.pg.ws.rest.durc.richiestaDURC.entity;


public class RichiestaDURCResponse {
  private Integer esito;
  private String messaggio;
  
  public Integer getEsito() {
    return esito;
  }
  
  public void setEsito(Integer esito) {
    this.esito = esito;
  }
  
  public String getMessaggio() {
    return messaggio;
  }
  
  public void setMessaggio(String messaggio) {
    this.messaggio = messaggio;
  }
  
  public RichiestaDURCResponse() {
    
  }

}

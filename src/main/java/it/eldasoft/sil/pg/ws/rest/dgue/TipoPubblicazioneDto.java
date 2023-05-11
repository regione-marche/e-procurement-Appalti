package it.eldasoft.sil.pg.ws.rest.dgue;


public class TipoPubblicazioneDto {
  private Integer id;
  private String descrizione;
  
  public TipoPubblicazioneDto(Integer id, String descrizione) {
    this.id = id;
    this.descrizione = descrizione;
  }

  public TipoPubblicazioneDto() {
  }

  /**
   * @return the id
   */
  public Integer getId() {
    return id;
  }
  
  /**
   * @param id the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }
  
  /**
   * @return the descrizione
   */
  public String getDescrizione() {
    return descrizione;
  }
  
  /**
   * @param descrizione the descrizione to set
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }
  
  
}

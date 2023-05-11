package it.eldasoft.sil.pg.ws.rest.dgue;


public class PubblicazioniDto {
  private String title;
  private String descr;
  private String tedUrl;
  private String tedReceptionId;
  private String nojcnNumber;
  private TipoPubblicazioneDto tipoPubblicazione;
  
  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }
  
  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }
  
  /**
   * @return the descr
   */
  public String getDescr() {
    return descr;
  }
  
  /**
   * @param descr the descr to set
   */
  public void setDescr(String descr) {
    this.descr = descr;
  }
  
  /**
   * @return the tedUrl
   */
  public String getTedUrl() {
    return tedUrl;
  }
  
  /**
   * @param tedUrl the tedUrl to set
   */
  public void setTedUrl(String tedUrl) {
    this.tedUrl = tedUrl;
  }
  
  /**
   * @return the tedReceptionId
   */
  public String getTedReceptionId() {
    return tedReceptionId;
  }
  
  /**
   * @param tedReceptionId the tedReceptionId to set
   */
  public void setTedReceptionId(String tedReceptionId) {
    this.tedReceptionId = tedReceptionId;
  }
  
  /**
   * @return the nojcnNumber
   */
  public String getNojcnNumber() {
    return nojcnNumber;
  }
  
  /**
   * @param nojcnNumber the nojcnNumber to set
   */
  public void setNojcnNumber(String nojcnNumber) {
    this.nojcnNumber = nojcnNumber;
  }
  
  /**
   * @return the tipoPubblicazione
   */
  public TipoPubblicazioneDto getTipoPubblicazione() {
    return tipoPubblicazione;
  }
  
  /**
   * @param tipoPubblicazione the tipoPubblicazione to set
   */
  public void setTipoPubblicazione(TipoPubblicazioneDto tipoPubblicazione) {
    this.tipoPubblicazione = tipoPubblicazione;
  }
  
  
}

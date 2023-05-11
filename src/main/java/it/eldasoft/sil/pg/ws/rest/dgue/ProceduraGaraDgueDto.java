package it.eldasoft.sil.pg.ws.rest.dgue;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class ProceduraGaraDgueDto {
  private String codiceGara;
  private String titolo;
  private String descrizione;
  private String tipoProcedura;
  private String oggetto;//lavori, forniture, servizi ENUM ????
  private List<LottoDgueDto> lotti;
  private TecniDgueDto rup;
  private List<String> cpv;
  private String codiceOffertePresentate;
  private Integer maxLottiOfferta;
  private Integer maxLottiAggiudicabili;
  private List<PubblicazioniDto> pubblicazioni;
  private String codiceANAC;
  
  /**
   * @return the codiceGara
   */
  public String getCodiceGara() {
    return codiceGara;
  }


  /**
   * @param codiceGara the codiceGara to set
   */
  public void setCodiceGara(String codiceGara) {
    this.codiceGara = codiceGara;
  }


  /**
   * @return the codiceOffertePresentate
   */
  public String getCodiceOffertePresentate() {
    return codiceOffertePresentate;
  }

  
  /**
   * @param codiceOffertePresentate the codiceOffertePresentate to set
   */
  public void setCodiceOffertePresentate(String codiceOffertePresentate) {
    this.codiceOffertePresentate = codiceOffertePresentate;
  }

  
  /**
   * @return the maxLottiOfferta
   */
  public Integer getMaxLottiOfferta() {
    return maxLottiOfferta;
  }

  
  /**
   * @param maxLottiOfferta the maxLottiOfferta to set
   */
  public void setMaxLottiOfferta(Integer maxLottiOfferta) {
    this.maxLottiOfferta = maxLottiOfferta;
  }

  
  /**
   * @return the maxLottiAggiudicabili
   */
  public Integer getMaxLottiAggiudicabili() {
    return maxLottiAggiudicabili;
  }

  
  /**
   * @param maxLottiAggiudicabili the maxLottiAggiudicabili to set
   */
  public void setMaxLottiAggiudicabili(Integer maxLottiAggiudicabili) {
    this.maxLottiAggiudicabili = maxLottiAggiudicabili;
  }

  /**
   * @return the titolo
   */
  public String getTitolo() {
    return titolo;
  }
  
  /**
   * @param titolo the titolo to set
   */
  public void setTitolo(String titolo) {
    this.titolo = titolo;
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
  
  /**
   * @return the tipoProcedura
   */
  public String getTipoProcedura() {
    return tipoProcedura;
  }

  /**
   * @param tipoProcedura the tipoProcedura to set
   */
  public void setTipoProcedura(String tipoProcedura) {
    this.tipoProcedura = tipoProcedura;
  }

  /**
   * @return the oggetto
   */
  public String getOggetto() {
    return oggetto;
  }
  
  /**
   * @param oggetto the oggetto to set
   */
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }
  
  /**
   * @return the lotti
   */
  public List<LottoDgueDto> getLotti() {
    return lotti;
  }
  
  /**
   * @param lotti the lotti to set
   */
  public void setLotti(List<LottoDgueDto> lotti) {
    this.lotti = lotti;
  }

  /**
   * @return the rup
   */
  public TecniDgueDto getRup() {
    return rup;
  }

  /**
   * @param rup the rup to set
   */
  public void setRup(TecniDgueDto rup) {
    this.rup = rup;
  }

  /**
   * @return the cpv
   */
  public List<String> getCpv() {
    return cpv;
  }

  /**
   * @param cpv the cpv to set
   */
  public void setCpv(List<String> cpv) {
    this.cpv = cpv;
  }


  /**
   * @return the pubblicazioni
   */
  public List<PubblicazioniDto> getPubblicazioni() {
    return pubblicazioni;
  }


  /**
   * @param pubblicazioni the pubblicazioni to set
   */
  public void setPubblicazioni(List<PubblicazioniDto> pubblicazioni) {
    this.pubblicazioni = pubblicazioni;
  }

  /**
   * @return the codiceANAC
   */
  public String getCodiceANAC() {
    return codiceANAC;
  }

  /**
   * @param codiceANAC the codiceANAC to set
   */
  public void setCodiceANAC(String codiceANAC) {
    this.codiceANAC = codiceANAC;
  }
  
  
}

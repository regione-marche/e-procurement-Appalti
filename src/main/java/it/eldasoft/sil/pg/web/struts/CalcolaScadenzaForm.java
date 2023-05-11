/*
 * Created on 07/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.util.Date;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import org.apache.struts.action.ActionForm;

/**
 * Form di raccolta dei dati provenienti dalla richiesta per il calcolo delle
 * dati di scadenza
 * 
 * @author Stefano.Sabbadin
 */
public class CalcolaScadenzaForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -3526689537388687100L;

  /** tipo di calcolo */
  private Integer           calcolo;
  /** tipo di appalto */
  private Integer           tipgen;
  /** importo appalto (dato ricevuto nel request) */
  private Double            imptor;
  /** tipo procedura */
  private Integer           tipgar;
  /** procedura urgente */
  private String            prourg;
  /** termine ridotto */
  private String            terrid;
  /** bando web */
  private String            banweb;
  /** documenti su web */
  private String            docweb;
  /** oggetto contratto */
  private Integer           oggcont;
  /**
   * valore della data di origine, sulla quale sommare i giorni frutto del
   * calcolo
   */
  private Date              dataOrigine;
  /** campo data destinazione nel quale inserire il risultato del calcolo */
  private String            campoDataDestinazione;

  public CalcolaScadenzaForm() {
    this.calcolo = null;
    this.tipgen = null;
    this.imptor = null;
    this.tipgar = null;
    this.prourg = null;
    this.terrid = null;
    this.banweb = null;
    this.docweb = null;
    this.oggcont = null;
  }

  /**
   * @return Ritorna tipoCalcolo.
   */
  public Integer getCalcolo() {
    return calcolo;
  }

  /**
   * @param tipoCalcolo
   *        tipoCalcolo da settare internamente alla classe.
   */
  public void setCalcolo(Integer tipoCalcolo) {
    this.calcolo = tipoCalcolo;
  }

  /**
   * @return Ritorna tipoAppalto.
   */
  public Integer getTipgen() {
    return tipgen;
  }

  /**
   * @param tipoAppalto
   *        tipoAppalto da settare internamente alla classe.
   */
  public void setTipgen(Integer tipoAppalto) {
    this.tipgen = tipoAppalto;
  }

  /**
   * @return Ritorna tipoProcedura.
   */
  public Integer getTipgar() {
    return tipgar;
  }

  /**
   * @param tipoProcedura
   *        tipoProcedura da settare internamente alla classe.
   */
  public void setTipgar(Integer tipoProcedura) {
    this.tipgar = tipoProcedura;
  }

  /**
   * @return Ritorna proceduraUrgente.
   */
  public String getProurg() {
    return prourg;
  }

  /**
   * @param proceduraUrgente
   *        proceduraUrgente da settare internamente alla classe.
   */
  public void setProurg(String proceduraUrgente) {
    this.prourg = UtilityStringhe.convertiStringaVuotaInNull(proceduraUrgente);
  }

  /**
   * @return Ritorna termineRidotto.
   */
  public String getTerrid() {
    return terrid;
  }

  /**
   * @param termineRidotto
   *        termineRidotto da settare internamente alla classe.
   */
  public void setTerrid(String termineRidotto) {
    this.terrid = UtilityStringhe.convertiStringaVuotaInNull(termineRidotto);
  }

  /**
   * @return Ritorna bandoWeb.
   */
  public String getBanweb() {
    return banweb;
  }

  /**
   * @param bandoWeb
   *        bandoWeb da settare internamente alla classe.
   */
  public void setBanweb(String bandoWeb) {
    this.banweb = UtilityStringhe.convertiStringaVuotaInNull(bandoWeb);
  }

  /**
   * @return Ritorna docWeb.
   */
  public String getDocweb() {
    return docweb;
  }

  /**
   * @param docWeb
   *        docWeb da settare internamente alla classe.
   */
  public void setDocweb(String docWeb) {
    this.docweb = UtilityStringhe.convertiStringaVuotaInNull(docWeb);
  }

  /**
   * @return Ritorna oggettoContratto.
   */
  public Integer getOggcont() {
    return oggcont;
  }

  /**
   * @param oggettoContratto
   *        oggettoContratto da settare internamente alla classe.
   */
  public void setOggcont(Integer oggettoContratto) {
    this.oggcont = oggettoContratto;
  }

  /**
   * @return Ritorna importoGara.
   */
  public Double getImportoAppalto() {
    return this.imptor;
  }

  /**
   * @param importoGara
   *        importoGara da settare internamente alla classe.
   */
  public void setImptor(String importoGara) {
    this.imptor = UtilityNumeri.convertiDouble(importoGara,
        UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
  }

  /**
   * @return Ritorna dataOrigine.
   */
  public Date getDataOrigineAsDate() {
    return dataOrigine;
  }

  /**
   * @param dataOrigine
   *        dataOrigine da settare internamente alla classe.
   */
  public void setDataOrigine(String dataOrigine) {
    this.dataOrigine = UtilityDate.convertiData(dataOrigine,
        UtilityDate.FORMATO_GG_MM_AAAA_CON_TRATTINI);
  }

  /**
   * @return Ritorna campoDataDestinazione.
   */
  public String getCampoDataDestinazione() {
    return campoDataDestinazione;
  }

  /**
   * @param campoDataDestinazione
   *        campoDataDestinazione da settare internamente alla classe.
   */
  public void setCampoDataDestinazione(String campoDataDestinazione) {
    this.campoDataDestinazione = campoDataDestinazione;
  }

}

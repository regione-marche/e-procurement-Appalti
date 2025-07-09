/*
 * Created on 30/mar/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts.sigla;

import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form per la memorizzazione dei criteri di filtro da applicare nella ricerca
 * degli utenti
 *
 * @author Stefano.Sabbadin
 */
public class TrovaArticoloForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -5781323251043286752L;

  private String            codice;
  private String            operatoreCodice;
  private String            descrizione;
  private String            operatoreDescrizione;
  private String            unitaDiMisura;
  private String            operatoreUnitaDiMisura;
  private String            codiceIva;
  private String            operatoreCodiceIva;
  private String            tipoGestione;
  private String            operatoreTipoGestione;
  private String            gestioneInventario;
  private String			gestioneMagazzino;
  private boolean           visualizzazioneAvanzata;
  
  private String            codiceCondition;
  private String            descrizioneCondition;
  private String            unitaDiMisuraCondition;
  private String            codiceIvaCondition;
  private String            tipoGestioneCondition;
  private String            gestioneInventarioCondition;
  private String            gestioneMagazzinoCondition;
  
  private String            risPerPagina;
  private String            activePage;
  
  /**
   * Costruttore della classe: inizializza le variabili a vuote
   */
  public TrovaArticoloForm() {
    this.inizializzaOggetto();
  }

  /**
   * Esecuzione del reset
   */
  @Override
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.inizializzaOggetto();
  }

  /**
   * Inizializza un oggetto vuoto
   */
  private void inizializzaOggetto() {
    // ATTENZIONE: gli operatori nella form di trova di default sono impostati a
    // "contiene" in modo da effettuare il like
    // Nel momento in cui si crea l'oggetto per il model, si va a sovrascrivere
    // l'impostazione di default degli operatori nell'oggetto del model

    this.codice = null;
    this.operatoreCodice = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.descrizione = null;
    this.operatoreDescrizione = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.codiceIva = null;
    this.operatoreCodiceIva = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.unitaDiMisura = null;
    this.operatoreUnitaDiMisura = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.tipoGestione = null;
    this.operatoreTipoGestione = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.risPerPagina = "20";
    this.activePage = "0";
    // campo collegato a una checkbox, non va va inizializzato direttamente a true ma variato prima di passarlo alla pagina in quanto il
    // setter viene richiamato solo se la checkbox e' selezionata pertanto non verrebbe mai resettato a false
    this.visualizzazioneAvanzata = false;
    this.gestioneInventario = null;
    this.gestioneMagazzino = null;
    this.codiceCondition = "AND";
    this.descrizioneCondition = "AND";
    this.unitaDiMisuraCondition = "AND";
    this.codiceIvaCondition = "AND";
    this.tipoGestioneCondition = "AND";
    this.gestioneInventarioCondition = "AND";
    this.gestioneMagazzinoCondition = "AND";
    
  }

  
  public String getCodice() {
    return codice;
  }

  
  public void setCodice(String codice) {
    this.codice = codice;
  }

  
  public String getOperatoreCodice() {
    return operatoreCodice;
  }

  
  public void setOperatoreCodice(String operatoreCodice) {
    this.operatoreCodice = operatoreCodice;
  }

  
  public String getDescrizione() {
    return descrizione;
  }

  
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  
  public String getOperatoreDescrizione() {
    return operatoreDescrizione;
  }

  
  public void setOperatoreDescrizione(String operatoreDescrizione) {
    this.operatoreDescrizione = operatoreDescrizione;
  }

  
  public String getUnitaDiMisura() {
    return unitaDiMisura;
  }

  
  public void setUnitaDiMisura(String unitaDiMisura) {
    this.unitaDiMisura = unitaDiMisura;
  }

  
  public String getOperatoreUnitaDiMisura() {
    return operatoreUnitaDiMisura;
  }

  
  public void setOperatoreUnitaDiMisura(String operatoreUnitaDiMisura) {
    this.operatoreUnitaDiMisura = operatoreUnitaDiMisura;
  }

  
  public String getCodiceIva() {
    return codiceIva;
  }

  
  public void setCodiceIva(String codiceIva) {
    this.codiceIva = codiceIva;
  }

  
  public String getOperatoreCodiceIva() {
    return operatoreCodiceIva;
  }

  
  public void setOperatoreCodiceIva(String operatoreCodiceIva) {
    this.operatoreCodiceIva = operatoreCodiceIva;
  }

  
  public String getTipoGestione() {
    return tipoGestione;
  }

  
  public void setTipoGestione(String tipoGestione) {
    this.tipoGestione = tipoGestione;
  }

  
  public String getOperatoreTipoGestione() {
    return operatoreTipoGestione;
  }

  
  public void setOperatoreTipoGestione(String operatoreTipoGestione) {
    this.operatoreTipoGestione = operatoreTipoGestione;
  }

  
  public String getRisPerPagina() {
    return risPerPagina;
  }

  
  public void setRisPerPagina(String risPerPagina) {
    this.risPerPagina = risPerPagina;
  }

  
  public String getGestioneInventario() {
    return gestioneInventario;
  }

  
  public void setGestioneInventario(String gestioneInventario) {
    this.gestioneInventario = gestioneInventario;
  }

  
  public String getGestioneMagazzino() {
    return gestioneMagazzino;
  }

  
  public void setGestioneMagazzino(String gestioneMagazzino) {
    this.gestioneMagazzino = gestioneMagazzino;
  }

  
  public boolean isVisualizzazioneAvanzata() {
    return visualizzazioneAvanzata;
  }

  
  public void setVisualizzazioneAvanzata(boolean visualizzazioneAvanzata) {
    this.visualizzazioneAvanzata = visualizzazioneAvanzata;
  }

  public String getActivePage() {
    return activePage;
  }

  public void setActivePage(String activePage) {
    this.activePage = activePage;
  }

  public String getCodiceCondition() {
    return codiceCondition;
  }

  public void setCodiceCondition(String codiceCondition) {
    this.codiceCondition = codiceCondition;
  }

  
  public String getDescrizioneCondition() {
    return descrizioneCondition;
  }

  
  public void setDescrizioneCondition(String descrizioneCondition) {
    this.descrizioneCondition = descrizioneCondition;
  }

  
  public String getUnitaDiMisuraCondition() {
    return unitaDiMisuraCondition;
  }

  
  public void setUnitaDiMisuraCondition(String unitaDiMisuraCondition) {
    this.unitaDiMisuraCondition = unitaDiMisuraCondition;
  }

  
  public String getCodiceIvaCondition() {
    return codiceIvaCondition;
  }

  
  public void setCodiceIvaCondition(String codiceIvaCondition) {
    this.codiceIvaCondition = codiceIvaCondition;
  }

  
  public String getTipoGestioneCondition() {
    return tipoGestioneCondition;
  }

  
  public void setTipoGestioneCondition(String tipoGestioneCondition) {
    this.tipoGestioneCondition = tipoGestioneCondition;
  }

  
  public String getGestioneInventarioCondition() {
    return gestioneInventarioCondition;
  }

  
  public void setGestioneInventarioCondition(String gestioneInventarioCondition) {
    this.gestioneInventarioCondition = gestioneInventarioCondition;
  }

  public String getGestioneMagazzinoCondition() {
    return gestioneMagazzinoCondition;
  }

  public void setGestioneMagazzinoCondition(String gestioneMagazzinoCondition) {
    this.gestioneMagazzinoCondition = gestioneMagazzinoCondition;
  }

}
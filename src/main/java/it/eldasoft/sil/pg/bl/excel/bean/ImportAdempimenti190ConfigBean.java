/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel.bean;

import it.eldasoft.sil.pg.bl.excel.ExcelConfigBean;

/**
 *
 * @author marco.perazzetta
 */
public class ImportAdempimenti190ConfigBean extends ExcelConfigBean {

	private boolean isUtenteAmministratore;
	private boolean aggiorna;
	private boolean aggiornaImpSommeLiquidate;
	private boolean aggiornaImpAggiudicazione;
	private boolean aggiornaDataInizio;
	private boolean aggiornaDataUltimazione;
	private boolean aggiornaPartecipante;
	private Long idAnticor;
	private String annoAdempimento;
	private String idStazioneAppaltante;
	private String codiceFiscaleStazioneAppaltante;
	private String codiceFiscaleResponsabile;
	private String nomeCognomeResponsabile;
	public static final String NO_GESTIONE_UFFICI_INTESTATARI = "*";

	public ImportAdempimenti190ConfigBean() {
	}
	
	public boolean isUtenteAmministratore() {
		return isUtenteAmministratore;
	}

	public void setUtenteAmministratore(boolean isUtenteAmministratore) {
		this.isUtenteAmministratore = isUtenteAmministratore;
	}

	/**
	 * @return the aggiorna
	 */
	public boolean isAggiorna() {
		return aggiorna;
	}

	/**
	 * @param aggiorna the aggiorna to set
	 */
	public void setAggiorna(boolean aggiorna) {
		this.aggiorna = aggiorna;
	}

	/**
	 * @return the aggiornaImpSommeLiquidate
	 */
	public boolean isAggiornaImpSommeLiquidate() {
		return aggiornaImpSommeLiquidate;
	}

	/**
	 * @param aggiornaImpSommeLiquidate the aggiornaImpSommeLiquidate to set
	 */
	public void setAggiornaImpSommeLiquidate(boolean aggiornaImpSommeLiquidate) {
		this.aggiornaImpSommeLiquidate = aggiornaImpSommeLiquidate;
	}

	/**
	 * @return the aggiornaImpAggiudicazione
	 */
	public boolean isAggiornaImpAggiudicazione() {
		return aggiornaImpAggiudicazione;
	}

	/**
	 * @param aggiornaImpAggiudicazione the aggiornaImpAggiudicazione to set
	 */
	public void setAggiornaImpAggiudicazione(boolean aggiornaImpAggiudicazione) {
		this.aggiornaImpAggiudicazione = aggiornaImpAggiudicazione;
	}

	/**
	 * @return the aggiornaDataInizio
	 */
	public boolean isAggiornaDataInizio() {
		return aggiornaDataInizio;
	}

	/**
	 * @param aggiornaDataInizio the aggiornaDataInizio to set
	 */
	public void setAggiornaDataInizio(boolean aggiornaDataInizio) {
		this.aggiornaDataInizio = aggiornaDataInizio;
	}

	/**
	 * @return the aggiornaDataUltimazione
	 */
	public boolean isAggiornaDataUltimazione() {
		return aggiornaDataUltimazione;
	}

	/**
	 * @param aggiornaDataUltimazione the aggiornaDataUltimazione to set
	 */
	public void setAggiornaDataUltimazione(boolean aggiornaDataUltimazione) {
		this.aggiornaDataUltimazione = aggiornaDataUltimazione;
	}

	/**
	 * @return the aggiornaPartecipante
	 */
	public boolean isAggiornaPartecipante() {
		return aggiornaPartecipante;
	}

	/**
	 * @param aggiornaPartecipante the aggiornaPartecipante to set
	 */
	public void setAggiornaPartecipante(boolean aggiornaPartecipante) {
		this.aggiornaPartecipante = aggiornaPartecipante;
	}

	/**
	 * @return the idAnticor
	 */
	public Long getIdAnticor() {
		return idAnticor;
	}

	/**
	 * @param idAnticor the idAnticor to set
	 */
	public void setIdAnticor(Long idAnticor) {
		this.idAnticor = idAnticor;
	}

	/**
	 * @return the annoAdempimento
	 */
	public String getAnnoAdempimento() {
		return annoAdempimento;
	}

	/**
	 * @param annoAdempimento the annoAdempimento to set
	 */
	public void setAnnoAdempimento(String annoAdempimento) {
		this.annoAdempimento = annoAdempimento;
	}

	private String ufficioIntestatario;

	public String getUfficioIntestatario() {
		return ufficioIntestatario;
	}

	public void setUfficioIntestatario(String ufficioIntestatario) {
		this.ufficioIntestatario = ufficioIntestatario;
	}

	public String getIdStazioneAppaltante() {
		return idStazioneAppaltante;
	}

	public void setIdStazioneAppaltante(String idStazioneAppaltante) {
		this.idStazioneAppaltante = idStazioneAppaltante;
	}

	public String getCodiceFiscaleStazioneAppaltante() {
		return codiceFiscaleStazioneAppaltante;
	}

	public void setCodiceFiscaleStazioneAppaltante(String codiceFiscaleStazioneAppaltante) {
		this.codiceFiscaleStazioneAppaltante = codiceFiscaleStazioneAppaltante;
	}

	public String getCodiceFiscaleResponsabile() {
		return codiceFiscaleResponsabile;
	}

	public void setCodiceFiscaleResponsabile(String codiceFiscaleResponsabile) {
		this.codiceFiscaleResponsabile = codiceFiscaleResponsabile;
	}

	public String getNomeCognomeResponsabile() {
		return nomeCognomeResponsabile;
	}

	public void setNomeCognomeResponsabile(String nomeCognomeResponsabile) {
		this.nomeCognomeResponsabile = nomeCognomeResponsabile;
	}
}

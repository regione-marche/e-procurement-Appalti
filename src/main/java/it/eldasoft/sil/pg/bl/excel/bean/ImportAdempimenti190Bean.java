/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel.bean;

import it.eldasoft.sil.pg.bl.excel.ExcelRowBean;
import java.util.Date;

/**
 *
 * @author marco.perazzetta
 */
public class ImportAdempimenti190Bean extends ExcelRowBean {

	private String codFiscProp;
	private String ragSocialeProp;
	private Long anno;
	private String cig;
	private String oggettoLotto;
	private String sceltaContraente;
	private String codFiscOpITA;
	private String codFiscOpEST;
	private String ragSocOperatore;
	private String denGruppo;
	private String ruolo;
	private String aggiudicatario;
	private Double importoAgg;
	private Date dataInizio;
	private Date dataUltimazione;
	private Double importoSommeLiquidate;
	private int stato;
	private Long idLotto;
	private String lottoInbo;
	private String daAnnoPrec;
	private String codFiscResponsabile;
	private String nomeResponsabile;
	public static final String LOTTO_IN_BACKOFFICE = "1";
	public static final String LOTTO_NOT_IN_BACKOFFICE = "2";
	public static final String ANNO_PRECEDENTE = "1";
	public static final String ANNO_CORRENTE = "2";
	public static final String ANNO_PRECEDENTE_RIPORTATO_ANNO_CORRENTE = "3";
	public static final String AGGIUDICATARIA = "1";
	public static final String NON_AGGIUDICATARIA = "2";
	public static final String PUBBLICABILE = "1";
	public static final String NON_PUBBLICABILE = "2";
	public static final String INVIABILE = "1";
	public static final String NON_INVIABILE = "2";
	public static final long RAGGRUPPAMENTO_IMPRESE = 2;
	public static final long IMPRESA_SINGOLA = 1;

	public ImportAdempimenti190Bean() {
	}

	/**
	 * @return the codFiscProp
	 */
	public String getCodFiscProp() {
		return codFiscProp;
	}

	/**
	 * @param codFiscProp the codFiscProp to set
	 */
	public void setCodFiscProp(String codFiscProp) {
		this.codFiscProp = codFiscProp;
	}

	/**
	 * @return the ragSocialeProp
	 */
	public String getRagSocialeProp() {
		return ragSocialeProp;
	}

	/**
	 * @param ragSocialeProp the ragSocialeProp to set
	 */
	public void setRagSocialeProp(String ragSocialeProp) {
		this.ragSocialeProp = ragSocialeProp;
	}

	/**
	 * @return the anno
	 */
	public Long getAnno() {
		return anno;
	}

	/**
	 * @param anno the anno to set
	 */
	public void setAnno(Long anno) {
		this.anno = anno;
	}

	/**
	 * @return the cig
	 */
	public String getCig() {
		return cig;
	}

	/**
	 * @param cig the cig to set
	 */
	public void setCig(String cig) {
		this.cig = cig;
	}

	/**
	 * @return the oggettoLotto
	 */
	public String getOggettoLotto() {
		return oggettoLotto;
	}

	/**
	 * @param oggettoLotto the oggettoLotto to set
	 */
	public void setOggettoLotto(String oggettoLotto) {
		this.oggettoLotto = oggettoLotto;
	}

	/**
	 * @return the sceltaContraente
	 */
	public String getSceltaContraente() {
		return sceltaContraente;
	}

	/**
	 * @param sceltaContraente the sceltaContraente to set
	 */
	public void setSceltaContraente(String sceltaContraente) {
		this.sceltaContraente = sceltaContraente;
	}

	/**
	 * @return the codFiscOpITA
	 */
	public String getCodFiscOpITA() {
		return codFiscOpITA;
	}

	/**
	 * @param codFiscOpITA the codFiscOpITA to set
	 */
	public void setCodFiscOpITA(String codFiscOpITA) {
		this.codFiscOpITA = codFiscOpITA;
	}

	/**
	 * @return the codFiscOpEST
	 */
	public String getCodFiscOpEST() {
		return codFiscOpEST;
	}

	/**
	 * @param codFiscOpEST the codFiscOpEST to set
	 */
	public void setCodFiscOpEST(String codFiscOpEST) {
		this.codFiscOpEST = codFiscOpEST;
	}

	/**
	 * @return the ragSocOperatore
	 */
	public String getRagSocOperatore() {
		return ragSocOperatore;
	}

	/**
	 * @param ragSocOperatore the ragSocOperatore to set
	 */
	public void setRagSocOperatore(String ragSocOperatore) {
		this.ragSocOperatore = ragSocOperatore;
	}

	/**
	 * @return the denGruppo
	 */
	public String getDenGruppo() {
		return denGruppo;
	}

	/**
	 * @param denGruppo the denGruppo to set
	 */
	public void setDenGruppo(String denGruppo) {
		this.denGruppo = denGruppo;
	}

	/**
	 * @return the ruolo
	 */
	public String getRuolo() {
		return ruolo;
	}

	/**
	 * @param ruolo the ruolo to set
	 */
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}

	/**
	 * @return the aggiudicatario
	 */
	public String getAggiudicatario() {
		return aggiudicatario;
	}

	/**
	 * @param aggiudicatario the aggiudicatario to set
	 */
	public void setAggiudicatario(String aggiudicatario) {
		this.aggiudicatario = aggiudicatario;
	}

	/**
	 * @return the importoAgg
	 */
	public Double getImportoAgg() {
		return importoAgg;
	}

	/**
	 * @param importoAgg the importoAgg to set
	 */
	public void setImportoAgg(Double importoAgg) {
		this.importoAgg = importoAgg;
	}

	/**
	 * @return the dataInizio
	 */
	public Date getDataInizio() {
		return dataInizio;
	}

	/**
	 * @param dataInizio the dataInizio to set
	 */
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	/**
	 * @return the dataUltimazione
	 */
	public Date getDataUltimazione() {
		return dataUltimazione;
	}

	/**
	 * @param dataUltimazione the dataUltimazione to set
	 */
	public void setDataUltimazione(Date dataUltimazione) {
		this.dataUltimazione = dataUltimazione;
	}

	/**
	 * @return the importoSommeLiquidate
	 */
	public Double getImportoSommeLiquidate() {
		return importoSommeLiquidate;
	}

	/**
	 * @param importoSommeLiquidate the importoSommeLiquidate to set
	 */
	public void setImportoSommeLiquidate(Double importoSommeLiquidate) {
		this.importoSommeLiquidate = importoSommeLiquidate;
	}

	/**
	 * @return the stato
	 */
	public int getStato() {
		return stato;
	}

	/**
	 * @param stato the stato to set
	 */
	public void setStato(int stato) {
		this.stato = stato;
	}

	/**
	 * @return the idLotto
	 */
	public Long getIdLotto() {
		return idLotto;
	}

	/**
	 * @param idLotto the idLotto to set
	 */
	public void setIdLotto(Long idLotto) {
		this.idLotto = idLotto;
	}

	/**
	 * @return the lottoInbo
	 */
	public String getLottoInbo() {
		return lottoInbo;
	}

	/**
	 * @param lottoInbo the lottoInbo to set
	 */
	public void setLottoInbo(String lottoInbo) {
		this.lottoInbo = lottoInbo;
	}

	/**
	 * @return the daAnnoPrec
	 */
	public String getDaAnnoPrec() {
		return daAnnoPrec;
	}

	/**
	 * @param daAnnoPrec the daAnnoPrec to set
	 */
	public void setDaAnnoPrec(String daAnnoPrec) {
		this.daAnnoPrec = daAnnoPrec;
	}

	/**
	 * @return the codFiscResponsabile
	 */
	public String getCodFiscResponsabile() {
		return codFiscResponsabile;
	}

	/**
	 * @param codFiscResponsabile the codFiscResponsabile to set
	 */
	public void setCodFiscResponsabile(String codFiscResponsabile) {
		this.codFiscResponsabile = codFiscResponsabile;
	}

	/**
	 * @return the nomeResponsabile
	 */
	public String getNomeResponsabile() {
		return nomeResponsabile;
	}

	/**
	 * @param nomeRepsonsabile the nomeRepsonsabile to set
	 */
	public void setNomeResponsabile(String nomeResponsabile) {
		this.nomeResponsabile = nomeResponsabile;
	}
	
}

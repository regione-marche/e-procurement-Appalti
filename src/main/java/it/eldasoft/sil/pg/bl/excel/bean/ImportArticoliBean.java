/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel.bean;

import it.eldasoft.sil.pg.bl.excel.ExcelRowBean;

/**
 *
 * @author marco.perazzetta
 */
public class ImportArticoliBean extends ExcelRowBean {

	private String codCat;
	private String codArt;
	private String tipoArt;
	private String desc;
	private String descTec;
	private String oblInsImg;
	private String oblInsDesAgg;
	private String oblInsDim;
	private String oblInsCert;
	private String cert;
	private String oblInsScedTec;
	private String oblInsGar;
	private Integer gar;
	private String colore;
	private String prezzoRifA;
	private String unitaMisuraPerPrz;
	private Integer decUnitaMisuraPerPrz;
	private String unitaMisuraAcq;
	private Integer decUnitaMisuraAcq;
	private Double qtaUnitaMisuraAcq;
	private Double qtaMinPerUnitaMisuraPrz;
	private Double qtaMaxPerUnitaMisuraPrz;
	private Integer tempoMaxConsegna;
	private String unitaMisuraTempoConsegna;
	private String articoloAcqVerde;
	private String prodottoDaVerificare;
	private String note;
	private Long tipoArticoloId;
	private Long statoArticoloId;
	private Long prezzoUnitarioRiferitoAId;
	private Long tempoConsegnaId;
	private Long unitaMisuraPrzId;
	private Long unitaMisuraAcqId;
	private Long numeroCategoria;
	private String statoArticolo;

	public ImportArticoliBean() {
	}

	/**
	 * @return the codCat
	 */
	public String getCodCat() {
		return codCat;
	}

	/**
	 * @param codCat the codCat to set
	 */
	public void setCodCat(String codCat) {
		this.codCat = codCat;
	}

	/**
	 * @return the codArt
	 */
	public String getCodArt() {
		return codArt;
	}

	/**
	 * @param codArt the codArt to set
	 */
	public void setCodArt(String codArt) {
		this.codArt = codArt;
	}

	/**
	 * @return the tipoArt
	 */
	public String getTipoArt() {
		return tipoArt;
	}

	/**
	 * @param tipoArt the tipoArt to set
	 */
	public void setTipoArt(String tipoArt) {
		this.tipoArt = tipoArt;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the descTec
	 */
	public String getDescTec() {
		return descTec;
	}

	/**
	 * @param descTec the descTec to set
	 */
	public void setDescTec(String descTec) {
		this.descTec = descTec;
	}

	/**
	 * @return the oblInsImg
	 */
	public String getOblInsImg() {
		return oblInsImg;
	}

	/**
	 * @param oblInsImg the oblInsImg to set
	 */
	public void setOblInsImg(String oblInsImg) {
		this.oblInsImg = oblInsImg;
	}

	/**
	 * @return the oblInsDesAgg
	 */
	public String getOblInsDesAgg() {
		return oblInsDesAgg;
	}

	/**
	 * @param oblInsDesAgg the oblInsDesAgg to set
	 */
	public void setOblInsDesAgg(String oblInsDesAgg) {
		this.oblInsDesAgg = oblInsDesAgg;
	}

	/**
	 * @return the oblInsDim
	 */
	public String getOblInsDim() {
		return oblInsDim;
	}

	/**
	 * @param oblInsDim the oblInsDim to set
	 */
	public void setOblInsDim(String oblInsDim) {
		this.oblInsDim = oblInsDim;
	}

	/**
	 * @return the oblInsCert
	 */
	public String getOblInsCert() {
		return oblInsCert;
	}

	/**
	 * @param oblInsCert the oblInsCert to set
	 */
	public void setOblInsCert(String oblInsCert) {
		this.oblInsCert = oblInsCert;
	}

	/**
	 * @return the cert
	 */
	public String getCert() {
		return cert;
	}

	/**
	 * @param cert the cert to set
	 */
	public void setCert(String cert) {
		this.cert = cert;
	}

	/**
	 * @return the oblInsScedTec
	 */
	public String getOblInsScedTec() {
		return oblInsScedTec;
	}

	/**
	 * @param oblInsScedTec the oblInsScedTec to set
	 */
	public void setOblInsScedTec(String oblInsScedTec) {
		this.oblInsScedTec = oblInsScedTec;
	}

	/**
	 * @return the oblInsGar
	 */
	public String getOblInsGar() {
		return oblInsGar;
	}

	/**
	 * @param oblInsGar the oblInsGar to set
	 */
	public void setOblInsGar(String oblInsGar) {
		this.oblInsGar = oblInsGar;
	}

	/**
	 * @return the gar
	 */
	public Integer getGar() {
		return gar;
	}

	/**
	 * @param gar the gar to set
	 */
	public void setGar(Integer gar) {
		this.gar = gar;
	}

	/**
	 * @return the colore
	 */
	public String getColore() {
		return colore;
	}

	/**
	 * @param colore the colore to set
	 */
	public void setColore(String colore) {
		this.colore = colore;
	}

	/**
	 * @return the prezzoRifA
	 */
	public String getPrezzoRifA() {
		return prezzoRifA;
	}

	/**
	 * @param prezzoRifA the prezzoRifA to set
	 */
	public void setPrezzoRifA(String prezzoRifA) {
		this.prezzoRifA = prezzoRifA;
	}

	/**
	 * @return the unitaMisuraPerPrz
	 */
	public String getUnitaMisuraPerPrz() {
		return unitaMisuraPerPrz;
	}

	/**
	 * @param unitaMisuraPerPrz the unitaMisuraPerPrz to set
	 */
	public void setUnitaMisuraPerPrz(String unitaMisuraPerPrz) {
		this.unitaMisuraPerPrz = unitaMisuraPerPrz;
	}

	/**
	 * @return the decUnitaMisuraPerPrz
	 */
	public Integer getDecUnitaMisuraPerPrz() {
		return decUnitaMisuraPerPrz;
	}

	/**
	 * @param decUnitaMisuraPerPrz the decUnitaMisuraPerPrz to set
	 */
	public void setDecUnitaMisuraPerPrz(Integer decUnitaMisuraPerPrz) {
		this.decUnitaMisuraPerPrz = decUnitaMisuraPerPrz;
	}

	/**
	 * @return the unitaMisuraAcq
	 */
	public String getUnitaMisuraAcq() {
		return unitaMisuraAcq;
	}

	/**
	 * @param unitaMisuraAcq the unitaMisuraAcq to set
	 */
	public void setUnitaMisuraAcq(String unitaMisuraAcq) {
		this.unitaMisuraAcq = unitaMisuraAcq;
	}

	/**
	 * @return the decUnitaMisuraAcq
	 */
	public Integer getDecUnitaMisuraAcq() {
		return decUnitaMisuraAcq;
	}

	/**
	 * @param decUnitaMisuraAcq the decUnitaMisuraAcq to set
	 */
	public void setDecUnitaMisuraAcq(Integer decUnitaMisuraAcq) {
		this.decUnitaMisuraAcq = decUnitaMisuraAcq;
	}

	/**
	 * @return the qtaUnitaMisuraAcq
	 */
	public Double getQtaUnitaMisuraAcq() {
		return qtaUnitaMisuraAcq;
	}

	/**
	 * @param qtaUnitaMisuraAcq the qtaUnitaMisuraAcq to set
	 */
	public void setQtaUnitaMisuraAcq(Double qtaUnitaMisuraAcq) {
		this.qtaUnitaMisuraAcq = qtaUnitaMisuraAcq;
	}

	/**
	 * @return the qtaMinPerUnitaMisuraPrz
	 */
	public Double getQtaMinPerUnitaMisuraPrz() {
		return qtaMinPerUnitaMisuraPrz;
	}

	/**
	 * @param qtaMinPerUnitaMisuraPrz the qtaMinPerUnitaMisuraPrz to set
	 */
	public void setQtaMinPerUnitaMisuraPrz(Double qtaMinPerUnitaMisuraPrz) {
		this.qtaMinPerUnitaMisuraPrz = qtaMinPerUnitaMisuraPrz;
	}

	/**
	 * @return the qtaMaxPerUnitaMisuraPrz
	 */
	public Double getQtaMaxPerUnitaMisuraPrz() {
		return qtaMaxPerUnitaMisuraPrz;
	}

	/**
	 * @param qtaMaxPerUnitaMisuraPrz the qtaMaxPerUnitaMisuraPrz to set
	 */
	public void setQtaMaxPerUnitaMisuraPrz(Double qtaMaxPerUnitaMisuraPrz) {
		this.qtaMaxPerUnitaMisuraPrz = qtaMaxPerUnitaMisuraPrz;
	}

	/**
	 * @return the tempoMaxConsegna
	 */
	public Integer getTempoMaxConsegna() {
		return tempoMaxConsegna;
	}

	/**
	 * @param tempoMaxConsegna the tempoMaxConsegna to set
	 */
	public void setTempoMaxConsegna(Integer tempoMaxConsegna) {
		this.tempoMaxConsegna = tempoMaxConsegna;
	}

	/**
	 * @return the unitaMisuraTempoConsegna
	 */
	public String getUnitaMisuraTempoConsegna() {
		return unitaMisuraTempoConsegna;
	}

	/**
	 * @param unitaMisuraTempoConsegna the unitaMisuraTempoConsegna to set
	 */
	public void setUnitaMisuraTempoConsegna(String unitaMisuraTempoConsegna) {
		this.unitaMisuraTempoConsegna = unitaMisuraTempoConsegna;
	}

	/**
	 * @return the articoloAcqVerde
	 */
	public String getArticoloAcqVerde() {
		return articoloAcqVerde;
	}

	/**
	 * @param articoloAcqVerde the articoloAcqVerde to set
	 */
	public void setArticoloAcqVerde(String articoloAcqVerde) {
		this.articoloAcqVerde = articoloAcqVerde;
	}

	/**
	 * @return the prodottoDaVerificare
	 */
	public String getProdottoDaVerificare() {
		return prodottoDaVerificare;
	}

	/**
	 * @param prodottoDaVerificare the prodottoDaVerificare to set
	 */
	public void setProdottoDaVerificare(String prodottoDaVerificare) {
		this.prodottoDaVerificare = prodottoDaVerificare;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return the tipoArticoloId
	 */
	public Long getTipoArticoloId() {
		return tipoArticoloId;
	}

	/**
	 * @param tipoArticoloId the tipoArticoloId to set
	 */
	public void setTipoArticoloId(Long tipoArticoloId) {
		this.tipoArticoloId = tipoArticoloId;
	}

	/**
	 * @return the statoArticoloArticoloId
	 */
	public Long getStatoArticoloId() {
		return statoArticoloId;
	}

	/**
	 * @param statoArticoloId the statoArticoloArticoloId to set
	 */
	public void setStatoArticoloId(Long statoArticoloId) {
		this.statoArticoloId = statoArticoloId;
	}

	/**
	 * @return the prezzoUnitarioRiferitoAId
	 */
	public Long getPrezzoUnitarioRiferitoAId() {
		return prezzoUnitarioRiferitoAId;
	}

	/**
	 * @param prezzoUnitarioRiferitoAId the prezzoUnitarioRiferitoAId to set
	 */
	public void setPrezzoUnitarioRiferitoAId(Long prezzoUnitarioRiferitoAId) {
		this.prezzoUnitarioRiferitoAId = prezzoUnitarioRiferitoAId;
	}

	/**
	 * @return the tempoConsegnaId
	 */
	public Long getTempoConsegnaId() {
		return tempoConsegnaId;
	}

	/**
	 * @param tempoConsegnaId the tempoConsegnaId to set
	 */
	public void setTempoConsegnaId(Long tempoConsegnaId) {
		this.tempoConsegnaId = tempoConsegnaId;
	}

	/**
	 * @return the unitaMisuraPrzId
	 */
	public Long getUnitaMisuraPrzId() {
		return unitaMisuraPrzId;
	}

	/**
	 * @param unitaMisuraPrzId the unitaMisuraPrzId to set
	 */
	public void setUnitaMisuraPrzId(Long unitaMisuraPrzId) {
		this.unitaMisuraPrzId = unitaMisuraPrzId;
	}

	/**
	 * @return the unitaMisuraAcqId
	 */
	public Long getUnitaMisuraAcqId() {
		return unitaMisuraAcqId;
	}

	/**
	 * @param unitaMisuraAcqId the unitaMisuraAcqId to set
	 */
	public void setUnitaMisuraAcqId(Long unitaMisuraAcqId) {
		this.unitaMisuraAcqId = unitaMisuraAcqId;
	}

	/**
	 * @return the numeroCategoria
	 */
	public Long getNumeroCategoria() {
		return numeroCategoria;
	}

	/**
	 * @param numeroCategoria the numeroCategoria to set
	 */
	public void setNumeroCategoria(Long numeroCategoria) {
		this.numeroCategoria = numeroCategoria;
	}

	/**
	 * @return the statoArticolo
	 */
	public String getStatoArticolo() {
		return statoArticolo;
	}

	/**
	 * @param statoArticolo the statoArticolo to set
	 */
	public void setStatoArticolo(String statoArticolo) {
		this.statoArticolo = statoArticolo;
	}
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author marco.perazzetta
 */
public class ExcelResultBean {

	private int numRigheTotaliAnalizzate;
	private Map<Integer, List<String>> righeConErrore;
	private int numRigheSuccesso;
	private int numRigheErrore;
	private int numeroRigheArticoliGiaInCatalogo;
	private StringBuilder codiciArticoliScartati;

	public ExcelResultBean() {

		numRigheTotaliAnalizzate = 0;
		numRigheSuccesso = 0;
		numRigheErrore = 0;
		numeroRigheArticoliGiaInCatalogo = 0;
		righeConErrore = new TreeMap<Integer, List<String>>();
		codiciArticoliScartati = new StringBuilder();
	}

	/**
	 * @return the numRigheTotaliAnalizzate
	 */
	public int getNumRigheTotaliAnalizzate() {
		return numRigheTotaliAnalizzate;
	}

	/**
	 * @param numRigheTotaliAnalizzate the numRigheTotaliAnalizzate to set
	 */
	public void setNumRigheTotaliAnalizzate(int numRigheTotaliAnalizzate) {
		this.numRigheTotaliAnalizzate = numRigheTotaliAnalizzate;
	}

	/**
	 * @return the righeConErrore
	 */
	public Map<Integer, List<String>> getRigheConErrore() {
		return righeConErrore;
	}

	/**
	 * @param righeConErrore the righeConErrore to set
	 */
	public void setRigheConErrore(Map<Integer, List<String>> righeConErrore) {
		this.righeConErrore = righeConErrore;
	}

	/**
	 * @return the numRigheSuccesso
	 */
	public int getNumRigheSuccesso() {
		return numRigheSuccesso;
	}

	/**
	 * @param numRigheSuccesso the numRigheSuccesso to set
	 */
	public void setNumRigheSuccesso(int numRigheSuccesso) {
		this.numRigheSuccesso = numRigheSuccesso;
	}

	/**
	 * @return the numRigheErrore
	 */
	public int getNumRigheErrore() {
		return numRigheErrore;
	}

	/**
	 * @param numRigheErrore the numRigheErrore to set
	 */
	public void setNumRigheErrore(int numRigheErrore) {
		this.numRigheErrore = numRigheErrore;
	}

	/**
	 * @return the numeroRigheArticoliGiaInCatalogo
	 */
	public int getNumeroRigheArticoliGiaInCatalogo() {
		return numeroRigheArticoliGiaInCatalogo;
	}

	/**
	 * @param numeroRigheArticoliGiaInCatalogo the
	 * numeroRigheArticoliGiaInCatalogo to set
	 */
	public void setNumeroRigheArticoliGiaInCatalogo(int numeroRigheArticoliGiaInCatalogo) {
		this.numeroRigheArticoliGiaInCatalogo = numeroRigheArticoliGiaInCatalogo;
	}

	/**
	 * @return the codiciArticoliScartati
	 */
	public StringBuilder getCodiciArticoliScartati() {
		return codiciArticoliScartati;
	}

	/**
	 * @param codiciArticoliScartati the codiciArticoliScartati to set
	 */
	public void setCodiciArticoliScartati(StringBuilder codiciArticoliScartati) {
		this.codiciArticoliScartati = codiciArticoliScartati;
	}
}

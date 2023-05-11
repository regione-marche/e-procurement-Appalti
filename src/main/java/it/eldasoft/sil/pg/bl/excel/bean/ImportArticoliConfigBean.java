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
public class ImportArticoliConfigBean extends ExcelConfigBean {

	private String codiceCatalogo;

	public ImportArticoliConfigBean() {
	}

	public String getCodiceCatalogo() {
		return codiceCatalogo;
	}

	public void setCodiceCatalogo(String codiceCatalogo) {
		this.codiceCatalogo = codiceCatalogo;
	}
}

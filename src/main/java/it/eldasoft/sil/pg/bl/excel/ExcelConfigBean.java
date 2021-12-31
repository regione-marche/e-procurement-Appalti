/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel;

import org.apache.struts.upload.FormFile;

/**
 *
 * @author marco.perazzetta
 */
public class ExcelConfigBean {

	private FormFile file;

	public ExcelConfigBean() {
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}
}

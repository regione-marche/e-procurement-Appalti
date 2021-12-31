/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl.excel.bean;

import it.eldasoft.sil.pg.bl.excel.ExcelResultBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marco.perazzetta
 */
public class ImportAdempimenti190ResultBean extends ExcelResultBean {

	private Map<String, List<ImportAdempimenti190Bean>> lottiConSuccesso;

	public ImportAdempimenti190ResultBean() {

		super();
		lottiConSuccesso = new HashMap<String, List<ImportAdempimenti190Bean>>();
	}

	/**
	 * @return the lottiConSuccesso
	 */
	public Map<String, List<ImportAdempimenti190Bean>> getLottiConSuccesso() {
		return lottiConSuccesso;
	}

	/**
	 * @param lottiConSuccesso the lottiConSuccesso to set
	 */
	public void setLottiConSuccesso(Map<String, List<ImportAdempimenti190Bean>> lottiConSuccesso) {
		this.lottiConSuccesso = lottiConSuccesso;
	}
}

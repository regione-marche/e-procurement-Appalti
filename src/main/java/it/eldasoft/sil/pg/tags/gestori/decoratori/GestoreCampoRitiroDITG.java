/*
 * Created on 05/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Gestore per i campi RITDOM, RITOFF e RITOFF di ditg
 * 
 * @author Marcello Caminiti
 */
public class GestoreCampoRitiroDITG extends AbstractGestoreCampo {

	public String gestisciDaTrova(Vector params, DataColumn colWithValue,
			String conf, SqlManager manager) {
		return null;
	}

	public String getClasseEdit() {
		return null;
	}

	public String getClasseVisua() {
		return null;
	}

	public String getHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	public String getValore(String valore) {
		return null;
	}

	public String getValorePerVisualizzazione(String valore) {
		String desc = null;
		
		// Se il campo non è valorizzato allora associo
		// la descrizione che prelevo da TAB1
		// con TAB1COD='A1045' e TAB1TIP=0
		if (valore == null || "".equals(valore)) {
			SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
					this.getPageContext(), SqlManager.class);
			String tab1cod = "A1045";
			Integer tab1tip = new Integer(0);
			try {
				desc = (String) sql
						.getObject(
								"select TAB1DESC from TAB1 where TAB1COD = ? and TAB1TIP = ?",
								new Object[] { tab1cod, tab1tip });
			} catch (SQLException e) {

			}
		}
		
		return desc;
	}

	public String getValorePreUpdateDB(String valore) {
		return null;
	}

	protected void initGestore() {

	}

	public String postHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	public String preHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

}

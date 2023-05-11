/*
 * Created on 17/05/21
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
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

/**
 * Gestore del campo fittizzio N_DOC_MOD della pagina lista documentazione di contratto,
 * per tale campo si deve recuperare il n di documenti collegati al contratto 
 * 
 * @author Peruzzo Riccardo
 */
public class GestoreCampoNumeroDocumentiPerModello extends AbstractGestoreCampo {

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

	

	public String getValore(String valore) {
		return null;
	}

	/**
	 * Nel campo valore viene passato un valore booleano che indica se effettuare il calcolo
	 * per popolare il campo 
	 */
	public String getValorePerVisualizzazione(String valore) {
		
		SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
				this.getPageContext(), SqlManager.class);
		HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
		        PageContext.REQUEST_SCOPE);
		
		String iddocumod = datiRiga.get("G1DOCUMOD_ID").toString();
		
		String valoreCampo="";
		
        String select="select count(*) from g1arcdocumod where iddocumod = ?";
			try {
				Long numDocumenti = (Long)sql.getObject(select, new Object[]{iddocumod});
				
				if (numDocumenti != null)
				  valoreCampo = numDocumenti.toString();
				else
				  valoreCampo = "0";
			    
			    
			} catch (SQLException e) {

			} 
		
		return valoreCampo;
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

  
  public String getHTML(boolean visualizzazione, boolean abilitato) {
    
    return null;
  }

}
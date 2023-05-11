/*
 * Created on 02/02/12
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

import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

/**
 * Gestore del campo fittizzio NUM_INVITI della pagina delle fasi di iscrizione,
 * per tale campo si deve calcolare il numero totale di inviti per quell'operatore 
 * per tutte le categorie per quell'operatore
 * 
 * @author Marcello Caminiti
 */
public class GestoreCampoCodiceGara extends AbstractGestoreCampo {

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
		
		HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
		        PageContext.REQUEST_SCOPE);
		
		String ret;
		String ngara = datiRiga.get("GARE_NGARA").toString();
		String codgar1 = datiRiga.get("GARE_CODGAR1").toString();
		
		ret = ngara;
		if(!ngara.equals(codgar1)&& codgar1.charAt(0)!='$'){
		  ret = codgar1;
		}
		
		
		return ret;
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
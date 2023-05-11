/*
 * Created on 13/gen/10
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Gestore del campo fittizzio DGUE_ELABORAZIONI della pagina delle fasi di gara,
 * per tale campo si deve calcolare il numero di lotti in cui PUNTEC è valorizzato
 * rispetto al numero totale di lotti. Inoltre viene impostato lo span del campo
 * per il messaggio del tooltip
 *
 * @author Manuel Bridda
 */
public class GestoreCampiDgue extends AbstractGestoreCampo {

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
	  
	  String ret = "";
	  String valore = this.campo.getValue();
      if(!"".equals(valore)){
          StringBuffer buf = new StringBuffer("");
          buf.append("<span class='rowValue' ");
          valore = valore.replace("#!#", "<br>");
          buf.append(UtilityTags.getHtmlAttrib("title",valore));
          buf.append(">");
          // Appendo il valore per la visualizzazione
          buf.append(valore);
          buf.append("</span>");
          ret = buf.toString();
        }
       return ret;
	}

	public String getValore(String valore) {
		return null;
	}

	/**
	 * Nel campo valore viene passato un valore booleano che indica se effettuare il calcolo
	 * per popolare il campo
	 */
	public String getValorePerVisualizzazione(String valore) {

		return null;
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
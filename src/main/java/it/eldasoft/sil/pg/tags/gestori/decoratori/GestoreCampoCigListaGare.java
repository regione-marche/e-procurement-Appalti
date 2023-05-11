/*
 * Created on 13/04/17
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

import java.util.Vector;
/**
 * Gestore del campo V_GARE_TORN.CODCIG, in cui se il valore del campo contiente un elenco di CIG(valori separati da ",") viene
 * impostato lo span del campo formattandolo con tag HTML per il messaggio del tooltip
 *
 * @author Marcello Caminiti
 */

public class GestoreCampoCigListaGare extends AbstractGestoreCampo {

	@Override
  public String gestisciDaTrova(Vector params, DataColumn colWithValue,
			String conf, SqlManager manager) {
		return null;
	}

	@Override
  public String getClasseEdit() {
		return null;
	}

	@Override
  public String getClasseVisua() {
		return null;
	}

	@Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {

	  String ret=null;
	  if(campo.getValue()!=null && campo.getValue().indexOf(",")>0){
    	  StringBuffer buf = new StringBuffer("");
          buf.append("<span class='tooltipCig' ");
          String listaCig = campo.getValue().replace(",", ", ");
          buf.append(UtilityTags.getHtmlAttrib("title", "Codice identificativo della gara (CIG):<br><b>" + listaCig + "</b>"));
          buf.append(">");
          // Appendo il valore per la visualizzazione
          buf.append("[...]");
          buf.append("</span>");
          ret = buf.toString();
    	}

		return ret;
	}

	@Override
  public String getValore(String valore) {
	  return null;
	}

	@Override
  public String getValorePerVisualizzazione(String valore) {
	  return null;
	}

	@Override
  public String getValorePreUpdateDB(String valore) {
		return null;
	}

	@Override
  protected void initGestore() {

	}

	@Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	@Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

		return null;
	}

}

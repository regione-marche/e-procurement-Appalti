/*
 * Created on 09/10/23
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
 * Gestore del campo V_W_INVCOM_OUT.DESINTEST, in cui se il valore del campo contiente un elenco di destinatari(valori separati da ",") viene
 * impostato lo span del campo formattandolo con tag HTML per il messaggio del tooltip
 *
 * @author Manuel Bridda
 */

public class GestoreCampoDesintestListaComunicazioni extends AbstractGestoreCampo {

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
          buf.append("<span class='tooltipDesintest' ");
          String listaDes = campo.getValue().replace(",", ",<br>");
          buf.append(UtilityTags.getHtmlAttrib("title", "Destinatari:<br><b>" + listaDes + "</b>"));
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

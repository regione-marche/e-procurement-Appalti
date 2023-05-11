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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.utils.spring.UtilitySpring;
/**
 * Gestore del campo fittizzio INFO_OFFEC della pagina delle fasi di gara,
 * per tale campo si deve calcolare il numero di lotti in cui IMPOFF è valorizzato
 * rispetto al numero totale di lotti. Inoltre viene impostato lo span del campo
 * per il messaggio del tooltip
 *
 * @author Marcello Caminiti
 */

public class GestoreCampoRegolaValutazione extends AbstractGestoreCampo {

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
		/*
	  StringBuffer buf = new StringBuffer("");
		buf.append("<span ");
		buf.append(UtilityTags.getHtmlAttrib("title", "Lotti con importo offerto rispetto ai lotti per cui partecipa la ditta"));
		buf.append(">");
		// Appendo il valore per la visualizzazione
	      buf.append(campo.getValuePerVisualizzazione());
	      buf.append("</span>");
	      return buf.toString();
		//return null;
		 */
	  return null;
	}

	@Override
  public String getValore(String valore) {
		return null;
	}

	/**
	 * Nel campo valore viene passato un valore booleano che indica se effettuare il calcolo
	 * per popolare il campo
	 */
	@Override
  public String getValorePerVisualizzazione(String valore) {

		SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
				this.getPageContext(), SqlManager.class);
		HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
		        PageContext.REQUEST_SCOPE);

		String modpunti = (String)datiRiga.get("G1CRIDEF_MODPUNTI");
		String tab1cod=null;
		Long tab1tip=null;
		String valoreCampo="";
		String select="select tab1desc from tab1 where tab1cod=? and tab1tip=? ";
		boolean eseguiSelect= false;
		if("1".equals(modpunti) || "3".equals(modpunti)){
		  eseguiSelect=true;
		  tab1cod="A1142";
		  if(datiRiga.get("G1CRIDEF_MODMANU")!=null)
		    tab1tip= new Long ((String)datiRiga.get("G1CRIDEF_MODMANU"));
		}else if("2".equals(modpunti)){
		  eseguiSelect=true;
		  tab1cod="A1147"; //Ancora da stabilire
		  if(datiRiga.get("G1CRIDEF_FORMULA")!=null && !datiRiga.get("G1CRIDEF_FORMULA").equals(""))
            tab1tip= new Long ((String)datiRiga.get("G1CRIDEF_FORMULA"));
		}

		try {
		  if(eseguiSelect)
		    valoreCampo = (String)sql.getObject(select, new Object[]{tab1cod,tab1tip});

		} catch (SQLException e) {
		}

		return valoreCampo;
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

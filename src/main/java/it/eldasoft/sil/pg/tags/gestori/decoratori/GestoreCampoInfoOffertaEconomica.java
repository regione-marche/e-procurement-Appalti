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
/**
 * Gestore del campo fittizzio INFO_OFFEC della pagina delle fasi di gara,
 * per tale campo si deve calcolare il numero di lotti in cui IMPOFF è valorizzato
 * rispetto al numero totale di lotti. Inoltre viene impostato lo span del campo
 * per il messaggio del tooltip
 *
 * @author Marcello Caminiti
 */

public class GestoreCampoInfoOffertaEconomica extends AbstractGestoreCampo {

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
		StringBuffer buf = new StringBuffer("");
		buf.append("<span ");
		buf.append(UtilityTags.getHtmlAttrib("title", "Lotti con importo offerto rispetto ai lotti per cui partecipa la ditta"));
		buf.append(">");
		// Appendo il valore per la visualizzazione
	      buf.append(campo.getValuePerVisualizzazione());
	      buf.append("</span>");
	      return buf.toString();
		//return null;
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

		String ngara = datiRiga.get("DITG_NGARA5").toString();
		String codgar = datiRiga.get("DITG_CODGAR5").toString();
		String dittao = datiRiga.get("DITG_DITTAO").toString();

		String valoreCampo="";
		String select="select COALESCE(PUNECO,IMPOFF, RIBAUO), NGARA5 from DITG where CODGAR5 = ? and NGARA5 <> ? and DITTAO = ? "
			 +"and (DITG.FASGAR >= 6 or DITG.FASGAR = 0 or DITG.FASGAR is null) and (DITG.PARTGAR = '1' or DITG.PARTGAR is null) ";

		try {
			List listaImpoff = sql.getListVector(select, new Object[]{codgar,ngara,dittao});
			if (listaImpoff != null && listaImpoff.size() > 0) {
				int numLotti=0;
				int numLottiConImpoff=0;
				Double puntec = null;
				String ngaraLotto=null;
				String costofisso = null;
				for (int i = 0; i < listaImpoff.size(); i++) {
					puntec = SqlManager.getValueFromVectorParam(
							listaImpoff.get(i), 0).doubleValue();
					ngaraLotto = SqlManager.getValueFromVectorParam(
                        listaImpoff.get(i), 1).stringValue();
					//Vanno esclusi i lotti con costofisso=1
					costofisso=(String)sql.getObject("select costofisso from gare1 where ngara=?", new Object[]{ngaraLotto});
					if(costofisso==null || "".equals(costofisso)){
    					numLotti++;
    					if (puntec != null )
    						numLottiConImpoff++;
					}
				}
				valoreCampo= Integer.toString(numLottiConImpoff) + "\\" + Integer.toString(numLotti);
			}
		} catch (SQLException e) {
		} catch (GestoreException e) {
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

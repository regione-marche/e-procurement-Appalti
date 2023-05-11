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
 * Gestore del campo fittizzio INFO_PUNTEC della pagina delle fasi di gara,
 * per tale campo si deve calcolare il numero di lotti in cui PUNTEC è valorizzato
 * rispetto al numero totale di lotti. Inoltre viene impostato lo span del campo
 * per il messaggio del tooltip
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoInfoPunteggioTecnico extends AbstractGestoreCampo {

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
		StringBuffer buf = new StringBuffer("");
		buf.append("<span ");
		buf.append(UtilityTags.getHtmlAttrib("title",
				"Lotti con punteggio tecnico rispetto ai lotti per cui partecipa la ditta"));
		buf.append(">");
		// Appendo il valore per la visualizzazione
		buf.append(campo.getValuePerVisualizzazione());
		buf.append("</span>");

		return buf.toString();
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

		String ngara = datiRiga.get("DITG_NGARA5").toString();
		String codgar = datiRiga.get("DITG_CODGAR5").toString();
		String dittao = datiRiga.get("DITG_DITTAO").toString();

		String valoreCampo="";


			String select="select PUNTEC from DITG where CODGAR5 = ? and NGARA5 <> ? "
				+ "and DITTAO = ? and (DITG.FASGAR >= 5 or DITG.FASGAR = 0 or "
				+ "DITG.FASGAR is null) and (DITG.PARTGAR = '1' or DITG.PARTGAR is null) "
				+ "and NGARA5 in (select NGARA from GARE "
				+ "where NGARA <> ? and CODGAR1 = ? and GENERE is null and MODLICG = 6)";

			try {
				List listaPunteggiTec = sql.getListVector(select, new Object[]{codgar,
						ngara,dittao,ngara,codgar});
				if (listaPunteggiTec != null && listaPunteggiTec.size() > 0) {
					int numLotti=0;
					int numLottiConPuntec=0;
					for (int i = 0; i < listaPunteggiTec.size(); i++) {
						Double puntec = SqlManager.getValueFromVectorParam(
								listaPunteggiTec.get(i), 0).doubleValue();
						numLotti++;
						if (puntec != null && puntec.doubleValue()>=0)
							numLottiConPuntec++;
					}
					valoreCampo= Integer.toString(numLottiConPuntec) + "\\" +	numLotti;
				}
			} catch (SQLException e) {

			} catch (GestoreException e) {

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

}
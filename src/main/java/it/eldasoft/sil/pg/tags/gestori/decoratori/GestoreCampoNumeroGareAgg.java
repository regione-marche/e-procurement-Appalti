/*
 * Created on 04/11/10
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
 * Gestore del campo fittizzio numero gare aggiudicate della pagina delle fasi di iscrizione,
 * per tale campo si deve calcolare il numero totale di gare associate all'elenco che
 * risultano aggiucate, con impresa aggiudicataria uguale all'impresa corrente
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoNumeroGareAgg extends AbstractGestoreCampo {

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
  public String getValore(String valore) {
		return null;
	}

	@Override
  public String getValorePerVisualizzazione(String valore) {

		SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
				this.getPageContext(), SqlManager.class);
		HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
		        PageContext.REQUEST_SCOPE);

		String ngara = datiRiga.get("DITG_NGARA5").toString();
		String dittao = datiRiga.get("DITG_DITTAO").toString();

		String valoreCampo="";

		String select="select sum(aggrea) from iscrizcat where ngara=? and codimp=?";
			try {
				Long numGareAggiudicate = (Long)sql.getObject(select, new Object[]{
				    ngara,dittao});

				if (numGareAggiudicate != null)
				  valoreCampo = numGareAggiudicate.toString();
				else
				  valoreCampo = "0";


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


  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

}
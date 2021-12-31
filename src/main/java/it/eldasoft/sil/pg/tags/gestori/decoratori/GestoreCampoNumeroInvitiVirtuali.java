/*
 * Created on 06/10/10
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
 * Gestore del campo fittizzio NUM_INVITI_VIRT della pagina delle fasi di iscrizione,
 * per tale campo si deve calcolare il numero totale di inviti virtuali per quell'operatore
 * per tutte le categorie per quell'operatore
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoNumeroInvitiVirtuali extends AbstractGestoreCampo {

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
		String dittao = datiRiga.get("DITG_DITTAO").toString();

		String valoreCampo="";

        String select="select sum(invpen) from iscrizcat where iscrizcat.codimp = ? and ngara = ?";
			try {
				Long numInviti = (Long)sql.getObject(select, new Object[]{dittao,ngara});

				if (numInviti != null)
				  valoreCampo = numInviti.toString();
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
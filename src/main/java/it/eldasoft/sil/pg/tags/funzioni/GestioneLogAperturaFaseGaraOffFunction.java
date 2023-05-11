/*
 * Created on 04-giu-2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.spring.UtilitySpring;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per la gestione dei log in apertura delle pagine di apertura offerte
 * 
 *
 * @author Riccardo.Peruzzo
 */
public class GestioneLogAperturaFaseGaraOffFunction extends AbstractFunzioneTag {

	public GestioneLogAperturaFaseGaraOffFunction() {
		super(3, new Class[] { PageContext.class, String.class, Integer.class });
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		if (pageContext.getRequest().getParameter("logAccessoFasiGara") != null) {
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext,
				TabellatiManager.class);

		Integer fase = (Integer) params[2];
		String descr = "";
		String entita = "";
		String key = (String) params[1];
		String key_val = key.substring(key.indexOf(":") + 1);
		String codiceGara = key_val;
		Long bustalotti = 0L;

		try {
			if (key.indexOf("GARE") >= 0) {
				entita = "GARE";

			} else {
				entita = "TORN";
				bustalotti = (Long) sqlManager.getObject("select BUSTALOTTI from gare where ngara = ? ",
						new Object[] { key_val });
			}

		} catch (SQLException e) {
			throw new JspException("Errore durante la lettura del codice bustalotti", e);
		}
		
		descr += "Accesso alla pagina '";
		if (bustalotti==2) {
			descr += "Apertura doc.ammin. e offerte";
		}else {
			descr += "Apertura offerte e calcolo aggiudicazione";
		}
		descr += "'";

		if (!"".equals(fase) && fase != null) {
			String descrfase = tabellatiManager.getDescrTabellato("A1012", fase.toString());
			descr += " fase '" + descrfase + "'";
		}

		try {
			LogEvento logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) pageContext.getRequest());
			logEvento.setLivEvento(1);
			logEvento.setOggEvento(codiceGara);
			logEvento.setCodEvento("GA_ACCESSO_PAGINAOFFERTE");
			logEvento.setDescr(descr);
			logEvento.setErrmsg("");
			LogEventiUtils.insertLogEventi(logEvento);
		} catch (Exception le) {
			String messageKey = "errors.logEventi.inaspettataException";
		}
		}
		
		return null;
	}
}
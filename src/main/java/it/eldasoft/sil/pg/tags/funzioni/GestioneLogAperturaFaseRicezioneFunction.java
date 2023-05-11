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
 * Funzione per la gestione dei log in apertura delle pagine ricezione offerte
 * 
 *
 * @author Riccardo.Peruzzo
 */
public class GestioneLogAperturaFaseRicezioneFunction extends AbstractFunzioneTag {

	public GestioneLogAperturaFaseRicezioneFunction() {
		super(3, new Class[] { PageContext.class, String.class, Integer.class });
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		if (pageContext.getRequest().getParameter("logAccessoFasiRic") != null) { 
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext,
				TabellatiManager.class);

		Long iterga;
		Integer fase = (Integer) params[2];
		String descr = "Accesso alla pagina '";
		String entita = "";
		String key = (String) params[1];
		String key_val = key.substring(key.indexOf(":") + 1);
		String key_iterga = "";
		String codiceGara = key_val;

		try {
			if (key.indexOf("GARE") >= 0) {
				entita = "GARE";
				key_iterga = (String) sqlManager.getObject("select CODGAR1 from gare where ngara = ? ",
							new Object[] { key_val });

			} else {
				entita = "TORN";
				key_iterga = key_val;
			}

			iterga = (Long) sqlManager.getObject("select ITERGA from torn where codgar = ? ",
					new Object[] { key_iterga });
		} catch (SQLException e) {
			throw new JspException("Errore durante la lettura del codice iterga", e);
		}

		switch (iterga.intValue()) {
		case 1:
			descr += "Ricezione offerte";
			break;
        case 3:
        case 5:
        case 6:
        case 8:
            descr += "Inviti e ricezione offerte";
            break;
        default:
            descr += "Ricezione domande e offerte";
            break;
		};

		descr += "'";
		if (!"".equals(fase) && fase != null) {
		  if(fase==20){
            descr += " fase 'Chiusura ricezione plichi'";
		  }else{
			String descrfase = tabellatiManager.getDescrTabellato("A1012", fase.toString());
			descr += " fase '" + descrfase + "'";
          }
		}

		try {
			LogEvento logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) pageContext.getRequest());
			logEvento.setLivEvento(1);
			logEvento.setOggEvento(codiceGara);
			logEvento.setCodEvento("GA_ACCESSO_PAGINARICEZIONE");
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
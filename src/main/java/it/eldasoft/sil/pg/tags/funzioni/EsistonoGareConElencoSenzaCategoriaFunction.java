/*
 * Created on 04/11/11
 * (S.Santi)
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;



import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class EsistonoGareConElencoSenzaCategoriaFunction extends AbstractFunzioneTag {

	public EsistonoGareConElencoSenzaCategoriaFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}

	public String function(PageContext pageContext, Object[] params)
			throws JspException {

	    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	            pageContext, SqlManager.class);
		
		String codiceElenco = new String((String) params[1]);
		String esito = "FALSE";
		
		try {
			Long conteggio = (Long) sqlManager.getObject(
			        "select count(*) from gare left outer join catg on (gare.ngara=catg.ngara and catg.ncatg=1 ) " +
					"where elencoe=? and catg.catiga is null",
					new Object[] { codiceElenco });
			if (conteggio != null && conteggio.longValue() > 0) esito = "TRUE";		
		} catch (SQLException e) {
			throw new JspException("Errore nel conteggio delle gare collegate a elenco senza categoria prevalente",e);
		}
	    
	    return esito;

	}

}

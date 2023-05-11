/*
 * Created on 19/04/10
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

public class ConteggioComponentiCommissioneFunction extends AbstractFunzioneTag {

	public ConteggioComponentiCommissioneFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}

	public String function(PageContext pageContext, Object[] params)
			throws JspException {

	    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	            pageContext, SqlManager.class);
		
		String ngara = new String((String) params[1]);
		String esistonoComponenti = "FALSE";
		
		try {
			Long conteggio = (Long) sqlManager.getObject("select count(*) from gfof where ngara2 = ?",new Object[] { ngara });
			if (conteggio != null && conteggio.longValue() > 0) esistonoComponenti = "TRUE";		
		} catch (SQLException e) {
			throw new JspException("Errore nel conteggio dei componenti della commissione",e);
		}
	    
	    return esistonoComponenti;

	}

}

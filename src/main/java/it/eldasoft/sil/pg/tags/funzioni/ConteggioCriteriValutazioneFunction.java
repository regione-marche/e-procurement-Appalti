/*
 * Created on 28/04/11
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

public class ConteggioCriteriValutazioneFunction extends AbstractFunzioneTag {

	public ConteggioCriteriValutazioneFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}

	public String function(PageContext pageContext, Object[] params)
			throws JspException {

	    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	            pageContext, SqlManager.class);
		
		String ngara = new String((String) params[1]);
		String esistonoCriteri = "FALSE";
		
		try {
			Long conteggio = (Long) sqlManager.getObject("select count(*) from goev where ngara = ?",new Object[] { ngara });
			if (conteggio != null && conteggio.longValue() > 0) esistonoCriteri = "TRUE";		
		} catch (SQLException e) {
			throw new JspException("Errore nel conteggio dei criteri di valutazione",e);
		}
	    
	    return esistonoCriteri;

	}

}

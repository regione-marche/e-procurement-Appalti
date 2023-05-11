/*
 * Created on 11/07/18
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ConteggioAttributiAggiuntiviFunction extends AbstractFunzioneTag {

	public ConteggioAttributiAggiuntiviFunction() {
		super(3, new Class[] { PageContext.class, String.class,String.class });
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

	    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	            pageContext, SqlManager.class);

		String ngara = new String((String) params[1]);
		String entita= new String((String) params[2]);
		String esistonoAttributi = "FALSE";

		try {
			Long conteggio = (Long) sqlManager.getObject("select count(id) from garconfdati where ngara=? and entita=?",new Object[] { ngara, entita });
			if (conteggio != null && conteggio.longValue() > 0) esistonoAttributi = "TRUE";
		} catch (SQLException e) {
			throw new JspException("Errore nel conteggio degli attributi aggiuntivi",e);
		}

	    return esistonoAttributi;

	}

}

/*
 * Created on 10-06-2016
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

/**
 * Viene letto il campo TORN.MODCONT a partire dal codgar
 *
 *
 * @author Marcello Caminiti
 */
public class GetModcontFunction extends AbstractFunzioneTag {

	public GetModcontFunction() {
		super(2, new Class[] { PageContext.class, String.class});
	}


	@Override
    public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		String select = "";
		String codice=(String)params[1];

		try {
			select = "select modcont from torn where codgar = ?";
			Long modcont=(Long)sqlManager.getObject(select, new Object[]{codice});
			if(modcont != null) {
	            result = String.valueOf(modcont);
	        }
		} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura della tabella TORN ", e);
		}
		return result;
	}
}

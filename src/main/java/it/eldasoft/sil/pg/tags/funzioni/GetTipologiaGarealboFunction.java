/*
 * Created on 29-09-2014
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che estrae il campo TIPOLOGIA di GAREALBO.
 *
 * @author Marcello Caminiti
 */
public class GetTipologiaGarealboFunction extends AbstractFunzioneTag {

	public GetTipologiaGarealboFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}


	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		String codice ="";
		String chiave = (String) GeneralTagsFunction.cast("string", params[1]);
		codice = chiave.substring(chiave.indexOf(":") + 1);

		try {
			Long tipologia = (Long) sqlManager.getObject(
					"select tipologia from garealbo where ngara=?", new Object[]{codice});

			if(tipologia!=null)
			  result = String.valueOf(tipologia);
		} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura della tipologia di GAREALBO ", e);
		}

		return result;
	}

}
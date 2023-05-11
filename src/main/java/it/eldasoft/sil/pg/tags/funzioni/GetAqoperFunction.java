/*
 * Created on 30-09-2016
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
 * Viene letto il campo TORN.AQOPER a partire dal codgar
 * Come parametri vengono passati la chiave e l'entità
 *
 *
 * @author Marcello Caminiti
 */
public class GetAqoperFunction extends AbstractFunzioneTag {

	public GetAqoperFunction() {
		super(3, new Class[] { PageContext.class, String.class, String.class});
	}


	@Override
    public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		String select = "";
		String codice=(String)params[1];
		String entita=(String)params[2];

		try {
			select = "select aqoper from gare1 where ngara = ?";
			if("TORN".equals(entita))
			  select = "select aqoper from torn where codgar = ?";
			Long aqoper=(Long)sqlManager.getObject(select, new Object[]{codice});
			if(aqoper != null) {
	            result = String.valueOf(aqoper);
	        }
		} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura del campo AQOPER dall'entita " + entita, e);
		}
		return result;
	}
}

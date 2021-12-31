/*
 * Created on 11-02-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae la descrizione del tabellato associato al campo TIPGEN di TORN.
 *
 *
 * @author Marcello Caminiti
 */
public class GetDescrizioneTipgenFunction extends AbstractFunzioneTag {

	public GetDescrizioneTipgenFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}


	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
				"tabellatiManager", pageContext, TabellatiManager.class);

		String result = "";
		String tipgen=(String)params[1];

		result = tabellatiManager.getDescrTabellato("A1007", tipgen);

		return result;
	}
}

/*
 * Created on 13-06-2023
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

import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Viene prelevato dal tabellato A1017/A1020/A1021 il valore per l'inizializzione del PGAROFF
 *
 *
 * @author Manuel Bridda
 */
public class GetPGARAOFFunction extends AbstractFunzioneTag {

	public GetPGARAOFFunction() {
		super(2, new Class[] { PageContext.class, String.class, String.class});
	}


	@Override
    public String function(PageContext pageContext, Object[] params)
			throws JspException {

		PgManager pgManager = (PgManager) UtilitySpring.getBean(
				"pgManager", pageContext, PgManager.class);
		
		Logger logger = Logger.getLogger(GetPGARAOFFunction.class);

		String result = "";
		String tipgen=(String)params[1];

		try {
			Double pgaroff = pgManager.initPGAROF(Long.parseLong(tipgen));
			result = UtilityNumeri.convertiImporto(pgaroff,2).replace(".","").replace(",",".");
		} catch (SQLException e) {
			logger.error("Errore durante la lettura della percentuale di garanzia indicata nel tabellato per il tipgen "+tipgen, e);
		}
		return result;
	}
}

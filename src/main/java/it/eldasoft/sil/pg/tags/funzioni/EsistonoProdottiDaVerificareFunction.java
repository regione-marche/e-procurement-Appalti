/*
 * Created on 7-04-2014
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
 * Funzione che effettua conteggio del numero di prodotti da verificare per un catalogo e per
 * un operatore.
 *
 *
 * @author Marcello Caminiti
 */
public class EsistonoProdottiDaVerificareFunction extends AbstractFunzioneTag {

	public EsistonoProdottiDaVerificareFunction() {
		super(4, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
	}


	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		String codgar=(String) GeneralTagsFunction.cast("string", params[1]);
		String ngara=(String) GeneralTagsFunction.cast("string", params[2]);
		String dittao=(String) GeneralTagsFunction.cast("string", params[3]);

		try {
			Long numProdotti = (Long) sqlManager.getObject(
					"select count(id) from meiscrizprod where codgar=? and ngara=? and codimp=? and stato=?",
					new Object[]{codgar, ngara,dittao, new Long(2)});
			if (numProdotti!=null)
			  result = numProdotti.toString();

		} catch (SQLException e) {
			throw new JspException(
					"Errore durante il conteggio dei prodotti da verificare per il catalogo:" + ngara + " e operatore:" + dittao, e);
		}

		return result;
	}

}
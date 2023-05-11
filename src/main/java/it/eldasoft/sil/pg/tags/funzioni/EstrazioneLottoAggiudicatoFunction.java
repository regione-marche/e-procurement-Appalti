/*
 * Created on 02-dic-2009
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che determina il numero gara di un lotto che risulta. 
 * aggiudicato dalla ditta passata come parametro.
 * Vi possono essere più lotti aggiudicati dalla ditta, ne viene
 * selezionato arbitrariamente uno
 * 
 * @author Marcello Caminiti
 */
public class EstrazioneLottoAggiudicatoFunction extends AbstractFunzioneTag {

	public EstrazioneLottoAggiudicatoFunction() {
		super(3, new Class[] { PageContext.class, String.class, String.class });
	}
	
	
	public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		String codiceGara = (String) GeneralTagsFunction.cast("string", params[1]);
		String select = "";
		String codiceDItta=(String) GeneralTagsFunction.cast("string", params[2]);
		
		try {
			select = "select ngara from gare where codgar1= ? and ditta = ? and genere is null order by ngara";
			
			List datiGARE = sqlManager.getListVector(select, new Object[] { codiceGara,codiceDItta });
			
			if (datiGARE != null && datiGARE.size() > 0) {
				String ngaraLotto = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 0).stringValue();
				result= ngaraLotto;
			}
		} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura del numero di gara del lotto", e);
		} catch (GestoreException e) {
			throw new JspException(
					"Errore durante la lettura del numero di gara del lotto", e);
		}
		
		
		return result;
	}
}

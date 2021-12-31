/*
 * Created on 8-ott-2009
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
 * Funzione che estrae il campo TIPFORN di TORN. 
 *  
 * 
 * @author Marcello Caminiti
 */
public class GetTipoFornitureFunction extends AbstractFunzioneTag {

	public GetTipoFornitureFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}
	
	
	public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		String select = "";
		String codgar=(String)params[1];
		
				
		try {
			/*
		    select = "select codgar1 from gare where ngara = ?";
				
			codgar = (String) sqlManager.getObject(
						select, new Object[] { ngara });
			
			*/		
			select = "select tipforn from torn where codgar = ?";
			
			Long tipforn = (Long) sqlManager.getObject(
					select, new Object[] { codgar });
			
			if (tipforn!= null)
	    	  result = String.valueOf(tipforn);
	    	
				} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura della tipo fornitura della gara ", e);
		}
		
		return result;
	}
}

/*
 * Created on 14-11-2011
 *
 /*
 * Created on 17-apr-2009
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
 * Funzione che estrae il campo TIPOALGO di GAREALBO a partire da NGARA. 
 * 
 * @author Marcello caminiti
 */
public class GetTIPOALGOFunction extends AbstractFunzioneTag {

  public GetTIPOALGOFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String result="";
    String codice = (String) GeneralTagsFunction.cast("string", params[1]);
    
      codice = codice.substring(codice.indexOf(":") + 1);
      try {
    	Long tipoalgo = (Long) sqlManager.getObject(
            "select tipoalgo from garealbo where ngara=? and codgar= ?", new Object[] { codice, "$" + codice });
    	if(tipoalgo != null) {
    		result = String.valueOf(tipoalgo);
    	}
        
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura del tipo algoritmo ", e);
      }

    
    return result;
  }
}

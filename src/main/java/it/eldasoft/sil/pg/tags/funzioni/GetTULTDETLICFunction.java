/*
 * Created on 15-12-2015
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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il campo ULTDETLIC di GARE1 a partire da NGARA.
 *
 * @author Marcello caminiti
 */
public class GetTULTDETLICFunction extends AbstractFunzioneTag {

  public GetTULTDETLICFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String result="";
    String ngara = (String) GeneralTagsFunction.cast("string", params[1]);

    try {
    	Long ultdetlic = (Long) sqlManager.getObject(
            "select ultdetlic from gare1 where ngara= ?", new Object[] { ngara });
    	if(ultdetlic != null) {
    		result = String.valueOf(ultdetlic);
    	}

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del campo GARE1.ULTDETLIC ", e);
    }


    return result;
  }
}

/*
 * Created on 19-09-2011
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
 * Funzione che estrae il campo TIPGAR di TORN a partire da CODGAR. 
 * 
 * @author Marcello caminiti
 */
public class GetTIPOELEFunction extends AbstractFunzioneTag {

  public GetTIPOELEFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String tipoele="";
    String nGara = (String) GeneralTagsFunction.cast("string", params[1]);
    String codiceGara = "$" + nGara;
    
      
    try {
  	tipoele = (String) sqlManager.getObject(
          "select tipoele from garealbo where codgar= ? and ngara = ?", new Object[] { codiceGara, nGara });
  	
  	
      
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del tipo della gara ", e);
    }

    
    return tipoele;
  }
}

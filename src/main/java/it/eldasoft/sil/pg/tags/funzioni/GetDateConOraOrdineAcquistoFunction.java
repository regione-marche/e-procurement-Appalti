/*
 * Created on 09-07-2014
 *
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
 * Funzione che estrae le date DATDEF,DATTRA, DATLET e DATREV di GARECONT in formato
 * stringa
 *
 */
public class GetDateConOraOrdineAcquistoFunction extends AbstractFunzioneTag {

  public GetDateConOraOrdineAcquistoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String ngara = (String) params[1];

    try {
      String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "DATDEF" });
      String dataString = (String)sqlManager.getObject("select " + dbFunctionDateToString + " from garecont where ngara=? and ncont=1", new Object[]{ngara});
      if(dataString!=null)
        pageContext.setAttribute("initDATDEF", dataString, PageContext.REQUEST_SCOPE);

      dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "DATTRA" });
      dataString = (String)sqlManager.getObject("select " + dbFunctionDateToString + " from garecont where ngara=? and ncont=1", new Object[]{ngara});
      if(dataString!=null)
        pageContext.setAttribute("initDATTRA", dataString, PageContext.REQUEST_SCOPE);

      dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "DATLET" });
      dataString = (String)sqlManager.getObject("select " + dbFunctionDateToString + " from garecont where ngara=? and ncont=1", new Object[]{ngara});
      if(dataString!=null)
        pageContext.setAttribute("initDATLET", dataString, PageContext.REQUEST_SCOPE);

      dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "DATREV" });
      dataString = (String)sqlManager.getObject("select " + dbFunctionDateToString + " from garecont where ngara=? and ncont=1", new Object[]{ngara});
      if(dataString!=null)
        pageContext.setAttribute("initDATREV", dataString, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore durante la conversione a stringa delle date dell'ordine di acquisto (tabella GARECONT)", e);
    }

    return null;
  }
}

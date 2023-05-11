/*
 * Created on 30/apr/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;


public class CheckAssociataRdaJIRIDEFunction extends AbstractFunzioneTag{

  public CheckAssociataRdaJIRIDEFunction() {
    super(2, new Class[] {PageContext.class, String.class});
  }
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    String numeroDoc = null;
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String codgar = (String) params[1];
    
    String select = "select WSDOCUMENTO.NUMERODOC from garerda, wsdocumento where garerda.codgar = ? and garerda.NUMRDA = WSDOCUMENTO.NUMERODOC";
    
    try {
      numeroDoc  = (String) sqlManager.getObject(select, new Object[] {codgar});
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura delle associazioni rda JIRIDE",e);
    }
    
    if(numeroDoc != null && numeroDoc.length() != 0){return numeroDoc;}
    return "";
    
  }

}


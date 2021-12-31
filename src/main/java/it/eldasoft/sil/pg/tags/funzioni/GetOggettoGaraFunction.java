/*
 * Created on 05-Giu-2009
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Estrae il campo GARE.NOT_GAR 
 * 
 */
public class GetOggettoGaraFunction extends AbstractFunzioneTag {

  public GetOggettoGaraFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

	String ngara = (String) params[1];
    String not_gar = "";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      
    try {
      not_gar = (String) sqlManager.getObject("select not_gar from GARE where NGARA = ?",new Object[] { ngara });
    } catch (SQLException s) {
        throw new JspException("Errore durante la lettura dell'oggetto della gara", s);
    }
    
    return not_gar;
    
  }

}
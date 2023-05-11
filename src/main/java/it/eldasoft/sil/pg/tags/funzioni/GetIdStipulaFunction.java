/*
 * Created on 28-Giugno-2021
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

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Estrae il campo G1STIPULA.ID
 * 
 */
public class GetIdStipulaFunction extends AbstractFunzioneTag {

  public GetIdStipulaFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

	String codStipula = (String) params[1];
	Long idStipula = null;
    String idStipulaStr = "";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      
    try {
      idStipula = (Long) sqlManager.getObject("select id from G1STIPULA where CODSTIPULA = ?",new Object[] { codStipula });
      if(idStipula != null) {
    	  idStipulaStr = idStipula.toString();
      }
      
    } catch (SQLException s) {
        throw new JspException("Errore durante la lettura dell'oggetto della gara", s);
    }
    
    return idStipulaStr;
    
  }

}
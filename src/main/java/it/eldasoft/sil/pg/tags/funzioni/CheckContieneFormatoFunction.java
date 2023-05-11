/*
 * Created on 25/giu/2018
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;


public class CheckContieneFormatoFunction extends AbstractFunzioneTag {

  public CheckContieneFormatoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
    // TODO Auto-generated constructor stub
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String codiceGara = null;
    String numeroGara = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
      if (numeroGara != null && numeroGara.length() > 0)
        numeroGara = numeroGara.substring(numeroGara.indexOf(":") + 1);
      
    Long result = null;
    
    String temp = (String) params[1];
    Long formato = Long.parseLong(temp);
    try {
      result = (Long) sqlManager.getObject(
          "select formato from g1cridef where formato = ? and g1cridef.ngara = ?",
          new Object[] { formato, numeroGara });
      if(result != null){
        return "true";
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "false";
    
  }
  
  
}

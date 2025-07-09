/*
 * Created on 12/06/14
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

public class CheckAccessoAutorizzatoFVOEFunction extends AbstractFunzioneTag {

  public CheckAccessoAutorizzatoFVOEFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String codimp = (String) params[2];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    try {

      Long count = (Long) sqlManager.getObject("select count(1) from g1fvoe where statorichiesta='2' and codgar=? and codimp=?",
              new Object[] {codgar,codimp});
      if(count!=null && count>0)
        return "true";
    } catch (SQLException e) {
      throw new JspException("Errore durante la validazione dello stato di richiesta d'accesso al fascicolo FVOE", e);
    }
    return "false";
  }

}
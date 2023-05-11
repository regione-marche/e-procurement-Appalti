/*
 * Created on 17-apr-2009
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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetTIPSCADFunction extends AbstractFunzioneTag {

  public GetTIPSCADFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String result="";
    String codice = (String) GeneralTagsFunction.cast("string", params[1]);
    //if (codice.indexOf("NGARA") > 0) {
      codice = codice.substring(codice.indexOf(":") + 1);
      try {
      //  
      Object o = sqlManager.getObject(
          "select tipscad from v_gare_nscad where ngara= ?",
          new Object[] { codice });
      if (o instanceof Long) {
        Long tipscad = (Long) o;
        result = String.valueOf(tipscad.intValue());
      } else if (o instanceof Double) {
        Double tipscad = (Double) o;
        if (tipscad != null) {
          result = String.valueOf(tipscad.intValue());
        }
      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del tipo scadenza della gara ", e);
    }

    /*
     * } else { if (codice.indexOf("$") >= 0) result = "true"; }
     */
    return result;
  }
}

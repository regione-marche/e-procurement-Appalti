/*
 * Created on 20-Ago-2015
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
 * Estrae il campo GARE1.LEGREGSIC
 *
 * @author C.F.
 */
public class GetLEGREGSICFunction extends AbstractFunzioneTag {

  public GetLEGREGSICFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "0";
    // Codice della gara
    String ngara = (String) params[1];


    if (ngara != null && ngara.length() > 0) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      String legregsic = null;
      try {
        legregsic = (String) sqlManager.getObject(
            "select LEGREGSIC from GARE1 where NGARA = ?",
            new Object[] { ngara });
      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura della fase di gara "
            + "(GARE1.LEGREGSIC)", s);
      }

      if (legregsic != null)
        result = legregsic;
      else
        result = "0";
    }
    return result;
  }

}
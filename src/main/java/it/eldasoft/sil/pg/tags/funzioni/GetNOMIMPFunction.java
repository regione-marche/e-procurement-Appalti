/*
 * Created on 18-nov-2009
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

/**
 * Funzione che estrae il campo NOMIMP di IMPR.
 *
 * @author Manuel Bridda
 */
public class GetNOMIMPFunction extends AbstractFunzioneTag {

  public GetNOMIMPFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codimp = (String) params[1];

    String nomimp="";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      nomimp = (String) sqlManager.getObject(
          "select NOMIMP from IMPR where CODIMP = ?", new Object[]{codimp});

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della ragione sociale della ditta ", e);
    }

    return nomimp;
  }
}

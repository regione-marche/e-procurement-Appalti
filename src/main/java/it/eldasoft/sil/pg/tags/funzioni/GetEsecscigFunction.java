/*
 * Created on 24-02-2016
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
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae il campo GARECONT.ESECSCIG
 *
 *
 *
 */
public class GetEsecscigFunction extends AbstractFunzioneTag {

  public GetEsecscigFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esecscig = "";
    String select = "select esecscig from garecont where ngara=? and ncont=?";

    String ngara = (String) params[1];
    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
    String ncont = (String) params[2];
    if (!"".equals(ngara)) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        esecscig = (String) sqlManager.getObject(select, new Object[] { ngara, new Long(ncont) });
      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura del campo ESECSCIG della gara" + ngara, s);
      }
    }
    return esecscig;
  }

}
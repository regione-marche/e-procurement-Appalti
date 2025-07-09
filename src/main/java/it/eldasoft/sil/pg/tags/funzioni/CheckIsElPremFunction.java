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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;


public class CheckIsElPremFunction extends AbstractFunzioneTag{

  public CheckIsElPremFunction() {
    super(2, new Class[] {PageContext.class, String.class});
  }
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String ngara = (String) params[1];

    String result = "";

    try {
      result  = (String) sqlManager.getObject("select iselprem from garealbo where ngara=?", new Object[] {ngara});
    } catch (SQLException e) {
      throw new JspException("Errore nella del valore 'iselprem' nella tabella 'garealbo'",e);
    }
    return "1".equals(result) ? "true" : "false";
  }

}


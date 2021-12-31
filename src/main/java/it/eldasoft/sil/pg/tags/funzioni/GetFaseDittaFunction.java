/*
 * Created on 05-Nov-2008
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
 * Viene letto il valore di DITG.FASGAR per una ditta
 *
 */
public class GetFaseDittaFunction extends AbstractFunzioneTag {

  public GetFaseDittaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String ngara = (String) params[1];
    String ditta = (String) params[2];
    String fasgar = null;
    try {
      Long lfasgar = (Long) sqlManager.getObject("select fasgar from ditg where ngara5 = ? and dittao=?", new Object[] { ngara,ditta });
      if (lfasgar != null) fasgar = lfasgar.toString();
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura della fase della ditta", s);
    }
    return fasgar;
  }

}
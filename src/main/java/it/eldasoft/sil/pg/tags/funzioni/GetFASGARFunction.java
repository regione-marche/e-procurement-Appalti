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

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Estrae il campo GARE.FASGAR necessario per la pagina
 * 
 */
public class GetFASGARFunction extends AbstractFunzioneTag {

  public GetFASGARFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String ngara = (String) params[1];
    String fasgar = null;
    try {
      Long lfasgar = (Long) sqlManager.getObject("select fasgar from gare where ngara = ?", new Object[] { ngara });
      if (lfasgar != null) fasgar = lfasgar.toString();
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura della fase di gara", s);
    }
    return fasgar;
  }

}
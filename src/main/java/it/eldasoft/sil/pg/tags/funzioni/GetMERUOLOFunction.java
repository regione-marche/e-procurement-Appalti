/*
 * Created on 18-06-2014
 *
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che estrae il campo MERUOLO di G_PERMESSI a partire da NGARA.
 * 
 */
public class GetMERUOLOFunction extends AbstractFunzioneTag {

  public GetMERUOLOFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    Long syscon = new Long(profilo.getId());
    
    String result = "";
    String ngara = (String) params[1];

    try {
      Long meruolo = (Long) sqlManager.getObject("select g_permessi.meruolo from g_permessi, torn, gare " +
      		" where g_permessi.codgar = torn.codgar " +
      		" and torn.codgar = gare.codgar1 " +
      		" and gare.ngara = ? and g_permessi.syscon = ?", new Object[] { ngara, syscon });
      if (meruolo != null) {
        result = String.valueOf(meruolo);
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del campo MERUOLO della tabella G_PERMESSI", e);
    }

    return result;
  }
}

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

import java.sql.SQLException;
import java.util.Date;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class IsValutazioneProdottiAttivataFunction extends AbstractFunzioneTag {

  public IsValutazioneProdottiAttivataFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String meric_id_s = (String) params[1];
    Long meric_id = null;
    if (meric_id_s != null && meric_id_s != "") {
      meric_id = new Long((String) params[1]);
    }

    String isValutazioneProdottiAttivata = "false";

    if (meric_id != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        Date datval = (Date) sqlManager.getObject("select datval from meric where id = ?", new Object[] { meric_id });
        if (datval != null) isValutazioneProdottiAttivata = "true";
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura delle informazioni della ricerca di mercato", e);
      }
    }
    
    return isValutazioneProdottiAttivata;
  }

}
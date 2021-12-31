/*
 * Created on 13-nov-2009
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Estrae il campo TORN.MODLIC necessario nella pagina torn-pagine-scheda.jsp
 * per rendere visibile o meno dei tab 
 * 
 * 
 * @author Marcello Caminiti
 */
public class GetMODLICFunction extends AbstractFunzioneTag {

  public GetMODLICFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "0";
    String codiceGara = (String) UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);

    if (codiceGara != null && codiceGara.length() > 0) {
    	codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      Long modlicg = null;
      try {
        modlicg = (Long) sqlManager.getObject(
            "select MODLIC from TORN where CODGAR = ?",
            new Object[] { codiceGara });
      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura del Criterio di aggiudicazione della gara "
            + codiceGara, s);
      }

      if (modlicg != null)
        result = String.valueOf(modlicg.intValue());
      else
        result = "0";
    }
    return result;
  }

}
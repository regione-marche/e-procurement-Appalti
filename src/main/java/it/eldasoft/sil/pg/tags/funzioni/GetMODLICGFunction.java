/*
 * Created on 05-Giu-2009
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae il campo GARE.MODLICG necessario per la pagina gare-pagine-scheda.jsp
 * per rendere attivo o meno il tab relativo alla pagina 'Criteri di
 * valutazione'
 *
 * @author Marcello Caminiti
 */
public class GetMODLICGFunction extends AbstractFunzioneTag {

  public GetMODLICGFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "0";
    String numeroGara = (String) params[1];
    if(numeroGara ==null || "".equals(numeroGara)){
      numeroGara = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
      if (numeroGara != null && numeroGara.length() > 0)
        numeroGara = numeroGara.substring(numeroGara.indexOf(":") + 1);
    }
    if (numeroGara != null && numeroGara.length() > 0) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

      Long modlicg = null;
      try {
        modlicg = (Long) sqlManager.getObject(
            "select MODLICG from GARE where NGARA = ?",
            new Object[] { numeroGara });
      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura della fase di gara "
            + "(GARE.FASGAR)", s);
      }

      if (modlicg != null)
        result = String.valueOf(modlicg.intValue());
      else
        result = "0";
    }
    return result;
  }
}
/*
 * Created on 05/11/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.springframework.dao.DataAccessException;

/**
 * Estrae i correttivi di default previsti in base alla tipologia di appalto
 * 
 * @author Stefano.Sabbadin
 */
public class GetCorrettiviDefaultFunction extends AbstractFunzioneTag {

  public GetCorrettiviDefaultFunction() {
    super(1, new Class[] { PageContext.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String[] correttivi = null;

    try {
      PgManager pgManager = (PgManager) UtilitySpring.getBean(
          "pgManager", pageContext, PgManager.class);
      correttivi = pgManager.getCorrettiviDefault();
    } catch (DataAccessException e) {
      throw new JspException(
          "Errore nell'estrarre i correttivi di default ", e);
    }

    return correttivi[0]+"#"+correttivi[1];
  }

}
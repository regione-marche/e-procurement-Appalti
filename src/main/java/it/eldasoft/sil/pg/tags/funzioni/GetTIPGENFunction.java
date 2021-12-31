/*
 * Created on 06-nov-2008
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
 * Funzione che estrae il campo TIPGEN di TORN. Questa function viene definita
 * perchè, nella lista dei lotti di gara, se la lista è vuota, non è possibile
 * sapere la tipologia di appalto
 * 
 * @author Stefano.Sabbadin
 */
public class GetTIPGENFunction extends AbstractFunzioneTag {

  public GetTIPGENFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    // Codice della gara
    String codiceGara = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      Long tipGen = (Long) sqlManager.getObject(
          "select TIPGEN from TORN where CODGAR = ? ", new Object[]{codiceGara});
      if(tipGen != null)
        pageContext.setAttribute("tipoAppalto", tipGen, PageContext.PAGE_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del tipo di tornata della gara ", e);
    }

    return null;
  }
}

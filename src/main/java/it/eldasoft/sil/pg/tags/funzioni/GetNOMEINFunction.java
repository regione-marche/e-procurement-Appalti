/*
 * Created on 18-nov-2009
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
 * Funzione che estrae il campo NOMIMO di DITG. Questa function viene definita
 * perchè, nella scheda v_gcap_dpre.jsp non è possibile sapere la ragione
 * sociale della ditta
 *
 * @author Marcello Caminiti
 */
public class GetNOMEINFunction extends AbstractFunzioneTag {

  public GetNOMEINFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codein = (String) params[1];

    String nomein="";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      nomein = (String) sqlManager.getObject(
          "select NOMEIN from UFFINT where CODEIN = ?", new Object[]{codein});

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della ragione sociale della ditta ", e);
    }

    return nomein;
  }
}

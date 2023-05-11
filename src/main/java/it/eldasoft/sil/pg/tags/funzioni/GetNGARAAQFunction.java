/*
 * Created on 13-01-2016
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
 * Funzione che estrae il campo NGARAA di TORN.
 *
 * @author Marcello Caminiti
 */
public class GetNGARAAQFunction extends AbstractFunzioneTag {

  public GetNGARAAQFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    // Numero gara
    String numeroGara = (String) params[1];

   String result="";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      String ngaraaq = (String) sqlManager.getObject(
          "select ngaraaq from gare,torn where ngara=? and codgar= codgar1", new Object[]{numeroGara});
      if(ngaraaq != null)
    	  result = ngaraaq;
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del campo ngaraaq della gara " + numeroGara, e);
    }

    return result;
  }
}

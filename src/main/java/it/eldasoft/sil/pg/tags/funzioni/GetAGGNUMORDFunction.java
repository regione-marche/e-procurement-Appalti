/*
 * Created on 24-06-2015
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il campo AGGNUMORD di GAREALBO a partire da NGARA.
 *
 * @author Marcello caminiti
 */
public class GetAGGNUMORDFunction extends AbstractFunzioneTag {

  public GetAGGNUMORDFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String result="";
    String codice = (String) GeneralTagsFunction.cast("string", params[1]);

      codice = codice.substring(codice.indexOf(":") + 1);
      try {
        result = (String) sqlManager.getObject(
            "select aggnumord from garealbo where ngara=? and codgar= ?", new Object[] { codice, "$" + codice });

      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura del campo aggnumord ", e);
      }


    return result;
  }
}

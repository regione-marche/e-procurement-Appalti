/*
 * Created on 14-lug-2008
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GaraLottoUnicoFunction extends AbstractFunzioneTag {

  public GaraLottoUnicoFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String result = "false";
    String codice = (String) GeneralTagsFunction.cast("string", params[1]);
    if (codice.indexOf("NGARA") > 0) {
      codice = codice.substring(codice.indexOf(":") + 1);
      try {
        String codgar1 = (String) sqlManager.getObject(
            "select codgar1 from gare where ngara = ?", new Object[] { codice });
        if (codgar1.indexOf("$") >= 0) result = "true";
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura dela chiave della gara ", e);
      }

    } else {
      if (codice.indexOf("$") >= 0) result = "true";
    }
    return result;
  }
}

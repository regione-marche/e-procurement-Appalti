/*
 * Created on 24-02-2016
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
 * Funzione che estrae il campo NCONT di GARECONT.
 *
 * @author Cristian.Febas
 */
public class GetNCONTFunction extends AbstractFunzioneTag {

  public GetNCONTFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "";
    String ngara = (String) params[1];
    String modcont = (String) params[2];
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    try {
      String select = "select NCONT from GARE,GARECONT where GARECONT.NGARA=GARE.CODGAR1 and GARECONT.CODIMP=GARE.DITTA and " +
          " GARE.NGARA = ? ";
      if("1".equals(modcont))
        select += " and GARE.NGARA = GARECONT.NGARAL";

      Long ncont = (Long) sqlManager.getObject(select, new Object[]{ngara});
      if(ncont != null){
        result = ncont.toString();
      }
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del numero progressivo del contratto ", e);
    }

    return result;
  }
}

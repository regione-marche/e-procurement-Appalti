/*
 * Created on 06-10-2016
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
 * Funzione che estrae il campo STATO di GARECONT
 *
 * @author Marcello Caminiti
 */
public class GetStatoGarecontFunction extends AbstractFunzioneTag {

  public GetStatoGarecontFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result="";

    String ngara = (String) params[1];
    String ncont = (String) params[2];
    if (ngara !=null && !"".equals(ngara)){
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      try {
        Long stato = (Long) sqlManager.getObject(
            "select stato from GARECONT where NGARA = ? and NCONT=?", new Object[]{ngara, new Long(ncont)});
        if(stato != null)
            result = stato.toString();
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura dello stato di GARECONT ", e);
      }

    }
    return result;
  }
}

/*
 * Created on 22-09-2014
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
 * Funzione che estrae il campo STATO di MEARTCAT
 *
 * @author Marcello Caminiti
 */
public class GetStatoMeartcatFunction extends AbstractFunzioneTag {

  public GetStatoMeartcatFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result="";

    String id = (String) params[1];
    if (id !=null && !"".equals(id)){
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      try {
        Long stato = (Long) sqlManager.getObject(
            "select stato from MEARTCAT where ID = ?", new Object[]{new Long(id)});
        if(stato != null)
            result = stato.toString();
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura dello stato dell'articolo ", e);
      }

    }
    return result;
  }
}

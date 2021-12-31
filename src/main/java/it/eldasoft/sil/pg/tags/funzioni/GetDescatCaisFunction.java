/*
 * Created on 26-08-2013
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
 * Funzione che estrae il valore di DESCAT.CAIS a partire da CAISIM.CAIS
 *
 * @author Marcello Caminiti
 */
public class GetDescatCaisFunction extends AbstractFunzioneTag {

  public GetDescatCaisFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codcat = (String) params[1];
    String descat=null;

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      descat = (String)sqlManager.getObject(
          "select descat from cais where caisim = ?",
          new Object[] { codcat});
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura della descrizione della categoria " + codcat,
          s);
    }

    if (descat != null && descat.length() > 0) {
      if(descat.length()>60){
        descat.substring(0, 60);
        descat += "...";
      }
    }

    return descat;
  }

}

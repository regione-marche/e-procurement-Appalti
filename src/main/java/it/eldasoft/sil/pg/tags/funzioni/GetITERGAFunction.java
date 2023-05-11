/*
 * Created on 09-feb-2022
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Estrae il campo TORN.ITERGA  
 * 
 * 
 * @author Cristian Febas
 */
public class GetITERGAFunction extends AbstractFunzioneTag {

  public GetITERGAFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "0";
    String codiceGara = (String)params[1];
    
    if (codiceGara != null && codiceGara.length() > 0) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      Long iterga = null;
      try {
        iterga = (Long) sqlManager.getObject(
            "select ITERGA from TORN where CODGAR = ?",
            new Object[] { codiceGara });
      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura del Criterio di aggiudicazione della gara "
            + codiceGara, s);
      }

      if (iterga != null)
        result = String.valueOf(iterga.intValue());
      else
        result = "0";
    }
    return result;
  }

}
/*
 * Created on 19-Apr-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetCIGAccQuadroW3LottFunction extends AbstractFunzioneTag {

  public GetCIGAccQuadroW3LottFunction() {
    super(2, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String cigAccQuadro = null;

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      Long numgara = new Long((String)params[1]);

      try {
        cigAccQuadro = (String) sqlManager.getObject(
            "select cig_acc_quadro from w3gara where numgara = ? ",
            new Object[] { numgara });
      } catch (SQLException s) {
        throw new JspException(
            "Errore durante la lettura del cig dell'accordo quadro collegato alla gara", s);
      }

    if (cigAccQuadro == null) cigAccQuadro = "";
    
    return cigAccQuadro;

  }

}
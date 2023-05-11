/*
 * Created on 12/06/14
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
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class IsProceduraTelematicaFunction extends AbstractFunzioneTag {

  public IsProceduraTelematicaFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];

    String isProceduraTelematica = "false";

    if (codgar != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String selectTORN = "select gartel from torn where codgar = ?";
        String gartel = (String) sqlManager.getObject(selectTORN, new Object[] {codgar});

        if (gartel != null && gartel.equals("1")) {
          isProceduraTelematica = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo delle procedura telematica", e);
      }
    }

    return isProceduraTelematica;
  }

}
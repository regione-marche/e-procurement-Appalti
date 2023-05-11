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

public class IsProceduraTelematicaGaraFunction extends AbstractFunzioneTag {

  public IsProceduraTelematicaGaraFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];

    String isProceduraTelematicaGara = "false";

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String selectTORN = "select torn.gartel from torn, gare where torn.codgar = gare.codgar1 and gare.ngara = ?";
        String gartel = (String) sqlManager.getObject(selectTORN, new Object[] {ngara});

        if (gartel != null && gartel.equals("1")) {
          isProceduraTelematicaGara = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo delle procedura telematica", e);
      }
    }

    return isProceduraTelematicaGara;
  }

}
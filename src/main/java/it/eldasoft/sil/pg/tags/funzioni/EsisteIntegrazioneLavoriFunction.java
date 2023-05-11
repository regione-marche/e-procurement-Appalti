/*
 * Created on 23/06/10
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
import it.eldasoft.utils.spring.UtilitySpring;

public class EsisteIntegrazioneLavoriFunction extends AbstractFunzioneTag {

  public EsisteIntegrazioneLavoriFunction() {
    super(1, new Class[] { PageContext.class});
  }

  //Viene controllato se in ELDAVER sono presenti le righe per G2 e PL.
  //Se queste sono presenti si deve gestire l'integrazione con Lavori
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String esisteIntegrazioneLavori = "FALSE";

    try {
      Long conteggio = (Long) sqlManager.getObject(
          "select count(*) from eldaver where "
              + " codapp = 'PL' or codapp = 'G2'",
          null);
      if (conteggio != null && conteggio.longValue() > 0)
        esisteIntegrazioneLavori = "TRUE";
    } catch (SQLException e) {
      throw new JspException(
          "Errore nella determinazione dell'integrazione con Lavori", e);
    }

    return esisteIntegrazioneLavori;

  }

}

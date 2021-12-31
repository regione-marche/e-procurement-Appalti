/*
 * Created on 11/06/19
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

public class EsistonoDitteEstimpValorizzatoFunction extends AbstractFunzioneTag {

  public EsistonoDitteEstimpValorizzatoFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String esistonoDitteEstimpValorizzato = "false";

    if (codgar != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String selectDITG = "select count(*) from ditg where ngara5 = ? and estimp is not null and (fasgar is null or fasgar>1)";

        Long conteggio = (Long) sqlManager.getObject(selectDITG, new Object[] {codgar});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoDitteEstimpValorizzato = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo del campo DITG.ESTIMP", e);
      }
    }

    return esistonoDitteEstimpValorizzato;
  }

}
/*
 * Created on 09/07/15
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

public class EsistonoDitteRequisitiMinimiNulliFunction extends AbstractFunzioneTag {

  public EsistonoDitteRequisitiMinimiNulliFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String isOffertaUnica = (String) params[2];

    String esistonoDitteReqminNullo = "false";
    String selectDITG="";

    if (ngara != null) {
      Long conteggio = null;
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        if(!"true".equals(isOffertaUnica)){
          selectDITG = "select count(*) from ditg where ngara5 = ? and reqmin is null and (fasgar > 5 or fasgar = 0 or fasgar is null)";
        }else {
          selectDITG ="select count(*) from ditg where codgar5 = ? and ngara5!= codgar5 and reqmin is null and (fasgar > 5 or fasgar = 0 or fasgar is null)" +
               " and exists (select ngara from gare1 where ngara=ngara5 and valtec ='1')";
        }
        conteggio = (Long) sqlManager.getObject(selectDITG, new Object[] {ngara});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoDitteReqminNullo = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo del campo conforme requisiti minimi delle ditte della gara", e);
      }
    }

    return esistonoDitteReqminNullo;
  }

}
/*
 * Created on 22/04/20
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
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class EsistonoOccorrenzeGaraltsogFunction extends AbstractFunzioneTag {

  public EsistonoOccorrenzeGaraltsogFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String ngaral = (String) params[2];
    String modcont = (String) params[3];
    String codimp = (String) params[4];

    String ret="false";
    String lotti="";
    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String select = "select count(*) from garaltsog where ngara ";
        if("1".equals(modcont) || modcont==null || "".equals(modcont)){
          select += " = '";
          if("1".equals(modcont))
            select+=ngaral;
          else
            select+=ngara;
          select += "'";
        }else if("2".equals(modcont)){
          PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);
          lotti = pgManagerEst1.getElencoLottiAggiudicati(ngara, codimp);
          select += " in(" + lotti + ")";
        }

        Long conteggio = (Long) sqlManager.getObject(select, null);

        if (conteggio != null && conteggio.longValue() > 0) {
          ret = "true";
          if("2".equals(modcont))
            pageContext.setAttribute("elencoLotti", lotti, PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il conteggio delle occorrenze in GARALTSOG", e);
      }
    }

    return ret;
  }

}
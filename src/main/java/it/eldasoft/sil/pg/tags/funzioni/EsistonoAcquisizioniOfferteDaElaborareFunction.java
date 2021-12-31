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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class EsistonoAcquisizioniOfferteDaElaborareFunction extends AbstractFunzioneTag {

  public EsistonoAcquisizioniOfferteDaElaborareFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String tipoOfferta = (String) params[2];

    String esistonoAcquisizioniOfferteDaElaborare = "false";

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      try {
        String selectW_INVCOM = "select count(*) from w_invcom where comtipo = ? and comstato in ('5','7','9','13','16','17') and comkey2 = ?";
        Long conteggio = (Long) sqlManager.getObject(selectW_INVCOM, new Object[] {tipoOfferta, ngara});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoAcquisizioniOfferteDaElaborare = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo delle procedura telematica", e);
      }
    }

    return esistonoAcquisizioniOfferteDaElaborare;
  }

}
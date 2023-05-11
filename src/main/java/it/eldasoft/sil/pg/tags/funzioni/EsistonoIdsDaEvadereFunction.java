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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class EsistonoIdsDaEvadereFunction extends AbstractFunzioneTag {

  public EsistonoIdsDaEvadereFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codiceGara = (String) params[1];

    String esistonoIdsDaEvadere = "false";

    if (codiceGara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String selectIdsDaEvadere  = "select count(ui2.ids_prog) from utenti_ids ui2,v_lista_gare_ids gi" +
        " where ui2.ids_prog = gi.ids_prog and ui2.flag_evadi = 2 and gi.codice_gara = ? ";

        Long conteggio = (Long) sqlManager.getObject(selectIdsDaEvadere, new Object[] {codiceGara});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoIdsDaEvadere = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo degli ids inevasi della gara", e);
      }
    }

    return esistonoIdsDaEvadere;
  }

}
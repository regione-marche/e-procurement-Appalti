/*
 * Created on 06-Feb-2012
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

public class IsGaraLottiPubblicabileFunction extends AbstractFunzioneTag {

  public IsGaraLottiPubblicabileFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String pubblicabile = "true";
    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(
        pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      Long numgara = new Long((String) params[1]);

      try {

        Long conteggio = (Long) sqlManager.getObject(
            "select count(*) from w3lott where numgara = ? and stato_simog in (1,3,5)",
            new Object[] { numgara });
        if (conteggio != null && conteggio.longValue() > 0) {
          pubblicabile = "false";
        }

      } catch (SQLException s) {
        throw new JspException(
            "Errore durante il controllo dello stato dei lotti", s);
      }
    }

    return pubblicabile;

  }

}
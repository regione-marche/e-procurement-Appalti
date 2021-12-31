/*
 * Created on 29/03/2021
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per il calcolo del numero di discussioni
 * 
 */
public class GetDiscussioniNumeroFunction extends AbstractFunzioneTag {

  public GetDiscussioniNumeroFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String entita = (String) params[1];
    String key1 = (String) params[2];

    try {
      // Conteggio discussioni
      String selectNumeroDiscussioni = "select count(discid_p) from w_discuss_p where discprg=? and discent=? and disckey1=?";
      Long numeroDiscussioni = (Long) sqlManager.getObject(selectNumeroDiscussioni, new Object[] { "PG", entita, key1 });
      pageContext.setAttribute("numeroDiscussioni", numeroDiscussioni, PageContext.REQUEST_SCOPE);

      // Numero totale messaggi
      String selectConteggioMessaggiTotali = "select count(w_discuss.discid) from w_discuss_p, w_discuss "
          + " where w_discuss_p.discid_p = w_discuss.discid_p"
          + " and w_discuss_p.discprg = ? "
          + " and w_discuss_p.discent = ? "
          + " and w_discuss_p.disckey1 = ? "
          + " and w_discuss.discmesspubbl = ?";

      Long numeroTotaleMessaggi = (Long) sqlManager.getObject(selectConteggioMessaggiTotali, new Object[] { "PG", entita, key1, "1" });
      if (numeroTotaleMessaggi == null) {
        numeroTotaleMessaggi = new Long(0);
      }

      // Numero totale messaggi non letti per l'utente corrente
      String selectMessaggiLettiUtente = "select count(w_discuss.discid) from w_discuss_p, w_discuss, w_discread "
          + " where w_discuss_p.discid_p = w_discuss.discid_p"
          + " and w_discuss_p.discprg = ? "
          + " and w_discuss_p.discent = ? "
          + " and w_discuss_p.disckey1 = ? "
          + " and w_discuss.discmesspubbl = ? "
          + " and w_discuss.discid_p = w_discread.discid_p "
          + " and w_discuss.discid = w_discread.discid "
          + " and w_discread.discmessope = ?";

      ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

      Long numeroMessaggiLettiUtente = (Long) sqlManager.getObject(selectMessaggiLettiUtente, new Object[] { "PG", entita, key1, "1",
          new Long(profiloUtente.getId()) });
      if (numeroMessaggiLettiUtente == null) {
        numeroMessaggiLettiUtente = new Long(0);
      }

      // Numero messaggi non letti per l'utente corrente
      Long numeroMessaggiNonLettiUtente = new Long(numeroTotaleMessaggi.longValue() - numeroMessaggiLettiUtente.longValue());
      if (numeroMessaggiNonLettiUtente.longValue() < 0) {
        numeroMessaggiNonLettiUtente = new Long(0);
      }
      pageContext.setAttribute("numeroMessaggiNonLettiUtente", numeroMessaggiNonLettiUtente, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio delle discussioni con chiave " + key1, e);
    }

    return "";
  }
}

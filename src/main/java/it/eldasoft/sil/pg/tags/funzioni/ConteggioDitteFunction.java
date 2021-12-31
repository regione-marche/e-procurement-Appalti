/*
 * Created on 06/ago/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ConteggioDitteFunction extends AbstractFunzioneTag {

  public ConteggioDitteFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    String filtro = (String) params[2];
    String risultato = "0";

    try {
      AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
          "aggiudicazioneManager", pageContext, AggiudicazioneManager.class);

      Long conteggio = aggiudicazioneManager.conteggioDitte(ngara, filtro);
      if (conteggio != null) risultato = conteggio.toString();

    } catch (Exception e) {
      throw new JspException("Errore nel conteggio delle ditte", e);
    }

    return risultato;
  }

}

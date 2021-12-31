/*
 * Created on 04/ago/08
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

/**
 * @author Stefano.Cestaro
 * 
 */
public class EsistonoDittePerAggiudicazioneFunction extends AbstractFunzioneTag {

  public EsistonoDittePerAggiudicazioneFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esito = "false";
    String ngara = (String) params[1];

    try {
      AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
          "aggiudicazioneManager", pageContext, AggiudicazioneManager.class);

      if(aggiudicazioneManager.esistonoDitte(ngara, "staggi > 1"))
          if(aggiudicazioneManager.isFaseDiGaraAggiudicazione(ngara))
            esito = "true";

    } catch (Exception e) {
      throw new JspException(
          "Errore nella verifica delle ditte ammesse al processo di aggiudicazione",
          e);
    }

    return esito;
  }
}

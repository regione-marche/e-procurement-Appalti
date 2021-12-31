/*
 * Created on 30/lug/08
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

public class EsistonoDitteRiammesseFunction extends AbstractFunzioneTag {

  public EsistonoDitteRiammesseFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esito = "false";
    String ngara = (String) params[1];
    String controlloPunecoString= (String) params[2];
    boolean controlloPuneco = true;
    if(!"true".equals(controlloPunecoString))
      controlloPuneco=false;

    try {
      AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
          "aggiudicazioneManager", pageContext, AggiudicazioneManager.class);

      if (aggiudicazioneManager.esistonoDitteRiammesse(ngara,controlloPuneco) == true)
        esito = "true";

    } catch (Throwable e) {
      throw new JspException("Errore nel controllo delle ditte riammesse", e);
    }
    return esito;
  }

}

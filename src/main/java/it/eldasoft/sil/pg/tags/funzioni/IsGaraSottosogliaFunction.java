/*
 * Created on 28-ago-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Function che ritorna 'true' se la gara è sottosoglia in base al DGLS 36/2023
 *
 * @author Manuel Bridda
 */
public class IsGaraSottosogliaFunction extends AbstractFunzioneTag {

  public IsGaraSottosogliaFunction() {
    super(2, new Class[] {PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean("aggiudicazioneManager", pageContext, AggiudicazioneManager.class);
    String codgar = (String) params[1];
    boolean isSottoSoglia=false;
    try {
      isSottoSoglia=aggiudicazioneManager.isSottoSoglia(codgar);
    } catch (GestoreException e) {
      throw new JspException("Errore nello stabilire se la gara è sottosoglia: " + codgar,e);
    }
    return String.valueOf(isSottoSoglia);
  }

}

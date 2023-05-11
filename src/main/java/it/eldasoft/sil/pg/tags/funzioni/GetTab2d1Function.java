/*
 * Created on 11-01-2017
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene estratto il valore tab2d1 del tabellato specificando come parametri il tab2cod e tab2tip
 *
 * @author Manuel Bridda
 */
public class GetTab2d1Function extends AbstractFunzioneTag {

  public GetTab2d1Function() {
    super(3, new Class[] { PageContext.class,String.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);
    String tab2cod = (String) params[1];
    String tab2tip = (String) params[2];
    String valore = tabellatiManager.getDescrSupplementare(tab2cod, tab2tip);
    return valore;
  }

}

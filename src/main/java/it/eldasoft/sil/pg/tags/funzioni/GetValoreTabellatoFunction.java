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
 * Viene estratto il valore del tabellato specificando come parametri il tab1cod e tab1tip
 *
 * @author Marcello Caminiti
 */
public class GetValoreTabellatoFunction extends AbstractFunzioneTag {

  public GetValoreTabellatoFunction() {
    super(4, new Class[] { PageContext.class,String.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);
    String tab1cod = (String) params[1];
    String tab1tip = (String) params[2];
    String soloPrimoCarattere = (String) params[3];
    String valore = tabellatiManager.getDescrTabellato(tab1cod, tab1tip);
    if("true".equals(soloPrimoCarattere) && valore!=null && !"".equals(valore)){
      valore = valore.substring(0, 1);
    }
    return valore;
  }

}

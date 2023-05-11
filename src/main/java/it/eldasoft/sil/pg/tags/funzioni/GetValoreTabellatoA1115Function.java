/*
 * Created on 08-07-2015
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
 * Viene estratto il valore del tabellato A1115 relativo al valore di
 * TAB1TIP specificato
 *
 * @author Marcello Caminiti
 */
public class GetValoreTabellatoA1115Function extends AbstractFunzioneTag {

  public GetValoreTabellatoA1115Function() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    //SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);
    String ta1tip = (String) params[1];
    String valore = tabellatiManager.getDescrTabellato("A1115", ta1tip);
    if(valore!=null && !"".equals(valore))
      valore = valore.substring(0, 1);
    else
      valore=null;
    return valore;
  }

}

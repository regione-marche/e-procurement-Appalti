/*
 * Created on 02/11/21
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
 * Viene letto il tabellato A1173 per stabilire come inizializzare il campo
 * ALLMAIL
 *
 * @author Peruzzo Riccardo
 */
public class InizializzazioneAllmailFunction extends AbstractFunzioneTag {

  public InizializzazioneAllmailFunction(){
    super(1, new Class[]{PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);
    String desc = tabellatiManager.getDescrTabellato("A1173", "1");
    if(desc!=null && !"".equals(desc))
      desc = desc.substring(0,1);
    String inizializzaAllmail="false";
    if("1".equals(desc))
    	inizializzaAllmail="true";

    return inizializzaAllmail;
  }

}
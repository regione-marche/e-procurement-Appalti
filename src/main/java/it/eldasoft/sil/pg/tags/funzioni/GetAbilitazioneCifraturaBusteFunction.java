/*
 * Created on 12-02-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

/**
 * Viene letto il tabellato A1122 per verificare l'abilitazione della cifratura delle buste
 *
 * @author Marcello Caminiti
 */
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetAbilitazioneCifraturaBusteFunction extends AbstractFunzioneTag {

  public GetAbilitazioneCifraturaBusteFunction() {
    super(1, new Class[]{PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ret="2";
    pageContext = (PageContext) params[0];


    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);

    String desc = tabellatiManager.getDescrTabellato("A1122", "1");
    if(desc!=null && !"".equals(desc)){
      ret = desc.substring(0,1);
    }
    return ret;
  }

}
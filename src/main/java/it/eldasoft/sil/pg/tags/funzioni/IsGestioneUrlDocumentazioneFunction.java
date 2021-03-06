/*
 * Created on 07/11/14
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
 * Viene letto il tabellato A1108 per stabilire se ? attiva la gestione
 * dell'url nei documenti
 *
 * @author Marcello Caminiti
 */
public class IsGestioneUrlDocumentazioneFunction extends AbstractFunzioneTag {

  public IsGestioneUrlDocumentazioneFunction(){
    super(1, new Class[]{PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);
    String desc = tabellatiManager.getDescrTabellato("A1108", "1");
    if(desc!=null && !"".equals(desc))
      desc = desc.substring(0,1);
    String gestioneUrl="false";
    if("1".equals(desc))
      gestioneUrl="true";

    return gestioneUrl;
  }

}
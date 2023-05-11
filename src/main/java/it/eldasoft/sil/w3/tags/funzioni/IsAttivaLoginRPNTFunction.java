/*
 * Created on 15-Sep-2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.funzioni;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class IsAttivaLoginRPNTFunction extends AbstractFunzioneTag {

  public IsAttivaLoginRPNTFunction() {
    super(1, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager = (GestioneServiziIDGARACIGManager) UtilitySpring.getBean("gestioneServiziIDGARACIGManager",
        pageContext, GestioneServiziIDGARACIGManager.class);

    return gestioneServiziIDGARACIGManager.isLoginRPNTEnabled() ? "1" : "0";
  }

}
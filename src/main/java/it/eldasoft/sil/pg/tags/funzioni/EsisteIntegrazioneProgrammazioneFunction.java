/*
 * Created on 06/12/22
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
import it.eldasoft.utils.properties.ConfigManager;

public class EsisteIntegrazioneProgrammazioneFunction extends AbstractFunzioneTag {

  public EsisteIntegrazioneProgrammazioneFunction() {
    super(1, new Class[] { PageContext.class});
  }

  //Viene controllato se sono valorizzati e presenti i parametri per Integrazione Programmazione.
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String integrazioneProgrammazione="0";
    String url = ConfigManager.getValore("programmazione.ws.url");
    String usr = ConfigManager.getValore("programmazione.ws.username");
    String pwd = ConfigManager.getValore("programmazione.ws.password");
    if(url != null && !"".equals(url) && usr != null && !"".equals(usr) && pwd != null && !"".equals(pwd)){
      integrazioneProgrammazione ="1";
    }
    return integrazioneProgrammazione;
  }

}

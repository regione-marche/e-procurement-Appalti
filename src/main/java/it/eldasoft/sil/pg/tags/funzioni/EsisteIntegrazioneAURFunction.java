/*
 * Created on 14/04/11
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
import it.eldasoft.utils.properties.ConfigManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Controlla se esiste l'integrazione AUR
 * 
 * @author Marcello Caminiti
 */
public class EsisteIntegrazioneAURFunction extends AbstractFunzioneTag {

  public EsisteIntegrazioneAURFunction(){
    super(1, new Class[]{PageContext.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    String integrazioneAUR="0";
    String urlWSAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
    if(urlWSAUR != null && !"".equals(urlWSAUR)){
      integrazioneAUR ="1";
    }
    return integrazioneAUR;
  }
  
}
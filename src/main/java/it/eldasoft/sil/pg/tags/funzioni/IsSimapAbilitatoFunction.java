/*
 * Created on 13/09/11
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
 * Controlla se è valorizzata la properties per "simap"
 * 
 * @author Marcello Caminiti
 */
public class IsSimapAbilitatoFunction extends AbstractFunzioneTag {

  public IsSimapAbilitatoFunction(){
    super(1, new Class[]{PageContext.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    String isSimapAbilitato="0";
    String urlSimap = ConfigManager.getValore("it.eldasoft.bandoavvisosimap.ws.url");
    if(urlSimap != null && !"".equals(urlSimap)){
      isSimapAbilitato ="1";
    }
    return isSimapAbilitato;
  }
  
}
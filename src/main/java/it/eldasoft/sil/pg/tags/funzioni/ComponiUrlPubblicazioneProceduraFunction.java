/*
 * Created on 30-05-2024
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
 * La function legge e concatena due properties da W_CONFIG, poi appende il codice della procedura
 *
 * @author Manuel Bridda
 */
public class ComponiUrlPubblicazioneProceduraFunction extends AbstractFunzioneTag {

  private static final String PROP_URL_PROCEDURE = "invioScp.urlProcedurePortaleAppalti";
  private static final String PROP_URL_PUBBLICA = "portaleAppalti.urlPubblica";

  public ComponiUrlPubblicazioneProceduraFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codiceProcedura = (String) params[1];
    String urlPubblica = ConfigManager.getValore(PROP_URL_PUBBLICA);
    String urlProcedure = ConfigManager.getValore(PROP_URL_PROCEDURE);
    if (urlPubblica.endsWith("/") && urlProcedure.startsWith("/")) {
      urlProcedure = urlProcedure.substring(1);
    }
    return urlPubblica + urlProcedure + codiceProcedura;
  }
}

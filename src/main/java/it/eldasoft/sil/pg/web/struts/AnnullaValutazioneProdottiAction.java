/*
 * Created on 12/06/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AnnullaValutazioneProdottiAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_SUCCESS = "annullavalutazioneprodottisuccess";
  protected static final String FORWARD_ERROR   = "annullavalutazioneprodottierror";

  static Logger                 logger          = Logger.getLogger(AnnullaValutazioneProdottiAction.class);

  private MEPAManager           mepaManager;

  public void setmepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("AnnullaValutazioneProdottiAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;

    try {
      Long id = new Long(request.getParameter("id"));
      
      mepaManager.annullaValutazioneProdotti(id);

      target = FORWARD_SUCCESS;
      request.getSession().setAttribute("id", id);
      request.getSession().setAttribute("numeroPopUp", "1");
    } catch (GestoreException e) {
      target = FORWARD_ERROR;
      messageKey = "errors.annullavalutazioneprodotti.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled()) logger.debug("AnnullaValutazioneProdottiAction: fine metodo");

    return mapping.findForward(target);

  }

}

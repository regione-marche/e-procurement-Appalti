/*
 * Created on 31/aug/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.web.struts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;

public class CancellaCredenzialiSimogAction extends ActionBaseNoOpzioni {

  protected static final String           FORWARD_SUCCESS = "success";

  static Logger                           logger          = Logger.getLogger(CancellaCredenzialiSimogAction.class);

  private GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager;  
  
  public void setGestioneServiziIDGARACIGManager(
      GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager) {
    this.gestioneServiziIDGARACIGManager = gestioneServiziIDGARACIGManager;
  }
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("DeleteCredenzialiSimogAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    
    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = (long) profilo.getId();

    try {
      this.gestioneServiziIDGARACIGManager.cancellaSIMOGWSUserPass(syscon);
      request.setAttribute("isRup",false);
      
    }  catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }
    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled())
      logger.debug("DeleteCredenzialiSimogAction: fine metodo");

    return mapping.findForward(target);

  }

}

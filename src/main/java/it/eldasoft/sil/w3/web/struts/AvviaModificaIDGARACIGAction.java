/*
 * Created on 15/nov/2010
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
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.sil.w3.bl.ValidazioneIDGARACIGManager;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AvviaModificaIDGARACIGAction extends ActionBaseNoOpzioni {

  protected static final String       FORWARD_CONTROLLO = "modificaidgaracigcontrollo";
  protected static final String       FORWARD_RICHIESTA = "modificaidgaracigrichiesta";

  static Logger                       logger            = Logger.getLogger(AvviaModificaIDGARACIGAction.class);

  private ValidazioneIDGARACIGManager validazioneIDGARACIGManager;

  public void setValidazioneIDGARACIGManager(
      ValidazioneIDGARACIGManager validazioneIDGARACIGManager) {
    this.validazioneIDGARACIGManager = validazioneIDGARACIGManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("AvviaModificaIDGARACIGAction: inizio metodo");

    String target = FORWARD_RICHIESTA;
    String messageKey = null;

    String entita = request.getParameter("entita");
    Long numgara = new Long(request.getParameter("numgara"));
    Long numlott = new Long(request.getParameter("numlott"));

    request.getSession().setAttribute("erroreInvioRichiestaSimog", "false");
    
    int numeroErrori = 0;

    try {

      HashMap infoValidazione = new HashMap();
      
      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());

      if ("W3GARA".equals(entita)) {
        infoValidazione = this.validazioneIDGARACIGManager.validateW3GARA(syscon, numgara);
      } else if ("W3LOTT".equals(entita)) {
        infoValidazione = this.validazioneIDGARACIGManager.validateW3LOTT(syscon, numgara, numlott);
      }

      if (infoValidazione.get("numeroErrori") != null)
        numeroErrori = ((Long) infoValidazione.get("numeroErrori")).intValue();

      request.getSession().setAttribute("entita", entita);
      request.getSession().setAttribute("numlott", numlott);
      request.getSession().setAttribute("numgara", numgara);
      request.getSession().setAttribute("numeroPopUp", "1");

      if (numeroErrori > 0) target = FORWARD_CONTROLLO;

    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled())
      logger.debug("AvviaModificaIDGARACIGAction: fine metodo");

    return mapping.findForward(target);

  }

}

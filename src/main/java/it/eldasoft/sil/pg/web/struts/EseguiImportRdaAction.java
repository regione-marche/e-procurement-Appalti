/*
 * Created on 20/12/2022
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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneProgrammazioneManager;
import it.eldasoft.utils.utility.UtilityStringhe;

public class EseguiImportRdaAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(EseguiImportRdaAction.class);

  private GestioneProgrammazioneManager            gestioneProgrammazioneManager;
  
  public void setGestioneProgrammazioneManager(GestioneProgrammazioneManager gestioneProgrammazioneManager) {
    this.gestioneProgrammazioneManager = gestioneProgrammazioneManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    
    String target = "success";
    String messageKey = null;
    if (logger.isDebugEnabled()) logger.debug("EseguiImportRdaAction: inizio metodo");

    String codgar = request.getParameter("codgar");
    String ngara = request.getParameter("ngara");
    String isLotto = request.getParameter("isLotto");
    try {
        gestioneProgrammazioneManager.importaRda(codgar, ngara, "true".equals(isLotto));
        request.setAttribute("RISULTATO", "ok");
     
    }catch (GestoreException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "wserp.erp.listarda.remote.error";
      this.aggiungiMessaggio(request, messageKey);
      logger.error(this.resBundleGenerale.getString(messageKey), e);
    }
    if (logger.isDebugEnabled()) logger.debug("EseguiImportRdaAction: fine metodo");
    return mapping.findForward(target);

  }
}

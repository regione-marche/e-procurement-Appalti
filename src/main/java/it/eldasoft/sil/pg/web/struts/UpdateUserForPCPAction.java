/*
 * Created on 12/12/2023
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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;

/**
 * Backdoor che consente al solo utente amministratore di impersonificare un utente per l'utilizzo dei servizi di invio a PCP
 */
public class UpdateUserForPCPAction extends ActionAjaxLogged {

  static Logger     logger = Logger.getLogger(UpdateUserForPCPAction.class);


  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    ProfiloUtente utente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    if(utente.getId()==48 || utente.getId()==49) {
      try {
  
        String standardProvider=request.getParameter("sp"); // SPID/CIE/CUSTOM
        String loa=request.getParameter("l"); // 2-3-4
        String cf=request.getParameter("cf"); // cf RP o DRP
        
        if(StringUtils.isBlank(standardProvider) || StringUtils.isBlank(loa) || StringUtils.isBlank(cf))
          return mapping.findForward("form");

        utente.setCodiceFiscale(cf);

        AccountSSO accountSSO = new AccountSSO();
        accountSSO.setStandardProvider(standardProvider);
        accountSSO.setCurrentLoa(Integer.parseInt(loa));

        request.getSession().setAttribute(CostantiGenerali.ID_ATTRIBUTO_SESSIONE_ACCOUNT_SSO, accountSSO);
        request.setAttribute("risultato", "OK");
        
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setCodEvento("SUBNPA_UP");
        logEvento.setLivEvento(1);
        logEvento.setDescr("ADMIN - RP "+cf+ " - "+standardProvider+" - "+ loa);
        LogEventiUtils.insertLogEventi(logEvento);

      }catch(Exception e) {
        request.setAttribute("risultato", "KO");
      }
    }else {
      request.setAttribute("risultato", "unauthorized");
    }
    
    //gestire log eventi?

    return mapping.findForward("form");
  }
}

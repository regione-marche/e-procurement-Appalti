/*
 * Created on 13/12/2022
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
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;

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

public class CollegaScollegaRdaAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(CollegaScollegaRdaAction.class);

  private GestioneProgrammazioneManager            gestioneProgrammazioneManager;
  
  public void setGestioneProgrammazioneManager(GestioneProgrammazioneManager gestioneProgrammazioneManager) {
    this.gestioneProgrammazioneManager = gestioneProgrammazioneManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    
    String target = "success";
    String messageKey = null;
    if (logger.isDebugEnabled()) logger.debug("CollegaScollegaRDAAction: inizio metodo");

    String codgar = request.getParameter("codgar");
    String ngara = request.getParameter("ngara");
   
    if(StringUtils.isNotEmpty(codgar)) {   
      HttpSession session = request.getSession();
      ProfiloUtente profilo = (ProfiloUtente) session.getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String uffintGara = (String)  session.getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
      
      String arrRda = request.getParameter("arrRda");
      
      arrRda = UtilityStringhe.convertiNullInStringaVuota(arrRda);
      String[] rda = null;
      if(!"".equals(arrRda)) {
        arrRda = arrRda.substring(0, arrRda.length()-1); 
        rda = arrRda.split(";");
         
        try {
          String handleRda = request.getParameter("handleRda");
        
          if(StringUtils.isNotEmpty(handleRda) && "collega".equals(handleRda)) {
            gestioneProgrammazioneManager.collegaRda(codgar, rda, uffintGara, syscon,request);
          }
          if(StringUtils.isNotEmpty(handleRda) && "collegalotto".equals(handleRda)) {
            gestioneProgrammazioneManager.collegalottoRda(codgar, ngara, rda,request);
          }
          if(StringUtils.isNotEmpty(handleRda) && "scollegalotto".equals(handleRda)) {
            gestioneProgrammazioneManager.scollegalottoRda(codgar, ngara, rda,request);
          }else if(StringUtils.isNotEmpty(handleRda) && "scollega".equals(handleRda)) {
            gestioneProgrammazioneManager.scollegaRda(codgar, rda,request);
          }
          request.setAttribute("backToScollega", true);
        }catch (GestoreException e) {
          
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "integrazioneprogrammazione.collegascollegaRda.remote.error";
          String error = "Errore nell'aggiornamento delle Rda/Rdi collegate";
          if(e.getCause()!=null) { 
            if(e.getCause() instanceof NotFoundException)
              error = GestioneProgrammazioneManager.NOT_FOUND_EXCEPTION;
            else if(e.getCause() instanceof SQLException)
              error = GestioneProgrammazioneManager.SQL_EXCEPTION;
            else if(e.getCause() instanceof ProcessingException)
              error = GestioneProgrammazioneManager.NOT_FOUND_EXCEPTION;
          }
          this.aggiungiMessaggio(request, messageKey,error);
          logger.error(this.resBundleGenerale.getString(messageKey), e);
        }
      }   
    }
    if (logger.isDebugEnabled()) logger.debug("CollegaScollegaRDAAction: fine metodo");
    return mapping.findForward(target);

  }
}

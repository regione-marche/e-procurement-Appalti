/*
 * Created on 09/12/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RichiestaRequisitiAction extends ActionBaseNoOpzioni {

  protected static final String           FORWARD_SUCCESS = "richiestareqsuccess";
  protected static final String           FORWARD_ERROR   = "richiestareqerror";

  static Logger                           logger          = Logger.getLogger(RichiestaRequisitiAction.class);

  private GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager;

  public void setGestioneServiziIDGARACIGManager(
      GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager) {
    this.gestioneServiziIDGARACIGManager = gestioneServiziIDGARACIGManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("RichiestaRequisitiAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    boolean rpntFailed = false;
    String errore = null;
    String evento = "INVIO_REQUISITI";
    String messaggioBaseResponse = "Risposta SIMOG comunicazione requisiti";
    String riferimento = "";
    Long numgara = null;
    
    try {
      String entita = request.getParameter("entita");
      numgara = new Long(request.getParameter("numgara"));
      String idgara = request.getParameter("idgara");
      String recuperaUser = request.getParameter("recuperauser");
      String recuperaPassword = request.getParameter("recuperapassword");
      String rpntFailedString = request.getParameter("rpntFailed");
      if("1".equals(rpntFailedString)) {
        rpntFailed=true;
      }
      
      String codrup = request.getParameter("codrup");
      String simogwsuser = null;
      String simogwspass = null;

      // Leggo le eventuali credenziali memorizzate
      HashMap<String, String> hMapSIMOGWSUserPass = new HashMap<String, String>();
      hMapSIMOGWSUserPass = this.gestioneServiziIDGARACIGManager.recuperaSIMOGWSUserPass(codrup);

      // Gestione USER
      if (recuperaUser != null && "1".equals(recuperaUser) && rpntFailed) {
        simogwsuser = ((String) hMapSIMOGWSUserPass.get("simogwsuser"));
      } else {
        simogwsuser = request.getParameter("simogwsuser");
      }

      // Gestione PASSWORD
      if (recuperaPassword != null && "1".equals(recuperaPassword) && rpntFailed) {
        simogwspass = ((String) hMapSIMOGWSUserPass.get("simogwspass"));
      } else {
        simogwspass = request.getParameter("simogwspass");
      }

      // Invio al web service
      if ("W3GARAREQ".equals(entita)) {
        List<?> datiW3GARA = this.gestioneServiziIDGARACIGManager.getSqlManager().getVector("select id_gara, codgar from w3gara where numgara = ?", new Object[] { numgara }); 
        String idgaraDB = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 0).getValue();
        String codgar = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 1).getValue();
        riferimento = " (N.gara "+idgaraDB;
        if(StringUtils.stripToNull(codgar)!=null)
          riferimento += ", rif.gara "+codgar.replace("$","");
        riferimento += ")";
        
        GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 0, 1, numgara.toString(), evento, "Invio richiesta a SIMOG comunicazione requisiti"+riferimento, null);    
        idgara = this.gestioneServiziIDGARACIGManager.richiestaRequisiti(simogwsuser,
            simogwspass, numgara, idgara,rpntFailed);
        
        GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, numgara.toString(), evento, messaggioBaseResponse+riferimento, null);    
      }

      target = FORWARD_SUCCESS;
      request.getSession().setAttribute("entita", entita);
      request.getSession().setAttribute("numgara", numgara);
      request.getSession().setAttribute("idgara", idgara);
      request.getSession().setAttribute("numeroPopUp", "1");
      request.getSession().removeAttribute("erroreInvioRichiestaSimog");
    } catch (GestoreException e) {
      errore = e.getMessage();
      target = FORWARD_ERROR;
      messageKey = "errors.gestioneIDGARACIG.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
      if(rpntFailed) {
        request.setAttribute("erroreInvioRichiestaSimog", "true");
      }else {
        if(e.getMessage().indexOf(GestioneServiziIDGARACIGManager.LOGIN_SIMOG_ERRATA)>-1 && !rpntFailed) {
          request.getSession().setAttribute("erroreCredenzialiRPNT", "true");
        }else {
          if(e.getMessage().indexOf(GestioneServiziIDGARACIGManager.RUP_NON_PRESENTE_O_SENZA_COLL)>-1 && !rpntFailed) {
            request.setAttribute("erroreCredenzialiRPNT", "true");
          }
          else {
            request.setAttribute("erroreInvioRichiestaSimog", "true");
          }
        }
      }
    } catch (Throwable e) {
      errore = e.getMessage();
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }
    if(errore!=null)
      GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 3, numgara.toString(), evento, messaggioBaseResponse+riferimento, errore); 

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled())
      logger.debug("RichiestaRequisitiAction: fine metodo");

    return mapping.findForward(target);

  }

}

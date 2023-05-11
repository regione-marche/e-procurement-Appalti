/*
 * Created on 15/feb/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ConsultaGaraLottoAction extends ActionBaseNoOpzioni {

  protected static final String           FORWARD_SUCCESS = "consultagaralottosuccess";
  protected static final String           FORWARD_ERROR   = "consultagaralottoerror";

  static Logger                           logger          = Logger.getLogger(ConsultaGaraLottoAction.class);

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
      logger.debug("ConsultaGaraLottoAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    boolean rpntFailed = false;

    try {
      String cig = request.getParameter("cig");
      String recuperaUser = request.getParameter("recuperauser");
      String recuperaPassword = request.getParameter("recuperapassword");
      String rpntFailedString = request.getParameter("rpntFailed");
      if("1".equals(rpntFailedString)) {
        rpntFailed=true;
      }

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());

      String codUffInt = (String) request.getSession().getAttribute(
  	        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
    
      String codrup = request.getParameter("codrup");
      String simogwsuser = null;
      String simogwspass = null;

      // Leggo le eventuali credenziali memorizzate
      HashMap<String, String> hMapSIMOGWSUserPass = new HashMap<String, String>();
      hMapSIMOGWSUserPass = this.gestioneServiziIDGARACIGManager.recuperaSIMOGWSUserPass(codrup);

      // Gestione USER
      if (recuperaUser != null && "1".equals(recuperaUser)) {
        simogwsuser = (hMapSIMOGWSUserPass.get("simogwsuser"));
      } else {
        simogwsuser = request.getParameter("simogwsuser");
      }

      // Gestione PASSWORD
      if (recuperaPassword != null && "1".equals(recuperaPassword)) {
        simogwspass = (hMapSIMOGWSUserPass.get("simogwspass"));
      } else {
        simogwspass = request.getParameter("simogwspass");
      }

      // Invio al web service
      HashMap<String, Object> hMapConsultaGaraLotto = new HashMap<String, Object>();
      hMapConsultaGaraLotto = this.gestioneServiziIDGARACIGManager.consultaGARALOTTO(simogwsuser, simogwspass, cig, syscon, codUffInt, codrup,rpntFailed);

      target = FORWARD_SUCCESS;
      request.getSession().setAttribute("w3gara_esistente", (hMapConsultaGaraLotto.get("w3gara_esistente")));
      request.getSession().setAttribute("w3gara_id_gara", (hMapConsultaGaraLotto.get("w3gara_id_gara")));
      request.getSession().setAttribute("w3gara_numgara", (hMapConsultaGaraLotto.get("w3gara_numgara")));
      request.getSession().setAttribute("w3gara_oggetto", (hMapConsultaGaraLotto.get("w3gara_oggetto")));
      request.getSession().setAttribute("w3lott_numlott", (hMapConsultaGaraLotto.get("w3lott_numlott")));
      request.getSession().setAttribute("w3lott_cig", cig);
      request.getSession().setAttribute("w3lott_oggetto", (hMapConsultaGaraLotto.get("w3lott_oggetto")));
      request.getSession().setAttribute("w3gara_provv_presa_carico", (hMapConsultaGaraLotto.get("w3gara_provv_presa_carico")));
      request.getSession().setAttribute("w3gara_escluso_avcpass", (hMapConsultaGaraLotto.get("w3gara_escluso_avcpass")));
      //smartcig
      request.getSession().setAttribute("w3smartcig_esistente", (hMapConsultaGaraLotto.get("w3smartcig_esistente")));
      request.getSession().setAttribute("w3smartcig_numgara", (hMapConsultaGaraLotto.get("w3smartcig_numgara")));
      request.getSession().setAttribute("w3smartcig_oggetto", (hMapConsultaGaraLotto.get("w3smartcig_oggetto")));
    } catch (GestoreException e) {
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
      target = FORWARD_ERROR;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled())
      logger.debug("RichiestaIDGARACIGAction: fine metodo");

    return mapping.findForward(target);

  }

}

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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AnagraficaSimogManager;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;
import it.eldasoft.utils.utility.UtilityDate;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RichiestaIDGARACIGAction extends ActionBaseNoOpzioni {

  protected static final String           FORWARD_SUCCESS = "richiestaidgaracigsuccess";
  protected static final String           FORWARD_COLLEGAMENTO = "collegaidgaracigsuccess";
  protected static final String           FORWARD_ERROR   = "richiestaidgaracigerror";

  static Logger                           logger          = Logger.getLogger(RichiestaIDGARACIGAction.class);

  private GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager;
  
  private AnagraficaSimogManager anagraficaSimogManager;

  public void setGestioneServiziIDGARACIGManager(
      GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager) {
    this.gestioneServiziIDGARACIGManager = gestioneServiziIDGARACIGManager;
  }

  public void setAnagraficaSimogManager(
		  AnagraficaSimogManager anagraficaSimogManager) {
	    this.anagraficaSimogManager = anagraficaSimogManager;
  }
  
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("RichiestaIDGARACIGAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    boolean rpntFailed = false;
    try {
      String entita = request.getParameter("entita");
      Long numgara = new Long(request.getParameter("numgara"));
      Long numlott = new Long(request.getParameter("numlott"));
      String recuperaUser = request.getParameter("recuperauser");
      String recuperaPassword = request.getParameter("recuperapassword");
      String memorizza = request.getParameter("memorizza");
      String rpntFailedString = request.getParameter("rpntFailed");
      if("1".equals(rpntFailedString)) {
        rpntFailed=true;
      }
        

      //APPALTI-1061
      String inviaConCigNonPresenti = request.getParameter("inviaConCigNonPresenti");
      String codiceCig =request.getParameter("codiceCig");
      String dataCreazioneLotto = request.getParameter("dataCreazioneLotto"); 
      //APPALTI-1061 fine
      
      String idgara = null;
      String cig = null;
      //APPALTI-1061
      request.getSession().removeAttribute("collegaCig");
      if("W3LOTT".equals(entita) && codiceCig!=null && !"".equals(codiceCig)) {
        cig=codiceCig;
        Date dataCreazione = UtilityDate.convertiData(dataCreazioneLotto, UtilityDate.FORMATO_GG_MM_AAAA);
        this.gestioneServiziIDGARACIGManager.aggiornaW3LOTTCIGManuale(numgara, numlott, cig, dataCreazione);
        this.anagraficaSimogManager.setGaraLotto("G", numgara, numlott, null, cig, dataCreazione);
        request.getSession().setAttribute("collegaCig", true);
      }else { //APPALTI-1061 fine
  
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
       
        if ("W3GARA".equals(entita)) {
          idgara = this.gestioneServiziIDGARACIGManager.richiestaIDGARA(simogwsuser,
              simogwspass, numgara, rpntFailed);
          if(idgara!=null) {
          	this.anagraficaSimogManager.setGaraLotto("T", numgara, null, idgara, null, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
          }
        } else if ("W3LOTT".equals(entita)){
          //APPALTI-1061: verifico se il numero di cig in locale è pari al numero di cig su SIMOG: se negativo, sanare la situazione
          HashMap<String, Object> mappaCigNonPresenti = this.gestioneServiziIDGARACIGManager.consultaLottiGara(simogwsuser, simogwspass, numgara,rpntFailed);
          if(mappaCigNonPresenti.size()>0 && !"1".equals(inviaConCigNonPresenti)) {
              request.getSession().setAttribute("cigNonPresenti", mappaCigNonPresenti);
              return mapping.findForward(FORWARD_ERROR);
          }//APPALTI-1061 fine
          cig = this.gestioneServiziIDGARACIGManager.richiestaCIG(simogwsuser,
              simogwspass, numgara, numlott,rpntFailed);
           if(cig!=null) {
            	this.anagraficaSimogManager.setGaraLotto("G", numgara, numlott, null, cig, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
           }
        } else if ("W3SMARTCIG".equals(entita)){
          cig = this.gestioneServiziIDGARACIGManager.richiestaSMARTCIG(simogwsuser,
              simogwspass, numgara,rpntFailed);
          if(cig!=null) {
          	this.anagraficaSimogManager.setGaraLotto("S", numgara, numlott, null, cig, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
          }
        }

        target = FORWARD_SUCCESS;
      }
      
      request.getSession().setAttribute("entita", entita);
      request.getSession().setAttribute("numgara", numgara);
      request.getSession().setAttribute("numlott", numlott);
      request.getSession().setAttribute("idgara", idgara);
      request.getSession().setAttribute("cig", cig);
      request.getSession().setAttribute("numeroPopUp", "1");
      request.getSession().removeAttribute("erroreInvioRichiestaSimog");
      request.getSession().removeAttribute("cigNonPresenti");
     
    } catch (GestoreException e) {
      target = FORWARD_ERROR;
      messageKey = "errors.gestioneIDGARACIG.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
      if(rpntFailed) {
        request.setAttribute("erroreInvioRichiestaSimog", "true");
      }else {
        if(e.getMessage().indexOf(GestioneServiziIDGARACIGManager.LOGIN_SIMOG_ERRATA)>-1) {
          request.getSession().setAttribute("erroreCredenzialiRPNT", "true");
        }
        else {
          request.setAttribute("erroreCredenzialiRPNT", "true");
        }
      }
    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
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

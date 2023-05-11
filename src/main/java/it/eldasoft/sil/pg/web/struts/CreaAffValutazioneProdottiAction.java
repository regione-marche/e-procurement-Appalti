package it.eldasoft.sil.pg.web.struts;


import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AtacManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class CreaAffValutazioneProdottiAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_ERROR  = "affvalproderror";
  protected static final String FORWARD_SUCCESS = "affvalprodsuccess";
  protected static final String FORWARD_LISTA = "affvalprodlista";

  static Logger                 logger          = Logger.getLogger(CreaAffValutazioneProdottiAction.class);

  private AtacManager            atacManager;

  public void setAtacManager(AtacManager atacManager) {
    this.atacManager = atacManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("CreaAffValutazioneProdotti: inizio metodo");

    String target = FORWARD_ERROR;
    String messageKey = null;
    String modo=request.getParameter("modo");
    String seguen=null;
    String dittao=null;
    String uffint=null;
    if(modo!= null && "2".equals(modo)) {
        seguen=new String(request.getParameter("seguen"));
        uffint=new String(request.getParameter("uffint"));
    }else {
        seguen=new String(request.getParameter("seguen"));
        dittao=new String(request.getParameter("dittao"));
        uffint=new String(request.getParameter("uffint"));
    }
    
    try {
    	
        ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        Long syscon = Long.valueOf(profilo.getId());
        
        if(modo!= null && "2".equals(modo)) {
        	String[] listaAffDaCreare = request.getParameterValues("keys");
            if (listaAffDaCreare != null) {
            	Boolean isAffidamentiCreati=true;
                for (int i = 0; i < listaAffDaCreare.length; i++) {
                	String dittao_i = listaAffDaCreare[i];
                	int k = atacManager.insAffidamentoValutazioneProdotti(syscon,seguen, dittao_i, uffint);
                	if(k<0) {
                		isAffidamentiCreati=false;
                	}
                }
                target = FORWARD_LISTA;
                if(isAffidamentiCreati) {
                	request.setAttribute("affidamentiCreati", "OK");
                }else {
                	request.setAttribute("affidamentiCreati", "KO");
                }
    	        messageKey = "errors.creaaffvp.error";
    	        this.aggiungiMessaggio(request, messageKey);
            }
        }else {
    		atacManager.insAffidamentoValutazioneProdotti(syscon,seguen, dittao, uffint);
    		target = FORWARD_SUCCESS;
        }
		
	} catch (GestoreException e) {
		messageKey = "errors.creaaffvp.error";
		target = FORWARD_ERROR;
	}

    if (messageKey != null) response.reset();
    if (logger.isDebugEnabled())
      logger.debug("CreaAffValutazioneProdotti: fine metodo");

    return mapping.findForward(target);

  }

}

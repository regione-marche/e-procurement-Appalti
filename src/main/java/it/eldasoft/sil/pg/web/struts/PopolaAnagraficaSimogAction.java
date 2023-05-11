/*
 * 	Created on 03/06/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.AnagraficaSimogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class PopolaAnagraficaSimogAction extends ActionBaseNoOpzioni {

  protected static final String    FORWARD_CIG_SUCCESS           = "anagraficacigsuccess";
  protected static final String    FORWARD_SMARTCIG_SUCCESS      = "anagraficasmartcigsuccess";
  protected static final String    FORWARD_ERROR             = "anagraficasimogerror";
  protected static final String    FORWARD_ERRORI_BLOCCANTI         = "anagraficasimogerroribloccanti";
  protected static final String    FORWARD_ERRORI_NON_BLOCCANTI     = "anagraficasimogerrorinonbloccanti";
  protected static final String    FORWARD_BACK             = "anagraficasimogback";

  static Logger                    logger            = Logger.getLogger(PopolaAnagraficaSimogAction.class);

  private AnagraficaSimogManager  anagraficaSimogManager;

  public void setAnagraficaSimogManager(AnagraficaSimogManager anagraficaSimogManager) {
    this.anagraficaSimogManager = anagraficaSimogManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("PopolaAnagraficaSimogAction: inizio metodo");

    String target = FORWARD_ERROR;
    String messageKey = null;

    String codgar = request.getParameter("codiceGara");
    String genere = request.getParameter("genereGara");
    String numeroLotto = request.getParameter("numeroLotto");
    String modalita = request.getParameter("modalita");
    modalita = StringUtils.stripToEmpty(modalita);
    String tiporichiesta = request.getParameter("tiporichiesta");
    String tipooperazione = request.getParameter("tipooperazione");
    String idStipula = request.getParameter("idStipula");
    
    //eventuale ritorno dopo un invio a simog: non dev aggiornare da gare
    String invioSimogEseguito = (String) request.getSession().getAttribute("invioSimogEseguito");
    invioSimogEseguito = StringUtils.stripToEmpty(invioSimogEseguito);
    if("OK".equals(invioSimogEseguito)) {
    	tipooperazione = "NOUPD";
    }

    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 1;
    String errMsgEvento = "";
    String codiceEvento ="";
    String descrEv = "Invio dei dati di gara per richiesta ";
      if("Cig".equals(tiporichiesta)){
        descrEv+= "CIG";
        codiceEvento="GA_RICHIESTA_CIG";
      }else{
        descrEv+= "smartCIG";
        codiceEvento="GA_RICHIESTA_SMARTCIG";
      }

    try{

      try {
        //Le operazioni da gestire sono:
        if(!"DEL".equals(tipooperazione)){
          HashMap<String,Object> result = new HashMap<String,Object>();
          if("Cig".equals(tiporichiesta) && !"NOUPD".equals(tipooperazione)) {
              result = null;
              if(idStipula != null && !"".equals(idStipula))
            	  result = anagraficaSimogManager.controllaDatiRichiestaCIGCollegato(idStipula, request.getSession());
              else
            	  result = anagraficaSimogManager.controllaDatiRichiestaCIG(codgar, numeroLotto, genere, request.getSession());
          }else{
        	  if("Scig".equals(tiporichiesta) && !"NOUPD".equals(tipooperazione)) {
                  //Smart CIG
                result =null;
                if(idStipula != null && !"".equals(idStipula))
              	    result = anagraficaSimogManager.controllaDatiRichiestaSmartCIGCollegato(idStipula, request.getSession());
                else
                	result = anagraficaSimogManager.controllaDatiRichiestaSmartCIG(codgar, numeroLotto, request.getSession());
        	  }
          }
          ArrayList<String> erroriBloccanti = new ArrayList<String>();
          if(!"NOUPD".equals(tipooperazione)) {
        	  erroriBloccanti = (ArrayList<String>) result.get("erroriBloccanti");  
          }
          ArrayList<String> erroriNonBloccanti = new ArrayList<String>();
          if(!"NOUPD".equals(tipooperazione)) {
        	  erroriNonBloccanti = (ArrayList<String>) result.get("erroriNonBloccanti");  
          }          
          if((erroriBloccanti.size() == 0 && erroriNonBloccanti.size() == 0) || modalita.equals("COMPLETA") )  {//BEST CASE---ACCEDO ALLA SCHEDA
            String esitoWS = null;
            String key = null;
            //recupero la chiave e apro la scheda
            if("Cig".equals(tiporichiesta)) {
            	if(idStipula != null && !"".equals(idStipula)) {
            		codgar = anagraficaSimogManager.creaAggGaraCigCollegato(idStipula, request);
            	}
            	HashMap<String, Object> hm = anagraficaSimogManager.setAnagraficaCig(codgar, genere, tipooperazione,idStipula);
            	String tipoRichiesta = (String) hm.get("tipoRichiesta");
                String id = (String) hm.get("key");
                if("C".equals(tipoRichiesta)) {
                	key="W3GARA.NUMGARA=N:" + id;	
                	target = FORWARD_CIG_SUCCESS;
                }
            }else {
            	if("Scig".equals(tiporichiesta)) {
                	if(idStipula != null && !"".equals(idStipula)) {
                		codgar = anagraficaSimogManager.creaAggGaraCigCollegato(idStipula, request);
                	}
                	HashMap<String, Object> hm = anagraficaSimogManager.setAnagraficaSmartCig(codgar, genere, tipooperazione,idStipula);
                    String tipoRichiesta = (String) hm.get("tipoRichiesta");
                    String id = (String) hm.get("key");
                    if("S".equals(tipoRichiesta)) {
                    	key="W3SMARTCIG.CODRICH=N:" + id;
                    	target = FORWARD_SMARTCIG_SUCCESS;
                    }

            	}
            }
            
            request.setAttribute("key", key);
            esitoWS = "true";
            request.setAttribute("esitoWS", esitoWS);
          }else{
            if(erroriBloccanti.size() == 0){
              //CAMBIARE QUESTO FORWARD NELLA PAGINA CHE CHIEDE ALL'UTENTE SE VUOLE PROSEGUIRE
              request.setAttribute("erroriNonBloccanti", erroriNonBloccanti);
              //request.setAttribute("genere",genere);
              //request.setAttribute("numeroLotto",numeroLotto);
              request.setAttribute("tiporichiesta",tiporichiesta);
              request.setAttribute("tipooperazione",tipooperazione);
              target = FORWARD_ERRORI_NON_BLOCCANTI;
            }else{
              //CAMBIARE QUESTO FORWARD NULLA PAGINA CHE ELENCA GLI ERRORI E NON FA PROSEGUIRE
              request.setAttribute("erroriNonBloccanti", erroriNonBloccanti);
              request.setAttribute("erroriBloccanti", erroriBloccanti);
              request.setAttribute("tiporichiesta",tiporichiesta);
              request.setAttribute("tipooperazione",tipooperazione);
              target = FORWARD_ERRORI_BLOCCANTI;
            }
          }

        }else{
        	;//DEL
        	if("Cig".equals(tiporichiesta)){
            	if(idStipula != null && !"".equals(idStipula)) {
            		codgar = anagraficaSimogManager.creaAggGaraCigCollegato(idStipula, request);
            	}
            	HashMap<String, Object> hm = anagraficaSimogManager.setAnagraficaCig(codgar, genere, tipooperazione,idStipula);
        	}else {
        		if("Scig".equals(tiporichiesta)){
                	if(idStipula != null && !"".equals(idStipula)) {
                		codgar = anagraficaSimogManager.creaAggGaraCigCollegato(idStipula, request);
                	}
        			HashMap<String, Object> hm = anagraficaSimogManager.setAnagraficaSmartCig(codgar, genere, tipooperazione,idStipula);	
        		}
        	}
            request.setAttribute("tipooperazione",tipooperazione);
            target = FORWARD_BACK;
        	
        }

      } catch (Exception e) {
        livEvento = 3;
        target = FORWARD_ERROR;
        messageKey = "errors.inviadatirichiestacig.error";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey, e.getMessage());
        errMsgEvento= this.resBundleGenerale.getString(messageKey) + ":" + e.getMessage();
      } catch (Throwable t) {
        livEvento = 3;
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.applicazione.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), t);
        this.aggiungiMessaggio(request, messageKey);
        errMsgEvento= this.resBundleGenerale.getString(messageKey) + ":" + t.getMessage();
      }
    }finally{

      //Tracciatura eventi solo per le operazioni di richiesta
      try {
        if(false){
          String chiave=numeroLotto;
          if(!"2".equals(genere))
            chiave=codgar;
          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(chiave);
          logEvento.setCodEvento(codiceEvento);
          logEvento.setDescr(descrEv);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        }
      } catch (Exception le) {
        logger.error(genericMsgErr, le);
      }


      if (messageKey != null) response.reset();

      if (logger.isDebugEnabled()) logger.debug("PopolaAnagraficaSimogAction: fine metodo");

      return mapping.findForward(target);
    }
  }

}

/*
 * Created on 10/12/2010
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
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xmlbeans.XmlException;
import org.example.getElencoCarrelliResults.RetXmlGetElencoCarrelliDocument;
import org.example.getElencoCarrelliResults.RetXmlGetElencoCarrelliDocument.RetXmlGetElencoCarrelli.ElencoCarrelli.Carrello;

import pc_nicola.aur.WebServices.WsAURSoapProxy;

/**
 * Esegue la lettura dei carrelli disponibili 
 */
public class CarrelloFabbisogniAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(CarrelloFabbisogniAction.class);

  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("CarrelloFabbisogniAction: inizio metodo");

    String target = null;
    String messageKey = null;
    String urlWsAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
    String userAUR = ConfigManager.getValore("it.eldasoft.sil.pg.userAUR");
    String passwordAUR = ConfigManager.getValore("it.eldasoft.sil.pg.passwordAUR");
    //String codicesaAUR = ConfigManager.getValore("it.eldasoft.sil.pg.codicesaAUR");
    String codicesaAUR = new String(request.getParameter("codStazioneAppaltante"));
    String tipoFornituraGara = new String(request.getParameter("tipoFornituraGara"));
    
    if(userAUR== null || "".equals(userAUR)){
      logger.error("Non è valorizzata l'user AUR");
      target = "errore";
      messageKey = "errors.aur.noUser";
      this.aggiungiMessaggio(request, messageKey);
    }
    
    if(passwordAUR== null || "".equals(passwordAUR)){
      logger.error("Non è valorizzata la password AUR");
      target = "errore";
      messageKey = "errors.aur.noPassword";
      this.aggiungiMessaggio(request, messageKey);
    }
    
    if(codicesaAUR== null || "".equals(codicesaAUR)){
      logger.error("Non è valorizzata la stazione appaltante");
      target = "errore";
      messageKey = "errors.aur.noStazioneAppaltante";
      this.aggiungiMessaggio(request, messageKey);
    }
    
    if(target == null){
      WsAURSoapProxy proxy=new WsAURSoapProxy(urlWsAUR);
      StringHolder holder = new StringHolder();
      IntHolder numCarrelli = new IntHolder();
      try {
        String tipoFornitura= "";
        if("1".equals(tipoFornituraGara))
          tipoFornitura = "farmaci";
        else if("2".equals(tipoFornituraGara))
          tipoFornitura = "dispositivi";
        
        if(logger.isDebugEnabled()) {
          String log="Chiamata al servizio AUR_GetElencoCarrelli con i seguenti parametri:";
          log+=" utente=" + userAUR + "; password=" + passwordAUR + "; codiceSA=" + codicesaAUR + "; tipoFornitura=" + tipoFornitura;
          logger.debug(log);
        }
        
        long esito=proxy.AUR_GetElencoCarrelli(userAUR, passwordAUR, codicesaAUR,tipoFornitura,holder,numCarrelli);
        
        target = "success";
        if(esito==-1000106){
          logger.error("Il webservice AUR_GetElencoCarrelli non ha trovato occorrenze");
          target = "errore";
          messageKey = "errors.aur.noOccorrenze";
          this.aggiungiMessaggio(request, messageKey);
        }else if(esito<0 && esito!=-1000106){
          logger.error("Il webservice AUR_GetElencoCarrelli ha restituito il codice di errore:" + String.valueOf(esito));
          target = "errore";
          messageKey = "errors.aur.esitoWebService";
          this.aggiungiMessaggio(request, messageKey);
        }else   if(esito>0){
          logger.error("Il webservice AUR_GetElencoCarrelli ha restituito il codice di warning:" + String.valueOf(esito));
          target = "errore";
          messageKey = "warning.aur.esitoWebService";
          this.aggiungiMessaggio(request, messageKey);
        }else{
          //Visto che xml restituito contiene "RetXml" e non "RetXmlGetElencoFornitori" faccio una sostituzione nel codice
          //holder.value = holder.value.replace("RetXml", "RetXmlGetElencoCarrelli");
          
          RetXmlGetElencoCarrelliDocument document = RetXmlGetElencoCarrelliDocument.Factory.parse(holder.value);
          Carrello[] carrelli = document.getRetXmlGetElencoCarrelli().getElencoCarrelli().getCarrelloArray();
          if (carrelli!=null && carrelli.length>0){
            
            request.setAttribute("listaElencoCarrelli", Arrays.asList(carrelli));
            request.setAttribute("numeroTotaleCarrelliEstratti", new Integer(numCarrelli.value));
            target = "success";
          }
          
        }
      } catch (RemoteException e) {
          logger.error("Errore nella chiamata al web service", e);
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.aur.erroreWebService";
          this.aggiungiMessaggio(request, messageKey);
      }
      catch (XmlException e) {
        logger.error("Errore nella lettura del xml ritornato dal webservice AUR_GetElencoCarrelli", e);
        target = "errore";
        messageKey = "errors.aur.erroreXmlWebService";
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    
       
    
    if(logger.isDebugEnabled()) logger.debug("CarrelloFabbisogniAction: fine metodo");

    if(target != null)
      return mapping.findForward(target);
    else
      return null;
  }

}

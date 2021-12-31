/*
 * Created on 11/01/2011
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
import it.eldasoft.sil.pg.bl.AurManager;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.holders.StringHolder;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xmlbeans.XmlException;
import org.example.getRigheCarrelloResults.RetXmlGetRigheCarrelloDocument;
import org.example.getRigheCarrelloResults.RetXmlGetRigheCarrelloDocument.RetXmlGetRigheCarrello.RigheCarrello;

import pc_nicola.aur.WebServices.WsAURSoapProxy;

/**
 * Esegue l'inserimento delle righe del carrello selezionato da AUR 
 */
public class InserimentoCarrelloAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(InserimentoConcorrentiAction.class);
  
  
  private AurManager aurManager;
  
  public void setAurManager(AurManager aurManager){
    this.aurManager = aurManager;
}
  
    
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("InserimentoCarrelloAction: inizio metodo");

    String target = null;
    String messageKey = null;
    String urlWsAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
    String userAUR = ConfigManager.getValore("it.eldasoft.sil.pg.userAUR");
    String passwordAUR = ConfigManager.getValore("it.eldasoft.sil.pg.passwordAUR");
    //String codicesaAUR = ConfigManager.getValore("it.eldasoft.sil.pg.codicesaAUR");
    String codicesaAUR =  new String(request.getParameter("codStazioneAppaltante")); 
    
    /*
    String tipoFornituraGara = new String(request.getParameter("tipoFornituraGara"));
    String tipoFornituraCarrello = new String(request.getParameter("tipoFornituraCarrello"));
    
    //La tipologia della fornitura della gara deve coincidere con la
    //tipologia del carrello
    tipoFornituraGara = tipoFornituraGara.toUpperCase();
    tipoFornituraCarrello = tipoFornituraCarrello.toUpperCase();
    
    
    
    if(("1".equals(tipoFornituraGara) && tipoFornituraCarrello.indexOf("FARMACI")<0) ||
        ("2".equals(tipoFornituraGara) && tipoFornituraCarrello.indexOf("DISPOSITIVI")<0)){
      logger.error("Il tipo forniture della gara non coincide con il tipo forniture del carrello");
      target = "errore";
      messageKey = "errors.aur.tipologiaErrata";
      this.aggiungiMessaggio(request, messageKey);
    }else if("3".equals(tipoFornituraGara)){
      logger.error("Il tipo forniture della gara non è compatibile con la L'AUR");
      target = "errore";
      messageKey = "errors.aur.noTipologiaAUR";
      this.aggiungiMessaggio(request, messageKey);
    }
    */
    if(target == null && (userAUR== null || "".equals(userAUR))){
      logger.error("Non è valorizzato l'user AUR");
      target = "errore";
      messageKey = "errors.aur.noUser";
      this.aggiungiMessaggio(request, messageKey);
    }
    
    if(target == null && (passwordAUR== null || "".equals(passwordAUR))){
      logger.error("Non è valorizzata la password AUR");
      target = "errore";
      messageKey = "errors.aur.noPassword";
      this.aggiungiMessaggio(request, messageKey);
    }
    
    if(target == null && (codicesaAUR== null || "".equals(codicesaAUR))){
      logger.error("Non è valorizzata la stazione appaltante");
      target = "errore";
      messageKey = "errors.aur.noStazioneAppaltante";
      this.aggiungiMessaggio(request, messageKey);
    }
    
    if(target == null){
      WsAURSoapProxy proxy=new WsAURSoapProxy(urlWsAUR);
      StringHolder holder = new StringHolder();
      String codiceCarrello = new String(request.getParameter("codice"));
      String gara = new String(request.getParameter("ngara"));
      String garaLottiConOffertaUnica = new String(request.getParameter("garaLottiConOffertaUnica"));
      
      HashMap mappaDatiWebService = new HashMap();
      mappaDatiWebService.put("proxy", proxy);
      mappaDatiWebService.put("user", userAUR);
      mappaDatiWebService.put("password", passwordAUR);
      //mappaDatiWebService.put("SA", codicesaAUR);
      mappaDatiWebService.put("carrello", codiceCarrello);
      try {
        if(logger.isDebugEnabled()) {
          String log="Chiamata al servizio AUR_GetDettaglioCarrello con i seguenti parametri:";
          log+=" utente=" + userAUR + "; password=" + passwordAUR + "; codiceSA=" + codicesaAUR + "; codiceCarrello=" + codiceCarrello;
          logger.debug(log);
        }
        
        long esito=proxy.AUR_GetDettaglioCarrello( userAUR, passwordAUR, codicesaAUR, codiceCarrello, holder );
        
        if(esito<0){
          logger.error("Il webservice AUR_GetDettaglioCarrello ha restituito il codice di errore:" + String.valueOf(esito));
          target = "errore";
          messageKey = "errors.aur.esitoWebService";
          this.aggiungiMessaggio(request, messageKey);
        }
        else   if(esito>0){
          logger.error("Il webservice AUR_GetDettaglioCarrello ha restituito il codice di warning:" + String.valueOf(esito));
          target = "errore";
          messageKey = "warning.aur.esitoWebService";
          this.aggiungiMessaggio(request, messageKey);
        }else{
            RetXmlGetRigheCarrelloDocument document = RetXmlGetRigheCarrelloDocument.Factory.parse(holder.value);
            RigheCarrello righeCarrello = document.getRetXmlGetRigheCarrello().getRigheCarrello();
            aurManager.insertCarrello(righeCarrello,gara,garaLottiConOffertaUnica,mappaDatiWebService);
            request.setAttribute("RISULTATO", "OK");                
            target = "success";
        }
         
        
        
      } catch (RemoteException e) {
          logger.error("Errore nella chiamata al web service AUR_GetDettaglioCarrello", e);
          target = "errore";
          messageKey = "errors.aur.erroreWebService";
          this.aggiungiMessaggio(request, messageKey);
      }catch (XmlException e) {
        logger.error("Errore nella lettura del xml ritornato dal webservice AUR_GetDettaglioCarrello", e);
        target = "errore";
        messageKey = "errors.aur.erroreXmlWebService";
        this.aggiungiMessaggio(request, messageKey);
      }
      
      catch (SQLException e) {
        target = "errore";
        messageKey = "errors.aur.InserimentoDettaglioCarrello";
        this.aggiungiMessaggio(request, messageKey);
      } 
      
    }
    
        
    
    if(logger.isDebugEnabled()) logger.debug("InserimentoCarrelloAction: fine metodo");

    if(target != null)
      return mapping.findForward(target);
    else
      return null;
  }
  
  
  
}

/*
 * Created on 210/12/2010
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.holders.StringHolder;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xmlbeans.XmlException;
import org.example.getElencoFornitoriResults.DettaglioFornitore;
import org.example.getElencoFornitoriResults.RetXmlGetDettaglioFornitoreDocument;

import pc_nicola.aur.WebServices.WsAURSoapProxy;

/**
 * Esegue l'inserimento dei concorrenti con i dati prelevati da AUR 
 */
public class InserimentoConcorrentiAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(InserimentoConcorrentiAction.class);
  
  
  private AurManager aurManager;
  
  public void setAurManager(AurManager aurManager){
    this.aurManager = aurManager;
}
  
    
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("InserimentoConcorrentiAction: inizio metodo");

    String target = null;
    String messageKey = null;
    String urlWsAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
    String userAUR = ConfigManager.getValore("it.eldasoft.sil.pg.userAUR");
    String passwordAUR = ConfigManager.getValore("it.eldasoft.sil.pg.passwordAUR");
    //String codicesaAUR = ConfigManager.getValore("it.eldasoft.sil.pg.codicesaAUR");
    String codicesaAUR = new String(request.getParameter("codStazioneAppaltante"));
    
    if(userAUR== null || "".equals(userAUR)){
      logger.error("Non è valorizzato l'user AUR");
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
      String codiceFornitore = new String(request.getParameter("codice"));
      String gara = new String(request.getParameter("ngara"));
      String garaLottiConOffertaUnica = new String(request.getParameter("garaLottiConOffertaUnica"));
      String numeroFaseAttiva = new String(request.getParameter("numeroFaseAttiva"));
      
      try {
        if(logger.isDebugEnabled()) {
          String log="Chiamata al servizio AUR_GetDettaglioFornitore con i seguenti parametri:";
          log+=" utente=" + userAUR + "; password=" + passwordAUR + "; codiceSA=" + codicesaAUR + "; codiceFornitore=" + codiceFornitore;
          logger.debug(log);
        }
        long esito=proxy.AUR_GetDettaglioFornitore( userAUR, passwordAUR, codicesaAUR, codiceFornitore, holder );
        
        if(esito<0){
          logger.error("Il webservice AUR_GetDettaglioFornitore ha restituito il codice di errore:" + String.valueOf(esito));
          target = "errore";
          messageKey = "errors.aur.esitoWebService";
          this.aggiungiMessaggio(request, messageKey);
        }
        else   if(esito>0){
          logger.error("Il webservice AUR_GetDettaglioFornitore ha restituito il codice di warning:" + String.valueOf(esito));
          target = "errore";
          messageKey = "warning.aur.esitoWebService";
          this.aggiungiMessaggio(request, messageKey);
        }else{
            RetXmlGetDettaglioFornitoreDocument document = RetXmlGetDettaglioFornitoreDocument.Factory.parse(holder.value);
            DettaglioFornitore fornitore = document.getRetXmlGetDettaglioFornitore().getDettaglioFornitore();
            Long numFaseAttiva=null;
            if (numeroFaseAttiva!=null && !"".equals(numeroFaseAttiva))
              numFaseAttiva = new Long(numeroFaseAttiva);
            aurManager.insertConcorrenti(fornitore,gara,garaLottiConOffertaUnica,numFaseAttiva);
            target = "success";
            request.setAttribute("RISULTATO", "OK");
            
          
        }
         
        
        
      } catch (RemoteException e) {
          logger.error("Errore nella chiamata al web service AUR_GetDettaglioFornitore", e);
          target = "errore";
          messageKey = "errors.aur.erroreWebService";
          this.aggiungiMessaggio(request, messageKey);
      }
      catch (XmlException e) {
        logger.error("Errore nella lettura del xml ritornato dal webservice AUR_GetDettaglioFornitore", e);
        target = "errore";
        messageKey = "errors.aur.erroreXmlWebService";
        this.aggiungiMessaggio(request, messageKey);
      } 
      catch (SQLException e) {
        target = "errore";
        messageKey = "errors.aur.InserimentoConcorrente";
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    
        
    
    if(logger.isDebugEnabled()) logger.debug("InserimentoConcorrentiAction: fine metodo");

    if(target != null)
      return mapping.findForward(target);
    else
      return null;
  }
  
  
  
}

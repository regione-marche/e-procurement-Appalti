/*
 * Created on 21/12/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
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
import org.example.getElencoFornitoriResults.Fornitore;
import org.example.getElencoFornitoriResults.RetXmlGetElencoFornitoriDocument;

import pc_nicola.aur.WebServices.WsAURSoapProxy;


/**
 * Esegue le operazioni necessarie per leggere l'elenco fornitori da AUR 
 */
public class ElencoFornitoriAction extends DispatchActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(ElencoFornitoriAction.class);

  public ActionForward initTrovaDitta(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("initTrovaDitta: inizio metodo");
    
    TrovaConcorrentiAURForm trovaConcorrentiAUR = new TrovaConcorrentiAURForm();
    
    request.setAttribute("trovaConcorrentiAUR", trovaConcorrentiAUR);
    
    String target = CostantiGeneraliStruts.FORWARD_OK.concat("InitTrova");
    // lista per il popolamento della comboBox dei valori della combobox di
    // confronto fra stringhe
    request.setAttribute("listaValueConfrontoStringa",
        GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA);
    // lista per il popolamento della comboBox dei testi della combobox di
    // confronto fra stringhe
    request.setAttribute("listaTextConfrontoStringa",
        GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA);
    
    String ngara = new String(request.getParameter("ngara"));
    String garaLottiConOffertaUnica = new String(request.getParameter("garaLottiConOffertaUnica"));
    String numeroFaseAttiva = new String(request.getParameter("numeroFaseAttiva"));
    String codStazioneAppaltante = new String(request.getParameter("codStazioneAppaltante")); 
    
    request.setAttribute("ngara",ngara);
    
    request.setAttribute("garaLottiConOffertaUnica",garaLottiConOffertaUnica);
    
    request.setAttribute("numeroFaseAttiva",numeroFaseAttiva);
    
    request.setAttribute("codStazioneAppaltante",codStazioneAppaltante);
    
    if(logger.isDebugEnabled()) logger.debug("initTrovaDitta: fine metodo");
    return mapping.findForward(target);
  }
  
  
  
  public ActionForward trovaDitta(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("trovaDitta: inizio metodo");
    
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
      TrovaConcorrentiAURForm trovaConcorrentiAURForm = (TrovaConcorrentiAURForm) form;
      
      String i_NAME1 = request.getParameter("i_NAME1");
      String codiceFiscale = request.getParameter("i_STCD1");
      String partitaIVA = request.getParameter("i_STCD2");
      String ragSoc= null;
      String CF = null;
      String PI = null;
      
      if(i_NAME1 != null && i_NAME1.length() > 0){
        ragSoc = i_NAME1;
      } else {
        if(trovaConcorrentiAURForm.getI_NAME1() != null && trovaConcorrentiAURForm.getI_NAME1().length() > 0)
          ragSoc = trovaConcorrentiAURForm.getI_NAME1();
      }
      
      if(codiceFiscale != null && codiceFiscale.length() > 0){
        CF= codiceFiscale;
      } else {
        if(trovaConcorrentiAURForm.getI_STCD1() != null && trovaConcorrentiAURForm.getI_STCD1().length() > 0)
          CF = trovaConcorrentiAURForm.getI_STCD1();
      }
      
      if(partitaIVA != null && partitaIVA.length() > 0){
        PI = partitaIVA;
      } else {
        if(trovaConcorrentiAURForm.getI_STCD2() != null && trovaConcorrentiAURForm.getI_STCD2().length() > 0)
          PI = trovaConcorrentiAURForm.getI_STCD2();
      }
      
      WsAURSoapProxy proxy=new WsAURSoapProxy(urlWsAUR);
      StringHolder holder = new StringHolder();
      IntHolder numfornitori = new IntHolder();
      try {
        if(logger.isDebugEnabled()) {
          String log="Chiamata al servizio AUR_GetElencoFornitori con i seguenti parametri:";
          log+=" utente=" + userAUR + "; password=" + passwordAUR + "; codiceSA=" + codicesaAUR + "; ragSoc=";
          if(ragSoc!= null)
            log+= ragSoc;
          else
            log+= "null";
          log+= " ;codiceFornitore= null; CF=";
          if(CF!= null)
            log+= CF;
          else
            log+= "null";
          log+= " ;PI=";
          if(PI!= null)
            log+= PI;
          else
            log+= "null";
          logger.debug(log);
        }
        
        long esito=proxy.AUR_GetElencoFornitori( userAUR, passwordAUR, codicesaAUR, ragSoc, null, CF, PI, holder, numfornitori );
        //long esito=proxy.AUR_GetElencoFornitori( userAUR, passwordAUR, codicesaAUR, null, null, null, null, holder, numfornitori );
        //long esito=proxy.AUR_GetElencoFornitori( userAUR, passwordAUR, codicesaAUR, null, null, null, null, RetXMLGetElencoFornitori, numforn );
        
        if(esito==-1000106){
          logger.error("Il webservice AUR_GetElencoFornitori non ha trovato occorrenze");
          target = "errore";
          messageKey = "errors.aur.noOccorrenze";
          this.aggiungiMessaggio(request, messageKey);
        }else if(esito<0 && esito!=-1000106){
          logger.error("Il webservice AUR_GetElencoFornitori ha restituito il codice di errore:" + String.valueOf(esito));
          target = "errore";
          messageKey = "errors.aur.esitoWebService";
          this.aggiungiMessaggio(request, messageKey);
        }else   if(esito>0){
          logger.error("Il webservice AUR_GetElencoFornitori ha restituito il codice di warning:" + String.valueOf(esito));
          target = "errore";
          messageKey = "warning.aur.esitoWebService";
          this.aggiungiMessaggio(request, messageKey);
        }else{
          RetXmlGetElencoFornitoriDocument document = RetXmlGetElencoFornitoriDocument.Factory.parse(holder.value);
          Fornitore[] fornitori = document.getRetXmlGetElencoFornitori().getElencoFornitori().getFornitoreArray();
          if (fornitori!=null && fornitori.length>0){
            
            request.setAttribute("listaElencoFornitori", Arrays.asList(fornitori));
            request.setAttribute("numeroTotaleFornitoriEstratti", new Integer(numfornitori.value));
            target = CostantiGeneraliStruts.FORWARD_OK.concat("Trova");
          }
        }
         
        
        
      } catch (RemoteException e) {
          logger.error("Errore nella chiamata al web service AUR AUR_GetElencoFornitori", e);
          target = "errore";
          messageKey = "errors.aur.erroreWebService";
          this.aggiungiMessaggio(request, messageKey);
      } catch (XmlException e) {
        logger.error("Errore nella lettura del xml ritornato dal webservice AUR_GetElencoFornitori", e);
        target = "errore";
        messageKey = "errors.aur.erroreXmlWebService";
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    
        
    
    if(logger.isDebugEnabled()) logger.debug("trovaDitta: fine metodo");

    if(target != null)
      return mapping.findForward(target);
    else
      return null;
    

    
  }

  
}

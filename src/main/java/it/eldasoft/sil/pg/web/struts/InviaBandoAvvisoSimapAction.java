/*
 * 	Created on 15/nov/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.sil.pg.bl.ControllaBandoAvvisoSimapManager;
import it.eldasoft.sil.pg.bl.InviaBandoAvvisoSimapManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.sil.pg.bl.ControllaBandoAvvisoSimapManager;
import it.eldasoft.sil.pg.bl.InviaBandoAvvisoSimapManager;
import it.eldasoft.sil.pg.bl.LeggiPubblicazioniManager;
import it.eldasoft.simap.ws.EsitoSimapWS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class InviaBandoAvvisoSimapAction extends DispatchActionBaseNoOpzioni {

  protected static final String    FORWARD_SUCCESS                  = "inviabandoavvisosimapsuccess";
  protected static final String    FORWARD_ERROR                    = "inviabandoavvisosimaperror";
  protected static final String    FORWARD_ERRORI_BLOCCANTI         = "inviabandoavvisosimaperroribloccanti";
  protected static final String    FORWARD_ERRORI_NON_BLOCCANTI     = "inviabandoavvisosimaperrorinonbloccanti";

  static Logger                    logger            = Logger.getLogger(InviaBandoAvvisoSimapAction.class);

  private InviaBandoAvvisoSimapManager  inviaBandoAvvisoSimapManager;

  public void setInviaBandoAvvisoSimapManager(InviaBandoAvvisoSimapManager inviaBandoAvvisoSimapManager) {
    this.inviaBandoAvvisoSimapManager = inviaBandoAvvisoSimapManager;
  }

  private ControllaBandoAvvisoSimapManager  controllaBandoAvvisoSimapManager;

  public void setControllaBandoAvvisoSimapManager(ControllaBandoAvvisoSimapManager controllaBandoAvvisoSimapManager) {
    this.controllaBandoAvvisoSimapManager = controllaBandoAvvisoSimapManager;
  }
  
  private LeggiPubblicazioniManager  leggiPubblicazioniManager;

  public void setLeggiPubblicazioniManager(LeggiPubblicazioniManager leggiPubblicazioniManager) {
    this.leggiPubblicazioniManager = leggiPubblicazioniManager;
  }

  public ActionForward controllaEdInvia(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response){

    if (logger.isDebugEnabled()) logger.debug("InviaBandoAvvisoSimapAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;

    String codgar = request.getParameter("codgar");
    String formulario = request.getParameter("formulario");
    String sottotipoStringa = request.getParameter("tipoavviso");
    BigInteger sottotipo = null;
    if(sottotipoStringa != null && sottotipoStringa.length() > 0){sottotipo = new BigInteger(sottotipoStringa);}
    String credenziali = request.getParameter("credenziali");
    String iterga = request.getParameter("iterga");
    String username = null;
    String password = null;

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    boolean autenticazioneSSO = profiloUtente.isAutenticazioneSSO();

    if ("CORRENTI".equals(credenziali) || autenticazioneSSO) {


      username = profiloUtente.getLogin();
      String passwordCifrata = profiloUtente.getPwd();
      if (passwordCifrata != null) {
        ICriptazioneByte decriptatore;
        try {
          decriptatore = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
              passwordCifrata.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
          password = new String(decriptatore.getDatoNonCifrato());
        } catch (CriptazioneException e) {
          logger.error(
              this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
        }
      }
    } else if ("ALTRE".equals(credenziali)) {
      username = request.getParameter("username");
      password = request.getParameter("password");
    }

    try {
      HashMap<String,Object> result = new HashMap<String,Object>();
      result = controllaBandoAvvisoSimapManager.controllaBandoAvvisoSimap(codgar, formulario,sottotipo, username, password);
      ArrayList<String> erroriBloccanti = (ArrayList<String>) result.get("erroriBloccanti");
      ArrayList<String> erroriNonBloccanti = (ArrayList<String>) result.get("erroriNonBloccanti");
      String xml = (String) result.get("xml");
      if(erroriBloccanti.size() == 0 && erroriNonBloccanti.size() == 0){
        //INVIO SUBITO PERCHE' NON SONO STATI TROVATI ERRORI
        String res = inviaBandoAvvisoSimapManager.inviaBandoAvvisoSimap(codgar, formulario, sottotipo, username, password);
        JSONObject jsonResult = JSONObject.fromObject(res);
        boolean esito = (Boolean) jsonResult.get("esito");
        if(esito){target = FORWARD_SUCCESS;}
        else{
          target = FORWARD_ERROR;
          String messaggio = (String) jsonResult.get("messaggio");
          logger.error("inviabandoavvisosimap ERROR : " + messaggio);
          this.aggiungiMessaggio(request, "errors.inviabandoavvisosimap.error", messaggio);
        }
      }
      else{
        if(erroriBloccanti.size() == 0){
          //CAMBIARE QUESTO FORWARD NELLA PAGINA CHE CHIEDE ALL'UTENTE SE VUOLE PROSEGUIRE
          request.setAttribute("erroriNonBloccanti", erroriNonBloccanti);
          request.setAttribute("xml", xml);
          request.setAttribute("tipoavviso",sottotipo);
          request.setAttribute("iterga",iterga);
          if (!"CORRENTI".equals(credenziali) && !autenticazioneSSO) {
          request.setAttribute("password",password);
          request.setAttribute("username",username);}
          else{
          request.setAttribute("credenziali","CORRENTI");
          }
          target = FORWARD_ERRORI_NON_BLOCCANTI;
        }else{
          //CAMBIARE QUESTO FORWARD NULLA PAGINA CHE ELENCA GLI ERRORI E NON FA PROSEGUIRE
          request.setAttribute("erroriNonBloccanti", erroriNonBloccanti);
          request.setAttribute("erroriBloccanti", erroriBloccanti);
          target = FORWARD_ERRORI_BLOCCANTI;
        }
      }

    } catch (Exception e) {
      target = FORWARD_ERROR;
      messageKey = "errors.inviabandoavvisosimap.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled()) logger.debug("InviaBandoAvvisoSimapAction: fine metodo");

    return mapping.findForward(target);
  }


  public ActionForward inviaComunque(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws Exception, Throwable{

    if (logger.isDebugEnabled()) logger.debug("InviaBandoAvvisoSimapAction: inizio metodo");

    String target = FORWARD_SUCCESS;

    String username = null;
    String password = null;
    String codgar = request.getParameter("codgar");
    String formulario = request.getParameter("formulario");
    String sottotipoStringa = request.getParameter("tipoavviso");
    BigInteger sottotipo = null;
    if(sottotipoStringa != null && sottotipoStringa.length() > 0){sottotipo = new BigInteger(sottotipoStringa);}
    String credenziali = request.getParameter("credenziali");
    String iterga = request.getParameter("iterga");

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    boolean autenticazioneSSO = profiloUtente.isAutenticazioneSSO();

    if ("CORRENTI".equals(credenziali) || autenticazioneSSO) {


      username = profiloUtente.getLogin();
      String passwordCifrata = profiloUtente.getPwd();
      if (passwordCifrata != null) {
        ICriptazioneByte decriptatore;
        try {
          decriptatore = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
              passwordCifrata.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
          password = new String(decriptatore.getDatoNonCifrato());
        } catch (CriptazioneException e) {
          logger.error(
              this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
        }
      }
    } else{
      username = request.getParameter("username");
      password = request.getParameter("password");
    }

    String res = inviaBandoAvvisoSimapManager.inviaBandoAvvisoSimap(codgar, formulario, sottotipo, username, password);
    JSONObject jsonResult = JSONObject.fromObject(res);
    boolean esito = (Boolean) jsonResult.get("esito");
    if(esito){target = FORWARD_SUCCESS;}
    else{
      target = FORWARD_ERROR;
      String messaggio = (String) jsonResult.get("messaggio");
      logger.error("inviabandoavvisosimap ERROR : " + messaggio);
      this.aggiungiMessaggio(request, "errors.inviabandoavvisosimap.error", messaggio);
    }

    /*
    if(esito.isEsito())target = FORWARD_SUCCESS;
    else {

      request.setAttribute("iterga", iterga);
      logger.error(esito.getMessaggio());
      this.aggiungiMessaggio(request, esito.getMessaggio());
    }
     */
    return mapping.findForward(target);
  }
  
  public ActionForward leggiPubblicazioni(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws Exception, Throwable{
    
    if (logger.isDebugEnabled()) logger.debug("InviaBandoAvvisoSimapAction: leggiPubblicazioni inizio metodo");
    
    String target = FORWARD_SUCCESS;
    
    String codgar = request.getParameter("codgar");
    JSONArray ja = this.leggiPubblicazioniManager.leggiPubblicazioni(codgar);
    
    ArrayList<ArrayList> pubblicazioni = new ArrayList<ArrayList>();
    
    if(ja.size()>0){
      target = "leggipubblicazionisuccess";
      request.setAttribute("numeroPubblicazioni", ja.size());
      
      for(int i=0; i<ja.size(); i++){
        ArrayList<Object> pubblicazione = new ArrayList<Object>();
        JSONObject jsonOb = ja.getJSONObject(i);
        String noticeNumber = " ";
        noticeNumber = (String) jsonOb.get("notice_number_oj");
        String noticeDate = (String) jsonOb.get("notice_date");
        String link = (String) jsonOb.get("ted_links");
        String formStr = (String) jsonOb.get("form");
        String tipoBando = " ";
        String datOj = (String) jsonOb.get("date_oj");
        tipoBando = (String) jsonOb.get("descrizione");
        
        request.setAttribute("tipoBando", tipoBando);
        String temp = " ";
        if(datOj != null){
          Date dateTemp = (Date) new SimpleDateFormat("yyyyMMdd").parse(datOj);
          DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
          temp = df.format(dateTemp);
        }
        pubblicazione.ensureCapacity(3);
        pubblicazione.add(0,temp);
        pubblicazione.add(1,tipoBando);
        pubblicazione.add(2,noticeNumber);
        pubblicazioni.add(pubblicazione);
      }
      request.setAttribute("pubblicazioni", pubblicazioni);
    }
    else{
      target = "leggipubblicazionisuccess";
    }
    return mapping.findForward(target);
  }
  

}

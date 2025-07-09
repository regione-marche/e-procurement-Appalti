/*
 * Created on 16/11/2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts.npa;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.npa.ScambioDatiPCPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author manuel.bridda
 *
 */
public class ScambioDatiPCPAction extends DispatchActionAjaxLogged {

  static Logger               logger = Logger.getLogger(ScambioDatiPCPAction.class);
  
  private static final Object PROP_CLIENT_TIMEOUT       = 60000;
  
  public static final String SERVIZIO          = "servizio";
  private static final String CHECKWSHEALTH     = "CHECK_WS_HEALTH";
  public static final String CREAPPALTO        = "CREA_APPALTO";
  public static final String VERIFICAAPPALTO   = "VERIFICA_APPALTO";
  public static final String ESITOOPERAZIONE   = "ESITO_OPERAZIONE";
  public static final String CONFERMAPPALTO    = "CONFERMA_APPALTO";
  public static final String STATOAPPALTO      = "STATO_APPALTO";
  public static final String RECUPERACIG       = "RECUPERA_CIG";
  public static final String PUBBLICAAVVISO    = "PUBBLICA_AVVISO";
  public static final String CANCELLAAPPALTO   = "CANCELLA_APPALTO";
  public static final String CANCELLAAVVISO    = "CANCELLA_AVVISO";
  public static final String AGGIUNGISOGGETTO   = "AGGIUNGI_SOGGETTO";
  public static final String PRESACARICO        = "PRESA_CARICO";
  public static final String ELIMINASOGGETTO    = "ELIMINA_SOGGETTO";
  public static final String CREASCHEDA        = "CREA_SCHEDA";
  public static final String VERIFICASCHEDA    = "VERIFICA_SCHEDA";
  public static final String CONFERMASCHEDA    = "CONFERMA_SCHEDA";
  public static final String CANCELLASCHEDA    = "CANCELLA_SCHEDA";
  public static final String CONSULTASCHEDA    = "CONSULTA_SCHEDA";
  public static final String STATOAVVISO       = "STATO_AVVISO";
  public static final String CONSULTAAVVISO    = "CONSULTA_AVVISO";
  public static final String CREAPIANO         = "CREA_PIANO";
  public static final String VERIFICAPIANO     = "VERIFICA_PIANO";
  public static final String CANCELLAPIANO     = "CANCELLA_PIANO";
  public static final String CONFERMAPIANO     = "CONFERMA_PIANO";
  public static final String CONSULTAPIANO     = "CONSULTA_PIANO";
  public static final String CENTRICOSTO       = "CENTRI_COSTO";
  public static final String RICERCAAPPALTO    = "RICERCA_APPALTO";
  public static final String STARTAUTOUPDATE   = "AVVIA_AUTOUPDATE";
  public static final String STOPAUTOUPDATE    = "FERMA_AUTOUPDATE";
  public static final String RICHIESTAACCESSOFVOE = "RICHIESTA_ACCESSO_FVOE";
  public static final String VERIFICARICHIESTAACCESSOFVOE = "VERIFICA_RICHIESTA_ACCESSO_FVOE";
  public static final String RICERCADOCUMENTIFVOE = "RICERCA_DOCUMENTI_FVOE";
  public static final String RICHIEDIDOCUMENTIFVOE = "RICHIEDI_DOCUMENTI_FVOE";
  public static final String VERIFICASTATORICHIESTADOCFVOE = "VERIFICA_RICHIESTA_DOCUM_FVOE";
  public static final String RECUPERADOCUMENTOFVOE = "RECUPERA_DOCUMENTO_FVOE";
  //non cablate
  public static final String CONSULTAAPPALTO       = "CONSULTA_APPALTO";
  public static final String MODIFICAAPPALTO       = "MODIFICA_APPALTO";
  public static final String MODIFICASCHEDA        = "MODIFICA_SCHEDA";
  public static final String MODIFICAAVVISO        = "MODIFICA_AVVISO";
  public static final String RETTIFICAAVVISO       = "RETTIFICA_AVVISO";
  
  private ScambioDatiPCPManager            scambioDatiPCPManager;
  
  public void setScambioDatiPCPManager(ScambioDatiPCPManager scambioDatiPCPManager) {
    this.scambioDatiPCPManager = scambioDatiPCPManager;
  }

  public ActionForward getHealthStatus(final ActionMapping mapping,
      final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception{
    
    JSONObject result = new JSONObject();
    PrintWriter out = response.getWriter();

    result = wrapperChiamate(request,CHECKWSHEALTH,false,null,null);

    out.print(result);
    out.flush();

    return null;
  }

  public ActionForward gestioneMRP(final ActionMapping mapping,
                                       final ActionForm form, final HttpServletRequest request,
                                       final HttpServletResponse response) throws Exception{

    JSONObject result = new JSONObject();
    PrintWriter out = response.getWriter();

    Long idFormScheda = Long.parseLong(request.getParameter(ScambioDatiPCPManager.ID_FORM));
    String abilitazione = request.getParameter(ScambioDatiPCPManager.ABILITAZIONE_MRP);

    result=scambioDatiPCPManager.updateAbilitazioneMRP(idFormScheda,abilitazione);

    String codgar = request.getParameter("codiceGara");

    LogEvento le = LogEventiUtils.createLogEvento(request);
    le.setCodEvento("NPA_MRP_ABILITA_SCHEDA");
    le.setLivEvento(1);
    le.setOggEvento(codgar);
    le.setDescr(("true".equals(abilitazione)? "Abilitazione":"Disabilitazione")+" della scheda con id. "+idFormScheda+" alla gestione tramite M-RP");

    String status= (String) result.get("status");
    if(status == null) {
      le.setLivEvento(3);
      le.setErrmsg((String) result.get(ScambioDatiPCPManager.PG_ERRORS));
    }
    LogEventiUtils.insertLogEventi(le);

    out.print(result);
    out.flush();

    return null;
  }
  
  public ActionForward gestioneAppalto(final ActionMapping mapping,
      final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception{
    
    JSONObject result;

    String servizio = request.getParameter(SERVIZIO);
    result = wrapperChiamate(request,servizio,false,null,response);

    if(RECUPERADOCUMENTOFVOE.equals(servizio)) {
      if (result.has("file")) {
        OutputStream out = response.getOutputStream();
        String nomeFile = result.getString("nomeFile");
        String file = result.getString("file");
        byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(file);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + nomeFile + "\"");
        response.setHeader("X-Filename", nomeFile);
        response.setContentLength(bytes.length);
        out.write(bytes);
        out.flush();
        out.close();
      } else{
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write(result.toString());
        logger.error(result.toString());
      }
    }else{
      PrintWriter out = response.getWriter();
      out.print(result);
      out.flush();
      out.close();
    }

    return null;
  }
  
  public ActionForward recuperaCentriCosto(final ActionMapping mapping,
      final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception{
    
    JSONObject result = new JSONObject();

    result = wrapperChiamate(request,CENTRICOSTO,false,null,null);
    
    JSONObject listaCCJson = new JSONObject();
    JSONArray centriJson = new JSONArray();

    if(result.has("done") && "Y".equals(result.getString("done"))){
      try {
        JSONArray items = result.getJSONObject("data").getJSONArray("items");
        request.setAttribute("cfUtilizzato", result.getString("cfUtilizzato"));
        request.setAttribute("codausaUtilizzato", result.getString("codausaUtilizzato"));
        request.setAttribute("fittizioANAC", result.getString("fittizioANAC"));
        for(int i=0; i<items.size();i++) {
          JSONObject stazioneAppaltante = items.getJSONObject(i).getJSONObject("scheda").getJSONObject("stazioneAppaltante");
          if("ATTIVO".equals(stazioneAppaltante.getJSONObject("statoAUSA").get("stato"))){//filtro per le sole stazioni appaltanti attive
            JSONArray centriDiCosto = stazioneAppaltante.getJSONArray("centriDiCosto");
            for(int j=0; j<centriDiCosto.size();j++) {
              JSONObject centro = centriDiCosto.getJSONObject(j);
              if("ATTIVO".equals(centro.getJSONObject("stato").getString("stato"))) {
                String denominazioneCentroDiCosto = centro.getString("denominazioneCentroDiCosto");
                String idCentroDiCosto = centro.getString("idCentroDiCosto");
                //vogliamo la versione json per le datatables
                JSONObject centroJson = new JSONObject();
                centroJson.put("denominazione", denominazioneCentroDiCosto);
                centroJson.put("id", idCentroDiCosto);
                centriJson.add(centroJson);
              }
            }
          }
        }
      }
    catch(Exception e) {
      logger.error("Errore inaspettato durante l'esecuzione della chiamata: "+e);
    }
      listaCCJson.put("centri", centriJson);
      request.setAttribute("listaCCJson", listaCCJson);
    }else {
      if(result.has("pgMessage"))
        request.setAttribute("erroriConfigurazione", result);
      else
        request.setAttribute("jsonConErrori", result);
    }
    
    request.setAttribute("RISULTATO", "OK");

    return mapping.findForward("result");
  }
  

  public JSONObject wrapperChiamate(HttpServletRequest request, String servizio, boolean isFromPannelloSupporto, HashMap<String,String> parametersMRP, HttpServletResponse response) {
    
      String messageKey = null;
      String error = null;
      JSONObject result = new JSONObject();
      Client client = ClientBuilder.newClient();
      client.property(ClientProperties.CONNECT_TIMEOUT, PROP_CLIENT_TIMEOUT);
      Map<String,Object> map = new HashMap<String,Object>();

      map.put(SERVIZIO, servizio);

      boolean isFVOE = RICHIESTAACCESSOFVOE.equals(servizio) || VERIFICARICHIESTAACCESSOFVOE.equals(servizio) || RICERCADOCUMENTIFVOE.equals(servizio)
              || RECUPERADOCUMENTOFVOE.equals(servizio) || RICHIEDIDOCUMENTIFVOE.equals(servizio) || VERIFICASTATORICHIESTADOCFVOE.equals(servizio);

      //lettura dati dalla request. Si sovrascrive (in quanto assenti) in caso di MRP
      String codgar=request.getParameter("codiceGara");
      String modalitaVerifica = request.getParameter(ScambioDatiPCPManager.MODALITA_INVIO);
      String idFormString = request.getParameter(ScambioDatiPCPManager.ID_FORM);
      String motivo = StringUtils.stripToNull(request.getParameter(ScambioDatiPCPManager.BODY_MOTIVO));//per le cancellazioni
      String tipoOperazione= request.getParameter(ScambioDatiPCPManager.TIPO_OPERAZIONE);
      String tipoRicerca= request.getParameter(ScambioDatiPCPManager.TIPO_RICERCA);
      String esito= StringUtils.stripToNull(request.getParameter(ScambioDatiPCPManager.ESITO));
      String codein= request.getParameter(ScambioDatiPCPManager.CODEIN);//no MRP
      String modalita= request.getParameter("modalita");//no MRP
      //String cig= request.getParameter(ScambioDatiPCPManager.CIG);, non utilizzato
      //String idContratto= request.getParameter(ScambioDatiPCPManager.ID_CONTRATTO);, non utilizzato

    if(parametersMRP!=null){
          codgar=parametersMRP.get("codiceGara");
          idFormString=parametersMRP.get(ScambioDatiPCPManager.ID_FORM);
          motivo=parametersMRP.get(ScambioDatiPCPManager.BODY_MOTIVO);
          tipoOperazione=parametersMRP.get(ScambioDatiPCPManager.TIPO_OPERAZIONE);
          tipoRicerca=parametersMRP.get(ScambioDatiPCPManager.TIPO_RICERCA);
          esito=parametersMRP.get(ScambioDatiPCPManager.ESITO);
      }
      

      if(ScambioDatiPCPManager.MODALITA_VERIFICA.equals(modalitaVerifica) || CENTRICOSTO.equals(servizio)) {
        if(ScambioDatiPCPManager.MODALITA_VERIFICA.equals(modalitaVerifica))
          map.put(ScambioDatiPCPManager.MODALITA_INVIO, ScambioDatiPCPManager.MODALITA_VERIFICA);
        else
          map.put(ScambioDatiPCPManager.MODALITA_INVIO, ScambioDatiPCPManager.MODALITA_RECUPERO_AUSA);
      }
      else {
        if(!isFromPannelloSupporto){
          LogEvento logEventoREQ = LogEventiUtils.createLogEvento(request);
          logEventoREQ.setCodEvento("NPA_REQ_"+servizio);
          logEventoREQ.setLivEvento(1);
          logEventoREQ.setOggEvento(codgar);
          logEventoREQ.setDescr("Invio richiesta ad ANAC - "+servizio);
		  if(parametersMRP!=null){
			  logEventoREQ.setCodProfilo("MRP");
			  map.put("cfUtenteMRP",parametersMRP.get("cfUtente"));
              map.put("loaMRP",parametersMRP.get("loa"));
              map.put("idpMRP",parametersMRP.get("idp"));
              map.put("scadenzaSessione",parametersMRP.get("scadenzaSessione"));
              map.put(ScambioDatiPCPManager.NOME_SCHEDA,parametersMRP.get(ScambioDatiPCPManager.NOME_SCHEDA));
		  }
          map.put(ScambioDatiPCPManager.LOG_EVENTO_REQ, logEventoREQ);
        }else{
          map.put(ScambioDatiPCPManager.SKIP_SCRITTURA_DB,true);
        }
      }
      
      try {
        map.put(ScambioDatiPCPManager.CLIENT, client);
        map.put(ScambioDatiPCPManager.SESSION, request.getSession());

        if(StringUtils.isNotBlank(idFormString)) {
          Integer idForm= Integer.parseInt(idFormString);
          map.put(ScambioDatiPCPManager.ID_FORM, idForm);
        }

        if(isFVOE) {
          map.put(ScambioDatiPCPManager.CODGAR, request.getParameter(ScambioDatiPCPManager.CODGAR));
          map.put(ScambioDatiPCPManager.CODIMP, request.getParameter(ScambioDatiPCPManager.CODIMP));
          map.put(ScambioDatiPCPManager.IDFVOE, request.getParameter(ScambioDatiPCPManager.IDFVOE));
        }

        if (parametersMRP!=null){
          scambioDatiPCPManager.wrapperGestisciConFEU(request, map, result);
        }

        switch (servizio) {
        case CHECKWSHEALTH:
          map.put(ScambioDatiPCPManager.MODALITA_INVIO,ScambioDatiPCPManager.MODALITA_VERIFICA);
          result = scambioDatiPCPManager.checkWSHealth(map);
          break;
        case CREAPPALTO:
          result = scambioDatiPCPManager.serviceCreaAppalto(map);
          break;
        case VERIFICAAPPALTO:
          result = scambioDatiPCPManager.serviceVerificaAppalto(map);
          break;
        case ESITOOPERAZIONE:
          map.put(ScambioDatiPCPManager.TIPO_OPERAZIONE,tipoOperazione);
          map.put(ScambioDatiPCPManager.TIPO_RICERCA,tipoRicerca);
          map.put(ScambioDatiPCPManager.ESITO,esito);

          result = scambioDatiPCPManager.serviceEsitoOperazione(map);
          break;
        case CONFERMAPPALTO:
          result = scambioDatiPCPManager.serviceConfermaAppalto(map);
          break;
        case STATOAPPALTO:
          //map.put(ScambioDatiPCPManager.CIG,cig);
          //map.put(ScambioDatiPCPManager.ID_CONTRATTO,idContratto);
          result = scambioDatiPCPManager.serviceStatoAppalto(map);
          break;
        case RECUPERACIG:
          result = scambioDatiPCPManager.serviceRecuperaCIG(map);
          break;
        case PUBBLICAAVVISO:
          result = scambioDatiPCPManager.servicePubblicaAvviso(map);
          break;
        case CANCELLAAPPALTO:
          map.put(ScambioDatiPCPManager.BODY_MOTIVO,motivo);
          result = scambioDatiPCPManager.serviceCancellaAppalto(map);
          break;
        case CANCELLAAVVISO:
          map.put(ScambioDatiPCPManager.BODY_MOTIVO,motivo);
          result = scambioDatiPCPManager.serviceCancellaAvviso(map);
          break;
        case AGGIUNGISOGGETTO:
          result = scambioDatiPCPManager.serviceAggiungiSoggetto(map);
          break;
        case CREASCHEDA:
          result = scambioDatiPCPManager.serviceCreaScheda(map);
          break;
        case VERIFICASCHEDA:
          result = scambioDatiPCPManager.serviceVerificaScheda(map);
          break;
        case CONFERMASCHEDA:
          result = scambioDatiPCPManager.serviceConfermaScheda(map);
          break;
        case CANCELLASCHEDA:
          map.put(ScambioDatiPCPManager.BODY_MOTIVO,motivo);
          result = scambioDatiPCPManager.serviceCancellaScheda(map);
          break;
        case CONSULTASCHEDA:
          result = scambioDatiPCPManager.serviceConsultaScheda(map);
          break;
        case STATOAVVISO:
          result = scambioDatiPCPManager.serviceStatoAvviso(map);
          break;
        case CONSULTAAVVISO:
          result = scambioDatiPCPManager.serviceConsultaAvviso(map);
          break;
        /*case CONSULTAAPPALTO:
          result = scambioDatiPCPManager.serviceConsultaAppalto(map);
          break;*/
        /*case MODIFICAAPPALTO:
          result = scambioDatiPCPManager.serviceConsultaAppalto(map);
          break;*/
        /*case MODIFICASCHEDA:
          result = scambioDatiPCPManager.serviceModificaScheda(map);
          break;*/
        /*case MODIFICAAVVISO:
          result = scambioDatiPCPManager.serviceModificaAvviso(map);
          break;*/
        case RETTIFICAAVVISO:
          //map.put(ScambioDatiPCPManager.TIPO_OPERAZIONE,tipoOperazione); (placeholder)
          result = scambioDatiPCPManager.serviceRettificaAvviso(map);
          break;
        /*case PRESACARICO:
          result = scambioDatiPCPManager.servicePresaCarico(map);
          break;*/
        /*case ELIMINASOGGETTO:
          result = scambioDatiPCPManager.serviceEliminaSoggetto(map);
          break; */
        case CREAPIANO:
          result = scambioDatiPCPManager.serviceCreaPiano(map);
          break;
        case VERIFICAPIANO:
          result = scambioDatiPCPManager.serviceVerificaPiano(map);
          break;
        case CANCELLAPIANO:
          map.put(ScambioDatiPCPManager.BODY_MOTIVO,motivo);
          result = scambioDatiPCPManager.serviceCancellaPiano(map);
          break;
        case CONFERMAPIANO:
          result = scambioDatiPCPManager.serviceConfermaPiano(map);
          break;
        case CONSULTAPIANO:
          result = scambioDatiPCPManager.serviceConsultaPiano(map);
          break;
        case CENTRICOSTO:
          map.put(ScambioDatiPCPManager.CODEIN, codein);
          map.put("altrisog", modalita);
          result = scambioDatiPCPManager.serviceAusaGetByCodAusa(map);
          break;
        case RICERCAAPPALTO:
          result = scambioDatiPCPManager.serviceRicercaAppalto(map);
          break;
        case STARTAUTOUPDATE:
          map.put(ScambioDatiPCPManager.AUTOUPDATE,"start");
          result = scambioDatiPCPManager.serviceHandleAutoupdate(map);
          break;
        case STOPAUTOUPDATE:
          map.put(ScambioDatiPCPManager.AUTOUPDATE,"stop");
          result = scambioDatiPCPManager.serviceHandleAutoupdate(map);
          break;
        case RICHIESTAACCESSOFVOE:
          result= scambioDatiPCPManager.serviceRichiestaAccessoFVOE(map);
          break;
        case VERIFICARICHIESTAACCESSOFVOE:
          result= scambioDatiPCPManager.serviceVerificaRichiestaAccessoFVOE(map);
          break;
        case RICERCADOCUMENTIFVOE:
          result=scambioDatiPCPManager.serviceRicercaDocumentiFVOE(map);
          break;
        case RICHIEDIDOCUMENTIFVOE:
          map.put("codice", request.getParameter("codice"));
          map.put("matricolaINPSFVOEdoc", request.getParameter("matricolaINPS"));
          map.put("annoFVOEdoc", request.getParameter("anno"));
          map.put("tipoDocumentoOEFVOEdoc", request.getParameter("tipoDocumentoOE"));
          map.put("dettaglioDocumentoOEOEdoc", request.getParameter("dettaglioDocumentoOE"));
          map.put("nomeFVOEdoc", request.getParameter("nome"));
          map.put("cognomeFVOEdoc", request.getParameter("cognome"));
          map.put("sessoFVOEdoc", request.getParameter("sesso"));
          map.put("cfFVOEdoc", request.getParameter("cf"));
          map.put("giornoFVOEdoc", request.getParameter("giorno"));
          map.put("meseFVOEdoc", request.getParameter("mese"));
          map.put("codiceStatoFVOEdoc", request.getParameter("codiceStato"));
          map.put("codiceComuneFVOEdoc", request.getParameter("codiceComune"));
          map.put("linguaTedescaFVOEdoc", request.getParameter("linguaTedesca"));
          map.put("indirizzoFVOEdoc", request.getParameter("indirizzo"));
          map.put("formaGiuridicaFVOEDOC", request.getParameter("formaGiuridica"));
          map.put("prefetturaFVOEDOC", request.getParameter("prefettura"));
          map.put("altriSogFVOEDOC", request.getParameter("altrisog"));
          result=scambioDatiPCPManager.serviceRichiediDocumentiFVOE(map);
          break;
        case VERIFICASTATORICHIESTADOCFVOE:
          result=scambioDatiPCPManager.serviceStatoRichiestaDocumentoFVOE(map);
          break;
        case RECUPERADOCUMENTOFVOE:
          result=scambioDatiPCPManager.serviceRecuperaDocumentoFVOE(map);
            break;
        }
        
        client.close();

        if (parametersMRP!=null){
          scambioDatiPCPManager.wrapperGestisciConFEU(request, map, result);
        }

      } catch (Exception e) {
        messageKey = "errors.scambiodatiPCP.error";
        if(e instanceof MessageBodyProviderNotFoundException) {
          String keycloakBaseUrl = ConfigManager.getValore("keycloak.realms.baseurl");
          String keycloakRealm = ConfigManager.getValore("keycloak.progettoappalti.subpath");
          if(StringUtils.isBlank(keycloakBaseUrl) || StringUtils.isBlank(keycloakRealm))
            messageKey="errors.gestoreException.*.scambiodatiPCP.ws.redirectLoginGateway";
          else
            messageKey="errors.gestoreException.*.scambiodatiPCP.ws.badResponseGateway";
        }
        error = UtilityStringhe.replaceParametriMessageBundle(resBundleGenerale.getString(messageKey),new String[] {e.getMessage()});
        logger.error(e);
        //this.aggiungiMessaggio(request, messageKey, e.getMessage());
      } catch (Throwable t) {
        messageKey = "errors.applicazione.inaspettataException";
        error=this.resBundleGenerale.getString(messageKey);
        logger.error(error, t);
        //this.aggiungiMessaggio(request, messageKey);
      }
      if(error!=null)
        result.put("pgErrors",error);
      
      if(isFromPannelloSupporto){
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setCodEvento("NPA_CONSULTA_DA_PANNELLO");
        logEvento.setLivEvento(1);
        logEvento.setOggEvento(codgar);
        logEvento.setDescr("Risposta ANAC - "+servizio);
        scambioDatiPCPManager.enrichLogevento(logEvento, map);
        logEvento.setErrmsg(result.toString());
        LogEventiUtils.insertLogEventi(logEvento);
      } else {
        if (!(ScambioDatiPCPManager.MODALITA_VERIFICA.equals(modalitaVerifica) || CENTRICOSTO.equals(servizio) || STARTAUTOUPDATE.equals(servizio) || STOPAUTOUPDATE.equals(servizio))){

          try {
            if (map.get(ScambioDatiPCPManager.ID_FORM) != null) {
              result.put("timestamp", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis())));
              if(RICERCAAPPALTO.equals(servizio) && !result.getJSONObject("data").containsKey("idAppalto"))
                scambioDatiPCPManager.updateUltimaResponse(null, map.get(ScambioDatiPCPManager.ID_FORM));
              else
                scambioDatiPCPManager.updateUltimaResponse(result.toString(), map.get(ScambioDatiPCPManager.ID_FORM));
            }
          } catch (SQLException e) {
            logger.error("updateUltimaResponse: " + e.getMessage());
          }

          String nomeFile = null;
          byte[] file = null;

          JSONObject resultToPrint = JSONObject.fromObject(result);
          //pulisco l'oggetto dai campi che non devono essere mostrati nella logeventi
          if(RECUPERADOCUMENTOFVOE.equals(servizio)){
            resultToPrint.remove("nomeFile");
            resultToPrint.remove("file");
          }

          LogEvento logEventoRES = LogEventiUtils.createLogEvento(request);
          logEventoRES.setCodEvento("NPA_RES_" + servizio);
          logEventoRES.setLivEvento(1);
          logEventoRES.setOggEvento(codgar);
          logEventoRES.setDescr("Risposta ANAC - " + servizio);
          logEventoRES.setErrmsg(resultToPrint.toString());
		  if(parametersMRP!=null){
			  logEventoRES.setCodProfilo("MRP");
			  map.put("cfUtenteMRP",parametersMRP.get("cfUtente"));
			}
          if(!(CHECKWSHEALTH.equals(servizio) || isFVOE))
            scambioDatiPCPManager.enrichLogevento(logEventoRES, map);
          if(isFVOE && !RICHIESTAACCESSOFVOE.equals(servizio) && !VERIFICARICHIESTAACCESSOFVOE.equals(servizio) && !VERIFICASTATORICHIESTADOCFVOE.equals(servizio)) {
            scambioDatiPCPManager.enrichLogEventoFVOE(logEventoRES, (String) map.get(ScambioDatiPCPManager.CODIMP));
          }
          LogEventiUtils.insertLogEventi(logEventoRES);
        }
      }

      return result;
  }

  public ActionForward gestionePannelloPCP(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    ProfiloUtente utente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    if(utente.getId()==48) {
      try {
        String codgar=request.getParameter(ScambioDatiPCPManager.CODGAR);
        String idForm=request.getParameter(ScambioDatiPCPManager.ID_FORM);
        String update=request.getParameter("update");
        String leggi=request.getParameter("leggi");
        String servizio = request.getParameter(SERVIZIO);

        if(StringUtils.isBlank(codgar) || StringUtils.isBlank(idForm))
          request.setAttribute("risultato", "Impossibile proseguire: riferimenti a codgar e idform mancanti");
        else {
          if ("true".equals(update)) {
            scambioDatiPCPManager.updateFromPannelloPCP(request, codgar, idForm);
            request.setAttribute("risultato", "OK");
          }
          if("true".equals(leggi)){
            PrintWriter out = response.getWriter();
            out.print(wrapperChiamate(request,servizio,true,null,null));
            out.flush();
            return null;
          }
          request.setAttribute("statonpaScheda", scambioDatiPCPManager.getStatoScheda(Long.parseLong(idForm)));
          request.setAttribute("DRPProcedura",scambioDatiPCPManager.getDRPProcedura(codgar));
        }
      } catch(Exception e) {
        logger.error("updateFromPannelloPCP: "+e.getMessage());
        request.setAttribute("risultato", "Errore nell'aggiornamento: "+ e.getMessage());
      }
    }else {
      request.setAttribute("risultato", "Impossibile proseguire: l'utente non dispone dei permessi necessari");
    }

    return mapping.findForward("pannello");
  }
}

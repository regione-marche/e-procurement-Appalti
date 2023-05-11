package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ScpManager;
import it.eldasoft.sil.pg.bl.scp.LoginResult;
import it.eldasoft.sil.pg.bl.scp.PubblicaAvvisoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicazioneAttoResult;
import it.eldasoft.sil.pg.bl.scp.PubblicaAttoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicazioneResult;
import it.eldasoft.sil.pg.bl.scp.ValidateEntry;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per eseguire la pubblicazione di una pubblicazione(Atti ex art.29) .
 */
public class InviaAttiScpAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(InviaAttiScpAction.class);

	private ScpManager scpManager;
	
	public void setScpManager(ScpManager scpManager) {
		this.scpManager = scpManager;
	}
	
	/**
	 * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String target = CostantiGeneraliStruts.FORWARD_OK;
		
		String codGara = null;
		String ngara = request.getParameter("ngara");
		String codiga = null;
		Long tipologia = null;
		String genere = null;
		String message = "";
		String errorEsito = "ok";
        genere = request.getParameter("genere");
        codGara = request.getParameter("codgar");
		String entita;
		String codEvento;
        String descrEv;
        
        if (logger.isDebugEnabled()) logger.debug("InviaAttiScpAction: inizio metodo");
        
        String oggettoEvento = codGara;
        if("2".equals(genere) && ngara != null && !"".equals(ngara)){
          oggettoEvento = ngara;
        }
        
		if ("1".equals(genere) || "2".equals(genere) || "3".equals(genere)) {
		  entita = "pubblicazioni";
		  codEvento = "GA_ATTO_SCP";
		  descrEv = "Invio dati atto a SCP";
		}else{
		  entita = "avvisi";
		  codEvento = "GA_AVVISO_SCP";
		  descrEv = "Invio dati avviso a SCP";
		  oggettoEvento = ngara;
		}
		String token = "";
		int esito;
		String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
	    int livEvento = 1;
	    String errMsgEvento = "";
	    
	    String url = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_URL);
	    
		try {
			// Indirizzo web service
		    Response accesso = scpManager.getLogin(entita);
		    esito = accesso.getStatus();
		    boolean formatoOk = true;
            if(!accesso.getMediaType().isCompatible(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)){
              formatoOk = false;
              throw new GestoreException(
                  "Verificare l'url per la connessione al servizio di pubblicazione", "errors.invioAttiScp.errorResponse");
            }
		    LoginResult resultAccesso = accesso.readEntity(LoginResult.class);
		    if (resultAccesso.isEsito() && formatoOk) {
		    	token = resultAccesso.getToken();
		    		if (entita.equals("pubblicazioni")) {
		    		  ArrayList<HashMap<String,Object>> tipologie = scpManager.getAttiDaInviare(codGara,genere);
		    		  for(int i = 0;i<tipologie.size();i++){
		    		    HashMap<String,Object> map = tipologie.get(i);
		    		    tipologia = (Long) map.get("tipologia");
		    		    ngara = (String) map.get("ngara");
		    		    codiga = (String) map.get("codiga");
		    		    String nomeTipologia = scpManager.getNomeFromTipologia(tipologia);
                        descrEv="Invio dati atto a SCP: " + nomeTipologia;
                        if(!"2".equals(genere) && ngara != null && !"".equals(ngara)){
                          descrEv+= " per il lotto " + ngara;
                        }
		    		    PubblicaAttoEntry pubblicazione = new PubblicaAttoEntry();
						scpManager.valorizzaAtto(pubblicazione, codGara, ngara, tipologia, genere);
						Client client = ClientBuilder.newClient();
						client = ClientBuilder.newBuilder().register(PubblicazioneAttoResult.class).build();
						WebTarget webTarget = client.target(url).path("Atti/Pubblica").queryParam("token", token).queryParam("modalitaInvio", "2");
						Response risultato = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(pubblicazione), Response.class);
						if(!risultato.getMediaType().isCompatible(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)){
			              throw new GestoreException(
			                  "Verificare l'url per la connessione al servizio di pubblicazione", "errors.invioAttiScp.errorResponse");
			            }
						esito = risultato.getStatus();
						PubblicazioneAttoResult risultatoPubblicazione = risultato.readEntity(PubblicazioneAttoResult.class);
						switch (esito) {
							case 200:
								//inserimento flusso
								pubblicazione.setIdRicevuto(risultatoPubblicazione.getIdExArt29());
								pubblicazione.getGara().setIdRicevuto(risultatoPubblicazione.getIdGara());
								scpManager.setUUID(pubblicazione, codGara, ngara, tipologia, genere);
								request.setAttribute("esito", errorEsito);
								livEvento = 1;
								errMsgEvento = "";
								break;
							case 400:
								String messageKey = "errors.invioAttiScp.errorResponse";
								ArrayList<ValidateEntry> validate = (ArrayList) risultatoPubblicazione.getValidate();
								message += "<br>Errore nell'invio dell'atto '" + nomeTipologia + "'";
								errMsgEvento = "Errore nell'invio dell'atto '" + nomeTipologia + "'";
								if(!"2".equals(genere) && ngara!= null && !"".equals(ngara)){
								  message += " per il lotto " + codiga;
								  errMsgEvento += " per il lotto " + codiga;
								}
								message+= ":";
								errMsgEvento+= ":";
								 for(int n = 0;n<validate.size();n++){
								   if("E".equals(validate.get(n).getTipo())){
    								   message += "<br>" + validate.get(n).getNome() + ": " + validate.get(n).getMessaggio();
    								   errMsgEvento += validate.get(n).getNome() + ": " + validate.get(n).getMessaggio() + "; ";
								   }
								 }
								 message += "<br>";
								 livEvento = 3;
								 errorEsito = "ko";
								 request.setAttribute("esito", errorEsito);
								 request.setAttribute("validate", message);
								break;
							default:
							    errorEsito = "ko";
							    request.setAttribute("esito", errorEsito);
								break;
						}
		                //Tracciatura eventi
	                    try {
	                      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
	                      logEvento.setLivEvento(livEvento);
	                      logEvento.setOggEvento(oggettoEvento);
	                      logEvento.setCodEvento(codEvento);
	                      logEvento.setDescr(descrEv);
	                      logEvento.setErrmsg(errMsgEvento);
	                      LogEventiUtils.insertLogEventi(logEvento);
	                    } catch (Exception le) {
	                      logger.error(genericMsgErr, le);
	                    }
		    		  }
			    	} 
		    		else if (entita.equals("avvisi")) {
		    		    PubblicaAvvisoEntry avviso = new PubblicaAvvisoEntry();
						scpManager.valorizzaAvviso(avviso, codGara, genere);
						Client client = ClientBuilder.newClient();
						WebTarget webTarget = client.target(url).path("Avvisi/Pubblica").queryParam("token", token).queryParam("modalitaInvio", "2");
						Response risultato = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(avviso), Response.class);
						esito = risultato.getStatus();
						PubblicazioneResult risultatoPubblicazione = risultato.readEntity(PubblicazioneResult.class);
						switch (esito) {
							case 200:
								//inserimento flusso
								avviso.setIdRicevuto(risultatoPubblicazione.getId());
								scpManager.setUUIDavviso(avviso, codGara, ngara);
								request.setAttribute("esito", errorEsito);
                                livEvento = 1;
                                errMsgEvento = "";
								break;
							case 400:
							  String messageKey = "errors.invioAttiScp.errorResponse";
                              ArrayList<ValidateEntry> validate = (ArrayList) risultatoPubblicazione.getValidate();
                              message += "<br>Errore nell'invio dell'atto dell'avviso";
                              errMsgEvento = "Errore nell'invio dell'atto dell'avviso";
                              message+= ":";
                              errMsgEvento+= ":";
                               for(int n = 0;n<validate.size();n++){
                                 if("E".equals(validate.get(n).getTipo())){
                                     message += "<br>" + validate.get(n).getNome() + ": " + validate.get(n).getMessaggio();
                                     errMsgEvento += validate.get(n).getNome() + ": " + validate.get(n).getMessaggio() + "; ";
                                 }
                               }
                               message += "<br>";
                               livEvento = 3;
                               errorEsito = "ko";
                               request.setAttribute("esito", errorEsito);
                               request.setAttribute("validate", message);
                               break;
							default:
							  errorEsito = "ko";
                              request.setAttribute("esito", errorEsito);
                              break;
						}
						//Tracciatura eventi
                        try {
                          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
                          logEvento.setLivEvento(livEvento);
                          logEvento.setOggEvento(oggettoEvento);
                          logEvento.setCodEvento(codEvento);
                          logEvento.setDescr(descrEv);
                          logEvento.setErrmsg(errMsgEvento);
                          LogEventiUtils.insertLogEventi(logEvento);
                        } catch (Exception le) {
                          logger.error(genericMsgErr, le);
                        }
			    	}

		    } else {
		    	if (resultAccesso.getError() == null || resultAccesso.getError().equals("")) {
		    		resultAccesso.setError("Si è verificato un problema nel servizio di autenticazione. Contattare l'amministratore");
		    	}
		    	request.setAttribute("validate","Si è verificato un problema nel servizio di autenticazione: " + resultAccesso.getError());
		    }
			
		} catch (Exception s) {
		    errorEsito = "ko";
		    request.setAttribute("esito", errorEsito);
			String messageKey = "errors.invioAttiScp.errorResponse";
			errMsgEvento = s.getMessage();
			livEvento = 3;
            logger.error(s.getStackTrace(),s);
            this.aggiungiMessaggio(request,messageKey,s.getMessage());
            
            try {
              LogEvento logEvento = LogEventiUtils.createLogEvento(request);
              logEvento.setLivEvento(livEvento);
              logEvento.setOggEvento(oggettoEvento);
              logEvento.setCodEvento(codEvento);
              logEvento.setDescr(descrEv);
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error(genericMsgErr, le);
            }
		}
		
		if (logger.isDebugEnabled()) logger.debug("InviaAttiScpAction: fine metodo");
		return mapping.findForward(target);
	}

}
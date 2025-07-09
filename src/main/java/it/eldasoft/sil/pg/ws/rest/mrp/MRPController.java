package it.eldasoft.sil.pg.ws.rest.mrp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.sil.pg.bl.npa.ScambioDatiPCPManager;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.sil.pg.web.struts.npa.ScambioDatiPCPAction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.sql.SQLException;
import java.util.*;

/**
 * This class will be used to send data in REST mode to the BFF M-RP
 * 
 * @author Manuel Bridda
 *
 */
@Path("/mrp")
public class MRPController {

  private static final String JSON_CFUTENTE = "cfUtente";
  private static final String JSON_LOA = "loa";
  private static final String JSON_PROVIDER = "idp";
  private static final String JSON_CFENTE = "cfEnte";
  private static final String JSON_IDSCHEDA = "idScheda";
  private static final String JSON_TOKEN_EXPIRATION = "scadenzaSessione";

  private static final String CREA_APPALTO_COMMAND = "Crea appalto";
  private static final String RETTIFICA_AVVISO_COMMAND = "Rettifica avviso";
  private static final String CONSULTA_RETTIFICA_COMMAND = "Consulta stato rettifica";
  private static final String VERIFICA_APPALTO_COMMAND = "Verifica appalto";
  private static final String CONSULTA_VERIFICA_APPALTO_COMMAND = "Consulta esito verifica appalto";
  private static final String CONFERMA_APPALTO_COMMAND = "Conferma appalto";
  private static final String CONSULTA_CONFERMA_APPALTO_COMMAND = "Verifica conferma appalto";
  private static final String ACQUISISCI_CIG_COMMAND = "Acquisisci CIG";
  private static final String RECUPERA_ID_AVVISO_APPALTO_COMMAND = "Recupera ID avviso appalto";
  private static final String RECUPERA_ID_AVVISO_SCHEDA_COMMAND = "Recupera ID avviso scheda";
  private static final String RECUPERA_ID_AVVISO_PIANO_COMMAND = "Recupera ID avviso piano";
  private static final String VERIFICA_PUBBLICAZIONE_COMMAND = "Verifica pubblicazione appalto";
  private static final String PUBBLICA_AVVISO_COMMAND = "Pubblica avviso";
  private static final String CONTROLLA_AVANZAMENTO_APPALTO_COMMAND = "Controlla avanzamento appalto";
  private static final String CONTROLLA_AVANZAMENTO_COMMAND = "Controlla avanzamento";
  private static final String BLOCCA_PUBBLICAZIONE_COMMAND = "Blocca pubblicazione";
  private static final String VERIFICA_STOP_PUBBLICAZIONE_COMMAND = "Verifica stop pubblicazione";
  private static final String CANCELLA_APPALTO_COMMAND = "Cancella appalto";
  private static final String CREA_PIANO_COMMAND = "Crea piano";
  private static final String VERIFICA_PIANO_COMMAND = "Verifica piano";
  private static final String CONSULTA_ESITO_VERIFICA_PIANO_COMMAND = "Consulta esito verifica piano";
  private static final String CONFERMA_PIANO_COMMAND = "Conferma piano";
  private static final String CANCELLA_PIANO_COMMAND = "Cancella piano";
  private static final String VERIFICA_CONFERMA_PIANO_COMMAND = "Verifica conferma piano";
  private static final String CREA_SCHEDA_COMMAND = "Crea scheda";
  private static final String VERIFICA_SCHEDA_COMMAND = "Verifica scheda";
  private static final String CONFERMA_SCHEDA_COMMAND = "Conferma scheda";
  private static final String CANCELLA_SCHEDA_COMMAND = "Cancella scheda";
  private static final String CONSULTA_ESITO_VERIFICA_SCHEDA_COMMAND = "Consulta esito verifica scheda";
  private static final String VERIFICA_CONFERMA_SCHEDA_COMMAND = "Verifica conferma scheda";
  private static final String START_AUTOUPDATE = "startAutoUpdate";
  private static final String STOP_AUTOUPDATE = "stopAutoUpdate";
  private static final String RECUPERA_ESTREMI_PUBBLICAZIONE = "Recupera estremi pubblicazione";


  private final Logger logger = Logger.getLogger(getClass());

  private final String[] AUTH1 =  {"AUTH1","La richiesta non contiene l'header 'Authorization'"};
  private final String[] AUTH2 =  {"AUTH2","Il token non è tipo Bearer"};
  private final String[] AUTH3 =  {"AUTH3","Il token non contiene tutti i dati richiesti o non è valido"};
  private final String[] ERR1 =   {"ERR1","Errore nel recupero della chiave privata per la verifica del token M-RP"};
  private final String[] ERR2 =   {"ERR2","L'identificativo della scheda passato non risulta essere formalmente corretto"};
  private final String[] ERR3 =   {"ERR3","Azione incompatibile con lo stato attuale della scheda"};
  private final String[] ERR4 =   {"ERR4","Azione non mappata"};
  private final String[] ERR5 =   {"ERR5","Scheda non trovata o utente non autorizzato"};
  private final String[] ERR9999 ={"ERR9999","Errore generico non gestito, consultare il log dell'applicativo"};
  private final String[] CFG1 =   {"CFG1","Non risulta configurata la chiave di decodifica 'mrp.symkey' per la verifica del token M-RP"};

  /**
   * AzioneSchedaBody
   */

  public static class AzioneSchedaBody   {

    @JsonProperty(value="identificativoScheda",required = true)
    private String identificativoScheda = null;

    @JsonProperty(value="azione",required = true)
    private String azione = null;

    @JsonProperty(value="scadenzaSessione",required = false)
    private String scadenzaSessione = null;

    public String getIdentificativoScheda() {
      return identificativoScheda;
    }

    public void setIdentificativoScheda(String identificativoScheda) {
      this.identificativoScheda = identificativoScheda;
    }

    public String getAzione() {
      return azione;
    }

    public void setAzione(String azione) {
      this.azione = azione;
    }

    public String getScadenzaSessione() {
      return scadenzaSessione;
    }

    public void setScadenzaSessione(String scadenzaSessione) {
      this.scadenzaSessione = scadenzaSessione;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("body: {\n");
      sb.append("    identificativoScheda: ").append(identificativoScheda).append("\n");
      sb.append("    azione: ").append(azione).append("\n");
      sb.append("}");
      return sb.toString();
    }
  }
  
  private JSONObject buildBaseResponse(String codApp, String evento) {
    JSONObject response = new JSONObject();
    response.put("codApp", codApp);
    response.put("tipoEvento", evento);
    response.put("done", "Y");
    return response;
  }
  
  private void addError(JSONObject json, String[] tipologicaErrore) {
    JSONObject errore = new JSONObject();
    errore.put("eccezione",tipologicaErrore[0]);
    errore.put("messaggio",tipologicaErrore[1]);
    json.put("done", "N");
    json.accumulate("errori", errore);
  }

  private void setMaxInactiveIntervalSession(HttpServletRequest request){
    request.getSession().setMaxInactiveInterval(60);
  }
  
  @GET
  @Path("/recupera-schede")
  public Response getSchede(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @Context HttpServletRequest request) {
    setMaxInactiveIntervalSession(request);
    return getSchede(auth,null,request,true,null,null);
  }
  
  @GET
  @Path("/recupera-schede/{identificativoScheda}")
  @Produces(MediaType.APPLICATION_JSON)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Response getSchede(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("identificativoScheda")String identificativoScheda, @Context HttpServletRequest request) {
    setMaxInactiveIntervalSession(request);
    return getSchede(auth,identificativoScheda,request,true,null,null);
  }

  public Response getSchede(String auth, String identificativoScheda,HttpServletRequest request,boolean checkToken, String cfUtente, String cfEnte) {
    
    if(logger.isDebugEnabled()) logger.debug("Called MRP end point /recupera-schede");

    String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    JSONObject response = buildBaseResponse(codapp,checkToken?"recupera-schede":"azione-scheda");
    try {
      if(checkToken){
      HashMap<String,Object> hmap = checkTokenAndParams(response,auth,identificativoScheda);
      if(hmap.get("error")!=null)
        return (Response) hmap.get("error");
      cfUtente = (String) hmap.get(JSON_CFUTENTE);
      cfEnte = (String) hmap.get(JSON_CFENTE);
      }

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", request.getSession().getServletContext(), SqlManager.class);
      
      String query = "select distinct v_gare_profilo.codice,v_gare_profilo.oggetto,g1npascheda.idform,g1cf_npa.nome_scheda,g1npascheda.statonpa,tab1.tab1desc,g1cf_npa.isiniz,g1npascheda.idschedanparet,t1.cftec,g1cf_npa.stato_finale,g1cf_npa.iscig,g1cf_npa.ispubbnaz,g1npascheda.wsresponse, g1npascheda.id_autoupdate, "
          + " g1cf_npa.oggetto, t1.nometei, t1.cogtei, uffint.nomein, g1cf_npa.eform, g1npascheda.ispubpvl, g1npascheda.ispubted "
          + " from torn join v_gare_profilo on v_gare_profilo.codgar=torn.codgar join g1npaform on g1npaform.codgar=torn.codgar join g1cf_npa on g1cf_npa.id=g1npaform.idcfnpa join g1npascheda on g1npascheda.idform=g1npaform.id left join tecni t1 on (torn.coddrp=t1.codtec) left join tecni t2 on (torn.codrup=t2.codtec)"
          + " join tab1 on (tab1.tab1cod='A1291' and tab1.tab1tip=g1npascheda.statonpa) join uffint on uffint.codein = v_gare_profilo.cenint where g1npascheda.inviabile='1' ";
      List<Object> params = new ArrayList<>();
      if(checkToken){
        query+=" and (t1.cftec=? or t2.cftec=?) and uffint.cfein=?";
        params.add(cfUtente);
        params.add(cfUtente);
        params.add(cfEnte);
      }
      if(StringUtils.isNotBlank(identificativoScheda)) {//se cerco puntualmente, non ho filtri sullo stato
        query+= " and g1npascheda.idform=?";
        params.add(Integer.parseInt(identificativoScheda));
      }else {//skippo le schede che hanno raggiunto lo stato finale (a meno che non sia stata chiesta puntalmente)
        query +=" and g1npascheda.statonpa not in (20,34,50) and (g1npascheda.statonpa<g1cf_npa.stato_finale or ((g1cf_npa.eform is not null and g1npascheda.ispubted is null) or (g1cf_npa.ispubbnaz is not null and g1npascheda.ispubpvl is null)))";
      }

      List<Vector> datiSchede = sqlManager.getListVector(query,params.toArray());
          
      if (datiSchede != null && !datiSchede.isEmpty()) {
        JSONArray schede = new JSONArray();
        for (Vector vector : datiSchede) {
            JSONObject item = new JSONObject();
            try {
                String codice = (String) SqlManager.getValueFromVectorParam(vector, 0).getValue();
                String descrizioneProcedura = (String) SqlManager.getValueFromVectorParam(vector, 1).getValue();
                Long idScheda = (Long) SqlManager.getValueFromVectorParam(vector, 2).getValue();
                String nomeScheda = (String) SqlManager.getValueFromVectorParam(vector, 3).getValue();
                Long statoScheda = (Long) SqlManager.getValueFromVectorParam(vector, 4).getValue();
                String descrizioneStatoScheda = (String) SqlManager.getValueFromVectorParam(vector, 5).getValue();
                String schedaIniziale = (String) SqlManager.getValueFromVectorParam(vector, 6).getValue();
                Long idSchedaRettifica = (Long) SqlManager.getValueFromVectorParam(vector, 7).getValue();
                String cfDRP = (String) SqlManager.getValueFromVectorParam(vector, 8).getValue();
                Long statoFinale = (Long) SqlManager.getValueFromVectorParam(vector, 9).getValue();
                String isCig = (String) SqlManager.getValueFromVectorParam(vector, 10).getValue();
                String isPubbNaz = (String) SqlManager.getValueFromVectorParam(vector, 11).getValue();
                String wsResponse = (String) SqlManager.getValueFromVectorParam(vector, 12).getValue();
                Long idautoupdate = (Long) SqlManager.getValueFromVectorParam(vector, 13).getValue();
                String oggetto = (String) SqlManager.getValueFromVectorParam(vector, 14).getValue();
                String nomeDRP = StringUtils.trimToEmpty((String) SqlManager.getValueFromVectorParam(vector, 15).getValue())+" "+StringUtils.trimToEmpty((String)SqlManager.getValueFromVectorParam(vector, 16).getValue());
                String nomein = (String) SqlManager.getValueFromVectorParam(vector, 17).getValue();
                String eform = (String) SqlManager.getValueFromVectorParam(vector, 18).getValue();
                String isPubPVL = (String) SqlManager.getValueFromVectorParam(vector, 19).getValue();
                String isPubTED = (String) SqlManager.getValueFromVectorParam(vector, 20).getValue();

                item.put("codApp", codapp);
                item.put("descrizioneProcedura", codice + ": " + descrizioneProcedura);
                item.put("identificativoScheda", idScheda.toString());
                item.put("nomeScheda", nomeScheda+" - "+oggetto);
                item.put("stato", descrizioneStatoScheda);
                item.put("azioni", calcolaAzioniScheda(statoScheda, schedaIniziale, nomeScheda, idSchedaRettifica, statoFinale, isCig, isPubbNaz, eform, isPubPVL, isPubTED, cfDRP, cfUtente, idautoupdate));//dobbiamo utilizzare statoScheda per calcolarli
                item.put("errori", elaboraResponse(wsResponse));//dobbiamo utilizzare wsresponse
                if(StringUtils.isNotBlank(cfDRP) && !cfDRP.equals(cfUtente)){
                  item.put("altroSoggetto", nomeDRP + " ("+cfDRP+")");
                }
                item.put("descrUffint", nomein);


              schede.add(item);
            } catch (Exception e) {
                logger.error("Risultano degli errori nell'elaborazione dei dati di una scheda" + e);
            }
        }
        response.put("schede", schede);
      }else {
        response.put("schede",new Object[0]);
      }
      if(!(response.get("schede") instanceof JSONArray))
        response.put("schede",new JSONArray());
      
      return Response.ok().entity(response.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();  //toString perch? ci sono errori di serializzazione per oggetti key: null
      
    } catch(ExpiredJwtException| MalformedJwtException | IllegalArgumentException | SignatureException e) {
      addError(response, AUTH3);
      return Response.status(Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build();
    } catch(Exception e) {
      logger.error("Error retrieving data",e);
      addError(response, ERR9999);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
  }

  @POST
  @Path("/azione-scheda")
  @Produces(MediaType.APPLICATION_JSON)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Response azioneScheda(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AzioneSchedaBody requestBody, @Context HttpServletRequest request) {

    if(logger.isDebugEnabled()) logger.debug("Called MRP end point /azione-scheda");

    setMaxInactiveIntervalSession(request);

    String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    JSONObject response = buildBaseResponse(codapp,"azione-scheda");

    try {
      String identificativoScheda = requestBody.getIdentificativoScheda();
      String azione = requestBody.getAzione();
      String scadenzaSessione = requestBody.getScadenzaSessione();

      HashMap<String,Object> hmap = checkTokenAndParams(response,auth,identificativoScheda);
      if(hmap.get("error")!=null)
        return (Response) hmap.get("error");
      hmap.put("azione",azione);
      if(StringUtils.isNotBlank(scadenzaSessione))
        hmap.put(JSON_TOKEN_EXPIRATION,scadenzaSessione);

      String[] err = eseguiAzioneScheda(hmap,request);//esegue l'azione, poi reinterroghiamo la base dati per vedere la response!
      if(err!=null){
        addError(response, err);
        return Response.status(Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build();
      }
      return getSchede(auth,identificativoScheda,request,false,(String) hmap.get(JSON_CFUTENTE),(String) hmap.get(JSON_CFENTE));

    } catch(ExpiredJwtException| MalformedJwtException | IllegalArgumentException | SignatureException e) {
      addError(response, AUTH3);
      return Response.status(Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build();
    } catch(Exception e) {
      logger.error("Error retrieving data",e);
      addError(response, ERR9999);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
  }

  private HashMap<String,Object> checkTokenAndParams(JSONObject response, String auth, String identificativoScheda){
    HashMap<String,Object> result = new HashMap<String,Object>();

    if(StringUtils.isNotBlank(identificativoScheda)) {
      try {
        result.put("idScheda",Integer.parseInt(identificativoScheda));
      } catch (NumberFormatException e) {
        addError(response, ERR2);
        result.put("error", Response.status(Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build());
      }
    }

    if(StringUtils.isBlank(auth)) {
      addError(response,AUTH1);
      result.put("error",Response.status(Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build());
    }

    String token = StringUtils.substringAfter(auth, "Bearer ");
    if(StringUtils.isBlank(token)) {
      addError(response,AUTH2);
      result.put("error",Response.status(Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build());
    }

    String symkey = ConfigManager.getValore("mrp.symkey");
    String symkeyChiaro = null;
    if (symkey != null && !symkey.trim().isEmpty()) {
      ICriptazioneByte passwordICriptazioneByte = null;
      try {
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
                ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), symkey.getBytes(),
                ICriptazioneByte.FORMATO_DATO_CIFRATO);
      } catch (CriptazioneException e) {
        addError(response,ERR1);
        result.put("error",Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build());
      }
      symkeyChiaro = new String(passwordICriptazioneByte.getDatoNonCifrato());
      result.put("symkeyChiaro",symkeyChiaro);
    }

    if(StringUtils.isBlank(symkeyChiaro)) {
      addError(response, CFG1);
      result.put("error",Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build());
    }

    JSONObject body = JSONObject.fromObject(JwtTokenUtilities.getBodyFromJwt(JwtTokenUtilities.parseJwt(token,symkeyChiaro)));

    String cfUtente = body.getString(JSON_CFUTENTE);
    String loa = body.getString(JSON_LOA);
    String idp = body.getString(JSON_PROVIDER);
    String cfEin = body.getString(JSON_CFENTE);

    if(logger.isDebugEnabled()) logger.debug("Utente che ha trasmesso la richiesta: "+cfUtente+" loggato con "+idp +"(LoA "+loa+")");
    if(StringUtils.isBlank(cfUtente) || StringUtils.isBlank(loa) || StringUtils.isBlank(idp) || StringUtils.isBlank(cfEin)) {
      throw new MalformedJwtException("Il token non contiene i dati obbligatori necessari");
    }
    result.put(JSON_CFUTENTE,cfUtente);
    result.put(JSON_LOA,loa);
    result.put(JSON_PROVIDER,idp);
    result.put(JSON_CFENTE,cfEin);

    return result;
  }

  private Object elaboraResponse(String response) {
    
    JSONObject json = JSONObject.fromObject(response);
    JSONArray errori = new JSONArray();
    if(json.containsKey(ScambioDatiPCPManager.PG_ERRORS) && json.get(ScambioDatiPCPManager.PG_ERRORS) instanceof String) {
      List<JSONObject> formattedList = new ArrayList<>();
      JSONObject pgError = new JSONObject();
      pgError.put("codice","PG");
      pgError.put("dettaglio", json.getString(ScambioDatiPCPManager.PG_ERRORS));
      formattedList.add(pgError);
      errori.add(formattedList.toArray());
    }
    if(json.containsKey(ScambioDatiPCPManager.ANAC_ERRORS))
      errori.addAll(json.getJSONArray(ScambioDatiPCPManager.ANAC_ERRORS));
    if(json.containsKey("internalErrors")){
      JSONArray ie = json.getJSONArray("internalErrors");
      for(int i=0;i<ie.size();i++){
        JSONObject internalError = new JSONObject();
        internalError.put("codice",i);
        internalError.put("dettaglio",ie.getString(i));
        errori.add(internalError);
      }
    }
    
    if(errori.isEmpty())
      return new Object[0];

    return errori;
  }
  
  private Object[] calcolaAzioniScheda(Long statoScheda, String schedaIniziale, String nomeScheda, Long idSchedaRettifica, Long statoFinale, String isCIG, String isPubbNaz, String eform, String isPubPVL, String isPubTED, String cfDRP, String cfUser, Long idautoupdate) {
    List<String> azioni = new ArrayList<String>();
    
    boolean isIniziale = "1".equals(schedaIniziale);
    boolean isRettifica = idSchedaRettifica!=null;
    boolean attribuisceCIG = "1".equals(isCIG);
    boolean previstaPubblicazione = "1".equals(isPubbNaz);
    boolean isPianificazione = StringUtils.isNotBlank(nomeScheda) && nomeScheda.indexOf("PL")==0 || "P7_3".equals(nomeScheda)  || "P1_15_1".equals(nomeScheda);
    boolean visualizzaRecuperaEstremiPubblicazione = (StringUtils.isNotEmpty(eform) && !"1".equals(isPubTED)) || ("1".equals(isPubbNaz) && !"1".equals(isPubPVL));
    
    if(!isPianificazione  && StringUtils.isNotBlank(cfDRP) && !cfDRP.equals(cfUser)){//unica azione possibile per un utente che non è responsabile degli invii, non vale per le pianificazioni
      azioni.add("Presa in carico schede ANAC");
      return azioni.toArray();
    }
    
    if(isIniziale && !isPianificazione) { //schede P* non di pianificazione
    switch(statoScheda.toString()) {
      case "1": //in compilazione
        if(isRettifica)
          azioni.add(RETTIFICA_AVVISO_COMMAND);
        else
          azioni.add(CREA_APPALTO_COMMAND);
        break;
      case "10": //in lavorazione
        if(isRettifica) {
          azioni.add(CONSULTA_RETTIFICA_COMMAND);
          azioni.add(getAutoupdateCmd(idautoupdate));
        }
        else
          azioni.add(VERIFICA_APPALTO_COMMAND);
        break;
      case "12": //in corso di verifica
          azioni.add(CONSULTA_VERIFICA_APPALTO_COMMAND);
        break;
      case "14": //verificato positivamente
        azioni.add(CONFERMA_APPALTO_COMMAND);
        break;
      case "15": //verificato positivamente
        azioni.add(CONSULTA_CONFERMA_APPALTO_COMMAND);
        break;
      case "30": //confermato
        if(attribuisceCIG) {
          azioni.add(ACQUISISCI_CIG_COMMAND);
        }
        else if(previstaPubblicazione) {
          azioni.add(RECUPERA_ID_AVVISO_APPALTO_COMMAND);
        }
        break;
      case "31": //acquisizione CIG
        if(previstaPubblicazione) {
          azioni.add(RECUPERA_ID_AVVISO_APPALTO_COMMAND);
        }else
          azioni.add(VERIFICA_PUBBLICAZIONE_COMMAND);
        break;
      case "32": //recupero ID avviso
        azioni.add(PUBBLICA_AVVISO_COMMAND);
        break;
      case "33": //pubblica avviso
        azioni.add(CONTROLLA_AVANZAMENTO_APPALTO_COMMAND);
        azioni.add(getAutoupdateCmd(idautoupdate));
        break;
      case "40": //in attesa pubblicazione
        if(isRettifica)
          azioni.add(CONSULTA_RETTIFICA_COMMAND);
        else{
          azioni.add(CONTROLLA_AVANZAMENTO_APPALTO_COMMAND);
          azioni.add(BLOCCA_PUBBLICAZIONE_COMMAND);
        }
        azioni.add(getAutoupdateCmd(idautoupdate));
        break;
      case "41": //richiesta stop pubblicazione
        azioni.add(VERIFICA_STOP_PUBBLICAZIONE_COMMAND);
        break;
      case "60":
        if (visualizzaRecuperaEstremiPubblicazione) {
          azioni.add(RECUPERA_ESTREMI_PUBBLICAZIONE);
          azioni.add(getAutoupdateCmd(idautoupdate));
        }
        break;
      }
      
      if(statoScheda>1 && !Long.valueOf(20L).equals(statoScheda) && (statoScheda<=15 || (previstaPubblicazione && statoScheda<=32)) && !isRettifica)
        azioni.add(CANCELLA_APPALTO_COMMAND);
    } else if(isIniziale && isPianificazione) { //schede di pianificazione
      switch(statoScheda.toString()) {
        case "1": //in compilazione
          azioni.add(CREA_PIANO_COMMAND);
          break;
        case "10": //in lavorazione
          azioni.add(VERIFICA_PIANO_COMMAND);
          break;
        case "12": //in corso di verifica
            azioni.add(CONSULTA_ESITO_VERIFICA_PIANO_COMMAND);
          break;
        case "14": //verificato positivamente
          azioni.add(CONFERMA_PIANO_COMMAND);
          break;
        case "15": //verificato positivamente
          azioni.add(VERIFICA_CONFERMA_PIANO_COMMAND);
          break;
        case "30": //confermato
          if(previstaPubblicazione) {
            azioni.add(RECUPERA_ID_AVVISO_PIANO_COMMAND);
          }
          break;
        case "32": //recupero ID avviso
        case "40": //pubblica avviso
          azioni.add(CONTROLLA_AVANZAMENTO_COMMAND);
          azioni.add(getAutoupdateCmd(idautoupdate));
          break;
        case "41": //richiesta stop pubblicazione
          azioni.add(VERIFICA_STOP_PUBBLICAZIONE_COMMAND);
          break;
        case "60":
          if (visualizzaRecuperaEstremiPubblicazione) {
            azioni.add(RECUPERA_ESTREMI_PUBBLICAZIONE);
            azioni.add(getAutoupdateCmd(idautoupdate));
          }
          break;
        }
        
        if(statoScheda>1 && statoScheda<=15)
          azioni.add(CANCELLA_PIANO_COMMAND);
    } else if(!isIniziale && !isPianificazione) {
      switch(statoScheda.toString()) {
      case "1": //in compilazione
        azioni.add(CREA_SCHEDA_COMMAND);
        break;
      case "10": //in lavorazione
        azioni.add(VERIFICA_SCHEDA_COMMAND);
        break;
      case "12": //in corso di verifica
          azioni.add(CONSULTA_ESITO_VERIFICA_SCHEDA_COMMAND);
        break;
      case "14": //verificato positivamente
        azioni.add(CONFERMA_SCHEDA_COMMAND);
        break;
      case "15": //verificato positivamente
        azioni.add(VERIFICA_CONFERMA_SCHEDA_COMMAND);
        break;
      case "30": //confermato
        if(previstaPubblicazione)
          azioni.add(RECUPERA_ID_AVVISO_SCHEDA_COMMAND);
        break;
      case "32": //recupero ID avviso
      case "40": //pubblica avviso
        azioni.add(CONTROLLA_AVANZAMENTO_COMMAND);
        azioni.add(getAutoupdateCmd(idautoupdate));
        break;
      case "60":
        if (visualizzaRecuperaEstremiPubblicazione) {
          azioni.add(RECUPERA_ESTREMI_PUBBLICAZIONE);
          azioni.add(getAutoupdateCmd(idautoupdate));
        }
        break;
      }
      
      if(statoScheda>1 && statoScheda<=15)
        azioni.add(CANCELLA_SCHEDA_COMMAND);
    }

    return azioni.toArray();
  }

  private String[] eseguiAzioneScheda(HashMap<String,Object> hmap, HttpServletRequest request) throws SQLException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", request.getSession().getServletContext(), SqlManager.class);

    String cfUtente = (String) hmap.get(JSON_CFUTENTE);
    String loa = (String) hmap.get(JSON_LOA);
    String idp = (String) hmap.get(JSON_PROVIDER);

    AccountSSO accountSSO = new AccountSSO();
    accountSSO.setStandardProvider(idp);
    accountSSO.setCurrentLoa(Integer.parseInt(loa));

    ProfiloUtente profiloUtente = new ProfiloUtente();
    profiloUtente.setCodiceFiscale(cfUtente);

    //iniezioni profilo e account sso fittizi
    request.getSession().setAttribute(CostantiGenerali.ID_ATTRIBUTO_SESSIONE_ACCOUNT_SSO, accountSSO);
    request.getSession().setAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE, profiloUtente);

    Integer idScheda = (Integer) hmap.get(JSON_IDSCHEDA);
    String azione = (String) hmap.get("azione");
    String servizio = null;
    String tipoOperazione = null;
    String tipoRicerca = "ULTIMO_ESITO";

    HashMap<String, String> parametersMRP = new HashMap<String, String>();
    String query = "select distinct v_gare_profilo.codice,g1cf_npa.nome_scheda,g1npascheda.statonpa,g1cf_npa.isiniz,g1npascheda.idschedanparet,t1.cftec,g1cf_npa.stato_finale,g1cf_npa.iscig,g1cf_npa.ispubbnaz, g1npascheda.id_autoupdate," +
            " g1cf_npa.eform, g1npascheda.ispubpvl, g1npascheda.ispubted" +
            "  from torn join v_gare_profilo on v_gare_profilo.codgar=torn.codgar join g1npaform on g1npaform.codgar=torn.codgar join g1cf_npa on g1cf_npa.id=g1npaform.idcfnpa join g1npascheda on g1npascheda.idform=g1npaform.id left join tecni t1 on (torn.coddrp=t1.codtec) left join tecni t2 on (torn.codrup=t2.codtec)" +
            " where g1npascheda.inviabile='1' and g1npaform.id=? and (t1.cftec=? or t2.cftec=?)";

    Vector vector = sqlManager.getVector(query, new Object[]{idScheda, cfUtente, cfUtente});

    if (vector != null && !vector.isEmpty()) {
      String codice = (String) SqlManager.getValueFromVectorParam(vector, 0).getValue();
      String nomeScheda = (String) SqlManager.getValueFromVectorParam(vector, 1).getValue();
      Long statoScheda = (Long) SqlManager.getValueFromVectorParam(vector, 2).getValue();
      String schedaIniziale = (String) SqlManager.getValueFromVectorParam(vector, 3).getValue();
      Long idSchedaRettifica = (Long) SqlManager.getValueFromVectorParam(vector, 4).getValue();
      String cfDRP = (String) SqlManager.getValueFromVectorParam(vector, 5).getValue();
      Long statoFinale = (Long) SqlManager.getValueFromVectorParam(vector, 6).getValue();
      String isCig = (String) SqlManager.getValueFromVectorParam(vector, 7).getValue();
      String isPubbNaz = (String) SqlManager.getValueFromVectorParam(vector, 8).getValue();
      Long idautoupdate = (Long) SqlManager.getValueFromVectorParam(vector, 9).getValue();
      String eform = (String) SqlManager.getValueFromVectorParam(vector, 10).getValue();
      String isPubPVL = (String) SqlManager.getValueFromVectorParam(vector, 11).getValue();
      String isPubTED = (String) SqlManager.getValueFromVectorParam(vector, 12).getValue();

      parametersMRP.put("codiceGara", codice);
      Object[] azioniDisponibili = calcolaAzioniScheda(statoScheda, schedaIniziale, nomeScheda, idSchedaRettifica, statoFinale, isCig, isPubbNaz, eform, isPubPVL, isPubTED, cfDRP, cfUtente, idautoupdate);
      if (!Arrays.asList(azioniDisponibili).contains(azione)) {
        return ERR3;
      }

      switch (azione) {
        case CREA_APPALTO_COMMAND:
          servizio = ScambioDatiPCPAction.CREAPPALTO;
          break;
        case RETTIFICA_AVVISO_COMMAND:
          servizio = ScambioDatiPCPAction.RETTIFICAAVVISO;
          break;
        case CANCELLA_APPALTO_COMMAND:
          servizio = ScambioDatiPCPAction.CANCELLAAPPALTO;
          break;
        case VERIFICA_APPALTO_COMMAND:
          servizio = ScambioDatiPCPAction.VERIFICAAPPALTO;
          break;
        case CONSULTA_VERIFICA_APPALTO_COMMAND:
          servizio = ScambioDatiPCPAction.ESITOOPERAZIONE;
          tipoOperazione = "AP_VERIF";
          break;
        case CONFERMA_APPALTO_COMMAND:
          servizio = ScambioDatiPCPAction.CONFERMAPPALTO;
          break;
        case CONSULTA_CONFERMA_APPALTO_COMMAND:
        case RECUPERA_ID_AVVISO_APPALTO_COMMAND:
          servizio = ScambioDatiPCPAction.ESITOOPERAZIONE;
          tipoOperazione = "AP_CONF";
          break;
        case ACQUISISCI_CIG_COMMAND:
          servizio = ScambioDatiPCPAction.RECUPERACIG;
          break;
        case VERIFICA_PUBBLICAZIONE_COMMAND:
          servizio = ScambioDatiPCPAction.STATOAPPALTO;
          break;
        case PUBBLICA_AVVISO_COMMAND:
          servizio = ScambioDatiPCPAction.PUBBLICAAVVISO;
          break;
        case CONTROLLA_AVANZAMENTO_APPALTO_COMMAND:
          servizio = ScambioDatiPCPAction.ESITOOPERAZIONE;
          tipoOperazione = "AV_PUBB";
          break;
        case BLOCCA_PUBBLICAZIONE_COMMAND:
          servizio = ScambioDatiPCPAction.CANCELLAAVVISO;
          break;
        case VERIFICA_STOP_PUBBLICAZIONE_COMMAND:
        case CONTROLLA_AVANZAMENTO_COMMAND:
          servizio = ScambioDatiPCPAction.STATOAVVISO;
          break;
        case CONSULTA_RETTIFICA_COMMAND:
          servizio = ScambioDatiPCPAction.ESITOOPERAZIONE;
          tipoOperazione = "AV_RETT";
          tipoRicerca = "TUTTI_ESITI";
          break;
        //scheda
        case CREA_SCHEDA_COMMAND:
          servizio = ScambioDatiPCPAction.CREASCHEDA;
          break;
        case CANCELLA_SCHEDA_COMMAND:
          servizio = ScambioDatiPCPAction.CANCELLASCHEDA;
          break;
        case VERIFICA_SCHEDA_COMMAND:
          servizio = ScambioDatiPCPAction.VERIFICASCHEDA;
          break;
        case CONSULTA_ESITO_VERIFICA_SCHEDA_COMMAND:
          servizio = ScambioDatiPCPAction.ESITOOPERAZIONE;
          tipoOperazione = "SC_VERIF";
          break;
        case CONFERMA_SCHEDA_COMMAND:
          servizio = ScambioDatiPCPAction.CONFERMASCHEDA;
          break;
        case VERIFICA_CONFERMA_SCHEDA_COMMAND:
        case RECUPERA_ID_AVVISO_SCHEDA_COMMAND:
          servizio = ScambioDatiPCPAction.ESITOOPERAZIONE;
          tipoOperazione = "SC_CONF";
          break;
        case CREA_PIANO_COMMAND:
          servizio= ScambioDatiPCPAction.CREAPIANO;
          break;
        case CANCELLA_PIANO_COMMAND:
          servizio = ScambioDatiPCPAction.CANCELLAPIANO;
          break;
        case VERIFICA_PIANO_COMMAND:
          servizio = ScambioDatiPCPAction.VERIFICAPIANO;
          break;
        case CONSULTA_ESITO_VERIFICA_PIANO_COMMAND:
          servizio = ScambioDatiPCPAction.ESITOOPERAZIONE;
          tipoOperazione = "PI_VERIF";
          break;
          case CONFERMA_PIANO_COMMAND:
              servizio = ScambioDatiPCPAction.CONFERMAPIANO;
              break;
        case VERIFICA_CONFERMA_PIANO_COMMAND:
        case RECUPERA_ID_AVVISO_PIANO_COMMAND:
            servizio = ScambioDatiPCPAction.ESITOOPERAZIONE;
            tipoOperazione = "PI_CONF";
            break;
        case RECUPERA_ESTREMI_PUBBLICAZIONE:
          servizio = ScambioDatiPCPAction.CONSULTAAVVISO;
          break;
        case START_AUTOUPDATE:
            servizio = ScambioDatiPCPAction.STARTAUTOUPDATE;
            parametersMRP.put(JSON_LOA,loa);
            parametersMRP.put(JSON_PROVIDER,idp);
            parametersMRP.put(JSON_TOKEN_EXPIRATION,(String)hmap.get(JSON_TOKEN_EXPIRATION));
            break;
        case STOP_AUTOUPDATE:
            servizio = ScambioDatiPCPAction.STOPAUTOUPDATE;
            break;
        default:
          return ERR4;
      }
      parametersMRP.put(JSON_CFUTENTE, cfUtente);
      parametersMRP.put(ScambioDatiPCPManager.ID_FORM, idScheda.toString());
      //motivo=parametersMRP.get(ScambioDatiPCPManager.BODY_MOTIVO); non utilizzato
      parametersMRP.put(ScambioDatiPCPManager.TIPO_OPERAZIONE, tipoOperazione);
      parametersMRP.put(ScambioDatiPCPManager.TIPO_RICERCA, tipoRicerca);
      parametersMRP.put(ScambioDatiPCPManager.ESITO, null);
      parametersMRP.put(ScambioDatiPCPManager.NOME_SCHEDA, nomeScheda);

      ScambioDatiPCPAction scambioDatiPCP = new ScambioDatiPCPAction();

      ScambioDatiPCPManager scambioDatiPCPManager = (ScambioDatiPCPManager) UtilitySpring.getBean("scambioDatiPCPManager", request.getSession().getServletContext(), ScambioDatiPCPManager.class);
      scambioDatiPCP.setScambioDatiPCPManager(scambioDatiPCPManager);
      scambioDatiPCP.wrapperChiamate(request, servizio, false, parametersMRP,null);//qui eseguo la chiamata vera e propria al gateway
      return null;
    } else {
      return ERR5;
    }
  }

  private static String getAutoupdateCmd(Long idautoupdate){
    return idautoupdate==null?START_AUTOUPDATE:STOP_AUTOUPDATE;
  }
}

package it.eldasoft.sil.pg.ws.rest.eforms;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.ws.rest.eforms.exceptions.ExceptionHandlerResult;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.sil.pg.ws.rest.eforms.exceptions.BadRequestException;
import it.eldasoft.sil.pg.ws.rest.eforms.exceptions.UnauthorizedException;
import it.eldasoft.sil.pg.ws.rest.eforms.export.ExportEForm;
import it.eldasoft.sil.pg.ws.rest.eforms.export.FormatoExportEForm;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

@Path("/eforms")
public class WSEForms {

  private static final Logger logger = Logger.getLogger(WSEForms.class);
  private static String INTEGRAZIONEMEFORMS_SSE_DISABLE = "integrazioneMEforms.sse.disable";
  private static final String JWT_SECRET = "utiCt/R4}_7MuoYaVuP-1,Z3kdTI%3:%fqaTX0G:td<!|_k8z<-moTTjw?kzg|3|";
  private static final String NOTICE_UUID = "noticeUUID";
  private static final String NOTICE_SUB_TYPE = "noticeSubType";
  private static final String NOTICE_RECT_UUID = "noticeChangedUUID";
  private static final String DATAGET_URL = "dataGetUrl";
  private static final String KEEPALIVE_URL = "keepAliveUrl";
  private static final String PERSISTENCE_URL = "persistenceUrl";

  @GET
  @Path("data-get")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response generaEforms(String in, @Context HttpServletRequest request) {

    int livEvento = 1;
    String errMsgEvento = "";
    int status = 200;

    // JSON di uscita
    JSONObject outputJson = new JSONObject();
    //BOZZA o INIZIALIZZAZIONE
    //tracciato
    String codgar = "";
    String uuidEform = "";
    String codiceEform = "";
    String bozza = "";
    String bozzaRettifica = "";
    String uuidRettificaPadre = "";
    JSONObject dataJson = null;
    Object tracciato = new Object();
    
    ExportEForm exportEForm = (ExportEForm) UtilitySpring.getBean("exportEForm", 
        SpringAppContext.getServletContext(), ExportEForm.class);
    try {
      dataJson = controllaToken(request);

      uuidEform = dataJson.optString(NOTICE_UUID);
      codiceEform = dataJson.optString(NOTICE_SUB_TYPE);
      uuidRettificaPadre = dataJson.optString(NOTICE_RECT_UUID);
      if(uuidRettificaPadre != null && "null".equals(uuidRettificaPadre)) {
        uuidRettificaPadre = "";
      }
      
      bozza = exportEForm.getBozzaByCodiceEformUuidEform(codiceEform, uuidEform, null);
      if(uuidRettificaPadre != null && !uuidRettificaPadre.isEmpty() && Boolean.TRUE.equals(isInizializzazione(bozza))){
        bozzaRettifica = exportEForm.getBozzaByCodiceEformUuidEform(codiceEform, uuidRettificaPadre, uuidEform);
      }
      codgar = exportEForm.getCodgarByUuid(uuidEform);
      if(codgar != null) {
        if (codgar.startsWith("$")) {
          codgar = codgar.substring(1);
        }
      }
      outputJson.put("newToken", newToken(dataJson));
    }catch (Exception e) {
      ExceptionHandlerResult ehResult = handleException(e, outputJson);
      status      = ehResult.getStatus();
      livEvento   = ehResult.getLivEvento();
      errMsgEvento = ehResult.getErrMsgEvento();
    } finally {
      if(Boolean.TRUE.equals(isInizializzazione(bozza))) {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(codgar);
          logEvento.setCodEvento("GA_WSEFORMS_REQ_DATAGET");
          logEvento.setDescr("Richiesta da M-eFORMS (api: data-get uuid: "+uuidEform+")");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception e) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", e);
        }
      }
    }
    try {
      if(Boolean.TRUE.equals(isInizializzazione(bozza)) && Boolean.TRUE.equals(isInizializzazione(bozzaRettifica))) {
        // Elaborazione in base al codice eform
        switch (codiceEform) {
        case "1":
        case "2":
        case "4":
        case "5":
        case "7":
        case "8":
        case "10":
        case "11":
        case "12":
        case "13":
        case "14":
          tracciato = exportEForm.creaJSONPin(codiceEform, uuidEform);
          break;

        case "15":
        case "16":
        case "17":
        case "18":
        case "19":
        case "20":
        case "21":
        case "22":
        case "23":
        case "24":
          tracciato = exportEForm.creaJSONCn(codiceEform, uuidEform);
          break;

        case "25":
        case "26":
        case "27":
        case "28":
        case "29":
        case "30":
        case "31":
        case "32":
        case "33":
        case "34":
        case "35":
        case "36":
        case "37":
        case "38":
        case "39":
        case "40":
          tracciato = exportEForm.creaJSONCan(codiceEform, uuidEform);
          break;

        default:
          break;
        }
        if(uuidRettificaPadre != null && !uuidRettificaPadre.isEmpty()) {
          outputJson.put("tipoTracciato", "INIZIALIZZAZIONE_RETTIFICA");
        } else {
          outputJson.put("tipoTracciato", "INIZIALIZZAZIONE");
        }
        outputJson.put("tracciato", tracciato);
        errMsgEvento = ""+tracciato;
      } else if(bozzaRettifica != null && !bozzaRettifica.isEmpty()) {
        outputJson.put("tipoTracciato", "BOZZA_RETTIFICA");
        outputJson.put("tracciato", "\""+bozzaRettifica+"\"");
      } else {
        outputJson.put("tipoTracciato", "BOZZA");
        outputJson.put("tracciato", "\""+bozza+"\"");
      }
      outputJson.put("success", true);
    } catch (Exception e) {
      // Gestione centralizzata delle eccezioni
      ExceptionHandlerResult ehResult = handleException(e, outputJson);
      status      = ehResult.getStatus();
      livEvento   = ehResult.getLivEvento();
      errMsgEvento = ehResult.getErrMsgEvento();
    } finally {
      if(Boolean.TRUE.equals(isInizializzazione(bozza))) {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(codgar);
          logEvento.setCodEvento("GA_WSEFORMS_RES_DATAGET");
          logEvento.setDescr("Risposta a M-eFORMS (api: data-get uuid: "+uuidEform+")");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception e) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", e);
        }
      }
    }

    // Restituisco la risposta con lo stato HTTP corretto e il JSON di output
    return Response.status(status).entity(outputJson).build();
  }
  
  
  @POST
  @Path("keep-alive")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response refreshToken(String in, @Context HttpServletRequest request) {

      int status = 200;
      JSONObject outputJson = new JSONObject();
      try {
          JSONObject dataJson = controllaToken(request);

          outputJson.put("success", true);
          outputJson.put("newToken", newToken(dataJson));
          
      } catch (Exception e) {
          // Gestione centralizzata delle eccezioni
          ExceptionHandlerResult ehResult = handleException(e, outputJson);
          status = ehResult.getStatus();
      }

      return Response.status(status).entity(outputJson).build();
  }

  

  @POST
  @Path("persistence")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response salvaBozza(String in, @Context HttpServletRequest request) {

    int livEvento = 1;
    String errMsgEvento = "";
    int status = 200;
    JSONObject outputJson = new JSONObject();

    String codiceEform = "";
    String uuidEform = "";
    String codgar = "";
    JSONObject dataJson = null;
    String tipoTracciato = "";
    String tracciato = "";
    ExportEForm exportEForm = (ExportEForm) UtilitySpring.getBean("exportEForm", 
        SpringAppContext.getServletContext(), ExportEForm.class);
    try {
      // 1. Validazione del token e recupero delle Claims
      dataJson = controllaToken(request);

      // Recupero dei valori dai dati decodificati
      codiceEform = dataJson.optString(NOTICE_SUB_TYPE);
      uuidEform = dataJson.optString(NOTICE_UUID);
      String uuidEformRettificaPadre = dataJson.optString(NOTICE_RECT_UUID);
      String dataGetUrl = dataJson.optString(DATAGET_URL);
      String keepAliveUrl = dataJson.optString(KEEPALIVE_URL);
      String persistenceUrl = dataJson.optString(PERSISTENCE_URL);
      logger.debug("Codice eform ottenuto dal token: " + codiceEform);
      logger.debug("UUID ottenuto dal token: " + uuidEform);
      logger.debug("UUID rettifica padre ottenuto dal token: " + uuidEformRettificaPadre);
      logger.debug("dataGetUrl ottenuto dal token: " + dataGetUrl);
      logger.debug("keepAliveUrl ottenuto dal token: " + keepAliveUrl);
      logger.debug("persistenceUrl ottenuto dal token: " + persistenceUrl);
      
      JSONObject inputJson = JSONObject.fromObject(in);
      tipoTracciato = inputJson.getString("tipoTracciato");
      tracciato = inputJson.getString("tracciato");
      
      codgar = exportEForm.getCodgarByUuid(uuidEform);
      if(codgar != null) {
        if (codgar.startsWith("$")) {
          codgar = codgar.substring(1);
        }
      }
      
    }catch (Exception e) {
      ExceptionHandlerResult ehResult = handleException(e, outputJson);
      status      = ehResult.getStatus();
      livEvento   = ehResult.getLivEvento();
      errMsgEvento = ehResult.getErrMsgEvento();
    } finally {
      if ("XML".equals(tipoTracciato)) {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(codgar);
          logEvento.setCodEvento("GA_WSEFORMS_REQ_PERSISTENCE");
          logEvento.setDescr("Richiesta da M-eFORMS (api: persistence uuid: "+uuidEform+")");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception e) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", e);
        }
      }
    }
    try {

      if (in != null) {
        Boolean primoSalvataggio = Boolean.FALSE;
        if ("BOZZA".equals(tipoTracciato)) {
          primoSalvataggio = exportEForm.getPrimoSalvataggioBozza(uuidEform);
          exportEForm.getSalvaBozza(codiceEform, uuidEform, tracciato);
        } else if ("XML".equals(tipoTracciato)) {
          primoSalvataggio = exportEForm.getPrimoSalvataggioXML(uuidEform);
          exportEForm.getSalvaXml(codiceEform, uuidEform, tracciato);
        }
        outputJson.put("success", true);
        String integrazioneSseDisabled = ConfigManager.getValore(INTEGRAZIONEMEFORMS_SSE_DISABLE);
        if(Boolean.TRUE.equals(primoSalvataggio) && !"1".equals(integrazioneSseDisabled)) {
          String message = String.format("{\"codiceEform\": \"%s\"}", String.valueOf(exportEForm.getIDByUuidEform(uuidEform)));
          SseEformsServlet.notifyClients(message);
        }

      } else {
        throw new BadRequestException("Il body della richiesta non puo' essere vuoto");
      }
    } catch (Exception e) {
      // Gestione centralizzata delle eccezioni
      ExceptionHandlerResult ehResult = handleException(e, outputJson);
      status = ehResult.getStatus();
      livEvento = ehResult.getLivEvento();
      errMsgEvento = ehResult.getErrMsgEvento();
    } finally {
      // Tracciatura evento su LogEventiUtils
      if ("XML".equals(tipoTracciato)) {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(codgar);
          logEvento.setCodEvento("GA_WSEFORMS_RES_PERSISTENCE");
          logEvento.setDescr("Risposta a M-eFORMS (api: persistence uuid: "+uuidEform+")");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception e) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", e);
        }
      }
    }

    return Response.status(status).entity(outputJson).build();
  }

  
  
  
  
  
  

  /**
   * Gestione centralizzata delle eccezioni.
   * - BadRequestException --> 400
   * - UnauthorizedException --> 401
   * - WebApplicationException --> 409 nel caso di xml non piu' aggiornabile per via dello statonpa maggiore di dieci
   * - Altrimenti 500
   * - Log dell'errore e aggiornamento del JSON di risposta
   *
   * @param e         Eccezione da gestire
   * @param outputJson JSONObject di risposta da aggiornare
   * @return Oggetto ExceptionHandlerResult con i dati di errore
   */
  private ExceptionHandlerResult handleException(Exception e, JSONObject outputJson) {
    int status = 500;
    int livEvento = 3;
    String errMsgEvento = e.getMessage();

    if (e instanceof BadRequestException) {
      status = 400;
    } else if (e instanceof UnauthorizedException) {
      status = 401;
    } else if (e instanceof WebApplicationException) {
      status = 409;
    }

    logger.error(errMsgEvento, e);
    outputJson.put("success", false);
    outputJson.put("errorMessage", errMsgEvento);
    return new ExceptionHandlerResult(status, livEvento, errMsgEvento);
  }

  /**
   * Metodo per controllare la validita' del token JWT presente nell'header "Authorization".
   * Se il token e' mancante o non valido, viene lanciata un'UnauthorizedException.
   * Restituisce le Claims estratte dal token, utili per ulteriori controlli in generaEforms.
   *
   * @param request HttpServletRequest
   * @return Claims estratte dal token JWT
   * @throws UnauthorizedException se il token e' mancante o non valido
   */
  private JSONObject controllaToken(HttpServletRequest request) throws UnauthorizedException {
    String token = request.getHeader("Authorization");
    if (token == null || token.trim().isEmpty()) {
      throw new UnauthorizedException("Token mancante");
    }
    // Se presente il prefisso "Bearer ", lo rimuovo
    if (token.startsWith("Bearer ")) {
      token = token.substring("Bearer ".length());
    }
    try {
      // Validazione del token
      Claims claims = validateToken(token);
      logger.debug("Token validato.");
      logger.debug("Claims ottenute dal token: " + claims);

      // Recupera il claim "data", che dovrebbe essere una Map<String, String>
      Map<String, String> dataMap = (Map<String, String>) claims.get("data");
      if (dataMap == null) {
          throw new UnauthorizedException("Il token non contiene i dati necessari.");
      }

      
      return JSONObject.fromObject(dataMap);
    } catch (Exception e) {
      throw new UnauthorizedException("Token non valido: " + e.getMessage());
    }
  }
  

  /**
   * Valida il token JWT e ne estrae i Claims.
   *
   * @param token Il token JWT da validare.
   * @return Claims estratte dal token.
   * @throws Exception Se il token e' scaduto, malformato, non supportato, con firma non valida o nullo.
   */
  public static Claims validateToken(String token) throws Exception {
      try {
          // Creiamo una chiave segreta a partire dalla stringa JWT_SECRET
          SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

          Claims claims = Jwts.parserBuilder()
                  .setSigningKey(key)
                  .build()
                  .parseClaimsJws(token)
                  .getBody();

          logger.debug("Token validato. Claims: " + claims);
          return claims;
      } catch (ExpiredJwtException e) {
          logger.error("Token scaduto", e);
          throw new Exception("Token scaduto", e);
      } catch (UnsupportedJwtException e) {
          logger.error("Token non supportato", e);
          throw new Exception("Token non supportato", e);
      } catch (MalformedJwtException e) {
          logger.error("Token malformato", e);
          throw new Exception("Token malformato", e);
      } catch (SignatureException e) {
          logger.error("Firma del token non valida", e);
          throw new Exception("Firma del token non valida", e);
      } catch (IllegalArgumentException e) {
          logger.error("Token nullo o vuoto", e);
          throw new Exception("Token nullo o vuoto", e);
      }
  }
  
  
  private String newToken(JSONObject dataJson) {
    // Recupero dei valori dai dati decodificati
    String codiceEform = dataJson.optString(NOTICE_SUB_TYPE);
    String uuidEform = dataJson.optString(NOTICE_UUID);
    String uuidEformRettificaPadre = dataJson.optString(NOTICE_RECT_UUID);
    String dataGetUrl = dataJson.optString(DATAGET_URL);
    String keepAliveUrl = dataJson.optString(KEEPALIVE_URL);
    String persistenceUrl = dataJson.optString(PERSISTENCE_URL);
    logger.debug("Codice eform ottenuto dal token: " + codiceEform);
    logger.debug("UUID ottenuto dal token: " + uuidEform);
    logger.debug("UUID rettifica padre ottenuto dal token: " + uuidEformRettificaPadre);
    logger.debug("dataGetUrl ottenuto dal token: " + dataGetUrl);
    logger.debug("keepAliveUrl ottenuto dal token: " + keepAliveUrl);
    logger.debug("persistenceUrl ottenuto dal token: " + persistenceUrl);

    HashMap<String,String> data = new HashMap<>();
    data.put(NOTICE_UUID, uuidEform);
    data.put(NOTICE_SUB_TYPE, codiceEform);
    data.put(NOTICE_RECT_UUID, uuidEformRettificaPadre);
    data.put(DATAGET_URL, dataGetUrl);
    data.put(KEEPALIVE_URL, keepAliveUrl);
    data.put(PERSISTENCE_URL, persistenceUrl);

    long expiration = System.currentTimeMillis() + 5 * 60000L; //5 minuti
    /*
    // 3. Preparazione dei dati per il nuovo token
    HashMap<String,String> tokenData = new HashMap<>();
    tokenData.put(NOTICE_SUB_TYPE, codiceEform);
    tokenData.put(notice_UUID, uuidEform);
    tokenData.put("urlChiamante", urlChiamante);

    // Imposta nuova scadenza: 5 minuti da ora
    long newExpiration = System.currentTimeMillis() + 5 * 60000L;
    */
    // 4. Rigenerazione del token (usando il metodo generateToken definito in JwtTokenUtilities)
    return JwtTokenUtilities.generateToken("Appalti", expiration, JWT_SECRET, data);
  }
  
  
  
  
  
  
  
  
  
  
  @GET
  @Path("data-test")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response generaFormatoEforms(String in, @Context HttpServletRequest request) {

    int livEvento = 1;
    String errMsgEvento = "";
    int status = 200;

    // JSON di uscita
    JSONObject outputJson = new JSONObject();
    //BOZZA o INIZIALIZZAZIONE
    //tracciato
    try {
      JSONObject dataJson = JSONObject.fromObject(in);

      // Recupera i valori di noticeSubType e noticeUUID dai dati decodificati
      String codiceEform = dataJson.optString(NOTICE_SUB_TYPE);
      String uuidEform = null;
      
      //outputJson.put("newToken", newToken(dataJson));
      
      FormatoExportEForm exportEForm = (FormatoExportEForm) UtilitySpring.getBean("formatoExportEForm", 
                                          SpringAppContext.getServletContext(), FormatoExportEForm.class);
      
      JSONObject tracciatoJson = new JSONObject();
      //String bozza = exportEForm.getBozzaByCodiceEformUuidEform(codiceEform, uuidEform);
      //if(bozza == null || bozza.isEmpty()) {
        // Elaborazione in base al codice eform
        switch (codiceEform) {
        case "1":
        case "2":
        case "4":
        case "5":
        case "7":
        case "8":
        case "10":
        case "11":
        case "12":
        case "13":
        case "14":
          tracciatoJson = exportEForm.creaJSONPin(codiceEform, uuidEform);
          break;

        case "15":
        case "16":
        case "17":
        case "18":
        case "19":
        case "20":
        case "21":
        case "22":
        case "23":
        case "24":
          tracciatoJson = exportEForm.creaJSONCn(codiceEform, uuidEform);
          break;

        case "25":
        case "26":
        case "27":
        case "28":
        case "29":
        case "30":
        case "31":
        case "32":
        case "33":
        case "34":
        case "35":
        case "36":
        case "37":
        case "38":
        case "39":
        case "40":
          tracciatoJson = exportEForm.creaJSONCan(codiceEform, uuidEform);
          break;

        default:
          break;
        }
        outputJson.put("tipoTracciato", "INIZIALIZZAZIONE");
      
      outputJson.put("tracciato", tracciatoJson);
      outputJson.put("success", true);
    } catch (Exception e) {
      // Gestione centralizzata delle eccezioni
      ExceptionHandlerResult ehResult = handleException(e, outputJson);
      status      = ehResult.getStatus();
      livEvento   = ehResult.getLivEvento();
      errMsgEvento = ehResult.getErrMsgEvento();
    } finally {
      // Tracciatura evento su LogEventiUtils
      try {
        LogEvento logEvento = new LogEvento();
        logEvento.setCodApplicazione("PG");
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento("eforms-data-test-get");
        logEvento.setCodEvento("");
        logEvento.setDescr("Response Richiesta");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception e) {
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", e);
      }
    }

    // Restituisco la risposta con lo stato HTTP corretto e il JSON di output
    return Response.status(status).entity(outputJson).build();
  }
  
  
  private Boolean isInizializzazione(String bozza) {
    return bozza == null || bozza.isEmpty();
  }
  
  
}

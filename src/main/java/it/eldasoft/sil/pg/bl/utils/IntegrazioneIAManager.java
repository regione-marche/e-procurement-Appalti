package it.eldasoft.sil.pg.bl.utils;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.keycloakclient.ITokenAuth;
import it.maggioli.keycloakclient.auth.EAuthToken;
import it.maggioli.keycloakclient.invoker.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import java.util.*;

import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getOptConfigurazionePerUfficioIntestatario;


/**
 * @author manuel.bridda
 *
 */
public class IntegrazioneIAManager {

  /**
   * Logger
   */
  static Logger logger = Logger.getLogger(IntegrazioneIAManager.class);


  public ResourceBundle resBundleGenerale = ResourceBundle.getBundle("AliceResources");

  public static JSONObject getDatiSensibili(String input, String uffint) throws GestoreException {

    JSONObject result = new JSONObject();
    //properties per il servizio IA
    String piiDetectorUrl = ConfigManager.getValore("ws.pii-detector.url");
    String piiDetectortoken = ConfigManager.getValore("ws.pii-detector.token");
    String piiDetectorTenant = getOptConfigurazionePerUfficioIntestatario("ws.pii-detector.tenant",uffint);
    String piiDetectorApp = getOptConfigurazionePerUfficioIntestatario("ws.pii-detector.applicazione",uffint);

    String error = null;
    if (StringUtils.isBlank(piiDetectortoken))
      error = "Il parametro 'Token di acceso' non e' definito 'ws.pii-detector.token'";
    if (StringUtils.isBlank(piiDetectorTenant)) {
      String append = "";
      if ("1".equals(ConfigManager.getValore("loginMultiEnte")))
        append = "/" + uffint;
      error = "Il parametro 'Nome tenant' non e' definito 'ws.pii-detector.token" + append + "'";
    }
    if (error != null)
      result.put("errore", UtilityStringhe.replaceParametriMessageBundle(ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE).getString("errors.integrazioneIA.error"), new String[]{"Integrazione IA: "+error}));
    else {

      JSONObject body = new JSONObject();
      body.put("text", input);

      Client client = ClientBuilder.newClient();
      client.property(ClientProperties.CONNECT_TIMEOUT, 10000);
      Response response = client.target(piiDetectorUrl).request(MediaType.APPLICATION_JSON_TYPE)
              .header("Authorization", "Bearer " + piiDetectortoken)
              .header("X-Client-Tenant",piiDetectorTenant)
              .header("X-Client-Application",piiDetectorApp)
              .post(Entity.json(body.toString()));

      if (Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
        result.put("array",response.readEntity(JSONArray.class).getJSONArray(0));
      } else {
        result.put("errore", "KO");
      }
    }

    return result;

  }

  public static JSONObject getDatiSensibiliOld(String input) throws GestoreException {

    JSONObject result = new JSONObject();
    //Header per integrazione keycloak + kong
    String keycloakBaseUrl = ConfigManager.getValore("keycloak.realms.baseurl");
    String keycloakRealm = ConfigManager.getValore("keycloak.progettoappalti.subpath");
    String keycloakClientId = ConfigManager.getValore("keycloak.progettoappalti.client.id");
    String keycloakClientSecret = ConfigManager.getValore("keycloak.progettoappalti.client.secret");
    //properties per il servizio IA
    String piiDetectorUrl = ConfigManager.getValore("ws.pii-detector.url");
    String piiDetectorLLM = ConfigManager.getValore("ws.pii-detector.llm");
    String piiDetectorTemperature = ConfigManager.getValore("ws.pii-detector.temperature");
    String piiDetectorMaxAttempt = ConfigManager.getValore("ws.pii-detector.max_attempt");
    String piiDetectorCatergories = ConfigManager.getValore("ws.pii-detector.categories");

    String error = null;
    if (StringUtils.isBlank(keycloakBaseUrl))
      error = "Il parametro 'Base URL per il sistema di autenticazione keycloak' non e' definito \"'keycloak.realms.baseurl\"";
    else if (StringUtils.isBlank(keycloakRealm))
      error = "Il parametro 'URL specifica del realm 'Progetto Appalti' non e' definito \"'keycloak.progettoappalti.subpath\"";
    else if (StringUtils.isBlank(keycloakClientId))
      error = "Il parametro 'Client id del realm 'Progetto Appalti' non e' definito \"'keycloak.progettoappalti.client.id\"";
    else if (StringUtils.isBlank(keycloakClientSecret))
      error = "Il parametro 'Secret del realm 'Progetto Appalti' non e' definito \"'keycloak.progettoappalti.client.secret\"";
    else if (StringUtils.isBlank(piiDetectorLLM))
      error = "Il parametro 'Nome modello' non e' definito \"'ws.pii-detector.llm\"";

    if (error != null)
      result.put("errore", UtilityStringhe.replaceParametriMessageBundle(ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE).getString("errors.integrazioneIA.error"), new String[]{error}));
    else {
      String authHeader = null;
      ITokenAuth tokenAuth = EAuthToken.newKongTokenBuilder()
              .withClientId(keycloakClientId)
              .withClientSecret(keycloakClientSecret)
              .withUrl(keycloakBaseUrl + keycloakRealm)   //Url base
              .withType(2)   //autenticazione clientId+clientSecret
              .build();
      try {
        authHeader = tokenAuth.getToken();
      } catch (ApiException e) {
        throw new GestoreException(
                "Errore durante la richiesta del token al servizio keycloak",
                "scambiodatiPCP.ws.keycloakErr");
      }

      JSONObject body = new JSONObject();
      body.put("text", input);
      body.put("llm_model_name", piiDetectorLLM);
      if (StringUtils.isNotBlank(piiDetectorTemperature))
        body.put("temperature", piiDetectorTemperature);
      if (StringUtils.isNotBlank(piiDetectorMaxAttempt))
        body.put("max_attempt", piiDetectorMaxAttempt);
      if (StringUtils.isNotBlank(piiDetectorCatergories))
        body.put("categories", piiDetectorCatergories);

      Client client = ClientBuilder.newClient();
      client.property(ClientProperties.CONNECT_TIMEOUT, 10000);
      Response response = client.target(piiDetectorUrl).request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + authHeader).post(Entity.json(body.toString()));

      if (Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
        result.put("array",response.readEntity(JSONArray.class));
      } else {
        result.put("errore", "KO");
      }
    }

    return result;

  }
}
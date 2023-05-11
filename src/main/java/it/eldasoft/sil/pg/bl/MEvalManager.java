/*
 * Created on 18 ott 2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.util.HashMap;
import java.util.Map;

import it.appaltiecontratti.appalticgmsclient.api.V10Api;
import it.appaltiecontratti.appalticgmsclient.invoker.ApiClient;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.utils.properties.ConfigManager;

public class MEvalManager {

  public static final String PROP_INTEGRAZIONE_MEVAL_URL_FRONTEND           = "appCg.url";
  public static final String PROP_INTEGRAZIONE_MEVAL_URL_BACKEND            = "appCg.ws.url";
  public static final String PROP_INTEGRAZIONE_MEVAL_OBBLIGO_PRESIDENTE     = "appCg.obbligoPresidente";
  public static final String PROP_INTEGRAZIONE_MEVAL_EXP_TOKEN_COMMISSARIO  = "appCg.ws.jwtkey-expiration";

  private final static String apiKeyPrefix = "Bearer";

  /**
   * Il metodo costruisce il client per accedere ai servizi dell'app M-EVAL. Per fare ciò viene generato il token JWT di autenticazione,
   * col codice fiscale della stazione appaltante e con la durata specificata
   * @param cfSa
   * @param durataToken
   * @return V10Api
   * @throws GestoreException
   */
  public static V10Api getClient(String cfSa, int durataToken) throws GestoreException {
    long expiration = System.currentTimeMillis() + durataToken * 60000l;
    Map<String, Object> claims = new HashMap<String, Object>();
    claims.put("USER_CF", cfSa);

    String chiaveSegretaJwtChiaro = JwtTokenUtilities.getChiaveSegretaJwtChiaro();

    String token = JwtTokenUtilities.generateTokenWithClaims("Appalti", expiration, chiaveSegretaJwtChiaro,claims);

    ApiClient apiClientSignatureCheck = new ApiClient();
    String urlMeval = ConfigManager.getValore(PROP_INTEGRAZIONE_MEVAL_URL_BACKEND);
    apiClientSignatureCheck.setBasePath(urlMeval);
    apiClientSignatureCheck.setApiKeyPrefix(apiKeyPrefix);
    apiClientSignatureCheck.setApiKey(token);
    V10Api apiAppaltiCgMs = new V10Api(apiClientSignatureCheck);

    return apiAppaltiCgMs;
  }



}

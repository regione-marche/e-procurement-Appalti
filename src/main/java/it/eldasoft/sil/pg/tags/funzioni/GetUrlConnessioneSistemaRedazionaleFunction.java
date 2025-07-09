package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.maggioli.keycloakclient.ITokenAuth;
import it.maggioli.keycloakclient.auth.EAuthToken;
import it.maggioli.keycloakclient.invoker.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getOptConfigurazionePerUfficioIntestatario;

public class GetUrlConnessioneSistemaRedazionaleFunction extends AbstractFunzioneTag {

	private static final Object PROP_CLIENT_TIMEOUT = 5000;

	public GetUrlConnessioneSistemaRedazionaleFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		Logger logger = Logger.getLogger(GetUrlConnessioneSistemaRedazionaleFunction.class);

		HttpSession session = pageContext.getSession();
		String codein = (String) session.getAttribute("uffint");
		String attivaIntegrazione = getOptConfigurazionePerUfficioIntestatario("sistemaRedazionale.attivaIntegrazione", codein);

		if (!"1".equals(attivaIntegrazione)) {
			return null;
		}

		String nomeApplication = getOptConfigurazionePerUfficioIntestatario("sistemaRedazionale.nomeApplication", codein);
		String cfEnte = getOptConfigurazionePerUfficioIntestatario("sistemaRedazionale.codiceEnte", codein);
		String clientSecret = getOptConfigurazionePerUfficioIntestatario("sistemaRedazionale.clientSecret", codein);
		String urlKeyCloak = getOptConfigurazionePerUfficioIntestatario("sistemaRedazionale.urlKeyCloak", codein);
		String urlAPI = getOptConfigurazionePerUfficioIntestatario("sistemaRedazionale.urlAPI", codein);

		ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
		String cfUtente = profiloUtente.getCodiceFiscale();
		String cfUtenteHashato;
		if (StringUtils.isNotEmpty(cfUtente))
			cfUtenteHashato = UtilityTags.genSha256(cfUtente);
		else
			cfUtenteHashato = UtilityTags.genSha256(cfEnte);

		MultivaluedMap <String,Object> parametriHeader = new MultivaluedHashMap<>();
		if(StringUtils.isNotBlank(urlKeyCloak)) {
			ITokenAuth tokenAuth = EAuthToken.newKongTokenBuilder()
					.withClientId(nomeApplication)
					.withClientSecret(clientSecret)
					.withUrl(urlKeyCloak)   //Url base
					.withType(2)   //autenticazione clientId+clientSecret
					.build();
			try {
				parametriHeader.putSingle("Authorization", "Bearer "+tokenAuth.getToken());
			} catch (ApiException e) {
				logger.error("Errore durante la chiamata al servizio KeyCloak del sistema redazionale: " + e.getMessage(), e);
				return null;
			}
		}

		JSONObject body = new JSONObject();
		body.put("username", nomeApplication + "_" + cfEnte + "_" + cfUtenteHashato);

		JSONObject context = new JSONObject();
		context.put("application", nomeApplication);
		String isFlat = getOptConfigurazionePerUfficioIntestatario("sistemaRedazionale.isForfettario", codein);
		if ("1".equals(isFlat))
			context.put("flat", "true");

		String isTestUser = getOptConfigurazionePerUfficioIntestatario("sistemaRedazionale.isTestUser", codein);
		if ("1".equals(isTestUser))
			context.put("test_user", "true");

		JSONArray aree = new JSONArray();
        aree.add(403L);
		context.put("aree", aree);

		body.put("context", context.toString());

		String urlPagina = "";
		try {
			Client client = ClientBuilder.newClient();
			client.property(ClientProperties.CONNECT_TIMEOUT, PROP_CLIENT_TIMEOUT);
			WebTarget webTarget = client.target(urlAPI);
			Builder builder = webTarget.request(MediaType.APPLICATION_JSON).headers(parametriHeader);
			Response response = builder.post(Entity.json(body.toString()));
			JSONObject resp = response.readEntity(JSONObject.class);
			boolean status = resp.getBoolean("status");
			if (!status) {
				throw new JspException("Errore nella lettura del sistema redazionale");
			}
			urlPagina = resp.getString("url");
			client.close();
		} catch (Throwable e) {
			logger.error("Errore durante la chiamata al servizio del sistema redazionale: " + e.getMessage(), e);
			return null;
		}

		return urlPagina;
	}

}

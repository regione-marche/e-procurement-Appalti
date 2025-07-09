package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.glassfish.jersey.client.ClientProperties;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getConfigurazionePerUfficioIntestatario;

public class LeggiDocumentiBICAction extends DispatchActionAjaxLogged {

	static Logger logger = Logger.getLogger(LeggiDocumentiBICAction.class);

	private static final String CONFIG_LFS_WS_URL = "lfs.ws.url";
	private static final String CONFIG_LFS_WS_UTENTE = "lfs.ws.utente";
	private static final String CONFIG_LFS_WS_PASSWORD = "lfs.ws.password";
	private static final String COMMESSA_APPALTO = "commessaAppalto";
	private static final String CODICE_COMMESSA = "codiceCommessa";
	private static final String NUMERO_APPALTO = "numeroAppalto";
	private static final String EDIFICIO = "edificio";
	private static final String ESITO = "esito";
	private static final String MESSAGGIO = "messaggio";

	private SqlManager sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public final ActionForward getLista(final ActionMapping mapping,
										final ActionForm form, final HttpServletRequest request,
										final HttpServletResponse response) {

		if (logger.isDebugEnabled()) {
			logger.debug("LeggiDocumentiBICAction: inizio metodo");
		}
		request.setAttribute("documenti", getJSONDaServizio(request,true));

		if (logger.isDebugEnabled()) {
			logger.debug("LeggiDocumentiBICAction: fine metodo");
		}
		request.setAttribute("RISULTATO", "OK");
		return mapping.findForward("result");
	}

	private JSONObject getJSONDaServizio(HttpServletRequest request, boolean tracciaLogeventi) {
		JSONObject jsonReturn = new JSONObject();

		String codein = StringUtils.trimToNull(request.getParameter("sa"));
		String codiceCommessa = request.getParameter(CODICE_COMMESSA);
		String numeroAppalto = request.getParameter(NUMERO_APPALTO);
		JSONObject body = new JSONObject();

		//aggiungere filtro per stazione appaltante nel caso di codein valorizzato? "codiceFiscaleSa":valore da recuperare su uffint",
		if (StringUtils.isNotBlank(codiceCommessa)) {
			body.put(CODICE_COMMESSA, codiceCommessa);
		}

		if (StringUtils.isNotBlank(numeroAppalto)) {
			body.put(NUMERO_APPALTO, numeroAppalto);
		}

		String lfsUrl = getConfigurazionePerUfficioIntestatario(CONFIG_LFS_WS_URL,codein);
		String lfsUtente = getConfigurazionePerUfficioIntestatario(CONFIG_LFS_WS_UTENTE,codein);
		String lfsPassword = getConfigurazionePerUfficioIntestatario(CONFIG_LFS_WS_PASSWORD,codein);

		String codeinToLog="";
		if(codein!=null)
			codeinToLog="/"+codein;

		boolean procedi = true;
		if (StringUtils.isBlank(lfsUrl)) {
			jsonReturn.put(ESITO, false);
			jsonReturn.put(MESSAGGIO, "La proprietà '" + CONFIG_LFS_WS_URL + codeinToLog + "' non è configurata");
			procedi = false;
		}
		if (StringUtils.isBlank(lfsUtente)) {
			jsonReturn.put(ESITO, false);
			jsonReturn.put(MESSAGGIO, "La proprietà '" + CONFIG_LFS_WS_UTENTE + codeinToLog + "' non è configurata");
			procedi = false;
		}
		if (StringUtils.isBlank(lfsPassword)) {
			jsonReturn.put(ESITO, false);
			jsonReturn.put(MESSAGGIO, "La proprietà '" + CONFIG_LFS_WS_PASSWORD + codeinToLog + "' non è configurata");
			procedi = false;
		}
		JSONObject data = new JSONObject();
		boolean datiElaborati=false;
		String codgar = request.getParameter("codgar");
		if (codgar != null && codgar.startsWith("$"))
			codgar = codgar.substring(1);

		if (procedi) {
			if(tracciaLogeventi) {
				JSONObject bodyPerLog = JSONObject.fromObject(body.toString());
				if(StringUtils.isNotBlank(codein)) {
					bodyPerLog.put("configurazioneUtilizzata", CONFIG_LFS_WS_URL + codeinToLog);
				}
				LogEvento logEventoREQ = LogEventiUtils.createLogEvento(request);
				logEventoREQ.setCodEvento("GA_LFS_REQ_URLBIC");
				logEventoREQ.setLivEvento(1);
				logEventoREQ.setOggEvento(codgar);
				logEventoREQ.setDescr("Invio richiesta a LFS - lettura documenti edifici BIC");
				logEventoREQ.setErrmsg(bodyPerLog.toString());
				LogEventiUtils.insertLogEventi(logEventoREQ);
			}
			try {
				String passwordDecoded = null;
				if (lfsPassword != null && lfsPassword.trim().length() > 0) {
					ICriptazioneByte passwordICriptazioneByte = null;
					passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
							ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), lfsPassword.getBytes(),
							ICriptazioneByte.FORMATO_DATO_CIFRATO);
					passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
				}

				Client client = ClientBuilder.newClient();
				client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
				String authString = lfsUtente + ":" + passwordDecoded;
				byte[] authByte = authString.getBytes("UTF-8");
				String authHeader = "Basic " + DatatypeConverter.printBase64Binary(authByte);
				Response resp = client.target(lfsUrl).request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", authHeader).post(Entity.json(body.toString()));
				if (Response.Status.Family.SUCCESSFUL.equals(resp.getStatusInfo().getFamily())) {
					jsonReturn = resp.readEntity(JSONObject.class);
					if (jsonReturn.has(COMMESSA_APPALTO)) {
						data.put(ESITO, jsonReturn.get(ESITO));
						List<JSONObject> listaEdifici = new ArrayList<JSONObject>();
						if (jsonReturn.get(COMMESSA_APPALTO) instanceof JSONArray) {
							JSONArray commesse = jsonReturn.getJSONArray(COMMESSA_APPALTO);
							for (int i = 0; i < commesse.size(); i++) {
								listaEdifici.addAll(explodeEdifici(commesse.getJSONObject(i)));
							}
						} else {
							listaEdifici.addAll(explodeEdifici(jsonReturn.getJSONObject(COMMESSA_APPALTO)));
						}
						data.put("data", listaEdifici.toArray());
						datiElaborati = true;
					}
				} else {
					jsonReturn.put(ESITO, false);
					jsonReturn.put(MESSAGGIO, resp.getStatusInfo().getReasonPhrase());
				}
				client.close();
			} catch (Throwable e) {
				String messaggioPerLog = e.getMessage();
				logger.error(messaggioPerLog, e);
				jsonReturn.put(ESITO, false);
				jsonReturn.put(MESSAGGIO, messaggioPerLog);
			}
			if(tracciaLogeventi) {
				LogEvento logEventoRES = LogEventiUtils.createLogEvento(request);
				logEventoRES.setCodEvento("GA_LFS_RES_URLBIC");
				logEventoRES.setLivEvento(1);
				logEventoRES.setOggEvento(codgar);
				logEventoRES.setDescr("Risposta LFS - lettura documenti edifici BIC");
				logEventoRES.setErrmsg(jsonReturn.toString());
				LogEventiUtils.insertLogEventi(logEventoRES);
			}
		}

		return datiElaborati? data: jsonReturn;
	}

	public ActionForward setDocumgara(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {


		DataSourceTransactionManagerBase.setRequest(request);

		JSONObject jsonReturn = new JSONObject();

		String codgar = request.getParameter("codgar");
		String ngara = request.getParameter("ngara");
		Long gruppo = Long.parseLong(request.getParameter("gruppo"));
		Long tipologia  = Long.parseLong(request.getParameter("tipologia"));
		String allmail=null;
		if(Long.valueOf(6).equals(gruppo))
			allmail="2";
		try {
			TransactionStatus status = null;
			boolean commitTransaction = false;
			try {
				List<String> documenti =  Arrays.asList((String[])request.getParameterMap().get("documentiSelezionati"));
				jsonReturn = getJSONDaServizio(request,false);
				JSONArray array = jsonReturn.optJSONArray("data");
				if(array!=null) {
					status = this.sqlManager.startTransaction();
					for (int i = 0; i < array.size(); i++) {
						JSONObject obj = array.getJSONObject(i);
						String codiceFiscaleSa = obj.getString("codiceFiscaleSa");
						String codiceCommessa = obj.getString("codiceCommessa");
						Integer numeroAppalto = obj.getInt("numeroAppalto");
						Integer numeroEdificio = obj.getInt("numeroEdificio");
						Integer numeroCantiere = obj.getInt("numeroCantiere");

						if (documenti.contains(codiceFiscaleSa + "#" + codiceCommessa + "#" + numeroAppalto + "#" + numeroCantiere + "#" + numeroEdificio)) {
							String codiceEdificio = obj.getString("codiceEdificio");
							String codiceOrganizzazione = obj.getString("codiceOrganizzazione");
							String utenteBic = obj.getString("utenteBic");
							String codicecartella = obj.getString("codicecartella");
							String urlDoc = obj.getString("urlDoc");
							String descrizione = "Documentazione tecnica CDE - building " + codiceEdificio + " (Stazione appaltante: " + codiceFiscaleSa + " - Commessa: " + codiceCommessa + "/" + numeroAppalto+")";
							String ultbic = codiceOrganizzazione + "|" + codiceEdificio + "|" + utenteBic + "|" + codicecartella;


							Long maxProgressivoNORDDOCG = (Long) this.sqlManager.getObject("select max(NORDDOCG) from DOCUMGARA where CODGAR=?", new Object[]{codgar});
							long newProgressivoNORDDOCG = 1;

							if (maxProgressivoNORDDOCG != null && maxProgressivoNORDDOCG.longValue() > 0) {
								newProgressivoNORDDOCG = maxProgressivoNORDDOCG.longValue() + 1;
							}
							Long numord = (Long) this.sqlManager.getObject("select max(NUMORD) from DOCUMGARA where CODGAR=? and GRUPPO=?", new Object[]{codgar, gruppo});
							long newnumord = 1;
							if (numord != null && numord.longValue() > 0) {
								newnumord = numord.longValue() + 1;
							}

							String insertDocumgara = "insert into documgara(codgar,ngara,norddocg,gruppo,tipologia,idprg,descrizione,urldoc,ultbic,numord,allmail) values(?,?,?,?,?,?,?,?,?,?,?)";
							this.sqlManager.update(insertDocumgara, new Object[]{codgar, ngara, newProgressivoNORDDOCG, gruppo, tipologia, "PG", descrizione, urlDoc, ultbic, newnumord,allmail});
						}
					}
					commitTransaction = true;
					request.setAttribute("COMPLETED", "OK");
				}

			} catch (Exception e) {
				commitTransaction = false;
				String messaggioPerLog = e.getMessage();
				logger.error(messaggioPerLog, e);
				jsonReturn.put(ESITO, false);
				jsonReturn.put(MESSAGGIO, messaggioPerLog);
				request.setAttribute("documenti", jsonReturn);
			} finally {
				if (status != null) {
					if (commitTransaction) {
						this.sqlManager.commitTransaction(status);
					} else {
						this.sqlManager.rollbackTransaction(status);
					}
				}
			}
		} catch (Throwable e) {
			String messaggioPerLog = e.getMessage();
			logger.error(messaggioPerLog, e);
			jsonReturn.put(ESITO, false);
			jsonReturn.put(MESSAGGIO, messaggioPerLog);
			request.setAttribute("documenti", jsonReturn);
		}
		request.setAttribute("RISULTATO", "OK");

		return  mapping.findForward("result");
	}

	private static List<JSONObject> explodeEdifici(JSONObject commessa){
		List<JSONObject> array = new ArrayList<>();
		if(commessa.get(EDIFICIO) instanceof JSONArray){
			JSONArray edifici = commessa.getJSONArray(EDIFICIO);
			for(int i=0; i<edifici.size();i++) {
				JSONObject edificio = JSONObject.fromObject(edifici.getJSONObject(i).toString());
				array.add(enrichEdificio(commessa,edificio));
			}
		}else{
			array.add(enrichEdificio(commessa, JSONObject.fromObject(commessa.getJSONObject(EDIFICIO).toString())));
		}
		return array;
	}

	private static JSONObject enrichEdificio(JSONObject commessa, JSONObject edificio) {
		edificio.put("codiceFiscaleSa", commessa.get("codiceFiscaleSa"));
		edificio.put("oggettoAppalto", commessa.get("oggettoAppalto"));
		edificio.put(CODICE_COMMESSA, commessa.get(CODICE_COMMESSA));
		edificio.put(NUMERO_APPALTO, commessa.get(NUMERO_APPALTO));
		return edificio;
	}
}

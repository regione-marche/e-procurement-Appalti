package it.eldasoft.sil.pg.bl.npa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import it.eldasoft.sil.pg.web.struts.npa.ScambioDatiPCPAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import net.sf.json.JSONObject;
import org.springframework.transaction.TransactionStatus;

public class GestisciInvioFEUManager {

	static Logger logger = Logger.getLogger(GestisciInvioFEUManager.class);

	private static final String PROP_BANDO_AVVISO_SIMAP_WS_URL = "it.eldasoft.bandoavvisosimap.ws.url";

	public static final String SCHEDA_PUBBLICATA = "PUBBLICAZIONE_SCHEDA";
	public static final String SCHEDA_ANNULLATA = "CANCELLA_SCHEDA";
	public static final String SCHEDA_INVIATA = "INVIA_SCHEDA";

	public static final String STATO_PUBBLICATO = "PUBLISHED";
	public static final String STATO_ERRORE = "VALIDATION_FAILED";
	public static final String STATO_INVIATO = "SUBMITTED";

	public static final String PROP_TOKEN_ACCESSO_FEU = "feu.token";

	public static final HashMap<String, String> MAPPA_METODI = new HashMap<String, String>() {{
		put(ScambioDatiPCPAction.CREAPPALTO, GestisciInvioFEUManager.SCHEDA_INVIATA);
		put(ScambioDatiPCPAction.CREASCHEDA, GestisciInvioFEUManager.SCHEDA_INVIATA);
		put(ScambioDatiPCPAction.CREAPIANO, GestisciInvioFEUManager.SCHEDA_INVIATA);
		put(ScambioDatiPCPAction.RETTIFICAAVVISO, GestisciInvioFEUManager.SCHEDA_INVIATA);
		put(ScambioDatiPCPAction.CONSULTAAVVISO, GestisciInvioFEUManager.SCHEDA_PUBBLICATA);
		put(ScambioDatiPCPAction.CANCELLAAPPALTO, GestisciInvioFEUManager.SCHEDA_ANNULLATA);
		put(ScambioDatiPCPAction.CANCELLASCHEDA, GestisciInvioFEUManager.SCHEDA_ANNULLATA);
		put(ScambioDatiPCPAction.CANCELLAPIANO, GestisciInvioFEUManager.SCHEDA_ANNULLATA);
		put(ScambioDatiPCPAction.CANCELLAAVVISO, GestisciInvioFEUManager.SCHEDA_ANNULLATA);
	}};

	private SqlManager sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	private FileAllegatoManager fileAllegatoManager;

	public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
		this.fileAllegatoManager = fileAllegatoManager;
	}

	private NpaFunzioniManager npaFunzioniManager;

	public void setNpaFunzioniManager(NpaFunzioniManager npaFunzioniManager) {
		this.npaFunzioniManager = npaFunzioniManager;
	}

	public Object[] gestisciInvio(HttpServletRequest request, Long idForm, String metodo) throws Throwable {

		String navpub = request.getParameter("navpub");

		JSONObject parametriRichiesta = new JSONObject();

		String uuid = getUid(idForm);

		Vector<?> datiScheda = sqlManager.getVector("select g1npascheda.idprg_eform, g1npascheda.iddoc_eform, g1npaform.codgar, g1cf_npa.eform, g1cf_npa.nome_scheda,"
				+ " g1npascheda.uuid_eform, g1npascheda.id"
				+ " from g1npaform, g1npascheda, g1cf_npa"
				+ " where g1npaform.id=? and g1npaform.id = g1npascheda.idform and g1cf_npa.id = g1npaform.idcfnpa", new Object[] { idForm });
		String idprg = ((JdbcParametro) datiScheda.get(0)).getStringValue();
		Long iddcodig = ((JdbcParametro) datiScheda.get(1)).longValue();
		String codgar = ((JdbcParametro) datiScheda.get(2)).getStringValue();
		String eform = ((JdbcParametro) datiScheda.get(3)).getStringValue();
		String nomeScheda = ((JdbcParametro) datiScheda.get(4)).getStringValue();
		String uuidEForm = ((JdbcParametro) datiScheda.get(5)).getStringValue();
		Long idScheda = ((JdbcParametro) datiScheda.get(6)).longValue();

		String ngara = null;
		if (codgar != null && codgar.startsWith("$")) {
			ngara = codgar.substring(1);
		} else {
			List<Vector<?>> datiGenere = (List<Vector<?>>) sqlManager.getListVector("select codgar, codice, genere from v_gare_genere where codgar=? order by genere asc", new Object[] {codgar});
			if (datiGenere != null && datiGenere.size() > 1) {
				Long genere = ((JdbcParametro) (datiGenere.get(0).get(2))).longValue();
				if (genere != null && (genere).equals(3L)) {
					ngara = ((JdbcParametro) (datiGenere.get(0).get(1))).getStringValue();
				} else if (genere != null && (genere).equals(1L)) {
					ngara = ((JdbcParametro) (datiGenere.get(1).get(1))).getStringValue();
				}
			}
		}

		BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, iddcodig);
		byte[] xmlByte = digogg.getStream();
		String xmlEnc = DatatypeConverter.printBase64Binary(xmlByte);

		String stato = null;
		if (SCHEDA_PUBBLICATA.equals(metodo)) {
			stato = STATO_PUBBLICATO;

			Boolean isBando = "P".equals(nomeScheda.substring(0, 1));
			Boolean isEsito = "A".equals(nomeScheda.substring(0, 1));
			Vector<?> datiPubblicazione = null;
			if (isBando) {
				datiPubblicazione = sqlManager.getVector("select navpub, urlpub, datpub from pubbli where codgar9=? and titpub like 'eForm " + eform + " %' and navpub=?", new Object [] { codgar,navpub });
			} else if (isEsito) {
				datiPubblicazione = sqlManager.getVector("select navpub, urlpub, dinpubg from pubg where ngara=? and titpub like 'eForm " + eform + " %' and navpub=?", new Object [] { ngara,navpub });
			}
			String pubblicationID = ((JdbcParametro) datiPubblicazione.get(0)).getStringValue();
			String pubblicationURL = ((JdbcParametro) datiPubblicazione.get(1)).getStringValue();
			Date dataPubblicazione = (Date) ((JdbcParametro) datiPubblicazione.get(2)).getValue();

			String dataFormatoANAC = npaFunzioniManager.getStringDateFormat(dataPubblicazione);
			String dataFormatoFEU = null;
			if (dataPubblicazione != null) {
				dataFormatoFEU = dataFormatoANAC.substring(0,4) + dataFormatoANAC.substring(5,7) + dataFormatoANAC.substring(8,10);
			}

			parametriRichiesta.put("notice_number_oj", pubblicationID);
			parametriRichiesta.put("ted_link", pubblicationURL);
			parametriRichiesta.put("date_oj", dataFormatoFEU);

		} else if (SCHEDA_ANNULLATA.equals(metodo)) {
			stato = STATO_ERRORE;
		} else if (SCHEDA_INVIATA.equals(metodo)) {
			stato = STATO_INVIATO;
		}

		parametriRichiesta.put("uuid", uuid);
		parametriRichiesta.put("status_code", stato);
		parametriRichiesta.put("eforms_xml", xmlEnc);

		HttpURLConnection conn = setConnessione();

		OutputStream os = conn.getOutputStream();
		String jsonStrg = parametriRichiesta.toString();
		os.write(jsonStrg.getBytes("UTF-8"));
		os.write(xmlEnc.getBytes());
		os.flush();

		String codiceGara = codgar;
		if (codiceGara.startsWith("$")) {
			codiceGara = codiceGara.substring(1);
		}
		int livEvento = 1;
		String codEvento = "GA_FEU_REQ_MODSTATO";
		String oggEvento = codiceGara;
		String descrEvento = "Invio richiesta a FEU - MODIFICA STATO \'" + stato + "\' EFORM " + eform + " (id.avviso " + uuidEForm + " - id.scheda " + idScheda + ")";
		String errMsgEvento = jsonStrg;

    	LogEvento logEvento=null;
		if(request==null) {
			logEvento = new LogEvento();
		} else {
			logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) request);
		}
		logEvento.setCodApplicazione("PG");
		logEvento.setLivEvento(livEvento);
		logEvento.setOggEvento(oggEvento);
		logEvento.setCodEvento(codEvento);
		logEvento.setDescr(descrEvento);
		logEvento.setErrmsg(errMsgEvento);
		LogEventiUtils.insertLogEventi(logEvento);

		int risposta = conn.getResponseCode();
		System.out.println("response code: " + risposta);
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		String res = "";
		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			res = res + output;
		}

		codEvento = "GA_FEU_RES_MODSTATO";
		descrEvento = "Risposta FEU - MODIFICA STATO \'" + stato + "\' EFORM " + eform + " (id.avviso " + uuidEForm + " - id.scheda " + idScheda + ")";
		errMsgEvento = res;

		if(request==null) {
			logEvento = new LogEvento();
		} else {
			logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) request);
		}
		logEvento.setCodApplicazione("PG");
		logEvento.setLivEvento(livEvento);
		logEvento.setOggEvento(oggEvento);
		logEvento.setCodEvento(codEvento);
		logEvento.setDescr(descrEvento);
		logEvento.setErrmsg(errMsgEvento);
		LogEventiUtils.insertLogEventi(logEvento);

		conn.disconnect();

		JSONObject jsonResult = JSONObject.fromObject(res);
		boolean esito = (Boolean) jsonResult.get("esito");
		String messaggio = "";
		if (!esito) {
			messaggio = (String) jsonResult.get("messaggio");
			logger.error("inviabandoavvisosimap ERROR : " + messaggio);
		}

		if (SCHEDA_PUBBLICATA.equals(metodo)) {
			TransactionStatus status = sqlManager.startTransaction();
			boolean commitTransaction = false;
			try {
				sqlManager.update("update g1npascheda set ispubted='1' where idform=?", new Object[]{idForm});
				commitTransaction = true;
			} catch (SQLException e) {
				commitTransaction = false;
				throw new Exception("Errore durante l'aggiornamento stato pubblicazione della scheda idForm=" + idForm);
			} finally {
				try {
					if (status != null) {
						if (commitTransaction) {
							this.sqlManager.commitTransaction(status);
						} else {
							this.sqlManager.rollbackTransaction(status);
						}
					}
				} catch (SQLException e) {
					throw new Throwable("Errore durante la chiusura della transazione", e);
				}
			}
		}

		return new Object[] {esito, messaggio};
	}

	public String getUid(Long idForm) throws Throwable {
		String uidQuery = "select UUID_EFORM from G1NPASCHEDA where idform=?";
		String uuid = (String) sqlManager.getObject(uidQuery, new Object[] { idForm });
		return uuid;
	}

	private HttpURLConnection setConnessione() throws Throwable {
		Vector<?> datiAdmin = (Vector) sqlManager.getVector("select sysnom, syspwd from usrsys where syscon = 48", null);
		String usernameCifrato = ((JdbcParametro) datiAdmin.get(0)).getStringValue();
		String username = null;
		String password = ConfigManager.getValore(PROP_TOKEN_ACCESSO_FEU);
		if (StringUtils.isEmpty(password)) {
			throw new Exception("Valorizzare il token per l\'accesso ai servizi (property \'feu.token\')");
		}

		ICriptazioneByte decriptatore;
		ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);
		try {
			decriptatore = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
					usernameCifrato.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
			username = new String(decriptatore.getDatoNonCifrato());
		} catch (CriptazioneException e) {
			logger.error(resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
			throw new Throwable(resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
		}

		String authString = username + ":" + password;
		byte[] authByte = authString.getBytes("UTF-8");
		String authStringEnc = DatatypeConverter.printBase64Binary(authByte);

		String urlString = ConfigManager.getValore(PROP_BANDO_AVVISO_SIMAP_WS_URL);
		if (urlString == null || "".equals(urlString)) {
			throw new GestoreException("L'indirizzo per la connessione al web service non e' definito",
					"inviabandoavvisosimap.ws.url");
		}
		if (urlString.endsWith("/"))
			urlString = urlString.substring(0, urlString.length() - 1);

		urlString = urlString + "/gestisciinviotoken";
		
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

		return conn;
	}
}

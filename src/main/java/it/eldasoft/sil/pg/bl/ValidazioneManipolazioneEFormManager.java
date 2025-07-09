package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.sil.pg.bl.npa.ScambioDatiPCPManager;
import it.eldasoft.utils.properties.ConfigManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.axis.encoding.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ValidazioneManipolazioneEFormManager {

	static Logger logger = Logger.getLogger(ValidazioneManipolazioneEFormManager.class);

	private SqlManager sqlManager;
	private FileAllegatoManager fileAllegatoManager;

	private static final String TED_CVS_URL_PROP                 = "eforms.ted.cvs.url";
	private static final String TED_XAPIKEY_PROP                 = "eforms.ted.xapikey";
	private static final String VALIDATION_EFORMSSDKVERSION_PROP = "eforms.sdkVersion";
	private static final String VALIDATION_LANGUAGE_PROP         = "eforms.cvs.language";

	private static final String VALIDATION_MODE              = "static";
	private static final String VALIDATION_API               = "/v1/notices/validation";
	private static final String VALIDATION_LANGUAGE_DEFAULT  = "en";

	private static final String ERRORE_VALORIZZARE_PROPERTY_CVS = "Valorizzare l'indirizzo URL del servizio TED CVS e la chiave X-API-KEY di accesso al servizio";

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
		this.fileAllegatoManager = fileAllegatoManager;
	}

	public Object[] controllaAggiornaEform(String eform_idprg, Long eform_iddoc, Map<String,Object> parameters) throws Exception {

		String avvisi = "";
		BlobFile digogg = fileAllegatoManager.getFileAllegato(eform_idprg, eform_iddoc);
		String eform = new String(digogg.getStream(), StandardCharsets.UTF_8);

		Document eFormParsed = parseEForm(eform);
		boolean fallitoControlloRettifica = false;
		if (parameters.get(ScambioDatiPCPManager.ID_SCHEDA_NPA_RET) != null) {
			NodeList changesNodes = eFormParsed.getElementsByTagName("efac:Changes");
			fallitoControlloRettifica = changesNodes == null || changesNodes.getLength() == 0;
		}
		String numeroEForm = getNumeroeForm(eFormParsed);
		String eFormsPossibili = (String) sqlManager.getObject("select eform from g1cf_npa,g1npaform where g1cf_npa.id=g1npaform.idcfnpa and g1npaform.id=?", new Object[] {parameters.get(ScambioDatiPCPManager.ID_FORM)});
		boolean numeroEFormIncoerente = false;
		if (StringUtils.isNotEmpty(eFormsPossibili)) {
			List<?> listaEFormsPossibili = Arrays.asList(eFormsPossibili.split(","));
			numeroEFormIncoerente = !(listaEFormsPossibili.contains(numeroEForm));
		}

		JSONArray erroriValidazioneSDK = new JSONArray();
		try {
			if (!fallitoControlloRettifica && !numeroEFormIncoerente)
				erroriValidazioneSDK = validazioneSDKEForm(eform);
		} catch (Exception e) {
			avvisi += "Non è stato possibile eseguire la validazione TED CVS dell'eForm: " + e.getMessage();
		}

		String versioneSDKeForm = getVersioneSDKeForm(eFormParsed);
		if (versioneSDKeForm != null && !versioneSDKeForm.equals(ConfigManager.getValore(VALIDATION_EFORMSSDKVERSION_PROP))) {
			avvisi += (StringUtils.isNotBlank(avvisi) ? "<br><br>" : "") + "ATTENZIONE: il file eForm allegato non è in versione " + ConfigManager.getValore(VALIDATION_EFORMSSDKVERSION_PROP) +
					"! ANAC accetta file esclusivamente in versione " + ConfigManager.getValore(VALIDATION_EFORMSSDKVERSION_PROP) +
					". Il software effetturà un downgrade a questa versione, tuttavia potrebbe non essere garantita la piena compatibilità";
		}
		if (!ScambioDatiPCPManager.MODALITA_VERIFICA.equals(parameters.get(ScambioDatiPCPManager.MODALITA_INVIO))) {
			manipolaEForm(eFormParsed, eform, versioneSDKeForm, eform_idprg, eform_iddoc);
		}

		return new Object[] {erroriValidazioneSDK, fallitoControlloRettifica, numeroEFormIncoerente, avvisi};
	}

	private String getNumeroeForm(Document eFormParsed) {
		String numeroEForm = null;
		NodeList subTypeCode = eFormParsed.getElementsByTagName("cbc:SubTypeCode");
		if (subTypeCode.getLength() > 0) {
			Node subTypeCodeNode = subTypeCode.item(0);
			numeroEForm = subTypeCodeNode.getTextContent();
		}
		return numeroEForm;
	}

	private String getVersioneSDKeForm(Document eFormParsed) {
		String versione = null;
		NodeList customizationIDNodes = eFormParsed.getElementsByTagName("cbc:CustomizationID");
		Node node = getChildOfNoticeNode(customizationIDNodes);
		if (node != null) {
			versione = node.getTextContent();
		}
		return versione;
	}

	private void setVersioneSDKeForm(Document eFormParsed) {
		NodeList customizationIDNodes = eFormParsed.getElementsByTagName("cbc:CustomizationID");
		Node node = getChildOfNoticeNode(customizationIDNodes);
		if (node != null) {
			node.setTextContent(ConfigManager.getValore(VALIDATION_EFORMSSDKVERSION_PROP));
		}
	}

	private Document parseEForm(String eform) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(eform.getBytes(StandardCharsets.UTF_8));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(input);
	}

	private Node getChildOfNoticeNode(NodeList nodeList) {
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i=0; i<nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (isChildOfNotice(node)) {
					return node;
				}
			}
		}
		return null;
	}

	private boolean isChildOfNotice(Node issueNode) {
		String nodeName = issueNode.getParentNode().getNodeName();
		return Arrays.asList("PriorInformationNotice","ContractNotice","ContractAwardNotice","BusinessRegistrationInformationNotice").contains(nodeName);
	}

	private void manipolaEForm(Document eFormParsed, String eform, String versioneSDKeForm, String eform_idprg, Long eform_iddoc) throws Exception {

		if (versioneSDKeForm != null && !versioneSDKeForm.equals(ConfigManager.getValore(VALIDATION_EFORMSSDKVERSION_PROP))) {
			setVersioneSDKeForm(eFormParsed);
		}
		String[] dateTime = getIssueDateTime();

		NodeList issueDateNodes = eFormParsed.getElementsByTagName("cbc:IssueDate");
		Node node = getChildOfNoticeNode(issueDateNodes);
		if (node != null) {
			node.setTextContent(dateTime[0]);
		}

		NodeList issueTimeNodes = eFormParsed.getElementsByTagName("cbc:IssueTime");
		node = getChildOfNoticeNode(issueTimeNodes);
		if (node != null) {
			node.setTextContent(dateTime[1]);
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(eFormParsed);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(output);
		transformer.transform(source, result);
		String updatedXmlContent = output.toString(StandardCharsets.UTF_8.name());

		if (StringUtils.isEmpty(updatedXmlContent) || updatedXmlContent.equals(eform)) {
			return;
		}

		TransactionStatus status = this.sqlManager.startTransaction();
		boolean commit = false;
		try {
			byte[] contenutoAllegato = updatedXmlContent.getBytes(StandardCharsets.UTF_8);
			LobHandler lobHandler = new DefaultLobHandler();
			this.sqlManager.update("update w_docdig set digogg=? where idprg=? and iddocdig=?", new Object[]{new SqlLobValue(contenutoAllegato, lobHandler), eform_idprg, eform_iddoc});
			ObjectStorageUtils.addFile(eform_idprg, eform_iddoc);
			commit = true;
		}catch (SQLException e) {
			throw new Exception("Errore durante la modifica dell'eForm");
		} finally {
			if (status != null) {
				try {
					if (commit) {
						this.sqlManager.commitTransaction(status);
					} else {
						this.sqlManager.rollbackTransaction(status);
					}
				} catch (SQLException e) {
					//TODO THROW
				}
			}
		}
	}

	private String[] getIssueDateTime() {
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin")); // Adjust the ZoneId as needed

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddXXX");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSXXX");

		String formattedDate = now.format(dateFormatter);
		String formattedTime = now.format(timeFormatter);

		return new String[] {formattedDate,formattedTime};
	}

	private JSONArray validazioneSDKEForm(String eform) throws Exception{
		String noticeBase64 = Base64.encode(eform.getBytes("UTF-8"));
		String cvsURL = getTEDCVSURL();
		String xApiKey = getTEDXAPIKEY();

		String response = null;
		HttpURLConnection connection = null;

		URL url = new URL(cvsURL + VALIDATION_API);
		connection = (HttpURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("x-api-key", xApiKey);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Connection", "Keep-Alive");

		JSONObject jsonInput = new JSONObject();
		jsonInput.put("notice", noticeBase64);
		jsonInput.put("language", getCVSLanguage());
		jsonInput.put("validationMode", VALIDATION_MODE);
		jsonInput.put("eFormsSdkVersion", getSDKVersion());

		DataOutputStream output = new DataOutputStream(connection.getOutputStream());
		output.write(jsonInput.toString().getBytes("UTF-8"));
		output.flush();

		// Log della richiesta
		if (logger.isDebugEnabled()) {
			OutputStream osr = connection.getOutputStream();
			logger.debug("SIMAP, URL: " + connection.getURL().getProtocol());
			logger.debug("SIMAP, URL Host: " + connection.getURL().getHost());
			logger.debug("SIMAP, URL Path: " + connection.getURL().getPath());
			logger.debug("SIMAP, x-api-key: " + xApiKey);
			logger.debug("SIMAP, getOutputStream: " + osr);
		}

		// Risposta
		int httpStatus = connection.getResponseCode();

		if (httpStatus == 200) {
			response = getInputStream(connection);
			if (logger.isDebugEnabled()) logger.debug("SIMAP, validazione, getInputStream: " + response);
		} else {
			response = getErrorStream(connection);
			logger.info("SIMAP, errore durante la validazione, getErrorStream: " + response);
			throw new GestoreException(response, "eforms.remote.error", new Object[] { response }, null);
		}

		connection.disconnect();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(response)));
		NodeList failedAssertNodes = doc.getElementsByTagName("svrl:failed-assert");
		JSONArray erroriControlloSDK = new JSONArray();
		for (int i = 0; i < failedAssertNodes.getLength(); i++) {
			Node node = failedAssertNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				String location = element.getAttribute("location");
				String test = element.getAttribute("test");
				String text = element.getElementsByTagName("svrl:text").item(0).getTextContent();
				JSONObject errore = new JSONObject();
				errore.put("errore","<b>ERRORE:</b> " + text + "<br><b>Sezione eForm:</b> " + location + "<br><b>Test:</b> " + test);
				erroriControlloSDK.add(errore);
			}
		}
		return erroriControlloSDK;
	}

	private String getInputStream(HttpURLConnection connection) {
		String _is = null;
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(connection.getInputStream(), writer, "UTF-8");
			_is = writer.toString();
		} catch (Throwable t) {
		}
		return _is;
	}

	private String getErrorStream(HttpURLConnection connection) {
		String _is = null;
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(connection.getErrorStream(), writer, "UTF-8");
			_is = writer.toString();
		} catch (Throwable t) {
		}
		return _is;
	}

	private String getTEDCVSURL() throws Exception {
		String url = ConfigManager.getValore(TED_CVS_URL_PROP);
		if (url == null || (url != null && "".equals(url.trim()))) {
			throw new Exception(ERRORE_VALORIZZARE_PROPERTY_CVS);
		} else {
			if (url.endsWith("/")) {
				url = url.substring(0, url.length() - 1);
			}
		}
		return url;
	}

	private String getTEDXAPIKEY() throws Exception {
		String x_api_key = ConfigManager.getValore(TED_XAPIKEY_PROP);
		if (x_api_key == null || (x_api_key != null && "".equals(x_api_key.trim()))) {
			throw new Exception(ERRORE_VALORIZZARE_PROPERTY_CVS);
		}
		return x_api_key;
	}

	private String getCVSLanguage() {
		String language = ConfigManager.getValore(VALIDATION_LANGUAGE_PROP);
		if (language == null || (language != null && "".equals(language.trim()))) {
			language = VALIDATION_LANGUAGE_DEFAULT;
		}
		return language;
	}

	private String getSDKVersion() throws GestoreException {
		String version = ConfigManager.getValore(VALIDATION_EFORMSSDKVERSION_PROP);
		if (version == null || (version != null && "".equals(version.trim()))) {
			throw new GestoreException("Versione SDK per validazione dell'eForm non definita", "eforms.sdkVersion.notdefined");
		}
		return version;
	}

}

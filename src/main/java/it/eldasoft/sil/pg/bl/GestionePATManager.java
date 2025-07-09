package it.eldasoft.sil.pg.bl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getOptConfigurazionePerUfficioIntestatario;

public class GestionePATManager {

	static Logger logger = Logger.getLogger(GestionePATManager.class);

	private SqlManager sqlManager;
	private PgManagerEst1 pgManagerEst1;
	private GenChiaviManager genChiaviManager;

	public static final String TIPO_REGIONE_MARCHE = "1";
//	GESTIONE PAT ISWEB

	public static final String PROP_NOME_ISTITUZIONALE = "sitoIstituzionale.ws.nomeIstituzionale";

	private static final String TIPO_WSBANDI_PAT = "2";

	private static final String ADDOGGETTO = "addOggetto";
	private static final String EDITOGGETTO = "editOggetto";
	private static final String DELETEOGGETTO = "deleteOggetto";
    public static final String GETOGGETTI = "getOggetti";

	public static final String TIPO_PROCEDURE = "bdncp_procedure";
	public static final String TIPO_AVVISO = "bdncp_procedure_avviso";
	public static final String PERMESSI_LETTURA = "permessi_lettura";
	public static final String TIPRIC = "PAT_procedure";

	private static final String NS_URN_XMETHODS = "urn:xmethods-delayed-quotes";
	private static final String NS_INTERNETSOLUZIONI = "http://www.internetsoluzioni.it/WebService";
	private static final String NS_SOAP_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
	private static final String NS_APACHE_SOAP = "http://xml.apache.org/xml-soap";

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
		this.pgManagerEst1 = pgManagerEst1;
	}

	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

	private ArrayList<Map<String, Object>> addOggetto (HttpServletRequest request, HashMap<String, String> datiRichiesta) throws GestoreException {
		try {
			return requestBassoLivello(request, datiRichiesta, ADDOGGETTO);
		} catch (IOException e) {
			throw new GestoreException("L'inserimento del bando da parte del servizio remoto non è andato a buon fine: " + e.getMessage(),"ws.PAT.InternetSoluzioni.addOggetto.esitoNOK", new Object[] {e.getMessage()},e);
		}
	}

	private ArrayList<Map<String, Object>> editOggetto (HttpServletRequest request, HashMap<String, String> datiRichiesta) throws GestoreException {
		try {
			return requestBassoLivello(request, datiRichiesta, EDITOGGETTO);
		} catch (IOException e) {
			throw new GestoreException("L'aggiornamento del bando da parte del servizio remoto non è andato a buon fine: " + e.getMessage(),"ws.PAT.InternetSoluzioni.updateBando.esitoNOK", new Object[] {e.getMessage()},e);
		}
	}

	private ArrayList<Map<String, Object>> getOggetti (HttpServletRequest request, HashMap<String, String> datiRichiesta) throws GestoreException {
		try {
			return requestBassoLivello(request, datiRichiesta, GETOGGETTI);
		} catch (IOException e) {
			throw new GestoreException("La ricerca del bando da parte del servizio remoto non è andato a buon fine: " + e.getMessage(),"ws.PAT.InternetSoluzioni.getOggetti.esitoNOK", new Object[] {e.getMessage()},e);
		}
	}

	private ArrayList<Map<String, Object>> deleteOggetto (HttpServletRequest request, HashMap<String, String> datiRichiesta) throws GestoreException {
		try {
			return requestBassoLivello(request, datiRichiesta, DELETEOGGETTO);
		} catch (IOException e) {
			throw new GestoreException(null,null,e);
		}
	}

	private ArrayList<Map<String, Object>> requestBassoLivello(HttpServletRequest request, HashMap<String, String> datiRichiesta, String metodoRichiesta) throws IOException, GestoreException {
		putCenint(datiRichiesta);
		String response = null;
		String errMsg = "";
		int livEvento = 1;
		try {
			response = chiamaServizio(request, datiRichiesta,metodoRichiesta);
			errMsg = response;
		} catch (RemoteException e) {
			livEvento = 3;
			errMsg = e.getMessage();
			throw e;
		} finally {
			try {
				LogEvento logEvento = LogEventiUtils.createLogEvento(request);
				logEvento.setLivEvento(livEvento);
				logEvento.setOggEvento(datiRichiesta.get("ngara"));
				logEvento.setCodEvento("GA_PAT_RES_PUBBLICA");
				logEvento.setDescr("Risposta PAT IsWeb pubblicazione procedura su sito istituzionale");
				logEvento.setErrmsg(errMsg);
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception e) {
				logger.error(ResourceBundle.getBundle("AliceResources").getString("errors.logEventi.inaspettataException"), e);
			}
		}
		String messaggio = estraiMessaggioDallaRisposta(response);
		if (GETOGGETTI.equals(metodoRichiesta) && "Nessun risultato".equals(messaggio)) return null;
		if (StringUtils.isNotBlank(messaggio))
			throw new RemoteException("Si è verificato un errore durante la chiamata al servizio del sito istituzionale: " + messaggio);
		try {
			return parseXmlToHashMap(response);
		} catch (Exception e) {
			throw new RemoteException("Si è verificato un errore durante la lettura della risposta dal sito istituzionale: " + e.getMessage());
		}
	}

	private static String chiamaServizio(HttpServletRequest request, HashMap<String, String> datiRichiesta, String metodoRichiesta) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = creaRequestBassoLivello(request, datiRichiesta,metodoRichiesta);
		HttpResponse lHttpResponse = httpClient.execute(httpPost);
		if (lHttpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			throw new RemoteException("Si è verificato un errore durante la lettura dei dati dal sito istituzionale: " + lHttpResponse.getStatusLine().getReasonPhrase());
		BufferedReader reader = new BufferedReader(new InputStreamReader(lHttpResponse.getEntity().getContent()));
		return org.apache.commons.io.IOUtils.toString(reader);
	}

	public static ArrayList<Map<String, Object>> parseXmlToHashMap(String xml) throws ParserConfigurationException, IOException, SAXException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));
		document.getDocumentElement().normalize();

		ArrayList<Map<String, Object>> results = null;

		NodeList resultNodeList = document.getElementsByTagName("Result");
		if (resultNodeList.getLength() > 0) {
			Element resultElement = (Element) resultNodeList.item(0);
			results = parseValoriArray(resultElement);
		}
		return results;
	}

	private static ArrayList<Map<String, Object>> parseValoriArray(Element resultElement) {
		ArrayList<Map<String, Object>> valoriList = new ArrayList<>();
		NodeList resultChildrenNodeList = resultElement.getChildNodes();
		for (int i = 0; i < resultChildrenNodeList.getLength(); i++) {
			if (resultChildrenNodeList.item(i).getNodeType() == Node.ELEMENT_NODE && "valori".equals(resultChildrenNodeList.item(i).getLocalName())) {
				Element valoriElement = (Element) resultChildrenNodeList.item(i);
				NodeList valoriNodeList = valoriElement.getChildNodes();

				if (valoriNodeList.getLength() > 0) {
					for (int j = 0; j < valoriNodeList.getLength(); j++) {
						Element mapElement = (Element) valoriNodeList.item(j);
						valoriList.add(parseMapElement(mapElement));
					}
				}
				return valoriList;
			}
		}
		return null;
	}

	private static Map<String, Object> parseMapElement(Element mapElement) {
		Map<String, Object> map = new HashMap<>();
		NodeList itemNodeList = mapElement.getChildNodes();

		for (int i = 0; i < itemNodeList.getLength(); i++) {
			Element itemElement = (Element) itemNodeList.item(i);
			String key = getTextContent(itemElement, "key");
			Object value = getTextContent(itemElement, "value");

//			NodeList figli = itemElement.getChildNodes();
//			for (int j = 0; j < figli.getLength(); j++) {
//				if (figli.item(i).getNodeType() == Node.ELEMENT_NODE && "value".equals(figli.item(i).getLocalName())) {
//					Element nestedArrayElement = (Element) figli.item(i).getFirstChild();
//					if (nestedArrayElement != null && nestedArrayElement.getNodeType() == Node.ELEMENT_NODE && nestedArrayElement.getChildNodes().getLength() > 0) {
//						value = parseNestedArray(nestedArrayElement);
//					}
//				}
//			}
			map.put(key, value);
		}
		return map;
	}

//	private static List<Map<String, Object>> parseNestedArray(Element arrayElement) {
//		List<Map<String, Object>> nestedList = new ArrayList<>();
//		NodeList itemNodeList = arrayElement.getChildNodes();
//		for (int i = 0; i < itemNodeList.getLength(); i++) {
//			Element mapElement = (Element) itemNodeList.item(i);
//			nestedList.add(parseMapElement(mapElement));
//		}
//		return nestedList;
//	}

	private static String getTextContent(Element parentElement, String tagName) {
		NodeList nodeList = parentElement.getElementsByTagName(tagName);
		if (nodeList.getLength() > 0) {
			return nodeList.item(0).getTextContent();
		}
		return "";
	}

	private static String estraiMessaggioDallaRisposta(String response) throws RemoteException{
		String messaggio = null;
		String tagMessaggio = "<message";
		int indexTagMessaggio = response.indexOf(tagMessaggio);
		if (indexTagMessaggio == -1) {
			throw new RemoteException("Si è verificato un errore durante la lettura dei dati dal sito istituzionale");
		}
		int indexEndMessaggio = response.indexOf("</message>");
		if (indexEndMessaggio != -1) {
			messaggio = response.substring(indexTagMessaggio, indexEndMessaggio);
			messaggio = messaggio.substring(messaggio.indexOf(">") + 1);
		}
		return messaggio;
	}

	private static void appendValue(StringBuilder buffer, String chiave, String valore) {
		if (StringUtils.isNotEmpty(chiave) && StringUtils.isNotEmpty(valore)) {
			buffer.append("<valori xsi:type=\"web:Valore\">");
			buffer.append("<chiave xsi:type=\"xsd:string\">").append(chiave).append("</chiave>");
			buffer.append("<valore xsi:type=\"xsd:string\">").append(valore).append("</valore>");
			buffer.append("</valori>");
		}
	}

	private static void appendCampo(StringBuilder buffer, String nome, String valore) {
		if (StringUtils.isNotEmpty(nome) && StringUtils.isNotEmpty(valore)) {
			buffer.append("<campi xsi:type=\"web:Campo\">");
			buffer.append("<nome xsi:type=\"xsd:string\">").append(StringEscapeUtils.escapeXml(nome)).append("</nome>");
			String valoreEscaped = StringEscapeUtils.escapeXml(valore);
			if ("oggetto".equals(nome) && valoreEscaped.length() > 1000) {
				valoreEscaped = valoreEscaped.substring(0, 999);
			}
			buffer.append("<valore xsi:type=\"xsd:string\">").append(valoreEscaped).append("</valore>");
			buffer.append("</campi>");
		}
	}

	private void putCenint(HashMap<String, String> datiRichiesta) throws GestoreException {
		String codgar = datiRichiesta.get("codgar");
		String select = "select cenint from torn where codgar=?";
		Object[] parametro = new Object[1];
		parametro[0] = codgar;

		if (StringUtils.isBlank(codgar)) {
			String ngara = datiRichiesta.get("ngara");
			select = "select cenint from torn where codgar=(select codgar1 from gare where ngara=?)";
			if (StringUtils.isBlank(ngara)) {
				throw new GestoreException("Request errata, parametro codgar o ngara mancante","PAT.LetturaScritturaDatabase.error", new Object[] {"Request errata, parametro codgar o ngara mancante"},null);
			}
			parametro[0] = ngara;
		}
		try {
			String cenint = (String) sqlManager.getObject(select, parametro);
			datiRichiesta.put("cenint", cenint);
		} catch (SQLException e) {
            throw new GestoreException("Errore durante la lettura dell'ufficio intestatario","PAT.LetturaScritturaDatabase.error", new Object[] {"Errore durante la lettura dell'ufficio intestatario"},e);
        }
	}

	private static HttpPost creaRequestBassoLivello(HttpServletRequest request, HashMap<String, String> datiRichiesta, String metodoRichiesta) throws UnsupportedEncodingException {
		String tipo = datiRichiesta.remove("tipo");
		String cenint = datiRichiesta.remove("cenint");
		String ngara = datiRichiesta.remove("ngara");
		String codgar = datiRichiesta.remove("codgar");
		String urlServizio = getOptConfigurazionePerUfficioIntestatario(CostantiAppalti.PROP_WSBANDI_URL, cenint);
		String user = getOptConfigurazionePerUfficioIntestatario(CostantiAppalti.PROP_WSBANDI_USER, cenint);
		String token = getOptConfigurazionePerUfficioIntestatario(CostantiAppalti.PROP_WSBANDI_TOKEN, cenint);

		HttpPost httpPost = new HttpPost(urlServizio);
		httpPost.addHeader("content-type", "text/xml");

		StringBuilder buffer = new StringBuilder();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
				+ " xmlns:urn=\"urn:xmethods-delayed-quotes\">");
		buffer.append("<soapenv:Body>");
		buffer.append("<urn:").append(metodoRichiesta).append(" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
		buffer.append("<request xsi:type=\"web:RequestWS\" xmlns:web=\"http://www.internetsoluzioni.it/WebService\">");
		appendValue(buffer, "user", user);
		appendValue(buffer, "token", token);
		appendValue(buffer, "tipo", tipo);
		datiRichiesta.forEach((nome, valore) -> appendCampo(buffer, nome, valore));
		buffer.append("</request>");
		buffer.append("</urn:").append(metodoRichiesta).append(">");
		buffer.append("</soapenv:Body>");
		buffer.append("</soapenv:Envelope>");

		datiRichiesta.put("tipo", tipo);
		datiRichiesta.put("ngara", ngara);
		datiRichiesta.put("codgar", codgar);

		String xmlRequest = buffer.toString();
		StringEntity lEntity = new StringEntity(xmlRequest);
		httpPost.setEntity(lEntity);

		if (ADDOGGETTO.equals(metodoRichiesta)) {
			try {
				LogEvento logEvento = LogEventiUtils.createLogEvento(request);
				logEvento.setLivEvento(1);
				logEvento.setOggEvento(ngara);
				logEvento.setCodEvento("GA_PAT_REQ_PUBBLICA");
				logEvento.setDescr("Invio richiesta a PAT IsWeb pubblicazione procedura su sito istituzionale");
				logEvento.setErrmsg(xmlRequest);
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception e) {
				logger.error(ResourceBundle.getBundle("AliceResources").getString("errors.logEventi.inaspettataException"), e);
			}
		}

		return httpPost;
	}

	public void pubblicaTornata(HttpServletRequest request, String codgar, Timestamp dataPubblicazione) throws GestoreException {
		try {
			Long genereGara = (Long) sqlManager.getObject("select genere from V_GARE_GENERE where codgar = ? and genere <100 ", new Object[]{codgar});
			int genere = genereGara.intValue();
			String select = null;
			switch (genere) {
				case 10:
				case 20:
					select ="select garealbo.ngara, garuuid.uuid, cast(null as varchar(2)) as codcig from garealbo left join garuuid on (garealbo.ngara=garuuid.ngara and garuuid.tipric=?) where garealbo.codgar=?";
					break;
				case 11:
					select = "select gareavvisi.ngara, garuuid.uuid, cast(null as varchar(2)) as codcig from gareavvisi left join garuuid on (gareavvisi.ngara=garuuid.ngara and garuuid.tipric=?) where gareavvisi.codgar=?";
					break;
				default:
					select = "select gare.ngara, garuuid.uuid, gare.codcig from gare left join garuuid on (gare.ngara=garuuid.ngara and garuuid.tipric=?) where gare.codgar1=? and gare.ngara!=gare.codgar1";
					break;
			}
			List<Vector<?>> gareDaPubblicare = this.sqlManager.getListVector(select, new Object[] { TIPRIC, codgar });
            for (Vector<?> garaDaPubblicare : gareDaPubblicare) {
                String ngara = SqlManager.getValueFromVectorParam(garaDaPubblicare, 0).stringValue();
                String uuid = SqlManager.getValueFromVectorParam(garaDaPubblicare, 1).stringValue();
				String codcig = SqlManager.getValueFromVectorParam(garaDaPubblicare, 2).stringValue();

				if (StringUtils.isEmpty(uuid) && (StringUtils.isEmpty(codcig) || !trovatoLottoPubblicatoManualmente(codgar, ngara, codcig, request))) {
					pubblicaLotto(request, codgar, ngara, genereGara, dataPubblicazione);
				}
            }
		} catch (SQLException e) {
			throw new GestoreException("Errore durante l'interazione con la base dati","PAT.LetturaScritturaDatabase.error", new Object[] {e.getMessage()},e);
		}
	}

	public void pubblicaLotto(HttpServletRequest request, String codgar, String ngara, Long genereGara, Timestamp dataPubblicazione) throws SQLException, GestoreException {
		HashMap<String, String> datiRichiesta = getDatiProcedura(codgar, ngara, genereGara, dataPubblicazione, null);
		List<Map<String, Object>> risposta = addOggetto(request, datiRichiesta);
		insertInGaruuid(risposta, codgar, ngara, false);
	}

	public void aggiornaLotto(HttpServletRequest request, String codgar, String ngara, Long genereGara, String uuid) throws SQLException, GestoreException {
		HashMap<String, String> datiRichiesta = getDatiProcedura(codgar, ngara, genereGara, null, uuid);
		List<Map<String, Object>> risposta = editOggetto(request, datiRichiesta);
		aggiornaGaruuid(risposta, ngara, uuid);
	}

	public boolean trovatoLottoPubblicatoManualmente(String codgar, String ngara, String codcig, HttpServletRequest request) throws GestoreException{
		boolean trovato = false;
		HashMap<String, String> datiRichiesta = new HashMap<String,String>();
		datiRichiesta.put("tipo", TIPO_PROCEDURE);
		datiRichiesta.put("cig", codcig);
		datiRichiesta.put("codgar", codgar);
		List<Map<String, Object>> risposta = getOggetti(request, datiRichiesta);
		if (risposta != null && !risposta.isEmpty()) {
			insertInGaruuid(risposta, codgar, ngara, true);
			trovato = true;
			try {
				LogEvento logEvento = LogEventiUtils.createLogEvento(request);
				logEvento.setLivEvento(2);
				logEvento.setOggEvento(ngara);
				logEvento.setCodEvento("GA_PAT_REQ_PUBBLICA");
				logEvento.setDescr("Invio richiesta a PAT IsWeb pubblicazione procedura su sito istituzionale");
				logEvento.setErrmsg("La pubblicazione del CIG " + codcig +  " non è stata effettuata perché già presente nel sito istituzionale");
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception e) {
				logger.error(ResourceBundle.getBundle("AliceResources").getString("errors.logEventi.inaspettataException"), e);
			}
		}
		return trovato;
	}

	private void insertInGaruuid(List<Map<String, Object>> risposta, String codgar, String ngara, boolean isInseritoManualmente) throws GestoreException {
		Map<String, Object> vettoreGara = risposta.get(0);
		String uuid = (String) vettoreGara.get("id");
		String permessi_lettura = (String) vettoreGara.get(PERMESSI_LETTURA);
		String riservata = null;
		String pubman = null;
		try {
			if ("H".equals(permessi_lettura))
				riservata = "1";
			if (isInseritoManualmente)
				pubman = "1";
			String insertGaruuid = "insert into garuuid(id,codgar,tipric,uuid,ngara,riservata,pubman) values(?,?,?,?,?,?,?)";
			Integer newId = this.genChiaviManager.getNextId("GARUUID");
			this.sqlManager.update(insertGaruuid, new Object[]{newId, codgar, TIPRIC, uuid, ngara, riservata, pubman});
		} catch (SQLException e) {
			throw new GestoreException("Errore durante l'interazione con la base dati","PAT.LetturaScritturaDatabase.error", new Object[] {e.getMessage()},e);
		}
	}

	private void aggiornaGaruuid(List<Map<String, Object>> risposta, String ngara, String uuid) throws GestoreException {
		Map<String, Object> vettoreGara = risposta.get(0);
		String permessi_lettura = (String) vettoreGara.get(PERMESSI_LETTURA);
		if ("H".equals(permessi_lettura)) {
			String updateGaruuid = "update garuuid set riservata=? where tipric=? and uuid=? and ngara=?";
			String riservata = "1";
			try {
				this.sqlManager.update(updateGaruuid, new Object[]{riservata, TIPRIC, uuid, ngara});
			} catch (SQLException e) {
				throw new GestoreException("Errore durante l'interazione con la base dati","PAT.LetturaScritturaDatabase.error", new Object[] {e.getMessage()},e);
			}
		}
	}

	private HashMap<String, String> getDatiProcedura(String codgar, String ngara, Long genereGara, Timestamp dataPubblicazione, String uuid) throws GestoreException, SQLException {
		HashMap<String, String> datiRichiesta = new HashMap<>();
		String oggetto = getOggettoGara(ngara, genereGara);
		if (StringUtils.isNotEmpty(oggetto)) datiRichiesta.put("oggetto", oggetto);
		Long iterga = (Long) sqlManager.getObject("select iterga from torn where codgar=?", new Object[] {codgar});
		boolean isProcedureAvviso = isProcedureAvviso(genereGara,iterga);

		String tipo = null;
		if (isProcedureAvviso) {
			if (dataPubblicazione != null) {
				String data = getTimestampUnix(dataPubblicazione.getTime());
				datiRichiesta.put("data", data);
			}
			tipo  = TIPO_AVVISO;
		} else {
			String cig = (String) sqlManager.getObject("select codcig from gare where ngara=?", new Object[] {ngara});
			tipo = TIPO_PROCEDURE;
			if (StringUtils.isNotEmpty(cig)) datiRichiesta.put("cig", cig);
			if (isNegoziata(iterga)) {
				Calendar dataScadenzaCalendar = calcoloDataScadenza(ngara, codgar, genereGara, iterga);
				boolean isTerminiNonScaduti = dataScadenzaCalendar != null && Calendar.getInstance().compareTo(dataScadenzaCalendar) < 0;
				if (isTerminiNonScaduti) {
					datiRichiesta.put(PERMESSI_LETTURA, "H");
				} else {
					datiRichiesta.put(PERMESSI_LETTURA, "N/A");
				}
			}
		}
		if (StringUtils.isNotEmpty(uuid)) {
			datiRichiesta.put("id", uuid);
		}
		datiRichiesta.put("tipo", tipo);
		datiRichiesta.put("codgar", codgar);
		datiRichiesta.put("ngara", ngara);

		String testoInternoLink = "Documentazione della procedura";
		String urlProceduraPortaleAppalti = "<a href='" + InviaBandoAvvisoSimapManager.getUrlDocumentiGara(codgar) + "'>" + testoInternoLink + "</a>";
		datiRichiesta.put("note", urlProceduraPortaleAppalti);
		return datiRichiesta;
	}

	private String getOggettoGara(String ngara, Long genereGara) throws SQLException {
		String select;
		if (genereGara != null && (genereGara == 10 || genereGara == 20)) {
			select = "select oggetto from garealbo where ngara = ?";
		} else if (genereGara != null && genereGara == 11) {
			select = "select oggetto from gareavvisi where ngara = ?";
		} else {
			select = "select not_gar from gare where ngara = ?";
		}
		return (String) sqlManager.getObject(select, new Object[]{ngara});
	}

	public void updateTerminiBando(String codgar,Timestamp data, Timestamp dataOriginale, String ora, String oraOriginale) throws GestoreException {
		if (data.equals(dataOriginale) && ora.equals(oraOriginale))
			return;
		Calendar cal = Calendar.getInstance();
		Date dataScadenza = new Date(data.getTime());
		String oraScadenza = ora.substring(0, 2);
		String minutiScadenza = ora.substring(3);
		cal.setTime(dataScadenza);
		cal.add(Calendar.HOUR, Integer.parseInt(oraScadenza));
		cal.add(Calendar.MINUTE, Integer.parseInt(minutiScadenza));

		Calendar currentTime = Calendar.getInstance();
		boolean isScadutiTermini = currentTime.compareTo(cal) > 0;

		if (!isScadutiTermini) {
			nascondiTornata(codgar);
		}
	}

	public void nascondiTornata(String codgar) throws GestoreException {
		List<Vector<?>> listaLotti;
		try {
			Vector<?> datiTorn = sqlManager.getVector("select iterga,cenint from torn where codgar=?", new Object[]{codgar});
			Long iterga = ((JdbcParametro) datiTorn.get(0)).longValue();
			if (!isNegoziata(iterga)) {
				return;
			}
			listaLotti = sqlManager.getListVector("select uuid,ngara,riservata from garuuid where codgar=? and tipric=? and ngara is not null", new Object[]{codgar, TIPRIC});
			if (listaLotti == null) {
				throw new GestoreException("Errore durante l'interazione con la base dati: Nessun lotto estratto","PAT.LetturaScritturaDatabase.error", new Object[] {"Nessun lotto estratto"}, null);
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore durante l'interazione con la base dati","PAT.LetturaScritturaDatabase.error", new Object[] {e.getMessage()},e);
		}
        for (Vector<?> lotto : listaLotti) {
			String uuid = SqlManager.getValueFromVectorParam(lotto, 0).getStringValue();
			String ngara = SqlManager.getValueFromVectorParam(lotto, 1).getStringValue();
			String riservata = SqlManager.getValueFromVectorParam(lotto, 2).getStringValue();
			if ("1".equals(riservata))
				continue;
			HashMap<String, String> datiRichiesta = new HashMap<String, String>();
			datiRichiesta.put("tipo", TIPO_PROCEDURE);
			datiRichiesta.put("id", uuid);
			datiRichiesta.put(PERMESSI_LETTURA, "H");
			datiRichiesta.put("codgar", codgar);
			List<Map<String, Object>> risposta = editOggetto(null, datiRichiesta);
			aggiornaGaruuid(risposta, ngara, uuid);
        }
	}

	public void rendiLottoVisibile(HttpServletRequest request, String uuid, String ngara) throws GestoreException {
        HashMap<String, String> datiRichiesta = new HashMap<String, String>();
		datiRichiesta.put("tipo", TIPO_PROCEDURE);
        datiRichiesta.put("id", uuid);
        datiRichiesta.put(PERMESSI_LETTURA, "N/A");
		datiRichiesta.put("ngara", ngara);
		List<Map<String, Object>> risposta = editOggetto(request, datiRichiesta);
		aggiornaGaruuid(risposta, ngara, uuid);
    }

	private String getTimestampUnix(long data) {
		return Long.toString(data / 1000);
	}

	private Calendar calcoloDataScadenza(String ngara, String codiceGara, Long genereGara, Long iterGara) throws GestoreException, SQLException {
		Calendar cal = Calendar.getInstance();
		boolean dataValorizzata = false;
		String oraScadenza = "";
		String minutiScadenza = "";
		if (((Long) 11L).equals(genereGara)) {
			// Per gli avvisi si deve considerare il campo GAREAVVISI.DATSCA se è
			// valorizzato
			Date datsca = (Date) this.sqlManager.getObject("select datsca from gareavvisi where ngara=?",
					new Object[] { ngara });
			if (datsca != null) {
				cal.setTime(datsca);
				cal.add(Calendar.HOUR, 23);
				cal.add(Calendar.MINUTE, 59);
				dataValorizzata = true;
			}
		} else if (((Long) 10L).equals(genereGara) || ((Long) 20L).equals(genereGara)) {
			// Per gli elenchi e cataloghi si deve considerare il max del campo PUBBTERM
			// .DTERMPRES se valorizzato
			Vector<?> datiPubbter = this.sqlManager.getVector(
					"select dtermpres, otermpres from pubbterm where ngara =? and dtermpres is not null order by dtermpres desc",
					new Object[] { ngara });
			if (datiPubbter != null && datiPubbter.size() > 0) {
				Date dtermpres = (Date) SqlManager.getValueFromVectorParam(datiPubbter, 0).getValue();
				String otermpres = SqlManager.getValueFromVectorParam(datiPubbter, 1).getStringValue();
				if (dtermpres != null) {
					cal.setTime(dtermpres);
					if (otermpres != null && !"".equals(otermpres)) {
						oraScadenza = otermpres.substring(0, 2);
						minutiScadenza = otermpres.substring(3);
						cal.add(Calendar.HOUR, Integer.parseInt(oraScadenza));
						cal.add(Calendar.MINUTE, Integer.parseInt(minutiScadenza));
					} else {
						cal.add(Calendar.HOUR, 23);
						cal.add(Calendar.MINUTE, 59);
					}
					dataValorizzata = true;
				}
			}
		}
		if (genereGara.longValue() != 10 && genereGara.longValue() != 11 && genereGara.longValue() != 20) {
			cal = this.getDataScadenzaGara(codiceGara, iterGara);
			if (cal != null)
				dataValorizzata = true;
		}
		if (dataValorizzata)
			return cal;
		else
			return null;
	}

	private Calendar getDataScadenzaGara(String codiceGara, Long iterga) throws GestoreException, SQLException {
		String select = "select dtepar, otepar, dteoff, oteoff from torn where codgar=?";
		Vector<?> datiTorn = this.sqlManager.getVector(select, new Object[] { codiceGara });
		Timestamp dtepar = null, dteoff = null;
		String otepar = null, oteoff = null;
		Date dataScadenza = null;
		String oraScadenza = "", minutiScadenza = "";
		Calendar cal = Calendar.getInstance();
		if (datiTorn != null && datiTorn.size() > 0) {
			dtepar = SqlManager.getValueFromVectorParam(datiTorn, 0).dataValue();
			otepar = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
			dteoff = SqlManager.getValueFromVectorParam(datiTorn, 2).dataValue();
			oteoff = SqlManager.getValueFromVectorParam(datiTorn, 3).getStringValue();
		}
		if (((Long) 2L).equals(iterga) || ((Long) 4L).equals(iterga)) {
			if (dtepar == null)
				return null;
			dataScadenza = new Date(dtepar.getTime());
			if (StringUtils.isNotEmpty(otepar)) {
				oraScadenza = otepar.substring(0, 2);
				minutiScadenza = otepar.substring(3);
			}
		} else {
			if (dteoff == null)
				return null;
			dataScadenza = new Date(dteoff.getTime());
			if (StringUtils.isNotEmpty(oteoff)) {
				oraScadenza = oteoff.substring(0, 2);
				minutiScadenza = oteoff.substring(3);
			}
		}
		cal.setTime(dataScadenza);

		if (StringUtils.isNotEmpty(oraScadenza))
			cal.add(Calendar.HOUR, Integer.parseInt(oraScadenza));
		if (StringUtils.isNotEmpty(minutiScadenza))
			cal.add(Calendar.MINUTE, Integer.parseInt(minutiScadenza));

		return cal;
	}

	public static String getNomeIstituzionale(String codein) {
		return getOptConfigurazionePerUfficioIntestatario(PROP_NOME_ISTITUZIONALE, codein);
	}

	public static String getWsBandiTipo(String codein) {
		return getOptConfigurazionePerUfficioIntestatario(CostantiAppalti.PROP_WSBANDI_TIPO, codein);
	}

	public static String getUrlWSPAT() {
		return "ws.PAT.InternetSoluzioni";
	}

	public static Boolean isConfiguratoPerPAT(String codein) {
		return TIPO_WSBANDI_PAT.equals(getWsBandiTipo(codein));
	}

	public Boolean isConfiguratoPerPAT() throws GestoreException {
		boolean configuratoStandard = isConfiguratoPerPAT(null);
		if (configuratoStandard) {
			return true;
		}
		HashMap<String,String> ufficiIntestatariConfigurati = getUfficiIntestatariConfigurati(CostantiAppalti.PROP_WSBANDI_TIPO);
		if (ufficiIntestatariConfigurati == null || ufficiIntestatariConfigurati.isEmpty())
			return false;
		return ufficiIntestatariConfigurati.containsValue(TIPO_WSBANDI_PAT);
	}

	public static Boolean[] controlloConfigurazionePAT(String cenint) {
		String suffissoUffint = "/" + cenint;
		boolean isConfigurazioneSpecifica = StringUtils.isNotEmpty(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO + suffissoUffint));
		if (!isConfigurazioneSpecifica) {
			suffissoUffint = "";
		}
		Boolean[] esitoControlloConfigurazione = new Boolean[] {
			StringUtils.isNotEmpty(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_URL + suffissoUffint)),
			StringUtils.isNotEmpty(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_USER + suffissoUffint)),
			StringUtils.isNotEmpty(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TOKEN + suffissoUffint))
		};
        return esitoControlloConfigurazione;
	}

	public static Boolean isNegoziata(Long iterGara) {
		return Arrays.asList((long) 3, (long) 5, (long) 6, (long) 8).contains(iterGara);
	}

	public static Boolean isNegoziata(String iterGara) {
		return Arrays.asList("3", "5", "6", "8").contains(iterGara);
	}

	public static boolean isProcedureAvviso(Long genereGara, Long iterga) {
		return Arrays.asList(10L, 11L, 20L).contains(genereGara) || ((Long) 7L).equals(iterga);
	}

	/**
	 * Retrieves the configured values for the given property for different uffici intestatari.
	 *
	 * @param property the property to retrieve the values for
	 * @return a map containing the codein (ufficio intestatario code) as the key and the corresponding value for the property
	 * @throws GestoreException if an error occurs while interacting with the database
	 */
	public HashMap<String,String> getUfficiIntestatariConfigurati(String property) throws GestoreException {
		HashMap<String,String> ufficiIntestatariConfigurati = new HashMap<>();
		List<Vector<?>> ufficiConfigurati;
		try {
			ufficiConfigurati = sqlManager.getListVector("select chiave,valore from w_config where codapp='PG' and chiave like ? and valore is not null", new Object[]{property + "/%"});
		} catch (SQLException e) {
			throw new GestoreException("Errore nella lettura dalla base dati","",e);
		}
		if (ufficiConfigurati == null)
			return ufficiIntestatariConfigurati;
        for (Vector<?> ufficio : ufficiConfigurati) {
            String chiave = ((JdbcParametro) ufficio.get(0)).stringValue();
            String valore = ((JdbcParametro) ufficio.get(1)).stringValue();
            if (StringUtils.isNotEmpty(chiave) && chiave.contains("/")) {
				String codein = chiave.substring(chiave.lastIndexOf("/") + 1);
				ufficiIntestatariConfigurati.put(codein, valore);
            }
        }
		return ufficiIntestatariConfigurati;
	}
}

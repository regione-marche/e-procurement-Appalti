/*
 * Created on 11/07/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xml.security.utils.Base64;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import net.sf.json.JSONObject;

public class GeneraTokenIntegrazioneBICAction extends ActionAjaxLogged {

	static Logger               logger                          = Logger.getLogger(GeneraTokenIntegrazioneBICAction.class);

	private static final String INTEGRAZIONE_BIC_CHIAVE_TOKEN   = "integrazionebic.chiavetoken";
	private static final String INTEGRAZIONE_BIC_DURATA_TOKEN   = "integrazionebic.duratatoken";
	private static final int    DURATA_TOKEN_DEFAULT            = 3;

	private static final String MESSAGE_NO_CHIAVE_TOKEN         = "Per effettuare l'inoltro della chiamata a BIC  necessario impostare la chiave di criptazione del token nel pannello di amministrazione alla sezione 'Integrazione BIC'";
	private static final String MESSAGE_DIMENSIONE_CHIAVE_TOKEN = "La chiave di criptazione del token deve essere lunga 16, 24 o 36 caratteri";

	private SqlManager          sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String messageKey = null;

		boolean esito = false;
		String messaggio = null;

		DataSourceTransactionManagerBase.setRequest(request);

		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		PrintWriter out = response.getWriter();

		JSONObject result = new JSONObject();

		try {

			String codgar = request.getParameter("codgar");
			Long gruppo = Long.parseLong(request.getParameter("gruppo"));
			Long norddocg = Long.parseLong(request.getParameter("norddocg"));

			String select = "select urldoc, ultbic from documgara where codgar=? and gruppo=? and norddocg = ?";
			List<?> dati = sqlManager.getVector(select, new Object[] { codgar, gruppo, norddocg });
			if (dati != null && dati.size() > 0) {
				String urldoc = (String) SqlManager.getValueFromVectorParam(dati, 0).getValue();
				String ultbic = (String) SqlManager.getValueFromVectorParam(dati, 1).getValue();

				// La generazione del token avviene solo se tutti i dati necessario
				// sono presenti. Questo per preservare l'integrazione precedente che
				// non aveva queste informazioni. Per i dati dell'integrazione
				// precedente non si genera alcun token ma si utilizza l'url diretto.
				if (StringUtils.isNotBlank(urldoc) && StringUtils.isNotBlank(ultbic)) {

					String chiavetokencifrata = ConfigManager.getValore(INTEGRAZIONE_BIC_CHIAVE_TOKEN);

					if (StringUtils.isNotBlank(chiavetokencifrata)) {

						ICriptazioneByte valoreICriptazioneByte = null;
						valoreICriptazioneByte = FactoryCriptazioneByte.getInstance(
								ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), chiavetokencifrata.getBytes(),
								ICriptazioneByte.FORMATO_DATO_CIFRATO);
						String chiavetoken = new String(valoreICriptazioneByte.getDatoNonCifrato());

						if (StringUtils.isNotBlank(chiavetoken)) {

							if (chiavetoken.length() == 16 || chiavetoken.length() == 24 || chiavetoken.length() == 36) {

								int duratatoken = getDurataToken();

								String token = ultbic + "|" + duratatoken;

								// Criptazione e trasformazione in base64
								byte[] keyEncrypt = chiavetoken.getBytes();
								Cipher cipherEncrypt = Cipher.getInstance("AES");
								SecretKeySpec secretKeySpecEncrypt = new SecretKeySpec(keyEncrypt, "AES");
								cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKeySpecEncrypt);

								// Crypt
								byte[] tokenEncrypt = cipherEncrypt.doFinal(token.getBytes());

								// Trasformazione Base64
								String tokenEncryptBase64 = Base64.encode(tokenEncrypt);

								// Codifica SAFE URL
								String tokenEncryptBase64_URL_SAFE = URLEncoder.encode(tokenEncryptBase64, "UTF-8");

								URI uri = new URI(urldoc);
								String parametri = uri.getQuery();
								String nuoviParametri = null;

								if (parametri == null) {
									nuoviParametri = "?token=" + tokenEncryptBase64_URL_SAFE;
								} else {
									nuoviParametri = "&token=" + tokenEncryptBase64_URL_SAFE;
								}

								urldoc += nuoviParametri;
								esito = true;


							} else {
								messaggio = MESSAGE_DIMENSIONE_CHIAVE_TOKEN;
								esito = false;
							}
						} else {
							messaggio = MESSAGE_NO_CHIAVE_TOKEN;
							esito = false;
						}

					} else {
						messaggio = MESSAGE_NO_CHIAVE_TOKEN;
						esito = false;
					}

				} else {
					// In questo caso rimane l'url originale senza token
					esito = true;
				}

				result.put("urlcompleto", urldoc);

			}

		} catch (Throwable e) {
			messageKey = "errors.applicazione.inaspettataException";
			logger.error(this.resBundleGenerale.getString(messageKey), e);
			messaggio = e.getMessage();
			esito = false;
		}

		result.put("esito", esito);
		result.put("messaggio", messaggio);

		out.println(result);
		out.flush();
		return null;

	}

	/**
	 * Lettura e controllo durata token.
	 *
	 * @return
	 * @throws GestoreException
	 */
	private int getDurataToken() throws GestoreException {
		int duratatoken = DURATA_TOKEN_DEFAULT;
		String d = ConfigManager.getValore(INTEGRAZIONE_BIC_DURATA_TOKEN);
		if (d != null) {
			try {
				duratatoken = Integer.parseInt(d);
			} catch (NumberFormatException nfe) {
				duratatoken = DURATA_TOKEN_DEFAULT;
			}
		}
		if (logger.isDebugEnabled()) logger.debug("Integrazione BIC, durate tokeb " + duratatoken + " minuti", null);
		return duratatoken;
	}

}

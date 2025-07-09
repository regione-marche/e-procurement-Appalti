package it.eldasoft.sil.pg.web.struts.npa;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.sil.pg.bl.ControllaBandoAvvisoSimapManager;
import it.eldasoft.sil.pg.bl.InviaBandoAvvisoSimapManager;
import it.eldasoft.sil.pg.bl.LeggiPubblicazioniManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ControllaInviaFEUAJAX extends ActionAjaxLogged {

	static Logger logger = Logger.getLogger(ControllaInviaFEUAJAX.class);

	private InviaBandoAvvisoSimapManager inviaBandoAvvisoSimapManager;

	public void setInviaBandoAvvisoSimapManager(InviaBandoAvvisoSimapManager inviaBandoAvvisoSimapManager) {
		this.inviaBandoAvvisoSimapManager = inviaBandoAvvisoSimapManager;
	}

	private ControllaBandoAvvisoSimapManager controllaBandoAvvisoSimapManager;

	public void setControllaBandoAvvisoSimapManager(ControllaBandoAvvisoSimapManager controllaBandoAvvisoSimapManager) {
		this.controllaBandoAvvisoSimapManager = controllaBandoAvvisoSimapManager;
	}

	private LeggiPubblicazioniManager leggiPubblicazioniManager;

	public void setLeggiPubblicazioniManager(LeggiPubblicazioniManager leggiPubblicazioniManager) {
		this.leggiPubblicazioniManager = leggiPubblicazioniManager;
	}

	private SqlManager sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Override
	public final ActionForward runAction(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("ControllaInviaFEUAJAX: inizio metodo");
		}

		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		JSONObject jsonReturn = new JSONObject();
		PrintWriter out = response.getWriter();

		try {
			TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
			boolean commitTransaction = false;
			try {
				String codgar = request.getParameter("codgar");
				Long idForm = null;
				if (StringUtils.isNotEmpty(request.getParameter("idForm"))) {
					idForm = Long.parseLong(request.getParameter("idForm"));
				}
				String isControllato = request.getParameter("controllato");
				String isControllatoXML = request.getParameter("controllatoXML");
				String credenziali = request.getParameter("credenziali");
				String username = null;
				String password = null;
				String operazione = request.getParameter("operazione");

				boolean usaToken = false;

				ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

				if ("CORRENTI".equals(credenziali)) {
					usaToken = true;
					username = profiloUtente.getLogin();
					password = ConfigManager.getValore(InviaBandoAvvisoSimapManager.PROP_TOKEN_ACCESSO_FEU);
					if (StringUtils.isEmpty(password)) {
						throw new Exception("Valorizzare il token per l\'accesso ai servizi (property \'feu.token\')");
					}
				} else if ("ALTRE".equals(credenziali)) {
					username = request.getParameter("username");
					password = request.getParameter("password");
				}
				if ("controllaInvia".equals(operazione)) {
					if ("false".equals(isControllato)) {

						HashMap<String, Object> result = controllaBandoAvvisoSimapManager
								.controllaBandoAvvisoSimap(codgar, idForm, null, null, username, password);
						ArrayList<String> erroriBloccanti = (ArrayList<String>) result.get("erroriBloccanti");
						ArrayList<String> erroriNonBloccanti = (ArrayList<String>) result.get("erroriNonBloccanti");
						if (erroriBloccanti.size() == 0 && erroriNonBloccanti.size() == 0) {
							inviaBandoAvvisoSimapManager.eliminaEFORM(idForm);
							String res = inviaBandoAvvisoSimapManager.inviaBandoAvvisoSimap(codgar, idForm, null, null, username, password, request, usaToken);
							JSONObject jsonResult = JSONObject.fromObject(res);
							boolean esito = (Boolean) jsonResult.get("esito");
							if (!esito) {
								String messaggio = (String) jsonResult.get("messaggio");
								logger.error("inviabandoavvisosimap ERROR : " + messaggio);
								jsonReturn.put("Esito", "Errore");
								jsonReturn.put("MsgErrore", messaggio);
							} else {
								commitTransaction = true;
								jsonReturn.put("idAvviso",getUid(idForm));
							}
						} else {
							jsonReturn.put("erroriNonBloccanti", erroriNonBloccanti);
							jsonReturn.put("erroriBloccanti", erroriBloccanti);
						}
					} else if ("true".equals(isControllato)) {
						inviaBandoAvvisoSimapManager.eliminaEFORM(idForm);
						String res = inviaBandoAvvisoSimapManager.inviaBandoAvvisoSimap(codgar, idForm, null, null, username, password, request, usaToken);
						JSONObject jsonResult = JSONObject.fromObject(res);
						boolean esito = (Boolean) jsonResult.get("esito");
						if (!esito) {
							String messaggio = (String) jsonResult.get("messaggio");
							logger.error("inviabandoavvisosimap ERROR : " + messaggio);
							jsonReturn.put("Esito", "Errore");
							jsonReturn.put("MsgErrore", messaggio);
						} else {
							commitTransaction = true;
							jsonReturn.put("idAvviso",getUid(idForm));
						}
					}
				} else if ("leggi".equals(operazione)) {
					JSONArray ja = leggiPubblicazioniManager.leggiPubblicazioni(codgar, request, idForm, isControllatoXML);
					JSONObject pubblicazione = ja.getJSONObject(ja.size()-1);
					Boolean isRettifica = pubblicazione.getBoolean("rettifica");
					JSONArray eformsValidate = (JSONArray) pubblicazione.get("eforms_validate");
					JSONObject validazione = null;
					Boolean contieneErrori = false;
					String uuid_eform = (String) sqlManager.getObject("select uuid_eform from g1npascheda where idForm=?", new Object[] { idForm });
					if (isRettifica && StringUtils.isEmpty(uuid_eform)) {
						String uuid_eform_rettifica = pubblicazione.getString("uuid");
						Long countUUIDDoppione = (Long) sqlManager.getObject("select count(uuid_eform) from g1npascheda where uuid_eform=?", new Object[] {uuid_eform_rettifica});
						if (countUUIDDoppione != null && countUUIDDoppione > 0) {
							throw new Throwable("L'identificativo avviso: " + uuid_eform_rettifica + " è già collegato ad un AnacForm");
						}
						sqlManager.update("update g1npascheda set uuid_eform=? where idForm=?", new Object[] { uuid_eform_rettifica, idForm });
					}
					if (eformsValidate != null) {
						validazione = (JSONObject) eformsValidate.get(0);
						contieneErrori = ((Integer) validazione.get("numeroErrori")) > 0;
					}
					if ("false".equals(isControllatoXML)) {
						jsonReturn.put("eformsValidate", validazione);
					} else if ("true".equals(isControllatoXML)) {
						if (!contieneErrori) {
							inviaBandoAvvisoSimapManager.salvaXML(pubblicazione, idForm);
							commitTransaction = true;
						}
					}
				}
			} catch (Throwable e) {
				String messaggioPerLog = e.getMessage();
				logger.error(messaggioPerLog, e);
				jsonReturn.put("Esito", "Errore");
				jsonReturn.put("MsgErrore", messaggioPerLog);
			} finally {
				try {
					if (commitTransaction) {
						this.sqlManager.commitTransaction(transazioneUpdate);
					} else {
						this.sqlManager.rollbackTransaction(transazioneUpdate);
					}
				} catch (SQLException e) {
					throw new Throwable("Errore durante la chiusura della transazione", e);
				}
			}
		} catch (Throwable e) {
			String messaggioPerLog = e.getMessage();
			logger.error(messaggioPerLog, e);
			jsonReturn.put("Esito", "Errore");
			jsonReturn.put("MsgErrore", messaggioPerLog);
		}
		out.print(jsonReturn);
		out.flush();
		if (logger.isDebugEnabled()) {
			logger.debug("ControllaInviaFEUAJAX: fine metodo");
		}
		return null;
	}

	private String getUid(Long idForm) throws Throwable {
		String uidQuery = "select UUID_EFORM from G1NPASCHEDA where idform=?";
		String uuid = (String) sqlManager.getObject(uidQuery, new Object[] { idForm });
		return uuid;
	}

}
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestionePATManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.internetsoluzioni.www.WebService.ResponseWS;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreAggionaDatiPubblicatiSuPortalePAT extends AbstractGestoreEntita {

	static Logger logger = Logger.getLogger(GestoreAggionaDatiPubblicatiSuPortalePAT.class);

	private GestionePATManager gestionePATManager;

	private GenChiaviManager genChiaviManager;

	public GestoreAggionaDatiPubblicatiSuPortalePAT() {
		super(false);
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);

		gestionePATManager = (GestionePATManager) UtilitySpring.getBean("gestionePATManager", this.getServletContext(),
				GestionePATManager.class);

		genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager", this.getServletContext(),
				GenChiaviManager.class);

	}

	@Override
	public String getEntita() {
		return "GARE";
	}

	@Override
	public void postDelete(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
	public void postInsert(DataColumnContainer datiForm) throws GestoreException {

	}

	@Override
	public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
	public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
//		String codgar = datiForm.getString("GARE.CODGAR1");
//		String ngara = datiForm.getString("GARE.NGARA");
//		Long genere = datiForm.getLong("GARE.GENERE");
//		String strutturaPAT = UtilityStruts.getParametroString(this.getRequest(), "strutturaPAT");
//		String uidBando = "";
//		String uidEsito = "";
//
//		String messageKey = "";
//		String errMsgEvento = "";
//		int livEvento = 1;
//		String oggEvento = ngara;
//		if (genere.longValue() == 1 || genere.longValue() == 3)
//			oggEvento = codgar;
//		String codEvento = "GA_ALLINEA_GARA_PAT";
//		String descr = "Allineamento dati gara su sito istituzionale " + GestionePATManager.getNomeIstituzionale();
//
//		boolean esistePubbli = false;
//		boolean esistePubg = false;
//
//		try {
//			// BANDO
//			uidBando = gestionePATManager.getGaruuid(codgar, "BANDO");
//			// Controllo esistenza data pubblicazione bando su portale
//			if (!"".equals(uidBando)) {
//				esistePubbli = gestionePATManager.getPubblicazione(codgar, "BANDO");
//				if (!esistePubbli) {
//					livEvento = 3;
//					messageKey = "errors.gestoreException.*.PAT.AllineamentoDati.noBando";
//					errMsgEvento = this.resBundleGenerale.getString(messageKey);
//					throw new GestoreException(errMsgEvento, "PAT.AllineamentoDati.noBando", null, new Exception());
//				} else {
//					esistePubbli = true;
//				}
//			}
//
//			// ESITO
//			uidEsito = gestionePATManager.getGaruuid(codgar, "ESITO");
//
//			// Controllo esistenza data pubblicazione esito su portale
//			if (!"".equals(uidEsito)) {
//				esistePubg = gestionePATManager.getPubblicazione(codgar, "ESITO");
//				if (!esistePubg) {
//					livEvento = 3;
//					messageKey = "errors.gestoreException.*.PAT.AllineamentoDati.noEsito";
//					errMsgEvento = this.resBundleGenerale.getString(messageKey);
//					throw new GestoreException(errMsgEvento, "PAT.AllineamentoDati.noEsito", null, new Exception());
//				} else {
//					esistePubg = true;
//				}
//
//			}
//		} catch (SQLException e) {
//			livEvento = 3;
//			errMsgEvento = "Errore nel controllo della presenza delle pubblicazioni " + e.getMessage();
//			throw new GestoreException(errMsgEvento, null, null, e);
//		} finally {
//			if (livEvento == 3) {
//				this.getRequest().setAttribute("allineamentoEseguito", "NO-Controlli");
//				// Tracciatura eventi
//				try {
//					LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
//					logEvento.setLivEvento(livEvento);
//					logEvento.setOggEvento(oggEvento);
//					logEvento.setCodEvento(codEvento);
//					logEvento.setDescr(descr);
//					logEvento.setErrmsg(errMsgEvento);
//					LogEventiUtils.insertLogEventi(logEvento);
//				} catch (Exception le) {
//					messageKey = "errors.logEventi.inaspettataException";
//					logger.error(this.resBundleGenerale.getString(messageKey), le);
//				}
//			}
//		}
//
//		livEvento = 1;
//		String esitoAllineamento = "OK";
//		try {
//			String tipoPubblicazione = "BANDO";
//			boolean proseguire = esistePubbli;
//			String tipric = GestionePATManager.getNomeIstituzionale() + "PAT_bando";
//			String uiid = uidBando;
//			errMsgEvento = "";
//			for (int j = 0; j < 2; j++) {
//				if (j == 1) {
//					tipoPubblicazione = "ESITO";
//					proseguire = esistePubg;
//					tipric = GestionePATManager.getNomeIstituzionale() + "PAT_esito";
//					uiid = uidEsito;
//				}
//				if (proseguire) {
//					try {
//						HashMap<String, String> risposta = this.gestionePATManager.allineamentoDatiGara(ngara, codgar,
//								genere, strutturaPAT, tipoPubblicazione, uiid, esistePubbli);
//						if (risposta != null) {
//							if (risposta.get("msg") != null && !"".equals(risposta.get("msg"))) {
//								if ("OK".equals(esitoAllineamento))
//									esitoAllineamento = "NOK";
//								esitoAllineamento += "-" + tipoPubblicazione;
//								String msg = risposta.get("msg");
//								livEvento = 3;
//								messageKey = "errors.gestoreException.*.PAT.AllineamentoDati." + tipoPubblicazione
//										+ ".esitoNOK";
//								String tmpErrMsgEvento = this.resBundleGenerale.getString(messageKey) + msg;
//								errMsgEvento += tmpErrMsgEvento + "\n";
//							} else {
//								String numLotti = risposta.get("numLotti");
//								int numLottiInt = Integer.parseInt(numLotti);
//								boolean erroreInsGaruuid = false;
//								if (numLottiInt > 0) {
//									Integer newId = null;
//									String codiceLotto = null;
//									String uuid = null;
//									for (int i = 1; i <= numLottiInt; i++) {
//										codiceLotto = null;
//										newId = this.genChiaviManager.getNextId("GARUUID");
//										if (risposta.get("idLotto" + i) != null
//												&& !"".equals(risposta.get("idLotto" + i))) {
//											uuid = risposta.get("idLotto" + i);
//										} else
//											uuid = null;
//										if (risposta.get("codiceLotto" + i) != null
//												&& !"".equals(risposta.get("codiceLotto" + i))) {
//											codiceLotto = risposta.get("codiceLotto" + i);
//										}
//										try {
//											this.sqlManager.update(
//													"insert into garuuid(id,codgar,tipric,uuid,ngara) values(?,?,?,?,?)",
//													new Object[] { newId, codgar, tipric, uuid, codiceLotto });
//										} catch (SQLException e) {
//											erroreInsGaruuid = true;
//											livEvento = 3;
//											errMsgEvento += "Errore nell'inserimento id GARUUID dei dati del lotto "
//													+ codiceLotto + " " + e.getMessage() + "\n";
//										}
//									}
//									if (erroreInsGaruuid) {
//										if ("OK".equals(esitoAllineamento))
//											esitoAllineamento = "NOK";
//										esitoAllineamento += "-" + tipoPubblicazione;
//									}
//								}
//								if ("BANDO".equals(tipoPubblicazione)) {
//									ResponseWS rispostaEditAvviso = gestionePATManager.editAvvisoCollegato(uiid, strutturaPAT);
//								}
//							}
//						}
//					} catch (GestoreException e) {
//						if ("OK".equals(esitoAllineamento))
//							esitoAllineamento = "NOK";
//						esitoAllineamento += "-" + tipoPubblicazione;
//						livEvento = 3;
//						messageKey = "errors.gestoreException.*.PAT.AllineamentoDati." + tipoPubblicazione
//								+ ".esitoNOK";
//						String tmpErrMsgEvento = this.resBundleGenerale.getString(messageKey) + e.getMessage();
//						errMsgEvento += tmpErrMsgEvento + "\n";
//					}
//				}
//			}
//			this.getRequest().setAttribute("allineamentoEseguito", esitoAllineamento);
//			this.getRequest().setAttribute("gara", ngara);
//			this.getRequest().setAttribute("codiceGara", codgar);
//
//		} finally {
//
//			// Tracciatura eventi
//			try {
//				LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
//				logEvento.setLivEvento(livEvento);
//				logEvento.setOggEvento(oggEvento);
//				logEvento.setCodEvento(codEvento);
//				logEvento.setDescr(descr);
//				logEvento.setErrmsg(errMsgEvento);
//				LogEventiUtils.insertLogEventi(logEvento);
//			} catch (Exception le) {
//				messageKey = "errors.logEventi.inaspettataException";
//				logger.error(this.resBundleGenerale.getString(messageKey), le);
//			}
//		}
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
	}
}
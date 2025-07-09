package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import net.sf.json.JSONObject;

public class AssegnaEtichettaComunicazioneAJAXAction extends Action {

	private SqlManager sqlManager;

	static Logger logger = Logger.getLogger(AssegnaEtichettaComunicazioneAJAXAction.class);

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Override
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws JspException {

		int livEvento = 1;
		String codEvento = "INVCOM_ETICHETTA";
		String oggEvento = "";
		String descrEvento = "";
		String errMsgEvento = "";
		Long etichettaVecchia = null;
		String coment,comkey1,comkey2,comtipo,codiceGara,tipoGara;

		String idPrg = null;
		Long idCom = null;
		try {
			DataSourceTransactionManagerBase.setRequest(request);
			response.setHeader("cache-control", "no-cache");
			response.setContentType("text/text;charset=utf-8");

			idPrg = request.getParameter("idPrg");
			idCom = Long.parseLong(request.getParameter("idCom"));
			Long etichetta = null;
			Boolean eliminaEtichetta = false;
			if ("nessunaEtichetta".equals(request.getParameter("etichetta"))) {
				eliminaEtichetta = true;
			}
			Vector<?> datiComunicazione = sqlManager.getVector("select cometich,coment,comkey1,comkey2,comtipo from w_invcom where idprg=? and idcom=?", new Object[] { idPrg, idCom });
			etichettaVecchia = SqlManager.getValueFromVectorParam(datiComunicazione, 0).longValue();
			coment = SqlManager.getValueFromVectorParam(datiComunicazione, 1).stringValue();
			comkey1 = SqlManager.getValueFromVectorParam(datiComunicazione, 2).stringValue();
			comkey2 = SqlManager.getValueFromVectorParam(datiComunicazione, 3).stringValue();
			comtipo = SqlManager.getValueFromVectorParam(datiComunicazione, 4).stringValue();

			if ("FS12".equals(comtipo)) {
				codiceGara = comkey2;
			} else {
				codiceGara = comkey1;
			}
			if ("G1STIPULA".equals(coment) || coment != null) {
				tipoGara = coment + " ";
			} else {
				tipoGara = "GARE ";
			}

			oggEvento = idPrg + "/" + idCom;
			descrEvento = "Assegna etichetta alla comunicazione (rif. " + tipoGara + codiceGara + "): valore vecchio " + etichettaVecchia + ", valore nuovo ";

			TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
			boolean commitTransaction = false;
			try {
				if (eliminaEtichetta) {
					sqlManager.update("update w_invcom set cometich=null where idprg=? and idcom=?", new Object[] { idPrg, idCom });
					descrEvento += "null";
				} else {
					etichetta = Long.parseLong(request.getParameter("etichetta"));
					descrEvento += etichetta;
					String descrizioneEtichetta = sqlManager
							.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip=?", new Object[] { "W_012", etichetta })
							.toString();
					if (StringUtils.isEmpty(descrizioneEtichetta)) {
						throw new Throwable("Nessuna etichetta trovata per il valore inserito");
					}
					sqlManager.update("update w_invcom set cometich=? where idprg=? and idcom=?", new Object[] { etichetta, idPrg, idCom });
				}
				commitTransaction = true;
			} catch (Throwable e) {
				livEvento = 3;
				errMsgEvento = e.getMessage();
			} finally {
				if (transazioneUpdate != null) {
					try {
						if (commitTransaction) {
							this.sqlManager.commitTransaction(transazioneUpdate);
						} else {
							this.sqlManager.rollbackTransaction(transazioneUpdate);
						}
					} catch (Throwable e) {
						livEvento = 3;
						errMsgEvento = e.getMessage();
						logger.error("Errore durante la chiusura della transazione");
					} finally {
						if (livEvento != 3) {
							PrintWriter out = response.getWriter();
							JSONObject result = new JSONObject();
							out.print(result);
							out.flush();
						}
					}
				}
			}
		} catch (SQLException | IOException | GestoreException e) {
			livEvento = 3;
			errMsgEvento = e.getMessage();
		}
		try {
			LogEvento logEvento = LogEventiUtils.createLogEvento(request);
			logEvento.setCodApplicazione("PG");
			logEvento.setOggEvento(oggEvento);
			logEvento.setLivEvento(livEvento);
			logEvento.setCodEvento(codEvento);
			logEvento.setDescr(descrEvento);
			logEvento.setErrmsg(errMsgEvento);
			LogEventiUtils.insertLogEventi(logEvento);
		} catch (Exception loggerError) {
			logger.error("Errore inaspettato durante la tracciatura su w_logeventi", loggerError);
		} catch (Throwable e) {
			logger.error("Errore inaspettato durante l'inserimento dei messaggi di fine procedura", e);
		}
		return null;
	}
}
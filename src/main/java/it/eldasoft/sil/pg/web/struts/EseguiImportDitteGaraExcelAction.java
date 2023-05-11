/*
 * Created on 22/Mar/2022
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
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportDitteGaraManager;
import it.eldasoft.sil.pg.bl.LoggerImportDitte;

/**
 * Action per eseguire l'import ditte gara su foglio Excel
 *
 * @author Riccardo.Peruzzo
 */
public class EseguiImportDitteGaraExcelAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(EseguiImportDitteGaraExcelAction.class);

	private ImportExportDitteGaraManager importExportDitteGaraManager = null;

	private SqlManager sqlManager;

	/**
	 * @param importExportDitteGaraManager importExportDitteGaraManager da settare
	 *                                     internamente alla classe.
	 */
	public void setImportExportDitteGaraManager(ImportExportDitteGaraManager importExportDitteGaraManager) {
		this.importExportDitteGaraManager = importExportDitteGaraManager;
	}

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if (logger.isDebugEnabled())
			logger.debug("runAction: inizio metodo");

		String target = CostantiGeneraliStruts.FORWARD_OK;
		String messageKey = null;
		String risultato = "OK";
		TransactionStatus status = null;
		LoggerImportDitte loggerImport = null;
		boolean errore = false;

		// Lettura codgar in cui importare i lotti
		String ngara = null;
		if (request.getParameter("ngara") != null)
			ngara = request.getParameter("ngara");
		else
			ngara = (String) request.getAttribute("ngara");

		String genereGara = null;
		if (request.getParameter("genereGara") != null)
			genereGara = request.getParameter("genereGara");
		else
			genereGara = (String) request.getAttribute("genereGara");

		int livEvento = 1;
		String oggEvento = ngara;
		String codEvento = "GA_IMPORTA_DITTA_EXCEL";
		String descrEvento = "Importazione ditte da Excel";
		String errMsgEvento = "";

		int countInsAnagrafica = 0;
		int countImport = 0;
		int countImportRT = 0;
		int countFailure = 0;

		UploadFileForm fileExcel = (UploadFileForm) form;

		if (fileExcel != null && fileExcel.getSelezioneFile().getFileSize() > 0) {
			try {
				status = sqlManager.startTransaction();

				loggerImport = this.importExportDitteGaraManager.importDitteGara((UploadFileForm) form, ngara,
						request.getSession(), genereGara, request, status);

				sqlManager.update("update GARE set FASGAR = -3, STEPGAR = -30 where ngara = ?", new Object[] { ngara });

				if (loggerImport.getListaMsgVerificaFoglio().size() > 0)
					risultato = "KO";
				else
					risultato = "OK";

				request.setAttribute("loggerImport", loggerImport);
				request.setAttribute("RISULTATO", risultato);

			} catch (IOException io) {
				messageKey = "errors.importDitteExcel.erroreIO";
				logger.error(this.resBundleGenerale.getString(messageKey), io);
				this.aggiungiMessaggio(request, messageKey);
				livEvento = 3;
				errore = true;
			} catch (GestoreException g) {
				messageKey = "errors.importDitteExcel.erroreLetturaDatiEstratti";
				this.aggiungiMessaggio(request, messageKey);
				livEvento = 3;
				errore = true;
				logger.error(this.resBundleGenerale.getString(messageKey), g);
			} catch (Exception e) {
				messageKey = "errors.importDitteExcel.erroreGen";
				logger.error(this.resBundleGenerale.getString(messageKey), e);
				this.aggiungiMessaggio(request, messageKey);
				livEvento = 3;
				errore = true;
			} catch (Throwable t) {
				messageKey = "errors.applicazione.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), t);
				this.aggiungiMessaggio(request, messageKey);
				livEvento = 3;
				errore = true;
			} finally {
				// A prescindere dall'esito dell'importazione, si ripristina
				// i valori letti dal request all'inizio della Action

				if (status != null) {
					try {
						if (!errore) {
							this.sqlManager.commitTransaction(status);
						} else {
							this.sqlManager.rollbackTransaction(status);
						}
					} catch (SQLException e) {
					}
				}

				request.setAttribute("ngara", ngara);
				request.setAttribute("genereGara", genereGara);

				if (messageKey != null) {
					// Si e' verificato un errore
					request.setAttribute("RISULTATO", "KO");
					target = "errorImport";
				}
			}

			if(!errore) {
    			countImport = loggerImport.getNumeroRecordImportati();
    			countImportRT = loggerImport.getNumeroRecordImportatiRT();
    			countInsAnagrafica = loggerImport.getnumeroAnagraficaDitteInserite();
    			countFailure = loggerImport.getNumeroRecordNonImportati();
    			if (loggerImport.getListaDitteImportate() != null && !"".equals(loggerImport.getListaDitteImportate())) {
    				errMsgEvento = "Lista ditte inserite: \n";
    				errMsgEvento += String.join("\n", loggerImport.getListaDitteImportate());
    			}

			}

			descrEvento += " (n.ditte inserite in gara " + countImport
                + ", n.ditte inserite come componenti RT " + countImportRT + ", n.ditte inserite in anagrafica "
                + countInsAnagrafica + ", n.righe non elaborate in seguito a errori: " + countFailure + ").";

			LogEvento logEvento = LogEventiUtils.createLogEvento(request);
			logEvento.setLivEvento(livEvento);
			logEvento.setOggEvento(oggEvento);
			logEvento.setCodEvento(codEvento);
			logEvento.setDescr(descrEvento);
			logEvento.setErrmsg(errMsgEvento);
			LogEventiUtils.insertLogEventi(logEvento);

		} else {
			// il file in upload e' null oppure e' un file di dimensione pari a 0 byte
			// TODO gestire il caso ...
		}
		if (logger.isDebugEnabled())
			logger.debug("runAction: fine metodo");
		return mapping.findForward(target);
	}

}
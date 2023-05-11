/*
 * 	Created on 13/09/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.Adempimenti190Manager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * @author marco.perazzetta
 *
 */
/**
 * Viene eseguito il download del modello adempimenti per la legge 190, con dati
 * di esempio o dati reali dell'anno di riferimento
 */
public class ExportAdempimenti190Action extends ActionBaseNoOpzioni {

	protected static final String FORWARD_ERRORE_DOWNLOAD = "errorDownload";
	static Logger logger = Logger.getLogger(ExportAdempimenti190Action.class);
	private Adempimenti190Manager adempimenti190Manager;

	/**
	 * @param adempimenti190Manager The adempimenti190Manager to set.
	 */
	public void setAdempimenti190Manager(Adempimenti190Manager adempimenti190Manager) {
		this.adempimenti190Manager = adempimenti190Manager;
	}

	private FileManager fileManager;

	/**
	 * @param fileManager The fileManager to set.
	 */
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("ExportAdempimenti190Action: inizio metodo");
		}

		String messageKey;
		String logMessageKey;
		String idanticor = request.getParameter("chiave");
		String anno = request.getParameter("anno");
		String tipoExport = request.getParameter("tipoExport");
		String percorsoFile = request.getSession().getServletContext().getRealPath("/") + "xlsx/";
		
		try {
			if (tipoExport == null || tipoExport.equals("") || tipoExport.equals("standard")) {

				fileManager.download(percorsoFile, Adempimenti190Manager.MODELLO_ADEMPIMENTI_LEGGE_190, response);

			} else if (tipoExport.equals("annorif") && idanticor != null && !idanticor.equals("")) {

				byte[] excelOutput = adempimenti190Manager.createExcel(
						percorsoFile + Adempimenti190Manager.MODELLO_ADEMPIMENTI_LEGGE_190, Long.parseLong(idanticor),
						(ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE));
				fileManager.download(anno + Adempimenti190Manager.MODELLO_ADEMPIMENTI_LEGGE_190, excelOutput, response);

			}
		} catch (Exception ex) {

			if (ex instanceof FileManagerException) {

				logMessageKey = ((FileManagerException) ex).getFamiglia() + "." + ((FileManagerException) ex).getCodiceErrore();
				logger.error(this.resBundleGenerale.getString(logMessageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0), (String) ((FileManagerException) ex).getParametri()[0]), ex);
				messageKey = "errors.download";
				if (logMessageKey.equals(FileManagerException.CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE)
								|| logMessageKey.equals(FileManagerException.CODICE_ERRORE_FILE_INESISTENTE)) {
					messageKey += ".noAccessoFile";
				}
			} else if (ex instanceof GestoreException) {
				if (!((GestoreException) ex).getCodice().contains("SQL")) {
					messageKey = "errors.gestoreException.*." + ((GestoreException) ex).getCodice();
				} else {
					messageKey = "errors.gestoreException.*.importaesportaexcel.erroreinaspettato";
				}				
				logger.error(this.resBundleGenerale.getString(messageKey), ex);
				if (((GestoreException) ex).getParameters() == null) {					
					this.aggiungiMessaggio(request, messageKey);
				} else {
					this.aggiungiMessaggio(request, messageKey, (String) ((GestoreException) ex).getParameters()[0]);
				}
			} else {
				messageKey = "errors.applicazione.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), ex);
				this.aggiungiMessaggio(request, messageKey);
			}			
			// Setto la idanticor per l'apertura e la pagina attiva come prima
			request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, idanticor);
			request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE, "0");
			return mapping.findForward(FORWARD_ERRORE_DOWNLOAD);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ExportAdempimenti190Action: fine metodo");
		}
		return null;
	}
}
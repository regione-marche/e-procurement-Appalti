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
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.tags.utils.UtilityTags;
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
 * Viene eseguito il download del modello degli articoli
 */
public class ExportModelloArticoliAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(ExportModelloArticoliAction.class);
	private static final String MODELLO_ARTICOLI = "articoli.xls";

	private FileManager fileManager;

	/**
	 * @param fileManager The fileManager to set.
	 */
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Override
	protected ActionForward runAction(ActionMapping mapping,
					ActionForm form,
					HttpServletRequest request,
					HttpServletResponse response) throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("ExportModelloArticoliAction: inizio metodo");
		}

		String messageKey;
		String logMessageKey;
		String idGaraCatalogo = request.getParameter("chiave");
		String percorsoFile = request.getSession().getServletContext().getRealPath("/") + "xls/";

		try {
			fileManager.download(percorsoFile, MODELLO_ARTICOLI, response);
		} catch (Exception ex) {

			if (ex instanceof FileManagerException) {

				logMessageKey = ((FileManagerException) ex).getFamiglia() + "." + ((FileManagerException) ex).getCodiceErrore();
				logger.error(this.resBundleGenerale.getString(logMessageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
								(String) ((FileManagerException) ex).getParametri()[0]), ex);
				messageKey = "errors.download";
				if (logMessageKey.equals(FileManagerException.CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE)
								|| logMessageKey.equals(FileManagerException.CODICE_ERRORE_FILE_INESISTENTE)) {
					messageKey += ".noAccessoFile";
				}
			} else {
				messageKey = "errors.applicazione.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), ex);
				this.aggiungiMessaggio(request, messageKey);
			}
			// Setto la idanticor per l'apertura e la pagina attiva come prima
			request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, idGaraCatalogo);
			request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE, "0");
			return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ExportModelloArticoliAction: fine metodo");
		}
		return null;
	}
}

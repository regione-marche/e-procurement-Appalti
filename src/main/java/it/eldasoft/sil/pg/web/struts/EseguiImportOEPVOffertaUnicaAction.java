/*
 * Created on 30/giu/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.docass.DocumentiAssociatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.gene.web.struts.docass.CostantiDocumentiAssociati;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportOEPVManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;

/**
 * Action per l'importazione da file Excel dei dati per OEPV
 *
 * @author Stefano.Cestaro
 */
public class EseguiImportOEPVOffertaUnicaAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(EseguiImportOEPVOffertaUnicaAction.class);

	private ImportExportOEPVManager importExportOEPVManager;

	private DocumentiAssociatiManager documentiAssociatiManager;

	/**
	 * @param importExportOEPVManager importExportOEPVManager da settare internamente alla classe.
	 */
	public void setImportExportOEPVManager(ImportExportOEPVManager importExportOEPVManager) {
		this.importExportOEPVManager = importExportOEPVManager;
	}

	public void setDocumentiAssociatiManager(DocumentiAssociatiManager documentiAssociatiManager){
		this.documentiAssociatiManager = documentiAssociatiManager;
	}

	@Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (logger.isDebugEnabled())
			logger.debug("ImportazioneExcelAction: inizio metodo");

		String target = "successimportazione";
		String messageKey = null;

		String codgar = request.getParameter("codgar");
		String lottoselezionato = request.getParameter("lottoSelezionato");
		String listalottiselezionati = "";

		if ( request.getParameter("listaLottiSelezionati") != null)
			listalottiselezionati = request.getParameter("listaLottiSelezionati");

		String archiviaXLSDocAss = request.getParameter("archiviaXLSDocAss");

		String step = request.getParameter("step");
        request.setAttribute("step", step);

		request.setAttribute("CODGAR", codgar);
		request.setAttribute("LOTTOSELEZIONATO", lottoselezionato);
		request.setAttribute("LISTALOTTISELEZIONATI",listalottiselezionati);
		request.setAttribute("ARCHIVIAXLSDOCASS",archiviaXLSDocAss);

		try {
			UploadFileForm documentoXLS = (UploadFileForm) form;
			Workbook XLS = WorkbookFactory.create(documentoXLS.getSelezioneFile().getInputStream());
			
			// Import dei dati dal file Excel
			this.importExportOEPVManager.importazione(lottoselezionato, step, XLS);

			if("1".equals(archiviaXLSDocAss)){
				// Creazione di un file temporaneo del file Excel uploadato
			    File tempFile = TempFileUtilities.getTempFile(documentoXLS.getSelezioneFile().getFileName(),request.getSession());
				FileUtils.writeByteArrayToFile(tempFile,documentoXLS.getSelezioneFile().getFileData());

				String moduloAttivo = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
				DocumentoAssociato docAss = new DocumentoAssociato();
				docAss.setEntita("GARE");
				docAss.setCodApp(moduloAttivo);
				docAss.setCampoChiave1(lottoselezionato);
				docAss.setCampoChiave2("#");
				docAss.setCampoChiave3("#");
				docAss.setCampoChiave4("#");
				docAss.setCampoChiave5("#");
				docAss.setDataInserimento(new Date());
				int stepAttuale = Integer.parseInt(step);
                if(stepAttuale==GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE)
                  docAss.setTitolo(ImportExportOEPVManager.TITOLO_FILE_C0OGASS_IMPORT_ECONOMICI);
                else
                  docAss.setTitolo(ImportExportOEPVManager.TITOLO_FILE_C0OGASS_IMPORT_TECNICI);
				docAss.setNomeDocAss(ImportExportOEPVManager.NOME_FILE_C0OGGASS_IMPORT);
				docAss.setPathDocAss(CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT);

				this.documentiAssociatiManager.associaFile(docAss, FileUtils.readFileToByteArray(tempFile));
				// Cancellazione del file (se non si riesce a cancellare, verra' rimosso
				// allo scadere della sessione)
				tempFile.delete();
			}

			// Aggiornamento della lista dei lotti selezionati
			if (listalottiselezionati != null && !"".equals(listalottiselezionati ))
				listalottiselezionati += ",";

			listalottiselezionati += lottoselezionato;
			request.setAttribute("LISTALOTTISELEZIONATI",listalottiselezionati);
			request.setAttribute("RISULTATO", "OPERAZIONEESEGUITA");

		} catch (IOException ioe) {
			target = "errorimportazione";
			messageKey = "errors.importazioneexcel.excelio";
			logger.error(this.resBundleGenerale.getString(messageKey), ioe);
			this.aggiungiMessaggio(request, messageKey);
			request.setAttribute("RISULTATO", "ERRORI");

		} catch (GestoreException e) {
			target = "errorimportazione";
			messageKey = "errors.gestoreException.*." + e.getCodice();
			if (e.getParameters() == null) {
				logger.error(e.getMessage(), e);
				this.aggiungiMessaggio(request, messageKey);
			} else {
				logger.error(e.getMessage(), e);
				this.aggiungiMessaggio(request, messageKey,
						(String) e.getParameters()[0]);
			}
			request.setAttribute("RISULTATO", "ERRORI");

		} catch (Throwable t) {
			target = "errorimportazione";
			messageKey = "errors.applicazione.inaspettataException";
			logger.error(this.resBundleGenerale.getString(messageKey), t);
			this.aggiungiMessaggio(request, messageKey);
			request.setAttribute("RISULTATO", "ERRORI");
		}

		if (logger.isDebugEnabled())
			logger.debug("ImportazioneExcelAction: fine metodo");

		return mapping.findForward(target);
	}

}
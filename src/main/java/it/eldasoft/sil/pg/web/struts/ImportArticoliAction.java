package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ArticoliManager;
import it.eldasoft.sil.pg.bl.excel.ExcelResultBean;
import it.eldasoft.sil.pg.bl.excel.bean.ImportArticoliConfigBean;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import javax.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.springframework.web.context.ServletContextAware;

/**
 *
 * @author Abdelhak.Benkadour
 *
 */
public class ImportArticoliAction extends ActionBaseNoOpzioni implements ServletContextAware {

	protected static final String FORWARD_ERRORE_DOWNLOAD = "error";
	protected static final String FORWARD_SUCCESS_DOWNLOAD = "success";
	static Logger logger = Logger.getLogger(ImportArticoliAction.class);
	private ArticoliManager articoliManager;
	protected ServletContext context;

	/**
	 * @param articoliManager The articoliManager to set.
	 */
	public void setArticoliManager(ArticoliManager articoliManager) {
		this.articoliManager = articoliManager;
	}

	@Override
	protected ActionForward runAction(ActionMapping mapping,
					ActionForm form,
					HttpServletRequest request,
					HttpServletResponse response) throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("ImportArticoliAction: inizio metodo");
		}
		String messageKey;
		String target = FORWARD_SUCCESS_DOWNLOAD;
		String idGaraCatalogo = request.getParameter("chiave");
		ExcelResultBean result = null;
		ImportArticoliConfigBean configBean = null;
		FormFile file = null;

		try {
			UploadFileForm uploadForm = (UploadFileForm) form;
			if (uploadForm.getSelezioneFile() == null) {
				throw new GestoreException("Non è stato selezionato alcun file da importare", "importaesportaexcel.nofile");
			}
			file = uploadForm.getSelezioneFile();
			checkMaxExcelFileSize(file);
			configBean = populateImportBean(request, file);
			result = articoliManager.importData(configBean, resBundleGenerale);
			request.setAttribute("numRigheSuccesso", result.getNumRigheSuccesso());
			request.setAttribute("numRigheTotali", result.getNumRigheTotaliAnalizzate());
			request.setAttribute("numeroRigheArticoliGiaInCatalogo", result.getNumeroRigheArticoliGiaInCatalogo());
			request.setAttribute("codiciArticoliScartati", result.getCodiciArticoliScartati().toString());
			request.setAttribute("errori", result.getRigheConErrore());

		} catch (Exception ex) {

			if (ex instanceof GestoreException) {
				if (!((GestoreException) ex).getCodice().contains("SQL")) {
					messageKey = "errors.gestoreException.*." + ((GestoreException) ex).getCodice();
				} else {
					messageKey = "errors.gestoreException.*.importaesportaexcel.erroreinaspettato";
				}
				if (((GestoreException) ex).getParameters() == null) {
					this.aggiungiMessaggio(request, messageKey);
					logger.error(this.resBundleGenerale.getString(messageKey), ex);
				} else {
					this.aggiungiMessaggio(request, messageKey, (String) ((GestoreException) ex).getParameters()[0]);
					logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
									(String) ((GestoreException) ex).getParameters()[0]), ex);
				}
			} else {
				messageKey = "errors.applicazione.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), ex);
				this.aggiungiMessaggio(request, messageKey);
			}
			// Setto la idanticor per l'apertura e la pagina attiva come prima
			request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, idGaraCatalogo);
			request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE, "0");
		}
		if (configBean != null && result != null) {
			logger.info("\nE' stato elaborato il file " + configBean.getFile().getFileName() + "\nNumero di righe processate: " + result.getNumRigheTotaliAnalizzate()
							+ "\nNumero di righe importate con successo: " + result.getNumRigheSuccesso() + "\nNumero di righe con errori: " + result.getNumRigheErrore());
		} else if (file != null) {
			logger.info("Il file (" + file.getFileName() + ") non è stato elaborato correttamente");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ImportArticoliAction: fine metodo");
		}
		return mapping.findForward(target);
	}

	private ImportArticoliConfigBean populateImportBean(HttpServletRequest request, FormFile file) throws GestoreException {

		ImportArticoliConfigBean importBean = new ImportArticoliConfigBean();
		String codiceCatalogo = request.getParameter("chiave");
		if (StringUtils.isBlank(codiceCatalogo)) {
			throw new GestoreException("Codice catalogo non valorizzato", "articoli.codicecatologononvalorizzato");
		}
		importBean.setFile(file);
		importBean.setCodiceCatalogo(codiceCatalogo);
		return importBean;
	}

	public void setServletContext(ServletContext context) {
		this.context = context;
	}

	private void checkMaxExcelFileSize(FormFile file) throws GestoreException {

		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", context, TabellatiManager.class);
		String dimTotaleFileExcelStringa = tabellatiManager.getDescrTabellato("A1096", "1");

		if (StringUtils.isBlank(dimTotaleFileExcelStringa)) {
			throw new GestoreException("Non è presente il tabellato A1096 per determinare la dimensione massima dell'upload di un file excel per l'upload",
							"excel.noTabellato", null);
		}
		int pos = dimTotaleFileExcelStringa.indexOf("(");
		if (pos < 1) {
			throw new GestoreException("Non è possibile determinare dal tabellato A1096 la dimensione massima di un file excel per l'upload",
							"excel.noValore", null);
		}
		dimTotaleFileExcelStringa = dimTotaleFileExcelStringa.substring(0, pos - 1);
		dimTotaleFileExcelStringa = dimTotaleFileExcelStringa.trim();
		double dimTotaleFileExcelByte = Math.pow(2, 20) * Double.parseDouble(dimTotaleFileExcelStringa);
		if (file.getFileSize() > dimTotaleFileExcelByte) {
			throw new GestoreException("La dimensione del file excel da importare ha superato il limite consentito di " + dimTotaleFileExcelStringa + " MB",
							"excel.overflowMultiplo", new String[]{dimTotaleFileExcelStringa}, null);
		}
	}
}

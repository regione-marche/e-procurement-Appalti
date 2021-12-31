package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.Adempimenti190Manager;
import it.eldasoft.sil.pg.bl.excel.bean.ImportAdempimenti190ConfigBean;
import it.eldasoft.sil.pg.bl.excel.bean.ImportAdempimenti190ResultBean;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import javax.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
public class ImportAdempimenti190Action extends ActionBaseNoOpzioni implements ServletContextAware {

	protected static final String FORWARD_ERRORE_DOWNLOAD = "error";
	protected static final String FORWARD_SUCCESS_DOWNLOAD = "success";
	static Logger logger = Logger.getLogger(ImportAdempimenti190Action.class);
	private Adempimenti190Manager adempimenti190Manager;
	protected ServletContext context;

	/**
	 * @param adempimenti190Manager The adempimenti190Manager to set.
	 */
	public void setAdempimenti190Manager(Adempimenti190Manager adempimenti190Manager) {
		this.adempimenti190Manager = adempimenti190Manager;
	}

	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("ImportAdempimenti190Manager: inizio metodo");
		}
		
		String messageKey;
		String target = FORWARD_SUCCESS_DOWNLOAD;
		String idAnticor = request.getParameter("chiave");
		ImportAdempimenti190ResultBean result = null;
		ImportAdempimenti190ConfigBean configBean = null;
		FormFile file = null;

		try {
			UploadFileForm uploadForm = (UploadFileForm) form;
			if (uploadForm.getSelezioneFile() == null) {
				throw new GestoreException("Non è stato selezionato alcun file da importare", "importaesportaexcel.nofile");
			}
			file = uploadForm.getSelezioneFile();
			checkMaxExcelFileSize(file);
			configBean = populateImportBean(request, file);
			result = this.adempimenti190Manager.importData(configBean, resBundleGenerale);
			request.setAttribute("numRigheSuccesso", result.getNumRigheSuccesso());
			request.setAttribute("numRigheTotali", result.getNumRigheTotaliAnalizzate());
			request.setAttribute("errori", result.getRigheConErrore());

		} catch (Exception ex) {

			if (ex instanceof GestoreException) {
				if (!((GestoreException) ex).getCodice().contains("erroreinaspettato")) {
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
			request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, idAnticor);
			request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE, "0");
		}
		if (configBean != null && result != null) {
			logger.info("\nE' stato elaborato il file " + configBean.getFile().getFileName() + "\nNumero di righe processate: " + result.getNumRigheTotaliAnalizzate()
							+ "\nNumero di righe importate con successo: " + result.getNumRigheSuccesso() + "\nNumero di righe con errori: " + result.getNumRigheErrore());
		} else if (file != null) {
			logger.info("Il file (" + file.getFileName() + ") non è stato elaborato correttamente");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ExportAdempimenti190Action: fine metodo");
		}
		return mapping.findForward(target);
	}

	private ImportAdempimenti190ConfigBean populateImportBean(HttpServletRequest request, FormFile file) throws GestoreException {

		ImportAdempimenti190ConfigBean importBean = new ImportAdempimenti190ConfigBean();
		Boolean aggiorna = Boolean.parseBoolean(request.getParameter("aggiorna"));
		Boolean impLiquidato = Boolean.parseBoolean(request.getParameter("impLiquidato"));
		Boolean impAggiudicazione = Boolean.parseBoolean(request.getParameter("impAggiudicazione"));
		Boolean dataInizio = Boolean.parseBoolean(request.getParameter("dataInizio"));
		Boolean dataUltimazione = Boolean.parseBoolean(request.getParameter("dataUltimazione"));
		Boolean partecipante = Boolean.parseBoolean(request.getParameter("partecipante"));
		String idAnticor = request.getParameter("chiave");
		String annoAdempimento = request.getParameter("anno");
		String ufficioIntestatario = null;
		HttpSession session = request.getSession();
		if (session != null) {
			ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
		}
		
		ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
				CostantiGenerali.PROFILO_UTENTE_SESSIONE);

		importBean.setUtenteAmministratore("A".equals(profiloUtente.getAbilitazioneGare()));
		importBean.setUfficioIntestatario(ufficioIntestatario);
		importBean.setAggiorna(aggiorna);
		importBean.setAggiornaDataInizio(dataInizio);
		importBean.setAggiornaDataUltimazione(dataUltimazione);
		importBean.setAggiornaImpAggiudicazione(impAggiudicazione);
		importBean.setAggiornaImpSommeLiquidate(impLiquidato);
		importBean.setAggiornaPartecipante(partecipante);
		importBean.setFile(file);
		importBean.setIdAnticor(Long.parseLong(idAnticor));
		importBean.setAnnoAdempimento(annoAdempimento);
		if (StringUtils.isNotEmpty(profiloUtente.getNome())) {
			importBean.setNomeCognomeResponsabile(profiloUtente.getNome());
		}
		if (StringUtils.isNotEmpty(profiloUtente.getCodiceFiscale())) {
			importBean.setCodiceFiscaleResponsabile(profiloUtente.getCodiceFiscale());
		}
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

package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.Adempimenti190Manager;
import it.eldasoft.sil.pg.bl.excel.bean.ImportAdempimenti190ResultBean;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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


public class ImportAdempimenti190SAPAction extends ActionBaseNoOpzioni implements ServletContextAware {

	protected static final String FORWARD_ERRORE_DOWNLOAD = "error";
	protected static final String FORWARD_SUCCESS_DOWNLOAD = "success";
	static Logger logger = Logger.getLogger(ImportAdempimenti190SAPAction.class);
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
			logger.debug("ImportAdempimenti190SAPAction: inizio metodo");
		}

		String messageKey;
		String target = FORWARD_SUCCESS_DOWNLOAD;
		String idAnticor = request.getParameter("chiave");
		ImportAdempimenti190ResultBean result = null;
		FormFile file = null;
		String uffint = null;
		HttpSession session = request.getSession();
        if (session != null) {
          uffint = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
        }

        int livEvento=1;
        String codEvento="GA_190_IMPORTA_SAP";
        String descrEvento="Importa dati da SAP nell'adempimento";
        String oggEvento = idAnticor;
        String errMsgEvento="";

		try {
			UploadFileForm uploadForm = (UploadFileForm) form;
			if (uploadForm.getSelezioneFile() == null) {
				throw new GestoreException("Non è stato selezionato alcun file da importare", "importaesportaexcel.nofile");
			}
			file = uploadForm.getSelezioneFile();
			checkMaxExcelFileSize(file);
			result = this.adempimenti190Manager.importDataSAP(idAnticor,file, uffint, resBundleGenerale);
			request.setAttribute("numRigheSuccesso", result.getNumRigheSuccesso());
			request.setAttribute("numRigheTotali", result.getNumRigheTotaliAnalizzate());
			request.setAttribute("errori", result.getRigheConErrore());
			if(result.getRigheConErrore()!=null){
			  Iterator<?> it = result.getRigheConErrore().entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<Integer,List<String>> pair = (Map.Entry<Integer,List<String>>)it.next();
			        errMsgEvento+= "Errori riga " + pair.getKey().toString() + ":\r\n";
			        List<String> lista = pair.getValue();
			        Iterator<String> it1 = lista.iterator();
			        while(it1.hasNext())
			          errMsgEvento+=it1.next() + ".\r\n";


			    }
			}

		} catch (Exception ex) {
		  livEvento =3;
		  errMsgEvento = ex.getMessage();
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

		}finally{
		  if (result != null) {
		    descrEvento +=" (n. righe totali " + result.getNumRigheTotaliAnalizzate() + ", importate " + result.getNumRigheSuccesso() + ", scartate " + result.getRigheConErrore().size() + ").";
		  }

		  LogEvento logEvento = LogEventiUtils.createLogEvento(request);
	      logEvento.setLivEvento(livEvento);
	      logEvento.setOggEvento(oggEvento);
	      logEvento.setCodEvento(codEvento);
	      logEvento.setDescr(descrEvento);
	      logEvento.setErrmsg(errMsgEvento);
	      LogEventiUtils.insertLogEventi(logEvento);
        }

		if (result != null) {
			logger.info("\nE' stato elaborato il file " + file.getFileName() + "\nNumero di righe processate: " + result.getNumRigheTotaliAnalizzate()
							+ "\nNumero di righe importate con successo: " + result.getNumRigheSuccesso() + "\nNumero di righe con errori: " + result.getNumRigheErrore());
		} else if (file != null) {
			logger.info("Il file (" + file.getFileName() + ") non è stato elaborato correttamente");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ExportAdempimenti190SAPAction: fine metodo");
		}
		return mapping.findForward(target);
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

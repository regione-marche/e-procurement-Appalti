/*
 * Created on 04/jan/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.web.struts.docass.GestioneFileDocumentiAssociatiException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportLottiGaraManager;
import it.eldasoft.sil.pg.bl.LoggerImportOffertaPrezzi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per eseguire l'import lotti gara su foglio Excel
 *
 * @author Cristian.Febas
 */
public class EseguiImportLottiGaraAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(EseguiImportLottiGaraAction.class);

	private ImportExportLottiGaraManager importExportLottiGaraManager = null;

	private SqlManager sqlManager;

    /**
     * @param importExportLottiGaraManager importExportLottiGaraManager
     * da settare internamente alla classe.
     */
    public void setImportExportLottiGaraManager(
            ImportExportLottiGaraManager importExportLottiGaraManager) {
        this.importExportLottiGaraManager = importExportLottiGaraManager;
    }

	public void setSqlManager(SqlManager sqlManager){
		this.sqlManager = sqlManager;
	}

	@Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

		String target = CostantiGeneraliStruts.FORWARD_OK;
		String messageKey = null;
		String risultato = "OK";

		// Lettura codgar in cui importare i lotti
		String codgar = null;
		if(request.getParameter("codgar") != null)
			codgar = request.getParameter("codgar");
		else
			codgar = (String) request.getAttribute("codgar");


		String tmp = null;
		// Lettura del flag per indicare che la gara e' a lotti con plico unico e offerte distinte
		boolean isGaraLottiOffDist = false;
		if(request.getParameter("garaLottiOffDist") != null)
			tmp = request.getParameter("garaLottiOffDist");
		else if(request.getAttribute("garaLottiOffDist") != null)
			tmp = (String) request.getAttribute("garaLottiOffDist");
		if("1".equals(tmp))
			isGaraLottiOffDist = true;

		tmp = null;
		// Lettura del flag per indicare che la codifica automatica e' attiva
		boolean isCodificaAutomatica = false;
		if(request.getParameter("isCodificaAutomatica") != null)
			tmp = request.getParameter("isCodificaAutomatica");
		else if(request.getAttribute("isCodificaAutomatica") != null)
			tmp = (String) request.getAttribute("isCodificaAutomatica");
		if("1".equals(tmp))
			isCodificaAutomatica = true;


		UploadFileForm fileExcel = (UploadFileForm) form;

		if(fileExcel != null && fileExcel.getSelezioneFile().getFileSize() > 0){
			try {
				LoggerImportOffertaPrezzi loggerImport =
					this.importExportLottiGaraManager.importLottiGara(
						(UploadFileForm) form, codgar, isGaraLottiOffDist, request.getSession());

	    	if(loggerImport.getListaMsgVerificaFoglio().size() > 0)
	    		risultato = "KO";
	    	else
	    		risultato = "OK";


	    	request.setAttribute("loggerImport", loggerImport);
	    	request.setAttribute("RISULTATO", risultato);
			} catch (IOException io) {
				messageKey = "errors.importOffertaPrezzi.erroreIO";
				logger.error(this.resBundleGenerale.getString(messageKey), io);
				this.aggiungiMessaggio(request, messageKey);
			} catch (GestioneFileDocumentiAssociatiException e) {
				messageKey = "errors.importOffertaPrezzi.erroreDocAss";
			} catch (GestoreException g) {
				if(g.getCause() == null)
					messageKey = "errors.importOffertaPrezzi.verifichePreliminari";
				else
					messageKey = "errors.importLottiGara.erroreLetturaDatiEstratti";
				this.aggiungiMessaggio(request, messageKey);
				logger.error(this.resBundleGenerale.getString(messageKey), g);
			} catch (Exception e) {
	      messageKey = "errors.importOffertaPrezzi.erroreDocAss";
	      logger.error(this.resBundleGenerale.getString(messageKey), e);
	      this.aggiungiMessaggio(request, messageKey);
	    } catch (Throwable t) {
	      messageKey = "errors.applicazione.inaspettataException";
	      logger.error(this.resBundleGenerale.getString(messageKey), t);
	      this.aggiungiMessaggio(request, messageKey);
			} finally {
	    	// A prescindere dall'esito dell'importazione, si ripristina
				// i valori letti dal request all'inizio della Action
	    	request.setAttribute("codgar", codgar);

	    	request.setAttribute("garaLottiOffDist",
	    			isGaraLottiOffDist ? "1" : "2");
	    	request.setAttribute("isCodificaAutomatica",
	    			isCodificaAutomatica ? "1" : "2");

				if(messageKey != null){
					// Si e' verificato un errore
					request.setAttribute("RISULTATO", "KO");
					target = "errorImport";
				}
			}
		} else {
			// il file in upload e' null oppure e' un file di dimensione pari a 0 byte
			// TODO gestire il caso ...
		}
		if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
		return mapping.findForward(target);
	}

}
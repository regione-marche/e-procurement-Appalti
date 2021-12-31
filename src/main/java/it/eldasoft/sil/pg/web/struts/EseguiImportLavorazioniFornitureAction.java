/*
 * Created on 20/ott/09
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
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportOffertaPrezziManager;
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
 * Action per l'import della lista delle lavorazioni e forniture
 * 
 * @author Luca.Giacomazzo
 */
public class EseguiImportLavorazioniFornitureAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(EseguiImportLavorazioniFornitureAction.class);
	
	private ImportExportOffertaPrezziManager importExportOffertaPrezziManager = null;
	
	private SqlManager sqlManager;
	/**
	 * @param importExportOffertaPrezziManager importExportOffertaPrezziManager
	 * da settare internamente alla classe.
	 */
	public void setImportExportOffertaPrezziManager(
			ImportExportOffertaPrezziManager importExportOffertaPrezziManager) {
		this.importExportOffertaPrezziManager = importExportOffertaPrezziManager;
	}
	
	public void setSqlManager(SqlManager sqlManager){
		this.sqlManager = sqlManager;
	}

	protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if(logger.isDebugEnabled()) logger.debug("runAcion: inizio metodo");
		
		String target = CostantiGeneraliStruts.FORWARD_OK;
		String messageKey = null;
		String risultato = "OK";
		
		// Lettura ngara in cui importare l'offerta prezzi
		String ngara = null;
		if(request.getParameter("ngara") != null)
			ngara = request.getParameter("ngara");
		else
			ngara = (String) request.getAttribute("ngara");

		String codiceDitta = null;
		if(request.getParameter("codiceDitta") != null && request.getParameter("codiceDitta").length() > 0)
			codiceDitta = request.getParameter("codiceDitta");
		else if(request.getAttribute("codiceDitta") != null && ((String) request.getAttribute("codiceDitta")).length() > 0)
			codiceDitta = (String) request.getAttribute("codiceDitta");

		// Lettura del flag che abilita l'insert in C0OGGASS del file Excel che si
		// sta importando
		boolean archiviaXLSDocAss = false;
		String archiviaXLS = null;
		if(request.getParameter("archiviaXLSDocAss") != null)
			archiviaXLS = request.getParameter("archiviaXLSDocAss");
		else if(request.getAttribute("archiviaXLSDocAss") != null)
			archiviaXLS = (String) request.getAttribute("archiviaXLSDocAss");
		if("1".equals(archiviaXLS))
			archiviaXLSDocAss = true;
		
		String tmp = null;
		// Lettura del flag per indicare che la gara e' a lotti con offerta unica
		boolean isGaraLottiConOffertaUnica = false;
		if(request.getParameter("garaLottiConOffertaUnica") != null)
			tmp = request.getParameter("garaLottiConOffertaUnica");
		else if(request.getAttribute("garaLottiConOffertaUnica") != null)
			tmp = (String) request.getAttribute("garaLottiConOffertaUnica");
		if("1".equals(tmp))
			isGaraLottiConOffertaUnica = true;
		
		tmp = null;
		// Lettura del flag per indicare che la codifica automatica e' attiva
		boolean isCodificaAutomatica = false;
		if(request.getParameter("isCodificaAutomatica") != null)
			tmp = request.getParameter("isCodificaAutomatica");
		else if(request.getAttribute("isCodificaAutomatica") != null)
			tmp = (String) request.getAttribute("isCodificaAutomatica");
		if("1".equals(tmp))
			isCodificaAutomatica = true;

		String isPrequalifica = "false";
        if(request.getParameter("isPrequalifica") != null)
          tmp = request.getParameter("isPrequalifica");
        else if(request.getAttribute("isPrequalifica") != null)
          tmp = (String) request.getAttribute("isPrequalifica");
        if("true".equals(tmp))
          isPrequalifica = "true";
		
		UploadFileForm fileExcel = (UploadFileForm) form;
		
		if(fileExcel != null && fileExcel.getSelezioneFile().getFileSize() > 0){
			try {
				LoggerImportOffertaPrezzi loggerImport =
					this.importExportOffertaPrezziManager.importOffertaPrezzi(
						(UploadFileForm) form, ngara, codiceDitta, archiviaXLSDocAss,
						isGaraLottiConOffertaUnica,isPrequalifica, request.getSession());

	    	if(loggerImport.getListaMsgVerificaFoglio().size() > 0)
	    		risultato = "KO";
	    	else
	    		risultato = "OK";

	    	if("OK".equals(risultato) && codiceDitta != null){
	    		String nomeImpresa = (String) this.sqlManager.getObject(
	  					"select NOMIMP from IMPR where CODIMP = ? ", new Object[]{codiceDitta});
	  			
	  			if(nomeImpresa != null && nomeImpresa.length() > 0)
	  				request.setAttribute("nomeImpresa", nomeImpresa);
	    	}
	    	
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
					messageKey = "errors.importOffertaPrezzi.erroreLetturaDatiEstratti";
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
	    	request.setAttribute("ngara", ngara);
	    	if(codiceDitta != null)
	    		request.setAttribute("codiceDitta", codiceDitta);
	    	if(archiviaXLS != null)
	    		request.setAttribute("archiviaXLSDocAss", archiviaXLSDocAss ? "1" : "2");

	    	request.setAttribute("garaLottiConOffertaUnica",
	    			isGaraLottiConOffertaUnica ? "1" : "2");
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
		if(logger.isDebugEnabled()) logger.debug("runAcion: fine metodo");
		return mapping.findForward(target);
	}

}
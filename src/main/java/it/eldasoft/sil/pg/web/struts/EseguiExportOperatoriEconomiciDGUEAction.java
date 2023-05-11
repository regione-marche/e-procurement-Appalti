/*
 * Created on 22/nov/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.sil.pg.web.struts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.docass.GestioneFileDocumentiAssociatiException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ExportOperatoriEconomiciDGUEManager;

/**
 * Action per eseguire l'export Operatori Iscritti su foglio Excel
 *
 * @author Manuel.Bridda
 */
public class EseguiExportOperatoriEconomiciDGUEAction extends
		ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(EseguiExportOperatoriEconomiciDGUEAction.class);

	private ExportOperatoriEconomiciDGUEManager exportOperatoriEconomiciDGUEManager = null;

	/**
	 * @param ExportOperatoriEconomiciDGUEManager exportOperatoriEconomiciDGUEManager
	 * da settare internamente alla classe.
	 */
	public void setExportOperatoriEconomiciDGUEManager(
			ExportOperatoriEconomiciDGUEManager exportOperatoriEconomiciDGUEManager) {
		this.exportOperatoriEconomiciDGUEManager = exportOperatoriEconomiciDGUEManager;
	}

	@Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
		if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

		String messageKey = null;

		// Lettura ngara
		String codgar = null;
		if(request.getParameter("codgar") != null)
		  codgar = request.getParameter("codgar");
		else
		  codgar = (String) request.getAttribute("codgar");
		
		String faseCall = null;
		Long fase = null;
		if(request.getParameter("faseCall") != null)
		  faseCall = request.getParameter("faseCall");
        else
          faseCall = (String) request.getAttribute("faseCall");
		if("Apertura doc. amministrativa".equals(faseCall)) {
		  fase = 1L;
		}else {
		  fase = 4L;
		}
		
		String codimp = null;
		if(request.getParameter("codimp") != null)
		  codimp = request.getParameter("codimp");
		else
		  codimp = (String) request.getAttribute("codimp");
		
		codimp=StringUtils.stripToEmpty(codimp);
		
		boolean prettyPrint = false;
		  prettyPrint = "0".equals(request.getParameter("tipologiaExcel"));

		// Esportazione su Excel degli operatori iscritti
		String nomeFile = null;
		try {
			nomeFile = this.exportOperatoriEconomiciDGUEManager.exportOperatoriEconomiciDGUE(codgar, codimp, fase, prettyPrint, request.getSession());
			// Set nel request il nome del file Excel salvato nella cartella temporanea
			request.setAttribute("nomeFile", nomeFile);
			request.setAttribute("esito", "ok");
			return mapping.findForward(CostantiGeneraliStruts.FORWARD_OK);

		} catch (FileNotFoundException fnf){
			messageKey = "errors.exportOperatoriIscritti.tempFileNonTrovato";
			logger.error(this.resBundleGenerale.getString(messageKey), fnf);
			this.aggiungiMessaggio(request, messageKey);
		} catch(IOException io){
			messageKey = "errors.exportOperatoriIscritti.erroreIO";
			logger.error(this.resBundleGenerale.getString(messageKey), io);
			this.aggiungiMessaggio(request, messageKey);
		} catch (SQLException s){
			messageKey = "errors.exportOperatoriIscritti.sqlError";
			this.aggiungiMessaggio(request, messageKey);
			logger.error(this.resBundleGenerale.getString(messageKey), s);
		} catch(GestoreException g){
			messageKey = "errors.exportOperatoriIscritti.erroreLetturaDatiEstratti";
			this.aggiungiMessaggio(request, messageKey);
			logger.error(this.resBundleGenerale.getString(messageKey), g);
		} catch(GestioneFileDocumentiAssociatiException gfda){
			messageKey = "errors.exportOperatoriIscritti.erroreDocAss";
        } catch (Exception e) {
          messageKey = "errors.exportOffertaPrezzi.erroreDocAss";
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        } catch (Throwable t) {
          messageKey = "errors.applicazione.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), t);
          this.aggiungiMessaggio(request, messageKey);
    	} 
		request.setAttribute("esito", "ko");
		request.setAttribute("Errore", messageKey);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
	}

}
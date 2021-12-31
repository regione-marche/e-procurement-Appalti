/*
 * Created on 18/06/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportOffertaPrezziManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per eseguire l'export offerta prezzi su foglio Excel
 *
 * @author marcello caminiti
 */
public class ExportOffertaPrezziExcelAction extends
		ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(ExportOffertaPrezziExcelAction.class);

	private ImportExportOffertaPrezziManager importExportOffertaPrezziManager = null;

	/**
	 * @param importExportOffertaPrezziManager importExportOffertaPrezziManager
	 * da settare internamente alla classe.
	 */
	public void setImportExportOffertaPrezziManager(
			ImportExportOffertaPrezziManager importExportOffertaPrezziManager) {
		this.importExportOffertaPrezziManager = importExportOffertaPrezziManager;
	}

	@Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
		if(logger.isDebugEnabled()) logger.debug("runAcion: inizio metodo");

		String target = CostantiGeneraliStruts.FORWARD_OK;
		String messageKey = null;

		// Lettura ngara di cui esportare l'offerta prezzi
		String ngara = null;
		if(request.getParameter("ngara") != null)
			ngara = request.getParameter("ngara");
		else
			ngara = (String) request.getAttribute("ngara");

		boolean isGaraLottiConOffertaUnica = false;
		String tmp = null;
		if(request.getParameter("isGaraLottiConOffertaUnica") != null)
		  tmp = request.getParameter("isGaraLottiConOffertaUnica");
        else
          tmp = (String) request.getAttribute("isGaraLottiConOffertaUnica");
		if("true".equals(tmp))
          isGaraLottiConOffertaUnica = true;

		String ribcal=null;
		if(request.getParameter("ribcal") != null)
		  ribcal = request.getParameter("ribcal");
        else
          ribcal = (String) request.getAttribute("ribcal");

		// Esportazione su Excel dell'offerta prezzi
		String nomeFile = null;
		try {
			nomeFile = this.importExportOffertaPrezziManager.exportOffertaPrezziOEPV(
					ngara, isGaraLottiConOffertaUnica, request.getSession(),ribcal);

			// Set nel request il nome del file Excel salvato nella cartella temporanea
			request.setAttribute("nomeFileExcel", nomeFile);
			request.setAttribute("RISULTATO", "OK");

		} catch (FileNotFoundException fnf){
			messageKey = "errors.exportOffertaPrezzi.tempFileNonTrovato";
			logger.error(this.resBundleGenerale.getString(messageKey), fnf);
			this.aggiungiMessaggio(request, messageKey);
		} catch(IOException io){
			messageKey = "errors.exportOffertaPrezzi.erroreIO";
			logger.error(this.resBundleGenerale.getString(messageKey), io);
			this.aggiungiMessaggio(request, messageKey);
		} catch (SQLException s){
			messageKey = "errors.exportOffertaPrezzi.sqlError";
			this.aggiungiMessaggio(request, messageKey);
			logger.error(this.resBundleGenerale.getString(messageKey), s);
		} catch(GestoreException g){
			messageKey = "errors.exportOffertaPrezzi.erroreLetturaDatiEstratti";
			this.aggiungiMessaggio(request, messageKey);
			logger.error(this.resBundleGenerale.getString(messageKey), g);
		} catch (Exception e) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.exportOffertaPrezzi.erroreDocAss";
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        } catch (Throwable t) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.applicazione.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), t);
          this.aggiungiMessaggio(request, messageKey);
		} finally {
			if(messageKey != null){
				// Si e' verificato un errore
				request.setAttribute("RISULTATO", "KO");

				// Ripristinare i parametri con cui e' stata aperta la popup
				request.setAttribute("ngara", ngara);
				target = "errorExport";
			}
		}

		if(logger.isDebugEnabled()) logger.debug("runAcion: fine metodo");
		return mapping.findForward(target);
	}

}
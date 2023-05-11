/*
 * Created on 15/oct/2021
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
import it.eldasoft.gene.web.struts.docass.GestioneFileDocumentiAssociatiException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ExportOperatoriIscrittiManager;

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
 * Action per eseguire l'export Operatori Iscritti su foglio Excel
 *
 * @author Cristian.Febas
 */
public class EseguiExportOperatoriIscrittiAction extends
		ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(EseguiExportOperatoriIscrittiAction.class);

	private ExportOperatoriIscrittiManager exportOperatoriIscrittiManager = null;

	/**
	 * @param ExportOperatoriIscrittiManager exportOperatoriIscrittiManager
	 * da settare internamente alla classe.
	 */
	public void setExportOperatoriIscrittiManager(
			ExportOperatoriIscrittiManager exportOperatoriIscrittiManager) {
		this.exportOperatoriIscrittiManager = exportOperatoriIscrittiManager;
	}

	@Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
		if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

		String target = CostantiGeneraliStruts.FORWARD_OK;
		String messageKey = null;

		// Lettura ngara
		String ngara = null;
		if(request.getParameter("ngara") != null)
			ngara = request.getParameter("ngara");
		else
			ngara = (String) request.getAttribute("ngara");

		// Lettura categoria
		String categoria = null;
		if(request.getParameter("categoria") != null)
			categoria = request.getParameter("categoria");
		else
			categoria = (String) request.getAttribute("categoria");
		

		// Esportazione su Excel degli operatori iscritti
		String nomeFile = null;
		try {
			nomeFile = this.exportOperatoriIscrittiManager.exportOperatoriIscritti(ngara, categoria, request.getSession());
			// Set nel request il nome del file Excel salvato nella cartella temporanea
			request.setAttribute("nomeFileExcel", nomeFile);
			request.setAttribute("RISULTATO", "OK");

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
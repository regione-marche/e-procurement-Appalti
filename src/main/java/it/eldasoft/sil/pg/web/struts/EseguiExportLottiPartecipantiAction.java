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

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.docass.GestioneFileDocumentiAssociatiException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ExportLottiPartecipantiManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Action per eseguire l'export dei lotti e degli operatori su foglio Excel
 *
 * @author Manuel.Bridda
 */
public class EseguiExportLottiPartecipantiAction extends
		ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(EseguiExportLottiPartecipantiAction.class);

	private ExportLottiPartecipantiManager exportLottiPartecipantiManager = null;

	/**
	 * @param exportLottiPartecipantiManager exportLottiPartecipantiManagerManager
	 * da settare internamente alla classe.
	 */
	public void setExportLottiPartecipantiManager(
			ExportLottiPartecipantiManager exportLottiPartecipantiManager) {
		this.exportLottiPartecipantiManager = exportLottiPartecipantiManager;
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

		boolean orderByPartecipante = false;
		orderByPartecipante = "partecipanti".equals(request.getParameter("tipologiaExcel"));

		// Esportazione su Excel dei lotti e dei partecipanti
		String nomeFile = null;
		try {
			nomeFile = this.exportLottiPartecipantiManager.exportLottiPartecipanti(codgar, orderByPartecipante,request.getSession());
			// Set nel request il nome del file Excel salvato nella cartella temporanea
			request.setAttribute("nomeFile", nomeFile);
			request.setAttribute("esito", "ok");

			try {
				String oggEvento= codgar;
				if(oggEvento.startsWith("$")) oggEvento = oggEvento.substring(1);
				String ordinamento= orderByPartecipante? "partecipante" : "lotto";
				String fase = "1".equals(request.getParameter("fase"))?"'Chiusura ricezione plichi'":"'Apertura doc.ammin. e offerte'";
				LogEvento logEvento = LogEventiUtils.createLogEvento(request);
				logEvento.setLivEvento(1);
				logEvento.setOggEvento(oggEvento);
				logEvento.setCodEvento("GA_ESPORTA_PARTECIPAZIONE_EXCEL");
				logEvento.setDescr("Export dettaglio partecipazione ai lotti di gara in excel (fase "+fase+" - ordinamento per "+ordinamento+")");
				logEvento.setErrmsg("");
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception le) {
				logger.error(le);
			}

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
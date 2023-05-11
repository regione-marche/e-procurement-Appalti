package it.eldasoft.sil.pg.web.struts.permessi;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import it.eldasoft.gene.bl.permessi.PermessiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;

public class VisualizzaPermessiUtentiCommissioneAction extends DispatchActionBaseNoOpzioni {

	protected final String FORWARD_VISUALIZZA = "visualizza";

	static Logger          logger             = Logger.getLogger(VisualizzaPermessiUtentiCommissioneAction.class);
	
	/**
	 * Reference alla classe di business logic
	 */
	private PermessiManager permessiManager;

	/**
	 * @param permessiManager
	 *            permessiManager da settare internamente alla classe.
	 */
	public void setPermessiManager(PermessiManager permessiManager) {
		this.permessiManager = permessiManager;
	}
	
	public ActionForward apri(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		if (logger.isDebugEnabled()) {
	    	logger.debug("VisualizzaPermessiUtentiCommissioneAction: inizio metodo");
	    }

	    String target = FORWARD_VISUALIZZA;
	    String messageKey = null;

		String id = request.getParameter("id");
		String operation = "VISUALIZZA";
		String permessimodificabili = request.getParameter("permessimodificabili");
		String codein = request.getParameter("codein");
		String genereGara = request.getParameter("genereGara");
        String codgar = request.getParameter("codgar");
        String ngara = request.getParameter("ngara");
        String gartel = request.getParameter("gartel");
		
		try {
		  request.getSession().setAttribute("id", id);
		  request.getSession().setAttribute("operation", operation);
		  request.getSession().setAttribute("permessimodificabili", permessimodificabili);
		  request.getSession().setAttribute("codein", codein);
		  request.getSession().setAttribute("genereGara", genereGara);
          request.getSession().setAttribute("codgar", codgar);
          request.getSession().setAttribute("ngara", ngara);
          request.getSession().setAttribute("gartel", gartel);
		
	      // Si determina se l'entità di cui si stanno visualizzando i permessi
	      // possiede una condivisione predefinita o meno
	      int utenteDiRiferimento = ((ProfiloUtente) request.getSession().getAttribute(
	          CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
		  
		  if (this.permessiManager.hasAccountCondivisionePredefinita(
				new Integer(utenteDiRiferimento), CostantiPermessi.VALORE_DEFAULT_PREDEFINITO_GARE)) {
		    request.setAttribute("esisteCondivisionePredefinita", "1");
		  }
		  
		} catch (Throwable e) {
		  target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
		  messageKey = "errors.applicazione.inaspettataException";
		  logger.error(this.resBundleGenerale.getString(messageKey), e);
		  this.aggiungiMessaggio(request, messageKey);
		}
		
		if (messageKey != null) response.reset();
		
		if (logger.isDebugEnabled()) {
			logger.debug("VisualizzaPermessiUtentiCommissioneAction: fine metodo");
		}

	    return mapping.findForward(target);
		
	}

	/**
	 * Metodo per impostare la attuale condivisione dell'entita' in analisi
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public ActionForward setPermessiPredefiniti(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("setPermessiPredefiniti: inizio metodo");
		}

		// target di default
		String target = CostantiGeneraliStruts.FORWARD_OK;
		String messageKey = null;

		try {
			String valoreChiave = request.getParameter("codgar");
			if (valoreChiave == null)
				valoreChiave = (String) request.getAttribute("codgar");

			String genereGara = request.getParameter("genereGara");
			if (genereGara == null)
				genereGara = (String) request.getAttribute("genereGara");

			List<?> listaPermessiEntita = this.permessiManager
					.getListaPermessiEntita("codgar", valoreChiave);

			int utenteDiRiferimento = ((ProfiloUtente) request.getSession()
					.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();

			if (listaPermessiEntita.size() > 0) {
				boolean inserito = false;
				int numeroTentativi = 0;

				// tento di inserire il record finchè non genero un ID univoco a
				// causa della concorrenza, o raggiungo il massimo numero di tentativi
				while (!inserito && numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
					try {
						this.permessiManager.insertPermessiPredefiniti("codgar", valoreChiave,
							new Integer(utenteDiRiferimento), CostantiPermessi.VALORE_DEFAULT_PREDEFINITO_GARE,
							listaPermessiEntita);
						inserito = true;
					} catch (DataIntegrityViolationException div) {
						if (numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
							logger.error("Fallito tentativo " + (numeroTentativi + 1)
								+ " di inserimento record per chiave duplicata, si ritenta nuovamente", div);
							numeroTentativi++;
						}
					}
				}
				if (!inserito && numeroTentativi >= CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
					throw new DataIntegrityViolationException(
							"Raggiunto limite massimo di tentativi");
				}
			} else {
				messageKey = "errors.permessi.noPermessiPredefiniti";
				logger.error(this.resBundleGenerale.getString(messageKey));
				this.aggiungiMessaggio(request, messageKey);
			}

			// set nel request del suo valore del campo chiave
			request.setAttribute("codgar", valoreChiave);
			request.setAttribute("genereGara", genereGara);

		} catch (DataIntegrityViolationException e) {
			target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
			messageKey = "errors.database.inserimento.chiaveDuplicata";
			logger.error(this.resBundleGenerale.getString(messageKey), e);
			this.aggiungiMessaggio(request, messageKey);
		} catch (DataAccessException e) {
			target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
			messageKey = "errors.database.dataAccessException";
			logger.error(this.resBundleGenerale.getString(messageKey), e);
			this.aggiungiMessaggio(request, messageKey);

		} catch (Throwable t) {
			target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
			messageKey = "errors.applicazione.inaspettataException";
			logger.error(this.resBundleGenerale.getString(messageKey), t);
			this.aggiungiMessaggio(request, messageKey);
		}

		if (logger.isDebugEnabled())
			logger.debug("visualizzaLista: fine metodo");

		return mapping.findForward(target);
	}

}

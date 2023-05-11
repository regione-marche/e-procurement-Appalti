package it.eldasoft.sil.pg.web.struts.permessi;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ModificaPermessiUtentiStipulaAction extends ActionBaseNoOpzioni {

	protected static final String FORWARD_MODIFICA = "modifica";

	static Logger                 logger             = Logger.getLogger(ModificaPermessiUtentiStipulaAction.class);

	@Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("ModificaPermessiUtentiStipulaAction: inizio metodo");
		}

		String target = FORWARD_MODIFICA;
		String messageKey = null;

		String id = request.getParameter("id");
		String operation = "MODIFICA";
		String permessimodificabili = request.getParameter("permessimodificabili");
		String codein = request.getParameter("codein");
        String codstipula = request.getParameter("codstipula");

		try {
			request.getSession().setAttribute("id", id);
			request.getSession().setAttribute("operation", operation);
			request.getSession().setAttribute("permessimodificabili", permessimodificabili);
			request.getSession().setAttribute("codein", codein);
            request.getSession().setAttribute("codstipula", codstipula);
			request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
					CostantiGenerali.DISABILITA_NAVIGAZIONE);

		} catch (Throwable e) {
			target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
			messageKey = "errors.applicazione.inaspettataException";
			logger.error(this.resBundleGenerale.getString(messageKey), e);
			this.aggiungiMessaggio(request, messageKey);
		}

		if (messageKey != null)
			response.reset();

		if (logger.isDebugEnabled()) {
			logger.debug("ModificaPermessiUtentiStipulaAction: fine metodo");
		}

		return mapping.findForward(target);
	}
}

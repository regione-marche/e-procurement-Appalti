package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.WsdmConfigManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InitSelezioneElPremAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(InitSelezioneElPremAction.class);

	private SqlManager sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		String forward = null;
		String ngara = request.getParameter("ngara");
		String tabStepSelezione = request.getParameter("tabStepSelezione");
		String modoAperturaLista = request.getParameter("modoAperturaLista");
		if (tabStepSelezione == null) {
			tabStepSelezione = "0";
		}
		if (modoAperturaLista == null) {
			modoAperturaLista = "visualizza";
		}

		try {
			request.setAttribute("key", "GARE.NGARA=T:" + ngara);
			String elencoe = (String) sqlManager.getObject("select elencoe from gare where ngara=?", new Object[] {ngara});
			request.setAttribute("elencoe", elencoe);
			request.setAttribute("tabStepSelezione", tabStepSelezione);
			request.setAttribute("modoAperturaLista", modoAperturaLista);

			forward = CostantiGeneraliStruts.FORWARD_OK;
		} catch (Throwable e) {
			String messageKey = "errors.applicazione.inaspettataException";
			logger.error(this.resBundleGenerale.getString(messageKey), e);
			this.aggiungiMessaggio(request, messageKey);
			return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
		}
		return mapping.findForward(forward);
	}
}

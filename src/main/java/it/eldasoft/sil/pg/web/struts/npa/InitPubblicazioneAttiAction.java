package it.eldasoft.sil.pg.web.struts.npa;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.WsdmConfigManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;

public class InitPubblicazioneAttiAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(InitPubblicazioneAttiAction.class);

	private SqlManager sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	private WsdmConfigManager wsdmConfigManager;

	public void setWsdmConfigManager(WsdmConfigManager wsdmConfigManager) {
		this.wsdmConfigManager = wsdmConfigManager;
	}
	
	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		String forward = null;
		String ngara = request.getParameter("ngara");

		try {
			request.setAttribute("key", "GARE.NGARA=T:" + ngara);
			String select="select cenint,codgar1 from gare, torn where ngara = ? and codgar=codgar1";
		    String uffint = (String) sqlManager.getObject(select, new Object[]{ngara});
			request.setAttribute("idconfi", wsdmConfigManager.getWsdmConfigurazione(uffint,"PG"));
			forward = CostantiGeneraliStruts.FORWARD_OK;
		} catch (Exception e) {
			String messageKey = "errors.applicazione.inaspettataException";
			logger.error(this.resBundleGenerale.getString(messageKey), e);
			this.aggiungiMessaggio(request, messageKey);
			return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
		}
		return mapping.findForward(forward);
	}
}

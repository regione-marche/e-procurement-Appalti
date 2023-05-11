/**
 * 
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;

/**
 * Action per l'apertura della poup di import lato ditta della lista lavorazioni
 *  e forniture
 *  
 * @author Luca.Giacomazzo
 */
public class InitImportLavorazioniFornitureDittaAction extends
		ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(InitImportLavorazioniFornitureDittaAction.class);
	
	private SqlManager sqlManager;
	
	public void setSqlManager(SqlManager sqlManager){
		this.sqlManager = sqlManager;
	}
	
	protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if(logger.isDebugEnabled())
			logger.debug("runAction: inizio metodo");

		String target = CostantiGeneraliStruts.FORWARD_OK;
		String messageKey = null;
		String codiceDitta = request.getParameter("codiceDitta");
		
		try {
			String nomeImpresa = (String) this.sqlManager.getObject(
					"select NOMIMP from IMPR where CODIMP = ? ", new Object[]{codiceDitta});
			
			if(nomeImpresa != null && nomeImpresa.length() > 0)
				request.setAttribute("nomeImpresa", nomeImpresa);
			
		} catch (SQLException e) {
			target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
		}
		if(logger.isDebugEnabled())
			logger.debug("runAction: fine metodo");
		
		return mapping.findForward(target);
	}

}
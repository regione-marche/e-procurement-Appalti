package it.eldasoft.sil.pg.web.struts.npa;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.npa.NpaManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * @author Alex.Mancini
 *
 */
public class InserisciJsonNpaAction extends ActionBaseNoOpzioni {

	protected static final String FORWARD_ERRORE = "error";
	protected static final String FORWARD_SUCCESS = "success";
	static Logger logger = Logger.getLogger(InserisciJsonNpaAction.class);
	private NpaManager npaManager;
	private GestioneWSERPManager gestioneWSERPManager;

	/**
	 * @param npaManager The npaManager to set.
	 */
	public void setNpaManager(NpaManager npaManager) {
		this.npaManager = npaManager;
	}
	
	public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
	    this.gestioneWSERPManager = gestioneWSERPManager;
	}

	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form,
	      HttpServletRequest request, HttpServletResponse response)
	      throws IOException, ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("InserisciJsonNPAction: inizio metodo");
		}
		
		response.setHeader("cache-control", "no-cache");
	    response.setContentType("text/text;charset=utf-8");
	    PrintWriter out = response.getWriter();
	    JSONObject json = new JSONObject();
		
        String tipoWSERP = "";
        String urlWSERP = ConfigManager.getValore("wserp.erp.url");
        urlWSERP = UtilityStringhe.convertiNullInStringaVuota(urlWSERP);
        if(!"".equals(urlWSERP)){
          try {
            WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
            if (configurazione.isEsito()) {
              tipoWSERP = configurazione.getRemotewserp();
            }
          } catch (GestoreException e) {
            UtilityStruts.addMessage(request, "error",
                "wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
          }
        }

		String codGar = request.getParameter("codgar");
		String ngara = request.getParameter("ngara");
		String scheda = request.getParameter("scheda");
		Long idscheda = Long.valueOf(request.getParameter("idscheda"));
		if(tipoWSERP == null) {
		  tipoWSERP = "";
		}
		
		try {
		  this.npaManager.registraJSON(idscheda, codGar, scheda, ngara, tipoWSERP);

		} catch (Exception ex) {
		  if (logger.isDebugEnabled()) {
		    if(ex.getMessage() != null) {
		      logger.debug("InserisciJsonNPAction: Error "+ex.getMessage());
              json.put("message", ex.getMessage());
		    } else {
		      logger.debug("InserisciJsonNPAction: Error "+ex.getCause());
		      json.put("message", ex.getCause());
		    }
		  }
		  logger.error("Errore riscontrato in fase di generazione scheda Anac ", ex);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("InserisciJsonNPAction: fine metodo");
		}
		
		out.println(json);
	    out.flush();
		return null;
	}
	

}

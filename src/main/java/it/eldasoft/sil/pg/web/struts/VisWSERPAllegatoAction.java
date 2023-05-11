package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Esegue il download di un file PDF allegato ad una gara o ad un appalto
 */
public class VisWSERPAllegatoAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(VisWSERPAllegatoAction.class);

  private GestioneWSERPManager gestioneWSERPManager;


  /**
   *
   * @param fileAllegatoManager
   */
  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }


  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = null;
    String messageKey = null;

    String nomedoc = request.getParameter("nomedoc");
    String path = request.getParameter("path");

    String servizio = request.getParameter("servizio");
    if(servizio==null || "".equals(servizio))
      servizio ="WSERP";


    try {

        ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        Long syscon = new Long(profilo.getId());
        String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

        String username = credenziali[0];
        String password = credenziali[1];


        this.gestioneWSERPManager.wserpDownloadFileAllegato(username, password, servizio, nomedoc, path, response);

    } catch (Exception io){
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.download";
      this.aggiungiMessaggio(request, messageKey);
	}
    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    if(target != null)
      return mapping.findForward(target);
    else
      return null;
  }

}

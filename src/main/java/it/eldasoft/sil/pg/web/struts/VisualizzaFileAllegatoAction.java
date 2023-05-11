package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;

/**
 * Esegue il download di un file PDF allegato ad una gara o ad un appalto
 */
public class VisualizzaFileAllegatoAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(VisualizzaFileAllegatoAction.class);

  private FileAllegatoManager fileAllegatoManager;


  /**
   *
   * @param fileAllegatoManager
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }


  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = null;
    String messageKey = null;

    try {
      String idprg = new String(request.getParameter("idprg"));
      Long iddocdig = new Long(request.getParameter("iddocdig"));
      String dignomdoc = new String(request.getParameter("dignomdoc"));
      //Con alcuni browser capita che vengano concatenati degli spazi alla fine del nome del file
      dignomdoc = dignomdoc.trim();
      String codiceProfilo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
      Integer idUtente = null;
      if(request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
        int idUtenteInt = ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
        idUtente = new Integer(idUtenteInt);
      }
      String ip = request.getRemoteAddr();
      this.fileAllegatoManager.downloadFileAllegato(dignomdoc, idprg, iddocdig, "PG", codiceProfilo, idUtente, ip,response);

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

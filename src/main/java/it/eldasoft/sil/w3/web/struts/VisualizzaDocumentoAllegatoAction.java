package it.eldasoft.sil.w3.web.struts;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.bl.DocumentoAllegatoManager;

import java.io.IOException;
import java.util.HashMap;

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
public class VisualizzaDocumentoAllegatoAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(VisualizzaDocumentoAllegatoAction.class);

  private DocumentoAllegatoManager documentoAllegatoManager;


  /**
   * 
   * @param documentoAllegatoManager
   */
  public void setDocumentoAllegatoManager(DocumentoAllegatoManager documentoAllegatoManager) {
    this.documentoAllegatoManager = documentoAllegatoManager;
  }


  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = null;
    String messageKey = null;
    HashMap params = new HashMap();

    try {
      Long numgara = new Long(request.getParameter("numgara"));
      Long numdoc = new Long(request.getParameter("numdoc"));
      String nome_documento = new String(request.getParameter("nome_documento"));
      
      params.put("numgara", numgara);
      params.put("numdoc", numdoc);
      
      this.documentoAllegatoManager.downloadDocumentoAllegato(nome_documento, params, response);

    } catch (IOException io){
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.download";
      this.aggiungiMessaggio(request, messageKey);
    } catch (GestoreException e) {
   	 	logger.error("Errore nella visualizzazione/download del documento allegato", e);
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

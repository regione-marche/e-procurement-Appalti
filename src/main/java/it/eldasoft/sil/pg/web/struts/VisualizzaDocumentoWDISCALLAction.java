package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.dao.DocumentoWDISCALLDao;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.utils.utility.UtilityWeb;

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
 * Esegue il download di un file allegato ad un messaggio di una discussione
 */
public class VisualizzaDocumentoWDISCALLAction extends ActionBaseNoOpzioni {

  static Logger                logger = Logger.getLogger(VisualizzaDocumentoWDISCALLAction.class);

  private DocumentoWDISCALLDao documentoWDISCALLDao;

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = null;
    String messageKey = null;

    try {
      Long discid_p = new Long(request.getParameter("discid_p"));
      Long discid = new Long(request.getParameter("discid"));
      Long allnum = new Long(request.getParameter("allnum"));
      String allname = new String(request.getParameter("allname"));

      HashMap params = new HashMap();
      params.put("discid_p", discid_p);
      params.put("discid", discid);
      params.put("allnum", allnum);

      BlobFile allstream = this.documentoWDISCALLDao.getStream(params);
      UtilityWeb.download(allname, allstream.getStream(), response);

    } catch (IOException io) {
      logger.error("Errore nella visualizzazione/download del documento allegato", io);
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.download";
      this.aggiungiMessaggio(request, messageKey);
    }
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    if (target != null)
      return mapping.findForward(target);
    else
      return null;
  }

  
  public DocumentoWDISCALLDao getDocumentoWDISCALLDao() {
    return documentoWDISCALLDao;
  }

  
  public void setDocumentoWDISCALLDao(DocumentoWDISCALLDao documentoWDISCALLDao) {
    this.documentoWDISCALLDao = documentoWDISCALLDao;
  }

}

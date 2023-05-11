package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.docass.DocumentiAssociatiManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

public class DocumentoAssociatoAppaltoDaGareAction extends ActionBaseNoOpzioni {

  private static final String SUCCESS_DOWNLOAD         = null;
  private static final String ERROR_DOWNLOAD           = "errorDownload";

  static Logger                 logger          = Logger.getLogger(DocumentoAssociatoAppaltoDaGareAction.class);

  /**
   * Reference al manager per la gestione delle operazioni di download e upload
   * di documenti associati
   */
  private DocumentiAssociatiManager documentiAssociatiManager;

  /**
   * Reference al manager per la gestione dei file da gestire dall'applicazione
   * web
   */
  private FileManager fileManager;

  /**
   * @param documentiAssociatiManager
   *        documentiAssociatiManager da settare internamente alla classe.
   */
  public void setDocumentiAssociatiManager(
      DocumentiAssociatiManager documentiAssociatiManager) {
    this.documentiAssociatiManager = documentiAssociatiManager;
  }

  /**
   * @param fileManager fileManager da settare internamente alla classe.
   */
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("download: inizio metodo");
    String messageKey = null;
    String target = SUCCESS_DOWNLOAD;

    if (logger.isDebugEnabled())
      logger.debug("AggiungiDitteConcorrenti: fine metodo");

    String idDoc = request.getParameter("id");
    if(idDoc == null)
      idDoc = (String) request.getAttribute("id");
    long id = Long.parseLong(idDoc);

    String tipgen = request.getParameter("tipgen");
    String clavor = request.getParameter("clavor");
    String numera = request.getParameter("numera");

    DocumentoAssociato documento = null;
    try {
      // Estraggo da DB il bean DocumentoAssociato a partire dall'Id, per risalire
      // al nome del file da scaricare
      documento = this.documentiAssociatiManager.getDocumentoAssociatobyId(id);
      String pathDocAss =ConfigManager.getValore("it.eldasoft.sil.pg.pathDocumentiAssociatiPL");

      if (logger.isDebugEnabled())
        logger.debug("Download documento associato: " + pathDocAss +
          documento.getNomeDocAss());

      // Download del documento associato richiesto tramite fileManager
      this.fileManager.download(pathDocAss, documento.getNomeDocAss(), response);

    } catch (FileManagerException fm) {
      String logMessage = this.resBundleGenerale.getString(
          fm.getChiaveResourceBundle());
      for(int i=0; i < fm.getParametri().length; i++)
        logMessage = logMessage.replaceAll("\\{" + i + "\\}", fm.getParametri()[i].toString());
      logger.error(logMessage, fm);

      messageKey = "errors.documentiAssociati.download";
      this.aggiungiMessaggio(request, messageKey,documento.getNomeDocAss());
    } catch (DataAccessException da){
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), da);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      if(messageKey != null){
        // Si e' verificata un'eccezione e quindi bisogna ricaricare la pagina
        // da cui e' stato fatto il download del documento associato.
        target = ERROR_DOWNLOAD;
        request.setAttribute("tipgen" , tipgen);
        request.setAttribute("clavor" , clavor);
        request.setAttribute("numera" , numera);
      }

    }

    if (logger.isDebugEnabled()) logger.debug("download: fine metodo");
    return mapping.findForward(target);

  }

}

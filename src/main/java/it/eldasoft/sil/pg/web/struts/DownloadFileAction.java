/*
 * 	Created on 13/09/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Viene eseguita il download di un file nelle pagine anticor-pg-datigen.jsp
 * e anticor-pg-appalti.jsp
 */
public class DownloadFileAction extends ActionBaseNoOpzioni {

  public static final String FORWARD_ERRORE_DOWNLOAD   = "errorDownload";
  static Logger                    logger            = Logger.getLogger(DownloadFileAction.class);

  private FileManager  fileManager;

  /**
   * @param fileManager The fileManager to set.
   */
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("DownloadFileAction: inizio metodo");

    String messageKey = null;
    String logMessageKey = null;
    String target = null;
    String chiave = request.getParameter("chiave");
    String paginaRicaricata = request.getParameter("paginaRicaricata");
    String percorso=null;
    String property=request.getParameter("property");
    String nomeFile = request.getParameter("nomeFile");
    String paginaAttiva=request.getParameter("paginaAttiva");
    String codfisc = request.getParameter("codfisc");
    try {
      //Se il parametro property='si' allora il percorso lo prendo dalla property it.eldasoft.sil.pg.avcp
      //altrimenti devo caricare il file dalla cartella /xls della webapp
      if(property!=null && "si".equals(property)){
        percorso=ConfigManager.getValore("it.eldasoft.sil.pg.avcp");
        if(codfisc!=null && !"".equals(codfisc))
          percorso+= codfisc;
      }else{
        percorso=request.getSession().getServletContext().getRealPath("/") + "xls/";
      }
      fileManager.download(percorso, nomeFile, response);
    } catch (FileManagerException e) {
      target = FORWARD_ERRORE_DOWNLOAD;
      logMessageKey = e.getFamiglia() + "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(
          logMessageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
          (String) e.getParametri()[0]), e);

      messageKey = "errors.download";
      if(logMessageKey.equals(FileManagerException.CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE)
         ||
         logMessageKey.equals(FileManagerException.CODICE_ERRORE_FILE_INESISTENTE))
        messageKey += ".noAccessoFile";
      this.aggiungiMessaggio(request, messageKey);

      // Setto la chiave per l'apertura
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, chiave);
      // Setto la pagina attiva
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE, paginaAttiva);

      /*
      if("scheda".equals(tipo)){
        // Setto l'apertura in visualizzazione
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
            UtilityTags.SCHEDA_MODO_VISUALIZZA);
      }else{
        // Setto l'apertura in visualizzazione
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
            "apri");
      }
      */

      return UtilityStruts.redirectToPage(paginaRicaricata, false, request);

    } catch (Throwable t) {
      target = FORWARD_ERRORE_DOWNLOAD;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);

      // Setto la chiave per l'apertura e la pagina attiva come prima
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, chiave);
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE, "0");
      // Setto l'apertura in visualizzazione
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
          UtilityTags.SCHEDA_MODO_VISUALIZZA);
      return UtilityStruts.redirectToPage(paginaRicaricata, false, request);
    }
    if (logger.isDebugEnabled()) logger.debug("DownloadFileAction: fine metodo");
    return mapping.findForward(target);

  }

}

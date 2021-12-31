/*
 * Created on 15/nov/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.ScaricaDocumentiManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ScaricaTuttiAllegatiComunicazioneAction  extends DispatchActionBaseNoOpzioni{

  static Logger logger = Logger.getLogger(ScaricaTuttiAllegatiComunicazioneAction.class);

  private FileManager      fileManager;

  private ScaricaDocumentiManager      scaricaDocumentiManager;

  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  public void setScaricaDocumentiManager(ScaricaDocumentiManager scaricaDocumentiManager) {
    this.scaricaDocumentiManager = scaricaDocumentiManager;
  }

  public final ActionForward creaArchivio(final ActionMapping mapping,
                  final ActionForm form, final HttpServletRequest request,
                  final HttpServletResponse response) throws Exception {

    if (logger.isDebugEnabled()) {
        logger.debug("ScaricaTuttiAllegatiComunicazioneAction: inizio metodo");
    }
    this.scaricaDocumentiManager.creaArchivio(mapping, form, request, response);
    return null;
  }

  public ActionForward download(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiAllegatiComunicazioneAction: inizio download");

    String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");
    String nomeArchivio = request.getParameter("path");

    nomeArchivio = nomeArchivio+ ".zip";
    this.fileManager.download(pathArchivioDocumenti, "/" + nomeArchivio, response);
    this.fileManager.delete(pathArchivioDocumenti, "/" + nomeArchivio);

    if (logger.isDebugEnabled()) {
      logger.debug("ScaricaTuttiAllegatiComunicazioneAction: fine metodo");
    }

    return mapping.findForward(null);
  }

  public ActionForward cancellaFileTemporanei(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiAllegatiComunicazioneAction: annulla download");

    String archivioCreato = request.getParameter("archivioCreato");
    if("true".equals(archivioCreato)){
      String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");
      String nomeArchivio = request.getParameter("path");
      nomeArchivio = nomeArchivio+ ".zip";
      this.fileManager.delete(pathArchivioDocumenti, "/" + nomeArchivio);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("ScaricaTuttiAllegatiComunicazioneAction: fine metodo");
    }

    return mapping.findForward(null);
  }

  public ActionForward getPath(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException, ParseException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiAllegatiComunicazioneAction: inizio download");
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String documenti = request.getParameter("documenti");

    String idcom = request.getParameter("idcom");
    String idprg = request.getParameter("idprg");
    String comkey1 = request.getParameter("comkey");
    String ngara = request.getParameter("ngara");
    String direzione = request.getParameter("direzione");

    String codiceEvento, descr;
    if("IN".equals(direzione)){
      codiceEvento = "GA_DOWNLOAD_ALLE_IN_ZIP";
      descr = "Download documenti allegati a comunicazione in ingresso su file zip (id.com.: " + idprg + "/" + idcom + ", id.op.:" + comkey1 + ")";
    }else{
      codiceEvento = "GA_DOWNLOAD_ALLE_OUT_ZIP";
      descr = "Download documenti allegati a comunicazione in uscita su file zip (id.com.: " + idprg + "/" + idcom + ")";
    }

    String errMsg="";
    if(documenti!=null && !"".equals(documenti)){
      JSONParser parser = new JSONParser();
      org.json.simple.JSONObject jsonDocumenti = (org.json.simple.JSONObject) parser.parse(documenti);
      JSONArray jArray = (JSONArray)jsonDocumenti.get("documenti");
      Iterator<?> it = jArray.iterator();
      org.json.simple.JSONObject documento = null;
      String idprgDoc = null;
      String iddocdg = null;
      String nomeDoc = null;
      while (it.hasNext()) {
        documento = (org.json.simple.JSONObject)it.next();
        idprgDoc = (String)documento.get("idprg");
        iddocdg = (String)documento.get("iddocdg");
        nomeDoc = (String)documento.get("nomeDoc");
        errMsg += idprgDoc + "/" + iddocdg + " - " + nomeDoc + "\r\n";
      }
    }

    LogEvento logevento = LogEventiUtils.createLogEvento(request);
    logevento.setLivEvento(1);
    logevento.setOggEvento(ngara);
    logevento.setCodEvento(codiceEvento);
    logevento.setDescr(descr);
    logevento.setErrmsg(errMsg);
    LogEventiUtils.insertLogEventi(logevento);

    ngara = ScaricaDocumentiManager.replaceInvalidChar(ngara);

    JSONObject result = new JSONObject();
    result.put("path", ngara + "_" + idprg + "_" + idcom);
    out.println(result);
    out.flush();

    return mapping.findForward(null);
  }



}

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
import it.eldasoft.gene.bl.SqlManager;
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


public class ScaricaTuttiDocumentiBustaAction  extends DispatchActionBaseNoOpzioni{

  static Logger logger = Logger.getLogger(ScaricaTuttiDocumentiBustaAction.class);

  private FileManager      fileManager;

  private SqlManager      sqlManager;

  private ScaricaDocumentiManager      scaricaDocumentiManager;

  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setScaricaDocumentiManager(ScaricaDocumentiManager scaricaDocumentiManager) {
    this.scaricaDocumentiManager = scaricaDocumentiManager;
  }

  public final ActionForward creaArchivio(final ActionMapping mapping,
                  final ActionForm form, final HttpServletRequest request,
                  final HttpServletResponse response) throws Exception {

    if (logger.isDebugEnabled()) {
        logger.debug("ScaricaTuttiDocumentiBustaAction: inizio metodo");
    }
    this.scaricaDocumentiManager.creaArchivio(mapping, form, request, response);
    return null;
  }

  public ActionForward download(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiDocumentiBustaAction: inizio download");

    String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");
    String nomeArchivio = request.getParameter("path");

    nomeArchivio = nomeArchivio+ ".zip";
    this.fileManager.download(pathArchivioDocumenti, "/" + nomeArchivio, response);
    this.fileManager.delete(pathArchivioDocumenti, "/" + nomeArchivio);

    if (logger.isDebugEnabled()) {
      logger.debug("ScaricaTuttiDocumentiBustaAction: fine metodo");
    }

    return mapping.findForward(null);
  }

  public ActionForward cancellaFileTemporanei(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiDocumentiBustaAction: annulla download");

    String archivioCreato = request.getParameter("archivioCreato");
    if("true".equals(archivioCreato)){
      String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");
      String nomeArchivio = request.getParameter("path");
      nomeArchivio = nomeArchivio+ ".zip";
      this.fileManager.delete(pathArchivioDocumenti, "/" + nomeArchivio);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("ScaricaTuttiDocumentiBustaAction: fine metodo");
    }

    return mapping.findForward(null);
  }

  public ActionForward getPath(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException, ParseException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiDocumentiBustaAction: inizio download");
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String documenti = request.getParameter("documenti");

    String ditta = request.getParameter("ditta");
    String ngara = request.getParameter("ngara");
    String busta = request.getParameter("busta");
    String stepWizard = request.getParameter("stepWizard");
    String bustaDescr = "";

    switch(new Integer(busta)) {
    case 4: bustaDescr = "prequalifica";
      break;
    case 1: bustaDescr = "amministrativa";
      break;
    case 2: bustaDescr = "tecnica";
      break;
    case 3: bustaDescr = "economica";
      break;
    default:
      // code block
  }

    String fase = null;
    if(stepWizard!=null && !"".equals(stepWizard))
      fase = (String)sqlManager.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip = ?", new Object[] {"A1011", new Long(stepWizard)});

    String descr="Download documenti presentati dall'operatore";
    if(!"".equals(busta) && !"0".equals(busta)){
      descr+= " per la busta " + bustaDescr;
    }
    descr+= " su file zip (cod.ditta: " + ditta ;
    if(fase!=null && !"".equals(fase))
      descr+=", fase: " + fase;
    descr+=")";

    String errMsg="";
    if(documenti!=null && !"".equals(documenti)){
      JSONParser parser = new JSONParser();
      org.json.simple.JSONObject jsonDocumenti = (org.json.simple.JSONObject) parser.parse(documenti);
      JSONArray jArray = (JSONArray)jsonDocumenti.get("documenti");
      Iterator<?> it = jArray.iterator();
      org.json.simple.JSONObject documento = null;
      String idprg = null;
      String iddocdg = null;
      String nomeDoc = null;
      while (it.hasNext()) {
        documento = (org.json.simple.JSONObject)it.next();
        idprg = (String)documento.get("idprg");
        iddocdg = (String)documento.get("iddocdg");
        nomeDoc = (String)documento.get("nomeDoc");
        if(nomeDoc != null && !"".equals(nomeDoc)){
          errMsg += idprg + "/" + iddocdg + " - " + nomeDoc + "\r\n";
        }
      }
    }

    LogEvento logevento = LogEventiUtils.createLogEvento(request);
    logevento.setLivEvento(1);
    logevento.setOggEvento(ngara);
    logevento.setCodEvento("GA_DOWNLOAD_DOCIMPRESA_ZIP");
    logevento.setDescr(descr);
    logevento.setErrmsg(errMsg);
    LogEventiUtils.insertLogEventi(logevento);

    ditta = (String)sqlManager.getObject("select nomimp from impr where codimp = ?", new Object[] {ditta});
    if(ditta!=null)
      ditta = ScaricaDocumentiManager.replaceInvalidChar(ditta);

    if(ngara!=null)
      ngara = ScaricaDocumentiManager.replaceInvalidChar(ngara);

    JSONObject result = new JSONObject();
    String name = ngara + "_" + ditta;
    if(!"".equals(busta) && !"0".equals(busta)){
      name = name + "_busta" + bustaDescr;
    }
    result.put("path", name);
    out.println(result);
    out.flush();

    return mapping.findForward(null);
  }



}

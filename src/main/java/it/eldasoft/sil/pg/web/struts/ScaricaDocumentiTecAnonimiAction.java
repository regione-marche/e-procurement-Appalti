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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.ScaricaDocumentiManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import net.sf.json.JSONObject;


public class ScaricaDocumentiTecAnonimiAction  extends DispatchActionAjaxLogged{

  static Logger logger = Logger.getLogger(ScaricaDocumentiTecAnonimiAction.class);

  private FileManager      fileManager;

  private GeneManager      geneManager;

  private ScaricaDocumentiManager      scaricaDocumentiManager;

  //Select per estrarre i documenti della busta tecnica delle ditte per cui è valorizzato IDANONIMO e per cui le buste tecniche sono state processate e poste in stato 13
  private static final String selectDocumenti = "select v.idprg, v.iddocdg,v.dignomdoc, v.codimp, d.idanonimo from v_gare_docditta v, ditg d  where v.ngara=? and v.busta=2 and v.ngara=d.ngara5 and v.codimp=d.dittao "
      + "  and d.idanonimo is not null and v.iddocdg is not null "
      + "  and d.dittao in (select d1.dittao from ditg d1 "
      + "  left outer join ragimp on (codime9=d1.dittao and impman='1') "
      + "  inner join w_puser on (userkey1=d1.dittao or (userkey1=coddic and impman='1')) "
      + "  inner join w_invcom on (comkey1=usernome and comkey2=d1.ngara5 and (comkey3 = d1.ncomope or comkey3 is null) and comtipo='FS11B' and comstato='13') "
      + "  where d1.ngara5=?)  order by v.codimp";

  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  public void setScaricaDocumentiManager(ScaricaDocumentiManager scaricaDocumentiManager) {
    this.scaricaDocumentiManager = scaricaDocumentiManager;
  }

  public void setGeneManager(GeneManager geneManager) {
	    this.geneManager = geneManager;
  }


  public final ActionForward creaArchivio(final ActionMapping mapping,
                  final ActionForm form, final HttpServletRequest request,
                  final HttpServletResponse response) throws Exception {

    if (logger.isDebugEnabled()) {
        logger.debug("ScaricaTuttiAllegatiVerificheAction: inizio metodo");
    }
    this.scaricaDocumentiManager.creaArchivioBusteCifrate(mapping, form, request, response);
    return null;
  }

  public ActionForward download(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaDocumentiTecAnonimiAction: inizio download");

    String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");
    String nomeArchivio = request.getParameter("path");

    nomeArchivio = nomeArchivio+ ".zip";
    this.fileManager.download(pathArchivioDocumenti, "/" + nomeArchivio, response);
    this.fileManager.delete(pathArchivioDocumenti, "/" + nomeArchivio);

    if (logger.isDebugEnabled()) {
      logger.debug("ScaricaDocumentiTecAnonimiAction: fine metodo");
    }

    return mapping.findForward(null);
  }

  public ActionForward getPath(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException, ParseException, CriptazioneException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaDocumentiTecAnonimiAction: inizio download");
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String ngara = request.getParameter("ngara");
    String documenti = request.getParameter("documenti");

    String codiceEvento, descr;
    codiceEvento = "GA_OEPV_DOWNLOAD_ANONIMO_ZIP";
    descr = "Download su file zip delle buste tecniche in forma anonima";
    String nuovaDitta = null;
    String idanonimo = null;
    String idInchiaro=null;

    if(documenti!=null && !"".equals(documenti)){
      JSONParser parser = new JSONParser();
      org.json.simple.JSONObject jsonDocumenti = (org.json.simple.JSONObject) parser.parse(documenti);
      JSONArray jArray = (JSONArray)jsonDocumenti.get("documenti");
      Iterator<?> it = jArray.iterator();
      org.json.simple.JSONObject documento = null;
      while (it.hasNext()) {
        documento = (org.json.simple.JSONObject)it.next();
        nuovaDitta = (String)documento.get("nuovaDitta");
        if("1".equals(nuovaDitta)) {
          idanonimo = (String)documento.get("idanonimo");
          idInchiaro = ScaricaDocumentiManager.decifraIdAnonimo(idanonimo);
        }
      }
    }

    LogEvento logevento = LogEventiUtils.createLogEvento(request);
    logevento.setLivEvento(1);
    logevento.setOggEvento(ngara);
    logevento.setCodEvento(codiceEvento);
    logevento.setDescr(descr);
    LogEventiUtils.insertLogEventi(logevento);


    JSONObject result = new JSONObject();
    result.put("path", ngara + "_buste_anonime");
    out.println(result);
    out.flush();

    return mapping.findForward(null);
  }

  public ActionForward getListaDocumenti(ActionMapping mapping, ActionForm form,
	      HttpServletRequest request, HttpServletResponse response)
	          throws IOException, ServletException, FileManagerException, SQLException, ParseException {
	    if(logger.isDebugEnabled()) logger.debug("ScaricaDocumentiTecAnonimiAction: inizio getListaDocumenti");
	    response.setHeader("cache-control", "no-cache");
	    response.setContentType("text/text;charset=utf-8");
	    PrintWriter out = response.getWriter();

	    String ngara = request.getParameter("ngara");
	    List<?> listaDoc;

	    //select che estrae tutti i documenti della busta tecnica
	    try {
	        listaDoc = this.geneManager.getSql().getListVector(selectDocumenti, new String[] {ngara,ngara});
	      } catch (SQLException e) {
	            // non si dovrebbe verificare mai...
	            logger.error(
	                this.resBundleGenerale.getString("errors.database.dataAccessException"),
	                e);
	            throw new RuntimeException(e.getMessage());
	    }

	    JSONObject result = new JSONObject();
	    result.put("listaDoc", listaDoc);
	    result.put("maxCount", listaDoc.size());
	    out.println(result);
	    out.flush();

	    return mapping.findForward(null);
	  }

}

package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportDitteManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ExportDitteInGaraAction extends DispatchActionBaseNoOpzioni {

  private static final String SUCCESS_DOWNLOAD         = "success";

  static Logger                 logger          = Logger.getLogger(ExportDitteInGaraAction.class);
 
  /**
   * Reference al manager per la gestione delle operazioni di download e upload
   * di documenti associati
   */
  private SqlManager sqlManager;

  /**
   * Reference al manager per la gestione dei file da gestire dall'applicazione
   * web
   */
  private PgManager pgManager;
  
  /**
   * Reference al manager per la gestione dei file da gestire dall'applicazione
   * web
   */
  private FileManager fileManager;
  
  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  private TabellatiManager tabellatiManager;

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  private ImportExportDitteManager importExportDitteManager;
  
  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(
      SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setPgManager(
      PgManager pgManager) {
    this.pgManager = pgManager;
  }
  
  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(
      TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }
  
  /**
   * @param fileManager fileManager da settare internamente alla classe.
   */
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }
  
  /**
   * @param ImportExportDitteManager ImportExportDitteManager da settare internamente alla classe.
   */
  public void setImportExportDitteManager(ImportExportDitteManager importExportDitteManager) {
    this.importExportDitteManager = importExportDitteManager;
  }
  
  @SuppressWarnings("unchecked")
  public final ActionForward download(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException, GestoreException {

    if (logger.isDebugEnabled()) logger.debug("download: inizio metodo");

    String target = SUCCESS_DOWNLOAD;
    
    JSONObject obj = new JSONObject();
    

    int livEvento = 1;
    String oggEvento = "";
    String codEvento = "GA_ESPORTA_DITTE_JSON";
    String descrEvento = "Esportazione ditte in formato M-Appalti (estensione JSON)";
    String errMsgEvento = "";
    
    try {
      String codgar = request.getParameter("codgar");

      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      
      
      obj.put(ImportExportDitteManager.CODAPP, "PG");
      obj.put(ImportExportDitteManager.UTENTECOD, profiloUtente.getId());
      obj.put(ImportExportDitteManager.UTENTENOME, profiloUtente.getNome());
      //get data attuale
      Date today = new Date();
      String data = UtilityDate.convertiData(today, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
      obj.put(ImportExportDitteManager.DATETIMEEXPORT, data);
      //get numero versione dell'applicativo
      String eldaver_sql = "select numver from eldaver where codapp='PG'";
      Object eldaver = this.sqlManager.getObject(eldaver_sql, new Object[] {});
      if (eldaver != null) {
        obj.put(ImportExportDitteManager.NUMVER, eldaver);
      }
      //get dati della gara
      JSONObject datiGara = importExportDitteManager.getDatiGara(codgar);     
      obj.put(ImportExportDitteManager.DATIGARA, datiGara);
      String ngara = codgar.substring(1, codgar.length());
      oggEvento = ngara;
      //this.fileManager.download(ngara + "_DitteGara.json", obj.toString().getBytes(), response);
      
      File tempFile = TempFileUtilities.getTempFileSenzaNumeoRandom(ngara + "_DitteGara.json",
          request.getSession());
      FileOutputStream os = new FileOutputStream(tempFile);
      String tempString = obj.toString();
      os.write(tempString.getBytes());
      
      request.setAttribute("nomeFile", ngara + "_DitteGara.json");
      request.setAttribute("esito", "ok");
      
      errMsgEvento = obj.toString();
      
    } catch (SQLException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      livEvento = 3;
      errMsgEvento = e.getMessage();
    } catch (GestoreException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      livEvento = 3;
      errMsgEvento = e.getMessage();
    }finally{
      
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(oggEvento);
      logEvento.setCodEvento(codEvento);
      logEvento.setDescr(descrEvento);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
      
    }

    if (logger.isDebugEnabled()) logger.debug("download: fine metodo");
    return mapping.findForward(target);

  }
  
  

}

package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.properties.ConfigManager;

public class DownloadExportDocumentiAction extends ActionBaseNoOpzioni {

  static Logger                 logger          = Logger.getLogger(DownloadExportDocumentiAction.class);

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  /**
   * Reference al manager per la gestione dei file da gestire dall'applicazione
   * web
   */
  private FileManager fileManager;

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
    String target = null;
    if (logger.isDebugEnabled())
      logger.debug("AggiungiDitteConcorrenti: fine metodo");

    String idRichiesta = request.getParameter("id");
    if(idRichiesta != null) {
    	long id = Long.parseLong(idRichiesta);
    	try {
    	  Vector<?> dati_Gardoc = this.sqlManager.getVector("select codgara,entita from gardoc_jobs where id_archiviazione = ?", new Object[] { id });
    	  String codgar = null;
    	  String entita= null;
    	  if(dati_Gardoc!=null && dati_Gardoc.size()>0) {
    	    codgar = SqlManager.getValueFromVectorParam(dati_Gardoc, 0).getStringValue();
    	    entita = SqlManager.getValueFromVectorParam(dati_Gardoc, 1).getStringValue();

    	    if("G1STIPULA".equals(entita))
              codgar = (String)this.sqlManager.getObject("select codstipula from g1stipula where id = ?", new Object[] {new Long(codgar)});
    	  }


    		String codgarApp = codgar;
      	  	if (codgar.startsWith("$")) {
      	  		codgarApp = codgar.substring(1);
      	  	}
      	  	codgarApp = codgarApp.toUpperCase().replaceAll("/", "_");
    		String pathArchivioDocumenti = ConfigManager.getValore("it.eldasoft.sil.pg.pathArchivioDocumentiGara");
   	      	if (logger.isDebugEnabled())
    	        logger.debug("Download export documenti gara: " + pathArchivioDocumenti +
    	          "/" + codgarApp + "_Documenti.zip");

    	      // Download del documento associato richiesto tramite fileManager
    	      this.fileManager.download(pathArchivioDocumenti, "/" + codgarApp + "_Documenti.zip", response);

    	    } catch (FileManagerException fm) {
    	      logger.error(fm.getMessage(), fm);
    	      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    	      messageKey = "errors.download";
    	      this.aggiungiMessaggio(request, messageKey);
    	    } catch (DataAccessException da){
    	      messageKey = "errors.database.dataAccessException";
    	      logger.error(this.resBundleGenerale.getString(messageKey), da);
    	      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    	      this.aggiungiMessaggio(request, messageKey);
    	    } catch (Throwable t) {
    	      messageKey = "errors.applicazione.inaspettataException";
    	      logger.error(this.resBundleGenerale.getString(messageKey), t);
    	      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    	      this.aggiungiMessaggio(request, messageKey);
    	    }
    }

    if (logger.isDebugEnabled()) logger.debug("download: fine metodo");
    if (target != null) {
        return mapping.findForward(target);
    } else {
        return null;
    }

  }

}

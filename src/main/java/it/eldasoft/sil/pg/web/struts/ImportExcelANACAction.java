/*
 * Created on 03/12/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.web.struts.docass.CostantiDocumentiAssociati;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class ImportExcelANACAction extends Action {

  static Logger               logger = Logger.getLogger(ImportExcelANACAction.class);
    
  private SqlManager          sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  /**
   * Reference al manager per la gestione dei file sul server
   */
  private FileManager fileManager;
  
  /**
   * @param fileManager
   *        fileManager da settare internamente alla classe.
   */
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }
  
  /**
   * Reference al manager per la generazione di una nuova chiave
   */
  private GenChiaviManager genChiaviManager;

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  
  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    logger.info("Import dell'Excel ANAC: inizio");

    DataSourceTransactionManagerBase.setRequest(request);
    
    ImportExcelANACForm importExcelANACForm = (ImportExcelANACForm) form;
    
    String tipoElenco = importExcelANACForm.getTipoElenco();
    
    String syscon = importExcelANACForm.getSyscon();

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String sqlInsert = null;    
    TransactionStatus status = null;    
    boolean eseguireCommit= true;       
    String errMsgEvento = "";    
    
    try {
    	status = this.sqlManager.startTransaction();
    	
    	Long c0acod	= null;
    	String	c0aprg	= "PG";
    	String	c0aent	= "UFFINT";
    	String	c0akey1	= "X000X";
		String	c0akey2 = "#", c0akey3 = "#", c0akey4 = "#", c0akey5 = "#";
		Timestamp	c0adat	= new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    	String	c0atit	= "";
    	String	c0adirogg = "[default]";
    	String	c0anomogg = "";
    	String  c0pdamd5 = syscon;
        
        long newId = this.genChiaviManager.getMaxId("C0OGGASS", "C0ACOD") + 1;
        
        c0acod=new Long(newId);
    	
    	if("1".equals(tipoElenco)) {
    		c0atit = "Elenco CIG SIMOG emessi e perfezionati";
    		c0anomogg = "ANAC-CIG.xlsx";
    	}else {
    		c0atit = "Elenco SmartCIG";
    		c0anomogg = "ANAC-SmartCIG.xlsx";
    	}

        //Inizio gestione file       

        String pathServer = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);
        
        File fileDst = new File(pathServer + c0anomogg);
        
        String tmpNomeDocAss = c0anomogg.concat(".tmp");
        
        if (fileDst.exists()) {
        	//Sovrascrivo
        	// Fase 1: Rinomina del file originale
            fileManager.rename(pathServer, c0anomogg,
                tmpNomeDocAss);
            // Fase 1 eseguita

            // Fase 2: Upload del nuovo file da inserire
        	FileOutputStream output = new FileOutputStream(fileDst);
        	output.write(importExcelANACForm.getSelezioneFile().getFileData());
        	output.close();
            // Fase 2 eseguita

            // Fase 3: Delete del file temporaneo 
            String tmp = pathServer;
            if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(tmp))
              tmp = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);

            fileManager.delete(pathServer, tmpNomeDocAss);
            // Fase 3 eseguita
        }
        else {
        	//Inserisco
        	FileOutputStream output = new FileOutputStream(fileDst);
        	output.write(importExcelANACForm.getSelezioneFile().getFileData());
        	output.close();
        }
        
        //Fine gestione file
    	
        sqlInsert="insert into C0OGGASS(C0ACOD,C0APRG,C0AENT,C0AKEY1,C0AKEY2,C0AKEY3,C0AKEY4,C0AKEY5,C0ADAT,C0ATIT,C0ADIROGG,C0ANOMOGG,C0PDAMD5) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        this.sqlManager.update(sqlInsert, new Object[]{c0acod,c0aprg,c0aent,c0akey1,c0akey2,c0akey3,c0akey4,c0akey5,c0adat,c0atit,c0adirogg,c0anomogg,c0pdamd5});
    	
    }
    catch(Exception e) {
    	errMsgEvento = "Errore nell'inserimento dati in tabella";
        result.put("Esito", "Errore");
        result.put("MsgErrore", errMsgEvento);
        eseguireCommit= false;
    }
    finally{
    if (status != null) {
        try {
           if (eseguireCommit) {
              this.sqlManager.commitTransaction(status);
              } else {
              this.sqlManager.rollbackTransaction(status);
           }
    }
    catch (SQLException ex) {
         throw new GestoreException("Errore inaspettato accorso durante la procedura di import dell'Excel ANAC", null, ex);
    }    
    }
    out.print(result);
    out.flush();

    }

    logger.info("Import dell'Excel ANAC: fine");

    return null;
  }
}

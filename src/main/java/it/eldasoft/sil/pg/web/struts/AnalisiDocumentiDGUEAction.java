/*
 * Created on 24/11/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GenChiaviManager;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import net.sf.json.JSONObject;
import it.eldasoft.gene.bl.GenChiaviManager;

public class AnalisiDocumentiDGUEAction extends Action {

  static Logger               logger = Logger.getLogger(AnalisiDocumentiDGUEAction.class);

  private SqlManager          sqlManager;
  
  /** Reference al manager per la gestione della tabella W_GENCHIAVI */
  private GenChiaviManager genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  /**
  * @param genChiaviManager the genChiaviManager to set
  */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
	  this.genChiaviManager = genChiaviManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    logger.info("Analisi documento DGUE: inizio");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String ngara = request.getParameter("ngara");
    
    String codimp = request.getParameter("codimp");
    
    String faseCall = request.getParameter("faseCall");

    String codgar1 = (String) this.sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] { ngara });

    String selectListaDocumentiNoCodimp = "select v.ngara,v.codimp,v.dignomdoc,v.idprg,v.iddocdg,v.busta from V_GARE_DOCDITTA v,DITG d "
    		+ "where v.codgar=d.codgar5 and v.codimp=d.dittao and v.ngara=d.ngara5 and codgar=? and upper(dignomdoc) like '%.XML%' and"
    		+ " v.iddocdg is not null and (situazdoci=2 or situazdoci=3 or situazdoci is null)";

    String selectListaDocumentiCodimp = "select v.ngara,v.codimp,v.dignomdoc,v.idprg,v.iddocdg,v.busta from V_GARE_DOCDITTA v where codgar=? and"
    		+ " upper(dignomdoc) like '%.XML%' and v.codimp=? and v.iddocdg is not null and (situazdoci=2 or "
    		+ "situazdoci=3 or situazdoci is null)";

    String selectListaDocumenti = null;
    String sqlInsert = null;    
    String sqlUpdate = null;    
    List<?> listaDocumenti=null;    
    TransactionStatus status = null;    
    boolean eseguireCommit= true;    
    boolean presenzaDoc=false;    
    String errMsgEvento = "";    
    String codimp_prev="";    
    int id_dgue_batch = 0;
    
    //Analisi DGUE
    try {
    	
    	if (codimp!= "" && codimp!= null) {
    		selectListaDocumenti = selectListaDocumentiCodimp;
    		if("Apertura doc. amministrativa".equals(faseCall)) {
    			selectListaDocumenti = selectListaDocumenti + " and v.busta='1' order by v.codimp";
    		}
    		else {
    			selectListaDocumenti = selectListaDocumenti + " and v.busta='4' order by v.codimp";
    		}
    		listaDocumenti = this.sqlManager.getListVector(selectListaDocumenti, new Object[] {codgar1,codimp });
    	}
    	else {
    		selectListaDocumenti = selectListaDocumentiNoCodimp;
    		if("Apertura doc. amministrativa".equals(faseCall)) {
    			selectListaDocumenti = selectListaDocumenti + " and d.statodgueamm is null and v.busta='1' order by v.codimp";
    		}
    		else {
    			selectListaDocumenti = selectListaDocumenti + " and d.statodguepreq is null and v.busta='4' order by v.codimp";
    		}
    		listaDocumenti = this.sqlManager.getListVector(selectListaDocumenti, new Object[] {codgar1 });
    	}

    	status = this.sqlManager.startTransaction();
    	
    	
        for (int i = 0; i < listaDocumenti.size(); i++) { 
        	
        	  String stato="DA_ELABORARE";
        	  
        	  presenzaDoc = true;        	
              
              String w_ngara = (String) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).getValue();
              String w_codimp = (String) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 1).getValue();
              String dignomdoc = (String) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 2).getValue();
              String idprg = (String) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 3).getValue();
              Long iddocdg = (Long) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 4).getValue();
              Long busta = (Long) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 5).getValue();
                                    
              //DGUE_BATCH
              if(!w_codimp.equals(codimp_prev)) {
            	  id_dgue_batch = genChiaviManager.getNextId("DGUE_BATCH");
              
            	  sqlInsert="insert into DGUE_BATCH(ID,CODGAR,NGARA,CODIMP,BUSTA,DATAINSERIMENTO,STATO) values(?,?,?,?,?,?,?)";
            	  this.sqlManager.update(sqlInsert, new Object[]{id_dgue_batch,codgar1,w_ngara,w_codimp,busta,new java.sql.Timestamp(System.currentTimeMillis()),stato});
            	  
            	  codimp_prev=w_codimp;
              }
              //DGUE_BATCH_DOC
              
              int id_dgue_batch_doc = genChiaviManager.getNextId("DGUE_BATCH_DOC");
              
              sqlInsert="insert into DGUE_BATCH_DOC(ID,IDBATCH,DIGNOMDOC,IDPRG,IDDOCDIG,STATO) values(?,?,?,?,?,?)";
              this.sqlManager.update(sqlInsert, new Object[]{id_dgue_batch_doc,id_dgue_batch,dignomdoc,idprg,iddocdg,stato});
              
              //DITG
              if("Apertura doc. amministrativa".equals(faseCall)) {
            	  sqlUpdate="update DITG set statodgueamm=1 where codgar5=? and dittao=? and ngara5=?";
                  this.sqlManager.update(sqlUpdate, new Object[]{codgar1,w_codimp,ngara});
              }
              else {
            	  sqlUpdate="update DITG set statodguepreq=1 where codgar5=? and dittao=? and ngara5=?";
                  this.sqlManager.update(sqlUpdate, new Object[]{codgar1,w_codimp,ngara});
              }
              
        }
        if (!presenzaDoc) {
        	errMsgEvento = "Non è stato trovato alcun documento da analizzare";
        	result.put("Esito", "Errore");
        	result.put("MsgErrore", errMsgEvento);
        	eseguireCommit= false;
        }
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
         throw new GestoreException("Errore inaspettato accorso durante la procedura di analisi documenti DGUE", null, ex);
    }    
    }
    out.print(result);
    out.flush();

    }

    logger.info("Analisi documento DGUE: fine");

    return null;
  }
}

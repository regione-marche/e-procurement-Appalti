/*
 * Created on 27/09/2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class SetIsriconclusaAction extends ActionAjaxLogged {

  static Logger                 logger          = Logger.getLogger(SetIsriconclusaAction.class);

  private SqlManager           sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws IOException {

    logger.info("SetIsriconclusa: inizio");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String ngara = request.getParameter("ngara");
    String isriconclusa = request.getParameter("isriconclusa");
    
    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 3;
    String errMsgEvento = genericMsgErr;
    TransactionStatus status = null;
    String codEvento="GA_CONCLUDI_GRADO2";
    String desEvento="Conclusione secondo grado di concorso di progettazione";
    try{
      status = this.sqlManager.startTransaction();
      
      if("0".equals(isriconclusa)) 
        sqlManager.update("update gare set isriconclusa='1',datconclusa=? where ngara=?", new Object[] {new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),ngara});
      else {
        sqlManager.update("update gare set isriconclusa=null,datconclusa=null where ngara=?", new Object[] {ngara});
        codEvento="GA_ANNULLA_CONCLUDI_GRADO2";
        desEvento="Annullamento conclusione secondo grado di concorso di progettazione";
      }
      this.sqlManager.commitTransaction(status);
      livEvento = 1;
      errMsgEvento = "";
      
    } catch (SQLException ge) {
      livEvento = 3;
      errMsgEvento = ge.getMessage();
      try {
        this.sqlManager.rollbackTransaction(status);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      result.put("Esito", "Errore");
      result.put("MsgErrore", errMsgEvento);
      logger.error(errMsgEvento);
    }finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(desEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr);
      }
    }

    out.print(result);
    out.flush();

    logger.info("SetIsriconclusa: fine");

    return null;

  }
}

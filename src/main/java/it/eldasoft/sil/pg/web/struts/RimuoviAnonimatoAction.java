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
import java.util.List;

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
import net.sf.json.JSONObject;

public class RimuoviAnonimatoAction extends ActionAjaxLogged {

  static Logger                 logger          = Logger.getLogger(RimuoviAnonimatoAction.class);

  private SqlManager           sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws IOException {

    logger.info("RimuoviAnonimato: inizio");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    TransactionStatus status = null;
    String codgar = (String) request.getParameter("codgar");
    try{
      status = this.sqlManager.startTransaction();

      List data = sqlManager.getListVector("select ditg.dittao, impr.nomimp from ditg, impr where ditg.dittao=impr.codimp and codgar5=?", new Object[] {codgar});
      
      if(data!=null) {
        for(int i=0; i<data.size();i++) {
          String dittao=SqlManager.getValueFromVectorParam(data.get(i), 0).toString();
          String nomimo=SqlManager.getValueFromVectorParam(data.get(i), 1).toString();
          sqlManager.update("update ditg set nomimo=? where dittao=? and codgar5=?", new Object[] {nomimo,dittao,codgar});
        }
        this.sqlManager.commitTransaction(status);
        result.put("Esito","OK");
      }
    } catch (SQLException ge) {
      
      try {
        this.sqlManager.rollbackTransaction(status);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      result.put("Esito", "Errore durante la rimozione dell'anonimato");
      logger.error(ge);
    }

    out.print(result);
    out.flush();

    logger.info("RimuoviAnonimato: fine");

    return null;

  }
}

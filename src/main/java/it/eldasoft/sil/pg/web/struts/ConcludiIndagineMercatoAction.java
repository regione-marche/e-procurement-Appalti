/*
 * Created on 10/02/2023
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class ConcludiIndagineMercatoAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(ConcludiIndagineMercatoAction.class);

  private SqlManager          sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("ConcludiIndagineMercatoAction: inizio metodo");

    String ngara = request.getParameter("ngara");
    String operazione = request.getParameter("operazione");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    JSONObject result = new JSONObject();
    boolean commitTransaction = false;
    String msgErr = null;

    if(StringUtils.isNotEmpty(ngara)) {
      TransactionStatus status = null;
      int livEvento=3;
      String errMsgEvento="";

      try {
        status = this.sqlManager.startTransaction();

        if("1".equals(operazione)) {
          this.sqlManager.update("update gare set isriconclusa=?, datconclusa=? where ngara=?", new Object[] {"1", new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), ngara});
        }else {
          this.sqlManager.update("update gare set isriconclusa=null,datconclusa=null where ngara=?", new Object[] {ngara});
        }
        livEvento = 1;
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
        logger.error("Errore nell'aggiornamento dell'importo della gara", e);
        msgErr=e.getMessage();
        errMsgEvento = msgErr;
      } finally {
        try {
          if (status != null) {
            if (commitTransaction) {
              this.sqlManager.commitTransaction(status);
            } else {
              this.sqlManager.rollbackTransaction(status);
            }
          }
        }catch(SQLException e) {
          logger.error("Errore nell'aggiornamento dell'importo della gara", e);
        }

        //Tracciatura eventi
        try {
          String descr="Conclusione indagine di mercato";
          String codEvento="GA_CONCLUDI_INDAGINE";
          if("2".equals(operazione)) {
            descr="Annullamento conclusione indagine di mercato";
            codEvento="GA_ANNULLA_CONCLUDI_INDAGINE";
          }
          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento(codEvento);
          logEvento.setDescr(descr);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi");
        }
      }
    }

    result.put("esito", new Boolean(commitTransaction));
    result.put("messaggio", msgErr);
    out.println(result);
    out.flush();

    if (logger.isDebugEnabled()) logger.debug("ConcludiIndagineMercatoAction: fine metodo");
    return null;
  }
}

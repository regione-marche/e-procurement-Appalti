package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class AggiornamentoPresaVisioneDocDittaAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");

    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();
      String codgar = request.getParameter("codgar");
      String ngara = request.getParameter("ngara");
      String codimp = request.getParameter("codimp");
      String norddoci = request.getParameter("norddoci");
      String proveni = request.getParameter("proveni");
      String syscon = request.getParameter("syscon");
      Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());

      this.sqlManager.update("update imprdocg set datalettura=?, sysconlet=? where codgar=? and ngara=? and"
          + " codimp=? and norddoci=? and proveni=? and datalettura is null and sysconlet is null", new Object[]{new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()) ,new Long(syscon),
              codgar, ngara,codimp,new Long(norddoci), new Long(proveni)});

      commitTransaction = true;
    } catch (Exception e) {
      commitTransaction = false;
      throw e;
    }finally{
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }
    }

    PrintWriter out = response.getWriter();
    JSONObject result = new JSONObject();
    out.println(result);

    out.flush();
    return null;

  }
}

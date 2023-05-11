package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class EliminaArticoloDaCarrelloAction extends Action {

  private SqlManager       sqlManager;


  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);
    
    Long meric_id = null;
    Long meartcat_id = null;

    if (request.getParameter("meric_id") != null && !"".equals(request.getParameter("meric_id"))) {
      meric_id = new Long(request.getParameter("meric_id"));
    }

    if (request.getParameter("meartcat_id") != null && !"".equals(request.getParameter("meartcat_id"))) {
      meartcat_id = new Long(request.getParameter("meartcat_id"));
    }
    if (meric_id != null && meartcat_id != null) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      try {
        status = this.sqlManager.startTransaction();
        this.sqlManager.update("delete from mericart where idric = ? and idartcat = ?", new Object[] { meric_id, meartcat_id });
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
      }
    }
    return null;
  }
}

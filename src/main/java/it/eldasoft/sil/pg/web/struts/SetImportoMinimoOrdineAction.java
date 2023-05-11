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

public class SetImportoMinimoOrdineAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    
    DataSourceTransactionManagerBase.setRequest(request);
    
    String ngara = request.getParameter("ngara");
    String caisim = request.getParameter("caisim");
    Double ordmin = null;
    if (request.getParameter("ordmin") != null && !"".equals(request.getParameter("ordmin"))) {
      ordmin = new Double(request.getParameter("ordmin"));
    }
 
    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();
      if (ordmin != null) {
        this.sqlManager.update("update opes set ordmin = ? where ngara3 = ? and catoff = ?", new Object[] { ordmin, ngara, caisim });
      } else {
        this.sqlManager.update("update opes set ordmin = null where ngara3 = ? and catoff = ?", new Object[] { ngara, caisim });
      }
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
    return null;
  }
}

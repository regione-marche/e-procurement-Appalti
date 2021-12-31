package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.MEPAManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class AggiungiArticoloACarrelloAction extends Action {

  private SqlManager       sqlManager;

  private GenChiaviManager genChiaviManager;

  private MEPAManager      mepaManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setmepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    Long mericart_id = null;
    Long meric_id = null;
    Long meartcat_id = null;
    Double mericart_quanti = null;

    if (request.getParameter("meric_id") != null && !"".equals(request.getParameter("meric_id"))) {
      meric_id = new Long(request.getParameter("meric_id"));
    }

    if (request.getParameter("meartcat_id") != null && !"".equals(request.getParameter("meartcat_id"))) {
      meartcat_id = new Long(request.getParameter("meartcat_id"));
    }

    if (request.getParameter("quanti") != null && !"".equals(request.getParameter("quanti"))) {
      mericart_quanti = new Double(request.getParameter("quanti"));
    }

    if (meric_id != null && meartcat_id != null && mericart_quanti != null) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      try {
        mericart_id = new Long(genChiaviManager.getNextId("MERICART"));
        status = this.sqlManager.startTransaction();
        if(request.getParameter("quanti1") != null && !"".equals(request.getParameter("quanti1"))){
          String des1 = request.getParameter("des1");
          String des2 = request.getParameter("des2");
          Double quanti1=null;
          Double quanti2=null;
          if (request.getParameter("quanti1") != null && !"".equals(request.getParameter("quanti1")))
            quanti1 = new Double(request.getParameter("quanti1"));
          if (request.getParameter("quanti2") != null && !"".equals(request.getParameter("quanti2")))
            quanti2 = new Double(request.getParameter("quanti2"));
          this.sqlManager.update("insert into mericart (id, idric, idartcat, quanti,desdet1,desdet2,quadet1,quadet2) values (?, ?, ?, ?, ?, ?, ?, ?)",
              new Object[] { mericart_id,meric_id, meartcat_id, mericart_quanti,des1,des2, quanti1, quanti2});
        }else{
          this.sqlManager.update("insert into mericart (id, idric, idartcat, quanti) values (?, ?, ?, ?)", new Object[] { mericart_id,
              meric_id, meartcat_id, mericart_quanti });
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
    }
    return null;
  }
}

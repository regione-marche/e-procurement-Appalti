package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

/**
 * @author Stefano.Cestaro
 * 
 */
public class EseguiOperazioniConversazioneAction extends Action {

  public static final String PUBBLICA_MESSAGGIO      = "pubblicaMessaggio";
  public static final String SET_MESSAGGIO_LETTO     = "setMessaggioLetto";
  public static final String SET_MESSAGGIO_NON_LETTO = "setMessaggioNonLetto";

  private SqlManager         sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  private GeneManager geneManager;

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");

    Long discid_p = null;
    if (request.getParameter("discid_p") != null && request.getParameter("discid_p") != "") {
      discid_p = new Long(request.getParameter("discid_p"));
    }

    Long discid = null;
    if (request.getParameter("discid") != null && request.getParameter("discid") != "") {
      discid = new Long(request.getParameter("discid"));
    }

    Long syscon = null;
    if (request.getParameter("syscon") != null && request.getParameter("syscon") != "") {
      syscon = new Long(request.getParameter("syscon"));
    }

    String operazione = request.getParameter("operazione");

    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();

      if (PUBBLICA_MESSAGGIO.equals(operazione)) {
        String updateW_DISCUSS = "update w_discuss set discmesspubbl = ? where discid_p = ? and discid = ?";
        this.sqlManager.update(updateW_DISCUSS, new Object[] { "1", discid_p, discid });
      }

      if (SET_MESSAGGIO_LETTO.equals(operazione)) {
        Long cnt = (Long) this.sqlManager.getObject(
            "select count(*) from w_discread where discid_p = ? and discid = ? and discmessope = ?", new Object[] { discid_p, discid,
                syscon });
        if (cnt == null || (cnt != null && cnt.longValue() == 0)) {
          String insertW_DISCREAD = "insert into w_discread (discid_p, discid, discmessope) values (?,?,?)";
          this.sqlManager.update(insertW_DISCREAD, new Object[] { discid_p, discid, syscon });
        }
      }

      if (SET_MESSAGGIO_NON_LETTO.equals(operazione)) {
        String deleteW_DISCREAD = "delete from w_discread where discid_p = ? and discid = ? and discmessope = ?";
        this.sqlManager.update(deleteW_DISCREAD, new Object[] { discid_p, discid, syscon });
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

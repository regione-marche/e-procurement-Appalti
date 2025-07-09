package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.npa.model.Invitato;
import net.sf.json.JSONObject;

public class EliminaBozzaMEFormAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject  jsonOnject = new JSONObject();

    String idScheda = request.getParameter("idscheda");
    String esito = "ok";
    if(idScheda!=null && !"".equals(idScheda)) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      String updateG1npascheda = "update g1npascheda set exchange_eform = null, uuid_eform = null where id = ?";
      try {
        status = this.sqlManager.startTransaction();
        
        String query = "select idprg_eform, iddoc_eform "
            + "from g1npascheda "
            + "where id = ? ";
        Vector<?> dati = this.sqlManager.getVector(query, new Object[]{idScheda});
        if(dati!=null && !dati.isEmpty()){
          String idprg = SqlManager.getValueFromVectorParam(dati, 0).getStringValue();
          Long iddoc = SqlManager.getValueFromVectorParam(dati, 1).longValue();
          
          if(idprg != null && iddoc != null) {
            updateG1npascheda = "update g1npascheda set exchange_eform = null, uuid_eform = null, idprg_eform = null, iddoc_eform = null, dnot_eform = null where id = ? ";
            sqlManager.update("delete from W_DOCDIG where idprg = ? and iddocdig = ? ", new Object[] { idprg, iddoc });
          }
        }        
        
        this.sqlManager.update(updateG1npascheda, new Object[] { Long.valueOf(idScheda) });
        commitTransaction = true;
      } catch (Exception e) {
        throw new Exception(e.getMessage(), e);
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
            esito="false";
          }
        }
      }
    }

    jsonOnject.put("esito", esito);
    out.println(jsonOnject);
    out.flush();

    return null;

  }

}

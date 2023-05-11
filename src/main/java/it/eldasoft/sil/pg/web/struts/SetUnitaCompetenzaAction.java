package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import net.sf.json.JSONObject;

public class SetUnitaCompetenzaAction extends Action {

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

    String entita = request.getParameter("entita");
    String key1 = request.getParameter("key1");
    String uocompetenza = request.getParameter("uocompetenza");
    String uocompetenzadescrizione = request.getParameter("uocompetenzadescrizione");
    String gara = request.getParameter("gara");

    JSONObject result = new JSONObject();
    PrintWriter out = response.getWriter();

    String oggEvento = gara;
    int livEvento = 1;
    String codEvento = "GA_WSDM_AGGIORNA_UO_COMPETENZA";
    String descrEvento = "Modifica unità operativa di competenza (nuovo valore " + uocompetenza + ")";
    String errMsgEvento  = "";

    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();

      this.sqlManager.update("update wsfascicolo set coduff = ?, desuff = ? where entita = ? and key1 = ?", new Object[] { uocompetenza, uocompetenzadescrizione, entita, key1 });

      commitTransaction = true;

    } catch (Exception e) {
      commitTransaction = false;
      errMsgEvento = e.getMessage();
    } finally {
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }

      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setCodApplicazione("PG");
      logEvento.setOggEvento(oggEvento);
      logEvento.setLivEvento(livEvento);
      logEvento.setCodEvento(codEvento);
      logEvento.setDescr(descrEvento);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);

      result.put("esito", Boolean.valueOf(commitTransaction));
      out.println(result);
      out.flush();
    }
    return null;
  }
}

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
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import net.sf.json.JSONObject;

public class EliminaQformAction extends Action {

  private SqlManager sqlManager;
  private PgManager pgManager;
  private PgManagerEst1 pgManagerEst1;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject  jsonOnject = new JSONObject();

    String id = request.getParameter("id");
    String busta = request.getParameter("busta");
    String codiceGara = request.getParameter("codiceGara");
    String ngara = request.getParameter("ngara");
    String esito = "ok";
    if(id!=null && !"".equals(id)) {
      TransactionStatus status = null;
      boolean commitTransaction = false;

      try {
        status = this.sqlManager.startTransaction();
        this.sqlManager.update("delete from qform where id = ? ", new Object[] { new Long(id) });
        if("1".equals(busta) || "4".equals(busta)) {
          Vector<?> datiTorn = this.sqlManager.getVector("select offtel,iterga from torn where codgar=?", new Object[] {codiceGara});
          if(datiTorn!=null && datiTorn.size()>0) {
            Long offtel=SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
            Long iterga=SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
            if(!new Long(3).equals(offtel)) {
              Long gruppo= new Long(1);
              if((new Long(2).equals(iterga) || new Long(4).equals(iterga)) && "1".equals(busta))
                gruppo= new Long(6);
              String  esitoDGUE= pgManagerEst1.gestioneDocDGUEConcorrenti(codiceGara, ngara, gruppo, new Long(busta), true);
              if("INS".equals(esitoDGUE))
                pgManager.updateImprdocgDaDocumgara(codiceGara, ngara);
            }
          }

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

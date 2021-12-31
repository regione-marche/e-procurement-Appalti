package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSERPTabellatoAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    String codice = request.getParameter("codice");

    String selectTabs = "select id,codice,valore,descrizione from tab_wserp where codice = ?  order by id";
    List<?> datiTabs = sqlManager.getListVector(selectTabs, new Object[] { codice });
    if (datiTabs != null && datiTabs.size() > 0) {
      for (int it = 0; it < datiTabs.size(); it++) {

        String cod = (String) SqlManager.getValueFromVectorParam(datiTabs.get(it), 2).getValue();
        String desc = (String) SqlManager.getValueFromVectorParam(datiTabs.get(it), 3).getValue();

        Object[] row = new Object[2];
        row[0] = cod;
        row[1] = desc;

        jsonArray.add(row);
      }
    }




    out.println(jsonArray);
    out.flush();
    return null;
  }

}

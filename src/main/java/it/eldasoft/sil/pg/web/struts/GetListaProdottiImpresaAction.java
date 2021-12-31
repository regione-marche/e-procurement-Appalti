package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetListaProdottiImpresaAction extends Action {

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

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    Long meric_id = new Long(request.getParameter("meric_id"));
    String codimp = request.getParameter("codimp");

    String selectMERICART = "select distinct mericart.id, "
        + " mericart.idartcat "
        + " from mericart, "
        + " mericprod "
        + " where mericart.idric = ? "
        + " and mericart.id = mericprod.idricart "
        + " and mericprod.codimp = ? ";

    String selectMERICPROD = "select mericprod.id, "
        + " mericprod.preoff "
        + " from mericprod "
        + " where mericprod.idricart = ? "
        + " and mericprod.codimp = ? "
        + " order by mericprod.preoff";

    List<?> datiMERICART = sqlManager.getListVector(selectMERICART, new Object[] { meric_id, codimp });
    if (datiMERICART != null && datiMERICART.size() > 0) {
      for (int iART = 0; iART < datiMERICART.size(); iART++) {
        Long mericart_id = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(iART), 0).getValue();
        Long mericart_idartcat = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(iART), 1).getValue();

        List<?> datiMERICPROD = sqlManager.getListVector(selectMERICPROD, new Object[] { mericart_id, codimp });
        if (datiMERICPROD != null && datiMERICPROD.size() > 0) {
          Long mericprod_id = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(0), 0).getValue();
          Double mericprod_prezzo = (Double) SqlManager.getValueFromVectorParam(datiMERICPROD.get(0), 1).getValue();

          Object[] row = new Object[4];
          row[0] = mericart_id;
          row[1] = mericart_idartcat;
          row[2] = mericprod_id;
          row[3] = mericprod_prezzo;

          jsonArray.add(row);

        }
      }
    }

    out.println(jsonArray);
    out.flush();

    return null;

  }
}

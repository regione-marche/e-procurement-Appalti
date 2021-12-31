package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetStoricoRisposteAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  private TabellatiManager tabellatiManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    String idcom = new String(request.getParameter("idcom"));
    String idprg = new String(request.getParameter("idprg"));

    try {

      String selectGARETTIF = "select " // 0
          + " COMMSGOGG, " // 1
          + " " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"COMDATINS"}) + ", " // 2
          + " COMSTATO " // 3
          + " from W_INVCOM where IDCOMRIS=? and IDPRGRIS=?";


      List<?> datiComRis = sqlManager.getListVector(selectGARETTIF, new Object[] { idcom,idprg });
      if (datiComRis != null && datiComRis.size() > 0) {
        for (int i = 0; i < datiComRis.size(); i++) {
          Object[] row = new Object[3];

          //Date drettif = (Date) SqlManager.getValueFromVectorParam(datiGARRETTIF.get(i), 0).getValue();
          String commsgogg = (String) SqlManager.getValueFromVectorParam(datiComRis.get(i), 0).getValue();
          String comdatins = (String) SqlManager.getValueFromVectorParam(datiComRis.get(i), 1).getValue();
          String comstato = (String) SqlManager.getValueFromVectorParam(datiComRis.get(i), 2).getValue();
          String comstatoDesc = tabellatiManager.getDescrTabellato("G_z20", comstato);

          row[0] = commsgogg;
          row[1] = comdatins;
          row[2] = comstatoDesc;

          jsonArray.add(row);
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dello storico delle risposte della comunicazione con id:" + idcom , e);
    }
    out.println(jsonArray);
    out.flush();

    return null;

  }

}

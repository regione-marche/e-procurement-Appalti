package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRilanciDittaAstaAction extends Action {

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

    //JSONArray jsonArray = new JSONArray();
    JSONObject result = new JSONObject();
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String, Object>>();

    String ngara = request.getParameter("ngara");
    String ditta = request.getParameter("ditta");
    Date data = null;
    Long dataLong=null;
    try {

      String dataorarilString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "DATAORARIL" });

      // 0 - Numero rilanci
      // 1 - Numero fase
      // 2 - Data e ora rilancio
      // 3 - Ribasso
      // 4 - Importo offerto
      // 5 - ID
      // 6 - Ditta
      String select = "select NUMRIL, " //0
          + "NUMFASE, " //1
          + dataorarilString + ", " //2
          + "RIBAUO, " //3
          + "IMPOFF, " //4
          + "ID, "  //5
          + "DITTAO "  //6
          + "FROM AERILANCI "
          + " WHERE NGARA = ? and DITTAO=?"
          + " order by NUMRIL DESC";

      List<?> datiRilanci = sqlManager.getListVector(select, new Object[] { ngara, ditta });
      if (datiRilanci != null && datiRilanci.size() > 0) {

        for (int i = 0; i < datiRilanci.size(); i++) {
          HashMap<String, Object> hMaprilancio = new HashMap<String, Object>();
          hMaprilancio.put("numril", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 0).getValue());
          hMaprilancio.put("numfase", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 1).getValue());
          dataorarilString = SqlManager.getValueFromVectorParam(datiRilanci.get(i), 2).stringValue();
          data = UtilityDate.convertiData(dataorarilString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          if(data!=null)
            dataLong = new Long(data.getTime());
          else
            dataLong = null;
          hMaprilancio.put("dataoraril", dataLong);
          hMaprilancio.put("ribauo", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 3).getValue());
          hMaprilancio.put("impoff", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 4).getValue());
          hMaprilancio.put("id", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 5).getValue());
          hMaprilancio.put("ditta", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 6).getValue());
          hMap.add(hMaprilancio);
        }
        result.put("iTotalRecords", datiRilanci.size());
        result.put("data", hMap);
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni delle ditte invitate all'asta.", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}

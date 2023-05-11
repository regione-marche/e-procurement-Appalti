package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRilanciDittaOffEcoAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  /**
   * Viene costruito una lista json di HashMap contenenti i seguenti dati:
   * -numril
   * -dataoraril
   * -ribauo
   * -impoff
   * -ngararil
   * -dataorateroff
   *
   */
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

    try {

      String dataorarilString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "DATAORAAGG" });


      // 0 - Numero rilanci
      // 1 - Data e ora rilancio
      // 2 - Ribasso
      // 3 - Importo offerto
      // 4 - ngara rilancio
      String select = "select NUMRIL, " //0
          + dataorarilString + ", " //1
          + "RIBAUO, " //2
          + "IMPOFF, " //3
          + "NGARARIL " //4
          + " FROM GARILANCI "
          + " WHERE NGARA = ? and DITTAO=? "
          + " order by NUMRIL DESC";

      List<?> datiRilanci = sqlManager.getListVector(select, new Object[] { ngara, ditta });
      if (datiRilanci != null && datiRilanci.size() > 0) {
        Calendar calendar = Calendar.getInstance();
        Long dataLong=null;
        Date dteoff = null;
        String oteoff="";
        String ngararil="";
        Vector datiTorn=null;
        for (int i = 0; i < datiRilanci.size(); i++) {
          HashMap<String, Object> hMaprilancio = new HashMap<String, Object>();
          hMaprilancio.put("numril", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 0).getValue());
          dataorarilString = SqlManager.getValueFromVectorParam(datiRilanci.get(i), 1).stringValue();
          data = UtilityDate.convertiData(dataorarilString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          if(data!=null)
            dataLong = new Long(data.getTime());
          else
            dataLong = null;
          hMaprilancio.put("dataoraril", dataLong);
          hMaprilancio.put("ribauo", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 2).getValue());
          hMaprilancio.put("impoff", SqlManager.getValueFromVectorParam(datiRilanci.get(i), 3).getValue());
          ngararil = SqlManager.getValueFromVectorParam(datiRilanci.get(i), 4).getStringValue();
          hMaprilancio.put("ngararil", ngararil);
          dataLong = null;
          if(ngararil!=null && !"".equals(ngararil)){
            datiTorn=sqlManager.getVector("select DTEOFF, OTEOFF from TORN where codgar=?", new Object[]{"$"+ngararil});

            if(datiTorn!=null && datiTorn.size()>0){
              dteoff = SqlManager.getValueFromVectorParam(datiTorn, 0).dataValue();
              if(dteoff!=null){
                calendar.setTime(dteoff);
                oteoff = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
                if(oteoff!=null && oteoff.indexOf(":")>0){
                  calendar.add(Calendar.HOUR_OF_DAY, (new Long(oteoff.split(":")[0])).intValue());
                  calendar.add(Calendar.MINUTE, (new Long(oteoff.split(":")[1])).intValue());
                }
                dataLong = new Long(calendar.getTime().getTime());
              }else
                dataLong = null;
            }
          }
          hMaprilancio.put("dataorateroff", dataLong);
          hMap.add(hMaprilancio);
        }
        result.put("iTotalRecords", datiRilanci.size());
        result.put("data", hMap);
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni dei rilanci per la ditta " + ditta , e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}

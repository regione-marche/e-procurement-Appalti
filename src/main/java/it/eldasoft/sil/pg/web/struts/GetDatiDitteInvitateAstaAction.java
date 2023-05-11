package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class GetDatiDitteInvitateAstaAction extends Action {

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

    try {
      Long ribcal = null;
      Long modlicg = null;
      Vector datiGara = sqlManager.getVector("select ribcal, modlicg from gare where ngara=?", new Object[]{ngara});
      if(datiGara!=null && datiGara.size()>0){
        ribcal = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
        modlicg = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
      }

      // 0 - Codice ditta
      // 1 - Numero rilascio
      // 2 - Id
      // 3 - Numero ordine plico
      // 4 - Ragione sociale
      // 5 - Ribasso
      // 6 - Importo offerto
      String select = "select D.DITTAO, " //0
          + "A.NUMRIL, " //1
          + "A.ID, " //2
          + "D.NUMORDPL, " //3
          + "D.NOMIMO, " //4
          + "A.RIBAUO, " //5
          + "A.IMPOFF " //6
          + "FROM DITG D, AERILANCI A "
          + " WHERE D.NGARA5 =? AND D.NGARA5=A.NGARA AND "
          + " D.DITTAO=A.DITTAO AND A.NUMRIL = (SELECT MAX(NUMRIL) "
          + " FROM AERILANCI B WHERE B.NGARA=A.NGARA AND B.DITTAO=A.DITTAO)"
          + " order by ";

      if(new Long(2).equals(ribcal))
        select += "A.IMPOFF";
      else
        select += "A.RIBAUO";

      List<?> datiDitte = sqlManager.getListVector(select, new Object[] { ngara });
      if (datiDitte != null && datiDitte.size() > 0) {

        for (int i = 0; i < datiDitte.size(); i++) {
          HashMap<String, Object> hMapDitta = new HashMap<String, Object>();
          String ditta = (String) SqlManager.getValueFromVectorParam(datiDitte.get(i), 0).getValue();
          hMapDitta.put("ditta", ditta);
          hMapDitta.put("numril", SqlManager.getValueFromVectorParam(datiDitte.get(i), 1).getValue());
          hMapDitta.put("idril", SqlManager.getValueFromVectorParam(datiDitte.get(i), 2).getValue());
          hMapDitta.put("numordpl", SqlManager.getValueFromVectorParam(datiDitte.get(i), 3).getValue());
          hMapDitta.put("nomimo", SqlManager.getValueFromVectorParam(datiDitte.get(i), 4).getValue());
          hMapDitta.put("ribauo", SqlManager.getValueFromVectorParam(datiDitte.get(i), 5).getValue());
          hMapDitta.put("impoff", SqlManager.getValueFromVectorParam(datiDitte.get(i), 6).getValue());
          Long numRil = (Long)sqlManager.getObject("select count(id) from aerilanci where ngara=? and dittao=? and numril<>-1", new Object[]{ngara,ditta});
          hMapDitta.put("totril", numRil);
          hMap.add(hMapDitta);
        }
        result.put("ribcal", ribcal);
        result.put("modlicg", modlicg);
        result.put("iTotalRecords", datiDitte.size());
        result.put("data", hMap);
        //jsonArray = JSONArray.fromObject(datiDitte.toArray());

      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni delle ditte invitate all'asta.", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}

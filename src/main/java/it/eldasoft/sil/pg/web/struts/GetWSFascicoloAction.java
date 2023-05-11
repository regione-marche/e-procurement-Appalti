package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import net.sf.json.JSONArray;

public class GetWSFascicoloAction extends Action {

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

    JSONArray jsonArray = new JSONArray();
    String entita = request.getParameter("entita");
    String key1 = request.getParameter("key1");
    String key2 = request.getParameter("key2");
    String key3 = request.getParameter("key3");
    String key4 = request.getParameter("key4");
    String codiceIn = request.getParameter("codice");
    String annoIn = request.getParameter("anno");
    String numeroIn = request.getParameter("numero");
    String tipo = request.getParameter("tipo");
    
    List<Object> parameters = new ArrayList<Object>();

    String selectWSFASCICOLO = "select codice, anno, numero, codaoo, coduff, struttura, classifica, desclassi,desvoce,desaoo,desuff from wsfascicolo ";

    if ("chiave".equals(tipo)) {
      selectWSFASCICOLO += "where entita = ? and key1 = ? ";
      parameters.add(entita);
      parameters.add(key1);
      if (key2 != null && !"".equals(key2)) {
        selectWSFASCICOLO += " and key2 = ? ";
        parameters.add(key2);
      } else {
        selectWSFASCICOLO += " and (key2 is null or key2='') ";
      }

      if (key3 != null && !"".equals(key3)) {
        selectWSFASCICOLO += " and key3 = ? ";
        parameters.add(key3);
      } else {
        selectWSFASCICOLO += " and (key3 is null or key3='') ";
      }

      if (key4 != null && !"".equals(key4)) {
        selectWSFASCICOLO += " and key4 = ? ";
        parameters.add(key1);
      } else {
        selectWSFASCICOLO += " and (key4 is null or key4='') ";
      }
    } else if ("codice".equals(tipo)) {
      selectWSFASCICOLO += "where anno = ? and numero =?";
      parameters.add(annoIn);
      parameters.add(numeroIn);
      if (codiceIn != null && !"".equals(codiceIn))
        selectWSFASCICOLO += " and codice = ? ";
      parameters.add(codiceIn);
    }

    List<?> datiWSFASCICOLO = sqlManager.getVector(selectWSFASCICOLO, parameters.toArray());

    if (datiWSFASCICOLO != null && datiWSFASCICOLO.size() > 0) {
      String codice = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 0).getValue();
      Long anno = (Long) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 1).getValue();
      String numero = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 2).getValue();
      String codaoo = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 3).getValue();
      String coduff = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 4).getValue();
      String struttura = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 5).getValue();
      String classifica = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 6).getValue();
      String descrizione = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 7).getValue();
      String voce = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 8).getValue();
      String desaoo = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 9).getValue();
      String desuff = (String) SqlManager.getValueFromVectorParam(datiWSFASCICOLO, 10).getValue();
      jsonArray.add(new Object[] { codice, anno, numero, codaoo, coduff, struttura, classifica,descrizione,voce,desaoo,desuff });
    }

    out.print(jsonArray);
    out.flush();

    return null;

  }

}

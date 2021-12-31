package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class GetListaW_INVCOMAction extends Action {

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

    JSONObject result = new JSONObject();
    int total = 0;
    int totalAfterFilter = 0;
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String, Object>>();

    try {
      String entita = request.getParameter("entita");
      String key1 = request.getParameter("key1");
      String key2 = request.getParameter("key2");
      String key3 = request.getParameter("key3");
      String key4 = request.getParameter("key4");

      String selectW_INVCOM = "select idprg, idcom, commsgogg, comdatins, compub, comstato, commsgtes, comdatprot, comnumprot from w_invcom where coment = ? and comkey1 = ? ";

      if (key2 != null && !"".equals(key2)) {
        selectW_INVCOM += " and comkey2 = '" + key2 + "' ";
      } else {
        selectW_INVCOM += " and comkey2 is null ";
      }

      if (key3 != null && !"".equals(key3)) {
        selectW_INVCOM += " and comkey3 = '" + key3 + "' ";
      } else {
        selectW_INVCOM += " and comkey3 is null ";
      }

      if (key4 != null && !"".equals(key4)) {
        selectW_INVCOM += " and comkey4 = '" + key4 + "' ";
      } else {
        selectW_INVCOM += " and comkey4 is null ";
      }

      List<?> datiW_INVCOM = sqlManager.getListVector(selectW_INVCOM, new Object[] { entita, key1 });
      if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
        total = datiW_INVCOM.size();
        totalAfterFilter = datiW_INVCOM.size();

        for (int i = 0; i < datiW_INVCOM.size(); i++) {
          HashMap<String, Object> hMapW_INVCOM = new HashMap<String, Object>();
          hMapW_INVCOM.put("idprg", (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 0).getValue());
          hMapW_INVCOM.put("idcom", (Long) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 1).getValue());
          hMapW_INVCOM.put("commsgogg", (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 2).getValue());

          Date comdatins = (Date) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 3).getValue();
          hMapW_INVCOM.put("comdatins", UtilityDate.convertiData(comdatins, UtilityDate.FORMATO_GG_MM_AAAA));

          Long compub = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 4).getValue();
          hMapW_INVCOM.put("compub", this.getDescrizione1("G_023", compub));

          String comstato = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 5).getValue();
          hMapW_INVCOM.put("comstato", this.getDescrizione2("G_z20", comstato));
         
          hMapW_INVCOM.put("commsgtes", (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 6).getValue());
          
          Date comdatprot = (Date) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 7).getValue();
          if (comdatprot != null) {
            hMapW_INVCOM.put("comdatprot", new Long(comdatprot.getYear() + 1900));  
          } else {
            hMapW_INVCOM.put("comdatprot", null);
          }
          hMapW_INVCOM.put("comnumprot", (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 8).getValue());
          
          hMap.add(hMapW_INVCOM);
        }

      }

      result.put("iTotalRecords", total);
      result.put("iTotalDisplayRecords", totalAfterFilter);
      result.put("data", hMap);

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della lista delle comunicazioni", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

  /**
   * Ricava la descrizione del tabellato TAB1
   * 
   * @param tab1cod
   * @param tab1tip
   * @return
   * @throws Exception
   */
  private String getDescrizione1(String tab1cod, Long tab1tip) throws Exception {
    String descrizione = null;
    if (tab1tip != null) {
      descrizione = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] { tab1cod,
          tab1tip });
    }
    return descrizione;
  }

  /**
   * Ricava la descrizione del tabellato TAB2
   * 
   * @param tab2cod
   * @param tab2tip
   * @return
   * @throws Exception
   */
  private String getDescrizione2(String tab2cod, String tab2tip) throws Exception {
    String descrizione = null;
    if (tab2tip != null) {
      descrizione = (String) sqlManager.getObject("select tab2d2 from tab2 where tab2cod = ? and tab2tip = ?", new Object[] { tab2cod,
          tab2tip });
    }
    return descrizione;
  }

}

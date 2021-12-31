package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

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

public class GetWSDocumentoAction extends Action {

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

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    int total = 0;
    int totalAfterFilter = 0;
    int totaltWsdocumento =0;
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String, Object>>();

    try {
      String entita = request.getParameter("entita");
      String key1 = request.getParameter("key1");
      String key2 = request.getParameter("key2");
      String key3 = request.getParameter("key3");
      String key4 = request.getParameter("key4");
      String letturaComunicazioni = request.getParameter("letturaComunicazioni");

      // Lettura degli elementi documentali generici
      String selectWSDocumento = "select numerodoc, annoprot, numeroprot, oggetto, inout from wsdocumento where entita = ? and key1 = ? ";

      if (key2 != null && !"".equals(key2)) {
        selectWSDocumento += " and key2 = '" + key2 + "' ";
      } else {
        selectWSDocumento += " and key2 is null ";
      }

      if (key3 != null && !"".equals(key3)) {
        selectWSDocumento += " and key3 = '" + key3 + "' ";
      } else {
        selectWSDocumento += " and key3 is null ";
      }

      if (key4 != null && !"".equals(key4)) {
        selectWSDocumento += " and key4 = '" + key4 + "' ";
      } else {
        selectWSDocumento += " and key4 is null ";
      }

      List<?> datiWSDocumento = sqlManager.getListVector(selectWSDocumento, new Object[] { entita, key1 });
      if (datiWSDocumento != null && datiWSDocumento.size() > 0) {
        total += datiWSDocumento.size();
        totalAfterFilter += datiWSDocumento.size();
        totaltWsdocumento += datiWSDocumento.size();

        for (int i = 0; i < datiWSDocumento.size(); i++) {
          HashMap<String, Object> hMapWSDocumento = new HashMap<String, Object>();
          hMapWSDocumento.put("numerodoc", SqlManager.getValueFromVectorParam(datiWSDocumento.get(i), 0).getValue());
          hMapWSDocumento.put("annoprot", SqlManager.getValueFromVectorParam(datiWSDocumento.get(i), 1).getValue());
          hMapWSDocumento.put("numeroprot", SqlManager.getValueFromVectorParam(datiWSDocumento.get(i), 2).getValue());
          hMapWSDocumento.put("oggetto", SqlManager.getValueFromVectorParam(datiWSDocumento.get(i), 3).getValue());
          hMapWSDocumento.put("inout", SqlManager.getValueFromVectorParam(datiWSDocumento.get(i), 4).getValue());
          hMap.add(hMapWSDocumento);
        }
      }

      // Lettura degli elementi documentali associati alle comunicazioni
      if(!"0".equals(letturaComunicazioni)){
        String selectW_INVCOM = "select comdatprot, comnumprot, commsgogg from w_invcom where coment = ? and comkey1 = ? and comdatprot is not null and comnumprot is not null ";

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
          total += datiW_INVCOM.size();
          totalAfterFilter += datiW_INVCOM.size();

          for (int i = 0; i < datiW_INVCOM.size(); i++) {
            HashMap<String, Object> hMapW_INVCOM = new HashMap<String, Object>();
            hMapW_INVCOM.put("numerodoc", "");
            Date comdatprot = (Date) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 0).getValue();
            if (comdatprot != null) {
              hMapW_INVCOM.put("annoprot", new Long(comdatprot.getYear() + 1900));
            } else {
              hMapW_INVCOM.put("annoprot", null);
            }
            hMapW_INVCOM.put("numeroprot", SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 1).getValue());
            hMapW_INVCOM.put("oggetto", SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 2).getValue());
            hMapW_INVCOM.put("inout", "OUT");
            hMap.add(hMapW_INVCOM);
          }
        }
      }

      result.put("iTotalRecords", total);
      result.put("iTotalDisplayRecords", totalAfterFilter);
      result.put("iTotaltWsdocumento", totaltWsdocumento);
      result.put("data", hMap);

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura degli elementi documentali", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}

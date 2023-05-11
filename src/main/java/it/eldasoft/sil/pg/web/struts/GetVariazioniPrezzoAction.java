package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;

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

public class GetVariazioniPrezzoAction extends Action {

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

    JSONArray jsonArray = new JSONArray();

    String ngara = new String(request.getParameter("ngara"));
    Long contaf = new Long(request.getParameter("contaf"));
    String ditta = new String(request.getParameter("ditta"));

    try {


      String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "dataoravar" });

      String selectGarvarpre = "select numvar, " // 0
          + " prezzo, " // 1
          + " importo, " // 2
          + dbFunctionDateToString // 3
          + " from garvarpre where ngara=? and contaf=?";

      if(ditta!=null && !"".equals(ditta))
        selectGarvarpre+=" and dittao=?";
      else
        selectGarvarpre+=" and dittao is null";
      selectGarvarpre += " order by id desc ";

      Object par[];
      if(ditta!=null && !"".equals(ditta)){
        par=new Object[3];
        par[2]=ditta;
      }else{
        par=new Object[2];
      }
      par[0]=ngara;
      par[1]=new Long(contaf);

      List<?> datiGARVARPRE = sqlManager.getListVector(selectGarvarpre, par);
      if (datiGARVARPRE != null && datiGARVARPRE.size() > 0) {
        String dataString = "";
        Double valoreDouble=null;
        String valoreString=null;
        for (int i = 0; i < datiGARVARPRE.size(); i++) {
          Object[] row = new Object[4];
          row[0]=SqlManager.getValueFromVectorParam(datiGARVARPRE.get(i), 0).getValue();
          valoreDouble = (Double)SqlManager.getValueFromVectorParam(datiGARVARPRE.get(i), 1).getValue();
          if(valoreDouble!=null){
            valoreString=UtilityNumeri.convertiImporto(valoreDouble, 2);
            valoreString+=" €";
          }else
            valoreString=null;
          row[1]=valoreString;
          valoreDouble = (Double)SqlManager.getValueFromVectorParam(datiGARVARPRE.get(i), 2).getValue();
          if(valoreDouble!=null){
            valoreString=UtilityNumeri.convertiImporto(valoreDouble, 2);
            valoreString+=" €";
          }else
            valoreString=null;
          row[2]=valoreString;
          dataString = (String) SqlManager.getValueFromVectorParam(datiGARVARPRE.get(i), 3).getValue();
          row[3] = UtilityDate.convertiData(UtilityDate.convertiData(dataString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          jsonArray.add(row);
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dello storico delle variazioni prezzo per la lavorazione: gara=" + ngara + ", contaf=" + contaf, e);
    }
    out.println(jsonArray);
    out.flush();

    return null;

  }

}

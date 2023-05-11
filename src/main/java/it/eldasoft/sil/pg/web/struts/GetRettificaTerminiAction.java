package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRettificaTerminiAction extends Action {

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

    String codgar = new String(request.getParameter("codgar"));
    Long tipo = new Long(request.getParameter("tipo"));

    try {
      // tipo
      // 1 - Termini presentazione domanda di partecipazione
      // 2 - Termini presendazione damanda di offerta
      // 3 - Apertura lichi

      String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "garrettif.drettif" });

      String selectGARETTIF = "select " + dbFunctionDateToString+ ", " // 0
          + " garrettif.datter, " // 1
          + " garrettif.orater, " // 2
          + " garrettif.drichc, " // 3
          + " garrettif.drispc " // 4
          + " from garrettif where garrettif.codgar=? and garrettif.tipter=?"
          + " order by garrettif.id desc ";

      if(new Long(3).equals(tipo))
        selectGARETTIF = "select " + dbFunctionDateToString+ ", " // 0
          + " garrettif.datter, " // 1
          + " garrettif.orater " // 2
          + " from garrettif where garrettif.codgar=? and garrettif.tipter=?"
          + " order by garrettif.id desc ";

      List<?> datiGARRETTIF = sqlManager.getListVector(selectGARETTIF, new Object[] { codgar,tipo });
      if (datiGARRETTIF != null && datiGARRETTIF.size() > 0) {
        for (int i = 0; i < datiGARRETTIF.size(); i++) {
          Object[] row = null;

          //Date drettif = (Date) SqlManager.getValueFromVectorParam(datiGARRETTIF.get(i), 0).getValue();
          String drettifString = (String) SqlManager.getValueFromVectorParam(datiGARRETTIF.get(i), 0).getValue();
          Date datter = (Date) SqlManager.getValueFromVectorParam(datiGARRETTIF.get(i), 1).getValue();
          String orater = (String) SqlManager.getValueFromVectorParam(datiGARRETTIF.get(i), 2).getValue();
          if(new Long(3).equals(tipo)){
            row = new Object[3];
          }else{
            row = new Object[5];
          }

          row[0] = UtilityDate.convertiData(UtilityDate.convertiData(drettifString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          if(datter!=null)
            row[1] = UtilityDate.convertiData(datter, UtilityDate.FORMATO_GG_MM_AAAA);
          else
            row[1] ="";
          if(orater!=null)
            row[2] = orater;
          else
            row[2] = "";

          if(!new Long(3).equals(tipo)){
            Date drichc = (Date) SqlManager.getValueFromVectorParam(datiGARRETTIF.get(i), 3).getValue();
            if(drichc!=null)
              row[3] =  UtilityDate.convertiData(drichc, UtilityDate.FORMATO_GG_MM_AAAA);
            else
              row[3] = "";
            Date drispc = (Date) SqlManager.getValueFromVectorParam(datiGARRETTIF.get(i), 4).getValue();
            if(drispc!=null)
              row[4] =  UtilityDate.convertiData(drispc, UtilityDate.FORMATO_GG_MM_AAAA);
            else
              row[4] = "";
          }

          jsonArray.add(row);
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dello storico delle rettifiche per la gara " + codgar, e);
    }
    out.println(jsonArray);
    out.flush();

    return null;

  }

}

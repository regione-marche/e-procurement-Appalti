package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetDatiFaseAstaAction extends Action {

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

    String ngara = request.getParameter("ngara");

    try {

      // 0 - Numero fase
      // 1 - Data ora inizio
      // 2 - Data ora fine
      // 3 - Durata minima
      // 4 - Durata massima
      // 5 - Tempo base
      String dataorainiString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "dataoraini" });
      String dataorafineString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "dataorafine" });

      String tipologia = "";
      Date oggi = UtilityDate.getDataOdiernaAsDate();

      String selectCampi = "select numfase, " // 0
          + dataorainiString +", " // 1
          + dataorafineString + ", " // 2
          + " durmin, " // 3
          + " durmax, " // 4
          + " tbase " // 5
          + " from aefasi a"
          + " where ngara = ? ";

      String ulterioriCondizioni = " and dataoraini <= ? and dataorafine>=?";  //Si cerca se esiste una fase in corso
      String select = selectCampi + ulterioriCondizioni;
      Vector<?> datiAEFASI = sqlManager.getVector(select, new Object[] { ngara, oggi,oggi});
      if(datiAEFASI != null && datiAEFASI.size() > 0){
        tipologia="1";
      }else if(datiAEFASI==null || (datiAEFASI!=null && datiAEFASI.size()==0)){
        ulterioriCondizioni =  " and numfase = (select min(numfase) from aefasi b "  //Si cerca se esiste la prima fase in apertura
            + "where a.ngara=b.ngara and b.dataoraini>=? group by ngara)";
        select = selectCampi + ulterioriCondizioni;
        datiAEFASI = sqlManager.getVector(select, new Object[] { ngara, oggi});
        if(datiAEFASI != null && datiAEFASI.size() > 0){
          tipologia="3";
        } else if(datiAEFASI==null || (datiAEFASI!=null && datiAEFASI.size()==0)){
          ulterioriCondizioni =  " and numfase = (select max(numfase) from aefasi b "  //Si cerca se esiste l'ultima fase chiusa
              + "where a.ngara=b.ngara and b.dataorafine<=? group by ngara)";
          select = selectCampi + ulterioriCondizioni;
          datiAEFASI = sqlManager.getVector(select, new Object[] { ngara, oggi});
          tipologia="2";
        }
      }


      if (datiAEFASI != null && datiAEFASI.size() > 0) {
        Vector<Object> vect = new Vector();
        vect.add(SqlManager.getValueFromVectorParam(datiAEFASI, 0).longValue());
        dataorainiString = SqlManager.getValueFromVectorParam(datiAEFASI, 1).stringValue();
        dataorafineString = SqlManager.getValueFromVectorParam(datiAEFASI, 2).stringValue();
        vect.add(dataorainiString);
        vect.add(dataorafineString);
        vect.add(SqlManager.getValueFromVectorParam(datiAEFASI, 3).longValue());
        vect.add(SqlManager.getValueFromVectorParam(datiAEFASI, 4).longValue());
        vect.add(SqlManager.getValueFromVectorParam(datiAEFASI, 5).longValue());
        vect.add(tipologia);
        jsonArray = JSONArray.fromObject(vect.toArray());

      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni relative all'articolo.", e);
    }

    out.println(jsonArray);
    out.flush();

    return null;

  }
}

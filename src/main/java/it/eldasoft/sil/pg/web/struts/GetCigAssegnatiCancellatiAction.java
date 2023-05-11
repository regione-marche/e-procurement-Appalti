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

public class GetCigAssegnatiCancellatiAction extends Action {

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
    String genere = new String(request.getParameter("genere"));
    
    try {

      String dbFunctionDateToStringAss = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "data_creazione" });
      
      String dbFunctionDateToStringCanc = sqlManager.getDBFunction("DATETIMETOSTRING",
              new String[] { "data_cancellazione_gara" });
      
      String dbFunctionDateToStringAssLott = sqlManager.getDBFunction("DATETIMETOSTRING",
              new String[] { "l.data_creazione_lotto" });
          
      String dbFunctionDateToStringCancLott = sqlManager.getDBFunction("DATETIMETOSTRING",
              new String[] { "l.data_cancellazione_lotto" });
      
      String dbFunctionDateToStringAssSmart = sqlManager.getDBFunction("DATETIMETOSTRING",
              new String[] { "data_operazione" });

      String selectW3DATI = "select " 
    	  + " id_gara as numero_gara, " // 0
    	  + " cast(null as varchar(10)) as CIG, " // 1
    	  + dbFunctionDateToStringAss +  " as data_assegnazione, "  // 2
          + dbFunctionDateToStringCanc +  " as data_cancellazione, " // 3
          + " (select tab2d2 from tab2 where tab2d1=id_motivazione and tab2cod='W3z14') as motivo_cancellazione, " // 4
          + " oggetto as oggetto, " // 5
          + " cast(null as varchar(10)) as n_lotto " // 6
          + " from w3gara where codgar=? and stato_simog=6 "
          + " union "
          + " select " 
          + " cast(null as varchar(20)) as numero_gara, " // 0
    	  + " l.cig as cig, " // 1
    	  + dbFunctionDateToStringAssLott +  " as data_assegnazione, "  // 2
          + dbFunctionDateToStringCancLott +  " as data_cancellazione, " // 3
          + " (select tab2d2 from tab2 where tab2d1=l.id_motivazione and tab2cod='W3z13') as motivo_cancellazione, " // 4
          + " l.oggetto as oggetto, " // 5
          + " g.codiga as n_lotto " // 6
          + " from w3lott l,gare g where g.codgar1=? and g.ngara=l.ngara and l.stato_simog=6 "    
          + " union "
          + " select " 
          + " cast(null as varchar(20)) as numero_gara, " // 0
    	  + " cig as cig, " // 1
    	  + dbFunctionDateToStringAssSmart +  " as data_assegnazione, "  // 2
    	  + " cast(null as varchar(20)) as data_cancellazione, "  // 3
          + " cast(null as varchar(200)) as motivo_cancellazione, " // 4
          + " oggetto as oggetto, " // 5
          + " cast(null as varchar(10)) as n_lotto " // 6
          + " from w3smartcig where codgar=? and stato=6 "            
          + " order by data_assegnazione ";
      
      List<?> datiW3DATI = sqlManager.getListVector(selectW3DATI, new Object[] { codgar,codgar,codgar });
      if (datiW3DATI != null && datiW3DATI.size() > 0) {
        for (int i = 0; i < datiW3DATI.size(); i++) {
          Object[] row = null;

          String numero_gara = (String) SqlManager.getValueFromVectorParam(datiW3DATI.get(i), 0).getValue();
          String cig = (String) SqlManager.getValueFromVectorParam(datiW3DATI.get(i), 1).getValue();
          String data_assegnazione = (String) SqlManager.getValueFromVectorParam(datiW3DATI.get(i), 2).getValue();
          String data_cancellazione = (String) SqlManager.getValueFromVectorParam(datiW3DATI.get(i), 3).getValue();
          String motivo_cancellazione = (String) SqlManager.getValueFromVectorParam(datiW3DATI.get(i), 4).getValue();
          String oggetto = (String) SqlManager.getValueFromVectorParam(datiW3DATI.get(i), 5).getValue();
          String n_lotto = (String) SqlManager.getValueFromVectorParam(datiW3DATI.get(i), 6).getValue();
          
          if("2".equals(genere)) {
        	  row = new Object[6];
          }
          else {
        	  row = new Object[7];
          }
          
          if(numero_gara!=null)
          row[0] = numero_gara;
          else
          row[0] ="";
          if(cig!=null)
            row[1] = cig;
          else
            row[1] ="";
          if(data_assegnazione!=null)
            row[2] = UtilityDate.convertiData(UtilityDate.convertiData(data_assegnazione, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          else
            row[2] = "";
          if(data_cancellazione!=null)
            row[3] =  UtilityDate.convertiData(UtilityDate.convertiData(data_cancellazione, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          else
            row[3] = "";
          if(motivo_cancellazione!=null)
            row[4] =  motivo_cancellazione;
          else
            row[4] = "";
          if(oggetto!=null)
            row[5] =  oggetto;
          else
            row[5] = "";
          if(!"2".equals(genere)) {
        	  if(n_lotto!=null)
        		  row[6] =  n_lotto;
        	  else
        		  row[6] = "";
          }
          
          jsonArray.add(row);
        }

    }} catch (SQLException e) {
      throw new JspException("Errore durante la lettura dello storico delle rettifiche per la gara " + codgar, e);
    }
    out.println(jsonArray);
    out.flush();

    return null;

  }

}

package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONArray;

public class GetRettificaTerminiPCPAction extends Action {

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
    String tipo = request.getParameter("tipo");
    
    try {
      // tipo
      // 1 - Termini presentazione domanda di partecipazione
      // 2 - Termini presendazione damanda di offerta
      // 3 - Apertura lichi
      
      String selectG1NPARET = "";
      switch(tipo) {
        case "1":
          selectG1NPARET += "select g1nparet.dtepar, " // 0
          + " g1nparet.otepar, " // 1
          + " g1nparet.dtermrichcdp, " // 2
          + " g1nparet.dtermrispcdp "; // 3
          break;
        case "2":
          selectG1NPARET += "select g1nparet.dteoff, " // 0
          + " g1nparet.oteoff, " // 1
          + " g1nparet.dtermrichcpo, " // 2
          + " g1nparet.dtermrispcpo "; // 3
          break;
        case "3":
          selectG1NPARET += "select g1nparet.desoff, " // 0
          + " g1nparet.oesoff "; // 1
          break;     
        }
      selectG1NPARET += "from g1nparet where g1nparet.idnpascheda = "
          + "(select max(g1npascheda.id) from g1npascheda, g1npaform where g1npascheda.idform=g1npaform.id and "
          + "g1npascheda.idschedanparet is not null and g1npascheda.statonpa <60 and g1npaform.codgar=?)";
          
      
      List<?> datiG1NPARET = sqlManager.getListVector(selectG1NPARET, new Object[] {codgar});
      if (datiG1NPARET != null && datiG1NPARET.size() > 0) {
        for (int i = 0; i < datiG1NPARET.size(); i++) {
          Object[] row = null;

          Date data = (Date) SqlManager.getValueFromVectorParam(datiG1NPARET.get(i), 0).getValue();
          String ora = (String) SqlManager.getValueFromVectorParam(datiG1NPARET.get(i), 1).getValue();
          if("3".equals(tipo)){
            row = new Object[2];
          }else{
            row = new Object[4];
          }

          if(data!=null)
            row[0] = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
          else
            row[0] ="";
          
          if(ora!=null)
            row[1] = ora;
          else
            row[1] = "";

          if(!"3".equals(tipo)){
            Date drichc = (Date) SqlManager.getValueFromVectorParam(datiG1NPARET.get(i), 2).getValue();
            if(drichc!=null)
              row[2] =  UtilityDate.convertiData(drichc, UtilityDate.FORMATO_GG_MM_AAAA);
            else
              row[2] = "";
            Date drispc = (Date) SqlManager.getValueFromVectorParam(datiG1NPARET.get(i), 3).getValue();
            if(drispc!=null)
              row[3] =  UtilityDate.convertiData(drispc, UtilityDate.FORMATO_GG_MM_AAAA);
            else
              row[3] = "";
          }

          jsonArray.add(row);
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati delle rettifiche in attesa di pubblicazione presso ANAC per la gara " + codgar, e);
    }
    out.println(jsonArray);
    out.flush();

    return null;

  }

}

package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetDatiRUPAction extends Action {

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

    try {
      String codgar = request.getParameter("codiceGara");
      String codrup = request.getParameter("codrup");
      Object par[] = null;
      String select = "select nometei,cogtei from torn, tecni where codgar = ? and codrup=codtec";
      if(codrup!=null && !"".equals(codrup)){
        select = "select nometei,cogtei from tecni where codtec = ? ";
        par = new Object[]{codrup};
      }else
        par = new Object[]{codgar};

      Vector datiRup =  sqlManager.getVector(select, par);
      if(datiRup!=null && datiRup.size()>0){
        String nome = SqlManager.getValueFromVectorParam(datiRup, 0).getStringValue();
        String cognome = SqlManager.getValueFromVectorParam(datiRup, 1).getStringValue();
        if(nome==null)
          nome="";
        if(cognome==null)
          cognome="";
        result.put("nome", nome);
        result.put("cognome", cognome);
      }



    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati del RUP", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}

package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetCodiceCigAction extends Action {

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
      String genereGara = request.getParameter("genereGara");
      String select = "select codcig from v_gare_torn where codgar = ?";
      if("4".equals(genereGara))
        select = "select codcig from gare where codgar1= ?";
      String cig = (String)sqlManager.getObject(select, new Object[]{codgar});

     result.put("cig", cig);

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del codice cig", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}

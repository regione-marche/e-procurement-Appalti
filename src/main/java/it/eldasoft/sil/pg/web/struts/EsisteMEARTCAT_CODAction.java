package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class EsisteMEARTCAT_CODAction extends Action {

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

  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String ngara = request.getParameter("ngara");
    String cod = request.getParameter("cod");
    HashMap<String, Boolean> hMapResult = new HashMap<String, Boolean>();

    Long numero = (Long) this.sqlManager.getObject("select count(*) from meartcat where ngara = ? and cod = ?", new Object[] {ngara, cod});
    boolean esisteMEARTCAT_COD = (numero != null && numero.longValue() > 0) ? true : false;
    hMapResult.put("esisteMEARTCAT_COD", esisteMEARTCAT_COD);
    JSONObject jsonResult = JSONObject.fromObject(hMapResult);
    out.println(jsonResult);

    out.flush();
    return null;
  }

}
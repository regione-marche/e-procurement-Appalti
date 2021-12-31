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

public class UrlsitoAdoperataAction extends Action {

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
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String urlsito = request.getParameter("urlsito");
    String id = request.getParameter("id");
    HashMap<String, Boolean> hMapResult = new HashMap<String, Boolean>();

    Long numero = (Long) this.sqlManager.getObject("select count(*) from anticor where esportato = ? and urlsito = ? and id <> ?", new Object[] {"1", urlsito, Long.parseLong(id)});
    boolean urlAdoperata = (numero != null && numero.longValue() > 0) ? true : false;
    hMapResult.put("urlAdoperata", urlAdoperata);
    JSONObject jsonResult = JSONObject.fromObject(hMapResult);
    out.println(jsonResult);

    out.flush();
    return null;
  }

}

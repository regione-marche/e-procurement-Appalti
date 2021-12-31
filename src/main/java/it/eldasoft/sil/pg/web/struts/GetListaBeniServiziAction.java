package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Cristian.Febas
 *
 */
public class GetListaBeniServiziAction extends Action {

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
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String codbs = request.getParameter("codbs");

    String selectBS = "select distinct(cod_bs),des_bs from t_ubuy_beniservizi where cod_bs like '" + codbs + "%' order by cod_bs";

    List<?> datiBS = null;
    datiBS = sqlManager.getListVector(selectBS, new Object[] {});
    if (datiBS.size() > 20) {
    	datiBS = datiBS.subList(0, 20);
    }
    JSONArray jsonArrayBS = null;
    if (datiBS != null && datiBS.size() > 0) {
    	jsonArrayBS= JSONArray.fromObject(datiBS.toArray());
    } else {
    	jsonArrayBS = new JSONArray();
    }

    out.println(jsonArrayBS);


    out.flush();
    return null;
  }

}

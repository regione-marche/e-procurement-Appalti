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

public class GetFascicoloAssociatoAction extends Action {

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
      String entita = request.getParameter("entita");
      String chiave1 = request.getParameter("chiave1");
      String chiave2 = request.getParameter("chiave2");
      String chiave3 = request.getParameter("chiave3");

      Long conteggioFascicoli=null;
      String fascicoloAssociato ="0";
      Object parametri[]=null;
      String select = "select count(id) from wsfascicolo where entita = ? and key1 = ?";
      if(chiave2!=null && !"".equals(chiave2))
        select +=" and key2 = ?";
      if(chiave3!=null && !"".equals(chiave3))
        select +=" and key3 = ?";

      if((chiave2==null || "".equals(chiave2)) && (chiave3==null || "".equals(chiave3)))
        parametri=new Object[]{entita,chiave1};
      else if (chiave3==null || "".equals(chiave3))
        parametri=new Object[]{entita,chiave1,chiave2};
      else
        parametri=new Object[]{entita,chiave1,chiave2,chiave3};

      conteggioFascicoli = (Long)sqlManager.getObject(select, parametri);

      if(conteggioFascicoli!= null && conteggioFascicoli.longValue()>0)
        fascicoloAssociato="1";

      result.put("fascicoliAssociati", fascicoloAssociato);
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della property in W_CONFIG", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}

package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import net.sf.json.JSONObject;

public class GetGaraStipulaAction extends Action {

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
      String chiave = request.getParameter("chiave");
      Long id=null;
      if(chiave!=null && !"".equals(chiave))
        id=new Long(chiave);
      String select = "select g.ngara, g.codgar1 from gare g, g1stipula s where id = ? and s.ngara=g.ngara";

      String ngara = null;
      String codgar = null;
      Vector<?> datiGara = sqlManager.getVector(select, new Object[] {id});
      if(datiGara!=null) {
        ngara = SqlManager.getValueFromVectorParam(datiGara, 0).stringValue();
        codgar = SqlManager.getValueFromVectorParam(datiGara, 1).stringValue();
      }

      result.put("ngaraStipula", ngara);
      result.put("codgarStipula", codgar);

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati del RUP", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}

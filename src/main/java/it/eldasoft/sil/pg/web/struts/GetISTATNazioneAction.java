package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import net.sf.json.JSONObject;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class GetISTATNazioneAction extends Action {

  private PgManagerEst1 pgManagerEst1;
  private SqlManager sqlManager;

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

      String codice = request.getParameter("codiceNazione");
      String select = "select codistat from g_nazioni where codice=?";

      String codIstat = (String)this.sqlManager.getObject(select, new Object[]{codice});

      result.put("codIstat", codIstat);

    } catch (SQLException e) {
      throw new JspException("Errore durante il recupero del codice ISTAT per la nazione straniera", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}

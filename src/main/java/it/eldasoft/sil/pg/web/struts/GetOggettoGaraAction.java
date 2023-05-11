package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import net.sf.json.JSONObject;

public class GetOggettoGaraAction extends Action {

  private PgManagerEst1 pgManagerEst1;

 /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
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
      String chiave1 = request.getParameter("chiave1");
      String codgar = request.getParameter("codiceGara");
      String isStipula = request.getParameter("isStipula");
      String genere = request.getParameter("genereGara");
      Long genereGara = null;
      if(genere!=null && !"".equals(genere))
        genereGara = new Long(genere);
      boolean stipula= false;
      if("true".equals(isStipula))
        stipula=true;
      String oggetto = this.pgManagerEst1.getOggettoGara(chiave1, codgar, genereGara,stipula);


      result.put("oggettoGara", oggetto);

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dell'oggetto della gara", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}

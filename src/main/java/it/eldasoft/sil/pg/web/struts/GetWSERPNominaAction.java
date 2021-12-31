package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSERPNominaAction extends Action {

  private SqlManager sqlManager;

  private final String TIPO_ARCHIVIO_IMPRESA = "I";
  private final String TIPO_ARCHIVIO_TECNICO = "T";

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

    String tab1cod = request.getParameter("tab1cod");
    String tab1tip = request.getParameter("tab1tip");
    String tipoarchivio = request.getParameter("tipoarchivio");
    String codicearchivio = request.getParameter("codicearchivio");

    String descrizioneincarico = "";
    String denominazioneincaricato = "";

    if (tab1cod != null && !"".equals(tab1cod) && tab1tip != null && !"".equals(tab1tip)) {
      try {
        descrizioneincarico = (String) this.sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] {tab1cod, new Long(tab1tip)});
      } catch (Exception e) {

      }

    }

    if (tipoarchivio != null && !"".equals(tipoarchivio) && codicearchivio != null && !"".equals(codicearchivio)) {
      if (TIPO_ARCHIVIO_IMPRESA.equals(tipoarchivio)) {
        denominazioneincaricato = (String) this.sqlManager.getObject("select nomest from impr where codimp = ?", new Object[] {codicearchivio});
      } else if (TIPO_ARCHIVIO_TECNICO.equals(tipoarchivio)) {
        denominazioneincaricato = (String) this.sqlManager.getObject("select nomtec from tecni where codtec = ?", new Object[] {codicearchivio});
      }
    }

    result.put("descrizioneincarico",descrizioneincarico);
    result.put("denominazioneincaricato",denominazioneincaricato);

    out.print(result);
    out.flush();

    return null;

  }

}

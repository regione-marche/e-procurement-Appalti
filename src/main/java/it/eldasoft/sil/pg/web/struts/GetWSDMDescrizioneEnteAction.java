package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSDMDescrizioneEnteAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String codein = request.getParameter("codein");
    codein = UtilityStringhe.convertiNullInStringaVuota(codein);
    HashMap<?, ?> hMapUffint = null;
    if(!"".equals(codein)){
      hMapUffint = sqlManager.getHashMap("select codein, nomein from uffint where codein = ?", new Object[] {codein });
    }
    JSONObject jsonUffint = new JSONObject();
    if (hMapUffint != null) {
      jsonUffint = JSONObject.fromObject(hMapUffint);
      jsonUffint.accumulate("enteEsistente", Boolean.TRUE);
    } else {
      jsonUffint.accumulate("enteEsistente", Boolean.FALSE);
    }
    out.println(jsonUffint);

    out.flush();
    return null;
  }

}

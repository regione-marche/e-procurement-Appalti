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

public class GetDescrizioneUtenteStipulaAction extends Action {

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

    String syscon = request.getParameter("syscon");
    String idStipula = request.getParameter("idStipula");
    HashMap<?, ?> hMapUtente = sqlManager.getHashMap("select sysute, email, syscon from usrsys where syscon = ?", new Object[] {new Long(syscon) });
    JSONObject jsonUtente = new JSONObject();
    if (hMapUtente != null) {
      String codStipula = (String)this.sqlManager.getObject("select codstipula from g1stipula where id = ? ", new Object[]{Long.valueOf(idStipula)});
      Long autori = (Long)this.sqlManager.getObject("select autori from g_permessi where idstipula = ? and g_permessi.syscon = ?", new Object[]{Long.valueOf(idStipula), syscon});
      jsonUtente = JSONObject.fromObject(hMapUtente);
      jsonUtente.accumulate("utenteEsistente", Boolean.TRUE);
      if(autori!=null && Long.valueOf(1).equals(autori)) {
    	  jsonUtente.accumulate("utentePermessi", Boolean.TRUE);
      }else {
    	  jsonUtente.accumulate("utentePermessi", Boolean.FALSE);  
      }
    } else {
      jsonUtente.accumulate("utenteEsistente", Boolean.FALSE);
      jsonUtente.accumulate("utentePermessi", Boolean.FALSE);
    }
    out.println(jsonUtente);

    out.flush();
    return null;
  }

}

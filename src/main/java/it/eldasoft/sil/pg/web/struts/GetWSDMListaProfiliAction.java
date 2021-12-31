package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMListaProfiliResType;
import it.maggioli.eldasoft.ws.dm.WSDMProfiloType;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSDMListaProfiliAction extends Action {

  private GestioneWSDMManager gestioneWSDMManager;

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    int total = 0;
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String,Object>>();

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String servizio = request.getParameter("servizio");
    String idconfi = request.getParameter("idconfi");
    String sso = request.getParameter("sso");
    String utenteSso = request.getParameter("utenteSso");

    if(username!=null && !"".equals(username)){
      boolean ssoBool = false;
      if("true".equals(sso))
        ssoBool = true;
      WSDMListaProfiliResType listaProfiliRes = this.gestioneWSDMManager.getWsdmListaProfili(username, password, servizio, idconfi,ssoBool,utenteSso);

      result.put("esito",listaProfiliRes.isEsito());
      result.put("messaggio", listaProfiliRes.getMessaggio());

      if (listaProfiliRes.isEsito()) {
        if (listaProfiliRes.getListaProfili() != null) {

          WSDMProfiloType[] listaProfili = listaProfiliRes.getListaProfili();
            if (listaProfili != null && listaProfili.length > 0) {

              total = listaProfili.length;

              for (int a = 0; a < total; a++) {
                HashMap<String, Object> hMapProfilo = new HashMap<String, Object>();
                hMapProfilo.put("numero", listaProfili[a].getNumeroProfilo());
                hMapProfilo.put("nome", listaProfili[a].getNomeProfilo());
                hMapProfilo.put("codiceaoo", listaProfili[a].getCodiceSede());
                hMapProfilo.put("codiceufficio", listaProfili[a].getCodiceUfficio());
                hMap.add(hMapProfilo);
              }
            }
          }
      }

      result.put("iTotalRecords", total);
      result.put("data", hMap);
    }

    out.print(result);
    out.flush();

    return null;

  }

}

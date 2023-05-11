package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMAmministrazioneAooType;
import it.maggioli.eldasoft.ws.dm.WSDMListaAmministrazioniAooResType;

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

public class GetWSDMListaAmministrazioniAooAction extends Action {

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

    if(username!=null && !"".equals(username)){

      WSDMListaAmministrazioniAooResType wsdmAooRes = this.gestioneWSDMManager.wsdmGetListaAmministrazioniAoo(username, password, servizio,idconfi);
      result.put("esito",wsdmAooRes.isEsito());
      result.put("messaggio",wsdmAooRes.getMessaggio());

      if (wsdmAooRes.isEsito()) {
        if (wsdmAooRes.getListaAmministrazioniAoo() != null) {

          WSDMAmministrazioneAooType[] listaAoo = wsdmAooRes.getListaAmministrazioniAoo();
            if (listaAoo != null && listaAoo.length > 0) {

              total = listaAoo.length;

              for (int a = 0; a < listaAoo.length; a++) {
                HashMap<String, Object> hMapCodiceAoo = new HashMap<String, Object>();
                hMapCodiceAoo.put("codiceaoo", listaAoo[a].getCodiceAmministrazioneAoo());
                hMapCodiceAoo.put("descrizioneaoo", listaAoo[a].getDescrizioneAmministrazioneAoo());
                hMap.add(hMapCodiceAoo);
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

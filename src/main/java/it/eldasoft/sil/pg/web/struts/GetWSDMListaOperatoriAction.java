package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMListaOperatoriResType;
import it.maggioli.eldasoft.ws.dm.WSDMOperatoreType;

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

public class GetWSDMListaOperatoriAction extends Action {

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
    String ruolo = request.getParameter("ruolo");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceuo = request.getParameter("codiceuo");
    String idconfi = request.getParameter("idconfi");
    String filtro = request.getParameter("filtro");

    if(username!=null && !"".equals(username)){

      WSDMListaOperatoriResType listaOpRes = this.gestioneWSDMManager.WSDMListaOperatori(username, password, ruolo, nome, cognome, codiceuo, filtro, servizio, idconfi);

      result.put("esito",listaOpRes.isEsito());
      result.put("messaggio",listaOpRes.getMessaggio());

      if (listaOpRes.isEsito()) {
        if (listaOpRes.getOperatori() != null) {

          WSDMOperatoreType[] listaOperatori = listaOpRes.getOperatori();
            if (listaOperatori != null && listaOperatori.length > 0) {

              total = listaOperatori.length;

              for (int a = 0; a < listaOperatori.length; a++) {
                HashMap<String, Object> hMapOperatore = new HashMap<String, Object>();
                hMapOperatore.put("nome", listaOperatori[a].getNome());
                hMapOperatore.put("cognome", listaOperatori[a].getCognome());
                hMapOperatore.put("ruolo", listaOperatori[a].getRuolo());
                hMapOperatore.put("codiceuo", listaOperatori[a].getCodiceUO());
                hMap.add(hMapOperatore);
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

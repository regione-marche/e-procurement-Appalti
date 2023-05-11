package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMListaTipiTrasmissioneResType;
import it.maggioli.eldasoft.ws.dm.WSDMTipoTrasmissioneType;

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

public class GetWSDMListaTipiTrasmissioneAction extends Action {

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

    if(username!=null && !"".equals(username)){

      WSDMListaTipiTrasmissioneResType listaTipiTrRes = this.gestioneWSDMManager.WSDMListaTipiTrasmissione(username, password, ruolo, nome, cognome, codiceuo, servizio, idconfi);

      result.put("esito",listaTipiTrRes.isEsito());
      result.put("messaggio",listaTipiTrRes.getMessaggio());

      if (listaTipiTrRes.isEsito()) {
        if (listaTipiTrRes.getTipiTrasmissione() != null) {

          WSDMTipoTrasmissioneType[] listaTipiTrasmissione = listaTipiTrRes.getTipiTrasmissione();
            if (listaTipiTrasmissione != null && listaTipiTrasmissione.length > 0) {

              total = listaTipiTrasmissione.length;

              for (int a = 0; a < listaTipiTrasmissione.length; a++) {
                HashMap<String, Object> hMapTipo = new HashMap<String, Object>();
                hMapTipo.put("codice", listaTipiTrasmissione[a].getCodice());
                hMapTipo.put("descrizione", listaTipiTrasmissione[a].getDescrizione());
                hMap.add(hMapTipo);
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

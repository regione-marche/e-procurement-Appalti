package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMClassificaType;
import it.maggioli.eldasoft.ws.dm.WSDMListaClassificheResType;

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

public class GetWSDMListaClassificheAction extends Action {

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
    String tipoGara = request.getParameter("tipoGara");
    String idconfi = request.getParameter("idconfi");
    String voce = null;

    if(username!=null && !"".equals(username)){
      if("1".equals(tipoGara))
        voce=GestioneWSDMManager.VOCE_LAVORI;
      else if("2".equals(tipoGara))
        voce=GestioneWSDMManager.VOCE_FORNITURA;
      else if("3".equals(tipoGara))
        voce=GestioneWSDMManager.VOCE_SERVIZI;

      WSDMListaClassificheResType listaClassificheRes = this.gestioneWSDMManager.wsdmListaClassifiche(username, password, null, null, null, null, null, null, servizio, null, null,voce,idconfi);

      result.put("esito",listaClassificheRes.isEsito());
      result.put("messaggio",listaClassificheRes.getMessaggio());

      if (listaClassificheRes.isEsito()) {
        if (listaClassificheRes.getListaClassifiche() != null) {

          WSDMClassificaType[] listaClassifiche = listaClassificheRes.getListaClassifiche();
            if (listaClassifiche != null && listaClassifiche.length > 0) {

              total = listaClassifiche.length;

              for (int a = 0; a < total; a++) {
                HashMap<String, Object> hMapClassifica = new HashMap<String, Object>();
                hMapClassifica.put("codice", listaClassifiche[a].getCodice());
                hMapClassifica.put("descrizione", listaClassifiche[a].getDescrizione());
                hMapClassifica.put("voce",listaClassifiche[a].getVoce());
                hMap.add(hMapClassifica);
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

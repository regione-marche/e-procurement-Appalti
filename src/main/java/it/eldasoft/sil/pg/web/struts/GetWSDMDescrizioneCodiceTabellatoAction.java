package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSDMTabellatoElementoType;
import it.maggioli.eldasoft.ws.conf.WSDMTabellatoType;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSDMDescrizioneCodiceTabellatoAction extends Action {

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

    JSONObject  jsonOnject = new JSONObject();

    String nome = request.getParameter("nome");
    String servizio = request.getParameter("servizio");
    String valore = request.getParameter("valore");
    String idconfi = request.getParameter("idconfi");
    
    boolean tabellatiInDb = this.gestioneWSDMManager.isTabellatiInDb();
    
    List<String[]> listaTab = gestioneWSDMManager.getValoriTabellato(servizio, nome,idconfi,tabellatiInDb);
    boolean continua = true;
    if(listaTab!=null && listaTab.size()>0){
      for(int i=0;i<listaTab.size() && continua;i++){
        if(listaTab.get(i)[0].equals(valore)){
          jsonOnject.put("descrizione", listaTab.get(i)[1]);
          continua=false;
        }
      }
    }

    out.println(jsonOnject);
    out.flush();

    return null;

  }

}

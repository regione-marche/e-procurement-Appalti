package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMRicercaFascicoloResType;
import net.sf.json.JSONObject;

public class GetWSDMListaFascicoliAction extends Action {

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
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String,Object>>();

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String cognome = request.getParameter("cognome");
    String annoString = request.getParameter("anno");
    String classifica = request.getParameter("classifica");
    String codicefascicolo = request.getParameter("codicefascicolo");
    String oggetto =  request.getParameter("oggetto");
    String struttura =  request.getParameter("struttura");
    String codiceproc =  request.getParameter("codiceproc");
    String cig =  request.getParameter("cig");
    String idconfi = request.getParameter("idconfi");
    String servizio = request.getParameter("servizio");

    Long anno=null;
    if(annoString!=null && !"".equals(annoString))
      anno= new Long(annoString);

    if("".equals(classifica))
      classifica = null;

    if("".equals(codicefascicolo))
      codicefascicolo = null;

    if("".equals(oggetto))
      oggetto = null;

    if("".equals(struttura))
      struttura = null;

    if("".equals(codiceproc))
      codiceproc = null;

    if("".equals(cig))
      cig = null;

    WSDMRicercaFascicoloResType ricerca = this.gestioneWSDMManager.wsdmRicercaFascicolo(username, password, cognome, anno, classifica, codicefascicolo,
        oggetto, struttura, codiceproc, cig, servizio, idconfi);
    if(ricerca.isEsito()) {
      WSDMFascicoloType[] fascicoli = ricerca.getFascicoli();
      String codice =null;
      oggetto = null;
      struttura=null;
      String numero = null;
      String classificaDesc= null;

      HashMap<String, Object> hMapFascicolo = null;
      for (int i = 0; i < fascicoli.length; i++) {
        codice = fascicoli[i].getCodiceFascicolo();
        oggetto = fascicoli[i].getOggettoFascicolo();
        classifica = fascicoli[i].getClassificaFascicolo();
        classificaDesc = fascicoli[i].getClassificaFascicoloDescrizione();
        anno=fascicoli[i].getAnnoFascicolo();
        struttura = fascicoli[i].getStruttura();
        numero = fascicoli[i].getNumeroFascicolo();
        hMapFascicolo = new HashMap<String, Object>();
        hMapFascicolo.put("codice", codice);
        hMapFascicolo.put("descrizione", oggetto);
        hMapFascicolo.put("classifica", classifica);
        hMapFascicolo.put("classificaDesc", classificaDesc);
        hMapFascicolo.put("anno", anno);
        hMapFascicolo.put("numero", numero);
        hMapFascicolo.put("struttura", struttura);
        hMap.add(hMapFascicolo);
      }
      result.put("data", hMap);
      result.put("esito",true);
    }else {
      result.put("esito",false);
      result.put("messaggio", ricerca.getMessaggio());
    }

    out.println(result);
    out.flush();

    return null;

  }

}

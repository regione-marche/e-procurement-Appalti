package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;

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

public class GetWSDMFascicoloAction extends Action {

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
    int totalAfterFilter = 0;
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String,Object>>();

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String ruolo = request.getParameter("ruolo");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceuo = request.getParameter("codiceuo");
    String idutente = request.getParameter("idutente");
    String idutenteunop = request.getParameter("idutenteunop");
    String codice = request.getParameter("codice");
    String entita = request.getParameter("entita");
    String key1 = request.getParameter("key1");
    String servizio = request.getParameter("servizio");
    String idconfi = request.getParameter("idconfi");
    Long anno = null;
    if (request.getParameter("anno") != null && !"".equals(request.getParameter("anno"))) {
      anno = new Long(request.getParameter("anno"));
    }
    String numero = request.getParameter("numero");
    String classifica = request.getParameter("classifica");

    if(username!=null && !"".equals(username)){
      WSDMFascicoloResType wsdmFascicoloRes = this.gestioneWSDMManager.wsdmFascicoloLeggi(username, password, ruolo, nome, cognome, codiceuo,
          idutente, idutenteunop, codice, anno, numero, servizio, classifica,idconfi);

      result.put("esito",wsdmFascicoloRes.isEsito());
      result.put("messaggio",wsdmFascicoloRes.getMessaggio());

      if (wsdmFascicoloRes.isEsito()) {
        if (wsdmFascicoloRes.getFascicolo() != null) {

          result.put("codicefascicolo",wsdmFascicoloRes.getFascicolo().getCodiceFascicolo());
          result.put("oggettofascicolo",wsdmFascicoloRes.getFascicolo().getOggettoFascicolo());
          result.put("classificafascicolodescrizione",wsdmFascicoloRes.getFascicolo().getClassificaFascicoloDescrizione());
          String classificaFascicolo = wsdmFascicoloRes.getFascicolo().getClassificaFascicolo();
          if(classificaFascicolo==null || "".equals(classificaFascicolo))
            classificaFascicolo = this.gestioneWSDMManager.getClassificaFascicolo(entita, key1);
          result.put("classificafascicolo",classificaFascicolo);
          result.put("descrizionefascicolo",wsdmFascicoloRes.getFascicolo().getDescrizioneFascicolo());
          if(wsdmFascicoloRes.getFascicolo().getAnnoFascicolo()!=null){
            result.put("annofascicolo",wsdmFascicoloRes.getFascicolo().getAnnoFascicolo());
          }
          if(wsdmFascicoloRes.getFascicolo().getNumeroFascicolo()!=null){
            result.put("numerofascicolo",wsdmFascicoloRes.getFascicolo().getNumeroFascicolo());
          }
          if(wsdmFascicoloRes.getFascicolo().getStruttura()!=null){
            result.put("struttura",wsdmFascicoloRes.getFascicolo().getStruttura());
          }

          if (wsdmFascicoloRes.getFascicolo().getDocumenti() != null) {
            WSDMFascicoloDocumentoType[] wsdmDocumenti = wsdmFascicoloRes.getFascicolo().getDocumenti();
            if (wsdmDocumenti != null && wsdmDocumenti.length > 0) {

              total = wsdmDocumenti.length;
              totalAfterFilter = wsdmDocumenti.length;

              for (int a = 0; a < wsdmDocumenti.length; a++) {
                HashMap<String, Object> hMapDocumento = new HashMap<String, Object>();
                hMapDocumento.put("numerodocumento", wsdmDocumenti[a].getNumeroDocumento());
                hMapDocumento.put("annoprotocollo", wsdmDocumenti[a].getAnnoProtocollo());
                hMapDocumento.put("numeroprotocollo", wsdmDocumenti[a].getNumeroProtocollo());
                hMapDocumento.put("oggetto", wsdmDocumenti[a].getOggetto());
                hMapDocumento.put("segnaturaprotocollo", wsdmDocumenti[a].getSegnaturaProtocollo());
                hMapDocumento.put("codiceUO", wsdmDocumenti[a].getCodiceUO());

                String inout = "";
                if (WSDMProtocolloInOutType.IN.equals(wsdmDocumenti[a].getInout())) {
                  inout = "Ingresso";
                } else if (WSDMProtocolloInOutType.OUT.equals(wsdmDocumenti[a].getInout())) {
                  inout = "Uscita";
                }
                hMapDocumento.put("inout", inout);
                hMap.add(hMapDocumento);
              }
            }

          }

        }

        result.put("iTotalRecords", total);
        result.put("iTotalDisplayRecords", totalAfterFilter);
        result.put("data", hMap);

      }
    }else{
      boolean letturaWsfascicolo = true;
      if("DOCUMENTALE".equals(servizio)){
        WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi(servizio,idconfi);
        if (configurazione.isEsito()){
          String tipoWSDM = configurazione.getRemotewsdm();
          if("ENGINEERINGDOC".equals(tipoWSDM)){
            result.put("codicefascicolo",codice);
            letturaWsfascicolo=false;
          }
        }
      }
      //Si caricano i dati minimi da WSFASCICOLO
      if(letturaWsfascicolo){
        String codicefascicolo = this.gestioneWSDMManager.getCodiceFascicoloDaWsfascicolo(entita, key1);
        String classificaFascicolo = this.gestioneWSDMManager.getClassificaFascicolo(entita, key1);
        result.put("codicefascicolo",codicefascicolo);
        result.put("classificafascicolo",classificaFascicolo);
        result.put("iTotalRecords", total);
        result.put("iTotalDisplayRecords", totalAfterFilter);
        result.put("data", hMap);
      }
      result.put("esito",true);
    }

    out.print(result);
    out.flush();

    return null;

  }

}

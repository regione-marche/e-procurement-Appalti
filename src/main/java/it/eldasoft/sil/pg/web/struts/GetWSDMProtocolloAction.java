package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
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

public class GetWSDMProtocolloAction extends Action {

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

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String ruolo = request.getParameter("ruolo");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceuo = request.getParameter("codiceuo");
    String idutente = request.getParameter("idutente");
    String idutenteunop = request.getParameter("idutenteunop");

    Long annoprotocollo = new Long(request.getParameter("annoprotocollo"));
    String numeroprotocollo = request.getParameter("numeroprotocollo");
    String servizio = request.getParameter("servizio");
    String idconfi = request.getParameter("idconfi");
    if(servizio==null || "".equals(servizio))
      servizio ="FASCICOLOPROTOCOLLO";

    String profilo = request.getParameter("profilo");
    String sso = request.getParameter("sso");
    String utenteSso = request.getParameter("utenteSso");
    String leggerefascicolo = request.getParameter("leggerefascicolo");

    boolean ssoBool = false;
    if("true".equals(sso))
      ssoBool = true;

    JSONObject result = new JSONObject();
    int totalMITTENTI = 0;
    int totalDESTINATARI = 0;
    int totalALLEGATI = 0;
    List<HashMap<String, Object>> hMapMITTENTI = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapDESTINATARI = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapALLEGATI = new ArrayList<HashMap<String, Object>>();

    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmProtocolloLeggi(username, password, ruolo,
        nome, cognome, codiceuo, idutente, idutenteunop, annoprotocollo, numeroprotocollo, servizio,idconfi,profilo, ssoBool, utenteSso);

    result.put("esito", wsdmProtocolloDocumentoRes.isEsito());
    result.put("messaggio", wsdmProtocolloDocumentoRes.getMessaggio());

    if (wsdmProtocolloDocumentoRes.isEsito()) {
      WSDMProtocolloDocumentoType wsdmProtocolloDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento();
      if (wsdmProtocolloDocumento != null) {

        // Dati generali
        result.put("oggetto", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getOggetto());
        result.put("numerodocumento", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento());
        result.put("annoprotocollo", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo());
        result.put("numeroprotocollo", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo());

        String inout = "";
        String inoutDes = "";
        if (WSDMProtocolloInOutType.IN.equals(wsdmProtocolloDocumentoRes.getProtocolloDocumento().getInout())) {
          inoutDes = "Ingresso";
        } else if (WSDMProtocolloInOutType.OUT.equals(wsdmProtocolloDocumentoRes.getProtocolloDocumento().getInout())) {
          inoutDes = "Uscita";
        }
        inout = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getInout().getValue();

        result.put("inout", inoutDes);
        result.put("INOUT", inout);
        result.put("mittenteinternodescrizione", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getMittenteInternoDescrizione());
        result.put("classificadescrizione", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getClassificaDescrizione());
        result.put("classifica", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getClassifica());
        result.put("tipodocumentodescrizione", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getTipoDocumentoDescrizione());
        result.put("voce", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getVoce());


        // Mittenti
        if (wsdmProtocolloDocumento.getMittenti() != null) {
          WSDMProtocolloAnagraficaType[] mittenti = wsdmProtocolloDocumento.getMittenti();
          totalMITTENTI = mittenti.length;
          for (int m = 0; m < mittenti.length; m++) {
            HashMap<String, Object> hMap = new HashMap<String, Object>();
            hMap.put("cognomeointestazione", mittenti[m].getCognomeointestazione());
            hMap.put("codicefiscale", mittenti[m].getCodiceFiscale());
            hMapMITTENTI.add(hMap);
          }
        }

        // Destinatari
        if (wsdmProtocolloDocumento.getDestinatari() != null) {
          WSDMProtocolloAnagraficaType[] destinatari = wsdmProtocolloDocumento.getDestinatari();
          totalDESTINATARI = destinatari.length;
          for (int d = 0; d < destinatari.length; d++) {
            HashMap<String, Object> hMap = new HashMap<String, Object>();
            hMap.put("cognomeointestazione", destinatari[d].getCognomeointestazione());
            hMap.put("codicefiscale", destinatari[d].getCodiceFiscale());
            hMapDESTINATARI.add(hMap);
          }
        }

        // Allegati
        if (wsdmProtocolloDocumento.getAllegati() != null) {
          WSDMProtocolloAllegatoType[] allegati = wsdmProtocolloDocumento.getAllegati();
          totalALLEGATI = allegati.length;
          for (int a = 0; a < allegati.length; a++) {
            HashMap<String, Object> hMap = new HashMap<String, Object>();
            hMap.put("titoloallegato", allegati[a].getTitolo());
            hMap.put("tipoallegato", allegati[a].getTipo());
            hMap.put("nomeallegato", allegati[a].getNome());
            hMap.put("urlallegato", allegati[a].getUrlDownload());
            hMapALLEGATI.add(hMap);
          }
        }

        // Fascicolo
        if("true".equals(leggerefascicolo) && wsdmProtocolloDocumento.getFascicolo() !=null){
          WSDMFascicoloType fascicolo = wsdmProtocolloDocumento.getFascicolo();
          result.put("codiceFascicolo", fascicolo.getCodiceFascicolo());
          result.put("numeroFascicolo",fascicolo.getNumeroFascicolo());
          result.put("annoFascicolo",fascicolo.getAnnoFascicolo());
          result.put("oggettoFascicolo", fascicolo.getOggettoFascicolo());
          result.put("classificaFascicolo", fascicolo.getClassificaFascicolo());
          result.put("classificaDescrizioneFascicolo", fascicolo.getClassificaFascicoloDescrizione());

        }
      }
    }

    result.put("iTotalRecordsMITTENTI", totalMITTENTI);
    result.put("iTotalDisplayRecordsMITTENTI", totalMITTENTI);
    result.put("dataMITTENTI", hMapMITTENTI);
    result.put("iTotalRecordsDESTINATARI", totalDESTINATARI);
    result.put("iTotalDisplayRecordsDESTINATARI", totalDESTINATARI);
    result.put("dataDESTINATARI", hMapDESTINATARI);
    result.put("iTotalRecordsALLEGATI", totalALLEGATI);
    result.put("iTotalDisplayRecordsALLEGATI", totalALLEGATI);
    result.put("dataALLEGATI", hMapALLEGATI);

    out.print(result);
    out.flush();

    return null;

  }
}

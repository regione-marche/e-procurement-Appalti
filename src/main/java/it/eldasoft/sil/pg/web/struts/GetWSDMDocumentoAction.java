package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.dm.WSDMCampoType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;
import it.maggioli.eldasoft.ws.dm.WSDMRigaType;
import it.maggioli.eldasoft.ws.dm.WSDMTabellaType;

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

public class GetWSDMDocumentoAction extends Action {

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
    String numeroDocumento = request.getParameter("numerodocumento");
    String servizio = request.getParameter("servizio");
    String idconfi = request.getParameter("idconfi");
    if(servizio==null || "".equals(servizio))
      servizio ="FASCICOLOPROTOCOLLO";

    JSONObject result = new JSONObject();
    int totalMITTENTI = 0;
    int totalDESTINATARI = 0;
    int totalALLEGATI = 0;
    int totalDATIPERSONALIZZATI = 0;
    List<HashMap<String, Object>> hMapMITTENTI = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapDESTINATARI = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapALLEGATI = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapDATIPERSONALIZZATI = new ArrayList<HashMap<String, Object>>();

    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmDocumentoLeggi(username, password, ruolo,
        nome, cognome, codiceuo, idutente, idutenteunop, numeroDocumento,servizio,idconfi);

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

        String wsdmGestioneERP = ConfigManager.getValore("wsdm.gestioneERP."+idconfi);
        wsdmGestioneERP = UtilityStringhe.convertiNullInStringaVuota(wsdmGestioneERP);
        if("1".equals(wsdmGestioneERP)){
          WSDMTabellaType[] datiPersonalizzati = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getDatiPersonalizzati();
          if(datiPersonalizzati!=null){
            String tipoDatiPersonalizzati = "";
            for(int i=0; i < datiPersonalizzati.length; i++){
              WSDMTabellaType datoP = datiPersonalizzati[i];
              tipoDatiPersonalizzati = datoP.getNome();//RDA verifica
              tipoDatiPersonalizzati = tipoDatiPersonalizzati.toUpperCase();
              if("RDA".equals(tipoDatiPersonalizzati)){
                result.put("tipoDatiPersonalizzati",tipoDatiPersonalizzati);
                WSDMRigaType[] righe = datiPersonalizzati[i].getRiga();
                for (int r = 0; r < righe.length; r++) {
                  WSDMRigaType riga = righe[r];
                  WSDMCampoType[] campi = riga.getCampo();
                  totalDATIPERSONALIZZATI = campi.length;
                  for (int c = 0; c < campi.length; c++) {
                    HashMap<String, Object> hMap = new HashMap<String, Object>();
                    WSDMCampoType campo = campi[c];
                    String nomeMetaDato = campo.getNome();
                    if("rup".equals(nomeMetaDato)){
                      String valRup = campo.getValore();
                      WSDMProtocolloAnagraficaType protocolloAnagraficaType = gestioneWSDMManager.WSDMAnagraficaLeggi(valRup,idconfi);
                      hMap.put("nomeMetadato", nomeMetaDato);
                      String valoreMetaDato = protocolloAnagraficaType.getCognomeointestazione();
                      if(valoreMetaDato == null || valoreMetaDato.length()==0){
                        valoreMetaDato = valRup;
                      }
                      hMap.put("valoreMetadato", valoreMetaDato);
                    }else{
                      if("dec".equals(nomeMetaDato)){
                        String valDec = campo.getValore();
                        WSDMProtocolloAnagraficaType protocolloAnagraficaType = gestioneWSDMManager.WSDMAnagraficaLeggi(valDec,idconfi);
                        hMap.put("nomeMetadato", nomeMetaDato);
                        String valoreMetaDato = protocolloAnagraficaType.getCognomeointestazione();
                        if(valoreMetaDato == null || valoreMetaDato.length()==0){
                          valoreMetaDato = valDec;
                        }
                        hMap.put("valoreMetadato", valoreMetaDato);
                      }else{
                        hMap.put("nomeMetadato", nomeMetaDato);
                        String valoreMetaDato = campo.getValore();
                        hMap.put("valoreMetadato", valoreMetaDato);
                      }
                    }
                    hMapDATIPERSONALIZZATI.add(hMap);
                  }
                }

                result.put("iTotalRecordsDATIPERSONALIZZATI", totalDATIPERSONALIZZATI);
                result.put("iTotalDisplayRecordsDATIPERSONALIZZATI", totalDATIPERSONALIZZATI);
                result.put("dataDATIPERSONALIZZATI", hMapDATIPERSONALIZZATI);
              }
            }
          }
        }

        String inout = "";
        if (WSDMProtocolloInOutType.IN.equals(wsdmProtocolloDocumentoRes.getProtocolloDocumento().getInout())) {
          inout = "Ingresso";
        } else if (WSDMProtocolloInOutType.OUT.equals(wsdmProtocolloDocumentoRes.getProtocolloDocumento().getInout())) {
          inout = "Uscita";
        }

        result.put("inout", inout);
        result.put("mittenteinternodescrizione", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getMittenteInternoDescrizione());
        result.put("classificadescrizione", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getClassificaDescrizione());
        result.put("tipodocumentodescrizione", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getTipoDocumentoDescrizione());
        result.put("classifica", wsdmProtocolloDocumentoRes.getProtocolloDocumento().getClassifica());

        // Fascicolo
        if (wsdmProtocolloDocumento.getFascicolo() != null) {
          WSDMFascicoloType fascicolo = wsdmProtocolloDocumento.getFascicolo();
          Long annofasc = fascicolo.getAnnoFascicolo();
          if(annofasc != null){
            String annofascicolo = annofasc.toString();
            result.put("annofascicolo", annofascicolo);
          }
          String numerofascicolo = fascicolo.getNumeroFascicolo();
          result.put("numerofascicolo", numerofascicolo);
        }

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
            if(allegati[a]!=null) {
              hMap.put("titoloallegato", allegati[a].getTitolo());
              hMap.put("tipoallegato", allegati[a].getTipo());
              hMap.put("nomeallegato", allegati[a].getNome());
              hMap.put("urlallegato", allegati[a].getUrlDownload());
              hMapALLEGATI.add(hMap);
            }
          }
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

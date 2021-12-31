package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPMetadatoType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSERPListaRdaAction extends Action {

  private GestioneWSERPManager gestioneWSERPManager;

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String servizio = request.getParameter("servizio");
    if(servizio==null || "".equals(servizio))
      servizio ="WSERP";

    String codiceGara = request.getParameter("codgar");
    String codiceRda = request.getParameter("codicerda");
    String gruppoAcquisti = request.getParameter("gruppoacq");
    String divisione = request.getParameter("divisione");
    String codiceMateriale = request.getParameter("codicemateriale");
    String dataConsegna = request.getParameter("dataconsegna");
    String oggetto = request.getParameter("oggetto");
    String tipoAppalto = request.getParameter("tipoAppalto");
    String uffint = request.getParameter("uffint");
    String scProfilo = request.getParameter("scProfilo");
    scProfilo = UtilityStringhe.convertiNullInStringaVuota(scProfilo);

    JSONObject result = new JSONObject();
    int totalRDA = 0;
    int totalAfterFilterRDA = 0;
    int totalDATIPERSONALIZZATI = 0;
    List<HashMap<String, Object>> hMapRDA = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapDATIPERSONALIZZATI = new ArrayList<HashMap<String, Object>>();
    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();

    String tipoWSERP =null;
    WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
    if(configurazione.isEsito()){
      tipoWSERP = configurazione.getRemotewserp();
    }



      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

      String username = credenziali[0];
      String password = credenziali[1];

      WSERPRdaType erpSearch = new WSERPRdaType();
      if("AVM".equals(tipoWSERP)){
        erpSearch.setCodiceRda(codiceRda);

        dataConsegna = UtilityStringhe.convertiNullInStringaVuota(dataConsegna);
        if(!"".equals(dataConsegna)){
          dataConsegna= dataConsegna.replace("/", "-");
          Calendar cal = Calendar.getInstance();
          Date dcons = UtilityDate.convertiData(dataConsegna, UtilityDate.FORMATO_GG_MM_AAAA_CON_TRATTINI);
          cal.setTime(dcons);
          erpSearch.setDataConsegna(cal);
        }
        erpSearch.setOggetto(oggetto);
        erpSearch.setGruppoAcquisti(gruppoAcquisti);
        erpSearch.setDivisione(divisione);
        erpSearch.setCodiceMateriale(codiceMateriale);
      }
      if("UGOVPA".equals(tipoWSERP)){
        if(!"".equals(uffint)){
          erpSearch.setDivisione(uffint);
        }
      }

      if("FNM".equals(tipoWSERP)){
        if(!"".equals(codiceRda)){
          erpSearch.setCodiceRda(codiceRda);
          erpSearch.setNatura(tipoAppalto);
        }
        if(!"".equals(divisione)){
          erpSearch.setEsercizio(divisione);
        }
        if(!"".equals(scProfilo)){
          erpSearch.setSceltaContraente(scProfilo);
        }
      }

      wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch );


    result.put("esito", wserpRdaRes.isEsito());
    result.put("messaggio", wserpRdaRes.getMessaggio());

    if (wserpRdaRes.isEsito()) {
      if (wserpRdaRes.getRdaArray()!= null) {
        WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
        totalRDA = rdaArray.length;


        if("FNM".equals(tipoWSERP) && codiceRda!= null && divisione!= null){
          WSERPRdaType rdaDP = rdaArray[0];

          WSERPMetadatoType[] metadati = rdaDP.getMetadatoArray();
          if(metadati != null){
            totalDATIPERSONALIZZATI = metadati.length;

            for (int m = 0; m < metadati.length; m++) {
              WSERPMetadatoType metadato = metadati[m];
              HashMap<String, Object> hMap = new HashMap<String, Object>();
              String nomeMetaDato = metadato.getNome();
              hMap.put("nomeMetadato", nomeMetaDato);
              String valoreMetaDato = metadato.getValore();
              hMap.put("valoreMetadato", valoreMetaDato);
              hMapDATIPERSONALIZZATI.add(hMap);
            }

            result.put("iTotalRecordsDATIPERSONALIZZATI", totalDATIPERSONALIZZATI);
            result.put("iTotalDisplayRecordsDATIPERSONALIZZATI", totalDATIPERSONALIZZATI);
            result.put("dataDATIPERSONALIZZATI", hMapDATIPERSONALIZZATI);

          }

        }else{

          for (int a = 0; a < totalRDA; a++) {
            HashMap<String, Object> hMap = new HashMap<String, Object>();

            WSERPRdaType rdaSingle = rdaArray[a];
            if(rdaSingle.getCodiceRda()!=null){
              hMap.put("codiceRda", rdaSingle.getCodiceRda());
              hMap.put("posizioneRda", rdaSingle.getPosizioneRda());
              if("UGOVPA".equals(tipoWSERP)){
                hMap.put("codiceRda", rdaSingle.getRichiedente());
                hMap.put("posizioneRda", rdaSingle.getOggetto());
              }
            }else{
              hMap.put("codiceRda", null);
              hMap.put("posizioneRda", null);
            }

            hMap.put("oggettoRda", rdaSingle.getOggetto());
            hMap.put("descrizioneRda", rdaSingle.getDescrizione());
            hMap.put("idLotto", rdaSingle.getIdLotto());
            hMap.put("qta", rdaSingle.getQuantita());
            hMap.put("um", rdaSingle.getUm());
            hMap.put("valStimato", rdaSingle.getValoreStimato());
            if(rdaSingle.getGruppoAcquisti()!=null){
              hMap.put("gruppoAcquisti", rdaSingle.getGruppoAcquisti());
            }else{
              hMap.put("gruppoAcquisti", null);
            }
            if("AVM".equals(tipoWSERP)){
              //hMap.put("gruppoAcquisti", rdaArray[a].getGruppoAcquisti());
              hMap.put("divisione", rdaArray[a].getDivisione());
              hMap.put("codiceMateriale", rdaArray[a].getCodiceMateriale());
              hMap.put("richiedente", rdaArray[a].getRichiedente());
              Calendar dcCal = rdaSingle.getDataConsegna();
              if(dcCal!= null && dcCal.getTime()!=null){
                Date dD = dcCal.getTime();
                String dconsStr = UtilityDate.convertiData(dD, UtilityDate.FORMATO_GG_MM_AAAA_CON_TRATTINI);
                hMap.put("dataConsegna", dconsStr);
              }else{
                hMap.put("dataConsegna", null);
              }
              hMap.put("luogoConsegna", rdaArray[a].getLuogoConsegna());
            }else{
              //hMap.put("gruppoAcquisti", null);
              if("FNM".equals(tipoWSERP)){
                hMap.put("divisione", rdaSingle.getEsercizio());
                hMap.put("codiceMateriale", rdaSingle.getCodiceMateriale());
              }else{
                hMap.put("divisione", null);
                hMap.put("codiceMateriale", null);
              }


              hMap.put("richiedente", null);
              hMap.put("dataConsegna", null);
              hMap.put("luogoConsegna", null);
            }

            hMap.put("checkRda", null);
            hMapRDA.add(hMap);

          }//for


        }

      }
    }

    if (hMapRDA != null &&hMapRDA.size() > 0) {
      totalRDA = hMapRDA.size();
      totalAfterFilterRDA = hMapRDA.size();
    }


    result.put("iTotalRecords", totalRDA);
    result.put("iTotalDisplayRecords", totalAfterFilterRDA);
    result.put("data", hMapRDA);

    out.print(result);
    out.flush();

    return null;

  }
}


package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPMetadatoType;
import it.maggioli.eldasoft.ws.erp.WSERPPosizioneRdaType;
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
    String codice = request.getParameter("codice");
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
    String tipoGara = request.getParameter("tipoGara");
    String modo = request.getParameter("modo");
    String filtroLotto = request.getParameter("filtroLotto");

    JSONObject result = new JSONObject();
    int totalRDA = 0;
    int totalAfterFilterRDA = 0;
    int totalDATIPERSONALIZZATI = 0;
    List<HashMap<String, Object>> hMapRDA = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapPosizioni = new ArrayList<>();
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
        erpSearch.setUfficioIntestatario(uffint);
      }
      if("RAIWAY".equals(tipoWSERP)){
        if(tipoGara != null){
          erpSearch.setDivisioneInLotti(!tipoGara.equals("garaLottoUnico"));
        }
        if(codiceRda != null){
          erpSearch.setCodiceRda(codiceRda);
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

            //RAIWAY columns
            if("RAIWAY".equals(tipoWSERP)){
              if(!"posRda".equals(modo)){
                  if(rdaSingle.getPosizioneRdaArray() != null){
                    hMap.put("nrPos", rdaSingle.getPosizioneRdaArray().length);
                  } else {
                    hMap.put("nrPos", null);
                  }
                  if(rdaSingle.getLottoArray() != null){
                    hMap.put("nrLotti", rdaSingle.getLottoArray().length);
                  } else {
                    hMap.put("nrLotti", null);
                  }
                  if(rdaSingle.getTecniciArray() != null){
                    hMap.put("rdp", rdaSingle.getTecniciArray(0).getDenominazione());
                  } else {
                    hMap.put("rdp", null);
                  }
                  hMap.put("totale", rdaSingle.getValoreStimato());
              } else{
                if(codiceRda != null && !"".equals(codiceRda)){
                  WSERPPosizioneRdaType[] posArray = rdaSingle.getPosizioneRdaArray();
//              	if("true".equals(filtroLotto)) {
                  String idLotto = gestioneWSERPManager.getCodiga(codiceGara, codice);
                  if(idLotto != null){
                    List<WSERPPosizioneRdaType> filteredPosizioneRdaList = new ArrayList<WSERPPosizioneRdaType>();
                    for (int p = 0; p < posArray.length; p++) {
                      WSERPPosizioneRdaType pos = posArray[p];
                      if(Long.valueOf(idLotto).equals(pos.getIdLotto())) {
                        filteredPosizioneRdaList.add(pos);
                      }
                    }
                    WSERPPosizioneRdaType[] filteredPosizioneRdaArray = new WSERPPosizioneRdaType[filteredPosizioneRdaList.size()];
                    for (int p = 0; p < filteredPosizioneRdaList.size(); p++) {
                      filteredPosizioneRdaArray[p]= filteredPosizioneRdaList.get(p);
                    }
                    posArray = filteredPosizioneRdaArray;
                  }
//              	}

                  for(WSERPPosizioneRdaType elem : posArray){
                    if(gestioneWSERPManager.verificaPresenzaPosizioneRda(null, codiceGara, codice, codiceRda, elem.getPosizioneRiferimento()) != 1){
                      HashMap<String, Object> hMapPos = new HashMap<>();
                      hMapPos.put("numPos", elem.getPosizioneRiferimento());
                      hMapPos.put("oggetto", elem.getDescrizioneEstesa());
                      hMapPos.put("totale", elem.getPrezzoPrevisto() * elem.getQuantita());
                      hMapPos.put("dettagli", "Link dettaglio rda");
                      hMapPosizioni.add(hMapPos);
                    }
                  }
                }
              }
            }else {
            	hMap.put("totale", null);
            	hMap.put("nrPos", null);
            	hMap.put("nrLotti", null);
            	hMap.put("rdp", null);
            }
            hMap.put("checkRda", null);
            String numRda = gestioneWSERPManager.verificaRdaAssociata(rdaSingle.getCodiceRda());
            if("RAIWAY".equals(tipoWSERP)){
              if(!numRda.equals(rdaSingle.getCodiceRda())){
                hMapRDA.add(hMap);
              }
            }else {
              hMapRDA.add(hMap);
            }
          }
        }
      }
    }

    if (hMapRDA != null &&hMapRDA.size() > 0) {
      totalRDA = hMapRDA.size();
      totalAfterFilterRDA = hMapRDA.size();
    }


    result.put("iTotalRecords", totalRDA);
    result.put("iTotalDisplayRecords", totalAfterFilterRDA);
    if(!"posRda".equals(modo)){
      result.put("data", hMapRDA);
    } else{
      result.put("data", hMapPosizioni);
    }
    out.print(result);
    out.flush();

    return null;

  }
}


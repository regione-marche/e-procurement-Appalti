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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    
    List<String> listaFiltroRda = new ArrayList<String>();

    JSONObject result = new JSONObject();
    int totalRDA = 0;
    int totalAfterFilterRDA = 0;
    int totalDATIPERSONALIZZATI = 0;
    List<HashMap<String, Object>> hMapRDA = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapPosizioni = new ArrayList<>();
    List<HashMap<String, Object>> hMapDATIPERSONALIZZATI = new ArrayList<HashMap<String, Object>>();
    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
    
    List<WSERPRdaResType> risultatoElaborato = new ArrayList<WSERPRdaResType>();
    Map<String, Object> risElaborazionePosizioniINARCASSA = new HashMap<>();

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
      if("CONI".equals(tipoWSERP)){
        if(!"".equals(codiceRda)){
          erpSearch.setCodiceRda(codiceRda);
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
      
      if("INARCASSA".equals(tipoWSERP)){
          if(tipoGara != null){
            erpSearch.setDivisioneInLotti(!tipoGara.equals("garaLottoUnico"));
          }//if                    
      }//if      

      // qui inizia la gestione..
      
      if(     "INARCASSA".equals(tipoWSERP) 
    	   && "posRda".equals(modo) 
      ){
    	  // recupero dal db quali sono le rda associate alla gara
          listaFiltroRda = gestioneWSERPManager.recuperaRdaAssociateAllaGara(codiceGara,codice,filtroLotto);
          
    	  // per ogni rda associata alla gara recupero le posizioni chiamando il servizio
    	  boolean elaborazioneIsOk = true;
    	  for (String _codiceRda : listaFiltroRda) {
        	  erpSearch.setCodiceRda(_codiceRda);
        	  wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch );
        	  if ( wserpRdaRes.isEsito() ) {
        		  // se l'interrogazione del servizio e'andata benee aggiungo le info restituite 
        		  // alla lista delle rda da elaborare...
        		  risultatoElaborato.add(wserpRdaRes);
        	  } else {
        		  // se c'e' stato un errore nella comunicazione con il servizio esci immediatamente
        		  elaborazioneIsOk = false;
        		  break;
        	  }//if        	  
		  }//for 
    	  
    	 
    	  if (elaborazioneIsOk) {
    		  // se non ho trovato errore nel recuperare le rda allora le elaboro..
        	  for (WSERPRdaResType rdaInfo: risultatoElaborato){
        		  risElaborazionePosizioniINARCASSA =  elaboraPosizioneINARCASSA(codiceGara, codice, rdaInfo);
            	  totalRDA += (int) risElaborazionePosizioniINARCASSA.get("totalRDA");
              	  totalAfterFilterRDA += (int) risElaborazionePosizioniINARCASSA.get("totalAfterFilterRDA");
              	  List<HashMap<String, Object>> hMapPosizioniRda = (List<HashMap<String, Object>>) risElaborazionePosizioniINARCASSA.get("hMapPosizioni");
              	  hMapPosizioni.addAll(hMapPosizioniRda);
        	  }//for        	  
        	  
        	  result.put("esito", true);
        	  result.put("messaggio", "");
    	  } else {
    		  // se ho trovato un errore devo fermarmi riportando l'errore..
    		  // nb: mi fermo alla prima errata che trovo
    		  result.put("esito", false);
    		  result.put("messaggio", wserpRdaRes.getMessaggio());
    	  }//if 
    	  
    	  
      } else {
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
		            if(    "RAIWAY".equals(tipoWSERP) 
		                || "INARCASSA".equals(tipoWSERP)   // TODO:ROS:moentanea.. poi diventera' un ramo...
		            ){
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
		                  
		                  if(rdaSingle.getDivisione() != null){
		                    hMap.put("divisione", rdaSingle.getDivisione());
		                  } else {
		                    hMap.put("divisione", null);
		                  }
		                  
		                  hMap.put("totale", rdaSingle.getValoreStimato());
		                  
		              } else{
		            	// qui vengono gestite le posizioni per RAIWAY.Per INARCASSA segue il blocco sopra 
		            	  
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
		                      }//if
		                    }//for
		                    WSERPPosizioneRdaType[] filteredPosizioneRdaArray = new WSERPPosizioneRdaType[filteredPosizioneRdaList.size()];
		                    for (int p = 0; p < filteredPosizioneRdaList.size(); p++) {
		                      filteredPosizioneRdaArray[p]= filteredPosizioneRdaList.get(p);
		                    }//for
		                    posArray = filteredPosizioneRdaArray;
		                  }//if if(idLotto != null)
		//              	}
		
		                  for(WSERPPosizioneRdaType elem : posArray){
		                    if(gestioneWSERPManager.verificaPresenzaPosizioneRda(null, codiceGara, codice, codiceRda, elem.getPosizioneRiferimento()) != 1){
		                      HashMap<String, Object> hMapPos = new HashMap<>();
		                      
		                      hMapPos.put("numPos", elem.getPosizioneRiferimento());
		                      hMapPos.put("oggetto", elem.getDescrizioneEstesa());
		                      hMapPos.put("totale", elem.getPrezzoPrevisto() * elem.getQuantita());
		                      hMapPos.put("dettagli", "Link dettaglio rda");
		                      
		                      if ("INARCASSA".equals(tipoWSERP)) {
		                    	  hMapPos.put("codiceRda", rdaSingle.getCodiceRda());
		                    	  hMapPos.put("prezzo", elem.getPrezzoPrevisto());
		                    	  hMapPos.put("qta", elem.getQuantita());
		                    	  hMapPos.put("udm", elem.getUm());
		                      }//if
		                      
		                      hMapPosizioni.add(hMapPos);
		                    }//if
		                  }//for
		                  
		                }//if 
		              }// if caso RAIWAY e INARCASSA
		            } else if("CONI".equals(tipoWSERP)){
                      hMap.put("divisione", rdaSingle.getDivisione());
                      hMap.put("totale", rdaSingle.getValoreStimato());
                      hMap.put("nrPos", null);
                      hMap.put("nrLotti", null);
                      hMap.put("rdp", null);
                    } else {
		            	hMap.put("totale", null);
		            	hMap.put("nrPos", null);
		            	hMap.put("nrLotti", null);
		            	hMap.put("rdp", null);
		            }//if 
		            
		            hMap.put("checkRda", null);
		            String numRda = gestioneWSERPManager.verificaRdaAssociata(rdaSingle.getCodiceRda());
		            if("RAIWAY".equals(tipoWSERP)){
		              if(!numRda.equals(rdaSingle.getCodiceRda())){
		                hMapRDA.add(hMap);
		              }//if
		            }else {
		              hMapRDA.add(hMap);
		            }//if
		            
		          }//for
		        }// else di if("FNM".equals(tipoWSERP) && codiceRda!= null && divisione!= null)
		      }//if (wserpRdaRes.getRdaArray()!= null)
		    }//if (wserpRdaRes.isEsito())
		
		    if (hMapRDA != null &&hMapRDA.size() > 0) {
		      totalRDA = hMapRDA.size();
		      totalAfterFilterRDA = hMapRDA.size();
		    }
    }//if 
      
    result.put("iTotalRecords", totalRDA);
    result.put("iTotalDisplayRecords", totalAfterFilterRDA);
    if(!"posRda".equals(modo)){
      result.put("data", hMapRDA);
    } else{
      result.put("data", hMapPosizioni);
    }//if
    out.print(result);
    out.flush();

    return null;

  }

  private Map<String, Object> elaboraPosizioneINARCASSA(String codiceGara, String codice, WSERPRdaResType rdaInfo) {
 
	Map<String, Object> res = new HashMap<>();
	List<HashMap<String, Object>> hMapPosizioni = new ArrayList<>();
	int _totalRDA = 0;
	int totalAfterFilterRDA = 0;
	 
    if (rdaInfo.getRdaArray()!= null) {
        WSERPRdaType[] rdaArray = rdaInfo.getRdaArray();
        int totalRDA = rdaArray.length;
        _totalRDA += totalRDA;

        for (int a = 0; a < totalRDA; a++) {

         	WSERPRdaType rdaSingle = rdaArray[a];
         	String codiceRda = rdaSingle.getCodiceRda();
  	  
			if(codiceRda != null && !"".equals(codiceRda)){
			  WSERPPosizioneRdaType[] posArray = rdaSingle.getPosizioneRdaArray();
			  String idLotto = gestioneWSERPManager.getCodiga(codiceGara, codice);
			  if(idLotto != null){
				  
				List<WSERPPosizioneRdaType> filteredPosizioneRdaList = new ArrayList<WSERPPosizioneRdaType>();
				for (int p = 0; p < posArray.length; p++) {
				  WSERPPosizioneRdaType pos = posArray[p];
				  //if(Long.valueOf(idLotto).equals(pos.getIdLotto())) {
					filteredPosizioneRdaList.add(pos);
				  //}//if
				}//for
				
				WSERPPosizioneRdaType[] filteredPosizioneRdaArray = new WSERPPosizioneRdaType[filteredPosizioneRdaList.size()];
				totalAfterFilterRDA += filteredPosizioneRdaList.size();
				for (int p = 0; p < filteredPosizioneRdaList.size(); p++) {
				  filteredPosizioneRdaArray[p]= filteredPosizioneRdaList.get(p);
				}//for
				
				posArray = filteredPosizioneRdaArray;
			  }//if
	
			  for(WSERPPosizioneRdaType elem : posArray){
				// TODO:ROS: per ilmomento commentiamo la parte di verifica perche' la richiesta del cliente 
				// e' diversa...
				// if(gestioneWSERPManager.verificaPresenzaPosizioneRda(null, codiceGara, codice, codiceRda, elem.getPosizioneRiferimento()) != 1){
				  HashMap<String, Object> hMapPos = new HashMap<>();
				  
				  hMapPos.put("numPos",  elem.getPosizioneRiferimento());
				  hMapPos.put("oggetto", elem.getDescrizioneEstesa());
				  hMapPos.put("totale",  elem.getPrezzoPrevisto() * elem.getQuantita());
				  hMapPos.put("dettagli", "Link dettaglio rda");
				  hMapPos.put("codiceRda", rdaSingle.getCodiceRda());
				  hMapPos.put("prezzo",  elem.getPrezzoPrevisto());
				  hMapPos.put("qta",     elem.getQuantita());
				  hMapPos.put("udm",     elem.getUm());
				  
				  hMapPosizioni.add(hMapPos);
				//}//if
			  }//for
			  
			}//if 			

        }//for
	  
	
    }//if (rdaInfo.getRdaArray()!= null)
	      
	  
	 res.put("hMapPosizioni", hMapPosizioni);
	 res.put("totalRDA", _totalRDA);
	 res.put("totalAfterFilterRDA", totalAfterFilterRDA);
	 
	 return res;
  }//elaboraPosizioneINARCASSA

  
}//class GetWSERPListaRdaAction


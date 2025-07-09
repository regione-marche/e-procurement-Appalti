package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPPosizioneRdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;
import net.sf.json.JSONObject;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SetWSERPPosRdaInGaraAction extends Action {

    private GestioneWSERPManager gestioneWSERPManager;

    public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
        this.gestioneWSERPManager = gestioneWSERPManager;
    }

    @Override public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        DataSourceTransactionManagerBase.setRequest(request);

        response.setHeader("cache-control", "no-cache");
        response.setContentType("text/text;charset=utf-8");
        PrintWriter out = response.getWriter();

        JSONObject result = new JSONObject();

        String servizio = request.getParameter("servizio");
        if(servizio==null || "".equals(servizio)){
            servizio ="WSERP";
        }
        String codiceGara = request.getParameter("codgar");
        String codiceRda = request.getParameter("codiceRda");
        String codice = request.getParameter("codice");
        String arrmultikey = request.getParameter("arrmultikey");
        arrmultikey = UtilityStringhe.convertiNullInStringaVuota(arrmultikey);
        String linkrda = request.getParameter("linkrda");
        linkrda = UtilityStringhe.convertiNullInStringaVuota(linkrda);
        String uffint = request.getParameter("uffint");
        ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        Long syscon = new Long(profilo.getId());
        String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

        String username = credenziali[0];
        String password = credenziali[1];

        WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi(servizio);
        String tipoWSERP = configurazione.getRemotewserp();
        
        switch(tipoWSERP) {
	        case "RAIWAY":
		        {
		            WSERPRdaType erpSearch= new WSERPRdaType();
		            erpSearch.setCodiceRda(codiceRda);
		            WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch);
		            if(!wserpRdaRes.isEsito()){
		                throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
		                    "wserp.erp.associarda.remote.error", null);
		            }else{
		                WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
		                if(!"".equals(arrmultikey)){
		                    String[] multikey = arrmultikey.split(";");
		                    int index = 0;
		                    WSERPPosizioneRdaType[] posizioneArray = new WSERPPosizioneRdaType[multikey.length];
		                    for (String mkey : multikey) {
		                        boolean found = false;
		                        for (int i = 0; i < rdaArray[0].getPosizioneRdaArray().length && !found ; i++) {
		                            if (rdaArray[0].getPosizioneRdaArray(i).getPosizioneRiferimento().equals(mkey)){
		                                posizioneArray[index] = rdaArray[0].getPosizioneRdaArray(i);
		                                found = true;
		                                index ++;
		                            }
		                        }
		                    }
		                    rdaArray[0].setPosizioneRdaArray(posizioneArray);
		                    this.gestioneWSERPManager.insCollegamentoRda(request, username, password, servizio, codiceGara, codice, rdaArray, linkrda, uffint);
		                    result.put("Esito", "0");
		                }
		            }
		        }; break;
	        case "INARCASSA": {
	        	    this.gestioneWSERPManager.eliminaAssociazionePosizioni(codice);
	        	    
	        		String[] multikey = arrmultikey.split(";");
	        		
	        		// dai checkbox selezionati estrai codiceRda e posizioneRiferimento
	        		// e per ogni Rda ricava le posizioni e li inserisco in una mappa 
	        		// ( devo strutturare il dato partendo da un dato "piatto" )
	        	    Map<String, Map<String, Boolean>> mappaRdaPosizioni = new HashMap<>(); 

	        		for (String mkeys : multikey) {
	        		    String[] info = mkeys.split("-");
	        		    String codRda = info[0];
	        		    String posRiferimento = info[1];
	        		    
	        		    if (mappaRdaPosizioni.containsKey(codRda)) {
	        		    	// se ho inserito gia' una rda allora devo inserire questa posizione
	        		    	Map<String, Boolean> posizioneHash = mappaRdaPosizioni.get(codRda);
	        		    	posizioneHash.put(posRiferimento, true);
	        		    } else {
	        		    	// non c'e' questa rda quindi la devo inserire nella mappa con al relativa posizione
	        		    	Map<String,Boolean> posizioneHash = new HashMap<>();
	        		    	posizioneHash.put(posRiferimento, true);
	        		    	
	        		    	mappaRdaPosizioni.put(codRda, posizioneHash);	        		    	
	        		    }//if
	        		       
	        		}//for
	        		
	        		// per ogni rda chiamo il servizio per ottenere le info su rda e relative posizioni	        			        	
	        		List<WSERPRdaResType> listaInfoRda = new ArrayList<WSERPRdaResType>();
	        		List<WSERPRdaResType> listaRdaKo = new ArrayList<WSERPRdaResType>();
	        		
	        		WSERPRdaType erpSearch= new WSERPRdaType();
	        		Iterator<String> chiaviRda = mappaRdaPosizioni.keySet().iterator();
	        		while(chiaviRda.hasNext()) {
	        			
		        		// per ogni rda eseguo la chiamate e filtro le posizioni in base a quelle che sono state 
		        		// selezionate	        	        	
	        				        		    
	        		    // imposto l'Rda di cui voglio ottenere le info
	        			String codRda = chiaviRda.next();
	        		    erpSearch.setCodiceRda(codRda);
	        		    WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch);
	        		    
	        		    if(wserpRdaRes.isEsito()){
	        		    	listaInfoRda.add(wserpRdaRes);
	        		    } else {
	        		       // gestione dell'errore => dobbiamo annullare tutte le rda associate
	        		    	listaRdaKo.add(wserpRdaRes);
	        		    }//if
	        		    	        		    	        		    
	        		}//while
	        		
	        		// se ci sono stati errori lo segnalo e non registro nulla
	        		// altrimenti registro le informazioni
	        		if (listaRdaKo.isEmpty()) {
	        			// significa che le chiamate sono andate tutte a buon fine e ho, quindi, ho recuperato
	        			// i dati di tutte le rda / posizioni coinvolte
	        			// Ora bisogna filtrare le posizioni che abbiamo tenendo solo quelle che sono state selezionate
	        			// ( quelle presenti nella mappaRdaPosizioni ) 
	        			
	        			for (Iterator iterator = listaInfoRda.iterator(); iterator.hasNext();) {
	        				// estraggo rda
							WSERPRdaResType wserpRdaResType = (WSERPRdaResType) iterator.next();
							WSERPRdaType[] rdaArray = wserpRdaResType.getRdaArray();
							WSERPRdaType rda = rdaArray[0];
							
							// filtro le posizioni di queste rda con quelle che sono state selezionate nella tabella
							List<WSERPPosizioneRdaType> listaPosizioniFiltrate = new ArrayList<WSERPPosizioneRdaType>();
							Map<String,Boolean> posizioneHash = mappaRdaPosizioni.get(rda.getCodiceRda());
							for( WSERPPosizioneRdaType posizione: rda.getPosizioneRdaArray() ) {								
								if (posizioneHash.containsKey(posizione.getPosizioneRiferimento())) {
									listaPosizioniFiltrate.add(posizione);
								}//if
							}//for
							
							// sovrascrivo le posizioni con quelle filtrate
							// nota: questo è necessario per il funzionamento della insCollegamentoRda
							WSERPPosizioneRdaType[] newPosizioneRdaArray = listaPosizioniFiltrate.toArray(new WSERPPosizioneRdaType[0]);
							rda.setPosizioneRdaArray(newPosizioneRdaArray);
							
							// effettuo il collegamento a db...
							this.gestioneWSERPManager.insCollegamentoRda(request, username, password, servizio, codiceGara, codice, rdaArray, linkrda, uffint);
		                    result.put("Esito", "0");
						}//for
	        			
	        		} else {
	        			// significa che ci sono delle chiamate che non sono andate bene => riporto eccezione
	        			// TODO:ROS: da vedere cosa riportare in questo caso
	        		}//if
	        		
	             }; break;
    	}//switch;
	      
        out.print(result);
        out.flush();
        return null;
    }
}

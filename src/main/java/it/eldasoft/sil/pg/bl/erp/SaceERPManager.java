/*
 * Created on 19/03/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.erp;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class SaceERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(SaceERPManager.class);

  private SqlManager          sqlManager;

  /** Manager per la gestione delle chiavi di una entita */
  private GenChiaviManager   genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }
  
  public static final String APPALTI_MS_SACE_WS_BASEURL = "sace.ws.baseUrl";
  public static final String APPALTI_MS_SACE_WS_CODICEFONTE = "sace.ws.codiceFonte";

  
  public void invioCodiceImpresa(String codiceImpresa, String codiceFiscaleImpresa, Long nazioneImpresa, String ragSociale, String indirizzoImpresa, String capImpresa, Long idFlusso, Long statoFlusso) throws Exception {
    String url = ConfigManager.getValore(APPALTI_MS_SACE_WS_BASEURL);
    String codiceFonte = ConfigManager.getValore(APPALTI_MS_SACE_WS_CODICEFONTE);
    if (url == null || "".equals(url)) {
      throw new GestoreException("L'indirizzo per la connessione al web service non e' definito", "");
    }
    if (url.endsWith("/"))
      url = url.substring(0, url.length() - 1);

    url = url + "/soggettoInfo";

    HttpURLConnection conn = null;
    try {
    	
	    conn = (HttpURLConnection) new URL(url).openConnection();
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/json");
	    conn.setDoOutput(true);
	
	    JSONObject jsonInput = new JSONObject();
	    //Attualmente risultano uguali gli oggetti in input alla chiamata (per revisioni di SACE)
	    if(Long.valueOf(1).equals(nazioneImpresa) && !Long.valueOf(10).equals(statoFlusso)) {
	      jsonInput.put("codiceFonte", Long.valueOf(codiceFonte));
	      jsonInput.put("codiceFiscale", codiceFiscaleImpresa);
	      jsonInput.put("codiceRiferimentoFonte", codiceImpresa);
	    } else {
	      jsonInput.put("codiceFonte", Long.valueOf(codiceFonte));
	      jsonInput.put("codiceFiscale",null);
	      jsonInput.put("idNum",  codiceFiscaleImpresa);
	      jsonInput.put("codicePaese", nazioneImpresa);
	      jsonInput.put("ragioneSociale", ragSociale);
	      jsonInput.put("indirizzo", indirizzoImpresa);
	      capImpresa = StringUtils.stripToEmpty(capImpresa);
	      if(!"".equals(capImpresa)) {
	    	  jsonInput.put("zipCode", capImpresa);  
	      }else {
	    	  jsonInput.put("zipCode", "blank");
	      }
	      jsonInput.put("codiceRiferimentoFonte", codiceImpresa);
	    }
	    
	    try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
	      wr.writeBytes(jsonInput.toString());
	      wr.flush();
	    }
	    
	    StringBuilder response;
	    String messaggioErrore = null;
	    JSONObject res = null;
	    if (conn.getResponseCode() == 200) {
	      try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
	        String inputLine;
	        response = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	      }
	      res = JSONObject.fromObject(response.toString());
	      if(res != null) {
	        if(res.has("transactionResult")) {
	          if(res.getJSONObject("transactionResult").has("errorCode")) {
	            messaggioErrore = res.getJSONObject("transactionResult").getString("errorDescription");
	            Timestamp datainvio = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
	            //imposto lo stato a 2 che significa che e' terminata con successo, 
	            //  perche' e' un vero errore solo se errorCode e' diverso da zero
	            //  quindi se lo errorCode e' zero un possibile messaggio che arriva e' che e' stato
	            //  ricevuto il flusso con successo ed e' stato preso in carico la creazione dell'anagrafica
	            //  quindi in questo caso imposto lo stato a 2 per evitare che viene rieseguito il flusso e
	            //  traccio il messaggio nella tabella g1msflussi.note come se fosse un errore
	            Long stato = Long.valueOf(2);
	            if(res.getJSONObject("transactionResult").getInt("errorCode") != 0) {
	              stato = Long.valueOf(3);
	              if(Long.valueOf(1).equals(nazioneImpresa) && !Long.valueOf(10).equals(statoFlusso)) {
	              	stato = Long.valueOf(2);
	              	//inserisco un flusso che rieseguira questa funziona utilizzando l'oggetto estero 
	              	Timestamp datains = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
	                  Long idG1msflussi = Long.valueOf(genChiaviManager.getNextId("G1MSFLUSSI"));
	                  String inserimentoFlusso = "INSERT INTO g1msflussi( "
	                      + " id, entita, chiave_int, chiave_ext, fonte, tipo_flusso, data_invio, data_richiesta, data_elab, stato, note) "
	                      + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	                  this.sqlManager.update(inserimentoFlusso, new Object[] { idG1msflussi, "IMPR", codiceImpresa, null, "MAG", Long.valueOf(1), null, datains, null, Long.valueOf(10), null });
	              }
	            }
	            this.sqlManager.update("update g1msflussi set data_invio = ?, stato = ?, note = ? where id = ? ", new Object[] { datainvio, stato, messaggioErrore, idFlusso } );
	          } else {
	            //update su impr se cgenimp c'è nella risposta
	            if(res.has("codiceSoggetto") && res.getString("codiceSoggetto") != null && !"".equals(res.getString("codiceSoggetto")) 
	                && !"0".equals(res.getString("codiceSoggetto"))) {
	              
	              //aggiorno l'impresa con il codice michelangelo
	              String updImpr = "update IMPR set cgenimp = ? where codimp = ?";
	              this.sqlManager.update(updImpr, new Object[]{res.getString("codiceSoggetto"), codiceImpresa});
	              
	              //Controllo se c'e' la chiave esterna perche' se non c'e' inserisco un flusso di aggiornamento
	              //caso in cui la registrazione è stata fatta manualmente e non tramite il servizio di SACE
	              String controlloChiaveEsternaSeGiaPresente = "select chiave_ext from g1msflussi where id = ?";
	              String chiaveEsterna = (String) this.sqlManager.getObject(controlloChiaveEsternaSeGiaPresente, new Object[] {idFlusso});
	              if(chiaveEsterna == null || chiaveEsterna.isEmpty()) {
	                Timestamp datains = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
	                Long idG1msflussi = Long.valueOf(genChiaviManager.getNextId("G1MSFLUSSI"));
	                String inserimentoFlusso = "INSERT INTO g1msflussi( "
	                    + " id, entita, chiave_int, chiave_ext, fonte, tipo_flusso, data_invio, data_richiesta, data_elab, stato, note) "
	                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	                this.sqlManager.update(inserimentoFlusso, new Object[] { idG1msflussi, "IMPR", codiceImpresa, res.getString("codiceSoggetto"), "MAG", Long.valueOf(2), null, datains, null, Long.valueOf(1), null });
	              }
	              this.sqlManager.update("update g1msflussi set chiave_ext = ? where id = ? and chiave_ext is null", new Object[] { res.getString("codiceSoggetto"), idFlusso } );
	            }
	            //invio riuscito aggiorno la tabella flussi
	            Timestamp datainvio = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
	            this.sqlManager.update("update g1msflussi set data_invio = ?, stato = ? where id = ? ", new Object[] { datainvio, Long.valueOf(2), idFlusso } );
	          }
	        }
	      }
	    } else {
	      logger.error("La richiesta ha restituito un codice di stato inaspettato: " + conn.getResponseCode());
	    }

    }finally {
    	if(conn!=null) {
    		conn.disconnect();
    	}	
    }

  }

  public void aggiornamentoDatiImpresa(String codiceEsterno, Long idFlusso) throws Exception {
    String url = ConfigManager.getValore(APPALTI_MS_SACE_WS_BASEURL);
    if (url == null || "".equals(url)) {
      throw new GestoreException("L'indirizzo per la connessione al web service non e' definito", "");
    }
    if (url.endsWith("/"))
      url = url.substring(0, url.length() - 1);

    url = url + "/company/"+codiceEsterno;
    
    HttpURLConnection conn = null;
    
    try {

	    conn = (HttpURLConnection) new URL(url).openConnection();
	
	    conn.setRequestMethod("GET");
	    conn.setRequestProperty("Content-Type", "application/json");
	    
	    String messaggioErrore = null;
	    JSONObject res = null;
	    BufferedReader reader = null;
	    if (conn.getResponseCode() == 200) {
	      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      StringBuilder response = new StringBuilder();
	      String line;
	      while ((line = reader.readLine()) != null) {
	          response.append(line);
	      }
	      res = JSONObject.fromObject(response.toString());
	      if(res != null) {
	        if(res.has("Header")) {
	          if(!res.getJSONObject("Header").getBoolean("IsError")) {
	            if(res.has("Company")) {
	              JSONObject datiImpresa = res.getJSONObject("Company");
	              String tipoIndirizzo = null;
	              String indirizzo = null;
	              String provincia = null;
	              //String regione = null;
	              String citta = null;
	              //String distretto = null;
	              String cap = null;
	              String paese = null;
	              //Long codicePaese = null;
	              String codiceMaster = null;
	              String ragioneSociale = null;
	              String piva = null;
	              String siglaFormaLegale = null;
	              String descrizioneFormaLegale = null;
	              String telefono = null;
	              String fax = null;
	              String ateco = null;
	              String cameraDiCommercio = null;
	              String numIscrCciaa = null;
	              String dataAggiornamento = null;
	              String tipoSocieta = null;
	              String pec = null;
	              if(datiImpresa.has("Address")) {
	                JSONObject indirizzoImpresa = datiImpresa.getJSONObject("Address");
	                tipoIndirizzo = getvalueOrNull(indirizzoImpresa,"AddressType");
	                indirizzo = getvalueOrNull(indirizzoImpresa,"StreetName");
	                provincia = getvalueOrNull(indirizzoImpresa,"Province");
	                //regione = getvalueOrNull(indirizzoImpresa,"Region");
	                citta = getvalueOrNull(indirizzoImpresa,"City");
	                //distretto = getvalueOrNull(indirizzoImpresa,"District");
	                cap = getvalueOrNull(indirizzoImpresa,"Zip");
	                paese = getvalueOrNull(indirizzoImpresa,"Country");
	                //codicePaese = getvalueOrNull(indirizzoImpresa.getLong("CodPaese");
	              }
	              if(datiImpresa.has("GeneralData")) {
	                JSONObject datiGeneraliImpresa = datiImpresa.getJSONObject("GeneralData");
	                codiceMaster = getvalueOrNull(datiGeneraliImpresa,"SaceGroupCode");
	                ragioneSociale = getvalueOrNull(datiGeneraliImpresa,"CompanyName");
	                paese = getvalueOrNull(datiGeneraliImpresa,"CompanyCountry");
	                piva = getvalueOrNull(datiGeneraliImpresa,"PIVA");
	                siglaFormaLegale = getvalueOrNull(datiGeneraliImpresa,"LegalForm");
	                descrizioneFormaLegale = getvalueOrNull(datiGeneraliImpresa,"LegalFormDescription");
	                telefono = getvalueOrNull(datiGeneraliImpresa,"Telephone");
	                fax = getvalueOrNull(datiGeneraliImpresa,"Fax");
	                ateco = getvalueOrNull(datiGeneraliImpresa,"TradeSector");
	                cameraDiCommercio = getvalueOrNull(datiGeneraliImpresa,"CCIAA");
	                numIscrCciaa = getvalueOrNull(datiGeneraliImpresa,"CCIAANumber");
	                dataAggiornamento = getvalueOrNull(datiGeneraliImpresa,"DateUpdated");
	                tipoSocieta = getvalueOrNull(datiGeneraliImpresa,"CompanyType");
	                pec = getvalueOrNull(datiGeneraliImpresa,"PEC_FE");
	              }
	              String updateImpr = "UPDATE impr SET cgenimp = ? ";
	              List<Object> lista = new ArrayList<>();
	              //ArrayList<Object> values = new ArrayList<Object>();
	              lista.add(codiceMaster);
	              
	              if(indirizzo != null) {
	                updateImpr += ", indimp = ? ";
	                lista.add(indirizzo);
	              }
	              if(provincia != null) {
	                updateImpr += ", proimp = ? ";
	                lista.add(provincia);
	              }
	              if(citta != null) {
	                updateImpr += ", locimp = ? ";
	                lista.add(citta);
	              }
	              if(cap != null) {
	                updateImpr += ", capimp = ? ";
	                lista.add(cap);
	              }
	              if(paese != null) {
	                Long cod = (Long) this.sqlManager.getObject("select codice from g_nazioni where codiso = ? ", new Object[]{paese});
	                if(cod != null) {
	                  paese = String.valueOf(cod);
	                  updateImpr += ", nazimp = ? ";
	                  lista.add(cod);
	                }
	              } else { //italia
	                updateImpr += ", nazimp = ? ";
	                lista.add(1);
	              }
	              if(ragioneSociale != null) {
	                updateImpr += ", nomest = ? ";
	                lista.add(ragioneSociale);
	                if(ragioneSociale.length()>61) {
	                	String ragioneSocialeCorta = ragioneSociale.substring(0,60);
	                    updateImpr += ", nomimp = ? ";
	                    lista.add(ragioneSocialeCorta);
	                }else {
	                    updateImpr += ", nomimp = ? ";
	                    lista.add(ragioneSociale);
	                }
	              }
	              if(piva != null) {
	                updateImpr += ", pivimp = ? ";
	                lista.add(piva);
	              }
	              if(telefono != null) {
	                updateImpr += ", telimp = ? ";
	                lista.add(telefono);
	              }
	              if(fax != null) {
	                updateImpr += ", faximp = ? ";
	                lista.add(fax);
	              }
	              if(cameraDiCommercio != null) {
	                String select = "select tabcod2 from tabsche where tabcod ='S2003' and tabcod1 = '07' and tabcod3 = ?";
	                cameraDiCommercio = (String) this.sqlManager.getObject(select, new Object[]{cameraDiCommercio});
	                updateImpr += ", pcciaa = ? ";
	                lista.add(cameraDiCommercio);
	              }
	              if(numIscrCciaa != null) {
	                updateImpr += ", ncciaa = ? ";
	                lista.add(numIscrCciaa);
	              }
	              if(siglaFormaLegale != null && !"0".equals(siglaFormaLegale)) {
	                if(!"PF".equals(siglaFormaLegale)) {
	                  String select = "select tab2d2 from tab2 where tab2cod = 'A1z28' and tab2d1 = ?";
	                  siglaFormaLegale = (String) this.sqlManager.getObject(select, new Object[]{siglaFormaLegale});
	                  updateImpr += ", tipimp = ?, natgiui = ? ";
	                  lista.add(siglaFormaLegale.split(",")[0]);
	                  lista.add(siglaFormaLegale.split(",")[1]);
	                } else {
	                  updateImpr += ", tipimp = ?, natgiui = ? ";
	                  lista.add("6");
	                  if(paese == null || "1".equals(paese)) {
	                    lista.add("10");
	                  } else {
	                    lista.add("16");                    
	                  }
	                }
	              }
	              if(ateco != null) {
	                String select = "select tab5tip from tab5 where tab5cod = 'G_j07' and tab5desc like ?";
	                ateco = (String) this.sqlManager.getObject(select, new Object[]{"%"+ateco+"%"});
	                updateImpr += ", codatt = ? ";
	                lista.add(ateco);
	              }
	              updateImpr += "where cgenimp = ? ";
	              lista.add(codiceMaster);
	              
	              Object[] oggetti = lista.toArray();
	              this.sqlManager.update(updateImpr, oggetti);
	              
	              //invio riuscito aggiorno la tabella flussi
	              Timestamp datainvio = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
	              this.sqlManager.update("update g1msflussi set data_elab = ?, stato = ? where id = ? ", new Object[] { datainvio, Long.valueOf(2), idFlusso } );
	            }
	          } else {
	            messaggioErrore = res.getJSONObject("Header").getString("Message");
	            Timestamp datainvio = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
	            this.sqlManager.update("update g1msflussi set data_invio = ?, stato = ?, note = ? where id = ? ", new Object[] { datainvio, Long.valueOf(3), messaggioErrore, idFlusso } );
	
	          }
	        }
	      }
	      
	    } else {
	      logger.error("La richiesta ha restituito un codice di stato inaspettato: " + conn.getResponseCode());
	    }
	
	    if (reader != null) {
	      reader.close();
	    }
    
    }finally {
    	if(conn!=null) {
    		conn.disconnect();		
    	}
    }
    
  }

  private String getvalueOrNull(JSONObject indirizzoImpresa, String campo) {
    String value = null;
    if(indirizzoImpresa.has(campo)) {
      value = indirizzoImpresa.getString(campo);
    }
    return value;
  }
  

 }



package it.eldasoft.sil.pg.bl;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * @author manuel.bridda
 *
 */
public class GestioneProgrammazioneManager {

  /** Logger */
  static Logger logger = Logger.getLogger(GestioneProgrammazioneManager.class);

  public static final String  PARAMS_CODAPP_NC = "codiceApplicativoNonCollegato";
  public static final String  RDA_COLLEGATE = "rdaCollegate";
  private static final String PARAMS_CFARRAY = "cfArray";
  private static final String PARAMS_CODRDA = "codRda";
  private static final String PARAMS_RUP = "rup";
  private static final String PARAMS_AMMINISTRAZIONE = "amministrazione";
  private static final String PARAMS_ARTICOLO = "descrizioneArticolo";
  private static final String PARAMS_STATO = "statoAppalto";
  private static final String PARAMS_CODEX = "codiceEsterno";
  private static final String PARAMS_CODAPP = "codiceApplicativo";
  private static final String PARAMS_CIG = "cig";
  private static final String APP = "Appalti";
  private static final String  PROP_PROGRAMMAZIONE_FILTRO_RUP = "programmazione.filtroRupCollab";
  private static final String  PROP_PROGRAMMAZIONE_WS_URL = "programmazione.ws.url";
  private static final String  PROP_PROGRAMMAZIONE_WS_USER = "programmazione.ws.username";
  private static final String  PROP_PROGRAMMAZIONE_WS_PASS = "programmazione.ws.password";
  private static final int    PROP_CLIENT_TIMEOUT = 10000;
  public static final String SQL_EXCEPTION = "Errore nell'aggiornamento della base dati";
  public static final String NOT_FOUND_EXCEPTION = "Integrazione programmazione RdA/RdI non disponibile";
  public static final String CRIPTAZIONE_EXCEPTION = "Errore nella decifratura della password impostata";
  public static final String GENERIC_EXCEPTION = "Errore nell'interazione con la programmazione";

  private SqlManager          sqlManager;
  private GenChiaviManager          genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public boolean isAttivaIntegrazioneProgrammazione() { 
    return StringUtils.stripToNull(ConfigManager.getValore(GestioneProgrammazioneManager.PROP_PROGRAMMAZIONE_WS_URL))!=null &&
        StringUtils.stripToNull(ConfigManager.getValore(GestioneProgrammazioneManager.PROP_PROGRAMMAZIONE_WS_USER))!=null &&
        StringUtils.stripToNull(ConfigManager.getValore(GestioneProgrammazioneManager.PROP_PROGRAMMAZIONE_WS_PASS))!=null;
  }

  private Map<String,String> getClientConfig() throws GestoreException{

    String rupFilter = ConfigManager.getValore(PROP_PROGRAMMAZIONE_FILTRO_RUP);
    String urlEndpoint = ConfigManager.getValore(PROP_PROGRAMMAZIONE_WS_URL);
    String username = ConfigManager.getValore(PROP_PROGRAMMAZIONE_WS_USER);
    String password = ConfigManager.getValore(PROP_PROGRAMMAZIONE_WS_PASS);
    String passwordDecoded = "";
    try {
      ICriptazioneByte icb = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          password.getBytes(), it.eldasoft.utils.sicurezza.ICriptazioneByte.FORMATO_DATO_CIFRATO);
      passwordDecoded = new String(icb.getDatoNonCifrato());
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Errore nella lettura della configurazione relativa a 'Integrazione programmazione'",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {CRIPTAZIONE_EXCEPTION}, e);
    }
    String credentials = username+":"+passwordDecoded;
    byte[] credEncoded = Base64.getEncoder().encode(credentials.getBytes());
    String authorization = "Basic "+new String(credEncoded);

    Map<String,String> map = new HashMap<String,String>();
    map.put("rupFilter", rupFilter);
    map.put("urlEndpoint", urlEndpoint);
    map.put("authorization",authorization);

    return map;   
  }

  /**
   * @param syscon
   * @param uffint
   * @param type
   * @return un oggetto JSONObject fornito dal WS
   * @throws GestoreException
   */
  public JSONObject consultaRda(Long syscon, String uffint, String type, String codrda, String rup, String amministrazioneDesc, String articoloDesc) throws GestoreException {
    Client client = ClientBuilder.newClient();
    client.property(ClientProperties.CONNECT_TIMEOUT, PROP_CLIENT_TIMEOUT);
    JSONObject resp = consultaRda(client,syscon,uffint,type,codrda,rup,amministrazioneDesc,articoloDesc);
    client.close();
    return resp;
  }

  /**
   * @param syscon
   * @param uffint
   * @param type
   * @return un oggetto JSONObject fornito dal WS
   * @throws GestoreException
   */
  private JSONObject consultaRda(Client client, Long syscon, String uffint, String type, String codrda, String rup, String amministrazioneDesc, String articoloDesc) throws GestoreException {

    Map<String,String> map = getClientConfig();
    WebTarget webTarget = client.target(map.get("urlEndpoint")+"ConsultaRdA");
    JSONObject resp = null;
    try {
      //se attivo il filtro per le collaborazione del rup
      if("1".equals(map.get("rupFilter"))) {
        try {
          List<Object> parameters = new ArrayList<Object>();
          parameters.add(syscon);
          String selectCfArray = "SELECT DISTINCT(CFRUP) FROM W9DELEGHE WHERE ID_COLLABORATORE = ?";
          //se ho l'uffint, prelevo i record con tale codein. Altrimenti prendo i record senza codein (in analogia alla w3deleghe-lista.jsp)
          if(StringUtils.isNotEmpty(uffint)) {
            selectCfArray += " AND CODEIN= ?";
            parameters.add(uffint);
          }else {
            //selectCfArray += " AND CODEIN IS NULL";
          }
          List<?> cfVector = sqlManager.getListVector(selectCfArray, parameters.toArray());
          if (cfVector != null && cfVector.size() > 0) {
            String cfArray= "";
            for(int i=0;i<cfVector.size();i++) {
              if(i>0)
                cfArray +=";";
              cfArray +=(String) SqlManager.getValueFromVectorParam(cfVector.get(i), 0).getValue();
            }
            webTarget = webTarget.queryParam(PARAMS_CFARRAY,cfArray.toString());
          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nella lettura delle collaborazioni'",
              "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, e);
        }
      }
      if(PARAMS_CODAPP_NC.equals(type))
        webTarget = webTarget.queryParam(PARAMS_CODAPP_NC,APP);
      if(StringUtils.isNotEmpty(codrda))
        webTarget = webTarget.queryParam(PARAMS_CODRDA,codrda);
      if(StringUtils.isNotEmpty(rup))
        webTarget = webTarget.queryParam(PARAMS_RUP,rup);
      if(StringUtils.isNotEmpty(amministrazioneDesc))
        webTarget = webTarget.queryParam(PARAMS_AMMINISTRAZIONE,amministrazioneDesc);
      if(StringUtils.isNotEmpty(articoloDesc))
        webTarget = webTarget.queryParam(PARAMS_ARTICOLO,articoloDesc);
      resp = webTarget.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, map.get("authorization")).get(JSONObject.class);
      
      if(PARAMS_CODAPP_NC.equals(type)) {
        if(resp!=null && resp.getBoolean("esito")) {
          JSONArray listaRda = resp.getJSONArray("listaRdA");
          JSONArray filteredListaRda = new JSONArray();
          if(listaRda.size()>0) {
            List<?> rdaVector = sqlManager.getListVector("select DISTINCT numrda from GARERDA",null);
            List<String> rdaList = new ArrayList<String>();
            if (rdaVector != null && rdaVector.size() > 0) {
              for(int i=0;i<rdaVector.size();i++) {
                String rda = (String) SqlManager.getValueFromVectorParam(rdaVector.get(i), 0).getValue();
                rdaList.add(rda);
              }
            }
            for(int i=0; i<listaRda.size();i++) {
              if(!rdaList.contains(listaRda.getJSONObject(i).getLong("codRdA")+""))
                filteredListaRda.add(listaRda.getJSONObject(i));
            }
            if(filteredListaRda.size()==0)
              resp.put("esito", false);
            resp.put("listaRdA", filteredListaRda);
            
          }
        }
      }

    }catch(NotFoundException e){
      throw new GestoreException(
          "Il servizio di ricerca RdA/RdI non risponde nei tempi attesi. Se il problema persiste contattare il servizio di assistenza",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {NOT_FOUND_EXCEPTION}, e);
    }catch(GestoreException ex) {
      throw ex;
    }catch(Exception ex) {
      String exceptionMsg = GENERIC_EXCEPTION;
      if(ex instanceof ProcessingException)
        exceptionMsg = NOT_FOUND_EXCEPTION;
      throw new GestoreException(
          "Errore nel contattare il servizio di consultazione relativo a 'Integrazione programmazione'",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {exceptionMsg}, ex);
    }
    return resp;
  }

  /**
   * @param ngara
   * @return un oggetto JSON con i record di GARERDA collgati alla gara passata
   * @throws GestoreException
   */
  public JSONObject recuperaRdaCollegate(String codgar, String ngara, boolean isCollegaLotto, boolean type, boolean getLottiXRda) throws GestoreException {

    JSONObject resp =  new JSONObject();
    JSONArray array =  new JSONArray();
    boolean ok = true;
    try {
      String select = "SELECT G.NUMRDA, G.TIPOLOGIA, G.DESCRIZIONE, G.RUP, G.CODVOC, G.CODCAT, G.VOCE, G.PREZUN, U.DESUNI, G.QUANTI, G.IMPORTO, G.CATALOGO, G.AMMINISTRAZIONE FROM GARERDA G LEFT JOIN UNIMIS U ON G.UNIMIS=U.TIPO WHERE CODGAR = ? ";
      List<Object> parameters = new ArrayList<Object>();
      parameters.add(codgar);
      if(StringUtils.isEmpty(ngara)) {
        select+= "and NGARA is null";
      }else {
        if(!isCollegaLotto) {
          select+= "and NGARA = ?";
          parameters.add(ngara);
        }else if(type){
          select+= " and NGARA is null and numrda not in (select distinct numrda from garerda where codgar = ? and ngara = ?)";
          parameters.add(codgar);
          parameters.add(ngara);
        }else {
          select+= " and NGARA is null and numrda not in (select distinct numrda from garerda where codgar = ? and ngara is not null)";
          parameters.add(codgar);
        }
      }

      List<?> datiRda = sqlManager.getListVector(select, parameters.toArray());
      if (datiRda != null && datiRda.size() > 0) {
        for (int l = 0; l < datiRda.size(); l++) {
          JSONObject item = new JSONObject();
          try {
            String numrda = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 0).getValue();
            String tipologia = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 1).getValue();
            String descrizione = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 2).getValue();
            String nomTec = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 3).getValue();         
            String codiceArticolo = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 4).getValue();
            String categoriaMerceologica = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 5).getValue();
            String voce = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 6).getValue();
            Double prezzoUnitario = (Double) SqlManager.getValueFromVectorParam(datiRda.get(l), 7).getValue();
            String unimis = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 8).getValue();
            Double quantit = (Double) SqlManager.getValueFromVectorParam(datiRda.get(l), 9).getValue();
            Double totint = (Double) SqlManager.getValueFromVectorParam(datiRda.get(l), 10).getValue();
            String codiceCatalogo = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 11).getValue();
            String amministrazione = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 12).getValue();

            if(StringUtils.isEmpty(ngara) && getLottiXRda) {
              List<?> datiLotti = sqlManager.getListVector("select distinct ngara from garerda where codgar = ? and ngara is not null and numrda = ? ", new Object[] {codgar,numrda});
              if(datiLotti!=null && datiLotti.size()>0) {
                String lotti = "";
                for(int i=0; i<datiLotti.size();i++) {
                  if(i>0)
                    lotti += ", ";
                  lotti += (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
                }
                item.put("lotti",lotti);
              }
            }

            item.put("codRdA", numrda);
            item.put("settore", tipologia);
            item.put("descrizione", descrizione);
            item.put("nomTec", nomTec);
            item.put("codiceArticolo",codiceArticolo);
            item.put("categoriaMerceologicaDesc", categoriaMerceologica);
            item.put("descrizioneArticolo", voce);
            item.put("prezzoUnitario", prezzoUnitario);
            item.put("unitaMisuraDesc",unimis);
            item.put("quantita",quantit);
            item.put("importo",totint);
            item.put("codiceCatalogo",codiceCatalogo);
            item.put("amministrazione", amministrazione);

            array.add(item);     
          }catch(Exception e) {
            logger.error(e);
            ok=false;
          }
        }
      }else {
        ok=false;
      }
    }catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura delle collaborazioni'",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, e);
    }catch(Exception e){
      throw new GestoreException(
          "Errore nel contattare il servizio di consultazione relativo a 'Integrazione programmazione'",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {GENERIC_EXCEPTION}, e);
    }
    resp.put("esito", ok);
    resp.put("listaRdA",array);

    return resp;
  }

  /**
   * Il metodo consente di inserire in GARERDA le RDA/RDI selezionate dalla maschera di selezione. Vengono trasmetti solo i dati essenziali,
   * in quanto i dati completi vengono prelevati ex-novo dal WS. Il collegamento a livello di gara dell'RdA viene comunicato al WS solo in caso di gare a lotto unico
   * @param ngara
   * @param codgara
   * @param rda
   * @param uffint
   * @param syscon
   * @throws GestoreException
   */
  public void collegaRda(String codgar, String rda[], String uffint, Long syscon, HttpServletRequest request) throws GestoreException {
    Client client = ClientBuilder.newClient();
    client.property(ClientProperties.CONNECT_TIMEOUT, PROP_CLIENT_TIMEOUT);

    try {
      JSONObject resp = consultaRda(client,syscon, uffint, null,null,null,null,null);
      if(resp!=null && resp.getBoolean("esito")) {
        JSONArray listaRda = resp.getJSONArray("listaRdA");
        if(listaRda.size()>0) {
          Map<String,JSONObject> rdaMap = new HashMap<String,JSONObject>();
          for(int i=0; i<listaRda.size();i++) {
            rdaMap.put(listaRda.getJSONObject(i).getLong("codRdA")+"",listaRda.getJSONObject(i));
          }
          //controllo preliminare
          for(int i=0; i < rda.length; i++){
            String codiceRda = rda[i];
            if(!rdaMap.containsKey(codiceRda)) 
              throw new GestoreException("È stato selezionato un codice Rda/Rdi non accessibile all'operatore!"+ codiceRda,null); 
          }
          List<String> rdaSuccess = new ArrayList<String>();
          List<String> rdaError = new ArrayList<String>();

          for(int i=0; i < rda.length; i++){
            String codiceRda = rda[i];
            String descRda = null;
            Double prezzoUnitarioRda = null;
            Double quantitaRda = null;
            String rupRda = null;
            String settoreRda = null;
            String codiceArticoloRda = null;
            String descrizioneArticoloRda = null;
            String categoriaMerceologicaDescRda = null;
            String unimisRda = null;
            Double importo = 0.0;
            String codiceCatalogo = null;
            String amministrazione = null;


            if(rdaMap.get(codiceRda).has("settore"))
              settoreRda  = rdaMap.get(codiceRda).getString("settore");
            if(rdaMap.get(codiceRda).has("codiceArticolo"))
              codiceArticoloRda  = rdaMap.get(codiceRda).getString("codiceArticolo");
            if(rdaMap.get(codiceRda).has("descrizioneArticolo"))
              descrizioneArticoloRda  = rdaMap.get(codiceRda).getString("descrizioneArticolo");
            if(rdaMap.get(codiceRda).has("descrizione"))
              descRda  = rdaMap.get(codiceRda).getString("descrizione");
            if(rdaMap.get(codiceRda).has("categoriaMerceologicaDesc"))
              categoriaMerceologicaDescRda  = rdaMap.get(codiceRda).getString("categoriaMerceologicaDesc");
            if(rdaMap.get(codiceRda).has("prezzoUnitario"))
              prezzoUnitarioRda = rdaMap.get(codiceRda).getDouble("prezzoUnitario");
            if(rdaMap.get(codiceRda).has("unitaMisuraDesc"))
              unimisRda = rdaMap.get(codiceRda).getString("unitaMisuraDesc");
            String tipo = unimisRda;
            try {
              String um = (String) sqlManager.getObject("select tipo from unimis where desuni = ?", new Object[] {unimisRda});
              if(um!=null && !"".equals(um)){
                tipo = um;
              }else {
                if(unimisRda.length()>3)
                  tipo = unimisRda.substring(0, 3);
                Long count = (Long) sqlManager.getObject("select count(*) from unimis where tipo like ?", new Object[] {tipo+"%"});
                if(count!=null && count.longValue() >0)
                  tipo += count;
                sqlManager.update("insert into UNIMIS (CONTA, TIPO, DESUNI, NUMDEC) values (?, ?, ?, ?)",
                    new Object[] { new Long(-1), tipo, unimisRda,new Long(0)});
              }
            } catch (SQLException e) {
              logger.error("Errore nell'inserimento del unita' di misura in UNIMIS: "+ unimisRda +"per l'RdA "+codiceRda);
            } 
            if(rdaMap.get(codiceRda).has("quantita"))
              quantitaRda = rdaMap.get(codiceRda).getDouble("quantita");
            if(rdaMap.get(codiceRda).has("nomTec"))
              rupRda = rdaMap.get(codiceRda).getString("nomTec");
            if(rdaMap.get(codiceRda).has("importo"))
              importo = rdaMap.get(codiceRda).getDouble("importo");
            if(rdaMap.get(codiceRda).has("codiceCatalogo"))
              codiceCatalogo = rdaMap.get(codiceRda).getString("codiceCatalogo");
            if(rdaMap.get(codiceRda).has("amministrazione"))
              amministrazione = rdaMap.get(codiceRda).getString("amministrazione");

            try {
              if(codgar.indexOf("$")==0) { //chiamo il WS solo se siamo in una gara a lotto unico
                String cig = (String) sqlManager.getObject("select codcig from gare where ngara = ?", new Object[] {codgar.replace("$", "")});
                Long stato = getStatoGara(codgar, codgar.replace("$", ""));
                collegaScollegaRdaWS(client,true, codiceRda, codgar,APP,stato,cig);//TODO fare una sola transazione con arrRda?
              }
              Long conteggio = (Long) sqlManager.getObject("select count(*) from garerda where numrda = ? and codgar = ?", new Object[] {codiceRda, codgar});
              if(conteggio == null || (conteggio!=null && conteggio.longValue()==0)){
                Long id = new Long(genChiaviManager.getNextId("GARERDA"));
                sqlManager.update("insert into garerda (id, codgar, numrda, tipologia, codvoc, voce, descrizione, codcat,prezun,unimis,quanti,rup,importo, catalogo, amministrazione) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 
                    new Object[] {id,codgar,codiceRda,settoreRda,codiceArticoloRda,descrizioneArticoloRda,descRda,categoriaMerceologicaDescRda,prezzoUnitarioRda,tipo,quantitaRda,rupRda,importo,codiceCatalogo,amministrazione});
                rdaSuccess.add(codiceRda);
              }else {
                sqlManager.update("update garerda set tipologia=?, codvoc=?, voce=?, descrizione=?, codcat=?, prezun=?, unimis=?, quanti=?, rup=?, importo=?, catalogo=?, amministrazione=? where codgar = ? and numrda=?", 
                    new Object[] {settoreRda,codiceArticoloRda,descrizioneArticoloRda,descRda,categoriaMerceologicaDescRda,prezzoUnitarioRda,tipo,quantitaRda,rupRda,importo,codiceCatalogo,amministrazione,codgar,codiceRda});
              }
            }catch(GestoreException ex) {
              throw ex;
            }catch(Exception ex) {
              logger.error("Errore nell'inserimento dell'rda con codRda: "+ codiceRda);
              rdaError.add(codiceRda);  
              throw new GestoreException("Errore nell'inserimento dell'RdA con codRda: "+ codiceRda,
                  "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {GENERIC_EXCEPTION}, ex);
            }
          }
          //Logeventi
          String errMsg = null;
          int livEvento = 1;
          if(rdaSuccess.size()>0) {
            errMsg = "Sono state collegate alla gara le seguenti RdA/RdI: "+rdaSuccess.toString()+".";
            if(rdaError.size()>0) {
              errMsg += "\n";
            }
          }
          if(rdaError.size()>0) {
            errMsg += "Non è stato possibile collegare alla gara le seguenti RdA/RdI: "+rdaError.toString()+".";
            livEvento=3;
          }
          if(errMsg!=null) {
            try {
              LogEvento logEvento = LogEventiUtils.createLogEvento(request);
              logEvento.setCodApplicazione("PG");
              logEvento.setOggEvento(codgar.replace("$", ""));
              logEvento.setLivEvento(livEvento);
              logEvento.setCodEvento("GA_COLLEGA_RDA");
              logEvento.setDescr("Collegamento RdA/RdI per la gara "+codgar.replace("$", ""));
              logEvento.setErrmsg(errMsg);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }
        }
      }
    }catch(GestoreException ex) {
      throw ex;
    }catch(Exception ex) {
      throw new GestoreException(
          "Errore nel contattare il servizio di consultazione relativo a 'Integrazione programmazione'",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {GENERIC_EXCEPTION}, ex);
    }
    client.close();
  }

  /**
   * Il metodo consente di scollegare tutte le RDA/RDI di una gara
   * @param codgar
   * @throws GestoreException
   */
  public void scollegaRdaGara(String codgar, HttpServletRequest request) throws GestoreException {
    try {
      //recupero tutte le rda collegate alla gara
      String selectRdaArray = "SELECT numrda FROM GARERDA WHERE codgar = ? and ngara is null and numrda is not null";
      List<?> rdaVector = sqlManager.getListVector(selectRdaArray, new Object[] {codgar});
      String[] rda = new String[rdaVector.size()];
      if (rdaVector != null && rdaVector.size() > 0) {
        for(int i=0;i<rdaVector.size();i++) {
          rda[i] = (String) SqlManager.getValueFromVectorParam(rdaVector.get(i), 0).getValue();
        }
        scollegaRda(codgar, rda, request);
      }
    }catch(SQLException e) {
      throw new GestoreException("Errore nel recupero delle RdA/RdI associate alla gara",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, e);
    }
  }

  /**
   * Il metodo consente di scollegare tutte le RDA/RDI di una gara
   * @param codgar
   * @throws GestoreException
   */
  public void scollegaRdaLotto(String codgar, String ngara, HttpServletRequest request) throws GestoreException {
    try {
      //recupero tutte le rda collegate al lotto (ngara valorizzato per gare a lotti, non valorizzato per lotto unico)
      String selectRdaArray = "SELECT numrda FROM GARERDA WHERE codgar = ? and ngara = ? and numrda is not null";
      List<?> rdaVector = sqlManager.getListVector(selectRdaArray, new Object[] {codgar, ngara});
      String[] rda = new String[rdaVector.size()];
      if (rdaVector != null && rdaVector.size() > 0) {
        for(int i=0;i<rdaVector.size();i++) {
          rda[i] = (String) SqlManager.getValueFromVectorParam(rdaVector.get(i), 0).getValue();
        }
        scollegalottoRda(codgar, ngara, rda, request);
      }
    }catch(SQLException e) {
      throw new GestoreException("Errore nel recupero delle RdA/RdI associate alla gara",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, e);
    }
  }
  /**
   * Il metodo consente di scollegare le RDA/RDI scelte. Lo scollegamento a livello di gara dell'RdA viene comunicato al WS solo in caso di gare a lotto unico
   * @param codgar
   * @param rda
   * @throws GestoreException
   */
  public void scollegaRda(String codgar, String rda[], HttpServletRequest request) throws GestoreException {
    try {
      List<String> rdaSuccess = new ArrayList<String>();
      List<String> rdaError = new ArrayList<String>();
      Client client = ClientBuilder.newClient();
      client.property(ClientProperties.CONNECT_TIMEOUT, PROP_CLIENT_TIMEOUT);
      List<String> parameters = new ArrayList<String>();
      parameters.add(codgar);

      for(int i=0; i < rda.length; i++){
        if(codgar.indexOf("$")==0) { //chiamo il WS solo se siamo in una gara a lotto unico
          collegaScollegaRdaWS(client,false, rda[i], codgar,APP,null,null); //TODO fare una sola con arrRda?         
        }
        //scollegare anche entita figlie!
        String selectRdaArray = "SELECT ngara FROM GARERDA WHERE codgar = ? and numrda = ? and ngara is not null";
        List<?> rdaVector = sqlManager.getListVector(selectRdaArray, new Object[] {codgar, rda[i]});
        if (rdaVector != null && rdaVector.size() > 0) {
          for(int j=0;j<rdaVector.size();j++) {
            String ngara = (String) SqlManager.getValueFromVectorParam(rdaVector.get(j), 0).getValue();
            collegaScollegaRdaWS(client,false, rda[i],ngara,APP,null,null); //TODO fare una sola con arrRda?
          }
        }
        rdaSuccess.add(rda[i]);  
      }
      client.close();

      String errMsg = null;
      int livEvento = 1;
      try {
        if(rdaSuccess.size()>0) {
          String inClause="(";
          for(int i=0; i < rdaSuccess.size(); i++){
            if(i>0)
              inClause+=",";
            inClause+="?";
            parameters.add(rdaSuccess.get(i));
          }
          inClause+=")";

          sqlManager.update("delete from garerda where codgar=? and numrda in "+inClause, parameters.toArray());
          //Logeventi
          if(rdaSuccess.size()>0) {
            errMsg = "Sono state scollegate dalla gara le seguenti RdA/RdI: "+rdaSuccess.toString()+".";
            if(rdaError.size()>0) {
              errMsg += "\n";
            }
          }
          if(rdaError.size()>0) {
            errMsg += "Non è stato possibile scollegare dalla gara le seguenti RdA/RdI: "+rdaError.toString()+".";
            livEvento=3;
          }
          if(errMsg!=null) {
            try {
              LogEvento logEvento = new LogEvento();
              if(request!=null)
                logEvento = LogEventiUtils.createLogEvento(request);
              logEvento.setCodApplicazione("PG");
              logEvento.setOggEvento(codgar.replace("$", ""));
              logEvento.setLivEvento(livEvento);
              logEvento.setCodEvento("GA_SCOLLEGA_RDA");
              logEvento.setDescr("Collegamento RdA/RdI per la gara "+codgar.replace("$", ""));
              logEvento.setErrmsg(errMsg);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }
          //
        }
      }catch(SQLException ex) {
        throw new GestoreException("Errore nell'eliminazione delle occorrenza in GARERDA per la gara: "+ codgar,
            "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, ex);  
      }

    }catch(GestoreException ex) {
      throw ex;
    }catch(Exception ex) {
      throw new GestoreException(
          "Errore nel contattare il servizio di consultazione relativo a 'Integrazione programmazione'",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {GENERIC_EXCEPTION}, ex);
    }
  }

  public void collegaScollegaRdaWS(Client client, boolean isCollega, String codRda, String codgar, String applicativo, Long stato, String cig) throws GestoreException {
    try{
      Map<String,String> map = getClientConfig();

      String method = "ScollegaRdA";
      if(isCollega) {
        method = "AggiornaRdA";
      }
      WebTarget webTarget = client.target(map.get("urlEndpoint")+method);
      webTarget = webTarget
          .queryParam(PARAMS_CODAPP,APP)
          .queryParam(PARAMS_CODEX, codgar)
          .queryParam(PARAMS_CODRDA, codRda)
          .queryParam(PARAMS_STATO, stato)
          .queryParam(PARAMS_CIG, cig);
      JSONObject resp = null;
      try {
        resp = webTarget.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, map.get("authorization")).post(null,JSONObject.class);
      }catch(NotFoundException e){
        throw new GestoreException(
            "Il servizio di collegamento delle RdA/RdI non risponde nei tempi attesi. Se il problema persiste contattare il servizio di assistenza",
            "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {NOT_FOUND_EXCEPTION}, e);
      }catch(Exception ex) {
        String exceptionMsg = GENERIC_EXCEPTION;
        if(ex instanceof ProcessingException)
          exceptionMsg = NOT_FOUND_EXCEPTION;
        throw new GestoreException(
            "Errore nel contattare il servizio di consultazione relativo a 'Integrazione programmazione'",
            "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {exceptionMsg}, ex);
      }
      if(resp!=null) {
        if(!resp.getBoolean("esito"))
          throw new GestoreException("Esito negativo per la chiamata \": "+ method+".\" per l'RdA "+codRda+". Errore: "+resp.getString("messaggio"),
              "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {GENERIC_EXCEPTION}, null);
      }
    }catch(GestoreException ex) {
      throw ex;
    }catch(Exception ex) {
      throw new GestoreException(
          "Errore nel contattare il servizio di consultazione relativo a 'Integrazione programmazione'",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {GENERIC_EXCEPTION}, ex);
    }
  }

  /**
   * Il metodo consente di inserire i record in GARERDA e nel WS
   * @param ngara
   * @param codgara
   * @param rda
   * @throws GestoreException
   */
  public void collegalottoRda(String codgar, String ngara, String rda[], HttpServletRequest request) throws GestoreException {
    try {
      Client client = ClientBuilder.newClient();
      client.property(ClientProperties.CONNECT_TIMEOUT, PROP_CLIENT_TIMEOUT);
      List<String> rdaSuccess = new ArrayList<String>();
      List<String> rdaError = new ArrayList<String>();
      for(int i=0; i < rda.length; i++){
        try {
          String cig = (String) sqlManager.getObject("select codcig from gare where ngara = ?", new Object[] {ngara});
          Long stato = getStatoGara(codgar, ngara);
          Long conteggio = (Long) sqlManager.getObject("select count(*) from garerda where numrda = ? and codgar = ? and ngara =?", new Object[] {rda[i], codgar, ngara});
          if(conteggio == null || (conteggio!=null && conteggio.longValue()==0)){
            Long id = new Long(genChiaviManager.getNextId("GARERDA"));
            sqlManager.update("insert into garerda (id, codgar, numrda, tipologia, codvoc, voce, descrizione, codcat,prezun,unimis,quanti,rup,importo, catalogo, amministrazione) "
                + "select distinct "+id+", codgar, numrda, tipologia, codvoc, voce, descrizione, codcat,prezun,unimis,quanti,rup,importo, catalogo, amministrazione from GARERDA where numrda = ? and ngara is null and codgar = ?",new Object[] {rda[i],codgar});
            sqlManager.update("update garerda set ngara = ? where id = ?",new Object[] {ngara,id}); 
          }
          collegaScollegaRdaWS(client,true, rda[i], ngara,APP,stato,cig);//TODO fare una sola transazione con arrRda? recuperare cig e stato!
          rdaSuccess.add(rda[i]);  
        }catch(Exception e) {
          rdaError.add(rda[i]);
        }    
      }
      
    //Logeventi
      String errMsg = null;
      int livEvento = 1;
      
      if(rdaSuccess.size()>0) {
        errMsg = "Sono state collegate al lotto le seguenti RdA/RdI: "+rdaSuccess.toString()+".";
        if(rdaError.size()>0) {
          errMsg += "\n";
        }
      }
      if(rdaError.size()>0) {
        errMsg += "Non è stato possibile collegare al lotto le seguenti RdA/RdI: "+rdaError.toString()+".";
        livEvento=3;
      }
      if(errMsg!=null) {
        try {
          LogEvento logEvento = new LogEvento();
          if(request!=null)
            logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setCodApplicazione("PG");
          logEvento.setOggEvento(ngara);
          logEvento.setLivEvento(livEvento);
          logEvento.setCodEvento("GA_COLLEGA_RDA_LOTTO");
          logEvento.setDescr("Collegamento RdA/RdI per il lotto "+ngara);
          logEvento.setErrmsg(errMsg);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
    }catch(Exception ex) {
      throw new GestoreException("Errore nell'inserimento delle occorrenze in GARERDA per il lotto: "+ ngara,
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {GENERIC_EXCEPTION}, ex);
    }
  }



  /**
   * Il metodo consente di eliminare il record in GARERDA e dal WS
   * @param ngara
   * @param codgara
   * @param rda
   * @throws GestoreException
   */
  public void scollegalottoRda(String codgar, String ngara, String rda[], HttpServletRequest request) throws GestoreException {
    try {
      Client client = ClientBuilder.newClient();
      client.property(ClientProperties.CONNECT_TIMEOUT, PROP_CLIENT_TIMEOUT);
      List<String> rdaSuccess = new ArrayList<String>();
      List<String> rdaError = new ArrayList<String>();
      for(int i=0; i < rda.length; i++){
       try { collegaScollegaRdaWS(client,false, rda[i], ngara,APP,null,null);//TODO fare una sola transazione con arrRda? recuperare cig e stato!
        sqlManager.update("delete from garerda where codgar=? and ngara = ? and numrda = ?", new Object[] {codgar,ngara,rda[i]});
        rdaSuccess.add(rda[i]);  
        }catch(Exception e) {
          rdaError.add(rda[i]);
        }
      }
    //Logeventi
      String errMsg = null;
      int livEvento = 1;
      
      if(rdaSuccess.size()>0) {
        errMsg = "Sono state scollegate dal lotto le seguenti RdA/RdI: "+rdaSuccess.toString()+".";
        if(rdaError.size()>0) {
          errMsg += "\n";
        }
      }
      if(rdaError.size()>0) {
        errMsg += "Non è stato possibile scollegare dal lotto le seguenti RdA/RdI: "+rdaError.toString()+".";
        livEvento=3;
      }
      if(errMsg!=null) {
        try {
          LogEvento logEvento = new LogEvento();
          if(request!=null)
            logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setCodApplicazione("PG");
          logEvento.setOggEvento(ngara);
          logEvento.setLivEvento(livEvento);
          logEvento.setCodEvento("GA_SCOLLEGA_RDA_LOTTO");
          logEvento.setDescr("Scollegamento RdA/RdI per il lotto "+ngara);
          logEvento.setErrmsg(errMsg);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
    }catch(Exception ex) {
      throw new GestoreException("Errore nell'eliminazione delle occorrenze in GARERDA per il lotto: "+ ngara,
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {GENERIC_EXCEPTION}, ex);   
    }
  }

  /**
   * Il metodo consente di sbiancare il campo  NGARA in GARERDA le RDA/RDI selezionate dalla maschera di selezione. 
   * @param ngara
   * @param codgara
   * @param rda
   * @param uffint
   * @param syscon
   * @throws GestoreException
   */
  public void importaRda(String codgar, String ngara, boolean isLotto) throws GestoreException {
    try {
      String select = "select codvoc, descrizione, prezun,unimis,quanti,catalogo, numrda from garerda where codgar = ? ";
      List<Object> parameters = new ArrayList<Object>();
      parameters.add(codgar);
      if(isLotto) {
        select += " and ngara = ?";
        parameters.add(ngara);
      }
      List<?> datiRda = sqlManager.getListVector(select, parameters.toArray());
      if (datiRda != null && datiRda.size() > 0) {
        for (int l = 0; l < datiRda.size(); l++) {
          try {
            String codiceArticolo = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 0).getValue();
            String voce = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 1).getValue();
            Double prezzoUnitario = (Double) SqlManager.getValueFromVectorParam(datiRda.get(l), 2).getValue();
            String unimis = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 3).getValue();
            Double quantit = (Double) SqlManager.getValueFromVectorParam(datiRda.get(l), 4).getValue();
            String codiceCatalogo = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 5).getValue();
            String numrda = (String) SqlManager.getValueFromVectorParam(datiRda.get(l), 6).getValue();

            Long contaf = new Long(0);
            Double norvoc = new Double(0);
            Long maxContaf = (Long)this.sqlManager.getObject("select max(contaf) from gcap where ngara=?",
                new Object[]{ngara});

            if(maxContaf==null) 
              contaf =  new Long(1);
            else
              contaf = new Long(maxContaf.longValue()+1);

            norvoc = Double.valueOf(contaf);

            String sqlInsert = "INSERT INTO GCAP"
                + "(NGARA,CONTAF,NORVOC,CODVOC,QUANTI,PREZUN,CLASI1,SOLSIC,SOGRIB,VOCE,UNIMIS,CODCAT,CODRDA) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

            String codrda = numrda;
            if(StringUtils.isNotEmpty(codiceCatalogo))
              codrda +="-"+codiceCatalogo;
            if(StringUtils.isNotEmpty(codiceArticolo))
              codrda+="-"+codiceArticolo;
            else
              codiceArticolo = numrda;

            sqlManager.update(sqlInsert,new Object[]{ngara,contaf,norvoc,codiceArticolo,quantit,prezzoUnitario,3L,2L,2L,voce,unimis,codiceCatalogo,codrda});

          }catch(SQLException ex) {
            throw new GestoreException("Errore nell'import delle RdA per il lotto: "+ codgar,
                "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, ex);  
          }
        }
      }
    }catch(SQLException ex) {
      throw new GestoreException("Errore nell'import delle RdA per il lotto: "+ codgar,
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, ex); 
    }
  }


  private Long getStatoGara(String codgar, String ngara) throws GestoreException {
    //stato 1: Collegamento rda/rdi
    Long stato = 1L;

    try {
      Long numOccorrenze = null;
      //stato 2: Pubblicazione
      String select="select count(*) from pubg,gare where gare.codgar1=? and pubg.ngara=gare.ngara and tippubg=?";
      numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar,new Long(12)});
      if(numOccorrenze!=null && numOccorrenze.longValue()>=1){
        stato = 2L;
      }else {
        select="select count(*) from pubbli where codgar9=? and tippub in (11,13) ";
        numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar});
        if(numOccorrenze!=null && numOccorrenze.longValue()>=1){
          stato = 2L;
        }
      }

      //stato 3: Aggiudicazione
      select="select DITTA,DATTOA from GARE where ngara=? ";
      List<?> datiGara =  sqlManager.getListVector(select, new Object[] {ngara});
      if (datiGara != null && datiGara.size() > 0) {
        for (int l = 0; l < datiGara.size(); l++) {
          if(SqlManager.getValueFromVectorParam(datiGara.get(l), 0).getValue()!=null && SqlManager.getValueFromVectorParam(datiGara.get(l), 1).getValue()!=null)
            stato = 3L;
        }
      }

      //stato 4/5: In esecuzione/Conclusa
      select="select gc.DVERBC,gc.dcertu from GARECONT gc,GARE ga"
            + " where gc.codimp = ga.ditta and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))"
            + " and ga.ditta is not null and ga.ngara= ?";
      
      List<?> datiGarecont =  sqlManager.getListVector(select, new Object[] {ngara});
      if (datiGarecont != null && datiGarecont.size() > 0) {
        for (int l = 0; l < datiGarecont.size(); l++) {
          //stato 4
          if(SqlManager.getValueFromVectorParam(datiGarecont.get(l), 0).getValue()!=null)
            stato = 4L;
          //stato 5
          if(SqlManager.getValueFromVectorParam(datiGarecont.get(l), 1).getValue()!=null)
            stato = 5L;
        }
      } 
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la verifica dello stato della gara ",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, e);
    }   
    return stato;
  }

  /**
   * Il metodo consente di aggiornare tutte le RDA/RDI di una gara/tornata
   * @param codgar
   * @throws GestoreException
   */
  public void aggiornaRdaGara(String codgar, String ngara, String ditta) throws GestoreException {
    if(logger.isDebugEnabled()) logger.debug("AggiornaRdaGara: inizio metodo");
    Client client = ClientBuilder.newClient();
    client.property(ClientProperties.CONNECT_TIMEOUT, PROP_CLIENT_TIMEOUT);
    try {
      //se siamo in una gara a lotto unico
      if(codgar.indexOf("$")==0) { 
        String cig = (String) sqlManager.getObject("select codcig from gare where ngara = ?", new Object[] {codgar.replace("$", "")});
        Long stato = getStatoGara(codgar, codgar.replace("$", ""));
        String selectRdaArray = "SELECT numrda FROM GARERDA WHERE codgar = ? and ngara is null and numrda is not null";
        List<?> rdaVector = sqlManager.getListVector(selectRdaArray, new Object[] {codgar});
        if (rdaVector != null && rdaVector.size() > 0) {
          for(int i=0;i<rdaVector.size();i++) {
            if(logger.isDebugEnabled()) logger.debug("Aggiornamento dell'RdA "+rdaVector.get(i)+" con ngara="+codgar.replace("$", "") + ", stato="+stato+", cig="+cig);
            collegaScollegaRdaWS(client,true, (String) SqlManager.getValueFromVectorParam(rdaVector.get(i), 0).getValue(), codgar,APP,stato,cig);//TODO fare una sola transazione con arrRda?
            if(logger.isDebugEnabled()) logger.debug("Aggiornamento dell'RdA riuscito");
          }
        }
      }else{
        if(ngara!=null) {
          //recupero e aggiorno tutte le rda associata al particolare lotto
          String cig = (String) sqlManager.getObject("select codcig from gare where ngara = ?", new Object[] {ngara});
          Long stato = getStatoGara(codgar, ngara);
          String selectRdaArray = "SELECT numrda FROM GARERDA WHERE codgar = ? and ngara = ? and numrda is not null";
          List<?> rdaVector = sqlManager.getListVector(selectRdaArray, new Object[] {codgar,ngara});
          if (rdaVector != null && rdaVector.size() > 0) {
            for(int j=0;j<rdaVector.size();j++) {
              if(logger.isDebugEnabled()) logger.debug("Aggiornamento dell'RdA "+rdaVector.get(j)+" con ngara="+ngara + ", stato="+stato+", cig="+cig);
              collegaScollegaRdaWS(client,true, (String) SqlManager.getValueFromVectorParam(rdaVector.get(j), 0).getValue(), ngara,APP,stato,cig);//TODO fare una sola transazione con arrRda?
              if(logger.isDebugEnabled()) logger.debug("Aggiornamento dell'RdA riuscito");
              
            }
          }  
        }else {
          //recupero tutte le gare collegate alla tornata (se stipula per aggiudicatario, ottimizzo filtrando per DITTA
          String selectNgaraArray = "SELECT ngara FROM GARE WHERE codgar1 = ? and ngara <> ?";
          Object[] params = new Object[] {codgar,codgar};
          if(StringUtils.stripToNull(ditta)!=null) {
            selectNgaraArray += " and ditta= ?";
            params = new Object[] {codgar,codgar,ditta};
          }
          List<?> ngaraVector = sqlManager.getListVector(selectNgaraArray, params);
          if (ngaraVector != null && ngaraVector.size() > 0) {
            for(int i=0;i<ngaraVector.size();i++) {
              //recupero e aggiorno tutte le rda associata al particolare lotto
              ngara = (String) SqlManager.getValueFromVectorParam(ngaraVector.get(i), 0).getValue();
              String cig = (String) sqlManager.getObject("select codcig from gare where ngara = ?", new Object[] {ngara});
              Long stato = getStatoGara(codgar, ngara);
              String selectRdaArray = "SELECT numrda FROM GARERDA WHERE codgar = ? and ngara = ? and numrda is not null";
              List<?> rdaVector = sqlManager.getListVector(selectRdaArray, new Object[] {codgar,ngara});
              if (rdaVector != null && rdaVector.size() > 0) {
                for(int j=0;j<rdaVector.size();j++) {
                  if(logger.isDebugEnabled()) logger.debug("Aggiornamento dell'RdA "+rdaVector.get(j)+" con ngara="+ngara + ", stato="+stato+", cig="+cig);
                  collegaScollegaRdaWS(client,true, (String) SqlManager.getValueFromVectorParam(rdaVector.get(j), 0).getValue(), ngara,APP,stato,cig);//TODO fare una sola transazione con arrRda?
                  if(logger.isDebugEnabled()) logger.debug("Aggiornamento dell'RdA riuscito");
                  
                }
              }
            }
          }
        }
      }
    }catch(SQLException e) {
      throw new GestoreException("Errore nel recupero delle RdA/RdI associate alla gara",
          "integrazioneprogrammazione.consultaRda.remote.error",new Object[] {SQL_EXCEPTION}, e);
    }
    if(logger.isDebugEnabled()) logger.debug("AggiornaRdaGara: fine metodo");
  }

}




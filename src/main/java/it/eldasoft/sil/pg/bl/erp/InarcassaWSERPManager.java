/*
 * Created on 08/05/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.erp;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class InarcassaWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(InarcassaWSERPManager.class);

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;
  
  /** Manager con funzionalita' generali */
  private GeneManager               geneManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }
  
  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
  
  public static final String APPALTI_MS_RAI_WS_BASEURL = "appalti-ms.rai.ws.baseUrl";
  public static final String APPALTI_MS_RAI_WS_CLIENTID = "appalti-ms.rai.ws.clientId";
  public static final String APPALTI_MS_RAI_WS_CLIENTKEY = "appalti-ms.rai.ws.clientKey";
  public static final String APPALTI_MS_RAI_WS_USERNAME = "appalti-ms.rai.ws.username";
  public static final String APPALTI_MS_RAI_WS_PASSWORD = "appalti-ms.rai.ws.password";
  //Aggiudica RDA
  public WSERPRdaResType  inviaDatiAggiudicazione (String username, String password, HashMap datiMask) throws Exception{

    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();

    String codgar = (String)datiMask.get("codgar");
    String ngara = (String)datiMask.get("ngara");
    /*
	     String codcig = (String)datiMask.get("codcig");
	     String codcigaq = (String)datiMask.get("codcigaq");
	     String ditta = (String)datiMask.get("ditta");
	     String idFornitore = (String)datiMask.get("idFornitore");
	     String tipoContratto = (String)datiMask.get("tipoContratto");
	     String oggettoGara = (String)datiMask.get("oggettoGara");
	     Double impapp = (Double)datiMask.get("impapp");
	     Double iaggiu = (Double)datiMask.get("iaggiu");
	     Double ribagg = (Double)datiMask.get("ribagg");
	     Double riboepv = (Double)datiMask.get("riboepv");
	     Long modlicg = (Long)datiMask.get("modlicg");
	     String numeroRepertorioContratto = (String)datiMask.get("nrepat");
	     Date dataContratto = (Date)datiMask.get("daatto");
     */

    String genereSql = "select genere from v_gare_genere where codgar = ? ";
    Long genere = (Long) this.sqlManager.getObject(genereSql, new Object[]{codgar});
    
    String getDetlicg = "select g.detlicg, g.critlicg, t.offtel "
        + "from gare g "
        + "inner join torn t on t.codgar = g.codgar1 "
        + "where g.codgar1 = ? "
        + "and g.ngara = ? ";
    Vector<?> datiDiControllo = sqlManager.getVector(getDetlicg, new Object[] {codgar,ngara});
    Long detlicg = null;
    Long critlicg = null;
    Long offtel = null;
    if(datiDiControllo != null && !datiDiControllo.isEmpty()) {
      detlicg = (Long)sqlManager.getValueFromVectorParam(datiDiControllo, 0).getValue();
      critlicg = (Long)sqlManager.getValueFromVectorParam(datiDiControllo, 1).getValue();
      offtel = (Long) SqlManager.getValueFromVectorParam(datiDiControllo, 2).getValue();
      
    }
    if(Long.valueOf(3).equals(offtel) || detlicg != null || (detlicg == null && Long.valueOf(2).equals(critlicg))) {

      String selezioneGareDaAggiornare = "select r.numrda, g.ribagg, g.codcig, "
          + "g.iaggiu, tc.nomtec, t.accqua, i.cgenimp, ra.impman, ra.quodic, "
          + "i.tipimp, ra.coddic, g.riboepv "
          + "from gare g "
          + "inner join garerda r on g.codgar1 = r.codgar ";
          if (Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
            selezioneGareDaAggiornare += "and g.ngara = r.ngara ";
          }
          selezioneGareDaAggiornare += "inner join torn t on t.codgar = g.codgar1 "
          + "left join tecni tc on t.codrup = tc.codtec "
          + "inner join impr i on i.codimp = g.ditta "
          + "left join ragimp ra on ra.codime9 = g.ditta and ra.impman='1' "
          + "where g.codgar1 = ? and g.ngara = ? "
          + "order by r.numrda ";
      List<?> listaGareDaAgg = sqlManager.getListVector(selezioneGareDaAggiornare, new Object[] {codgar,ngara});
      if(!listaGareDaAgg.isEmpty()){
        List<WSERPOdaType> odaArray = new ArrayList<>();
        WSERPGaraType datiGara = new WSERPGaraType();
        for (int g = 0; g < listaGareDaAgg.size(); g++){
          WSERPOdaType oda = new WSERPOdaType();
          String numrda = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 0).stringValue());
          Double ribagg = checkDoubleValue(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 1).getValue());
          String codcig = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 2).stringValue());
          Double iaggiu = checkDoubleValue(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 3).getValue());
          String nomtec = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 4).stringValue());
          String accqua = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 5).stringValue());
          String cgenimp = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 6).stringValue());
          String impman = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 7).stringValue());
          Double quodic = checkDoubleValue(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 8).getValue());
          Long tipimp = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 9).getValue();
          String coddic = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 10).stringValue());
          Double riboepv = checkDoubleValue(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 11).getValue());
          
          if(tipimp != null && (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp))){
            String recuperoCgenimpRti = "select i.cgenimp "
                + "from ragimp ra "
                + "inner join impr i on i.codimp = ra.coddic "
                + "where ra.coddic = ? ";
            cgenimp = UtilityStringhe.convertiNullInStringaVuota((String) sqlManager.getObject(recuperoCgenimpRti, new Object[] {coddic}));
          }

          //invoco il ws aggiorna procedura
          //oda.setIdLotto(ngara);
          //if prezzi unitari
          if(Long.valueOf(3).equals(detlicg)) {
            odaArray.addAll(getListaOda(codgar, ngara, numrda));
          } else {
            //a corpo
            oda.setCodiceRda(numrda);
            oda.setNumeroGara(ngara);
            odaArray.add(oda);
          }


          
          WSERPFornitoreType[] fornitoreArr = new WSERPFornitoreType[1];
          WSERPFornitoreType fornitore = new WSERPFornitoreType();
          fornitore.setIdFornitore(cgenimp);
          if(tipimp != null) {
            if(Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp)) {
              fornitore.setTipologia("Y");
            } else {
              fornitore.setTipologia("N");
            }
          }
          //fornitore.setRuolo(impman);
          //fornitore.setQuota(quodic.longValue());
          fornitoreArr[0] = fornitore;
          datiGara.setAggiudicatariArray(fornitoreArr);

          
          if(Long.valueOf(3).equals(offtel)) {
            datiGara.setModalitaOfferta("A CORPO");
            datiGara.setTipologiaOfferta("RIBASSO");
          } else if(detlicg != null) {
            switch (String.valueOf(detlicg)) {
            case "3":
              datiGara.setModalitaOfferta("PREZZI UNITARI");
              datiGara.setTipologiaOfferta("IMPORTO");
              break;
            case "4":
              datiGara.setModalitaOfferta("A CORPO");
              datiGara.setTipologiaOfferta("IMPORTO");
              break;
            default:
              datiGara.setModalitaOfferta("A CORPO");
              datiGara.setTipologiaOfferta("RIBASSO");
              break;
            }
          } else {
            if(Long.valueOf(2).equals(critlicg)) {
              datiGara.setModalitaOfferta("A CORPO");
              datiGara.setTipologiaOfferta("IMPORTO");
            }
          }

          if(Long.valueOf(2).equals(critlicg)) {
            datiGara.setRibassoAggiudicazione(riboepv);
          } else {
            datiGara.setRibassoAggiudicazione(ribagg);
          }
          datiGara.setCodiceCig(codcig);
          datiGara.setImportoAggiudicazione(iaggiu);
          datiGara.setRup(nomtec);
          if (accqua != null && "1".equals(accqua)) {
            datiGara.setAccordoQuadro("Y");
          } else {
            datiGara.setAccordoQuadro("N");
          }
        }
        
        wserpRdaRes = gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray.toArray(new WSERPOdaType[0]), datiGara);
      }


    }else {
      wserpRdaRes.setMessaggio("La gara non risulta in uno stato da aggiornare!");
    }

    return wserpRdaRes;

  }
  
  private List<WSERPOdaType> getListaOda(String codgar, String ngara, String numrda) throws Exception {
    String selectPerPosizioneRda = "select v.perrib ,v.impoff, v.posrda "
        + "from gare g "
        + "inner join garerda r on g.codgar1 = r.codgar "
        + "inner join v_gcap_dpre v on v.codgar = g.codgar1 and v.ngara = g.ngara and v.cod_ditta = g.ditta and r.numrda = v.codrda "
        + "where g.codgar1 = ? and g.ngara = ? and r.numrda = ? ";
    List<?> listaPerPosizioneRda = sqlManager.getListVector(selectPerPosizioneRda, new Object[] {codgar,ngara,numrda});
    List<WSERPOdaType> odaLista = new ArrayList<>();
    if(!listaPerPosizioneRda.isEmpty()){
      for (int g = 0; g < listaPerPosizioneRda.size(); g++){
        Double perrib = checkDoubleValue(SqlManager.getValueFromVectorParam(listaPerPosizioneRda.get(g), 0).getValue());
        Double impoff = checkDoubleValue(SqlManager.getValueFromVectorParam(listaPerPosizioneRda.get(g), 1).getValue());
        String posrda = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaPerPosizioneRda.get(g), 2).stringValue());

        //invoco il ws aggiorna procedura
        WSERPOdaType oda = new WSERPOdaType();
        //oda.setIdLotto(ngara);
        oda.setCodiceRda(numrda);
        oda.setNumeroGara(ngara);
        oda.setImporto(impoff);
        oda.setPosizioneRda(posrda);
        odaLista.add(oda);
      }
    }
    //return odaLista.toArray(new WSERPOdaType[0]);
    return odaLista;
  }
  
  public Double checkDoubleValue(Object value) {
    Double valueDouble = null;
    if (value instanceof Number) {
        valueDouble = ((Number) value).doubleValue();
    }
    return valueDouble;
  }

  //Crea aggiorna fornitore
  public WSERPFornitoreResType  inviaDatiFornitore (String username, String password, HashMap datiMask) throws GestoreException, SQLException{

    WSERPFornitoreResType wserpFornitoreRes = new WSERPFornitoreResType();
    String dittaAggiudicataria = (String)datiMask.get("ditta");
    String ngara = (String)datiMask.get("ngara");

    WSERPFornitoreType fornitore = new WSERPFornitoreType();
    //recupero i dati da  DITTA
    if (dittaAggiudicataria != null) {
      //devo controllare se la gara è un accordo quadro
      Boolean isAccQuadro = Boolean.FALSE;
      List<String> ditteAggiudicatarie = new ArrayList<>();
      String controlloAccQuadro = "select dittao from ditgaq where ngara = ?";
      List<String> ditteAccQuadro = this.sqlManager.getListVector(controlloAccQuadro, new Object[]{ngara});
      if(ditteAccQuadro != null && !ditteAccQuadro.isEmpty()){
        isAccQuadro = Boolean.TRUE;
        for (int i = 0; i < ditteAccQuadro.size(); i++) {
          ditteAggiudicatarie.add((String) SqlManager.getValueFromVectorParam(ditteAccQuadro.get(i), 0).getValue());
        }
      }
      if(Boolean.FALSE.equals(isAccQuadro)) {
        ditteAggiudicatarie.add(dittaAggiudicataria);
      }
      for (int i = 0; i < ditteAggiudicatarie.size(); i++) {
        String ditta = ditteAggiudicatarie.get(i);
        //Utilizzo una lista cosi' da poter gestire una eventuale RTI verticale
        List<String> ditteAggiudicatarieRTI = new ArrayList<>();
        //recupero i dati da  DITTA, considerando che potrebbe anche essere una RTI
        Vector<?> datiImpr = this.sqlManager.getVector("select tipimp,tiprti from impr where codimp = ?", new Object[]{ditta});
        Long tipimp = null;
        if(datiImpr != null && !datiImpr.isEmpty()) {
          tipimp = (Long)sqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
          Long tiprti = (Long)sqlManager.getValueFromVectorParam(datiImpr, 1).getValue();

          if(tipimp != null && (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp))){
            if(tiprti != null && Long.valueOf(1).equals(tiprti)) {
              List<String> rtiVerticale = this.sqlManager.getListVector("select coddic from ragimp" +
                  " where codime9 = ? ", new Object[]{ditta});
              if(rtiVerticale != null && !rtiVerticale.isEmpty()){
                for (int x = 0; x < rtiVerticale.size(); x++) {
                  ditteAggiudicatarieRTI.add((String) SqlManager.getValueFromVectorParam(rtiVerticale.get(x), 0).getValue());
                }
              }
            } else {
              String mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                  " where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
              ditteAggiudicatarieRTI.add(mandataria);
            }
          } else {
            ditteAggiudicatarieRTI.add(ditta);
          }
        }

        for (int y = 0; y < ditteAggiudicatarieRTI.size(); y++) {
          String dittaRTI = ditteAggiudicatarieRTI.get(y);

          String selectIMPR = "select cgenimp,nomimp,cfimp,pivimp,indimp,nciimp,proimp,capimp,locimp,nazimp,emai2ip,emaiip " +
              "from impr where codimp = ?";
          Vector<?> datiIMPR = sqlManager.getVector(selectIMPR, new Object[] { dittaRTI });
          if (datiIMPR != null && !datiIMPR.isEmpty()) {
            String cgenimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 0).getValue();
            cgenimp = StringUtils.stripToEmpty(cgenimp);
            String nomimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 1).getValue();
            String cfimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 2).getValue();
            cfimp = UtilityStringhe.convertiNullInStringaVuota(cfimp);
            String pivimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 3).getValue();
            pivimp = UtilityStringhe.convertiNullInStringaVuota(pivimp);
            String indimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 4).getValue();
            indimp = UtilityStringhe.convertiNullInStringaVuota(indimp);
            String nciimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 5).getValue();
            nciimp = UtilityStringhe.convertiNullInStringaVuota(nciimp);
            String proimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 6).getValue();
            proimp = UtilityStringhe.convertiNullInStringaVuota(proimp);
            String capimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 7).getValue();
            String locimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 8).getValue();

            Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 9).getValue();
            String siglanaz = "";//"Non presente";
            if(nazimp != null){
              if(Long.valueOf(1).equals(nazimp)){
                siglanaz = "IT";
              }else{
                siglanaz = (String) this.sqlManager.getObject("select codiso from g_nazioni where codice =? ",
                        new Object[]{nazimp});
              }
            }
            
            String emai2ip = (String) SqlManager.getValueFromVectorParam(datiIMPR, 10).getValue();
            String emaiip = (String) SqlManager.getValueFromVectorParam(datiIMPR, 11).getValue();
            
            fornitore.setCodiceFornitore(dittaRTI);
            fornitore.setCodiceFiscale(cfimp);
            fornitore.setPartitaIva(pivimp);
            fornitore.setRagioneSociale(nomimp);
            fornitore.setLocalita(locimp);
            fornitore.setIndirizzo(indimp);
            fornitore.setCap(capimp);
            fornitore.setProvincia(proimp);
            fornitore.setCivico(nciimp);
            fornitore.setNazionalita(siglanaz);
            
            //Controllo emai2ip se e' null, inserisco emaiip
            if(emai2ip != null)
              fornitore.setEmail(emai2ip);
            else
              fornitore.setEmail(emaiip);
            
            if(tipimp!=null) {
              fornitore.setTipologia(tipimp.toString());  
            }

            if(!"".equals(cgenimp)) {
              fornitore.setIdFornitore(cgenimp); 
            }
            
            wserpFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(username,password, "WSERP", fornitore);
          }
        }
      }
    }
    return wserpFornitoreRes;

  }
	  
  public String insertAnagraficaFornitore(String codiceFiscale, String partitaIva) throws Exception {
    Object[] obj = new Object[]{codiceFiscale, partitaIva};
    String controlloSeEsisteImpr = "select codimp from impr where cfimp = ? and ";
    if(partitaIva != null) {
      controlloSeEsisteImpr += "pivimp = ? ";
    } else {
      controlloSeEsisteImpr += "pivimp is null ";
      obj = new Object[]{codiceFiscale};
    }
    String codiceImpresa = null;
    String codimprSql = (String)sqlManager.getObject(controlloSeEsisteImpr, obj);
    if (codimprSql != null) {
        codiceImpresa = codimprSql;
    } else {
      JSONObject operatoreJson = getOperatoreEconomico(codiceFiscale, partitaIva);
      String denominazione = operatoreJson.getString("denominazione");
      codiceFiscale = operatoreJson.getString("codiceFiscale");
      partitaIva = operatoreJson.getString("partitaIva");
      String email = operatoreJson.getString("email");
      String pec = operatoreJson.getString("pec");
      String nazione = operatoreJson.getString("nazione");
      codiceImpresa = geneManager.calcolaCodificaAutomatica("IMPR","CODIMP");
      String insertQuery = "INSERT INTO IMPR (CODIMP,TIPIMP,NOMEST,NOMIMP,CFIMP,PIVIMP,EMAIIP,EMAI2IP,NAZIMP) VALUES (?,?,?,?,?,?,?,?,?)";
      sqlManager.update(insertQuery, new Object[]{codiceImpresa,null,denominazione,denominazione,codiceFiscale,partitaIva,email,pec,null});
    }
    
    return codiceImpresa;
  }
  
  
  public String getAuthorization() throws Exception {
    
    String url = ConfigManager.getValore(APPALTI_MS_RAI_WS_BASEURL);
    if (url == null || "".equals(url)) {
      throw new GestoreException("L'indirizzo per la connessione al web service non e' definito", "");
    }
    if (url.endsWith("/"))
      url = url.substring(0, url.length() - 1);

    url = url + "/authenticate";

    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

    conn.setRequestMethod("POST");
    conn.setRequestProperty("clientId", ConfigManager.getValore(APPALTI_MS_RAI_WS_CLIENTID));
    conn.setRequestProperty("clientKey", ConfigManager.getValore(APPALTI_MS_RAI_WS_CLIENTKEY));
    conn.setRequestProperty("password", ConfigManager.getValore(APPALTI_MS_RAI_WS_PASSWORD));
    conn.setRequestProperty("username", ConfigManager.getValore(APPALTI_MS_RAI_WS_USERNAME));
    conn.setRequestProperty("Content-Type", "application/json");

    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      response.append(line);
    }
    reader.close();
    conn.disconnect();

    JSONObject jsonObject = JSONObject.fromObject(response.toString());

    return jsonObject.getString("token");

  }
  
  public JSONObject getOperatoreEconomico(String cf, String piva) throws Exception {

    String url = ConfigManager.getValore(APPALTI_MS_RAI_WS_BASEURL);
    if (url == null || "".equals(url)) {
      throw new GestoreException("L'indirizzo per la connessione al web service non e' definito", "");
    }
    if (url.endsWith("/"))
      url = url.substring(0, url.length() - 1);

    url = url + "/getOperatoriEconomici?cfOe="+cf;
    
    if(piva != null) {
      url += "&pivaOe="+piva;
    }
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Authorization", getAuthorization());
    conn.setRequestProperty("Content-Type", "application/json");

    JSONObject res = null;
    BufferedReader reader = null;
    if (conn.getResponseCode() == 404) {
      logger.error("La risorsa con codice fiscale "+cf+" non è stata trovata (404 Not Found)");
    } else if (conn.getResponseCode() == 200) {
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
          response.append(line);
      }
      res = JSONArray.fromObject(response.toString()).getJSONObject(0);
    } else {
      logger.error("La richiesta ha restituito un codice di stato inaspettato: " + conn.getResponseCode());
    }

    if (reader != null) {
      reader.close();
    }
    conn.disconnect();

    return res;

  }



 }



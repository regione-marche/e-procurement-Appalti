/*
 * Created on 21/08/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.nadc;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.nadc.model.Contratto;
import it.eldasoft.sil.pg.bl.nadc.model.Response;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.sil.pg.nadc.model.Aggiornamento;
import it.eldasoft.sil.pg.nadc.model.Anagrafica;
import it.eldasoft.sil.pg.nadc.model.Appalto;
import it.eldasoft.sil.pg.nadc.model.Creazione;
import it.eldasoft.sil.pg.nadc.model.DataEproc;
import it.eldasoft.sil.pg.nadc.model.DatiContratto;
import it.eldasoft.sil.pg.nadc.model.Documento;
import it.eldasoft.sil.pg.nadc.model.DocumentoDaInviare;
import it.eldasoft.sil.pg.nadc.model.ImportazioneContrattiPCP;
import it.eldasoft.sil.pg.nadc.model.Rup;
import it.eldasoft.sil.pg.nadc.model.RupScheda;
import it.eldasoft.sil.pg.nadc.model.Scheda;
import it.eldasoft.sil.pg.nadc.model.Stipula;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;


public class NewAdcManager {

  static Logger           logger = Logger.getLogger(NewAdcManager.class);

  private static String NADC_RAI_WS_BASEURL         = "nadc.rai.ws.baseUrl";
  private static String NADC_MAGGIOLI_WS_BASEURL    = "apiEsecuzione.ws.baseUrl";
  private static String NADC_RAI_WS_HOURS           = "nadc.rai.align.hours";
  
  private LocalDateTime currentDateTime    = null;
  private LocalDateTime previewDateTime    = null;
  
  
  private SqlManager      sqlManager;

  private FileAllegatoManager fileAllegatoManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   * 
   * @param mod = INSERIMENTO o AGGIORNAMENTO
   * @throws Exception
   */
  public void importPCPContracts(String mod) throws Exception {
    try {
      
      this.currentDateTime = LocalDateTime.now();
      
      this.previewDateTime = calculatePreviousDate(this.currentDateTime, getCronExpression());
      
      
      // Endpoint per l'importazione dei contratti PCP
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);
      
      url = url + "/api/ms/pcp";
      
      List<?> dati = getDatiDaMod(mod, "PCP");
      
      if (dati != null && !dati.isEmpty()) {
        for (int i = 0; i < dati.size(); i++) {
          String ngara = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
          String codcig = (String) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();
          String doftec = (String) SqlManager.getValueFromVectorParam(dati.get(i), 2).getValue();
          String sysnom = (String) SqlManager.getValueFromVectorParam(dati.get(i), 3).getValue();
          String nometei = (String) SqlManager.getValueFromVectorParam(dati.get(i), 4).getValue();
          String cogtei = (String) SqlManager.getValueFromVectorParam(dati.get(i), 5).getValue();
          String cftec = (String) SqlManager.getValueFromVectorParam(dati.get(i), 6).getValue();
          String ematec = (String) SqlManager.getValueFromVectorParam(dati.get(i), 7).getValue();
          Date dacqcig = (Date) SqlManager.getValueFromVectorParam(dati.get(i), 8).getValue();
          String idappaltonpa = (String) SqlManager.getValueFromVectorParam(dati.get(i), 9).getValue();
          Long sysuffapp = (Long) SqlManager.getValueFromVectorParam(dati.get(i), 10).getValue();
          
          String token = login();
          int livEvento = 1;
          String errMsgEvento = null;
          String listaNomiSchede = "";
          String jsonInputString = "";
          Integer responseCode = null;
          HttpURLConnection conn = null;
          ImportazioneContrattiPCP contrattiPCP = null;
          List<Scheda> listaSchede = null;
          try { //GESTIONE TRACCIATURA

            contrattiPCP = new ImportazioneContrattiPCP();
            contrattiPCP.setCig(codcig);
            contrattiPCP.setIdAppalto(idappaltonpa);
            contrattiPCP.setDataEproc(creaOggettoDataEproc(ngara, codcig));
            Rup rup = new Rup();
            rup.setCodiceFiscale(cftec);
            rup.setNome(nometei);
            rup.setCognome(cogtei);
            rup.setEmail(ematec);

            String sysnomDecoded = null;
            if (sysnom != null && sysnom.trim().length() > 0) {
              ICriptazioneByte passwordICriptazioneByte = null;
              passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
                  ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), sysnom.getBytes(),
                  ICriptazioneByte.FORMATO_DATO_CIFRATO);
              sysnomDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
            }

            String sysuffappDesc = null;
            if(sysuffapp!= null) {
              sysuffappDesc = (String) this.sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ? ", new Object[]{"G_022",sysuffapp});
            }
            
            rup.setMatricola(sysnomDecoded);
            rup.setDirezione(doftec);
            rup.setSottostruttura(sysuffappDesc);
            rup.setPassword(null);
            contrattiPCP.setRup(rup);

            listaSchede = creaArraySchede(ngara, codcig);
            listaNomiSchede = listaSchede.stream()
                .map(Scheda::getCodice) // Estraggo la proprieta' codice
                .collect(Collectors.joining(","));
            
            List<Scheda> listaSchedeRipuliteDaEsecuzione = getSchedeRipuliteDaEsecuzione(listaSchede);
            contrattiPCP.setSchede(listaSchedeRipuliteDaEsecuzione);

            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", token);
            conn.setDoOutput(true);

            ObjectMapper objectMapper = new ObjectMapper();
            jsonInputString = objectMapper.writeValueAsString(contrattiPCP);

            // Scrivo il JSON nel body della richiesta
            OutputStream os = conn.getOutputStream();
            os.write(jsonInputString.getBytes());
            os.flush();
            errMsgEvento = jsonInputString;

          }catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
            logger.error("Errore inaspettato request", e);
          } finally {

            try {
              LogEvento logEvento = new LogEvento();
              logEvento.setCodApplicazione("PG");
              logEvento.setLivEvento(livEvento);
              logEvento.setOggEvento(codcig);
              logEvento.setCodEvento("GA_NADC_REQ_MSPCP");
              String descrizione = "Invio richiesta a NEW ADC:  /api/ms/pcp ("+codcig+", data acq. "+dacqcig+"), Schede: "+listaNomiSchede;
              if(descrizione.length()>500) {
                descrizione = descrizione.substring(0, 500);
              }
              logEvento.setDescr(descrizione);
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }

          }

          try {
            responseCode = conn.getResponseCode();
            InputStream inputStream = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
              inputStream = conn.getInputStream();
            } else {
              inputStream = conn.getErrorStream();
            }
            String responseBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
            errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;
            
          }catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
            logger.error("Errore inaspettato response", e);
          } finally {
            if(conn != null) {
              conn.disconnect();
            }
            if(token != null) {
              logout(token);
            }

            try {
              LogEvento logEvento1 = new LogEvento();
              logEvento1.setCodApplicazione("PG");
              logEvento1.setLivEvento(livEvento);
              logEvento1.setOggEvento(codcig);
              logEvento1.setCodEvento("GA_NADC_RES_MSPCP");
              logEvento1.setDescr("Risposta da NEW ADC:  /api/ms/pcp ("+codcig+", data acq. "+dacqcig+")");
              logEvento1.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento1);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }
        }
      }
      
      
      
    } catch (Exception e) {
      throw new Exception("Errore durante l'importazione del contratto PCP: "+e.getMessage(), e);
    }

  }

  public void importPCPContractsPuntuale(String cig) throws Exception {
    try {
      
      // Endpoint per l'importazione dei contratti PCP
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);
      
      url = url + "/api/ms/pcp";
      
      List<?> dati = getDatiDaCig(cig);
      
      if (dati != null && !dati.isEmpty()) {
        for (int i = 0; i < dati.size(); i++) {
          String ngara = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
          String codcig = (String) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();
          String doftec = (String) SqlManager.getValueFromVectorParam(dati.get(i), 2).getValue();
          String sysnom = (String) SqlManager.getValueFromVectorParam(dati.get(i), 3).getValue();
          String nometei = (String) SqlManager.getValueFromVectorParam(dati.get(i), 4).getValue();
          String cogtei = (String) SqlManager.getValueFromVectorParam(dati.get(i), 5).getValue();
          String cftec = (String) SqlManager.getValueFromVectorParam(dati.get(i), 6).getValue();
          String ematec = (String) SqlManager.getValueFromVectorParam(dati.get(i), 7).getValue();
          Date dacqcig = (Date) SqlManager.getValueFromVectorParam(dati.get(i), 8).getValue();
          String idappaltonpa = (String) SqlManager.getValueFromVectorParam(dati.get(i), 9).getValue();
          Long sysuffapp = (Long) SqlManager.getValueFromVectorParam(dati.get(i), 10).getValue();
          
          String token = login();
          int livEvento = 1;
          String errMsgEvento = null;
          String listaNomiSchede = "";
          String jsonInputString = "";
          Integer responseCode = null;
          HttpURLConnection conn = null;
          ImportazioneContrattiPCP contrattiPCP = null;
          List<Scheda> listaSchede = null;
          try { //GESTIONE TRACCIATURA

            contrattiPCP = new ImportazioneContrattiPCP();
            contrattiPCP.setCig(codcig);
            contrattiPCP.setIdAppalto(idappaltonpa);
            contrattiPCP.setDataEproc(creaOggettoDataEproc(ngara, codcig));
            Rup rup = new Rup();
            rup.setCodiceFiscale(cftec);
            rup.setNome(nometei);
            rup.setCognome(cogtei);
            rup.setEmail(ematec);

            String sysnomDecoded = null;
            if (sysnom != null && sysnom.trim().length() > 0) {
              ICriptazioneByte passwordICriptazioneByte = null;
              passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
                  ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), sysnom.getBytes(),
                  ICriptazioneByte.FORMATO_DATO_CIFRATO);
              sysnomDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
            }

            String sysuffappDesc = null;
            if(sysuffapp!= null) {
              sysuffappDesc = (String) this.sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ? ", new Object[]{"G_022",sysuffapp});
            }
            
            rup.setMatricola(sysnomDecoded);
            rup.setDirezione(doftec);
            rup.setSottostruttura(sysuffappDesc);
            rup.setPassword(null);
            contrattiPCP.setRup(rup);

            listaSchede = creaArraySchede(ngara, codcig);
            listaNomiSchede = listaSchede.stream()
                .map(Scheda::getCodice) // Estraggo la proprieta' codice
                .collect(Collectors.joining(","));
            
            List<Scheda> listaSchedeRipuliteDaEsecuzione = getSchedeRipuliteDaEsecuzione(listaSchede);
            contrattiPCP.setSchede(listaSchedeRipuliteDaEsecuzione);

            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", token);
            conn.setDoOutput(true);

            ObjectMapper objectMapper = new ObjectMapper();
            jsonInputString = objectMapper.writeValueAsString(contrattiPCP);

            // Scrivo il JSON nel body della richiesta
            OutputStream os = conn.getOutputStream();
            os.write(jsonInputString.getBytes());
            os.flush();
            errMsgEvento = jsonInputString;

          }catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw new Exception("Errore inaspettato request durante l'importazione del contratto PCP: "+e.getMessage(), e);
          } finally {

            try {
              LogEvento logEvento = new LogEvento();
              logEvento.setCodApplicazione("PG");
              logEvento.setLivEvento(livEvento);
              logEvento.setOggEvento(codcig);
              logEvento.setCodEvento("GA_NADC_REQ_MSPCP");
              String descrizione = "Invio richiesta a NEW ADC:  /api/ms/pcp ("+codcig+", data acq. "+dacqcig+"), Schede: "+listaNomiSchede;
              if(descrizione.length()>500) {
                descrizione = descrizione.substring(0, 500);
              }
              logEvento.setDescr(descrizione);
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }

          }

          try {
            responseCode = conn.getResponseCode();
            InputStream inputStream = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
              inputStream = conn.getInputStream();
            } else {
              inputStream = conn.getErrorStream();
            }
            String responseBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
            errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;
            
          }catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw new Exception("Errore inaspettato response durante l'importazione del contratto PCP: "+e.getMessage(), e);
          } finally {
            if(conn != null) {
              conn.disconnect();
            }
            if(token != null) {
              logout(token);
            }

            try {
              LogEvento logEvento1 = new LogEvento();
              logEvento1.setCodApplicazione("PG");
              logEvento1.setLivEvento(livEvento);
              logEvento1.setOggEvento(codcig);
              logEvento1.setCodEvento("GA_NADC_RES_MSPCP");
              logEvento1.setDescr("Risposta da NEW ADC:  /api/ms/pcp ("+codcig+", data acq. "+dacqcig+")");
              logEvento1.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento1);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }
        }
      } 
    } catch (Exception e) {
      throw new Exception("Errore durante l'importazione del contratto PCP: "+e.getMessage(), e);
    }
  }

  
  
  
  
  /**
   * 
   * @param mod = INSERIMENTO o AGGIORNAMENTO
   * @throws Exception
   */
  public void importDocuments(String mod) throws Exception {
    try {

      this.currentDateTime = LocalDateTime.now();

      this.previewDateTime = calculatePreviousDate(this.currentDateTime, getCronExpression());

      List<?> dati = getDatiDaMod(mod, "DOC");

      if (dati != null && !dati.isEmpty()) {
        for (int i = 0; i < dati.size(); i++) {
          String ngara = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
          String codcig = (String) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();

          List<Scheda> listaSchede = creaArraySchede(ngara, codcig);
          gestioneInvioDocumenti(listaSchede, ngara, codcig);
        }
      }
      eliminaDocAssociati();
    } catch (Exception e) {
      throw new Exception("Errore durante l'importazione del contratto PCP: "+e.getMessage(), e);
    }

  }
  
  public void importDocumentsPuntuale(String cig) throws Exception {
    try {

      List<?> dati = getDatiDaCig(cig);

      if (dati != null && !dati.isEmpty()) {
        for (int i = 0; i < dati.size(); i++) {
          String ngara = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
          String codcig = (String) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();

          List<Scheda> listaSchede = creaArraySchede(ngara, codcig);
          gestioneInvioDocumenti(listaSchede, ngara, codcig);
        }
      }
      eliminaDocAssociati();
    } catch (Exception e) {
      throw new Exception("Errore durante l'importazione del contratto PCP: "+e.getMessage(), e);
    }

  }
  
  
  
  
  private List<Scheda> getSchedeRipuliteDaEsecuzione(List<Scheda> listaSchede) {
    List<Scheda> listaSchedeRipulita = new ArrayList<>();
    for (Scheda scheda : listaSchede) {
      Scheda schedaPulita = scheda.deepCopy();
      schedaPulita.setCodice(schedaPulita.getCodice().replace("Scheda", "").replace("Type", ""));
      listaSchedeRipulita.add(schedaPulita);
    }
    return listaSchedeRipulita;
  }

  private List<?> getDatiDaMod(String mod, String provenienza) throws Exception {

    List<?> dati = null;
    String query = null;

    //java.sql.Date ieri = getDataMenoGiorni(1);

    if("INSERIMENTO".equals(mod)) {
      query = "select g.ngara, g.codcig, rup.doftec, y.sysnom, rup.nometei, "
          + "rup.cogtei, rup.cftec, rup.ematec, g.dacqcig, t.idappaltonpa, y.sysuffapp "
          + "from torn t "
          + "inner join gare g on g.codgar1 = t.codgar "
          + "inner join uffint u on t.cenint = u.codein  "
          + "left join tecni rup on rup.codtec = t.codrup "
          + "inner join usrsys y on y.syscf = rup.cftec "
          + "where g.ngara != g.codgar1 "
          + "and g.dacqcig is not null "
          + "and g.dacqcig >= ? ";
      
      //In questo caso se viene utilizzata la CronExpression, azzerando ore minuti e secondi si utilizzera' la giornata odierna
      LocalDateTime dataSenzaOreMinSec = this.previewDateTime.toLocalDate().atStartOfDay();
      
      dati = this.sqlManager.getListVector(query, new Object[] {dataSenzaOreMinSec});

    } else if("AGGIORNAMENTO".equals(mod)) {
      
      
      //lista dei cig da appalti esecuzione (test I0091ACDBE)
      
      List<String> listaCigAppEsec = getListaCigWs(getAuthorizationWs());
      List<String> listaLottiAnacEsecString = new ArrayList<>();
      
      if(listaCigAppEsec != null && !listaCigAppEsec.isEmpty()) {
        String listaCigFilter = listaCigAppEsec.stream()
            .map(cig -> "'" + cig + "'")
            .collect(Collectors.joining(","));

        List<?> listaLottiAnacEsec = this.sqlManager.getListVector("select ngara from gare where codcig in ("+listaCigFilter+") ", new Object[] {});
        
        for (int i = 0; i < listaLottiAnacEsec.size(); i++) {
          listaLottiAnacEsecString.add((String) SqlManager.getValueFromVectorParam(listaLottiAnacEsec.get(i), 0).getValue());
        }
      }
      
      
      //Modifiche alle tabelle del cig nella giornata
      query = "select distinct(g.ngara) "
          + "from W_LOGEVENTI w "
          + "inner join gare g on replace(g.codgar1,'$','') = w.oggevento and g.ngara != g.codgar1 "
          + "where w.dataora >= ? "
          + "and w.oggevento is not null ";
      
      //Verifica se ci sono schede nuove (non di rettifica) nella giornata
      query += "union "
          + "select distinct(g.ngara) "
          + "from g1npascheda s "
          + "inner join g1npaform f on f.id = s.idform "
          + "inner join torn t on t.codgar = f.codgar "
          + "inner join gare g on g.codgar1 = t.codgar and g.ngara != g.codgar1 "
          + "where s.dataconf is not null "
          + "and s.dataconf >= ? "
          + "and s.statonpa >= 30 ";
      
      //Verifica se ci sono schede di rettifica nella giornata
      query +=  "union "
          + "select distinct(g.ngara) "
          + "from g1npascheda s "
          + "inner join g1npaform f on f.id = s.idform "
          + "inner join torn t on t.codgar = f.codgar "
          + "inner join gare g on g.codgar1 = t.codgar and g.ngara != g.codgar1 "
          + "inner join W_LOGEVENTI w on replace(g.codgar1,'$','') = w.oggevento and g.ngara != g.codgar1 "
          + "where idschedanparet is not null "
          + "and w.dataora >= ? "
          + "and w.codevento like '%RETTIFICA_AVVISO%' ";
      
      Object[] obj = new Object[] {this.previewDateTime,this.previewDateTime,this.previewDateTime};
      
      if("DOC".equals(provenienza)) {
        query +=  "union "
            + "select distinct(g.ngara) "
            + "from gare g "
            + "    inner join garecont c1 on (g.ngara=c1.ngara and c1.ncont=1) or (c1.ngara=g.codgar1 and (c1.ngaral is null or c1.ngaral=g.ngara)) "
            + "    inner join g1stipula c on c.ngara=c1.ngara and c.ncont=c1.ncont and c.stato = 5 "
            + "    inner join g1docstipula dc on dc.idstipula=c.id and dc.statodoc in (2,4,5) "
            + "    left join tab1 t_fase on t_fase.tab1cod='A1181' and t_fase.tab1tip=dc.fase "
            + "    inner join w_docdig o on o.idprg='PG' and o.digent='G1DOCSTIPULA' and o.digkey1=CAST(dc.id AS VARCHAR(10)) "
            + "    inner join W_LOGEVENTI w on c.codstipula = w.oggevento "
            + "where w.dataora >= ? "
            + "and w.codevento like 'GA_STIPULA_DOC_%' ";
        obj = new Object[] {this.previewDateTime,this.previewDateTime,this.previewDateTime,this.previewDateTime};
      }
      
      List<?> listaLotti = this.sqlManager.getListVector(query, obj);
      
      List<String> listaLottiString = new ArrayList<>();
      if (listaLotti != null && !listaLotti.isEmpty()) {
        for (int i = 0; i < listaLotti.size(); i++) {
          listaLottiString.add((String) SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getValue());
        }
      }
      
      List<String> listaLottiSenzaDuplicati = mergeListsWithoutDuplicates(listaLottiString, listaLottiAnacEsecString);
      
      if(listaLottiSenzaDuplicati != null && !listaLottiSenzaDuplicati.isEmpty()) {
        String listalottiFilter = "";
        List<String> lottiString = listaLottiSenzaDuplicati.stream()
            .map(lotto -> "'" + lotto + "'") // Aggiunge gli apici singoli
            .collect(Collectors.toList());
        listalottiFilter = String.join(",", lottiString);
        
        query = "select g.ngara, g.codcig, rup.doftec, y.sysnom, rup.nometei, "
            + "rup.cogtei, rup.cftec, rup.ematec, g.dacqcig, t.idappaltonpa, y.sysuffapp "
            + "from torn t "
            + "inner join gare g on g.codgar1 = t.codgar "
            + "inner join uffint u on t.cenint = u.codein  "
            + "left join tecni rup on rup.codtec = t.codrup "
            + "inner join usrsys y on y.syscf = rup.cftec "
            + "where g.ngara in ("+ listalottiFilter  +") "
            + "and g.dacqcig is not null ";
        
        dati = this.sqlManager.getListVector(query, new Object[] {});
      }
    }

    return dati;
  }
  
  private List<?> getDatiDaCig(String cig) throws Exception {

    String query = "select g.ngara, g.codcig, rup.doftec, y.sysnom, rup.nometei, "
        + "rup.cogtei, rup.cftec, rup.ematec, g.dacqcig, t.idappaltonpa, y.sysuffapp "
        + "from torn t "
        + "inner join gare g on g.codgar1 = t.codgar "
        + "inner join uffint u on t.cenint = u.codein  "
        + "left join tecni rup on rup.codtec = t.codrup "
        + "inner join usrsys y on y.syscf = rup.cftec "
        + "where g.ngara != g.codgar1 "
        + "and g.codcig = ? ";

    return this.sqlManager.getListVector(query, new Object[] {cig});

  }

  public void invioAssociazione() throws Exception {
    try {
      // Endpoint per l'associazione dei contratti PCP
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);
      
      url = url + "/api/ms/sap/associa";

      String query1 = "select cig, codicesap, operazione from G1CONT_ERP where cig is not null and data_operazione >= ? ";

      List<?> dati = this.sqlManager.getListVector(query1, new Object[] {this.previewDateTime});
      if (dati != null && !dati.isEmpty()) {
        for (int i = 0; i < dati.size(); i++) {
          String cig = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
          String codicesap = (String) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();
          String operazione = (String) SqlManager.getValueFromVectorParam(dati.get(i), 2).getValue();
          
          String token = login();
          int livEvento = 1;
          String errMsgEvento = null;
          String jsonInputString = "";
          Integer responseCode = null;
          HttpURLConnection conn = null;
          DatiContratto datiContratto = null;
          try { //GESTIONE TRACCIATURA

            datiContratto = new DatiContratto();
            datiContratto.setCig(cig);
            datiContratto.setNumero_contratto(codicesap);
            if(operazione != null) {
              if("A".equals(operazione)) {
                datiContratto.setOperazione("LINK");
              }
              if("D".equals(operazione)) {
                datiContratto.setOperazione("UNLINK");
              }
            }
            
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", token);
            conn.setDoOutput(true);

            ObjectMapper objectMapper = new ObjectMapper();
            jsonInputString = objectMapper.writeValueAsString(datiContratto);

            // Scrivo il JSON nel body della richiesta
            OutputStream os = conn.getOutputStream();
            os.write(jsonInputString.getBytes());
            os.flush();
            errMsgEvento = jsonInputString;

          }catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
          } finally {

            try {
              LogEvento logEvento = new LogEvento();
              logEvento.setCodApplicazione("PG");
              logEvento.setLivEvento(livEvento);
              logEvento.setOggEvento(cig);
              logEvento.setCodEvento("GA_NADC_REQ_MSASSOCIA");
              String descrizione = "Invio richiesta a NEW ADC:  /api/ms/sap/associa ("+cig+")";
              logEvento.setDescr(descrizione);
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }

          }

          try {
            responseCode = conn.getResponseCode();
            InputStream inputStream = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
              inputStream = conn.getInputStream();
            } else {
              inputStream = conn.getErrorStream();
            }
            String responseBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
            errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;
          }catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
          } finally {
            if(conn != null) {
              conn.disconnect();
            }
            if(token != null) {
              logout(token);
            }

            try {
              LogEvento logEvento1 = new LogEvento();
              logEvento1.setCodApplicazione("PG");
              logEvento1.setLivEvento(livEvento);
              logEvento1.setOggEvento(cig);
              logEvento1.setCodEvento("GA_NADC_RES_MSASSOCIA");
              logEvento1.setDescr("Risposta da NEW ADC:  /api/ms/sap/associa ("+cig+")");
              logEvento1.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento1);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }
        }
      }
      
    } catch (Exception e) {
      throw new Exception("Errore durante l'associazione dei contratti: "+e.getMessage(), e);
    }
  }
  
  public void invioAssociazionePuntuale(String cig) throws Exception {
    try {
      // Endpoint per l'associazione dei contratti PCP
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);
      
      url = url + "/api/ms/sap/associa";

      String query1 = "select codicesap, operazione from G1CONT_ERP where cig = ? ";

      List<?> dati = this.sqlManager.getListVector(query1, new Object[] { cig });
      if (dati != null && !dati.isEmpty()) {
        for (int i = 0; i < dati.size(); i++) {
          String codicesap = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
          String operazione = (String) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();
          
          String token = login();
          int livEvento = 1;
          String errMsgEvento = null;
          String jsonInputString = "";
          Integer responseCode = null;
          HttpURLConnection conn = null;
          DatiContratto datiContratto = null;
          try { //GESTIONE TRACCIATURA

            datiContratto = new DatiContratto();
            datiContratto.setCig(cig);
            datiContratto.setNumero_contratto(codicesap);
            if(operazione != null) {
              if("A".equals(operazione)) {
                datiContratto.setOperazione("LINK");
              }
              if("D".equals(operazione)) {
                datiContratto.setOperazione("UNLINK");
              }
            }
            
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", token);
            conn.setDoOutput(true);

            ObjectMapper objectMapper = new ObjectMapper();
            jsonInputString = objectMapper.writeValueAsString(datiContratto);

            // Scrivo il JSON nel body della richiesta
            OutputStream os = conn.getOutputStream();
            os.write(jsonInputString.getBytes());
            os.flush();
            errMsgEvento = jsonInputString;

          }catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw new Exception("Errore inaspettato request durante l'invio associazione: "+e.getMessage(), e);
          } finally {

            try {
              LogEvento logEvento = new LogEvento();
              logEvento.setCodApplicazione("PG");
              logEvento.setLivEvento(livEvento);
              logEvento.setOggEvento(cig);
              logEvento.setCodEvento("GA_NADC_REQ_MSASSOCIA");
              String descrizione = "Invio richiesta a NEW ADC:  /api/ms/sap/associa ("+cig+")";
              logEvento.setDescr(descrizione);
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }

          }

          try {
            responseCode = conn.getResponseCode();
            InputStream inputStream = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
              inputStream = conn.getInputStream();
            } else {
              inputStream = conn.getErrorStream();
            }
            String responseBody = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
            errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;
          }catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw new Exception("Errore inaspettato response durante l'invio associazione: "+e.getMessage(), e);
          } finally {
            if(conn != null) {
              conn.disconnect();
            }
            if(token != null) {
              logout(token);
            }

            try {
              LogEvento logEvento1 = new LogEvento();
              logEvento1.setCodApplicazione("PG");
              logEvento1.setLivEvento(livEvento);
              logEvento1.setOggEvento(cig);
              logEvento1.setCodEvento("GA_NADC_RES_MSASSOCIA");
              logEvento1.setDescr("Risposta da NEW ADC:  /api/ms/sap/associa ("+cig+")");
              logEvento1.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento1);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }
        }
      }
      
    } catch (Exception e) {
      throw new Exception("Errore durante l'associazione dei contratti: "+e.getMessage(), e);
    }
  }

  public Contratto getContrattoWs(String codicesap) throws Exception {
    try {
      Contratto contratto = null;
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);

      url = url + "/api/ms/sap/contratto/" + codicesap;

      String token = login();
      int livEvento = 1;
      String errMsgEvento = null;
      Integer responseCode = null;
      HttpURLConnection conn = null;

      try { // GESTIONE TRACCIATURA

        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(false);

        errMsgEvento = "GET " + url;

      } catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(codicesap);
          logEvento.setCodEvento("GA_NADC_REQ_MSCONTRATTO");
          logEvento.setDescr("Invio richiesta a NEW ADC: /api/ms/sap/contratto (" + codicesap + ")");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }

      try {
        responseCode = conn.getResponseCode();
        InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();

        String responseBody = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));
        errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;

        if (responseCode == HttpURLConnection.HTTP_OK) {
          ObjectMapper mapper = new ObjectMapper();
          Response response = mapper.readValue(responseBody, Response.class);

          contratto = response.getData().getContratto();
        }

      } catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        if (conn != null) {
          conn.disconnect();
        }
        if (token != null) {
          logout(token);
        }

        try {
          LogEvento logEvento1 = new LogEvento();
          logEvento1.setCodApplicazione("PG");
          logEvento1.setLivEvento(livEvento);
          logEvento1.setOggEvento(codicesap);
          logEvento1.setCodEvento("GA_NADC_RES_MSCONTRATTO");
          logEvento1.setDescr("Risposta da NEW ADC: /api/ms/sap/contratto (" + codicesap + ")");
          logEvento1.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento1);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
      return contratto;

    } catch (Exception e) {
      throw new Exception("Errore durante l'associazione dei contratti: " + e.getMessage(), e);
    }
  }


  private void gestioneInvioDocumenti(List<Scheda> schede, String ngara, String codcig) throws Exception {
    DocumentoDaInviare doc = new DocumentoDaInviare();
    doc.setCig(codcig);
    doc.setCodiceGaraEproc(ngara);
    doc.setTipo("XML");

    //MEMORIZZO LE SCHEDE PROVENIENTI DA APPALTI ESECUZIONE A LIVELLO GLOBALE 
    //IN MODO TALE CHE SE HO GIA' LA LISTA NON RIESEGUO LA CHIAMATA
    List<Scheda> schedeEsec = null;
    
    //SCHEDE DI GARA
    if (schede != null && !schede.isEmpty()) {
      for (int i = 0; i < schede.size(); i++) {
        Scheda scheda = schede.get(i);
        String idprg = null;
        String iddocdig = null;
        if(scheda.getCodice_eform() != null) {
          DocumentoDaInviare docForEform = new DocumentoDaInviare(doc); // Creo una copia di doc
          
          if(scheda.getCodice().contains("Type")) { //CASO DI PROVENIENZA SCHEDA DA APPALTI ESECUZIONE
            if(schedeEsec == null) {
              schedeEsec = getSchedeEsecuzione(getAuthorizationWs(), codcig, Boolean.TRUE);
            }
            
            //Non serve controllase se schedeEsec e' diverso da null perche' getSchedeEsecuzione
            // al massimo torna un array vuoto
            String eformScheda = schedeEsec.stream()
                .filter(schedaEsec -> scheda.getIdSchedaNpa().equals(schedaEsec.getIdSchedaNpa()))
                .map(Scheda::getCodice_eform)
                .findFirst()
                .orElse(null);
            
            //Nella variabile scheda abbiamo al posto del file base64, l'idDocumento
            //Invece nella variabile schedaEsec abbiamo il file base64
            docForEform.setIdFile(Long.valueOf(scheda.getCodice_eform()));
            docForEform.setFile(Base64.getDecoder().decode(eformScheda));
            docForEform.setNomeFile(scheda.getCodice()+"_EformEsecuzione.xml");
            invioAllegato(docForEform);
          } else {
            idprg = scheda.getCodice_eform().split("-")[0];
            iddocdig = scheda.getCodice_eform().split("-")[1];
            docForEform.setTitolo("eform: "+ (String) this.sqlManager.getObject("select eform from G1CF_NPA where nome_scheda = ? ", new Object[]{scheda.getCodice()}));
            docForEform.setIdFile(Long.valueOf(iddocdig));
            getAltriDatiDoc(docForEform,idprg);
          }
        }
        if(scheda.getCodice_espd() != null) {
          DocumentoDaInviare docForEspd = new DocumentoDaInviare(doc); // Creo una copia di doc

          if(scheda.getCodice().contains("Type")) { //CASO DI PROVENIENZA SCHEDA DA APPALTI ESECUZIONE
            if(schedeEsec == null) {
              schedeEsec = getSchedeEsecuzione(getAuthorizationWs(), codcig, Boolean.TRUE);
            }
            
            //Non serve controllase se schedeEsec e' diverso da null perche' getSchedeEsecuzione
            // al massimo torna un array vuoto
            String espdScheda = schedeEsec.stream()
                .filter(schedaEsec -> scheda.getIdSchedaNpa().equals(schedaEsec.getIdSchedaNpa()))
                .map(Scheda::getCodice_espd)
                .findFirst()
                .orElse(null);
            //Nella variabile scheda abbiamo al posto del file base64, l'idDocumento
            //Invece nella variabile schedaEsec abbiamo il file base64
            docForEspd.setIdFile(Long.valueOf(scheda.getCodice_espd()));
            docForEspd.setFile(Base64.getDecoder().decode(espdScheda));
            docForEspd.setNomeFile(scheda.getCodice()+"_EspdEsecuzione.xml");
            invioAllegato(docForEspd);
          } /*else {
            idprg = scheda.getCodice_espd().split("-")[0];
            iddocdig = scheda.getCodice_espd().split("-")[1];
            docForEspd.setTitolo("Documento di gara unico europeo (DGUE)");
            docForEspd.setIdFile(Long.valueOf(iddocdig));
            getAltriDatiDoc(docForEspd,idprg);
          }*/
        }
      }
    }
    
    
    //PARTE RELATIVA AGLI INVII DEI DOCUMENTI DELLA GARA
    String query = getQueryListaDocumentiGara();
    List<DocumentoDaInviare> listaDocumentiDaInviare = null;
    List<?> datiGara = this.sqlManager.getListVector(query, new Object[] {ngara,codcig,codcig,codcig});
    if (datiGara != null && !datiGara.isEmpty()) {
      listaDocumentiDaInviare = new ArrayList<>();
      for (int i = 0; i < datiGara.size(); i++) {
        String codiceGaraEproc = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 0).getValue();
        String cig = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 1).getValue();
        Long idDocumento = (Long) SqlManager.getValueFromVectorParam(datiGara.get(i), 2).getValue();
        Long tipologia = (Long) SqlManager.getValueFromVectorParam(datiGara.get(i), 3).getValue();
        String descTipologia = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 4).getValue();
        Long numOrdine = (Long) SqlManager.getValueFromVectorParam(datiGara.get(i), 5).getValue();
        String tipo = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 6).getValue();
        //String archiviato = (String) SqlManager.getValueFromVectorParam(dati.get(i), 7).getValue();
        String docRifLotto = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 8).getValue();
        String titoloDocumento = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 9).getValue();
        String urlDocumento = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 10).getValue();
        String nomeFile = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 11).getValue();
        Long idFile = (Long) SqlManager.getValueFromVectorParam(datiGara.get(i), 12).getValue();
        String hash_file = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 13).getValue();
        String idprg = (String) SqlManager.getValueFromVectorParam(datiGara.get(i), 14).getValue();
        
        DocumentoDaInviare documentoDaInviare = new DocumentoDaInviare();
        documentoDaInviare.setCig(cig);
        documentoDaInviare.setCodiceGaraEproc(codiceGaraEproc);
        documentoDaInviare.setDescTipologia(descTipologia);
        documentoDaInviare.setDocRifLotto(docRifLotto);
        documentoDaInviare.setIdDocumento(idDocumento);
        documentoDaInviare.setNomeFile(nomeFile);
        documentoDaInviare.setNumOrdine(numOrdine);
        documentoDaInviare.setTipo(tipo);
        documentoDaInviare.setTipologia(tipologia);
        documentoDaInviare.setTitoloDocumento(titoloDocumento);
        documentoDaInviare.setUrlDocumento(urlDocumento);
        
        BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, idFile);
        if(digogg != null) {
          documentoDaInviare.setIdFile(idFile);
          documentoDaInviare.setFile(digogg.getStream());
        }
        
        listaDocumentiDaInviare.add(documentoDaInviare);
      }
      
      if(listaDocumentiDaInviare != null && !listaDocumentiDaInviare.isEmpty()) {
        for (int i = 0; i < listaDocumentiDaInviare.size(); i++) {
          invioAllegato(listaDocumentiDaInviare.get(i));
        }
      }
    }
    
    
    //PARTE RELATIVA AGLI INVII DEI DOCUMENTI DELLE STIPULE
    query = getQueryListaDocumentiStipule();
    List<?> datiStipule = this.sqlManager.getListVector(query, new Object[] {ngara,codcig});
    if (datiStipule != null && !datiStipule.isEmpty()) {
      List<List<DocumentoDaInviare>> listaStipuleDocumenti = new ArrayList<>();
      listaDocumentiDaInviare = new ArrayList<>();
      String codstipulaDiAppoggio = null;
      for (int i = 0; i < datiStipule.size(); i++) {
        String codiceGaraEproc = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 0).getValue();
        String cig = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 1).getValue();
        Long idDocumento = (Long) SqlManager.getValueFromVectorParam(datiStipule.get(i), 2).getValue();
        Long tipologia = (Long) SqlManager.getValueFromVectorParam(datiStipule.get(i), 3).getValue();
        String descTipologia = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 4).getValue();
        Long numOrdine = (Long) SqlManager.getValueFromVectorParam(datiStipule.get(i), 5).getValue();
        String tipo = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 6).getValue();
        //String archiviato = (String) SqlManager.getValueFromVectorParam(dati.get(i), 7).getValue();
        String docRifLotto = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 8).getValue();
        String titoloDocumento = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 9).getValue();
        String urlDocumento = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 10).getValue();
        String nomeFile = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 11).getValue();
        Long idFile = (Long) SqlManager.getValueFromVectorParam(datiStipule.get(i), 12).getValue();
        String hash_file = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 13).getValue();
        String idprg = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 14).getValue();
        String codstipula = (String) SqlManager.getValueFromVectorParam(datiStipule.get(i), 15).getValue();
        
        DocumentoDaInviare documentoDaInviare = new DocumentoDaInviare();
        documentoDaInviare.setCig(cig);
        documentoDaInviare.setCodiceGaraEproc(codstipula);
        documentoDaInviare.setDescTipologia(descTipologia);
        documentoDaInviare.setDocRifLotto(docRifLotto);
        documentoDaInviare.setIdDocumento(idDocumento);
        documentoDaInviare.setNomeFile(nomeFile);
        documentoDaInviare.setNumOrdine(numOrdine);
        documentoDaInviare.setTipo(tipo);
        documentoDaInviare.setTipologia(tipologia);
        documentoDaInviare.setTitoloDocumento(titoloDocumento);
        documentoDaInviare.setUrlDocumento(urlDocumento);
        
        BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, idFile);
        if(digogg != null) {
          documentoDaInviare.setIdFile(idFile);
          documentoDaInviare.setFile(digogg.getStream());
        }
        
        if(codstipulaDiAppoggio == null || (codstipulaDiAppoggio != null && codstipulaDiAppoggio.equals(codstipula))) {
          listaDocumentiDaInviare.add(documentoDaInviare);
          codstipulaDiAppoggio = codstipula;
        } else {
          codstipulaDiAppoggio = codstipula;
          listaStipuleDocumenti.add(listaDocumentiDaInviare);
          listaDocumentiDaInviare = new ArrayList<>();
          listaDocumentiDaInviare.add(documentoDaInviare);
        }
        
        if(i+1 >= datiStipule.size()) {
          listaStipuleDocumenti.add(listaDocumentiDaInviare);
        }
      }
      
      if(listaStipuleDocumenti != null && !listaStipuleDocumenti.isEmpty()) {
        for (int i = 0; i < listaStipuleDocumenti.size(); i++) {
          if(listaStipuleDocumenti.get(i) != null && !listaStipuleDocumenti.get(i).isEmpty()) {
            ByteArrayOutputStream zipBytes = createZip(listaStipuleDocumenti.get(i));
            /* Salva il file ZIP localmente per test
            try (FileOutputStream fos = new FileOutputStream("output_stipule_"+listaStipuleDocumenti.get(i).get(0).getCodiceGaraEproc()+".zip")) {
                fos.write(zipBytes.toByteArray());
            }
            */
            invioAllegatoZip(zipBytes, codcig, ngara, listaStipuleDocumenti.get(i).get(0).getCodiceGaraEproc());
          }
        }
      }
    }
  }

  private void getAltriDatiDoc(DocumentoDaInviare doc, String idprg) throws Exception {
    String query1 = "select w.digtipdoc, w.dignomdoc "
        + "from W_DOCDIG w "
        + "where w.idprg = ? "
        + "and w.iddocdig = ? ";

    Vector<?> datiRup = this.sqlManager.getVector(query1, new Object[]{idprg,doc.getIdFile()});
    if(datiRup!=null && !datiRup.isEmpty()){
      String digtipdoc = (String) SqlManager.getValueFromVectorParam(datiRup, 0).getValue();
      String dignomdoc = (String) SqlManager.getValueFromVectorParam(datiRup, 1).getValue();
      BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, doc.getIdFile());

      doc.setNomeFile(dignomdoc);
      doc.setFile(digogg.getStream());

      /*
      if(digtipdoc != null) {
        doc.setDescTipologia((String) this.sqlManager.getObject("select tab2d2 from tab2 where tab2cod='G_z22' and tab2tip = ? ", new Object[]{digtipdoc}));
      }
      */
      invioAllegato(doc);

    }

  }


  private List<Scheda> creaArraySchede(String ngara, String codcig) throws Exception {

    List<Scheda> schedeArray = new ArrayList<>();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    String query1 = "select gn.nome_scheda, gs.idschedanpa, gs.datainvio,gs.dataconf, "
        + "gs.textnpa,gs.idprg_eform,gs.iddoc_eform,gs.idprg_espd,gs.iddoc_espd "
        + "from gare g "
        + "inner join g1npaform gf on gf.codgar = g.codgar1 "
        + "inner join g1cf_npa gn on gn.id = gf.idcfnpa "
        + "inner join g1npascheda gs on gs.idform = gf.id "
        + "inner join g1npalottischeda l on l.ngara = g.ngara and gs.id = l.idscheda "
        + "where g.ngara = ? and g.ngara != g.codgar1 "
        + "and (gn.nome_scheda not like 'PL%' or gn.nome_scheda != 'P7_3' or gn.nome_scheda != 'P1_15_1') "
        + "and gs.statonpa >= 30 "
        + "and gn.modpres = 3 "
        + "union "
        + "select gn.nome_scheda, gs.idschedanpa, gs.datainvio, "
        + "gs.dataconf,gs.textnpa,gs.idprg_eform,gs.iddoc_eform,gs.idprg_espd,gs.iddoc_espd "
        + "from gare g "
        + "inner join g1npaform gf on gf.codgar = g.codgar1 "
        + "inner join g1cf_npa gn on gn.id = gf.idcfnpa "
        + "inner join g1npascheda gs on gs.idform = gf.id " 
        + "where g.ngara = ? and g.ngara != g.codgar1 "
        + "and (gn.nome_scheda not like 'PL%' or gn.nome_scheda != 'P7_3' or gn.nome_scheda != 'P1_15_1') "
        + "and gs.statonpa >= 30 "
        + "and gn.modpres = 1 ";

    List<?> dati = this.sqlManager.getListVector(query1, new Object[] {ngara,ngara});
    if (dati != null && !dati.isEmpty()) {
      for (int i = 0; i < dati.size(); i++) {
        String nomeScheda = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
        String idschedanpa = (String) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();
        Date datainvio = (Date) SqlManager.getValueFromVectorParam(dati.get(i), 2).getValue();
        Date dataconf = (Date) SqlManager.getValueFromVectorParam(dati.get(i), 3).getValue();
        String textnpa = (String) SqlManager.getValueFromVectorParam(dati.get(i), 4).getValue();
        String idprg_eform = (String) SqlManager.getValueFromVectorParam(dati.get(i), 5).getValue();
        Long iddoc_eform = (Long) SqlManager.getValueFromVectorParam(dati.get(i), 6).getValue();
        String idprg_espd = (String) SqlManager.getValueFromVectorParam(dati.get(i), 7).getValue();
        Long iddoc_espd = (Long) SqlManager.getValueFromVectorParam(dati.get(i), 8).getValue();

        //inserisco la scheda solo se questa esiste nella logeventi con il codice REQ_CREA_APPALTO o REQ_CREA_SCHEDA
        RupScheda rupScheda = getRupScheda(nomeScheda, ngara);
        if(rupScheda != null) {        
          Scheda scheda = new Scheda();
          scheda.setCodice(nomeScheda);
          scheda.setIdSchedaNpa(idschedanpa);
          if(dataconf != null) {
            scheda.setData_conferma(formatter.format(dataconf));
          }
          if(datainvio != null) {
            scheda.setData_invio(formatter.format(datainvio));
            scheda.setData_inserimento(formatter.format(datainvio));
          }
          
          scheda.setDati(new ObjectMapper().readTree(textnpa));
          if(idprg_eform != null && iddoc_eform != null) {
            scheda.setCodice_eform(idprg_eform+"-"+iddoc_eform);
          }
          if(idprg_espd != null && iddoc_espd != null) {
            scheda.setCodice_espd(idprg_espd+"-"+iddoc_espd);
          }
          scheda.setRup(rupScheda);

          schedeArray.add(scheda);
        }
      }
      
      //lista dei documenti recuperati da appalti esecuzione (chiamara rest attraverso il cig)
      schedeArray.addAll(getSchedeEsecuzione(getAuthorizationWs(), codcig, Boolean.FALSE));
    }

    return schedeArray;
  }
  
  /**
   * 
   * @param token
   * @param codcig
   * @param withFile boolean che definisce se la lista ha o meno il contenuto del file
   * @return lista schede esecuzione
   * @throws Exception
   */
  private List<Scheda> getSchedeEsecuzione(String token, String codcig, Boolean withFile) throws Exception {
    
    List<Scheda> schedeArray = new ArrayList<>();
    
    List<Map<String, String>> schedeEsecuzione = getDettaglioCigWs(token, codcig, withFile);
    
    for (Map<String, String> schedaMap : schedeEsecuzione) {
      Scheda scheda = new Scheda();
      scheda.setCodice(schedaMap.get("type"));
      scheda.setDati(new ObjectMapper().readTree(schedaMap.get("anacForm")));
      scheda.setData_inserimento(schedaMap.get("dataInserimento"));
      if(schedaMap.get("espd") != null) {
        if(Boolean.TRUE.equals(withFile)) {
          scheda.setCodice_espd(schedaMap.get("espd"));
        } else {
          scheda.setCodice_espd(schedaMap.get("idDocumento"));
        }          
      }
      if(schedaMap.get("eform") != null) {
        if(Boolean.TRUE.equals(withFile)) {
          scheda.setCodice_eform(schedaMap.get("eform"));
        } else {
          scheda.setCodice_eform(schedaMap.get("idDocumento"));
        }
      }
      scheda.setIdSchedaNpa(schedaMap.get("idScheda"));
      
      if(schedaMap.get("cfRup") != null && !schedaMap.get("cfRup").isEmpty()) {
          String getdatirup = "select nometei, cogtei, cftec "
              + "from tecni "
              + "where cftec = ? ";

          Vector<?> datiRup = this.sqlManager.getVector(getdatirup, new Object[]{schedaMap.get("cfRup")});
          if(datiRup!=null && !datiRup.isEmpty()){
            String nometei = (String) SqlManager.getValueFromVectorParam(datiRup, 0).getValue();
            String cogtei = (String) SqlManager.getValueFromVectorParam(datiRup, 1).getValue();
            String cftec = (String) SqlManager.getValueFromVectorParam(datiRup, 2).getValue();

            scheda.setRup(new RupScheda(cftec,nometei,cogtei));
          }
      }
      schedeArray.add(scheda);
    }
    return schedeArray;
    
  }

  private RupScheda getRupScheda(String nomeScheda, String ngara) throws SQLException {
    RupScheda rupScheda = null;

    String getidevento = "select max(l.idevento) "
        + "from gare g "
        + "inner join g1npaform gf on gf.codgar = g.codgar1 "
        + "inner join g1cf_npa gn on gn.id = gf.idcfnpa "
        + "inner join g1npascheda gs on gs.idform = gf.id " 
        + "inner join w_logeventi l on l.oggevento = replace(g.codgar1,'$','') "
        + "inner join usrsys y on y.syscon = l.syscon "
        + "left join tecni rup on rup.syscon = y.syscon "
        + "where g.ngara = ? and g.ngara != g.codgar1 "
        + "and ((gn.isiniz = '1' and l.codevento like '%REQ_CREA_APPALTO%') or "
        + "     (gn.isiniz is null and l.codevento like '%REQ_CREA_SCHEDA%') "
        + "    )  "
        + "and l.descr like '%("+nomeScheda+"%'"
        + "and gn.nome_scheda = ? "
        + "and gs.statonpa >= 30 ";

    Long idevento = (Long) this.sqlManager.getObject(getidevento, new Object[] {ngara,nomeScheda});

    if(idevento != null) {
      String getdatirup = "select rup.nometei, rup.cogtei, rup.cftec "
          + "from w_logeventi l "
          + "inner join usrsys y on y.syscon = l.syscon "
          + "left join tecni rup on rup.syscon = y.syscon "
          + "where idevento = ? ";

      Vector<?> datiRup = this.sqlManager.getVector(getdatirup, new Object[]{idevento});
      if(datiRup!=null && !datiRup.isEmpty()){
        String nometei = (String) SqlManager.getValueFromVectorParam(datiRup, 0).getValue();
        String cogtei = (String) SqlManager.getValueFromVectorParam(datiRup, 1).getValue();
        String cftec = (String) SqlManager.getValueFromVectorParam(datiRup, 2).getValue();

        rupScheda = new RupScheda(cftec,nometei,cogtei);
      }
    }

    return rupScheda;
  }

  private DataEproc creaOggettoDataEproc(String ngara, String codcig) throws SQLException {
    DataEproc dataEproc = null;
    String query = "select "
        + "    g.NGARA as codice_gara_eproc, "
        + "    case  "
        + "        when t.GARTEL='1' then 'Si' "
        + "        else 'No' "
        + "    end as proc_telematica, "
        + "    g.CODCIG as cig, "
        + "    t.NUMAVCP as numero_gara_anac, "
        + "    g.DACQCIG as data_acquisizione_cig, "
        + "    case  "
        + "        when g.CODCIG is null then 'ND' "
        + "        when (g.CODCIG like '$%' or g.CODCIG like '#%') then 'Esente' "
        + "        when (g.CODCIG like 'X%' or g.CODCIG like 'Y%'  or g.CODCIG like 'Z%') then 'smartCIG' "
        + "        else 'CIG' "
        + "    end as tipologia_cig, "
        + "    t_tipgen.tab1desc as tipo_appalto, "
        + "    t.destor as oggetto_gara, "
        + "    g.NOT_GAR as oggetto_lotto, "
        + "    sa.cfein as cf_amministrazione, "
        + "    sa.nomein as nome_amministrazione, "
        + "    rc.stato_gara as id_stato_cig, "
        + "    t_stato_gara.tab1desc as stato_cig, "
        + "    t_stato_simog.tab1desc as stato_cig_dettagliato, "
        + "    rc.data_conferma_gara as dt_perfez_cig, "
        + "    i.impapp as imp_base_gara, "
        + "    i.imprin as imp_rinnovo_contr, "
        + "    i.impserv as imp_servizi_analoghi, "
        + "    i.imppror as imp_proroga_tecnica, "
        + "    i.impaltro as imp_altre_opzioni, "
        + "    i.valmax as valore_massimo_stimato, "
        + "    p.datpub as dt_pubbl_piattaf, "
        + "    t.DTEOFF as dt_termine_pres_off, "
        + "    t.OTEOFF as hh_termine_pres_off, "
        + "    t.DTEPAR as dt_termine_pres_dom, "
        + "    t.OTEPAR as hh_termine_pres_dom, "
        + "    g.TIPGARG as id_tipo_procedura_eproc, "
        + "    t_TIPGARG.tab1desc as tipo_procedura_eproc, "
        + "    t_proc_simog.tab2tip as id_scelta_contraente_simog, "
        + "    t_proc_simog.tab2d1 as scelta_contraente_simog, "
        + "    t_proc_190.tab2tip as id_tipo_procedura_l190, "
        + "    t_proc_190.tab2d1 as tipo_procedura_l190, "
        + "    t_tipneg.tab1desc as cond_proc_negoz, "
        + "    c.catiga as cod_prestaizone_princ, "
        + "    ci.descat as desc_prestaizone_princ, "
        + "    lot.n_lotti as num_lotti, "
        + "    g.CODIGA as num_lotto, "
        + "    rup.nometei rup_nome, "
        + "    rup.cogtei rup_cognome, "
        + "    rup.cftec rup_cf, "
        + "    ra.nometei ra_nome, "
        + "    ra.cogtei ra_cognome, "
        + "    ra.cftec ra_cf, "
        + "    cpv.codcpv as cpv, "
        + "    t.ISTAUT as contr_anac_sa_gara, "
        + "    g.IDIAUT as contr_anac_oe_lotto, "
        + "    s.id as num_sospensioni, "
        + "    s.datini as dt_inizio_sosp, "
        + "    s.datfine as dt_fine_sosp, "
        + "    g.CRITLICG as id_criterio_aggiud, "
        + "    t_critlicg.tab1desc as crtierio_aggiud, "
        + "    g.DATTOA as dt_aggiudic, "
        + "    g.IAGGIU as imp_aggiudic, "
        + "    g.DATNEG as dt_mancata_agg, "
        + "    t_esineg.tab1desc as motivo_mancata_agg, "
        + "    g.codgar1 as codgar "
        + "from  "
        + "    torn t "
        + "    join gare g on g.codgar1=t.codgar "
        + "    left join tab1 t_tipgen on t_tipgen.tab1cod='A1007' and t_tipgen.tab1tip=t.TIPGEN "
        + "    join uffint sa on sa.codein=t.cenint "
        + "    left join V_W3GARE rc on rc.codgar=t.codgar "
        + "        left join tab1 t_stato_gara on t_stato_gara.tab1cod='A1171' and t_stato_gara.tab1tip=rc.stato_gara "
        + "        left join tab1 t_stato_simog on t_stato_simog.tab1cod='W3025' and t_stato_simog.tab1tip=rc.stato_simog "
        + "    join V_GARE_IMPORTI i on i.codgar=g.codgar1 and i.ngara=g.ngara "
        + "    left join PUBBLI p on p.codgar9=t.codgar and p.TIPPUB in (11,13,14) "
        + "    left join tab1 t_TIPGARG on t_TIPGARG.tab1cod='A2044' and t_TIPGARG.tab1tip=g.TIPGARG "
        + "    left join tab2 t_proc_simog on t_proc_simog.tab2cod='A1z11' and (','||t_proc_simog.tab2d2||',') like ('%,'||cast(g.TIPGARG as varchar(5))||',%') "
        + "    left join tab2 t_proc_190 on t_proc_190.tab2cod='A1z05' and (  (','||t_proc_190.tab2d2||',') like ('%,'||cast(g.TIPGARG as varchar(5))||',%') or cast(g.TIPGARG as varchar(5))=t_proc_190.tab2tip ) "
        + "    left join tab1 t_tipneg on t_tipneg.tab1cod='A1040' and t_tipneg.tab1tip=g.TIPNEG "
        + "    left join CATG c on c.ngara=g.ngara and c.ncatg='1' "
        + "    left join CAIS ci on ci.caisim=c.catiga "
        + "    join (select count(ngara) as n_lotti, codgar1 as codgar from gare where genere is null group by codgar1) lot on lot.codgar=t.codgar "
        + "    left join tecni rup on rup.codtec=t.codrup "
        + "    left join ( "
        + "                select y.codgar, y.codtec from GARTECNI y join (select codgar, max(numtec) as numtec from GARTECNI where inctec=1 group by codgar) x on x.codgar=y.codgar and x.numtec=y.numtec "
        + "              ) drp on drp.codgar=t.codgar "
        + "    left join tecni ra on ra.codtec=drp.codtec "
        + "    left join garcpv cpv on cpv.ngara=g.ngara and cpv.tipcpv='1' "
        + "    left join (select max(id) as id, min(datini) as datini, max(datfine) as datfine, codgar from GARSOSPE group by codgar) s on s.codgar=t.codgar "
        + "    left join tab1 t_critlicg on t_critlicg.tab1cod='A2081' and t_critlicg.tab1tip=g.CRITLICG "
        + "    left join tab1 t_esineg on t_esineg.tab1cod='A1088' and t_esineg.tab1tip=g.ESINEG "
        + "where g.ngara = ? "
        + "and g.CODCIG = ? ";

    Vector<?> datiDataEproc = this.sqlManager.getVector(query, new Object[]{ngara, codcig});
    if(datiDataEproc!=null && !datiDataEproc.isEmpty()){
      String codice_gara_eproc = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 0).getValue();
      String proc_telematica = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 1).getValue();
      String cig = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 2).getValue();
      String numero_gara_anac = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 3).getValue();
      Date data_acquisizione_cig = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 4).getValue();
      String tipologia_cig = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 5).getValue();
      String tipo_appalto = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 6).getValue();
      String oggetto_gara = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 7).getValue();
      String oggetto_lotto = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 8).getValue();
      String cf_amministrazione = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 9).getValue();
      String nome_amministrazione = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 10).getValue();
      Long id_stato_cig = (Long) SqlManager.getValueFromVectorParam(datiDataEproc, 11).getValue();
      String stato_cig = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 12).getValue();
      String stato_cig_dettagliato = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 13).getValue();
      Date dt_perfez_cig = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 14).getValue();
      Double imp_base_gara = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 15).getValue();
      Double imp_rinnovo_contr = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 16).getValue();
      Double imp_servizi_analoghi = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 17).getValue();
      Double imp_proroga_tecnica = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 18).getValue();
      Double imp_altre_opzioni = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 19).getValue();
      Double valore_massimo_stimato = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 20).getValue();
      Date dt_pubbl_piattaf = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 21).getValue();
      Date dt_termine_pres_off = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 22).getValue();
      String hh_termine_pres_off = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 23).getValue();
      Date dt_termine_pres_dom = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 24).getValue();
      String hh_termine_pres_dom = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 25).getValue();
      Long id_tipo_procedura_eproc = (Long) SqlManager.getValueFromVectorParam(datiDataEproc, 26).getValue();
      String tipo_procedura_eproc = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 27).getValue();
      String id_scelta_contraente_simog = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 28).getValue();
      String scelta_contraente_simog = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 29).getValue();
      String id_tipo_procedura_l190 = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 30).getValue();
      String tipo_procedura_l190 = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 31).getValue();
      String cond_proc_negoz = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 32).getValue();
      String cod_prestaizone_princ = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 33).getValue();
      String desc_prestaizone_princ = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 34).getValue();
      Long num_lotti = (Long) SqlManager.getValueFromVectorParam(datiDataEproc, 35).getValue();
      String num_lotto = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 36).getValue();
      String rup_nome = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 37).getValue();
      String rup_cognome = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 38).getValue();
      String rup_cf = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 39).getValue();
      String ra_nome = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 40).getValue();
      String ra_cognome = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 41).getValue();
      String ra_cf = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 42).getValue();
      String cpv = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 43).getValue();
      Double contr_anac_sa_gara = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 44).getValue();
      Double contr_anac_oe_lotto = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 45).getValue();
      Long num_sospensioni = (Long) SqlManager.getValueFromVectorParam(datiDataEproc, 46).getValue();
      Date dt_inizio_sosp = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 47).getValue();
      Date dt_fine_sosp = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 48).getValue();
      Long id_criterio_aggiud = (Long) SqlManager.getValueFromVectorParam(datiDataEproc, 49).getValue();
      String crtierio_aggiud = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 50).getValue();
      Date dt_aggiudic = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 51).getValue();
      Double imp_aggiudic = (Double) SqlManager.getValueFromVectorParam(datiDataEproc, 52).getValue();
      Date dt_mancata_agg = (Date) SqlManager.getValueFromVectorParam(datiDataEproc, 53).getValue();
      String motivo_mancata_agg = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 54).getValue();
      String codgar = (String) SqlManager.getValueFromVectorParam(datiDataEproc, 55).getValue();

      dataEproc = new DataEproc();
      Anagrafica anagrafica = new Anagrafica();
      Appalto appalto = new Appalto();
      appalto.setCf_amministrazione(cf_amministrazione);
      appalto.setCig(cig);
      appalto.setCodice_gara_eproc(codice_gara_eproc);
      appalto.setCond_proc_negoz(cond_proc_negoz);
      appalto.setCpv(cpv);
      appalto.setCrtierio_aggiud(crtierio_aggiud);
      appalto.setData_acquisizione_cig(data_acquisizione_cig);
      appalto.setDt_aggiudic(dt_aggiudic);
      appalto.setDt_perfez_cig(dt_perfez_cig);
      appalto.setDt_pubbl_piattaf(dt_pubbl_piattaf);
      appalto.setDt_termine_pres_off(dt_termine_pres_off);
      appalto.setHh_termine_pres_off(hh_termine_pres_off);
      appalto.setId_criterio_aggiud(id_criterio_aggiud);
      if(id_scelta_contraente_simog != null) {
        appalto.setId_scelta_contraente_simog(Integer.parseInt(id_scelta_contraente_simog));
      }
      appalto.setId_stato_cig(id_stato_cig);
      appalto.setId_tipo_procedura_eproc(id_tipo_procedura_eproc);
      if(id_tipo_procedura_l190 != null) {
        appalto.setId_tipo_procedura_l190(Integer.parseInt(id_tipo_procedura_l190));
      }
      appalto.setImp_aggiudic(imp_aggiudic);
      appalto.setImp_base_gara(imp_base_gara);
      appalto.setNome_amministrazione(nome_amministrazione);
      appalto.setNum_lotti(num_lotti);
      appalto.setOggetto_lotto(oggetto_lotto);
      appalto.setProc_telematica(proc_telematica);
      appalto.setRup_cf(rup_cf);
      appalto.setRup_cognome(rup_cognome);
      appalto.setRup_nome(rup_nome);
      appalto.setScelta_contraente_simog(scelta_contraente_simog);
      appalto.setStato_cig(stato_cig);
      appalto.setStato_cig_dettagliato(stato_cig_dettagliato);
      appalto.setTipo_appalto(tipo_appalto);
      appalto.setTipo_procedura_eproc(tipo_procedura_eproc);
      appalto.setTipo_procedura_l190(tipo_procedura_l190);
      appalto.setTipologia_cig(tipologia_cig);
      appalto.setValore_massimo_stimato(valore_massimo_stimato);
      appalto.setStipule(getListaStipule(ngara));
      anagrafica.setAppalto(appalto);
      anagrafica.setCreazione(getEventoCreazione(codgar));
      anagrafica.setAggiornamento(getEventoAggiornamento(codgar));
      anagrafica.setDocumenti(getListaDocumenti(ngara, codcig));
      dataEproc.setAnagrafica(anagrafica);

    }
    return dataEproc;
  }

  private List<Stipula> getListaStipule(String ngara) throws SQLException {
    List<Stipula> stipulaArray = null;

    String query1 = "SELECT "
        + "    c.id, "
        + "    c.ngara as codice_gara_eproc, "
        + "    c.ncont, "
        + "    c.codstipula as cod_stipula, "
        + "    c.oggetto as oggetto_contratto, "
        + "    c.impstipula as imp_contratto, "
        + "    t_tiatto.tab1desc as tipo_contratto, "
        + "    c.nrepat as num_contratto, "
        + "    c.daatto as dt_contratto, "
        + "    u1.sysute as creato_da, "
        + "    u2.sysute as contract_manager, "
        + "    t_stato.tab1desc as stato, "
        + "    gc.nometei gc_nome, "
        + "    gc.cogtei gc_cognome, "
        + "    gc.cftec gc_cf "
        + "FROM "
        + "    gare l "
        + "    left join garecont c1 on c1.ngara=l.ngara "
        + "    left join g1stipula c on c.ngara=c1.ngara and c.ncont=c1.ncont "
        + "    left join tab1 t_tiatto on t_tiatto.tab1cod='A2042' and t_tiatto.tab1tip=c.tiatto "
        + "    left join usrsys u1 on u1.syscon=c.syscon "
        + "    left join usrsys u2 on u2.syscon=c.assegnatario "
        + "    left join tab1 t_stato on t_stato.tab1cod='A1180' and t_stato.tab1tip=c.stato "
        + "    left join tecni gc on gc.codtec=c.coddec "
        + "    where c.ngara = ? ";

    List<?> dati = this.sqlManager.getListVector(query1, new Object[] {ngara});
    if (dati != null && !dati.isEmpty()) {
      stipulaArray = new ArrayList<>();
      for (int i = 0; i < dati.size(); i++) {
        Long id = (Long) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
        String codice_gara_eproc = (String) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();
        Long ncont = (Long) SqlManager.getValueFromVectorParam(dati.get(i), 2).getValue();
        String cod_stipula = (String) SqlManager.getValueFromVectorParam(dati.get(i), 3).getValue();
        String oggetto_contratto = (String) SqlManager.getValueFromVectorParam(dati.get(i), 4).getValue();
        Double imp_contratto = (Double) SqlManager.getValueFromVectorParam(dati.get(i), 5).getValue();
        String tipo_contratto = (String) SqlManager.getValueFromVectorParam(dati.get(i), 6).getValue();
        String num_contratto = (String) SqlManager.getValueFromVectorParam(dati.get(i), 7).getValue();
        Date dt_contratto = (Date) SqlManager.getValueFromVectorParam(dati.get(i), 8).getValue();
        String creato_da = (String) SqlManager.getValueFromVectorParam(dati.get(i), 9).getValue();
        String contract_manager = (String) SqlManager.getValueFromVectorParam(dati.get(i), 10).getValue();
        String stato = (String) SqlManager.getValueFromVectorParam(dati.get(i), 11).getValue();
        String gc_nome = (String) SqlManager.getValueFromVectorParam(dati.get(i), 12).getValue();
        String gc_cognome = (String) SqlManager.getValueFromVectorParam(dati.get(i), 13).getValue();
        String gc_cf = (String) SqlManager.getValueFromVectorParam(dati.get(i), 14).getValue();

        Stipula stipula = new Stipula();
        stipula.setCod_stipula(cod_stipula);
        stipula.setCodice_gara_eproc(codice_gara_eproc);
        stipula.setContract_manager(contract_manager);
        stipula.setCreato_da(creato_da);
        stipula.setDt_contratto(dt_contratto);
        stipula.setId(id);
        stipula.setImp_contratto(imp_contratto);
        stipula.setNcont(ncont);
        stipula.setNum_contratto(num_contratto);
        stipula.setOggetto_contratto(oggetto_contratto);
        stipula.setStato(stato);
        stipula.setTipo_contratto(tipo_contratto);

        stipulaArray.add(stipula);
      }
    }

    return stipulaArray;
  }

  private Creazione getEventoCreazione(String codgar) throws SQLException {
    codgar = codgar.replace("$", "");
    Creazione creazione = new Creazione();
    String query = "select "
        + "    l.idevento, "
        + "    l.dataora, "
        + "    l.oggevento, "
        + "    l.codevento, "
        + "    l.syscon, "
        + "    u.sysute, "
        + "    u.syslogin "
        + "from  "
        + "    w_logeventi l  "
        + "    join usrsys u on u.syscon=l.syscon  "
        + "where  "
        + "    l.codevento in ('GA_CREAZIONE_PROCEDURA_DACOPIA','GA_CREAZIONE_PROCEDURA') "
        + "    and l.oggevento is not null and oggevento !='' "
        + "    and l.oggevento = ? ";
    Vector<?> datiCreazione = this.sqlManager.getVector(query, new Object[]{codgar});
    if(datiCreazione!=null && !datiCreazione.isEmpty()){
      Long idevento = (Long) SqlManager.getValueFromVectorParam(datiCreazione, 0).getValue();
      Date dataora = (Date) SqlManager.getValueFromVectorParam(datiCreazione, 1).getValue();
      String oggevento = (String) SqlManager.getValueFromVectorParam(datiCreazione, 2).getValue();
      String codevento = (String) SqlManager.getValueFromVectorParam(datiCreazione, 3).getValue();
      Long syscon = (Long) SqlManager.getValueFromVectorParam(datiCreazione, 4).getValue();
      String sysute = (String) SqlManager.getValueFromVectorParam(datiCreazione, 5).getValue();
      String syslogin = (String) SqlManager.getValueFromVectorParam(datiCreazione, 6).getValue();

      creazione.setCodevento(codevento);
      creazione.setDataora(dataora);
      creazione.setIdevento(idevento);
      creazione.setOggevento(oggevento);
      creazione.setSyscon(syscon);
      creazione.setSyslogin(syslogin);
      creazione.setSysute(sysute);

    }
    return creazione;
  }

  private Aggiornamento getEventoAggiornamento(String codgar) throws SQLException {
    codgar = codgar.replace("$", "");
    Aggiornamento aggiornamento = new Aggiornamento();
    String query = "select "
        + "    l.idevento, "
        + "    l.dataora, "
        + "    l.oggevento, "
        + "    l.codevento, "
        + "    l.syscon, "
        + "    u.sysute, "
        + "    u.syslogin "
        + "from  "
        + "    w_logeventi l "
        + "    join ( "
        + "            select max(idevento) as idevento, oggevento from w_logeventi  "
        + "            where oggevento is not null and oggevento !='' "
        + "            and (codevento not like 'GA_ACCESSO_%' and codevento not like 'GA_LETTURA_%' and codevento not like '%DOWNLOAD_%') "
        + "            group by oggevento "
        + "        ) x on x.idevento=l.idevento and x.oggevento=l.oggevento "
        + "    join usrsys u on u.syscon=l.syscon "
        + "where l.oggevento = ? ";
    Vector<?> datiAggiornamento = this.sqlManager.getVector(query, new Object[]{codgar});
    if(datiAggiornamento!=null && !datiAggiornamento.isEmpty()){
      Long idevento = (Long) SqlManager.getValueFromVectorParam(datiAggiornamento, 0).getValue();
      Date dataora = (Date) SqlManager.getValueFromVectorParam(datiAggiornamento, 1).getValue();
      String oggevento = (String) SqlManager.getValueFromVectorParam(datiAggiornamento, 2).getValue();
      String codevento = (String) SqlManager.getValueFromVectorParam(datiAggiornamento, 3).getValue();
      Long syscon = (Long) SqlManager.getValueFromVectorParam(datiAggiornamento, 4).getValue();
      String sysute = (String) SqlManager.getValueFromVectorParam(datiAggiornamento, 5).getValue();
      String syslogin = (String) SqlManager.getValueFromVectorParam(datiAggiornamento, 6).getValue();

      aggiornamento.setCodevento(codevento);
      aggiornamento.setDataora(dataora);
      aggiornamento.setIdevento(idevento);
      aggiornamento.setOggevento(oggevento);
      aggiornamento.setSyscon(syscon);
      aggiornamento.setSyslogin(syslogin);
      aggiornamento.setSysute(sysute);

    }
    return aggiornamento;
  }

  private List<Documento> getListaDocumenti(String ngara, String codcig) throws SQLException {
    List<Documento> listaDocumenti = new ArrayList<>();
    String query = getQueryListaDocumentiGara();
    List<?> datiCreazione = this.sqlManager.getListVector(query, new Object[]{ngara, codcig, codcig, codcig});
    if(datiCreazione!=null && !datiCreazione.isEmpty()){
      for (int i = 0; i < datiCreazione.size(); i++) {
        String codice_gara_eproc = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 0).getValue();
        String cig = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 1).getValue();
        Long id_documento = (Long) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 2).getValue();
        Long tipologia = (Long) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 3).getValue();
        String desc_tipologia = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 4).getValue();
        Long num_ordine = (Long) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 5).getValue();
        String tipo = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 6).getValue();
        String archiviato = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 7).getValue();
        String doc_rif_lotto = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 8).getValue();
        String titolo_documento = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 9).getValue();
        String url_documento = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 10).getValue();
        String nome_file = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 11).getValue();
        Long id_file = (Long) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 12).getValue();
        String hash_file = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 13).getValue();
        String idprg = (String) SqlManager.getValueFromVectorParam(datiCreazione.get(i), 14).getValue();
        
        Documento documento = new Documento();
        documento.setArchiviato(archiviato);
        documento.setCig(cig);
        documento.setCodice_gara_eproc(codice_gara_eproc);
        documento.setDesc_tipologia(desc_tipologia);
        documento.setDoc_rif_lotto(doc_rif_lotto);
        documento.setHash_file(hash_file);
        documento.setId_documento(id_documento);
        documento.setId_file(id_file);
        documento.setNome_file(nome_file);
        documento.setNum_ordine(num_ordine);
        documento.setTipologia(tipologia);
        documento.setTitolo_documento(titolo_documento);

        listaDocumenti.add(documento);
      }
    }
    return listaDocumenti;
  }


  private void invioAllegato(DocumentoDaInviare doc) throws Exception {

    try {
      // Endpoint per l'importazione dei contratti PCP
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);
      
      url = url + "/api/ms/eproc";
      String token = login();
      int livEvento = 1;
      String errMsgEvento = null;
      Integer responseCode = null;
      HttpURLConnection conn = null;
      StringBuilder requestContent = new StringBuilder();
      try { //GESTIONE TRACCIATURA

        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", token);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=---BOUNDARY");
        conn.setDoOutput(true);
        
        OutputStream os = conn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);

        //Boundary da usare nel multipart
        String boundary = "---BOUNDARY";

        addFormField(writer, requestContent, boundary, "cig", doc.getCig());
        addFormField(writer, requestContent, boundary, "titolo", doc.getTitolo());
        addFormField(writer, requestContent, boundary, "tipo", doc.getTipo());
        addFormField(writer, requestContent, boundary, "urlDocumento", doc.getUrlDocumento());
        addFormField(writer, requestContent, boundary, "idDocumento", String.valueOf(doc.getIdDocumento()));
        addFormField(writer, requestContent, boundary, "codiceGaraEproc", doc.getCodiceGaraEproc());
        addFormField(writer, requestContent, boundary, "docRifLotto", doc.getDocRifLotto());
        addFormField(writer, requestContent, boundary, "numOrdine", String.valueOf(doc.getNumOrdine()));
        addFormField(writer, requestContent, boundary, "tipologia", String.valueOf(doc.getTipologia()));
        addFormField(writer, requestContent, boundary, "titoloDocumento", doc.getTitoloDocumento());
        addFormField(writer, requestContent, boundary, "descTipologia", doc.getDescTipologia());
        addFormField(writer, requestContent, boundary, "nomeFile", doc.getNomeFile());
        
        if(doc.getIdFile() != null) {
          addFormField(writer, requestContent, boundary, "idFile", String.valueOf(doc.getIdFile()));
          //Aggiungo il file al multipart
          addFilePart(writer, os, requestContent, boundary, "file", doc.getFile(), doc.getNomeFile());
        }

        writer.append("--" + boundary + "--").append("\r\n");
        writer.flush();
        writer.close();

        os.flush();
        os.close();
        
        errMsgEvento = requestContent.toString();
        
      }catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {

        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(doc.getCig());
          logEvento.setCodEvento("GA_NADC_REQ_MSEPROC");
          logEvento.setDescr("Invio richiesta a NEW ADC:  /api/ms/eproc ("+doc.getCig()+")");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }

      }

      try {
        
        responseCode = conn.getResponseCode();
        InputStream inputStream = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
          inputStream = conn.getInputStream();
        } else {
          inputStream = conn.getErrorStream();
        }
        String responseBody = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));
        errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;

      }catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        if(conn!=null) {
          conn.disconnect();
        }
        if(token != null) {
          logout(token);
        }

        try {
          LogEvento logEvento1 = new LogEvento();
          logEvento1.setCodApplicazione("PG");
          logEvento1.setLivEvento(livEvento);
          logEvento1.setOggEvento(doc.getCig());
          logEvento1.setCodEvento("GA_NADC_RES_MSEPROC");
          logEvento1.setDescr("Invio richiesta a NEW ADC:  /api/ms/eproc ("+doc.getCig()+")");
          logEvento1.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento1);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }

    } catch (Exception e) {
      throw new Exception("Errore durante l'invio del documento: "+e.getMessage(), e);
    }

  }

  private void invioAllegatoZip(ByteArrayOutputStream zipBytes, String codcig, String ngara, String codStipula) throws Exception {

    try {
      // Endpoint per l'importazione dei contratti PCP
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);
      
      url = url + "/api/ms/eproc";
      String token = login();
      int livEvento = 1;
      String errMsgEvento = null;
      Integer responseCode = null;
      HttpURLConnection conn = null;
      StringBuilder requestContent = new StringBuilder();
      try { //GESTIONE TRACCIATURA

        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", token);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=---BOUNDARY");
        conn.setDoOutput(true);
        
        OutputStream os = conn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);

        //Boundary da usare nel multipart
        String boundary = "---BOUNDARY";

        addFormField(writer, requestContent, boundary, "cig", codcig);
        //addFormField(writer, requestContent, boundary, "titolo", "Doc di gara");
        //addFormField(writer, requestContent, boundary, "tipo", doc.getTipo());
        //addFormField(writer, requestContent, boundary, "urlDocumento", doc.getUrlDocumento());
        addFormField(writer, requestContent, boundary, "idDocumento", String.valueOf(generateLongWithPrefix(codStipula)));
        addFormField(writer, requestContent, boundary, "codiceGaraEproc", ngara);
        //addFormField(writer, requestContent, boundary, "docRifLotto", doc.getDocRifLotto());
        //addFormField(writer, requestContent, boundary, "numOrdine", String.valueOf(doc.getNumOrdine()));
        //addFormField(writer, requestContent, boundary, "tipologia", String.valueOf(doc.getTipologia()));
        //addFormField(writer, requestContent, boundary, "titoloDocumento", "Documenti di gara");
        //addFormField(writer, requestContent, boundary, "descTipologia", doc.getDescTipologia());
        addFormField(writer, requestContent, boundary, "nomeFile", codStipula+".zip");
        
        //Aggiungo il file al multipart
        addFilePart(writer, os, requestContent, boundary, "file", zipBytes.toByteArray(), codStipula+".zip");
        
        writer.append("--" + boundary + "--").append("\r\n");
        writer.flush();
        writer.close();

        os.flush();
        os.close();
        
        errMsgEvento = requestContent.toString();
        
      }catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {

        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(codcig);
          logEvento.setCodEvento("GA_NADC_REQ_MSEPROC");
          logEvento.setDescr("Invio richiesta a NEW ADC:  /api/ms/eproc ("+codcig+")");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }

      }

      try {
        
        responseCode = conn.getResponseCode();
        InputStream inputStream = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
          inputStream = conn.getInputStream();
        } else {
          inputStream = conn.getErrorStream();
        }
        String responseBody = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));
        errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;

      }catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        if(conn!=null) {
          conn.disconnect();
        }
        if(token != null) {
          logout(token);
        }

        try {
          LogEvento logEvento1 = new LogEvento();
          logEvento1.setCodApplicazione("PG");
          logEvento1.setLivEvento(livEvento);
          logEvento1.setOggEvento(codcig);
          logEvento1.setCodEvento("GA_NADC_RES_MSEPROC");
          logEvento1.setDescr("Invio richiesta a NEW ADC:  /api/ms/eproc ("+codcig+")");
          logEvento1.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento1);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }

    } catch (Exception e) {
      throw new Exception("Errore durante l'invio del documento: "+e.getMessage(), e);
    }

  }

  
  private String getQueryListaDocumentiGara() {
    return "select "
        + " g.NGARA as codice_gara_eproc, "
        + " g.CODCIG as cig, "
        + " d.NORDDOCG as id_documento, "
        + " d.tipologia as tipologia, "
        + " gpubb.nome as desc_tipologia, "
        + " d.numord as num_ordine, "
        + " case "
        + "     when d.busta=4 then 'Prequalifica' "
        + "     when d.busta=1 then 'Amministrativa' "
        + "     when d.busta=2 then 'Tecnica' "
        + "     when d.busta=3 then 'Economica' "
        + "     else cast(d.busta as varchar(20)) "
        + " end as tipo, "
        + " case "
        + "     when d.isarchi = '1' then 'Si' "
        + "     else 'No' "
        + " end archiviato, "
        + " d.ngara as doc_rif_lotto, "
        + " d.descrizione as titolo_documento, "
        + " d.urldoc as url_documento, "
        + " o.dignomdoc as nome_file, "
        + " d.iddocdg as id_file, "
        + " md5(o.digogg)::text as hash_file, "
        + " d.idprg as idprg "
        + " from "
        + " documgara d "
        + " join gare g on g.codgar1=d.codgar "
        + " left join w_docdig o on o.idprg=d.idprg and o.iddocdig=d.iddocdg "
        + " left join G1CF_PUBB gpubb on gpubb.ID = d.TIPOLOGIA "
        + " where g.ngara = ? "
        + " and g.codcig = ? "
        + " union "
        + "select "
        + " c0.c0akey1 as codice_gara_eproc, "
        + " g.codcig as cig, "
        + " o.iddocdig as id_documento, "
        + " CAST(null as NUMERIC(12)) as tipologia, "
        + " CAST(null as VARCHAR(2)) as desc_tipologia, "
        + " CAST(null as NUMERIC(7)) as num_ordine, "
        + " 'Doc. associato di gara' as tipo, "
        + " CAST(null as VARCHAR(2)) as archiviato, "
        + " g.ngara as doc_rif_lotto, "
        + " c0.c0atit  as titolo_documento, "
        + " CAST(null as VARCHAR(2)) as url_documento, "
        + " o.dignomdoc as nome_file, "
        + " o.iddocdig as id_file, "
        + " md5(o.digogg)::text as hash_file,"
        + " o.idprg as idprg "
        + "from  "
        + " c0oggass c0 "
        + " join w_docdig o on o.idprg='PG' and o.digent='C0OGGASS' and o.digkey1=CAST(c0.c0acod AS VARCHAR(10))"
        + " join gare g on replace(g.codgar1,'$','')=c0.c0akey1 "
        + "where c0.c0aprg='PG' "
        + " and c0.c0aent in ('TORN','GARE') "
        + " and g.codcig = ? "
        + "union "
        + "select "
        + " c.codstipula as codice_gara_eproc, "
        + " g.codcig as cig, "
        + " o.iddocdig as id_documento, "
        + " CAST(null as NUMERIC(12)) as tipologia, "
        + " CAST(null as VARCHAR(2)) as desc_tipologia, "
        + " CAST(null as NUMERIC(7)) as num_ordine, "
        + " 'Doc. associato a stipula' as tipo, "
        + " CAST(null as VARCHAR(2)) as archiviato, "
        + " g.ngara as doc_rif_lotto, "
        + " c0.c0atit  as titolo_documento, "
        + " CAST(null as VARCHAR(2)) as url_documento, "
        + " o.dignomdoc as nome_file, "
        + " o.iddocdig as id_file, "
        + " md5(o.digogg)::text as hash_file, "
        + " o.idprg as idprg "
        + "from "
        + " c0oggass c0 "
        + " join w_docdig o on o.idprg='PG' and o.digent='C0OGGASS' and o.digkey1=CAST(c0.c0acod AS VARCHAR(10)) "
        + " join g1stipula c on cast(c.id as varchar(20))=c0.c0akey1 "
        + " join garecont c1 on c1.ngara=c.ngara and c1.ncont=c.ncont "
        + " join gare g on (g.ngara=c1.ngara and c1.ncont=1) or (c1.ngara=g.codgar1 and (c1.ngaral is null or c1.ngaral=g.ngara)) "
        + "where c0.c0aprg='PG' "
        + " and c0.c0aent ='G1STIPULA' "
        + " and g.codcig = ? ";
  }
  
  
  private String getQueryListaDocumentiStipule() {
    return " select "
        + "    g.ngara as codice_gara_eproc, "
        + "    g.codcig as cig, "
        + "    dc.id as id_documento, "
        + "    CAST(null as NUMERIC(12)) as tipologia, "
        + "    CAST(null as VARCHAR(2)) as desc_tipologia, "
        + "    dc.numord as num_ordine, "
        + "    CAST('6 - Doc. contratto' as VARCHAR(50)) as tipo, "
        + "    CAST(null as VARCHAR(2)) as archiviato, "
        + "    g.ngara as doc_rif_lotto, "
        + "    coalesce(dc.titolo,'') || ' - ' || coalesce(dc.descrizione,'') || ' (' || coalesce(t_fase.tab1desc,'FASE ND') || ')'  as titolo_documento, "
        + "    CAST(null as VARCHAR(2)) as url_documento, "
        + "    o.dignomdoc as nome_file, "
        + "    o.iddocdig as id_file, "
        + "    md5(o.digogg)::text as hash_file, "
        + "    o.idprg as idprg,"
        + "    c.codstipula "
        + " from "
        + "    gare g "
        + "    join garecont c1 on (g.ngara=c1.ngara and c1.ncont=1) or (c1.ngara=g.codgar1 and (c1.ngaral is null or c1.ngaral=g.ngara)) "
        + "    join g1stipula c on c.ngara=c1.ngara and c.ncont=c1.ncont and c.stato = 5 "
        + "    join g1docstipula dc on dc.idstipula=c.id and dc.statodoc in (2,4,5) " 
        + "    left join tab1 t_fase on t_fase.tab1cod='A1181' and t_fase.tab1tip=dc.fase "
        + "    join w_docdig o on o.idprg='PG' and o.digent='G1DOCSTIPULA' and o.digkey1=CAST(dc.id AS VARCHAR(10)) "
        + " where g.ngara = ? "
        + " and g.codcig = ? "
        + " and c.codstipula in (select Distinct(w.oggevento) "
        + "                      from gare g "
        + "                         join garecont c1 on (g.ngara=c1.ngara and c1.ncont=1) or (c1.ngara=g.codgar1 and (c1.ngaral is null or c1.ngaral=g.ngara)) "
        + "                         join g1stipula c on c.ngara=c1.ngara and c.ncont=c1.ncont and c.stato = 5 "
        + "                         join g1docstipula dc on dc.idstipula=c.id and dc.statodoc in (2,4,5) "
        + "                         left join tab1 t_fase on t_fase.tab1cod='A1181' and t_fase.tab1tip=dc.fase "
        + "                         join w_docdig o on o.idprg='PG' and o.digent='G1DOCSTIPULA' and o.digkey1=CAST(dc.id AS VARCHAR(10)) "
        + "                         inner join W_LOGEVENTI w on c.codstipula = w.oggevento "
        + "                      where w.codevento like 'GA_STIPULA_DOC_%' )"
        + " order by c.codstipula ";
  }
  
  
  
  private static void addFormField(PrintWriter writer, StringBuilder requestContent, String boundary, String name, String value) {
    if(value != null) {
      writer.append("--" + boundary).append("\r\n");
      writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append("\r\n");
      writer.append("Content-Type: text/plain; charset=UTF-8").append("\r\n");
      writer.append("\r\n").append(value).append("\r\n");
      writer.flush();
      
      requestContent.append("--" + boundary).append("\r\n");
      requestContent.append("Content-Disposition: form-data; name=\"" + name + "\"").append("\r\n");
      requestContent.append("Content-Type: text/plain; charset=UTF-8").append("\r\n");
      requestContent.append("\r\n").append(value).append("\r\n");
    }
  }

  private static void addFilePart(PrintWriter writer, OutputStream outputStream, StringBuilder requestContent, String boundary, String fieldName, byte[] uploadFile, String fileName) throws Exception {
    writer.append("--" + boundary).append("\r\n");
    writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append("\r\n");
    writer.append("Content-Type: application/octet-stream").append("\r\n");
    writer.append("\r\n");
    writer.flush();

    // Scrivi i byte del file
    if (uploadFile != null) {
      outputStream.write(uploadFile);
      outputStream.flush();      
      writer.append("\r\n");
      writer.flush();
    }

    
    requestContent.append("--" + boundary).append("\r\n");
    requestContent.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append("\r\n");
    requestContent.append("Content-Type: application/octet-stream").append("\r\n");
    requestContent.append("\r\n");
  }
  
  
  private void eliminaAllegato(String cig, String idDocumento) throws Exception {

    try {
      // Endpoint per l'importazione dei contratti PCP
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);
      
      url = url + "/api/ms/eproc/"+cig+"/"+idDocumento;
      String token = login();
      int livEvento = 1;
      String errMsgEvento = null;
      Integer responseCode = null;
      HttpURLConnection conn = null;
      try { //GESTIONE TRACCIATURA

        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(true);


      }catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {

        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(cig);
          logEvento.setCodEvento("GA_NADC_REQ_MSEPROCDEL");
          logEvento.setDescr("Invio richiesta a NEW ADC:  /api/ms/eproc/"+cig+"/"+idDocumento);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }

      }

      try {
        responseCode = conn.getResponseCode();
        InputStream inputStream = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
          inputStream = conn.getInputStream();
        } else {
          inputStream = conn.getErrorStream();
        }
        String responseBody = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));
        errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;
      }catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        if(conn != null) {
          conn.disconnect();
        }
        if(token != null) {
          logout(token);
        }

        try {
          LogEvento logEvento1 = new LogEvento();
          logEvento1.setCodApplicazione("PG");
          logEvento1.setLivEvento(livEvento);
          logEvento1.setOggEvento(cig);
          logEvento1.setCodEvento("GA_NADC_RES_MSEPROCDEL");
          logEvento1.setDescr("Invio richiesta a NEW ADC:  /api/ms/eproc/"+cig+"/"+idDocumento);
          logEvento1.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento1);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }

    } catch (Exception e) {
      throw new Exception("Errore durante l'invio del documento: "+e.getMessage(), e);
    }

  }


  private void eliminaDocAssociati() throws Exception {

    //java.sql.Date ieri = getDataMenoGiorni(1);

    String query = "SELECT DISTINCT w.descr "
        + "FROM W_LOGEVENTI w "
        + "WHERE w.oggevento IS NOT NULL "
        + "AND w.dataora >= ? "
        + "AND w.codevento = 'C0OGGASS_DELETE' ";

    List<?> listaDescrizione = this.sqlManager.getListVector(query, new Object[] {this.previewDateTime});


    if (listaDescrizione != null && !listaDescrizione.isEmpty()) {
      for (int i = 0; i < listaDescrizione.size(); i++) {
        String descrEvento = (String) SqlManager.getValueFromVectorParam(listaDescrizione.get(i), 0).getValue();

        // ESEMPIO: Cancellazione documento associato - GARE/G00237/PG/668
        // ESEMPIO: Cancellazione documento associato - G1STIPULA/28/PG/682
        if(descrEvento.contains(" - ")) {
          
          String entita = descrEvento.split(" - ")[1].split("/")[0];
          String codice = descrEvento.split(" - ")[1].split("/")[1];
          //String idprg = oggevento.split(" - ")[1].split("/")[2];
          String iddocdig = descrEvento.split(" - ")[1].split("/")[3];
          
          List<String> listaNgara = new ArrayList<>();
          
          String ngara = codice;
          if("G1STIPULA".equals(entita)) {
            String getNgaraByIdStipula = "select g.ngara "
                + "from gare g "
                + "join garecont c1 on (g.ngara=c1.ngara and c1.ncont=1) or (c1.ngara=g.codgar1 and (c1.ngaral is null or c1.ngaral=g.ngara)) "
                + "join g1stipula c on c.ngara=c1.ngara and c.ncont=c1.ncont "
                + "where g.ngara != g.codgar1 "
                + "and c.id = ? ";
            List<?> listaObjNgara = this.sqlManager.getListVector(getNgaraByIdStipula, new Object[] {codice});
            if(listaObjNgara != null && !listaObjNgara.isEmpty()) {
              for (int y = 0; y < listaObjNgara.size(); y++) {
                listaNgara.add((String) SqlManager.getValueFromVectorParam(listaObjNgara.get(y), 0).getValue());
              }
            }
          } else {
            listaNgara.add(ngara);
          }
  
          for (int y = 0; y < listaNgara.size(); y++) {
            if("TORN".equals(entita)) {
              query = "select codcig from gare where codgar1 = ? and dacqcig is not null and ngara != codgar1 ";
              List<?> listaCodCig = this.sqlManager.getListVector(query, new Object[] {listaNgara.get(y)});
              if(listaCodCig != null && !listaCodCig.isEmpty()) {
                for (int x = 0; x < listaCodCig.size(); x++) {
                  String codcig = (String) SqlManager.getValueFromVectorParam(listaCodCig.get(x), 0).getValue();
                  if(codcig != null)
                    eliminaAllegato(codcig,iddocdig);
                }
              }
            } else {
              query = "select codcig from gare where ngara = ? and dacqcig is not null and ngara != codgar1 "; 
              String codcig = (String) this.sqlManager.getObject(query, new Object[] {listaNgara.get(y)});
              if(codcig != null)
                eliminaAllegato(codcig,iddocdig);
            }

          }
        }
      }
    }
  }

  private java.sql.Date getDataMenoGiorni(int giorni) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new java.sql.Date(System.currentTimeMillis())); //OGGI
    cal.add(Calendar.DAY_OF_MONTH, giorni*(-1)); // Sottrai un giorno
    return new java.sql.Date(cal.getTimeInMillis());
  }



  private String login() throws Exception {
    String token = null;

    try {
      String username = null;
      String passwordDecoded = null;
      
      List<?> datiWSLogin = sqlManager.getVector(
          "select username, password from wslogin where syscon = ? and servizio = ?", new Object[] { new Long(-1), "NEWADC" });

      if (datiWSLogin != null && !datiWSLogin.isEmpty()) {
        username = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 0).getValue();
        String password = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 1).getValue();

        if (password != null && password.trim().length() > 0) {
          ICriptazioneByte passwordICriptazioneByte = null;
          passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
              ICriptazioneByte.FORMATO_DATO_CIFRATO);
          passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
        }
      }
      
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);

      url = url + "/auth/login";
      HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);

      SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", Locale.ITALIAN);
      String dateStringNow = sdf.format(new Date());

      JSONObject jsonInput = new JSONObject();
      jsonInput.put("credential", Base64.getEncoder().encodeToString((username+":" + dateStringNow + ":"+passwordDecoded).getBytes()));

      try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
        wr.writeBytes(jsonInput.toString());
        wr.flush();
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder res = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        res.append(line);
      }
      reader.close();
      conn.disconnect();

      JSONObject jsonObject = JSONObject.fromObject(res.toString());

      token = jsonObject.getString("token");

    } catch (Exception e) {
      throw new Exception("Errore durante l'authentication", e);
    }

    return token;
  }

  private boolean logout(String token) throws Exception {
    boolean isLogoutSuccessful = false;

    try {
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);

      url = url + "/auth/logout";
      HttpURLConnection connessione = (HttpURLConnection) new URL(url).openConnection();
      connessione.setRequestMethod("POST");
      connessione.setRequestProperty("Content-Type", "application/json");
      connessione.setRequestProperty("X-Auth-Token", token);
      connessione.setDoOutput(true);

      // Controlla il codice di risposta
      int responseCode = connessione.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connessione.getInputStream()));
        StringBuilder res = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          res.append(line);
        }
        reader.close();
        connessione.disconnect();

        // Converte la risposta in JSON
        JSONObject jsonResponse = JSONObject.fromObject(res.toString());
        String logoutStatus = jsonResponse.getString("logout");

        // Verifica se il logout è andato a buon fine
        if ("ok".equalsIgnoreCase(logoutStatus)) {
          isLogoutSuccessful = true;
        }

      } else {
        throw new Exception("Logout fallito con il codice risposta: " + responseCode);
      }

    } catch (Exception e) {
      throw new Exception("Errore durante il logout", e);
    }

    return isLogoutSuccessful;
  }
  
  private String getAuthorizationWs() throws Exception {
    HashMap<String,String> data = new HashMap<String,String>();
    
    long expiration = System.currentTimeMillis() + 5 * 60000l;
    String chiaveSegretaJwtChiaro = JwtTokenUtilities.getChiaveSegretaJwtChiaroEsec();
    String authorization = JwtTokenUtilities.generateToken("Appalti", expiration, chiaveSegretaJwtChiaro, data);
    
    logger.debug("Token generato: "+authorization);
    
    return authorization;
  }

  
  private List<Map<String, String>> getDettaglioCigWs(String token, String cig, Boolean withFile) throws Exception {

    List<Map<String, String>> schedeEsecuzione = new ArrayList<>();
    try {
      String url = ConfigManager.getValore(NADC_MAGGIOLI_WS_BASEURL);
      if (url.endsWith("/")) {
        url = url.substring(0, url.length() - 1);
      }

      url = url + "/gestioneContratti/getJsonListaSchede?cig=" + cig;
      int livEvento = 1;
      String errMsgEvento = null;
      Integer responseCode = null;
      HttpURLConnection conn = null;

      try {
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(true);

      } catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(cig);
          logEvento.setCodEvento("GA_NADC_REQ_ESESCHCIG");
          logEvento.setDescr("Invio richiesta a MAGGIOLI per NEW ADC:  /gestioneContratti/getJsonListaSchede?cig=" + cig);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }

      try {
        responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
          InputStream inputStream = conn.getInputStream();
          String responseBody = new BufferedReader(new InputStreamReader(inputStream))
              .lines().collect(Collectors.joining("\n"));

          JSONObject jsonResponse = JSONObject.fromObject(responseBody);

          boolean esito = jsonResponse.getBoolean("esito");
          if (esito) {
            String cfRup = jsonResponse.getString("cfRup");
            //Recupera il contenuto del tag "data"
            JSONArray dataArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < dataArray.size(); i++) {
              JSONObject dataObject = dataArray.getJSONObject(i);
              schedeEsecuzione.addAll(getTypeAndAnacForm(dataObject, withFile, cfRup));
            }
            errMsgEvento = "Codice risposta: " + responseCode + " - Esito: true - Dati recuperati correttamente.";
          } else {
            errMsgEvento = "Codice risposta: " + responseCode + " - Esito: false.";
          }

        } else {
          errMsgEvento = "Codice risposta: " + responseCode;
        }
      } catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        if (conn != null) {
          conn.disconnect();
        }

        try {
          LogEvento logEvento1 = new LogEvento();
          logEvento1.setCodApplicazione("PG");
          logEvento1.setLivEvento(livEvento);
          logEvento1.setOggEvento(cig);
          logEvento1.setCodEvento("GA_NADC_RES_ESESCHCIG");
          logEvento1.setDescr("Invio richiesta a MAGGIOLI per NEW ADC:  /gestioneContratti/getJsonListaSchede?cig=" + cig);
          logEvento1.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento1);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
      return schedeEsecuzione;
    } catch (Exception e) {
      throw new Exception("Errore durante l'invio del documento: " + e.getMessage(), e);
    }
  }


  private List<Map<String, String>> getTypeAndAnacForm(JSONObject dataObject, Boolean withFile, String cfRup) {
    List<Map<String, String>> resultList = new ArrayList<>();

    // Lista di possibili chiavi (sc1, i1, etc.)
    String[] keys = {"m","sc1", "i1", "sa1", "co1", "co2", "cl1", "ac1", "so1", "sq1", "ri1", "rsu1", "es1", "cs1", "ir1"};

    for (String key : keys) {
      if (dataObject.containsKey(key)) {
        Object obj = dataObject.get(key);

        // Verifica se l'oggetto associato e' un JSONObject
        if (obj != null) {
          if (obj instanceof JSONObject) {
            JSONObject jsonNestedObject = (JSONObject) obj;

            if (!jsonNestedObject.isNullObject()) {
              // Recupera il campo "type"
              if (jsonNestedObject.containsKey("type")) {
                resultList.add(getDatiDaJSON(jsonNestedObject, withFile, cfRup));
              }
            }
          } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;

            if (!jsonArray.isEmpty()) {
              // Se è una lista piena, itera su ogni oggetto della lista
              for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject arrayItem = jsonArray.getJSONObject(i);

                if (arrayItem.containsKey("type")) {
                  resultList.add(getDatiDaJSON(arrayItem, withFile, cfRup));
                }
              }
            }
          }
        }
      }
    }

    return resultList;
  }


  

  private Map<String, String> getDatiDaJSON(JSONObject jsonNestedObject, Boolean withFile, String cfRup) {

    String type = jsonNestedObject.getString("type");

    // Recupera il campo "anacForm"
    JSONObject anacFormObject = jsonNestedObject.optJSONObject("anacForm");
    String anacForm = anacFormObject != null ? anacFormObject.toString() : "anacForm non presente";

    String espd = null;
    if (jsonNestedObject.containsKey("espd")) {
      if(Boolean.TRUE.equals(withFile)) {
        espd = jsonNestedObject.getString("espd");
      } else {
        espd = "espd";
      }
    }
    
    String eform = null;
    if (jsonNestedObject.containsKey("eform")) {
      if(Boolean.TRUE.equals(withFile)) {
        eform = jsonNestedObject.getString("eform");
      } else {
        eform = "eform";
      }
    }
    
    String idDocumento = null;
    if (jsonNestedObject.containsKey("idDocumento")) {
      idDocumento = jsonNestedObject.getString("idDocumento");
    }
    
    String idScheda = null;
    if (jsonNestedObject.containsKey("idScheda")) {
      idScheda = jsonNestedObject.getString("idScheda");
    }
    
    String dataInserimento = null;
    if (jsonNestedObject.containsKey("dataInserimento")) {
      dataInserimento = jsonNestedObject.getString("dataInserimento");
    } else {
      dataInserimento = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Aggiungi il risultato alla mappa
    Map<String, String> resultMap = new HashMap<>();
    resultMap.put("type", type);
    resultMap.put("anacForm", anacForm);
    resultMap.put("espd", espd);
    resultMap.put("eform", eform);
    resultMap.put("idDocumento", idDocumento);
    resultMap.put("idScheda", idScheda);
    resultMap.put("dataInserimento", dataInserimento);
    resultMap.put("cfRup", cfRup);

    return resultMap;
  
  }

  private List<String> getListaCigWs(String token) throws Exception {

    List<String> listaCig = new ArrayList<>();
    try {
      String url = ConfigManager.getValore(NADC_MAGGIOLI_WS_BASEURL);
      if (url.endsWith("/")) {
        url = url.substring(0, url.length() - 1);
      }
      
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
      
      String prec = this.previewDateTime.format(formatter);
      String now = this.currentDateTime.format(formatter);
      
      url = url + "/gestioneContratti/getListaCig?dataDa=" + prec +"&dataA="+ now;
      
      int livEvento = 1;
      String errMsgEvento = null;
      Integer responseCode = null;
      HttpURLConnection conn = null;

      try {
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(true);

      } catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(prec+"/"+now);
          logEvento.setCodEvento("GA_NADC_REQ_ESELISTACIG");
          logEvento.setDescr("Invio richiesta a MAGGIOLI per NEW ADC:  /gestioneContratti/getListaCig?dataDa=" + prec +"&dataA="+ now);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }

      try {
        responseCode = conn.getResponseCode();
        if (responseCode == 200) {
          InputStream inputStream = conn.getInputStream();
          String responseBody = new BufferedReader(new InputStreamReader(inputStream))
              .lines().collect(Collectors.joining("\n"));

          JSONObject jsonResponse = JSONObject.fromObject(responseBody);

          boolean esito = jsonResponse.getBoolean("esito");
          if (esito) {
            //Recupera il contenuto del tag "data"
            JSONArray dataArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < dataArray.size(); i++) {
              listaCig.add(dataArray.getString(i));
            }
            errMsgEvento = "Codice risposta: " + responseCode + " - Esito: true - Lista cig recuperata correttamente.";
          } else {
            errMsgEvento = "Codice risposta: " + responseCode + " - Esito: false.";
          }

        } else {
          errMsgEvento = "Codice risposta: " + responseCode;
        }
      } catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
      } finally {
        if (conn != null) {
          conn.disconnect();
        }

        try {
          LogEvento logEvento1 = new LogEvento();
          logEvento1.setCodApplicazione("PG");
          logEvento1.setLivEvento(livEvento);
          logEvento1.setOggEvento(prec+"/"+now);
          logEvento1.setCodEvento("GA_NADC_RES_ESELISTACIG");
          logEvento1.setDescr("Invio richiesta a MAGGIOLI per NEW ADC:  /gestioneContratti/getListaCig?dataDa=" + prec +"&dataA="+ now);
          logEvento1.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento1);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
      return listaCig;
    } catch (Exception e) {
      throw new Exception("Errore durante l'invio del documento: " + e.getMessage(), e);
    }
  }

  private static List<String> mergeListsWithoutDuplicates(List<String> list1, List<String> list2) {
    //Sostituisci liste null con liste vuote
    list1 = (list1 == null) ? new ArrayList<>() : list1;
    list2 = (list2 == null) ? new ArrayList<>() : list2;

    //Aggiunge tutti gli elementi della prima lista
    Set<String> set = new HashSet<>(list1);
    //Aggiunge gli elementi della seconda lista, ignorando duplicati
    set.addAll(list2);

    //Converte il Set di nuovo in una List
    return new ArrayList<>(set);
  }
  
  
  
  
  private static LocalDateTime calculatePreviousDate(LocalDateTime currentDateTime, String cronExpression) {

    // Dividi la cron expression e prendi solo i valori dei minuti e delle ore
    String[] cronParts = cronExpression.split(" ");

    // Controlla se ci sono almeno due campi (minuti e ore)
    if (cronParts.length < 2) {
      throw new IllegalArgumentException("Formato cron non valido. Devono esserci almeno minuti e ore.");
    }

    Boolean dataCalcolata = Boolean.FALSE;
    // Estrai i valori dei minuti e delle ore
    String minutePart = cronParts[1];
    String hourPart = cronParts[2];

    if (minutePart.startsWith("0/")) {
      int minuteInterval = Integer.parseInt(minutePart.substring(2));
      currentDateTime = currentDateTime.minusMinutes(minuteInterval);
      dataCalcolata = Boolean.TRUE;
    }

    if (hourPart.startsWith("0/")) {
      int hourInterval = Integer.parseInt(hourPart.substring(2));
      currentDateTime = currentDateTime.minusHours(hourInterval);
      dataCalcolata = Boolean.TRUE;
    }
    
    if(dataCalcolata.equals(Boolean.FALSE)) {
      currentDateTime = currentDateTime.minusDays(1);
    }

    return currentDateTime;
  }

  private String getCronExpression() throws SQLException {
    return (String) this.sqlManager.getObject("select cron_expression from w_quartz where bean_id = 'inviaCigNewADCTrigger'", new Object[]{ });
  }
  
  private static ByteArrayOutputStream createZip(List<DocumentoDaInviare> documents) throws Exception {
    // OutputStream che conterra' lo ZIP
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try (ZipOutputStream zipOut = new ZipOutputStream(baos)) {
      for (DocumentoDaInviare doc : documents) {
        try {
          // Crea una nuova entrata ZIP
          ZipEntry entry = new ZipEntry(doc.getIdFile()+"_"+doc.getNomeFile());
          zipOut.putNextEntry(entry);

          // Scrive il contenuto del file nella ZIP
          zipOut.write(doc.getFile());

          // Chiude l'entrata corrente
          zipOut.closeEntry();
        } catch (ZipException e) {
          // il file è già presente nello zip .., lo ignoro
        }
      }
    } catch (Exception e) {
      logger.error("Errore inaspettato nell'inserimento del documento nel file zip", e);
    }

    return baos; // Ritorna lo ZIP come array di byte
  }
  
  
  public void invioRupNewAdc() throws Exception {
    //java.sql.Date ieri = getDataMenoGiorni(1);
    List<Rup> listaRup = new ArrayList<>();
    
    String queryRup = "select y.sysnom, rup.nometei, rup.cogtei, rup.cftec, rup.ematec, y.syscon "
        +" from tecni rup "
        +" inner join usrsys y on y.syscf = rup.cftec " 
        +" inner join W_LOGEVENTI w on rup.codtec = w.oggevento "
        +" where w.codevento in ('TECNI_INSERT','TECNI_EDIT') "
        +" and w.dataora >= ? ";
    
    List<?> listaDatiRup = this.sqlManager.getListVector(queryRup, new Object[] {this.previewDateTime});
    
    if (listaDatiRup != null && !listaDatiRup.isEmpty()) {
      for (int i = 0; i < listaDatiRup.size(); i++) {
        String sysnom = (String) SqlManager.getValueFromVectorParam(listaDatiRup.get(i), 0).getValue();
        String nometei = (String) SqlManager.getValueFromVectorParam(listaDatiRup.get(i), 1).getValue();
        String cogtei = (String) SqlManager.getValueFromVectorParam(listaDatiRup.get(i), 2).getValue();
        String cftec = (String) SqlManager.getValueFromVectorParam(listaDatiRup.get(i), 3).getValue();
        String ematec = (String) SqlManager.getValueFromVectorParam(listaDatiRup.get(i), 4).getValue();
        Long syscon = (Long) SqlManager.getValueFromVectorParam(listaDatiRup.get(i), 5).getValue();
        
        String query = "select y.sysuffapp, rup.doftec "
            + " from tecni rup "
            + " inner join usrsys y on y.syscf = rup.cftec "
            + " where y.syscon = ? ";
        
        Vector<?> ufficio = this.sqlManager.getVector(query, new Object[] {syscon});
        
        if (ufficio != null && !ufficio.isEmpty()) {
          
          String sottostruttura = (String) SqlManager.getValueFromVectorParam(ufficio, 0).getValue();
          String direzione = (String) SqlManager.getValueFromVectorParam(ufficio, 1).getValue();

          String sysnomDecoded = null;
          if (sysnom != null && sysnom.trim().length() > 0) {
            ICriptazioneByte passwordICriptazioneByte = null;
            passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
                ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), sysnom.getBytes(),
                ICriptazioneByte.FORMATO_DATO_CIFRATO);
            sysnomDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
          }
          listaRup.add(new Rup(cftec,nometei,cogtei,ematec,sysnomDecoded,direzione,sottostruttura,null));
        }
      }
    }
    
    if(!listaRup.isEmpty()) {
      for (int i = 0; i < listaRup.size(); i++) {
        wsInvioRupNewAdc(listaRup.get(i));
      }
    }
  }
  
  
  private void wsInvioRupNewAdc(Rup rup) throws Exception {
    try {
      String url = ConfigManager.getValore(NADC_RAI_WS_BASEURL);
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);

      url = url + "/api/ms/rup";

      String token = login();
      int livEvento = 1;
      String errMsgEvento = null;
      String jsonInputString = "";
      Integer responseCode = null;
      HttpURLConnection conn = null;

      try {
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(true);
        
        ObjectMapper objectMapper = new ObjectMapper();
        jsonInputString = objectMapper.writeValueAsString(rup);

        // Scrivo il JSON nel body della richiesta
        OutputStream os = conn.getOutputStream();
        os.write(jsonInputString.getBytes());
        os.flush();
        errMsgEvento = jsonInputString;

        try {
          LogEvento logEventoRequest = new LogEvento();
          logEventoRequest.setCodApplicazione("PG");
          logEventoRequest.setLivEvento(livEvento);
          logEventoRequest.setOggEvento(rup.getCodiceFiscale());
          logEventoRequest.setCodEvento("GA_NADC_REQ_MSRUP");
          String descrizioneRequest = "Invio richiesta a NEW ADC: /api/ms/rup (" + rup.getCodiceFiscale() + ")";
          logEventoRequest.setDescr(descrizioneRequest);
          logEventoRequest.setErrmsg(jsonInputString);
          LogEventiUtils.insertLogEventi(logEventoRequest);
        }catch (Exception e) {
          livEvento = 3;
          errMsgEvento = e.getMessage();
          logger.error("Errore inaspettato request", e);
        }

        // Ricezione della risposta
        responseCode = conn.getResponseCode();
        InputStream inputStream = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
          inputStream = conn.getInputStream();
        } else {
          inputStream = conn.getErrorStream();
        }
        String responseBody = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));
        errMsgEvento = "Codice risposta: " + responseCode + " - Risposta: " + responseBody;
        
        try {
          LogEvento logEventoResponse = new LogEvento();
          logEventoResponse.setCodApplicazione("PG");
          logEventoResponse.setLivEvento(livEvento);
          logEventoResponse.setOggEvento(rup.getCodiceFiscale());
          logEventoResponse.setCodEvento("GA_NADC_RES_MSRUP");
          String descrizioneResponse = "Risposta da NEW ADC: /api/ms/rup (" + rup.getCodiceFiscale() + ")";
          logEventoResponse.setDescr(descrizioneResponse);
          logEventoResponse.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEventoResponse);
        } catch (Exception le) {
          logger.error("Errore durante la tracciatura dell'evento response", le);
        }
      } catch (Exception e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
        logger.error("Errore inaspettato response", e);
      } finally {
        if (conn != null) {
          conn.disconnect();
        }
        logout(token);
      }
    } catch (Exception e) {
      throw new Exception("Errore durante l'importazione del RUP: " + e.getMessage(), e);
    }
  }
 
  
  public static Long generateLongWithPrefix(String codStipula) {
    
    // Estrai la parte numerica e rimuovi gli zeri iniziali
    String numericPart = codStipula.substring(3).replaceFirst("^0+", "");

    // Grandezza massima 15 caratteri definito da rai
    int prefixLength = 15 - numericPart.length();
    
    // Genera il prefisso con il numero corretto di 9
    String prefix = repeatString("9",prefixLength);

    // Combina il prefisso con la parte numerica
    String resultString = prefix + numericPart;

    // Converte il risultato in Long
    return Long.parseLong(resultString);
  }
  
  public static String repeatString(String str, int volte) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < volte; i++) {
      sb.append(str);
    }
    return sb.toString();
  }
 
  /**
   *  | Valore Config | Significato       |
   *  | ------------- | ----------------- |
   *  | `1.0`         | 1 ora             |
   *  | `0.5`         | 30 minuti         |
   *  | `0.1`         | 6 minuti          |
   *  | `2.25`        | 2 ore e 15 minuti |
   *
   * @throws Exception
   */
  public void allineaCig() throws Exception {
    try {
      String query = "select codcig from NADC_ALLINEACIG where elab is null ";
      String oreDurataStr = ConfigManager.getValore(NADC_RAI_WS_HOURS);

      Double oreDurata = Double.valueOf(1.0); // default

      try {
        oreDurata = Double.valueOf(oreDurataStr);
      } catch (NumberFormatException e) {
        logger.warn("Valore oreDurata non valido: '"+oreDurataStr+"'. Uso valore di default: "+oreDurata);
      }

      LocalDateTime oraInizio = LocalDateTime.now();
      Long durataMassimaMinuti = (long) (oreDurata * 60);

      List<?> dati = this.sqlManager.getListVector(query, new Object[] {});
      if (dati != null && !dati.isEmpty()) {
        for (int i = 0; i < dati.size(); i++) {
          Long minutiTrascorsi = Long.valueOf(Duration.between(oraInizio, LocalDateTime.now()).toMinutes());

          if (minutiTrascorsi.compareTo(durataMassimaMinuti) >= 0) {
            logger.info("Durata massima di "+oreDurata+" ore ("+durataMassimaMinuti+" minuti) raggiunta. Uscita anticipata dal ciclo.");
            break;
          }
          Long statoElaborazione = Long.valueOf(1);
          String codcig = null;
          try {
            codcig = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();

            importPCPContractsPuntuale(codcig);
            invioAssociazionePuntuale(codcig);
            importDocumentsPuntuale(codcig);
          } catch (Exception e) {
            statoElaborazione = Long.valueOf(2);
          } finally {
            TransactionStatus status = null;
            boolean doCommit = false;
            try {
              status = this.sqlManager.startTransaction();
              String updateAllinea = "UPDATE NADC_ALLINEACIG SET elab = ?, data_elab = ? WHERE codcig = ? ";
              this.sqlManager.update(updateAllinea, new Object[] { statoElaborazione, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), codcig });
              doCommit = true;
            } catch (Exception e) {
              logger.error("Errore durante l'aggiornamento dell'elaborazione. Codice Cig: "+codcig+", messaggio di errore: " + e.getMessage(), e );
            } finally {
              if (status != null) {
                try {
                  if (doCommit) {
                    this.sqlManager.commitTransaction(status);
                  } else {
                    this.sqlManager.rollbackTransaction(status);
                  }
                } catch (SQLException ex) {
                  throw new GestoreException("", "", ex);
                }
              }
            }
          }
        }
      }

    } catch (Exception e) {
      throw new Exception("Errore durante l'associazione dei contratti: " + e.getMessage(), e);
    }
  }

  
}
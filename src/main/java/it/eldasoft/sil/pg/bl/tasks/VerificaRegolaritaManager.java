/*
 * Created on 13/06/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class VerificaRegolaritaManager {

  static Logger           logger = Logger.getLogger(VerificaRegolaritaManager.class);

  private SqlManager      sqlManager;
  private GenChiaviManager    genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public SqlManager getSqlManager() {
    return this.sqlManager;
  }
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }


  public void verificaRegolarita() throws GestoreException {

    String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);

    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();

    if (logger.isDebugEnabled()){
      logger.debug("verificaRegolarita: inizio metodo");
    }
    TransactionStatus status = null;
    boolean doCommit = true;
    int livEvento = 1;
    String errMsgEvento = "";
    String idRichiesta = "";
    try {
      String urlS = ConfigManager.getValore("verifica.regolarita.ws.client.url");
      if(urlS != null && !"".equals(urlS)){

        HttpURLConnection connection = null;
        URL url = new URL(urlS);

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
        connection.setRequestProperty("Host", url.getHost());

        String queryImpreseNoIMPDURC = "";
        String queryImpreseIMPDurcScadute = "";
        String queryImpreseIMPDurcNonScadute = "";
        if("ORA".equals(db)){
          queryImpreseNoIMPDURC = "select codimp, cfimp "
              + "from impr "
              + "where codimp in ( "
              + "    select distinct(i.codimp) "
              + "    from impr i "
              + "        join ditg d on d.dittao=i.codimp "
              + "        join garealbo e on e.codgar=d.codgar5 and e.ngara=d.ngara5 "
              + "    where codimp not in (select codimp from impdurc) "
              + ")";
          queryImpreseIMPDurcScadute = "select codimp, cfimp, datsca "
              + "from ( "
              + "select distinct i1.codimp, i1.cfimp,datric,datsca,esito_verifica_regolarita, esito_richiesta_durc, tipo_richiesta "
              + "from impr i1 "
              + "   join impdurc imd1 on imd1.codimp = i1.codimp "
              + "   join ditg d on d.dittao=i1.codimp "
              + "   join garealbo e on e.codgar=d.codgar5 and e.ngara=d.ngara5 "
              + "   join( "
              + "       select MAX(datric) AS datric2, codimp AS codimp2 "
              + "       from impdurc "
              + "       GROUP BY codimp "
              + "   ) imd2 on imd1.datric = imd2.datric2 and imd1.codimp = imd2.codimp2 "
              + "where (imd1.tipo_richiesta = 1 "
              + "       and imd1.esito_verifica_regolarita is not null) "
              + "       or (imd1.tipo_richiesta = 2 "
              + "       and imd1.esito_richiesta_durc is not null) "
              + "       ) "
              + "       where datsca is not null "
              + "       and datsca < current_date "
              + "       order by datsca asc";
          queryImpreseIMPDurcNonScadute = "select codimp, cfimp, datsca "
              + "from ( "
              + "select distinct i1.codimp, i1.cfimp,datric,datsca,esito_verifica_regolarita, esito_richiesta_durc, tipo_richiesta "
              + "from impr i1 "
              + "   join impdurc imd1 on imd1.codimp = i1.codimp "
              + "   join ditg d on d.dittao=i1.codimp "
              + "   join garealbo e on e.codgar=d.codgar5 and e.ngara=d.ngara5 "
              + "   join( "
              + "       select MAX(datric) AS datric2, codimp AS codimp2 "
              + "       from impdurc "
              + "       GROUP BY codimp "
              + "   ) imd2 on imd1.datric = imd2.datric2 and imd1.codimp = imd2.codimp2 "
              + "where (imd1.tipo_richiesta = 1 "
              + "       and imd1.esito_verifica_regolarita is not null) "
              + "       or (imd1.tipo_richiesta = 2 "
              + "       and imd1.esito_richiesta_durc is not null) "
              + "       ) "
              + "       where datsca is not null "
              + "       and datsca > current_date "
              + "       order by datsca asc";

        } else if("POS".equals(db)){

          queryImpreseNoIMPDURC = "select codimp, cfimp "
              + "from impr "
              + "where codimp in ( "
              + "    select distinct(i.codimp) "
              + "    from impr i "
              + "        join ditg d on d.dittao=i.codimp "
              + "        join garealbo e on e.codgar=d.codgar5 and e.ngara=d.ngara5 "
              + "    where codimp not in (select codimp from impdurc) "
              + ")";
          queryImpreseIMPDurcScadute = "select t1.codimp, t1.cfimp,t1.datsca "
              + "from ( "
              + "select distinct i1.codimp, i1.cfimp,datric,datsca,esito_verifica_regolarita, esito_richiesta_durc, tipo_richiesta "
              + "from impr i1 "
              + "    join impdurc imd1 on imd1.codimp = i1.codimp "
              + "    join ditg d on d.dittao=i1.codimp "
              + "    join garealbo e on e.codgar=d.codgar5 and e.ngara=d.ngara5 "
              + "    join( "
              + "        select MAX(datric) AS datric2, codimp AS codimp2 "
              + "        from impdurc "
              + "        GROUP BY codimp "
              + "    ) imd2 on imd1.datric = imd2.datric2 and imd1.codimp = imd2.codimp2 "
              + "where (imd1.tipo_richiesta = 1 "
              + "        and imd1.esito_verifica_regolarita is not null) "
              + "        or (imd1.tipo_richiesta = 2 "
              + "        and imd1.esito_richiesta_durc is not null) "
              + "        ) as t1 "
              + "        where datsca is not null "
              + "        and datsca < current_date "
              + "        order by datsca asc";
          queryImpreseIMPDurcNonScadute = "select t1.codimp, t1.cfimp,t1.datsca "
              + "from ( "
              + "select distinct i1.codimp, i1.cfimp,datric,datsca,esito_verifica_regolarita, esito_richiesta_durc, tipo_richiesta "
              + "from impr i1 "
              + "    join impdurc imd1 on imd1.codimp = i1.codimp "
              + "    join ditg d on d.dittao=i1.codimp "
              + "    join garealbo e on e.codgar=d.codgar5 and e.ngara=d.ngara5 "
              + "    join( "
              + "        select MAX(datric) AS datric2, codimp AS codimp2 "
              + "        from impdurc "
              + "        GROUP BY codimp "
              + "    ) imd2 on imd1.datric = imd2.datric2 and imd1.codimp = imd2.codimp2 "
              + "where (imd1.tipo_richiesta = 1 "
              + "        and imd1.esito_verifica_regolarita is not null) "
              + "        or (imd1.tipo_richiesta = 2 "
              + "        and imd1.esito_richiesta_durc is not null) "
              + "        ) as t1 "
              + "        where datsca is not null "
              + "        and datsca > current_date "
              + "        order by datsca asc";
        }

        int maxVRRecords = 2000;

        List<List<String>> matrice = new ArrayList<>();
        JSONArray codiciFiscaliArray = new JSONArray();
        List<String> datiImprese = this.sqlManager.getListVector(queryImpreseNoIMPDURC, new Object[]{ });
        if(datiImprese!=null && datiImprese.size()<maxVRRecords){
          List<String> altreImpreseScadute= this.sqlManager.getListVector(queryImpreseIMPDurcScadute, new Object[]{ });
          if(altreImpreseScadute!=null && !altreImpreseScadute.isEmpty()){
            for (int i = 0; i < altreImpreseScadute.size() && datiImprese.size()<maxVRRecords; i++) {
              datiImprese.add(altreImpreseScadute.get(i));
            }
          }
          if(datiImprese.size()<maxVRRecords){
            List<String> altreImpreseNonScadute= this.sqlManager.getListVector(queryImpreseIMPDurcNonScadute, new Object[]{ });
            if(altreImpreseNonScadute!=null && !altreImpreseNonScadute.isEmpty()){
              for (int i = 0; i < altreImpreseNonScadute.size() && datiImprese.size()<maxVRRecords; i++) {
                datiImprese.add(altreImpreseNonScadute.get(i));
              }
            }
          }
        }
        if(datiImprese!=null && !datiImprese.isEmpty()){
          for (int i = 0; i < datiImprese.size() && i < maxVRRecords; i++) {
            String codiceImpresa = (String) SqlManager.getValueFromVectorParam(datiImprese.get(i), 0).getValue();
            String codiceFiscale = (String) SqlManager.getValueFromVectorParam(datiImprese.get(i), 1).getValue();
            matrice.add(new ArrayList<String>());
            matrice.get(i).add(codiceImpresa);
            matrice.get(i).add(codiceFiscale);

            codiciFiscaliArray.add(codiceFiscale);
          }


          List<?> datiWSLogin = sqlManager.getVector(
              "select username, ruolo from wslogin where syscon = ? and servizio = ?", new Object[] { Long.valueOf(-1), "WSDURC" });

          if (datiWSLogin != null && !datiWSLogin.isEmpty()) {
            String username = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 0).getValue();
            String codice = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 1).getValue();

            idRichiesta = "VR_"+UUID.randomUUID().toString();
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("codice", codice);
            jsonInput.put("idrichiesta", idRichiesta);
            jsonInput.put("username", username);
            jsonInput.put("codicifiscali", codiciFiscaliArray);

            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.write(jsonInput.toString().getBytes());
            output.flush();

            int httpStatus = connection.getResponseCode();

            JSONObject jsonOutput = new JSONObject();
            String messageErrorResponse = "";
            String res = "";
            if (httpStatus == HttpURLConnection.HTTP_OK) {
              status = this.sqlManager.startTransaction();
              res = getDatiDaResponse(connection.getInputStream());
              jsonOutput = JSONObject.fromObject(res);

              if (jsonOutput.has("Esito") && jsonOutput.getInt("Esito") == 1) {
                String query = "INSERT INTO IMPDURC (ID,CODIMP,DATRIC,ID_RICHIESTA,TIPO_RICHIESTA)"
                    + " VALUES (?,?,?,?,?)";
                for (int i = 0; i < matrice.size(); i++) {
                  int nextId = this.genChiaviManager.getNextId("IMPDURC");
                  this.sqlManager.update(query, new Object[] {nextId, matrice.get(i).get(0), new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idRichiesta, 1});
                }
              } else {
                if(jsonOutput.has("Messaggio"))
                  errMsgEvento = jsonOutput.getString("Messaggio");
              }
            } else {
              errMsgEvento = getDatiDaResponse(connection.getErrorStream());
            }

            System.out.println("Esito: "+ jsonOutput.getString("Esito"));
            System.out.println("Messaggio: "+ jsonOutput.getString("Messaggio"));
            System.out.println("messageErrorResponse: "+ messageErrorResponse);
            System.out.println("res: "+ res);
          } else {
            doCommit = false;
            livEvento = 3;
            errMsgEvento = "Le credenziali di accesso al servizio WSDURC non sono configurate";
          }
        }
      }
      if (logger.isDebugEnabled()){
        logger.debug("verificaRegolarita: fine metodo");
      }
    }catch (Exception e) {
      doCommit = false;
      livEvento = 3;
      errMsgEvento = "Errore in verifica regolarita: "+e.getMessage();
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
      try{
        LogEvento logEvento =  new LogEvento();
        logEvento.setCodApplicazione("PG");
        logEvento.setLivEvento(livEvento);
        String oggetto = "";
        logEvento.setOggEvento(oggetto);
        logEvento.setCodEvento("GA_WSDURC_REQ_VERIFICA_REGOLARITA");
        logEvento.setDescr("Request Verifica Regolarita idRichiesta: "+idRichiesta);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      }catch(Exception e){
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi");
      }
    }

  }

  private String getDatiDaResponse(InputStream input) throws IOException {
    StringBuilder sb = new StringBuilder();
    if(input != null) {
      BufferedReader br = new BufferedReader(new InputStreamReader(input));
      String strLine;
      while ((strLine = br.readLine()) != null) {
        sb.append(strLine);
      }
      br.close();
    }
    return sb.toString();
  }

  private String recuperoErroreDaResponse(JSONObject response) throws GestoreException {
    String errorMessage = "";
    if(response.has("Esito") && response.has("Messaggio")) {
      errorMessage = response.getString("Messaggio");
    }
    return errorMessage;
  }

}

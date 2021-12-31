/*
 * Created on 02/lug/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;


public class LeggiPubblicazioniManager {

  static Logger               logger                         = Logger.getLogger(LeggiPubblicazioniManager.class);

  private static final String PROP_BANDO_AVVISO_SIMAP_WS_URL = "it.eldasoft.bandoavvisosimap.ws.url";

  private SqlManager          sqlManager;
  private GenChiaviManager    genChiaviManager;
  private TabellatiManager    tabellatiManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public JSONArray leggiPubblicazioni(String codgar) throws GestoreException, IOException, SQLException, ParseException{


    String urlString = ConfigManager.getValore(PROP_BANDO_AVVISO_SIMAP_WS_URL);
    if (urlString == null || "".equals(urlString)) {
      throw new GestoreException(
          "L'indirizzo per la connessione al web service non e' definito",
          "inviabandoavvisosimap.ws.url");
    }

    urlString = urlString + "/leggipubblicazioni";

    JSONArray ja = new JSONArray();
    JSONArray jaInviati = new JSONArray();

    List codiciUUID = sqlManager.getListVector("select uuid from garuuid where codgar = ?",
        new Object[] { codgar });
    if (codiciUUID != null && codiciUUID.size() > 0) {
      for (int i = 0; i < codiciUUID.size(); i++) {
        String uuid = (String) SqlManager.getValueFromVectorParam(
            codiciUUID.get(i), 0).getValue();
        ja.add(uuid);
      }
    }

    URL url = new URL(urlString);
    HttpURLConnection conn = null;
    conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");

    OutputStream os = conn.getOutputStream();
    String jsonStrg = ja.toString();
    os.write(jsonStrg.getBytes("UTF-8"));
    os.flush();

    int risposta = conn.getResponseCode();
    System.out.println("response code: " + risposta);
    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
    String res = "";
    String output;
    System.out.println("Output from Server .... \n");
    while ((output = br.readLine()) != null) {
      res= res + output;
    }

    JSONObject jsonResult = JSONObject.fromObject(res);
    boolean esito = (Boolean) jsonResult.get("esito");
    if(!esito){return jaInviati;}
    JSONArray jsonPubb = (JSONArray) jsonResult.get("pubblicazioni");
    for (int i = 0; i<jsonPubb.size(); i++){
      JSONObject pubblicazione = jsonPubb.getJSONObject(i);
      String noticeNumber = (String) pubblicazione.get("notice_number_oj");
      if(noticeNumber != null){
       logger.debug(noticeNumber);
       String descr = inserisciPubb(pubblicazione,codgar);
       if(descr != null){
         pubblicazione.put("descrizione", descr);
         jaInviati.add(pubblicazione);
       }
      }
    }
    conn.disconnect();
    return jaInviati;
  }

  private String inserisciPubb(JSONObject pubb, String codgar) throws SQLException, ParseException, GestoreException{

    String formulario = (String) sqlManager.getObject(
        "select tipric from garuuid where uuid = ?",
        new Object[] { pubb.get("uuid") });

    String descrizione = null;
    String tespub = null;
    String noticeNumber = (String) pubb.get("notice_number_oj");
    String form = (String) pubb.get("form");
    if(form != null && form.equals("FS1")){
      tespub = noticeNumber + " - " + form + " - Avviso di preinformazione";
      descrizione = "Avviso di preinformazione";
    }
    if(form != null && form.equals("FS4")){
      tespub = noticeNumber + " - " + form + " - Avviso periodico indicativo";
      descrizione = "Avviso periodico indicativo";
    }
    if(form != null && form.equals("FS2")){
      tespub = noticeNumber + " - " + form + " - Bando di gara";
      descrizione = "Bando di gara";
    }
    if(form != null && form.equals("FS5")){
      tespub = noticeNumber + " - " + form + " - Bando di gara settori speciali";
      descrizione = "Bando di gara settori speciali";
    }
    if(form != null && form.equals("FS8")){
      tespub = noticeNumber + " - " + form + " - Avviso relativo al profilo di committente";
      descrizione = "Avviso relativo al profilo di committente";
    }
    if(form != null && form.equals("FS3")){
      tespub = noticeNumber + " - " + form + " - Avviso di aggiudicazione";
      descrizione = "Avviso di aggiudicazione";
    }
    if(form != null && form.equals("FS6")){
      tespub = noticeNumber + " - " + form + " - Avviso di aggiudicazione settori speciali";
      descrizione = "Avviso di aggiudicazione settori speciali";
    }
    if(form != null && form.equals("FS14")){
      tespub = noticeNumber + " - " + form + " - Rettifica ";
      descrizione = "Rettifica ";
    }

    if(form.equals("FS14")){

      String description = null;
      String selectGaruuid = "select tipric from garuuid where uuid = ?";
      String tipric = (String) this.sqlManager.getObject(selectGaruuid, new Object[] { pubb.get("uuid")});
      if(tipric != null && tipric.equals("SIMAP-FS1")){
        tespub = tespub + " avviso di preinformazione";
        formulario = "SIMAP-FS1";
        descrizione = descrizione + "avviso di preinformazione";
      }
      if(tipric != null && tipric.equals("SIMAP-FS4")){
        tespub = tespub + " avviso periodico indicativo";
        formulario = "SIMAP-FS4";
        descrizione = descrizione + "avviso periodico indicativo";
      }
      if(tipric != null && tipric.equals("SIMAP-FS2")){
        tespub = tespub + " bando di gara";
        formulario = "SIMAP-FS2";
        descrizione = descrizione + "bando di gara";
      }
      if(tipric != null && tipric.equals("SIMAP-FS5")){
        tespub = tespub + " bando di gara settori speciali";
        formulario = "SIMAP-FS5";
        descrizione = descrizione + "bando di gara settori speciali";
      }
      if(tipric != null && tipric.equals("SIMAP-FS8")){
        tespub = tespub + " avviso relativo al profilo di committente";
        formulario = "SIMAP-FS8";
        descrizione = descrizione + "avviso relativo al profilo di committente";
      }
      if(tipric != null && tipric.equals("SIMAP-FS3")){
        tespub = tespub + " avviso di aggiudicazione";
        formulario = "SIMAP-FS3";
        descrizione = descrizione + "avviso di aggiudicazione";
      }
      if(tipric != null && tipric.equals("SIMAP-FS6")){
        tespub = tespub + " avviso di aggiudicazione settori speciali";
        formulario = "SIMAP-FS6";
        descrizione = descrizione + "avviso di aggiudicazione settori speciali";
      }
    }

    Date dateOjUtil = null;
    Date noticeDateUtil = null;
    java.sql.Date noticeDateSql = null;
    java.sql.Date dateOjSql = null;
    String StrNoticeDate = (String) pubb.get("notice_date");
    if(StrNoticeDate != null){
      noticeDateUtil = new SimpleDateFormat("yyyyMMdd").parse(StrNoticeDate);
      noticeDateSql = new java.sql.Date(noticeDateUtil.getTime());
      }
    String StrDateOj = (String) pubb.get("date_oj");
    if(StrDateOj != null){
      dateOjUtil = new SimpleDateFormat("yyyyMMdd").parse(StrDateOj);
      dateOjSql = new java.sql.Date(dateOjUtil.getTime());
    }

    if(formulario != null && formulario.equals("SIMAP-FS1") || formulario.equals("SIMAP-FS2") || formulario.equals("SIMAP-FS4") || formulario.equals("SIMAP-FS5") || formulario.equals("SIMAP-FS8")){

      String esistePub = (String) sqlManager.getObject(
          "select codgar9 from pubbli where codgar9 = ? and tespub like '"+ pubb.get("notice_number_oj") +"%'",
          new Object[] { codgar });

      if(esistePub == null){
        //inserisco la pubb in pubbli
        Long numpub = (Long) sqlManager.getObject(
            "select max(numpub) from pubbli where codgar9 = ?",
            new Object[] { codgar });
        if(numpub == null){
          numpub = new Long(0);}

        String insertPubbli = "insert into pubbli(numpub,tippub,dinpub,datpub,tespub,codgar9) values(?,?,?,?,?,?)";
        this.sqlManager.update(insertPubbli, new Object[] { numpub +1,3,noticeDateSql,dateOjSql,tespub,codgar});

        String desc = tabellatiManager.getDescrTabellato("A1108", "1");
        if(desc!=null && !"".equals(desc))
          desc = desc.substring(0,1);
        boolean gestioneUrl=false;
        if("1".equals(desc))
          gestioneUrl=true;

        //se attiva la conf. nei tabellati, aggiorno occ. in documgara
        if(gestioneUrl){
          Long norddocg = (Long) sqlManager.getObject(
              "select max(norddocg) from documgara where codgar = ?",
              new Object[] { codgar });
          Long numeroPubbli = (Long) sqlManager.getObject(
              "select count(*) from pubbli where codgar9 = ? and (tippub = 11 or tippub = 13 or tippub = 23)",
              new Object[] { codgar });
          if(norddocg == null){
            norddocg = new Long(0);}
          Long tipologia = this.getTipologiaFromGruppo(codgar, new Long(1));
          if(numeroPubbli.intValue() == 0){
            if(codgar.charAt(0) == '$'){
              String insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,ngara,tipologia) values(?,?,?,?,?,?,?,?)";
              this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,1,tespub,pubb.get("ted_links"),codgar,codgar.substring(1),tipologia});
            }else{
              String insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,tipologia) values(?,?,?,?,?,?,?)";
              this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,1,tespub,pubb.get("ted_links"),codgar,tipologia});
            }
          }else{
            if(codgar.charAt(0) == '$'){
              String insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,ngara,datarilascio,statodoc,tipologia) values(?,?,?,?,?,?,?,?,?,?)";
              this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,1,tespub,pubb.get("ted_links"),codgar,codgar.substring(1),dateOjSql,new Long(5),tipologia});
            }else{
              String insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,datarilascio,statodoc,tipologia) values(?,?,?,?,?,?,?,?,?)";
              this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,1,tespub,pubb.get("ted_links"),codgar,dateOjSql,new Long(5),tipologia});
            }
          }
        }

        //se FS2 aggiorno il bando di gara in torn e gare
        if(form != null && (form.equals("FS2") || form.equals("FS5"))){
          String updateBandoGare = "update gare set DIBANDG = ?, DPUBAVG = ? where codgar1 = ?";
          this.sqlManager.update(updateBandoGare, new Object[] { noticeDateSql,dateOjSql,codgar});
          String updateBandoTorn = "update torn set DIBAND = ?, DPUBAV = ? where codgar = ?";
          this.sqlManager.update(updateBandoTorn, new Object[] { noticeDateSql,dateOjSql,codgar});
        }

        return descrizione;
      }
    }
    else{
      if(formulario != null && (formulario.equals("SIMAP-FS3") || formulario.equals("SIMAP-FS6"))){

        String ngara;
        Long numpub;
        if(codgar.charAt(0) == '$'){
          ngara = codgar.substring(1);
          numpub = (Long) sqlManager.getObject(
              "select max(npubg) from pubg where ngara = ?",
              new Object[] { ngara });
          if(numpub == null){
            numpub = new Long(0);}
        }else{
          ngara = (String) sqlManager.getObject(
              "select ngara from gare where codgar1 = ? and genere = 3",
              new Object[] { codgar });
          if(ngara == null){
            ngara = (String) sqlManager.getObject(
                "select ngara from gare where codgar1 = ? order by ngara",
                new Object[] { codgar });
          }
          numpub = (Long) sqlManager.getObject(
              "select max(npubg) from pubg where ngara = ?",
              new Object[] { ngara });
          if(numpub == null){
            numpub = new Long(0);}
        }

        String esistePub = (String) sqlManager.getObject(
            "select ngara from pubg where ngara = ? and tespubg like '"+ pubb.get("notice_number_oj") +"%'",
            new Object[] { ngara });

        if(esistePub == null){

          String insertPubg = "insert into pubg(npubg,tippubg,dinpubg,dinvpubg,tespubg,ngara) values(?,?,?,?,?,?)";

          this.sqlManager.update(insertPubg, new Object[] { numpub +1,3,dateOjSql,noticeDateSql,tespub,ngara});

          String desc = tabellatiManager.getDescrTabellato("A1108", "1");
          if(desc!=null && !"".equals(desc))
            desc = desc.substring(0,1);
          boolean gestioneUrl=false;
          if("1".equals(desc))
            gestioneUrl=true;

          //se attiva la conf. nei tabellati, aggiorno occ. in documgara
          if(gestioneUrl){
            Long norddocg = (Long) sqlManager.getObject(
                "select max(norddocg) from documgara where codgar = ?",
                new Object[] { codgar });
            Long numeroPubbli = (Long) sqlManager.getObject(
                "select count(*) from pubg where ngara = ? and tippubg = 12",
                new Object[] { ngara });
            if(norddocg == null){
              norddocg = new Long(0);}
            String insertDocumgara;
            Long tipologia = getTipologiaFromGruppo(codgar,new Long(4));
            if(codgar.charAt(0) == '$'){
              //lotto unico
              if(numeroPubbli.intValue() == 0){
                insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,ngara,tipologia) values(?,?,?,?,?,?,?,?)";
                this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,4,tespub,pubb.get("ted_links"),codgar,codgar.substring(1),tipologia});
              }else{
                insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,ngara,datarilascio,statodoc,tipologia) values(?,?,?,?,?,?,?,?,?,?)";
                this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,4,tespub,pubb.get("ted_links"),codgar,codgar.substring(1),dateOjSql,new Long(5),tipologia});
              }
            }else{
              ngara = (String) sqlManager.getObject(
                  "select ngara from gare where codgar1 = ? and genere = 3",
                  new Object[] { codgar });
              if(ngara == null){
                //a lotto plichi distinti
                List codiciGare = sqlManager.getListVector("select ngara from gare where codgar1 = ? order by ngara",
                    new Object[] { codgar });
                if (codiciGare != null && codiciGare.size() > 0) {
                    ngara = (String) SqlManager.getValueFromVectorParam(
                        codiciGare.get(0), 0).getValue();
                        if(new Long(0).equals(numeroPubbli)){
                          insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,ngara,tipologia) values(?,?,?,?,?,?,?,?)";
                          this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,4,tespub,pubb.get("ted_links"),codgar,ngara,tipologia});
                        }else{
                          insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,ngara,datarilascio,statodoc,tipologia) values(?,?,?,?,?,?,?,?,?,?)";
                          this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,4,tespub,pubb.get("ted_links"),codgar,ngara,dateOjSql,new Long(5),tipologia});;
                        }
                }
              }else{
                //a lotti plicco unico
                if(new Long(0).equals(numeroPubbli)){
                  insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar) values(?,?,?,?,?,?)";
                  this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,4,tespub,pubb.get("ted_links"),codgar});
                }else{
                  insertDocumgara = "insert into documgara(norddocg,numord,gruppo,descrizione,urldoc,codgar,datarilascio,statodoc) values(?,?,?,?,?,?,?,?)";
                  this.sqlManager.update(insertDocumgara, new Object[] { norddocg+1,norddocg+1,4,tespub,pubb.get("ted_links"),codgar,dateOjSql,new Long(5)});
                }
              }
            }
          }
          return descrizione;
        }
      }
    }
    return null;
  }

  private Long getTipologiaFromGruppo(String codgar, Long gruppo) throws SQLException, GestoreException{
    Long tipologia = null;
    List<?> w9cfPubb = sqlManager.getListVector("select ID, CL_WHERE_VIS, CL_WHERE_ULT from G1CF_PUBB where GRUPPO = ? order by NUMORD", new Object[] {gruppo});
    if (w9cfPubb != null && w9cfPubb.size() > 0) {

      for (int i = 0; i < w9cfPubb.size(); i++) {
        tipologia = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 0).longValue();
        String clausolaWhereVis = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 1).getValue();
        String clausolaWhereUlt = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 2).getValue();
        if (clausolaWhereVis != null && !clausolaWhereVis.equals("")) {
          clausolaWhereVis = " and (" + clausolaWhereVis + ")";
        }
        if (clausolaWhereUlt != null && !clausolaWhereUlt.equals("")) {
          clausolaWhereVis = clausolaWhereVis + " and (" + clausolaWhereUlt + ")";
        }
        String selectVisibile = "select count(*) from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=? ";
        Long countVisibile = (Long) sqlManager.getObject(selectVisibile + clausolaWhereVis, new Object[] {codgar});
        if(countVisibile.intValue()>0){
          break;
        }
      }
    }
    if(tipologia==null){
      switch(gruppo.intValue()) {
      case 1:
        tipologia = new Long(3);
      break;
      case 4:
        tipologia = new Long(20);
      break;
      default:
      }
    }
    return new Long(tipologia);
  }

  /**
   *
   * @param codgar
   * @param gruppo
   * @return boolean
   * @throws SQLException
   * @throws GestoreException
   */
  public boolean isGruppoVisibile(String codgar, Long gruppo) throws SQLException, GestoreException{
    boolean visibile=false;
    List<?> w9cfPubb = sqlManager.getListVector("select CL_WHERE_VIS, CL_WHERE_ULT from G1CF_PUBB where GRUPPO = ? order by NUMORD", new Object[] {gruppo});
    if (w9cfPubb != null && w9cfPubb.size() > 0) {

      for (int i = 0; i < w9cfPubb.size(); i++) {
        String clausolaWhereVis = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 0).getValue();
        String clausolaWhereUlt = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 1).getValue();
        if (clausolaWhereVis != null && !clausolaWhereVis.equals("")) {
          clausolaWhereVis = " and (" + clausolaWhereVis + ")";
        }
        if (clausolaWhereUlt != null && !clausolaWhereUlt.equals("")) {
          clausolaWhereVis = clausolaWhereVis + " and (" + clausolaWhereUlt + ")";
        }
        String selectVisibile = "select count(*) from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=? ";
        Long countVisibile = (Long) sqlManager.getObject(selectVisibile + clausolaWhereVis, new Object[] {codgar});
        if(countVisibile.intValue()>0){
          visibile=true;
          break;
        }
      }
    }
    return visibile;
  }

}

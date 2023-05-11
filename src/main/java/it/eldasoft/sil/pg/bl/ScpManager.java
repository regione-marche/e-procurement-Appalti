/*
 * Created on 10/feb/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.scp.AggiudicatarioEntry;
import it.eldasoft.sil.pg.bl.scp.AllegatoEntry;
import it.eldasoft.sil.pg.bl.scp.AppaFornEntry;
import it.eldasoft.sil.pg.bl.scp.CategoriaLottoEntry;
import it.eldasoft.sil.pg.bl.scp.CpvLottoEntry;
import it.eldasoft.sil.pg.bl.scp.DatiGeneraliStazioneAppaltanteEntry;
import it.eldasoft.sil.pg.bl.scp.DatiGeneraliTecnicoEntry;
import it.eldasoft.sil.pg.bl.scp.ImpresaEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicaAttoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicaAvvisoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicaGaraEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicaLottoEntry;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Classe di utilita' per l'interfacciamento con SCP
 *
 * @author Mirco.Franzoni
 *
 */
public class ScpManager {
	/** Logger. */
	static Logger logger = Logger.getLogger(ScpManager.class);

	public static final String PROP_WS_PUBBLICAZIONI_USERNAME = "invioScp.ws.username";
	public static final String PROP_WS_PUBBLICAZIONI_PASSWORD = "invioScp.ws.password";
	public static final String PROP_WS_PUBBLICAZIONI_URL = "invioScp.ws.url";
	//public static final String PROP_WS_PUBBLICAZIONI_URLTABELLECONTESTO = "invioScp.ws.urlTabelleContesto";
	public static final String PROP_WS_PUBBLICAZIONI_URL_LOGIN = "invioScp.ws.urlLogin";
	public static final String PROP_WS_PUBBLICAZIONI_IDCLIENT = "invioScp.ws.idClient";
	public static final String PROP_WS_PUBBLICAZIONI_KEYCLIENT = "invioScp.ws.keyClient";


	/** Manager SQL per le operazioni su database. */
	private SqlManager sqlManager;
	private GenChiaviManager genChiaviManager;
	private InviaVigilanzaManager inviaVigilanzaManager;
	private AggiudicazioneManager aggiudicazioneManager;
	private ControlliOepvManager controlliOepvManager;
	private PgManagerEst1 pgManagerEst1;

	/** DAO per la gestione dei file allegati. */

	public void setSqlManager(SqlManager sqlManager) {
	  this.sqlManager = sqlManager;
	}

	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
	  this.genChiaviManager = genChiaviManager;
	}

	public void setInviaVigilanzaManager(InviaVigilanzaManager inviaVigilanzaManager) {
  	  this.inviaVigilanzaManager = inviaVigilanzaManager;
  	}

	public void setAggiudicazioneManager(AggiudicazioneManager aggiudicazioneManager) {
      this.aggiudicazioneManager = aggiudicazioneManager;
    }

	public void setControlliOepvManager(ControlliOepvManager controlliOepvManager) {
      this.controlliOepvManager = controlliOepvManager;
    }

	public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
	  this.pgManagerEst1 = pgManagerEst1;
    }

	public ArrayList<HashMap<String,Object>> getAttiDaInviare(String codgar, String genere) throws SQLException{

	  ArrayList<HashMap<String,Object>> res = new ArrayList<HashMap<String,Object>>();
      String pubblicazioni = "SELECT distinct(dc.tipologia) FROM DOCUMGARA dc JOIN G1CF_PUBB G1 ON G1.ID = DC.TIPOLOGIA AND G1.INVIOSCP = '1'"+
      " LEFT OUTER JOIN GARATTISCP GA ON DC.TIPOLOGIA = GA.TIPOLOGIA AND GA.CODGAR = DC.CODGAR "+
      " where ga.tipologia is null and dc.codgar = ? and dc.statodoc = 5 and (dc.isarchi is null or dc.isarchi != '1') and dc.tipologia is not null and dc.tipologia not in (17,19,20)";

      List atti = sqlManager.getListVector(pubblicazioni, new Object[] {codgar});
      for(int i=0;i<atti.size();i++){
        Long tipologia = (Long) SqlManager.getValueFromVectorParam(atti.get(i), 0).getValue();
        HashMap<String,Object> temp = new HashMap<String,Object>();
        temp.put("tipologia", tipologia);
        res.add(temp);
      }

      String selectLottiAggiudicati = "select ngara,codiga from gare where codgar1 = ? and codgar1 != ngara and ditta is not null";
      String selectLottiAnnullati = "select ngara,codiga from gare where codgar1 = ? and codgar1 != ngara and esineg is not null";
      String selectAtti;
      if("2".equals(genere)){
        selectAtti = "SELECT count(*) FROM DOCUMGARA DC LEFT OUTER JOIN GARATTISCP GA ON DC.TIPOLOGIA = GA.TIPOLOGIA AND GA.CODGAR = DC.CODGAR " +
      		"where ga.tipologia is null and dc.codgar = ? and dc.statodoc = 5 and (dc.isarchi is null or dc.isarchi != '1') and dc.tipologia = ?";
      }else{
        selectAtti = "SELECT count(*) FROM DOCUMGARA DC LEFT OUTER JOIN GARATTISCP GA ON DC.TIPOLOGIA = GA.TIPOLOGIA AND GA.CODGAR = DC.CODGAR AND GA.NGARA = ? " +
        "where ga.tipologia is null and dc.codgar = ? and dc.statodoc = 5 and (dc.isarchi is null or dc.isarchi != '1') and dc.tipologia = ?";
      }
      List lottiAggiudicati = sqlManager.getListVector(selectLottiAggiudicati, new Object[] {codgar});
      for(int i=0;i<lottiAggiudicati.size();i++){
        String ngara = (String) SqlManager.getValueFromVectorParam(lottiAggiudicati.get(i), 0).getValue();
        String codiga = (String) SqlManager.getValueFromVectorParam(lottiAggiudicati.get(i), 1).getValue();
        Long count;
        if("2".equals(genere)){
          //se la gara è aggiudicata e ci sono atti di tipo 19 pubblicati, controllo se ci sono pubblicazioni SCP di tipo 19 associate alla gara
          count = (Long) sqlManager.getObject(selectAtti, new Object[] {codgar,19});
        }else{
          //se ci sono lotti aggiudicati e atti di tipo 19 pubblicati, controllo se ci sono pubblicazioni SCP di tipo 19 associate a quel lotto
          count = (Long) sqlManager.getObject(selectAtti, new Object[] {ngara,codgar,19});
        }
        if(count.intValue()>0){
          HashMap<String,Object> temp = new HashMap<String,Object>();
          temp.put("tipologia", new Long(19));
          temp.put("ngara", ngara);
          temp.put("codiga", codiga);
          res.add(temp);
        }
        if("2".equals(genere)){
          //se la gara è aggiudicata e ci sono atti di tipo 20 pubblicati, controllo se ci sono pubblicazioni SCP di tipo 20 associate alla gara
          count = (Long) sqlManager.getObject(selectAtti, new Object[] {codgar,20});
        }else{
          //se ci sono lotti aggiudicati e atti di tipo 20 pubblicati, controllo se ci sono pubblicazioni SCP di tipo 20 associate a quel lotto
          count = (Long) sqlManager.getObject(selectAtti, new Object[] {ngara,codgar,20});
        }
        if(count.intValue()>0){
          HashMap<String,Object> temp = new HashMap<String,Object>();
          temp.put("tipologia", new Long(20));
          temp.put("ngara", ngara);
          temp.put("codiga", codiga);
          res.add(temp);
        }
      }
      List lottiAnnullati = sqlManager.getListVector(selectLottiAnnullati, new Object[] {codgar});
      for(int i=0;i<lottiAnnullati.size();i++){
        String ngara = (String) SqlManager.getValueFromVectorParam(lottiAnnullati.get(i), 0).getValue();
        String codiga = (String) SqlManager.getValueFromVectorParam(lottiAnnullati.get(i), 1).getValue();
        Long count;
        if("2".equals(genere)){
          //se la gara è annullata e ci sono atti di tipo 17 pubblicati, controllo se ci sono pubblicazioni SCP di tipo 17 associate alla gara
          count = (Long) sqlManager.getObject(selectAtti, new Object[] {codgar,17});
        }else{
          //se ci sono lotti annullati e atti di tipo 17 pubblicati, controllo se ci sono pubblicazioni SCP di tipo 17 associate a quel lotto
          count = (Long) sqlManager.getObject(selectAtti, new Object[] {ngara,codgar,17});
        }
        if(count.intValue()>0){
          HashMap<String,Object> temp = new HashMap<String,Object>();
          temp.put("tipologia", new Long(17));
          temp.put("ngara", ngara);
          temp.put("codiga", codiga);
          res.add(temp);
        }
      }
      return res;
    }

	public String getNomeFromTipologia(Long tipologia) throws SQLException{
	  String nome = (String) sqlManager.getObject("select nome from g1cf_pubb where id = ?", new Object[] {tipologia});
	  return nome;
	}

	public Response getLogin(String entita) throws GestoreException, CriptazioneException{
	  String login = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_USERNAME);
      String password = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_PASSWORD);
      String url = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_URL);
      String urlLogin = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_URL_LOGIN);
      //String urlTabelleContesto = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_URLTABELLECONTESTO);
      String idClient = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_IDCLIENT);
      String keyClient = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_KEYCLIENT);
      if (login == null || "".equals(login) ||
              password == null || "".equals(password) ||
              urlLogin == null || "".equals(urlLogin) ||
              //urlTabelleContesto == null || "".equals(urlTabelleContesto) ||
              idClient == null || "".equals(idClient) ||
              keyClient == null || "".equals(keyClient)) {
          throw new GestoreException(
                    "Verificare i parametri per la connessione al servizio di pubblicazione",
                    "errors.errorResponse");
      }
      if ((entita.equals("pubblicazioni") || entita.equals("avvisi")) &&
              (url == null || "".equals(url)) ) {
          throw new GestoreException(
                    "Verificare i parametri per la connessione al servizio di pubblicazione",
                    "errors.errorResponse");
      }

      ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
              password.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
      password = new String(decriptatore.getDatoNonCifrato());

      decriptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          keyClient.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
      keyClient = new String(decriptatore.getDatoNonCifrato());

      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client.target(urlLogin).path("Account/LoginPubblica");
      MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
      formData.add("username", login);
      formData.add("password", password);
      formData.add("clientKey", keyClient);
      formData.add("clientId", idClient);

      return webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(formData,MediaType.APPLICATION_FORM_URLENCODED));
	}

	/*
	public void inserimentoFlussoAvviso(final PubblicaAvvisoEntry avviso, final ProfiloUtente profilo, final Long idAvviso, final String codein) throws GestoreException {

	    String insertW9Flussi = "INSERT INTO W9FLUSSI(IDFLUSSO, AREA, KEY01, KEY02, KEY03, TINVIO2, DATINV, CODCOMP, AUTORE, CFSA , XML ) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

	    TransactionStatus status = null;
	    boolean commitTransaction = false;
	    try {
	    	Long tipoInvio = new Long(1);
	    	Long idFlusso = new Long(this.genChiaviManager.getMaxId("W9FLUSSI", "IDFLUSSO") + 1);
	    	Long countFlussi = (Long)this.sqlManager.getObject("select count(*) from W9FLUSSI where AREA = ? and KEY01 = ? and KEY02 = ? and KEY03 = ? and CFSA = ? ",
	    			new Object[] {new Long(3), idAvviso, new Long(1), new Long(989), avviso.getCodiceFiscaleSA()});
	    	if (countFlussi > 0) {
	    		tipoInvio = new Long(2);
	    	}
	    	status = this.sqlManager.startTransaction();
	    	ObjectMapper mapper = new ObjectMapper();
	      	this.sqlManager.update(insertW9Flussi, new Object[] { idFlusso, new Long(3), idAvviso, new Long(1),
	      			new Long(989), tipoInvio, new Timestamp(System.currentTimeMillis()), profilo.getId(), profilo.getNome(), avviso.getCodiceFiscaleSA(), mapper.writeValueAsString(avviso) });
	      	this.sqlManager.update(updateAVVISOIdRicevuto, new Object[] {avviso.getIdRicevuto(), codein, idAvviso, new Long(1)});
	      	commitTransaction = true;
	    } catch (Exception e) {
	      throw new GestoreException(
	          "errore durante l'archiviazione della gara",
	          "archiviagara.error", e);
	    } finally {
	        if (status != null) {
	          try {
	            if (commitTransaction) {
	            	this.sqlManager.commitTransaction(status);
	            } else {
	            	this.sqlManager.rollbackTransaction(status);
	            }
	          } catch (SQLException e) {
	            logger.error("Errore durante la chiusura della transazione", e);
	          }
	        }
	      }

	    }

	public void inserimentoFlussoExArt29(final PubblicaAttoEntry pubblicazione, final ProfiloUtente profilo, final String codGara, final Long numeroPubblicazione) throws GestoreException {

	    String insertW9Flussi = "INSERT INTO W9FLUSSI(IDFLUSSO, AREA, KEY01, KEY03, KEY04, TINVIO2, DATINV, CODCOMP, AUTORE, CFSA, XML ) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	    TransactionStatus status = null;
	    boolean commitTransaction = false;
	    try {
	    	Long tipoInvio = new Long(1);
	    	Long idFlusso = new Long(this.genChiaviManager.getMaxId("W9FLUSSI", "IDFLUSSO") + 1);
	    	Long countFlussi = (Long)this.sqlManager.getObject("select count(*) from W9FLUSSI where AREA = ? and KEY01 = ? and KEY03 = ? and KEY04 = ? ",
	    			new Object[] {new Long(2), codGara, new Long(901), numeroPubblicazione});
	    	if (countFlussi > 0) {
	    		tipoInvio = new Long(2);
	    	}
	    	status = this.sqlManager.startTransaction();
	    	ObjectMapper mapper = new ObjectMapper();

	      	this.sqlManager.update(insertW9Flussi, new Object[] { idFlusso, new Long(2), codGara,
	      			new Long(901), numeroPubblicazione, tipoInvio, new Timestamp(System.currentTimeMillis()), profilo.getId(), profilo.getNome(), pubblicazione.getGara().getCodiceFiscaleSA(),
	      			mapper.writeValueAsString(pubblicazione)});
	      	this.sqlManager.update(updateW9PUBBLICAZIONIIdRicevuto, new Object[] {pubblicazione.getIdRicevuto(), codGara, numeroPubblicazione});
	      	this.sqlManager.update(updateW9GARAIdRicevuto, new Object[] {pubblicazione.getGara().getIdRicevuto(), codGara});
	      	commitTransaction = true;
	    } catch (Exception e) {
	      throw new GestoreException(
	          "errore durante l'archiviazione della gara",
	          "archiviagara.error", e);
	    } finally {
	        if (status != null) {
	          try {
	            if (commitTransaction) {
	            	this.sqlManager.commitTransaction(status);
	            } else {
	            	this.sqlManager.rollbackTransaction(status);
	            }
	          } catch (SQLException e) {
	            logger.error("Errore durante la chiusura della transazione", e);
	          }
	        }
	      }
	    }
	*/

	  public void setUUID(PubblicaAttoEntry pubblicazione, String codgar, String ngara, Long tipologia, String genere) throws SQLException{

	    Long countOcc = (Long)sqlManager.getObject("select count(*) from garattiscp where codgar = ?", new Object[] {codgar});

        Date sqlData = new Date(System.currentTimeMillis());
        Timestamp dataTimestamp = new Timestamp(System.currentTimeMillis());
        java.util.Date today = DateUtils.truncate(sqlData, Calendar.DATE);

	    if(countOcc.intValue()==0){

	        Integer newId= this.genChiaviManager.getNextId("GARUUID");
	        String insertGaruuid = "insert into garuuid(id,codgar,tipric,uuid) values(?,?,?,?)";
	        this.sqlManager.update(insertGaruuid, new Object[] { newId,codgar,"SCP",pubblicazione.getGara().getIdRicevuto()});

	        Long numpub = (Long) sqlManager.getObject(
	            "select max(numpub) from pubbli where codgar9 = ?",
	            new Object[] { codgar });
	        if(numpub == null){
	          numpub = new Long(0);}
	        String insertPubbli = "insert into pubbli(numpub,tippub,datpub,codgar9) values(?,?,?,?)";
	        this.sqlManager.update(insertPubbli, new Object[] { numpub +1,16,today,codgar});

	    }

	    Integer newId= this.genChiaviManager.getNextId("GARATTISCP");
	    if(!"2".equals(genere) && (tipologia.intValue() == 17 || tipologia.intValue() == 19 || tipologia.intValue() == 20)){
	      String insertGarattiSCP = "insert into garattiscp(ID,CODGAR,NGARA,TIPOLOGIA,DATPUB,UUID) values (?,?,?,?,?,?)";
	      this.sqlManager.update(insertGarattiSCP, new Object[] {newId,codgar,ngara,tipologia,dataTimestamp,pubblicazione.getIdRicevuto()});
	    }else{
	      String insertGarattiSCP = "insert into garattiscp(ID,CODGAR,TIPOLOGIA,DATPUB,UUID) values (?,?,?,?,?)";
	      this.sqlManager.update(insertGarattiSCP, new Object[] {newId,codgar,tipologia,dataTimestamp,pubblicazione.getIdRicevuto()});
	    }

	  }

	  public void setUUIDavviso(PubblicaAvvisoEntry avviso, String codgar, String ngara) throws SQLException{

	    Long countOcc = (Long)sqlManager.getObject("select count(*) from pubbli where tippub = 16 and codgar9 = ?", new Object[] {codgar});

        if(countOcc.intValue()==0){
      	    Date sqlData = new Date(System.currentTimeMillis());
      	    java.util.Date today = DateUtils.truncate(sqlData, Calendar.DATE);

      	    Integer newId= this.genChiaviManager.getNextId("GARUUID");
      	    String insertGaruuid = "insert into garuuid(id,codgar,tipric,uuid) values(?,?,?,?)";
      	    this.sqlManager.update(insertGaruuid, new Object[] { newId,codgar,"SCP",avviso.getIdRicevuto()});

      	    Long numpub = (Long) sqlManager.getObject(
      	       "select max(numpub) from pubbli where codgar9 = ?",
      	       new Object[] { codgar });
      	    if(numpub == null){
      	       numpub = new Long(0);}
      	    String insertPubbli = "insert into pubbli(numpub,tippub,datpub,codgar9) values(?,?,?,?)";
      	    this.sqlManager.update(insertPubbli, new Object[] { numpub +1,16,today,codgar});
        }
	}

	/**
	 * Metodo valorizzazione Dati Avviso
	 *
	 * @param avviso
	 * 			oggetto da valorizzare
	 * @param codein
	 *            codice stazione appaltante
	 * @param idAvviso
	 *            id avviso
	 * @param codiceSistema
	 *            codice sistema
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 * @throws GestoreException
	 */
	public void valorizzaAvviso(final PubblicaAvvisoEntry avviso,
			final String codgar, String genere)
	throws SQLException, ParseException, GestoreException {

		String sqlUffint = "select cfein, iscuc, altrisog, cfanac, codrup, uffdet from torn, uffint where torn.cenint = uffint.codein and torn.codgar = ?";
        List datiUffint = sqlManager.getListVector(sqlUffint, new Object[] {codgar});

        String cfein = SqlManager.getValueFromVectorParam(datiUffint.get(0), 0).stringValue();
        String iscuc = SqlManager.getValueFromVectorParam(datiUffint.get(0), 1).stringValue();
        Long altrisog = (Long) SqlManager.getValueFromVectorParam(datiUffint.get(0), 2).getValue();
        String cfanac = SqlManager.getValueFromVectorParam(datiUffint.get(0), 3).stringValue();
        String codrup = SqlManager.getValueFromVectorParam(datiUffint.get(0), 4).stringValue();
        Long uffdet = (Long) SqlManager.getValueFromVectorParam(datiUffint.get(0), 5).getValue();

        if("1".equals(iscuc) && (altrisog!= null && (altrisog.intValue()==2 || altrisog.intValue()==3)) && verificaEsistenzaValore(cfanac)){
          avviso.setCodiceFiscaleSA(cfanac);//obbligatorio
        }else{
          if (verificaEsistenzaValore(cfein)) {
            avviso.setCodiceFiscaleSA(cfein);//obbligatorio
          }
        }
        if (verificaEsistenzaValore(uffdet)) {
          avviso.setUfficio(this.getTab1("A1092",uffdet.toString()));
        }

        String oggetto;
        Long tipologia;
        Date scadenza;
        if("11".equals(genere)){
          String sqlGareavvisi = "select tipoavv, oggetto, datsca from gareavvisi where gareavvisi.codgar = ?";
          List datiGareavvisi = sqlManager.getListVector(sqlGareavvisi, new Object[] {codgar});
          Long tipoavviso = (Long) SqlManager.getValueFromVectorParam(datiGareavvisi.get(0), 0).getValue();
          oggetto = SqlManager.getValueFromVectorParam(datiGareavvisi.get(0), 1).stringValue();
          scadenza = (Date) SqlManager.getValueFromVectorParam(datiGareavvisi.get(0), 2).getValue();
          if(tipoavviso != null && tipoavviso.intValue() == 1){
            tipologia = new Long(2);
          }else{
            tipologia = new Long(6);
          }
          avviso.setTipologia(tipologia);
        }else{
          avviso.setTipologia(new Long(6));
          String sqlGarealbo = "select oggetto from garealbo where garealbo.codgar = ?";
          oggetto = (String) sqlManager.getObject(sqlGarealbo, new Object[] {codgar});
          scadenza = (Date)sqlManager.getObject("select DTERMPRES from PUBBTERM where codgar = ? order by numpt desc", new Object[]{codgar});
        }

        if (verificaEsistenzaValore(scadenza)) {
          SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
          String tempDate = formatter.format(scadenza);
          avviso.setScadenza(tempDate.toString());//obbligatorio
        }
        if (verificaEsistenzaValore(oggetto)) {
          avviso.setDescrizione(this.truncateString(oggetto,500));//obbligatorio
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dataPrimaPubb = new Date(System.currentTimeMillis());
        String currentDate = formatter.format(dataPrimaPubb);
        avviso.setPrimaPubblicazioneSCP(currentDate);

        String sqlDataRilascio = "select datarilascio from documgara where codgar = ? and statodoc = 5 and (isarchi is null or isarchi != '1') order by datarilascio asc";
        Date dataRilascio = (Date) sqlManager.getObject(sqlDataRilascio, new Object[] {codgar});
        String dataRilascioStringa = formatter.format(dataRilascio);
        avviso.setData(dataRilascioStringa);

        if (verificaEsistenzaValore(codrup)) {
          DatiGeneraliTecnicoEntry tecnico = new DatiGeneraliTecnicoEntry();
          this.valorizzaTecnico(tecnico, codrup);//OK
          avviso.setRup(tecnico);
        }

        String uuid = (String)this.sqlManager.getObject("SELECT UUID FROM GARUUID WHERE CODGAR = ? AND TIPRIC = 'SCP'", new Object[] {codgar});
        if (verificaEsistenzaValore(uuid)) {
          avviso.setIdRicevuto(new Long(uuid));
        }

        this.valorizzaDocumenti(avviso.getDocumenti(), codgar, null, genere);
	}

	/**
	 * Metodo valorizzazione Dati Pubblicazione
	 *
	 * @param pubblicazione
	 * 			oggetto da valorizzare
	 * @param codGara
	 *            codice della gara
	 * @param numeroPubblicazione
	 *            numero pubblicazione gara
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 * @throws GestoreException
	 */
	public void valorizzaAtto(final PubblicaAttoEntry pubblicazione,
			final String codGara,final String ngara, Long tipologia, String genere)
	throws SQLException, ParseException, GestoreException {
		String sqlGare = "select ribagg, iaggiu, dattoa, modlicg, ditta, riboepv from GARE where ngara = ?";
		String sqlTorn = "select iterga, dtepar, dteoff, aqoper, cenint from TORN where codgar = ?";
		List< ? > datiGare, datiTorn;
		List< ? > itemTorn = new ArrayList<Object>();
		List< ? > itemGare = new ArrayList<Object>();

		datiTorn = sqlManager.getListVector(sqlTorn, new Object[] {codGara});

		if (datiTorn.size() > 0) {
			itemTorn = (List< ? >) datiTorn.get(0);

			if (verificaEsistenzaValore(tipologia)) {
				pubblicazione.setTipoDocumento(tipologia);//obbligatorio
			}

			Date datapubblicazione = (Date)sqlManager.getObject("select min(datarilascio) from DOCUMGARA where codgar = ? and tipologia = ? and statodoc = 5 and (isarchi is null or isarchi != '1')", new Object[]{codGara,tipologia});
			if (verificaEsistenzaValore(datapubblicazione)) {
			    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	            String tempDate = formatter.format(datapubblicazione);
				pubblicazione.setDataPubblicazione(tempDate.toString());
			}

			if(tipologia.intValue()==2 || tipologia.intValue()==3 || tipologia.intValue() == 6){
    			String iterga = (itemTorn.get(0)).toString();
    			Date dataScadenza;
    			if(iterga != null && ("2".equals(iterga) || "4".equals(iterga)) && tipologia.intValue() != 6){
    			  dataScadenza = (Date) SqlManager.getValueFromVectorParam(itemTorn, 1).getValue();
    			}else{
    			  dataScadenza = (Date) SqlManager.getValueFromVectorParam(itemTorn, 2).getValue();
    			}
    			if (verificaEsistenzaValore(dataScadenza)){
    			    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String tempDate = formatter.format(dataScadenza);
    				pubblicazione.setDataScadenza(tempDate);
    			}
			}

			List provvedimento = sqlManager.getVector("select dataprov, numprov from DOCUMGARA where STATODOC = 5 and DATAPROV is not null and tipologia = ? and codgar = ? and statodoc = 5 and (isarchi is null or isarchi != '1') order by dataprov asc", new Object[]{tipologia,codGara});
			if(provvedimento != null && provvedimento.size()>0){
    			Date dataProvvedimento = (Date) SqlManager.getValueFromVectorParam(provvedimento, 0).getValue();
    			String numeroProvvedimento = SqlManager.getValueFromVectorParam(provvedimento, 1).stringValue();
    			if (verificaEsistenzaValore(dataProvvedimento)) {
    			    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    			    String tempDate = formatter.format(dataProvvedimento);
                    pubblicazione.setDataProvvedimento(tempDate);
                }
    			if (verificaEsistenzaValore(numeroProvvedimento)) {
    				pubblicazione.setNumeroProvvedimento(numeroProvvedimento);
    			}
			}

			if(tipologia.intValue() == 19 || tipologia.intValue() == 20){
			    datiGare = sqlManager.getListVector(sqlGare, new Object[] {ngara});
			    if(tipologia != null && (tipologia.intValue() == 19 || tipologia.intValue() == 20)){
	              itemGare = (List< ? >) datiGare.get(0);
	            }

			    Double iaggiu = (Double) SqlManager.getValueFromVectorParam(itemGare, 1).getValue();
                pubblicazione.setImportoAggiudicazione(iaggiu);

    			Long modlicg = (Long) SqlManager.getValueFromVectorParam(itemGare, 3).getValue();
    			String ditta = (String) SqlManager.getValueFromVectorParam(itemGare, 4).getValue();
    			Double ribauo = null;
    			Double riboepv = null;
    			if(modlicg != null && modlicg.intValue() == 6){
					riboepv = (Double) SqlManager.getValueFromVectorParam(itemGare, 5).getValue();
					if(riboepv != null) {
						ribauo = riboepv;
					}else {
	  	                String select = "select impapp, onprge, impsic, impnrl, sicinc, impoff, onsogrib from gare, ditg where gare.ngara = ? and gare.ditta = ? and ditg.ngara5 = gare.ngara and gare.ditta = ditg.dittao";
	  	                List ribassoVector = sqlManager.getVector(select, new Object[]{ngara,ditta});
	  	                if(ribassoVector != null && ribassoVector.size()>0){
	  	                  Double impapp = (Double) SqlManager.getValueFromVectorParam(ribassoVector, 0).getValue();
	  	                  if (!verificaEsistenzaValore(impapp)){impapp = new Double(0);}
	  	                  Double onprge = (Double) SqlManager.getValueFromVectorParam(ribassoVector, 1).getValue();
	  	                  if (!verificaEsistenzaValore(onprge)){onprge = new Double(0);}
	  	                  Double impsic = (Double) SqlManager.getValueFromVectorParam(ribassoVector, 2).getValue();
	  	                  if (!verificaEsistenzaValore(impsic)){impsic = new Double(0);}
	  	                  Double impnrl = (Double) SqlManager.getValueFromVectorParam(ribassoVector, 3).getValue();
	  	                  if (!verificaEsistenzaValore(impnrl)){impnrl = new Double(0);}
	  	                  String sicinc = (String) SqlManager.getValueFromVectorParam(ribassoVector, 4).getValue();
	  	                  String onsogrib = (String) SqlManager.getValueFromVectorParam(ribassoVector, 6).getValue();
	    	              //calcolo il valore offerto a partire dall'aggiudicazione
	  	                  Double impoff = new Double(0);
	  	                  if(sicinc != null && "2".equals(sicinc)){
	  	                    impoff = iaggiu - impsic;
	  	                  }else{
	  	                    impoff = iaggiu;
	  	                  }
	  	                  ribauo = new Double(this.aggiudicazioneManager.calcolaRIBAUO(impapp.doubleValue(), onprge.doubleValue(), impsic.doubleValue(), impnrl.doubleValue(), sicinc, impoff.doubleValue(), onsogrib));
	  	                  String cifreRibasso=pgManagerEst1.getNumeroDecimaliRibasso(codGara);
	  	                  if(cifreRibasso!=null && !"".equals(cifreRibasso)){
		  	                 ribauo = (Double)UtilityNumeri.arrotondaNumero(ribauo, new Integer(cifreRibasso));
	  	                  }
	  	                }
					}
    			}else{
    				ribauo = (Double) SqlManager.getValueFromVectorParam(itemGare, 0).getValue();
    			}
    			if(ribauo != null){
    			  if(ribauo <= 0){
    			    pubblicazione.setRibassoAggiudicazione(ribauo * (-1));
    			  }else{
    			    pubblicazione.setOffertaAumento(ribauo);
    			  }
    			}

    			Date dattoa = (Date) SqlManager.getValueFromVectorParam(itemGare, 2).getValue();
                if (verificaEsistenzaValore(dattoa)) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String tempDate = formatter.format(dattoa);
                    pubblicazione.setDataAggiudicazione(tempDate);
                }
			}
			String uffint = itemTorn.get(4).toString();
			String profco = (String)sqlManager.getObject("select profco from UFFINT where codein = ?", new Object[]{uffint});
			if (verificaEsistenzaValore(profco)) {
				pubblicazione.setUrlCommittente(profco);
			}

			String urlEprocurement = ConfigManager.getValore("portaleAppalti.urlPubblica");
			if (verificaEsistenzaValore(urlEprocurement)) {
				pubblicazione.setUrlEProcurement(urlEprocurement);
			}

			String idRicevuto = null;
			Date dataPrimaPubb = null;
            if((tipologia.intValue() == 17 || tipologia.intValue() == 19 || tipologia.intValue() == 20) && !"2".equals(genere)){
              List vector = sqlManager.getVector("select uuid, datpub from garattiscp where ngara = ? and tipologia = ?", new Object[] {ngara,tipologia});
              if(vector!= null && vector.size() > 0){
                idRicevuto = (String)SqlManager.getValueFromVectorParam(vector, 0).getValue();
                dataPrimaPubb = (Date) SqlManager.getValueFromVectorParam(vector, 1).getValue();
              }
            }else{
              List vector = sqlManager.getVector("select uuid, datpub from garattiscp where codgar = ? and tipologia = ?", new Object[] {codGara,tipologia});
              if(vector!= null && vector.size() > 0){
                idRicevuto = (String) SqlManager.getValueFromVectorParam(vector, 0).getValue();
                dataPrimaPubb = (Date) SqlManager.getValueFromVectorParam(vector, 1).getValue();
              }
            }
            if (verificaEsistenzaValore(idRicevuto)) {
              pubblicazione.setIdRicevuto(new Long(idRicevuto));
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            if (!verificaEsistenzaValore(dataPrimaPubb)) {
              dataPrimaPubb = new Date(System.currentTimeMillis());
            }
		    String currentDate = formatter.format(dataPrimaPubb);
		    if (verificaEsistenzaValore(currentDate)) {
              pubblicazione.setPrimaPubblicazioneSCP(currentDate);
		    }

			PubblicaGaraEntry gara = new PubblicaGaraEntry();
			this.valorizzaGara(gara, codGara, ngara, genere);
			pubblicazione.setGara(gara);
			if(tipologia.intValue() == 19 || tipologia.intValue() == 20){
			  this.valorizzaAggiudicatari(pubblicazione.getAggiudicatari(), ngara);
			}

			this.valorizzaDocumenti(pubblicazione.getDocumenti(), codGara, tipologia, genere);
		}
	}


	public BlobFile getDocumentoAvviso(Long idavviso, String codein, Long codsistema, Long numdoc) throws DataAccessException, SQLException {
		HashMap<String, Object> hashMapFileAllegato = new HashMap<String, Object>();
	    hashMapFileAllegato.put("idavviso", idavviso);
	    hashMapFileAllegato.put("codein", codein);
	    hashMapFileAllegato.put("codsistema", codsistema);
	    hashMapFileAllegato.put("numdoc", numdoc);
	    //BlobFile documento = this.w9FileDao.getFileAllegato("AVVISO", hashMapFileAllegato);
		//return documento;
	    return null;
	}

	public BlobFile getDocumentoAtto(String codiceGara, Long numeroPubblicazione, Long numdoc) throws DataAccessException, SQLException {
		HashMap<String, Object> hashMapFileAllegato = new HashMap<String, Object>();
		hashMapFileAllegato.put("codGara", codiceGara);
        hashMapFileAllegato.put("numdoc", numdoc);
        hashMapFileAllegato.put("num_pubb", numeroPubblicazione);
	    //BlobFile documento = this.w9FileDao.getFileAllegato("GARA", hashMapFileAllegato);
        //return documento;
        return null;
	}

	/**
	 * Metodo valorizzazione Dati Tecnico
	 *
	 * @param tecnico
	 * 			oggetto da valorizzare
	 * @param codtec
	 *            codice tecnico
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 */
	private void valorizzaTecnico(final DatiGeneraliTecnicoEntry tecnico,
			final String codtec)
	throws SQLException, ParseException {
		String sqlRecuperaTECNICO = "SELECT cogtei, nometei, nomtec, indtec, "
			+ "ncitec, loctec, protec, captec, teltec, faxtec, cftec, PIVATEC, CITTEC "
			+ "FROM TECNI WHERE CODTEC = ? ";

		List< ? > listaTecnico;
		List< ? > itemTecnico = new ArrayList<Object>();

		listaTecnico = sqlManager.getListVector(sqlRecuperaTECNICO, new Object[] {
				codtec });
		if (listaTecnico.size() > 0) {
			itemTecnico = (List< ? >) listaTecnico.get(0);

			if (verificaEsistenzaValore(itemTecnico.get(0))) {
				tecnico.setCognome(itemTecnico.get(0).toString());//obbligatorio
			}
			if (verificaEsistenzaValore(itemTecnico.get(1))) {
				tecnico.setNome(itemTecnico.get(1).toString());//obbligatorio
			}
			if (verificaEsistenzaValore(itemTecnico.get(2))) {
				tecnico.setNomeCognome(itemTecnico.get(2).toString());
			}
			if (verificaEsistenzaValore(itemTecnico.get(3))) {
				tecnico.setIndirizzo(this.truncateString(itemTecnico.get(3).toString(),100));
			}
			if (verificaEsistenzaValore(itemTecnico.get(4))) {
				tecnico.setCivico(itemTecnico.get(4).toString());
			}

			if (verificaEsistenzaValore(itemTecnico.get(5))) {
				//tecnico.setLocalita(itemTecnico.get(5).toString());
			}
			if (verificaEsistenzaValore(itemTecnico.get(6))) {
				tecnico.setProvincia(itemTecnico.get(6).toString());
			}
			if (verificaEsistenzaValore(itemTecnico.get(7))) {
				tecnico.setCap(itemTecnico.get(7).toString());
			}
			if (verificaEsistenzaValore(itemTecnico.get(8))) {
				//tecnico.setTelefono(itemTecnico.get(8).toString());
			}
			if (verificaEsistenzaValore(itemTecnico.get(9))) {
				//tecnico.setFax(itemTecnico.get(9).toString());
			}
			if (verificaEsistenzaValore(itemTecnico.get(10))) {
			  tecnico.setCfPiva(itemTecnico.get(10).toString());//obbligatorio
			}else{
			  tecnico.setCfPiva(itemTecnico.get(11).toString());//obbligatorio
			}
			if (verificaEsistenzaValore(itemTecnico.get(12))) {
				tecnico.setLuogoIstat(itemTecnico.get(12).toString());
			}
		}
	}

	/**
	 * Metodo valorizzazione Dati Stazione Appaltante
	 *
	 * @param stazioneAppaltante
	 * 			oggetto da valorizzare
	 * @param codein
	 *            codice stazione appaltante
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 */
	public void valorizzaStazioneAppaltante(final DatiGeneraliStazioneAppaltanteEntry stazioneAppaltante,
			final String codein)
	throws SQLException, ParseException {
		String sqlRecuperaUFFINT = "SELECT NOMEIN,VIAEIN,NCIEIN,CITEIN,CODCIT,PROEIN,CAPEIN,CODNAZ,TELEIN,FAXEIN,CFEIN,TIPOIN,EMAIIN,EMAI2IN,CFANAC,ISCUC "
			+ "FROM UFFINT WHERE CODEIN = ? ";

		List< ? > listaUffint;
		List< ? > itemUffint = new ArrayList<Object>();

		listaUffint = sqlManager.getListVector(sqlRecuperaUFFINT, new Object[] {
				codein });
		if (listaUffint.size() > 0) {
			itemUffint = (List< ? >) listaUffint.get(0);

			if (verificaEsistenzaValore(itemUffint.get(0))) {
				stazioneAppaltante.setDenominazione(itemUffint.get(0).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(1))) {
				stazioneAppaltante.setIndirizzo(itemUffint.get(1).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(2))) {
				stazioneAppaltante.setCivico(itemUffint.get(2).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(3))) {
				stazioneAppaltante.setLocalita(itemUffint.get(3).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(4))) {
				stazioneAppaltante.setCodiceIstat(itemUffint.get(4).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(5))) {
				stazioneAppaltante.setProvincia(itemUffint.get(5).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(6))) {
				stazioneAppaltante.setCap(itemUffint.get(6).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(7))) {
				stazioneAppaltante.setCodiceNazione(Integer.parseInt(itemUffint.get(7).toString()));
			}
			if (verificaEsistenzaValore(itemUffint.get(8))) {
				stazioneAppaltante.setTelefono(itemUffint.get(8).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(9))) {
				stazioneAppaltante.setFax(itemUffint.get(9).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(10))) {
				stazioneAppaltante.setCodiceFiscale(itemUffint.get(10).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(11))) {
				stazioneAppaltante.setTipoAmministrazione(new Long(itemUffint.get(11).toString()));
			}
			if (verificaEsistenzaValore(itemUffint.get(12))) {
				stazioneAppaltante.setEmail(itemUffint.get(12).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(13))) {
				stazioneAppaltante.setPec(itemUffint.get(13).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(14))) {
				stazioneAppaltante.setCfAnac(itemUffint.get(14).toString());
			}
			if (verificaEsistenzaValore(itemUffint.get(15))) {
			  stazioneAppaltante.setIscuc(itemUffint.get(15).toString());
			}
		}
	}

	/**
	 * Metodo valorizzazione Dati Impresa
	 *
	 * @param impresa
	 * 			oggetto da valorizzare
	 * @param codimp
	 *            codice impresa
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 */
	private void valorizzaImpresa(final ImpresaEntry impresa,
			final String codimp)
	throws SQLException, ParseException {
		String sqlRecuperaIMPRESA = "SELECT nomest, natgiui, proimp, cfimp, "
			+ "pivimp, nazimp "
			+ "FROM IMPR WHERE CODIMP = ? ";

		List< ? > listaImpresa;
		List< ? > itemImpresa = new ArrayList<Object>();

		listaImpresa = sqlManager.getListVector(sqlRecuperaIMPRESA, new Object[] {codimp });
		if (listaImpresa.size() > 0) {
			itemImpresa = (List< ? >) listaImpresa.get(0);
			String ragioneSociale = itemImpresa.get(0).toString();
			if (verificaEsistenzaValore(ragioneSociale)) {
				impresa.setRagioneSociale(this.truncateString(ragioneSociale,61));
			}
			if (verificaEsistenzaValore(itemImpresa.get(1))) {
			  Long natgiu = new Long(itemImpresa.get(1).toString());
	          if (natgiu.intValue() >=0 && natgiu.intValue() <=17) {
			    impresa.setFormaGiuridica(natgiu);
	          }
			}
			if (verificaEsistenzaValore(itemImpresa.get(2))) {
				impresa.setProvincia(itemImpresa.get(2).toString());
			}
			if (verificaEsistenzaValore(itemImpresa.get(3))) {
				impresa.setCodiceFiscale(itemImpresa.get(3).toString());
			}else{
			  if (verificaEsistenzaValore(itemImpresa.get(4))) {
                impresa.setCodiceFiscale(itemImpresa.get(4).toString());
              }
			}
			if (verificaEsistenzaValore(itemImpresa.get(4))) {
				impresa.setPartitaIva(itemImpresa.get(4).toString());
			}
			if (verificaEsistenzaValore(itemImpresa.get(5))) {
			  String sigla = getSiglaTabG_z23(itemImpresa.get(5).toString());
			  if (verificaEsistenzaValore(sigla)) {
				impresa.setNazione(sigla);
			  }
			}
		}
	}

	/**
	 * Metodo valorizzazione Documenti Pubblicazione
	 *
	 * @param documenti
	 * 			oggetto da valorizzare
	 * @param codGara
	 *            codice della gara
	 * @param numeroPubblicazione
	 *            numero pubblicazione gara
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 */
	private void valorizzaDocumenti(final List<AllegatoEntry> documenti,
	    final String codgar,final Long tipologia,final String genere)
	throws SQLException, ParseException {
	    String selectTitolo = "select nome from g1cf_pubb where id = ?";
	    String titolo = null;
	    String ngara = (String) sqlManager.getObject("select ngara from gare where codgar1 = ?", new Object[] {codgar});
		String url = ConfigManager.getValore("portaleAppalti.urlPubblica");
		//String urlEprocurement = ConfigManager.getValore("portaleAppalti.urlPubblica");
		if("10".equals(genere) || "11".equals(genere) || "20".equals(genere)){
		  url+= ConfigManager.getValore("invioScp.urlAvvisiPortaleAppalti");
		  if("11".equals(genere)){
		    titolo = (String) sqlManager.getObject(selectTitolo, new Object[] {new Long(85)});
		  }else if("10".equals(genere) || "20".equals(genere)){
		    titolo = (String) sqlManager.getObject(selectTitolo, new Object[] {new Long(80)});
		  }
		  url+=ngara;
		}else{
		    titolo = (String) sqlManager.getObject(selectTitolo, new Object[] {tipologia});
    		if(tipologia.intValue() == 17 || tipologia.intValue() == 19 || tipologia.intValue() == 20){
    		  url+= ConfigManager.getValore("invioScp.urlEsitiPortaleAppalti");
    		}else{
        		if(tipologia.intValue() == 1){
        		  url+= ConfigManager.getValore("invioScp.urlDeliberaPortaleAppalti");
                }else{
                  String queryPubg = "select count(*) from pubg, gare where gare.ngara = pubg.ngara and gare.codgar1 = ? and pubg.tippubg = 12";
                  Long pubblicatoEsito = (Long) sqlManager.getObject(queryPubg, new Object[] {codgar});
                  if(tipologia.intValue() != 2 && tipologia.intValue() != 3 && pubblicatoEsito.intValue() > 0){
                    url+= ConfigManager.getValore("invioScp.urlEsitiPortaleAppalti");
                  }else{
                    url+= ConfigManager.getValore("invioScp.urlBandiPortaleAppalti");
                  }
                }
    		}
    		if(!(tipologia.intValue() == 1)){
              if("1".equals(genere) || "3".equals(genere)){
                url += codgar;
              }else{
                url += ngara;
              }
    		}
		}

		AllegatoEntry documento = new AllegatoEntry();
		if (verificaEsistenzaValore(titolo)) {
			documento.setTitolo(titolo);//obbligatorio
		}
		if (verificaEsistenzaValore(url)){
			documento.setUrl(url);
		}
		documenti.add(documento);

	}

	/**
	 * Metodo valorizzazione Dati Aggiudicatari
	 *
	 * @param aggiudicatari
	 * 			oggetto da valorizzare
	 * @param codgara
	 *            codice gara
	 * @param numeroPubblicazione
	 *            numero pubblicazione gara
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 * @throws GestoreException
	 */
	private void valorizzaAggiudicatari(final List<AggiudicatarioEntry> aggiudicatari,
			final String ngara)
	throws SQLException, ParseException, GestoreException {

		String sqlGare = "SELECT DITTA FROM GARE WHERE NGARA = ?";
		String sqlGare1 = "SELECT AQOPER FROM GARE1 WHERE NGARA = ?";

		String ditta;
		Long aqoper;
		List< ? > listDitgaq = new ArrayList<Object>();
		ArrayList<Long> numordAccQuadro = new ArrayList<Long>();
		List< ? > aggiudicatarie = new ArrayList<Object>();
		ArrayList<String> ditteAgg = new ArrayList<String>();

		ditta = (String) sqlManager.getObject(sqlGare, new Object[] {ngara});
		aqoper = (Long) sqlManager.getObject(sqlGare1, new Object[] {ngara});
		if(aqoper != null && aqoper.intValue() == 2){
		  listDitgaq = sqlManager.getListVector("select dittao,numord from ditgaq where ngara = ? order by numord", new Object[] {ngara});
		  for (int i = 0; i < listDitgaq.size(); i++) {
		    aggiudicatarie = (List< ? >) listDitgaq.get(i);
		    ditteAgg.add(SqlManager.getValueFromVectorParam(aggiudicatarie, 0).stringValue());
		    numordAccQuadro.add(SqlManager.getValueFromVectorParam(aggiudicatarie, 1).longValue());
		  }
		}else{
		  ditteAgg.add(ditta);
		}
		for(int i = 0;i<ditteAgg.size();i++){
		  ditta = ditteAgg.get(i);
		  String selectRT = "select tipimp from impr where codimp = ?";
          Long tipimp = (Long) sqlManager.getObject(selectRT, new Object[] {ditta});
          //se RT
          if(tipimp != null && (tipimp.intValue() == 3 || tipimp.intValue() == 10)){
            List<?> datiRagimp = sqlManager.getListVector("select coddic, impman from ragimp where codime9 = ?", new Object[] {ditta});
            for (int m = 0; m < datiRagimp.size(); m++) {
              List itemRagimp = (List< ? >) datiRagimp.get(m);
              String coddic = SqlManager.getValueFromVectorParam(itemRagimp, 0).stringValue();
              String impman = SqlManager.getValueFromVectorParam(itemRagimp, 1).stringValue();
              AggiudicatarioEntry aggiudicatario = new AggiudicatarioEntry();
              aggiudicatario.setTipoAggiudicatario(new Long(1));
              if (verificaEsistenzaValore(impman) && "1".equals(impman)) {
                aggiudicatario.setRuolo(new Long(1));
              }else{
                aggiudicatario.setRuolo(new Long(2));
              }
              if (verificaEsistenzaValore(coddic)) {
                  ImpresaEntry impresa = new ImpresaEntry();
                  this.valorizzaImpresa(impresa, coddic);
                  aggiudicatario.setImpresa(impresa);
              }
              if (numordAccQuadro != null && numordAccQuadro.size()>0) {
                  aggiudicatario.setIdGruppo(numordAccQuadro.get(i));
              }else{
                aggiudicatario.setIdGruppo(new Long(1));
              }
              aggiudicatari.add(aggiudicatario);
            }
          }else{//se non RT
            AggiudicatarioEntry aggiudicatario = new AggiudicatarioEntry();
            if(tipimp != null && (tipimp.intValue() == 2 || tipimp.intValue() == 11)){
              aggiudicatario.setTipoAggiudicatario(new Long(2));
            }else{
              if(tipimp != null && (tipimp.intValue() == 4)){
                aggiudicatario.setTipoAggiudicatario(new Long(4));
              }else{
                aggiudicatario.setTipoAggiudicatario(new Long(3));
              }
            }
            if (numordAccQuadro != null && numordAccQuadro.size()>0) {
              aggiudicatario.setIdGruppo(numordAccQuadro.get(i));
            }else{
              aggiudicatario.setIdGruppo(new Long(1));
            }
            ImpresaEntry impresa = new ImpresaEntry();
            this.valorizzaImpresa(impresa, ditteAgg.get(i));
            aggiudicatario.setImpresa(impresa);
            aggiudicatari.add(aggiudicatario);
          }
		}
	}

	/**
	 * Metodo valorizzazione Dati Gara
	 *
	 * @param gara
	 * 			oggetto da valorizzare
	 * @param codgara
	 *            codice gara
	 * @param numeroPubblicazione
	 *            numero pubblicazione gara
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 * @throws GestoreException
	 */
	public void valorizzaGara(final PubblicaGaraEntry gara,
			final String codgar, String ngara, final String genere)
	throws SQLException, ParseException, GestoreException {

	  String sqlGare = "select not_gar, nomssl, loclav, prosla from GARE where ngara = ?";
      String sqlTorn = "select destor, numavcp, cenint, altrisog, codcigaq, isadesione, settore, modrea, accqua, codrup, uffdet from TORN where codgar = ?";

      String lottoAggiudicazione = ngara;

      List< ? > datiGare, datiTorn, datiUffint;
      List< ? > itemTorn = new ArrayList<Object>();

      if(ngara == null || "".equals(ngara)){
        ngara = (String)this.sqlManager.getObject("SELECT NGARA FROM GARE WHERE CODGAR1 = ? AND CODGAR1 != NGARA", new Object[] {codgar});
      }

      datiGare = sqlManager.getListVector(sqlGare, new Object[] {ngara});
      datiTorn = sqlManager.getListVector(sqlTorn, new Object[] {codgar});

		List< ? > itemGara = new ArrayList<Object>();

		if (datiTorn.size() > 0) {
			itemGara = (List< ? >) datiGare.get(0);
			itemTorn = (List< ? >) datiTorn.get(0);

			if("2".equals(genere)){
    			if (verificaEsistenzaValore(itemGara.get(0))) {
    				gara.setOggetto(this.truncateString(itemGara.get(0).toString(),1024));//obbligatorio
    			}
			}else{
              if (verificaEsistenzaValore(itemTorn.get(0))) {
                gara.setOggetto(this.truncateString(itemTorn.get(0).toString(),1024));//obbligatorio
              }
			}
			//torn.numavcp
			if (verificaEsistenzaValore(itemTorn.get(1))) {
				gara.setIdAnac(itemTorn.get(1).toString());
			}else{
			  gara.setIdAnac("0");
			}

			String sqlUffint = "select nomein, cfein, iscuc, cfanac from uffint where uffint.codein = ?";
			List< ? > itemUffint = new ArrayList<Object>();
			String cenint = itemTorn.get(2).toString();

  			datiUffint = sqlManager.getListVector(sqlUffint, new Object[] {cenint});
  			itemUffint = (List< ? >) datiUffint.get(0);
  			String iscuc = itemUffint.get(2).toString();
  			String cfanac = itemUffint.get(3).toString();
  			String altrisog = "";
  			if (verificaEsistenzaValore(itemTorn.get(3))) {
  			  altrisog = itemTorn.get(3).toString();
            }

  			if("1".equals(iscuc) && ("2".equals(altrisog) || "3".equals(altrisog)) && verificaEsistenzaValore(cfanac)){
  			  gara.setCodiceFiscaleSA(cfanac);//obbligatorio
  			}else{
    			if (verificaEsistenzaValore(itemUffint.get(1))) {
                gara.setCodiceFiscaleSA(itemUffint.get(1).toString());//obbligatorio
    			}
  			}

  			if(!"1".equals(genere)){
    			if (verificaEsistenzaValore(itemGara.get(1))) {
    				gara.setIndirizzo(this.truncateString(itemGara.get(1).toString(), 100));
    			}
    			if (verificaEsistenzaValore(itemGara.get(2))) {
    				gara.setComune(this.truncateString(itemGara.get(2).toString(),32));
    			}
    			if (verificaEsistenzaValore(itemGara.get(3))) {
    				gara.setProvincia(itemGara.get(3).toString());
    			}
  			}

  			String uffdet = itemTorn.get(10).toString();
  			if (verificaEsistenzaValore(uffdet)) {
  			  gara.setUfficio(this.getTab1("A1092",uffdet));
  			}

  			String isAdesione = itemTorn.get(5).toString();
  			if("1".equals(isAdesione) && (!"2".equals(altrisog) && !"3".equals(altrisog))){
    			if (verificaEsistenzaValore(itemTorn.get(4))) {
                gara.setCigAccQuadro(itemTorn.get(4).toString());
    			}
  			}
  			if("2".equals(altrisog)){
              gara.setSaAgente("1");
              gara.setTipoSA(new Long(6));
              String accqua = itemTorn.get(8).toString();
              if("1".equals(accqua)){
                gara.setTipoProcedura(new Long(1));
              }else{
                gara.setTipoProcedura(new Long(3));
              }
              gara.setCentraleCommittenza("2");

              List< ? > datiUffintAltrisog = new ArrayList();
              if("2".equals(genere)){
                datiUffintAltrisog = this.sqlManager.getVector("select nomein, cfein from uffint, garaltsog, gare where codein=cenint and garaltsog.ngara=gare.ngara and gare.codgar1=?", new Object[]{codgar});
              }else{
                List listaCenint = this.sqlManager.getListVector("select distinct(cenint) from garaltsog, gare where gare.ngara=garaltsog.ngara and codgar1=?", new Object[]{codgar});
                if(listaCenint!=null && listaCenint.size()==1){
                  String cenintTemp = SqlManager.getValueFromVectorParam(listaCenint.get(0), 0).getStringValue();
                  datiUffintAltrisog = this.sqlManager.getVector("select nomein, cfein from uffint where codein=?", new Object[]{cenintTemp});
                }
              }
              if(datiUffintAltrisog != null && datiUffintAltrisog.size()>0){
                String nomein =  (String) SqlManager.getValueFromVectorParam(datiUffintAltrisog, 0).getValue();
                String cfein = (String) SqlManager.getValueFromVectorParam(datiUffintAltrisog, 1).getValue();
                if (verificaEsistenzaValore(nomein)) {
                  gara.setNomeSA(nomein);
                }
                if (verificaEsistenzaValore(cfein)) {
                  gara.setCfAgente(cfein);
                }
              }
  			}else{//fine altrisog = 2
			  gara.setSaAgente("2");
  			}

			if (verificaEsistenzaValore("")) {
			  //gara.setAltreSA("");
			}

			Number valmax = (Number)this.sqlManager.getObject("SELECT VALMAX FROM v_gare_importi WHERE codgar = ? order by VALMAX desc", new Object[] {codgar});
			if (verificaEsistenzaValore(valmax)) {
				gara.setImportoGara(valmax.doubleValue());//obbligatorio
			}
			String settore = itemTorn.get(6).toString();
			if("S".equals(settore)){
			  gara.setSettore("S");
			}else{
			  gara.setSettore("O");
			}
			if (verificaEsistenzaValore(itemTorn.get(7))) {
				gara.setRealizzazione(new Long(itemTorn.get(7).toString()));
			}

			Date primaPubb = (Date)this.sqlManager.getObject("SELECT MIN(DATPUB) FROM GARATTISCP WHERE CODGAR = ?", new Object[] {codgar});
			if (!verificaEsistenzaValore(primaPubb)) {
			  primaPubb = new Date(System.currentTimeMillis());
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = formatter.format(primaPubb);
            if (verificaEsistenzaValore(currentDate)) {
              gara.setPrimaPubblicazioneSCP(currentDate);
            }

			/*
			if (verificaEsistenzaValore(itemGara.get(30))) {
				gara.setDurataAccordoQuadro(new Long(itemGara.get(30).toString()));
			}*/

            if (verificaEsistenzaValore(itemTorn.get(9))) {
                DatiGeneraliTecnicoEntry rup = new DatiGeneraliTecnicoEntry();
                this.valorizzaTecnico(rup, itemTorn.get(9).toString());//OK
                gara.setTecnicoRup(rup);//obbligatorio
            }

            String uuid = (String)this.sqlManager.getObject("SELECT UUID FROM GARUUID WHERE CODGAR = ? AND TIPRIC = 'SCP'", new Object[] {codgar});
            if (verificaEsistenzaValore(uuid)) {
              gara.setIdRicevuto(new Long(uuid));
            }

			this.valorizzaLotti(gara.getLotti(), codgar,lottoAggiudicazione, genere);//obbligatorio
		}
	}

	/**
	 * Metodo valorizzazione Pubbligazione bando gara
	 *
	 * @param pubblicazioneBando
	 * 			oggetto da valorizzare
	 * @param codgara
	 *            codice gara
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 */
	/*
	private void valorizzaPubblicazioneBando(final PubblicazioneBandoEntry pubblicazioneBando,
			final String codgara)
	throws SQLException, ParseException {
		String sqlRecuperaW9PUBB = "SELECT DATA_GUCE, DATA_GURI, DATA_ALBO, QUOTIDIANI_NAZ, "
			+ "QUOTIDIANI_REG, PROFILO_COMMITTENTE, SITO_MINISTERO_INF_TRASP, SITO_OSSERVATORIO_CP, DATA_BORE, PERIODICI "
			+ "FROM W9PUBB WHERE CODGARA = ? AND CODLOTT = 1 AND NUM_APPA = 1 AND NUM_PUBB = 1";

		List< ? > listaBando;
		List< ? > itemBando = new ArrayList<Object>();

		listaBando = sqlManager.getListVector(sqlRecuperaW9PUBB, new Object[] {codgara });
		if (listaBando.size() > 0) {
			itemBando = (List< ? >) listaBando.get(0);

			if (verificaEsistenzaValore(itemBando.get(0))) {
				pubblicazioneBando.setDataGuce(itemBando.get(0).toString());
			}
			if (verificaEsistenzaValore(itemBando.get(1))) {
				pubblicazioneBando.setDataGuri(itemBando.get(1).toString());
			}
			if (verificaEsistenzaValore(itemBando.get(2))) {
				pubblicazioneBando.setDataAlbo(itemBando.get(2).toString());
			}
			if (verificaEsistenzaValore(itemBando.get(3))) {
				pubblicazioneBando.setQuotidianiNazionali(new Long(itemBando.get(3).toString()));
			}
			if (verificaEsistenzaValore(itemBando.get(4))) {
				pubblicazioneBando.setQuotidianiLocali(new Long(itemBando.get(4).toString()));
			}
			if (verificaEsistenzaValore(itemBando.get(5))) {
				pubblicazioneBando.setProfiloCommittente(itemBando.get(5).toString());
			}
			if (verificaEsistenzaValore(itemBando.get(6))) {
				pubblicazioneBando.setProfiloInfTrasp(itemBando.get(6).toString());
			}
			if (verificaEsistenzaValore(itemBando.get(7))) {
				pubblicazioneBando.setProfiloOsservatorio(itemBando.get(7).toString());
			}
			if (verificaEsistenzaValore(itemBando.get(8))) {
				pubblicazioneBando.setDataBore(itemBando.get(8).toString());
			}
			if (verificaEsistenzaValore(itemBando.get(9))) {
				pubblicazioneBando.setPeriodici(new Long(itemBando.get(9).toString()));
			}
		}
	}
	*/
	/**
	 * Metodo valorizzazione Dati Lotto
	 *
	 * @param lotti
	 * 			oggetto da valorizzare
	 * @param codgara
	 *            codice gara
	 * @param numeroPubblicazione
	 *            numero pubblicazione gara
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 * @throws GestoreException
	 */
	private void valorizzaLotti(final List<PubblicaLottoEntry> lotti,
			final String codgar,String ngara, String genere)
	throws SQLException, ParseException, GestoreException {

		String sqlGARE = "SELECT NGARA, NOT_GAR, CODIGA, IMPSIC, TIPGARG, CRITLICG, LOCINT, CODCIG, CUPPRG FROM GARE WHERE CODGAR1 = ? AND GARE.CODGAR1 != GARE.NGARA";

		List< ? > listaLotti;
		List< ? > itemLotto = new ArrayList<Object>();

		if(ngara!= null && !"".equals(ngara)){
          sqlGARE+= " and GARE.NGARA = ?";
          listaLotti = sqlManager.getListVector(sqlGARE, new Object[] { codgar,ngara });
        }else{
          listaLotti = sqlManager.getListVector(sqlGARE, new Object[] { codgar });
        }

		if (listaLotti.size() > 0) {
			for (int i = 0; i < listaLotti.size(); i++) {
				itemLotto = (List< ? >) listaLotti.get(i);
				PubblicaLottoEntry lotto = new PubblicaLottoEntry();
				ngara = itemLotto.get(0).toString();

				String not_gar = itemLotto.get(1).toString();;
				if (verificaEsistenzaValore(not_gar)) {
				  lotto.setOggetto(this.truncateString(not_gar,1024));//obbligatorio
				}

				List importi = sqlManager.getVector("select valmax from V_GARE_IMPORTI where ngara = ?", new Object[]{ngara});
	            Number valmax = (Number) SqlManager.getValueFromVectorParam(importi, 0).getValue();
				if (verificaEsistenzaValore(valmax)) {
				    lotto.setImportoLotto(valmax.doubleValue());//obbligatorio
				}

				if(!"2".equals(genere)){
    				boolean codigaTuttiNumeri = true;
    				for(int indexCodiga = 0; indexCodiga < listaLotti.size(); indexCodiga ++){
    				  String codiga = (String) SqlManager.getValueFromVectorParam(listaLotti.get(i), 2).getValue();
    		          if(!UtilityNumeri.isAValidNumber(codiga, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE)){
    		            codigaTuttiNumeri=false;
    		            break;
    		          }
    				}
    				if(codigaTuttiNumeri){
    				  String codiga = itemLotto.get(2).toString();;
    				  lotto.setNumeroLotto(new Long(codiga));//obbligatorio
    				}else{
    				  lotto.setNumeroLotto(new Long(i+1));//obbligatorio
    				}
				}else{
				  lotto.setNumeroLotto(new Long(1));//obbligatorio
				}

				String cpv = (String)this.sqlManager.getObject("SELECT CODCPV FROM GARCPV WHERE NGARA = ? AND TIPCPV = 1", new Object[] {ngara});
				if (verificaEsistenzaValore(cpv)) {
					lotto.setCpv(cpv);
				}

				Long idTipoProcedura = SqlManager.getValueFromVectorParam(itemLotto, 4).longValue();
				if(verificaEsistenzaValore(idTipoProcedura)){

    				String procedura= inviaVigilanzaManager.getFromTab2("A1z11", idTipoProcedura,false);
    				if (verificaEsistenzaValore(procedura)) {
    					lotto.setIdSceltaContraente50(new Long(procedura));
    				}

				}

				List datiTORN = sqlManager.getVector("select tipgen, codnuts from TORN where codgar = ?", new Object[]{codgar});
                Long tipgen = SqlManager.getValueFromVectorParam(datiTORN, 0).longValue();

				//categoria prevalente
                String param = ngara;
                if("3".equals(genere)){
                  param = codgar;
                }
                Vector<JdbcParametro> datiCategoriaPrevalente = sqlManager.getVector("select cg.catiga,cs.tiplavg,cg.numcla  " +
                    " from catg cg,cais cs where cg.catiga = cs.caisim and cg.ncatg = 1 " +
                    "  and cg.ngara = ?", new Object[]{param});
                String codiceCategoriaPrevalente = null;
                Long numcla = null;
                Long tiplavg = null;

                //valorizzo il codice categoria se la gara non è per lavori
                if(tipgen.intValue()==2){
                  codiceCategoriaPrevalente="FB";
                }else {
                  if(tipgen.intValue()==3){
                  codiceCategoriaPrevalente="FS";
                  }
                }
                //Se gara per lavori ed esiste occorrenza in catg
                if(datiCategoriaPrevalente!=null && datiCategoriaPrevalente.size()>0 && codiceCategoriaPrevalente == null){
                    tiplavg = datiCategoriaPrevalente.get(1).longValue();
                  codiceCategoriaPrevalente = datiCategoriaPrevalente.get(0).getStringValue();
                  if(tiplavg== null )tiplavg= new Long(0);
                  //Se tiplvag non è 1 setto il codice = AA e classe = I
                  if(tiplavg==null || tiplavg.intValue() != 1){
                    codiceCategoriaPrevalente="AA";
                    String classe = this.getClasseFromNumcla(new Long(0));
                    lotto.setClasse(classe);
                  }else{
                    //se tiplavg = 1 e la categoria è OG o OS di massimo 5 caratteri tengo il valore preso dalla query, altrimenti setto il codice = AA
                    if (codiceCategoriaPrevalente == null || codiceCategoriaPrevalente.length()>5 ||
                        !(codiceCategoriaPrevalente.startsWith("OG") || codiceCategoriaPrevalente.startsWith("OS"))){
                        codiceCategoriaPrevalente="AA";
                        String classe = this.getClasseFromNumcla(new Long(0));
                        lotto.setClasse(classe);
                    }else{
                      //se tiplavg = 1 imposto la classe in base al numero classifica
                      numcla = datiCategoriaPrevalente.get(2).longValue();
                      if (verificaEsistenzaValore(numcla)) {
                        String classe = this.getClasseFromNumcla(numcla);
                        lotto.setClasse(classe);
                      }
                    }
                  }
                }else{
                  //se la gara è per lavori ma non c'è l'occorrenza in catg (es. profilo anticor), imposto i dati obbligatori codice categoria = AA e classe = I
                  if(codiceCategoriaPrevalente == null){
                    codiceCategoriaPrevalente="AA";
                    String classe = this.getClasseFromNumcla(new Long(0));
                    lotto.setClasse(classe);
                  }
                }
                lotto.setCategoria(codiceCategoriaPrevalente);

                if (verificaEsistenzaValore(tipgen)) {

                  if(tipgen.intValue() == 1){
                    lotto.setTipoAppalto("L");//obbligatorio
                  }
                  if(tipgen.intValue() == 2){
                    lotto.setTipoAppalto("F");//obbligatorio
                  }
                  if(tipgen.intValue() == 3){
                    lotto.setTipoAppalto("S");//obbligatorio
                  }
				}

                List gare1Item = this.sqlManager.getVector("SELECT COSTOFISSO,CODCUI FROM GARE1 WHERE NGARA = ?", new Object[] {ngara});
                String costofisso = (String) SqlManager.getValueFromVectorParam(gare1Item, 0).getValue();
                Long critlicg = SqlManager.getValueFromVectorParam(itemLotto, 5).longValue();
				if (verificaEsistenzaValore(critlicg) && critlicg.intValue() == 2 && ("1".equals(costofisso))){
					lotto.setCriterioAggiudicazione(new Long(5));
				}else if(verificaEsistenzaValore(critlicg) && (critlicg.intValue() == 1 || critlicg.intValue() == 3)){
				    lotto.setCriterioAggiudicazione(new Long(4));
				}else if(verificaEsistenzaValore(critlicg) && critlicg.intValue() == 2 && ("".equals(costofisso) || !"1".equals(costofisso))){
				    lotto.setCriterioAggiudicazione(new Long(3));
				}

				String locint = itemLotto.get(6).toString();
				if("3".equals(genere)){
				  locint = (String)this.sqlManager.getObject("SELECT locint FROM GARE WHERE CODGAR1 = ? AND NGARA = CODGAR1", new Object[] {codgar});
				}
				if (verificaEsistenzaValore(locint)) {
                  lotto.setLuogoIstat(locint);
                }

				String codnuts = SqlManager.getValueFromVectorParam(datiTORN, 1).getStringValue();
				if (verificaEsistenzaValore(codnuts)) {
					lotto.setLuogoNuts(codnuts);
				}
				String codcig = itemLotto.get(7).toString();
				if (verificaEsistenzaValore(codcig)) {
					lotto.setCig(codcig);//obbligatorio
				}
				String cupprg = itemLotto.get(8).toString();
				if (verificaEsistenzaValore(cupprg)){
					lotto.setCupEsente("2");
					lotto.setCup(cupprg);
				}else{
				  lotto.setCupEsente("1");
				}

				String codcui = (String) SqlManager.getValueFromVectorParam(gare1Item, 1).getValue();
				if (verificaEsistenzaValore(codcig)) {
				  lotto.setCui(codcui);
				}

				if(tipgen!= null && tipgen.intValue()==1){
				  this.valorizzaCategorie(lotto.getCategorie(), codgar, ngara, genere);
				}
				this.valorizzaCpvSecondari(lotto.getCpvSecondari(), ngara);
				lotti.add(lotto);

			}
		}
	}

	/**
	 * Metodo valorizzazione Categorie lotto
	 *
	 * @param categorie
	 * 			oggetto da valorizzare
	 * @param codgara
	 *            codice della gara
	 * @param codlotto
	 *            codice del lotto
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 */
	private void valorizzaCategorie(final List<CategoriaLottoEntry> categorie,
			final String codgar, final String ngara, String genere)
	throws SQLException, ParseException {
	  String param;
	  if("3".equals(genere)){
	    param = codgar;
	  }else{
	    param = ngara;
	  }
      String selectCategorie="select o.catoff, o.numclu, o.quaobb, o.acontec from opes o, cais c where c.tiplavg = 1 and o.ngara3= ? and o.catoff=c.caisim";
      List listaCategorie = sqlManager.getListVector(selectCategorie, new Object[]{param});
      for(int i=0;i<listaCategorie.size();i++){
        CategoriaLottoEntry categoria = new CategoriaLottoEntry();
        String codCategoria = (String)SqlManager.getValueFromVectorParam(listaCategorie.get(i),0).getValue();
        Long numcla = (Long)SqlManager.getValueFromVectorParam(listaCategorie.get(i),1).getValue();
        String quaobb = (String)SqlManager.getValueFromVectorParam(listaCategorie.get(i),2).getValue();
        if(codCategoria!=null && !"".equals(codCategoria)){
          if (codCategoria.length()>5 || !(codCategoria.startsWith("OG") || codCategoria.startsWith("OS"))){
            codCategoria="AA";
          }
          categoria.setCategoria(codCategoria);
          String classe = getClasseFromNumcla(numcla);
          if (verificaEsistenzaValore(classe)) {
            categoria.setClasse(classe);
          }
          if(verificaEsistenzaValore(quaobb) && "1".equals(quaobb)){
            categoria.setScorporabile("1");
          }else{
            categoria.setScorporabile("2");
          }
          categoria.setSubappaltabile("1");
        }
        categorie.add(categoria);
      }
	}

	/**
	 * Metodo valorizzazione Cpv Secondari lotto
	 *
	 * @param cpvSecondari
	 * 			oggetto da valorizzare
	 * @param codgara
	 *            codice della gara
	 * @param codlotto
	 *            codice del lotto
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 */
	private void valorizzaCpvSecondari(final List<CpvLottoEntry> cpvSecondari,
			final String ngara)
	throws SQLException, ParseException {
		String sqlRecuperaW9CPV = "SELECT CODCPV FROM GARCPV WHERE TIPCPV = 2 AND NGARA = ?";

		List< ? > listaCpv;
		List< ? > itemCpv = new ArrayList<Object>();

		listaCpv = sqlManager.getListVector(sqlRecuperaW9CPV, new Object[] {ngara});

		if (listaCpv.size() > 0) {
			for (int i = 0; i < listaCpv.size(); i++) {
				itemCpv = (List< ? >) listaCpv.get(i);
				CpvLottoEntry cpv = new CpvLottoEntry();
				if (verificaEsistenzaValore(itemCpv.get(0))) {
					cpv.setCpv(itemCpv.get(0).toString());
				}
				cpvSecondari.add(cpv);
			}
		}
	}

	/**
	 * Metodo valorizzazione Modalità acquisizione forniture.
	 *
	 * @param modalitaAcquisizioneForniture
	 * 			oggetto da valorizzare
	 * @param codgara
	 *            codice della gara
	 * @param codlotto
	 *            codice del lotto
	 * @throws SQLException
	 *             SQLException
	 * @throws ParseException
	 *             ParseException
	 */
	private void valorizzaModalitaAcquisizioneForniture(final List<AppaFornEntry> modalitaAcquisizioneForniture,
			final Long codgara, final Long codlotto)
	throws SQLException, ParseException {
		String sqlRecuperaW9APPAFORN = "SELECT NUM_APPAF, ID_APPALTO "
			+ "FROM W9APPAFORN WHERE CODGARA = ? AND CODLOTT = ?";

		List< ? > listaModalita;
		List< ? > itemModalita = new ArrayList<Object>();

		listaModalita = sqlManager.getListVector(sqlRecuperaW9APPAFORN, new Object[] {
				codgara, codlotto });

		if (listaModalita.size() > 0) {
			for (int i = 0; i < listaModalita.size(); i++) {
				itemModalita = (List< ? >) listaModalita.get(i);
				AppaFornEntry modalita = new AppaFornEntry();
				if (verificaEsistenzaValore(itemModalita.get(1))) {
					modalita.setModalitaAcquisizione(new Long(itemModalita.get(1).toString()));
				}
				modalitaAcquisizioneForniture.add(modalita);
			}
		}
	}

	/**
	 * Utility per il controllo dei valori in arrivo.
	 *
	 * @param obj Object
	 * @return Ritorna true se obj e' diversa da null, false altrimenti
	 */
	public boolean verificaEsistenzaValore(final Object obj) {
		boolean esistenza;
		if (obj != null && !obj.toString().trim().equals("")) {
			esistenza = true;
		} else {
			esistenza = false;
		}
		return esistenza;
	}

	public String getSiglaTabG_z23 (final String tab2tip) throws SQLException {
	  String sigla = (String) sqlManager.getObject("select tab2d1 from tab2 where  tab2cod = 'G_z23' AND TAB2TIP = ?", new Object[] {tab2tip});
	  if(sigla != null){
	    return sigla;
	  }
	  return "";
	}

	public String getTab1 (final String tab1cod, final String tab1tip) throws SQLException {
	   String tab = (String) sqlManager.getObject("select tab1desc from tab1 where  tab1cod = ? AND tab1tip = ?", new Object[] {tab1cod,tab1tip});
	   if(tab != null){
	     return tab;
	   }
	   return "";
	}

    public String getClasseFromNumcla (final Long numcla){
      String classe = null;
      if (verificaEsistenzaValore(numcla)) {
        switch(numcla.intValue()) {
        case 0:
        case 1:
          classe = "I";
        break;
        case 2:
          classe = "II";
        break;
        case 3:
          classe = "III";
        break;
        case 4:
          classe = "IIIB";
        break;
        case 5:
          classe = "IV";
        break;
        case 6:
          classe = "IVB";
        break;
        case 7:
          classe = "V";
        break;
        case 8:
          classe = "VI";
        break;
        case 9:
          classe = "VII";
        break;
        case 10:
          classe = "VIII";
        break;
        default:
          classe = "I";
        }
      }
      return classe;
    }

    private String truncateString(String word, int n){
      if(word.length() > n){
        return word.substring(0,n);
      }else{
        return word;
      }
    }

}


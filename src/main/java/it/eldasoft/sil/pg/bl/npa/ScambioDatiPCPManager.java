package it.eldasoft.sil.pg.bl.npa;

import it.eldasoft.gene.bl.*;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.ValidazioneManipolazioneEFormManager;
import it.eldasoft.sil.pg.bl.utils.EstrazioneContenutoFileFirmatoMarcato;
import it.eldasoft.sil.pg.web.struts.npa.ScambioDatiPCPAction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.keycloakclient.ITokenAuth;
import it.maggioli.keycloakclient.auth.EAuthToken;
import it.maggioli.keycloakclient.invoker.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * @author manuel.bridda
 *
 */
public class ScambioDatiPCPManager {
  
  public static final String CLIENT                     = "client";
  private static final String BASE_URL                  = "ws.inviopcp.url";
  private static final String TENANT_ID                 = "ws.inviopcp.tenantid";
  private static final String CODICE_COMPONENTE         = "ws.inviopcp.codiceComponente";
  private static final String CODICE_PIATTAFORMA        = "ws.inviopcp.codicePiattaforma";
  
  private static final String SERVICE_TYPE              = "serviceType";
  private static final String SERVICE_TYPE_HEALTH       = "HEALTH";
  private static final String SERVICE_TYPE_PROTECTED    = "PROTECTED";
  private static final String SERVICE_TYPE_PUBLIC       = "PUBLIC";
  private static final String SERVICE_URL               = "serviceURL";
  
  //headers
  public static final String HEADER_SA_CODICE_AUSA      = "X-SAcodiceAUSA";
  public static final String HEADER_TENANT_ID           = "X-tenantId";
  public static final String HEADER_TIPOLOGICA_SCHEDA   = "X-tipologicaScheda";
  public static final String HEADER_USER_CODICE_FISCALE = "X-userCodiceFiscale";
  public static final String HEADER_USER_IDP_TYPE       = "X-userIdpType";
  public static final String HEADER_USER_LOA            = "X-userLoa";
  public static final String HEADER_USER_ROLE           = "X-userRole";
  public static final String HEADER_VERSIONE_TRACCIATO  = "X-versioneTracciato";
  public static final String HEADER_MODALITA_INVIO      = "X-modalitaInvio";
  public static final String HEADER_CODICE_PIATTAFORMA  = "X-regCodicePiattaforma";
  public static final String HEADER_CODICE_COMPONENTE   = "X-regCodiceComponente";
  public static final String HEADER_SACODICE_FISCALE    = "X-SACodiceFiscale";
  
  //query parameters
  public static final String ESITO                      = "esito";
  public static final String ID_APPALTO                 = "idAppalto";
  public static final String ID_AVVISO                  = "idAvviso";
  public static final String ID_NUOVO_AVVISO            = "idNuovoAvviso";
  public static final String ID_SCHEDA                  = "idScheda";
  public static final String ID_PIANIFICAZIONE          = "idPianificazione";
  public static final String TIPO_OPERAZIONE            = "tipoOperazione";
  public static final String TIPO_RICERCA               = "tipoRicerca";
  public static final String ID_CONTRATTO               = "idContratto";
  public static final String CIG                        = "cig";
  public static final String PAGE                       = "page";
  public static final String PER_PAGE                   = "perPage";
  public static final String AP_VERIF                   = "AP_VERIF";
  public static final String AP_CONF                    = "AP_CONF";
  public static final String AV_PUBB                    = "AV_PUBB";
  public static final String SC_VERIF                   = "SC_VERIF";
  public static final String SC_CONF                    = "SC_CONF";
  public static final String PI_VERIF                   = "PI_VERIF";
  public static final String PI_CONF                    = "PI_CONF";
  public static final String AV_RETT                    = "AV_RETT";
  
  //request
  private static final String BODY                      = "body";
  private static final String BODY_ANAC_FORM            = "anacForm";
  private static final String BODY_EFORM                = "eform";
  private static final String BODY_ESPD                 = "espd";
  public static final String BODY_RUOLO                 = "ruolo";
  private static final String BODY_CF                   = "codiceFiscale";
  private static final String BODY_DT_INIZIO            = "dataInizio";
  private static final String CODAUSA                   = "codiceAusa";
  private static final String CHIAVEACCESSOFVOE         = "chiaveAccesso";
  public static final String ALTRI_DATI                 = "AltriDati";
  public static final String DATI_NOMINATIVO            = "DatiNominativo";
  public static final String LINGUA_TEDESCA             = "LinguaTedesca";
  public static final String CODICE_CATASTO_STATO       = "CodiceCatastoStato";
  public static final String CODICE_CATASTO_COMUNE_ITALIANO = "CodiceCatastoComuneItaliano";
  private static final List<String> CODICI_DATISA       = Arrays.asList("00001","00004","00005","00035","00036","00037","00041","00899");
  
  //response
  private static final String OK                        = "OK";
  private static final String KO                        = "KO";
  private static final String WT                        = "WT";
  private static final String DATA_DETAIL               = "detail";
  private static final String LISTA_ESITI               = "listaEsiti";
  private static final String CODICE                    = "codice";
  private static final String ESITO_STATO               = "esitoStato";
  private static final String STATO                     = "stato";
  private static final String STATO_CONF                = "CONF";
  private static final String STATO_PUBB                = "PUBB";
  private static final String AV_IN_PUBB                = "AV_IN_PUBB";
  private static final String IN_STOP_PUBB              = "IN_STOP_PUBB";
  private static final String STATO_STOP_PUBB           = "STOP_PUBB";
  private static final String STATO_IN_ATT_PUBB         = "IN_ATT_PUBB";
  private static final String RESULT                    = "result";
  public static final String ANAC_ERRORS               = "anacErrors";
  public static final String PG_ERRORS                 = "pgErrors";
  private static final String LOT_IDENTIFIER            = "lotIdentifier";
  private static final String DETTAGLIO                 = "dettaglio";
  private static final String RICH_ACQUISITA            = "Richiesta acquisita";
  private static final String AGGIORNAMENTO_ESEGUITO    = "Aggiornamento eseguito con successo";
  private static final String STATO_AVVISO              = "statoAvviso";
  private static final String AVVISO                    = "avviso";
  private static final String DATI_PUBBLICAZIONE_ITALIA = "datiPubblicazioneIT";
  private static final String DATI_PUBBLICAZIONE_EUROPA = "datiPubblicazioneEU";
  private static final String DATA_INOLTRO_PUBBLICAZIONE	= "dataInoltroPubblicazione";
  private static final String DATA_PUBBLICAZIONE        = "dataPubblicazione";
  private static final String PUBBLICATION_ID           = "publicationId";
  private static final String PUBBLICATION_URL          = "publicationUrl";
  private static final String ID_AVVISO_PVL             = "idAvvisoPVL";
  private static final String PROP_URL_BASE_PVL         = "npa.urlBasePVL";
  
  public static final String SESSION                    = "session";
  
  //dati dalla action/jsp
  public static final String ID_FORM                    = "idForm";
  public static final String UUID_EFORM                 = "uuid_eform";
  public static final String BODY_MOTIVO                = "motivo";
  public static final String LOG_EVENTO_REQ             = "logEventoREQ";
  public static final String MODALITA_VERIFICA          = "VERIFICA";
  public static final String MODALITA_INVIO             = "modalitaInvio";
  public static final String CODEIN                     = "codein";
  public static final String MODALITA_RECUPERO_AUSA     = "RECUPERO_AUSA";
  public static final String ABILITAZIONE_MRP           = "abilitazioneMrp";
  public static final String AUTOUPDATE                 = "AUTOUPDATE";
  
  //dati db
  public static final String NOME_SCHEDA                = "nomeScheda";
  public static final String CODICE_AUSA                = "codiceAUSA";
  public static final String STATO_NPA                  = "statoNPA";
  public static final String CODGAR                     = "codgar";
  public static final String VER_NPA                    = "verNpa";
  public static final String CFRP                       = "cfRP";
  public static final String CFDRP                      = "cfDRP";
  public static final String SACF                       = "codiceFiscaleSA";
  //public static final String GENERE                     = "genere";
  public static final String ID_APPALTO_TORN            = "idAppaltoTorn";
  public static final String ID_SCHEDA_NPA              = "idSchedaNpa";
  public static final String ID_PIANI_TORN              = "idPianiTorn";
  public static final String ISCIG                      = "isCig";
  public static final String DATACONF                   = "dataConf";
  public static final String ID_SCHEDA_NPA_RET          = "idSchedaNpaRet";
  public static final String ID_G1NPASCHEDA             = "id_g1npascheda";
  public static final String IS_CODAUSA_FITT            = "isCodausaFit";
  public static final String SKIP_SCRITTURA_DB          = "skipScritturaInDb";
  public static final String CODICEAPPALTO              = "codiceAppalto";
  public static final String CODIMP                     = "codimp";
  public static final String IDFVOE                     = "idfvoe";
  
  //stati scheda
  public static final Long IN_COMPILAZIONE              = 1L;
  public static final Long VERIFICA_CREAZIONE           = 2L;
  public static final Long IN_LAVORAZIONE               = 10L;
  public static final Long DELEGA_SOGGETTO              = 11L;
  public static final Long IN_CORSO_DI_VERIFICA         = 12L;
  public static final Long VERICATO_NEGATIVAMENTE       = 13L;
  public static final Long VERICATO_POSITIVAMENTE       = 14L;
  public static final Long IN_CONFERMA                  = 15L;
  public static final Long CANCELLATO                   = 20L;
  public static final Long CONFERMATO                   = 30L;
  public static final Long ACQUISIZIONE_CIG             = 31L;
  public static final Long RECUPERO_IDAVVISO            = 32L;
  public static final Long RICHIESTA_PUBBLICAZIONE      = 33L;
  public static final Long NON_PUBBLICATO               = 34L;
  public static final Long IN_ATTESA_PUBBLICAZIONE      = 40L;
  public static final Long RICH_STOP_PUBBLICAZIONE      = 41L;
  public static final Long STOP_PUBBLICAZIONE           = 50L;
  public static final Long PUBBLICATO                   = 60L;

  /** Logger */
  static Logger logger = Logger.getLogger(ScambioDatiPCPManager.class);


  private SqlManager          sqlManager;
  private NpaFunzioniManager    npaFunzioniManager;
  private FileAllegatoManager fileAllegatoManager;
  private PgManager pgManager;
  private PgManagerEst1 pgManagerEst1;
  private TabellatiManager tabellatiManager;
  private GeneManager geneManager;
  private GenChiaviManager genChiaviManager;
  private ValidazioneManipolazioneEFormManager validazioneManipolazioneEFormManager;
  private GestisciInvioFEUManager gestisciInvioFEUManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setNpaFunzioniManager(NpaFunzioniManager npaFunzioniManager) {
    this.npaFunzioniManager = npaFunzioniManager;
  }
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }
  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }
  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }
  public void setValidazioneManipolazioneEFormManager(ValidazioneManipolazioneEFormManager validazioneManipolazioneEFormManager) {this.validazioneManipolazioneEFormManager = validazioneManipolazioneEFormManager;}
  public void setGestisciInvioFEUManager(GestisciInvioFEUManager gestisciInvioFEUManager) {this.gestisciInvioFEUManager = gestisciInvioFEUManager;}

  public boolean checkControlliInviante() { 
    return true;
  }
  
  private Map<String,Object> getParametersFromDB(Map<String,Object> parameters) throws SQLException, GestoreException {
    Vector values = sqlManager.getVector("select G1CF_NPA.NOME_SCHEDA, UFFINT.CODAUSA, G1NPASCHEDA.IDSCHEDANPA, G1NPASCHEDA.STATONPA, TORN.CODGAR, G1NPASCHEDA.IDAVVISONPA, "
        + "TORN.VERNPA, T1.CFTEC, T2.CFTEC, V_GARE_GENERE.GENERE, TORN.IDAPPALTONPA, TORN.IDPIANINPA, G1CF_NPA.ISCIG, G1NPASCHEDA.DATACONF, G1NPASCHEDA.IDSCHEDANPARET, G1NPASCHEDA.ID, "
        + "TORN.ALTRISOG, UFFINT.CODAUSAANAC, UFFINT.CFANAC, G1NPASCHEDA.CODICEAPPALTO, UFFINT.CFEIN "
        + "from G1CF_NPA "
        + "join G1NPAFORM on (G1CF_NPA.ID=G1NPAFORM.IDCFNPA) "
        + "join G1NPASCHEDA on (G1NPASCHEDA.IDFORM=G1NPAFORM.ID) "
        + "JOIN TORN on (G1NPAFORM.CODGAR=TORN.CODGAR) "
        + "JOIN V_GARE_GENERE on (V_GARE_GENERE.CODGAR=TORN.CODGAR) "
        + "JOIN UFFINT on (TORN.CENINT=UFFINT.CODEIN) "
        + "LEFT JOIN TECNI T1 ON (TORN.CODRUP=T1.CODTEC) "
        + "LEFT JOIN TECNI T2 ON (TORN.CODDRP=T2.CODTEC) "
        + "WHERE G1NPAFORM.ID=?",new Object[] {parameters.get(ID_FORM)});
    
    parameters.put(NOME_SCHEDA,SqlManager.getValueFromVectorParam(values, 0).stringValue());
    String codausa=SqlManager.getValueFromVectorParam(values, 1).stringValue();
    parameters.put(ID_SCHEDA_NPA,SqlManager.getValueFromVectorParam(values, 2).stringValue());
    parameters.put(STATO_NPA,SqlManager.getValueFromVectorParam(values, 3).longValue());
    parameters.put(CODGAR,SqlManager.getValueFromVectorParam(values, 4).stringValue());
    parameters.put(ID_AVVISO,SqlManager.getValueFromVectorParam(values,5).stringValue());
    parameters.put(VER_NPA,SqlManager.getValueFromVectorParam(values,6).stringValue());
    parameters.put(CFRP,SqlManager.getValueFromVectorParam(values,7).stringValue());
    String drp = SqlManager.getValueFromVectorParam(values,8).stringValue();
    if(drp==null) drp="";
    parameters.put(CFDRP,drp);
    //parameters.put(GENERE,SqlManager.getValueFromVectorParam(values,9).longValue());
    parameters.put(ID_APPALTO_TORN,SqlManager.getValueFromVectorParam(values,10).stringValue());
    parameters.put(ID_PIANI_TORN,SqlManager.getValueFromVectorParam(values,11).stringValue());
    parameters.put(ISCIG,SqlManager.getValueFromVectorParam(values,12).stringValue());
    parameters.put(DATACONF,SqlManager.getValueFromVectorParam(values,13).dataValue());
    parameters.put(ID_SCHEDA_NPA_RET,SqlManager.getValueFromVectorParam(values,14).longValue());
    parameters.put(ID_G1NPASCHEDA,SqlManager.getValueFromVectorParam(values,15).longValue());
    parameters.put(CODICEAPPALTO, SqlManager.getValueFromVectorParam(values,19).stringValue());
    String cfSA = SqlManager.getValueFromVectorParam(values,20).stringValue();
    
    //prelevo i dati anac fittizi in caso di altrisog in (2,3,4)
    Long altrisog=SqlManager.getValueFromVectorParam(values,16).longValue();
    if (altrisog != null && ((SqlManager.getValueFromVectorParam(values, 18).stringValue()!=null && (Long.valueOf(2).equals(altrisog) || Long.valueOf(3).equals(altrisog))) || Long.valueOf(4).equals(altrisog))) {
       codausa = SqlManager.getValueFromVectorParam(values, 17).stringValue();
       cfSA = SqlManager.getValueFromVectorParam(values, 18).stringValue();
      parameters.put(IS_CODAUSA_FITT,true);
    }
    parameters.put(CODICE_AUSA,codausa);
    parameters.put(SACF,cfSA);
    
    return parameters;
  }

  private Map<String,Object> getFVOEParametersFromDB(Map<String,Object> parameters) throws SQLException, GestoreException {
    Vector values = sqlManager.getVector("select UFFINT.CODAUSA, TORN.VERNPA, T1.CFTEC, T2.CFTEC, TORN.IDAPPALTONPA, UFFINT.CFEIN, TORN.ALTRISOG, UFFINT.CODAUSAANAC,UFFINT.CFANAC"
            + " from TORN JOIN UFFINT on (TORN.CENINT=UFFINT.CODEIN) "
            + " LEFT JOIN TECNI T1 ON (TORN.CODRUP=T1.CODTEC) "
            + " LEFT JOIN TECNI T2 ON (TORN.CODDRP=T2.CODTEC) "
            + " WHERE TORN.CODGAR=?",new Object[] {parameters.get(CODGAR)});

    String codausa=SqlManager.getValueFromVectorParam(values, 0).stringValue();
    parameters.put(VER_NPA,SqlManager.getValueFromVectorParam(values,1).stringValue());
    parameters.put(CFRP,SqlManager.getValueFromVectorParam(values,2).stringValue());
    String drp = SqlManager.getValueFromVectorParam(values,3).stringValue();
    if(drp==null) drp="";
    parameters.put(CFDRP,drp);
    parameters.put(ID_APPALTO_TORN,SqlManager.getValueFromVectorParam(values,4).stringValue());
    String cfSA = SqlManager.getValueFromVectorParam(values, 5).stringValue();
    //prelevo i dati anac fittizi in caso di altrisog in (2,3,4)
    Long altrisog=SqlManager.getValueFromVectorParam(values,6).longValue();
    if (altrisog != null && ((SqlManager.getValueFromVectorParam(values, 8).stringValue()!=null && (Long.valueOf(2).equals(altrisog) || Long.valueOf(3).equals(altrisog))) || Long.valueOf(4).equals(altrisog))) {
      cfSA = SqlManager.getValueFromVectorParam(values, 8).stringValue();
      codausa = SqlManager.getValueFromVectorParam(values, 7).stringValue();
      parameters.put(IS_CODAUSA_FITT,true);
    }
    parameters.put(SACF,cfSA);
    parameters.put(CODICE_AUSA,codausa);

    return parameters;
  }
  
  private MultivaluedMap <String,Object> getHeaders(Map<String,Object> parameters) throws SQLException, GestoreException{
    
    MultivaluedMap <String,Object> headersMap = new MultivaluedHashMap<String,Object>();
    
    
    if(MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) { //in modalità verifica ho solo bisogno tipologica scheda e modalita invio
      headersMap.putSingle(HEADER_MODALITA_INVIO, MODALITA_VERIFICA);
      headersMap.putSingle(HEADER_TIPOLOGICA_SCHEDA, parameters.get(NOME_SCHEDA));
    } else {
      String tenantId = ConfigManager.getValore(TENANT_ID); // se non siamo in modalità verifica, ho sempre bisogno del tenant id
      if (StringUtils.isBlank(tenantId)) {
        throw new GestoreException(
            "Il parametro 'tenant ID' per l'utilizzo del servizio di invio a PCP non e' definito",
            "scambiodatiPCP.ws.tenantid");
      }
      headersMap.putSingle(HEADER_TENANT_ID, tenantId);
      if(!MODALITA_RECUPERO_AUSA.equals(parameters.get(MODALITA_INVIO))) { //se non stiamo recuperando i centri di costo, ho bisogno di ulteriori headers
        
        String codicePiattaforma = ConfigManager.getValore(CODICE_PIATTAFORMA);
        if (StringUtils.isBlank(codicePiattaforma)) {
          throw new GestoreException(
              "Il parametro 'codice piattaforma' per l'utilizzo del servizio di invio a PCP non e' definito",
              "scambiodatiPCP.ws.codicePiattaforma");
        }
        headersMap.putSingle(HEADER_CODICE_PIATTAFORMA, codicePiattaforma);
        
        String codiceComponente = ConfigManager.getValore(CODICE_COMPONENTE);
        if (StringUtils.isBlank(codiceComponente)) {
          throw new GestoreException(
              "Il parametro 'codice componente' per l'utilizzo del servizio di invio a PCP non e' definito",
              "scambiodatiPCP.ws.codiceComponente");
        }
        headersMap.putSingle(HEADER_CODICE_COMPONENTE, codiceComponente);
        
        if(StringUtils.isBlank((String)parameters.get(CODICE_AUSA))) {
          if(Boolean.TRUE.equals(parameters.get(IS_CODAUSA_FITT))) {
            throw new GestoreException(
                "Il codice AUSA fittizio non è valorizzato per la stazione appaltante corrente",
                "scambiodatiPCP.ws.ausafitt");
          } else {
            throw new GestoreException(
                "Il codice AUSA non è valorizzato per la stazione appaltante corrente",
                "scambiodatiPCP.ws.ausa");
          }
        }
        String provider = "CUSTOM";
        String role = "RP";
        String loa = "LOA_1";
        String cf = "";

        if("true".equals(parameters.get("isTask"))){
          cf=(String)parameters.get("cfUtente");
          loa="LOA_"+parameters.get("loa");
          provider=(String)parameters.get("idp");
        } else {
          HttpSession session = (HttpSession) parameters.get(SESSION);

          ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          cf=profiloUtente.getCodiceFiscale();

          AccountSSO accountSSO = (AccountSSO) session.getAttribute(CostantiGenerali.ID_ATTRIBUTO_SESSIONE_ACCOUNT_SSO);
          if (StringUtils.isNotBlank(cf) && accountSSO != null) {
            if (accountSSO.isProviderStandard())
              provider = accountSSO.getStandardProvider();
//          if(!cf.equalsIgnoreCase(parameters.get(CFRP).toString()) && cf.equalsIgnoreCase(parameters.get(CFDRP).toString()))
//            role = "DRP";
            loa = "LOA_" + accountSSO.getCurrentLoa();
          }
        }
        headersMap.putSingle(HEADER_TIPOLOGICA_SCHEDA, parameters.get(NOME_SCHEDA));
        headersMap.putSingle(HEADER_SA_CODICE_AUSA, parameters.get(CODICE_AUSA));
        headersMap.putSingle(HEADER_USER_CODICE_FISCALE, cf.toUpperCase());
        headersMap.putSingle(HEADER_USER_IDP_TYPE, provider);
        headersMap.putSingle(HEADER_USER_LOA, loa);
        headersMap.putSingle(HEADER_USER_ROLE, role);
        headersMap.putSingle(HEADER_VERSIONE_TRACCIATO, parameters.get(VER_NPA));
        headersMap.putSingle(HEADER_SACODICE_FISCALE, parameters.get(SACF));
        
      }
    }
    //APPALTI-1836
    //dopo aver messo gli headers corretti, ripristino la modalità verifica se la chiamata arriva dal pannello di supporto, per evitare aggiornamenti in db
    if(parameters.get(SKIP_SCRITTURA_DB) != null && (boolean)parameters.get(SKIP_SCRITTURA_DB)){
      parameters.put(MODALITA_INVIO, MODALITA_VERIFICA);
    }
    //Header per integrazione keycloak + kong
    String keycloakBaseUrl = ConfigManager.getValore("keycloak.realms.baseurl");
    String keycloakRealm = ConfigManager.getValore("keycloak.progettoappalti.subpath");
    String keycloakClientId = ConfigManager.getValore("keycloak.progettoappalti.client.id");
    String keycloakClientSecret = ConfigManager.getValore("keycloak.progettoappalti.client.secret");
    
    if(StringUtils.isNotBlank(keycloakBaseUrl) && StringUtils.isNotBlank(keycloakRealm)) {
      ITokenAuth tokenAuth = EAuthToken.newKongTokenBuilder()
              .withClientId(keycloakClientId)
              .withClientSecret(keycloakClientSecret)
              .withUrl(keycloakBaseUrl+keycloakRealm)   //Url base
              .withType(2)   //autenticazione clientId+clientSecret 
          .build();
      try {
        headersMap.putSingle("Authorization", "Bearer "+tokenAuth.getToken());
      } catch (ApiException e) {
        throw new GestoreException(
            "Errore durante la richiesta del token al servizio keycloak",
            "scambiodatiPCP.ws.keycloakErr");
      }
    }
    
    return headersMap;
    
  }
  private Builder getBuilder(Client client, Map<String,String> map, Map<String,String> queryParameters) throws GestoreException{
    
    WebTarget webTarget = getWebTarget(client, map);

    return getBuilder(webTarget,queryParameters);   
  }
  
  private Builder getBuilder(WebTarget webTarget, Map<String,String> queryParameters) {
    
    if(queryParameters!=null) {
      for (Map.Entry<String, String> entry: queryParameters.entrySet()) {
        webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
      }
    } 
    return webTarget.request(MediaType.APPLICATION_JSON_TYPE);
  }
  
  private WebTarget getWebTarget(Client client, Map<String,String> map) throws GestoreException {
    String url = ConfigManager.getValore(BASE_URL);
    if (StringUtils.isBlank(url)) {
      throw new GestoreException(
          "L'indirizzo per la connessione al web service non e' definito",
          "scambiodatiPCP.ws.url");
    }
    if(SERVICE_TYPE_HEALTH.equals(map.get(SERVICE_TYPE)))
      url=url.substring(0, url.lastIndexOf('/'))+"/q";//baseURL = http://<host>[:<port>]/rest/npa-gateway-ms/q
    url+=map.get(SERVICE_URL); //serviceURL= "/protected/comunica-appalto/crea-appalto

   return client.target(url);
  }
  
  
  /** chiamata per verificare lo stato del web service **/
  public JSONObject checkWSHealth(Map<String,Object> parameters) throws Exception, Throwable{
    
    JSONObject resp = new JSONObject();
    resp.put("status", "DOWN");
    
    Client client = (Client) parameters.get(CLIENT);
    
    Map<String,String> map = new HashMap<String,String>();
    map.put(SERVICE_TYPE, SERVICE_TYPE_HEALTH);
    map.put(SERVICE_URL, "/health");
    
    Builder builder = getBuilder(client, map,null).headers(getHeaders(parameters));
    resp = builder.get(JSONObject.class);
      
    return resp;
  }

  public JSONObject updateAbilitazioneMRP(Long idFormScheda, String abilitare){

    JSONObject resp = new JSONObject();
    String inviabile= "true".equals(abilitare)? "1" : "0";

    try {
      sqlManager.update("update G1NPASCHEDA set INVIABILE=? where IDFORM=?", new Object[]{inviabile, idFormScheda});
    } catch(Exception e) {
        resp.put(PG_ERRORS, "Errore nella gestione dell'abilitazione del servizio M-RP per la scheda" + e.getMessage());
        return resp;
    }
    resp.put("status", inviabile);

    return resp;
  }
  
  //############### SERVIZI COMUNI ###############
  
  /** chiamata per verificare la validità dei dati inviati **/
  public JSONObject serviceEsitoOperazione(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    boolean aggiornaDatiDaRettifica = false;
    String idNuovoAvviso = null;
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/servizi-comuni/esito-operazione");
    
    Map<String,String> parametersMap = new HashMap<String,String>();
   
    
    parametersMap.put(TIPO_OPERAZIONE,parameters.get(TIPO_OPERAZIONE).toString());
    if(parameters.get(TIPO_OPERAZIONE).toString().indexOf("PI_")==0)//se sono sulla pianificazione, si cerca per IDPIANIFICAZIONE
      parametersMap.put(ID_PIANIFICAZIONE,parameters.get(ID_PIANI_TORN).toString());
    else
      parametersMap.put(ID_APPALTO,parameters.get(ID_APPALTO_TORN).toString());
    parametersMap.put(TIPO_RICERCA,parameters.get(TIPO_RICERCA).toString());
    if(parameters.get(ESITO)!=null)
      parametersMap.put(ESITO,parameters.get(ESITO).toString());
    
    Builder builder = getBuilder(client,builderMap,parametersMap).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+parametersMap.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.get();
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      JSONArray listaEsiti = resp.getJSONObject("data").getJSONArray(LISTA_ESITI);

      if(listaEsiti!=null && listaEsiti.size()>0) {
        
        switch(parameters.get(TIPO_OPERAZIONE).toString()) {
        
        case AP_VERIF:
        case SC_VERIF:
        case PI_VERIF:
          if(OK.equals(listaEsiti.getJSONObject(0).getJSONObject(ESITO).get(CODICE)))
            updateStatoForm(VERICATO_POSITIVAMENTE, parameters.get(ID_FORM));
          else if(KO.equals(listaEsiti.getJSONObject(0).getJSONObject(ESITO).get(CODICE))){
            updateStatoForm(VERICATO_NEGATIVAMENTE, parameters.get(ID_FORM));
            estraiArrayErrori(resp, listaEsiti.getJSONObject(0));
            }
          break;
          
        case AP_CONF:
        case SC_CONF:
        case PI_CONF:
          if(OK.equals(listaEsiti.getJSONObject(0).getJSONObject(ESITO).get(CODICE))) {
            if(listaEsiti.getJSONObject(0).get(ID_AVVISO) instanceof  String && ((Long) parameters.get(STATO_NPA) >= CONFERMATO)) {
              String idAvviso = listaEsiti.getJSONObject(0).getString(ID_AVVISO);
              updateStatoFormAndSetIdAvviso(RECUPERO_IDAVVISO, parameters.get(ID_FORM),idAvviso);
            }
            else {
              updateStatoForm(CONFERMATO, parameters.get(ID_FORM));
              SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
              
              Timestamp timestamp = new Timestamp(System.currentTimeMillis());
              try {
                  java.util.Date parsedDate = dateFormat.parse(listaEsiti.getJSONObject(0).getString("dataControllo"));
                  timestamp = new Timestamp(parsedDate.getTime());
              } catch (ParseException e) {
                 logger.error("G1NPASCHEDA, setting del campo DATACONF: Errore nella conversione del campo dataControllo ("+ listaEsiti.getJSONObject(0).getString("dataControllo")+"). Sarà utilizzato l'istante attuale." );
              }
              sqlManager.update("update g1npascheda set dataconf=? where idform=?", new Object[] {timestamp,parameters.get(ID_FORM)});
            }
            if("ANN".equals(parameters.get(NOME_SCHEDA))){//le schede ANN che passano in conferma distruggono tutto, come se avessi richiesto la cancellazione dell'appalto
              String codgar=parameters.get(CODGAR).toString();
              sqlManager.update("update g1npascheda set isann='1' where idform in (select id from g1npaform where codgar=? and id <> ?)", new Object[]{codgar,parameters.get(ID_FORM)});
              sbiancaCIG(codgar);
              sbiancaIdAppalto(codgar);
              sbiancaDGUE(codgar,parameters.get(ID_FORM));
            }
          }
          else if(KO.equals(listaEsiti.getJSONObject(0).getJSONObject(ESITO).get(CODICE)))
            estraiArrayErrori(resp, listaEsiti.getJSONObject(0));//porto in cima gli errori e cambio lo stato
          break;
          
        case AV_PUBB:
          if(OK.equals(listaEsiti.getJSONObject(0).getJSONObject(ESITO).get(CODICE))) {
            if(AV_IN_PUBB.equals(listaEsiti.getJSONObject(0).getJSONObject(DETTAGLIO).get(CODICE))) {
              updateStatoFormAndSetIdAvviso(IN_ATTESA_PUBBLICAZIONE, parameters.get(ID_FORM),listaEsiti.getJSONObject(0).getString(ID_AVVISO));
            }
              else
            if(AV_PUBB.equals(listaEsiti.getJSONObject(0).getJSONObject(DETTAGLIO).get(CODICE)))
              updateStatoFormAndSetIdAvviso(PUBBLICATO, parameters.get(ID_FORM),listaEsiti.getJSONObject(0).getString(ID_AVVISO));
          }else if(KO.equals(listaEsiti.getJSONObject(0).getJSONObject(ESITO).get(CODICE))) {
            updateStatoForm(NON_PUBBLICATO, parameters.get(ID_FORM));
            estraiArrayErrori(resp, listaEsiti.getJSONObject(0));//porto in cima gli errori e cambio lo stato
            String codgar=parameters.get(CODGAR).toString();
            //Sbianco i CIG, idAppalto e DGUE se la scheda assegnava i CIG
            if("1".equals(parameters.get(ISCIG))) {
              sbiancaCIG(codgar);
              sbiancaIdAppalto(codgar);
              sbiancaDGUE(codgar,parameters.get(ID_FORM));
              resp.put("validationFailedFEU","true");
            }
          }
          break;
        
      case AV_RETT: //se la rettifica va a buon fine, si usa l'idAvviso nuovo. Si deve cercare l'evento con idNuovoAvviso valorizzato
        String idAvvisoScheda = (String) parameters.get(ID_AVVISO);
        boolean idAvvisoSchedaPresente = StringUtils.isNotBlank(idAvvisoScheda);
        String idAvvisoSchedaRettificata = (String) sqlManager.getObject("select idavvisonpa from g1npascheda where id=?", new Object[] {parameters.get(ID_SCHEDA_NPA_RET)});
        boolean esitoTrovato = false;
        JSONObject mostRecentError= null;
        for(int i=0;i<listaEsiti.size();i++) {
          //skippiamo tutti quegli esiti che NON sono relativi all'idAvviso della scheda che stiamo rettificando
          if(StringUtils.isNotBlank(idAvvisoSchedaRettificata) && !idAvvisoSchedaRettificata.equals(listaEsiti.getJSONObject(i).get(ID_AVVISO))) {
            continue;
          }
          //recupero l'idNuovoAvviso (se c'è)
          if(listaEsiti.getJSONObject(i).get(ID_NUOVO_AVVISO) instanceof String) {
            idNuovoAvviso = listaEsiti.getJSONObject(i).getString(ID_NUOVO_AVVISO);
          }
          //se ho già l'idAvviso sulla scheda, devo skippare gli esiti di idAvviso non pertinenti
          if(idAvvisoSchedaPresente && !idAvvisoScheda.equals(idNuovoAvviso)) {
            continue;
          }
          
          if(WT.equals(listaEsiti.getJSONObject(i).getJSONObject(ESITO).get(CODICE))) {
            if(StringUtils.isBlank(idNuovoAvviso)) {
              //se è WT senza idNuovoAvviso non facciamo nulla
            }else {//se è WT con idNuovoAvviso, aggiorniamo "idavvisonpa" e portiamo allo stato "in attesa pubblicazione"
              updateStatoFormAndSetIdAvviso(IN_ATTESA_PUBBLICAZIONE, parameters.get(ID_FORM),idNuovoAvviso);
            }
            esitoTrovato=true;
            break;
          }
          //se è OK aggiorno e porto allo stato 60. Nota 05/07/2024 La il dettaglio.codice può essere sia AV_PUBB che AV_RETT!
          if(OK.equals(listaEsiti.getJSONObject(i).getJSONObject(ESITO).get(CODICE)) && StringUtils.isNotBlank(idNuovoAvviso)) {
            if(AV_IN_PUBB.equals(listaEsiti.getJSONObject(i).getJSONObject(DETTAGLIO).get(CODICE))) {
              updateStatoFormAndSetIdAvviso(IN_ATTESA_PUBBLICAZIONE, parameters.get(ID_FORM),idNuovoAvviso);
            }else if(AV_PUBB.equals(listaEsiti.getJSONObject(i).getJSONObject(DETTAGLIO).get(CODICE)) || AV_RETT.equals(listaEsiti.getJSONObject(i).getJSONObject(DETTAGLIO).get(CODICE))) {
              aggiornaDatiDaRettifica=true;
            }
            esitoTrovato=true;
            break;
          }
          
          //se siamo arrivati qui, lo stato deve essere KO. Recupero l'evento KO più recente per restituirlo
          if(KO.equals(listaEsiti.getJSONObject(i).getJSONObject(ESITO).get(CODICE))) {
            if(checkHasErrore(listaEsiti.getJSONObject(i), "ERR44")) {//se errore 44, la schede sono state manomesse in db: non consideriamo questo errore come esito utile per aggiornare lo stato
              continue;
            }else {
              if(mostRecentError==null)
                mostRecentError=listaEsiti.getJSONObject(i);
              else {
                java.util.Date dataControlloOld = getDataFromString(mostRecentError.getString("dataControllo"));
                java.util.Date dataControlloNew = getDataFromString(listaEsiti.getJSONObject(i).getString("dataControllo"));
                if(dataControlloNew.after(dataControlloOld)) {
                  mostRecentError=listaEsiti.getJSONObject(i);
                }
              }
            }
          }
        }
        //se nel ciclo non ho trovato stati WT o OK utili, restituisco l'errore più recente
        if(!esitoTrovato && mostRecentError!=null) {
           updateStatoForm(NON_PUBBLICATO, parameters.get(ID_FORM));
           estraiArrayErrori(resp, mostRecentError);//porto in cima gli errori e cambio lo stato
           break;
         }
        }
      }
    }
    
    if(aggiornaDatiDaRettifica) {
      updateTornFromG1nparet(parameters);//aggiorno i dati copiando da G1NPARET
      updateStatoFormAndSetIdAvviso(PUBBLICATO, parameters.get(ID_FORM),idNuovoAvviso);
    }
      
    return resp;
  }
  
  /** chiamata per verificare lo stato dell'appalto **/
  public JSONObject serviceStatoAppalto(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/servizi-comuni/stato-appalto");
    
    Map<String,String> parametersMap = new HashMap<String,String>();
    
    if(parameters.get(CIG)!=null)
      parametersMap.put(CIG,parameters.get(CIG).toString());
    if(parameters.get(ID_CONTRATTO)!=null)
      parametersMap.put(ID_CONTRATTO,parameters.get(ID_CONTRATTO).toString());
    if(parameters.get(ID_APPALTO_TORN)!=null)
      parametersMap.put(ID_APPALTO,parameters.get(ID_APPALTO_TORN).toString());
    
    Builder builder = getBuilder(client,builderMap,parametersMap).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+parametersMap.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.get();
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      if(((Long) parameters.get(STATO_NPA) < CONFERMATO) && STATO_CONF.equals(resp.getJSONObject("data").getJSONObject(ESITO_STATO).getJSONObject(STATO).getString(CODICE)))
          updateStatoForm(CONFERMATO, parameters.get(ID_FORM));
      if(((Long) parameters.get(STATO_NPA) < STOP_PUBBLICAZIONE) && IN_STOP_PUBB.equals(resp.getJSONObject("data").getJSONObject(ESITO_STATO).getJSONObject(STATO).getString(CODICE)))
        updateStatoForm(STOP_PUBBLICAZIONE, parameters.get(ID_FORM));
      if(((Long) parameters.get(STATO_NPA) < PUBBLICATO) && STATO_PUBB.equals(resp.getJSONObject("data").getJSONObject(ESITO_STATO).getJSONObject(STATO).getString(CODICE))) {
        if("1".equals(parameters.get(ISCIG)) && (Long) parameters.get(STATO_NPA) < CONFERMATO)
          updateStatoForm(CONFERMATO, parameters.get(ID_FORM));//forzo le schede che non prevedono la pubblicazione manuale ma che attribuiscono i CIG, allo Stato "CONFERMATO" che ne permette il recupero
        else
          updateStatoForm(PUBBLICATO, parameters.get(ID_FORM));
      }
    }
        
    return resp;
  }
  
  //############### PIANIFICAZIONE APPALTO ###############
  /** chiamata per creare il piano **/
  public JSONObject serviceCreaPiano(Map<String,Object> parameters) throws Exception, Throwable{
    
    parameters=getParametersFromDB(parameters);
    Client client = (Client) parameters.get(CLIENT);
    
    String codtecInviante="";
    if(!MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)))
      codtecInviante=getCodtecInviante(parameters);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pianificazione-appalto/crea-piano");
    
    Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));
    
    //costruzione body
    JSONObject bodyFields = new JSONObject();
    Vector objects = sqlManager.getVector("select idprg_eform, iddoc_eform, textnpa from G1NPASCHEDA where idform=?",
        new Object[] {parameters.get(ID_FORM)});
    
   
    String eform_idprg= SqlManager.getValueFromVectorParam(objects, 0).getStringValue();
    Long eform_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 1).getValue();


    Object[] avvisiErroriValidazioneEForm = null;
    if(eform_iddoc!=null) {
      if (!MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        avvisiErroriValidazioneEForm = validazioneManipolazioneEFormManager.controllaAggiornaEform(eform_idprg, eform_iddoc, parameters);
        lanciaErroriValidazioneEForm(avvisiErroriValidazioneEForm);
      }
      BlobFile digogg = fileAllegatoManager.getFileAllegato(eform_idprg, eform_iddoc);
      bodyFields.put(BODY_EFORM,org.apache.commons.codec.binary.Base64.encodeBase64String(digogg.getStream()));
    }
    String anacForm= SqlManager.getValueFromVectorParam(objects, 2).getStringValue();
    if (StringUtils.isNotEmpty(anacForm)) {
      bodyFields.put(BODY_ANAC_FORM,JSONObject.fromObject(anacForm));
    }

    JSONObject body = new JSONObject();
    body.put(BODY,bodyFields);
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
      
    Response response = builder.post(Entity.json(body.toString()));
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      updateStatoAndIdScheda(IN_LAVORAZIONE,resp.getJSONObject("data").getString(ID_PIANIFICAZIONE), parameters);
      sqlManager.update("update TORN set idpianinpa=?, coddrp=? where codgar=?", new Object[] {resp.getJSONObject("data").getString(ID_PIANIFICAZIONE),codtecInviante,parameters.get(CODGAR)});
    }

    return resp;
  }
  
  /** chiamata per verificare il piano ASINCRONA **/
  public JSONObject serviceVerificaPiano(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pianificazione-appalto/verifica-piano/"+parameters.get(ID_SCHEDA_NPA));
    
    Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Id pianificazione="+parameters.get(ID_SCHEDA_NPA));
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(""));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      updateStatoForm(IN_CORSO_DI_VERIFICA, parameters.get(ID_FORM));
    }
      
    return resp;
  }
  
  /** chiamata per cancellare il piano **/
  public JSONObject serviceCancellaPiano(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pianificazione-appalto/cancella-piano");
    
    Builder builder = getBuilder(client, builderMap,null).headers(getHeaders(parameters));
    
    JSONObject body = new JSONObject();
    
    body.put(ID_PIANIFICAZIONE, parameters.get(ID_SCHEDA_NPA));
    
    if(parameters.get(BODY_MOTIVO)!=null)
    body.put(BODY_MOTIVO, parameters.get(BODY_MOTIVO));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(body.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && "Cancellazione eseguita con successo".equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      updateStatoForm(CANCELLATO, parameters.get(ID_FORM));
      sbiancaIdPiano(parameters.get(CODGAR).toString());
    }
      
    return resp;
  }
  
  /** chiamata per confermare il piano ASINCRONA **/
  public JSONObject serviceConfermaPiano(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pianificazione-appalto/conferma-piano/"+parameters.get(ID_SCHEDA_NPA));
    
    Builder builder = getBuilder(client, builderMap,null).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Id pianificazione="+parameters.get(ID_SCHEDA_NPA));
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(""));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      updateStatoForm(IN_CONFERMA, parameters.get(ID_FORM));
    }
      
    return resp;
  }
  
  /** chiamata per consultare un piano **/
  public JSONObject serviceConsultaPiano(Map<String,Object> parameters) throws GestoreException, SQLException {
    
      parameters=getParametersFromDB(parameters);
      Client client = (Client) parameters.get(CLIENT);
      
      JSONObject resp = null;
      Map<String,String> builderMap = new HashMap<String,String>();
      builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
      builderMap.put(SERVICE_URL, "/protected/pianificazione-appalto/consulta-piano");
      
      Map<String,String> parametersMap = new HashMap<String,String>();
      parametersMap.put(ID_PIANIFICAZIONE,parameters.get(ID_SCHEDA_NPA).toString());
      
      Builder builder = getBuilder(client, builderMap,parametersMap).headers(getHeaders(parameters));
      
      if(parameters.get(LOG_EVENTO_REQ)!=null) {
        LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
        logEvento.setErrmsg("Parametri= "+parametersMap.toString());
        enrichLogevento(logEvento,parameters);
        LogEventiUtils.insertLogEventi(logEvento);
      }
      
      Response response = builder.get();
      
      resp = response.readEntity(JSONObject.class);
      
      if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        String stato = resp.getJSONObject("data").getJSONObject("piano").getJSONObject("scheda").getJSONObject(STATO).getString(CODICE);
        if(STATO_CONF.equals(stato)) {
            updateStatoForm(CONFERMATO, parameters.get(ID_FORM));
        }
      }
      
      return resp;
  }

  
  //############### COMUNICA APPALTO ###############
  /** chiamata per creare l'appalto **/
  public JSONObject serviceCreaAppalto(Map<String,Object> parameters) throws Exception, Throwable{
    
    parameters=getParametersFromDB(parameters);
    Client client = (Client) parameters.get(CLIENT);
    
    String codtecInviante="";
    if(!MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)))
      codtecInviante=getCodtecInviante(parameters);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/comunica-appalto/crea-appalto");
    
    Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));
    
    //costruzione body
    JSONObject bodyFields = new JSONObject();
    Vector objects = sqlManager.getVector("select textnpa, idprg_eform, iddoc_eform, idprg_espd, iddoc_espd from G1NPASCHEDA where idform=?",
        new Object[] {parameters.get(ID_FORM)});
    
    String anacForm= SqlManager.getValueFromVectorParam(objects, 0).getStringValue();
    String eform_idprg= SqlManager.getValueFromVectorParam(objects, 1).getStringValue();
    Long eform_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 2).getValue();
    String espd_idprg= SqlManager.getValueFromVectorParam(objects, 3).getStringValue();
    Long espd_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 4).getValue();
    
    bodyFields.put(BODY_ANAC_FORM,JSONObject.fromObject(anacForm));

    Object[]  avvisiErroriValidazioneEForm = null;
    if(eform_iddoc!=null) {
      if (!MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        avvisiErroriValidazioneEForm = validazioneManipolazioneEFormManager.controllaAggiornaEform(eform_idprg, eform_iddoc, parameters);
        lanciaErroriValidazioneEForm(avvisiErroriValidazioneEForm);
      }
      BlobFile digogg = fileAllegatoManager.getFileAllegato(eform_idprg, eform_iddoc);
      bodyFields.put(BODY_EFORM,org.apache.commons.codec.binary.Base64.encodeBase64String(digogg.getStream()));
    }
    if(espd_iddoc!=null) {
      BlobFile digogg = fileAllegatoManager.getFileAllegato(espd_idprg, espd_iddoc);
      byte[] file = EstrazioneContenutoFileFirmatoMarcato.estraiContenutoFile(digogg.getStream(),digogg.getNome());
      bodyFields.put(BODY_ESPD,org.apache.commons.codec.binary.Base64.encodeBase64String(file));
     
    }
    
    JSONObject body = new JSONObject();
    body.put(BODY,bodyFields);
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
      
    Response response = builder.post(Entity.json(body.toString()));
    resp = response.readEntity(JSONObject.class);
    boolean settaCodiceAppalto=false;
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      updateStatoAndIdScheda(IN_LAVORAZIONE,resp.getJSONObject("data").getString(ID_APPALTO), parameters);
      sqlManager.update("update TORN set idappaltonpa=?, coddrp=? where codgar=?", new Object[] {resp.getJSONObject("data").getString(ID_APPALTO),codtecInviante,parameters.get(CODGAR)});
      settaCodiceAppalto=true;
    }else{
      if(Family.SERVER_ERROR.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
          if (checkInternalError(resp,"GEN004")) {
            updateStatoForm(VERIFICA_CREAZIONE, parameters.get(ID_FORM));
            settaCodiceAppalto=true;
          }
      }
    }
    if(settaCodiceAppalto){
      String insertG1NPAFORM = "UPDATE G1NPASCHEDA SET codiceappalto=? WHERE idform = ? ";
      this.sqlManager.update(insertG1NPAFORM, new Object[] {bodyFields.getJSONObject(BODY_ANAC_FORM).getJSONObject("appalto").getString(CODICEAPPALTO),parameters.get(ID_FORM)});
    }

    return resp;
  }
  
  /** chiamata per verificare l'appalto ASINCRONA **/
  public JSONObject serviceVerificaAppalto(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/comunica-appalto/verifica-appalto/"+parameters.get(ID_SCHEDA_NPA));
    
    Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Id appalto="+parameters.get(ID_SCHEDA_NPA));
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(""));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      updateStatoForm(IN_CORSO_DI_VERIFICA, parameters.get(ID_FORM));
    }
      
    return resp;
  }
  
  /** chiamata per confermare l'appalto ASINCRONA **/
  public JSONObject serviceConfermaAppalto(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/comunica-appalto/conferma-appalto/"+parameters.get(ID_SCHEDA_NPA));
    
    Builder builder = getBuilder(client, builderMap,null).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Id appalto="+parameters.get(ID_SCHEDA_NPA));
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(""));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      updateStatoForm(IN_CONFERMA, parameters.get(ID_FORM));
    }
      
    return resp;
  }
  
  /** chiamata per verificare lo recuperari i CIG dei lotti associati all'appalto **/
  public JSONObject serviceRecuperaCIG(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/comunica-appalto/recupera-cig");
    
    WebTarget webTarget = getWebTarget(client, builderMap);
    MultivaluedMap <String,Object> headers = getHeaders(parameters);
    
    Map<String,String> parametersMap = new HashMap<String,String>();
    
    int perPage=20;
    
    if(parameters.get(ID_APPALTO_TORN)!=null)
      parametersMap.put(ID_APPALTO,parameters.get(ID_APPALTO_TORN).toString());
    if(parameters.get(PER_PAGE)!=null) {
      perPage=(int) parameters.get(PER_PAGE);
      parametersMap.put(PER_PAGE,Integer.toString(perPage));    
    }
    if(parameters.get(PAGE)!=null)
      parametersMap.put(PAGE,parameters.get(PAGE).toString());
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+parametersMap.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    String codgar= parameters.get(CODGAR).toString(); 
    int chiamateDaFare=1;
    if(codgar.charAt(0)!='$') {
      Long lotti =  ((Long) sqlManager.getObject("select count(1) from gare where codgar1=? and codgar1<>ngara", new Object[] {codgar}));
      chiamateDaFare = (int) Math.ceil((double)lotti / (double) perPage);
    }
    
    JSONObject respAcc = new JSONObject();

    for(int i=0;i<chiamateDaFare;i++) {
        parametersMap.put(PAGE,Integer.toString(i+1));
        Builder builder = getBuilder(webTarget,parametersMap).headers(headers);
        Response response = builder.get();
        resp = response.readEntity(JSONObject.class);
        if(chiamateDaFare>1)
          respAcc.put("response"+Integer.toString(i+1), resp);
       
        if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
          JSONArray results = resp.getJSONObject("data").getJSONArray(RESULT);
          if(results!=null && results.size()>0) {
              assegnaCIG(results,parameters);
          }else {
            resp.put(PG_ERRORS,"CIG non ancora assegnati da ANAC");
          }
        } else {
          if(chiamateDaFare>1) {
            respAcc.put("done", "N");
            respAcc.put(PG_ERRORS,"ERRORE: Si è verificato un errore nell'elaborazione della richiesta da parte di ANAC.");
            if(resp.getString("govwayTransactionId")!=null)
              respAcc.accumulate("govwayTransactionId",resp.getString("govwayTransactionId"));
          }
        } 
    }
    if(chiamateDaFare==1) {
      respAcc=resp;
    }
    Long lottiSenzaCIG =  (Long) sqlManager.getObject("select count(1) from gare where codgar1=? and codgar1<>ngara and codcig is null", new Object[] {codgar});
    if((Long) parameters.get(STATO_NPA) < ACQUISIZIONE_CIG && lottiSenzaCIG==0 && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      updateStatoForm(ACQUISIZIONE_CIG, parameters.get(ID_FORM));
      respAcc.put("done", "Y");
      respAcc.remove(PG_ERRORS);
    }
    if(MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))){
      respAcc.put("done", "Y");
      respAcc.remove(PG_ERRORS);
    }
      
    return respAcc;
  }
  
  /** chiamata per confermare l'appalto **/
  public JSONObject serviceCancellaAppalto(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/comunica-appalto/cancella-appalto");
    
    Builder builder = getBuilder(client, builderMap,null).headers(getHeaders(parameters));
    
    JSONObject body = new JSONObject();
    
    body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
    
    if(parameters.get(BODY_MOTIVO)!=null)
    body.put(BODY_MOTIVO, parameters.get(BODY_MOTIVO));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(body.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && "Cancellazione eseguita con successo".equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      updateStatoForm(CANCELLATO, parameters.get(ID_FORM));
      String codiceGara=parameters.get(CODGAR).toString();
      sbiancaCIG(codiceGara);
      sbiancaIdAppalto(codiceGara);
      sbiancaDGUE(codiceGara,parameters.get(ID_FORM));
      resp.put("validationFailedFEU","true");
    }
      
    return resp;
  }

  /** chiamata per recuperare i dati dell'appalto tramite codiceAppalto' **/
  public JSONObject serviceRicercaAppalto(Map<String,Object> parameters) throws GestoreException, SQLException {

    parameters=getParametersFromDB(parameters);

    Client client = (Client) parameters.get(CLIENT);

    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/comunica-appalto/ricerca-appalto");

    Map<String,String> parametersMap = new HashMap<>();

    int perPage=20;

    if(parameters.get(CODICEAPPALTO)!=null)
      parametersMap.put(CODICEAPPALTO,parameters.get(CODICEAPPALTO).toString());
    if(parameters.get(PER_PAGE)!=null) {
      perPage=(int) parameters.get(PER_PAGE);
      parametersMap.put(PER_PAGE,Integer.toString(perPage));
    }
    if(parameters.get(PAGE)!=null)
      parametersMap.put(PAGE,parameters.get(PAGE).toString());

    Builder builder = getBuilder(client,builderMap,parametersMap).headers(getHeaders(parameters));

    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+parametersMap.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    String codtecInviante="";
    if(!MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)))
      codtecInviante=getCodtecInviante(parameters);

    Response response = builder.get();

    resp = response.readEntity(JSONObject.class);

    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      JSONArray listaEsiti = resp.getJSONObject("data").getJSONArray(RESULT);
      if(!listaEsiti.isEmpty()){
        String idAppalto=listaEsiti.getJSONObject(0).getString(ID_APPALTO);
        updateStatoAndIdScheda(IN_LAVORAZIONE,idAppalto, parameters);
        sqlManager.update("update TORN set idappaltonpa=?, coddrp=? where codgar=?", new Object[] {idAppalto,codtecInviante,parameters.get(CODGAR)});
        resp.getJSONObject("data").put(ID_APPALTO, idAppalto);
      }else{
        updateStatoForm(IN_COMPILAZIONE,parameters.get(ID_FORM));
      }
    }




    return resp;
  }
  
  /** chiamata per consultare l'appalto **/
 /*public JSONObject serviceConsultaAppalto(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/comunica-appalto/consulta-appalto");
    
    Map<String,String> parametersMap = new HashMap<String,String>();
    
    if(parameters.get(CIG)!=null)
      parametersMap.put(CIG,parameters.get(CIG).toString());
    if(parameters.get(ID_SCHEDA_NPA)!=null)
      parametersMap.put(ID_APPALTO,parameters.get(ID_SCHEDA_NPA).toString());
    
    Builder builder = getBuilder(client, builderMap,parametersMap).headers(getHeaders(parameters));
    Response response = builder.get();
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      //TODO
    }
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+parametersMap.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
      
    return resp;
  }*/
 
 /** chiamata per creare l'appalto **/
 /*public JSONObject serviceModificaAppalto(Map<String,Object> parameters) throws Exception, Throwable{
   
   parameters=getParametersFromDB(parameters);
   Client client = (Client) parameters.get(CLIENT);
   
   JSONObject resp = null;
   Map<String,String> builderMap = new HashMap<String,String>();
   builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
   builderMap.put(SERVICE_URL, "/protected/comunica-appalto/modifica-appalto");
   
   Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));
   
   //costruzione body
   JSONObject bodyFields = new JSONObject();
   Vector objects = sqlManager.getVector("select textnpa, idprg_eform, iddoc_eform, idprg_espd, iddoc_espd from G1NPASCHEDA where idform=?",
       new Object[] {parameters.get(ID_FORM)});
   
   String anacForm= SqlManager.getValueFromVectorParam(objects, 0).getStringValue();
   String eform_idprg= SqlManager.getValueFromVectorParam(objects, 1).getStringValue();
   Long eform_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 2).getValue();
   String espd_idprg= SqlManager.getValueFromVectorParam(objects, 3).getStringValue();
   Long espd_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 4).getValue();
   
   bodyFields.put(BODY_ANAC_FORM,JSONObject.fromObject(anacForm));

   if(eform_iddoc!=null) {
     BlobFile digogg = fileAllegatoManager.getFileAllegato(eform_idprg, eform_iddoc);
     bodyFields.put(BODY_EFORM,org.apache.commons.codec.binary.Base64.encodeBase64String(digogg.getStream()));
   }
   if(espd_iddoc!=null) {
     BlobFile digogg = fileAllegatoManager.getFileAllegato(espd_idprg, espd_iddoc);
     byte[] file = EstrazioneContenutoFileFirmatoMarcato.estraiContenutoFile(digogg.getStream(),digogg.getNome());
      bodyFields.put(BODY_ESPD,org.apache.commons.codec.binary.Base64.encodeBase64String(file));
   }
   
   JSONObject body = new JSONObject();
   body.put(BODY,bodyFields);
   body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
     
   Response response = builder.post(Entity.json(body.toString()));
   resp = response.readEntity(JSONObject.class);
   
   if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
     //TODO
     }
   
   if(parameters.get(LOG_EVENTO_REQ)!=null) {
     LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
     logEvento.setErrmsg(body.toString());
     enrichLogevento(logEvento,parameters);
     LogEventiUtils.insertLogEventi(logEvento);
   }
     
   return resp;
 }*/
  
  //############### PUBBLICA AVVISO ###############
  /** chiamata per pubblicare l'avviso **/
  public JSONObject servicePubblicaAvviso(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pubblica-avviso/pubblica-avviso");
    
    Map<String,String> parametersMap = new HashMap<String,String>();
    
    JSONObject body = new JSONObject();
    body.put(ID_APPALTO,parameters.get(ID_APPALTO_TORN).toString());
    body.put(ID_AVVISO,parameters.get(ID_AVVISO).toString());
    
    Builder builder = getBuilder(client, builderMap,parametersMap).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    Response response = builder.post(Entity.json(body.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
          updateStatoForm(RICHIESTA_PUBBLICAZIONE, parameters.get(ID_FORM));
    }
    
    return resp;
  }
  
  /** chiamata per confermare l'avviso ASINCRONA **/
  public JSONObject serviceCancellaAvviso(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pubblica-avviso/cancella-avviso");
    
    Builder builder = getBuilder(client, builderMap,null).headers(getHeaders(parameters));
    
    JSONObject body = new JSONObject();
    
    body.put(ID_AVVISO, parameters.get(ID_AVVISO));
    
    if(parameters.get(ID_APPALTO_TORN)!=null)
      body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
    
    if(parameters.get(BODY_MOTIVO)!=null)
      body.put(BODY_MOTIVO, parameters.get(BODY_MOTIVO));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(body.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      updateStatoForm(RICH_STOP_PUBBLICAZIONE, parameters.get(ID_FORM));
    }
      
    return resp;
  }
  
  /** chiamata per verificare l'avviso ASINCRONA**/
  public JSONObject serviceStatoAvviso(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pubblica-avviso/stato-avviso/"+parameters.get(ID_AVVISO));
    
    Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Id avviso="+parameters.get(ID_AVVISO));
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.get();
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      if(((Long) parameters.get(STATO_NPA) < IN_ATTESA_PUBBLICAZIONE) && STATO_IN_ATT_PUBB.equals(resp.getJSONObject("data").getJSONObject(STATO_AVVISO).getJSONObject(STATO).getString(CODICE)))
          updateStatoForm(IN_ATTESA_PUBBLICAZIONE, parameters.get(ID_FORM));
      if(((Long) parameters.get(STATO_NPA) < PUBBLICATO) && STATO_PUBB.equals(resp.getJSONObject("data").getJSONObject(STATO_AVVISO).getJSONObject(STATO).getString(CODICE)))
        updateStatoForm(PUBBLICATO, parameters.get(ID_FORM));
      if(STATO_STOP_PUBB.equals(resp.getJSONObject("data").getJSONObject(STATO_AVVISO).getJSONObject(STATO).getString(CODICE))) {
        updateStatoForm(STOP_PUBBLICAZIONE, parameters.get(ID_FORM));
        //sbianco i CIG, idAppalto, DGUE se la scheda assegnava i CIG
        if("1".equals(parameters.get(ISCIG))) {
          String codgar=parameters.get(CODGAR).toString();
          sbiancaCIG(codgar);
          sbiancaIdAppalto(codgar);
          sbiancaDGUE(codgar,parameters.get(ID_FORM));
          resp.put("validationFailedFEU","true");
        }
      }
    }
      
    return resp;
  }
  
	public JSONObject serviceConsultaAvviso(Map<String, Object> parameters) throws GestoreException, SQLException {

		parameters = getParametersFromDB(parameters);

		Client client = (Client) parameters.get(CLIENT);

		JSONObject resp = null;
		Map<String, String> builderMap = new HashMap<String, String>();
		builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
		builderMap.put(SERVICE_URL, "/protected/pubblica-avviso/consulta-avviso/" + parameters.get(ID_AVVISO));

		Builder builder = getBuilder(client, builderMap, null).headers(getHeaders(parameters));

		if (parameters.get(LOG_EVENTO_REQ) != null) {
          LogEvento logEvento = (LogEvento) parameters.get(LOG_EVENTO_REQ);
          logEvento.setErrmsg("Id avviso=" + parameters.get(ID_AVVISO));
          enrichLogevento(logEvento, parameters);
          LogEventiUtils.insertLogEventi(logEvento);
        }

		Response response = builder.get();

		resp = response.readEntity(JSONObject.class);

		if (Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
			if ((PUBBLICATO.equals((Long) parameters.get(STATO_NPA))) && STATO_PUBB.equals(resp.getJSONObject("data").getJSONObject(AVVISO).getJSONObject(STATO).getString(CODICE))) {
				String codgar = parameters.get(CODGAR).toString();
				inserisciPubblicazioni(resp, codgar, parameters.get(ID_FORM));
			}
		}

		return resp;
	}

  /** chiamata per modificare l'avviso non ancora pubblicato**/
  /*public JSONObject serviceModificaAvviso(Map<String,Object> parameters) throws Exception, Throwable {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pubblica-avviso/modifica-avviso");
    
    Map<String,String> parametersMap = new HashMap<String,String>();
    
    //costruzione body
    JSONObject bodyFields = new JSONObject();
    Vector objects = sqlManager.getVector("select textnpa, idprg_eform, iddoc_eform, idprg_espd, iddoc_espd from G1NPASCHEDA where idform=?",
        new Object[] {parameters.get(ID_FORM)});
    
    String anacForm= SqlManager.getValueFromVectorParam(objects, 0).getStringValue();
    String eform_idprg= SqlManager.getValueFromVectorParam(objects, 1).getStringValue();
    Long eform_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 2).getValue();
    String espd_idprg= SqlManager.getValueFromVectorParam(objects, 3).getStringValue();
    Long espd_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 4).getValue();
    
    bodyFields.put(BODY_ANAC_FORM,JSONObject.fromObject(anacForm));

    if(eform_iddoc!=null) {
      BlobFile digogg = fileAllegatoManager.getFileAllegato(eform_idprg, eform_iddoc);
      bodyFields.put(BODY_EFORM,org.apache.commons.codec.binary.Base64.encodeBase64String(digogg.getStream()));
    }
    if(espd_iddoc!=null) {
      BlobFile digogg = fileAllegatoManager.getFileAllegato(espd_idprg, espd_iddoc);
      byte[] file = EstrazioneContenutoFileFirmatoMarcato.estraiContenutoFile(digogg.getStream(),digogg.getNome());
      bodyFields.put(BODY_ESPD,org.apache.commons.codec.binary.Base64.encodeBase64String(file));
    }
    
    JSONObject body = new JSONObject();
    body.put(BODY,bodyFields);
    Builder builder = getBuilder(client, builderMap,parametersMap).headers(getHeaders(parameters));

    Response response = builder.post(Entity.json(body.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
          //TODO
    }
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
        
    return resp;
  }*/
  
  /** chiamata per rettificare l'avviso pubblicato**/
  public JSONObject serviceRettificaAvviso(Map<String,Object> parameters) throws Exception, Throwable {
    
    parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/pubblica-avviso/rettifica-avviso");
    
    Map<String,String> parametersMap = new HashMap<String,String>();
    
    JSONObject payload = new JSONObject();
    
    String idAvvisoSchedaRettificata = (String) sqlManager.getObject("select idavvisonpa from g1npascheda where id=?", new Object[] {parameters.get(ID_SCHEDA_NPA_RET)});
    payload.put(ID_AVVISO, idAvvisoSchedaRettificata);
    
    //lettura da request del parametro tipoOperazione (placeholder)
    String tipoOperazione = "COMUNICA_APPALTO"; //parameters.get(TIPO_OPERAZIONE).toString();
    payload.put("tipologiaSchedaGroupType", tipoOperazione);
    
    if("PIANIFICAZIONE".equals(tipoOperazione))
      payload.put(ID_PIANIFICAZIONE, parameters.get(ID_PIANI_TORN));
    else
      payload.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
    
    //costruzione body interno
    JSONObject bodyFields = new JSONObject();
    Vector objects = sqlManager.getVector("select textnpa, idprg_eform, iddoc_eform, idprg_espd, iddoc_espd from G1NPASCHEDA where idform=?",
        new Object[] {parameters.get(ID_FORM)});
    
    String anacForm= SqlManager.getValueFromVectorParam(objects, 0).getStringValue();
    String eform_idprg= SqlManager.getValueFromVectorParam(objects, 1).getStringValue();
    Long eform_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 2).getValue();
    String espd_idprg= SqlManager.getValueFromVectorParam(objects, 3).getStringValue();
    Long espd_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 4).getValue();
    
    bodyFields.put(BODY_ANAC_FORM,JSONObject.fromObject(anacForm));

    if(eform_iddoc!=null) {
      if (!MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        Object[] avvisiErroriValidazioneEForm = validazioneManipolazioneEFormManager.controllaAggiornaEform(eform_idprg, eform_iddoc, parameters);
        lanciaErroriValidazioneEForm(avvisiErroriValidazioneEForm);
      }
      BlobFile digogg = fileAllegatoManager.getFileAllegato(eform_idprg, eform_iddoc);
      bodyFields.put(BODY_EFORM,org.apache.commons.codec.binary.Base64.encodeBase64String(digogg.getStream()));
    }
    if(espd_iddoc!=null) {
      BlobFile digogg = fileAllegatoManager.getFileAllegato(espd_idprg, espd_iddoc);
      byte[] file = EstrazioneContenutoFileFirmatoMarcato.estraiContenutoFile(digogg.getStream(),digogg.getNome());
      bodyFields.put(BODY_ESPD,org.apache.commons.codec.binary.Base64.encodeBase64String(file));
    }
    
    JSONObject body = new JSONObject();
    body.put(BODY,bodyFields);
    payload.put("scheda", body);
    
    Builder builder = getBuilder(client, builderMap,parametersMap).headers(getHeaders(parameters));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(payload.toString());
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    Response response = builder.post(Entity.json(payload.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      updateStatoForm(IN_LAVORAZIONE, parameters.get(ID_FORM));
      //non ci serve, ma lo scriviamo lo stesso per omogeneità
      String insertG1NPAFORM = "UPDATE G1NPASCHEDA SET codiceappalto=? WHERE idform = ? ";
      this.sqlManager.update(insertG1NPAFORM, new Object[] {bodyFields.getJSONObject(BODY_ANAC_FORM).getJSONObject("appalto").getString(CODICEAPPALTO),parameters.get(ID_FORM)});
    }
        
    return resp;
  }
  
  //############### COMUNICA POST PUBBLICAZIONE ###############
  /** chiamata per creare una scheda **/
  public JSONObject serviceCreaScheda(Map<String,Object> parameters) throws Exception, Throwable {
    
      parameters=getParametersFromDB(parameters);
      Client client = (Client) parameters.get(CLIENT);
      
      JSONObject resp = null;
      Map<String,String> builderMap = new HashMap<String,String>();
      builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
      builderMap.put(SERVICE_URL, "/protected/comunica-post-pubblicazione/crea-scheda");
      
      Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));
      
      //costruzione body
      JSONObject bodyFields = new JSONObject();
      Vector objects = sqlManager.getVector("select textnpa, idprg_eform, iddoc_eform from G1NPASCHEDA where idform=?",
          new Object[] {parameters.get(ID_FORM)});
      
      String anacForm= SqlManager.getValueFromVectorParam(objects, 0).getStringValue();
      String eform_idprg= SqlManager.getValueFromVectorParam(objects, 1).getStringValue();
      Long eform_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 2).getValue();
      
      bodyFields.put(BODY_ANAC_FORM,JSONObject.fromObject(anacForm));

      Object[] avvisiErroriValidazioneEForm = null;
      if(eform_iddoc!=null) {
        if (!MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
          avvisiErroriValidazioneEForm = validazioneManipolazioneEFormManager.controllaAggiornaEform(eform_idprg, eform_iddoc, parameters);
          lanciaErroriValidazioneEForm(avvisiErroriValidazioneEForm);
        }
        BlobFile digogg = fileAllegatoManager.getFileAllegato(eform_idprg, eform_iddoc);
        bodyFields.put(BODY_EFORM,org.apache.commons.codec.binary.Base64.encodeBase64String(digogg.getStream()));
      }

      JSONObject body = new JSONObject();
      body.put(BODY,bodyFields);
      body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
      
      if(parameters.get(LOG_EVENTO_REQ)!=null) {
        LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
        logEvento.setErrmsg(body.toString());
        enrichLogevento(logEvento,parameters);
        LogEventiUtils.insertLogEventi(logEvento);
      }
      
      Response response = builder.post(Entity.json(body.toString()));
      resp = response.readEntity(JSONObject.class);
      
      if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        updateStatoAndIdScheda(IN_LAVORAZIONE,resp.getJSONObject("data").getString(ID_SCHEDA), parameters);
      }

      return resp;
  }
  
  /** chiamata per verificare una scheda ASINCRONA**/
  public JSONObject serviceVerificaScheda(Map<String,Object> parameters) throws GestoreException, SQLException {
    
      parameters=getParametersFromDB(parameters);
      Client client = (Client) parameters.get(CLIENT);
      
      JSONObject resp = null;
      Map<String,String> builderMap = new HashMap<String,String>();
      builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
      builderMap.put(SERVICE_URL, "/protected/comunica-post-pubblicazione/verifica-scheda");
      
      Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));

      JSONObject body = new JSONObject();
      body.put(ID_SCHEDA,parameters.get(ID_SCHEDA_NPA));
      body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
      
      if(parameters.get(LOG_EVENTO_REQ)!=null) {    
        LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
        logEvento.setErrmsg(body.toString());
        enrichLogevento(logEvento,parameters);
        LogEventiUtils.insertLogEventi(logEvento);
      }
      
      Response response = builder.post(Entity.json(body.toString()));
      resp = response.readEntity(JSONObject.class);
      
      if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        updateStatoForm(IN_CORSO_DI_VERIFICA, parameters.get(ID_FORM));
      }
      
      return resp;
  }
  
  /** chiamata per confermare una scheda ASINCRONA**/
  public JSONObject serviceConfermaScheda(Map<String,Object> parameters) throws GestoreException, SQLException {
    
      parameters=getParametersFromDB(parameters);
      Client client = (Client) parameters.get(CLIENT);
      
      JSONObject resp = null;
      Map<String,String> builderMap = new HashMap<String,String>();
      builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
      builderMap.put(SERVICE_URL, "/protected/comunica-post-pubblicazione/conferma-scheda");
      
      Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));

      JSONObject body = new JSONObject();
      body.put(ID_SCHEDA,parameters.get(ID_SCHEDA_NPA));
      body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
      
      if(parameters.get(LOG_EVENTO_REQ)!=null) {
        LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
        logEvento.setErrmsg(body.toString());
        enrichLogevento(logEvento,parameters);
        LogEventiUtils.insertLogEventi(logEvento);
      }
      
      Response response = builder.post(Entity.json(body.toString()));
      resp = response.readEntity(JSONObject.class);
      
      if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        updateStatoForm(IN_CONFERMA, parameters.get(ID_FORM));
      }
      
      return resp;
  }
  
  /** chiamata per confermare una scheda **/
  public JSONObject serviceCancellaScheda(Map<String,Object> parameters) throws GestoreException, SQLException {
    
      parameters=getParametersFromDB(parameters);
      Client client = (Client) parameters.get(CLIENT);
      
      JSONObject resp = null;
      Map<String,String> builderMap = new HashMap<String,String>();
      builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
      builderMap.put(SERVICE_URL, "/protected/comunica-post-pubblicazione/cancella-scheda");
      
      Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));

      JSONObject body = new JSONObject();
      body.put(ID_SCHEDA,parameters.get(ID_SCHEDA_NPA));
      body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
      
      if(parameters.get(BODY_MOTIVO)!=null)
        body.put(BODY_MOTIVO, parameters.get(BODY_MOTIVO));
      
      if(parameters.get(LOG_EVENTO_REQ)!=null) {
        LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
        logEvento.setErrmsg(body.toString());
        enrichLogevento(logEvento,parameters);
        LogEventiUtils.insertLogEventi(logEvento);
      }
      
      Response response = builder.post(Entity.json(body.toString()));
      resp = response.readEntity(JSONObject.class);
      
      if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        updateStatoForm(CANCELLATO, parameters.get(ID_FORM));
      }
      
      return resp;
  }
  
  /** chiamata per confermare una scheda **/
  public JSONObject serviceConsultaScheda(Map<String,Object> parameters) throws GestoreException, SQLException {
    
      parameters=getParametersFromDB(parameters);
      Client client = (Client) parameters.get(CLIENT);
      
      JSONObject resp = null;
      Map<String,String> builderMap = new HashMap<String,String>();
      builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
      builderMap.put(SERVICE_URL, "/protected/comunica-post-pubblicazione/consulta-scheda");
      
      Map<String,String> parametersMap = new HashMap<String,String>();
      parametersMap.put(ID_SCHEDA,parameters.get(ID_SCHEDA_NPA).toString());
      
      Builder builder = getBuilder(client, builderMap,parametersMap).headers(getHeaders(parameters));
      
      if(parameters.get(LOG_EVENTO_REQ)!=null) {
        LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
        logEvento.setErrmsg("Parametri= "+parametersMap.toString());
        enrichLogevento(logEvento,parameters);
        LogEventiUtils.insertLogEventi(logEvento);
      }
      
      Response response = builder.get();
      
      resp = response.readEntity(JSONObject.class);
      
      if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        String stato = resp.getJSONObject("data").getJSONObject("scheda").getJSONObject(STATO).getString(CODICE);
        if(STATO_CONF.equals(stato)) {
            updateStatoForm(CONFERMATO, parameters.get(ID_FORM));
        }
      }
      
      return resp;
  }
  
  /** chiamata per modificare una scheda **/
  /*public JSONObject serviceModificaScheda(Map<String,Object> parameters) throws Exception, Throwable {
    
      parameters=getParametersFromDB(parameters);
      Client client = (Client) parameters.get(CLIENT);
      
      JSONObject resp = null;
      Map<String,String> builderMap = new HashMap<String,String>();
      builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
      builderMap.put(SERVICE_URL, "/protected/comunica-post-pubblicazione/modifica-scheda");
      
      Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));
      
      //costruzione body
      JSONObject bodyFields = new JSONObject();
      Vector objects = sqlManager.getVector("select textnpa, idprg_eform, iddoc_eform from G1NPASCHEDA where idform=?",
          new Object[] {parameters.get(ID_FORM)});
      
      String anacForm= SqlManager.getValueFromVectorParam(objects, 0).getStringValue();
      String eform_idprg= SqlManager.getValueFromVectorParam(objects, 1).getStringValue();
      Long eform_iddoc= (Long) SqlManager.getValueFromVectorParam(objects, 2).getValue();
      
      bodyFields.put(BODY_ANAC_FORM,JSONObject.fromObject(anacForm));

      if(eform_iddoc!=null) {
        BlobFile digogg = fileAllegatoManager.getFileAllegato(eform_idprg, eform_iddoc);
        bodyFields.put(BODY_EFORM,org.apache.commons.codec.binary.Base64.encodeBase64String(digogg.getStream()));
      }

      JSONObject body = new JSONObject();
      body.put(BODY,bodyFields);
      body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
      body.put(ID_SCHEDA, parameters.get(ID_SCHEDA_NPA));
      
      Response response = builder.post(Entity.json(body.toString()));
      resp = response.readEntity(JSONObject.class);
      
      if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        //TODO
      }
      
      if(parameters.get(LOG_EVENTO_REQ)!=null) {
        LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
        logEvento.setErrmsg(body.toString());
        enrichLogevento(logEvento,parameters);
        LogEventiUtils.insertLogEventi(logEvento);
      }
      
      return resp;
  }*/

  //############### GESTIONE FVOE ###############
  /** chiamata per richiesta l'accesso alla documentazione FVOE **/
  public JSONObject serviceRichiestaAccessoFVOE(Map<String,Object> parameters) throws Exception, Throwable{

    Client client = (Client) parameters.get(CLIENT);

    parameters=getFVOEParametersFromDB(parameters);

    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/fvc/richiesta-accesso-fvoe");

    Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters))
            .header(HEADER_SACODICE_FISCALE, parameters.get(SACF));

    String codgar = (String) parameters.get(CODGAR);
    String idfvoe = (String) parameters.get(IDFVOE);
    List<String> codimpArray = new ArrayList<>();

    String select = "select i.cfimp,g.codimp,g.statorichiesta,g.dittao,g.isattivo,g.ruolo,g.statoverifiche from g1fvoe g join impr i on g.codimp=i.codimp where g.codgar=?";
    List<Object> params = new ArrayList<>();
    params.add(codgar);

    if(StringUtils.isNotBlank(idfvoe)){
      select+=" and g.id=?";
      params.add(idfvoe);
    }else{
      select+=" and g.idrichiesta is null and g.isattivo='1'";
    }
    List<JSONObject> formattedList = new ArrayList<>();
    JSONObject appalto = new JSONObject();
    appalto.put(ID_APPALTO,parameters.get(ID_APPALTO_TORN));

    List<Object> parametriNuovaOccorrenza = new ArrayList<>();
    boolean skip = false;

    List<Vector> dati = sqlManager.getListVector(select, params.toArray());
    if(dati!=null && !dati.isEmpty()){
      for(int i=0; i<dati.size();i++) {
        String cf = SqlManager.getValueFromVectorParam(dati.get(i), 0).getStringValue();
        String codimp=SqlManager.getValueFromVectorParam(dati.get(i), 1).getStringValue();
        codimpArray.add(codimp);
        String statoRichiesta = SqlManager.getValueFromVectorParam(dati.get(i), 2).getStringValue();
        //questa condizione per vedere se creare un nuovo record, valutiamo solo in caso di gestione puntuale
        if (StringUtils.isNotBlank(idfvoe) && "3".equals(statoRichiesta) || "4".equals(statoRichiesta) || "5".equals(statoRichiesta)){
          parametriNuovaOccorrenza.add(codimp);
          parametriNuovaOccorrenza.add(SqlManager.getValueFromVectorParam(dati.get(i), 3).stringValue());//dittao
          parametriNuovaOccorrenza.add(SqlManager.getValueFromVectorParam(dati.get(i), 4).stringValue());//isAttivo
          parametriNuovaOccorrenza.add(SqlManager.getValueFromVectorParam(dati.get(i), 5).longValue());//ruolo
          parametriNuovaOccorrenza.add(SqlManager.getValueFromVectorParam(dati.get(i), 6).longValue());//statoverifiche
        }

        JSONObject ditta = new JSONObject();
        ditta.put(BODY_CF, cf);
        if(!formattedList.contains(ditta))
          formattedList.add(ditta);
      }
    }else{
      skip=true;
    }

    //costruzione body
    JSONObject body = new JSONObject();
    body.put("appalto",appalto);
    body.put("dataInizioAutorizzazione",npaFunzioniManager.getStringDateFormat(new Date(System.currentTimeMillis())));
    body.put("operatoriEconomici",formattedList.toArray());


    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      LogEventiUtils.insertLogEventi(logEvento);
    }

    if(skip) {
      resp = new JSONObject();
      resp.put("done", "Y");
      resp.put("occorrenzeProcessate",0);
      return resp;
    }

    Response response = builder.post(Entity.json(body.toString()));
    resp = response.readEntity(JSONObject.class);

    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
       for(int i=0;i<codimpArray.size();i++) {
         String idrichiesta = resp.getJSONObject("data").getString("idRichiesta");
         if(!parametriNuovaOccorrenza.isEmpty()){
           int id = genChiaviManager.getNextId("G1FVOE");
           parametriNuovaOccorrenza.add(0,id);
           parametriNuovaOccorrenza.add(1,codgar);
           parametriNuovaOccorrenza.add(2,idrichiesta);
           parametriNuovaOccorrenza.add(3,timestamp);
           parametriNuovaOccorrenza.add(4,"1");
           sqlManager.update("insert into G1FVOE (id,codgar,idrichiesta,dtrichiesta,statorichiesta,codimp,dittao,isattivo,ruolo,statoverifiche) values (?,?,?,?,?,?,?,?,?,?)",
                   parametriNuovaOccorrenza.toArray());
           //archivio l'occorrenza originale
           sqlManager.update("update G1FVOE set isarchi='1' where id=?", new Object[]{idfvoe});
         }else {
           sqlManager.update("update G1FVOE set idrichiesta=?, dtrichiesta=?, statorichiesta=? where codgar=? and codimp=?",
                   new Object[]{idrichiesta, timestamp, "1", codgar, codimpArray.get(i)});
         }
       }
       resp.put("occorrenzeProcessate",codimpArray.size());
    }

    return resp;
  }

  /** chiamata per verificare l'esito della richiesta l'accesso alla documentazione FVOE **/
  public JSONObject serviceVerificaRichiestaAccessoFVOE(Map<String,Object> parameters) throws GestoreException, SQLException {

    parameters=getFVOEParametersFromDB(parameters);

    Client client = (Client) parameters.get(CLIENT);

    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/fvc/verifica-richiesta-accesso-fvoe");

    String idfvoe = (String) parameters.get(IDFVOE);
    String codgar = (String) parameters.get(CODGAR);

    Map<String,String> parametersMap = new HashMap<String,String>();
    parametersMap.put(ID_APPALTO,parameters.get(ID_APPALTO_TORN).toString());

    WebTarget webTarget = getWebTarget(client, builderMap);
    MultivaluedMap <String,Object> headers = getHeaders(parameters);

    //estraggo tutte le richieste per cui chiedere l'aggiornamento
    String select = "select i.cfimp,g.codimp from g1fvoe g join impr i on g.codimp=i.codimp where g.codgar=? and g.idrichiesta is not null and isarchi is null";
    List<Object> params = new ArrayList<>();
    params.add(codgar);

    if(StringUtils.isNotBlank(idfvoe)){
      select+=" and g.id=?";
      params.add(idfvoe);
    }else {
      select += " and g.isattivo='1'";
    }

    JSONObject respAcc = new JSONObject();

    List<Vector> dati = sqlManager.getListVector(select, params.toArray());

    if(dati!=null && !dati.isEmpty() && StringUtils.isNotBlank(idfvoe)) {
      parametersMap.put(BODY_CF, SqlManager.getValueFromVectorParam(dati.get(0), 0).getStringValue());
    }else{
      parametersMap.put(BODY_CF, "Chiamata massiva, i cf delle imprese sono visibili nella response");
    }

    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+ parametersMap);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    int occorrenzeProcessate = 0;
    int occorrenzeConErrori = 0;
    boolean unicaOccorrenza=false;

    if(dati!=null && !dati.isEmpty()) {
      for(int i=0;i<dati.size();i++) {
        String cfimp = SqlManager.getValueFromVectorParam(dati.get(i), 0).getStringValue();
        String codimp = SqlManager.getValueFromVectorParam(dati.get(i), 1).getStringValue();
        parametersMap.put(BODY_CF, cfimp);
        Builder builder = getBuilder(webTarget,parametersMap).headers(headers);
        Response response = builder.get();
        resp = response.readEntity(JSONObject.class);
        if(dati.size()>1)
          respAcc.put("Response_"+cfimp, resp);

        if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
          JSONObject autorizzazione = resp.getJSONObject("data").getJSONObject("autorizzazione");
          String chiaveAccesso = autorizzazione.getString(CHIAVEACCESSOFVOE);
          String stato = autorizzazione.getJSONObject(STATO).getString(CODICE);
          String statoTab2 = (String) sqlManager.getObject("select tab2tip from tab2 where tab2cod=? and tab2d1=?", new Object[]{"A1z29",stato});
          Timestamp dataUltimaLettura = new Timestamp(System.currentTimeMillis());
          Timestamp dataInizio = null;
          Timestamp dataFine = null;
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
          try {
            dataInizio = new Timestamp(dateFormat.parse(autorizzazione.getString("dataInizio")).getTime());
          } catch (ParseException e) {
            logger.error("G1FVOE, setting del campo dtinizioauth: Errore nella conversione del campo dataInizio ("+ autorizzazione.getString("dataInizio"));
          }
          try {
            dataFine = new Timestamp(dateFormat.parse(autorizzazione.getString("dataFine")).getTime());
          } catch (ParseException e) {
            logger.error("G1FVOE, setting del campo dtfineuth: Errore nella conversione del campo dataFine ("+ autorizzazione.getString("dataFine"));
          };
          sqlManager.update("update G1FVOE set chiaveaccesso=?, statorichiesta=?, dtinizioauth=?, dtfineauth=?, dtultimalett=? where codgar=? and codimp=?",
                  new Object[]{chiaveAccesso,statoTab2,dataInizio,dataFine,dataUltimaLettura,codgar,codimp});
          occorrenzeProcessate++;
        }else{
          occorrenzeConErrori++;
        }
      }
      if(dati.size()==1) {
        respAcc=resp;
        unicaOccorrenza=true;
      }
    }
    if(!unicaOccorrenza)
      respAcc.put("done","Y");
    respAcc.put("occorrenzeProcessate", occorrenzeProcessate);
    respAcc.put("occorrenzeConErrori", occorrenzeConErrori);

    return respAcc;
  }

  /** chiamata per recupeare gli estremi della documentazione FVOE **/
  public JSONObject serviceRicercaDocumentiFVOE(Map<String,Object> parameters) throws GestoreException, SQLException {

    parameters=getFVOEParametersFromDB(parameters);

    Client client = (Client) parameters.get(CLIENT);

    JSONObject resp = new JSONObject();
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/fvc/ricerca-documenti-fvoe");

    String codgar = (String) parameters.get(CODGAR);
    String codimp = (String) parameters.get(CODIMP);

    WebTarget webTarget = getWebTarget(client, builderMap);
    MultivaluedMap <String,Object> headers = getHeaders(parameters);

    Map<String,String> parametersMap = new HashMap<String,String>();
    String chiaveAccesso = getChiaveAccesso(codgar,codimp);
    if(StringUtils.isBlank(chiaveAccesso)){
      resp.put(PG_ERRORS,"Nessuna chiave d'accesso valida trovata per la ditta. La richiesta deve essere nello stato 'Autorizzata')");
      return resp;
    }
    parametersMap.put(CHIAVEACCESSOFVOE,chiaveAccesso);

    int perPage=20;

    if(parameters.get(PER_PAGE)!=null) {
      perPage=(int) parameters.get(PER_PAGE);
      parametersMap.put(PER_PAGE,Integer.toString(perPage));
    }
    if(parameters.get(PAGE)!=null)
      parametersMap.put(PAGE,parameters.get(PAGE).toString());

    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+parametersMap.toString());
      enrichLogEventoFVOE(logEvento,codimp);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    int chiamateDaFare=1;
    int occorrenzeAggiunte=0;
    int occorrenzeAggiornate=0;
    JSONObject respAcc = new JSONObject();
    respAcc.put("done", "Y");

    Timestamp dtacquisizione = new Timestamp(System.currentTimeMillis());

    for(int j=0;j<chiamateDaFare;j++) {
      parametersMap.put(PAGE,Integer.toString(j+1));
      Builder builder = getBuilder(webTarget,parametersMap).headers(headers);
      Response response = builder.get();
      resp = response.readEntity(JSONObject.class);


      if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
        if(j==0){
          try {
            chiamateDaFare = resp.getJSONObject("data").getInt("totPages");
          } catch (Exception e) {
            respAcc.put(PG_ERRORS,"ERRORE: Si è verificato un errore nell'elaborazione della richiesta da parte di ANAC.");
            logger.error(e);
          }
        }
        if(chiamateDaFare>1)
          respAcc.put("response"+Integer.toString(j+1), resp);

        JSONArray documenti = resp.getJSONObject("data").getJSONArray(RESULT);
        rimuoviArrayFascicoli(documenti);
        List<Vector> documentiRegistrati = sqlManager.getListVector("select iddocumento from G1FVOEDOC where codgar=? and codimp=? and iddocumento is not null", new Object[]{codgar, codimp});
        List<String> iddocumentiRegistrati = new ArrayList<>();
        for (int i = 0; i < documentiRegistrati.size(); i++) {
          iddocumentiRegistrati.add(SqlManager.getValueFromVectorParam(documentiRegistrati.get(i), 0).stringValue());
        }
        for (int i = 0; i < documenti.size(); i++) {
          JSONObject documento = documenti.getJSONObject(i);
          String iddocumento = documento.getString("idDocumento");
          String oggetto = documento.getString("oggetto");
          String codice = documento.getJSONObject("tipo").getString(CODICE);
          String stato = documento.getJSONObject(STATO).getString(CODICE);
          Long statoInt = 0L;
          try {
            statoInt = Long.parseLong(stato);
          } catch (Exception e) {
            statoInt = -1L;
          }
          if (iddocumentiRegistrati.contains(iddocumento)) {
            sqlManager.update("update g1fvoedoc set oggetto=?, codice=?, statodocumento=? where iddocumento=?", new Object[]{oggetto, codice, statoInt, iddocumento});
            occorrenzeAggiornate++;
          } else {
            int id = genChiaviManager.getNextId("G1FVOEDOC");
            sqlManager.update("insert into g1fvoedoc (id, codgar, codimp, iddocumento, statodocumento, codice, oggetto,dtacquisizione) values (?,?,?,?,?,?,?,?)",
                    new Object[]{new Long(id), codgar, codimp, iddocumento, statoInt, codice, oggetto, dtacquisizione});
            occorrenzeAggiunte++;
          }
        }
      } else {
        if(chiamateDaFare>1) {
          respAcc.put("done", "N");
          respAcc.put(PG_ERRORS,"ERRORE: Si è verificato un errore nell'elaborazione della richiesta da parte di ANAC.");
          if(resp.getString("govwayTransactionId")!=null)
            respAcc.accumulate("govwayTransactionId",resp.getString("govwayTransactionId"));
        }
      }
    }
    if(chiamateDaFare==1) {
      respAcc=resp;
    }
    if(MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))){
      respAcc.remove(PG_ERRORS);
    }
    respAcc.put("occorrenzeAggiunte",occorrenzeAggiunte);
    respAcc.put("occorrenzeAggiornate",occorrenzeAggiornate);

    return respAcc;
  }

  /** chiamata per richiedere nuova documentazione FVOE **/
  public JSONObject serviceRichiediDocumentiFVOE(Map<String,Object> parameters) throws Exception, Throwable{

    parameters=getFVOEParametersFromDB(parameters);
    Client client = (Client) parameters.get(CLIENT);

    JSONObject resp = new JSONObject();
    Map<String,String> builderMap = new HashMap<>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/fvc/richiedi-documenti");

    String codgar = (String) parameters.get(CODGAR);
    String codimp = (String) parameters.get(CODIMP);
    String codice = (String) parameters.get("codice");

    //costruzione body
    JSONObject body = new JSONObject();

    JSONObject tipoDocumento = new JSONObject();
    tipoDocumento.put("id","tipoDocumento");
    tipoDocumento.put("codice",codice);

    String chiaveAccesso = getChiaveAccesso(codgar,codimp);
    if(StringUtils.isBlank(chiaveAccesso)){
      resp.put(PG_ERRORS,"Nessuna chiave d'accesso valida trovata per la ditta. La richiesta deve essere nello stato 'Autorizzata')");
      return resp;
    }

    body.put(CHIAVEACCESSOFVOE,chiaveAccesso);
    body.put("tipoDocumento",tipoDocumento);

    JSONObject datiRichiesta = buildDatiSARichiesta(codice,codimp,parameters);
    if(datiRichiesta!=null)
      body.put("datiRichiesta",datiRichiesta);

    Builder builder = getBuilder(client,builderMap,null).headers(getHeaders(parameters));

    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      enrichLogEventoFVOE(logEvento,codimp);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    Response response = builder.post(Entity.json(body.toString()));
    resp = response.readEntity(JSONObject.class);

    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      String idrichiesta = resp.getJSONObject("data").getString("idRichiesta");
      int id = genChiaviManager.getNextId("G1FVOEDOC");
      sqlManager.update("insert into G1FVOEDOC (id,codgar,codimp,codice,idrichiesta,dtrichiesta,statorichiesta,datirichiesta) values (?,?,?,?,?,?,?,?)",
              new Object[]{new Long(id),codgar,codimp,codice,idrichiesta,new Timestamp(System.currentTimeMillis()), "1",body.toString()});
    }

    return resp;
  }

  /** chiamata per verificare l'esito della richiesta di documentazione FVOE **/
  public JSONObject serviceStatoRichiestaDocumentoFVOE(Map<String,Object> parameters) throws GestoreException, SQLException {

    parameters=getFVOEParametersFromDB(parameters);

    Client client = (Client) parameters.get(CLIENT);

    JSONObject resp = new JSONObject();
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/fvc/verifica-stato-richiesta-documento");

    WebTarget webTarget = getWebTarget(client, builderMap);
    MultivaluedMap <String,Object> headers = getHeaders(parameters);

    String idfvoe = (String) parameters.get(IDFVOE);
    String codgar = (String) parameters.get(CODGAR);
    String codimp = (String) parameters.get(CODIMP);
    Map<String,String> parametersMap = new HashMap<String,String>();

    String select = "select idrichiesta,codimp from g1fvoedoc where 1=1";
    List<Object> params = new ArrayList<>();
    if(StringUtils.isNotBlank(idfvoe)){
      select+=" and id=?";
      params.add(idfvoe);
    }else {
      select += " and statorichiesta in ('1','2','3') and codgar=? and codimp=?";
      params.add(codgar);
      params.add(codimp);
    }

    JSONObject respAcc = new JSONObject();
    List<Vector> dati = sqlManager.getListVector(select, params.toArray());

    if(dati==null || dati.isEmpty()){
      resp.put(PG_ERRORS,"Nessuna richiesta da verificare");
      return resp;
    }
    if(StringUtils.isNotBlank(idfvoe)) {
      parametersMap.put("idRichiesta", SqlManager.getValueFromVectorParam(dati.get(0), 0).getStringValue());
    }else{
      parametersMap.put("idRichiesta", "Chiamata massiva, gli idRichiesta sono visibili nella response");
    }

    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+ parametersMap);
      enrichLogEventoFVOE(logEvento,codimp);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    boolean unicaOccorrenza=false;
    int occorrenzeAggiornate=0;
    int occorrenzeNonAggiornate=0;

    if(!dati.isEmpty()) {
      for (int i = 0; i < dati.size(); i++) {
        boolean aggiornata=false;
        String idRichiesta = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
        parametersMap.put("idRichiesta", idRichiesta);
        Builder builder = getBuilder(webTarget, parametersMap).headers(headers);
        Response response = builder.get();
        resp = response.readEntity(JSONObject.class);
        if (dati.size() > 1)
          respAcc.put("Response_" + idRichiesta, resp);

        if (Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
          JSONArray richiestaDoc = resp.getJSONObject("data").getJSONArray("result");
          Timestamp dtacquisizione = null;
          if (!richiestaDoc.isEmpty()) {
            for (int j = 0; j < richiestaDoc.size(); j++) {
              JSONObject documento = richiestaDoc.getJSONObject(j);
              String idrichiesta = documento.getString("idRichiesta");
              if (idrichiesta != null && idrichiesta.equals(idRichiesta)) {
                String iddocumento = null;
                if (documento.get("idDocumento") instanceof String) {
                  iddocumento = documento.getString("idDocumento");
                  dtacquisizione = new Timestamp(System.currentTimeMillis());
                }
                String stato = documento.getJSONObject(STATO).getString(CODICE);
                String statoTab2 = (String) sqlManager.getObject("select tab2tip from tab2 where tab2cod=? and tab2d1=?", new Object[]{"A1z31", stato});
                sqlManager.update("update G1FVOEDOC set iddocumento=?, statorichiesta=?,dtacquisizione=? where idrichiesta=?", new Object[]{iddocumento, statoTab2, dtacquisizione, idrichiesta});
                aggiornata=true;
              }
            }
          }
        }
        if(aggiornata)
          occorrenzeAggiornate++;
        else
          occorrenzeNonAggiornate++;
      }
      if(dati.size()==1) {
        respAcc=resp;
        unicaOccorrenza=true;
      }
    }
    if(!unicaOccorrenza)
          respAcc.put("done","Y");
    respAcc.put("occorrenzeProcessate", occorrenzeAggiornate);
    respAcc.put("occorrenzeConErrori", occorrenzeNonAggiornate);

    return respAcc;
  }

  /** chiamata per verificare l'esito della richiesta di documentazione FVOE **/
  public JSONObject serviceRecuperaDocumentoFVOE(Map<String,Object> parameters) throws GestoreException, SQLException, NoSuchAlgorithmException, IOException {

    parameters=getFVOEParametersFromDB(parameters);

    Client client = (Client) parameters.get(CLIENT);

    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/fvc/recupera-documento-fvoe");

    String idfvoedoc = (String) parameters.get(IDFVOE);

    Map<String,String> parametersMap = new HashMap<String,String>();
    Vector dati = sqlManager.getVector("select iddocumento, codimp from G1FVOEDOC where id=?", new  Object[]{idfvoedoc});
    String idDocumento = (String) SqlManager.getValueFromVectorParam(dati,0).getValue();
    String codimp=(String) SqlManager.getValueFromVectorParam(dati,1).getValue();
    parameters.put(CODIMP,codimp);
    parametersMap.put("idDocumento",idDocumento);

    Builder builder = getBuilder(client,builderMap,parametersMap).headers(getHeaders(parameters));

    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg("Parametri= "+ parametersMap);
      enrichLogEventoFVOE(logEvento,codimp);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    Response response = builder.get();

    resp = response.readEntity(JSONObject.class);

    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      JSONObject documento = resp.getJSONObject("data").getJSONObject("contenutoDocumento");
      String nomeFile = documento.getString("nomefile");
      String contenuto = documento.getString("contenuto");
      resp.put("file", contenuto);
      resp.put("nomeFile",nomeFile);
      documento.put("contenuto","Contenuto rimosso per privacy. Viene fornito l'hash");
      byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(contenuto);
      documento.put("hashContenuto",getImpronta(bytes));
    }

    return resp;
  }
  
  //############### GESTIONE UTENTI ###############
  /** chiamata per aggiungere un soggetto **/
  public JSONObject serviceAggiungiSoggetto(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    if(parameters.get(ID_FORM)!=null)//se nullo, la chiamata arriva dalla funzione modifica DRP!
      parameters=getParametersFromDB(parameters);
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/gestione-utenti/aggiungi-soggetto");
    
    Builder builder = getBuilder(client, builderMap,null).headers(getHeaders(parameters));
    
    JSONObject body = new JSONObject();
    
    String ruolo = "DRP";
    //probabilmente dovremo fare una seconda chiamata per definire lo stesso soggetto anche come DRP2
    
    body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
    String drp = parameters.get(CFDRP).toString().toUpperCase();
    if(StringUtils.isBlank(drp)){
      throw new GestoreException(
          "Il DRP selezionato non ha il codice fiscale valorizzato",
          "scambiodatiPCP.ws.drpCfValorizzato");
    }
    body.put(BODY_CF, drp);
    body.put(BODY_RUOLO, ruolo);
    body.put(BODY_DT_INIZIO, npaFunzioniManager.getStringDateFormat(new Date(System.currentTimeMillis())));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      if(parameters.get(ID_FORM)!=null)//se arriva dalla scheda appendo le info
        enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(body.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && AGGIORNAMENTO_ESEGUITO.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
      if(parameters.get(ID_FORM)!=null)
        updateStatoForm(DELEGA_SOGGETTO, parameters.get(ID_FORM));
      else {}//chiamata per le definizione del delegato da "modifica DRP"
        
    }
      
    return resp;
  }
  
  /** chiamata per eliminare un soggetto **/
  public JSONObject serviceEliminaSoggetto(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/gestione-utenti/elimina-soggetto");
    
    Builder builder = getBuilder(client, builderMap,null).headers(getHeaders(parameters));
    
    JSONObject body = new JSONObject();
    
    
    body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
    if(parameters.get(CFDRP)!=null)
      body.put(BODY_CF, parameters.get(CFDRP).toString().toUpperCase());
    if(parameters.get(CIG)!=null)
      body.put(CIG, parameters.get(CIG));
    if(parameters.get(BODY_RUOLO)!=null)
      body.put(BODY_RUOLO, parameters.get(BODY_RUOLO));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body.toString());
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(body.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO)) && RICH_ACQUISITA.equals(resp.getJSONObject("data").getString(DATA_DETAIL))) {
     //TODO
    }

    return resp;
  }
  
  /** chiamata per imporsi come RP di un appalto (o lotto) **/
  public JSONObject servicePresaCarico(Map<String,Object> parameters) throws GestoreException, SQLException {
    
    Client client = (Client) parameters.get(CLIENT);
    
    JSONObject resp = null;
    Map<String,String> builderMap = new HashMap<String,String>();
    builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
    builderMap.put(SERVICE_URL, "/protected/gestione-utenti/presa-carico");
    
    Builder builder = getBuilder(client, builderMap,null).headers(getHeaders(parameters));
    
    JSONObject body = new JSONObject();
    body.put(ID_APPALTO, parameters.get(ID_APPALTO_TORN));
    if(parameters.get(CIG)!=null)
      body.put(CIG, parameters.get(CIG));
    
    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setErrmsg(body+"\nCF RUP: "+parameters.get(CFRP)+"\nCF SA: "+parameters.get(SACF));
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    Response response = builder.post(Entity.json(body.toString()));
    
    resp = response.readEntity(JSONObject.class);
    
    if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily()) && !MODALITA_VERIFICA.equals(parameters.get(MODALITA_INVIO))) {
      String codgar = (String) sqlManager.getObject("select codgar from torn where idappaltonpa=?",new Object[]{parameters.get(ID_APPALTO_TORN)});
      List<Long> listaIdForm = sqlManager.getListVector("select idform from g1npascheda s, g1npaform f where s.idform=f.id and s.id_autoupdate is not null and f.codgar=?", new Object[]{codgar});
      parameters.put(ScambioDatiPCPManager.AUTOUPDATE, "stop");
      for(int i=0;i<listaIdForm.size();i++){
        parameters.put(ScambioDatiPCPManager.ID_FORM,SqlManager.getValueFromVectorParam(listaIdForm.get(i),0).longValue());
        serviceHandleAutoupdate(parameters);
      }
    }
      
    return resp;
  }
  
//############### GESTIONE CENTRI DI COSTO ###############
  /** chiamata per confermare una scheda **/
  public JSONObject serviceAusaGetByCodAusa(Map<String,Object> parameters) throws GestoreException, SQLException {
    
      Client client = (Client) parameters.get(CLIENT);
      
      JSONObject resp = null;
      Map<String,String> builderMap = new HashMap<String,String>();
      builderMap.put(SERVICE_TYPE, SERVICE_TYPE_PROTECTED);
      builderMap.put(SERVICE_URL, "/protected/ausa/getBy");
      
      Vector values = sqlManager.getVector("select codausa,codausaanac,cfanac,cfein from uffint where codein=?",new Object[] {parameters.get(CODEIN)}); 
      String codausa = SqlManager.getValueFromVectorParam(values, 0).stringValue();
      String codausaCUC = SqlManager.getValueFromVectorParam(values, 1).stringValue();
      String cfANAC = SqlManager.getValueFromVectorParam(values, 2).stringValue();
      String cfein = SqlManager.getValueFromVectorParam(values, 3).stringValue();
      
      String cfUtilizzato = null;
      Boolean isFittizio = false;
      
      if(("2".equals(parameters.get("altrisog")) || "3".equals(parameters.get("altrisog")) || "4".equals(parameters.get("altrisog"))) && StringUtils.isNotBlank(cfANAC)) {
        if(StringUtils.isBlank(codausaCUC)) {
          resp=new JSONObject();
          resp.put("pgMessage", "Non risulta impostato il codice AUSA fittizio per la stazione appaltante scelta.");
          return resp;
        }else {
          codausa=codausaCUC;
          cfUtilizzato = cfANAC;
          isFittizio = true;
        }
      } else if ("4".equals(parameters.get("altrisog")) && StringUtils.isBlank(cfANAC)) {
    	  resp=new JSONObject();
          resp.put("pgMessage", "Non risulta impostato il codice fiscale fittizio per la stazione appaltante scelta.");
          return resp;
      } else {
    	  cfUtilizzato = cfein;
      }
      if(StringUtils.isBlank(codausa)) {
          resp=new JSONObject();
          resp.put("pgMessage", "Non risulta impostato il codice AUSA per la stazione appaltante scelta.");
          return resp;
      }
      
      Map<String,String> parametersMap = new HashMap<String,String>();
      parametersMap.put(CODAUSA,codausa);
      
      Builder builder = getBuilder(client, builderMap,parametersMap).headers(getHeaders(parameters));
      Response response = builder.get();
      
      resp = response.readEntity(JSONObject.class);
      
      resp.put("cfUtilizzato",cfUtilizzato);
      resp.put("codausaUtilizzato",codausa);
      if (isFittizio) {
    	  resp.put("fittizioANAC"," fittizio");
      } else {
    	  resp.put("fittizioANAC","");
      }
      
      return resp;
  }
  
  
  private void updateStatoAndIdScheda(Long stato, String idschedanpa, Map<String,Object> parameters) throws SQLException {
	  if(Long.valueOf(10).equals(stato)) {
		  Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		  sqlManager.update("update G1NPASCHEDA set statonpa=?, idschedanpa=?, datainvio = ?  where idform=?", new Object[] {stato,idschedanpa,timestamp,parameters.get(ID_FORM)});  
	  }else {
		  sqlManager.update("update G1NPASCHEDA set statonpa=?, idschedanpa=? where idform=?", new Object[] {stato,idschedanpa,parameters.get(ID_FORM)});
	  }
  }
  
  private void updateStatoForm(Long stato, Object idForm) throws SQLException {
	  if(Long.valueOf(10).equals(stato)) {
		  Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		  sqlManager.update("update G1NPASCHEDA set statonpa=?, datainvio = ? where idform=?", new Object[] {stato,timestamp,idForm});
	  }else {
		  sqlManager.update("update G1NPASCHEDA set statonpa=? where idform=?", new Object[] {stato,idForm});  
	  }
  }
  
  private void updateStatoFormAndSetIdAvviso(Long stato, Object idForm, Object idAvviso) throws SQLException {
    sqlManager.update("update G1NPASCHEDA set statonpa=?, idavvisonpa=? where idform=?", new Object[] {stato,idAvviso,idForm});
}
  
  public void updateUltimaResponse(String response, Object idForm) throws SQLException {
    sqlManager.update("update G1NPASCHEDA set WSRESPONSE=? where idform=?", new Object[] {response,idForm});
  }
  
  private void assegnaCIG(JSONArray cig, Map<String,Object> parameters) throws SQLException {
    String codgar=parameters.get(CODGAR).toString();
    for(int i=0;i<cig.size();i++) {
      String codcig= cig.getJSONObject(i).getString(CIG);
      String lotIdentifier= cig.getJSONObject(i).getString(LOT_IDENTIFIER).split("LOT-")[1];
      String codiga = StringUtils.stripStart(lotIdentifier,"0");
      if(codgar.indexOf("$")==0)
        sqlManager.update("update gare set codcig=? where codgar1=?", new Object[] {codcig, codgar});
      else
        sqlManager.update("update gare set codcig=? where codgar1=? and codiga=?", new Object[] {codcig,codgar, codiga});
      sqlManager.update("update gare set dacqcig=? where codgar1=?", new Object[] {parameters.get(DATACONF), codgar});
    }
  }
  
  private void sbiancaCIG(String codgar) throws SQLException {
    sqlManager.update("update gare set dacqcig=null, codcig=null where codgar1=?", new Object[] {codgar});
  }
  
  private void sbiancaIdAppalto(String codgar) throws SQLException {
    sqlManager.update("update TORN set idappaltonpa=null,coddrp=null where codgar=?", new Object[] {codgar});
  }
  
  private void sbiancaIdPiano(String codgar) throws SQLException {
    sqlManager.update("update TORN set idpianinpa=null,coddrp=null where codgar=?", new Object[] {codgar}); 
  }
  
  private void sbiancaDGUE(String codgar, Object idForm) {
    try {
      String ngara = null;
      if (codgar.charAt(0) == '$')
        ngara = codgar.substring(1);

      sqlManager.update("delete from documgara where codgar=? and idstampa='DGUE' and (statodoc is null or statodoc <> 5)", new Object[]{codgar});

      Vector objects = sqlManager.getVector("select idprg_espd, iddoc_espd from G1NPASCHEDA where idform=?",
              new Object[]{idForm});
      String espd_idprg = SqlManager.getValueFromVectorParam(objects, 0).getStringValue();
      Long espd_iddoc = (Long) SqlManager.getValueFromVectorParam(objects, 1).getValue();

      Long count = (Long) sqlManager.getObject("select count(*) from documgara where idprg=? and iddocdg=? and idstampa='DGUE' and codgar=?", new Object[]{espd_idprg, espd_iddoc, codgar});
      if (count == 0){
        sqlManager.update("update w_docdig set digent='G1NPASCHEDA', digkey1=?, digkey2=null where idprg=? and iddocdig=?", new Object[]{idForm.toString(), espd_idprg, espd_iddoc.toString()});
      }
      pgManager.updateImprdocgDaDocumgara(codgar, ngara);
      
    }catch(Exception e) {
      logger.error("Errore nell'eliminazione dei record con idstampa 'DGUE' in documgara");
    }
    
  }

  public void enrichLogevento(LogEvento le, Map<String,Object> parameters) {
    Long idNpaScheda= -1L;
    try {
      idNpaScheda = (Long) sqlManager.getObject("select id from g1npascheda where idform=?", 
          new Object[] {parameters.get(ID_FORM)});
    } catch (SQLException e) {
      logger.error("Errore nel recupero di G1NPASCHEDA.ID (G1NPASCHEDA.IDFORM="+parameters.get(ID_FORM)+")", e);
    }
    le.setDescr(le.getDescr()+" ("+parameters.get(NOME_SCHEDA)+", id. "+idNpaScheda+
            (parameters.get("cfUtenteMRP")!=null?(", CF utente "+parameters.get("cfUtenteMRP")):"") +")");
  }

  public void enrichLogEventoFVOE(LogEvento le, String codimp){
    le.setDescr(le.getDescr()+" (Codimp: "+codimp+")");
  }
  
  /**
   * 
   * @param errorsArray
   * @param error
   * @return true se l'errore è presente, false altrimenti
   */
  private boolean checkHasErrore(JSONObject errorsArray, String error) {
    try {
      return errorsArray.getJSONArray("errori").size()>0 && error.equals(errorsArray.getJSONArray("errori").getJSONObject(0).get("codice"));
    }catch(Exception e) {
      return false;
    }
  }

  private void estraiArrayErrori(JSONObject resp, JSONObject objErr) { 
    JSONArray errori=new JSONArray();
    try {
      errori=objErr.getJSONArray("errori");
      if(errori.size()==0)
        throw new Exception("Errore");
    }catch(Exception e) {
      JSONObject errore = new JSONObject();
      errore.put("codice", "GEN");
      errore.put("dettaglio","Il servizio ANAC ha risposto con errore, ma senza specificare la tipologia. Eliminare e riprovare. Qualora il problema si ripresentasse, contattare l'assistenza.");
      errori.add(errore); 
    }
    resp.put(ANAC_ERRORS,errori);//porto in cima gli errori
  }

	private void inserisciPubblicazioni(JSONObject resp, String codiceGara, Object idForm) throws SQLException, GestoreException {

		Vector<?> datiScheda = sqlManager.getVector("select cf.eform, cf.oggetto, cf.nome_scheda, g1npascheda.idschedanparet from g1npaform,g1cf_npa cf,g1npascheda where g1npaform.id=? and g1npaform.idcfnpa = cf.id and g1npaform.id=g1npascheda.idform", new Object[] {idForm});
		String eform = ((JdbcParametro) datiScheda.get(0)).getStringValue();
		String oggetto = ((JdbcParametro) datiScheda.get(1)).getStringValue();
		String nomeScheda = ((JdbcParametro) datiScheda.get(2)).getStringValue();
		String idschedanparet = ((JdbcParametro) datiScheda.get(3)).getStringValue();

		Boolean isRettifica = StringUtils.isNotEmpty(idschedanparet);

		JSONObject datiPubblicazioneEU = resp.getJSONObject("data").getJSONObject(AVVISO).getJSONObject(DATI_PUBBLICAZIONE_EUROPA);
		JSONObject datiPubblicazioneIT = resp.getJSONObject("data").getJSONObject(AVVISO).getJSONObject(DATI_PUBBLICAZIONE_ITALIA);

		if (StringUtils.isNotEmpty(eform) && datiPubblicazioneEU != null && !datiPubblicazioneEU.isNullObject() && !datiPubblicazioneEU.isEmpty() && !(datiPubblicazioneEU.getJSONObject(STATO).isNullObject()) && !(datiPubblicazioneEU.getJSONObject(STATO).isEmpty())) {
			String statoPubblicazione = datiPubblicazioneEU.getJSONObject(STATO).getString(CODICE);
			resp.put("statoPubbEU", statoPubblicazione);
			if (STATO_PUBB.equals(statoPubblicazione)) {
				inserisciPubblicazione(resp, codiceGara, idForm, datiPubblicazioneEU, true, eform, oggetto, nomeScheda, isRettifica);
			}
		}
		if (datiPubblicazioneIT != null && !datiPubblicazioneIT.isNullObject() && !datiPubblicazioneIT.isEmpty() && !(datiPubblicazioneIT.getJSONObject(STATO).isNullObject()) && !(datiPubblicazioneIT.getJSONObject(STATO).isEmpty())) {
			String statoPubblicazione = datiPubblicazioneIT.getJSONObject(STATO).getString(CODICE);
			resp.put("statoPubbIT", statoPubblicazione);
			if (STATO_PUBB.equals(statoPubblicazione)) {
				inserisciPubblicazione(resp, codiceGara, idForm, datiPubblicazioneIT, false, null, oggetto, nomeScheda, isRettifica);
			}
		}
	}

	private void inserisciPubblicazione(JSONObject resp, String codgar, Object idForm, JSONObject datiPubblicazione, Boolean isPubblicazioneEuropea,
			String eform, String oggetto, String nomeScheda, Boolean isRettifica) throws SQLException, GestoreException {
		Boolean isBando = "P".equals(nomeScheda.substring(0, 1));
		Boolean isEsito = "A".equals(nomeScheda.substring(0, 1));

		String dataInoltroPubblicazione = datiPubblicazione.getString(DATA_INOLTRO_PUBBLICAZIONE);
		String dataPubblicazione = datiPubblicazione.getString(DATA_PUBBLICAZIONE);
		String pubblicationID = null;
		String pubblicationUrl = null;

		String titoloPubb = null;
		String idAvvisoPVL = null;
		Long tippub;
		Long isInseritaInPUBBLI = null;
		Long isInseritaInPUBG = null;

		Long genere = null;

		String ngara = null;
		if (codgar != null && codgar.startsWith("$")) {
			ngara = codgar.substring(1);
		} else {
			List<Vector<?>> datiGenere = (List<Vector<?>>) sqlManager.getListVector("select codgar, codice, genere from v_gare_genere where codgar=? order by genere asc", new Object[] {codgar});
			if (datiGenere != null && datiGenere.size() > 1) {
				genere = ((JdbcParametro) (datiGenere.get(0).get(2))).longValue();
				if (genere != null && (genere).equals(3L)) {
					ngara = ((JdbcParametro) (datiGenere.get(0).get(1))).getStringValue();
				} else if (genere != null && (genere).equals(1L)) {
					ngara = ((JdbcParametro) (datiGenere.get(1).get(1))).getStringValue();
				}
			}
		}
		String codiceGara = null;

		if (isPubblicazioneEuropea) {
			tippub = 3L;
			titoloPubb = "eForm " + eform +  " - ";
			if (isRettifica) {
				titoloPubb += "Rettifica ";
			}
			titoloPubb += oggetto;
			pubblicationID = datiPubblicazione.getString(PUBBLICATION_ID);
			pubblicationUrl = datiPubblicazione.getString(PUBBLICATION_URL);
			if (isBando) {
				isInseritaInPUBBLI = (Long) sqlManager.getObject("select count(*) from pubbli where navpub=?", new Object[] {pubblicationID});
			} else if (isEsito) {
				isInseritaInPUBG = (Long) sqlManager.getObject("select count(*) from pubg where navpub=?", new Object[] {pubblicationID});
			}

            boolean attivoFEU = StringUtils.isNotEmpty(ConfigManager.getValore("it.eldasoft.bandoavvisosimap.ws.url"));

			resp.put("navpub", pubblicationID);
            String select = "update g1npascheda set ispubted='1' where idform=?";
            if (attivoFEU)
              select += " and uuid_eform is null";
            sqlManager.update(select, new Object[] {idForm}); //update eseguita solo nel caso di assenza di integrazione con FEU
			
		} else {
			tippub = 17L;
			titoloPubb = nomeScheda +  " - ";
			if (isRettifica) {
				titoloPubb += "Rettifica ";
			}
			titoloPubb += oggetto;
			idAvvisoPVL = datiPubblicazione.getString(ID_AVVISO_PVL);

			if (StringUtils.isEmpty(ConfigManager.getValore(PROP_URL_BASE_PVL))) {
				throw new GestoreException("Valorizzare la property: " + PROP_URL_BASE_PVL, null);
			}
			pubblicationUrl = ConfigManager.getValore(PROP_URL_BASE_PVL);
			if (pubblicationUrl.endsWith("/")) {
				pubblicationUrl = pubblicationUrl.substring(0, pubblicationUrl.length() - 1);
			}
			if (isEsito) {
				isInseritaInPUBG = (Long) sqlManager.getObject("select count(*) from pubg where urlpub like '%" + idAvvisoPVL + "'", null);

				pubblicationUrl += "/esiti/";
			} else if (isBando) {
				isInseritaInPUBBLI = (Long) sqlManager.getObject("select count(*) from pubbli where urlpub like '%" + idAvvisoPVL + "'", null);

				if (nomeScheda.startsWith("PL") || (!"P7_2".equals(nomeScheda) && nomeScheda.startsWith("P7"))) {
					pubblicationUrl += "/avvisi/";
				} else {
					pubblicationUrl += "/bandi/";
				}
			}
			pubblicationUrl += idAvvisoPVL;
            sqlManager.update("update g1npascheda set ispubpvl='1' where idform=?", new Object[] {idForm});
		}

		if ((!isBando && !isEsito) || (isInseritaInPUBBLI != null && isInseritaInPUBBLI > 0) || (isInseritaInPUBG != null && isInseritaInPUBG > 0)) {
			return;
		}

		String desc = tabellatiManager.getDescrTabellato("A1108", "1");
		if (desc != null && !"".equals(desc))
			desc = desc.substring(0, 1);
		boolean gestioneUrl = false;
		if ("1".equals(desc))
			gestioneUrl = true;

		String entita = null, nomeCampoChiave = null, nomeCampoChiaveNumerica = null;

		Vector<DataColumn> elencoCampi = new Vector<DataColumn>();

		if (isBando) {
			entita = "PUBBLI";
			nomeCampoChiave = "CODGAR9";
			nomeCampoChiaveNumerica = "NUMPUB";
			codiceGara = codgar;

			elencoCampi.add(new DataColumn(entita + "." + "CODGAR9", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
			elencoCampi.add(new DataColumn(entita + "." + "NUMPUB", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
			elencoCampi.add(new DataColumn(entita + "." + "TIPPUB", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tippub)));
            if (StringUtils.isNotEmpty(dataInoltroPubblicazione)) {
              elencoCampi.add(new DataColumn(entita + "." + "DINPUB", new JdbcParametro(JdbcParametro.TIPO_DATA_FULL, getDataFromString(dataInoltroPubblicazione))));
            }
			elencoCampi.add(new DataColumn(entita + "." + "DATPUB", new JdbcParametro(JdbcParametro.TIPO_DATA_FULL, getDataFromString(dataPubblicazione))));
			elencoCampi.add(new DataColumn(entita + "." + "URLPUB", new JdbcParametro(JdbcParametro.TIPO_TESTO, pubblicationUrl)));
			elencoCampi.add(new DataColumn(entita + "." + "TITPUB", new JdbcParametro(JdbcParametro.TIPO_TESTO, titoloPubb)));

			if (isPubblicazioneEuropea) {
				elencoCampi.add(new DataColumn("PUBBLI.NAVPUB", new JdbcParametro(JdbcParametro.TIPO_TESTO, pubblicationID)));
			}
		} else if (isEsito) {
			entita = "PUBG";
			nomeCampoChiave = "NGARA";
			nomeCampoChiaveNumerica = "NPUBG";
			codiceGara = ngara;

			elencoCampi.add(new DataColumn(entita + "." + "NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
			elencoCampi.add(new DataColumn(entita + "." + "NPUBG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
			elencoCampi.add(new DataColumn(entita + "." + "TIPPUBG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tippub)));
            if (StringUtils.isNotEmpty(dataInoltroPubblicazione)) {
              elencoCampi.add(new DataColumn(entita + "." + "DINVPUBG", new JdbcParametro(JdbcParametro.TIPO_DATA_FULL, getDataFromString(dataInoltroPubblicazione))));
            }
			elencoCampi.add(new DataColumn(entita + "." + "DINPUBG", new JdbcParametro(JdbcParametro.TIPO_DATA_FULL, getDataFromString(dataPubblicazione))));
			elencoCampi.add(new DataColumn(entita + "." + "URLPUB", new JdbcParametro(JdbcParametro.TIPO_TESTO, pubblicationUrl)));
			elencoCampi.add(new DataColumn(entita + "." + "TITPUB", new JdbcParametro(JdbcParametro.TIPO_TESTO, titoloPubb)));

			if (isPubblicazioneEuropea) {
				elencoCampi.add(new DataColumn("PUBG.NAVPUB", new JdbcParametro(JdbcParametro.TIPO_TESTO, pubblicationID)));
			}
		}
		DataColumnContainer container = new DataColumnContainer(elencoCampi);

		Long maxNumPub = (Long) sqlManager.getObject("select max(" + nomeCampoChiaveNumerica + ") from " + entita + " where " + nomeCampoChiave + "=?", new Object[] { codiceGara });
		if (maxNumPub == null) {
			maxNumPub = 0L;
		}
		Long numPub = maxNumPub + 1;

		container.setValue(entita + "." + nomeCampoChiaveNumerica, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numPub));
		container.insert(entita, sqlManager);

		if (gestioneUrl) {
			entita = "DOCUMGARA";
			nomeCampoChiave = "CODGAR";
			nomeCampoChiaveNumerica = "NORDDOCG";
			Long gruppo = isBando ? 1L : 4L;
			Long maxNorddocg = (Long) sqlManager.getObject("select max(" + nomeCampoChiaveNumerica + ") from " + entita + " where " + nomeCampoChiave + "=?", new Object[] { codgar });
			if (maxNorddocg == null) {
				maxNorddocg = 0L;
			}
			Long norddocg = maxNorddocg + 1;

			nomeCampoChiaveNumerica = "NUMORD";
			Long maxNumord = (Long) sqlManager.getObject("select max(" + nomeCampoChiaveNumerica + ") from " + entita + " where " + nomeCampoChiave + "=? and gruppo=?", new Object[] { codgar, gruppo });
			if (maxNumord == null) {
				maxNumord = 0L;
			}
			Long numord = maxNumord + 1;

			Long tipologia = null;
			List<?> datiCfPubb = sqlManager.getListVector("select id, cl_where_vis, cl_where_ult from g1cf_pubb where gruppo=? and id > 100 and id <> 108", new Object[] { gruppo });
			for (int i = 0; i < datiCfPubb.size(); i++) {
			  Long tipologiaDaControllare = SqlManager.getValueFromVectorParam(datiCfPubb.get(i), 0).longValue();
				String clausolaWhereVis = (String) SqlManager.getValueFromVectorParam(datiCfPubb.get(i), 1).getValue();
				String clausolaWhereUlt = (String) SqlManager.getValueFromVectorParam(datiCfPubb.get(i), 2).getValue();
				if (clausolaWhereVis != null && !clausolaWhereVis.equals("")) {
					clausolaWhereVis = " and (" + clausolaWhereVis + ")";
				}
				if (clausolaWhereUlt != null && !clausolaWhereUlt.equals("")) {
					clausolaWhereVis = clausolaWhereVis + " and (" + clausolaWhereUlt + ")";
				}
				String selectVisibile = "select count(*) from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=? ";
				Long countVisibile = (Long) sqlManager.getObject(selectVisibile + clausolaWhereVis,
						new Object[] { codgar });
				if (countVisibile.intValue() > 0) {
					tipologia = tipologiaDaControllare;
					break;
				}
			}
			if (tipologia == null) {
				tipologia = isBando ? 103L : 110L;
			}

			elencoCampi = new Vector<DataColumn>();
			elencoCampi.add(new DataColumn(entita + "." + "CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
			if (!(((Long) 1L).equals(genere) || ((Long) 3L).equals(genere)) && StringUtils.isNotEmpty(ngara))
				elencoCampi.add(new DataColumn(entita + "." + "NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
			elencoCampi.add(new DataColumn(entita + "." + "NORDDOCG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, norddocg)));
			elencoCampi.add(new DataColumn(entita + "." + "NUMORD", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numord)));
			elencoCampi.add(new DataColumn(entita + "." + "GRUPPO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, gruppo)));
			elencoCampi.add(new DataColumn(entita + "." + "TIPOLOGIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tipologia)));
			elencoCampi.add(new DataColumn(entita + "." + "DESCRIZIONE", new JdbcParametro(JdbcParametro.TIPO_TESTO, titoloPubb)));
			elencoCampi.add(new DataColumn(entita + "." + "URLDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, pubblicationUrl)));

			String selectPubblicazionePortale = null;
			Object[] parametriCount = null;
			if (isBando) {
				selectPubblicazionePortale = "select codgar9, datpub from pubbli where codgar9=? and tippub in (11,13,23)";
				parametriCount = new Object[] { codgar };
			}
			else if (isEsito) {
				selectPubblicazionePortale = "select ngara, dinpubg from pubg where ngara=? and tippubg=12";
				parametriCount = new Object[] { ngara };
			}

			Vector datiPubblicazionePortale = sqlManager.getVector(selectPubblicazionePortale, parametriCount);
			Boolean isPubblicataSuPortale = datiPubblicazionePortale != null && StringUtils.isNotEmpty(((JdbcParametro) datiPubblicazionePortale.get(0)).getStringValue());
			if (isPubblicataSuPortale) {
				elencoCampi.add(new DataColumn(entita + "." + "DATARILASCIO", (JdbcParametro) datiPubblicazionePortale.get(1)));
				elencoCampi.add(new DataColumn(entita + "." + "STATODOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 5L)));
			}
			container = new DataColumnContainer(elencoCampi);
			container.insert(entita, sqlManager);
		}
	}

	public static java.util.Date getDataFromString(String dataDaConvertire) throws GestoreException {
		try {
          OffsetDateTime offsetDateTime = OffsetDateTime.parse(dataDaConvertire);
          ZonedDateTime italianTime = offsetDateTime.atZoneSameInstant(ZoneId.of("Europe/Rome"));
          return Date.from(italianTime.toInstant());
		} catch (Exception e) {
			e.printStackTrace();
			throw new GestoreException("Errore nella conversione della data", null, e);
		}
	}
	
	private void updateTornFromG1nparet(Map<String,Object> parameters) throws GestoreException, SQLException {
	  if(parameters.get(ID_SCHEDA_NPA_RET)!=null) {
	    
	    String codgar=(String) parameters.get(CODGAR);
	    
	    DataColumnContainer campiDaCopiareG1NPARET = new DataColumnContainer(sqlManager,
	        "G1NPARET", "select dtepar,otepar,dteoff,oteoff,desoff,oesoff,dtermrichcdp,dtermrispcdp,dtermrichcpo,dtermrispcpo "
	            + " from g1nparet where idnpascheda=?", new Object[] {parameters.get(ID_G1NPASCHEDA)});
	    DataColumnContainer campiDaCopiareTORN = new DataColumnContainer(sqlManager,
            "TORN", "select dtepar,otepar,dteoff,oteoff,desoff,oesoff,dtermrichcdp,dtermrispcdp,dtermrichcpo,dtermrispcpo "
                + " from torn where codgar=?", new Object[] {codgar});

	    //inizio parte di aggiornamento in garrettif e rettifica verso le integrazioni
	    
	    String errMsgEvento=null;
	    String messageKey = null;
	    int livEvento = 3;
	    String descrTermineVisualizzato = "";
	    
	    int termineVisualizzatoIntero = 1;

        Object[] gestioni = new Object[] {termineVisualizzatoIntero};
        if(campiDaCopiareG1NPARET.getColumn("G1NPARET.DTEOFF").getOriginalValue().getValue()!=null || campiDaCopiareG1NPARET.getColumn("G1NPARET.DESOFF").getOriginalValue().getValue()!=null) {
          List<Object> listaGestione = new ArrayList<Object>();
          if(campiDaCopiareG1NPARET.getColumn("G1NPARET.DTEOFF").getOriginalValue().getValue()!=null)
            listaGestione.add(2);//gestisco 2 solo se è presente ed è presente DTEOFF
          if(campiDaCopiareG1NPARET.getColumn("G1NPARET.DESOFF").getOriginalValue().getValue()!=null)
            listaGestione.add(3);//gestisco 3 solo se è presente ed è variato DESOFF
          gestioni=listaGestione.toArray();
        }
        
        for(int i=0;i<gestioni.length;i++) {
          termineVisualizzatoIntero = (int) gestioni[i];

    	    try {
    
    	      Timestamp data = null;
    	      Timestamp data_orig = null;
    	      String ora = null;
    	      String ora_orig = null;
    	      Timestamp data1 = null;
    	      Timestamp data1_orig = null;
    	      Timestamp data2 = null;
    	      Timestamp data2_orig = null;
    
    	      HashMap<String,Object> hMap = new HashMap<String,Object>();
    	      switch (termineVisualizzatoIntero) {
    	      case 1:
    	        //Termini presentazione domanda di partecipazione
    	        descrTermineVisualizzato = "Rettifica termini di presentazione domanda di partecipazione";
    	        data = campiDaCopiareG1NPARET.getData("G1NPARET.DTEPAR");
    	        data_orig = campiDaCopiareTORN.getData("TORN.DTEPAR");
    	        ora = campiDaCopiareG1NPARET.getString("G1NPARET.OTEPAR");
    	        ora_orig = campiDaCopiareTORN.getString("TORN.OTEPAR");
    	        data1 = campiDaCopiareG1NPARET.getData("G1NPARET.DTERMRICHCDP");
    	        data1_orig = campiDaCopiareTORN.getData("TORN.DTERMRICHCDP");
    	        data2 =campiDaCopiareG1NPARET.getData("G1NPARET.DTERMRISPCDP");
    	        data2_orig =campiDaCopiareTORN.getData("TORN.DTERMRISPCDP");
    	        hMap.put("DTEPAR",data);
    	        break;
    	      case 2:
    	        // Termini presentazione domanda di offerta
    	        descrTermineVisualizzato = "Rettifica termini di presentazione dell' offerta";
    	        data = campiDaCopiareG1NPARET.getData("G1NPARET.DTEOFF");
    	        data_orig = campiDaCopiareTORN.getData("TORN.DTEOFF");
    	        ora = campiDaCopiareG1NPARET.getString("G1NPARET.OTEOFF");
    	        ora_orig = campiDaCopiareTORN.getString("TORN.OTEOFF");
    	        data1 = campiDaCopiareG1NPARET.getData("G1NPARET.DTERMRICHCPO");
    	        data1_orig = campiDaCopiareTORN.getData("TORN.DTERMRICHCPO");
    	        data2 =campiDaCopiareG1NPARET.getData("G1NPARET.DTERMRISPCPO");
    	        data2_orig =campiDaCopiareTORN.getData("TORN.DTERMRISPCPO");
    	        hMap.put("DTEOFF",data);
    	        break;
    	      case 3:
    	        //Temini apertura plichi
    	        descrTermineVisualizzato = "Rettifica termini di apertura dei plichi";
    	        data = campiDaCopiareG1NPARET.getData("G1NPARET.DESOFF");
    	        data_orig = campiDaCopiareTORN.getData("TORN.DESOFF");
    	        ora = campiDaCopiareG1NPARET.getString("G1NPARET.OESOFF");
    	        ora_orig = campiDaCopiareTORN.getString("TORN.OESOFF");
    	        break;
    	      }
    
    	      String bandoPubblicato = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar,"BANDO11",false);
    	      String invitoPubblicato = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar,"BANDO13",false);
    
    	      Vector dati = sqlManager.getVector("select iterga, genere from v_gare_torn, torn where v_gare_torn.codgar=torn.codgar and torn.codgar=?", new Object[] {codgar});
    
    	      hMap.put("iterga",Long.toString(SqlManager.getValueFromVectorParam(dati, 0).longValue()));
    	      hMap.put("bandoPubblicato",bandoPubblicato);
    	      hMap.put("invitoPubblicato",invitoPubblicato);
    	      hMap.put("termineVisualizzatoIntero",termineVisualizzatoIntero);
    	      hMap.put("codgar",codgar);
    	      hMap.put("tipoGara",Long.toString(SqlManager.getValueFromVectorParam(dati, 1).longValue()));
    	      hMap.put("data",data); 
    	      hMap.put("data_orig",data_orig);
    	      hMap.put("ora",ora);
    	      hMap.put("ora_orig",ora_orig);
    	      hMap.put("data1",data1);
    	      hMap.put("data2",data2);
    	      hMap.put("genere",Long.toString(SqlManager.getValueFromVectorParam(dati, 1).longValue()));

    	      pgManagerEst1.gestioneRettificheIntegrazioni(hMap);
    
    	      String updateTorn=(String)hMap.get("updateTorn");
    	      Object[] parametri=(Object[])hMap.get("parametri");
    	      messageKey=(String)hMap.get("messageKey");
    	      errMsgEvento=(String)hMap.get("errMsgEvento");
    	      if(hMap.get("exception")!=null)
                throw (GestoreException) hMap.get("exception");
    
    	      this.sqlManager.update(updateTorn, parametri);
    
    	      String insert="insert into garrettif(ID,CODGAR,TIPTER,DATTER,ORATER,DRICHC,DRISPC,DRETTIF) values(?,?,?,?,?,?,?,?)";
    
    	      Long id = new Long(genChiaviManager.getNextId("GARRETTIF"));
    	      java.util.Date oggi = UtilityDate.getDataOdiernaAsDate();
    	      this.sqlManager.update(insert, new Object[]{id, codgar, termineVisualizzatoIntero, data_orig, ora_orig, data1_orig, data2_orig, new Timestamp(oggi.getTime())});
    
    	      String pubblicazioneBandoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "BANDO", false);
    	      String pubblicazioneEsitoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "ESITO", true);
    	      if("TRUE".equals(pubblicazioneBandoPortale) || "TRUE".equals(pubblicazioneEsitoPortale)){
    	        this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{oggi,codgar});
    	      }
    	      livEvento = 1 ;
    	      errMsgEvento = "";
    	    } catch (SQLException e) {
    	      throw new GestoreException("Errore nella storicizzazione della rettifica dei termini di gara", null, e);
    	    }finally{
    	      //Tracciatura eventi
    	      try {
    	        LogEvento le = (LogEvento)parameters.get(LOG_EVENTO_REQ);
    	        LogEvento logEvento =new LogEvento();
    	        logEvento.setCodApplicazione(le.getCodApplicazione());
    	        logEvento.setCodProfilo(le.getCodProfilo());
    	        logEvento.setIdUtente(le.getIdUtente());
    	        logEvento.setIp(le.getIp());
    	        logEvento.setLivEvento(livEvento);
    	        logEvento.setOggEvento(le.getOggEvento());
    	        logEvento.setCodEvento("GA_RETTIFICA_TERMINI");
    	        logEvento.setDescr(descrTermineVisualizzato);
    	        logEvento.setErrmsg(errMsgEvento);
    	        LogEventiUtils.insertLogEventi(logEvento);
    	      } catch (Exception le) {
    	        messageKey = "errors.logEventi.inaspettataException";
    	        logger.error(ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE).getString(messageKey), le);
    	      }
    	    }
    	  }
    	}
	}

	private String getCodtecInviante(Map<String,Object> params) throws SQLException, GestoreException {//valorizza coddrp col codtec del tecnico con lo stesso CF. Se non presente in db, lo si aggiunge
	  
	  String codgar= params.get(CODGAR).toString();
	  HttpSession session = (HttpSession) params.get(SESSION);
	 
	  ProfiloUtente profilo = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
	  String cf = profilo.getCodiceFiscale();
	  String sysute = profilo.getNome();
	  String cogtei = "";
	  String nometei = "";
	  if(StringUtils.isNotBlank(sysute)) {
	   int splitter = sysute.trim().indexOf(" ");
	    cogtei=sysute;
	    if(splitter>-1){
	      cogtei=sysute.substring(0,splitter);
	      nometei=sysute.substring(splitter+1);
	    }
	  }
	  String codtec="";
	  
	  //creo un record in tecni per il rup, se non ce l'ha
      String archiviFiltrati = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata.archiviFiltrati");
      boolean tecniFiltrato = (archiviFiltrati.indexOf("TECNI") != -1);
      if(tecniFiltrato) {
        String uffint = (String) session.getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
        if(StringUtils.isBlank(uffint)){ //se è nullo, lo preleviamo dalla gara
          uffint = (String) this.sqlManager.getObject("select cenint from TORN where codgar= ?", new Object [] {codgar});
        }
        codtec = (String) this.sqlManager.getObject("select codtec from TECNI where CFTEC = ? AND CGENTEI = ? order by codtec", new Object [] {cf,uffint});
        if(StringUtils.isBlank(codtec)){
          if (geneManager.isCodificaAutomatica("TECNI", "CODTEC"))
            codtec = this.geneManager.calcolaCodificaAutomatica("TECNI","CODTEC");
          else
            throw new GestoreException("Non è stato trovato alcun tecnico con codice fiscale '"+cf+"' per la stazione appaltante. Crearlo manualmente o attivare la codifica automatica.", null);
        sqlManager.update("insert into TECNI (codtec, nomtec, cftec, cgentei, cogtei, nometei) values (?,?,?,?,?,?)", new Object [] {codtec,sysute,cf,uffint,cogtei,nometei});
        }
      }else {
        codtec = (String)  this.sqlManager.getObject("select codtec from TECNI where CFTEC = ? order by codtec", new Object [] {cf});
        if(StringUtils.isBlank(codtec)){
          if (geneManager.isCodificaAutomatica("TECNI", "CODTEC"))
            codtec = this.geneManager.calcolaCodificaAutomatica("TECNI","CODTEC");
          else
            throw new GestoreException("Non è stato trovato alcun tecnico con codice fiscale '"+cf+"'. Crearlo manualmente o attivare la codifica automatica.", null);
          this.sqlManager.update("insert into TECNI (codtec, nomtec, cftec, cogtei, nometei) values (?,?,?,?,?)", new Object [] {codtec,sysute,cf,cogtei,nometei});
        }
      }
      return codtec;
	}

    public void updateFromPannelloPCP(HttpServletRequest request, String codgar, String idForm) throws Exception {
      String coddrp = request.getParameter("coddrp");
      String idAppalto = request.getParameter("idAppalto");
      String idAvviso = request.getParameter("idAvviso");
      String idPiano = request.getParameter("idPiano");
      String idScheda = request.getParameter("idScheda");
      String statonpa = request.getParameter("statonpa");
      String dataconf = request.getParameter("dataconf");

      List<String> queryEseguite = new ArrayList<>();
      String messaggioModifica = "";
      if (StringUtils.isNotBlank(statonpa)) {//l'update è vincolato a una transizione di stato
      //UPDATE su TORN solo in presenza di almeno un campo da aggiornare (coddrp, idAppalto, idPiano) e stato 10
        messaggioModifica="Stato scheda modificato da '"+getStatoScheda(Long.parseLong(idForm))+"'";
      if ("10".equals(statonpa) && (StringUtils.isNotBlank(coddrp) || StringUtils.isNotBlank(idAppalto) || StringUtils.isNotBlank(idPiano))) {
        String updateTorn = "update TORN set ";
        String whereTorn = " where CODGAR=?";
        List<String> campiTorn2Update = new ArrayList<>();
        List<Object> valoriTorn2Update = new ArrayList<>();
        if (StringUtils.isNotBlank(idAppalto)) {
          campiTorn2Update.add("IDAPPALTONPA=?");
          valoriTorn2Update.add(idAppalto);
        }
        if (StringUtils.isNotBlank(idPiano)) {
          campiTorn2Update.add("IDPIANINPA=?");
          valoriTorn2Update.add(idPiano);
        }
        if (StringUtils.isNotBlank(coddrp)) {
          campiTorn2Update.add("CODDRP=?");
          valoriTorn2Update.add(coddrp);
        }
        if(valoriTorn2Update.size()>0) {
          valoriTorn2Update.add(codgar);
          String sql = updateTorn + String.join(",", campiTorn2Update) + whereTorn;
          this.sqlManager.update(sql, valoriTorn2Update.toArray());

          for (int i = 0; i < valoriTorn2Update.size(); i++) {
            sql = StringUtils.replaceOnce(sql, "?", valoriTorn2Update.get(i).toString());
          }
          queryEseguite.add(sql);
        }
      }
      //UPDATE SU GARE
      if ("30".equals(statonpa)) {
        String updateGare = "update GARE set ";
        String whereGare = " where CODGAR1=?";
        List<String> campiGareUpdate = new ArrayList<>();
        List<Object> valoriGare2Update = new ArrayList<>();
        if (StringUtils.isNotBlank(dataconf)){
          campiGareUpdate.add("DACQCIG=?");
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
          java.util.Date parsedDate = dateFormat.parse(dataconf);
          Timestamp timestamp = new Timestamp(parsedDate.getTime());
          valoriGare2Update.add(timestamp);
        }
        if(campiGareUpdate.size()>0) {
          valoriGare2Update.add(codgar);
          String sql = updateGare + String.join(",", campiGareUpdate) + whereGare;
          this.sqlManager.update(sql, valoriGare2Update.toArray());

          for (int i = 0; i < valoriGare2Update.size(); i++) {
            sql = StringUtils.replaceOnce(sql, "?", valoriGare2Update.get(i).toString());
          }
          queryEseguite.add(sql);
        }
      }

      //UPDATE SU G1NPASCHEDA
      String updateG1NPASCHEDA = "update G1NPASCHEDA set ";
      String whereG1NPASCHEDA = " where IDFORM=?";
      List<String> campiG1NPASCHEDA2Update = new ArrayList<>();
      List<Object> valoriG1NPASCHEDA2Update = new ArrayList<>();

        campiG1NPASCHEDA2Update.add("STATONPA=?");
        valoriG1NPASCHEDA2Update.add(Long.valueOf(statonpa));

        if (StringUtils.isNotBlank(idAppalto)) {
          campiG1NPASCHEDA2Update.add("IDSCHEDANPA=?");
          valoriG1NPASCHEDA2Update.add(idAppalto);
        } else if (StringUtils.isNotBlank(idScheda)) {
          campiG1NPASCHEDA2Update.add("IDSCHEDANPA=?");
          valoriG1NPASCHEDA2Update.add(idScheda);
        }
        if (StringUtils.isNotBlank(idAvviso)) {
          campiG1NPASCHEDA2Update.add("IDAVVISONPA=?");
          valoriG1NPASCHEDA2Update.add(idAvviso);
        }
        if (StringUtils.isNotBlank(dataconf)) {
          campiG1NPASCHEDA2Update.add("DATACONF=?");
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
          java.util.Date parsedDate = dateFormat.parse(dataconf);
          Timestamp timestamp = new Timestamp(parsedDate.getTime());
          valoriG1NPASCHEDA2Update.add(timestamp);
        }

        valoriG1NPASCHEDA2Update.add(Long.valueOf(idForm));
        String sql = updateG1NPASCHEDA + String.join(",", campiG1NPASCHEDA2Update) + whereG1NPASCHEDA;
        this.sqlManager.update(sql, valoriG1NPASCHEDA2Update.toArray());

        for(int i=0;i<valoriG1NPASCHEDA2Update.size();i++){
          sql=StringUtils.replaceOnce(sql,"?",valoriG1NPASCHEDA2Update.get(i).toString());
        }
        queryEseguite.add(sql);

        if(codgar.startsWith("$")){
         codgar = codgar.substring(1);
        }

        messaggioModifica+=" a '"+getStatoScheda(Long.parseLong(idForm))+"'.";
        queryEseguite.add(0,messaggioModifica);

        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setCodEvento("NPA_UPDATE_DA_PANNELLO");
        logEvento.setLivEvento(1);
        logEvento.setDescr("Aggiornamento manuale scheda ANAC");
        logEvento.setOggEvento(codgar);
        logEvento.setErrmsg(String.join("\n- ",queryEseguite));
        HashMap<String,Object> hm = new HashMap<>();
        hm.put(ID_FORM,Long.valueOf(idForm));
        String nome_scheda = (String) sqlManager.getObject("select nome_scheda from g1cf_npa, g1npaform where g1npaform.idcfnpa=g1cf_npa.id and g1npaform.id=?",
                new Object[] {hm.get(ID_FORM)});
        hm.put(NOME_SCHEDA,nome_scheda);
        enrichLogevento(logEvento, hm);
        LogEventiUtils.insertLogEventi(logEvento);
      }


    }

    public String getStatoScheda(Long idform) throws SQLException {
      return (String) this.sqlManager.getObject("select tab1desc from g1npascheda, tab1 where tab1cod='A1291' and tab1tip=statonpa and idform=?", new Object[]{idform});
    }

  public String getDRPProcedura(String codgar) throws SQLException {
    return (String) this.sqlManager.getObject("select cftec from tecni, torn where coddrp=codtec and codgar=?", new Object[]{codgar});
  }
  public static boolean checkInternalError(JSONObject resp, String errorcode){
    boolean hasError = false;
      try {
        if(resp.containsKey("internalErrors")) {
          JSONArray ie = resp.getJSONArray("internalErrors");
          for (int i = 0; i < ie.size(); i++) {
            String err = ie.getString(i);
            if (StringUtils.isNotBlank(err) && err.indexOf(errorcode) == 0)
              hasError=true;
          }
        }
      } catch (Exception e) {
        logger.error(e);
      }
      return hasError;
  }

  /** chiamata per confermare l'appalto ASINCRONA **/
  public JSONObject serviceHandleAutoupdate(Map<String,Object> parameters) throws SQLException {

    JSONObject resp = new JSONObject();

    if("start".equals(parameters.get(AUTOUPDATE))) {
      String scadenza = (String) parameters.get("scadenzaSessione");
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      Timestamp timestamp = new Timestamp(System.currentTimeMillis()+5*24*60*60*1000); //di default il token scade dopo 5gg
      if(scadenza!=null) {
        try {
          java.util.Date parsedDate = dateFormat.parse(scadenza);
          timestamp = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
          logger.error("G1NPAAUTOUPDATE, setting del campo EXPIRATIONDATE: Errore nella conversione del campo scadenzaSessione. Sarà utilizzato l'istante attuale +5giorni.");
        }
      }
      String orario = StringUtils.rightPad(timestamp.getHours()+"",2,"0") + ":" + StringUtils.rightPad(timestamp.getMinutes()+"",2,"0");
      Long loa=Long.parseLong((String)parameters.get("loaMRP"));
      int id = genChiaviManager.getNextId("G1NPAAUTOUPDATE");
      sqlManager.update("insert into g1npaautoupdate (id,cfrup,provider,loa,expirationdate,expirationtime) values (?,?,?,?,?,?)",
      new Object[]{id,parameters.get("cfUtenteMRP"),parameters.get("idpMRP"),loa,timestamp,orario});
      sqlManager.update("update g1npascheda set id_autoupdate=? where idform=?", new Object[]{id,parameters.get(ID_FORM)});
    } else {
      Long idToDelete = (Long) sqlManager.getObject("(select id_autoupdate from g1npascheda where idform=?)", new Object[]{parameters.get(ID_FORM)});
      sqlManager.update("update g1npascheda set id_autoupdate=null where idform=?", new Object[]{parameters.get(ID_FORM)});
      sqlManager.update("delete from g1npaautoupdate where id=?",new Object[]{idToDelete});
    }

    if(parameters.get(LOG_EVENTO_REQ)!=null) {
      LogEvento logEvento= (LogEvento) parameters.get(LOG_EVENTO_REQ);
      logEvento.setDescr("Gestione scheda tramite task "+("start".equals(parameters.get(AUTOUPDATE))?"attivata":"interrotta"));
      enrichLogevento(logEvento,parameters);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    resp.put("done", "Y");

    return resp;
  }

  public void lanciaErroriValidazioneEForm(Object[] avvisiErroriValidazioneEForm) throws Exception {
    if (avvisiErroriValidazioneEForm != null) {
      String testoEccezione = "";
      if (avvisiErroriValidazioneEForm[0] != null && !(((JSONArray) avvisiErroriValidazioneEForm[0]).isEmpty())) {
        testoEccezione += "Sono stati rilevati degli errori durante la validazione TED CVS dell\'eForm. " +
                "<a id=\"visualizzaNascondiDettaglio\" href=\"javascript:toggleErroriCVS()\" style=\"color:#721c24;\">(<span>Visualizza dettaglio</span><span style=\"display: none;\">Nascondi dettaglio</span>)</a>" +
                "<ul id=\"bulletListCVS\" style=\"display: none;\">";
        JSONArray erroriCVS = (JSONArray) avvisiErroriValidazioneEForm[0];
        for (int i=0; i<erroriCVS.size(); i++) {
          testoEccezione += "<li>" + erroriCVS.getJSONObject(i).get("errore") + "</li>";
        }
        testoEccezione+="</ul>";
      }
      boolean fallitoControlloRettifica = (boolean) avvisiErroriValidazioneEForm[1];
      boolean numeroEFormIncoerente = (boolean) avvisiErroriValidazioneEForm[2];
      if (fallitoControlloRettifica) {
        testoEccezione += "L\'eForm allegato non è di rettifica.";
      }
      if (numeroEFormIncoerente) {
        testoEccezione += (fallitoControlloRettifica ? "<br>" : "") + "L\'eForm allegato non è coerente con la scheda.";
      }
      if (StringUtils.isNotBlank(testoEccezione)) {
        throw new Exception(testoEccezione);
      }
    }
  }

  public void wrapperGestisciConFEU(HttpServletRequest request, Map<String,Object> map, JSONObject result) throws Exception {
    if (StringUtils.isEmpty(ConfigManager.getValore("it.eldasoft.bandoavvisosimap.ws.url"))) {
      return; // se feu non attivo non faccio nulla
    }
    String metodo = GestisciInvioFEUManager.MAPPA_METODI.get((String) map.get(ScambioDatiPCPAction.SERVIZIO));
    boolean feuDopo =  Arrays.asList(GestisciInvioFEUManager.SCHEDA_ANNULLATA, GestisciInvioFEUManager.SCHEDA_PUBBLICATA).contains(metodo) && !(result.isEmpty());
    boolean feuPrima = GestisciInvioFEUManager.SCHEDA_INVIATA.equals(metodo) && result.isEmpty();
    try {
      String uuid_eform = gestisciInvioFEUManager.getUid(((Integer)map.get(ScambioDatiPCPManager.ID_FORM)).longValue());
      if ("true".equals(result.get("validationFailedFEU"))) {
        metodo = GestisciInvioFEUManager.SCHEDA_ANNULLATA;
      }
      boolean gestisciConFEU = feuPrima || feuDopo;
      if (StringUtils.isNotEmpty(uuid_eform) && StringUtils.isNotEmpty(metodo) && gestisciConFEU) {
        gestisciInvioFEUManager.gestisciInvio(request, ((Integer)map.get(ScambioDatiPCPManager.ID_FORM)).longValue(), metodo);
      }
    } catch (Throwable e) {
      if (feuDopo)
        result.put("validationErrors", e.getMessage());
      if (feuPrima)
        throw new Exception(e.getMessage());
    }
  }

  private void rimuoviArrayFascicoli(JSONArray tmp){
    try {
      if(tmp!=null && !tmp.isEmpty()) {
        for (int i = 0; i < tmp.size(); i++) {
          rimuoviFascicoli(tmp.getJSONObject(i));
        }
      }
    }catch (Exception e){
      //fallisce silenziosamente, vogliamo solo pulire un po' la response e non ha nulla di funzionale
    }
  }

  private void rimuoviFascicoli(JSONObject obj){
    try {
      obj.remove("fascicoli");
    }catch (Exception e){
      //fallisce silenziosamente, vogliamo solo pulire un po' la response e non ha nulla di funzionale
    }
  }

  public String getImpronta(byte[] buffer) throws NoSuchAlgorithmException, IOException {
    int count;
    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    java.io.BufferedInputStream bis = new java.io.BufferedInputStream(new java.io.ByteArrayInputStream(buffer));
    while ((count = bis.read(buffer)) > 0) {
      digest.update(buffer, 0, count);
    }
    bis.close();

    byte[] hash = digest.digest();
    return javax.xml.bind.DatatypeConverter.printHexBinary(hash);
  }

  private String getChiaveAccesso(String codgar, String codimp) throws SQLException {
    return (String) sqlManager.getObject("select chiaveaccesso from G1FVOE g where g.codgar=? and g.codimp=? and " +
                    " id = (select max(id) from G1FVOE x where x.codgar=g.codgar and x.codimp=g.codimp and statorichiesta='2')",
            new  Object[]{codgar,codimp});
  }

  private JSONObject buildDatiSARichiesta(String codice, String codimp, Map<String,Object> parameters){
    JSONObject datiRichiesta = null;
    JSONArray datiSoggetti = new JSONArray();
    JSONObject datiSoggetto = new JSONObject();
    JSONObject datiNominativo = new JSONObject();
    JSONObject altridati = new JSONObject();
    JSONObject datiSedeSociale = new JSONObject();
    String codiceComune;
    boolean linguaTedesca=false;

    if(CODICI_DATISA.contains(codice)){
      datiRichiesta = new JSONObject();
      switch (codice){
        case "00001":
          datiRichiesta.put("prefettura", parameters.get("prefetturaFVOEDOC"));
          boolean altriSogg = "1".equals(parameters.get("altriSogFVOEDOC"));
          datiRichiesta.put("altrisoggetti",altriSogg);

          if(altriSogg){
            datiSoggetto.put("Nome_Soggetto", parameters.get("nomeFVOEdoc"));
            datiSoggetto.put("Cognome_Soggetto", parameters.get("cognomeFVOEdoc"));
            datiSoggetto.put("Sesso_Soggetto", parameters.get("sessoFVOEdoc"));
            datiSoggetto.put("CF_Soggetto", parameters.get("cfFVOEdoc"));

            String giorno = String.format("%2s", parameters.get("giornoFVOEdoc")).replace(' ', '0');
            String mese = String.format("%2s", parameters.get("meseFVOEdoc")).replace(' ', '0');
            String anno = (String)parameters.get("annoFVOEdoc");

            String dateTimeString = anno+"-"+mese+"-"+giorno+"T00:00:00";
            String timeZoneString = "Europe/Rome";
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString);
            ZoneId zoneId = ZoneId.of(timeZoneString);
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            String rfc3339String = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            datiSoggetto.put("DataNascita_Soggetto",rfc3339String);
            String codiceStatoFVOEdoc = (String)parameters.get("codiceStatoFVOEdoc");
            codiceComune = (String)parameters.get("codiceComuneFVOEdoc");
            if(StringUtils.isNotBlank(codiceComune))
              datiSoggetto.put("LuogoNascita_Soggetto",codiceComune);
            else
              datiSoggetto.put("LuogoNascita_Soggetto",codiceStatoFVOEdoc);

            datiSoggetto.put("Soggetto_daVerificare",true);
            datiSoggetti.add(datiSoggetto);
            datiRichiesta.put("soggetto_da_verificare",datiSoggetti);
          }
          break;
        case "00004":
          datiNominativo.put("Nome", parameters.get("nomeFVOEdoc"));
          datiNominativo.put("Cognome", parameters.get("cognomeFVOEdoc"));
          datiNominativo.put("Sesso", parameters.get("sessoFVOEdoc"));
          datiNominativo.put("CF", parameters.get("cfFVOEdoc"));

          JSONObject datiNascita = new JSONObject();
          datiNascita.put("Giorno",String.format("%2s", parameters.get("giornoFVOEdoc")).replace(' ', '0'));
          datiNascita.put("Mese",String.format("%2s", parameters.get("meseFVOEdoc")).replace(' ', '0'));
          datiNascita.put("Anno", parameters.get("annoFVOEdoc"));
          datiNascita.put(CODICE_CATASTO_STATO, parameters.get("codiceStatoFVOEdoc"));
          codiceComune = (String)parameters.get("codiceComuneFVOEdoc");
          if(StringUtils.isNotBlank(codiceComune))
            datiNascita.put(CODICE_CATASTO_COMUNE_ITALIANO,codiceComune);

          datiNominativo.put("DatiNascita",datiNascita);
          datiSoggetto.put(DATI_NOMINATIVO,datiNominativo);

          if("1".equals(parameters.get("linguaTedescaFVOEdoc")))
            linguaTedesca=true;
          altridati.put(LINGUA_TEDESCA,linguaTedesca);
          datiSoggetto.put(ALTRI_DATI,altridati);

          datiSoggetti.add(datiSoggetto);
          datiRichiesta.put("datiSoggetti",datiSoggetti);
          break;
        case "00005":
          datiNominativo.put("DenominazioneSociale",parameters.get("cognomeFVOEdoc"));
          datiNominativo.put("CodiceFormaGiuridica",parameters.get("formaGiuridicaFVOEDOC"));
          datiNominativo.put("PIVA",parameters.get("cfFVOEdoc"));
          datiSedeSociale.put("Indirizzo",parameters.get("indirizzoFVOEdoc"));
          datiSedeSociale.put(CODICE_CATASTO_STATO, parameters.get("codiceStatoFVOEdoc"));
          codiceComune = (String)parameters.get("codiceComuneFVOEdoc");
          if(StringUtils.isNotBlank(codiceComune))
            datiSedeSociale.put(CODICE_CATASTO_COMUNE_ITALIANO,codiceComune);

          datiNominativo.put("DatiSedeSociale",datiSedeSociale);
          if("1".equals(parameters.get("linguaTedescaFVOEdoc")))
            linguaTedesca=true;
          altridati.put(LINGUA_TEDESCA,linguaTedesca);

          datiRichiesta.put(DATI_NOMINATIVO,datiNominativo);
          datiRichiesta.put(ALTRI_DATI,altridati);
        break;
        case "00035":
        case "00041":
          JSONObject codiceCatastale = new JSONObject();
          codiceCatastale.put("codiceCatastale", parameters.get("codiceComuneFVOEdoc"));
          datiRichiesta.put("domicilioFiscale",codiceCatastale);
          datiRichiesta.put("annoRiferimento", parameters.get("annoFVOEdoc"));
          break;
        case "00036":
        case "00037":
          datiRichiesta.put("matr_OE", parameters.get("matricolaINPSFVOEdoc"));
          datiRichiesta.put("anno", parameters.get("annoFVOEdoc"));
        break;
        case "00899":
          datiRichiesta.put("tipoDocumentoOE", parameters.get("tipoDocumentoOEFVOEdoc"));
          datiRichiesta.put("dettaglioDocumentoOE", parameters.get("dettaglioDocumentoOEOEdoc"));
        break;
      }

    }
    return datiRichiesta;
  }

  public boolean isDGUEValid(String idprg, Long iddocdig) {
    // Controllo che sia effettivamente un DGUE Request
    try {
      BlobFile file = fileAllegatoManager.getFileAllegato(idprg,iddocdig);
      String xmlContent = IOUtils.toString(file.getStream(), "UTF-8");
      return xmlContent.contains("QualificationApplicationRequest");
    } catch (Exception e) {
      logger.error("Errore nella lettura del file DGUE Request");
    }
    return true;
  }

}




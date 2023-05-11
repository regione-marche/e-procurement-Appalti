package it.eldasoft.sil.pg.bl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.xml.security.utils.Base64;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import com.lowagie.text.DocumentException;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.integrazioni.WsdmConfigManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.TabellatoWsdm;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.utils.AllegatoSintesiUtils;
import it.eldasoft.sil.pg.bl.utils.MarcaturaTemporaleFileUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazione_ServiceLocator;
import it.maggioli.eldasoft.ws.conf.WSDMTabellatoElementoType;
import it.maggioli.eldasoft.ws.conf.WSDMTabellatoType;
import it.maggioli.eldasoft.ws.dm.WSDMAggiungiAllegatiInType;
import it.maggioli.eldasoft.ws.dm.WSDMAggiungiAllegatiResType;
import it.maggioli.eldasoft.ws.dm.WSDMAnagraficaLeggiResType;
import it.maggioli.eldasoft.ws.dm.WSDMCampoType;
import it.maggioli.eldasoft.ws.dm.WSDMClassificaType;
import it.maggioli.eldasoft.ws.dm.WSDMDocumentoCollegaResType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloInType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloModificaInType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloModificaResType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloResType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInserimentoInFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailResType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailType;
import it.maggioli.eldasoft.ws.dm.WSDMListaAccountEmailResType;
import it.maggioli.eldasoft.ws.dm.WSDMListaAmministrazioniAooResType;
import it.maggioli.eldasoft.ws.dm.WSDMListaClassificheResType;
import it.maggioli.eldasoft.ws.dm.WSDMListaOperatoriResType;
import it.maggioli.eldasoft.ws.dm.WSDMListaProfiliResType;
import it.maggioli.eldasoft.ws.dm.WSDMListaTipiTrasmissioneResType;
import it.maggioli.eldasoft.ws.dm.WSDMListaUfficiResType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginEngAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginTitAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMMailFormatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloModificaInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloModificaResType;
import it.maggioli.eldasoft.ws.dm.WSDMRicercaAccountEmailType;
import it.maggioli.eldasoft.ws.dm.WSDMRicercaFascicoloResType;
import it.maggioli.eldasoft.ws.dm.WSDMRicercaFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMRigaType;
import it.maggioli.eldasoft.ws.dm.WSDMTabellaType;
import it.maggioli.eldasoft.ws.dm.WSDMTipoVoceRubricaType;
import it.maggioli.eldasoft.ws.dm.WSDMTrasmissioneDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMTrasmissioneResType;
import it.maggioli.eldasoft.ws.dm.WSDMVerificaMailResType;
import it.maggioli.eldasoft.ws.dm.WSDM_PortType;
import it.maggioli.eldasoft.ws.dm.WSDM_ServiceLocator;
import it.maggioli.eldasoft.wssec.PasswordCallback;

public class GestioneWSDMManager {

  /** Logger */
  static Logger               logger                                          = Logger.getLogger(GestioneWSDMManager.class);

  public  static final String SERVIZIO_FASCICOLOPROTOCOLLO                    = "FASCICOLOPROTOCOLLO";
  private static final String SERVIZIO_ATTO                                   = "ATTO";
  public  static final String SERVIZIO_DOCUMENTALE                            = "DOCUMENTALE";

  private static final String PROP_WSDMCONFIGURAZIONE_FASCICOLOPROTOCOLLO_URL = "wsdmconfigurazione.fascicoloprotocollo.url.";
  private static final String PROP_WSDM_FASCICOLOPROTOCOLLO_URL               = "wsdm.fascicoloprotocollo.url.";

  private static final String PROP_WSDMCONFIGURAZIONE_DOCUMENTALE_URL         = "wsdmconfigurazione.documentale.url.";
  private static final String PROP_WSDM_DOCUMENTALE_URL                       = "wsdm.documentale.url.";

  private static final String PROP_WSDM_ACCEDIFASCICOLODOCUMENTALE            = "wsdm.accediFascicoloDocumentale.";

  public static final String PROP_WSDM_APPLICAFASCICOLAZIONE                 = "pg.wsdm.applicaFascicolazione.";

  public static final String PROP_WSDM_LOGIN_COMUNE                          = "wsdm.loginComune.";

  public static final String TIPO_DOCUMENTO_GARA                                = "UBUY - Avvisi e comunicazioni";
  public static final String TIPO_DOCUMENTO_ELENCO                              = "UBUY EO - Avvisi e comunicazioni";
  public static final String TIPO_DOCUMENTO_GARA_PEC                            = "UBUY - Avvisi e comunicazioni (PEC)";
  public static final String TIPO_DOCUMENTO_ELENCO_PEC                          = "UBUY EO - Avvisi e comunicazioni (PEC)";
  public static final String TIPO_DOCUMENTO_AVVISO_PEC                          = "UBUY AV - Avvisi e comunicazioni (PEC)";
  public static final String TIPO_DOCUMENTO_AVVISO                              = "UBUY AV - Avvisi e comunicazioni";
  public static final String TIPO_DOCUMENTO_AVVISO_PUBBLIAZIONE_TITULUS         = "UBUY AV - Pubblicazione avviso";
  public static final String TIPO_DOCUMENTO_ELENCO_PUBBLIAZIONE_TITULUS         = "UBUY EO - Pubblicazione elenco";
  public static final String TIPO_DOCUMENTO_GARA_PUBBLIAZIONE_TITULUS           = "UBUY - Bando di gara";
  public static final String TIPO_DOCUMENTO_GARA_PUBBLIAZIONE_PALEO             = "UBUY - documentazione procedura";

  public static final String TIPO_DOCUMENTO_ELENCO_ARCHIVIAZIONE                = "UBUY EO - documentazione procedura";
  public static final String TIPO_DOCUMENTO_GARA_ARCHIVIAZIONE                  = "UBUY - documentazione procedura";
  public static final String TIPO_DOCUMENTO_AVVISO_ARCHIVIAZIONE                = "UBUY AV - documentazione procedura";

  public static final String LIVELLO_RISERVATEZZA_DEFAULT                       = "riservatezza_gare_differimento";
  public static final String INSERIMENTO_FASCICOLO_ESISTENTE                    = "SI_FASCICOLO_ESISTENTE";

  public static final String LABEL_IDCOM = "idcom";
  public static final String LABEL_COMMSGETS = "commsgtes";
  public static final String LABEL_COMMSGOGG = "commsgogg";
  public static final String LABEL_COMMSGTIP = "commsgtip";
  public static final String LABEL_CLASSIFICA_DOCUMENTO = "classificadocumento";
  public static final String LABEL_CODICE_REGISTRO_DOCUMENTO = "codiceregistrodocumento";
  public static final String LABEL_TIPO_DOCUMENTO = "tipodocumento";
  public static final String LABEL_MITTENTE_INTERNO = "mittenteInterno";
  public static final String LABEL_ID_INDICE = "idindice";
  public static final String LABEL_ID_TITOLAZIONE = "idtitolazione";
  public static final String LABEL_ID_UNITA_OPERATIVA_DESTINATARIA = "idunitaoperativadestinataria";
  public static final String LABEL_USERNAME = "username";
  public static final String LABEL_PASSWORD = "password";
  public static final String LABEL_RUOLO = "ruolo";
  public static final String LABEL_NOME = "nome";
  public static final String LABEL_COGNOME = "cognome";
  public static final String LABEL_CODICEUO = "codiceuo";
  public static final String LABEL_ID_UTENTE = "idutente";
  public static final String LABEL_ID_UTENTE_UNITA_OPERATIVA = "idutenteunop";
  public static final String LABEL_MEZZO = "mezzo";
  public static final String LABEL_STRUTTURA = "struttura";
  public static final String LABEL_SUPPORTO = "supporto";
  public static final String LABEL_INDIRIZZO_MITTENTE = "indirizzomittente";
  public static final String LABEL_MEZZO_INVIO = "mezzoinvio";
  public static final String LABEL_OGGETTO_DOCUMENTO = "oggettodocumento";
  public static final String LABEL_LIVELLO_RISERVATEZZA = "livelloriservatezza";
  public static final String LABEL_CODICE_FASCICOLO = "codicefascicolo";
  public static final String LABEL_ANNO_FASCICOLO = "annofascicolo";
  public static final String LABEL_NUMERO_FASCICOLO = "numerofascicolo";
  public static final String LABEL_CLASSIFICA_FASCICOLO = "classificafascicolo";
  public static final String LABEL_CODAOO = "codaoo";
  public static final String LABEL_CODICE_UFFICIO = "coduff";
  public static final String LABEL_IS_RISERVATEZZA = "isriservatezza";
  public static final String LABEL_TIPO_WSDM = "tipoWSDM";
  public static final String LABEL_ABILITATO_INVIO_MAIL_DOCUMENTALE = "abilitatoInvioMailDocumentale";
  public static final String LABEL_NUMERO_DOCUMENTO = "numeroDocumento";
  public static final String LABEL_ANNO_PROTOCOLLO = "annoProtocollo";
  public static final String LABEL_NUMERO_PROTOCOLLO = "numeroProtocollo";
  public static final String LABEL_DATA_PROTOCOLLO = "dataProtocollo";
  public static final String LABEL_IDPRG = "idprg";
  public static final String LABEL_IDCONFI = "idconfi";
  public static final String LABEL_NUMERO_ALLEGATI_REALI = "numAllegatiReali";
  public static final String LABEL_ABILITATO_INVIO_SINGOLO = "abilitatoInvioSingolo";
  public static final String LABEL_CLASSIFICA_DESRIZIONE = "classificadescrizione";
  public static final String LABEL_VOCE = "voce";
  public static final String LABEL_SOTTOTIPO = "sottotipo";
  public static final String LABEL_ACRONIMO_RUP = "acronimoRup";
  public static final String LABEL_NOME_RUP = "nomeRup";
  public static final String LABEL_RUP = "RUP";
  public static final String LABEL_DESCRIZIONE_FASCICOLO = "descrizionefascicolo";
  public static final String LABEL_OGGETTO_FASCICOLO = "oggettofascicolo";
  public static final String LABEL_TIPO_FASCICOLO = "tipofascicolo";
  public static final String LABEL_COMENT = "coment";
  public static final String LABEL_UOCOMPETENZA = "uocompetenza";
  public static final String LABEL_DESCRIZIONE_UOCOMPETENZA = "uocompetenzadescrizione";

  public static final String FILTRO_GRUPPO_VUOTO = "gruppovuoto";

  public static final String VOCE_LAVORI = "UBUY – Tipo lavori";
  public static final String VOCE_FORNITURA = "UBUY – Tipo fornitura";
  public static final String VOCE_SERVIZI = "UBUY – Tipo servizi";

  public static final String CONTATORE_FASCICOLI_CREATI = "FascicoliProcessati";

  public static final String SELECT_CONTEGGIO_FASCICOLI_MANCANTI = "select count( v.codgar) from V_GARE_GENERE v where v.genere ? "
      + "and exists (select * from pubbli where codgar=codgar9 and tippub in (11,12,13)) "
      + "and not exists (select * from wsfascicolo where key1=v.codice) "
      + "and not exists (select * from gare where ngara=codice and preced is not null)";

  public static final String FILTRO_UFFINT_SELECT_CONTEGGIO_FASCICOLI_MANCANTI = " and exists (select * from torn t where t.codgar=v.codgar and t.cenint in $)";

  public static final String SELECT_CONTEGGIO_FASCICOLI_MANCANTI_RILANCI = "select count(preced) from gare where preced is not null "
      + "and exists (select * from pubbli where codgar1=codgar9 and tippub in (11,12,13)) "
      + "and not exists (select * from wsfascicolo where key1=ngara) ";

  public static final String FILTRO_UFFINT_SELECT_CONTEGGIO_FASCICOLI_MANCANTI_RILANCI = " and exists (select * from torn t where codgar1=codgar and t.cenint in $)";

  public static final String SEPARATORE_SEZIONI = "\r\n";
  public static final String SEPARATORE_LINEA_COMPLETA = "_____________________________________________________________________________________\r\n";
  public static final String SEPARATORE_ELEMENTIMULTIPLI = "\r\n";

  public static final String PREFISSO_COD_FASCICOLO_LAPISOPERA = "ID_";

  private SqlManager          sqlManager;
  private GenChiaviManager    genChiaviManager;
  private FileAllegatoManager fileAllegatoManager;
  private WsdmConfigManager wsdmConfigManager;
  private TabellatiManager tabellatiManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  public void setWsdmConfigManager(WsdmConfigManager wsdmConfigManager) {
    this.wsdmConfigManager = wsdmConfigManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * Restituisce l'oggetto contenente le configurazioni del server in funzione
   * del servizio richiesto.
   *
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSDMConfigurazioneOutType wsdmConfigurazioneLeggi(String servizio,String idconfi) throws GestoreException {
    WSDMConfigurazioneOutType configurazione = null;

    String url = null;
    if (SERVIZIO_FASCICOLOPROTOCOLLO.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSDMCONFIGURAZIONE_FASCICOLOPROTOCOLLO_URL+idconfi);
    }else if (SERVIZIO_DOCUMENTALE.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSDMCONFIGURAZIONE_DOCUMENTALE_URL+idconfi);
    }

    configurazione=this.getWsdmConfigurazione(url);
    return configurazione;
  }


  /**
   * Restituisce l'oggetto contenente le configurazioni del server in funzione
   * dell'url di configurazione.
   *
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSDMConfigurazioneOutType getWsdmConfigurazione(String url) throws GestoreException {
    WSDMConfigurazioneOutType configurazione = null;
    try {
      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al servizio di configurazione non e' definito",
            "wsdmconfigurazione.url.error");
      }

      WSDMConfigurazione_ServiceLocator wsdmconfigurazioneLocator = new WSDMConfigurazione_ServiceLocator();
      wsdmconfigurazioneLocator.setWSDMConfigurazioneImplPortEndpointAddress(url);
      configurazione = wsdmconfigurazioneLocator.getWSDMConfigurazioneImplPort().WSDMConfigurazioneLeggi();
    } catch (RemoteException r) {
      throw new GestoreException("Si e' verificato un errore durante la lettura delle configurazioni: " + r.getMessage(),
          "wsdmconfigurazione.configurazioneleggi.remote.error", r);
    } catch (ServiceException s) {
      throw new GestoreException("Si e' verificato un errore durante la lettura delle configurazioni: " + s.getMessage(),
          "wsdmconfigurazione.configurazioneleggi.remote.error", s);
    }
    return configurazione;
  }


  /**
   * Restituisce puntatore al servizio WSDM.
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   * @throws NoSuchAlgorithmException
   * @throws NoSuchPaddingException
   * @throws InvalidKeyException
   * @throws IllegalBlockSizeException
   * @throws BadPaddingException
   * @throws ServiceException
   */
  private WSDM_PortType getWSDM(String username, String password, String servizio, String idconfi) throws GestoreException, NoSuchAlgorithmException,
      NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ServiceException {

    String url = null;
    if (SERVIZIO_FASCICOLOPROTOCOLLO.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSDM_FASCICOLOPROTOCOLLO_URL+idconfi);
      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al servizio per la gestione del fascicolo non e' definito",
            "wsdm.fascicoloprotocollo.url.error");
      }
    } else if (SERVIZIO_DOCUMENTALE.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSDM_DOCUMENTALE_URL+idconfi);
      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al servizio per la gestione documentale non e' definito",
            "wsdm.documentale.url.error");
      }
    }

    if (password == null){
      password = UtilityStringhe.convertiNullInStringaVuota(password);
    }

    byte[] key = "T/Yer@#2983273&d".getBytes();
    Cipher c = Cipher.getInstance("AES");
    SecretKeySpec k = new SecretKeySpec(key, "AES");
    c.init(Cipher.ENCRYPT_MODE, k);
    byte[] passwordEncoded = c.doFinal(password.getBytes());

    EngineConfiguration config = new FileProvider("client_wssec.wsdd");
    WSDM_ServiceLocator wsdmLocator = new WSDM_ServiceLocator(config);
    wsdmLocator.setWSDMImplPortEndpointAddress(url);
    Remote remote = wsdmLocator.getPort(WSDM_PortType.class);
    Stub axisPort = (Stub) remote;
    axisPort._setProperty(WSHandlerConstants.USER, username);
    PasswordCallback passwordCallback = new PasswordCallback();
    passwordCallback.setAliasPassword(username, Base64.encode(passwordEncoded));
    axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_REF, passwordCallback);

    WSDM_PortType wsdm = (WSDM_PortType) axisPort;
    return wsdm;
  }

  /**
   * Lettura del fascicolo e della lista dei documenti allegati.
   *
   * @param username
   * @param password
   * @param ruolo
   * @param codice
   * @param anno
   * @param numero
   * @param servizio
   * @param classifica
   * @return
   * @throws GestoreException
   */
  public WSDMFascicoloResType wsdmFascicoloLeggi(String username, String password, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, String codice, Long anno, String numero, String servizio, String classifica, String idconfi) throws GestoreException {
    WSDMFascicoloResType wsdmFascicoloRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      //Per TITULUS per potere leggere i dati del fascicolo si deve valorizzare il numero del profilo
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      wsdmFascicoloRes = wsdm.WSDMFascicoloLeggi(loginAttr, codice, anno, numero, classifica,null);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fascicolo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.fascicololeggi.remote.error", t);
    }
    return wsdmFascicoloRes;
  }

  /**
   * Lettura del fascicolo e della lista dei documenti allegati.
   *
   * @param username
   * @param password
   * @param ruolo
   * @param codice
   * @param anno
   * @param numero
   * @param servizio
   * @param classifica
   * @return
   * @throws GestoreException
   */
  public WSDMFascicoloResType wsdmFascicoloMetadatiLeggi(String username, String password, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, String codice, Long anno, String numero, String servizio, String classifica, String idconfi) throws GestoreException {
    WSDMFascicoloResType wsdmFascicoloRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      //Per TITULUS per potere leggere i dati del fascicolo si deve valorizzare il numero del profilo
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      wsdmFascicoloRes = wsdm.WSDMFascicoloMetadatiLeggi(loginAttr, codice, anno, numero, classifica,null);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fascicolo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.fascicololeggi.remote.error", t);
    }
    return wsdmFascicoloRes;
  }

  /**
   * Lettura del documento identificato da numeroDocumento.
   *
   * @param username
   * @param password
   * @param ruolo
   * @param numeroDocumento
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType wsdmDocumentoLeggi(String username, String password, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, String numeroDocumento, String servizio, String idconfi) throws GestoreException {
    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      wsdmProtocolloDocumentoRes = wsdm.WSDMDocumentoLeggi(loginAttr, numeroDocumento);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fascicolo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.documentoleggi.remote.error", t);
    }
    return wsdmProtocolloDocumentoRes;
  }


  public WSDMProtocolloAnagraficaType WSDMAnagraficaLeggi(String numeroAnagrafica, String idconfi) throws GestoreException {
    WSDMAnagraficaLeggiResType wsdmAnagraficaRes = null;
    try {
      String servizio = GestioneWSDMManager.SERVIZIO_DOCUMENTALE;
      String[] loginComune = this.getWSDMLoginComune(servizio,idconfi);
      String username = loginComune[0];
      String password = loginComune[1];
      String ruolo = loginComune[2];

      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      wsdmAnagraficaRes = wsdm.WSDMAnagraficaLeggi(loginAttr, numeroAnagrafica);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura delle anagrafiche: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.documentoleggi.remote.error", t);
    }
    return wsdmAnagraficaRes.getAnagrafica();
  }


  /**
   * Lettura del protocollo
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param annoProtocollo
   * @param numeroProtocollo
   * @param servizio
   * @param idconfi
   * @param profilo
   * @return
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType wsdmProtocolloLeggi(String username, String password, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, Long annoProtocollo, String numeroProtocollo, String servizio, String idconfi) throws GestoreException {
    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.wsdmProtocolloLeggi(username,password,ruolo,nome,cognome,codiceUO,idutente,idutenteunop,annoProtocollo,numeroProtocollo,servizio,idconfi,null,false,null);

    return wsdmProtocolloDocumentoRes;
  }

  /**
   * Lettura del protocollo
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param annoProtocollo
   * @param numeroProtocollo
   * @param servizio
   * @param idconfi
   * @param profilo
   * @param sso
   * @param utenteReale
   * @return
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType wsdmProtocolloLeggi(String username, String password, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, Long annoProtocollo, String numeroProtocollo, String servizio, String idconfi,
      String profilo, boolean sso, String utenteReale) throws GestoreException {
    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      if(profilo!=null && !"".equals(profilo)){
        WSDMLoginTitAttrType logintitAttr = new WSDMLoginTitAttrType();
        logintitAttr.setNumeroProfilo(profilo);
        if(sso)
          logintitAttr.setUtenteApplicativo(utenteReale);
        loginAttr.setLoginTitAttr(logintitAttr);
      }

      wsdmProtocolloDocumentoRes = wsdm.WSDMProtocolloLeggi(loginAttr, annoProtocollo, numeroProtocollo);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fascicolo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.protocolloleggi.remote.error", t);
    }
    return wsdmProtocolloDocumentoRes;
  }


  /**
   * Inserimento nel protocollo.
   *
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param codiceaoo
   * @param codiceUfficio
   * @param protocolloDocumentoIn
   * @return
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType wsdmProtocolloInserisci(String username, String password, String ruolo, String nome,
      String cognome, String codiceUO, String idutente, String idutenteunop, String codiceaoo, String codiceUfficio,
      WSDMProtocolloDocumentoInType protocolloDocumentoIn, String idconfi) throws GestoreException {
    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      if(codiceaoo!=null && !"".equals(codiceaoo)){
        WSDMLoginTitAttrType loginTitAttr = new WSDMLoginTitAttrType();
        loginTitAttr.setCodiceAmministrazioneAoo(codiceaoo);
        if(codiceUfficio!=null && !"".equals(codiceUfficio))
          loginTitAttr.setCodiceUfficio(codiceUfficio);
        loginAttr.setLoginTitAttr(loginTitAttr);
      }

      wsdmProtocolloDocumentoRes = wsdm.WSDMProtocolloInserisci(loginAttr, protocolloDocumentoIn);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante l'inserimento del protocollo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.protocolloinserisci.remote.error", t);
    }
    return wsdmProtocolloDocumentoRes;
  }

  /**
   * Aggiunge un documento ad un fascicolo esistente.
   *
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param codiceFascicolo
   * @param numeroDocumento
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSDMFascicoloResType wsdmFascicoloAggiungiDocumento(String username, String password, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, String codiceFascicolo, String numeroDocumento, String servizio, String idconfi) throws GestoreException {
    WSDMFascicoloResType wsdmFascicoloRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      wsdmFascicoloRes = wsdm.WSDMFascicoloAggiungiDocumento(loginAttr, codiceFascicolo, numeroDocumento);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante l'inserimento del documento nel fascicolo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.fascicoloaggiungidocumento.remote.error", t);
    }
    return wsdmFascicoloRes;

  }

  /**
   * Salvataggio delle informazioni del fascicolo nell'entità WSFASCICOLO
   *
   * @param entita
   * @param key1
   * @param key2
   * @param key3
   * @param key4
   * @param codice
   * @param anno
   * @param numero
   * param classifica
   * param codiceAoo
   * param codiceUfficio
   * param struttura
   * @throws SQLException
   */
  public void setWSFascicolo(String entita, String key1, String key2, String key3, String key4, String codice, Long anno, String numero,
      String classifica, String codiceAoo, String codiceUfficio, String struttura, Long isRiservatezzaAttiva, String descrizioneFascicolo, String voce,
      String desaoo, String desuff)
      throws SQLException {

    if (entita != null && !"".equals(entita) && key1 != null && !"".equals(key1)) {

      List<Object> parameters = new ArrayList<Object>();
      String whereWSFASCICOLO = " entita = ? and key1 = ? ";
      parameters.add(entita);
      parameters.add(key1);

      if (key2 != null && !"".equals(key2)) {
        whereWSFASCICOLO += " and key2 = ? ";
        parameters.add(key2);
      } else {
        whereWSFASCICOLO += " and key2 is null ";
        key2=null;
      }

      if (key3 != null && !"".equals(key3)) {
        whereWSFASCICOLO += " and key3 = ? ";
        parameters.add(key3);
      } else {
        whereWSFASCICOLO += " and key3 is null ";
        key3=null;
      }

      if (key4 != null && !"".equals(key4)) {
        whereWSFASCICOLO += " and key4 = ? ";
        parameters.add(key4);
      } else {
        whereWSFASCICOLO += " and key4 is null ";
        key4=null;
      }

      TransactionStatus status = null;
      boolean commitTransaction = false;


      try {
        status = this.sqlManager.startTransaction();
        String countWSFASCICOLO = "select count(*) from wsfascicolo where " + whereWSFASCICOLO;
        Long cnt = (Long) this.sqlManager.getObject(countWSFASCICOLO, parameters.toArray());
        numero = StringUtils.stripToNull(numero);

        List<Object> updateParameters = new ArrayList<Object>();
        if (cnt != null && cnt.longValue() > 0) {
          String updateWSFASCICOLO = "update wsfascicolo set codice = ?, anno = ?, numero = ?";
          updateParameters.add(codice);
          updateParameters.add(anno);
          updateParameters.add(numero);
          if(classifica!=null && !"".equals(classifica)) {
            updateWSFASCICOLO += ", classifica = ?";
            updateParameters.add(classifica);
          }
          if(struttura!=null && !"".equals(struttura)) {
            updateWSFASCICOLO += ", struttura = ? ";
            updateParameters.add(struttura);
          }
          if(descrizioneFascicolo!=null && !"".equals(descrizioneFascicolo)) {
            updateWSFASCICOLO += ", desclassi = ? ";
            updateParameters.add(descrizioneFascicolo);
          }

          //if("JIRIDE".equals(tipoWSDM) && ("GARE".equals(entita) || "TORN".equals(entita)) && (isGara.intValue()>0)){
          //  updateWSFASCICOLO += ", isriserva =" + new Long(1);}
          updateWSFASCICOLO+="  where " + whereWSFASCICOLO;
          updateParameters.addAll(parameters);

          this.sqlManager.update(updateWSFASCICOLO, updateParameters.toArray());

        } else {
          int id = this.genChiaviManager.getNextId("WSFASCICOLO");
          codiceAoo = StringUtils.stripToNull(codiceAoo);
          codiceUfficio = StringUtils.stripToNull(codiceUfficio);
          struttura = StringUtils.stripToNull(struttura);
          String insertWSFASCICOLO = "insert into wsfascicolo (id, entita, key1, key2, key3, key4, codice, anno, numero, classifica, codaoo, coduff, "
              + "struttura, isriserva,desclassi,desvoce,desaoo,desuff) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
          if("".equals(descrizioneFascicolo) || "undefined".equals(descrizioneFascicolo))
            descrizioneFascicolo=null;
          if("".equals(voce) || "undefined".equals(voce))
            voce=null;
          if("".equals(desaoo))
            desaoo=null;
          if("".equals(desuff))
            desuff=null;
          this.sqlManager.update(insertWSFASCICOLO, new Object[] { new Long(id), entita, key1, key2, key3, key4, codice,
              anno, numero, classifica, codiceAoo,codiceUfficio,struttura,isRiservatezzaAttiva,descrizioneFascicolo,voce,
              desaoo,desuff});

        }

        commitTransaction = true;
      } catch (SQLException e) {
        commitTransaction = false;
        throw e;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
      }
    }
  }

  /**
   * Salvataggio in WSDOCUMENTO
   *
   * @param entita
   * @param key1
   * @param key2
   * @param key3
   * @param key4
   * @param numDoc
   * @param annoProt
   * @param numProt
   * @param oggetto
   * @throws SQLException
   */
  public Long setWSDocumento(String entita, String key1, String key2, String key3, String key4, String numDoc, Long annoProt, String numProt, String oggetto, String inout)
      throws SQLException {
    Long idWSDocumento= null;
    if (entita != null && !"".equals(entita) && key1 != null && !"".equals(key1)) {

      TransactionStatus status = null;
      boolean commitTransaction = false;
      try {
        status = this.sqlManager.startTransaction();
        int id = this.genChiaviManager.getNextId("WSDOCUMENTO");
        String insertWSDOCUMENTO = "insert into wsdocumento (id, entita, key1, key2, key3, key4, numerodoc, annoprot, numeroprot,oggetto,inout) values (?,?,?,?,?,?,?,?,?,?,?)";
        idWSDocumento=new Long(id);
        if(oggetto!=null && oggetto.length()>2000)
          oggetto = oggetto.substring(0, 2000);
        this.sqlManager.update(insertWSDOCUMENTO, new Object[] { idWSDocumento, entita, key1, key2, key3, key4, numDoc, annoProt, numProt, oggetto, inout });
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
        logger.error("Errore nell'inserimento in WSDOCUMENTO", e);
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
      }
    }
    return idWSDocumento;
  }

  /**
   * Salvataggio in WSDOCUMENTO
   *
   * @param entita
   * @param key1
   * @param key2
   * @param key3
   * @param key4
   * @param numDoc
   * @param annoProt
   * @param numProt
   * @param oggetto
   * @throws SQLException
   */
  public void setWSAllegati(String entita, String key1, String key2, String key3, String key4, Long idwsdoc)
      throws SQLException {

    if (entita != null && !"".equals(entita) && key1 != null && !"".equals(key1)) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      try {
        status = this.sqlManager.startTransaction();
        int id = this.genChiaviManager.getNextId("WSALLEGATI");
        String insertWSDOCUMENTO = "insert into wsallegati (id, entita, key1, key2, key3, key4, idwsdoc) values (?,?,?,?,?,?,?)";
        this.sqlManager.update(insertWSDOCUMENTO, new Object[] { new Long(id), entita, key1, key2, key3, key4, idwsdoc });
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
        logger.error("Errore nell'inserimento in WSALLEGATI", e);
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
      }
    }
  }


  public String getClassificaFascicolo(String entita, String key1) throws GestoreException{
    String codiceClassifica ="";
    try {
        codiceClassifica =(String)this.sqlManager.getObject("select classifica from wsfascicolo where entita=? and key1=?", new Object[]{entita, key1});
    } catch (SQLException e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fascicolo: " + e.getMessage(),
          "wsdm.fascicoloprotocollo.fascicololeggi.remote.error", e);
    }


    return codiceClassifica;

  }

  /**
   * Viene inizializzato un oggetto prelevando i valori dalla variabile HashMap parametri che contiene i valori:
   * classificadocumento
   * tipodocumento
   * oggettodocumento
   * descrizionedocumento
   * mittenteinterno
   * codiceregistrodocumento
   * inout
   * idindice
   * idtitolazione
   * idunitaoperativamittente
   * inserimentoinfascicolo
   * codicefascicolo
   * oggettofascicolo
   * classificafascicolo
   * descrizionefascicolo
   * annofascicolo
   * numerofascicolo
   * tipoWSDM
   * idprg
   * idcom
   *
   * @param parametri
   * @return WSDMProtocolloDocumentoInType
   * @throws GestoreException
   */

 public WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoPopola(HashMap<String,Object> parametri,String idconfi ) throws GestoreException{
   WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();

   boolean tabellatiInDB = this.isTabellatiInDb();

   String codiceGaralotto = (String)parametri.get("codiceGaralotto");
   if("FOLIUM".equals(parametri.get("tipoWSDM")) && "OUT".equals(parametri.get("inout"))){
     String note= this.getDescrizioneTabellato(SERVIZIO_FASCICOLOPROTOCOLLO, "note",idconfi,tabellatiInDB);
     codiceGaralotto=note + " " + codiceGaralotto;
   }

   // Dati generali dell'elemento documentale
   wsdmProtocolloDocumentoIn.setClassifica((String)parametri.get("classificadocumento"));
   wsdmProtocolloDocumentoIn.setTipoDocumento((String)parametri.get("tipodocumento"));
   wsdmProtocolloDocumentoIn.setOggetto((String)parametri.get("oggettodocumento"));
   wsdmProtocolloDocumentoIn.setDescrizione((String)parametri.get("descrizionedocumento"));
   wsdmProtocolloDocumentoIn.setMittenteInterno((String)parametri.get("mittenteinterno"));
   wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.fromString((String)parametri.get("inout")));
   wsdmProtocolloDocumentoIn.setCodiceRegistro((String)parametri.get("codiceregistrodocumento"));
   Calendar cal = Calendar.getInstance();
   cal.setTime(UtilityDate.getDataOdiernaAsDate());
   wsdmProtocolloDocumentoIn.setData(cal);
   wsdmProtocolloDocumentoIn.setDataArrivo(cal);
   wsdmProtocolloDocumentoIn.setIdIndice((String)parametri.get("idindice"));
   wsdmProtocolloDocumentoIn.setIdTitolazione((String)parametri.get("idtitolazione"));
   wsdmProtocolloDocumentoIn.setIdUnitaOperativaMittente((String)parametri.get("idunitaoperativamittente"));
   wsdmProtocolloDocumentoIn.setMezzo((String)parametri.get("mezzo"));
   wsdmProtocolloDocumentoIn.setSocieta((String)parametri.get("societa"));
   wsdmProtocolloDocumentoIn.setCodiceGaraLotto(codiceGaralotto);
   wsdmProtocolloDocumentoIn.setCig((String)parametri.get("cig"));
   wsdmProtocolloDocumentoIn.setSupporto((String)parametri.get("supporto"));
   wsdmProtocolloDocumentoIn.setNumeroAllegati((Long)parametri.get("numeroallegati"));
   wsdmProtocolloDocumentoIn.setStruttura((String)parametri.get("struttura"));
   if("EASYDOC".equals(parametri.get("tipoWSDM")) && parametri.get("servizio")!=null){
    String channelCode= this.getcodiceTabellato((String)parametri.get("servizio"), "channelcode",idconfi, tabellatiInDB);
     wsdmProtocolloDocumentoIn.setChannelCode(channelCode);
   }

   if("JPROTOCOL".equals(parametri.get("tipoWSDM")) && parametri.get("servizio")!=null){
     String strutt= this.getcodiceTabellato((String)parametri.get("servizio"), "struttura",idconfi, tabellatiInDB);
     wsdmProtocolloDocumentoIn.setStruttura(strutt);
     String tipoassegnazione = this.getcodiceTabellato((String)parametri.get("servizio"), "tipoassegnazione",idconfi,tabellatiInDB);
     wsdmProtocolloDocumentoIn.setTipoAssegnazione(tipoassegnazione);
   }

   if("TITULUS".equals(parametri.get("tipoWSDM"))){
     wsdmProtocolloDocumentoIn.setClassificaDescrizione((String)parametri.get("classificadescrizione"));
     wsdmProtocolloDocumentoIn.setVoce((String)parametri.get("voce"));
   }

   // Inserimento in fascicolo
   if ("NO".equals(parametri.get("inserimentoinfascicolo"))) {
     wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.NO);
   }

   if("ENGINEERINGDOC".equals(parametri.get("tipoWSDM"))){
     wsdmProtocolloDocumentoIn.setGenericS31((String)parametri.get(LABEL_UOCOMPETENZA));
   }

   if ("SI_FASCICOLO_ESISTENTE".equals(parametri.get("inserimentoinfascicolo"))) {
     wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_ESISTENTE);
     WSDMFascicoloType wsdmFascicolo = new WSDMFascicoloType();
     wsdmFascicolo.setCodiceFascicolo((String)parametri.get("codicefascicolo"));
     String annofascicolo = UtilityStringhe.convertiNullInStringaVuota((String)parametri.get("annofascicolo"));
     if (!"".equals(annofascicolo)) wsdmFascicolo.setAnnoFascicolo(new Long((String)parametri.get("annofascicolo")));
     String numerofascicolo = UtilityStringhe.convertiNullInStringaVuota((String)parametri.get("numerofascicolo"));
     if (!"".equals(numerofascicolo)) wsdmFascicolo.setNumeroFascicolo(numerofascicolo);
     if("TITULUS".equals(parametri.get("tipoWSDM")) || "FOLIUM".equals(parametri.get("tipoWSDM")))
       wsdmFascicolo.setOggettoFascicolo((String)parametri.get("oggettofascicolo"));
     if("ARCHIFLOWFA".equals(parametri.get("tipoWSDM")) || "FOLIUM".equals(parametri.get("tipoWSDM")) || "INFOR".equals(parametri.get("tipoWSDM")) || "DOCER".equals(parametri.get("tipoWSDM"))
         || "ITALPROT".equals(parametri.get("tipoWSDM")) || "ENGINEERINGDOC".equals(parametri.get("tipoWSDM")) || "LAPISOPERA".equals(parametri.get("tipoWSDM")))
       wsdmFascicolo.setClassificaFascicolo((String)parametri.get("classificafascicolo"));
     if("PRISMA".equals(parametri.get("tipoWSDM")) || "JIRIDE".equals(parametri.get("tipoWSDM")) || "INFOR".equals(parametri.get("tipoWSDM")))
       wsdmFascicolo.setStruttura((String)parametri.get("struttura"));
     if("JIRIDE".equals(parametri.get("tipoWSDM"))){
       wsdmProtocolloDocumentoIn.setLivelloRiservatezza((String)parametri.get("livelloriservatezza"));
     }

     if("DOCER".equals(parametri.get("tipoWSDM"))){
       wsdmProtocolloDocumentoIn.setGenericS11((String)parametri.get("tipofirma"));
       wsdmProtocolloDocumentoIn.setGenericS21((String)parametri.get("idunitaoperativamittenteDesc"));
       wsdmProtocolloDocumentoIn.setGenericS22((String)parametri.get("idunitaoperativamittente"));
     }

     wsdmProtocolloDocumentoIn.setFascicolo(wsdmFascicolo);
   }


   if ("SI_FASCICOLO_NUOVO".equals(parametri.get("inserimentoinfascicolo"))) {

     wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_NUOVO);
     WSDMFascicoloType wsdmFascicolo = new WSDMFascicoloType();
     wsdmFascicolo.setOggettoFascicolo((String)parametri.get("oggettofascicolo"));
     wsdmFascicolo.setClassificaFascicolo((String)parametri.get("classificafascicolo"));
     wsdmFascicolo.setDescrizioneFascicolo((String)parametri.get("descrizionefascicolo"));
     if("TITULUS".equals(parametri.get("tipoWSDM")) || "SMAT".equals(parametri.get("tipoWSDM")) || "ARCHIFLOWFA".equals(parametri.get("tipoWSDM"))
         || "ITALPROT".equals(parametri.get("tipoWSDM")))
       wsdmFascicolo.setCodiceFascicolo((String)parametri.get("codicefascicolo"));
     if("PRISMA".equals(parametri.get("tipoWSDM"))){
       wsdmFascicolo.setStruttura((String)parametri.get("struttura"));
       wsdmFascicolo.setAnnoFascicolo(new Long((String)parametri.get("annofascicolo")));
       wsdmFascicolo.setNumeroFascicolo((String)parametri.get("numerofascicolo"));
     }
     if("ITALPROT".equals(parametri.get("tipoWSDM"))){
       wsdmFascicolo.setAnnoFascicolo(new Long((String)parametri.get("annofascicolo")));
     }
     if("JIRIDE".equals(parametri.get("tipoWSDM"))){
       wsdmFascicolo.setStruttura((String)parametri.get("struttura"));
       wsdmFascicolo.setTipo((String)parametri.get("tipofascicolo"));
       wsdmProtocolloDocumentoIn.setLivelloRiservatezza((String)parametri.get("livelloriservatezza"));
     }
     if("JDOC".equals(parametri.get("tipoWSDM"))){
       wsdmFascicolo.setGenericS11((String)parametri.get("acronimoRup"));
       wsdmFascicolo.setGenericS12((String)parametri.get("nomeRup"));
     }
     if("INFOR".equals(parametri.get("tipoWSDM")))
       wsdmFascicolo.setStruttura((String)parametri.get("struttura"));

     wsdmProtocolloDocumentoIn.setFascicolo(wsdmFascicolo);
   }

   if(("ARCHIFLOWFA".equals(parametri.get("tipoWSDM")) || "FOLIUM".equals(parametri.get("tipoWSDM")) || "PRISMA".equals(parametri.get("tipoWSDM"))
       || "ITALPROT".equals(parametri.get("tipoWSDM"))) && "SI_FASCICOLO_NUOVO".equals(parametri.get("inserimentoinfascicolo"))){
     wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_ESISTENTE);
   }

   if("TITULUS".equals(parametri.get("tipoWSDM")) && parametri.get("idcom") !=null){
     Long idcom = (Long)parametri.get("idcom");
     wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM|" + (String)parametri.get("idprg") + "|" + idcom.toString());
   }
   if("JDOC".equals(parametri.get("tipoWSDM"))){
     wsdmProtocolloDocumentoIn.setGenericS11((String)parametri.get("sottotipo"));
     wsdmProtocolloDocumentoIn.setGenericS12((String)parametri.get("nomeRup"));
     wsdmProtocolloDocumentoIn.setGenericS45((String)parametri.get("acronimoRup"));
   }

   return wsdmProtocolloDocumentoIn;
 }

  /**
   * Si ricavano i dati dell'anagrafica dei componenti di un raggruppamento a partire da una descrizione con formato:
   * Ragione sociale raggruppamento - regione sociale componente - Ruolo della componente
   *
   * Si deve tenere conto che con la nuova gestione delle componenti di un RT, in w_invcomdes si avrà solo la mandataria inserita con
   * codice del ragguppamento, le mandanti vengono inserite direttamente col loro codice impresa
   *
   * @param idprg
   * @parma idcom
   * @param codiceRaggruppamento
   * @param descrizione
   * @return String[] con i seguenti valori: ragSocialeComponente,codiceFiscale,indirizzoResidenza,comuneResidenza,codiceComuneResidenza,provincia,cap
   * @throws GestoreException
   */
  public String[] getDatiImpresaComponente(String idprg, Long idcom, String codiceRaggruppamento, String descrizione) throws GestoreException{
    String ret[]= null;
    boolean vecchiaGestione = true;
    String selectIMPR=null;
    try {
      if(descrizione!=null){
        Long numOccorrenze = (Long)this.sqlManager.getObject("select count(idcom) from w_invcomdes where idprg = ? and idcom = ? and descodsog=?  and (descc is null or descc <>1)", new Object[]{idprg, idcom, codiceRaggruppamento});
        if(numOccorrenze!=null && numOccorrenze.longValue()==1){
          //Si deve controllare se la descrizione termina in " - Mandataria"
          if(descrizione.endsWith("- Mandataria")){
            vecchiaGestione=false;
            selectIMPR = "select cfimp,indimp,nciimp,locimp,codcit,nomdic,emaiip,pivimp,proimp,capimp from impr,ragimp where codimp=coddic and codime9=? and impman='1'";
          }
        }

        if(vecchiaGestione){
          String ragSocialeRaggruppamento = (String) this.sqlManager.getObject("select nomimp from impr where codimp=?", new Object[]{codiceRaggruppamento});
          //si toglie dalla variabile descrizione la componente ruolo
          descrizione = descrizione.replace(" - Mandataria", " ");
          descrizione = descrizione.replace(" - Mandante", " ");
          descrizione = descrizione.trim();
          if(descrizione.indexOf("'")>0)
            descrizione=descrizione.replace("'", "''");
          if(ragSocialeRaggruppamento.indexOf("'")>0)
            ragSocialeRaggruppamento=ragSocialeRaggruppamento.replace("'", "''");
          String condizioneAppend = this.sqlManager.getDBFunction("concat",  new String[] {"'" + ragSocialeRaggruppamento + " - '" , "nomdic" });

          //codiceFiscale = (String)this.sqlManager.getObject("select cfimp,indimp,nciimp,locimp,codcit from impr,ragimp where codimp=coddic and codime9=? and nomdic=?", new Object[]{descodsog, ragSocialeComponente});
          selectIMPR = "select cfimp,indimp,nciimp,locimp,codcit,nomdic,emaiip,pivimp,proimp,capimp from impr,ragimp where codimp=coddic and codime9=? and " + condizioneAppend + " = '" + descrizione + "'";
        }

        Vector<?> datiIMPR = sqlManager.getVector(selectIMPR, new Object[] { codiceRaggruppamento });
        String codiceFiscale="";
        String indirizzoResidenza="";
        String comuneResidenza ="";
        String codiceComuneResidenza ="";
        String ragSocialeComponente = "";
        String emaiip = "";
        String pivimp = "";
        String provincia=null;
        String cap=null;
        if(datiIMPR!=null && datiIMPR.size()>0){
          codiceFiscale = (String) SqlManager.getValueFromVectorParam(datiIMPR,0).getValue();
          indirizzoResidenza = (String) SqlManager.getValueFromVectorParam(datiIMPR,1).getValue();
          String numeroCivico= (String) SqlManager.getValueFromVectorParam(datiIMPR,2).getValue();
          if (indirizzoResidenza != null && !"".equals(indirizzoResidenza) && numeroCivico!=null){
            indirizzoResidenza += ", " + numeroCivico;
          }
          comuneResidenza= (String) SqlManager.getValueFromVectorParam(datiIMPR,3).getValue();
          codiceComuneResidenza = (String) SqlManager.getValueFromVectorParam(datiIMPR,4).getValue();
          ragSocialeComponente = (String) SqlManager.getValueFromVectorParam(datiIMPR,5).getValue();
          emaiip = (String) SqlManager.getValueFromVectorParam(datiIMPR,6).getValue();
          pivimp = (String) SqlManager.getValueFromVectorParam(datiIMPR,7).getValue();
          provincia = (String) SqlManager.getValueFromVectorParam(datiIMPR,8).getValue();
          cap = (String) SqlManager.getValueFromVectorParam(datiIMPR,9).getValue();
        }
        ret= new String[]{ragSocialeComponente,codiceFiscale,indirizzoResidenza,comuneResidenza,codiceComuneResidenza,emaiip,pivimp,provincia,cap};

      }
    } catch (SQLException e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura dei dati della componente del raggruppamento: " + codiceRaggruppamento,
          "wsdm.destinatari.letturaDatiComponenteRaggruppamento", new Object[]{codiceRaggruppamento},e);
    }
    return ret;
  }

  public HashMap<String, String> getDatiImpresa(String codiceImpresa) throws SQLException, GestoreException{
    HashMap<String, String> ret= new  HashMap<String, String>();
    String selectMailPecImpresa = "select tipimp,nomimp,cfimp,indimp,nciimp,locimp,codcit,codimp,emaiip,emai2ip,pivimp,proimp,capimp  from impr where codimp = ? ";
    Vector<JdbcParametro> datiImpr = this.sqlManager.getVector(selectMailPecImpresa,new Object[] {codiceImpresa});
    if (datiImpr != null) {
      Long tipologiaImpresa = (datiImpr.get(0)).longValue();
      if (tipologiaImpresa!=null && (tipologiaImpresa.longValue()==3 || tipologiaImpresa.longValue()==10)) {
       String selectMailPecRaggruppamento = "select nomimp,cfimp,indimp,nciimp,locimp,codcit,coddic,emaiip,emai2ip,pivimp,proimp,capimp from ragimp,impr where codime9 = ? and impman ='1' and codimp = coddic ";
        Vector<JdbcParametro> datiRaggruppamento = new Vector<JdbcParametro>();
        datiRaggruppamento = this.sqlManager.getVector(selectMailPecRaggruppamento, new Object[] {codiceImpresa });
        if (datiRaggruppamento != null) {
          ret.put("codiceFiscale", datiRaggruppamento.get(1).getStringValue());
          String indirizzoResidenza = datiRaggruppamento.get(2).getStringValue();
          String numeroCivico= datiRaggruppamento.get(3).getStringValue();
          if (indirizzoResidenza != null && !"".equals(indirizzoResidenza) && numeroCivico!=null){
            indirizzoResidenza += ", " + numeroCivico;
          }
          ret.put("indirizzoResidenza", indirizzoResidenza);
          ret.put("comuneResidenza", datiRaggruppamento.get(4).getStringValue());
          ret.put("codiceComuneResidenza", datiRaggruppamento.get(5).getStringValue());
          ret.put("cognomeIntestazione", datiRaggruppamento.get(0).getStringValue());
          ret.put("codice", datiRaggruppamento.get(6).getStringValue());
          ret.put("emaiip", datiRaggruppamento.get(7).getStringValue());
          ret.put("emai2ip", datiRaggruppamento.get(8).getStringValue());
          ret.put("piva", datiRaggruppamento.get(9).getStringValue());
          ret.put("proimp", datiRaggruppamento.get(10).getStringValue());
          ret.put("capimp", datiRaggruppamento.get(11).getStringValue());
        }
      } else {
        ret.put("codiceFiscale", datiImpr.get(2).getStringValue());
        String indirizzoResidenza = datiImpr.get(3).getStringValue();
        String numeroCivico= datiImpr.get(4).getStringValue();
        if (indirizzoResidenza != null && !"".equals(indirizzoResidenza) && numeroCivico!=null){
          indirizzoResidenza += ", " + numeroCivico;
        }
        ret.put("indirizzoResidenza", indirizzoResidenza);
        ret.put("comuneResidenza", datiImpr.get(5).getStringValue());
        ret.put("codiceComuneResidenza", datiImpr.get(6).getStringValue());
        ret.put("cognomeIntestazione",datiImpr.get(1).getStringValue());
        ret.put("codice", datiImpr.get(7).getStringValue());
        ret.put("emaiip", datiImpr.get(8).getStringValue());
        ret.put("emai2ip", datiImpr.get(9).getStringValue());
        ret.put("piva", datiImpr.get(10).getStringValue());
        ret.put("proimp", datiImpr.get(11).getStringValue());
        ret.put("capimp", datiImpr.get(12).getStringValue());
      }

    }
    return ret;
  }


  /**
   * Contestualmente all'inserimento del documento avviene l'inserimento del fascicolo
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param idutente
   * @param idutenteunop
   * @param protocolloDocumentoIn
   * @param servizio
   * @param codiceAoo
   * @param codiceufficio
   * @return WSDMProtocolloDocumentoResType
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType WSDMDocumentoInserisci(String username, String password, String ruolo, String nome,
      String cognome, String codiceUO, String idutente, String idutenteunop, WSDMProtocolloDocumentoInType protocolloDocumentoIn,
      String servizio, String codiceAoo, String codiceufficio, String idconfi) throws GestoreException{
    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      if(codiceAoo!=null){
        WSDMLoginTitAttrType loginTitAttr = new WSDMLoginTitAttrType();
        loginTitAttr.setCodiceAmministrazioneAoo(codiceAoo);
        if(codiceufficio!=null && !"".equals(codiceufficio))
          loginTitAttr.setCodiceUfficio(codiceufficio);
        loginAttr.setLoginTitAttr(loginTitAttr);
      }

      wsdmProtocolloDocumentoRes = wsdm.WSDMDocumentoInserisci(loginAttr, protocolloDocumentoIn);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante l'inserimento del documento: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.documentoinserisci.remote.error", t);
    }

    return wsdmProtocolloDocumentoRes;
  }

  /**
   *
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param idutente
   * @param idutenteunop
   * @param fascicoloIn
   * @param servizio
   * @param codiceaoo
   * @param codiceUfficio
   * @return WSDMFascicoloResType
   * @throws GestoreException
   */
  public WSDMFascicoloResType WSDMFasciloInserisci(String username, String password, String ruolo, String nome,
      String cognome, String codiceUO, String idutente, String idutenteunop, WSDMFascicoloInType fascicoloIn,
      String servizio, String codiceaoo, String codiceUfficio, String idconfi) throws GestoreException{
    WSDMFascicoloResType wsdmFascicoloRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      WSDMLoginTitAttrType loginTitAttr = new WSDMLoginTitAttrType();
      loginTitAttr.setCodiceAmministrazioneAoo(codiceaoo);
      if(codiceUfficio!=null && !"".equals(codiceUfficio))
        loginTitAttr.setCodiceUfficio(codiceUfficio);
      loginAttr.setLoginTitAttr(loginTitAttr);
      wsdmFascicoloRes = wsdm.WSDMFascicoloInserisci(loginAttr, fascicoloIn);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante l'inserimento del fascicolo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.fascicoloinserisci.remote.error", t);
    }

    return wsdmFascicoloRes;
  }

  public String getCodiceFascicoloDaWsfascicolo(String entita, String key1) throws GestoreException{
    String codice ="";
    try {
        codice =(String)this.sqlManager.getObject("select codice from wsfascicolo where entita=? and key1=?", new Object[]{entita, key1});
    } catch (SQLException e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fascicolo: " + e.getMessage(),
          "wsdm.fascicoloprotocollo.fascicololeggi.remote.error", e);
    }


    return codice;

  }

  /**
   * Invio mail dal documentale. Viene restituito l'esito dell'invio
   *
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param idutente
   * @param idutenteunop
   * @param parametriIn
   * @return WSDMInviaMailResType
   * @throws GestoreException
   */
  public WSDMInviaMailResType wsdmInviaMail(String username, String password, String ruolo, String nome,
      String cognome, String codiceUO, String idutente, String idutenteunop, WSDMInviaMailType parametriIn, String idconfi)  {
    WSDMInviaMailResType wsdmInvicaMailRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      //wsdmProtocolloDocumentoRes = wsdm.WSDMProtocolloInserisci(loginAttr, protocolloDocumentoIn);
      wsdmInvicaMailRes = wsdm.WSDMInviaMail(loginAttr, parametriIn);
    } catch (Throwable t) {
      wsdmInvicaMailRes = new WSDMInviaMailResType();
      wsdmInvicaMailRes.setEsito(false);
      wsdmInvicaMailRes.setMessaggio(t.getMessage());
    }
    return wsdmInvicaMailRes;
  }

  /**
   * Verifica se sono presenti le condizioni per l'invio mail dal documentale.
   *
   * @param idprg
   * @param configurazione
   * @return boolean
   * @throws GestoreException
   */
  public boolean abilitatoInvioMailDocumentale(String configurazione, String idconfi) throws GestoreException {
    boolean abilitazioneInvioMail = false;
    boolean integrazioneWSDM = false;
    String valoreWSDM = ConfigManager.getValore("wsdmconfigurazione.fascicoloprotocollo.url."+idconfi);
    if(valoreWSDM!=null && !"".equals(valoreWSDM))
      integrazioneWSDM=true;

    boolean delegaInvioMailDocumentaleAbilitata = false;
    valoreWSDM = ConfigManager.getValore("pg.wsdm.invioMailPec."+idconfi);
    if(valoreWSDM!=null && "1".equals(valoreWSDM))
      delegaInvioMailDocumentaleAbilitata=true;

    String tipoWSDM=null;
    if(integrazioneWSDM && delegaInvioMailDocumentaleAbilitata){
      WSDMConfigurazioneOutType config = this.wsdmConfigurazioneLeggi(configurazione,idconfi);
      if (config.isEsito())
        tipoWSDM = config.getRemotewsdm();
    }

    if(integrazioneWSDM && delegaInvioMailDocumentaleAbilitata && !"IRIDE".equals(tipoWSDM))
      abilitazioneInvioMail= true;

    return abilitazioneInvioMail;
  }

  /**
   * Viene effettuato l'invio della mail tramite il documentale a tutti i destinatari della comunicazione(W_INVCOMDES), e viene aggiornato lo stato della
   * W_INVCOMDES.
   * Se tutte le mail vengono inviate senza errori, viene ritornato il codice di successo "10", altrimenti "11".
   *
   * @param idprg
   * @param configurazione
   * @param idcom
   * @param datiWSDM
   * @param allegati
   * @param numAllegatiReali
   * @return String
   * @throws GestoreException, SQLException
   */
  public String wsdmInvioMailEAggiornamentoDb(String idprg, String configurazione, String idcom, HashMap<String,String> datiWSDM, WSDMProtocolloAllegatoType[] allegati, int numAllegatiReali) throws GestoreException, SQLException{
    String statoComunicazione = "2";
    String desstato=null;
    String msgErroreInvioMail=null;
    String tipoWSDM = null;
    String numeroProtocollo = null;
    String annoProtocollo = null;
    String protocolloMail = null;
    String oggettoMail = null;
    String idconfi = datiWSDM.get("idconfi");

    if(this.abilitatoInvioMailDocumentale(configurazione,idconfi)){
      WSDMConfigurazioneOutType config = this.wsdmConfigurazioneLeggi(configurazione,idconfi);
      if (config.isEsito()){
        tipoWSDM = config.getRemotewsdm();
      }
      WSDMInviaMailType parametriMailIn = new WSDMInviaMailType();
      parametriMailIn.setNumeroDocumento(datiWSDM.get("numeroDocumento"));
      annoProtocollo = datiWSDM.get("annoProtocollo");
      annoProtocollo = UtilityStringhe.convertiNullInStringaVuota(annoProtocollo);
      Long anno=null;
      if(datiWSDM.get("annoProtocollo")!=null)
        anno=new Long(datiWSDM.get("annoProtocollo"));
      parametriMailIn.setAnnoProtocollo(anno);
      numeroProtocollo = datiWSDM.get("numeroProtocollo");
      numeroProtocollo = UtilityStringhe.convertiNullInStringaVuota(numeroProtocollo);
      parametriMailIn.setNumeroProtocollo(datiWSDM.get("numeroProtocollo"));
      oggettoMail = datiWSDM.get("oggettodocumento");
      protocolloMail = numeroProtocollo;
      protocolloMail = UtilityStringhe.convertiNullInStringaVuota(protocolloMail);
      if("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM)){
        oggettoMail = UtilityStringhe.convertiNullInStringaVuota(oggettoMail);
        if("JIRIDE".equals(tipoWSDM)){
          protocolloMail = UtilityStringhe.fillLeft(protocolloMail, '0', 7);
        }
        oggettoMail = "Prot.N." + protocolloMail + "/" + annoProtocollo + " - " + oggettoMail;
      }
      parametriMailIn.setOggettoMail(oggettoMail);
      parametriMailIn.setTestoMail(datiWSDM.get("testoMail"));
      parametriMailIn.setMittenteMail(datiWSDM.get("indirizzomittente"));
      String formatoMail = datiWSDM.get("formatoMail");
      if ("1".equals(formatoMail))
        parametriMailIn.setFormatoMail(WSDMMailFormatoType.HTML);
      else
        parametriMailIn.setFormatoMail(WSDMMailFormatoType.TEXT);

      String selectW_INVCOMDES = "select desmail, idcomdes from w_invcomdes where idprg = ? and idcom = ?  and (descc is null or descc <>1)";
      if("PALEO".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM) || "INFOR".equals(tipoWSDM)){
        if("ARCHIFLOW".equals(tipoWSDM)){
          String dest[] = null;
          List<?> datiW_INVCOMDES = this.sqlManager.getListVector(selectW_INVCOMDES, new Object[] { idprg, idcom });
          if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
            //Si deve impostare il vettore dei destinatari
            dest = new String[datiW_INVCOMDES.size()];
            for (int i = 0; i < datiW_INVCOMDES.size(); i++)
              dest[i] = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 0).getValue();
          }
          parametriMailIn.setDestinatariMail(dest);
        }
        if("INFOR".equals(tipoWSDM)){
          parametriMailIn.setCodiceRegistro(datiWSDM.get("codiceregistrodocumento"));
        }

        WSDMInviaMailResType wsdmInviaMailResType = this.wsdmInviaMail(datiWSDM.get("username"), datiWSDM.get("password"), datiWSDM.get("ruolo"),
          datiWSDM.get("nome"), datiWSDM.get("cognome"), datiWSDM.get("codiceuo"), null, null, parametriMailIn,idconfi);
        if(wsdmInviaMailResType.isEsito()){
          statoComunicazione = "10";
          desstato = "4";
        }else{
          statoComunicazione = "11";
          desstato = "5";
          msgErroreInvioMail = wsdmInviaMailResType.getMessaggio();
        }
        String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore =?, desdatinv = ? where idprg = ? and idcom = ? and (descc is null or descc <>1)";
        this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idprg, idcom });
      }else if("ENGINEERING".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM)){
        statoComunicazione = "10";
        desstato = "4";
        String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore =?, desdatinv = ? where idprg = ? and idcom = ? and (descc is null or descc <>1)";
        this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idprg, idcom });
      }else{
        //JIRIDE si deve inviare una mail per ogni destinatario
        //anche per EASYDOC
        if("JIRIDE".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM)  || "ARCHIFLOWFA".equals(tipoWSDM)){
          boolean invioMailOk = true;

          List<?> datiW_INVCOMDES = this.sqlManager.getListVector(selectW_INVCOMDES, new Object[] { idprg, idcom });
          if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
            WSDMInviaMailResType wsdmInviaMailResType = null;
            String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore =?, desdatinv = ? where idprg = ? and idcom = ? and idcomdes = ? and (descc is null or descc <>1)";
            WSDMProtocolloAllegatoType[] allegatiReali = null;
            if("EASYDOC".equals(tipoWSDM)){
              boolean tabellatiInDB = this.isTabellatiInDb();
              String mailChannelCode = this.getcodiceTabellato(configurazione, "mailchannelcode",idconfi, tabellatiInDB);
              parametriMailIn.setMailChannelCode(mailChannelCode);
              String mailConfigurationCode=this.getcodiceTabellato(configurazione, "mailconfigurationcode",idconfi, tabellatiInDB);
              parametriMailIn.setMailConfigurationCode(mailConfigurationCode);
              allegatiReali= this.getAllegatiReali(allegati, numAllegatiReali,idconfi);
              parametriMailIn.setAllegati(allegatiReali);
            }
            for (int i = 0; i < datiW_INVCOMDES.size(); i++) {
              String delayPec = ConfigManager.getValore("wsdm.invioMailPec.delay."+idconfi);
              if(!"".equals(delayPec) && delayPec != null && i > 0){
                try {
                  TimeUnit.MILLISECONDS.sleep(Integer.parseInt(delayPec));
                } catch (InterruptedException e) {
                  logger.error("Errore durante l'attesa tra l'invio delle mail in carico al documentale", e);
                }
              }
              String desmail = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 0).getValue();
              Long idcomdes = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 1).getValue();
              parametriMailIn.setDestinatariMail(new String[]{desmail});
              wsdmInviaMailResType = this.wsdmInviaMail(datiWSDM.get("username"), datiWSDM.get("password"), datiWSDM.get("ruolo"),
                  datiWSDM.get("nome"), datiWSDM.get("cognome"), datiWSDM.get("codiceuo"), null, null, parametriMailIn,idconfi);
              if(wsdmInviaMailResType.isEsito()){
                msgErroreInvioMail = null;
                desstato = "4";
              }else{
                invioMailOk = false;
                msgErroreInvioMail = wsdmInviaMailResType.getMessaggio();
                desstato = "5";
              }
              try {
                this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idprg, idcom, idcomdes });
              }catch (SQLException e) {
                //Nel caso vi sia un errore nell'aggiornamento di INCOMDES non si deve bloccare il ciclo ma si deve proseguire
              }
            }
            if(invioMailOk)
              statoComunicazione = "10";
            else
              statoComunicazione = "11";
          }
        }
      }

    }

    return  statoComunicazione;
  }

  /**
   * Viene letta la property per l'abilitazione per l'invio per singolo destinatario
   *
   * @return String
   */
  public static boolean getAbilitazioneInvioSingolo(String idconfi){
    boolean result = false;
    String valore = ConfigManager.getValore("wsdm.protocolloSingoloInvito."+idconfi);
    if("1".equals(valore))
      result= true;
    return result;
  }

  /**
   * Verifica mail dal documentale. Viene restituito il numero delle ricevute di accettazione e di consegna
   *
   * @param syscon
   * @param numeroDocumento
   * @param annoProtocollo
   * @param numeroProtocollo
   * @return WSDMVerificaMailResType
   * @throws GestoreException
   */
  public WSDMVerificaMailResType wsdmVerificaMail(Long syscon, String idcom, String idprg, String idconfi) throws GestoreException {
    WSDMVerificaMailResType wsdmVerificaMailRes = null;
    try {

      String sql = "select wi.comnumprot,wi.comdatprot,wd.numerodoc,wi.comstato from w_invcom wi,wsallegati ws, wsdocumento wd " +
            "where ws.entita = ? and ws.key1 = wi.idprg and ws.key2 = wi.idcom and wi.idprg = ? and wi.idcom = ? and wd.id = ws.idwsdoc";

        Vector datiProtocollo =  sqlManager.getVector(sql,new Object[] { "W_INVCOM", idprg, idcom });
        if(datiProtocollo != null && datiProtocollo.size()>0){
          String numeroProtocollo = ((JdbcParametro) datiProtocollo.get(0)).getStringValue();
          Date dataProtocollo = (Date) ((JdbcParametro) datiProtocollo.get(1)).getValue();
          String numeroDocumento = ((JdbcParametro) datiProtocollo.get(2)).getStringValue();
          String annoProtocollo = UtilityDate.convertiData(dataProtocollo, UtilityDate.FORMATO_AAAAMMGG).substring(0,4);
          String statoComunicazione = ((JdbcParametro) datiProtocollo.get(3)).getStringValue();
          statoComunicazione = UtilityStringhe.convertiNullInStringaVuota(statoComunicazione);
          if("10".equals(statoComunicazione) || "11".equals(statoComunicazione)){

            List<?> datiWSLogin = sqlManager.getVector(
                "select username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop from wslogin" +
                " where syscon = ? and servizio = ? and idconfiwsdm = ?", new Object[] { syscon, SERVIZIO_FASCICOLOPROTOCOLLO,new Long(idconfi) });
            // CF 10022017 Su indicazione di S.Santi , se non sono presenti le credenziali per l'accesso al servizio dell'utente
            // in sessione si recuperano le eventuali credenziali di un utente qualsiasi.
            if (!(datiWSLogin != null && datiWSLogin.size() > 0)) {
              datiWSLogin = sqlManager.getVector(
                  "select username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop from wslogin" +
                  " where servizio = ? and idconfiwsdm = ?", new Object[] { SERVIZIO_FASCICOLOPROTOCOLLO, new Long(idconfi) });
            }

            if (datiWSLogin != null && datiWSLogin.size() > 0) {
              String username = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 0).getValue();
              String password = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 1).getValue();

              String passwordDecoded = null;
              if (password != null && password.trim().length() > 0) {
                ICriptazioneByte passwordICriptazioneByte = null;
                passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
                    ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
                    ICriptazioneByte.FORMATO_DATO_CIFRATO);
                passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
              }else{
                passwordDecoded = "";
              }

              String ruolo = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 2).getValue();
              String nome = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 3).getValue();
              String cognome = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 4).getValue();
              String codiceuo = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 5).getValue();

              WSDM_PortType wsdm = this.getWSDM(username, passwordDecoded, SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);
              WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
              loginAttr.setRuolo(ruolo);
              loginAttr.setNome(nome);
              loginAttr.setCognome(cognome);
              loginAttr.setCodiceUO(codiceuo);
              wsdmVerificaMailRes = wsdm.WSDMVerificaMail(loginAttr, numeroDocumento, new Long(annoProtocollo), numeroProtocollo);
            }

          }
        }

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante l'invio della mail: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.inviomail.remote.error", t);
    }
    return wsdmVerificaMailRes;
  }


  /**
   * Verifica letto il tipo di WSDM attivo per il servizio specificato.
   *
   * @param servizio
   * @return String
   * @throws GestoreException
   */
  public String getTipoWSDM(String servizio,String idconfi) throws GestoreException{
    String tipoWSDM="";
    WSDMConfigurazioneOutType config = this.wsdmConfigurazioneLeggi(servizio,idconfi);
    if (config.isEsito())
      tipoWSDM = config.getRemotewsdm();

    return tipoWSDM;
  }

  /**
   * Verifica se è attiva l'integrazione WSDM ed è valida in base al servizio specificato.
   * Per ENGINEERING si considera solo il servzio = FASCICOLOPROTOCOLLO, mentre per
   * ENGINEERINGDOC solo servizio = DOCUMENTALE
   *
   * @param servizio
   * @param codice
   * @return boolean
   * @throws GestoreException
   * @throws SQLException
   */
  public boolean isIntegrazioneWSDMAttivaValida(String servizio, String idconfi) throws GestoreException, SQLException{
    boolean ret=false;

    if(idconfi != null && !idconfi.equals("")){
      String url=null;
      if (SERVIZIO_FASCICOLOPROTOCOLLO.equals(servizio)) {
        url = ConfigManager.getValore(PROP_WSDMCONFIGURAZIONE_FASCICOLOPROTOCOLLO_URL + idconfi);
      }else if (SERVIZIO_DOCUMENTALE.equals(servizio)) {
        url = ConfigManager.getValore(PROP_WSDMCONFIGURAZIONE_DOCUMENTALE_URL + idconfi);
      }
      if(url!=null && !"".equals(url)){
        WSDMConfigurazioneOutType config = this.wsdmConfigurazioneLeggi(servizio,idconfi);
        if (config.isEsito()){
          ret=true;

        }
      }
    }

    return ret;
  }


  /**
   * Verifica se da configurazione risulta attiva la funzione Fascicolo documentale ed il WSDM è configurato.
   * VIENE escluso ENGINEERINGDOC
   *
   * @param codice
   * @return boolean
   * @throws GestoreException
   *  @throws SQLException
   */
  public boolean isFascicoloDocumentaleAbilitato(String codice, String idconfi) throws GestoreException, SQLException{
    boolean ret=false;
    String accediFascicoloDocumentale=ConfigManager.getValore(PROP_WSDM_ACCEDIFASCICOLODOCUMENTALE + idconfi);
    if("1".equals(accediFascicoloDocumentale)){
      ret=this.isWSDMAbilitato(codice,idconfi);
    }
    return ret;
  }

  /**
  * Verifica se da configurazione risulta attiva la funzione Applica Fascicolazione ed il WSDM è configurato.
  * VIENE escluso ENGINEERINGDOC
  *
  * @param codice
  * @return boolean
  * @throws GestoreException
  *  @throws SQLException
  */
  public boolean isApplicaFascicolazioneAbilitato(String codice, String idconfi) throws GestoreException, SQLException{
    boolean ret=false;
    String applicaFascicolazione=ConfigManager.getValore(PROP_WSDM_APPLICAFASCICOLAZIONE + idconfi);
    if("1".equals(applicaFascicolazione)){
     ret=this.isWSDMAbilitato(codice,idconfi);
    }
    return ret;
  }


  /**
   * Verifica se il WSDM è configurato, ossia è specicificato o l'url per il fascicolo protocollo
   * o l'url per il documentale.
   * Viene escluso ENGINEERINGDOC
   *
   * @param codice
   * @return boolean
   * @throws GestoreException
   * @throws SQLException
   */
  public boolean isWSDMAbilitato(String codice,String idconfi) throws GestoreException, SQLException{
    boolean ret=false;
    String url = ConfigManager.getValore(PROP_WSDM_FASCICOLOPROTOCOLLO_URL + idconfi.toString());
    String servizio = SERVIZIO_FASCICOLOPROTOCOLLO;
    if(url==null || "".equals(url)){
      url = ConfigManager.getValore(PROP_WSDM_DOCUMENTALE_URL + idconfi.toString());
      servizio=SERVIZIO_DOCUMENTALE;
    }
    if(url!=null && !"".equals(url)){
      WSDMConfigurazioneOutType config = this.wsdmConfigurazioneLeggi(servizio,idconfi);
      if (config.isEsito()){
        ret = true;
      }
    }
    return ret;
  }

  /**
   * Viene controllato se è impostato l'url di configurazione del WSDM
   * @param servizio
   * @return boolean
   */
  public boolean isWsdmConfigurato(String servizio, String idconfi){
    boolean ret=false;

    String url=null;
    if (SERVIZIO_FASCICOLOPROTOCOLLO.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSDMCONFIGURAZIONE_FASCICOLOPROTOCOLLO_URL+idconfi);
    }else if (SERVIZIO_DOCUMENTALE.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSDMCONFIGURAZIONE_DOCUMENTALE_URL+idconfi);
    }
    if(url!=null && !"".equals(url))
      ret = true;

    return ret;
  }

  /**
   * Controlla se esiste l'occorrenza in WSFASCICOLO
   *
   * @param entita
   * @param key1
   * @param key2
   * @param key3
   * @param key4
   * @throws SQLException
   */
  public boolean esisteWSFascicolo(String entita, String key1, String key2, String key3, String key4)
      throws SQLException {

    boolean ret = false;
    if (entita != null && !"".equals(entita) && key1 != null && !"".equals(key1)) {

      String whereWSFASCICOLO = " entita = ? and key1 = ? ";
      Object[] params = new Object[2];
      params[0] = entita;
      params[1] = key1;

      if (key2 != null && !"".equals(key2)) {
        whereWSFASCICOLO += " and key2 = '" + key2 + "' ";
      } else {
        whereWSFASCICOLO += " and key2 is null ";
        key2=null;
      }

      if (key3 != null && !"".equals(key3)) {
        whereWSFASCICOLO += " and key3 = '" + key3 + "' ";
      } else {
        whereWSFASCICOLO += " and key3 is null ";
        key3=null;
      }

      if (key4 != null && !"".equals(key4)) {
        whereWSFASCICOLO += " and key4 = '" + key4 + "' ";
      } else {
        whereWSFASCICOLO += " and key4 is null ";
        key4=null;
      }

      String countWSFASCICOLO = "select count(*) from wsfascicolo where " + whereWSFASCICOLO;
      Long cnt = (Long) this.sqlManager.getObject(countWSFASCICOLO, new Object[] { entita, key1 });
      if (cnt != null && cnt.longValue() > 0)
       ret=true;
    }

    return ret;
  }

  /**
   * Estrae dall'occorrenza in WSFASCICOLO i campi codice, anno, numero,
   * restituendoli in un VECTOR rispettivamente in posizione 0,1 e 2
   *
   * @param entita
   * @param key1
   * @param key2
   * @param key3
   * @param key4
   * @return Vector
   * @throws SQLException
   */
  public Vector<?> getDatiWsfascicolo (String entita, String key1, String key2, String key3, String key4)
      throws SQLException {

    Vector<?> ret = null;
    if (entita != null && !"".equals(entita) && key1 != null && !"".equals(key1)) {

      String whereWSFASCICOLO = " entita = ? and key1 = ? ";
      Object[] params = new Object[2];
      params[0] = entita;
      params[1] = key1;

      if (key2 != null && !"".equals(key2)) {
        whereWSFASCICOLO += " and key2 = '" + key2 + "' ";
      } else {
        whereWSFASCICOLO += " and key2 is null ";
        key2=null;
      }

      if (key3 != null && !"".equals(key3)) {
        whereWSFASCICOLO += " and key3 = '" + key3 + "' ";
      } else {
        whereWSFASCICOLO += " and key3 is null ";
        key3=null;
      }

      if (key4 != null && !"".equals(key4)) {
        whereWSFASCICOLO += " and key4 = '" + key4 + "' ";
      } else {
        whereWSFASCICOLO += " and key4 is null ";
        key4=null;
      }

      String select = "select codice, anno, numero from wsfascicolo where " + whereWSFASCICOLO;
      ret = this.sqlManager.getVector(select,new Object[]{entita, key1});
    }

    return ret;
  }


  /**
   * Chiamata al servizio remoto per ottenere la lista delle amministrazioni AOO.
   *
   * @param username
   * @param password
   * @param servizio
   * @param codiceAOO
   * @param ruolo
   * @param descrizioneUfficio
   * @param utente
   * @param idconfi
   * @return WSDMListaAmministrazioniAooResType
   * @throws GestoreException
   */
  public WSDMListaUfficiResType wsdmGetListaUffici(String username, String password, String servizio, String codiceAOO,String ruolo, String descrizioneUfficio, String utente, String idconfi) throws GestoreException {
    WSDMListaUfficiResType  listaUffici = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      if(ruolo!=null)
        loginAttr.setRuolo(ruolo);
      if(codiceAOO!=null)
        codiceAOO=codiceAOO.substring(codiceAOO.length()-3);

      listaUffici= wsdm.WSDMListaUffici(loginAttr,codiceAOO, descrizioneUfficio, utente);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura della lista degli uffici: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.uffici.remote.error", t);
    }
    return listaUffici;
  }


  /**
   * Chiamata al servizio remoto per ottenere la lista delle amministrazioni AOO.
   *
   * @param username
   * @param password
   * @param servizio
   * @return WSDMListaAmministrazioniAooResType
   * @throws GestoreException
   */
  public WSDMListaAmministrazioniAooResType wsdmGetListaAmministrazioniAoo(String username, String password, String servizio, String idconfi) throws GestoreException {
    WSDMListaAmministrazioniAooResType  listaAoo = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio,idconfi);
      listaAoo = wsdm.WSDMListaAmministrazioniAoo();

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura della lista delle amministrazioni AOO: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.amministrazioniAOO.remote.error", t);
    }
    return listaAoo;
  }

  /**
   * Controlla se esiste l'occorrenza in WSFASCICOLO
   *
   * @param entita
   * @param key1
   * @param key2
   * @param key3
   * @param key4
   * @throws SQLException
   */
  public HashMap<String, Object> getGenereCodiceGara(String chiave)
      throws SQLException {

    HashMap<String, Object> ret = new HashMap<String, Object>();
    Long genereGara = null;
    String codgar = null;
    Vector datiGara = this.sqlManager.getVector("select genere,codgar1 from gare where ngara=?", new Object[]{chiave});
    if(datiGara!=null && datiGara.size()>0){
      try{
        genereGara = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
        codgar = SqlManager.getValueFromVectorParam(datiGara, 1).stringValue();
      }catch(GestoreException e){
        throw new SQLException();
      }
      if(genereGara==null)
        genereGara = (Long)this.sqlManager.getObject("select genere from v_gare_torn where codgar=?", new Object[]{codgar});
    }else{
      //La funzione potrebbe essere richiamata dalla tornata, quindi in chiave ho il valore di codgar e no ngara
      genereGara = (Long)this.sqlManager.getObject("select genere from v_gare_torn where codgar=?", new Object[]{chiave});
      if(genereGara!=null && genereGara.longValue()==1)
        codgar=chiave;
    }
    ret.put("genereGara", genereGara);
    ret.put("codgar", codgar);
    return ret;
  }

  public String formattazioneDestinatarioPrincipale(String destinatario){
    if(destinatario.length()>4000){
      destinatario = destinatario.substring(0, 3990);
      destinatario +=", Altri...";
    }
    return destinatario;
  }

  /**
   * Viene determinato il valore della data protocollo.
   * Se non è presente nell'oggetto wsdmProtocolloDocumentoRes allora si considera la data attuale.
   *
   * @param wsdmProtocolloDocumentoRes
   * @return Timestamp
   */
  public Timestamp getDataProtocollo(WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes){
    Timestamp dataProtocollo= new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    if(wsdmProtocolloDocumentoRes.getProtocolloDocumento().getDataProtocollo()!=null){
      Calendar dataProt = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getDataProtocollo();
      dataProtocollo = new Timestamp(dataProt.getTime().getTime());
    }
    return dataProtocollo;
  }

  /**
   * viene restituita l'anno in formato Long da una data in formato Timestamp
   * @param data
   * @return Long
   */
  public Long getAnnoFromDate(Timestamp data){
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(data.getTime());
    return new Long(calendar.get(Calendar.YEAR));
  }


  /**
   * Estrazione dei valori del tabellato di interesse
   * @param servizio
   * @param nome
   * @return
   * @throws GestoreException
   */
  public List<String[]> getValoriTabellato(String servizio,String nome,String idconfi, boolean tabellatiInDB) throws GestoreException{
    List<String[]> listaTabellati = new Vector<String[]>();
    WSDMConfigurazioneOutType configurazione;
    configurazione = this.wsdmConfigurazioneLeggi(servizio,idconfi);
    if (configurazione.isEsito()) {
      if(tabellatiInDB){
        List <TabellatoWsdm> tabellati = this.tabellatiManager.getTabellatiWsdm(new Long(idconfi), configurazione.getRemotewsdm(), nome);
        if (tabellati != null && tabellati.size() > 0) {
          for (int e = 0; e < tabellati.size(); e++) {
            String[] row = new String[2];
            row[0] = tabellati.get(e).getValore();
            row[1] = tabellati.get(e).getDescri();
            listaTabellati.add(row);
          }
        }
      }else if (configurazione.getTabellati() != null) {
        WSDMTabellatoType[] wsdmTabellati = configurazione.getTabellati();
        if (wsdmTabellati != null && wsdmTabellati.length > 0) {
          for (int t = 0; t < wsdmTabellati.length; t++) {
            if (nome.equals(wsdmTabellati[t].getNome())) {
              WSDMTabellatoElementoType[] elementi = wsdmTabellati[t].getElementi();
              if (elementi != null && elementi.length > 0) {
                for (int e = 0; e < elementi.length; e++) {
                  String[] row = new String[2];
                  row[0] = elementi[e].getCodice();
                  row[1] = elementi[e].getDescrizione();
                  listaTabellati.add(row);
                }
              }
            }
          }
        }
      }
    }
    return listaTabellati;
  }

  /**
   * Viene restituito il codice del primo valore di un tabellato
   * @param servizio
   * @param nome
   * @return
   * @throws GestoreException
   */
  public String getcodiceTabellato(String servizio, String nome, String idconfi, boolean tabellatiInDB) throws GestoreException{
    String codice="";
    List<String[]> listaValori = this.getValoriTabellato(servizio, nome,idconfi, tabellatiInDB);
    if(listaValori!=null && listaValori.size()>0){
      codice= listaValori.get(0)[0];
    }
    return codice;
  }

  /**
   * Viene restituito la descrizione del primo valore di un tabellato
   * @param servizio
   * @param nome
   * @return String
   * @throws GestoreException
   */
  public String getDescrizioneTabellato(String servizio, String nome, String idconfi, boolean tabellatiInDB) throws GestoreException{
    String desrizione="";
    List<String[]> listaValori = this.getValoriTabellato(servizio, nome, idconfi, tabellatiInDB);
    if(listaValori!=null && listaValori.size()>0){
      desrizione= listaValori.get(0)[1];
    }
    return desrizione;
  }


  /**
   * Nel vettore degli allegati passato come parametro in ingresso è presente anche il testo della comunicazione, che
   * può essere o in prima o in ultima posizione, in base al valore della proprietà "wsdm.posizioneAllegatoComunicazione".
   * Il metodo si occupa di eliminarlo dal vettore
   * @param allegati
   * @param numAllegatiReali
   * @param idconfi
   * @return WSDMProtocolloAllegatoType[]
   */
  public WSDMProtocolloAllegatoType[] getAllegatiReali(WSDMProtocolloAllegatoType[] allegati, int numAllegatiReali,String idconfi){
    WSDMProtocolloAllegatoType[] allegatiReali = null;
    //Se posizioneAllegatoComunicazione=1 il testo della comunicazione è in prima posizione, altrimenti è in ultima posizione
    if(numAllegatiReali>0){
      allegatiReali =new WSDMProtocolloAllegatoType[numAllegatiReali];
      //Si devono inviare gli allegati reali, togliendo il testo della comunicazione
      String posizioneAllegatoComunicazione = ConfigManager.getValore("wsdm.posizioneAllegatoComunicazione." + idconfi);
      int indicePartenza=0;
      int indiceFine = numAllegatiReali;
      if("1".equals(posizioneAllegatoComunicazione)) {
        indicePartenza=1;
        indiceFine = indiceFine +1;
      }
      for(int z=indicePartenza; z<indiceFine; z++){
        allegatiReali[z] = new WSDMProtocolloAllegatoType();
        allegatiReali[z]=allegati[z];
      }
    }
    return allegatiReali;
  }

  /**
   * Estrazione dei valori del tabellato di interesse
   * @param servizio
   * @param nome
   * @return
   * @throws GestoreException
   */
  public List<String[]> getValoriClassifiche(String username, String password, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, String servizio, String classifica, String descrClassifica, String voce, String idconfi) throws GestoreException{

    List<String[]> listaTabellati = new Vector<String[]>();
    WSDMListaClassificheResType risposta = this.wsdmListaClassifiche(username, password, ruolo, nome, cognome, codiceUO, idutente, idutenteunop, servizio, classifica, descrClassifica,voce, idconfi);
    if(risposta.isEsito()){
      WSDMClassificaType classifiche[] = risposta.getListaClassifiche();
      if(classifiche!=null && classifiche.length>0){
        String[] row=null;
        for(int i=0;i<classifiche.length;i++){
          row = new String[3];
          row[0] = classifiche[i].getCodice();
          row[1] = classifiche[i].getDescrizione();
          row[2] = classifiche[i].getVoce();
          listaTabellati.add(row);
        }
      }
    }
    return listaTabellati;
  }

  /**
   * Lettura delle classifiche, funzionalità attiva solo per PRISMA
   *
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param idutente
   * @param idutenteunop
   * @param servizio
   * @param classifica
   * @param descrClassifica
   * @return
   * @throws GestoreException
   */
  public WSDMListaClassificheResType wsdmListaClassifiche(String username, String password, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, String servizio, String classifica, String descrClassifica, String voce, String idconfi) throws GestoreException {
    WSDMListaClassificheResType wsdmClassificheRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);
      //Per TITULUS per potere leggere i dati del fascicolo si deve valorizzare il numero del profilo
      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }
      wsdmClassificheRes = wsdm.WSDMListaClassifiche(loginAttr, classifica, descrClassifica, voce );
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fascicolo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.fascicololeggi.remote.error", t);
    }
    return wsdmClassificheRes;
  }

  /**
   * Lettura delle credenziali per una login comune per un determinato servizio
   * idconfi = identificativo della configurazione wsdm
   *
   * @param servizio
   *
   * @return array (Dati Login)
   * @throws GestoreException
   */
  public String[] getWSDMLoginComune(String servizio, String idconfi)throws GestoreException{
    String[] datiWSLogin = new String[3];
    Long syscon = new Long(-1);

    String filtroIdconfi = "";
    if(idconfi != null && !"".equals(idconfi)){
      filtroIdconfi = " and idconfiwsdm = " + idconfi;
    }

    try {
      List<?> datiWSLoginComune = sqlManager.getVector(
          "select username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop" +
          " from wslogin where syscon = ? and servizio = ?" + filtroIdconfi, new Object[] { syscon, servizio });
      String username = (String) SqlManager.getValueFromVectorParam(datiWSLoginComune, 0).getValue();
      String password = (String) SqlManager.getValueFromVectorParam(datiWSLoginComune, 1).getValue();

      String passwordDecoded = null;
      if (password != null && password.trim().length() > 0) {
        ICriptazioneByte passwordICriptazioneByte = null;
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
            ICriptazioneByte.FORMATO_DATO_CIFRATO);
        passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
      }

      String ruolo = (String) SqlManager.getValueFromVectorParam(datiWSLoginComune, 2).getValue();
      String nome = (String) SqlManager.getValueFromVectorParam(datiWSLoginComune, 3).getValue();
      String cognome = (String) SqlManager.getValueFromVectorParam(datiWSLoginComune, 4).getValue();
      String codiceuo = (String) SqlManager.getValueFromVectorParam(datiWSLoginComune, 5).getValue();
      String idutente = (String) SqlManager.getValueFromVectorParam(datiWSLoginComune, 6).getValue();
      String idutenteunop = (String) SqlManager.getValueFromVectorParam(datiWSLoginComune, 7).getValue();


      datiWSLogin[0] = username;
      datiWSLogin[1] = passwordDecoded;
      datiWSLogin[2] = ruolo;


    } catch (SQLException e) {
        throw new GestoreException("Si e' verificato un errore durante la lettura delle credenziali di accesso al WSDM: "
            + e.getMessage(), null, e);
    } catch (CriptazioneException e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura delle credenziali di accesso al WSDM: "
          + e.getMessage(), null, e);
    }




    return datiWSLogin;
  }

  /**
   * Lettura del documento di una Rda
   *
   * @param numeroDocumento
   *
   * @return
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType wsdmDocumentoERPLeggi(String numeroDocumento, String idconfi) throws GestoreException {

    String servizio = GestioneWSDMManager.SERVIZIO_DOCUMENTALE;
    String[] loginComune = this.getWSDMLoginComune(servizio,idconfi);
    String username = loginComune[0];
    String password = loginComune[1];
    String ruolo = loginComune[2];

    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      wsdmProtocolloDocumentoRes = wsdm.WSDMDocumentoLeggi(loginAttr, numeroDocumento);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del documento: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.documentoleggi.remote.error", t);
    }
    return wsdmProtocolloDocumentoRes;
  }

  /**
   * Modifica del fascicolo:
   * Tale funzione viene usata anche per aggiornare lo stato di una gara
   * in caso di gestioneERP con wsdm
   *
   * @param codiceFascicolo
   * @param stato
   *
   * @return
   * @throws GestoreException
   */
  public WSDMFascicoloModificaResType wsdmFascicoloERPModifica(String codiceFascicolo,String stato, Double impliq, Date dataliq, String idconfi) throws GestoreException {

    String servizio = GestioneWSDMManager.SERVIZIO_DOCUMENTALE;
    String[] loginComune = this.getWSDMLoginComune(servizio, idconfi);
    String username = loginComune[0];
    String password = loginComune[1];
    String ruolo = loginComune[2];

    WSDMFascicoloModificaResType wsdmFascicoloModificaRes = new  WSDMFascicoloModificaResType();
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      WSDMFascicoloModificaInType fascicoloModificaIn = new WSDMFascicoloModificaInType();
      fascicoloModificaIn.setCodiceFascicolo(codiceFascicolo);
      WSDMTabellaType[] datiPersonalizzati = new WSDMTabellaType[2];
      //tabella fascicolo
      WSDMTabellaType wsdmTabellaStatoGaraType  = new WSDMTabellaType();
      wsdmTabellaStatoGaraType.setNome("fascicolo");
      WSDMRigaType riga = new WSDMRigaType();
      WSDMCampoType campo = new WSDMCampoType();
      campo.setNome("stato_gara");
      campo.setTipo("string");
      campo.setValore(stato);
      WSDMCampoType[] campi = new WSDMCampoType[1];
      campi[0] = campo;
      riga.setNumero(new Long(0));
      riga.setCampo(campi);
      WSDMRigaType[] righe = new WSDMRigaType[1];
      righe[0] = riga;
      wsdmTabellaStatoGaraType.setRiga(righe);
      //tabella Liquidazione
      WSDMTabellaType wsdmTabellaL190Type  = new WSDMTabellaType();
      wsdmTabellaL190Type.setNome("somme_liquidate");
      WSDMRigaType rigaL190 = new WSDMRigaType();
      WSDMCampoType[] campiL190 = new WSDMCampoType[2];
      if(dataliq!= null){
        WSDMCampoType campoBL190 = new WSDMCampoType();
        campoBL190.setNome("data_liquidazione");
        String dataLiquidato = null;
        dataLiquidato = UtilityDate.convertiData(dataliq, UtilityDate.FORMATO_GG_MM_AAAA_CON_TRATTINI);
        dataLiquidato = dataLiquidato.replace("-","/");
        campoBL190.setValore(dataLiquidato);
        campoBL190.setTipo("date");
        campiL190[0] = campoBL190;
      }
      if(impliq!= null){
        WSDMCampoType campoAL190 = new WSDMCampoType();
        campoAL190.setNome("importo");
        String importoLiquidato = null;
        campoAL190.setTipo("double");
        //conversione e formattazione dell'importo: SE IMP NULLO?
        double doubleImpliq = impliq.doubleValue();
        importoLiquidato = UtilityNumeri.convertiDouble(doubleImpliq, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 2);
        campoAL190.setValore(importoLiquidato);
        campiL190[1] = campoAL190;
      }
      rigaL190.setNumero(new Long(0));
      rigaL190.setCampo(campiL190);
      WSDMRigaType[] righeL190 = new WSDMRigaType[1];
      righeL190[0] = rigaL190;
      wsdmTabellaL190Type.setRiga(righeL190);

      datiPersonalizzati[0] = wsdmTabellaStatoGaraType;
      datiPersonalizzati[1] = wsdmTabellaL190Type;

      fascicoloModificaIn.setDatiPersonalizzati(datiPersonalizzati);

      wsdmFascicoloModificaRes = wsdm.WSDMFascicoloModifica(loginAttr, fascicoloModificaIn );



    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fascicolo: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.documentoleggi.remote.error", t);
    }
    return wsdmFascicoloModificaRes;
  }

  /**
   * Modifica del fascicolo:
   * Tale funzione viene usata anche per aggiornare lo stato di una gara
   * in caso di gestioneERP con wsdm
   *
   * @param codiceFascicolo
   * @param stato
   *
   * @return
   * @throws GestoreException
   * @throws SQLException
   */
  public WSDMProtocolloModificaResType wsdmModificaProtocollo(String ngara, String idconfi) throws GestoreException, SQLException {

    String servizio = GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO;
    String[] loginComune = this.getWSDMLoginComune(servizio,idconfi);
    String username = loginComune[0];
    String password = loginComune[1];
    String ruolo = loginComune[2];

    List<?> listaNumDoc = this.sqlManager.getListVector("select numerodoc from wsdocumento where (entita = 'GARE' or entita = 'TORN') and (INOUT = 'IN' or INOUT = 'OUT') and key1 = ?", new Object[] { ngara });
    String[] numeroDoc = new String[listaNumDoc.size()];
    if (listaNumDoc != null && listaNumDoc.size() > 0) {
      for (int i = 0; i < listaNumDoc.size(); i++)
        numeroDoc[i] = (String) SqlManager.getValueFromVectorParam(listaNumDoc.get(i), 0).getValue();
    }

    WSDMProtocolloModificaResType wsdmProtocolloModificaRes = new  WSDMProtocolloModificaResType();
    try {
      if(numeroDoc.length > 0){
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      WSDMProtocolloModificaInType  protocolloModifica = new WSDMProtocolloModificaInType();
      protocolloModifica.setDataFineRiservatezza(null);
      protocolloModifica.setNumeroDocumento(numeroDoc);
      protocolloModifica.setLivelloRiservatezza("");
      wsdmProtocolloModificaRes = wsdm.WSDMProtocolloModifica(loginAttr,  protocolloModifica);
      }else{
        wsdmProtocolloModificaRes.setEsito(true);
        wsdmProtocolloModificaRes.setMessaggio("Nessun elemento documentale riservato per la gara");
      }
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la modifica dell'elemento documentale: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.documentomodifica.remote.error", t);
    }
    return wsdmProtocolloModificaRes;
  }

  /**
   * Viene effettuato l'associazione dei documenti al protocollo assegnato alle relative buste
   * Servizio disponibile solo per JIRIDE
   * @param username
   * @param password
   * @param ruolo
   * @param servizio
   * @param numeroDocumentoPadre
   * @param numeroDocumentoFiglio
   * @param tipoCollegamento
   * @return WSDMDocumentoCollegaResType
   * @throws GestoreException
   */
  public WSDMDocumentoCollegaResType WSDMDocumentoCollega(String username, String ruolo, String servizio, String numeroDocumentoPadre, String numeroDocumentoFiglio, String tipoCollegamento, String idconfi) throws GestoreException{
    WSDMDocumentoCollegaResType wsdmDocumentoCollegaRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, null, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);

      wsdmDocumentoCollegaRes = wsdm.WSDMDocumentoCollega(loginAttr, numeroDocumentoPadre, numeroDocumentoFiglio, tipoCollegamento);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante il collegamento del documento: " + t.getMessage(),
          "wsdm.documentale.documentocollega.error", t);
    }

    return wsdmDocumentoCollegaRes;
  }

  public WSDMAggiungiAllegatiResType WSDMAggiungiAllegati(String username, String ruolo, String servizio, String numeroDocumento, Integer annoProtocollo, String numeroProtocollo,WSDMProtocolloAllegatoType[] allegati, String idconfi) throws GestoreException{
    WSDMAggiungiAllegatiResType wsdmDocumentoCollegaRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, null, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);

      WSDMAggiungiAllegatiInType aggiungiAllegatiInType = new WSDMAggiungiAllegatiInType();
      aggiungiAllegatiInType.setAllegati(allegati);
      aggiungiAllegatiInType.setAnnoProtocollo(new Long(annoProtocollo));
      aggiungiAllegatiInType.setNumeroDocumento(numeroDocumento);
      aggiungiAllegatiInType.setNumeroProtocollo(numeroProtocollo);
      wsdmDocumentoCollegaRes = wsdm.WSDMAggiungiAllegati(loginAttr, aggiungiAllegatiInType);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante il collegamento del documento: " + t.getMessage(),
          "wsdm.documentale.documentocollega.error", t);
    }

    return wsdmDocumentoCollegaRes;
  }

  /**
   * Vengono letti i destinatari di una comunicazione per popolare i destinatari da inviare al WSDM
   * Viene inoltre valorizzato il destinatario principale
   * Viene restituita la lista dei destinatari della comunicazione
   *
   * @param idprg
   * @param idcom
   * @param tipoWSDM
   * @param mezzoInvio
   * @param wsdmProtocolloDocumentoIn
   * @return List<br>
   *           desintest<br>
   *           descodent<br>
   *           descodsog<br>
   *           desmail<br>
   *           idcomdes<br>
   * @throws SQLException
   * @throws GestoreException
   */
  public List<?>  popolaDestinatariWSDM(String idprg, Long idcom, String tipoWSDM, String mezzoInvio, WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn) throws SQLException, GestoreException{
    String selectW_INVCOMDES = "select desintest, descodent, descodsog, desmail, idcomdes from w_invcomdes where idprg = ? and idcom = ?  and (descc is null or descc <>1)";
    List<?> datiW_INVCOMDES = this.sqlManager.getListVector(selectW_INVCOMDES, new Object[] { idprg, idcom });
    String destinatarioPrincipale="";
    if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
      WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[datiW_INVCOMDES.size()];
      for (int i = 0; i < datiW_INVCOMDES.size(); i++) {
        String descodesintestdent = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 0).getValue();
        String descodent = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 1).getValue();
        String descodsog = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 2).getValue();
        String desmail = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 3).getValue();
        destinatarioPrincipale+=descodesintestdent;
        if(i<datiW_INVCOMDES.size()-1)
          destinatarioPrincipale+= ", ";
        destinatari[i] = new WSDMProtocolloAnagraficaType();
        if ("IMPR".equals(descodent) || "TECNI".equals(descodent)) {
          String codiceFiscale = null;
          String indirizzoResidenza = null;
          String numeroCivico= null;
          String comuneResidenza= null;
          String codiceComuneResidenza = null;
          String piva =null;
          String emailAggiuntiva=null;
          String provincia=null;
          String cap=null;
          String nome=null;
          String select = "select cfimp,indimp,nciimp,locimp,codcit,tipimp,pivimp,emaiip,proimp,capimp from impr where codimp = ?";
          if("TECNI".equals(descodent))
            select = "select cftec,indtec,ncitec,loctec,cittec,pivatec,ematec,protec,captec,nometei from tecni where codtec = ?";
          Vector<?> datiImpr = this.sqlManager.getVector(select, new Object[] { descodsog });
          if(datiImpr!=null && datiImpr.size()>0){
            Long tipimp = null;
            if("IMPR".equals(descodent))
              tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 5).getValue();
            if(tipimp!= null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
              //Si devono cercare i dati a partire dal valore del campo desintest
              //Il campo è così formato: Ragione sociale del raggruppamento - Ragione sociale componente - indicazione sulla tipologia di componente
              String datiAnagraficiComponente[] = this.getDatiImpresaComponente(idprg, idcom, descodsog, descodesintestdent);
              if(datiAnagraficiComponente!=null && datiAnagraficiComponente.length>0){
                descodesintestdent = datiAnagraficiComponente[0];
                codiceFiscale = datiAnagraficiComponente[1];
                indirizzoResidenza = datiAnagraficiComponente[2];
                comuneResidenza = datiAnagraficiComponente[3];
                codiceComuneResidenza = datiAnagraficiComponente[4];
                emailAggiuntiva = datiAnagraficiComponente[5];
                piva = datiAnagraficiComponente[6];
                provincia = datiAnagraficiComponente[7];
                cap = datiAnagraficiComponente[8];
              }
            }else{
              codiceFiscale = (String) SqlManager.getValueFromVectorParam(datiImpr,0).getValue();
              if("IMPR".equals(descodent))
                piva = (String) SqlManager.getValueFromVectorParam(datiImpr,6).getValue();
              else
                piva = (String) SqlManager.getValueFromVectorParam(datiImpr,5).getValue();
              indirizzoResidenza = (String) SqlManager.getValueFromVectorParam(datiImpr,1).getValue();
              numeroCivico= (String) SqlManager.getValueFromVectorParam(datiImpr,2).getValue();
              if (indirizzoResidenza != null && !"".equals(indirizzoResidenza) && numeroCivico!=null){
                indirizzoResidenza += ", " + numeroCivico;
              }
              comuneResidenza= (String) SqlManager.getValueFromVectorParam(datiImpr,3).getValue();
              codiceComuneResidenza = (String) SqlManager.getValueFromVectorParam(datiImpr,4).getValue();
              if("IMPR".equals(descodent)){
                emailAggiuntiva = (String) SqlManager.getValueFromVectorParam(datiImpr,7).getValue();
                provincia  = (String) SqlManager.getValueFromVectorParam(datiImpr,8).getValue();
                cap  = (String) SqlManager.getValueFromVectorParam(datiImpr,9).getValue();
              }else{
                emailAggiuntiva = (String) SqlManager.getValueFromVectorParam(datiImpr,6).getValue();
                provincia  = (String) SqlManager.getValueFromVectorParam(datiImpr,7).getValue();
                cap  = (String) SqlManager.getValueFromVectorParam(datiImpr,8).getValue();
                nome = (String) SqlManager.getValueFromVectorParam(datiImpr,9).getValue();
              }
            }
          }

          if("FOLIUM".equals(tipoWSDM)){
            codiceFiscale ="";
            piva = "";
          }
          destinatari[i].setCodiceFiscale(codiceFiscale);
          destinatari[i].setPartitaIVA(piva);
          destinatari[i].setIndirizzoResidenza(indirizzoResidenza);
          destinatari[i].setComuneResidenza(comuneResidenza);
          destinatari[i].setCodiceComuneResidenza(codiceComuneResidenza);
          destinatari[i].setEmail(desmail);
          destinatari[i].setEmailAggiuntiva(emailAggiuntiva);
          destinatari[i].setCapResidenza(cap);
          destinatari[i].setProvinciaResidenza(provincia);
          if("IMPR".equals(descodent)) {
            destinatari[i].setTipoVoceRubrica(WSDMTipoVoceRubricaType.IMPRESA);
          }else {
            destinatari[i].setTipoVoceRubrica(WSDMTipoVoceRubricaType.PERSONA);
            if (nome != null) {
              destinatari[i].setNome(nome);
            }
          }
        }
        destinatari[i].setCognomeointestazione(descodesintestdent);
        if(mezzoInvio!=null && !"".equals(mezzoInvio))
          destinatari[i].setMezzo(mezzoInvio);
      }
      wsdmProtocolloDocumentoIn.setDestinatari(destinatari);
      destinatarioPrincipale=this.formattazioneDestinatarioPrincipale(destinatarioPrincipale);
    }
    wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(destinatarioPrincipale);

    return datiW_INVCOMDES;
  }

  /**
   * Vengono inseriti i destinarati alla richiesta di protocollazione
   * @param idprg
   * @param idcom
   * @param inviaMail
   * @param wsdmProtocolloDocumentoIn
   * @throws SQLException
   */
  public void setDestinatariMail(String idprg, Long idcom, WSDMInviaMailType inviaMail, WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn) throws SQLException{
    String selectW_INVCOMDESMail = "select desmail from w_invcomdes where idprg = ? and idcom = ?  and (descc is null or descc <>1)";
    List<?> datiW_INVCOMDESMail = this.sqlManager.getListVector(selectW_INVCOMDESMail, new Object[] { idprg, idcom });
    if (datiW_INVCOMDESMail != null && datiW_INVCOMDESMail.size() > 0) {
      String[] destinatariMail = new String[datiW_INVCOMDESMail.size()];
      for (int ides = 0; ides < datiW_INVCOMDESMail.size(); ides++) {
        destinatariMail[ides] = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDESMail.get(ides), 0).getValue();
      }
      inviaMail.setDestinatariMail(destinatariMail);
    }
    wsdmProtocolloDocumentoIn.setInviaMail(inviaMail);
  }


  /**
   *
   * @param nome
   * @return
   */
  static public String getTipoFile(String nome){
    String tipo = "";
    int index = nome.lastIndexOf('.');
    if (index > 0) {
      tipo = nome.substring(index + 1);
    }
    return tipo;
  }

  /**
   *
   * @param ngara
   * @param codgar
   * @param genere
   * @param tipoWSDM
   * @param abilitatoInvioMailDocumentale
   * @param abilitatoInvioSingolo
   * @param datiWSDM
   * @param datiProtocollo
   * @return
   * @throws GestoreException
   */
  public Object[] protocollaComunicazioniWSDM(String ngara, String codgar, Long genere, String tipoWSDM, boolean abilitatoInvioMailDocumentale,
      boolean abilitatoInvioSingolo, String inserimentoFascicolo, HashMap<String, Object> datiWSDM ) throws GestoreException{

    String idprg="PG";
    WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = null;
    WSDMProtocolloAnagraficaType[] destinatari = null;
    WSDMProtocolloAllegatoType[] allegati = null;
    //Nel caso di EASYDOC mi servono solamante gli allegati effettivi, senza il testo della comunicazione
    WSDMProtocolloAllegatoType[] allegatiReali = null;
    String messaggioRitorno=null;
    String esito = "OK";
    HashMap<String, Object> datiProtocollo = null;

    Long idcom = (Long)datiWSDM.get(LABEL_IDCOM);
    try{
      String classificadocumento =  (String)datiWSDM.get(LABEL_CLASSIFICA_DOCUMENTO);
      String tipodocumento = (String)datiWSDM.get(LABEL_TIPO_DOCUMENTO);
      String oggettodocumento = (String)datiWSDM.get(LABEL_OGGETTO_DOCUMENTO);
      String coment = (String)datiWSDM.get(LABEL_COMENT);
      String descrizionedocumento = null;
      String mittenteinterno = (String)datiWSDM.get(LABEL_MITTENTE_INTERNO);
      String indirizzomittente = (String)datiWSDM.get(LABEL_INDIRIZZO_MITTENTE);
      String mezzoinvio = (String)datiWSDM.get(LABEL_MEZZO_INVIO);
      String mezzo = (String)datiWSDM.get(LABEL_MEZZO);
      String codiceregistrodocumento = (String)datiWSDM.get(LABEL_CODICE_REGISTRO_DOCUMENTO);
      String inout = "OUT";  //Capire se va lasciato fisso o va modificato per PALEO mettendo INT, perchè quando si crea il documento fittizio si da verso INT
      String idindice = (String)datiWSDM.get(LABEL_ID_INDICE);
      String idtitolazione = (String)datiWSDM.get(LABEL_ID_TITOLAZIONE);
      String idunitaoperativamittente = (String)datiWSDM.get(LABEL_ID_UNITA_OPERATIVA_DESTINATARIA);
      String inserimentoinfascicolo = inserimentoFascicolo;
      String codicefascicolo = (String)datiWSDM.get(LABEL_CODICE_FASCICOLO);

      String classificafascicolo = (String)datiWSDM.get(LABEL_CLASSIFICA_FASCICOLO);
      Long annofascicolo = (Long)datiWSDM.get(LABEL_ANNO_FASCICOLO);
      String annoFascicoloString=null;
      if(annofascicolo!=null)
        annoFascicoloString = annofascicolo.toString();
      String numerofascicolo = (String)datiWSDM.get(LABEL_NUMERO_FASCICOLO);
      String codiceaoo =   (String)datiWSDM.get(LABEL_CODAOO);
      String idconfi =   (String)datiWSDM.get(LABEL_IDCONFI);
      String societa = null  ;
      String cig = null;
      String RUP = null;
      String nomeRup = null;
      String acronimoRup = "";
      String sottotipo = (String)datiWSDM.get(LABEL_SOTTOTIPO);

      Vector<?> datiTorn = sqlManager.getVector("select t.cenint, codcig from torn t, v_gare_torn v where t.codgar=? and t.codgar=v.codgar", new Object[]{codgar});
      if(datiTorn!=null && datiTorn.size()>0){
        societa = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
        cig = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
      }

      String codiceufficio  =   (String)datiWSDM.get(LABEL_CODICE_UFFICIO);
      String uocompetenza = null;
      if("ENGINEERINGDOC".equals(tipoWSDM)) {
        uocompetenza = (String)datiWSDM.get(LABEL_CODICE_UFFICIO);
        codiceufficio = null;
      }

      Long numeroallegatiComunicazione = new Long(0);
      String selectAllegati="select iddocdig, dignomdoc, digdesdoc from w_docdig where idprg=? and DIGENT=? and digkey1=? and digkey2=? ";
      selectAllegati += " and DIGNOMDOC <> " +  AllegatoSintesiUtils.creazioneFiltroNomeFileSintesi(true,this.sqlManager);
      selectAllegati += " and DIGNOMDOC <> " +  AllegatoSintesiUtils.creazioneFiltroNomeFileSintesi(false,this.sqlManager);
      selectAllegati += " order by iddocdig";
      List<?> listaAllegatiComunicazione = this.sqlManager.getListVector(selectAllegati, new Object[]{idprg,"W_INVCOM", idprg, idcom.toString()});
      if(numeroallegatiComunicazione!=null)
        numeroallegatiComunicazione = new Long(listaAllegatiComunicazione.size());
      int  numeroallegati = numeroallegatiComunicazione.intValue();

      String supporto = (String)datiWSDM.get(LABEL_SUPPORTO);
      String struttura = (String)datiWSDM.get(LABEL_STRUTTURA);

      String classificadescrizione = (String)datiWSDM.get(LABEL_CLASSIFICA_DESRIZIONE);
      String voce = (String)datiWSDM.get(LABEL_VOCE);

      String oggettofascicolo = null;
      if("TITULUS".equals(tipoWSDM)){
        String selectOggetto="select not_gar from gare where ngara = ?";
        String chiave = ngara;
        if(genere.longValue()==3){
          selectOggetto="select destor from torn where codgar = ?";
          chiave = codgar;
        }
        oggettofascicolo = (String)sqlManager.getObject(selectOggetto, new Object[]{chiave});
      }

      if("TITULUS".equals(tipoWSDM) && !abilitatoInvioMailDocumentale)
        tipodocumento=this.TIPO_DOCUMENTO_GARA;
      else if("TITULUS".equals(tipoWSDM) && abilitatoInvioMailDocumentale)
        tipodocumento=this.TIPO_DOCUMENTO_GARA_PEC;

      String livelloriservatezza = (String)datiWSDM.get(LABEL_LIVELLO_RISERVATEZZA);

      if("JDOC".equals(tipoWSDM)){
        String select = "select nometei,cogtei from torn, tecni where codgar = ? and codrup=codtec";
        Vector datiRup =  sqlManager.getVector(select, new Object[]{codgar});
        if(datiRup!=null && datiRup.size()>0){
          String nome = SqlManager.getValueFromVectorParam(datiRup, 0).getStringValue();
          String cognome = SqlManager.getValueFromVectorParam(datiRup, 1).getStringValue();
          if(nome==null)
            nome="";
          if(cognome==null)
            cognome="";
         RUP = cognome + " " + nome;
         nomeRup = RUP;
         if(nome.length()>0)
           acronimoRup +=nome.substring(0, 1);
         if(cognome.length()>0)
           acronimoRup +=cognome.substring(0, 1);
        }
      }

      HashMap<String,Object> par = new HashMap<String,Object>();
      par.put("classificadocumento", classificadocumento);
      par.put("tipodocumento", tipodocumento);
      par.put("oggettodocumento", oggettodocumento);
      par.put("descrizionedocumento", descrizionedocumento);
      par.put("mittenteinterno", mittenteinterno);
      par.put("codiceregistrodocumento", codiceregistrodocumento);
      par.put("inout", inout);
      par.put("idindice", idindice);
      par.put("idtitolazione", idtitolazione);
      par.put("idunitaoperativamittente", idunitaoperativamittente);
      par.put("inserimentoinfascicolo", inserimentoinfascicolo);
      par.put("codicefascicolo", codicefascicolo);
      par.put("oggettofascicolo", oggettofascicolo);
      par.put("classificafascicolo", classificafascicolo);
      par.put("annofascicolo", annoFascicoloString);
      par.put("numerofascicolo", numerofascicolo);
      par.put("tipoWSDM", tipoWSDM);
      par.put("idprg", idprg);
      par.put("idcom", idcom);
      par.put("mezzo", mezzo);
      par.put("societa", societa);
      par.put("codiceGaralotto", ngara);
      par.put("cig", cig);
      par.put("numeroallegati", new Long(numeroallegati));
      par.put("struttura", struttura);
      par.put("supporto", supporto);
      par.put("servizio", "FASCICOLOPROTOCOLLO");
      par.put(GestioneWSDMManager.LABEL_CLASSIFICA_DESRIZIONE, classificadescrizione);
      par.put(GestioneWSDMManager.LABEL_VOCE, voce);
      par.put(GestioneWSDMManager.LABEL_SOTTOTIPO, sottotipo);
      par.put(GestioneWSDMManager.LABEL_ACRONIMO_RUP, acronimoRup);
      par.put(GestioneWSDMManager.LABEL_NOME_RUP, nomeRup);
      par.put(GestioneWSDMManager.LABEL_RUP, RUP);
      par.put(GestioneWSDMManager.LABEL_UOCOMPETENZA, uocompetenza);

      if("JIRIDE".equals(tipoWSDM) && "SI_FASCICOLO_ESISTENTE".equals(inserimentoinfascicolo)){
        par.put("livelloriservatezza", livelloriservatezza);
      }
      wsdmProtocolloDocumentoIn = this.wsdmProtocolloDocumentoPopola(par,idconfi);

      //Destinatari protocollo
      List<?> listaW_INVCOMDES = this.popolaDestinatariWSDM(idprg, idcom, tipoWSDM, mezzoinvio, wsdmProtocolloDocumentoIn);

      // Testo email
      String commsgtes = (String)datiWSDM.get(LABEL_COMMSGETS);
      if (commsgtes == null || "".equals(commsgtes)){
        commsgtes = "[testo vuoto]";
      }

      // Invio mail mediante servizi di protocollazione per ENGINEERING, TITULUS,SMAT,URBI
      if(abilitatoInvioMailDocumentale && ("ENGINEERING".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM))){
        WSDMInviaMailType inviaMail = new WSDMInviaMailType();


        inviaMail.setTestoMail(commsgtes);
        if("ENGINEERING".equals(tipoWSDM)){
          // Oggetto email
          inviaMail.setOggettoMail(oggettodocumento);
        }
        // Destinatari
        this.setDestinatariMail(idprg, idcom, inviaMail, wsdmProtocolloDocumentoIn);
      }

      //Allegati
      int numAllegatiWSDM = 1;
      if(numeroallegati>0)
        numAllegatiWSDM = 1 + numeroallegati;

      allegati = new WSDMProtocolloAllegatoType[numAllegatiWSDM];

      //si si deve leggere il valore della property "wsdm.posizioneAllegatoComunicazione".
      //Se la property vale 1 allora il testo della comunicazione viene messo come primo allegato, altrimenti rimane in coda
      int indiceAllegati = 0;
      String posizioneAllegatoComunicazione = ConfigManager.getValore("wsdm.posizioneAllegatoComunicazione." + idconfi);
      if("1".equals(posizioneAllegatoComunicazione)){
        indiceAllegati = 1;
      }

      if(numeroallegati>0){
        for(int i=0; i<numeroallegati; i++){
          Long iddocdig = SqlManager.getValueFromVectorParam(listaAllegatiComunicazione.get(i), 0).longValue();
          String dignomdoc = SqlManager.getValueFromVectorParam(listaAllegatiComunicazione.get(i), 1).getStringValue();
          String descrizione = SqlManager.getValueFromVectorParam(listaAllegatiComunicazione.get(i), 1).getStringValue();
          String tipo = getTipoFile(dignomdoc);
          allegati[indiceAllegati + i] = new WSDMProtocolloAllegatoType();
          allegati[indiceAllegati + i].setNome(dignomdoc);
          allegati[indiceAllegati + i].setTitolo( descrizione);
          allegati[indiceAllegati + i].setTipo(tipo);
          BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
          allegati[indiceAllegati + i].setContenuto(digogg.getStream());
          if("TITULUS".equals(tipoWSDM))
            allegati[indiceAllegati + i].setIdAllegato("W_DOCDIG|" + idprg + "|" + iddocdig.toString());
          if("NUMIX".equals(tipoWSDM)) {
            allegati[indiceAllegati + i] = GestioneWSDMManager.popolaAllegatoInfo(dignomdoc,allegati[indiceAllegati + i]);
            if(indiceAllegati + i ==0 )
              allegati[indiceAllegati + i].setIsSealed(new Long(1));
          }
        }
      }

      String commsgogg = (String)datiWSDM.get(LABEL_COMMSGOGG);

      //gestione allegato sintesi
      byte[] contenutoPdf = null;
      Long idAllegatoSintesi = cancellaAllegatoSintesi(idprg,idcom);
      String nomeFile=null;
      String estensioneFile = "pdf";
      String titoloFile = null;
      if(idAllegatoSintesi==null) {
        HashMap<String, Object> ret = aggiungiAllegatoSintesi(ngara, cig, commsgogg, commsgtes, idprg, idcom, coment, null);
        if(ret==null) {
          this.impostaStatoComunicazione(idprg, idcom, new Long(15));
          messaggioRitorno = "Errore nella creazione del file di sintesi della comunicazione";
          esito="NOK";
        }else {
          idAllegatoSintesi = (Long)ret.get("idAllegatoSintesi");
          nomeFile = (String)ret.get("nomeFile");
          estensioneFile = (String)ret.get("estensioneFile");
          titoloFile = (String)ret.get("titoloFile");
          contenutoPdf = (byte[]) ret.get("pdf");
        }
      }else {
        Vector<?> datiAllegato = this.sqlManager.getVector("select dignomdoc, digdesdoc from  w_docdig where idprg=? and iddocdig=?", new Object[] {idprg,idAllegatoSintesi});
        if(datiAllegato!=null && datiAllegato.size()>0) {
          nomeFile = SqlManager.getValueFromVectorParam(datiAllegato, 0).getStringValue();
          titoloFile = SqlManager.getValueFromVectorParam(datiAllegato, 1).getStringValue();
          if(nomeFile.endsWith(".tsd"))
          estensioneFile = "tsd";
        }
        BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, idAllegatoSintesi);
        contenutoPdf = digogg.getStream();
      }
      if(!"NOK".equals(esito)) {
        // Aggiunta del testo della comunicazione
        int posTestoComunicazione = numeroallegati;
        if ("1".equals(posizioneAllegatoComunicazione))
          posTestoComunicazione = 0;
        String commsgtip = (String)datiWSDM.get(GestioneWSDMManager.LABEL_COMMSGTIP);
        if ("1".equals(commsgtip)) {
          commsgtes = "<!DOCTYPE html><html><body>" + commsgtes + "</body></html>";
          allegati[posTestoComunicazione] = new WSDMProtocolloAllegatoType();
          allegati[posTestoComunicazione].setNome("Comunicazione.html");
          allegati[posTestoComunicazione].setTipo("html");
          allegati[posTestoComunicazione].setTitolo("Testo della comunicazione");
          allegati[posTestoComunicazione].setContenuto(commsgtes.getBytes());
        } else {
          allegati[posTestoComunicazione] = new WSDMProtocolloAllegatoType();
          allegati[posTestoComunicazione].setNome(nomeFile);
          allegati[posTestoComunicazione].setTipo(estensioneFile);
          allegati[posTestoComunicazione].setTitolo(titoloFile);
          allegati[posTestoComunicazione].setContenuto(contenutoPdf);
        }

        if("NUMIX".equals(tipoWSDM)) {
          if(!"1".equals(commsgtip)) {
            allegati[posTestoComunicazione] = GestioneWSDMManager.popolaAllegatoInfo(nomeFile,allegati[posTestoComunicazione]);
          }
          if(posTestoComunicazione ==0 )
            allegati[posTestoComunicazione].setIsSealed(new Long(1));
        }
        if("TITULUS".equals(tipoWSDM))
          allegati[posTestoComunicazione].setIdAllegato("W_INVCOM|" + idprg + "|" + idcom.toString());

        wsdmProtocolloDocumentoIn.setAllegati(allegati);

        String username = (String)datiWSDM.get(LABEL_USERNAME);
        String password = (String)datiWSDM.get(LABEL_PASSWORD);
        String ruolo = (String)datiWSDM.get(LABEL_RUOLO);
        String nome = (String)datiWSDM.get(LABEL_NOME);
        String cognome = (String)datiWSDM.get(LABEL_COGNOME);
        String codiceuo = (String)datiWSDM.get(LABEL_CODICEUO);
        String idutente = (String)datiWSDM.get(LABEL_ID_UTENTE);
        String idutenteunop = (String)datiWSDM.get(LABEL_ID_UTENTE_UNITA_OPERATIVA);

        WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.wsdmProtocolloInserisci(username, password,
            ruolo, nome, cognome, codiceuo, idutente, idutenteunop, codiceaoo, codiceufficio,  wsdmProtocolloDocumentoIn,idconfi);

        if (wsdmProtocolloDocumentoRes.isEsito()) {
          String numeroDocumento = null;
          if(!"LAPISOPERA".equals(tipoWSDM))
            numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();
          Long annoProtocollo = null;
          if(!"LAPISOPERA".equals(tipoWSDM))
            annoProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
          String numeroProtocollo = null;
          if("LAPISOPERA".equals(tipoWSDM))
            numeroProtocollo = GestioneWSDMManager.PREFISSO_COD_FASCICOLO_LAPISOPERA + wsdmProtocolloDocumentoRes.getProtocolloDocumento().getGenericS11();
          else
            numeroProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();

          Timestamp dataProtocollo= this.getDataProtocollo(wsdmProtocolloDocumentoRes);
          if(annoProtocollo==null && !"LAPISOPERA".equals(tipoWSDM)){
            annoProtocollo = this.getAnnoFromDate(dataProtocollo);
          }


          //Salvataggio in WSDOCUMENTO
          String key1=ngara;
          if(genere.longValue()==3)
            key1=codgar;
          if(oggettodocumento!=null && oggettodocumento.length()>2000)
            oggettodocumento = oggettodocumento.substring(0, 2000);
          Long idWSDocumento = this.setWSDocumento("GARE", key1, null, null, null, numeroDocumento, annoProtocollo, numeroProtocollo, oggettodocumento,inout);

          //Salvataggio della mail in WSALLEGATI
          this.setWSAllegati("W_INVCOM", idprg, idcom.toString(), null, null, idWSDocumento);

          //Salvataggio degli allegati in WSALLEGATI
          if (idcom.longValue() > 0) {
            Long iddocdig=null;
            Long numOccorrenze=null;

            //Inserimento allegati della documentazione di gara documenti (gruppo=6 e allmail='1')
            String selectAllegatiDocumgara="select d.IDDOCDG from DOCUMGARA d,W_DOCDIG w where CODGAR=? ";
            if( genere.longValue()!=3)
              selectAllegatiDocumgara+=" and NGARA = '"+ ngara + "'";
            selectAllegatiDocumgara+=" and GRUPPO = ? and d.IDPRG=w.IDPRG and d.IDDOCDG = w.IDDOCDIG and allmail=? order by numord,norddocg";
            List listaDocumenti = sqlManager.getListVector(selectAllegatiDocumgara, new Object[]{codgar,new Long(6) ,"1"});
            if(listaDocumenti!=null && listaDocumenti.size()>0){
              for (int i = 0; i < listaDocumenti.size(); i++) {
                iddocdig =SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).longValue();
                //Se l'occorrenza è già presente in W_DOCDIG non si deve inserire(caso che si presenta nel caso sia abilitato l'invio singolo)
                numOccorrenze = (Long)this.sqlManager.getObject("select count(id) from wsallegati where entita=? and key1=? and key2=?", new Object[]{"W_DOCDIG",idprg,iddocdig.toString()});
                if(numOccorrenze==null || new Long(0).equals(numOccorrenze))
                  this.setWSAllegati("W_DOCDIG", idprg, iddocdig.toString(), null, null, idWSDocumento);
              }
            }

            //Inserimento allegati della comunicazione
            for (int i = 0; i < numeroallegati; i++) {
              iddocdig = SqlManager.getValueFromVectorParam(listaAllegatiComunicazione.get(i), 0).longValue();
              this.setWSAllegati("W_DOCDIG", idprg, iddocdig.toString(), null, null, idWSDocumento);
            }

            //Inserimento allegato di sintesi
            this.setWSAllegati("W_DOCDIG", idprg, idAllegatoSintesi.toString(), null, null, idWSDocumento);
          }

          datiProtocollo = new HashMap<String,Object>();
          datiProtocollo.put(LABEL_NUMERO_DOCUMENTO, numeroDocumento);
          datiProtocollo.put(LABEL_ANNO_PROTOCOLLO, annoProtocollo);
          datiProtocollo.put(LABEL_NUMERO_PROTOCOLLO, numeroProtocollo);
          datiProtocollo.put(LABEL_DATA_PROTOCOLLO, dataProtocollo);
          datiProtocollo.put(LABEL_NUMERO_ALLEGATI_REALI, new Long(numeroallegati));

        }else{
          //La protocollazione non è andata a buon fine, si deve impostare lo stato della comunicazione a 15
          //String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom = ?";
          //this.sqlManager.update(updateW_INVCOM, new Object[]{new Long(15),idprg,idcom});
          this.impostaStatoComunicazione(idprg, idcom, new Long(15));
          messaggioRitorno = wsdmProtocolloDocumentoRes.getMessaggio();
          esito="NOK";
        }
      }
      return new Object[]{esito,messaggioRitorno, datiProtocollo, listaW_INVCOMDES,allegati};
    }catch(Throwable e){
      String msg="Errore nella protocollazione della comunicazione " + idcom;
      if(genere.longValue()==3)
        msg+= " del lotto ";
      else
        msg+=" della gara ";
      msg+= ngara + ". " + e.getMessage();
      throw new GestoreException(msg, null, e);
    }

  }

  /**
   * Nel caso di mail in carico al documentale, viene gestito l'invio delle mail nel caso in cui questa debba essere effettuata
   * dal documentale come operazione successiva alla protocollazione.
   * In fine viene salvato il numero protocollo nella comunicazione  e inoltre ne viene impostato lo stato
   * NOTA: tale metodo ha contesto transazione PROPAGATION_REQUIRES_NEW, quindi ha una transazione indipendente.
   *       Se si vuole lavorare nella stessa transazione del chiamante, adoperare "gestioneComunicazioneDopoProtocollazioneSenzaTransazionePropria"
   *
   * @param codagr
   * @param dati contiente i seguenti valori:<br>
   *    tipoWSDM<br>
   *    abilitatoInvioMailDocumentale<br>
   *    numeroDocumento<br>
   *    annoProtocollo<br>
   *    numeroProtocollo<br>
   *    dataProtocollo<br>
   *    oggettodocumento<br>
   *    commsgtes<br>
   *    indirizzomittente<br>
   *    comsgtip<br>
   *    idprg<br>
   *    idcom<br>
   *    numAllegatiReali<br>
   * @param datiLogin contiente i seguenti valori:<br>
   *    username<br>
   *    password<br>
   *    ruolo<br>
   *    nome<br>
   *    cognome<br>
   *    codiceuo<br>
   * @param listaW_INVCOMDES associata alla comunicazione con id e idcom passati come parametri, contiene i valori:<br>
   *    desmail<br>
   *    idcomdes<br>
   * @param allegati
   * @return String
   * @throws GestoreException
   * @throws SQLException
   *
   *
   */
  public void gestioneComunicazioneDopoProtocollazione(String codgar, HashMap<String, Object> dati, HashMap<String, Object> datiLogin, List<?> datiW_INVCOMDES,WSDMProtocolloAllegatoType[] allegati,String idconfi) throws GestoreException{
    String tipoWSDM = (String)dati.get(LABEL_TIPO_WSDM);
    Boolean abilitatoInvioMailDocumentale = (Boolean)dati.get(LABEL_ABILITATO_INVIO_MAIL_DOCUMENTALE);
    String numeroDocumento = (String)dati.get(LABEL_NUMERO_DOCUMENTO);
    Long annoProtocollo = (Long)dati.get(LABEL_ANNO_PROTOCOLLO);
    String numeroProtocollo = (String)dati.get(LABEL_NUMERO_PROTOCOLLO);
    Timestamp dataProtocollo = (Timestamp)dati.get(LABEL_DATA_PROTOCOLLO);
    String oggettodocumento = (String)dati.get(LABEL_OGGETTO_DOCUMENTO);
    String commsgtes = (String)dati.get(LABEL_COMMSGETS);
    String indirizzomittente = (String)dati.get(LABEL_INDIRIZZO_MITTENTE);
    String comsgtip = (String)dati.get(LABEL_COMMSGTIP);
    String idprg = (String)dati.get(LABEL_IDPRG);
    Long idcom = (Long)dati.get(LABEL_IDCOM);
    Long numAllegatiReali = (Long)dati.get(LABEL_NUMERO_ALLEGATI_REALI);

    String statoComunicazione = "2";
    String desstato=null;
    String protocolloMail = null;
    String oggettoMail = null;
    String msgErroreInvioMail=null;
    TransactionStatus status = null;
    boolean commitTransaction = false;

    if(abilitatoInvioMailDocumentale.booleanValue()){
      String username = (String)datiLogin.get(LABEL_USERNAME);
      String password = (String)datiLogin.get(LABEL_PASSWORD);
      String ruolo = (String)datiLogin.get(LABEL_RUOLO);
      String nome = (String)datiLogin.get(LABEL_NOME);
      String cognome = (String)datiLogin.get(LABEL_COGNOME);
      String codiceuo = (String)datiLogin.get(LABEL_CODICEUO);

      WSDMProtocolloAllegatoType[] allegatiReali = null;

      WSDMInviaMailType parametriMailIn = new WSDMInviaMailType();
      parametriMailIn.setNumeroDocumento(numeroDocumento);
      parametriMailIn.setAnnoProtocollo(annoProtocollo);
      parametriMailIn.setNumeroProtocollo(numeroProtocollo);
      protocolloMail = numeroProtocollo;
      protocolloMail = UtilityStringhe.convertiNullInStringaVuota(protocolloMail);
      oggettoMail = oggettodocumento;
      oggettoMail = UtilityStringhe.convertiNullInStringaVuota(oggettoMail);
      if("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM)){
        if("JIRIDE".equals(tipoWSDM)){
          protocolloMail = UtilityStringhe.fillLeft(protocolloMail, '0', 7);
        }
        oggettoMail = "Prot.N." + protocolloMail + "/" + annoProtocollo + " - " +oggettoMail;
      }
      parametriMailIn.setOggettoMail(oggettoMail);
      parametriMailIn.setTestoMail(commsgtes);
      parametriMailIn.setMittenteMail(indirizzomittente);
      if ("1".equals(comsgtip))
        parametriMailIn.setFormatoMail(WSDMMailFormatoType.HTML);
      else
        parametriMailIn.setFormatoMail(WSDMMailFormatoType.TEXT);


      if("PALEO".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM) || "INFOR".equals(tipoWSDM)){
        if("ARCHIFLOW".equals(tipoWSDM) ){
          String dest[] = null;
          if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
            //Si deve impostare il vettore dei destinatari
            dest = new String[datiW_INVCOMDES.size()];
            for (int i = 0; i < datiW_INVCOMDES.size(); i++)
              dest[i] = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 0).getValue();
          }
          parametriMailIn.setDestinatariMail(dest);

        }
        if("INFOR".equals(tipoWSDM) )
          parametriMailIn.setCodiceRegistro((String)dati.get(LABEL_CODICE_REGISTRO_DOCUMENTO));

        WSDMInviaMailResType wsdmInviaMailResType = this.wsdmInviaMail(username, password, ruolo, nome, cognome, codiceuo, null, null, parametriMailIn,idconfi);
        if(wsdmInviaMailResType.isEsito()){
          statoComunicazione = "10";
          desstato = "4";
        }else{
          statoComunicazione = "11";
          desstato = "5";
          msgErroreInvioMail = wsdmInviaMailResType.getMessaggio();
        }
      } else if ("IRIDE".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM)) {
        //si deve inviare una mail per ogni destinatario
        boolean invioMailOk = true;
        if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
          WSDMInviaMailResType wsdmInviaMailResType = null;
          String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore =?, desdatinv = ? where idprg = ? and idcom = ? and idcomdes = ? and (descc is null or descc <>1)";
          if("EASYDOC".equals(tipoWSDM)){
            boolean tabellatiInDB = this.isTabellatiInDb();
            String mailChannelCode = this.getcodiceTabellato("FASCICOLOPROTOCOLLO", "mailchannelcode",idconfi, tabellatiInDB);
            parametriMailIn.setMailChannelCode(mailChannelCode);
            String mailConfigurationCode=this.getcodiceTabellato("FASCICOLOPROTOCOLLO", "mailconfigurationcode",idconfi, tabellatiInDB);
            parametriMailIn.setMailConfigurationCode(mailConfigurationCode);
            if("EASYDOC".equals(tipoWSDM)){
              allegatiReali = this.getAllegatiReali(allegati, numAllegatiReali.intValue(),idconfi);
              parametriMailIn.setAllegati(allegatiReali);
            }
          }
          for (int i = 0; i < datiW_INVCOMDES.size(); i++) {
            String delayPec = ConfigManager.getValore("wsdm.invioMailPec.delay."+idconfi);
            if(!"".equals(delayPec) && delayPec != null && i > 0){
              try {
                TimeUnit.MILLISECONDS.sleep(Integer.parseInt(delayPec));
              } catch (InterruptedException e) {
                logger.error("Errore durante l'attesa tra l'invio delle mail in carico al documentale", e);
              }
            }
            String desmail = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 0).getValue();
            Long idcomdes = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 1).getValue();
            parametriMailIn.setDestinatariMail(new String[]{desmail});

            wsdmInviaMailResType = this.wsdmInviaMail(username, password, ruolo, nome, cognome, codiceuo, null, null, parametriMailIn,idconfi);
            if(wsdmInviaMailResType.isEsito()){
              msgErroreInvioMail = null;
              desstato = "4";
            }else{
              invioMailOk = false;
              msgErroreInvioMail = wsdmInviaMailResType.getMessaggio();
              desstato = "5";
            }

            try {
              status = this.sqlManager.startTransaction();
              this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idprg, idcom.toString() , idcomdes });
              commitTransaction = true;
            }catch (Exception e) {
              commitTransaction = false;
            }finally {
              if (status != null) {
                try{
                if (commitTransaction) {
                  this.sqlManager.commitTransaction(status);
                } else {
                  this.sqlManager.rollbackTransaction(status);
                }
                }catch(SQLException e1){
                  logger.error("Errore nell'aggiornamento dello stato del destinatario " + idcom + " della comunicazione " + idcom +  e1);
                }
              }
            }
          }
          if(invioMailOk)
            statoComunicazione = "10";
          else
            statoComunicazione = "11";
        }
      }else if ("ENGINEERING".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM)) {
        statoComunicazione = "10";
        desstato = "4";
      }
    }


    // Salvataggio del numero protocollo nella comunicazione e nella gara, impostazione
    // dello stato a "In uscita"
    //Il campo COMMITT va aggiornato solo se indirizzoMittente è valorizzato
    Object param[]=null;
    String updateW_INVCOM = null;
    if(abilitatoInvioMailDocumentale.booleanValue() && ("JIRIDE".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM))){
      updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ?, committ = ? where idprg = ? and idcom = ?";
      param = new Object[]{statoComunicazione, dataProtocollo,
          numeroProtocollo, indirizzomittente, idprg,  idcom };
    }else{
      updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ? where idprg = ? and idcom = ?";
      param = new Object[]{statoComunicazione, dataProtocollo,
          numeroProtocollo, idprg,  idcom };
    }

    try{
      this.sqlManager.update(updateW_INVCOM, param);
    }catch(SQLException e){
      throw new GestoreException("Errore nell'aggiornamento dello stato della comunicazione " + idcom , null, e);
    }

    if("PALEO".equals(tipoWSDM) || "ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "SMAT".equals(tipoWSDM)
        || "ARCHIFLOWFA".equals(tipoWSDM) || "URBI".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM) || "INFOR".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM)){
      String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore =?, desdatinv = ? where idprg = ? and idcom = ? and (descc is null or descc <>1)";
      try{
        this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idprg, idcom.toString() });
      }catch(SQLException e){
        throw new GestoreException("Errore nell'aggiornamento dello stato del destinatario " + idcom + " della comunicazione " + idcom , null, e);
      }
    }

  }

  /**
   * Tale metodo è stato creato per definire un contesto transazionale PROPAGATION_REQUIRED, per potere adoperare la stessa transazione del chiamante,
   * a differenza di gestioneComunicazioneDopoProtocollazione, che ha un contesto transazionale PROPAGATION_REQUIRES_NEW, cioè una transazione indipendente
   *
   * @param codgar
   * @param dati
   * @param datiLogin
   * @param datiW_INVCOMDES
   * @param allegati
   * @throws GestoreException
   */
  public void gestioneComunicazioneDopoProtocollazioneSenzaTransazionePropria(String codgar, HashMap<String, Object> dati, HashMap<String, Object> datiLogin, List<?> datiW_INVCOMDES,WSDMProtocolloAllegatoType[] allegati, String idconfi) throws GestoreException{
    this.gestioneComunicazioneDopoProtocollazione(codgar, dati, datiLogin, datiW_INVCOMDES, allegati,idconfi);
  }

  /**
   * Aggiornamento dello stato della pubblicazione, portata da protocollazione in corso a pubblicata su portale
   * Aggiornamento di nproti col numero protocollo
   * @param codgar
   * @param numeroProtocollo
   * @param abilitatoInvioSingolo
   * @throws SQLException
   */
  public void aggiornamentoGara(String codgar, String numeroProtocollo, boolean abilitatoInvioSingolo) throws SQLException{
    sqlManager.update("update pubbli set tippub=? where codgar9=? and tippub=?", new Object[]{new Long(13),codgar,new Long(23)});

    if(!abilitatoInvioSingolo){
      String updateTorn = "update torn set nproti = ? where codgar= ? and nproti is null" ;
      this.sqlManager.update(updateTorn, new Object[] {numeroProtocollo, codgar });
    }
  }

  public static String decodificaPassword(String password) throws CriptazioneException{
    String passwordDecoded = null;
    if (password != null && password.trim().length() > 0) {
      ICriptazioneByte passwordICriptazioneByte = null;
      passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
          ICriptazioneByte.FORMATO_DATO_CIFRATO);
      passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
    }
    return passwordDecoded;
  }

  public void impostaStatoComunicazione(String idprg, Long idcom, Long stato){
    String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom = ?";
    try {
      this.sqlManager.update(updateW_INVCOM, new Object[]{new Long(15),idprg,idcom});
    } catch (SQLException e) {


    }
  }

  /**
   * Chiamata al servizio WSDMListaOperatori che richiede la lista degli operatori.
   * La lista può essere filtrata in base al cognome(obbligatorio) e codice fiscale(opzionale).
   *
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param codiceFiscale
   * @param servizio
   * @return WSDMListaOperatoriResType
   * @throws GestoreException
   */
  public WSDMListaOperatoriResType WSDMListaOperatori(String username, String password, String ruolo, String nome,
      String cognome, String codiceUO, String filtro, String servizio, String idconfi) throws GestoreException{
    WSDMListaOperatoriResType listaOperatoriRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password,  servizio, idconfi);
      WSDMLoginAttrType loginAttr = null;
      if((nome!=null && !"".equals(nome)) && (cognome!=null && !"".equals(cognome)) && (ruolo!=null && !"".equals(ruolo)) && (codiceUO!=null && !"".equals(codiceUO))){
        loginAttr = new WSDMLoginAttrType();
        if(nome!=null && !"".equals(nome))
          loginAttr.setNome(nome);
        if(cognome!=null && !"".equals(cognome))
          loginAttr.setCognome(cognome);
        if(ruolo!=null && !"".equals(ruolo))
          loginAttr.setRuolo(ruolo);
        if(codiceUO!=null && !"".equals(codiceUO))
          loginAttr.setCodiceUO(codiceUO);
      }
      listaOperatoriRes = wsdm.WSDMListaOperatori(loginAttr, filtro, null);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la chiamata del servizio WSDMListaOperatori: " + t.getMessage(),
          "wsdm.listaoperatori.remote.error", t);
    }

    return listaOperatoriRes;
  }

  /**
   * Chiamata al servizio WSDMListaTipiTrasmissione per ottenere la lista dei tipi di trasmissione
   * @param username
   * @param password
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param servizio
   * @return WSDMListaTipiTrasmissioneResType
   * @throws GestoreException
   */
  public WSDMListaTipiTrasmissioneResType WSDMListaTipiTrasmissione(String username, String password, String ruolo, String nome,
      String cognome, String codiceUO, String servizio, String idconfi) throws GestoreException{
    WSDMListaTipiTrasmissioneResType listaTipiTrasmissioneRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setRuolo(ruolo);
      loginAttr.setCodiceUO(codiceUO);
      listaTipiTrasmissioneRes = wsdm.WSDMListaTipiTrasmissione(loginAttr);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la chiamata del servizio WSDMListaTipiTrasmissione: " + t.getMessage(),
          "wsdm.listatipitrasmissione.remote.error", t);
    }

    return listaTipiTrasmissioneRes;
  }

  /**
   * Chiamata al servizio WSDMTrasmissione per trasmettere un documento ad un operatore
   * @param username
   * @param password
   * @param servizio
   * @param loginAttr
   * @param trasmissioneDocumento
   * @return WSDMTrasmissioneResType
   * @throws GestoreException
   */
  public WSDMTrasmissioneResType  WSDMTrasmissione(String username, String password, String servizio, WSDMLoginAttrType loginAttr, WSDMTrasmissioneDocumentoType[] trasmissioneDocumento, String idconfi) throws GestoreException{
    WSDMTrasmissioneResType  trasmissioneRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password,  servizio, idconfi);
      trasmissioneRes = wsdm.WSDMTrasmissione(loginAttr, trasmissioneDocumento);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la chiamata del servizio WSDMTrasmissione: " + t.getMessage(),
          "wsdm.trasmissione.remote.error", t);
    }

    return trasmissioneRes;
  }

  public Long getWsdmConfigurazioneFromCodgar(String codgar, String codapp) throws SQLException {

    String select="select cenint from torn where codgar = ?";
    String uffint = (String) sqlManager.getObject(select, new Object[]{codgar});

    return wsdmConfigManager.getWsdmConfigurazione(uffint,codapp);
  }

  public Long getWsdmConfigurazioneFromIdStipula(Long idStipula, String codapp) throws SQLException {

    String ngara = (String) sqlManager.getObject("select ngara from g1stipula where id = ?", new Object[]{idStipula});
    String select="select cenint from gare, torn where ngara = ? and codgar=codgar1";
    String uffint = (String) sqlManager.getObject(select, new Object[]{ngara});

    return wsdmConfigManager.getWsdmConfigurazione(uffint,codapp);
  }

  public List<Long> getListaConfigurazioniValoreProp(String chiave, String valore) throws SQLException{
    String select = "select w.id from wsdmconfipro wp, wsdmconfi w where wp.idconfi = w.id and wp.chiave = ? and wp.valore = ? and w.codapp = 'PG'";
    List<?> listaConfigurazioni = this.sqlManager.getListVector(select, new Object[] { chiave, valore });
    List<Long> ret = new ArrayList<Long>();
    if(listaConfigurazioni != null && listaConfigurazioni.size()>0){
      for(int i=0;i<listaConfigurazioni.size();i++){
        ret.add((Long) SqlManager.getValueFromVectorParam(listaConfigurazioni.get(i), 0).getValue());
      }
    }
    return ret;
  }

  public List<Long> getListaConfigurazioniNotValoreProp(String chiave, String valore) throws SQLException{
    String select = "select w.id from wsdmconfipro wp, wsdmconfi w where wp.idconfi = w.id and wp.chiave = ? and wp.valore != ? and w.codapp = 'PG'";
    List<?> listaConfigurazioni = this.sqlManager.getListVector(select, new Object[] { chiave, valore });
    List<Long> ret = new ArrayList<Long>();
    if(listaConfigurazioni != null && listaConfigurazioni.size()>0){
      for(int i=0;i<listaConfigurazioni.size();i++){
        ret.add((Long) SqlManager.getValueFromVectorParam(listaConfigurazioni.get(i), 0).getValue());
      }
    }
    return ret;
  }

  public Long getDefaultConfig() throws SQLException{
    String select = "select wc.id from wsdmconfi wc where codapp = 'PG' and not exists(select * from wsdmconfiuff wu where wu.idconfi = wc.id) order by id asc";
    Long id = (Long) sqlManager.getObject(select, new Object[]{});
    return id;
  }

  public String getFiltroUffintFromIdconfi(List<Long> idconfiList) throws SQLException{
    String ret = "";
    String filter = "";
    if(idconfiList != null && idconfiList.size()>0){
      for(int i=0;i<idconfiList.size();i++){
        if(!"".equals(filter)){filter+=", ";}
        filter+=idconfiList.get(i);
      }
      String select = "select codein from wsdmconfiuff where idconfi in ( "+ filter +" )";
      List<?> listaUffint = this.sqlManager.getListVector(select, new Object[] { });
      if(listaUffint != null && listaUffint.size()>0){
        for(int i=0;i<listaUffint.size();i++){
          if(!"".equals(ret)){ret+=", ";}
          ret+= "'" + SqlManager.getValueFromVectorParam(listaUffint.get(i), 0).getValue() + "'";
        }
      }
    }
    return ret;
  }

  public String getFiltroUffintFromValoreProp(String chiave, String valore) throws SQLException{
    Long idDefaultConfig = this.getDefaultConfig();
    List<Long> listaConfig;
    String filtro = "";
    String valoreDefault = null;
    if(idDefaultConfig != null){
      valoreDefault = (String) sqlManager.getObject("select valore from wsdmconfipro where chiave = ? and idconfi = ?", new Object[]{chiave,idDefaultConfig});
    }
    if(valoreDefault != null && valoreDefault.equals(valore)){
      listaConfig = this.getListaConfigurazioniNotValoreProp(chiave, valore);
      filtro = getFiltroUffintFromIdconfi(listaConfig);
      if(filtro!=null && !"".equals(filtro)){
        filtro = " not in  ( "+ filtro +" )";
      }
    }else{
      listaConfig = this.getListaConfigurazioniValoreProp(chiave, valore);
      if(listaConfig!=null && listaConfig.size()>0){
        filtro = getFiltroUffintFromIdconfi(listaConfig);
        if(filtro!=null && !"".equals(filtro)){
          filtro = " in  ( "+ filtro +" )";
        }else{
          filtro = this.FILTRO_GRUPPO_VUOTO;
        }
      }else{
        filtro = this.FILTRO_GRUPPO_VUOTO;
      }
    }
    return filtro;
  }

  public boolean isTabellatiInDb() throws GestoreException{
    boolean tabellatiInDB = false;
    String countTabellati = "select count(*) from wsdmtab";
    Long cnt;
    try {
      cnt = (Long) sqlManager.getObject(countTabellati, new Object[] {});
    } catch (SQLException e1) {
      throw new GestoreException("Si e' verificato un errore durante la lettura dei tabellati: " + e1.getMessage(),null,e1);
    }
    if(cnt != null && cnt.intValue()>0){
      tabellatiInDB = true;
    }
    return tabellatiInDB;
  }

  public WSDMListaProfiliResType getWsdmListaProfili(String username, String password, String servizio, String idconfi, boolean sso, String utenteSso) throws GestoreException{
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password,  servizio, idconfi);
      String utente=utenteSso;
      if(!sso)
        utente = username;
      WSDMListaProfiliResType res = wsdm.WSDMListaProfili(utente);
      return res;
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la chiamata del servizio WSDMTrasmissione: " + t.getMessage(),
          "wsdm.trasmissione.remote.error", t);
    }
  }

  /**
   * Viene inserito il Fascicolo per le modalità: IRIDE, JIRIDE, ENGINEERING, ARCHIFLOW, INFOR, JPROTOCOL, JDOC
   * Se la creazione del fascicolo da errore, viene restituio il messaggio di errore generato dal WSDM
   * @param tiposistemaremoto
   * @param servizio
   * @param idconfi
   * @param entita
   * @param key1
   * @param isRiservatezza
   * @param par
   * @return String
   * @throws GestoreException
   * @throws SQLException
   */
  public String setFascicolo(String tiposistemaremoto, String servizio, String idconfi, String entita, String key1, Long isRiservatezza, HashMap<String, Object> par) throws GestoreException, SQLException{
    String messaggio = null;
    String coduff = null;
    String desuff = null;
    WSDMFascicoloInType wsdmFascicoloIn = new WSDMFascicoloInType();
    wsdmFascicoloIn.setClassificaFascicolo((String)par.get(LABEL_CLASSIFICA_FASCICOLO));
    wsdmFascicoloIn.setDescrizioneFascicolo((String)par.get(LABEL_DESCRIZIONE_FASCICOLO));
    wsdmFascicoloIn.setOggettoFascicolo((String)par.get(LABEL_OGGETTO_FASCICOLO));
    if("JIRIDE".equals(tiposistemaremoto)){
      wsdmFascicoloIn.setStruttura((String)par.get(LABEL_STRUTTURA));
      wsdmFascicoloIn.setTipo((String)par.get(LABEL_TIPO_FASCICOLO));
    }
    if("ENGINEERINGDOC".equals(tiposistemaremoto) || "INFOR".equals(tiposistemaremoto)){
      wsdmFascicoloIn.setStruttura((String)par.get(LABEL_STRUTTURA));
    }
    if("JDOC".equals(tiposistemaremoto)){
      wsdmFascicoloIn.setGenericS11((String)par.get(LABEL_ACRONIMO_RUP));
      wsdmFascicoloIn.setGenericS12((String)par.get(LABEL_NOME_RUP));
    }
    WSDMFascicoloResType wsdmFascicoloRes = this.WSDMFasciloInserisci((String)par.get(LABEL_USERNAME), (String)par.get(LABEL_PASSWORD), (String)par.get(LABEL_RUOLO), (String)par.get(LABEL_NOME),
         (String)par.get(LABEL_COGNOME), (String)par.get(LABEL_CODICEUO), (String)par.get(LABEL_ID_UTENTE), (String)par.get(LABEL_ID_UTENTE_UNITA_OPERATIVA), wsdmFascicoloIn, servizio,null,null,idconfi);
    if(wsdmFascicoloRes.isEsito()){
      // Salvataggio del riferimento al fascicolo
      String codiceFascicoloNUOVO = wsdmFascicoloRes.getFascicolo().getCodiceFascicolo();
      Long annoFascicoloNUOVO = null;
      if (wsdmFascicoloRes.getFascicolo().getAnnoFascicolo() != null) {
        annoFascicoloNUOVO = wsdmFascicoloRes.getFascicolo().getAnnoFascicolo();
      }else{
        Date oggi = new Date();
        annoFascicoloNUOVO=this.getAnnoFromDate(new Timestamp(oggi.getTime()));
      }
      String numeroFascicoloNUOVO = wsdmFascicoloRes.getFascicolo().getNumeroFascicolo();

      if("ENGINEERINGDOC".equals(tiposistemaremoto)){
        coduff = (String)par.get(LABEL_UOCOMPETENZA);
        desuff = (String)par.get(LABEL_DESCRIZIONE_UOCOMPETENZA);
      }
     this.setWSFascicolo(entita, key1, null, null, null, codiceFascicoloNUOVO, annoFascicoloNUOVO,
          numeroFascicoloNUOVO, (String)par.get("classificafascicolo"),null,coduff,(String)par.get("struttura"),isRiservatezza,null,null,null,desuff);

    }else{
      messaggio = wsdmFascicoloRes.getMessaggio();
    }
    return messaggio;
  }


  /**
   * Viene costruito il testo della mail dandogli una formattazione
   * @param ngara
   * @param cig
   * @param oggettoComunicazione
   * @param testoComunicazione
   * @param idprg
   * @param idcom
   * @param iccInputStream
   * @return byte[]
   * @throws SQLException
   * @throws GestoreException
   * @throws IOException
   * @throws DocumentException
   */
  public byte[] getTestoComunicazioneFormattato(String ngara, String cig, String oggettoComunicazione, String testoComunicazione, String idprg, Long idcom, InputStream iccInputStream) throws SQLException, GestoreException, IOException, DocumentException{

    String testo = "";
    String denominazione = ConfigManager.getValore("denominazioneEnte");
    if (denominazione != null) {
      testo += denominazione + "\r\n";
    }
    Long genere = null;
    String oggettoGara = null;
    String codgar = null;
    Vector<?> datiGara= this.sqlManager.getVector("select genere,codgar  from v_gare_genere where codice=?", new Object[]{ngara});
    if (datiGara != null) {
      genere = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
      codgar = SqlManager.getValueFromVectorParam(datiGara, 1).getStringValue();
    }

    String nomein = null;
    String viaein = null;
    String nciein = null;
    String capein = null;
    String citein = null;
    String proein = null;

    Vector<?> datiUffint= this.sqlManager.getVector("select nomein,viaein, nciein, capein, citein, proein from torn,uffint where codgar=? and cenint=codein", new Object[]{codgar});
    if (datiUffint != null) {
      nomein = SqlManager.getValueFromVectorParam(datiUffint, 0).getStringValue();
      viaein = SqlManager.getValueFromVectorParam(datiUffint, 1).getStringValue();
      nciein = SqlManager.getValueFromVectorParam(datiUffint, 2).getStringValue();
      capein = SqlManager.getValueFromVectorParam(datiUffint, 3).getStringValue();
      citein = SqlManager.getValueFromVectorParam(datiUffint, 4).getStringValue();
      proein = SqlManager.getValueFromVectorParam(datiUffint, 5).getStringValue();

      //Stazione appaltante o ufficio
      if (nomein == null)
        nomein = "";
      testo += nomein + "\r\n";

      // Indirizzo
      if (viaein == null)
        viaein = "";
      testo += viaein;
      if (nciein != null && !"".equals(nciein))
        testo += ", " + nciein;
      if (capein != null && !"".equals(capein))
        testo +=  " - " + capein + " ";
      if (citein == null)
        citein = "";
      testo += citein ;
      if(proein !=null && !"".equals(proein))
        testo += " (" + proein + ")";

      testo += "\r\n";

      //Riga vuota
      testo += "\r\n";

    }

    // Oggetto
    if (cig == null)
      cig = "";
    switch (genere.intValue()) {
      case 1:
      case 3:
        oggettoGara = (String)this.sqlManager.getObject("select destor from torn where codgar=?", new Object[]{codgar});
        if (oggettoGara == null)
          oggettoGara = "";
        testo += "Oggetto gara: " + oggettoGara + " - Codice gara: " + codgar + " - CIG: " + cig + "\r\n\r\n";
        break;
      case 2:
      case 4:
        oggettoGara = (String)this.sqlManager.getObject("select not_gar from gare where ngara=?", new Object[]{ngara});
        if (oggettoGara == null)
          oggettoGara = "";
        testo += "Oggetto gara: " + oggettoGara + " - Codice gara: " + ngara + " - CIG: " + cig + "\r\n\r\n";
        break;
      case 10:
        oggettoGara = (String)this.sqlManager.getObject("select oggetto from garealbo where ngara=?", new Object[]{ngara});
        if (oggettoGara == null)
          oggettoGara = "";
        testo += "Oggetto elenco: " + oggettoGara + " - Codice elenco: " + ngara + "\r\n\r\n";
        break;
      case 11:
        oggettoGara = (String)this.sqlManager.getObject("select oggetto from gareavvisi where ngara=?", new Object[]{ngara});
        if (oggettoGara == null)
          oggettoGara = "";
        testo += "Oggetto avviso: " + oggettoGara + " - Codice avviso: " + ngara + "\r\n\r\n";
        break;
      case 20:
        oggettoGara = (String)this.sqlManager.getObject("select oggetto from garealbo where ngara=?", new Object[]{ngara});
        if (oggettoGara == null)
          oggettoGara = "";
        testo += "Oggetto catalogo: " + oggettoGara + " - Codice catalogo: " + ngara + "\r\n\r\n";
        break;
    }

    //Oggetto comunicazione
    if (oggettoComunicazione == null)
      oggettoComunicazione = "";
    testo += "Oggetto comunicazione: " + oggettoComunicazione + "\r\n";

    //Riga vuota
    testo += "\r\n";

    //Testo comunicazione
    String commsgtip = (String)this.sqlManager.getObject("select commsgtip from w_invcom where idprg = ? and idcom = ?", new Object[] {idprg, idcom });
    if("1".equals(commsgtip))
      testoComunicazione = "(testo della comunicazione in formato html)";
    else if (testoComunicazione == null)
      testoComunicazione = "";
    testo += testoComunicazione + "\r\n";

    //Riga vuota
    testo += "\r\n";

    //Elenco allegati;
    /*
    String testoAllegati="Allegati:\r\n";
    List<?> allegati= this.sqlManager.getListVector("select idprg, iddocdig,dignomdoc from w_docdig where digent = ? and digkey1 = ? and digkey2 = ?", new Object[] { "W_INVCOM", idprg, idcom.toString() });
    if(allegati!=null && allegati.size()>0){
      BlobFile digogg = null;
      String idprgAllegato = null;
      Long iddocdig = null;
      String sha = null;

      String dignomdoc = null;
      for(int i=0;i<allegati.size();i++) {
        idprgAllegato = SqlManager.getValueFromVectorParam(allegati.get(i), 0).getStringValue();
        iddocdig = SqlManager.getValueFromVectorParam(allegati.get(i), 1).longValue();
        dignomdoc = SqlManager.getValueFromVectorParam(allegati.get(i), 2).getStringValue();
        digogg = fileAllegatoManager.getFileAllegato(idprgAllegato, iddocdig);
        if(digogg!=null) {
          sha = DigestUtils.shaHex(digogg.getStream());
          testoAllegati += sha + "*" + dignomdoc;
          if( i<allegati.size() - 1)
            testoAllegati += "\r\n";
        }
      }

    }else {
      testoAllegati += "Nessun documento allegato\r\n";
    }
    testo+=testoAllegati;
    */
    testo += this.getTestoAllegati(idprg, idcom);
    byte[] pdf = null;
    try {
      pdf = UtilityStringhe.string2PdfA(testo, iccInputStream);
      logger.debug("PDF-A creato.");
    } catch (com.itextpdf.text.DocumentException e) {
      throw new DocumentException(e);
    }
    return pdf;
  }

  /**
   * Viene costruito il testo della mail dandogli una formattazione
   * @param ngara
   * @param cig
   * @param oggettoComunicazione
   * @param testoComunicazione
   * @param idprg
   * @param idcom
   * @param iccInputStream
   * @return byte[]
   * @throws SQLException
   * @throws GestoreException
   * @throws IOException
   * @throws DocumentException
   */
  public byte[] getTestoComunicazioneFormattatoStipula(String codStipula, String oggettoComunicazione, String testoComunicazione, String idprg, Long idcom, InputStream iccInputStream) throws SQLException, GestoreException, IOException, DocumentException{

    String testo = "";
    String denominazione = ConfigManager.getValore("denominazioneEnte");
    if (denominazione != null) {
      testo += denominazione + "\r\n";
    }

    String nomein = null;
    String viaein = null;
    String nciein = null;
    String capein = null;
    String citein = null;
    String proein = null;

    String oggettoStipula = null;
    Vector<?> datiUffint= this.sqlManager.getVector("select nomein,viaein, nciein, capein, citein, proein,oggetto from v_gare_stipula,uffint where codstipula=? and cenint=codein", new Object[]{codStipula});
    if (datiUffint != null) {
      nomein = SqlManager.getValueFromVectorParam(datiUffint, 0).getStringValue();
      viaein = SqlManager.getValueFromVectorParam(datiUffint, 1).getStringValue();
      nciein = SqlManager.getValueFromVectorParam(datiUffint, 2).getStringValue();
      capein = SqlManager.getValueFromVectorParam(datiUffint, 3).getStringValue();
      citein = SqlManager.getValueFromVectorParam(datiUffint, 4).getStringValue();
      proein = SqlManager.getValueFromVectorParam(datiUffint, 5).getStringValue();
      oggettoStipula = SqlManager.getValueFromVectorParam(datiUffint, 6).getStringValue();

      //Stazione appaltante o ufficio
      if (nomein == null)
        nomein = "";
      testo += nomein + "\r\n";

      // Indirizzo
      if (viaein == null)
        viaein = "";
      testo += viaein;
      if (nciein != null && !"".equals(nciein))
        testo += ", " + nciein;
      if (capein != null && !"".equals(capein))
        testo +=  " - " + capein + " ";
      if (citein == null)
        citein = "";
      testo += citein ;
      if(proein !=null && !"".equals(proein))
        testo += " (" + proein + ")";

      testo += "\r\n";

      //Riga vuota
      testo += "\r\n";

    }

    // Oggetto
    String ngaraStipula = (String)this.sqlManager.getObject("select ngara from g1stipula where codstipula=?", new Object[] {codStipula});
    testo += "Oggetto stipula: " + oggettoStipula + " - Codice stipula: " + codStipula + "(rif.gara: " + ngaraStipula + ")\r\n\r\n";

    //Oggetto comunicazione
    if (oggettoComunicazione == null)
      oggettoComunicazione = "";
    testo += "Oggetto comunicazione: " + oggettoComunicazione + "\r\n";

    //Riga vuota
    testo += "\r\n";

    //Testo comunicazione
    String commsgtip = (String)this.sqlManager.getObject("select commsgtip from w_invcom where idprg = ? and idcom = ?", new Object[] {idprg, idcom });
    if("1".equals(commsgtip))
      testoComunicazione = "(testo della comunicazione in formato html)";
    else if (testoComunicazione == null)
      testoComunicazione = "";
    testo += testoComunicazione + "\r\n";

    //Riga vuota
    testo += "\r\n";

    //Elenco allegati;
    testo += this.getTestoAllegati(idprg, idcom);
    byte[] pdf = null;
    try {
      pdf = UtilityStringhe.string2PdfA(testo, iccInputStream);
      logger.debug("PDF-A creato.");
    } catch (com.itextpdf.text.DocumentException e) {
      throw new DocumentException(e);
    }
    return pdf;
  }

  /**
   * Viene creata la stringa che concatena il nome dell'allegato ed il suo hask
   * @param idprg
   * @param idcom
   * @return String
   * @throws SQLException
   * @throws GestoreException
   * @throws IOException
   */
  private String getTestoAllegati(String idprg, Long idcom) throws SQLException, GestoreException, IOException {
    String testoAllegati="Allegati:\r\n";
    List<?> allegati= this.sqlManager.getListVector("select idprg, iddocdig,dignomdoc from w_docdig where digent = ? and digkey1 = ? and digkey2 = ?", new Object[] { "W_INVCOM", idprg, idcom.toString() });
    if(allegati!=null && allegati.size()>0){
      BlobFile digogg = null;
      String idprgAllegato = null;
      Long iddocdig = null;
      String sha = null;

      String dignomdoc = null;
      for(int i=0;i<allegati.size();i++) {
        idprgAllegato = SqlManager.getValueFromVectorParam(allegati.get(i), 0).getStringValue();
        iddocdig = SqlManager.getValueFromVectorParam(allegati.get(i), 1).longValue();
        dignomdoc = SqlManager.getValueFromVectorParam(allegati.get(i), 2).getStringValue();
        digogg = fileAllegatoManager.getFileAllegato(idprgAllegato, iddocdig);
        if(digogg!=null) {
          sha = DigestUtils.shaHex(digogg.getStream());
          testoAllegati += sha + "*" + dignomdoc;
          if( i<allegati.size() - 1)
            testoAllegati += "\r\n";
        }
      }

    }else {
      testoAllegati += "Nessun documento allegato\r\n";
    }

    return testoAllegati;
  }


  /**
   * Viene creata la stringa che concatena la lista dei destinatari
   * @param idprg
   * @param idcom
   * @return String
   * @throws SQLException
   * @throws GestoreException
   * @throws IOException
   */
  private String getTestoDestinatari(String idprg, Long idcom, String descc) throws SQLException, GestoreException, IOException {
    String testodestinatari="Destinatari:";

    String desdatinv_ToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "desdatinv" });
    String desdatlet_ToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "desdatlet" });
    String desdatcons_ToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "desdatcons" });

    String addDescc = "and (descc <> '1' or descc is null)";
    if("1".equals(descc)) {
      addDescc = "and descc = 1";
      testodestinatari = "Destinatari in copia conoscenza:";
    }

    List<?> destinatari= this.sqlManager.getListVector("select desmail, comtipma, desstato, "+desdatinv_ToString+", deserrore, desesitopec, "+desdatlet_ToString+", "+desdatcons_ToString+" from w_invcomdes where idprg = ? and idcom = ? "+addDescc+" order by idcomdes", new Object[] {idprg, idcom});
    if(destinatari!=null && destinatari.size()>0){

      for(int i=0;i<destinatari.size();i++) {
        testodestinatari += SEPARATORE_ELEMENTIMULTIPLI;

        String desmail = SqlManager.getValueFromVectorParam(destinatari.get(i), 0).getStringValue();
        if(desmail==null)
          desmail="";

        String tipomail_cod = SqlManager.getValueFromVectorParam(destinatari.get(i), 1).getStringValue();
        String tipomail = "";
        if(tipomail_cod!=null && !"".equals(tipomail_cod))
          tipomail = this.tabellatiManager.getDescrTabellato("W_003", tipomail_cod);

        String desstato_cod = SqlManager.getValueFromVectorParam(destinatari.get(i), 2).getStringValue();
        String desstato = "";
        if(desstato_cod!=null && !"".equals(desstato_cod))
          desstato = this.tabellatiManager.getDescrTabellato("G_z21", desstato_cod);

        String desdatinv = SqlManager.getValueFromVectorParam(destinatari.get(i), 3).getStringValue();
        if(desdatinv==null)
          desdatinv="";

        String deserrore = SqlManager.getValueFromVectorParam(destinatari.get(i), 4).getStringValue();

        String desesitopec_cod = SqlManager.getValueFromVectorParam(destinatari.get(i), 5).getStringValue();
        String desesitopec = "";
        if(desesitopec_cod!=null && !"".equals(desesitopec_cod))
          desesitopec = this.tabellatiManager.getDescrTabellato("G_z26", desesitopec_cod);

        String desdatlet = SqlManager.getValueFromVectorParam(destinatari.get(i), 6).getStringValue();

        String desdatcons = SqlManager.getValueFromVectorParam(destinatari.get(i), 7).getStringValue();
        if(desdatcons==null)
          desdatcons="";

        testodestinatari += (i+1)+") Indirizzo: "+desmail +" ("+tipomail+")\r\n";
        testodestinatari += "- Stato: " +desstato+"\r\n";
        testodestinatari += "- Data e ora invio: " +desdatinv+"\r\n";
        if(desdatlet!=null && !"".equals(desdatlet))
          testodestinatari += "- Data e ora lettura su portale: " +desdatlet+"\r\n";
        if(desesitopec!=null && !"".equals(desesitopec))
          testodestinatari += "- Esito PEC: " +desesitopec+"\r\n";
        if(desdatcons!=null && !"".equals(desdatcons))
          testodestinatari += "- Data e ora di consegna PEC: " +desdatcons+"\r\n";
        if(deserrore!=null && !"".equals(deserrore))
          testodestinatari += "- Errore: " +deserrore+"\r\n";


      }

    }else {
      testodestinatari = "";
    }

    return testodestinatari;
  }

  /**
   * Viene creata la stringa che concatena la lista dei destinatari
   * @param idprg
   * @param idcom
   * @return String
   * @throws SQLException
   * @throws GestoreException
   * @throws IOException
   */
  private String getTestoDocumentiRichiesti(String idprg, Long idcom) throws SQLException, GestoreException, IOException {
    String testodocumenti="Documenti richiesti:";
    List<?> documenti= this.sqlManager.getListVector("select descrizione, obbligatorio, formato from g1docsoc where idprg = ? and idcom = ? order by numord", new Object[] {idprg, idcom});
    if(documenti!=null && documenti.size()>0){

      for(int i=0;i<documenti.size();i++) {
        testodocumenti += SEPARATORE_ELEMENTIMULTIPLI;

        String descrizione = SqlManager.getValueFromVectorParam(documenti.get(i), 0).getStringValue();
        if(descrizione==null)
          descrizione="";

        String obbligatorio = SqlManager.getValueFromVectorParam(documenti.get(i), 1).getStringValue();
        obbligatorio = "1".equals(obbligatorio) ? "Si" : "No";

        String formdoc = "";
        String formato = SqlManager.getValueFromVectorParam(documenti.get(i), 2).getStringValue();
        if(formato!=null && !"".equals(formato))
          formdoc = this.tabellatiManager.getDescrTabellato("A1105", formato);

        testodocumenti += (i+1)+")Descrizione: "+descrizione +"\r\n";
        testodocumenti += "- Obbligatorio: " +obbligatorio+"\r\n";
        testodocumenti += "- Formato del documento:" +formdoc+"\r\n";
      }

    }else {
      testodocumenti += "Nessun documento richiesto\r\n";
    }

    return testodocumenti;
  }

  /**
   * Lettura della lista degli account email.
   *
   * @param username
   * @param password
   * @param servizio
   * @param ruolo
   * @param nome
   * @param cognome
   * @param codiceUO
   * @param idutente
   * @param idutenteunop
   * @param utente
   * @param idconfi
   * @return
   * @throws GestoreException
   */
  public WSDMListaAccountEmailResType wsdmListaAccountEmail(String username, String password, String servizio, String ruolo, String nome, String cognome,
      String codiceUO, String idutente, String idutenteunop, String utente, String email, String idconfi) throws GestoreException {

    WSDMListaAccountEmailResType wsdmListaAccountEmail = null;

    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
      loginAttr.setRuolo(ruolo);
      loginAttr.setNome(nome);
      loginAttr.setCognome(cognome);
      loginAttr.setCodiceUO(codiceUO);

      if (idutente != null || idutenteunop != null) {
        WSDMLoginEngAttrType loginEngAttr = new WSDMLoginEngAttrType();
        loginEngAttr.setIdUtente(idutente);
        loginEngAttr.setIdUtenteUnitaOperativa(idutenteunop);
        loginAttr.setLoginEngAttr(loginEngAttr);
      }

      WSDMRicercaAccountEmailType wsdmRicercaAccountEmail = new WSDMRicercaAccountEmailType();
      wsdmRicercaAccountEmail.setTipo("pec");
      if (utente != null && !"".equals(utente)) {
        wsdmRicercaAccountEmail.setUtilizzo("invio");
        wsdmRicercaAccountEmail.setUserGroupName(utente);

        // 12/03/2021, se valorizzato l'indirizzo email si cerca quel
        // particolare indirizzo email
        if (email != null && !"".equals(email)) {
          wsdmRicercaAccountEmail.setEmailAddress(email);
        }

      }

      wsdmListaAccountEmail = wsdm.WSDMListaAccountEmail(loginAttr, wsdmRicercaAccountEmail);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura degli lista degli account email: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.listaaccountemail.remote.error", t);
    }
    return wsdmListaAccountEmail;
  }

  /**
   * Viene controllata l'esistenza del file allegato e se è marcato temporalmente.
   * Se esite il file marcato temporalmente non viene cancellato, altrimenti viene cancellato
   * Il metodo restituisce il valore di W_DOCDIG.IDDOCDIG se esiste il file marcato temporalmente
   * @param idprg
   * @param idcom
   * @param entita
   * @return Long
   * @throws SQLException
   */
  public Long cancellaAllegatoSintesi(String idprg, Long idcom) throws SQLException {
    Object par[] = new Object[] {idprg, idcom.toString(), "W_INVCOM"};
    String urlAccesso = ConfigManager.getValore("marcaturaTemp.url");
    boolean applicareMarcaTemp = false;
    boolean esisteMarcatura = false;
    Long IDDOCDIG = null;
    if(urlAccesso!=null && !"".equals(urlAccesso))
      applicareMarcaTemp=true;
    if(applicareMarcaTemp) {
      String sqlFileMarcaTemporale = "select IDDOCDIG from W_DOCDIG where DIGKEY1 = ? and DIGKEY2=? and DIGENT =? and DIGNOMDOC = "
          + AllegatoSintesiUtils.creazioneFiltroNomeFileSintesi(true,this.sqlManager);
      IDDOCDIG=(Long)this.sqlManager.getObject(sqlFileMarcaTemporale, par);
      if(IDDOCDIG!=null)
        esisteMarcatura = true;
    }
    if(!esisteMarcatura) {
      //Si deve cancellare un eventuale allegato di sintesi già presente
      String delete = "delete from W_DOCDIG  where DIGKEY1 = ? and DIGKEY2=? and DIGENT =? and DIGNOMDOC = "
          + AllegatoSintesiUtils.creazioneFiltroNomeFileSintesi(false,this.sqlManager);
      this.sqlManager.update(delete, par);
    }
    return IDDOCDIG;
  }



  /**
   * Viene creato l'allegato di sintesi, che eventualmente viene marcato temporalmente ed inserito in W_DOCDIG.
   * Vengono restituiti il valore di W_DOCDIG.IDDOCDIG, il nome del file, l'estensione, il titolo ed il pdf
   * @param comkey1
   * @param cig
   * @param commsgogg
   * @param commsgtes
   * @param idprg
   * @param idcom
   * @param request
   * @return HashMap
   * @throws SQLException
   * @throws GestoreException
   * @throws IOException
   * @throws DocumentException
   */
  public HashMap<String,Object> aggiungiAllegatoSintesi(String comkey1, String cig, String commsgogg, String commsgtes, String idprg, Long idcom, String entita,HttpServletRequest request) throws SQLException, GestoreException, IOException, DocumentException {
    HashMap<String,Object> ret = new HashMap<String,Object>();
    InputStream iccInputStream = null;
    if(request!=null) {
      iccInputStream = new FileInputStream(request.getSession(true).getServletContext().getRealPath("/WEB-INF/jrReport/sRGB_v4_ICC_preference.icc"));
    } else {
      iccInputStream = new FileInputStream(SpringAppContext.getServletContext().getRealPath("/WEB-INF/jrReport/sRGB_v4_ICC_preference.icc"));
    }
    byte[] pdf = null;
    if("G1STIPULA".equals(entita)) {
      pdf = getTestoComunicazioneFormattatoStipula(comkey1, commsgogg, commsgtes, idprg,idcom,iccInputStream);
    }else {
      pdf = getTestoComunicazioneFormattato(comkey1, cig, commsgogg, commsgtes, idprg,idcom, iccInputStream);
    }
    String insert="insert into w_docdig(idprg,iddocdig,digent,digkey1,digkey2,dignomdoc,digdesdoc,digogg) values(?,?,?,?,?,?,?,?)";
    String esitoMarcaTemporale="";
    HashMap<String,Object> marcaTemporale = null;

    //Se attiva la gestione della marcatemporale si deve chiamare l'apposito servizio
    String urlAccesso = ConfigManager.getValore("marcaturaTemp.url");
    String descrizioneAllegato = "Riepilogo comunicazione";
    String estensione="pdf";

    if(urlAccesso!=null && !"".equals(urlAccesso)) {
      marcaTemporale = MarcaturaTemporaleFileUtils.creaMarcaTemporale(pdf, idcom, comkey1, entita, request);
      esitoMarcaTemporale = (String)marcaTemporale.get("esito");
      if(!"OK".equals(esitoMarcaTemporale)) {
        return null;
      }
    }

    LobHandler lobHandler = new DefaultLobHandler();
    Long maxContatore = (Long)this.sqlManager.getObject("select  coalesce(max(iddocdig),0) + 1 from W_DOCDIG where idprg = ?", new Object[] {idprg});
    String nomeAllegato = idprg + maxContatore.toString() + "_comunicazione.pdf";
    if(urlAccesso!=null && !"".equals(urlAccesso) && "OK".equals(esitoMarcaTemporale)) {
      pdf = (byte[])marcaTemporale.get("file");
      nomeAllegato+=".tsd";
      estensione ="tsd";
      descrizioneAllegato += " con marcatura temporale";
    }

    this.sqlManager.update(insert, new Object[] {idprg, maxContatore, "W_INVCOM",idprg,idcom.toString(),nomeAllegato,descrizioneAllegato,new SqlLobValue(pdf, lobHandler)});


    ret.put("pdf", pdf);
    ret.put("nomeFile", nomeAllegato);
    ret.put("estensioneFile", estensione);
    ret.put("titoloFile",descrizioneAllegato);
    ret.put("idAllegatoSintesi", maxContatore);

    return ret;
  }

  /**
   * L'unica differenza col metodo precedente è che in spring per tale metodo viene chiesto di generare una transazione indipendente.
   * @param comkey1
   * @param cig
   * @param commsgogg
   * @param commsgtes
   * @param idprg
   * @param idcom
   * @param request
   * @return HashMap
   * @throws SQLException
   * @throws GestoreException
   * @throws IOException
   * @throws DocumentException
   */
  public HashMap<String,Object> aggiungiAllegatoSintesiConTransazione(String comkey1, String cig, String commsgogg, String commsgtes, String idprg, Long idcom, String entita,HttpServletRequest request) throws SQLException, GestoreException, IOException, DocumentException {
    HashMap<String,Object> ret = aggiungiAllegatoSintesi(comkey1, cig, commsgogg, commsgtes, idprg, idcom, entita, request);
    return ret;
  }

  /**
   * Ricerca del fascicolo basata su anno e/o classifica. Supportata solo da ITALPROT
   *
   * @param username
   * @param password
   * @param anno
   * @param servizio
   * @param classifica
   * @param idconfi
   * @return WSDMRicercaFascicoloResType
   * @throws GestoreException
   */
  public WSDMRicercaFascicoloResType wsdmRicercaFascicolo(String username, String password, String cognome, Long anno, String classifica,
      String codicefascicolo, String oggetto, String struttura, String codiceproc, String cig, String servizio, String idconfi) throws GestoreException {
    WSDMRicercaFascicoloResType wsdmFascicoloRes = null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio,idconfi);
      WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();

      WSDMRicercaFascicoloType ricerca =  new WSDMRicercaFascicoloType();
      if(anno!=null)
        ricerca.setAnnoFascicolo(anno);
      if(classifica!=null)
        ricerca.setClassificaFascicolo(classifica);

      if(codicefascicolo!=null)
        ricerca.setCodiceFascicolo(codicefascicolo);
      if(oggetto!=null)
        ricerca.setOggettoFascicolo(oggetto);
      if(struttura!=null)
        ricerca.setStrutturaFascicolo(struttura);
      if(codiceproc!=null)
        ricerca.setIdentificativoGara(codiceproc);;
      if(cig!=null)
        ricerca.setCig(cig);


      wsdmFascicoloRes = wsdm.WSDMRicercaFascicolo(loginAttr, ricerca);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la ricerca dei fascicoli: " + t.getMessage(),
          "wsdm.fascicoloprotocollo.fascicoloricerca.remote.error", t);
    }
    return wsdmFascicoloRes;
  }

  /**
   * Metodo adoperato solo da italprot per la richiesta dell'inserimento della firma di un documento
   * @param username
   * @param password
   * @param protocolloDocumentoIn
   * @param idconfi
   * @return WSDMProtocolloDocumentoResType
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType wsdmFirmaInserisci(String username, String password, WSDMProtocolloDocumentoInType protocolloDocumentoIn, String idconfi) throws GestoreException {
    WSDMProtocolloDocumentoResType resType =null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
      resType = wsdm.WSDMFirmaInserisci(null, protocolloDocumentoIn);
    } catch (Throwable  e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata del WSDMFirmaInserisci : " + e.getMessage(),
          "wsdm.firma.inserisci.remote.error", e);
    }
    return resType;
  }

  /**
   * Metodo adoperato solo da italprot per avere restitituito un documento di cui è stata richiesta la firma tramite il servizio WSDMFirmaInserisci
   * @param username
   * @param password
   * @param nDocumento
   * @param idconfi
   * @return
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType wsdmFirmaVerifica(String username, String password, String nDocumento, String idconfi) throws GestoreException {
    WSDMProtocolloDocumentoResType resType =null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
      resType = wsdm.WSDMFirmaVerifica(null, nDocumento);
    } catch (Throwable  e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata del WSDMFirmaVerifica : " + e.getMessage(),
          "wsdm.firma.verifica.remote.error", e);
    }
    return resType;
  }

  /**
   * Il metodo restituisce il numero di fascicoli da creare per la tipologia specificata,
   * ("GARE","AVVISI","ELENCHI","CATALOGHI","RILANCI").
   * Si considerano le occorrenze pubblicate su Portale.
   * @param tipo
   * @param elencoUffint
   * @return
   * @throws SQLException
   */
  public Long getConteggioFascicoliMancanti(String tipo, String elencoUffint) throws SQLException {
    Long conteggio=null;
    String select="";
    if("GARE".equals(tipo)) {
      select = SELECT_CONTEGGIO_FASCICOLI_MANCANTI.replace("?", "in (1,2,3)");
    }else if("AVVISI".equals(tipo)) {
      select = SELECT_CONTEGGIO_FASCICOLI_MANCANTI.replace("?", "= 11");
    }else if("ELENCHI".equals(tipo)) {
      select = SELECT_CONTEGGIO_FASCICOLI_MANCANTI.replace("?", "= 10");
    }else if("CATALOGHI".equals(tipo)) {
      select = SELECT_CONTEGGIO_FASCICOLI_MANCANTI.replace("?", "= 20");
    }else if("RILANCI".equals(tipo)) {
      select = SELECT_CONTEGGIO_FASCICOLI_MANCANTI_RILANCI.replace("?", "= 11");
      if(!"".equals(elencoUffint))
        select += FILTRO_UFFINT_SELECT_CONTEGGIO_FASCICOLI_MANCANTI_RILANCI.replace("$", elencoUffint);
    }

    if(!"RILANCI".equals(tipo) && !"".equals(elencoUffint))
      select += FILTRO_UFFINT_SELECT_CONTEGGIO_FASCICOLI_MANCANTI.replace("$", elencoUffint);

    conteggio = (Long)sqlManager.getObject(select, new Object[] {});

    return conteggio;
  }

  /**
   * Il metodo valorizza gli attributi isSigned e isTimeMarked della classe WSDMProtocolloAllegatoType.
   * Se nel nome del file passato come parametro è presente le estensioni "p7m" si imposta a 1 isSigned.
   * Se nel nome del file passato come parametro è presente le estensioni "tsd" si imposta a 1 isTimeMarked.
   *
   * @param nomeFile
   * @param allegatoType
   * @return WSDMProtocolloAllegatoType
   */
  public static WSDMProtocolloAllegatoType popolaAllegatoInfo(String nomeFile, WSDMProtocolloAllegatoType allegatoType) {
    if(nomeFile!=null) {
      nomeFile = nomeFile.toLowerCase();
      if(nomeFile.lastIndexOf("p7m")>0)
        allegatoType.setIsSigned(new Long(1));
      if(nomeFile.lastIndexOf("tsd")>0)
        allegatoType.setIsTimeMarked(new Long(1));

    }
    return allegatoType;
  }

  public byte[] esportaRiepilogoComunicazione(String idprg, Long idcom, String operatore, InputStream iccInputStream) throws SQLException, GestoreException, IOException, DocumentException {

    String comdatins_ToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "comdatins" });
    String comdatprot_ToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "comdatprot" });

    Vector<?> datiComunicazione = this.sqlManager.getVector("select "
        + "comkey1, " //0
        + "coment, " //1
        + "commsgtes, "//2
        + "commsgogg, "//3
        + "comstato, "//4
        +  comdatins_ToString+", "//5
        + "comdatsca, "//6
        + "comorasca, "//7
        + "committ, "//8
        + "comcodope, "//9
        + "comnumprot, "//10
        +  comdatprot_ToString+", "//11
        + "commodello " //12

        + "from w_invcom where idprg = ? and idcom = ?", new Object[] {idprg, idcom});

    String comkey1 =  SqlManager.getValueFromVectorParam(datiComunicazione, 0).getStringValue();
    String coment =  SqlManager.getValueFromVectorParam(datiComunicazione, 1).getStringValue();
    String testoComunicazione =  SqlManager.getValueFromVectorParam(datiComunicazione, 2).getStringValue();
    String oggettoComunicazione =  SqlManager.getValueFromVectorParam(datiComunicazione, 3).getStringValue();
    String stato =  SqlManager.getValueFromVectorParam(datiComunicazione, 4).getStringValue();
    String datains =  SqlManager.getValueFromVectorParam(datiComunicazione, 5).getStringValue();
    String comdatsca = SqlManager.getValueFromVectorParam(datiComunicazione, 6).getStringValue();
    String comorasca = SqlManager.getValueFromVectorParam(datiComunicazione, 7).getStringValue();
    String committ  = SqlManager.getValueFromVectorParam(datiComunicazione, 8).getStringValue();
    String comcodope = SqlManager.getValueFromVectorParam(datiComunicazione, 9).getStringValue();
    String comnumprot = SqlManager.getValueFromVectorParam(datiComunicazione, 10).getStringValue();
    String comdatprot = SqlManager.getValueFromVectorParam(datiComunicazione, 11).getStringValue();
    String commodello = SqlManager.getValueFromVectorParam(datiComunicazione, 12).getStringValue();


    String testo = "";
    //metadati generazione report
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss", Locale.ITALIAN);
    String dateString = sdf.format(new Date());

    testo += "Report comunicazione PG/"+idcom+" generato il "+ dateString.split(" ")[0]+" alle ore "+ dateString.split(" ")[1] +"\r\nOperatore " +operatore+"\r\n";
    testo += SEPARATORE_LINEA_COMPLETA+"\r\n";

    //sezione denominazione
    String denominazione = ConfigManager.getValore("denominazioneEnte");
    if (denominazione != null) {
      testo += denominazione + "\r\n";
    }

    String nomein = null;
    String viaein = null;
    String nciein = null;
    String capein = null;
    String citein = null;
    String proein = null;

    String oggettoStipula=null;

    Long genere = null;
    String codgar = null;

    Vector<?> datiGara= this.sqlManager.getVector("select genere,codgar  from v_gare_genere where codice=?", new Object[]{comkey1});
    if (datiGara != null) {
      genere = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
      codgar = SqlManager.getValueFromVectorParam(datiGara, 1).getStringValue();
      if(genere == 100 || genere == 300) { //devo recuperare il vero genere se siamo in un lotto
        Vector<?> datiLotto= this.sqlManager.getVector("select genere,codgar  from v_gare_genere where codice=?", new Object[]{codgar});
        genere = SqlManager.getValueFromVectorParam(datiLotto, 0).longValue();
      }
    }

    String select = "select nomein,viaein, nciein, capein, citein, proein from torn,uffint where codgar=? and cenint=codein";

    if("G1STIPULA".equals(coment)) {
      select = "select nomein,viaein, nciein, capein, citein, proein,oggetto from v_gare_stipula,uffint where codstipula=? and cenint=codein";
      codgar=comkey1;
    }

    Vector<?> datiUffint= this.sqlManager.getVector(select, new Object[]{codgar});
    if (datiUffint != null) {
      nomein = SqlManager.getValueFromVectorParam(datiUffint, 0).getStringValue();
      viaein = SqlManager.getValueFromVectorParam(datiUffint, 1).getStringValue();
      nciein = SqlManager.getValueFromVectorParam(datiUffint, 2).getStringValue();
      capein = SqlManager.getValueFromVectorParam(datiUffint, 3).getStringValue();
      citein = SqlManager.getValueFromVectorParam(datiUffint, 4).getStringValue();
      proein = SqlManager.getValueFromVectorParam(datiUffint, 5).getStringValue();
      if("G1STIPULA".equals(coment)) {
        oggettoStipula = SqlManager.getValueFromVectorParam(datiUffint, 6).getStringValue();
      }

      //Stazione appaltante o ufficio
      if (nomein == null)
        nomein = "";
      testo += nomein + "\r\n";

      // Indirizzo
      if (viaein == null)
        viaein = "";
      testo += viaein;
      if (nciein != null && !"".equals(nciein))
        testo += ", " + nciein;
      if (capein != null && !"".equals(capein))
        testo +=  " - " + capein + " ";
      if (citein == null)
        citein = "";
      testo += citein ;
      if(proein !=null && !"".equals(proein))
        testo += " (" + proein + ")";

      testo += "\r\n";
      }
      //Riga vuota
      testo += SEPARATORE_SEZIONI+"\r\n";




      // Oggetto
    if("G1STIPULA".equals(coment)) {
      testo += "Oggetto stipula: " + oggettoStipula + "\r\nCodice stipula: " + comkey1 + "\r\n";
    }else {
      String oggettoGara = null;

      String cig = null;


      Vector<?> datiTorn = sqlManager.getVector("select codcig from v_gare_torn v where codgar = ?", new Object[]{codgar});
      if(datiTorn!=null && datiTorn.size()>0){
        cig = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
      }

      if (cig == null) {
        cig = "";
      }
      switch (genere.intValue()) {
        case 1:
        case 3:
          oggettoGara = (String)this.sqlManager.getObject("select destor from torn where codgar=?", new Object[]{codgar});
          if (oggettoGara == null)
            oggettoGara = "";
          testo += "Oggetto gara: " + oggettoGara + "\r\nCodice gara: " + codgar + "\r\nCIG: " + cig + "\r\n";
          break;
        case 2:
        case 4:
          oggettoGara = (String)this.sqlManager.getObject("select not_gar from gare where ngara=?", new Object[]{comkey1});
          if (oggettoGara == null)
            oggettoGara = "";
          testo += "Oggetto gara: " + oggettoGara + "\r\nCodice gara: " + comkey1 + "\r\nCIG: " + cig + "\r\n";
          break;
        case 10:
          oggettoGara = (String)this.sqlManager.getObject("select oggetto from garealbo where ngara=?", new Object[]{comkey1});
          if (oggettoGara == null)
            oggettoGara = "";
          testo += "Oggetto elenco: " + oggettoGara + "\r\nCodice elenco: " + comkey1 + "\r\n";
          break;
        case 11:
          oggettoGara = (String)this.sqlManager.getObject("select oggetto from gareavvisi where ngara=?", new Object[]{comkey1});
          if (oggettoGara == null)
            oggettoGara = "";
          testo += "Oggetto avviso: " + oggettoGara + "\r\nCodice avviso: " + comkey1 + "\r\n";
          break;
        case 20:
          oggettoGara = (String)this.sqlManager.getObject("select oggetto from garealbo where ngara=?", new Object[]{comkey1});
          if (oggettoGara == null)
            oggettoGara = "";
          testo += "Oggetto catalogo: " + oggettoGara + "\r\nCodice catalogo: " + comkey1 + "\r\n";
          break;
      }
    }
    testo += SEPARATORE_LINEA_COMPLETA+ "\r\n";
    //Oggetto comunicazione
    if (oggettoComunicazione == null)
      oggettoComunicazione = "";
    testo += "Oggetto comunicazione: " + oggettoComunicazione + "\r\n\r\n";

    //Testo comunicazione
    String commsgtip = (String)this.sqlManager.getObject("select commsgtip from w_invcom where idprg = ? and idcom = ?", new Object[] {idprg, idcom });
    if("1".equals(commsgtip))
      testoComunicazione = "(testo della comunicazione in formato html)\r\n";
    else if (testoComunicazione == null)
      testoComunicazione = "";
    testo += testoComunicazione + "\r\n";

    //in caso di soccorso istruttorio
    String commod = "";
    if(commodello!=null && !"".equals(commodello)) {

      testo += SEPARATORE_SEZIONI+"\r\n";

      commod = this.tabellatiManager.getDescrTabellato("W_008", commodello);
      testo += "Tipologia di comunicazione: "+commod + "\r\n";

      if(comdatsca!=null && !"".equals(comdatsca)) {
        testo += "Data termine presentazione documentazione: "+comdatsca + "\r\n";
      }

      if(comorasca!=null && !"".equals(comorasca)) {
        testo += "Ora termine presentazione documentazione: "+comorasca + "\r\n";
      }
      testo += "\r\n";
      testo += this.getTestoDocumentiRichiesti(idprg,idcom);
    }

    testo += SEPARATORE_SEZIONI+"\r\n";
    testo += this.getTestoAllegati(idprg, idcom)+"\r\n";

    //dati della w_invcom

    //Riga vuota
    testo += SEPARATORE_LINEA_COMPLETA+"\r\n";

    String statoCom = "";
    if(stato != null && !"".equals(stato))
      statoCom = this.tabellatiManager.getDescrTabellato("G_z20", stato);
    testo += "Stato della comunicazione: "+statoCom + "\r\n";

    if(datains == null)
      datains = "";
    testo += "Data di inserimento: "+datains + "\r\n";

    if(committ!=null)
    testo += "Mittente: "+committ + "\r\n";

    String operCom = "";
    if(comcodope!=null && !"".equals(comcodope)) {
      Vector<?> sysute = this.sqlManager.getVector("select sysute from usrsys where syscon=?", new Object[] {comcodope});
      operCom= SqlManager.getValueFromVectorParam(sysute, 0).stringValue();
    }
    testo += "Operatore: "+operCom + "\r\n";

    //solo se attiva la protocollazione
    if(comnumprot!=null && !"".equals(comnumprot)) {
      testo += "Numero protocollo: "+comnumprot + "\r\n";
    }
    //solo se attiva la protocollazione
    if(comdatprot!=null && !"".equals(comdatprot)) {
      testo += "Data protocollo: "+comdatprot + "\r\n";
    }

    testo += SEPARATORE_SEZIONI+"\r\n";
    testo += this.getTestoDestinatari(idprg, idcom, null);

    testo += SEPARATORE_SEZIONI+"\r\n";
    testo += this.getTestoDestinatari(idprg, idcom, "1");



    byte[] pdf = null;
    try {
      pdf = UtilityStringhe.string2PdfA(testo, iccInputStream);
      logger.debug("PDF-A creato.");
      return pdf;
    } catch (com.itextpdf.text.DocumentException e) {
      throw new DocumentException(e);
    }
  }


  /**
   * Lettura del protocollo asincrono
   * @param username
   * @param password
   * @param cognome
   * @param codiceProcesso
   * @param servizio
   * @param idconfi
   * @return WSDMProtocolloDocumentoResType
   * @throws GestoreException
   */
  public WSDMProtocolloDocumentoResType wsdmProtocolloAsincronoEsito(String username, String password, String cognome, String codiceProcesso, String servizio, String idconfi) throws GestoreException {
    WSDMProtocolloDocumentoResType resType =null;
    try {
      WSDM_PortType wsdm = this.getWSDM(username, password, servizio, idconfi);
      WSDMLoginAttrType login = new WSDMLoginAttrType();
      login.setCognome(cognome);
      resType = wsdm.WSDMProtocolloAsincronoEsito(login, codiceProcesso);
    } catch (Throwable  e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata del WSDMProtocolloAsincronoEsito : " + e.getMessage(),
          "wsdm.protocollo.asincrono.remote.error", e);
    }
    return resType;
  }

}




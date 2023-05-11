/*
 * Created on 05/dic/2017
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.utils.utility.UtilityWeb;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazione_ServiceLocator;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoResType;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;
import it.maggioli.eldasoft.ws.erp.WSERPArticoloResType;
import it.maggioli.eldasoft.ws.erp.WSERPCondizionePagamentoResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPLiquidatoResType;
import it.maggioli.eldasoft.ws.erp.WSERPLiquidatoType;
import it.maggioli.eldasoft.ws.erp.WSERPLottoRdaType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPPosizioneRdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;
import it.maggioli.eldasoft.ws.erp.WSERP_PortType;
import it.maggioli.eldasoft.ws.erp.WSERP_ServiceLocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class GestioneWSERPManager {


  /** Logger */
  static Logger               logger                                          = Logger.getLogger(GestioneWSDMManager.class);

  public  static final String SERVIZIO_ERP                                    = "WSERP";
  private static final String PROP_WSERPCONFIGURAZIONE_ERP_URL                = "wserpconfigurazione.erp.url";
  private static final String PROP_WSERP_ERP_URL                              = "wserp.erp.url";


  private SqlManager          sqlManager;

  private PgManager           pgManager;

  private TabellatiManager    tabellatiManager;

  private GenChiaviManager           genChiaviManager;


  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * Restituisce l'oggetto contenente le configurazioni del server in funzione
   * del servizio richiesto.
   *
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSERPConfigurazioneOutType wserpConfigurazioneLeggi(String servizio) throws GestoreException {
    WSERPConfigurazioneOutType configurazione = null;
    try {
      String url = null;
      if (SERVIZIO_ERP.equals(servizio)) {
        url = ConfigManager.getValore(PROP_WSERPCONFIGURAZIONE_ERP_URL);
      }else if (SERVIZIO_ERP.equals(servizio)) {
        url = ConfigManager.getValore(PROP_WSERPCONFIGURAZIONE_ERP_URL);
      }

      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al servizio di configurazione non e' definito",
            "wserpconfigurazione.url.error");
      }

      WSERPConfigurazione_ServiceLocator wserpconfigurazioneLocator = new WSERPConfigurazione_ServiceLocator();
      wserpconfigurazioneLocator.setWSERPConfigurazioneImplPortEndpointAddress(url);
      configurazione = wserpconfigurazioneLocator.getWSERPConfigurazioneImplPort().WSERPConfigurazioneLeggi();
    } catch (RemoteException r) {
      throw new GestoreException("Si e' verificato un errore durante la lettura delle configurazioni: " + r.getMessage(),
          "wserpconfigurazione.configurazioneleggi.remote.error", r);
    } catch (ServiceException s) {
      throw new GestoreException("Si e' verificato un errore durante la lettura delle configurazioni: " + s.getMessage(),
          "wserpconfigurazione.configurazioneleggi.remote.error", s);
    }
    return configurazione;
  }

  /**
   * Restituisce puntatore al servizio WSERP.
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
  private WSERP_PortType getWSERP(String username, String password, String servizio) throws GestoreException, NoSuchAlgorithmException,
      NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ServiceException {

    String url = null;
    if (SERVIZIO_ERP.equals(servizio)) {
      url = ConfigManager.getValore(PROP_WSERP_ERP_URL);
      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al servizio per la gestione dell' ERP non e' definito",
            "wserp.erp.url.error");
      }
    }



    WSERP_ServiceLocator wserpLocator = new WSERP_ServiceLocator();
    wserpLocator.setWSERPImplPortEndpointAddress(url);
    Remote remote = wserpLocator.getPort(WSERP_PortType.class);
    Stub axisPort = (Stub) remote;

    WSERP_PortType wserp = (WSERP_PortType) axisPort;
    return wserp;
  }

  /**
   * Lettura dellla lista di RDA.
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSERPRdaResType wserpListaRda(String username, String password, String servizio, WSERPRdaType rdaSearch) throws GestoreException {
    WSERPRdaResType wserpRdaRes = null;
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpRdaRes = wserp.WSERPListaRda(username, password, rdaSearch );
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura della lista delle RdA: " + t.getMessage(),
          "wserp.erp.listarda.remote.error", t);
    }

    wserpRdaRes.getRdaArray();

    return wserpRdaRes;
  }


  /**
   * Lettura dell'articolo.
   *
   * @param username
   * @param password
   * @param servizio
   * @param codiceArticolo
   * @return
   * @throws GestoreException
   */
  public WSERPRdaResType wserpDettaglioRda(String username, String password, String servizio, WSERPRdaType rdaSearch) throws GestoreException {
    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpRdaRes = wserp.WSERPDettaglioRda(username, password, rdaSearch);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del dettaglio rda: " + t.getMessage(),
          "wserp.erp.dettaglioRda.remote.error", t);
    }

    return wserpRdaRes;
  }


  /**
   * Lettura dell'ODA.
   *
   * @param username
   * @param password
   * @param servizio
   * @param odaSearch
   * @return WSERPOdaResType
   * @throws GestoreException
   */
  public WSERPOdaResType wserpDettaglioOda(String username, String password, String servizio, WSERPOdaType odaSearch) throws GestoreException {
    WSERPOdaResType wserpOdaRes = new WSERPOdaResType();
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpOdaRes = wserp.WSERPDettaglioOda(username, password, odaSearch);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del dettaglio oda: " + t.getMessage(),
          "wserp.erp.dettaglioOda.remote.error", t);
    }

    return wserpOdaRes;
  }
  
  

  /**
   * Lettura degli allegati di una Rda.
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSERPRdaResType wserpListaFilesRda(String username, String password, String servizio, WSERPRdaType[] rdaArray) throws GestoreException {
    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();

    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpRdaRes = wserp.WSERPListaFilesRda(username, password, rdaArray);

    } catch (Throwable t) {

      throw new GestoreException("Si e' verificato un errore durante la lettura della lista delle RdA: " + t.getMessage(),
          "wserp.erp.listafilesrda.remote.error", t);
    }



    return wserpRdaRes;
  }


  /**
   * Download allegato
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public void wserpDownloadFileAllegato(String username, String password, String servizio, String nomedoc, String path, HttpServletResponse response) throws GestoreException {
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);

      WSERPAllegatoResType wserpAllegatoRes = wserp.WSERPDownloadFile(username, password, nomedoc, path, null,null);

      WSERPAllegatoType wserpAllegato = wserpAllegatoRes.getAllegato();
      //recupero il file content
      byte[] fileContent = wserpAllegato.getContenuto();

      UtilityWeb.download(nomedoc, fileContent, response);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura dell'articolo: " + t.getMessage(),
          "wserp.erp.dettaglioarticolo.remote.error", t);
    }

  }
  
  /**
   * Download allegato
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSERPAllegatoType wserpLeggiAllegato(String username, String password, String nomedoc, String servizio, String idfile) throws GestoreException {
    
	WSERPAllegatoType wserpAllegato=null;  
	try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);

      WSERPAllegatoResType wserpAllegatoRes = wserp.WSERPDownloadFile(username, password, nomedoc, null, null, idfile);

      wserpAllegato = wserpAllegatoRes.getAllegato();


    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura dell'articolo: " + t.getMessage(),
          "wserp.erp.dettaglioarticolo.remote.error", t);
    }
	
	return wserpAllegato;

  }
  
  
  /**
   * Upload allegato
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public int wserpUploadFileAllegati(String username, String password, String servizio, String codiceGara, String codiceLotto, WSERPAllegatoType[] allegatoArray) throws GestoreException {
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);

      wserp.WSERPUploadFile(username, password, codiceGara, codiceLotto, allegatoArray);

      return 0;

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura dell'articolo: " + t.getMessage(),
          "wserp.erp.dettaglioarticolo.remote.error", t);
    }

  }

  /**
   * Lettura dell'articolo.
   *
   * @param username
   * @param password
   * @param servizio
   * @param codiceArticolo
   * @return
   * @throws GestoreException
   */
  public WSERPArticoloResType wserpDettaglioArticolo(String username, String password, String servizio, String codiceArticolo) throws GestoreException {
    WSERPArticoloResType wserpArticoloRes = null;
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpArticoloRes = wserp.WSERPDettaglioArticolo(username, password, codiceArticolo);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura dell'articolo: " + t.getMessage(),
          "wserp.erp.dettaglioarticolo.remote.error", t);
    }

    return wserpArticoloRes;
  }

  /**
   * Lettura dell'articolo.
   *
   * @param username
   * @param password
   * @param servizio
   * @param codiceArticolo
   * @return
   * @throws GestoreException
   */
  public WSERPFornitoreResType wserpDettaglioFornitore(String username, String password, String servizio, String codiceFiscale, String partitaIva, WSERPFornitoreType fornitoreSearch) throws GestoreException {
    WSERPFornitoreResType wserpFornitoreRes = null;
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpFornitoreRes = wserp.WSERPDettaglioFornitore(username, password, codiceFiscale, partitaIva, fornitoreSearch);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fornitore: " + t.getMessage(),
          "wserp.erp.dettagliofornitore.remote.error", t);
    }

    return wserpFornitoreRes;
  }

  /**
   * Creazione del fornitore.
   *
   * @param username
   * @param password
   * @param servizio
   * @param fornitore
   * @return
   * @throws GestoreException
   */
  public WSERPFornitoreResType wserpCreaFornitore(String username, String password, String servizio, WSERPFornitoreType fornitore) throws GestoreException {
    WSERPFornitoreResType wserpFornitoreRes = null;
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpFornitoreRes = wserp.WSERPCreaFornitore(username, password, fornitore);

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del fornitore: " + t.getMessage(),
          "wserp.erp.dettagliofornitore.remote.error", t);
    }

    return wserpFornitoreRes;
  }

  /**
   * SET RDA in GARA
   *
   * @param username
   * @param password
   * @param ruolo
   * @param numeroDocumento
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSERPRdaResType wserpAssociaRdaGara(String username, String password, String servizio,
      String codiceCarrello, String codiceRda, String posizioneRda, String codiceLotto, WSERPRdaType rdaDatiaggiuntivi, Boolean flagAssociazione) throws GestoreException {
    WSERPRdaResType wserpRdaRes = null;

    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      Long numeroProgressivoContratto = null;
      String codiceLavoro = null;
      Long numeroAppalto = null;
      String tipoRdaErp = null;
      wserpRdaRes = wserp.WSERPAssociaRdaGara(username, password, codiceCarrello, codiceRda, posizioneRda, codiceLotto, codiceLavoro, numeroAppalto, numeroProgressivoContratto, tipoRdaErp, rdaDatiaggiuntivi, flagAssociazione);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + t.getMessage(),
          "wserp.erp.associarda.remote.error", t);
    }

    return wserpRdaRes;

  }

  /**
   * Aggiudica RDA
   *
   * @param username
   * @param password
   * @param servizio
   * @param odaArray
   * @return
   * @throws GestoreException
   */
  public WSERPRdaResType wserpAggiudicaRdaGara(String username, String password, String servizio, WSERPOdaType[] odaArray, WSERPGaraType datiGara)
    throws GestoreException {
    WSERPRdaResType wserpRdaRes = null;
    Boolean isPresRda = true;

    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);

      if(isPresRda){
        wserpRdaRes = wserp.WSERPAggiudicaRdaGara(username, password, odaArray, datiGara);
      }else{
        wserpRdaRes.setEsito(false);
        wserpRdaRes.setMessaggio("Non sono presenti tutte le rda per la comunicazione dell'esito dell'aggiudicazione!");
      }

    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la aggiudicazione delle RdA: " + t.getMessage(),
          "wserp.erp.aggiudicarda.remote.error", t);
    }

    wserpRdaRes.isEsito();

    return wserpRdaRes;
  }


  /**
   * Lettura dei dati Legge 190.
   *
   * @param username
   * @param password
   * @param servizio
   * @param array cig
   * @return
   * @throws GestoreException
   */
  public WSERPLiquidatoResType wserpDatiL190(String username, String password, String servizio, String[] cigArray, Calendar datainizio, Calendar datafine) throws GestoreException {
    WSERPLiquidatoResType wserpLiquidatoRes = new WSERPLiquidatoResType();
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpLiquidatoRes = wserp.WSERPDatiL190(username, password, cigArray, datainizio, datafine);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura dei dati della legge 190: " + t.getMessage(),
          "wserp.erp.datil190.remote.error", t);
    }

    return wserpLiquidatoRes;
  }


  /**
   * Lettura dellla lista delle condizioni di pagamento.
   *
   * @param username
   * @param password
   * @param servizio
   * @return
   * @throws GestoreException
   */
  public WSERPCondizionePagamentoResType wserpListaCondizioniPagamento(String username, String password, String servizio) throws GestoreException {
    WSERPCondizionePagamentoResType wserpCondizionePagamentoRes = null;
    try {
      WSERP_PortType wserp = this.getWSERP(username, password, servizio);
      wserpCondizionePagamentoRes = wserp.WSERPListaCondizioniPagamento(username, password);
    } catch (Throwable t) {
      throw new GestoreException("Si e' verificato un errore durante la lettura della lista delle condizioni di pagamento: " + t.getMessage(),
          "wserp.erp.listacondpag.remote.error", t);
    }

    return wserpCondizionePagamentoRes;

  }



  /**
   * Metodo cper il recupero delle credenziali
   *
   * @param servizio (WSERP)
   *
   * @return String[] (credenziali)
   * @throws GestoreException
   *
   */

  public String[] wserpGetLogin(Long syscon, String servizio) throws GestoreException{
    String [] cred = new String [2];

    List<?> datiWSLogin;
    try {

      datiWSLogin = sqlManager.getVector(
          "select username, password from wslogin where syscon = ? and servizio = ?", new Object[] { new Long(-1), servizio });

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
        }

        cred[0] = username;
        cred[1] = passwordDecoded;

      }

    } catch (SQLException e) {
      throw new GestoreException("Errore nel recupero delle credenziali di accesso al sistema WSERP", null,e);
    } catch (CriptazioneException e) {
      throw new GestoreException("Errore nella memorizzazione delle credenziali di accesso al sistema WSERP", null,e);
    }

    return cred;
  }


  /*
   *
   */
  public int insCollegamentoRda(HttpServletRequest request, String username, String password, String servizio, String codiceGara, String codiceLotto,
      WSERPRdaType[] rdaArray, String linkRda, String uffint)

  throws GestoreException {

  logger.debug("insLavorazioneLotto: inizio metodo");
  try {
    WSERPConfigurazioneOutType configurazione = this.wserpConfigurazioneLeggi(servizio);
    String tipoWSERP = configurazione.getRemotewserp();

    String strSqlInsert="";
    String strSqlInsertEst="";
    if(("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)) && "1".equals(linkRda)){
      strSqlInsert="insert into GARERDA (ID,CODGAR,DATCRE,DATRIL,CODCARR,NUMRDA,POSRDA,DATACONS,LUOGOCONS," +
      		"CODVOC,VOCE,UNIMIS,CODCAT,PERCIVA,QUANTI,PREZUN)" +
      		" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }else{
      strSqlInsert="insert into GCAP (NGARA,CONTAF,NORVOC,CODVOC,QUANTI,PREZUN,CLASI1," +
      "SOLSIC,SOGRIB,VOCE,IDVOC,UNIMIS,CODCARR,CODRDA,POSRDA,CODCAT,PERCIVA,DATACONS,LUOGOCONS)" +
      " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      strSqlInsertEst = "insert into GCAP_EST (DESEST,NGARA,CONTAF) values(?,?,?)";

    }

    //inserire anche in GCAP_EST la descrizione

    if("CAV".equals(tipoWSERP)){
      if(rdaArray != null && rdaArray.length > 0){
        for (int h = 0; h < rdaArray.length; h++) {
          WSERPRdaType hRda = rdaArray[h];
          WSERPPosizioneRdaType[] hPosRdaArray = hRda.getPosizioneRdaArray();
          if(hPosRdaArray != null && hPosRdaArray.length > 0){
            for (int p = 0; p < hPosRdaArray.length; p++) {
              WSERPPosizioneRdaType hPosRda = hPosRdaArray[p];
              String codiceCarrello = null;
              String codiceRda = hRda.getCodiceRda();
              String posizioneRda = hPosRda.getPosizioneRiferimento();
              String codiceCategoria = null;
              String percentualeIva = null;

              Double quantita = null;
              Double prezzo = null;
              if(hPosRda.getQuantita() != null){
                quantita = hPosRda.getQuantita();
              }
              String descrizione = hRda.getOggetto();

              String oggetto = hPosRda.getDescrizioneEstesa();

              String codiceArticolo = hPosRda.getCodiceArticolo();
              codiceArticolo= UtilityStringhe.convertiNullInStringaVuota(codiceArticolo) ;
              String unitaMisura = hPosRda.getUm();
              unitaMisura = UtilityStringhe.convertiNullInStringaVuota(unitaMisura);
              //verifica ed eventuale inserimento unita misura
              Long ret = (Long) this.sqlManager.getObject(
                  "select count(*) from UNIMIS where CONTA = ? and TIPO = ? ",
                  new Object[] { new Long(-1), unitaMisura });
              if(new Long(0).equals(ret) && !"".equals(unitaMisura)){
                this.sqlManager.update("insert into UNIMIS (CONTA, TIPO, DESUNI, NUMDEC) values (?, ?, ?, ?)",
                    new Object[] { new Long(-1), unitaMisura, unitaMisura, new Long(0) });
              }
              if(hPosRda.getPrezzoPrevisto() != null){
                prezzo = hPosRda.getPrezzoPrevisto();
              }


              Long maxContafEsistente = (Long) sqlManager.getObject(
                  "select coalesce(max(contaf),0) from GCAP where ngara = ?", new Object[] { codiceLotto });
              Long contaf = new Long(maxContafEsistente+1);
              if("".equals(codiceArticolo)){
                codiceArticolo = "#"+contaf+"#";
              }
              this.sqlManager.update(strSqlInsert, new Object[]{codiceLotto, contaf,
                  new Long(maxContafEsistente+1),codiceArticolo,quantita,prezzo,new Long(3),"2","2",oggetto,
                  null,unitaMisura,codiceCarrello,codiceRda,posizioneRda,null,null,null,null});
              //descrizione estesa
              this.sqlManager.update(strSqlInsertEst,
                  new Object[] { descrizione,codiceLotto, new Long(maxContafEsistente+1)});
              maxContafEsistente++;



            }//for
          }//if
        }//for
      }

    }else if("RAIWAY".equals(tipoWSERP)){
      // impelmentazione per RAIWAY
      if(rdaArray != null && rdaArray.length > 0){
        for (int h = 0; h < rdaArray.length; h++) {
          WSERPRdaType hRda = rdaArray[h];
          WSERPPosizioneRdaType[] hPosRdaArray = hRda.getPosizioneRdaArray();
          if(hPosRdaArray != null && hPosRdaArray.length > 0){
            for (int p = 0; p < hPosRdaArray.length; p++) {
              WSERPPosizioneRdaType hPosRda = hPosRdaArray[p];
              Long codiceCarrello = hPosRda.getIdLotto();
              String codiceRda = hRda.getCodiceRda();
              String posizioneRda = hPosRda.getPosizioneRiferimento();
              String codiceCategoria = null;
              String percentualeIva = null;
              String sicurezza = null;
              if(hPosRda.getSicurezza() != null){
                if(hPosRda.getSicurezza()){
                  sicurezza = "1";
                }else {
                  sicurezza = "2";
                }
              }

              Double quantita = null;
              Double prezzoPrevisto = hPosRda.getPrezzoPrevisto();
              if(hPosRda.getQuantita() != null){
                quantita = hPosRda.getQuantita();
              }

              String oggetto = hPosRda.getDescrizioneEstesa();

              String codiceArticolo = hPosRda.getCodiceArticolo();
              
              String unitaMisura = hPosRda.getUm();
              unitaMisura = UtilityStringhe.convertiNullInStringaVuota(unitaMisura);
              //verifica ed eventuale inserimento unita misura
              Long ret = (Long) this.sqlManager.getObject(
                  "select count(*) from UNIMIS where CONTA = ? and TIPO = ? ",
                  new Object[] { new Long(-1), unitaMisura });
              if(new Long(0).equals(ret) && !"".equals(unitaMisura)){
                this.sqlManager.update("insert into UNIMIS (CONTA, TIPO, DESUNI, NUMDEC) values (?, ?, ?, ?)",
                    new Object[] { new Long(-1), unitaMisura, unitaMisura, new Long(0) });
              }


              Long maxContafEsistente = (Long) sqlManager.getObject(
                  "select coalesce(max(contaf),0) from GCAP where ngara = ?", new Object[] { codiceLotto });
              Long contaf = new Long(maxContafEsistente+1);
              this.sqlManager.update(strSqlInsert, new Object[]{codiceLotto, contaf,
                  new Long(maxContafEsistente+1),codiceArticolo,quantita,prezzoPrevisto,new Long(3),"2",sicurezza,oggetto,
                  null,unitaMisura,codiceCarrello,codiceRda,posizioneRda,null,null,null,null});
              //descrizione estesa
              this.sqlManager.update(strSqlInsertEst,
                  new Object[] { null,codiceLotto, new Long(maxContafEsistente+1)});
              maxContafEsistente++;



            }//for
          }//if
        }//for
      }
    }else{
      if(rdaArray != null && rdaArray.length > 0){
        for (int h = 0; h < rdaArray.length; h++) {
          WSERPRdaType hRda = rdaArray[h];
          Double quantita = null;
          Double prezzo = null;
          if(hRda.getQuantita() != null){
            quantita = hRda.getQuantita();
          }
          String descrizione = hRda.getDescrizione();
          String oggetto = hRda.getOggetto();
          Calendar calConsegna = hRda.getDataConsegna();
          Date dataConsegna = null;
          if(calConsegna != null){
            dataConsegna = calConsegna.getTime();
            String dConsStr = UtilityDate.convertiData(dataConsegna, UtilityDate.FORMATO_GG_MM_AAAA);
            //oggetto = oggetto + " Data consegna: " + dConsStr;
          }
          String luogoConsegna = hRda.getLuogoConsegna();
          luogoConsegna = UtilityStringhe.convertiNullInStringaVuota(luogoConsegna);
          if(!"".equals(luogoConsegna)){
            //oggetto = oggetto + " - Luogo consegna: " + luogoConsegna;
          }


          String codiceArticolo = hRda.getCodiceArticolo();
          String unitaMisura = hRda.getUm();
          unitaMisura = UtilityStringhe.convertiNullInStringaVuota(unitaMisura);
          //verifica ed eventuale inserimento unita misura
          Long ret = (Long) this.sqlManager.getObject(
              "select count(*) from UNIMIS where CONTA = ? and TIPO = ? ",
              new Object[] { new Long(-1), unitaMisura });
          if(new Long(0).equals(ret) && !"".equals(unitaMisura)){
            this.sqlManager.update("insert into UNIMIS (CONTA, TIPO, DESUNI, NUMDEC) values (?, ?, ?, ?)",
                new Object[] { new Long(-1), unitaMisura, unitaMisura, new Long(0) });
          }
          if(hRda.getValoreStimato() != null){
            prezzo = hRda.getValoreStimato();
          }
          String codiceCategoria = hRda.getCodiceCategoria();
          String codiceCarrello = hRda.getIdLotto();
          String codiceRda = hRda.getCodiceRda();
          String posizioneRda = hRda.getPosizioneRda();
          Double percentualeIva = hRda.getIva();

          if("1".equals(linkRda)){
            int id = genChiaviManager.getNextId("GARERDA");
            this.sqlManager.update(strSqlInsert, new Object[]{id, codiceGara, null, null,codiceCarrello,codiceRda,posizioneRda,dataConsegna,luogoConsegna,
                codiceArticolo,oggetto,unitaMisura,codiceCategoria,percentualeIva,quantita,prezzo});
          }else{
            Long maxContafEsistente = (Long) sqlManager.getObject(
                "select coalesce(max(contaf),0) from GCAP where ngara = ?", new Object[] { codiceLotto });

            this.sqlManager.update(strSqlInsert, new Object[]{codiceLotto, new Long(maxContafEsistente+1),
                new Long(maxContafEsistente+1),codiceArticolo,quantita,prezzo,new Long(3),"2","2",oggetto,
                null,unitaMisura,codiceCarrello,codiceRda,posizioneRda,codiceCategoria,percentualeIva,dataConsegna,luogoConsegna});
            //descrizione estesa
            this.sqlManager.update(strSqlInsertEst,
                new Object[] { descrizione,codiceLotto, new Long(maxContafEsistente+1)});
            maxContafEsistente++;
          }

        }//for

      }

    }



  } catch (SQLException e) {
    throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
        " della gara  (" + codiceLotto +")", null, e);
  }

  if (logger.isDebugEnabled()) logger.debug("insLavorazioniLotto: fine metodo");

  return 0;
}


  public int insPosizioniRda(String username, String password, String servizio,
      String codiceGara, String codiceLotto, Long idRda, WSERPRdaType[] rdaArray)

  throws GestoreException {

  logger.debug("insPosizioniRda: inizio metodo");
  WSERPConfigurazioneOutType configurazione = this.wserpConfigurazioneLeggi(servizio);
  String tipoWSERP = configurazione.getRemotewserp();

  String strSqlInsert="";
    strSqlInsert="insert into GAREPOSRDA (ID, RDA_ID, NUMRDA, POSRDA, WBE, CDC, CONTOCOGE," +
    		" DESCRIZIONE, CODARTICOLO, QUANTITA, PREZZO, UNIMIS) values (?,?,?,?,?,?,?,?,?,?,?,?)";

    if(rdaArray != null && rdaArray.length > 0){


      for (int h = 0; h < rdaArray.length; h++) {
        WSERPRdaType hRda = rdaArray[h];
        WSERPPosizioneRdaType[] hPosRdaArray = hRda.getPosizioneRdaArray();
        if(hPosRdaArray != null && hPosRdaArray.length > 0){
          for (int p = 0; p < hPosRdaArray.length; p++) {
            WSERPPosizioneRdaType hPosRda = hPosRdaArray[p];
            String codiceCarrello = null;
            String codiceRda = hRda.getCodiceRda();
            String posizioneRda = hPosRda.getPosizioneRiferimento();
            String wbe = hPosRda.getWbe();
            String cdc = hPosRda.getCdc();
            String contocoge = hPosRda.getContoCoGe();
            String descrizione = hPosRda.getDescrizioneEstesa();
            String codiceArticolo = hPosRda.getCodiceArticolo();
            Double quantita = hPosRda.getQuantita();
            Double prezzo = hPosRda.getPrezzoPrevisto();
            String unitaMisura = hPosRda.getUm();

            try {
              Vector<?> datiTest = this.sqlManager.getVector("select numRda from garerda where id =? ",new Object[] { idRda });
            } catch (SQLException sqle1) {
              throw new GestoreException("Errore nel caricamento delle rda " +
                  " della gara  (" + codiceLotto +")", null, sqle1);
            }
            int id = genChiaviManager.getNextId("GAREPOSRDA");
            try {
              this.sqlManager.update(strSqlInsert, new Object[]{id,idRda,codiceRda,posizioneRda,wbe,cdc,contocoge,
                descrizione,codiceArticolo,quantita,prezzo,unitaMisura});
            } catch (SQLException e) {
              throw new GestoreException("Errore nel caricamento delle posizioni rda " +
                  " della gara  (" + codiceLotto +")", null, e);
            }

          }//for
        }//if
      }//for
    }

  if (logger.isDebugEnabled()) logger.debug("insPosizioniRda: fine metodo");

  return 0;
}




  /*CREAZIONE DEI LOTTI DAI PROCEDIMENTI FNM*/

  public String [] insLottiDaProcedimenti(HttpServletRequest request, String username, String password, String servizio,
      String codiceGara, Long tipoAppalto, Long tipgar, WSERPRdaType[] rdaArray, String linkRda, String uffint)
    throws GestoreException {

    logger.debug("insLottiDaProcedimenti: inizio metodo");

    String[] res = new String[2];
    res[0] = "0";
    String resMsg = "";

    try {

      Long iterga = null;
      Long tipgen = null;
      Long modlic = null;
      Long critlic = null;
      Long detlic = null;
      String calcsoan = null;
      String valtec = null;
      Long ultdetlic = null;


      List<?> datiGaraLotti = this.sqlManager.getVector("select" +
      		" t.tipgar, t.iterga, t.tipgen, g.sicinc, t.modlic, t.critlic, t.detlic, t.calcsoan, t.valtec, t.ultdetlic " +
      		" from torn t,gare g where t.codgar = g.codgar1 and t.codgar = ? and g.ngara = ? ",
      		new Object[] { codiceGara,codiceGara });
      if (datiGaraLotti != null && datiGaraLotti.size() > 0) {
        tipgar = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 0).getValue();
        iterga = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 1).getValue();
        tipgen = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 2).getValue();
        String sicinc = (String) SqlManager.getValueFromVectorParam(datiGaraLotti, 3).getValue();
        modlic = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 4).getValue();
        critlic = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 5).getValue();
        detlic = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 6).getValue();
        calcsoan = (String) SqlManager.getValueFromVectorParam(datiGaraLotti, 7).getValue();
        valtec = (String) SqlManager.getValueFromVectorParam(datiGaraLotti, 8).getValue();
        ultdetlic = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 9).getValue();


      }
      //qui si suppone che il codiga sia sempre numerico
      String maxCodigaStr = (String) this.sqlManager.getObject(
          "select max(codiga) from gare where codgar1  = ?", new Object[]{codiceGara});
      Long maxCodiga = null;
      if(maxCodigaStr!=null){
        maxCodiga = new Long(maxCodigaStr);
      }else{
        maxCodiga=new Long(0);
      }


      WSERPConfigurazioneOutType configurazione = this.wserpConfigurazioneLeggi(servizio);
      String tipoWSERP = configurazione.getRemotewserp();

      //valori fissi per tutti i lotti
      String precutStr = tabellatiManager.getDescrTabellato("A1018", "1");
      Long precut = new Long(precutStr);

      String tabellato = PgManager.getTabellatoPercCauzioneProvvisoria(tipoAppalto.intValue());
      String descrPercentuale = tabellatiManager.getDescrTabellato(tabellato, "1");
      Double percentuale = UtilityNumeri.convertiDouble(descrPercentuale,
          UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);


      //ciclo per generazione dei lotti
      if(rdaArray != null && rdaArray.length > 0){
        for (int h = 0; h < rdaArray.length; h++) {
          WSERPRdaType hRda = rdaArray[h];

          String codProcedimento = hRda.getCodiceRda();
          codProcedimento = UtilityStringhe.convertiNullInStringaVuota(codProcedimento);
          String esercizio = hRda.getEsercizio();
          esercizio = UtilityStringhe.convertiNullInStringaVuota(esercizio);
          String codiceLotto = codProcedimento + "-" + esercizio;
          String oggetto = hRda.getOggetto();
          String codiga =  String.valueOf(maxCodiga+h+1); //valorizzato con il contatore del ciclo
          String natura = hRda.getNatura();
          if("L".equals(natura)){
            tipgen = new Long(1);
          }else{
            if("F".equals(natura)){
              tipgen = new Long(2);
            }else{
              if("S".equals(natura)){
                tipgen = new Long(3);
              }
            }
          }

          String codCritlic = hRda.getCriterioAggiudicazione();
          Long modastg = null;

          Long critlicLotto = null;
          if("A".equals(codCritlic) || "C".equals(codCritlic)){
            critlicLotto = new Long(1);
            if("C".equals(codCritlic)){
              modastg = new Long(1);
            }else{
              modastg = new Long(2);
            }
          }
          if("B".equals(codCritlic)){
            critlicLotto = new Long(2);
          }

          calcsoan = "1";
          Long applegreg = null;
          String ribcal = null;
          String sicinc = null;

          modlic = pgManager.getMODLICG(critlic, detlic, calcsoan, applegreg);


          Double impapp = hRda.getValoreStimato();

          Double impsic = hRda.getImportoSicurezza();

          Double impRinnovi = hRda.getImportoRinnovi();
          String ammrin = null;
          if(impRinnovi!=null && impRinnovi > new Double(0)){
            ammrin = "1";
          }
          Double impAltro = hRda.getQuantita();
          String ammopz = null;
          if(impAltro!=null && impAltro > new Double(0) ){
            ammopz = "1";
          }

          Boolean inseribile = true;

          if(new Long(2).equals(critlicLotto) && !new Long(2).equals(critlic)){
            inseribile = false;
            res[0] = "1";
            resMsg = resMsg + "Il procedimento "+codProcedimento+" non viene importato per incongruenza del criterio di aggiudicazione!";
          }


          if(inseribile){
            WSERPRdaResType wserpRdaRes = this.wserpAssociaRdaGara(username, password, servizio, esercizio, codProcedimento, null, codiceLotto, null, true);
            if(!wserpRdaRes.isEsito()){
              throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
                  "wserp.erp.associarda.remote.error", null);
            }else{
              this.sqlManager.update("insert into GARE (ngara, codgar1, codiga, not_gar, precut, tipgarg, modlicg," +
                  " critlicg, detlicg, calcsoang, applegregg, estimp, pgarof, modastg, ribcal, sicinc, onsogrib," +
                  " impapp, impsic)" +
                  " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{codiceLotto, codiceGara, codiga, oggetto, precut, tipgar, modlic ,
                critlic, detlic, calcsoan, applegreg, "1", percentuale, modastg, ribcal, sicinc, "1", impapp, impsic});

              this.sqlManager.update("insert into GARERDA (id,codgar,ngara,numrda,esercizio) values(?,?,?,?,?)",
                  new Object[]{this.genChiaviManager.getNextId("GARERDA"),codiceGara, codiceLotto, codProcedimento, esercizio});

              this.sqlManager.update("insert into GARE1 (ngara,codgar1,valtec,ultdetlic,imprin,impaltro,ammrin,ammopz) values(?,?,?,?,?,?,?,?)",
                  new Object[]{codiceLotto, codiceGara,valtec,ultdetlic, impRinnovi, impAltro, ammrin,ammopz});

              this.sqlManager.update("insert into GARECONT (ngara,ncont) values(?,?)",
                  new Object[]{codiceLotto , new Long(1)});

              //Carico gli allegati
              WSERPRdaType[] rdaArrayConAllegati = new WSERPRdaType[1];
              WSERPRdaType rdaConAllegati = new WSERPRdaType();
              rdaConAllegati.setCodiceRda(codProcedimento);
              rdaConAllegati.setEsercizio(esercizio);
              rdaArrayConAllegati[0] = rdaConAllegati;
              wserpRdaRes  = this.wserpListaFilesRda(username, password, servizio, rdaArrayConAllegati);
              //riuso le stesse strutture
              rdaArrayConAllegati = wserpRdaRes.getRdaArray();
              rdaConAllegati = rdaArrayConAllegati[0];//eventualmente ciclare
              WSERPAllegatoType[] allegatoArray = rdaConAllegati.getAllegatoArray();
              if(allegatoArray != null){
                for(int d = 0 ; d < allegatoArray.length; d++){
                  WSERPAllegatoType allegato = allegatoArray[d];
                  this.gestioneFileAllegati(allegato, codiceGara, null, null, iterga);
                }//for
              }//if

            }//else associazione OK

          }//inseribile

        }//for

        res[1] = resMsg;

      }


    } catch (SQLException e) {
      throw new GestoreException("Errore nella creazione dei lotti  " +
          " della gara  (" + codiceGara +")", null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("LottiDaProcedimenti: fine metodo");


    return res;
  }

  //INS.LOTTI DA RDA (RAIWAY)
  public String [] insLottiDaRda(HttpServletRequest request, String username, String password, String servizio,
	      String codiceGara, Long tipoAppalto, Long tipgar, WSERPRdaType[] rdaArray, String linkRda, String uffint, Long genere)
	    throws GestoreException {

	    logger.debug("insLottiDaProcedimenti: inizio metodo");

	    String[] res = new String[2];
	    res[0] = "0";
	    String resMsg = "";

	    try {

	      Long iterga = null;
	      Long tipgen = null;
	      Long modlic = null;
	      Long critlic = null;
	      Long detlic = null;
	      String calcsoan = null;
	      String valtec = null;
	      Long ultdetlic = null;
	      Long modastg = null;
	      Long ribcal = null;
	      String sicinc = null;
	      List<?> datiGaraLotti = null;

	      if(Long.valueOf(3).equals(genere)) {
		      datiGaraLotti = this.sqlManager.getVector("select" +
			      		" t.tipgar, t.iterga, t.tipgen, g.sicinc, t.modlic, t.critlic, t.detlic, t.calcsoan, t.valtec, t.ultdetlic, g.modastg, g.ribcal " +
			      		" from torn t,gare g where t.codgar = g.codgar1 and t.codgar = ? ",
			      		new Object[] { codiceGara });
	    	  
	      }else {
		      datiGaraLotti = this.sqlManager.getVector("select" +
			      		" t.tipgar, t.iterga, t.tipgen, cast('1' as varchar(2)) as sicinc, t.modlic, t.critlic, t.detlic, t.calcsoan, t.valtec, t.ultdetlic, t.modast, 1 as ribcal" +
			      		" from torn t where t.codgar = ? ",
			      		new Object[] { codiceGara });
	      }

	      if (datiGaraLotti != null && datiGaraLotti.size() > 0) {
	        tipgar = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 0).getValue();
	        iterga = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 1).getValue();
	        tipgen = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 2).getValue();
	        sicinc = (String) SqlManager.getValueFromVectorParam(datiGaraLotti, 3).getValue();
	        modlic = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 4).getValue();
	        critlic = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 5).getValue();
	        detlic = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 6).getValue();
	        calcsoan = (String) SqlManager.getValueFromVectorParam(datiGaraLotti, 7).getValue();
	        valtec = (String) SqlManager.getValueFromVectorParam(datiGaraLotti, 8).getValue();
	        ultdetlic = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 9).getValue();
	        modastg = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 10).getValue();
	        ribcal = (Long) SqlManager.getValueFromVectorParam(datiGaraLotti, 11).getValue();
	      }

	      //valori fissi per tutti i lotti
	      String precutStr = tabellatiManager.getDescrTabellato("A1018", "1");
	      Long precut = new Long(precutStr);

	      String tabellato = PgManager.getTabellatoPercCauzioneProvvisoria(tipgen.intValue());
	      String descrPercentuale = tabellatiManager.getDescrTabellato(tabellato, "1");
	      Double percentuale = UtilityNumeri.convertiDouble(descrPercentuale,
	          UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);

	      //ciclo per generazione dei lotti (si predispone per eventuali rda multiple)
	      if(rdaArray != null && rdaArray.length > 0){
	        for (int h = 0; h < rdaArray.length; h++) {
	          WSERPRdaType hRda = rdaArray[h];
	          
	          WSERPLottoRdaType[] lottiRdaArray = hRda.getLottoArray();
	          if(lottiRdaArray!= null && lottiRdaArray.length > 0) {
	        	  for (int l = 0; l < lottiRdaArray.length; l++) {
	        		  WSERPLottoRdaType lottoRda = lottiRdaArray[l];
	        		  
			          String codiceLotto =  pgManager.getNumeroGaraCodificaAutomatica(codiceGara, null,"GARE","NGARA");
			          String oggetto = lottoRda.getDescrizione();
			          Long idLotto = lottoRda.getIdLotto();
			          String codiga = String.valueOf(idLotto);
			          
			          Double impapp = lottoRda.getImportoLotto();
			          Double impsic = lottoRda.getImportoSicurezza();
			          Double impAltro = lottoRda.getImportoOpzioni();
			          
			          String ammopz = null;
			          if(impAltro!=null && impAltro > new Double(0) ){
			            ammopz = "1";
			          }
			          
			          String codcpv =  lottoRda.getCodCpv();
			          
			          Double importoCauzioneProvvisoria = pgManager.calcolaGAROFF(impapp, percentuale, tipgen);
			          
		              this.sqlManager.update("insert into GARE (ngara, codgar1, codiga, not_gar, precut, tipgarg, modlicg," +
			                  " critlicg, detlicg, calcsoang, applegregg, estimp, pgarof, garoff, modastg, ribcal, sicinc, onsogrib," +
			                  " impapp, impsic)" +
			                  " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
			                new Object[]{codiceLotto, codiceGara, codiga, oggetto, precut, tipgar, modlic ,
			                critlic, detlic, calcsoan, null, "1", percentuale, importoCauzioneProvvisoria, modastg, ribcal, sicinc, "1", impapp, impsic});
		              
		              this.sqlManager.update("insert into GARE1 (ngara,codgar1,valtec,ultdetlic,imprin,impaltro,ammrin,ammopz) values(?,?,?,?,?,?,?,?)",
			                  new Object[]{codiceLotto, codiceGara,valtec,ultdetlic, null, impAltro, null,ammopz});
		              
		              this.sqlManager.update("insert into GARECONT (ngara,ncont) values(?,?)", new Object[]{codiceLotto , Long.valueOf(1)});		              


	        		  //CODCPV
		              this.sqlManager.update("insert into garcpv(ngara,numcpv,codcpv,tipcpv) values (?,?,?,?)", new Object[]{codiceLotto, Long.valueOf(1),codcpv, "1"});
		              
		              
	        	  }//for lotti
	          }//if lotti

	        }//for rda

	        res[1] = resMsg;

	      }


	    } catch (SQLException e) {
	      throw new GestoreException("Errore nella creazione dei lotti  " +
	          " della gara  (" + codiceGara +")", null, e);
	    }

	    if (logger.isDebugEnabled()) logger.debug("LottiDaProcedimenti: fine metodo");


	    return res;
	  }

  
  public String [] insAffidamento (String codiceCig, WSERPLiquidatoType liquidatoType, Long tipoAppalto, Long tipgar, String uffint)
    throws GestoreException {


    if (logger.isDebugEnabled()) logger.debug("insAffidamento: inizio metodo");

    String[] res = new String[2];
    res[0] = "0";
    String resMsg = "";

  //Inserimento in GARE
    this.inserimentoGara(liquidatoType,uffint);

    if (logger.isDebugEnabled()) logger.debug("insAffidamento: fine metodo");

    return res;
  }


  public String[] scollegaRda(String codgar, String ngara, String linkrda, String codiceRda, String posizioneRda, HttpServletRequest request)
    throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("scollegaRda: inizio metodo");
      String[] res = new String[2];
      String servizio = "WSERP";
      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.wserpGetLogin(syscon, servizio);
      String username = credenziali[0];
      String password = credenziali[1];
      WSERPConfigurazioneOutType configurazione = this.wserpConfigurazioneLeggi(servizio);
      String tipoWSERP = configurazione.getRemotewserp();
      tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);
      
      if("ATAC".equals(tipoWSERP) || "RAIWAY".equals(tipoWSERP)){
    	  res[0]="0";
    	  return res;  
      }
      

      try {
    	codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
    	ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
        codiceRda = UtilityStringhe.convertiNullInStringaVuota(codiceRda);
        posizioneRda = UtilityStringhe.convertiNullInStringaVuota(posizioneRda);
        List listaRdaGara = null;
        if("1".equals(linkrda)){
        	
          if("FNM".equals(tipoWSERP)  && !"".equals(codgar) && !"".equals(ngara) && !codgar.substring(1).equals(ngara)){
            if(!"".equals(codiceRda)){
              listaRdaGara = this.sqlManager.getListVector(
                  "select CODGAR,NUMRDA,POSRDA,CODCARR,ESERCIZIO from GARERDA where CODGAR = ? and NGARA = ? and NUMRDA = ?",
                    new Object[] { codgar,ngara,codiceRda });
            }else{
              listaRdaGara = this.sqlManager.getListVector(
                  "select CODGAR,NUMRDA,POSRDA,CODCARR,ESERCIZIO from GARERDA where CODGAR = ? and NGARA = ? and NUMRDA is not null",
                    new Object[] { codgar,ngara });
            }
          }else if("AMIU".equals(tipoWSERP)) {
             if(!"".equals(codiceRda)){
            	  if(!"".equals(codgar) && !"".equals(ngara) && !codgar.substring(1).equals(ngara)) {
                      listaRdaGara = this.sqlManager.getListVector(
                              "select CODGAR,NUMRDA,POSRDA,CODCARR,ESERCIZIO from GARERDA where CODGAR = ? and NGARA = ? and NUMRDA = ?",
                                new Object[] { codgar,ngara,codiceRda });
            	  }else {
                      listaRdaGara = this.sqlManager.getListVector(
                              "select CODGAR,NUMRDA,POSRDA,CODCARR,ESERCIZIO from GARERDA where CODGAR = ? and NUMRDA = ?",
                                new Object[] { codgar,codiceRda });
            		  
            	  }
             }else{
                  listaRdaGara = this.sqlManager.getListVector(
                      "select CODGAR,NUMRDA,POSRDA,CODCARR,ESERCIZIO from GARERDA where CODGAR = ? and NGARA = ? and NUMRDA is not null",
                        new Object[] { codgar,ngara });
             }
          }else {
            if(!"".equals(codiceRda)){
	        	if("CAV".equals(tipoWSERP)) {
	                listaRdaGara = this.sqlManager.getListVector(
	                        "select CODGAR,NUMRDA,POSRDA,CODCARR,ESERCIZIO from GARERDA where CODGAR = ? and NUMRDA = ? ",
	                          new Object[] { codgar,codiceRda });
	        	}else {
	                listaRdaGara = this.sqlManager.getListVector(
	                        "select CODGAR,NUMRDA,POSRDA,CODCARR,ESERCIZIO from GARERDA where CODGAR = ? and NUMRDA = ? and POSRDA = ?",
	                          new Object[] { codgar,codiceRda,posizioneRda });
	        	}
            }else{
              listaRdaGara = this.sqlManager.getListVector(
                  "select CODGAR,NUMRDA,POSRDA,CODCARR,ESERCIZIO from GARERDA where CODGAR = ? and NUMRDA is not null",
                    new Object[] { codgar });
            }
          }

          if (listaRdaGara != null && listaRdaGara.size() > 0) {
            for (int k = 0; k < listaRdaGara.size(); k++) {
              Vector tmp = (Vector) listaRdaGara.get(k);
              codiceRda = ((JdbcParametro) tmp.get(1)).getStringValue();
              posizioneRda = ((JdbcParametro) tmp.get(2)).getStringValue();
              String codiceCarrello = ((JdbcParametro) tmp.get(3)).getStringValue();
              codiceCarrello = UtilityStringhe.convertiNullInStringaVuota(codiceCarrello);
              String esercizio = ((JdbcParametro) tmp.get(4)).getStringValue();
              esercizio = UtilityStringhe.convertiNullInStringaVuota(esercizio);
              WSERPRdaType rdaDatiAggiuntivi = new WSERPRdaType();

              String codiceGara = codgar;
              if("FNM".equals(tipoWSERP) || "AMIU".equals(tipoWSERP)){
                //verifico il genere
                if(!"".equals(ngara)){
                  codiceGara = ngara;
                }else{
                  codiceGara = codgar.substring(1);
                }

                codiceCarrello =  esercizio;
              }else{
                if(codiceGara.indexOf("$")==0){
                  codiceGara = codgar.substring(1);
                }
              }
              
              if("AMIU".equals(tipoWSERP)){
                  String codiceCig = (String) this.sqlManager.getObject(
                          "select codcig from GARE where NGARA = ?", new Object[] { ngara });
                rdaDatiAggiuntivi.setCodiceCig(codiceCig);
                rdaDatiAggiuntivi.setCodiceGara(codgar);
              }

              WSERPRdaResType wserpRdaRes = this.wserpAssociaRdaGara(username, password, servizio, codiceCarrello, codiceRda, posizioneRda, codiceGara, rdaDatiAggiuntivi, false);
              if(wserpRdaRes.isEsito()){
                if("FNM".equals(tipoWSERP)){
                  if(!"".equals(codiceRda)){
                    if( !"".equals(codgar) && !"".equals(ngara) && !codgar.substring(1).equals(ngara)){
                      this.sqlManager.update(
                          "delete from garerda where codgar = ? and ngara is not null and ngara = ? and numrda = ? ",
                          new Object[] { codgar,ngara,codiceRda});
                    }else{
                      this.sqlManager.update(
                          "update garerda set numrda = null, posrda = null where codgar = ? and numrda = ? ",
                          new Object[] { codgar,codiceRda});
                    }

                  }else{
                    this.sqlManager.update(
                        "update garerda set numrda = null, posrda = null where codgar = ? ",
                        new Object[] { codgar });
                  }
                }else{
                  if(!"".equals(codiceRda)){
                    this.sqlManager.update(
                        "update garerda set numrda = null, posrda = null where codgar = ? and numrda = ? and posrda = ? ",
                        new Object[] { codgar,codiceRda,posizioneRda });
                  }else{
                    this.sqlManager.update(
                        "update garerda set numrda = null, posrda = null where codgar = ? ",
                        new Object[] { codgar });
                  }
                }
              }else{
            	res[0]="-1";
            	if("AMIU".equals(tipoWSERP)) {
            		String msg = wserpRdaRes.getMessaggio();
            		if(msg!=null) {
            			res[1] = msg;
            		}
            	}
                return res;
              }
            }//for
          }

        }else{

          String dittaAggiudicataria = (String) this.sqlManager.getObject("select ditta from gare where NGARA = ? ", new Object[] { ngara });
          dittaAggiudicataria = UtilityStringhe.convertiNullInStringaVuota(dittaAggiudicataria);
          if("".equals(dittaAggiudicataria)){
            if(!"".equals(codiceRda)){
              //devo selezionare tutte le rda o il carrello  presenti nelle lavorazioni della gara
              listaRdaGara = this.sqlManager.getListVector(
                  "select NGARA,CONTAF,CODCARR,CODRDA,POSRDA from GCAP where NGARA = ? and CODRDA = ? and POSRDA = ?",
                    new Object[] { ngara,codiceRda,posizioneRda });
            }else{
              //devo selezionare tutte le rda o il carrello  presenti nelle lavorazioni della gara
              listaRdaGara = this.sqlManager.getListVector(
                  "select NGARA,CONTAF,CODCARR,CODRDA,POSRDA from GCAP where NGARA = ? and CODRDA is not null", new Object[] { ngara });
            }
            if (listaRdaGara != null && listaRdaGara.size() > 0) {
              String codCarrelloScollegato = null;
              for (int k = 0; k < listaRdaGara.size(); k++) {
                Vector tmp = (Vector) listaRdaGara.get(k);
                Long rigaGcap = (Long) ((JdbcParametro) tmp.get(1)).getValue();
                String codiceCarrello = ((JdbcParametro) tmp.get(2)).getStringValue();
                codiceRda = ((JdbcParametro) tmp.get(3)).getStringValue();
                posizioneRda = ((JdbcParametro) tmp.get(4)).getStringValue();
                codiceCarrello = UtilityStringhe.convertiNullInStringaVuota(codiceCarrello);
                if("SMEUP".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
                  if(!"".equals(codiceCarrello) && !codiceCarrello.equals(codCarrelloScollegato)){
                      //effettuo una verifica della presenza della rda prima di scollegarla
                      //devo fare la chiamata filtrata (se e' aggiudicata non vedo piu' la rda)

                      //WSERPRdaType erpSearch = new WSERPRdaType();
                      //erpSearch.setCodiceCarrello(codiceCarrello);
                      //WSERPRdaResType wserpRdaRes = this.wserpListaRda(username, password, servizio, erpSearch);
                      //if(wserpRdaRes.isEsito() && wserpRdaRes.getRdaArray()!=null ){
                        WSERPRdaResType wserpRdaRes = this.wserpAssociaRdaGara(username, password, servizio, codiceCarrello, codiceRda, posizioneRda, ngara, null, false);
                        codCarrelloScollegato = codiceCarrello;
                        if(wserpRdaRes.isEsito()){
                          this.sqlManager.update(
                              "update gcap set codcarr = null, codrda = null, posrda = null where ngara = ? and codcarr = ? ",
                              new Object[] { ngara,codiceCarrello });
                        }else{
                        	res[0]="-1";
        					return res;
                        }
                      //}
                  }
                }else{
                  WSERPRdaResType wserpRdaRes = this.wserpAssociaRdaGara(username, password, servizio, codiceCarrello, codiceRda, posizioneRda, ngara, null, false);
                  if(wserpRdaRes.isEsito()){
                    this.sqlManager.update(
                        "update gcap set codcarr = null, codrda = null, posrda = null where ngara = ? and contaf = ? ",
                        new Object[] { ngara,rigaGcap });
                  }else{
                	res[0]="-1";
					return res;
				  }
                }
              }
            }

          }

        }
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante l'operazione di dis-associazione delle rda",null,  e);
      }


    if (logger.isDebugEnabled()) logger.debug("scollegaRda: fine metodo");

	res[0]="0";
	return res;

  }

  public int updDatiL190Cig(String tipoWSERP, String codcig, Double impLiquidato, Date dataInizio, Date dataUltimazione, String ngara, Long ncont, Double impLiqOrig, Date dataIniOrig, Date dataUltOrig)
    throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("updDatiL190Cig: inizio metodo");
      String updateDatiL190 = "";

      try {

        if(ngara != null && ncont!= null){
          //valori originari
          String dataIniOstr = "nulla";
          if(dataIniOrig != null){
            dataIniOstr = dataIniOrig.toString();
          }
          String dataOstr = "nulla";
          if(dataUltOrig != null){
            dataOstr = dataUltOrig.toString();
          }
          String impliqOstr = "nullo";
          if(impLiqOrig != null){
            impliqOstr = impLiqOrig.toString();
          }
          //valori finali
          String impLstr = "nullo";
          if(impLiquidato != null){
            impLstr = impLiquidato.toString();
          }
          String dataInistr = "nulla";
          if(dataInizio != null){
            dataInistr = dataInizio.toString();
          }
          String dataUstr = "nulla";
          if(dataUltimazione != null){
            dataUstr = dataUltimazione.toString();
          }


          if("AVM".equals(tipoWSERP)){
            updateDatiL190 = "update garecont set impliq = ? , dcertu = ?" +
            " where ngara = ? and ncont = ?";
            this.sqlManager.update(updateDatiL190, new Object[] {impLiquidato, dataUltimazione, ngara, ncont });
            if (logger.isInfoEnabled()) {
              logger.info("Aggiornamento CIG: " + codcig + "\r\n" +
                    " valori iniziali: " + "importo liquidato: " + impliqOstr + " data ultimazione: " + dataOstr + "\r\n" +
                    " valori finali: " + "importo liquidato: " + impLstr + " data ultimazione: " + dataUstr);
            }

          }
          if("TPER".equals(tipoWSERP) || "ANTHEA".equals(tipoWSERP) || "ATAC".equals(tipoWSERP)){
            updateDatiL190 = "update garecont set impliq = ? , dverbc = ? , dcertu = ?" +
            " where ngara = ? and ncont = ?";
            this.sqlManager.update(updateDatiL190, new Object[] {impLiquidato, dataInizio, dataUltimazione, ngara, ncont });
            if (logger.isInfoEnabled()) {
              logger.info("Aggiornamento CIG: " + codcig + "\r\n" +
                    " valori iniziali: " + "importo liquidato: " + impliqOstr + " data inizio: " + dataIniOstr + " data ultimazione: " + dataOstr + "\r\n" +
                    " valori finali: " + "importo liquidato: " + impLstr + " data inizio: " + dataInistr + " data ultimazione: " + dataUstr);
            }

          }
          if("UGOV".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP) || "SMEUP".equals(tipoWSERP) || "ENEA".equals(tipoWSERP)){
            updateDatiL190 = "update garecont set impliq = ?" +
            " where ngara = ? and ncont = ?";
            this.sqlManager.update(updateDatiL190, new Object[] {impLiquidato, ngara, ncont });
            if (logger.isInfoEnabled()) {
              logger.info("Aggiornamento CIG: " + codcig + "\r\n" +
                    " valori iniziali: " + "importo liquidato: " + impliqOstr + "\r\n" +
                    " valori finali: " + "importo liquidato: " + impLstr);
            }

          }


        }

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dei Dati L190 " +
            " del cig  (" + codcig +")", null, e);
      }

    if (logger.isDebugEnabled()) logger.debug("updDatiL190Cig: fine metodo");

    return 0;

  }

  public int updNumeroRdo(String tipoWSERP, String codgar, String ngara, String numRdo)
		  throws GestoreException {

  if (logger.isDebugEnabled()) logger.debug("updNumeroRdo: inizio metodo");

    try {

      if(ngara != null){
        if("FNM".equals(tipoWSERP) || "RAIWAY".equals(tipoWSERP)){
          String updateNumRdo = "update gare1 set numrdo = ?  where codgar1 = ? and ngara = ? ";
          this.sqlManager.update(updateNumRdo, new Object[] {numRdo, codgar, ngara });
          if (logger.isInfoEnabled()) {
            //logger.info("");
          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento del numero Rdo ", null, e);
    }

  if (logger.isDebugEnabled()) logger.debug("updNumeroRdo: fine metodo");

  return 0;

}
  
  public int updComunicazione(String idprg,Long idcom, String stato)
		  throws GestoreException {

  if (logger.isDebugEnabled()) logger.debug("updComunicazione: inizio metodo");
  
  String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom=?";

    try {

    	this.sqlManager.update(updateW_INVCOM, new Object[] { stato, idprg, idcom});

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento della comunicazione ", null, e);
    }

  if (logger.isDebugEnabled()) logger.debug("updComunicazione: fine metodo");

  return 0;

}
  
  public int updFornitore(String tipoWSERP, String codimp,String cgenimp,String dofimp)
		  throws GestoreException {

  if (logger.isDebugEnabled()) logger.debug("updFornitore: inizio metodo");
  
  String updateIMPR = null;
  try {
    	
    	  if("RAI".equals(tipoWSERP)){
    		  updateIMPR = "update impr set cgenimp = ?, dofimp = ?  where codimp = ?";
    		  this.sqlManager.update(updateIMPR, new Object[] { cgenimp,dofimp,codimp});
    	  }
    	  
    	  if("RAIWAY".equals(tipoWSERP)){
    		  updateIMPR = "update impr set cgenimp = ?  where codimp = ? and cgenimp is null";
    	      this.sqlManager.update(updateIMPR, new Object[] { cgenimp,codimp});
    	  }

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento del fornitore ", null, e);
    }

  if (logger.isDebugEnabled()) logger.debug("updFornitore: fine metodo");

  return 0;

}
  
  public int updLogEventi(LogEvento logEvento)
		  throws GestoreException {

	  if (logger.isDebugEnabled()) logger.debug("updLogEventi: inizio metodo");

        LogEventiUtils.insertLogEventi(logEvento);
  
      if (logger.isDebugEnabled()) logger.debug("updLogEventi: fine metodo");

  return 0;

 }


  public int insMsgDatiL190(String msg)
    throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("insMsgDatiL190: inizio metodo");

      try {
        String insertW_MESSAGE_IN = "insert into w_message_in (message_id, message_date, message_subject, message_body, message_sender_syscon, message_recipient_syscon, message_recipient_read) values (?,?,?,?,?,?,?)";
        Long maxMessageIdIn = (Long) this.sqlManager.getObject("select max(message_id) from w_message_in", new Object[] {});
        if (maxMessageIdIn == null) maxMessageIdIn = new Long(0);
        maxMessageIdIn = new Long(maxMessageIdIn.longValue() + 1);
        this.sqlManager.update(insertW_MESSAGE_IN, new Object[] { maxMessageIdIn, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
                msg, null, 50, 50, new Long(0) });

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento del resoconto dell'acquisizione dei Dati L190 ", null, e);
      }

    if (logger.isDebugEnabled()) logger.debug("insMsgDatiL190: fine metodo");

    return 0;

  }


  public String[] verificaIntegrazioneArticoli(String username, String password, String servizio, String ngara)
    throws GestoreException {

  if (logger.isDebugEnabled()) logger.debug("verificaIntegrazioneArticoli: inizio metodo");

    String controlloSuperato = "SI";
    String msg =  "<br>" + "Tutte le lavorazioni risultano collegate ad articoli dell'ERP";
    String artMsg ="";
    int noPresCount = 0;
    Long statoWS = null;


    try {
      //verificare che non sia nullo
      String selectArticoli = "select codvoc from gcap where ngara = ? and codvoc is not null";
      List<?> articoli = this.sqlManager.getListVector(selectArticoli, new Object[]{ngara});
      if(articoli.size() > 0){
        for (int i = 0; i < articoli.size(); i++) {
          String codiceArticolo = (String) SqlManager.getValueFromVectorParam(articoli.get(i), 0).getValue();
          WSERP_PortType wserp = this.getWSERP(username, password, servizio);
          WSERPArticoloResType wserpArticoloRes = wserp.WSERPDettaglioArticolo(username, password, codiceArticolo);
          statoWS = wserpArticoloRes.getStato();

          if(statoWS != null && statoWS.intValue() <0 ){
            controlloSuperato = "ERR";
            msg =  "<br>" + "Si e' verificato un errore durante la lettura degli articoli dell'ERP";
            break;
          }

          if(!wserpArticoloRes.isEsito()){
            noPresCount = noPresCount + 1;
            if(noPresCount == 1){
              artMsg +=  codiceArticolo;
            }else{
              artMsg +=  "," + codiceArticolo;
            }
          }
        }//for

        if((statoWS != null && statoWS.intValue()>=0 ) && noPresCount > 0){
          controlloSuperato = "NO";
          msg =  "<br>" + "Alcune lavorazioni non risultano collegate ad articoli dell'ERP:";
          msg += artMsg;
        }
      }

    } catch (Throwable t) {

      throw new GestoreException("Si e' verificato un errore durante la lettura dell'articolo: " + t.getMessage(),
          "wserp.erp.dettaglioarticolo.remote.error", t);
    }

    if (logger.isDebugEnabled()) logger.debug("verificaIntegrazioneArticoli: fine metodo");

    return new String[]{controlloSuperato,msg};

  }

  public String verificaPreliminareRda (String ngara, String tipoWSERP)
    throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("verificaPreliminareRda: inizio metodo");

  String msg =  "<br>" + "Tutte le rda vanno controllate per la data di consegna";


  String selRdaGara = null;

  Long countRda = new Long(0);
  String linkrda = "";

  try {
    Vector<?> datiGara = sqlManager.getVector("select codgar,genere from v_gare_genere where codice = ? ", new Object[] { ngara });
    if (datiGara != null && datiGara.size() > 0) {
      String codgar = (String) ((JdbcParametro) datiGara.get(0)).getValue();
      Long genere = (Long) ((JdbcParametro) datiGara.get(1)).getValue();

      List<Vector> rdaGara = null;

      if(new Long(1).equals(genere) || new Long(3).equals(genere)){
        if("AVM".equals(tipoWSERP)){
          countRda = (Long) sqlManager.getObject(
              "select count(*) from garerda where codgar = ?",new Object[] { codgar });
        }
        if(new Long(0).equals(countRda)){
          linkrda = "2";
          selRdaGara = "select codrda,posrda,codcarr,codvoc from gcap" +
          " where ngara in (select ngara from gare where codgar1 = ?) order by codcarr,codrda,posrda";
        }else{
          linkrda = "1";
          selRdaGara = "select numrda,posrda from garerda" +
          " where codgar = ? order by numrda,posrda";
        }
        rdaGara = sqlManager.getListVector(selRdaGara, new Object[] { codgar });
      }else{
        if("AVM".equals(tipoWSERP)){
          countRda = (Long) sqlManager.getObject(
              "select count(*) from garerda where codgar in (select codgar1 from gare where ngara = ?)",new Object[] { ngara });
        }
        if(new Long(0).equals(countRda)){
          linkrda = "2";
          selRdaGara = "select codrda,posrda,codcarr,codvoc from gcap where ngara = ? order by codcarr,codrda,posrda";
        }else{
          linkrda = "1";
          selRdaGara = "select numrda,posrda from garerda" +
          " where codgar in (select codgar1 from gare where ngara = ?) order by numrda,posrda";
        }
        rdaGara = sqlManager.getListVector(selRdaGara, new Object[] { ngara });
      }


      WSERPRdaType[] rdaArray= new WSERPRdaType[rdaGara.size()];
      if(rdaGara.size() > 0){
        for (int i = 0; i < rdaGara.size(); i++) {
          String codRda = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 0).getValue();
          String posRda = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 1).getValue();

        }

      }


    }



  } catch (SQLException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }





  if (logger.isDebugEnabled()) logger.debug("verificaPreliminareRda: fine metodo");

  return msg;

}
  /**
   * Viene effettuato il controllo della presenza di una rda
   * @throws GestoreException
   */

  public Long verificaPresenzaRda (String numeroRda, String tabella)
    throws GestoreException {

    Long countRda = null;

    if (logger.isDebugEnabled()) logger.debug("verificaPresenzaRda: inizio metodo");

    try {
      countRda = (Long) sqlManager.getObject(
          "select count(*) from "+ tabella +" where numrda = ?",new Object[] { numeroRda });

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'operazione di verifica della presenza di una rda",null,  e);
    }


    if (logger.isDebugEnabled()) logger.debug("verificaPresenzaRda: fine metodo");

    return countRda;

}


  /**
   * Viene effettuato il controllo dei dati obbligatori
   * @return HashMap esito true controlli superati false altrimenti msg messaggi
   *         sui controlli non superati
   *
   *
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String, Object> controlloDatiObbligatori(String ngara, String tipoWSERP) throws SQLException, GestoreException {
    Boolean controlliSuperati = new Boolean(true);
    String msg = "";
    boolean msgLotto = false;
    boolean isRaggruppamentoImprese = false;
    Vector<?> datiFornGara = null;
    String queryFornitore = null;
    HashMap<String, Object> valoriRitorno = new HashMap<String, Object>();
    Long tipimp = (Long) this.sqlManager.getObject("select tipimp from impr,gare where ditta = codimp and ngara = ?",new Object[]{ngara});
    //se si tratta di raggruppamento imprese
    if(tipimp!= null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))){
      isRaggruppamentoImprese = true;
    }

    if("TPER".equals(tipoWSERP)){
      if(isRaggruppamentoImprese){
        queryFornitore = "select codcig," + //0
        "cfimp," + //1
        "pivimp," + //2
        "nomest," + //3
        "indimp," + //4
        "nciimp," + //5
        "capimp," + //6
        "locimp," + //7
        "proimp," + //8
        "iscrcciaa," + //9
        "iaggiu," + //10
        "nazimp" + //11
        " from impr,ragimp,gare" +
        " where coddic = codimp and codime9 = ditta and impman = '1' and ngara = ? ";
      }else{
        queryFornitore = "select codcig," + //0
        "cfimp," + //1
        "pivimp," + //2
        "nomest," + //3
        "indimp," + //4
        "nciimp," + //5
        "capimp," + //6
        "locimp," + //7
        "proimp," + //8
        "iscrcciaa," + //9
        "iaggiu," + //10
        "nazimp" + //11
        " from impr,gare where ditta = codimp and ngara = ? ";
      }

      datiFornGara = this.sqlManager.getVector(queryFornitore,new Object[]{ngara});

      if(datiFornGara!= null && datiFornGara.size()>0){

        String codCig = (String) SqlManager.getValueFromVectorParam(datiFornGara, 0).getValue();
        codCig = UtilityStringhe.convertiNullInStringaVuota(codCig);
        // Codice CIG
        if ("".equals(codCig)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il codice CIG della gara.\n";
        }

        String cf = (String) SqlManager.getValueFromVectorParam(datiFornGara, 1).getValue();
        cf = UtilityStringhe.convertiNullInStringaVuota(cf);
        // Codice fiscale
        if ("".equals(cf)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il codice fiscale dell'aggiudicatario.\n";
        }

        String piva = (String) SqlManager.getValueFromVectorParam(datiFornGara, 2).getValue();
        piva = UtilityStringhe.convertiNullInStringaVuota(piva);
        // Codice fiscale
        if ("".equals(piva)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la partita iva dell'aggiudicatario.\n";
        }

        String ragSoc = (String) SqlManager.getValueFromVectorParam(datiFornGara, 3).getValue();
        ragSoc = UtilityStringhe.convertiNullInStringaVuota(ragSoc);
        // Ragione sociale
        if ("".equals(ragSoc)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la ragione sociale dell'aggiudicatario.\n";
        }

        String indirizzo = (String) SqlManager.getValueFromVectorParam(datiFornGara, 4).getValue();
        indirizzo = UtilityStringhe.convertiNullInStringaVuota(indirizzo);
        // Indirizzo
        if ("".equals(indirizzo)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato l'indirizzo dell'aggiudicatario.\n";
        }

        String nCivico = (String) SqlManager.getValueFromVectorParam(datiFornGara, 5).getValue();
        nCivico = UtilityStringhe.convertiNullInStringaVuota(nCivico);
        // Civico
        if ("".equals(nCivico)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il numero civico dell'aggiudicatario.\n";
        }


        String cap = (String) SqlManager.getValueFromVectorParam(datiFornGara, 6).getValue();
        cap = UtilityStringhe.convertiNullInStringaVuota(cap);
        // CAP
        if ("".equals(cap)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il C.A.P. dell'aggiudicatario.\n";
        }

        String locimp = (String) SqlManager.getValueFromVectorParam(datiFornGara, 7).getValue();
        locimp = UtilityStringhe.convertiNullInStringaVuota(locimp);
        // Località
        if ("".equals(locimp)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato la localita' dell'aggiudicatario.\n";
        }

        String proimp = (String) SqlManager.getValueFromVectorParam(datiFornGara, 8).getValue();
        proimp = UtilityStringhe.convertiNullInStringaVuota(proimp);
        // Provincia
        if ("".equals(proimp)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la provincia dell'aggiudicatario.\n";
        }

        Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiFornGara, 11).getValue();
        if(nazimp==null){
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la nazione dell'aggiudicatario.\n";
        }

        String iscrcciaa = (String) SqlManager.getValueFromVectorParam(datiFornGara, 9).getValue();
        iscrcciaa = UtilityStringhe.convertiNullInStringaVuota(iscrcciaa);
        // iscr. CCIAA
        if ("".equals(iscrcciaa)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata l'iscrizione alla C.C.I.A.A.\n";
        }

        Double impAggiudicazione = null;
        Object impAgg = SqlManager.getValueFromVectorParam(datiFornGara, 10).getValue();
        if (impAgg != null) {
          if (impAgg instanceof Long) {
            impAggiudicazione = new Double(((Long) impAgg));
          } else if (impAgg instanceof Double) {
            impAggiudicazione = new Double((Double) impAgg);
          }
        }
        if (impAggiudicazione == null ) {
          controlliSuperati = new Boolean(false);
          msg += "Non risulta valorizzato l'importo di aggiudicazione.\n";
        }
        if (new Double(0).equals(impAggiudicazione) ) {
          controlliSuperati = new Boolean(false);
          msg += "L'importo di aggiudicazione risulta nullo.\n";
        }

      }

    }//IF TPER

    if("AVM".equals(tipoWSERP)){
      if(isRaggruppamentoImprese){
        queryFornitore = "select codcig," + //0
            "cfimp," + //1
            "pivimp," + //2
            "nomest," + //3
            "indimp," + //4
            "nciimp," + //5
            "capimp," + //6
            "locimp," + //7
            "proimp," + //8
            "nazimp" + //9
            " from impr,ragimp,gare" +
            " where coddic = codimp and codime9 = ditta and impman = '1' and ngara = ? ";

      }else{
        queryFornitore = "select codcig," + //0
            "cfimp," + //1
            "pivimp," + //2
            "nomest," + //3
            "indimp," + //4
            "nciimp," + //5
            "capimp," + //6
            "locimp," + //7
            "proimp," + //8
            "nazimp" + //9
            " from impr,gare where ditta = codimp and ngara = ? ";
      }

      datiFornGara = this.sqlManager.getVector(queryFornitore,new Object[]{ngara});

      if(datiFornGara!= null && datiFornGara.size()>0){

        Boolean isStraniera = false;
        Long naz = (Long) SqlManager.getValueFromVectorParam(datiFornGara, 9).getValue();
        if(naz!= null && !new Long(1).equals(naz)){
          isStraniera = true;
        }

        String codCig = (String) SqlManager.getValueFromVectorParam(datiFornGara, 0).getValue();
        codCig = UtilityStringhe.convertiNullInStringaVuota(codCig);
        // Codice CIG
        if ("".equals(codCig)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il codice CIG della gara.\n";
        }

        String cf = (String) SqlManager.getValueFromVectorParam(datiFornGara, 1).getValue();
        cf = UtilityStringhe.convertiNullInStringaVuota(cf);
        // Codice fiscale
        if ("".equals(cf)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il codice fiscale dell'aggiudicatario.\n";
        }

        String piva = (String) SqlManager.getValueFromVectorParam(datiFornGara, 2).getValue();
        piva = UtilityStringhe.convertiNullInStringaVuota(piva);
        // Codice fiscale
        if ("".equals(piva)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la partita iva dell'aggiudicatario.\n";
        }

        String ragSoc = (String) SqlManager.getValueFromVectorParam(datiFornGara, 3).getValue();
        ragSoc = UtilityStringhe.convertiNullInStringaVuota(ragSoc);
        // Ragione sociale
        if ("".equals(ragSoc)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la ragione sociale dell'aggiudicatario.\n";
        }

        String indirizzo = (String) SqlManager.getValueFromVectorParam(datiFornGara, 4).getValue();
        indirizzo = UtilityStringhe.convertiNullInStringaVuota(indirizzo);
        // Indirizzo
        if ("".equals(indirizzo)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato l'indirizzo dell'aggiudicatario.\n";
        }

        String nCivico = (String) SqlManager.getValueFromVectorParam(datiFornGara, 5).getValue();
        nCivico = UtilityStringhe.convertiNullInStringaVuota(nCivico);
        // Civico
        if ("".equals(nCivico)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il numero civico dell'aggiudicatario.\n";
        }


        String cap = (String) SqlManager.getValueFromVectorParam(datiFornGara, 6).getValue();
        cap = UtilityStringhe.convertiNullInStringaVuota(cap);
        // CAP
        if ("".equals(cap)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il C.A.P. dell'aggiudicatario.\n";
        }

        String locimp = (String) SqlManager.getValueFromVectorParam(datiFornGara, 7).getValue();
        locimp = UtilityStringhe.convertiNullInStringaVuota(locimp);
        // Località
        if ("".equals(locimp)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato la localita' dell'aggiudicatario.\n";
        }

        String proimp = (String) SqlManager.getValueFromVectorParam(datiFornGara, 8).getValue();
        proimp = UtilityStringhe.convertiNullInStringaVuota(proimp);
        // Provincia
        if (!isStraniera && "".equals(proimp)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la provincia dell'aggiudicatario.\n";
        }

      }
    }//IF AVM

    if("CAV".equals(tipoWSERP)){
      String queryDatiObbligatori = "select codcig,daatto,nrepat from gare where ngara = ? ";
      Vector<?> datiObbligatori = this.sqlManager.getVector(queryDatiObbligatori,new Object[]{ngara});
      if(datiObbligatori!= null && datiObbligatori.size()>0){
        String codCig = (String) SqlManager.getValueFromVectorParam(datiObbligatori, 0).getValue();
        codCig = UtilityStringhe.convertiNullInStringaVuota(codCig);
        Date daatto = (Date) SqlManager.getValueFromVectorParam(datiObbligatori, 1).getValue();
        String nrepat = (String) SqlManager.getValueFromVectorParam(datiObbligatori, 2).getValue();
        // Codice CIG
        if ("".equals(codCig)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il codice CIG della gara.\n";
        }
      //effettuo anche controlli su correttezza di alcuni formati dei dati
        nrepat = StringUtils.stripToEmpty(nrepat);
        if(!"".equals(nrepat)) {
      	  int l = nrepat.length();
      	  if(l > 7) {
                controlliSuperati = new Boolean(false);
                msg += "Il numero del contratto eccede i 7 caratteri.\n";
      	  }
        }

        if("".equals(nrepat) && daatto == null){
            controlliSuperati = new Boolean(false);
            msg += "Numero e data del contratto non risultano valorizzati.\n";
        }

        if("".equals(nrepat) && daatto != null){
            controlliSuperati = new Boolean(false);
            msg += "Il numero contratto non risulta valorizzato.\n";
        }
        if(!"".equals(nrepat) && daatto == null){
            controlliSuperati = new Boolean(false);
            msg += "La data del contratto non risulta valorizzata.\n";
        }
        
      }
      //effettuo anche controlli su correttezza di alcuni formati dei dati
      String ditta = (String) this.sqlManager.getObject("select ditta from gare where ngara = ?",new Object[]{ngara});
      ditta = StringUtils.stripToEmpty(ditta);
      Vector<?> datiGarecont  = this.sqlManager.getVector(
              "select gc.nprotcoorba,gc.dprotcoorba from GARECONT gc,GARE ga"
              + " where gc.codimp = ga.ditta and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))"
              + " and ga.ditta is not null and ga.ngara= ? and ga.ditta = ?", new Object[] { ngara, ditta });
      
      if(datiGarecont!=null && datiGarecont.size()>0){
          String nprotcoorba = (String) SqlManager.getValueFromVectorParam(datiGarecont, 0).getValue();
          nprotcoorba = StringUtils.stripToEmpty(nprotcoorba);
          if(!"".equals(nprotcoorba)) {
        	  int l = nprotcoorba.length();
        	  if(l > 7) {
                  controlliSuperati = new Boolean(false);
                  msg += "Il numero protocollo del conto corrente dedicato eccede i 7 caratteri.\n";
        	  }
          }

          Date dprotcoorba = (Date) SqlManager.getValueFromVectorParam(datiGarecont, 1).getValue();
          if("".equals(nprotcoorba) && dprotcoorba == null){
              controlliSuperati = new Boolean(false);
              msg += "Gli estremi del protocollo del conto corrente dedicato non risultano valorizzati.\n";
          }

          if("".equals(nprotcoorba) && dprotcoorba != null){
              controlliSuperati = new Boolean(false);
              msg += "Il numero protocollo del conto corrente dedicato non risulta valorizzato.\n";
          }
          if(!"".equals(nprotcoorba) && dprotcoorba == null){
              controlliSuperati = new Boolean(false);
              msg += "La data protocollo del conto corrente dedicato non risulta valorizzata.\n";
          }
      }

    }
    
    if("ATAC".equals(tipoWSERP)){
        queryFornitore = "select codcig," + //0
        "cfimp," + //1
        "pivimp," + //2
        "nomest," + //3
        "indimp," + //4
        "nciimp," + //5
        "capimp," + //6
        "locimp," + //7
        "proimp," + //8
        "iscrcciaa," + //9
        "iaggiu," + //10
        "nazimp" + //11
        " from impr,gare where ditta = codimp and ngara = ? ";
      

      datiFornGara = this.sqlManager.getVector(queryFornitore,new Object[]{ngara});

      if(datiFornGara!= null && datiFornGara.size()>0){

        String codCig = (String) SqlManager.getValueFromVectorParam(datiFornGara, 0).getValue();
        codCig = UtilityStringhe.convertiNullInStringaVuota(codCig);
        // Codice CIG
        if ("".equals(codCig)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il codice CIG della gara.\n";
        }

        String cf = (String) SqlManager.getValueFromVectorParam(datiFornGara, 1).getValue();
        cf = UtilityStringhe.convertiNullInStringaVuota(cf);
        // Codice fiscale
        if ("".equals(cf)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzato il codice fiscale dell'aggiudicatario.\n";
        }

        String piva = (String) SqlManager.getValueFromVectorParam(datiFornGara, 2).getValue();
        piva = UtilityStringhe.convertiNullInStringaVuota(piva);
        // Codice fiscale
        if ("".equals(piva)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la partita iva dell'aggiudicatario.\n";
        }

        String ragSoc = (String) SqlManager.getValueFromVectorParam(datiFornGara, 3).getValue();
        ragSoc = UtilityStringhe.convertiNullInStringaVuota(ragSoc);
        // Ragione sociale
        if ("".equals(ragSoc)) {
          controlliSuperati = new Boolean(false);
          msg += "Non è valorizzata la ragione sociale dell'aggiudicatario.\n";
        }

        Double impAggiudicazione = null;
        Object impAgg = SqlManager.getValueFromVectorParam(datiFornGara, 10).getValue();
        if (impAgg != null) {
          if (impAgg instanceof Long) {
            impAggiudicazione = new Double(((Long) impAgg));
          } else if (impAgg instanceof Double) {
            impAggiudicazione = new Double((Double) impAgg);
          }
        }
        if (impAggiudicazione == null ) {
          controlliSuperati = new Boolean(false);
          msg += "Non risulta valorizzato l'importo di aggiudicazione.\n";
        }
        if (new Double(0).equals(impAggiudicazione) ) {
          controlliSuperati = new Boolean(false);
          msg += "L'importo di aggiudicazione risulta nullo.\n";
        }

      }

    }//IF ATAC

    valoriRitorno.put("esito", controlliSuperati);
    valoriRitorno.put("msg", msg);

    return valoriRitorno;
  }

  public int gestioneFileAllegati(WSERPAllegatoType allegato, String codiceGara, String codiceLotto, String uffint, Long iterga)
    throws GestoreException {

      logger.debug("gestioneFileAllegati: inizio metodo");

      byte[] fileContent = allegato.getContenuto();
      String nomeFile = allegato.getNome();
      //Occorre mettere l'estensione nel nome
      String tipo = allegato.getTipo();
      nomeFile = nomeFile + "." + tipo;
      String titoloFile = allegato.getTitolo();
      Long gruppo = new Long(1);
      Long tipologia = new Long(1);

      if(iterga != null && !(new Long(1).equals(iterga) || new Long(2).equals(iterga) || new Long(4).equals(iterga))){
        gruppo = new Long(6);
        tipologia = new Long(6);
      }
      if(iterga != null && new Long(4).equals(iterga)){
        gruppo = new Long(1);
        tipologia = new Long(2);
      }

      // Inserimento in W_DOCDIG
      if (fileContent != null) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          baos.write(fileContent);
        } catch (IOException e) {
          logger.error("Errore durante la lettura del file " + nomeFile, e);
          throw new GestoreException("Errore durante la lettura del file " + nomeFile , null, e);
        } catch (Exception e) {
          logger.error("Errore durante la lettura del file " + nomeFile, e);
          throw new GestoreException("Errore durante la lettura del file " + nomeFile , null, e);
        }
        Long newIDDOCDIG = null;
        // Si deve calcolare il valore di IDDOCDIG
        Long maxIDDOCDIG;
        try {
          maxIDDOCDIG = (Long) this.sqlManager.getObject("select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?", new Object[] { "PG" });

          if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0){
            newIDDOCDIG = maxIDDOCDIG.longValue() + 1;
          }

          Vector<DataColumn> elencoCampiW_DOCDIG = new Vector<DataColumn>();
          elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
          elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIDDOCDIG)));
          elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "DOCUMGARA")));
          elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeFile)));
          elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)));

          DataColumnContainer containerW_DOCDIG = new DataColumnContainer(elencoCampiW_DOCDIG);

          containerW_DOCDIG.insert("W_DOCDIG", sqlManager);

          String selectDOCUMGARA_MAX = "select max(NORDDOCG) from DOCUMGARA where CODGAR=?";
          Long nProgressivoDOCUMGARA = (Long) sqlManager.getObject(selectDOCUMGARA_MAX, new Object[] { codiceGara });
          if (nProgressivoDOCUMGARA == null) nProgressivoDOCUMGARA = new Long(0);
          nProgressivoDOCUMGARA = new Long(nProgressivoDOCUMGARA.longValue() + 1);

          Vector<DataColumn> elencoCampiDOCUMGARA = new Vector<DataColumn>();
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceGara)));
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceLotto)));
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.NORDDOCG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, nProgressivoDOCUMGARA)));
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.IDDOCDG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIDDOCDIG)));
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.GRUPPO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, gruppo)));
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.TIPOLOGIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tipologia)));
          elencoCampiDOCUMGARA.add(new DataColumn("DOCUMGARA.DESCRIZIONE", new JdbcParametro(JdbcParametro.TIPO_TESTO, titoloFile)));


          DataColumnContainer containerDOCUMGARA = new DataColumnContainer(elencoCampiDOCUMGARA);

          containerDOCUMGARA.insert("DOCUMGARA", sqlManager);

          // Aggiornamento di W_DOCDIG con il riferimento a DOCUMGARA
          sqlManager.update("update W_DOCDIG set DIGKEY1 = ?, DIGKEY2 = ? where IDPRG=? and IDDOCDIG=?",
              new Object[] { codiceGara, nProgressivoDOCUMGARA, "PG", newIDDOCDIG });


        } catch (SQLException e) {
          logger.error("Errore durante l'inserimento del file " + nomeFile, e);
          throw new GestoreException("Errore durante l'inserimento del file " + nomeFile , null, e);
        }


      }


      logger.debug("gestioneFileAllegati: fine metodo");


      return 0;
  }


  private void inserimentoGara(WSERPLiquidatoType liquidatoType, String uffint) throws GestoreException{

    String insertGaraComplete = "insert into gare(ngara, codgar1, codcig, tipgarg, not_gar, impapp, ditta, nomima, iaggiu, dattoa," +
    " fasgar, stepgar, modastg, estimp, sicinc, temesi, ribcal, precut, pgarof, garoff, idiaut, calcsoang, onsogrib, ribagg) " +
    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String insertGara = "insert into gare(ngara, codgar1, codcig, tipgarg, not_gar, ditta, nomima, iaggiu, dattoa)" +
    		" values(?,?,?,?,?,?,?,?,?)";
    String insertGare1 = "insert into gare1(ngara, codgar1) values(?,?)";
    String insertTorn = "insert into torn(codgar, cenint, tipgen, offaum, compreq, istaut, iterga, cliv1, settore)" +
    		" values(?,?,?,?,?,?,?,?,?)";
    String insertGarecont = "insert into garecont(ngara, ncont, dverbc, dcertu, impliq, codimp) values(?,?,?,?,?,?)";
    String insertDitg = "insert into ditg(ngara5, codgar5, dittao, nomimo, invoff) values(?,?,?,?,?)";
    String insertG_permessi = "insert into g_permessi(numper, syscon, autori, propri, codgar) values(?,?,?,?,?)";

    //ATTENZIONE in ANTHEA DEVE ESSERCI LA CODIFICA AUTOMATICA
    HashMap hm = pgManager.calcolaCodificaAutomatica("GARE", Boolean.TRUE, null,null);
    String ngara =  (String) hm.get("numeroGara");
    String codgar = "$" + ngara;
    String codcig = liquidatoType.getCig();
    Long sceltaContraente = liquidatoType.getSceltaContraente();
    Long tipgarg = null;
    Long iterga = null;
    Long scL = sceltaContraente + new Long(50);
    String valoreA1z05 = null;
    String valoreA1115 = null;


      try {


        valoreA1z05 = (String) this.sqlManager.getObject(
            "select tab2d2 from tab2 where tab2cod='A1z05' and tab2tip= ?", new Object[]{scL});


        valoreA1z05 = UtilityStringhe.convertiNullInStringaVuota(valoreA1z05);
        String valoreA1z05Founded = null;
        if(!"".equals(valoreA1z05)){
          if (valoreA1z05.indexOf(",")>0) {
            String vettValori[] = valoreA1z05.split(",");
            valoreA1z05Founded = vettValori[0];
          }else{
            valoreA1z05Founded = valoreA1z05;
          }
          //verifica sul profilo affidamenti? per ORA NO
          if(valoreA1z05Founded != null){
            tipgarg = new Long(valoreA1z05Founded);
            iterga = this.pgManager.getITERGA(new Long(tipgarg));
          }
        }
        //SETTORE
        valoreA1115 = (String) this.sqlManager.getObject(
            "select tab1desc from tab1 where tab1cod='A1115' and tab1tip= ?", new Object[]{new Long(6)});
        valoreA1115 = UtilityStringhe.convertiNullInStringaVuota(valoreA1115);
        String settore = null;
        if(!"".equals(valoreA1115)){
          settore = valoreA1115.substring(0,1) ;
        }


        String descrizione = liquidatoType.getDescrizione();
        String ditta = liquidatoType.getCodFornitore();
        Double importoAggiudicazione = liquidatoType.getImpAggiudicazione();

        Calendar calInizio = liquidatoType.getDataInizio();
        Date dataInizio = null;
        if(calInizio != null){
          dataInizio = calInizio.getTime();
        }

        Calendar calUltimazione = liquidatoType.getDataUltimazione();
        Date dataUltimazione = null;
        if(calUltimazione != null){
          dataUltimazione = calUltimazione.getTime();
        }

        String nomimo = (String) this.sqlManager.getObject( "select nomimp from impr where codimp = ? ", new Object[] {ditta});
        Double impLiquidato = liquidatoType.getImpLiquidato();
        //TIPGARG viene messo il valore che arriva direttamente dal ws visualizzabile nel profilo ANTICOR

        this.sqlManager.update(insertGara,new Object[] { ngara, codgar, codcig, scL, descrizione, ditta, nomimo, importoAggiudicazione,dataInizio});

        this.sqlManager.update(insertGare1, new Object[] {ngara,codgar});
        //String cenint = (String) this.sqlManager.getObject( "select min(codein) from uffint", new Object[] {});
        Long cliv1 = null;
        String fonte = liquidatoType.getFonte();
        if("2".equals(fonte)){
          cliv1= new Long(2);
        }

        this.sqlManager.update(insertTorn, new Object[] {codgar,uffint,new Long(1),new Long(2),new Long(2),null,iterga,cliv1,settore});

        this.sqlManager.update(insertGarecont, new Object[] {ngara, new Long(1), dataInizio, dataUltimazione, impLiquidato, ditta});

        this.sqlManager.update(insertDitg, new Object[] {ngara, codgar, ditta, nomimo, new Long(1)});

        Long numper = (Long) this.sqlManager.getObject( "select max(numper) from g_permessi", new Object[] {});
        numper =numper + new Long(1);

        this.sqlManager.update(insertG_permessi, new Object[] {numper, new Long(48), new Long(1), new Long(1), codgar});

          if (logger.isInfoEnabled()) {

            String impLstr = "nullo";
            if(impLiquidato != null){
              impLstr = impLiquidato.toString();
            }
            String dataInistr = "nulla";
            if(dataInizio != null){
              dataInistr = dataInizio.toString();
            }
            String dataUstr = "nulla";
            if(dataUltimazione != null){
              dataUstr = dataUltimazione.toString();
            }


            logger.info("Inserimento CIG: " + codcig + "\r\n" +
                " valori inseriti: " + "importo liquidato: " + impLstr + " data inizio: " + dataInistr + " data ultimazione: " + dataUstr);
          }


      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento del cig " + codcig, null,e);
      }

  }

  public Long verificaPresenzaPosizioneRda(String entita, String codgar, String codice, String codiceRda, String numPosizioneRda){
    Long result = null;
    try {
      result = (Long) this.sqlManager.getObject(
          "select count(POSRDA) from GCAP where NGARA = ? AND CODRDA = ? AND POSRDA = ?",
          new Object[]{codice, codiceRda, numPosizioneRda});
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  public String verificaRdaAssociata(String numRda) {
    String result ="";
    try {
      Vector<?> numero = this.sqlManager.getVector("select r.numrda from garerda r, gare g where r.codgar=g.codgar1 and g.esineg is null and r.numrda = ? ",new Object[] {numRda});
      if(numero != null){
        result = (String) SqlManager.getValueFromVectorParam(numero, 0).getValue();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
  
  public String getCodiga(String codgar, String codice){
	    String result = null;
	    try {
	      result = (String) this.sqlManager.getObject(
	          "select codiga from GARE where CODGAR1 = ? and NGARA = ?",
	          new Object[]{codgar,codice});
	    } catch (SQLException e) {
	      throw new RuntimeException(e);
	    }
	    return result;
  }

}

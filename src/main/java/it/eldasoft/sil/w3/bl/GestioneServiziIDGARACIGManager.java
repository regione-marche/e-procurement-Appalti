package it.eldasoft.sil.w3.bl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.transaction.TransactionStatus;

import it.avlp.simog.massload.xmlbeans.AllegatoType;
import it.avlp.simog.massload.xmlbeans.CPVSecondariaType;
import it.avlp.simog.massload.xmlbeans.CUPLOTTOType;
import it.avlp.simog.massload.xmlbeans.CancellaGara;
import it.avlp.simog.massload.xmlbeans.CancellaGaraDocument;
import it.avlp.simog.massload.xmlbeans.CancellaGaraResponse;
import it.avlp.simog.massload.xmlbeans.CancellaGaraResponseDocument;
import it.avlp.simog.massload.xmlbeans.CancellaLotto;
import it.avlp.simog.massload.xmlbeans.CancellaLottoDocument;
import it.avlp.simog.massload.xmlbeans.CancellaLottoResponse;
import it.avlp.simog.massload.xmlbeans.CancellaLottoResponseDocument;
import it.avlp.simog.massload.xmlbeans.ChiudiSessione;
import it.avlp.simog.massload.xmlbeans.ChiudiSessioneDocument;
import it.avlp.simog.massload.xmlbeans.ChiudiSessioneResponseDocument;
import it.avlp.simog.massload.xmlbeans.Collaborazione;
import it.avlp.simog.massload.xmlbeans.Collaborazioni;
import it.avlp.simog.massload.xmlbeans.CondizioneLtType;
import it.avlp.simog.massload.xmlbeans.ConsultaGara;
import it.avlp.simog.massload.xmlbeans.ConsultaGaraDocument;
import it.avlp.simog.massload.xmlbeans.ConsultaGaraResponse;
import it.avlp.simog.massload.xmlbeans.ConsultaGaraResponseDocument;
import it.avlp.simog.massload.xmlbeans.ConsultaNumeroGara;
import it.avlp.simog.massload.xmlbeans.ConsultaNumeroGaraDocument;
import it.avlp.simog.massload.xmlbeans.ConsultaNumeroGaraResponse;
import it.avlp.simog.massload.xmlbeans.ConsultaNumeroGaraResponseDocument;
import it.avlp.simog.massload.xmlbeans.DatiCUPType;
import it.avlp.simog.massload.xmlbeans.DatiGaraType;
import it.avlp.simog.massload.xmlbeans.ElencoCategMercType;
import it.avlp.simog.massload.xmlbeans.FlagSNType;
import it.avlp.simog.massload.xmlbeans.GaraType;
import it.avlp.simog.massload.xmlbeans.InserisciGara;
import it.avlp.simog.massload.xmlbeans.InserisciGara.DatiGara;
import it.avlp.simog.massload.xmlbeans.InserisciGaraDocument;
import it.avlp.simog.massload.xmlbeans.InserisciGaraResponse;
import it.avlp.simog.massload.xmlbeans.InserisciGaraResponseDocument;
import it.avlp.simog.massload.xmlbeans.InserisciLotto;
import it.avlp.simog.massload.xmlbeans.InserisciLotto.DatiLotto;
import it.avlp.simog.massload.xmlbeans.InserisciLottoDocument;
import it.avlp.simog.massload.xmlbeans.InserisciLottoResponse;
import it.avlp.simog.massload.xmlbeans.InserisciLottoResponseDocument;
import it.avlp.simog.massload.xmlbeans.InviaRequisiti;
import it.avlp.simog.massload.xmlbeans.InviaRequisiti.Requisiti;
import it.avlp.simog.massload.xmlbeans.InviaRequisitiDocument;
import it.avlp.simog.massload.xmlbeans.InviaRequisitiResponse;
import it.avlp.simog.massload.xmlbeans.InviaRequisitiResponseDocument;
import it.avlp.simog.massload.xmlbeans.Login;
import it.avlp.simog.massload.xmlbeans.LoginDocument;
import it.avlp.simog.massload.xmlbeans.LoginRPNT;
import it.avlp.simog.massload.xmlbeans.LoginRPNTDocument;
import it.avlp.simog.massload.xmlbeans.LoginRPNTResponse;
import it.avlp.simog.massload.xmlbeans.LoginRPNTResponseDocument;
import it.avlp.simog.massload.xmlbeans.LoginResponse;
import it.avlp.simog.massload.xmlbeans.LoginResponseDocument;
import it.avlp.simog.massload.xmlbeans.LottoType;
import it.avlp.simog.massload.xmlbeans.ModificaGara;
import it.avlp.simog.massload.xmlbeans.ModificaGaraDocument;
import it.avlp.simog.massload.xmlbeans.ModificaGaraResponse;
import it.avlp.simog.massload.xmlbeans.ModificaGaraResponseDocument;
import it.avlp.simog.massload.xmlbeans.ModificaLotto;
import it.avlp.simog.massload.xmlbeans.ModificaLottoDocument;
import it.avlp.simog.massload.xmlbeans.ModificaLottoResponse;
import it.avlp.simog.massload.xmlbeans.ModificaLottoResponseDocument;
import it.avlp.simog.massload.xmlbeans.Pubblica;
import it.avlp.simog.massload.xmlbeans.Pubblica.DatiPubblicazione;
import it.avlp.simog.massload.xmlbeans.PubblicaDocument;
import it.avlp.simog.massload.xmlbeans.PubblicaResponse;
import it.avlp.simog.massload.xmlbeans.PubblicaResponseDocument;
import it.avlp.simog.massload.xmlbeans.ReqGaraType;
import it.avlp.simog.massload.xmlbeans.ResponseCancellaGara;
import it.avlp.simog.massload.xmlbeans.ResponseCancellaLotto;
import it.avlp.simog.massload.xmlbeans.ResponseCheckLogin;
import it.avlp.simog.massload.xmlbeans.ResponseChiudiSession;
import it.avlp.simog.massload.xmlbeans.ResponseConsultaGara;
import it.avlp.simog.massload.xmlbeans.ResponseConsultaNumeroGara;
import it.avlp.simog.massload.xmlbeans.ResponseInserisciLotto;
import it.avlp.simog.massload.xmlbeans.ResponseInviaRequisiti;
import it.avlp.simog.massload.xmlbeans.ResponseModificaGara;
import it.avlp.simog.massload.xmlbeans.ResponseModificaLotto;
import it.avlp.simog.massload.xmlbeans.ResponsePubblicazioneBando;
import it.avlp.simog.massload.xmlbeans.SchedaType;
import it.avlp.simog.massload.xmlbeans.SimogWSPDDServiceStub;
import it.avlp.simog.massload.xmlbeans.TipiAppaltoType;
import it.avlp.smartCig.comunicazione.ComunicazioneType;
import it.avlp.smartCig.errore.ErroreType;
import it.avlp.smartCig.risultato.CodiceRisultatoType;
import it.avlp.smartCig.risultato.RisultatoType;
import it.avlp.smartCig.user.LoggedUserInfoType;
import it.avlp.smartCig.ws.AnnullaComunicazioneRequest;
import it.avlp.smartCig.ws.AnnullaComunicazioneResponse;
import it.avlp.smartCig.ws.ComunicaSingolaRequest;
import it.avlp.smartCig.ws.ComunicaSingolaResponse;
import it.avlp.smartCig.ws.ConsultaComunicazioneRequest;
import it.avlp.smartCig.ws.ConsultaComunicazioneResponse;
import it.avlp.smartCig.ws.Services;
import it.avlp.smartCig.ws.ServicesServiceLocator;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.utils.UtilityDateExtension;
import it.eldasoft.sil.w3.utils.UtilitySITAT;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;


public class GestioneServiziIDGARACIGManager {

  /** Logger */
  static Logger                       logger                           = Logger.getLogger(GestioneServiziIDGARACIGManager.class);

  private static final String         PROP_SIMOG_WS_URL                = "it.eldasoft.simog.ws.url";
  private static final String         PROP_SIMOG_WS_SMARTCIG_URL       = "it.eldasoft.simog.ws.smartcig.url";
  public static final String          GARAGIAPUBBLICATA = "SIMOGWS_GARALOTTOMANAGER_APP_31";
  public static final String          LOGIN_SIMOG_ERRATA = "SIMOGWS_UNDEFINEND_ERR_01";
  public static final String          RUP_NON_PRESENTE_O_SENZA_COLL = "SIMOGWS_ACTIONS_APP_03";
  private static final String         PROP_SIMOG_WS_LOGIN = "it.eldasoft.simog.ws.login";
  private static final String         PROP_SIMOG_WS_PASSWORD = "it.eldasoft.simog.ws.password";
  private static final String         PROP_SIMOG_WS_RUOLOUTENTE =  "it.eldasoft.simog.ws.ruoloutente";
  
  
//  private static final String         PROP_SIMOG_WS_TRUSTSTORE         = "it.eldasoft.simog.ws.truststore";
//  private static final String         PROP_SIMOG_WS_TRUSTSTOREPASSWORD = "it.eldasoft.simog.ws.truststorepassword";

//  private static final String         PROP_HTTP_PROXY_HOST             = "it.eldasoft.http.proxyhost.url";
//  private static final String         PROP_HTTP_PROXY_PORT             = "it.eldasoft.http.proxyhost.port";

  private SqlManager                  sqlManager;

  private GestioneXMLIDGARACIGManager gestioneXMLIDGARACIGManager;

  private TabellatiManager tabellatiManager;
  
  public SqlManager getSqlManager() {
    return this.sqlManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneXMLIDGARACIGManager(
      GestioneXMLIDGARACIGManager gestioneXMLIDGARACIGManager) {
    this.gestioneXMLIDGARACIGManager = gestioneXMLIDGARACIGManager;
  }
  
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
	  this.tabellatiManager = tabellatiManager;
  }

  /**
   * Invio della richiesta di generazione del numero identificativo per una gara
   *
   * @param numgara
   * @throws GestoreException
   */
  public String GareMassivo(String simogwsuser, String simogwspass,
      String sql, String index, boolean rpntFailed) throws GestoreException {

    String idgara = null;

    if (logger.isDebugEnabled())
      logger.debug("richiestaIdGara: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
    	
      simogWS = this.getSimogWS();
      String codcentro = (String) this.sqlManager.getObject("select codcentro from centricosto where idcentro =?", new Object[] {index});
      //String index = this.getIndiceCollaborazione(numgara, "W3GARA");
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, codcentro,rpntFailed);
      //hMapwsLogin.put("index", index);
      
      if(!((Boolean) hMapwsLogin.get("indiceAmmesso"))) {
        throw new GestoreException(
            "L'utente SIMOG non ha accesso al centro di costo indicato",
            "gestioneIDGARACIG.ws.error");
      }

      // Creazione del contenuto XML
      String ufficio_id = ((String) hMapwsLogin.get("ufficio_id"));
      String ufficio_denominazione = ((String) hMapwsLogin.get("ufficio_denominazione"));
      String azienda_codicefiscale = ((String) hMapwsLogin.get("azienda_codicefiscale"));
      String azienda_denominazione = ((String) hMapwsLogin.get("azienda_denominazione"));

      // Gestione identificativo per i lotti (CIG)
      // Si richiede il codice CIG per ogni lotto che ne è privo.
      List<?> datiW3 = this.sqlManager.getListVector(sql, new Object[] { });
      if (datiW3 != null && datiW3.size() > 0) {

        for (int i = 0; i < datiW3.size(); i++) {
        	Long numgara = 0L;
        	try {
        		numgara = (Long) SqlManager.getValueFromVectorParam(datiW3.get(i), 0).getValue();
        		GaraType gara = this.gestioneXMLIDGARACIGManager.getDatiGara(
                        numgara, ufficio_id, ufficio_denominazione, azienda_codicefiscale,
                        azienda_denominazione, true);
        		DatiGara datiGara = DatiGara.Factory.newInstance();
        	    datiGara.setDatiGara(gara);
                idgara = this.wsInserisciGara(simogWS, hMapwsLogin, datiGara);
                //aggiorna idgara
       
                this.aggiornaW3GARACIGMassivo(numgara, idgara);
                
                logger.info("Richiesta id gara " + numgara + " eseguita con successo IDANAC=" + idgara);
                //Richiest CIG Lotti
            
                List<?> datiW3LOTTI = this.sqlManager.getListVector("SELECT NUMLOTT FROM W3LOTT WHERE NUMGARA = ?", new Object[] {numgara });
                if (datiW3LOTTI != null && datiW3LOTTI.size() > 0) {
					for (int j = 0; j < datiW3LOTTI.size(); j++) {
                    	Long numlott = (Long) SqlManager.getValueFromVectorParam(datiW3LOTTI.get(j), 0).getValue();
                   		// Ricavo il contenuto XML
                   		LottoType lotto = this.gestioneXMLIDGARACIGManager.getDatiLotto(numgara, numlott, true);
                   		DatiLotto datiLotto = DatiLotto.Factory.newInstance();
                   	    datiLotto.setLotto(lotto);
                    	    
						HashMap<String, Object> hMapwsInserisciLotto = this.wsInserisciLotto(simogWS, hMapwsLogin, datiLotto, idgara);
                            
						this.aggiornaW3LOTTCIGMassivo(numgara, numlott, hMapwsInserisciLotto );
                    }
                }
            
                logger.info("Richiesta cig per gara " + numgara + " eseguita con successo");
              	//Pubblica Gara
              	String selectW3GARA = "select data_perfezionamento_bando, data_termine_pagamento, id_gara, tipo_operazione," +
              		" ora_scadenza, dscad_richiesta_invito, data_lettera_invito" +
              		" from w3gara where numgara = ?";
              List<?> datiW3GARA = this.sqlManager.getVector(selectW3GARA, new Object[] {numgara});

              if (datiW3GARA != null && datiW3GARA.size() > 0 ) {
                // ********** Data pubblicazione
              	Date dataPubblicazione = (Date) SqlManager.getValueFromVectorParam(datiW3GARA, 0).getValue();
                String dataPubblicazioneS = UtilityDate.convertiData(dataPubblicazione, UtilityDate.FORMATO_AAAAMMGG);

                // ********** Data scadenza pagamenti
              	Date dataScadenzaPagamenti = (Date) SqlManager.getValueFromVectorParam(datiW3GARA, 1).getValue();
                String dataScadenzaPagamentiS = UtilityDate.convertiData(dataScadenzaPagamenti, UtilityDate.FORMATO_AAAAMMGG);

                // IDGARA (CIG)
                String cig = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 2).getValue();

                // ********** Progressivo aggiudicazione
                String progCui = "";
                String tipoOperazione = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 3).getValue();
                String oraScadenza = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 4).getValue();

                Date dataScadenzaRichiestaInvito = (Date) SqlManager.getValueFromVectorParam(datiW3GARA, 5).getValue();
                String dataScadenzaRichiestaInvitoS = "";
                if (dataScadenzaRichiestaInvito != null) {
                  dataScadenzaRichiestaInvitoS = UtilityDate.convertiData(dataScadenzaRichiestaInvito, UtilityDate.FORMATO_AAAAMMGG);
                }

                Date dataLetteraInvito = (Date) SqlManager.getValueFromVectorParam(datiW3GARA, 6).getValue();
                String dataLetteraInvitoS = "";
                if (dataLetteraInvito != null) {
                  dataLetteraInvitoS = UtilityDate.convertiData(dataLetteraInvito, UtilityDate.FORMATO_AAAAMMGG);
                }
                
                // verifico la scelta del contraente se la procedura è negoziata non devo inviare i dati della pubblicazione
                Long tipoProcedura = (Long) this.sqlManager.getObject("SELECT ID_SCELTA_CONTRAENTE FROM W3LOTT WHERE NUMGARA = ? AND STATO_SIMOG IN (2,3,4)",  new Object[] {numgara});
                // Pubblicazione
                DatiPubblicazione datiPubblicazione = null;
                if (!tipoProcedura.equals(4L) && !tipoProcedura.equals(10L) && !tipoProcedura.equals(14L)) {
                	datiPubblicazione = this.gestioneXMLIDGARACIGManager.getDatiPubblicazione(numgara);
                }
                // Documenti allegati
                AllegatoType[] documentiAllegati = null;
                documentiAllegati = this.gestioneXMLIDGARACIGManager.getDocumentiAllegati(numgara);

                // Cup lotti
                CUPLOTTOType[] cuplotti = null;
                cuplotti = this.gestioneXMLIDGARACIGManager.getCupLotti(numgara);

                this.wsPubblica(simogWS, hMapwsLogin, dataPubblicazioneS,
                  dataScadenzaPagamentiS, cig, progCui, datiPubblicazione, tipoOperazione,
                  documentiAllegati, oraScadenza, dataScadenzaRichiestaInvitoS, dataLetteraInvitoS, cuplotti);

                this.aggiornaPubblicazioneMassivo(numgara);
              }

              logger.info("Pubblicazione gara " + numgara + " eseguita con successo");
              
        	} catch (Exception e) {
        		logger.error("Errore nella gara " + numgara + "", e);
        	}
        }
      }
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (Exception e) {
    	throw new GestoreException(
    	          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
    	          "gestioneIDGARACIG.ws.error", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("richiestaIdGara: fine metodo");

    return idgara;
  }
  
  /**
   * Invio della richiesta di generazione del numero identificativo per una gara
   *
   * @param numgara
   * @throws GestoreException
   */
  public String richiestaIDGARA(String simogwsuser, String simogwspass,
      Long numgara, boolean rpntFailed) throws GestoreException {

    String idgara = null;

    if (logger.isDebugEnabled())
      logger.debug("richiestaIdGara: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3GARA");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3GARA");
      
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from w3gara left join uffint on w3gara.codein=uffint.codein where w3gara.numgara = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);
      
      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	// Creazione del contenuto XML
          String ufficio_id = ((String) hMapwsLogin.get("ufficio_id"));
          String ufficio_denominazione = ((String) hMapwsLogin.get("ufficio_denominazione"));
          String azienda_codicefiscale = ((String) hMapwsLogin.get("azienda_codicefiscale"));
          String azienda_denominazione = ((String) hMapwsLogin.get("azienda_denominazione"));

          GaraType gara = this.gestioneXMLIDGARACIGManager.getDatiGara(
              numgara, ufficio_id, ufficio_denominazione, azienda_codicefiscale,
              azienda_denominazione, true);

          DatiGara datiGara = DatiGara.Factory.newInstance();
          datiGara.setDatiGara(gara);
         
          idgara = this.wsInserisciGara(simogWS, hMapwsLogin, datiGara);
          this.aggiornaW3GARACIG(numgara, idgara);
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (SQLException e) {
        throw new GestoreException(
                "Si e' verificato un errore durante l'interazione con la base dati",
                "gestioneIDGARACIG.sqlerror", e);
    } catch (CriptazioneException e) {
            throw new GestoreException(
                "Si e' verificato un errore durante l'interazione con la base dati",
                "gestioneIDGARACIG.sqlerror", e);
    }
	finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("richiestaIdGara: fine metodo");

    return idgara;

  }


  /**
   * Invio della richiesta di generazione SMARTCIG
   *
   * @param numgara
   * @throws GestoreException
   */
  public String richiestaSMARTCIG(String simogwsuser, String simogwspass,
      Long numgara, boolean rpntFailed) throws GestoreException {

    String smartCig = null;

    if (logger.isDebugEnabled())
      logger.debug("richiestaSMARTCIG: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3SMARTCIG");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3SMARTCIG");
      
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from W3SMARTCIG left join uffint on W3SMARTCIG.codein=uffint.codein where W3SMARTCIG.CODRICH = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);
      
      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	  //String index = index_ufficioId;
      	//if (index_ufficioId.indexOf(";") != -1) {
      	//	index = index_ufficioId.substring(0, index_ufficioId.indexOf(";"));
      	//  } 
          // Creazione della Request comunicazione 
          String ticket = ((String) hMapwsLogin.get("ticket"));
        String index =  ((String) hMapwsLogin.get("index"));
          ComunicaSingolaRequest comunicazioneRequest = this.gestioneXMLIDGARACIGManager.getComunicazioneSmartCig(numgara, index, ticket);

          Services simogSmartCigWS = this.getSimogWSSmartCig();
          
          smartCig = this.wsComunicaSingola(simogSmartCigWS, comunicazioneRequest);
          this.aggiornaW3SMARTCIG(numgara, smartCig);
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
    } catch (GestoreException e) {
      throw e;

    } catch (SocketTimeoutException e) {
    	throw new GestoreException(
  	          "Il servizio di elaborazione degli identificativi di gara", "gestioneIDGARACIG.error",
  	          new Object[] { "Il servizio di elaborazione degli identificativi di gara" }, null);
    } 
      catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } 
    catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (SQLException e) {
        throw new GestoreException(
                "Si e' verificato un errore durante l'interazione con la base dati",
                "gestioneIDGARACIG.sqlerror", e);
	} catch (CriptazioneException e) {
	  throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("richiestaSMARTCIG: fine metodo");

    return smartCig;

  }
  
  /**
   * Invio della richiesta dei requisiti per una gara
   *
   * @param numgara
   * @throws GestoreException
   */
  public String richiestaRequisiti(String simogwsuser, String simogwspass,
      Long numgara, String idgara, boolean rpntFailed) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("richiestaRequisiti: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();



    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3GARA");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3GARA");
      
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from w3gara left join uffint on w3gara.codein=uffint.codein where w3gara.numgara = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);
      
      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	// Creazione del contenuto XML
          Requisiti datiRequisiti = this.gestioneXMLIDGARACIGManager.getDatiRequisiti(numgara, true);
          
          HashMap<String, Object> hMapwsInviaRequisiti = this.wsInviaRequisiti(simogWS, hMapwsLogin, datiRequisiti, idgara);
          String messaggio = (String) (hMapwsInviaRequisiti.get("messaggio"));
          if (logger.isDebugEnabled())
              logger.debug("wsInviaRequisiti.messaggio: " + messaggio);
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
      
    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione dei requisiti di gara",
          "gestioneIDGARACIG.ws.error", e);
    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione dei requisiti di gara",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (SQLException e) {
        throw new GestoreException(
            "Si e' verificato un errore durante l'interazione con la base dati",
            "gestioneIDGARACIG.sqlerror", e);
	} catch (CriptazioneException e) {
	  throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("richiestaRequisiti: fine metodo");

    return idgara;

  }


  /**
   * Richiesta lista collaborazioni
   *
   * @param simogwsuser
   * @param simogwspass
   * @return
   * @throws GestoreException
   */
  /*APPALTI-1063
  public String richiestaCollaborazioni(Long syscon, String codrup, String simogwsuser,
      String simogwspass) throws GestoreException {

    String idgara = null;

    if (logger.isDebugEnabled())
      logger.debug("richiestaCollaborazioni: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();

      hMapwsLogin = this.wsLoginCollaborazioni(simogWS, simogwsuser,
          simogwspass);

      // Gestione e memorizzazione della lista delle collaborazioni
      ResponseCheckLogin responseCheckLogin = ((ResponseCheckLogin) hMapwsLogin.get("responseCheckLogin"));
      if (responseCheckLogin.getColl() != null) {
        if (responseCheckLogin.getColl().getCollaborazioniArray() != null) {
          Collaborazione[] arrayOfCollaborazione = responseCheckLogin.getColl().getCollaborazioniArray();
          if (arrayOfCollaborazione != null && arrayOfCollaborazione.length > 0) {

            // Elimino le associazioni alle collaborazioni di archivio
        	this.sqlManager.update("delete from w3usrsyscoll where syscon = ? and rup_codtec = ?",
                new Object[] { syscon, codrup });

            for (int i = 0; i < arrayOfCollaborazione.length; i++) {
              //String index = arrayOfCollaborazione[i].getIndex();
              String ufficio_id = arrayOfCollaborazione[i].getUfficioId();
              String ufficio_denominazione = arrayOfCollaborazione[i].getUfficioDenominazione();
              String ufficio_profilo = arrayOfCollaborazione[i].getUfficioProfilo();
              String azienda_codicefiscale = arrayOfCollaborazione[i].getAziendaCodiceFiscale();
              String azienda_denominazione = arrayOfCollaborazione[i].getAziendaDenominazione();

              // Verifico se esiste gia' una collaborazione nell'archivio generale
              Long conteggioW3AZIENDAUFFICIO = (Long) this.sqlManager.getObject(
                  //"select count(*) from w3aziendaufficio where indexcoll = ? and ufficio_id = ?",
              		"select count(*) from w3aziendaufficio where ufficio_id = ?",
                  //new Object[] { index, ufficio_id });
              		new Object[] { ufficio_id });

              Long id = null;

              if (conteggioW3AZIENDAUFFICIO != null && conteggioW3AZIENDAUFFICIO.longValue() == 0) {
                // E' necessario inserire una nuova riga nella lista delle
                // amministrazioni/stazioni appaltanti
                id = (Long) this.sqlManager.getObject(
                    "select max(id) from w3aziendaufficio", new Object[] {});
                if (id == null) id = 0L;
                id = (id.longValue() + 1);
                this.sqlManager.update("insert into w3aziendaufficio (id, "
                    + "azienda_cf, "
                    + "azienda_denom, "
                    //+ "indexcoll, "
                    + "ufficio_denom, "
                    + "ufficio_id, "
                    + "ufficio_profilo) "
                    //+ "values (?,?,?,?,?,?,?)", new Object[] { id,
                    + "values (?,?,?,?,?,?)", new Object[] { id,
                    azienda_codicefiscale, azienda_denominazione, //index,
                    ufficio_denominazione, ufficio_id, ufficio_profilo });
              } else {
                id = (Long) this.sqlManager.getObject(
                    //"select id from w3aziendaufficio where indexcoll = ? and ufficio_id = ?",
                		"select id from w3aziendaufficio where ufficio_id = ?",
                    //new Object[] { index, ufficio_id });
                		new Object[] { ufficio_id });
                this.sqlManager.update("update w3aziendaufficio set azienda_cf = ?," +
                		"azienda_denom = ?," +
                		"ufficio_denom = ?," +
                		"ufficio_profilo = ?" + 
                		" where id = ?", new Object[] {
                        azienda_codicefiscale, azienda_denominazione,
                        ufficio_denominazione, ufficio_profilo, id });
              }

              // Aggiungo l'associazione nella tabella delle associazioni W3USRSYSCOLL
              Long conta = (Long) this.sqlManager.getObject(
            		"select count(*) from w3usrsyscoll where syscon=? and rup_codtec=? and w3aziendaufficio_id=?", 
            		new Object[] { syscon, codrup, id } );
              if (conta.longValue() == 0) {
            	  this.sqlManager.update("insert into w3usrsyscoll (syscon, rup_codtec, w3aziendaufficio_id) values (?,?,?)",
                  new Object[] { syscon, codrup, id });
              }
            }
          }
        }
      }

    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli lista delle collaborazioni",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled())
      logger.debug("richiestaCollaborazioni: fine metodo");

    return idgara;

  }*/
  
  /**
   * Richiesta lista collaborazioni
   *
   * @param simogwsuser
   * @param simogwspass
   * @return
   * @throws GestoreException
   */
  public List<Vector<Object>> checkCollaborazioni(String codein, String simogwsuser,
      String simogwspass, boolean rpntFailed) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("checkCollaborazioni: inizio metodo");
    
    List<Vector<Object>> result= new ArrayList<Vector<Object>>();
    
   
    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();

      hMapwsLogin = this.wsLoginCollaborazioni(simogWS, simogwsuser,
          simogwspass, rpntFailed);
      
      String cfAzienda = (String) sqlManager.getObject(
          "select cfein from uffint where codein = ?",
          new Object[] { codein });
      String denomAzienda = (String) sqlManager.getObject(
          "select nomein from uffint where codein = ?",
          new Object[] { codein });


      // Gestione della lista delle collaborazioni
      ResponseCheckLogin responseCheckLogin = ((ResponseCheckLogin) hMapwsLogin.get("responseCheckLogin"));
      if (responseCheckLogin.getColl() != null) {
        if (responseCheckLogin.getColl().getCollaborazioniArray() != null) {
          Collaborazione[] arrayOfCollaborazione = responseCheckLogin.getColl().getCollaborazioniArray();
          if (arrayOfCollaborazione != null && arrayOfCollaborazione.length > 0) {
            for (int i = 0; i < arrayOfCollaborazione.length; i++) {
              //String index = arrayOfCollaborazione[i].getIndex();
              String ufficio_id = arrayOfCollaborazione[i].getUfficioId();
              String ufficio_denominazione = arrayOfCollaborazione[i].getUfficioDenominazione();
              String ufficio_profilo = arrayOfCollaborazione[i].getUfficioProfilo();
              String azienda_codicefiscale = arrayOfCollaborazione[i].getAziendaCodiceFiscale();
              String azienda_denominazione = arrayOfCollaborazione[i].getAziendaDenominazione();
              
              if(!"null".equals(ufficio_id)) {  
                //OPERAZIONI DI INSERIMENTO/AGGIORNAMENTO DB
                // Verifico se esiste gia' il centro di costo
                Long conteggioW3CENTRICOSTO = (Long) sqlManager.getObject(
                      "select count(*) from CENTRICOSTO where codcentro = ?",
                      new Object[] { ufficio_id });
  
                Long idcentro = null;
  
                if (conteggioW3CENTRICOSTO != null && conteggioW3CENTRICOSTO.longValue() == 0) {
                  // E' necessario inserire una nuova riga nella tabella 
                  idcentro = (Long) sqlManager.getObject(
                      "select max(idcentro) from CENTRICOSTO", new Object[] {});
                  if (idcentro == null) idcentro = 0L;
                  idcentro = (idcentro.longValue() + 1);
                  sqlManager.update("insert into CENTRICOSTO (idcentro, "
                      + "codcentro, "
                      + "denomcentro "
                      + ") "
                      + " values (?,?,?)", new Object[] { idcentro, ufficio_id, ufficio_denominazione });
                } else {
                  idcentro = (Long) sqlManager.getObject("select idcentro from CENTRICOSTO where codcentro = ?",
                          new Object[] { ufficio_id });
                  sqlManager.update("update CENTRICOSTO set DENOMCENTRO = ?"
                          +" where idcentro = ?", new Object[] {
                          ufficio_denominazione, idcentro });
                }
                //OPERAZIONI DI POPOLAMENTO DELLA LISTA IN OUTPUT
              //filtro per l'uffint attivo e recupero il nome dalla mia anagrafica. 
              //Se non ho un cf settato, propongo i valori come se non avessi il filtro sull'uffint
                if(codein!=null && cfAzienda!=null) {
                  if(cfAzienda.equals(azienda_codicefiscale)) {
                    Vector<Object> vector = new Vector<Object>();
                    vector.add(idcentro);
                    vector.add(ufficio_denominazione);
                    vector.add(azienda_codicefiscale);
                    vector.add(denomAzienda);
                    vector.add(ufficio_id);
                    vector.add(codein);
                    result.add(vector);
                  }
                  
                }else {
                  List<?> aziende = sqlManager.getListVector("select codein, nomein from uffint where cfein = ?",new Object[] { azienda_codicefiscale });
                  for(int j=0; j<aziende.size();j++) {
                    String codeinFromSql = (String) SqlManager.getValueFromVectorParam(aziende.get(j), 0).getValue();
                    denomAzienda = (String) SqlManager.getValueFromVectorParam(aziende.get(j), 1).getValue();
                    Vector<Object> vector = new Vector<Object>();
                    vector.add(idcentro);
                    vector.add(ufficio_denominazione);
                    vector.add(azienda_codicefiscale);
                    vector.add(denomAzienda);
                    vector.add(ufficio_id);
                    vector.add(codeinFromSql);
                    result.add(vector);
                  }
                }
              }
            }
          }
        }
      }

    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione della lista delle collaborazioni",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled())
      logger.debug("richiestaCollaborazioni: fine metodo");

    return result;

  }

  /**
   * Invio della richiesta di pubblicazione della gara e dei lotti associati
   *
   * @param simogwsuser
   * @param simogwspass
   * @param numgara
   * @throws GestoreException
   */
  public void pubblica(String simogwsuser, String simogwspass, Long numgara, boolean rpntFailed)
      throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("pubblica: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();
    
    //APPALTI-1085
    String esito = "";

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3GARA");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3GARA");
      
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from w3gara left join uffint on w3gara.codein=uffint.codein where w3gara.numgara = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);

      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	  String selectW3GARA = "select data_perfezionamento_bando, data_termine_pagamento, id_gara, tipo_operazione," +
    		" ora_scadenza, dscad_richiesta_invito, data_lettera_invito" +
    		" from w3gara where numgara = ?";
    	  List<?> datiW3GARA = this.sqlManager.getVector(selectW3GARA, new Object[] { numgara });

    if (datiW3GARA != null && datiW3GARA.size() > 0 ) {
    	// ********** Data pubblicazione
    	Date dataPubblicazione = (Date) SqlManager.getValueFromVectorParam(datiW3GARA, 0).getValue();
    	String dataPubblicazioneS = UtilityDate.convertiData(dataPubblicazione, UtilityDate.FORMATO_AAAAMMGG);

    	// ********** Data scadenza pagamenti
    	Date dataScadenzaPagamenti = (Date) SqlManager.getValueFromVectorParam(datiW3GARA, 1).getValue();
    	String dataScadenzaPagamentiS = UtilityDate.convertiData(dataScadenzaPagamenti, UtilityDate.FORMATO_AAAAMMGG);

    	// IDGARA (CIG)
    	String cig = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 2).getValue();

      // ********** Progressivo aggiudicazione
      String progCui = "";

      String tipoOperazione = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 3).getValue();

      String oraScadenza = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 4).getValue();

      Date dataScadenzaRichiestaInvito = (Date) SqlManager.getValueFromVectorParam(datiW3GARA, 5).getValue();
      String dataScadenzaRichiestaInvitoS = "";
      if (dataScadenzaRichiestaInvito != null) {
        dataScadenzaRichiestaInvitoS = UtilityDate.convertiData(dataScadenzaRichiestaInvito, UtilityDate.FORMATO_AAAAMMGG);
      }

      Date dataLetteraInvito = (Date) SqlManager.getValueFromVectorParam(datiW3GARA, 6).getValue();
      String dataLetteraInvitoS = "";
      if (dataLetteraInvito != null) {
        dataLetteraInvitoS = UtilityDate.convertiData(dataLetteraInvito, UtilityDate.FORMATO_AAAAMMGG);
      }
      
      // verifico la scelta del contraente se la procedura è negoziata non devo inviare i dati della pubblicazione
      //Long tipoProcedura = (Long) this.sqlManager.getObject("SELECT ID_SCELTA_CONTRAENTE FROM W3LOTT WHERE NUMGARA = ? AND STATO_SIMOG IN (2,3,4)",  new Object[] {numgara});
      // Pubblicazione
      DatiPubblicazione datiPubblicazione = null;
      //if (!tipoProcedura.equals(new Long("4")) && !tipoProcedura.equals(new Long("10")) && !tipoProcedura.equals(new Long("14"))) {
      datiPubblicazione = this.gestioneXMLIDGARACIGManager.getDatiPubblicazione(numgara);
      //}
      // Documenti allegati
      AllegatoType[] documentiAllegati = null;
      documentiAllegati = this.gestioneXMLIDGARACIGManager.getDocumentiAllegati(numgara);

      // Cup lotti
      CUPLOTTOType[] cuplotti = null;
      cuplotti = this.gestioneXMLIDGARACIGManager.getCupLotti(numgara);


      esito = this.wsPubblica(simogWS, hMapwsLogin, dataPubblicazioneS,
        dataScadenzaPagamentiS, cig, progCui, datiPubblicazione, tipoOperazione,
        documentiAllegati, oraScadenza, dataScadenzaRichiestaInvitoS, dataLetteraInvitoS, cuplotti);
      }
      if(!GARAGIAPUBBLICATA.equals(esito)) { //APPALTI-1085
        this.aggiornaPubblicazione(numgara);
      }else {
        throw new GestoreException(
            GARAGIAPUBBLICATA,
            "gestioneIDGARACIG.ws.pubblica.error",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }

    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("pubblica: fine metodo");

  }

  /**
   * Invio della richiesta di modifica dei dati della gara successivamente alla
   * generazione del numero identificativo della gara
   *
   * @param simogwsuser
   * @param simogwspass
   * @param numgara
   * @throws GestoreException
   */
  public void modificaGARA(String simogwsuser, String simogwspass, Long numgara, boolean rpntFailed)
      throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("modificaGARA: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3GARA");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3GARA");
      
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from w3gara left join uffint on w3gara.codein=uffint.codein where w3gara.numgara = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);
      
      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	  String idgara = (String) this.sqlManager.getObject(
    			  "select id_gara from w3gara where numgara = ?", new Object[] { numgara });

    	  String ufficio_id = ((String) hMapwsLogin.get("ufficio_id"));
    	  String ufficio_denominazione = ((String) hMapwsLogin.get("ufficio_denominazione"));
    	  String azienda_codicefiscale = ((String) hMapwsLogin.get("azienda_codicefiscale"));
    	  String azienda_denominazione = ((String) hMapwsLogin.get("azienda_denominazione"));

    	  GaraType gara = this.gestioneXMLIDGARACIGManager.getDatiGara(numgara, ufficio_id, ufficio_denominazione, 
    			  azienda_codicefiscale, azienda_denominazione, true);

    	  ModificaGara.DatiGara datiGara = ModificaGara.DatiGara.Factory.newInstance();
    	  datiGara.setDatiGara(gara);
    	      
    	  this.wsModificaGara(simogWS, hMapwsLogin, idgara, datiGara);
    	  this.aggiornaW3GARASTATO(numgara, 4L);
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("modificaGARA: fine metodo");

  }

  /**
   * Consultazione della gara e del lotto
   *
   * @param simogwsuser
   * @param simogwspass
   * @param cig
   * @throws GestoreException
   */
  public HashMap<String, Object> consultaGARALOTTO(String simogwsuser,
      String simogwspass, String cig, Long syscon, String codUffInt, String codrup, boolean rpntFailed) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("consultaGARALOTTO: inizio metodo");

    HashMap<String, Object> hMapConsultaGaraLotto = new HashMap<String, Object>();

    // Verifico esistenza di un lotto con il CIG indicato
    if (this.esisteCIG(cig)) {
      throw new GestoreException(
          "Il lotto identificato dal CIG indicato è già presente in base dati. Non è possibile procedere con il recupero dei dati.",
          "gestioneIDGARACIG.consultagaralotto.cigesistente");
    }

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();
    int validitaCig = this.controlloCIG(cig);
    if (validitaCig == -1) {
    	throw new GestoreException(
    	          "Il codice cig inserito non è valido",
    	          "gestioneIDGARACIG.consultagaralotto.cigesistente");
    }
    // Interrogazione del servizio di consultazione gara
    try {
      simogWS = this.getSimogWS();
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass,rpntFailed);

      if (validitaCig == 0) {
    	  String datiGaraLottoXML = this.wsConsultaGara(simogWS, hMapwsLogin, cig);
    	  hMapConsultaGaraLotto = this.gestioneXMLIDGARACIGManager.inserisciGaraLottodaSIMOG(
    	          datiGaraLottoXML, cig, syscon, codUffInt, codrup);
      } else if (validitaCig == 1) {
    	  Services simogSmartCigWS = this.getSimogWSSmartCig();
    	  ComunicazioneType comunicazione = this.wsConsultaSmartCig(simogSmartCigWS, hMapwsLogin, cig, syscon, codrup);
    	  hMapConsultaGaraLotto = this.gestioneXMLIDGARACIGManager.inserisciSMARTCIGdaSIMOG(
    			  comunicazione, cig, syscon, codUffInt, codrup, hMapwsLogin);
      }
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante la consultazione della gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di consultazione della gara",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled())
      logger.debug("consultaGARALOTTO: fine metodo");

    return hMapConsultaGaraLotto;

  }

  /**
   * Caricamento dei dati per confronto della gara e del lotto in locale con i dati di SIMOG
   *
   * @param simogwsuser
   * @param simogwspass
   * @param cig
   * @throws GestoreException
   */
  public HashMap<String, Object> confrontaGaraLotto(String simogwsuser, String simogwspass, String cig, String codUffInt, boolean rpntFailed) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("confrontaGaraLotto: inizio metodo");

    HashMap<String, Object> hMapConfrontaGaraLotto = new HashMap<String, Object>();

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();
    int validitaCig = this.controlloCIG(cig);  
    if (validitaCig != 0) {
    	throw new GestoreException(
    	          "Il codice cig inserito non è valido",
    	          "gestioneIDGARACIG.consultagaralotto.cigesistente");
    }
    
    try {
    	simogWS = this.getSimogWS();
    	hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass,rpntFailed);

    	if (validitaCig == 0) {
    		// Richiesta dei dati gara e lotto a SIMOG a partire dal CIG
    		String datiGaraLottoXML = this.wsConsultaGara(simogWS, hMapwsLogin, cig);
    		SchedaType schedaDatiSIMOG = SchedaType.Factory.parse(datiGaraLottoXML);

    		if (schedaDatiSIMOG != null) {
    			hMapConfrontaGaraLotto.put("schedaDatiSIMOG", schedaDatiSIMOG);
    		}
    		
    		// Caricamento dell'oggetto SchedaType con i dati in locale
    		Long numgara = (Long) this.sqlManager.getObject("select NUMGARA from W3LOTT where CIG=?", new Object[] { cig });
    		Long numlott = (Long) this.sqlManager.getObject("select NUMLOTT from W3LOTT where CIG=?", new Object[] { cig });
	      
    		String ufficio_id = null;
    		String ufficio_denominazione = null;
    		String azienda_codicefiscale = null;
    		String azienda_denominazione = null;

    		Collaborazioni collaborazioni = (Collaborazioni) hMapwsLogin.get("collaborazioni");
    		
    		if (collaborazioni.getCollaborazioniArray().length == 1) {
    			Collaborazione collaborazione = collaborazioni.getCollaborazioniArray(0);
    			ufficio_id = collaborazione.getUfficioId();
    			ufficio_denominazione = collaborazione.getUfficioDenominazione();
    			azienda_codicefiscale = collaborazione.getAziendaCodiceFiscale();
    			azienda_denominazione = collaborazione.getAziendaDenominazione();
    		} else {
    			boolean trovato = false;
    			String cfUffint = (String) this.sqlManager.getObject("select CFEIN from UFFINT where CODEIN=?", new Object[] { codUffInt });
    			Collaborazione collaborazione = null;
    			Collaborazione[] arrayCollaborazioni = collaborazioni.getCollaborazioniArray();
    			for (int i=0; i < arrayCollaborazioni.length && !trovato; i++) {
    				collaborazione = arrayCollaborazioni[i];
    				if (collaborazione.getAziendaCodiceFiscale().equalsIgnoreCase(cfUffint)) {
    					trovato = true;
    					ufficio_id = collaborazione.getUfficioId();
    	    			ufficio_denominazione = collaborazione.getUfficioDenominazione();
    	    			azienda_codicefiscale = collaborazione.getAziendaCodiceFiscale();
    	    			azienda_denominazione = collaborazione.getAziendaDenominazione();
    				}
    			}
    		}

    		// Creazione del contenuto XML    		
    		GaraType garaType = this.gestioneXMLIDGARACIGManager.getDatiGara(numgara, ufficio_id, ufficio_denominazione, 
    				azienda_codicefiscale, azienda_denominazione, false);
    		// set di altri dati della gara
    		HashMap<?,?> altriCampiDiGara = this.sqlManager.getHashMap(
    				"select ID_GARA, DATA_CREAZIONE, DATA_CONFERMA_GARA, DATA_TERMINE_PAGAMENTO, DATA_PERFEZIONAMENTO_BANDO from W3GARA where NUMGARA=?", 
    				new Object[] { numgara });
    		if (altriCampiDiGara != null && !altriCampiDiGara.isEmpty()) {
    			JdbcParametro jdbcParam = (JdbcParametro) altriCampiDiGara.get("ID_GARA");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				garaType.setIDGARA(Long.parseLong(jdbcParam.getStringValue()));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriCampiDiGara.get("DATA_CREAZIONE");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				garaType.setDATACREAZIONE(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriCampiDiGara.get("DATA_CONFERMA_GARA");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				garaType.setDATACONFERMAGARA(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriCampiDiGara.get("DATA_TERMINE_PAGAMENTO");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				garaType.setDATATERMINEPAGAMENTO(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriCampiDiGara.get("DATA_PERFEZIONAMENTO_BANDO");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				garaType.setDATAPERFEZIONAMENTOBANDO(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    		}
    		
    		LottoType lottoType = this.gestioneXMLIDGARACIGManager.getDatiLotto(numgara, numlott, false);
    		// set di altri campi del lotto
    		HashMap<?,?> altriDatiDelLotto = this.sqlManager.getHashMap(
    			"select CIG, DATA_CANCELLAZIONE_LOTTO, DATA_COMUNICAZIONE, DATA_CREAZIONE_LOTTO, DATA_INIB_PAGAMENTO, "
    			+ " DATA_PUBBLICAZIONE, DATA_SCADENZA_PAGAMENTI, LUOGO_ISTAT, LUOGO_NUTS from W3LOTT where NUMGARA=? and NUMLOTT=?",
    					new Object[] { numgara, numlott } );
    		
    		if (altriDatiDelLotto != null && !altriDatiDelLotto.isEmpty()) {
    			JdbcParametro jdbcParam = (JdbcParametro) altriDatiDelLotto.get("CIG");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				lottoType.setCIG(jdbcParam.getStringValue());
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriDatiDelLotto.get("DATA_CANCELLAZIONE_LOTTO");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				lottoType.setDATACANCELLAZIONELOTTO(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriDatiDelLotto.get("DATA_COMUNICAZIONE");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				lottoType.setDATACOMUNICAZIONE(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriDatiDelLotto.get("DATA_CREAZIONE_LOTTO");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				lottoType.setDATACREAZIONELOTTO(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriDatiDelLotto.get("DATA_INIB_PAGAMENTO");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				lottoType.setDATAINIBPAGAMENTO(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriDatiDelLotto.get("DATA_PUBBLICAZIONE");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				lottoType.setDATAPUBBLICAZIONE(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = null;
    			jdbcParam = (JdbcParametro) altriDatiDelLotto.get("DATA_SCADENZA_PAGAMENTI");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				lottoType.setDATASCADENZAPAGAMENTI(UtilityDateExtension.convertJdbcParametroDateToCalendar(jdbcParam));
    			}
    			jdbcParam = (JdbcParametro) altriDatiDelLotto.get("LUOGO_NUTS");
    			if (jdbcParam != null && jdbcParam.getValue() != null) {
    				lottoType.setLUOGONUTS(jdbcParam.getStringValue());
    			}
    		}
    		
    		Requisiti requisiti = this.gestioneXMLIDGARACIGManager.getDatiRequisiti(numgara, false);

    		SchedaType schedaDatiVigilanza = SchedaType.Factory.newInstance();
    		DatiGaraType datiGaraType = DatiGaraType.Factory.newInstance();
    		datiGaraType.addNewGara();
    		datiGaraType.setGara(garaType);
    		datiGaraType.addNewLotto();
    		datiGaraType.setLotto(lottoType);
    	  
    		if (requisiti != null && requisiti.getRequisitoArray().length > 0) {
    			datiGaraType.addNewRequisito();
    			datiGaraType.setRequisitoArray(requisiti.getRequisitoArray());
    		}
    		schedaDatiVigilanza.addNewDatiGara();
    		schedaDatiVigilanza.setDatiGara(datiGaraType);
    		
    		if (schedaDatiVigilanza != null) {
    			hMapConfrontaGaraLotto.put("schedaDatiVigilanza", schedaDatiVigilanza);
    		}
    		
    		this.preparaConfrontoDati(schedaDatiVigilanza, schedaDatiSIMOG, hMapConfrontaGaraLotto);
    	}
    } catch (XmlException xe) {
    	
    	
    } catch (SQLException se) {
    	logger.error("Si e' verificato un errore nell'estrazione dei dati da DB per il confronto dati del CIG " + cig, se);
    	
    } catch (GestoreException e) {
    	logger.error("Si e' verificato un errore nell'estrazione dei dati da DB per il confronto dati del CIG " + cig, e);
    	
    	throw e;
    } catch (ServiceException e) {
    	throw new GestoreException(
    			"Si e' verificato un errore durante la consultazione della gara",
    				"gestioneIDGARACIG.ws.error", e);
    } catch (RemoteException e) {
    	throw new GestoreException(
    			"Si e' verificato un errore durante l'interazione con il servizio di consultazione della gara",
    		  		"gestioneIDGARACIG.ws.remote.error", e);
	} catch (CriptazioneException e) {
	  throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
    	this.wsChiudiSessione(simogWS, hMapwsLogin);
	}

	if (logger.isDebugEnabled())
    	logger.debug("confrontaGaraLotto: fine metodo");

    return hMapConfrontaGaraLotto;

  }
  
  private void preparaConfrontoDati(final SchedaType schedaDatiVigilanza, final SchedaType schedaDatiSIMOG, HashMap<String, Object> hMapConfrontaGaraLotto) {
	 
	  String sqlEstrazioneCampiSimog = "select FASE_SIMOG,CAMPO_SIMOG,SEZIONE,DESCRIZIONE_CAMPO,TIPO,TABELLATO from DIZIONARIO_CAMPI_SIMOG where MODULO=? "
	  		+ " and FASE_ORD=? and ATTIVO > 0 order by SEZIONE_ORD asc, CAMPO_ORD asc";
	  
	  List<Vector<String>> listaCampiAnagraficaGara = this.confrontoDati(schedaDatiVigilanza.getDatiGara().getGara(), 
			  schedaDatiSIMOG.getDatiGara().getGara(),sqlEstrazioneCampiSimog, new Object[] { "W3", 1});
	  
	  if (listaCampiAnagraficaGara == null) {
		  listaCampiAnagraficaGara = new ArrayList<Vector<String>>();
		  Vector<String> vector = new Vector<String>();
		  vector.add("Errore nel confronto dei dati dell'anagrafica di gara");
		  vector.add("");
		  vector.add("");
		  vector.add("");
		  vector.add("");
		  listaCampiAnagraficaGara.add(vector);
	  }
	  
	  hMapConfrontaGaraLotto.put("listaCampiAnagraficaGara", listaCampiAnagraficaGara);
	  
	  List<String> elencoCategorieVigilanza = new ArrayList<String>();
 
	  ElencoCategMercType elencoCategorieMerceologicheVigilanza = schedaDatiVigilanza.getDatiGara().getGara().getCATEGORIEMERC();
	  if (elencoCategorieMerceologicheVigilanza != null && elencoCategorieMerceologicheVigilanza.getCATEGORIAArray() != null 
	   && elencoCategorieMerceologicheVigilanza.getCATEGORIAArray().length > 0) {
	    for(String categoria: elencoCategorieMerceologicheVigilanza.getCATEGORIAArray()) {
	         elencoCategorieVigilanza.add(this.tabellatiManager.getDescrTabellato("W3031",categoria));
	       }
	  }
	  Collections.sort(elencoCategorieVigilanza);  
	  hMapConfrontaGaraLotto.put("listaCategorieMerceologicheVigilanza", elencoCategorieVigilanza); 
	  
	  List<String> elencoCategorieSIMOG = new ArrayList<String>();
	  
	  ElencoCategMercType elencoCategorieMerceologicheSIMOG = schedaDatiSIMOG.getDatiGara().getGara().getCATEGORIEMERC();
      if (elencoCategorieMerceologicheSIMOG != null && elencoCategorieMerceologicheSIMOG.getCATEGORIAArray() != null 
       && elencoCategorieMerceologicheSIMOG.getCATEGORIAArray().length > 0) {
        for(String categoria: elencoCategorieMerceologicheSIMOG.getCATEGORIAArray()) {
          elencoCategorieSIMOG.add(this.tabellatiManager.getDescrTabellato("W3031",categoria));
        }
      }

      Collections.sort(elencoCategorieSIMOG);   
      hMapConfrontaGaraLotto.put("listaCategorieMerceologicheSIMOG", elencoCategorieSIMOG); 

	  LottoType anagraficaLottoVigilanza = null;
	  if (schedaDatiVigilanza.getDatiGara().isSetLotto()) {
		  anagraficaLottoVigilanza = schedaDatiVigilanza.getDatiGara().getLotto();
	  }
	  
	  LottoType anagraficaLottoSIMOG = null;
	  if (schedaDatiSIMOG.getDatiGara().isSetLotto()) {
		  anagraficaLottoSIMOG = schedaDatiSIMOG.getDatiGara().getLotto();
	  }
	  
	  List<Vector<String>> listaCampiAnagraficaLotto = this.confrontoDati(anagraficaLottoVigilanza, 
			  anagraficaLottoSIMOG, sqlEstrazioneCampiSimog, new Object[] { "W3", 2});
	  
	  if (anagraficaLottoVigilanza.getCUPLOTTO() != null && anagraficaLottoVigilanza.getCUPLOTTO().getCODICICUPArray() != null 
			  && anagraficaLottoVigilanza.getCUPLOTTO().getCODICICUPArray().length > 0 ) {
		  anagraficaLottoVigilanza.getCUPLOTTO().getCIG();
		  List<String> listaCodiciCUPVigilanza = new ArrayList<String>();
		  for (int i=0; i < anagraficaLottoVigilanza.getCUPLOTTO().getCODICICUPArray().length; i++) {
			  listaCodiciCUPVigilanza.add(anagraficaLottoVigilanza.getCUPLOTTO().getCODICICUPArray(i).getCUP());
		  }
	  }
	  
	  if (listaCampiAnagraficaLotto == null) {
		  listaCampiAnagraficaLotto = new ArrayList<Vector<String>>();
		  Vector<String> vector = new Vector<String>();
		  vector.add("Errore nel confronto dei dati dell'anagrafica del lotto");
		  vector.add("");
		  vector.add("");
		  vector.add("");
		  vector.add("");
		  listaCampiAnagraficaLotto.add(vector);
	  }
	  
	  hMapConfrontaGaraLotto.put("listaCampiAnagraficaLotto", listaCampiAnagraficaLotto);
	  
	  // Ulteriori categorie sui dati di Vigilanza
	  if (anagraficaLottoVigilanza.getCATEGORIE() != null && anagraficaLottoVigilanza.getCATEGORIE().getCATEGORIAArray() != null 
			  && anagraficaLottoVigilanza.getCATEGORIE().getCATEGORIAArray().length > 0) {
		  List<String> listaUlterioriCategorieVigilanza = new ArrayList<String>();
		  String[] arrayUlterioriCategorie = anagraficaLottoVigilanza.getCATEGORIE().getCATEGORIAArray();
		  for (int i = 0; i < arrayUlterioriCategorie.length; i++) {
		    try {
		      listaUlterioriCategorieVigilanza.add(this.tabellatiManager.getDescrTabellato("W3z03", UtilitySITAT.getCategoriaSITAT(this.sqlManager, arrayUlterioriCategorie[i])));
            } catch (SQLException e) {
              listaUlterioriCategorieVigilanza.add(arrayUlterioriCategorie[i]);
            }
		  }
		  hMapConfrontaGaraLotto.put("listaUlterioriCategorieVigilanza", listaUlterioriCategorieVigilanza);
	  }

	  // Ulteriori categorie sui dati di SIMOG
	  if (anagraficaLottoSIMOG.getCATEGORIE() != null && anagraficaLottoSIMOG.getCATEGORIE().getCATEGORIAArray() != null 
			  && anagraficaLottoSIMOG.getCATEGORIE().getCATEGORIAArray().length > 0) {
		  List<String> listaUlterioriCategorieSIMOG = new ArrayList<String>();
		  String[] arrayUlterioriCategorie = anagraficaLottoSIMOG.getCATEGORIE().getCATEGORIAArray();
		  for (int i = 0; i < arrayUlterioriCategorie.length; i++) {
		    try {
		      listaUlterioriCategorieSIMOG.add(this.tabellatiManager.getDescrTabellato("W3z03", UtilitySITAT.getCategoriaSITAT(this.sqlManager, arrayUlterioriCategorie[i])));
            } catch (SQLException e) {
              listaUlterioriCategorieSIMOG.add(arrayUlterioriCategorie[i]);
            }
		  }
		  hMapConfrontaGaraLotto.put("listaUlterioriCategorieSIMOG", listaUlterioriCategorieSIMOG);
	  }
	  
	  // Condizioni per procedura negoziata sui dati di Vigilanza 
	  if (anagraficaLottoVigilanza.getCondizioniArray() != null && anagraficaLottoVigilanza.getCondizioniArray().length > 0) {
		  List<String> listaCondizioniVigilanza = new ArrayList<String>();
		  CondizioneLtType[] arrayCondizioni = anagraficaLottoVigilanza.getCondizioniArray();
		  for (int i = 0; i < arrayCondizioni.length; i++) {
			  listaCondizioniVigilanza.add(this.tabellatiManager.getDescrTabellato("W3006", arrayCondizioni[i].getIDCONDIZIONE()));
		  }
		  hMapConfrontaGaraLotto.put("listaCondizioniVigilanza", listaCondizioniVigilanza);
	  }

	  // Condizioni per procedura negoziata sui dati di SIMOG
	  if (anagraficaLottoSIMOG.getCondizioniArray() != null && anagraficaLottoSIMOG.getCondizioniArray().length > 0) {
		  List<String> listaCondizioniSIMOG = new ArrayList<String>();
		  CondizioneLtType[] arrayCondizioni = anagraficaLottoSIMOG.getCondizioniArray();
		  for (int i = 0; i < arrayCondizioni.length; i++) {
			  listaCondizioniSIMOG.add(this.tabellatiManager.getDescrTabellato("W3006", arrayCondizioni[i].getIDCONDIZIONE()));
		  }
		  hMapConfrontaGaraLotto.put("listaCondizioniSIMOG", listaCondizioniSIMOG);
	  }
	  
	  // CPV secondari sui dati di Vigilanza
	  if (anagraficaLottoVigilanza.getCPVSecondariaArray() != null && anagraficaLottoVigilanza.getCPVSecondariaArray().length > 0) {
		  List<String> listaCPVSecondariVigilanza = new ArrayList<String>();
		  CPVSecondariaType[] arrayCpvSecondari = anagraficaLottoVigilanza.getCPVSecondariaArray();
		  for (int i = 0; i < arrayCpvSecondari.length; i++) {
			  listaCPVSecondariVigilanza.add(arrayCpvSecondari[i].getCODCPVSECONDARIA());
		  }
		  hMapConfrontaGaraLotto.put("listaCPVSecondariVigilanza", listaCPVSecondariVigilanza);
	  }

	  // CPV secondari sui dati di SIMOG
	  if (anagraficaLottoSIMOG.getCPVSecondariaArray() != null && anagraficaLottoSIMOG.getCPVSecondariaArray().length > 0) {
		  List<String> listaCPVSecondariSIMOG = new ArrayList<String>();
		  CPVSecondariaType[] arrayCpvSecondari = anagraficaLottoSIMOG.getCPVSecondariaArray();
		  for (int i = 0; i < arrayCpvSecondari.length; i++) {
			  listaCPVSecondariSIMOG.add(arrayCpvSecondari[i].getCODCPVSECONDARIA());
		  }
		  hMapConfrontaGaraLotto.put("listaCPVSecondariSIMOG", listaCPVSecondariSIMOG);
	  }
	  
	  // Tipi appalto per forniture e servizi sui dati di Vigilanza
	  List<String> listaTipiAppaltoVigilanza = null;
	  if (anagraficaLottoVigilanza.getTipiAppaltoFornArray() != null && anagraficaLottoVigilanza.getTipiAppaltoFornArray().length > 0) {
		  listaTipiAppaltoVigilanza = new ArrayList<String>();
		  TipiAppaltoType[] arrayTipiAppaltoForniture = anagraficaLottoVigilanza.getTipiAppaltoFornArray();
		  for (int i = 0; i < arrayTipiAppaltoForniture.length; i++) {
			  listaTipiAppaltoVigilanza.add(this.tabellatiManager.getDescrTabellato("W3019", arrayTipiAppaltoForniture[i].getIDAPPALTO()));
		  }
	  }
	  
	  // Tipi appalto per lavori sui dati di Vigilanza
	  if (anagraficaLottoVigilanza.getTipiAppaltoLavArray() != null && anagraficaLottoVigilanza.getTipiAppaltoLavArray().length > 0) {
		 if (listaTipiAppaltoVigilanza == null) {
			 listaTipiAppaltoVigilanza = new ArrayList<String>();
		 }
		 TipiAppaltoType[] arrayTipiAppalto = anagraficaLottoVigilanza.getTipiAppaltoLavArray();
		  for (int i = 0; i < arrayTipiAppalto.length; i++) {
			  listaTipiAppaltoVigilanza.add(this.tabellatiManager.getDescrTabellato("W3002", arrayTipiAppalto[i].getIDAPPALTO()));
		  }
	  }

	  if (listaTipiAppaltoVigilanza != null && listaTipiAppaltoVigilanza.size() > 0) {
		  hMapConfrontaGaraLotto.put("listaTipiAppaltoVigilanza", listaTipiAppaltoVigilanza);
	  }
	  
	  // Tipi appalto per forniture e servizi sui dati di SIMOG
	  List<String> listaTipiAppaltoSIMOG = null;
	  if (anagraficaLottoSIMOG.getTipiAppaltoFornArray() != null && anagraficaLottoSIMOG.getTipiAppaltoFornArray().length > 0) {
		  listaTipiAppaltoSIMOG = new ArrayList<String>();
		  TipiAppaltoType[] arrayTipiAppalto = anagraficaLottoSIMOG.getTipiAppaltoFornArray();
		  for (int i = 0; i < arrayTipiAppalto.length; i++) {
			  listaTipiAppaltoSIMOG.add(this.tabellatiManager.getDescrTabellato("W3019", arrayTipiAppalto[i].getIDAPPALTO()));
		  }
	  }
	  
	  // Tipi appalto per lavori sui dati di SIMOG	  
	  if (anagraficaLottoSIMOG.getTipiAppaltoLavArray() != null && anagraficaLottoSIMOG.getTipiAppaltoLavArray().length > 0) {
		 if (listaTipiAppaltoSIMOG == null) {
			 listaTipiAppaltoSIMOG = new ArrayList<String>();
		 }
		 TipiAppaltoType[] arrayTipiAppalto = anagraficaLottoSIMOG.getTipiAppaltoLavArray();
		  for (int i = 0; i < arrayTipiAppalto.length; i++) {
			  listaTipiAppaltoSIMOG.add(this.tabellatiManager.getDescrTabellato("W3002", arrayTipiAppalto[i].getIDAPPALTO()));
		  }
	  }
	  
	  if (listaTipiAppaltoSIMOG != null && listaTipiAppaltoSIMOG.size() > 0) {
		  hMapConfrontaGaraLotto.put("listaTipiAppaltoSIMOG", listaTipiAppaltoSIMOG);
	  }
	  
	  // Motivi deroga sui dati di Vigilanza
      if (anagraficaLottoVigilanza.getMotivoDerogaArray() != null && anagraficaLottoVigilanza.getMotivoDerogaArray().length > 0) {
          List<String> listaMotiviDerogaVigilanza = new ArrayList<String>();
          String[] arrayMotiviDeroga = anagraficaLottoVigilanza.getMotivoDerogaArray();
          for (int i = 0; i < arrayMotiviDeroga.length; i++) {
            listaMotiviDerogaVigilanza.add(this.tabellatiManager.getDescrTabellato("W3040", arrayMotiviDeroga[i]));
          }
          hMapConfrontaGaraLotto.put("listaMotiviDerogaVigilanza", listaMotiviDerogaVigilanza);
      }

      // CPV secondari sui dati di SIMOG
      if (anagraficaLottoSIMOG.getMotivoDerogaArray() != null && anagraficaLottoSIMOG.getMotivoDerogaArray().length > 0) {
          List<String> listaMotiviDerogaSIMOG = new ArrayList<String>();
          String[] arrayMotiviDeroga = anagraficaLottoSIMOG.getMotivoDerogaArray();
          for (int i = 0; i < arrayMotiviDeroga.length; i++) {
              listaMotiviDerogaSIMOG.add(this.tabellatiManager.getDescrTabellato("W3040", arrayMotiviDeroga[i]));
          }
          hMapConfrontaGaraLotto.put("listaMotiviDerogaSIMOG", listaMotiviDerogaSIMOG);
      }
      
      // Misure premiali sui dati di Vigilanza
      if (anagraficaLottoVigilanza.getMisuraPremialeArray() != null && anagraficaLottoVigilanza.getMisuraPremialeArray().length > 0) {
          List<String> listaMisurePremialiVigilanza = new ArrayList<String>();
          String[] arrayMisurePremiali = anagraficaLottoVigilanza.getMisuraPremialeArray();
          for (int i = 0; i < arrayMisurePremiali.length; i++) {
            listaMisurePremialiVigilanza.add(this.tabellatiManager.getDescrTabellato("W3039", arrayMisurePremiali[i]));
          }
          hMapConfrontaGaraLotto.put("listaMisurePremialiVigilanza", listaMisurePremialiVigilanza);
      }

      // Misure premiali sui dati di SIMOG
      if (anagraficaLottoSIMOG.getMisuraPremialeArray() != null && anagraficaLottoSIMOG.getMisuraPremialeArray().length > 0) {
          List<String> listaMisurePremialiSIMOG = new ArrayList<String>();
          String[] arrayMisurePremiali = anagraficaLottoSIMOG.getMisuraPremialeArray();
          for (int i = 0; i < arrayMisurePremiali.length; i++) {
            listaMisurePremialiSIMOG.add(this.tabellatiManager.getDescrTabellato("W3039", arrayMisurePremiali[i]));
          }
          hMapConfrontaGaraLotto.put("listaMisurePremialiSIMOG", listaMisurePremialiSIMOG);
      }

	  
	  //Confronto dei requisiti
	  
	  if(schedaDatiSIMOG.getDatiGara().getGara().isSetDATAPERFEZIONAMENTOBANDO()) {
	   ReqGaraType[] anagraficaReqGaraVigilanza = schedaDatiVigilanza.getDatiGara().getRequisitoArray();
	      
	   ReqGaraType[] anagraficaReqGaraSimog = schedaDatiSIMOG.getDatiGara().getRequisitoArray();
	      
	   List<String> codiciDettaglio = new ArrayList<String>();
	      
	   Map<String,ReqGaraType> reqGaraVigilanza = new HashMap<String, ReqGaraType>();

	   for(ReqGaraType req : anagraficaReqGaraVigilanza) {
	     String codiceDettaglio = req.getCodiceDettaglio();
	      reqGaraVigilanza.put(codiceDettaglio, req);
	      if(!codiciDettaglio.contains(codiceDettaglio)) {
	        codiciDettaglio.add(codiceDettaglio);
	      }
	    }
	      
	    Map<String,ReqGaraType> reqGaraSIMOG = new HashMap<String, ReqGaraType>();
	      
	    for(ReqGaraType req : anagraficaReqGaraSimog) {
	      String codiceDettaglio = req.getCodiceDettaglio();
	      reqGaraSIMOG.put(codiceDettaglio, req);
	      if(codiciDettaglio.contains(codiceDettaglio)) {
	        codiciDettaglio.add(codiceDettaglio);
	      }
	    }
	    
	    Map<String, List<Vector<String>>> listaConfrontoDatiRequisiti = new HashMap<String, List<Vector<String>>>();
	    
	    for(String i : codiciDettaglio) {
	      String descrizione = i;
	      try {
	        descrizione = (String) this.sqlManager.getObject("select DESCRIZIONE from W3TABREQ where CODICE_DETTAGLIO=?", new Object[] {i} );
          } catch (NumberFormatException e) {} 
	        catch (SQLException e) {}
	       listaConfrontoDatiRequisiti.put(descrizione,this.confrontoDati(reqGaraVigilanza.get(i), 
	          reqGaraSIMOG.get(i) , sqlEstrazioneCampiSimog, new Object[] { "W3", 3}));
	    }
	    hMapConfrontaGaraLotto.put("listaConfrontoDatiRequisiti", listaConfrontoDatiRequisiti);   
	  }
	 }
  

  private List<Vector<String>> confrontoDati(final Object datiVigilanza, final Object datiSIMOG, final String sqlEstrazioneCampiSimog, 
		  final Object[] sqlParam) {
	
	  List<Vector<String>> listaCampiAConfronto = null;
	  List<?> listaCampiDizionarioSIMOG = null;

	  try {
		  listaCampiDizionarioSIMOG = this.sqlManager.getListVector(sqlEstrazioneCampiSimog, sqlParam);
	  } catch (SQLException se) {
		  logger.error("Errore nell'estrazione dei dati dalla tabella DIZIONARIO_CAMPI_SIMOG", se);
	  }

	  if (listaCampiDizionarioSIMOG != null && listaCampiDizionarioSIMOG.size() > 0) {
		  listaCampiAConfronto = new ArrayList<Vector<String>>();
		  Iterator<?> iter = listaCampiDizionarioSIMOG.iterator();
		  
		  while (iter.hasNext()) {
			  Vector<?> campiAnagraficaSIMOG = (Vector<?>) iter.next();
			  String faseSimog = SqlManager.getValueFromVectorParam(campiAnagraficaSIMOG, 0).getStringValue();
			  String nomeCampo = SqlManager.getValueFromVectorParam(campiAnagraficaSIMOG, 1).getStringValue();
			  String sezione = SqlManager.getValueFromVectorParam(campiAnagraficaSIMOG, 2).getStringValue();
			  String descrizioone = SqlManager.getValueFromVectorParam(campiAnagraficaSIMOG, 3).getStringValue();
			  String tipo = SqlManager.getValueFromVectorParam(campiAnagraficaSIMOG, 4).getStringValue();
			  String tabellato = SqlManager.getValueFromVectorParam(campiAnagraficaSIMOG, 5).getStringValue();
			  
			  String valoreCampoVigilanza = null;
			  String valoreCampoSIMOG = null;
			  
			  if (datiVigilanza != null) {
				  try {
					  valoreCampoVigilanza = this.getValoreCampo(datiVigilanza, nomeCampo, tipo, tabellato);
				  } catch (Exception e) {
					  logger.error("Errore nella lettura del campo " + nomeCampo + " dai dati di Vigilanza", e);
					  valoreCampoVigilanza = "Errore nella lettura del campo";
				  }
			  }
			  
			  if (datiSIMOG != null) {
				  try {
					  valoreCampoSIMOG = this.getValoreCampo(datiSIMOG, nomeCampo, tipo, tabellato);
				  	
					  if (StringUtils.isNotEmpty(valoreCampoSIMOG) && valoreCampoSIMOG.indexOf("@") >= 0) {
						  if (StringUtils.isNotEmpty(sezione)) {
							  logger.error("Problemi nella lettura del valore del campo " + nomeCampo + "della fase " + faseSimog + " e sezione " + sezione);
						  } else {
							  logger.error("Problemi nella lettura del valore del campo " + nomeCampo + "della fase " + faseSimog);
						  }
					  }
				  } catch (Exception e) {
					  logger.error("Errore nella lettura del campo " + nomeCampo + " dal XML proveniente SIMOG", e);
					  valoreCampoSIMOG = "Errore nella lettura del campo";
				  }
			  }
			  
			  Vector<String> campoAConfronto = new Vector<String>();
			  campoAConfronto.add(faseSimog);
			  campoAConfronto.add(sezione);
			  campoAConfronto.add(descrizioone);
			  campoAConfronto.add(valoreCampoVigilanza);
			  campoAConfronto.add(valoreCampoSIMOG);
			  
			  listaCampiAConfronto.add(campoAConfronto);
		  }
	  }
	  
	  return listaCampiAConfronto;
  }

	
	/** 
	 * 
	 * @param obj
	 * @param nomeCampo
	 * @param tipoCampo
	 * @param tabellato
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private String getValoreCampo(Object obj, String nomeCampo, String tipoCampo, String tabellato) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		String valoreCampo = null;
		
		// Dal nome del campo si rimuovono gli underscore
		String tmpNomeCampo = StringUtils.remove(nomeCampo, "_");
		
		// Per reflection si invoca il metodo get
		Method metodoGet = obj.getClass().getDeclaredMethod("get".concat(tmpNomeCampo), new Class[] {} );
		Object result = metodoGet.invoke(obj, new Object[] {});

		if (result != null && !StringUtils.equals("", result.toString())) {
			if (StringUtils.isEmpty(tipoCampo)) {
				// gestisco il campo come una stringa, anche se numerico
				if (StringUtils.isNotEmpty(tabellato)) {
					// carico la descrizione del tabellato
				    if (tabellato.equals("W3z03")) {
      				   try {
      				     valoreCampo = this.tabellatiManager.getDescrTabellato(tabellato, UtilitySITAT.getCategoriaSITAT(this.sqlManager,result.toString()));
                       } catch (SQLException e) {
                         valoreCampo = result.toString();
                       }
				    }  else {
				      valoreCampo = this.tabellatiManager.getDescrTabellato(tabellato, result.toString());
				    }
				} else {
					if (result != null) { 
						valoreCampo = result.toString();
					}
				}
			} else if ("MONEY".equalsIgnoreCase(tipoCampo)) {
				if (result instanceof BigDecimal) {
					valoreCampo  = UtilityNumeri.convertiImporto(((BigDecimal) result).doubleValue(),2);
				} else if (result instanceof Double) {
					valoreCampo = UtilityNumeri.convertiImporto((Double) result, 2);
				} else try {
				  valoreCampo = UtilityNumeri.convertiImporto(Double.parseDouble((String) result),2) ;
				} catch(Exception e) {
				  valoreCampo = result.toString();
				}
				if ("-1.000".equals(result.toString())) {
                  valoreCampo = "N.D.";
				}
			} else if ("DATA".equalsIgnoreCase(tipoCampo)) {
				if (result instanceof Calendar) {
					Date tempDate = new Date(((Calendar) result).getTimeInMillis());
					valoreCampo = UtilityDate.convertiData(tempDate, UtilityDate.FORMATO_GG_MM_AAAA);
				} else if (result instanceof java.util.Date) {
					Date tempDate = new Date(((java.util.Date) result).getTime());
					valoreCampo = UtilityDate.convertiData(tempDate, UtilityDate.FORMATO_GG_MM_AAAA);
				} else if (result instanceof java.sql.Date) {
					Date tempDate = new Date(((java.sql.Date) result).getTime());
					valoreCampo = UtilityDate.convertiData(tempDate, UtilityDate.FORMATO_GG_MM_AAAA);
				} else if (result instanceof Timestamp) {
					Date tempDate = new Date(((Timestamp) result).getTime());
					valoreCampo = UtilityDate.convertiData(tempDate, UtilityDate.FORMATO_GG_MM_AAAA);
				} else {
					valoreCampo = result.toString();
				}
			} else if ("SN".equalsIgnoreCase(tipoCampo)) {
				if ("S".equalsIgnoreCase(result.toString()) || "1".equalsIgnoreCase(result.toString())) {
					valoreCampo = "Si";
				} else {
					valoreCampo = "No";
				}
			} else if ("ORA".equalsIgnoreCase(tipoCampo) || "PRC".equalsIgnoreCase(tipoCampo)) {
				valoreCampo = result.toString();
			} else {
				valoreCampo = result.toString();
			}
		}
		
		return valoreCampo;
	}


  /**
   * Restituisce la validita' del CIG
   *
   * @param codiceCIG cig
   * @return validita'(-1:cig non valido, 0:cig, 1:smartcig)
   */
  private int controlloCIG(final String codiceCIG) {

	    String strC1_7 = "";// primi 7 caratteri
	    String strC4_10 = "";// dal 4 al 10 carattere
	    String strK = ""; //Firma
	    long nDecStrK_chk = 0;//Firma decimale
	    long nDecStrK = 0;//controllo della firma decimale
	    int result = -1;

	    if ("".equals(codiceCIG) || codiceCIG.length() != 10 || "0000000000".equals(codiceCIG)) {
	      //Errori di struttura
	      return result;
	    }
	    //Verifico se si tratta di cig o smart cig
	    String strC1 = "" + codiceCIG.charAt(0); //Estraggo il primo carattere
	    if(StringUtils.isNumeric(strC1)){

	    //CIG
	      try {
	        strK = codiceCIG.substring(7,10); //Estraggo la firma
	        nDecStrK = Integer.parseInt (strK, 16); //trasformo in decimale
	        strC1_7 = codiceCIG.substring(0,7); //Estraggo la parte significativa
	        long nStrC1 = Integer.parseInt(strC1_7);
	         //Calcola Firma
	        nDecStrK_chk = ((nStrC1 * 1/1) * 211 % 4091);
	        result = 0;
	      }catch(Exception e){
	          //Impossibile calcolare la firma
	          return result;
	      }

	    }else{

	      //SMART CIG
	      if(!strC1.equals("X") && !strC1.equals("Z") && !strC1.equals("Y")){
	        return result;
	      }
	      try {
	        strK=codiceCIG.substring(1,3);//Estraggo la firma
	        nDecStrK = Integer.parseInt (strK, 16); //trasformo in decimale
	        strC4_10 = codiceCIG.substring(3,10);
	        long nDecStrC4_10 = Integer.parseInt (strC4_10, 16); //trasformo in decimale
	        //Calcola Firma
	        nDecStrK_chk = ((nDecStrC4_10 * 1/1) * 211 % 251);
	        result = 1;
	      }catch(Exception e){
	        //Impossibile calcolare la firma
	        return result;
	      }
	    }

	    if (nDecStrK_chk != nDecStrK) {
	      //La firma non coincide
	      return result;
	    }

	    return result;

	  }
  
  /**
   * Richiesta di cancellazione della gara già assegnata
   *
   * @param simogwsuser
   * @param simogwspass
   * @param numgara
   * @param idmotivazione
   * @param notecanc
   * @throws GestoreException
   */
  public void cancellaGARA(String simogwsuser, String simogwspass,
      Long numgara, String idmotivazione, String notecanc, boolean rpntFailed)
      throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("cancellaGARA: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3GARA");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3GARA");
      
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from w3gara left join uffint on w3gara.codein=uffint.codein where w3gara.numgara = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);

      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	  String idgara = (String) this.sqlManager.getObject(
    	          "select id_gara from w3gara where numgara = ?",
    	          new Object[] { numgara });
    	      this.wsCancellaGara(simogWS, hMapwsLogin, idgara, idmotivazione, notecanc);
    	      this.aggiornaW3GARASTATO(numgara, 6L);
    	      this.aggiornaGARACancellazione("Cig",numgara);
    	      this.aggiornaW3GARACancellazione(numgara, idmotivazione, notecanc);
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
      
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("cancellaGARA: fine metodo");

  }

  /**
   * Aggiornamento dell'identificativo IDGARA in base dati
   *
   * @param numgara
   * @param idgara
   * @throws GestoreException
   */
  public void aggiornaW3GARACIG(Long numgara, String idgara)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3GARACIG: inizio metodo");

    try {
      String updateW3GARA = "update w3gara set id_gara = ?, data_creazione = ?, stato_simog = ? where numgara = ?";
      this.sqlManager.update(updateW3GARA, new Object[] { idgara, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), 2L, numgara });

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3GARACIG: fine metodo");
  }
  
  public void aggiornaW3GARACIGMassivo(Long numgara, String idgara) throws GestoreException {

	  if (logger.isDebugEnabled())
		  logger.debug("aggiornaW3GARACIG: inizio metodo");
	  
	  TransactionStatus status = null;
	  try {
		  status = this.sqlManager.startTransaction();
		  String updateW3GARA = "update w3gara set id_gara = ?, data_creazione = ?, stato_simog = ? where numgara = ?";
		  this.sqlManager.update(updateW3GARA, new Object[] { idgara, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), 2L, numgara });
	  } catch (SQLException e) {
		  throw new GestoreException(
			"Si e' verificato un errore durante l'interazione con la base dati",
			"gestioneIDGARACIG.sqlerror", e);
	  } finally {
		  try {
			  this.sqlManager.commitTransaction(status);
		  } catch (SQLException e) {
			  e.printStackTrace();
		  }
	  }

	  if (logger.isDebugEnabled())
		  logger.debug("aggiornaW3GARACIG: fine metodo");
  }
  
  /**
   * Aggiornamento dell'identificativo SMARTCIG in base dati
   *
   * @param numgara
   * @param smartCig
   * @throws GestoreException
   */
  public void aggiornaW3SMARTCIG(Long numgara, String smartCig) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3SMARTCIG: inizio metodo");

    try {
      String updateW3GARA = "update W3SMARTCIG set CIG = ?, DATA_OPERAZIONE = ?, STATO = ? where CODRICH = ?";
      this.sqlManager.update(updateW3GARA, new Object[] { smartCig, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), 2L, numgara });

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3SMARTCIG: fine metodo");
  }
  
  /**
   * Aggiornamento dello stato SMARTCIG in base dati a seguito dell'annullamento
   *
   * @param numgara
   * @param smartCig
   * @throws GestoreException
   */
  public void annullaW3SMARTCIG(Long numgara)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("annullaW3SMARTCIG: inizio metodo");

    try {
      String updateW3GARA = "update W3SMARTCIG set STATO = ?, CIG = null, DATA_OPERAZIONE = null where CODRICH = ?";
      String updateG1STIPULA = "update g1stipula set cigvar = null where ngaravar = ?";
      
	  // ngara della gara
	  String codgar = (String) sqlManager.getObject("select codgar from W3SMARTCIG where CODRICH = ?",
	          new Object[] { numgara });
	  if(codgar != null) {
    	  String ngara = codgar.substring(codgar.indexOf("$")+1);
          
    	  //verifico se si tratta di una richiesta CIG collegato
          String ngaraCollegata = (String) sqlManager.getObject(
                  "select ngara from G1STIPULA where ngaravar = ? ",
                  new Object[] { ngara });
          
          if(!"".equals(ngaraCollegata) && ngaraCollegata != null) {
        	  this.sqlManager.update(updateG1STIPULA, new Object[] { ngara });
          }
          
          this.sqlManager.update(updateW3GARA, new Object[] { 1L, numgara });
		}
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("annullaW3SMARTCIG: fine metodo");

  }
  /**
   * Aggiornamento dello stato della gara
   *
   * @param numgara
   * @param stato
   * @throws GestoreException
   */
  public void aggiornaW3GARASTATO(Long numgara, Long stato)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3GARASTATO: inizio metodo");

    try {
    	// Importo della gara
        Object importoObj = sqlManager.getObject("select SUM(" + sqlManager.getDBFunction("isnull", new String[] {"IMPORTO_LOTTO", "0.00" }) + ") from W3LOTT where NUMGARA=? and STATO_SIMOG in (1,2,3,4,7,99)", 
            new Object[] { numgara });

        Double importo = 0D;
        if (importoObj != null) {
              if (!(importoObj instanceof Double)) {
                importo = Double.parseDouble(importoObj.toString());
              } else {
                importo = (Double) importoObj;
              }
            } 
    	Long numero_lotti = (Long) this.sqlManager.getObject("select count(*) from w3lott where numgara = ? and stato_simog in (1,2,3,4,7,99)", new Object[] { numgara });
        
    	String updateW3GARA = "update w3gara set stato_simog = ?, importo_gara = ?, numero_lotti = ? where numgara = ?";
    	this.sqlManager.update(updateW3GARA, new Object[] { stato, importo, numero_lotti, numgara });

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3GARASTATO: fine metodo");

  }

  /**
   * Aggiornamento cigvar ed ngaravar in caso di gara collegata
   *
   * @param numgara
   * @param stato
   * @throws GestoreException
   */
  public void aggiornaGaraCollegataLotto(Long numgara)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaGaraCollegataLotto: inizio metodo");

    try {
    	
    	String updateG1STIPULA = "update g1stipula set cigvar=null where ngaravar = ? ";
    	
		// ngara della gara
		String ngara = (String) sqlManager.getObject("select ngara from W3LOTT where NUMGARA=?",
				new Object[] { numgara });

		// verifico se si tratta di una richiesta CIG collegato
		String ngaraCollegata = (String) sqlManager.getObject("select ngara from G1STIPULA where ngaravar = ? ",
				new Object[] { ngara });

		if (!"".equals(ngaraCollegata) && ngaraCollegata != null) {
			this.sqlManager.update(updateG1STIPULA, new Object[] { ngara });
		}

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaGaraCollegataLotto: fine metodo");

  }
  
  /**
   * Aggiornamento dei motivi della cancellazione per la gara
   *
   * @param numgara
   * @param idmotivazione
   * @param notecanc
   * @throws GestoreException
   */
  public void aggiornaW3GARACancellazione(Long numgara, String idmotivazione,
      String notecanc) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3GARACancellazione: inizio metodo");

    try {
      String updateW3GARA = "update w3gara set id_motivazione = ?, note_canc = ?, data_cancellazione_gara = ? where numgara = ?";
      this.sqlManager.update(updateW3GARA,
          new Object[] { idmotivazione, notecanc,
              new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
              numgara });

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3GARACancellazione: fine metodo");

  }

  /**
   *  Scollegamento della gara a seguito della cancellazione logica
   *
   * @param tipoRichiesta (Cig o Smart Cig)
   * @param numgara
   * @throws GestoreException
   */
  public void aggiornaGARACancellazione(String tipoRichiesta, Long numgara) throws GestoreException {

	  if (logger.isDebugEnabled())
      logger.debug("aggiornaGARACancellazione: inizio metodo");

    try {
      String codgar= null;
      String updateTORN = "update torn set numavcp = null where codgar = ?";
      String updateGARE = "update gare set codcig = null where codgar1 = ?";
      if("Cig".equals(tipoRichiesta)) {
    	  codgar = (String) this.sqlManager.getObject("select codgar from w3gara where numgara = ?", new Object[] { numgara });
    	  this.sqlManager.update(updateTORN, new Object[] { codgar });
      }else {
    	  if("Scig".equals(tipoRichiesta)) {
    		  codgar = (String) this.sqlManager.getObject("select codgar from w3smartcig where codrich = ?", new Object[] { numgara });
    		  this.sqlManager.update(updateGARE, new Object[] { codgar });
    	  }
      }
      
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaGARACancellazione: fine metodo");

  }

  /**
   * Verifica l'esistenza di un lotto con un determinato CIG
   *
   * @param cig
   * @return
   * @throws GestoreException
   */
  public boolean esisteCIG(String cig) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("esisteCIG: inizio metodo");

    boolean esisteCIG = false;

    try {
    	Long conteggioCig = null;
    	Long conteggioSmartCig = null;
    	if (cig != null && !cig.equals("")) {
    		conteggioCig = (Long) this.sqlManager.getObject(
    		          "select count(*) from w3lott where cig = ?", new Object[] { cig });
    		conteggioSmartCig = (Long) this.sqlManager.getObject(
  		          "select count(*) from w3smartcig where cig = ?", new Object[] { cig });
    	} 
    	if ((conteggioCig != null && conteggioCig.longValue() > 0) || 
    		(conteggioSmartCig != null && conteggioSmartCig.longValue() > 0)) esisteCIG = true;

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled()) logger.debug("esisteCIG: fine metodo");

    return esisteCIG;

  }

  /**
   * Verifica l'esistenza di un terminato numero gara IDGARA
   *
   * @param idgara
   * @return
   * @throws GestoreException
   */
  public boolean esisteIDGARA(String idgara) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("esisteIDGARA: inizio metodo");

    boolean esisteIDGARA = false;

    try {
      Long conteggio = (Long) this.sqlManager.getObject(
          "select count(*) from w3gara where id_gara = ?",
          new Object[] { idgara });
      if (conteggio != null && conteggio.longValue() > 0) esisteIDGARA = true;

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled()) logger.debug("esisteIDGARA: fine metodo");

    return esisteIDGARA;

  }

  /**
   * Ricava l'indice della collaborazione associato alla gara o allo smartcig
   *
   * @param numgara
   * @param entita
   * @return
   * @throws GestoreException
   */
/*  public String getIndiceCollaborazione(Long numgara, String entita) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("getIndiceCollaborazione: inizio metodo");

    String index = null;
	String idUfficio = null;
    try {
    	if (entita.equals("W3GARA")) {
    	      index = (String) sqlManager.getObject(
    	              "select w3aziendaufficio.indexcoll from w3aziendaufficio, w3gara where w3aziendaufficio.id = w3gara.collaborazione and w3gara.numgara = ?",
    	              new Object[] { numgara });
			  idUfficio = (String) sqlManager.getObject(
    	              "select w3aziendaufficio.ufficio_id from w3aziendaufficio, w3gara where w3aziendaufficio.id = w3gara.collaborazione and w3gara.numgara = ?",
    	              new Object[] { numgara });
    	      
    	} else if (entita.equals("W3SMARTCIG")) {
    	      index = (String) sqlManager.getObject(
    	              "select w3aziendaufficio.indexcoll from w3aziendaufficio, W3SMARTCIG where w3aziendaufficio.id = W3SMARTCIG.collaborazione and W3SMARTCIG.CODRICH = ?",
    	              new Object[] { numgara });
			  idUfficio = (String) sqlManager.getObject(
    	              "select w3aziendaufficio.ufficio_id from w3aziendaufficio, W3SMARTCIG where w3aziendaufficio.id = W3SMARTCIG.collaborazione and W3SMARTCIG.CODRICH = ?",
    	              new Object[] { numgara });
    	}
		idUfficio = (idUfficio == null)?"":idUfficio;
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la lettura dell'indice della collaborazione associata alla gara",
          "controlloW3GARA", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("getIndiceCollaborazione: fine metodo");

    return index + ";" + idUfficio;
  }*/

  
  /**
   * Ricava l'idUfficio della collaborazione associato alla gara o allo smartcig
   *
   * @param numgara
   * @param entita
   * @return idUfficio della collaborazione associato alla gara o allo smartcig
   * @throws GestoreException
   */
  public String getIdUfficioCollaborazione(Long numgara, String entita) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("getIdUfficioCollaborazione: inizio metodo");

    String idUfficio = null;
    try {
//APPALTI-1063 -- ora si utilizza la tabelle centricosto e non più w3aziendaufficio
//    	if (entita.equals("W3GARA")) {
// 	      idUfficio = (String) sqlManager.getObject(
//            "select w3aziendaufficio.ufficio_id from w3aziendaufficio, w3gara where w3aziendaufficio.id = w3gara.collaborazione and w3gara.numgara = ?",
// 	              new Object[] { numgara });
//    	} else if (entita.equals("W3SMARTCIG")) {
// 	      idUfficio = (String) sqlManager.getObject(
//            "select w3aziendaufficio.ufficio_id from w3aziendaufficio, W3SMARTCIG where w3aziendaufficio.id = W3SMARTCIG.collaborazione and W3SMARTCIG.CODRICH = ?",
// 	              new Object[] { numgara });
//		}
      if (entita.equals("W3GARA")) {
        idUfficio = (String) sqlManager.getObject(
          "select CENTRICOSTO.CODCENTRO from CENTRICOSTO, w3gara where CENTRICOSTO.idcentro = w3gara.idcc and w3gara.numgara = ?",
                new Object[] { numgara });
      } else if (entita.equals("W3SMARTCIG")) {
        idUfficio = (String) sqlManager.getObject(
          "select CENTRICOSTO.CODCENTRO from CENTRICOSTO, W3SMARTCIG where CENTRICOSTO.idcentro = W3SMARTCIG.idcc and W3SMARTCIG.CODRICH = ?",
                new Object[] { numgara });
      }

    	idUfficio = (idUfficio == null)?"":idUfficio;
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la lettura dell'idUfficio del centro di costo associato alla gara",
          "controlloW3GARA", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("getIdUfficioCollaborazione: fine metodo");

    return idUfficio;
  }
  
  
  /**
   * Aggiornamento dell'identificativo CIG in base dati
   *
   * @param numgara
   * @param numlott
   * @param HashMap<String, Object>
   * @throws GestoreException
   */
  public void aggiornaW3LOTTCIG(Long numgara, Long numlott, HashMap<String, Object> hMapwsLotto)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3LOTTCIG: inizio metodo");

    String cig = ((String) hMapwsLotto.get("cig"));
    CUPLOTTOType cuplotto = (CUPLOTTOType) ( hMapwsLotto.get("cuplotto"));

    try {
      if(cig!=null){
        String updateW3LOTT = "update w3lott set cig = ?, data_creazione_lotto = ?, stato_simog = ? where numgara = ? and numlott = ?";
        this.sqlManager.update(updateW3LOTT, new Object[] { cig,new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), 2L, numgara, numlott });
      }
      if(cuplotto!=null && cuplotto.getCODICICUPArray() !=null){
    	  DatiCUPType[] codiciCup = cuplotto.getCODICICUPArray();
    	  if (codiciCup != null && codiciCup.length > 0) {
    		  String updateW3LOTTCUP = "update w3lottcup set dati_dipe = ? where numgara = ? and numlott = ? and cup = ? ";
    		  for (int i = 0; i < codiciCup.length; i++) {
    			  String cup = codiciCup[i].getCUP();
    			  String dati_dipe = codiciCup[i].getDATIDIPE();
    			  this.sqlManager.update(updateW3LOTTCUP, new Object[] {dati_dipe, numgara, numlott, cup });
    		  }
    	  }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3LOTTCIG: fine metodo");

  }

  public void aggiornaW3LOTTCIGMassivo(Long numgara, Long numlott, HashMap<String, Object> hMapwsLotto)
  throws GestoreException {

	  if (logger.isDebugEnabled())
		  logger.debug("aggiornaW3LOTTCIG: inizio metodo");

	  String cig = ((String) hMapwsLotto.get("cig"));
	  CUPLOTTOType cuplotto = (CUPLOTTOType) ( hMapwsLotto.get("cuplotto"));
	  TransactionStatus status = null;
	  try {
		  status = this.sqlManager.startTransaction();
		  if (cig != null) {
			  String updateW3LOTT = "update w3lott set cig = ?, data_creazione_lotto = ?, stato_simog = ? where numgara = ? and numlott = ?";
			  this.sqlManager.update(updateW3LOTT, new Object[] { cig, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), 2L, numgara, numlott });
		  }
		  if( cuplotto != null && cuplotto.getCODICICUPArray() != null) {
			  DatiCUPType[] codiciCup = cuplotto.getCODICICUPArray();
			  if (codiciCup != null && codiciCup.length > 0) {
				  String updateW3LOTTCUP = "update w3lottcup set dati_dipe = ? where numgara = ? and numlott = ? and cup = ? ";
				  for (int i = 0; i < codiciCup.length; i++) {
					  String cup = codiciCup[i].getCUP();
					  String dati_dipe = codiciCup[i].getDATIDIPE();
					  this.sqlManager.update(updateW3LOTTCUP, new Object[] {dati_dipe, numgara, numlott, cup });
				  }
			  }
		  }
	  } catch (SQLException e) {
		  throw new GestoreException(
				  "Si e' verificato un errore durante l'interazione con la base dati",
				  "gestioneIDGARACIG.sqlerror", e);
	  } finally {
		  try {
			  this.sqlManager.commitTransaction(status);
		  } catch (SQLException e) {
			  e.printStackTrace();
		  }
	  }
	  if (logger.isDebugEnabled())
		  logger.debug("aggiornaW3LOTTCIG: fine metodo");
  }
  
  /**
   * Aggiornamento dello stato del lotto
   *
   * @param numgara
   * @param numlott
   * @param stato
   * @throws GestoreException
   */
  public void aggiornaW3LOTTSTATO(Long numgara, Long numlott, Long stato)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3LOTTSTATO: inizio metodo");

    try {
      String updateW3LOTT = "update w3lott set stato_simog = ? where numgara = ? and numlott = ?";
      this.sqlManager.update(updateW3LOTT, new Object[] { stato, numgara, numlott });

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3LOTTSTATO: fine metodo");

  }

  /**
   * Aggiorna stato e data di pubblicazione della gara e dei lotti a fronte
   * della pubblicazione SIMOG
   *
   * @param numgara
   * @throws GestoreException
   */
  public void aggiornaPubblicazione(Long numgara) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaPubblicazione: inizio metodo");

    try {

      Timestamp dataOdierna = new Timestamp(
          UtilityDate.getDataOdiernaAsDate().getTime());

      String updateW3GARA = "update w3gara set stato_simog = ?, data_conferma_gara = ? where numgara = ?";
      this.sqlManager.update(updateW3GARA, new Object[] { 7L, dataOdierna, numgara });

      Date data_perfezionamento_bando = (Date) this.sqlManager.getObject("select data_perfezionamento_bando from w3gara where numgara = ?", new Object[] {numgara});
      String updateW3LOTT = "update w3lott set stato_simog = ?, data_pubblicazione = ? where numgara = ? and stato_simog in (2,4)";
      this.sqlManager.update(updateW3LOTT, new Object[] { 7L, data_perfezionamento_bando, numgara });

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaPubblicazione: fine metodo");

  }

  public void aggiornaPubblicazioneMassivo(Long numgara) throws GestoreException {

	    if (logger.isDebugEnabled())
	      logger.debug("aggiornaPubblicazione: inizio metodo");

	    TransactionStatus status = null;
	    try {
	    	status = this.sqlManager.startTransaction();
	      Timestamp dataOdierna = new Timestamp(
	          UtilityDate.getDataOdiernaAsDate().getTime());

	      String updateW3GARA = "update w3gara set stato_simog = ?, data_conferma_gara = ? where numgara = ?";
	      this.sqlManager.update(updateW3GARA, new Object[] { 7L, dataOdierna, numgara });

	      Date data_perfezionamento_bando = (Date) this.sqlManager.getObject("select data_perfezionamento_bando from w3gara where numgara = ?", new Object[] {numgara});
	      String updateW3LOTT = "update w3lott set stato_simog = ?, data_pubblicazione = ? where numgara = ? and stato_simog in (2,4)";
	      this.sqlManager.update(updateW3LOTT, new Object[] { 7L, data_perfezionamento_bando, numgara });

	    } catch (SQLException e) {
	      throw new GestoreException(
	          "Si e' verificato un errore durante l'interazione con la base dati",
	          "gestioneIDGARACIG.sqlerror", e);
	    } finally {
	    	try {
				this.sqlManager.commitTransaction(status);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    if (logger.isDebugEnabled())
	      logger.debug("aggiornaPubblicazione: fine metodo");

	  }
  
  /**
   * Aggiornamento dei motivi della cancellazione del lotto
   *
   * @param numgara
   * @param numlott
   * @param idmotivazione
   * @param notecanc
   * @throws GestoreException
   */
  public void aggiornaW3LOTTCancellazione(Long numgara, Long numlott,
      String idmotivazione, String notecanc) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3LOTTCancellazione: inizio metodo");

    try {
      String updateW3LOTT = "update w3lott set id_motivazione = ?, note_canc = ?, data_cancellazione_lotto = ? where numgara = ? and numlott = ?";
      this.sqlManager.update(updateW3LOTT, new Object[] { idmotivazione,
          notecanc,
          new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), numgara,
          numlott });

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaW3LOTTCancellazione: fine metodo");

  }
  
  /**
   *  Scollegamento del lotto di una gara a seguito della cancellazione logica
   *
   * @param numgara
   * @throws GestoreException
   */
  public void aggiornaLOTTOCancellazione(Long numgara, Long numlott) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("aggiornaLOTTOCancellazione: inizio metodo");

    try {
      String ngara = (String) this.sqlManager.getObject("select ngara from w3lott where numgara = ? and numlott = ? ", new Object[] { numgara,numlott });
      String codgar = (String) this.sqlManager.getObject("select codgar from w3gara where numgara = ? ", new Object[] { numgara});
      String updateGARE = "update gare set codcig = null,dacqcig=null where ngara = ?";
      String updateDACQCIGGara = "update gare set dacqcig = null where ngara = ?";
      this.sqlManager.update(updateGARE, new Object[] { ngara });
      //nel caso di gara a lotti la data di acquisizione sta sulla gara
      String ngaraGara  = (String) sqlManager.getObject(
              "select ngara from gare where ngara = ?",
              new Object[] {codgar});
      
      ngaraGara  = StringUtils.stripToEmpty(ngaraGara);
      if(!"".equals(ngaraGara)) {
    	  Long ccig = (Long) sqlManager.getObject("select count(*) from gare where codgar1 = ? and codcig is not null", new Object[] {codgar});
    	  if(Long.valueOf(0).equals(ccig)) {
    		  this.sqlManager.update(updateDACQCIGGara, new Object[] { ngaraGara });  
    	  }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("aggiornaLOTTOCancellazione: fine metodo");

  }

  /**
   * Gestione dei requisiti per la gara
   *
   * @param numgara
   * @throws GestoreException
   */
  public void gestioneRequisiti(Long numgara) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneRequisiti: inizio metodo");

    boolean result = false;
    TransactionStatus status = null;
    try {
    	// Ricavo l'importo totale dei lotti con stato 2 
    	//Double importo = 0D;
    	/*List<?> datiW3LOTT = this.sqlManager.getListVector(
		          "select IMPORTO_LOTTO from W3LOTT where NUMGARA = ? and CIG IS NOT NULL",
		          new Object[] {numgara });
    	if (datiW3LOTT != null W3LOTT.size() > 0) {
    		for (int i = 0; i < datiW3LOTT.size(); i++) {
    			Double importo_lotto = (Double) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 0).getValue();
    			if (importo_lotto != null) {
        			importo += importo_lotto;	
    			}
    		}
    	}*/
    	status = this.sqlManager.startTransaction();
    	//Controllo AVCPASS
    	String avcpass = (String)sqlManager.getObject("select ESCLUSO_AVCPASS from W3GARA where NUMGARA = ?", new Object[] { numgara });
    	if (avcpass != null && !avcpass.equals("1")) {
    		//Verifico se i requisiti sono già stati creati
        	Long numeroRequisiti = (Long)sqlManager.getObject(
    	              "select COUNT(*) from W3GARAREQ where NUMGARA = ?",
    	              new Object[] { numgara });
        	if (numeroRequisiti.equals(0L)) {
        		//se i requisiti non sono stati ancora inseriti allora li inserisco
        		List<?> datiW3TABREQ = this.sqlManager.getListVector(
        		          "select CODICE_DETTAGLIO, DESCRIZIONE, DOCUMENTI_DEFAULT from W3TABREQ where INIZIALE = '1'",
        		          new Object[] { });
        		if (datiW3TABREQ != null && datiW3TABREQ.size() > 0) {
        		    for (int i = 0; i < datiW3TABREQ.size(); i++) {
        		          String codice = (String) SqlManager.getValueFromVectorParam(
        		        		  datiW3TABREQ.get(i), 0).getValue();
        		          String descrizione = (String) SqlManager.getValueFromVectorParam(
        		        		  datiW3TABREQ.get(i), 1).getValue();
        		          String documenti = (String) SqlManager.getValueFromVectorParam(
        		        		  datiW3TABREQ.get(i), 2).getValue();
        		          int indexDescrizione = descrizione.indexOf("-");
        		          if (indexDescrizione != -1) {
        		        	  descrizione = descrizione.substring(indexDescrizione + 2);
        		          }
        		          if (descrizione.length() > 80) {
        		        	  descrizione = descrizione.substring(0,80);
        		          }
        		          Long numreq = (long) i+1;
        		          this.sqlManager.update("insert into W3GARAREQ(NUMGARA, NUMREQ, CODICE_DETTAGLIO, DESCRIZIONE, FLAG_ESCLUSIONE, FLAG_COMPROVA_OFFERTA, FLAG_AVVALIMENTO, FLAG_BANDO_TIPO, FLAG_RISERVATEZZA) VALUES(?,?,?,?,?,?,?,?,?)", new Object[] {
        		        		  numgara, numreq, codice, descrizione, "2", "2", "2", "2", "2" });
        		          if (documenti != null && !documenti.trim().equals("")) {
        		        	  String[] listaDocumenti = documenti.split("-");
        		        	  int j = 1;
        		        	  for(String tipoDocumento:listaDocumenti) {
        		        		  String descrizioneDoc = (String)sqlManager.getObject(
        		        	              "select TAB1DESC from TAB1 where TAB1COD = 'W3029' and TAB1TIP = ?",
        		        	              new Object[] { Long.parseLong(tipoDocumento) });
        		        		  if (descrizioneDoc != null) {
        		        			  this.sqlManager.update("insert into W3GARAREQDOC(NUMGARA, NUMREQ, NUMDOC, CODICE_TIPO_DOC, DESCRIZIONE, EMETTITORE, FAX, TELEFONO, MAIL, MAIL_PEC) VALUES(?,?,?,?,?,?,?,?,?,?)", 
        		        				new Object[] { numgara, numreq, (long)j, Long.parseLong(tipoDocumento), descrizioneDoc, "-", "0", "0", "-", "-" });
            		        		  j++;
        		        		  }
        		        	  }
        		          }
        		     }
        		}
        	}
        	result = true;
    	} else {
    		//se importo è inferiore a 40000 elimino i requisiti
    		this.sqlManager.update("delete from W3GARAREQDOC where NUMGARA = ?", new Object[] {numgara});
    		this.sqlManager.update("delete from W3GARAREQ where NUMGARA = ?", new Object[] {numgara});
    		result = true;
    	}
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
    	if (status != null) {
			try {
				if (result) {
					this.sqlManager.commitTransaction(status);
				} else {
					this.sqlManager.rollbackTransaction(status);
				}
			} catch (SQLException ex) {
				throw new GestoreException(
				          "Si e' verificato un errore durante l'interazione con la base dati",
				          "gestioneIDGARACIG.sqlerror", ex);
			}
		}
    }

    if (logger.isDebugEnabled())
      logger.debug("gestioneRequisiti: fine metodo");

  }
  
  /**
   * Invio della richiesta di generazione del codice CIG per un lotto
   *
   * @param simogwsuser
   * @param simogwspass
   * @param numgara
   * @param numlott
   * @return
   * @throws GestoreException
   */
  public String richiestaCIG(String simogwsuser, String simogwspass,
      Long numgara, Long numlott, boolean rpntFailed) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("richiestaCIG: inizio metodo");

    String cig = null;

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3GARA");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3GARA");
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from w3gara left join uffint on w3gara.codein=uffint.codein where w3gara.numgara = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);
      
      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	  this.AggiornaImportoNumeroLottiGara(numgara,hMapwsLogin,simogWS);
          String idgara = (String) this.sqlManager.getObject(
              "select id_gara from w3gara where numgara = ?",
              new Object[] { numgara });

          // Ricavo il contenuto XML
          LottoType lotto = this.gestioneXMLIDGARACIGManager.getDatiLotto(numgara, numlott, true);

          DatiLotto datiLotto = DatiLotto.Factory.newInstance();
          datiLotto.setLotto(lotto);
          
          HashMap<String, Object> hMapwsInserisciLotto = this.wsInserisciLotto(simogWS, hMapwsLogin, datiLotto, idgara);
          this.aggiornaW3LOTTCIG(numgara, numlott, hMapwsInserisciLotto );
          //this.gestioneRequisiti(numgara);
          cig = ((String) hMapwsInserisciLotto.get("cig"));
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("richiestaCIG: fine metodo");

    return cig;

  }

  /**
   * Invio della richiesta di modifica per un lotto con CIG già assegnato
   *
   * @param simogwsuser
   * @param simogwspass
   * @param numgara
   * @param numlott
   * @throws GestoreException
   */
  public void modificaLOTTO(String simogwsuser, String simogwspass,
      Long numgara, Long numlott, boolean rpntFailed) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("modificaLOTTO: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3GARA");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3GARA");
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from w3gara left join uffint on w3gara.codein=uffint.codein where w3gara.numgara = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);
      
      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	  this.AggiornaImportoNumeroLottiGara(numgara,hMapwsLogin,simogWS);
          String cig = (String) this.sqlManager.getObject(
              "select cig from w3lott where numgara = ? and numlott = ?",
              new Object[] { numgara, numlott });

          LottoType lotto = this.gestioneXMLIDGARACIGManager.getDatiLotto(numgara, numlott, true);

          ModificaLotto.DatiLotto datiLotto = ModificaLotto.DatiLotto.Factory.newInstance();
          datiLotto.setLotto(lotto);
          
          HashMap<String, Object> hMapwsModificaLotto =  this.wsModificaLotto(simogWS, hMapwsLogin, datiLotto, cig);
          this.aggiornaW3LOTTCIG(numgara, numlott, hMapwsModificaLotto);
          this.aggiornaW3LOTTSTATO(numgara, numlott, 4L);
          //this.gestioneRequisiti(numgara);
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
      
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("richiestaLOTTO: fine metodo");

  }

  /**
   * Invio della richiesta di cancellazione di un lotto
   *
   * @param simogwsuser
   * @param simogwspass
   * @param numgara
   * @param numlott
   * @throws GestoreException
   */
  public void cancellaLOTTO(String simogwsuser, String simogwspass,
      Long numgara, Long numlott, String idmotivazione, String notecanc, boolean rpntFailed)
      throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("cancellaLOTTO: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3GARA");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3GARA");
      
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from w3gara left join uffint on w3gara.codein=uffint.codein where w3gara.numgara = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);

      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	  String cig = (String) this.sqlManager.getObject(
    	          "select cig from w3lott where numgara = ? and numlott = ?",
    	          new Object[] { numgara, numlott });
    	  	  this.aggiornaGaraCollegataLotto(numgara);
    	      this.wsCancellaLotto(simogWS, hMapwsLogin, cig, idmotivazione, notecanc);
    	      this.aggiornaW3LOTTSTATO(numgara, numlott, 6L);
    	      this.aggiornaLOTTOCancellazione(numgara,numlott);
    	      this.aggiornaW3LOTTCancellazione(numgara, numlott, idmotivazione, notecanc);
    	      //this.AggiornaImportoNumeroLottiGara(numgara,hMapwsLogin,simogWS);
    	      this.aggiornaW3GARASTATO(numgara, 3L);
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
      
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);

    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("cancellaLOTTO: fine metodo");

  }

  /**
   * Invio della richiesta di annullamento SMARTCIG
   *
   * @param numgara
   * @throws GestoreException
   */
  public void cancellaSMARTCIG(String simogwsuser, String simogwspass,
      Long numgara, boolean rpntFailed) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("cancellaSMARTCIG: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    try {
      simogWS = this.getSimogWS();
      //String index_ufficioId = this.getIndiceCollaborazione(numgara, "W3SMARTCIG");
      String idUfficio = this.getIdUfficioCollaborazione(numgara, "W3SMARTCIG");
      
      String cfein = (String) this.sqlManager.getObject(
      		"select uffint.cfein from W3SMARTCIG left join uffint on W3SMARTCIG.codein=uffint.codein where W3SMARTCIG.CODRICH = ?",
              new Object[] { numgara });
      //hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, index_ufficioId, cfein);
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass, idUfficio, cfein, rpntFailed);
      
      if (hMapwsLogin.containsKey("indiceAmmesso") && (Boolean)hMapwsLogin.get("indiceAmmesso")) {
    	  //String index = index_ufficioId;
      	String index = (String) hMapwsLogin.get("index");
      	//if (index_ufficioId.indexOf(";") != -1) {
      	//	index = index_ufficioId.substring(0, index_ufficioId.indexOf(";"));
      	//} 
        
          // Creazione della Request comunicazione 
          String ticket = ((String) hMapwsLogin.get("ticket"));

        AnnullaComunicazioneRequest comunicazioneRequest = this.gestioneXMLIDGARACIGManager.getAnnullaComunicazioneSmartCig(
        		numgara, index, ticket);

          Services simogSmartCigWS = this.getSimogWSSmartCig();
          
          this.wsAnnullaComunicazione(simogSmartCigWS, comunicazioneRequest);
          this.aggiornaGARACancellazione("Scig",numgara);
          this.annullaW3SMARTCIG(numgara);
      } else {
        throw new GestoreException(
            "L'indice del centro di costo indicato "
                //+ hMapwsLogin.get("index")
                + idUfficio
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato oppure non fa riferimento a questa stazione appaltante. Si prega di riassociare il centro di costo nei dati generali della Gara",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { hMapwsLogin.get("index") }, null);
      }
    	    
    } catch (GestoreException e) {
      throw e;

    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);

    } catch (RemoteException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.remote.error", e);
    } catch (SQLException e) {
        throw new GestoreException(
                "Si e' verificato un errore durante l'interazione con la base dati",
                "gestioneIDGARACIG.sqlerror", e);
          } catch (CriptazioneException e) {
            throw new GestoreException(
                "Si e' verificato un errore durante l'interazione con la base dati",
                "gestioneIDGARACIG.sqlerror", e);
    }
	finally {
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }

    if (logger.isDebugEnabled()) logger.debug("cancellaSMARTCIG: fine metodo");
  }
  
  /**
   * Procedura che aggiorna in simog l'importo gara e il numero lotti ogni volta che viene 
   * fatta una modifica in simog sui lotti
   *
   * @param numgara
   * @param hMapwsLogin
   * @param simogWS
   * @return
   */
  private void AggiornaImportoNumeroLottiGara(Long numgara, HashMap<String, Object> hMapwsLogin, SimogWSPDDServiceStub simogWS) {
	  try {
		  String idgara = (String) this.sqlManager.getObject(
	              "select id_gara from w3gara where numgara = ?",
	              new Object[] { numgara });
	      
	      String ufficio_id = ((String) hMapwsLogin.get("ufficio_id"));
	      String ufficio_denominazione = ((String) hMapwsLogin.get("ufficio_denominazione"));
	      String azienda_codicefiscale = ((String) hMapwsLogin.get("azienda_codicefiscale"));
	      String azienda_denominazione = ((String) hMapwsLogin.get("azienda_denominazione"));

	      GaraType gara = this.gestioneXMLIDGARACIGManager.getDatiGara(
	              numgara, ufficio_id, ufficio_denominazione, azienda_codicefiscale,
	              azienda_denominazione, true);

	      ModificaGara.DatiGara datiGara = ModificaGara.DatiGara.Factory.newInstance();
	      datiGara.setDatiGara(gara);
	          
	      this.wsModificaGara(simogWS, hMapwsLogin, idgara, datiGara);
	      this.aggiornaW3GARASTATO(numgara, 4L);
	  } catch (Exception ex) {
		  logger.error("AggiornaImportoNumeroLottiGara : si è verificato un errore imprevito",ex);
	  }
  }
  
  /**
   * Tramite questa funzione è possibile verificare se nel sistema la login ai servizi SIMOG è predisposta alla tipologia RPNT 
   *
   * @return 
   */
  
  public boolean isLoginRPNTEnabled() {
    String login = ConfigManager.getValore(PROP_SIMOG_WS_LOGIN);
    String password = ConfigManager.getValore(PROP_SIMOG_WS_PASSWORD);
    String ruoloutente = ConfigManager.getValore(PROP_SIMOG_WS_RUOLOUTENTE);
    return !(login == null || "".equals(login) || password == null || "".equals(password) || !"3".equals(ruoloutente));
  }
  
  /**
   * Chiamata al metodo login del WS SIMOG per ricavare l'identificativo di
   * sessione (ticket) e la lista delle collaborazioni Utilizzata solamente per
   * ricava la lista delle collaborazioni
   *
   * @param simogWS
   * @param login
   * @param password
   * @return
   * @throws ServiceException
   * @throws GestoreException
   * @throws RemoteException
   * @throws CriptazioneException 
   */
  private HashMap<String, Object> wsLoginCollaborazioni(SimogWSPDDServiceStub simogWS,
      String login, String password, boolean rpntFailed) throws ServiceException, GestoreException,
      RemoteException, CriptazioneException {

    if (logger.isDebugEnabled())
      logger.debug("wsLoginCollaborazioni: inizio metodo");

    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    ResponseCheckLogin responseCheckLogin = null;
    if(this.isLoginRPNTEnabled() && !rpntFailed) {
      String loginRPNT = ConfigManager.getValore(PROP_SIMOG_WS_LOGIN);
      String passwordRPNT = ConfigManager.getValore(PROP_SIMOG_WS_PASSWORD);
      ICriptazioneByte icb = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          passwordRPNT.getBytes(), it.eldasoft.utils.sicurezza.ICriptazioneByte.FORMATO_DATO_CIFRATO);
      String passwordDecoded = new String(icb.getDatoNonCifrato());
      
      LoginRPNTDocument loginRPNTDoc = LoginRPNTDocument.Factory.newInstance();
      LoginRPNT loginInRPNT = LoginRPNT.Factory.newInstance();
      loginInRPNT.setLogin(loginRPNT);
      loginInRPNT.setPassword(passwordDecoded);
      loginInRPNT.setCfrup(login); //sarebbe il cf del rup
      loginRPNTDoc.setLoginRPNT(loginInRPNT);
      
      LoginRPNTResponseDocument loginRPNTResponseDoc = simogWS.loginRPNT(loginRPNTDoc);
      LoginRPNTResponse loginResponse = loginRPNTResponseDoc.getLoginRPNTResponse();
      
      if (loginResponse.isSetReturn()) {
        responseCheckLogin = loginResponse.getReturn();
      }
    }else {
      LoginDocument loginDoc = LoginDocument.Factory.newInstance();
      Login loginIn = Login.Factory.newInstance();
      loginIn.setLogin(login);
      loginIn.setPassword(password);
      loginDoc.setLogin(loginIn);
      
      LoginResponseDocument loginResponseDoc = simogWS.login(loginDoc);
      
      LoginResponse loginResponse = loginResponseDoc.getLoginResponse();
      
      if (loginResponse.isSetReturn()) {
        responseCheckLogin = loginResponse.getReturn();
      }
    }

    String error = null;
    if (responseCheckLogin != null ) {
    	if (responseCheckLogin.getSuccess()) {
    		// Ticket della sessione
    		String ticket = responseCheckLogin.getTicket();
    	    hMapwsLogin.put("ticket", ticket);
    	    hMapwsLogin.put("responseCheckLogin", responseCheckLogin);
    	} else {
    		error = responseCheckLogin.getError();
    	}
    } else {
    	error = "Errore imprevisto nel servizio di login";
    }
    
    if (error != null) {
    	logger.error("Il servizio di login ha risposto con il seguente messaggio: "
    	          + error);
    	      throw new GestoreException(
    	          "Il servizio di login ha risposto con il seguente messaggio: "
    	              + error, "gestioneIDGARACIG.ws.login.error",
    	          new Object[] { error }, null);
    }

    if (logger.isDebugEnabled())
      logger.debug("wsLoginCollaborazioni: fine metodo");

    return hMapwsLogin;
  }

  /**
   * Chiamata al metodo login del WS SIMOG per ricavare l'identificativo di
   * sessione (ticket) e la lista delle collaborazioni In questo caso viene
   * indicato anche l'indice della collaborazione e controllato che tale indice
   * sia ancora disponibile nella lista della collaborazioni SIMOG
   *
   * @param simogWS
   * @param login
   * @param password
   * @param index_ufficioId(index+";"+ufficio_id)
   * @return
   * @throws ServiceException
   * @throws GestoreException
   * @throws RemoteException
   * @throws CriptazioneException 
   */
  private HashMap<String, Object> wsLogin(SimogWSPDDServiceStub simogWS, String login,
      String password, String idUfficio, String cfStazioneAppaltante, boolean rpntFailed) throws ServiceException, GestoreException,
      RemoteException, CriptazioneException {

    if (logger.isDebugEnabled()) logger.debug("wsLogin: inizio metodo");

    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    ResponseCheckLogin responseCheckLogin = null;
    if(this.isLoginRPNTEnabled() && !rpntFailed) {
      String loginRPNT = ConfigManager.getValore(PROP_SIMOG_WS_LOGIN);
      String passwordRPNT = ConfigManager.getValore(PROP_SIMOG_WS_PASSWORD);
      ICriptazioneByte icb = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          passwordRPNT.getBytes(), it.eldasoft.utils.sicurezza.ICriptazioneByte.FORMATO_DATO_CIFRATO);
      String passwordDecoded = new String(icb.getDatoNonCifrato());
      
      LoginRPNTDocument loginRPNTDoc = LoginRPNTDocument.Factory.newInstance();
      LoginRPNT loginInRPNT = LoginRPNT.Factory.newInstance();
      loginInRPNT.setLogin(loginRPNT);
      loginInRPNT.setPassword(passwordDecoded);
      loginInRPNT.setCfrup(login); //sarebbe il cf del rup
      loginRPNTDoc.setLoginRPNT(loginInRPNT);
      
      LoginRPNTResponseDocument loginRPNTResponseDoc = simogWS.loginRPNT(loginRPNTDoc);
      LoginRPNTResponse loginResponse = loginRPNTResponseDoc.getLoginRPNTResponse();
      
      if (loginResponse.isSetReturn()) {
        responseCheckLogin = loginResponse.getReturn();
      }
    }else {
      LoginDocument loginDoc = LoginDocument.Factory.newInstance();
      Login loginIn = Login.Factory.newInstance();
      loginIn.setLogin(login);
      loginIn.setPassword(password);
      loginDoc.setLogin(loginIn);
    
      LoginResponseDocument loginResponseDoc = simogWS.login(loginDoc);
    
      LoginResponse loginResponse = loginResponseDoc.getLoginResponse();
      
      if (loginResponse.isSetReturn()) {
        responseCheckLogin = loginResponse.getReturn();
      }
    }
    
    if (responseCheckLogin != null && responseCheckLogin.getSuccess()) {
    	String ticket = responseCheckLogin.getTicket();
    	hMapwsLogin.put("ticket", ticket);

    	// Controllo se l'indice della collaborazione indicato esiste tra quelli ammessi da SIMOG
    	boolean indiceAmmesso = false;

   		if (responseCheckLogin.getColl() != null) {
  			if (responseCheckLogin.getColl().getCollaborazioniArray() != null && responseCheckLogin.getColl().getCollaborazioniArray().length > 0) {
    			Collaborazione[] arrayOfCollaborazione = responseCheckLogin.getColl().getCollaborazioniArray();
    			for (int i = 0; i < arrayOfCollaborazione.length; i++) {
    				boolean check = false;
					if (cfStazioneAppaltante != null && !"".equals(cfStazioneAppaltante)) {
						if (cfStazioneAppaltante.equalsIgnoreCase(arrayOfCollaborazione[i].getAziendaCodiceFiscale().trim())) {
							if (idUfficio != null && !idUfficio.equals("") && idUfficio.equalsIgnoreCase(arrayOfCollaborazione[i].getUfficioId().trim())) {
    							indiceAmmesso = true;
            					check = true;
    						}
    					}
    				} else {
    					indiceAmmesso = true;
    					check = true;
    				}
							
    				if (indiceAmmesso && check) {
						if (! hMapwsLogin.containsKey("index")) {
							hMapwsLogin.put("index", arrayOfCollaborazione[i].getIndex());
						}
						hMapwsLogin.put("ufficio_id", arrayOfCollaborazione[i].getUfficioId());
						hMapwsLogin.put("ufficio_denominazione", arrayOfCollaborazione[i].getUfficioDenominazione());
						hMapwsLogin.put("azienda_codicefiscale", arrayOfCollaborazione[i].getAziendaCodiceFiscale());
						hMapwsLogin.put("azienda_denominazione", arrayOfCollaborazione[i].getAziendaDenominazione());
						break;
					}
				}
			}
		}

    	if (!indiceAmmesso) {
    	    logger.error("L'indice della collaborazione indicato "
            + idUfficio
            + " non e' piu' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato.");
        /*throw new GestoreException(
            "L'indice della collaborazione indicato "
                + index
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per l'utente connesso.",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { index }, null);*/
    	}
		hMapwsLogin.put("indiceAmmesso", indiceAmmesso);
    	//hMapwsLogin.put("index", index);

    } else {
    	String messaggio = "Errore imprevisto nel servizio di login";
    	if (responseCheckLogin != null) {
    		messaggio = responseCheckLogin.getError();
    	}
      
      logger.error("Il servizio di login ha risposto con il seguente messaggio: "
          + messaggio);
      throw new GestoreException(
          "Il servizio di login ha risposto con il seguente messaggio: "
              + messaggio, "gestioneIDGARACIG.ws.login.error",
          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsLogin: fine metodo");

    return hMapwsLogin;
  }
  
  private HashMap<String, Object> wsLogin(SimogWSPDDServiceStub simogWS, String login,
      String password, String idUfficio, boolean rpntFailed) throws ServiceException, GestoreException,
      RemoteException, CriptazioneException {

    if (logger.isDebugEnabled()) logger.debug("wsLogin: inizio metodo");

    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    ResponseCheckLogin responseCheckLogin = null;
    if(this.isLoginRPNTEnabled() && !rpntFailed) {
      String loginRPNT = ConfigManager.getValore(PROP_SIMOG_WS_LOGIN);
      String passwordRPNT = ConfigManager.getValore(PROP_SIMOG_WS_PASSWORD);
      ICriptazioneByte icb = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          passwordRPNT.getBytes(), it.eldasoft.utils.sicurezza.ICriptazioneByte.FORMATO_DATO_CIFRATO);
      String passwordDecoded = new String(icb.getDatoNonCifrato());
      
      LoginRPNTDocument loginRPNTDoc = LoginRPNTDocument.Factory.newInstance();
      LoginRPNT loginInRPNT = LoginRPNT.Factory.newInstance();
      loginInRPNT.setLogin(loginRPNT);
      loginInRPNT.setPassword(passwordDecoded);
      loginInRPNT.setCfrup(login); //sarebbe il cf del rup
      loginRPNTDoc.setLoginRPNT(loginInRPNT);
      
      LoginRPNTResponseDocument loginRPNTResponseDoc = simogWS.loginRPNT(loginRPNTDoc);
      LoginRPNTResponse loginResponse = loginRPNTResponseDoc.getLoginRPNTResponse();
      
      if (loginResponse.isSetReturn()) {
        responseCheckLogin = loginResponse.getReturn();
      }
    }else {
      LoginDocument loginDoc = LoginDocument.Factory.newInstance();
      Login loginIn = Login.Factory.newInstance();
      loginIn.setLogin(login);
      loginIn.setPassword(password);
      loginDoc.setLogin(loginIn);
    
      LoginResponseDocument loginResponseDoc = simogWS.login(loginDoc);
    
      LoginResponse loginResponse = loginResponseDoc.getLoginResponse();
      
      if (loginResponse.isSetReturn()) {
        responseCheckLogin = loginResponse.getReturn();
      }
    }
    
    if (responseCheckLogin != null && responseCheckLogin.getSuccess()) {
        String ticket = responseCheckLogin.getTicket();
        hMapwsLogin.put("ticket", ticket);

        // Controllo se l'indice della collaborazione indicato esiste tra quelli ammessi da SIMOG
        boolean indiceAmmesso = false;

        if (responseCheckLogin.getColl() != null) {
            if (responseCheckLogin.getColl().getCollaborazioniArray() != null && responseCheckLogin.getColl().getCollaborazioniArray().length > 0) {
                Collaborazione[] arrayOfCollaborazione = responseCheckLogin.getColl().getCollaborazioniArray();
                for (int i = 0; i < arrayOfCollaborazione.length; i++) {
                    boolean check = false;
                     if (idUfficio != null && !idUfficio.equals("") && idUfficio.equalsIgnoreCase(arrayOfCollaborazione[i].getUfficioId().trim())) {
                                indiceAmmesso = true;
                    }
                            
                    if (indiceAmmesso) {
                        if (! hMapwsLogin.containsKey("index")) {
                            hMapwsLogin.put("index", arrayOfCollaborazione[i].getIndex());
                        }
                        hMapwsLogin.put("ufficio_id", arrayOfCollaborazione[i].getUfficioId());
                        hMapwsLogin.put("ufficio_denominazione", arrayOfCollaborazione[i].getUfficioDenominazione());
                        hMapwsLogin.put("azienda_codicefiscale", arrayOfCollaborazione[i].getAziendaCodiceFiscale());
                        hMapwsLogin.put("azienda_denominazione", arrayOfCollaborazione[i].getAziendaDenominazione());
                        break;
                    }
                }
            }
        }

        if (!indiceAmmesso) {
            logger.error("L'indice della collaborazione indicato "
            + idUfficio
            + " non e' piu' presente nella lista degli indici ammessi dai servizi SIMOG per il RUP indicato.");
        /*throw new GestoreException(
            "L'indice della collaborazione indicato "
                + index
                + " non e' presente nella lista degli indici ammessi dai servizi SIMOG per l'utente connesso.",
            "gestioneIDGARACIG.ws.login.indexNonAmmesso",
            new Object[] { index }, null);*/
        }
        hMapwsLogin.put("indiceAmmesso", indiceAmmesso);
        //hMapwsLogin.put("index", index);

    } else {
        String messaggio = "Errore imprevisto nel servizio di login";
        if (responseCheckLogin != null) {
          messaggio = responseCheckLogin.getError();
      }
      
      logger.error("Il servizio di login ha risposto con il seguente messaggio: "
          + messaggio);
      throw new GestoreException(
          "Il servizio di login ha risposto con il seguente messaggio: "
              + messaggio, "gestioneIDGARACIG.ws.login.error",
          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsLogin: fine metodo");

    return hMapwsLogin;
  }

  /**
   * Chiamata al metodo login del WS SIMOG per ricavare l'identificativo di
   * sessione (ticket). Non gestisce la lista delle collaborazioni e l'indice
   * della collaborazione. E' un metodo utilizzato dai metodi SIMOG che non
   * richiedono l'indicazione dell'index (consultaGara)
   *
   * @param simogWS
   * @param login
   * @param password
   * @return
   * @throws ServiceException
   * @throws GestoreException
   * @throws RemoteException
   * @throws CriptazioneException 
   */
  private HashMap<String, Object> wsLogin(SimogWSPDDServiceStub simogWS, String login,
      String password, boolean rpntFailed) throws ServiceException, GestoreException,
      RemoteException, CriptazioneException {

    if (logger.isDebugEnabled()) logger.debug("wsLogin: inizio metodo");

    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();

    ResponseCheckLogin responseCheckLogin = null;
    if(this.isLoginRPNTEnabled() && !rpntFailed) {
      String loginRPNT = ConfigManager.getValore(PROP_SIMOG_WS_LOGIN);
      String passwordRPNT = ConfigManager.getValore(PROP_SIMOG_WS_PASSWORD);
      ICriptazioneByte icb = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          passwordRPNT.getBytes(), it.eldasoft.utils.sicurezza.ICriptazioneByte.FORMATO_DATO_CIFRATO);
      String passwordDecoded = new String(icb.getDatoNonCifrato());
      
      LoginRPNTDocument loginRPNTDoc = LoginRPNTDocument.Factory.newInstance();
      LoginRPNT loginInRPNT = LoginRPNT.Factory.newInstance();
      loginInRPNT.setLogin(loginRPNT);
      loginInRPNT.setPassword(passwordDecoded);
      loginInRPNT.setCfrup(login); //sarebbe il cf del rup
      loginRPNTDoc.setLoginRPNT(loginInRPNT);
      
      LoginRPNTResponseDocument loginRPNTResponseDoc = simogWS.loginRPNT(loginRPNTDoc);
      LoginRPNTResponse loginResponse = loginRPNTResponseDoc.getLoginRPNTResponse();
      
      if (loginResponse.isSetReturn()) {
        responseCheckLogin = loginResponse.getReturn();
      }
    }else {
      LoginDocument loginDoc = LoginDocument.Factory.newInstance();
      Login loginIn = Login.Factory.newInstance();
      loginIn.setLogin(login);
      loginIn.setPassword(password);
      loginDoc.setLogin(loginIn);
    
      LoginResponseDocument loginResponseDoc = simogWS.login(loginDoc);
    
      LoginResponse loginResponse = loginResponseDoc.getLoginResponse();
      
      if (loginResponse.isSetReturn()) {
        responseCheckLogin = loginResponse.getReturn();
      }
    }
    
    String error = null;
    if (responseCheckLogin != null ) {
        if (responseCheckLogin.getSuccess()) {
            // Ticket della sessione
            String ticket = responseCheckLogin.getTicket();
            hMapwsLogin.put("ticket", ticket);
            hMapwsLogin.put("collaborazioni", responseCheckLogin.getColl());
        } else {
            error = responseCheckLogin.getError();
        }
    } else {
        error = "Errore imprevisto nel servizio di login";
    }   
    
    if (error != null) {
    	logger.error("Il servizio di login ha risposto con il seguente messaggio: "
    	          + error);
    	      throw new GestoreException(
    	          "Il servizio di login ha risposto con il seguente messaggio: "
    	              + error, "gestioneIDGARACIG.ws.login.error",
    	          new Object[] { error }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsLogin: fine metodo");

    return hMapwsLogin;
  }

  /**
   * Chiamata al metodo inserisciGara del WS SIMOG per ottenere il codice
   * identificativo gara (IDGARA)
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param datiGaraXML
   * @return
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private String wsInserisciGara(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, DatiGara datiGara)
      throws ServiceException, RemoteException, GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("wsInserisciGara: inizio metodo");

    String idgara = null;
    String ticket = ((String) hMapwsLogin.get("ticket"));
    String index = ((String) hMapwsLogin.get("index"));
    
    InserisciGaraDocument inserisciGaraDoc = InserisciGaraDocument.Factory.newInstance();
    InserisciGara inserisciGara = InserisciGara.Factory.newInstance();
    inserisciGara.setTicket(ticket);
    inserisciGara.setIndexCollaborazione(index);
    inserisciGara.setDatiGara(datiGara);
    inserisciGaraDoc.setInserisciGara(inserisciGara);
    InserisciGaraResponseDocument InserisciresponseGaraDoc = simogWS.inserisciGara(inserisciGaraDoc);
    InserisciGaraResponse responseInserisciGara = InserisciresponseGaraDoc.getInserisciGaraResponse();
    
    if (responseInserisciGara.isSetReturn() && responseInserisciGara.getReturn().getSuccess()) {
      idgara = responseInserisciGara.getReturn().getIdGara();
    } else {
    	String messaggio = "Errore imprevisto nel servizio di inserisciGara";
    	if (responseInserisciGara.isSetReturn()) {
    		messaggio = responseInserisciGara.getReturn().getError();
    	}
      
      logger.error("Il servizio di inserimento della gara ha risposto con il seguente messaggio: "
          + messaggio);
      throw new GestoreException(
          "Il servizio di inserimento della gara ha risposto con il seguente messaggio: "
              + messaggio, "gestioneIDGARACIG.ws.inseriscigara.error",
          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsInserisciGara: fine metodo");

    return idgara;

  }

  /**
   * Chiamata al metodo comunicaSingola del WS SIMOG SMARTCIG per ottenere il codice SmartCig
   * identificativo gara (IDGARA)
   *
   * @param simogSmartCigWS
   * @param comunicazioneRequest
   * @return
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private String wsComunicaSingola(Services simogSmartCigWS, ComunicaSingolaRequest comunicazioneRequest)
      throws ServiceException, RemoteException, GestoreException, SocketTimeoutException {

    if (logger.isDebugEnabled())
      logger.debug("wsComunicaSingola: inizio metodo");

    String smartCig = null;
    
    ComunicaSingolaResponse responseComunicazione = simogSmartCigWS.comunicaSingola(comunicazioneRequest);
    
    logger.info(responseComunicazione.toString());
    
    smartCig = responseComunicazione.getCig();
    if (smartCig == null || smartCig.equals("")) {
    	ErroreType[] errori = responseComunicazione.getErrore();
    	String messaggio = "";
    	for (int i = 0; i < errori.length; i++) {
    		messaggio += errori[i].getErrore() + "\r\n";
    	}
    	if (messaggio.indexOf("java.util.NoSuchElementException") != -1)
    		messaggio += ". La richiesta non rispetta il formato previsto (verificare se il tipo procedura di scelta contraente è previsto da formato)";
    	logger.error("Il servizio di comunicazione singola ha risposto con il seguente messaggio: "
          + messaggio);
      throw new GestoreException(
          "Il servizio di comunicazione singola ha risposto con il seguente messaggio: "
              + messaggio, "gestioneIDGARACIG.ws.inseriscigara.error",
          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsComunicaSingola: fine metodo");

    return smartCig;

  }
  
  /**
   * Chiamata al metodo AnnullaComunicazione del WS SIMOG SMARTCIG per annullare il codice SmartCig
   * identificativo gara (IDGARA)
   *
   * @param simogSmartCigWS
   * @param comunicazioneRequest
   * @return
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private void wsAnnullaComunicazione(Services simogSmartCigWS, AnnullaComunicazioneRequest comunicazioneRequest)
      throws ServiceException, RemoteException, GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("wsAnnullaComunicazione: inizio metodo");

    AnnullaComunicazioneResponse responseComunicazione = simogSmartCigWS.annullaComunicazione(comunicazioneRequest);
    RisultatoType result = responseComunicazione.getCodiceRisultato();
    if (!result.getCodice().equals(CodiceRisultatoType.COD_001)) {
    	//se codice di ritorno non è COD_001 OK
    	String messaggio = result.getIdTransazione() + " - "
    	+ result.getCodice().getValue() + " - "
    	+ result.getDescrizione().getValue();
    	
    	logger.error("Il servizio di annullacomunicazione ha risposto con il seguente messaggio: "
          + messaggio);
      throw new GestoreException(
          "Il servizio di annullacomunicazione ha risposto con il seguente messaggio: "
              + messaggio, "gestioneIDGARACIG.ws.cancellagara.error",
          new Object[] { messaggio }, null);
    }
    if (logger.isDebugEnabled()) logger.debug("wsAnnullaComunicazione: fine metodo");
  }
  
  /**
   * Chiamata al metodo modificaGara del WS SIMOG per comunicare le modifiche ai
   * dati di una gara identificata dal codice IDGARA
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param numgara
   * @param idgara
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private void wsModificaGara(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, String idgara, ModificaGara.DatiGara datiGara)
      throws ServiceException, RemoteException, GestoreException {

    if (logger.isDebugEnabled()) logger.debug("wsModificaGara: inizio metodo");

    String ticket = ((String) hMapwsLogin.get("ticket"));
    String index = ((String) hMapwsLogin.get("index"));

    ModificaGaraDocument modificaGaraDoc = ModificaGaraDocument.Factory.newInstance();
    ModificaGara modificaGara = ModificaGara.Factory.newInstance();
    modificaGara.setTicket(ticket);
    modificaGara.setIndexCollaborazione(index);
    modificaGara.setDatiGara(datiGara);
    modificaGara.setIdGara(idgara);
    modificaGaraDoc.setModificaGara(modificaGara);
    ModificaGaraResponseDocument modificaResponseGaraDoc = simogWS.modificaGara(modificaGaraDoc);
    ModificaGaraResponse responseModificaGara = modificaResponseGaraDoc.getModificaGaraResponse();
  
    String messaggio = null;
    if (responseModificaGara.isSetReturn()) {
    	ResponseModificaGara response = responseModificaGara.getReturn();
    	if (!response.getSuccess()) {
    		 messaggio = response.getError();
    	}
    } else {
    	messaggio = "Errore imprevisto nel servizio di modificaGara";
    }
    
    if (messaggio != null) {
    	logger.error("Il servizio di modifica dei dati della gara ha risposto con il seguente messaggio: "
  	          + messaggio);
  	      throw new GestoreException(
  	          "Il servizio di modifica dei dati della gara ha risposto con il seguente messaggio: "
  	              + messaggio, "gestioneIDGARACIG.ws.modificagara.error",
  	          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsModificaGara: fine metodo");

  }

  /**
   * Chiamata al metodo cancellaGara del WS SIMOG per richiedere la
   * cancellazione dei dati di una gara identificata dal codice IDGARA
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param idgara
   * @param idmotivazione
   * @param notecanc
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private void wsCancellaGara(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, String idgara, String idmotivazione,
      String notecanc) throws ServiceException, RemoteException,
      GestoreException {

    if (logger.isDebugEnabled()) logger.debug("wsCancellaGara: inizio metodo");

    String ticket = ((String) hMapwsLogin.get("ticket"));
    String index = ((String) hMapwsLogin.get("index"));

    CancellaGaraDocument cancellaGaraDoc = CancellaGaraDocument.Factory.newInstance();
    CancellaGara cancellaGara = CancellaGara.Factory.newInstance();
    cancellaGara.setTicket(ticket);
    cancellaGara.setIndexCollaborazione(index);
    cancellaGara.setIdMotivazione(idmotivazione);
    cancellaGara.setNoteCanc(notecanc);
    cancellaGara.setIdGara(idgara);
    cancellaGaraDoc.setCancellaGara(cancellaGara);
    CancellaGaraResponseDocument cancellaResponseGaraDoc = simogWS.cancellaGara(cancellaGaraDoc);
    CancellaGaraResponse responseCancellaGara = cancellaResponseGaraDoc.getCancellaGaraResponse();
  
    String messaggio = null;
    if (responseCancellaGara.isSetReturn()) {
    	ResponseCancellaGara response = responseCancellaGara.getReturn();
    	if (!response.getSuccess()) {
    		 messaggio = response.getError();
    	}
    } else {
    	messaggio = "Errore imprevisto nel servizio di cancellaGara";
    }
    
    if (messaggio != null) {
    	logger.error("Il servizio di cancellazione della gara ha risposto con il seguente messaggio: "
    	          + messaggio);
    	      throw new GestoreException(
    	          "Il servizio di cancellazione della gara ha risposto con il seguente messaggio: "
    	              + messaggio, "gestioneIDGARACIG.ws.cancellagara.error",
    	          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsCancellaGara: fine metodo");

  }

  /**
   * Chiamata al metodo inserisciLotto del WS SIMOG per ottenere il codice
   * identificativo del lotto (CIG)
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param datiLottoXML
   * @param idgara
   * @return HashMap
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   * @throws SQLException
   */
  private HashMap<String, Object> wsInserisciLotto(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, DatiLotto datiLotto, String idgara)
      throws ServiceException, RemoteException, GestoreException, SQLException {

    if (logger.isDebugEnabled())
      logger.debug("wsInserisciLotto: inizio metodo");
    HashMap<String, Object> hMapwsInserisciLotto = new HashMap<String, Object>();
    String cig = null;
    String ticket = ((String) hMapwsLogin.get("ticket"));
    String index = ((String) hMapwsLogin.get("index"));

    InserisciLottoDocument inserisciLottoDoc = InserisciLottoDocument.Factory.newInstance();
    InserisciLotto inserisciLotto = InserisciLotto.Factory.newInstance();
    inserisciLotto.setTicket(ticket);
    inserisciLotto.setIndexCollaborazione(index);
    inserisciLotto.setDatiLotto(datiLotto);
    inserisciLotto.setIdGara(idgara);
    inserisciLottoDoc.setInserisciLotto(inserisciLotto);
	logger.info("wsInserisciLotto: Call per gara = " + idgara);
    InserisciLottoResponseDocument InserisciresponseLottoDoc = simogWS.inserisciLotto(inserisciLottoDoc);
	logger.info("wsInserisciLotto: End Call per gara = " + idgara);
    InserisciLottoResponse responseInserisciLotto = InserisciresponseLottoDoc.getInserisciLottoResponse();
    
    if (responseInserisciLotto.isSetReturn())
    {
    	ResponseInserisciLotto response = responseInserisciLotto.getReturn();
    	if (response.getSuccess()) {
    		cig = response.getCig().getCig();
            cig += response.getCig().getCigKKK();
            hMapwsInserisciLotto.put("cig", cig);
            CUPLOTTOType cuplotto = response.getCUPLOTTO();
            hMapwsInserisciLotto.put("cuplotto", cuplotto);
    	} else {
    		
    		CUPLOTTOType cuplotto = response.getCUPLOTTO();
    	      String msgCupAnomali = "";
    	      if(cuplotto != null && cuplotto.getCODICICUPArray() != null){
    	    	  DatiCUPType[] codiciCup = cuplotto.getCODICICUPArray();
    	    	  for (int i = 0; i < codiciCup.length; i++) {
    	    		  String cup = codiciCup[i].getCUP();
    	    		  if(codiciCup[i].getVALIDO().equals(FlagSNType.N)){
    	    			  msgCupAnomali = "\r\n" + msgCupAnomali + "\r\n" + cup;
    	    		  }
    	        	}
    	      }
    	      String messaggio = response.getError();
    	      if(!"".equals(msgCupAnomali)){
    	        msgCupAnomali = " - Correggere i seguenti Cup Anomali: " + msgCupAnomali;
    	        messaggio = messaggio + msgCupAnomali;
    	      }
    	      logger.error("Il servizio di inserimento del lotto ha risposto con il seguente messaggio: "
    	          + messaggio);
    	      throw new GestoreException(
    	          "Il servizio di inserimento del lotto ha risposto con il seguente messaggio: "
    	              + messaggio, "gestioneIDGARACIG.ws.inseriscilotto.error",
    	          new Object[] { messaggio }, null);
    	}
    }
    else {
    	String messaggio = "Errore imprevisto nel servizio di inserisciLotto";
    	logger.error(messaggio);
    	throw new GestoreException(
    	          "Il servizio di inserimento del lotto ha risposto con il seguente messaggio: "
    	              + messaggio, "gestioneIDGARACIG.ws.inseriscilotto.error",
    	          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsInserisciLotto: fine metodo");

    return hMapwsInserisciLotto;
  }

  /**
   * Chiamata al metodo modificaLotto del WS SIMOG per comunicare le modifiche
   * ai dati del lotto identificato dal codice CIG
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param numgara
   * @param numlott
   * @param cig
   * @return HashMap
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   * @throws SQLException
   */
  private HashMap<String, Object> wsModificaLotto(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, ModificaLotto.DatiLotto datiLotto, String cig)
      throws ServiceException, RemoteException, GestoreException, SQLException {

    if (logger.isDebugEnabled())
      logger.debug("wsModificaLotto: inizio metodo");

    String ticket = ((String) hMapwsLogin.get("ticket"));
    String index = ((String) hMapwsLogin.get("index"));

    HashMap<String, Object> hMapwsModificaLotto = new HashMap<String, Object>();

    ModificaLottoDocument modificaLottoDoc = ModificaLottoDocument.Factory.newInstance();
    ModificaLotto modificaLotto = ModificaLotto.Factory.newInstance();
    modificaLotto.setTicket(ticket);
    modificaLotto.setIndexCollaborazione(index);
    modificaLotto.setDatiLotto(datiLotto);
    modificaLotto.setCig(cig);
    modificaLottoDoc.setModificaLotto(modificaLotto);
    ModificaLottoResponseDocument modificaResponseLottoDoc = simogWS.modificaLotto(modificaLottoDoc);
    ModificaLottoResponse responseModificaLotto = modificaResponseLottoDoc.getModificaLottoResponse();
  
    String messaggio = null;
    if (responseModificaLotto.isSetReturn()) {
    	ResponseModificaLotto response = responseModificaLotto.getReturn();
    	if (!response.getSuccess()) {
    	      CUPLOTTOType cuplotto = response.getCUPLOTTO();
    	      String msgCupAnomali = "";
    	      if(cuplotto != null && cuplotto.getCODICICUPArray() != null){
    	    	  DatiCUPType[] codiciCup = cuplotto.getCODICICUPArray();
    	    	  for (int i = 0; i < codiciCup.length; i++) {
    	    		  String cup = codiciCup[i].getCUP();
    	    		  if(codiciCup[i].getVALIDO().equals(FlagSNType.N)){
    	    			  msgCupAnomali = "\r\n" + msgCupAnomali + "\r\n" + cup;
    	    		  }
    	    	  }
    	      }
    	      messaggio = response.getError();
    	      if(!"".equals(msgCupAnomali)){
    	        msgCupAnomali = " - Correggere i seguenti Cup Anomali: " + msgCupAnomali;
    	        messaggio = messaggio + msgCupAnomali;
    	      }
    	    }else{
    	    	CUPLOTTOType cuplotto = response.getCUPLOTTO();
    	    	hMapwsModificaLotto.put("cuplotto", cuplotto);
    	    }
    } else {
    	messaggio = "Errore imprevisto nel servizio di modificaLotto";
    }
    
    if (messaggio != null) {
    	logger.error("Il servizio di modifica dei dati del lotto ha risposto con il seguente messaggio: "
    	          + messaggio);
    	      throw new GestoreException(
    	          "Il servizio di modifica dei dati del lotto ha risposto con il seguente messaggio: "
    	              + messaggio, "gestioneIDGARACIG.ws.modificalotto.error",
    	          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsModificaLotto: fine metodo");

    return hMapwsModificaLotto;
  }

  /**
   * Chiamata al metodo cancellaLotto del WS SIMOG per inviare la richiesta di
   * cancellazione del lotto identificato dal codice CIG
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param cig
   * @param idmotivazione
   * @param notecanc
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   * @throws SQLException
   */
  private void wsCancellaLotto(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, String cig, String idmotivazione,
      String notecanc) throws ServiceException, RemoteException,
      GestoreException, SQLException {

    if (logger.isDebugEnabled())
      logger.debug("wsCancellaLotto: inizio metodo");

    String ticket = ((String) hMapwsLogin.get("ticket"));
    String index = ((String) hMapwsLogin.get("index"));

    CancellaLottoDocument cancellaLottoDoc = CancellaLottoDocument.Factory.newInstance();
    CancellaLotto cancellaLotto = CancellaLotto.Factory.newInstance();
    cancellaLotto.setTicket(ticket);
    cancellaLotto.setIndexCollaborazione(index);
    cancellaLotto.setIdMotivazione(idmotivazione);
    cancellaLotto.setNoteCanc(notecanc);
    cancellaLotto.setCig(cig);
    cancellaLottoDoc.setCancellaLotto(cancellaLotto);
    CancellaLottoResponseDocument cancellaResponseLottoDoc = simogWS.cancellaLotto(cancellaLottoDoc);
    CancellaLottoResponse responseCancellaLotto = cancellaResponseLottoDoc.getCancellaLottoResponse();

    String messaggio = null;
    if (responseCancellaLotto.isSetReturn()) {
    	ResponseCancellaLotto response = responseCancellaLotto.getReturn();
    	if (!response.getSuccess()) {
    		 messaggio = response.getError();
    	}
    } else {
    	messaggio = "Errore imprevisto nel servizio di cancellaLotto";
    }
    
    if (messaggio != null) {
    	logger.error("Il servizio di cancellazione del lotto ha risposto con il seguente messaggio: "
    	          + messaggio);
    	      throw new GestoreException(
    	          "Il servizio di cancellazione del lotto ha risposto con il seguente messaggio: "
    	              + messaggio, "gestioneIDGARACIG.ws.cancellalotto.error",
    	          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsCancellaLotto: fine metodo");

  }

  /**
   * Chiamata al metodo consultaGara del WS SIMOG per ottenere i dati della
   * gara, del lotto e delle eventuali schede associate al lotto identificato
   * dal codice CIG
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param cig
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private String wsConsultaGara(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, String cig) throws ServiceException,
      RemoteException, GestoreException {

    if (logger.isDebugEnabled()) logger.debug("wsConsultaGara: inizio metodo");

    String ticket = ((String) hMapwsLogin.get("ticket"));

    ConsultaGaraDocument consultaGaraDoc = ConsultaGaraDocument.Factory.newInstance();
    ConsultaGara consultaGara = ConsultaGara.Factory.newInstance();
    consultaGara.setTicket(ticket);
    consultaGara.setCIG(cig);
    consultaGara.setSchede("3.04.2.0");
    consultaGaraDoc.setConsultaGara(consultaGara);
    ConsultaGaraResponseDocument consultaGaraResponseDoc = simogWS.consultaGara(consultaGaraDoc);
    ConsultaGaraResponse responseConsultaGara = consultaGaraResponseDoc.getConsultaGaraResponse();
  
    String error = null;
    ResponseConsultaGara response = null;
    if (responseConsultaGara.isSetReturn()) {
    	response = responseConsultaGara.getReturn();
    	if (!response.getSuccess()) {
    		error = response.getError();
    	}
    } else {
    	error = "Errore imprevisto nel servizio di consultaGara";
    }
    
    if (error != null) {
    	logger.error("Il servizio di consultazione della gara ha risposto con il seguente messaggio: "
    	          + error);
    	      throw new GestoreException(
    	          "Il servizio di consultazione della gara ha risposto con il seguente messaggio: "
    	              + error, "gestioneIDGARACIG.ws.consultagaralotto.error",
    	          new Object[] { error }, null);
    }
    
    if (logger.isDebugEnabled()) logger.debug("wsConsultaGara: fine metodo");

    return response.getGaraXML().toString();

  }

  /**
   * Chiamata al metodo consultaGara del WS SIMOG per ottenere i dati della
   * gara, del lotto e delle eventuali schede associate al lotto identificato
   * dal codice CIG
   *
   * @param simogSmartCigWS
   * @param hMapwsLogin
   * @param smartcig
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private ComunicazioneType wsConsultaSmartCig(Services simogSmartCigWS,
      HashMap<String, Object> hMapwsLogin, String smartcig, Long syscon, String codrup) throws ServiceException,
      RemoteException, GestoreException {

    if (logger.isDebugEnabled()) logger.debug("wsConsultaSmartCig: inizio metodo");
    String messaggio = null;
    String ticket = ((String) hMapwsLogin.get("ticket"));
    Collaborazioni collaborazioni = ((Collaborazioni) hMapwsLogin.get("collaborazioni"));
    ConsultaComunicazioneRequest richiesta = new ConsultaComunicazioneRequest();
    richiesta.setCig(smartcig);
    ConsultaComunicazioneResponse comunicazioneResponse = null;
    LoggedUserInfoType user = new LoggedUserInfoType();
    
    user.setTicket(ticket);
    if (collaborazioni != null && collaborazioni.getCollaborazioniArray() != null && collaborazioni.getCollaborazioniArray().length>0) {
    	for(Collaborazione collaborazione:collaborazioni.getCollaborazioniArray()) {
        	user.setIndex(collaborazione.getIndex());
        	richiesta.setUser(user);
        	try {
        		comunicazioneResponse = simogSmartCigWS.consultaComunicazione(richiesta);
        		if (comunicazioneResponse.getComunicazione() != null) {
        			//Long idCollaborazione = getIndiceCollaborazione(collaborazione, syscon, codrup);
        			//hMapwsLogin.put("collaborazione", idCollaborazione);
        			break;
        		}
        	} catch (Exception ex) {
        		messaggio = ex.getMessage();
        	}
        }
    	if (comunicazioneResponse == null) {
    		throw new GestoreException(
    		          "Il servizio di consultazione dello SMARTCIG ha risposto con il seguente messaggio: "
    		              + messaggio, "gestioneIDGARACIG.ws.consultagaralotto.error",
    		          new Object[] { messaggio }, null);
    	}
    } else {
    	user.setIndex("0");
    	richiesta.setUser(user);
    	comunicazioneResponse = simogSmartCigWS.consultaComunicazione(richiesta);
    }

    if (comunicazioneResponse.getComunicazione() == null) {
    	RisultatoType risultato = comunicazioneResponse.getCodiceRisultato();
    	messaggio = risultato.getDescrizione().getValue();
    	logger.error("Il servizio di consultazione dello SMARTCIG ha risposto con il seguente messaggio: "
          + messaggio);
    	throw new GestoreException(
          "Il servizio di consultazione dello SMARTCIG ha risposto con il seguente messaggio: "
              + messaggio, "gestioneIDGARACIG.ws.consultagaralotto.error",
          new Object[] { messaggio }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsConsultaSmartCig: fine metodo");

    return comunicazioneResponse.getComunicazione();

  }
  
  /**
   * Ritorna l'indice di collaborazione utilizzato se non esiste lo crea
   *
   * @param collaborazione
   * @throws GestoreException
   */
  /*APPALTI-1063
  private Long getIndiceCollaborazione(Collaborazione collaborazione, Long syscon, String codrup) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("getIndiceCollaborazione: inizio metodo");
    Long result = 0L;
    try {
    	// Verifico se esiste gia' una collaborazione nell'archivio
        // generale
        Long indiceCollaborazione = (Long) sqlManager.getObject(
            "select ID from w3aziendaufficio where indexcoll = ? and ufficio_id = ?",
            new Object[] { collaborazione.getIndex(), collaborazione.getUfficioId() });
        
        if (indiceCollaborazione != null) {
        	result = indiceCollaborazione;
        } else {
        	TransactionStatus status = null;
        	try {
        		status = this.sqlManager.startTransaction();
            	// inserisco l'indice di collaborazione per il rup
            	Long id = (Long) sqlManager.getObject(
                        "select max(id) from w3aziendaufficio", new Object[] {});
                    if (id == null) id = 0L;
                    result = (long) (id.longValue() + 1);
                    sqlManager.update("insert into w3aziendaufficio (id, "
                        + "azienda_cf, "
                        + "azienda_denom, "
                        + "indexcoll, "
                        + "ufficio_denom, "
                        + "ufficio_id, "
                        + "ufficio_profilo) "
                        + "values (?,?,?,?,?,?,?)", new Object[] { result,
                        		collaborazione.getAziendaCodiceFiscale(), collaborazione.getAziendaDenominazione(), collaborazione.getIndex(),
                        		collaborazione.getUfficioDenominazione(), collaborazione.getUfficioId(), collaborazione.getUfficioProfilo() });
                    
                    sqlManager.update(
                            "insert into w3usrsyscoll (syscon, rup_codtec, w3aziendaufficio_id) values (?,?,?)",
                            new Object[] { syscon, codrup, result });
        	} catch(SQLException e) {
        		;
        	} finally {
               	try {
               		this.sqlManager.commitTransaction(status);
               	} catch (SQLException e) {
               		// TODO Auto-generated catch block
               		e.printStackTrace();
               	}
        	}
        }

    } catch (SQLException e) {
    	throw new GestoreException(
    	          "Si e' verificato un errore durante l'interazione con la base dati",
    	          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("getIndiceCollaborazione: fine metodo");

    return result;
  }*/
  
  /**
   * Chiamata la metodo pubblica del WS SIMOG per inviare richiesta di
   * pubblicazione della gara e dei lotti associati
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param numgara
   * @return
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private String wsPubblica(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, String dataPubblicazione,
      String dataScadenzaPagamenti, String cig, String progCui,
      DatiPubblicazione pubblicazione, String tipoOperazione, AllegatoType[] allegati,
      String oraScadenza, String dataScadenzaRichiestaInvito, String dataLetteraInvito, CUPLOTTOType[] cuplotti)
      throws ServiceException, RemoteException, GestoreException {

    if (logger.isDebugEnabled()) logger.debug("wsPubblica: inizio metodo");

    String ticket = ((String) hMapwsLogin.get("ticket"));
    String index = ((String) hMapwsLogin.get("index"));

    PubblicaDocument pubblicaDoc = PubblicaDocument.Factory.newInstance();
    Pubblica pubblica = Pubblica.Factory.newInstance();
    pubblica.setTicket(ticket);
    pubblica.setIndexCollaborazione(index);
    if (allegati != null) {
    	pubblica.setAllegatoArray(allegati);
    }
    pubblica.setCig(cig);
    if (cuplotti != null && cuplotti.length > 0) {
    	pubblica.setCUPLOTTOArray(cuplotti);
    }
    if (dataLetteraInvito != null && !dataLetteraInvito.equals("")) {
    	pubblica.setDataLetteraInvito(dataLetteraInvito);
    }
    if (dataPubblicazione != null && !dataPubblicazione.equals("")) {
    	pubblica.setDataPubblicazione(dataPubblicazione);
    }
    if (oraScadenza != null && !oraScadenza.equals("")) {
    	pubblica.setOraScadenza(oraScadenza);
    }
    if (progCui != null && !progCui.equals("")) {
    	pubblica.setProgCui(progCui);
    }
    if (tipoOperazione != null && !tipoOperazione.equals("")) {
    	pubblica.setTipoOperazione(tipoOperazione);
    }
    if (dataScadenzaPagamenti != null && !dataScadenzaPagamenti.equals("")) {
    	pubblica.setDataScadenzaPag(dataScadenzaPagamenti);
    }
    if (dataScadenzaRichiestaInvito != null && !dataScadenzaRichiestaInvito.equals("")) {
    	pubblica.setDataScadenzaRichiestaInvito(dataScadenzaRichiestaInvito);
    }
    if (pubblicazione != null) {
    	pubblica.setDatiPubblicazione(pubblicazione);
    }
    pubblicaDoc.setPubblica(pubblica);
    
    PubblicaResponseDocument pubblicaResponseDoc = simogWS.pubblica(pubblicaDoc);
    PubblicaResponse pubblicaResponse = pubblicaResponseDoc.getPubblicaResponse();
  
    String error = null;
    if (pubblicaResponse.isSetReturn()) {
    	ResponsePubblicazioneBando response = pubblicaResponse.getReturn();
    	if(!response.getSuccess()) {
    		error = response.getError();
    	} else {
    		try {
        		//Chiamo il consulta gara per ricavare i requisiti
    			ConsultaGaraDocument consultaGaraDoc = ConsultaGaraDocument.Factory.newInstance();
    			ConsultaGara consultaGara = ConsultaGara.Factory.newInstance();
    			consultaGara.setCIG(cig);
    			consultaGara.setTicket(ticket);
    			consultaGara.setSchede("3.04.0.0");
    			consultaGaraDoc.setConsultaGara(consultaGara);
    			ConsultaGaraResponseDocument  responseConsultaGaraDoc = simogWS.consultaGara(consultaGaraDoc);
    			ConsultaGaraResponse consultaGaraResponse = responseConsultaGaraDoc.getConsultaGaraResponse();
    			if (consultaGaraResponse.isSetReturn()) {
    				ResponseConsultaGara responseCG = consultaGaraResponse.getReturn();
    				if (responseCG.getSuccess()) {
    					SchedaType scheda = responseCG.getGaraXML();
    					DatiGaraType datiGaraLotto = scheda.getDatiGara();
    					Long idgara =  datiGaraLotto.getGara().getIDGARA();
    		           	Long numgara = (Long) this.sqlManager.getObject("select numgara from w3gara where id_gara = ?", new Object[] { idgara });
    		           	if (numgara != null) {
    		           		ReqGaraType[] requisitiGara = datiGaraLotto.getRequisitoArray();
    		           		// Inserimento requisiti
    		           		if (requisitiGara != null) {
    		           			this.gestioneXMLIDGARACIGManager.inserisciW3GARAREQdaSIMOG(requisitiGara, numgara);
    		           		}
    		            }
    				}
    			}
        	} catch (Exception e) {
        		;
        	}
    	}
    } else {
    	error = "Errore imprevisto nel servizio di pubblica";
    }
    if (error != null) {
      //APPALTI-1085
      if(error.indexOf(GARAGIAPUBBLICATA)>-1) {
        try {
          //recupero la data di perfezionamento
          Date dataConfermaGara = new Date();
          ConsultaNumeroGaraDocument consultaNGaraDoc = ConsultaNumeroGaraDocument.Factory.newInstance();
          ConsultaNumeroGara consultaNGara = ConsultaNumeroGara.Factory.newInstance();
          consultaNGara.setTicket(ticket);
          consultaNGara.setIdGara(cig);
          consultaNGara.setSchede("3.04.2.0");
          consultaNGaraDoc.setConsultaNumeroGara(consultaNGara);
          ConsultaNumeroGaraResponseDocument consultaNGaraResponseDoc = simogWS.consultaNumeroGara(consultaNGaraDoc);
          
          ConsultaNumeroGaraResponse responseConsultaNumeroGara = consultaNGaraResponseDoc.getConsultaNumeroGaraResponse();
          
          if (responseConsultaNumeroGara.isSetReturn()) {
              ResponseConsultaNumeroGara responseCG = responseConsultaNumeroGara.getReturn();
              if (responseCG.getSuccess()) {
                  dataConfermaGara = responseCG.getSchedaGaraCig().getGara().getDATACONFERMAGARA().getTime();
              }
          }
          String sql = "update w3gara set stato_simog=?, data_conferma_gara = ? where id_gara = ? ";
          this.sqlManager.update(sql, new Object[] {7L, new Timestamp(dataConfermaGara.getTime()), cig});
          
          Date data_perfezionamento_bando = (Date) this.sqlManager.getObject("select data_perfezionamento_bando from w3gara where id_gara = ?", new Object[] {cig});
          String updateW3LOTT = "update w3lott set stato_simog = ?, data_pubblicazione = ? where numgara in (select numgara from w3gara where id_gara = ?) and stato_simog in (2,4)";
          this.sqlManager.update(updateW3LOTT, new Object[] { 7L, data_perfezionamento_bando, cig });
        }catch (Exception e) {
          ;
      }
        return GARAGIAPUBBLICATA;
      }
      //APPALTI-1085
    	logger.error("Il servizio di pubblicazione ha risposto con il seguente messaggio: "
    	          + error);
    	      throw new GestoreException(
    	          "Il servizio di pubblicazione ha risposto con il seguente messaggio: "
    	              + error, "gestioneIDGARACIG.ws.pubblica.error",
    	          new Object[] { error }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsPubblica: fine metodo");

    return "";

  }

  /**
   * Chiamata al metodo invioRequisiti del WS SIMOG per inviare i requisiti
   * (restituisce identificativo gara (IDGARA))
   *
   * @param simogWS
   * @param hMapwsLogin
   * @param datiRequisitiXML
   * @return hMapwsInviaRequisiti
   * @throws ServiceException
   * @throws RemoteException
   * @throws GestoreException
   */
  private HashMap<String, Object> wsInviaRequisiti(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, Requisiti datiRequisiti, String idGara)
      throws ServiceException, RemoteException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("wsInviaRequisiti: inizio metodo");
    HashMap<String, Object> hMapwsInviaRequisiti = new HashMap<String, Object>();

    String idgara = idGara;
    String ticket = ((String) hMapwsLogin.get("ticket"));
    String index = ((String) hMapwsLogin.get("index"));
    hMapwsInviaRequisiti.put("idgara", idgara);
    
    InviaRequisitiDocument inviaRequisitiDoc = InviaRequisitiDocument.Factory.newInstance();
    InviaRequisiti inviaRequisiti = InviaRequisiti.Factory.newInstance();
    inviaRequisiti.setTicket(ticket);
    inviaRequisiti.setIndexCollaborazione(index);
    inviaRequisiti.setRequisiti(datiRequisiti);
    inviaRequisiti.setIdGara(idgara);
    inviaRequisitiDoc.setInviaRequisiti(inviaRequisiti);
    logger.error(inviaRequisitiDoc);
    InviaRequisitiResponseDocument inviaRequisitiResponseDoc = simogWS.inviaRequisiti(inviaRequisitiDoc);
    InviaRequisitiResponse responseInviaRequisiti = inviaRequisitiResponseDoc.getInviaRequisitiResponse();
   
    String errore = null;
    String messaggio = null;
    if (responseInviaRequisiti.isSetReturn()) {
    	ResponseInviaRequisiti response = responseInviaRequisiti.getReturn();
    	if (response.getSuccess()) {
    		messaggio = response.getMessaggio();
    	    hMapwsInviaRequisiti.put("messaggio", messaggio);
    	} else {
    		errore = response.getError();
    	}
    } else {
    	errore = "Errore imprevisto nel servizio di inviaRequisiti";
    }
    
    if (errore != null) {
    	hMapwsInviaRequisiti.put("messaggio", errore);
        logger.error("Il servizio di invio requisiti ha risposto con il seguente messaggio: "
            + messaggio);
        throw new GestoreException(
            "Il servizio di invio requisiti ha risposto con il seguente messaggio: "
                + errore, "gestioneIDGARACIG.ws.inviorequisiti.error",
            new Object[] { errore }, null);
    }

    if (logger.isDebugEnabled()) logger.debug("wsInviaRequisiti: fine metodo");

    return hMapwsInviaRequisiti;

  }


  /**
   * Collegamento al WS SIMOG. Ottiene l'oggetto SimogWS
   *
   * @return
   * @throws ServiceException
   * @throws GestoreException
 * @throws AxisFault 
   */
  private SimogWSPDDServiceStub getSimogWS() throws ServiceException, GestoreException, AxisFault {

    if (logger.isDebugEnabled()) logger.debug("getSimogWS: inizio metodo");

    // Indirizzo web service
    String url = ConfigManager.getValore(PROP_SIMOG_WS_URL);
    if (url == null || "".equals(url)) {
      throw new GestoreException(
          "L'indirizzo per la connessione al web service di elaborazione degli identificativi di gara non e' definito",
          "gestioneIDGARACIG.ws.url.error");
    }


    SimogWSPDDServiceStub simogWS = new SimogWSPDDServiceStub(url);

    if (logger.isDebugEnabled()) logger.debug("getSimogWS: fine metodo");

    return simogWS;

  }

  /**
   * Collegamento al WS SIMOG per gestione SMARTCIG. Ottiene l'oggetto Service
   *
   * @return
   * @throws ServiceException
   * @throws GestoreException
   */
  private Services getSimogWSSmartCig() throws ServiceException, GestoreException {

    if (logger.isDebugEnabled()) logger.debug("getSimogWSSmartCig: inizio metodo");

    // Indirizzo web service
    String url = ConfigManager.getValore(PROP_SIMOG_WS_SMARTCIG_URL);
    if (url == null || "".equals(url)) {
      throw new GestoreException(
          "L'indirizzo per la connessione al web service di elaborazione dei smartcig non e' definito",
          "gestioneIDGARACIG.ws.url.error");
    }


    ServicesServiceLocator WSLocator = new ServicesServiceLocator();
    WSLocator.setServicesSoap11EndpointAddress(url);
    Services simogWS = WSLocator.getServicesSoap11();

    if (logger.isDebugEnabled()) logger.debug("getSimogWSSmartCig: fine metodo");

    return simogWS;

  }
  
  /**
   * Recupero delle credenziali di accesso al WS SIMOG
   *
   * @param codrup
   * @return
   * @throws GestoreException
   */
  public HashMap<String, String> recuperaSIMOGWSUserPass(String codrup)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("recuperaSIMOGWSUserPass: inizio metodo");

    HashMap<String, String> hMapSIMOGWSUserPass = new HashMap<String, String>();

    try {
      List<?> datiW9LOADER_APPALTO_USR = this.sqlManager.getVector(
          "select simoguser, simogpass from W9LOADER_APPALTO_USR where "
          + "syscon in (select syscon from usrsys where syscf in (select cftec from tecni where codtec = ?))",
          new Object[] {codrup });

      if (datiW9LOADER_APPALTO_USR != null && datiW9LOADER_APPALTO_USR.size() > 0) {

        // Utente
        String simogwsuser = (String) SqlManager.getValueFromVectorParam(
            datiW9LOADER_APPALTO_USR, 0).getValue();
        hMapSIMOGWSUserPass.put("simogwsuser", simogwsuser);

        // Password
        String simogwspass = (String) SqlManager.getValueFromVectorParam(
            datiW9LOADER_APPALTO_USR, 1).getValue();
        String simogwspassDecriptata = null;
        if (simogwspass != null && simogwspass.trim().length() > 0) {
          ICriptazioneByte simogwspassICriptazioneByte = null;
          simogwspassICriptazioneByte = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
              simogwspass.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
          simogwspassDecriptata = new String(
              simogwspassICriptazioneByte.getDatoNonCifrato());
        }
        hMapSIMOGWSUserPass.put("simogwspass", simogwspassDecriptata);

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella gestione delle credenziali di accesso al servizio di elaborazione degli identificativi di gara",
          "gestioneSIMOGWSUserPass.error", e);

    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Errore nella gestione delle credenziali di accesso al servizio di elaborazione degli identificativi di gara",
          "gestioneSIMOGWSUserPass.error", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("recuperaSIMOGWSUserPass: fine metodo");

    return hMapSIMOGWSUserPass;

  }
  
  /**
   * Recupero delle credenziali di accesso al WS SIMOG
   *
   * @param codrup
   * @return
   * @throws GestoreException
   */
  public HashMap<String, String> recuperaSIMOGWSUserPassFromSyscon(Long syscon)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("recuperaSIMOGWSUserPass: inizio metodo");

    HashMap<String, String> hMapSIMOGWSUserPass = new HashMap<String, String>();

    try {
      List<?> datiW9LOADER_APPALTO_USR = this.sqlManager.getVector(
          "select simoguser, simogpass from W9LOADER_APPALTO_USR where syscon = ?",
          new Object[] {syscon });

      if (datiW9LOADER_APPALTO_USR != null && datiW9LOADER_APPALTO_USR.size() > 0) {

        // Utente
        String simogwsuser = (String) SqlManager.getValueFromVectorParam(
            datiW9LOADER_APPALTO_USR, 0).getValue();
        hMapSIMOGWSUserPass.put("simogwsuser", simogwsuser);

        // Password
        String simogwspass = (String) SqlManager.getValueFromVectorParam(
            datiW9LOADER_APPALTO_USR, 1).getValue();
        String simogwspassDecriptata = null;
        if (simogwspass != null && simogwspass.trim().length() > 0) {
          ICriptazioneByte simogwspassICriptazioneByte = null;
          simogwspassICriptazioneByte = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
              simogwspass.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
          simogwspassDecriptata = new String(
              simogwspassICriptazioneByte.getDatoNonCifrato());
        }
        hMapSIMOGWSUserPass.put("simogwspass", simogwspassDecriptata);

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella gestione delle credenziali di accesso al servizio di elaborazione degli identificativi di gara",
          "gestioneSIMOGWSUserPass.error", e);

    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Errore nella gestione delle credenziali di accesso al servizio di elaborazione degli identificativi di gara",
          "gestioneSIMOGWSUserPass.error", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("recuperaSIMOGWSUserPass: fine metodo");

    return hMapSIMOGWSUserPass;

  }

  /**
   * Memorizzazione delle credenziali di accesso al WS SIMOG
   *
   * @param syscon
   * @param codrup
   * @param simogwsuser
   * @param simogwspass
   * @throws GestoreException
   */
  public void memorizzaSIMOGWSUserPass(Long syscon, String simogwsuser,
      String simogwspass) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("memorizzaSIMOGWSUserPass: inizio metodo");

    try {
      // Password
      String simogwspassCriptata = null;
      if (simogwspass != null && simogwspass.trim().length() > 0) {
        ICriptazioneByte simogwspassICriptazioneByte = null;
        simogwspassICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            simogwspass.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        simogwspassCriptata = new String(
            simogwspassICriptazioneByte.getDatoCifrato());
      }

//      this.sqlManager.update(
//              "update W9LOADER_APPALTO_USR set simogwsuser = ?, simogwspass = ? where syscon = ? and rup_codtec = ?",
//              new Object[] { simogwsuser, simogwspassCriptata, syscon});
      
      // Controllo se esiste gia' una riga con lo stesso SYSCON
      Long conteggio = (Long) this.sqlManager.getObject(
          "select count(*) from W9LOADER_APPALTO_USR where syscon = ?",
          new Object[] { syscon });
      if (conteggio != null && conteggio.longValue() > 0) {
        this.sqlManager.update(
            "update W9LOADER_APPALTO_USR set simoguser = ?, simogpass = ? where syscon = ?",
            new Object[] { simogwsuser, simogwspassCriptata, syscon });
      } else {
        this.sqlManager.update(
            "insert into W9LOADER_APPALTO_USR (simoguser, simogpass, syscon) values (?,?,?)",
            new Object[] { simogwsuser, simogwspassCriptata, syscon });
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella gestione delle credenziali di accesso al servizio di elaborazione degli identificativi di gara",
          "gestioneSIMOGWSUserPass.error", e);

    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Errore nella gestione delle credenziali di accesso al servizio di elaborazione degli identificativi di gara",
          "gestioneSIMOGWSUserPass.error", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("memorizzaSIMOGWSUserPass: fine metodo");

  }

  /*
   * Cancellazione delle credenziali di accesso al WS SIMOG
   *
   * @param syscon
   * @param codrup 
   * @throws GestoreException
  
  public void cancellaSIMOGWSUserPass(Long syscon, String codrup) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("cancellaSIMOGWSUserPass: inizio metodo");

    try {
      this.sqlManager.update("delete from w3usrsys where syscon = ? and RUP_CODTEC = ?",
          new Object[] { syscon, codrup });

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella gestione delle credenziali di accesso al servizio di elaborazione degli identificativi di gara",
          "gestioneSIMOGWSUserPass.error", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("cancellaSIMOGWSUserPass: fine metodo");

  } */
  
  public void cancellaSIMOGWSUserPass(Long syscon) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("cancellaSIMOGWSUserPass: inizio metodo");

    try {
      this.sqlManager.update("delete from w9loader_appalto_usr where syscon = ? ",
          new Object[] { syscon});

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella gestione delle credenziali di accesso al servizio di elaborazione degli identificativi di gara",
          "gestioneSIMOGWSUserPass.error", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("cancellaSIMOGWSUserPass: fine metodo");

  }

  /**
   * Chiamata al metodo chiudiSessione del WS SIMOG per comunicare la chiusura
   * della sessione
   *
   * @param simogWS
   * @param hMapwsLogin
   */
  private void wsChiudiSessione(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin) {

    if (logger.isDebugEnabled())
      logger.debug("wsChiudiSessione: inizio metodo");

    // In caso di errore si scrive solamente sul log.
    // Non deve essere scatenata alcuna ulteriore segnalazione di errore
    // in modo da non scartare tutta la transazione
    try {
      String ticket = ((String) hMapwsLogin.get("ticket"));
      if (ticket != null) {
    	  ChiudiSessione chiudiSessione = ChiudiSessione.Factory.newInstance();
          chiudiSessione.setTicket(ticket);
        
          ChiudiSessioneDocument chiudiSessioneDoc = ChiudiSessioneDocument.Factory.newInstance();
          chiudiSessioneDoc.setChiudiSessione(chiudiSessione);
         
          ChiudiSessioneResponseDocument chiudiSessioneResponseDoc = 
        	  simogWS.chiudiSessione(chiudiSessioneDoc);
          logger.info("Invocazione del metodo chiudiSessione verso i Servizi SIMOG (Ticket="
              + ticket + ")");
          ResponseChiudiSession responseChiudiSessione = 
              chiudiSessioneResponseDoc.getChiudiSessioneResponse().getReturn();
          logger.info("Invocato il metodo chiudiSessione verso i Servizi SIMOG (Ticket="
              + ticket + ")");
          
          if (!responseChiudiSessione.getSuccess()) {
            logger.error("La chiusura della connessione identificata dal ticket "
                + ticket
                + " ha generato il seguente errore: "
                + responseChiudiSessione.getError());
          } else {
            logger.info("Logout al WS SIMOG avvenuta con successo. La sessione con ticket "
                + ticket
                + " e' stata chiusa.");
            ticket = null;
          }
      }
    } catch (Throwable t) {

    }
    if (logger.isDebugEnabled()) logger.debug("wsChiudiSessione: fine metodo");

  }
  
  /**
   * Consultazione della gara e dei lotti
   *
   * @param simogwsuser
   * @param simogwspass
   * @param cig
   * @throws GestoreException
   */
  public HashMap<String, Object> consultaLottiGara(String simogwsuser,
      String simogwspass, Long numgara, boolean rpntFailed) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("consultaGARALOTTO: inizio metodo");

    SimogWSPDDServiceStub simogWS = null;
    HashMap<String, Object> hMapwsLogin = new HashMap<String, Object>();
    
    HashMap<String, Object> hMapwsLottiNonPresenti = new HashMap<String, Object>();
    
    try {
      String idgara = (String) this.sqlManager.getObject(
          "select id_gara from w3gara where numgara = ?", new Object[] { numgara });
      
      simogWS = this.getSimogWS();
      hMapwsLogin = this.wsLogin(simogWS, simogwsuser, simogwspass,rpntFailed);
      String[] cigArray = this.wsConsultaNumeroGara(simogWS, hMapwsLogin, idgara);
      for(String cig : cigArray) {
        if (!this.esisteCIG(cig)) { //se non esiste in base dati, vuol dire che il cig devo approfondire la valutazione
          String datiGaraLottoXML = this.wsConsultaGara(simogWS, hMapwsLogin, cig); 
          SchedaType schedaDatiSIMOG = SchedaType.Factory.parse(datiGaraLottoXML);
          LottoType lotto = schedaDatiSIMOG.getDatiGara().getLotto();
          if(!schedaDatiSIMOG.getDatiGara().getGara().isSetDATACANCELLAZIONEGARA()) { //se la gara non è cancellata, allora potrebbe essere da importare 
            List<String> datiScheda = new ArrayList<String>();
            datiScheda.add(UtilityNumeri.convertiImporto(lotto.getIMPORTOLOTTO().doubleValue(),2));
            datiScheda.add(lotto.getOGGETTO());
            Date tempDate = new Date(((Calendar) lotto.getDATACREAZIONELOTTO()).getTimeInMillis());
            datiScheda.add(UtilityDate.convertiData(tempDate, UtilityDate.FORMATO_GG_MM_AAAA));
            hMapwsLottiNonPresenti.put(cig, datiScheda);
          }
        }
      }
    }catch(SQLException e){
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    } 
    catch (AxisFault e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);
    } catch (ServiceException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);
    } catch (RemoteException e) {
        throw new GestoreException( "Si e' verificato un errore durante l'interazione con il servizio di elaborazione degli identificativi di gara",
        "gestioneIDGARACIG.ws.remote.error", e);
    } catch (XmlException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'elaborazione degli identificativi di gara",
          "gestioneIDGARACIG.ws.error", e);
    } catch (CriptazioneException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }
    finally {
    
      this.wsChiudiSessione(simogWS, hMapwsLogin);
    }
    if (logger.isDebugEnabled())
      logger.debug("consultaGARALOTTO: fine metodo");

    return hMapwsLottiNonPresenti;

  }
  
  private String[] wsConsultaNumeroGara(SimogWSPDDServiceStub simogWS,
      HashMap<String, Object> hMapwsLogin, String idGara) throws ServiceException,
      RemoteException, GestoreException {

    if (logger.isDebugEnabled()) logger.debug("wsConsultaNumeroGara: inizio metodo");

    String ticket = ((String) hMapwsLogin.get("ticket"));
    
    ConsultaNumeroGaraDocument consultaNGaraDoc = ConsultaNumeroGaraDocument.Factory.newInstance();
    ConsultaNumeroGara consultaNGara = ConsultaNumeroGara.Factory.newInstance();
    consultaNGara.setTicket(ticket);
    consultaNGara.setIdGara(idGara);
    consultaNGara.setSchede("3.04.2.0");
    consultaNGaraDoc.setConsultaNumeroGara(consultaNGara);
    ConsultaNumeroGaraResponseDocument consultaNGaraResponseDoc = simogWS.consultaNumeroGara(consultaNGaraDoc);
    
    ConsultaNumeroGaraResponse responseConsultaNumeroGara = consultaNGaraResponseDoc.getConsultaNumeroGaraResponse();
  
    String error = null;
    ResponseConsultaNumeroGara response = null;
    if (responseConsultaNumeroGara.isSetReturn()) {
        response = responseConsultaNumeroGara.getReturn();
        if (!response.getSuccess()) {
            error = response.getError();
        }
    } else {
        error = "Errore imprevisto nel servizio di consultaNumeroGara";
    }
    
    if (error != null) {
        logger.error("Il servizio di consultazione della gara ha risposto con il seguente messaggio: "
                  + error);
              throw new GestoreException(
                  "Il servizio di consultazione del numero della gara ha risposto con il seguente messaggio: "
                      + error, "gestioneIDGARACIG.ws.consultagaralotto.error",
                  new Object[] { error }, null);
    }
    
    if (logger.isDebugEnabled()) logger.debug("wsConsultaNumeroGara: fine metodo");

    return response.getSchedaGaraCig().getCIGArray();
  }
  
  public void aggiornaW3LOTTCIGManuale(Long numgara, Long numlott, String cig, Date dataCreazioneLotto)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("collegaW3LOTTCIG: inizio metodo");

    try {
      if(cig!=null){
        String updateW3LOTT = "update w3lott set cig = ?, data_creazione_lotto = ?, stato_simog = ? where numgara = ? and numlott = ?";
        this.sqlManager.update(updateW3LOTT, new Object[] { cig,new Timestamp(dataCreazioneLotto.getTime()), 3L, numgara, numlott });
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Si e' verificato un errore durante l'interazione con la base dati",
          "gestioneIDGARACIG.sqlerror", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("collegaW3LOTTCIG: fine metodo");

  }
}

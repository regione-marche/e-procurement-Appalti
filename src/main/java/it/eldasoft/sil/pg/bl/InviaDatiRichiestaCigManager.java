package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.simog.ws.EldasoftSimogWS;
import it.eldasoft.simog.ws.EldasoftSimogWSServiceLocator;
import it.eldasoft.simog.ws.EsitoConsultaCIG;
import it.eldasoft.simog.ws.EsitoConsultaIDGARA;
import it.eldasoft.simog.ws.EsitoInserisciGaraLotto;
import it.eldasoft.simog.ws.EsitoInserisciSmartCIG;
import it.eldasoft.simog.ws.xmlbeans.CategorieMerceologicheSmartCIGType;
import it.eldasoft.simog.ws.xmlbeans.CategorieMerceologicheType;
import it.eldasoft.simog.ws.xmlbeans.CategorieType;
import it.eldasoft.simog.ws.xmlbeans.CondizioneNegoziataType;
import it.eldasoft.simog.ws.xmlbeans.GaraDocument;
import it.eldasoft.simog.ws.xmlbeans.GaraType;
import it.eldasoft.simog.ws.xmlbeans.ListaCUPType;
import it.eldasoft.simog.ws.xmlbeans.LottoType;
import it.eldasoft.simog.ws.xmlbeans.LuogoNUTSType;
import it.eldasoft.simog.ws.xmlbeans.LuogoNUTSType.Enum;
import it.eldasoft.simog.ws.xmlbeans.SmartCIGDocument;
import it.eldasoft.simog.ws.xmlbeans.SmartCIGType;
import it.eldasoft.simog.ws.xmlbeans.SnType;
import it.eldasoft.simog.ws.xmlbeans.TecnicoType;
import it.eldasoft.simog.ws.xmlbeans.TipologiaFornituraType;
import it.eldasoft.simog.ws.xmlbeans.TipologiaLavoroType;
import it.eldasoft.simog.ws.xmlbeans.W3005Type;
import it.eldasoft.simog.ws.xmlbeans.W3999Type;
import it.eldasoft.simog.ws.xmlbeans.W3Z05Type;
import it.eldasoft.simog.ws.xmlbeans.W3Z08Type;
import it.eldasoft.simog.ws.xmlbeans.W3Z20Type;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityFiscali;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;


public class InviaDatiRichiestaCigManager {

  static Logger               logger                         = Logger.getLogger(InviaDatiRichiestaCigManager.class);

  private static final String ERRORI_NON_BLOCCANTI_KEY          = "erroriNonBloccanti";

  private static final String ERRORI_BLOCCANTI_KEY              = "erroriBloccanti";

  private static final String PROP_INVIO_DATI_CIG_WS_URL        = "it.eldasoft.inviodaticig.ws.url";

  private static final String CTR_NESSUN_LOTTO                  = "Non sono stati definiti i lotti della gara";

  private static final String CTR_GARA_OGGETTO                  = "L'oggetto della gara non e' valorizzato";

  private static final String CTR_LOTTI_OGGETTO                 = "L'oggetto della gara non è valorizzato nei lotti ";

  private static final String CTR_GARA_RUP                      = "Il riferimento al responsabile unico procedimento non e' valorizzato";

  private static final String CTR_GARA_IMPORTO                  = "L'importo a base di gara non e' valorizzato";

  private static final String CTR_LOTTI_IMPORTO                 = "L'importo a base di gara non è valorizzato nei lotti ";

  private static final String CTR_RUP_INTESTAZIONE              = "Il nome del responsabile unico procedimento non e' valorizzato";

  private static final String CTR_RUP_CF                        = "Il codice fiscale del responsabile unico procedimento non e' valorizzato";

  private static final String CTR_RUP_CF_NON_VALIDO             = "Il codice fiscale del responsabile unico procedimento non ha un formato valido";

  private static final String CTR_RUP_PIVA_NON_VALIDA           = "La partita Iva del responsabile unico procedimento non ha un formato valido";

  private static final String CTR_GARE_TORN_TIPGAR              = "Il tipo procedura non è valorizzato";

  private static final String CTR_MODREA                        = "La modalità di realizzazione non è valorizzata";

  private static final String CTR_TIPLAV                        = "La tipologia lavoro non è valorizzata";

  private static final String CTR_TIPLAV_LOTTI                  = "La tipologia lavoro non è valorizzata nei lotti ";

  private static final String CTR_OGGCONT                       = "L'oggetto contratto non è valorizzato";

  private static final String CTR_OGGCONT_LOTTI                 = "L'oggetto contratto non è valorizzato nei lotti ";

  private static final String CTR_CPV                           = "Il codice CPV non è valorizzato";

  private static final String CTR_CPV_LOTTI                     = "Il codice CPV non è valorizzato nei lotti ";

  private static final String CTR_CPV_NON_VALIDO                = "Il codice CPV deve essere dettagliato almeno fino al terzo livello";

  private static final String CTR_CPV_NON_VALIDO_LOTTI          = "Il codice CPV deve essere dettagliato almeno fino al terzo livello nei lotti ";

  private static final String CTR_DONUTS                        = "Il codice NUTS non è valorizzato";

  private static final String CTR_SA_CF                         = "Il codice fiscale della stazione appaltante non e' valorizzato";

  private static final String CTR_SA                            = "Il riferimento alla stazione appaltante non e' valorizzato";

  private static final String CTR_SA_CF_NON_VALIDO              = "Il codice fiscale della stazione appaltante non ha un formato valido";

  private static final String CTR_CUI                           = "Il codice CUI non è valorizzato. Procedendo la procedura verrà intesa come non inserita nella programmazione";

  private static final String CTR_CUPPRG                        = "Il codice CUP di progetto non è valorizzato. Procedendo la procedura verrà intesa come esente CUP";

  private static final String CTR_CUI_LOTTI                     = "Il codice CUI non è valorizzato nei lotti ";

  private static final String CTR_CUPPRG_LOTTI                  = "Il codice CUP di progetto non è valorizzato nei lotti ";

  private static final String CTR_GARE_TORN_MAP_TIPGAR_A1z11    = "Il tipo procedura non risulta mappato nei valori di scelta contraente SIMOG (verificare il parametro A1z11)";
  private static final String CTR_GARE_TORN_MAP_TIPGAR_A1z05    = "Il tipo procedura non risulta mappato nei valori di scelta contraente L.190/2012 (verificare il parametro A1z05)";

  private static final String UUID_CONSULTA                     = "CONSULTA";
  private static final String UUID_RICHIESTA                    = "RICHIESTA";

  private static final String XML_MAP_KEY                       = "XML";
  private static final String LOTTO_MAP_KEY                     = "LOTTO";
  private static final String UUID_MAP_KEY                      = "UUID";
  private static final String LISTA_LOTTI_MAP_KEY               = "LISTALOTTI";
  private static final String NGARA_MAP_KEY                     = "NGARA";

  private SqlManager          sqlManager;

  private GeneManager          geneManager;

  private InviaVigilanzaManager  inviaVigilanzaManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setInviaVigilanzaManager(InviaVigilanzaManager inviaVigilanzaManager) {
    this.inviaVigilanzaManager = inviaVigilanzaManager;
  }


  private String getUrl() throws GestoreException{
    String url = ConfigManager.getValore(PROP_INVIO_DATI_CIG_WS_URL);
    if (url == null || "".equals(url)) {
      throw new GestoreException(
          "L'indirizzo per la connessione al web service non e' definito",
          "inviadatirichiestacig.ws.url");
    }
    return url;
  }

  /**
   *
   * @param url
   * @return EldasoftSimogWS
   * @throws ServiceException
   */
  private EldasoftSimogWS getWs(String url) throws ServiceException{
    EldasoftSimogWSServiceLocator locator = new EldasoftSimogWSServiceLocator();
    locator.setEldasoftSimogWSEndpointAddress(url);
    locator.getEngine().setOption(MessageContext.HTTP_TRANSPORT_VERSION, HTTPConstants.HEADER_PROTOCOL_V11);
    EldasoftSimogWS servizio = locator.getEldasoftSimogWS();
    return servizio;
  }

  /**
   *
   * @param codgar
   * @param numeroLotto
   * @param genere
   * @param username
   * @param password
   * @return
   * @throws Exception
   * @throws Throwable
   */

  @SuppressWarnings("unchecked")
  public String inviaDatiRichiestaCig(String codgar,String numeroLotto,String genere, String username, String password) throws Exception, Throwable {

	  if (logger.isDebugEnabled())
		  logger.debug("inviaDatiRichiestaCig: inizio metodo");

	  String esitoWS =  null;
	  String url = this.getUrl();
      String uuid = null;

	    try {
	      EldasoftSimogWS servizio = this.getWs(url);

		        EsitoInserisciGaraLotto esitoSimogWS = new EsitoInserisciGaraLotto();
		        HashMap<String,Object> hm = this.getXMLDatiGaraCig(codgar,numeroLotto, genere);
                String xml = (String) hm.get(XML_MAP_KEY);
                uuid = (String) hm.get(UUID_MAP_KEY);
                ArrayList<HashMap> listaLotti = (ArrayList<HashMap>) hm.get(LISTA_LOTTI_MAP_KEY);
                if (xml != null) {
                  logger.debug("inviaDatiRichiestaCig: XML = \n" + xml);
                  esitoSimogWS = servizio.inserisciGaraLotto(username, password, xml);
                  if(esitoSimogWS.isEsito()){
                    if(esitoSimogWS.getOperazioniDML()!=null ){
                        String tipoInformazione = null;
                        String tipoOperazione =  null;
                        esitoWS = "";

                         for(int i=0; i < esitoSimogWS.getOperazioniDML().length; i++){
                            esitoSimogWS.getOperazioniDML(i);
                            tipoInformazione=esitoSimogWS.getOperazioniDML(i).getTipoInformazione().toString();
                            tipoOperazione = esitoSimogWS.getOperazioniDML(i).getTipoOperazione().toString();
                            String strEsitoWS = "";
                            if("GARA".equals(tipoInformazione)){
                                if("2".equals(genere)){
                                    if("INS".equals(tipoOperazione))
                                        strEsitoWS = "E' stata inserita la gara "+numeroLotto;
                                    if("UPD".equals(tipoOperazione))
                                        strEsitoWS = "E' stata aggiornata la gara "+numeroLotto;
                                    if("DEL".equals(tipoOperazione))
                                        strEsitoWS = "E' stata eliminata la gara "+numeroLotto;
                                }else{
                                    if("INS".equals(tipoOperazione))
                                        strEsitoWS = "E' stata inserita la gara "+codgar;
                                    if("UPD".equals(tipoOperazione))
                                        strEsitoWS = "E' stata aggiornata la gara "+codgar;
                                    if("DEL".equals(tipoOperazione))
                                        strEsitoWS = "E' stata eliminata la gara "+codgar;
                                }
                            }
                            if("LOTTO".equals(tipoInformazione)){
                                if("2".equals(genere)){
                                     if("INS".equals(tipoOperazione) && "".equals(esitoWS))
                                        strEsitoWS = "E' stata inserita la gara "+numeroLotto;
                                     if("UPD".equals(tipoOperazione) && "".equals(esitoWS))
                                         strEsitoWS = "E' stata aggiornata la gara "+numeroLotto;
                                     if("DEL".equals(tipoOperazione) && "".equals(esitoWS))
                                         strEsitoWS = "E' stata eliminata la gara "+numeroLotto;
                                }else{
                                    if("INS".equals(tipoOperazione))
                                        strEsitoWS = "E' stato inserito un lotto nella gara "+codgar;
                                    if("UPD".equals(tipoOperazione))
                                        strEsitoWS = "E' stato aggiornato un lotto della gara "+codgar;
                                    if("DEL".equals(tipoOperazione))
                                        strEsitoWS = "E' stato eliminato un lotto della gara "+codgar;
                                }
                            }

                        esitoWS=esitoWS+strEsitoWS+"\n";

                         }//for

                    }else{
                        esitoWS="Non sono state effettuate modifiche"+"\n"+"ai dati della gara corrente"+"\n";
                    }
                    //flagEsito = "OK";

                    sqlManager.update("update torn set uuid=? where codgar=?",new Object[]{uuid,codgar});
                    for(int n=0; n<listaLotti.size();n++){
                      HashMap<String,String> lotto = listaLotti.get(n);
                      String ngara = lotto.get(NGARA_MAP_KEY);
                      String uuidLotto = lotto.get(UUID_MAP_KEY);
                      sqlManager.update("update gare1 set uuid=? where ngara=?",new Object[]{uuidLotto,ngara});
                    }

                  }else{
                    esitoWS = esitoSimogWS.getMessaggio();
                    //flagEsito = "KO";
                  }//if esito

                }//if xml

                // if (flagEsito=="KO" )
                //     throw new Exception(esitoWS);
	    	} catch (RemoteException e) {
	          throw new GestoreException(
	              "Si e' verificato un errore durante la connessione al web service!",
	              "errors.RichiestaCig.ws.error", e);
	        } catch (ServiceException e) {
	          throw new GestoreException(
	              "Si e' verificato un errore durante la connessione al web service!",
	              "errors.RichiestaCig.ws.error", e);
	        }catch (Throwable t) {
	            throw t;
	        }




	  if (logger.isDebugEnabled())
		  logger.debug("inviaDatiRichiestaCig: fine metodo");
	  if("\n".equals(esitoWS))
	    esitoWS=null;

    return esitoWS;

  }

  /**
   * Il metodo effettua la consultazione dei dati della gara, prelevando dal servizio il codice CIG ed il numero ANAC e li riporta in gara
   * @param codgar
   * @param numeroLotto
   * @param genere
   * @param username
   * @param password
   * @param smartCig
   * @return String
   * @throws Exception
   * @throws Throwable
   */
  public String consultaDati(String codgar,String numeroLotto,String genere, String username, String password,boolean smartCig) throws Exception, Throwable {

    if (logger.isDebugEnabled())
        logger.debug("consultaDati: inizio metodo");

    String esitoWS =  null;
    String url = this.getUrl();
    boolean lottoUuid = false;

      try {
          EldasoftSimogWS servizio = this.getWs(url);

              String uuid = null;
              esitoWS = "";
              String strEsitoWS = "";
              EsitoConsultaIDGARA esitoConsultaIdGara = new EsitoConsultaIDGARA();
              if(!smartCig){
                uuid = this.getIdRichiestaGara(codgar,UUID_CONSULTA);
                if(uuid!=null && !"".equals(uuid)){
                  esitoConsultaIdGara = servizio.consultaIDGARA(uuid);
                  if(esitoConsultaIdGara.isEsito()){
                      strEsitoWS = esitoConsultaIdGara.getIdgara();
                      //flagEsito = "OK";
                      String updStr = "update torn set numavcp = ? where codgar = ?";
                      sqlManager.update(updStr,new Object[]{strEsitoWS,codgar});
                  }else{
                      strEsitoWS = esitoConsultaIdGara.getMessaggio()+"\n";
                      //flagEsito = "KO";
                  }
                  if("2".equals(genere)){
                      esitoWS = "Numero gara ANAC gara "+numeroLotto+" : "+"\n"+strEsitoWS+"\n";
                  }else{
                      esitoWS = "Numero gara ANAC gara "+codgar+" : "+"\n"+strEsitoWS+"\n";
                  }
                }
              }
              //CIG
              EsitoConsultaCIG esitoConsultaCig = new EsitoConsultaCIG();
              if("2".equals(genere)){
                  uuid = this.getIdRichiestaLottoGara(numeroLotto,UUID_CONSULTA);
                  if(uuid!=null && !"".equals(uuid)){
                    esitoConsultaCig = servizio.consultaCIG(uuid);
                    if(esitoConsultaCig.isEsito()){
                        strEsitoWS = esitoConsultaCig.getCig();
                        //flagEsito = "OK";
                        String updStr = "update gare set codCIG = ? where codgar1 = ? and ngara = ?";
                        sqlManager.update(updStr,new Object[]{strEsitoWS,codgar,numeroLotto});
                    }else{
                        strEsitoWS = esitoConsultaCig.getMessaggio()+"\n";
                        //flagEsito = "KO";
                    }
                  }
                  esitoWS = esitoWS+"Codice CIG della gara "+numeroLotto+" : "+"\n"+strEsitoWS+"\n";
              }else{
                  //genere = 1 gara a lotti:ciclo devo ciclare su tutti i lotti
                  String selectLottiGara = "select NGARA from GARE where GARE.CODGAR1 = ? and " +
                  "(GARE.GENERE is null or GARE.GENERE <> 3) order by ngara";
                  List listaLotti = this.sqlManager.getListVector(selectLottiGara, new Object[]{codgar});
                  if(listaLotti != null && listaLotti.size() > 0){
                      for(int i=0; i < listaLotti.size(); i++){
                       Vector<JdbcParametro> tmpVect = (Vector<JdbcParametro>) listaLotti.get(i);
                       numeroLotto = tmpVect.get(0).getStringValue();
                       uuid = this.getIdRichiestaLottoGara(numeroLotto,UUID_CONSULTA);
                       if(uuid!=null && !"".equals(uuid)){
                         lottoUuid = true;
                         esitoConsultaCig = servizio.consultaCIG(uuid);
                         if(esitoConsultaCig.isEsito()){
                            strEsitoWS = esitoConsultaCig.getCig();
                            String updStr = "update gare set codCIG = ? where codgar1 = ? and ngara = ?";
                            sqlManager.update(updStr,new Object[]{strEsitoWS,codgar,numeroLotto});
                         }else{
                            strEsitoWS = esitoConsultaCig.getMessaggio()+"\n";
                            //flagEsito = "KO";
                         }
                       }
                          esitoWS = esitoWS+"Codice CIG del lotto "+numeroLotto+" della gara "+codgar+" : "+"\n"+strEsitoWS+"\n";
                      }//for
                  }

              }



          if ((uuid==null || "".equals(uuid)) && !lottoUuid){
            throw new Exception("Nessuna richiesta CIG inviata per la gara");
          }

          } catch (RemoteException e) {
            throw new GestoreException(
                "Si e' verificato un errore durante la connessione al web service!",
                "errors.RichiestaCig.ws.error", e);
          } catch (ServiceException e) {
            throw new GestoreException(
                "Si e' verificato un errore durante la connessione al web service!",
                "errors.RichiestaCig.ws.error", e);
          } catch (Throwable t) {
              throw t;
          }




    if (logger.isDebugEnabled())
        logger.debug("consultaDati: fine metodo");
    if("\n".equals(esitoWS))
      esitoWS=null;

  return esitoWS;

}



  /**
   * Restituisce e memorizza la stringa  corrispondente a IdRichiesta
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
  public String getIdRichiestaGara(String codiceGara, String modo)
      throws SQLException, IOException, Exception {

    if (logger.isDebugEnabled())
	        logger.debug("getIdRichiesta: inizio metodo");
    String uuid =null;

    //verifico se esiste già la richiesta
    uuid = (String) sqlManager.getObject("select uuid from torn where codgar = ?", new Object[] { codiceGara });
    if (uuid != null) {
    	return uuid;
    }else if(!UUID_CONSULTA.equals(modo)){
        //altrimenti la genero
    	UUID newUuid = UUID.randomUUID();
    	uuid = newUuid.toString();
    }
    if (logger.isDebugEnabled())
        logger.debug("getIdRichiesta: fine metodo");

    return uuid;

  }

  /**
   * Restituisce e memorizza la stringa  corrispondente a IdRichiesta per il lotto
   *
   * @param numeroLotto
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
  public String getIdRichiestaLottoGara(String numeroLotto, String modo)
      throws SQLException, IOException, Exception {

    if (logger.isDebugEnabled())
	        logger.debug("getIdRichiestaLottoGara: inizio metodo");
    String uuidawsLotto = null;
    //calcolo la richiesta della gara
    uuidawsLotto = (String) sqlManager.getObject(
            "select uuid from gare1 where ngara = ?", new Object[] {numeroLotto});
    if (uuidawsLotto != null) {
    	return uuidawsLotto;
    }else if(!UUID_CONSULTA.equals(modo)){
        //altrimenti la genero
    	UUID uuid = UUID.randomUUID();
    	uuidawsLotto = uuid.toString();
    	//to do //verifica su unicità
    	//calcolo progressivo
        //sqlManager.update("update gare1 set uuid=? where ngara=?",new Object[]{uuidawsLotto,numeroLotto});
    }
    if (logger.isDebugEnabled())
        logger.debug("getIdRichiestaLottoGara: fine metodo");

    return uuidawsLotto;

  }


  /**
   * Restituisce il lotto  della gara
   *
   * @param numeroLotto
   * @return
 * @throws Exception
 * @throws IOException
   */
  @SuppressWarnings({"unchecked", "rawtypes" })
  private HashMap<String,Object> getLotto(String codgar,String numeroLotto,Long genere) throws IOException, Exception {

    if (logger.isDebugEnabled()) logger.debug("getLotto: inizio metodo");

    LottoType lottoGara = LottoType.Factory.newInstance();
    String uuid = null;

    String selectDatiLotto = "select g.ngara,g.codcig,g.not_gar,t.tipgen,g.tipgarg,g.impapp,g.impsic," +
        "t.codnuts, t.sommaur, t.tiplav, g.tiplav, t.oggcont, g.oggcont, g.locint, g.cupprg, g.tipneg, g1.codcui, g1.annint from gare g,torn t, gare1 g1 where g.codgar1= t.codgar and g.ngara=g1.ngara and g.ngara = ?";

    List datiLotto = sqlManager.getVector(selectDatiLotto, new Object[] { numeroLotto });
    if (datiLotto != null && datiLotto.size() > 0) {

      //UUID
      uuid = this.getIdRichiestaLottoGara(numeroLotto,UUID_RICHIESTA);
      lottoGara.setUUID(uuid);

      //codiceCIG
      String codiceCIG = (String) SqlManager.getValueFromVectorParam(datiLotto,1).getValue();
      if (codiceCIG != null) {
         lottoGara.setCIG(codiceCIG);
      }

      //oggettoLotto
      String oggettoLotto = (String) SqlManager.getValueFromVectorParam(datiLotto,2).getValue();
      if (oggettoLotto.length()>1024)
    	  oggettoLotto=oggettoLotto.substring(0, 1024);
      lottoGara.setOGGETTO(oggettoLotto);

      //sommaUrgenza
      String sommaUrgenza = (String) SqlManager.getValueFromVectorParam(datiLotto,8).getValue();
      if("1".equals(sommaUrgenza))
        lottoGara.setSOMMAURGENZA(SnType.X_1);
      else
    	lottoGara.setSOMMAURGENZA(SnType.X_2);

      //tipoContratto (Lavori,Forniture,Servizi)
      Long tipoContratto = (Long) SqlManager.getValueFromVectorParam(datiLotto,3).getValue();
      if (tipoContratto != null) {
    	    switch (tipoContratto.intValue()) {
    	    case 1: //lavori
    	    	lottoGara.setTIPOCONTRATTO(W3Z05Type.L);
    	    	break;
    	    case 2: //forniture
    	    	lottoGara.setTIPOCONTRATTO(W3Z05Type.F);
    	    	break;
    	    case 3: //servizi
    	    	lottoGara.setTIPOCONTRATTO(W3Z05Type.S);
    	    	break;
    	    }

      }

      //Tipologia lavoro
      Long tiplav=null;
      if(genere.longValue()==3)
        tiplav = (Long)SqlManager.getValueFromVectorParam(datiLotto,9).getValue();
      else
        tiplav = (Long)SqlManager.getValueFromVectorParam(datiLotto,10).getValue();
      if((new Long(1)).equals(tipoContratto) && tiplav!=null){
        TipologiaLavoroType tipologiaLavoro = TipologiaLavoroType.Factory.newInstance();
        //tipologiaLavoro.setTIPOLOGIALAVOROArray(new BigInteger[]{new BigInteger(tiplav.toString())});
        tipologiaLavoro.addTIPOLOGIALAVORO(new BigInteger(tiplav.toString()));
        lottoGara.setTIPOLOGIALAVORO(tipologiaLavoro);
      }


      //Tipologia fornitura
      Long oggcont=null;
      if(genere.longValue()==3)
        oggcont = (Long)SqlManager.getValueFromVectorParam(datiLotto,11).getValue();
      else
        oggcont = (Long)SqlManager.getValueFromVectorParam(datiLotto,12).getValue();
      if((new Long(2)).equals(tipoContratto) && oggcont!=null){
        oggcont = new Long(oggcont.longValue() - 9);
        TipologiaFornituraType tipologiaFornitura = TipologiaFornituraType.Factory.newInstance();
        tipologiaFornitura.addTIPOLOGIAFORNITURA(new BigInteger(oggcont.toString()));
        lottoGara.setTIPOLOGIAFORNITURA(tipologiaFornitura);
      }

      //Contratto escluso
      lottoGara.setFLAGESCLUSO(SnType.X_2);


      Object param[] = new Object[1];
      if(genere.longValue()==3){
        param[0] = codgar;
      }else{
        param[0] = numeroLotto;
      }

      //codice CPV
      //nel caso di offerta unica il codice CPV è associato alla gara fittizia
      String codiceCPV = (String) sqlManager.getObject(
              "select codcpv from garcpv where ngara = ? and TIPCPV='1'",new Object[]{numeroLotto});

      if (codiceCPV != null) {
          lottoGara.setCPV(codiceCPV);
      }

      //tipo Procedura
      Long idTipoProcedura = (Long) SqlManager.getValueFromVectorParam(datiLotto,4).getValue();
      String proceduraScelta= inviaVigilanzaManager.getFromTab2("A1z11", idTipoProcedura,false);
      lottoGara.setIDSCELTACONTRAENTE(W3005Type.Enum.forString(proceduraScelta));


      // Importo Lotto
      String selectImportoLotto="select valmax from v_gare_importi where ngara=?";
      Double importoLotto=null;
      Object importoLottoObject = sqlManager.getObject(selectImportoLotto, new Object[] { numeroLotto });
      if (importoLottoObject != null) {
        if (importoLottoObject instanceof Long)
          importoLotto = new Double(((Long) importoLottoObject));
        else if (importoLottoObject instanceof Double)
          importoLotto = new Double((Double) importoLottoObject);
      }
      //Double importoLotto = (Double) SqlManager.getValueFromVectorParam(datiLotto,5).getValue();
      if (importoLotto != null) {
      	lottoGara.setIMPORTOLOTTO(importoLotto.doubleValue());
      }else {
        //throw new Exception("Lotto " + numeroLotto + ": " + CTR_LOTTO_IMPORTO);
      }
      // Importo sicurezza Lotto
    	Double importoSicurezzaLotto = (Double) SqlManager.getValueFromVectorParam(datiLotto,6).getValue();
      if (importoSicurezzaLotto != null) {
      	lottoGara.setIMPORTOATTUAZIONESICUREZZA(importoSicurezzaLotto.doubleValue());
      }


      //nel caso di gara ad offerta unica si devono considerare le categorie associate alla gara fittizia

    	//codice Categoria Prevalente
      Vector<JdbcParametro> datiCategoriaPrevalente = sqlManager.getVector("select cg.catiga,cs.tiplavg  " +
          " from catg cg,cais cs where cg.catiga = cs.caisim and cg.ncatg = 1 " +
          "  and cg.ngara = ?", param);
      String codiceCategoriaPrevalente = null;
      if(datiCategoriaPrevalente!=null && datiCategoriaPrevalente.size()>0){
    	  Long tiplavg = datiCategoriaPrevalente.get(1).longValue();
        codiceCategoriaPrevalente = datiCategoriaPrevalente.get(0).getStringValue();
    	  if(tiplavg== null )
    	    tiplavg= new Long(0);
        if(codiceCategoriaPrevalente!= null && !"".equals(codiceCategoriaPrevalente)){
          if(tipoContratto.longValue()==2)
            codiceCategoriaPrevalente="FB";
          else if(tipoContratto.longValue()==3)
            codiceCategoriaPrevalente="FS";
          else{
            //Gara per lavori
            if((new Long(2)).equals(tiplavg))
              codiceCategoriaPrevalente="FB";
            else if((new Long(3)).equals(tiplavg) || (new Long(5)).equals(tiplavg))
              codiceCategoriaPrevalente="FS";
            else if((new Long(4)).equals(tiplavg))
              codiceCategoriaPrevalente="AA";
            else if (codiceCategoriaPrevalente.length()>5 || !(codiceCategoriaPrevalente.startsWith("OG") || codiceCategoriaPrevalente.startsWith("OS")))
              codiceCategoriaPrevalente="AA";
          }
        }
        if (codiceCategoriaPrevalente != null) {
            lottoGara.setIDCATEGORIAPREVALENTE(codiceCategoriaPrevalente);
      	}
      }

      //Categorie Ulteriori
      String selectCategorieLotto = "select op.catoff, cs.tiplavg from opes op,cais cs" +
          " where op.catoff = cs.caisim and op.ngara3 = ? ";
      List listaCategorieLotto = this.sqlManager.getListVector(selectCategorieLotto, param);
      if(listaCategorieLotto != null && listaCategorieLotto.size() > 0){
		    CategorieType[] categorieType = null;
	        categorieType = new CategorieType[listaCategorieLotto.size()];
	        for(int i=0; i < listaCategorieLotto.size(); i++){
	          String codiceCategoria="";
	          Vector<JdbcParametro> tmpVect = (Vector) listaCategorieLotto.get(i);
	           codiceCategoria = tmpVect.get(0).getStringValue();
	           Long tiplavg = tmpVect.get(1).longValue();
  				if(tiplavg== null )
  	              tiplavg= new Long(0);
  	          if(codiceCategoria!= null && !"".equals(codiceCategoria)){
	            if(tipoContratto.longValue()==2)
	              codiceCategoria="FB";
	            else if(tipoContratto.longValue()==3)
	              codiceCategoria="FS";
	            else{
	              //Gara per lavori
	              if((new Long(2)).equals(tiplavg))
	                codiceCategoria="FB";
	              else if((new Long(3)).equals(tiplavg) || (new Long(5)).equals(tiplavg))
	                codiceCategoria="FS";
	              else if((new Long(4)).equals(tiplavg))
	                codiceCategoria="AA";
	              else if (codiceCategoria.length()>5 || !(codiceCategoria.startsWith("OG") || codiceCategoria.startsWith("OS")))
	                codiceCategoria="AA";
  	            }
  	          }

  				categorieType[i] = CategorieType.Factory.newInstance();
  				categorieType[i].addCATEGORIA(codiceCategoria);
  				lottoGara.setCATEGORIE(categorieType[i]);
	        }
      }

      //codice NUTS Lotto
      String codiceNUTS = (String) SqlManager.getValueFromVectorParam(datiLotto,7).getValue();
      if (codiceNUTS != null && !"".equals(codiceNUTS)) {
        Enum enumNuts = LuogoNUTSType.Enum.forString(codiceNUTS);
   	    if(enumNuts!=null)
   	      lottoGara.setLUOGONUTS(enumNuts);
      }

      //Luogo istat
      String locint = (String)SqlManager.getValueFromVectorParam(datiLotto,13).getValue();
      if(genere.intValue()==3)
        locint=(String)this.sqlManager.getObject("select locint from gare where ngara=?", new Object[]{codgar});
      if(locint!=null){
        lottoGara.setLUOGOISTAT(locint);
      }

      //cupprg
      String cupprg = (String)SqlManager.getValueFromVectorParam(datiLotto,14).getValue();
      if(cupprg!=null)
        lottoGara.setFLAGCUP(SnType.X_1);
      else
        lottoGara.setFLAGCUP(SnType.X_2);

      //Lista CUP
      if(cupprg!=null){
        ListaCUPType listaCup = ListaCUPType.Factory.newInstance();
        listaCup.addCUP(cupprg);
        lottoGara.setLISTACUP(listaCup);
      }

      //TIPNEG
      Long tipneg = (Long)SqlManager.getValueFromVectorParam(datiLotto,15).getValue();
      if(tipneg != null && tipneg.longValue() > 32 && tipneg.longValue() < 44 ){
        CondizioneNegoziataType condizioneNegoziataType = CondizioneNegoziataType.Factory.newInstance();
        condizioneNegoziataType.addCONDIZIONE(new BigInteger(tipneg.toString()));
        lottoGara.setCONDIZIONINEGOZIATA(condizioneNegoziataType);
      }

      //Programmazione
      String codcui=(String)SqlManager.getValueFromVectorParam(datiLotto,16).getValue();
      if(codcui!=null){
        lottoGara.setFLAGDL50(SnType.X_1);
        lottoGara.setCUIPROGRAMMA(codcui);
      }else
        lottoGara.setFLAGDL50(SnType.X_2);

      Long annint = (Long)SqlManager.getValueFromVectorParam(datiLotto,17).getValue();
      if(annint!=null){
        lottoGara.setPRIMAANNUALITA(annint.intValue());
      }
    }

    if (logger.isDebugEnabled()) logger.debug("getLotto: fine metodo");

    HashMap<String,Object> hm = new HashMap<String,Object>();
    hm.put(LOTTO_MAP_KEY, lottoGara);
    hm.put(UUID_MAP_KEY, uuid);
    return hm;

  }

  /**
   * Restituisce il tecnico RUP  per la gara
   *
   * @param codRUP
   * @return
   * @throws SQLException
   */
  private TecnicoType getTecnico(String codRUP) throws SQLException,Exception {

    if (logger.isDebugEnabled()) logger.debug("getTecnico: inizio metodo");

    TecnicoType tecnicoRUP = TecnicoType.Factory.newInstance();

    String selectDatiTecnico = "select cogtei,nometei,nomtec,cftec, pivatec, naztei " +
    		"from tecni where codtec = ?";
    List datiTecnico = sqlManager.getVector(selectDatiTecnico, new Object[] {codRUP});
    if (datiTecnico != null && datiTecnico.size() > 0) {
    	String cognome = (String) SqlManager.getValueFromVectorParam(datiTecnico,0).getValue();
      	//cognome
      	if (cognome != null) {
      		tecnicoRUP.setCOGTEI(cognome);
      	}
      	String nome = (String) SqlManager.getValueFromVectorParam(datiTecnico,1).getValue();
    	//nome
    	if (nome != null) {
    		tecnicoRUP.setNOMETEI(nome);
    	}
      	String cognomenome = (String) SqlManager.getValueFromVectorParam(datiTecnico,2).getValue();
    	//cognomenome
    	if (cognomenome != null) {
    		tecnicoRUP.setNOMTEC(cognomenome);
    	}

      	String codicefiscale = (String) SqlManager.getValueFromVectorParam(datiTecnico,3).getValue();
    	//codicefiscale
    	if (codicefiscale != null) {
    		tecnicoRUP.setCFTEC(codicefiscale);
    	}

    	Long nazione = (Long) SqlManager.getValueFromVectorParam(datiTecnico,5).getValue();
    	if(nazione == null || nazione.longValue()==1){
        	//Partita iva
        	String partitaIva = (String) SqlManager.getValueFromVectorParam(datiTecnico,4).getValue();
        	if (partitaIva != null) {
                tecnicoRUP.setPIVATEC(partitaIva);
            }
    	}

    }

    if (logger.isDebugEnabled()) logger.debug("getTecnico: fine metodo");

    return tecnicoRUP;

  }

  /**
   * Restituisce XML contenente i dati della gara
   *  per la richiesta CIG
   * @param codgar
   * @param ngara
   * @param genereString
   * @return String
   * @throws SQLException
   * @throws IOException
   */
  private HashMap<String,Object> getXMLDatiGaraCig(String codgar, String ngara, String genereString)
  	throws SQLException,IOException, Exception {

    if (logger.isDebugEnabled())
	        logger.debug("getXMLDatiGaraCig: inizio metodo");

	String xml = null;

    GaraDocument garaDocument =GaraDocument.Factory.newInstance();
    garaDocument.documentProperties().setEncoding("UTF-8");
    GaraType garaType = garaDocument.addNewGara();

    //UUID;
    String uuid = this.getIdRichiestaGara(codgar,UUID_RICHIESTA);
    garaType.setUUID(uuid);

    //DISTINGUO il genere della Gara
    Long genere = new Long(genereString);
    String selectDatiGara = null;
    String selectLottiGara = null;

    switch (genere.intValue()) {
    case 1: //gara a lotti con offerte distinte
    case 3:
        selectDatiGara = "select "
            + " torn.destor, "      //0
            + " torn.imptor, "      //1
            + " torn.codrup, "      //2
            + " torn.isadesione, "  //3
            + " torn.codcigaq, "    //4
            + " torn.modrea, "       //5
            + " torn.settore, "      //6
            + " uffint.cfein, "      //7
            + " torn.sommaur, "       //8
            + " torn.accqua, "      //9
            + " torn.altrisog, "     //10
            + " torn.aqdurata, "    //11
            + " torn.aqtempo, "     //12
            + " uffint.iscuc,"      //13
            + " uffint.cfanac "     //14
            + " from torn, uffint where codgar = ? and torn.cenint = uffint.codein";
        break;
    case 2: //gara a lotto unico
    	selectDatiGara = "select "
          + " gare.not_gar, "       //0
          + " gare.impapp, "        //1
          + " torn.codrup, "        //2
          + " torn.isadesione, "    //3
          + " torn.codcigaq,"       //4
          + " torn.modrea, "         //5
          + " torn.settore, "        //6
          + " uffint.cfein, "      //7
          + " torn.sommaur, "       //8
          + " torn.accqua, "      //9
          + " torn.altrisog, "    //10
          + " torn.aqdurata, "    //11
          + " torn.aqtempo, "     //12
          + " uffint.iscuc,"      //13
          + " uffint.cfanac "     //14
          + " from torn, gare,uffint where torn.codgar = gare.codgar1 "
          + " and torn.codgar = ? and torn.cenint = uffint.codein";
      break;
    }

    List datiGara = sqlManager.getVector(selectDatiGara,new Object[] { codgar });
    if (datiGara != null && datiGara.size() > 0) {

        //Oggetto
    	String oggetto = (String) SqlManager.getValueFromVectorParam(datiGara,0).getValue();
    	if (oggetto.length()>1024)
    	  oggetto=oggetto.substring(0, 1024);
    	garaType.setOGGETTO(oggetto);


        // Importo Gara
    	String selectImporto="select valmax from v_gare_importi where codgar=?";
        if(genere==2)
          selectImporto+=" and ngara is not null";
        else
          selectImporto+=" and ngara is null";
        Double importoGara = null;
        Object importoGaraObject = sqlManager.getObject(selectImporto, new Object[] { codgar });
        if (importoGaraObject != null) {
          if (importoGaraObject instanceof Long)
            importoGara = new Double(((Long) importoGaraObject));
          else if (importoGaraObject instanceof Double)
            importoGara = new Double((Double) importoGaraObject);
        }
        garaType.setIMPORTOGARA(importoGara.doubleValue());

        //Codice fiscale stazione appaltante
        String cfein = null;
        Long altrisog= (Long) SqlManager.getValueFromVectorParam(datiGara,10).getValue();
        String iscuc = (String) SqlManager.getValueFromVectorParam(datiGara,13).getValue();
        if("1".equals(iscuc) && (new Long(2).equals(altrisog) || new Long(3).equals(altrisog))){
          cfein = (String) SqlManager.getValueFromVectorParam(datiGara,14).getValue();
          if(cfein==null || "".equals(cfein))
            cfein = (String) SqlManager.getValueFromVectorParam(datiGara,7).getValue();
        }else
          cfein = (String) SqlManager.getValueFromVectorParam(datiGara,7).getValue();

        garaType.setCFSTAZIONEAPPALTANTE(cfein);

        String codRUP = (String) SqlManager.getValueFromVectorParam(datiGara,2).getValue();
    	//RUP
    	if (codRUP != null) {
            TecnicoType tecnicoType = TecnicoType.Factory.newInstance();
            tecnicoType = this.getTecnico(codRUP);
    		garaType.setRUP(tecnicoType);
    	}

    	//Cig Accordo quadro - solo nel caso di adesione ad accordo quadro
        String isAdesione = (String) SqlManager.getValueFromVectorParam(datiGara,3).getValue();
        if (isAdesione != null && "1".equals(isAdesione)){
          String cigAccordoQuadro = (String) SqlManager.getValueFromVectorParam(datiGara,4).getValue();
          if (cigAccordoQuadro != null) {
            garaType.setCIGACCQUADRO(cigAccordoQuadro);
          }
        }

        //MODREA
        String modrea = (String) SqlManager.getValueFromVectorParam(datiGara,5).getValue();
        garaType.setMODOREALIZZAZIONE(W3999Type.Enum.forString(modrea));

        String settore = (String) SqlManager.getValueFromVectorParam(datiGara,6).getValue();
        if(settore != null && "S".equals(settore)){
            //Tipo_scheda;
          garaType.setTIPOSCHEDA(W3Z08Type.S);
        }else{
          //Tipo_scheda;
          garaType.setTIPOSCHEDA(W3Z08Type.O);
        }


        //SOMMAURGENZA
        String sommaur = (String) SqlManager.getValueFromVectorParam(datiGara,8).getValue();
        if("1".equals(sommaur))
          garaType.setSOMMAURGENZA(SnType.X_1);
        else
          garaType.setSOMMAURGENZA(SnType.X_2);

        //DURATA ACCORDO QUADRO
        String accqua = (String) SqlManager.getValueFromVectorParam(datiGara,9).getValue();
        if("1".equals(accqua) || new Long(2).equals(altrisog) || new Long(3).equals(altrisog)){
          int tempoUtile=-1;
          if("1".equals(accqua)){
            Long aqdurata = (Long)SqlManager.getValueFromVectorParam(datiGara,
                11).getValue();
            Long aqtempo = (Long)SqlManager.getValueFromVectorParam(datiGara,
                12).getValue();

            if(aqdurata!=null){
                if(aqtempo != null) {
              	  if(aqtempo.longValue()==1) {
              		  tempoUtile= aqdurata.intValue() * 30;	  
              	  }else if(aqtempo.longValue()==3) {
              		  tempoUtile= aqdurata.intValue();	  
              	  }else {
              		  tempoUtile= aqdurata.intValue() * 365;
              	  }
                }else {
              	  tempoUtile= aqdurata.intValue() * 365;            		
                }
            }
          }else{
            tempoUtile = this.inviaVigilanzaManager.calcolaTempoUtile(codgar, genereString);
          }
          if(tempoUtile>0)
            garaType.setDURATAACCQUADRO(tempoUtile);
        }

    }

    //UUID dei lotti;
    //separo per tipologia di gara

    switch (genere.intValue()) {
    case 1: // gara a lotto unico
    case 2: //gara a lotti con offerte distinte
        selectLottiGara = "select NGARA from GARE where GARE.CODGAR1 = ? and " +
		"(GARE.GENERE is null or GARE.GENERE <> 3) order by ngara";
        break;
    case 3://gara offerta unica
        selectLottiGara = "select NGARA from GARE where GARE.CODGAR1 = ? and " +
		"(GARE.GENERE is null or GARE.GENERE <> 3) and GARE.CODGAR1 <> GARE.NGARA order by ngara";
        break;
    }

    List listaLotti = this.sqlManager.getListVector(selectLottiGara, new Object[]{codgar});
    ArrayList<HashMap> uuidLotti = new ArrayList<HashMap>();
	if(listaLotti != null && listaLotti.size() > 0){
	  LottoType[] lottoType = null;
	  lottoType = new LottoType[listaLotti.size()];

	  for(int i=0; i < listaLotti.size(); i++){
	    Vector<JdbcParametro> tmpVect = (Vector) listaLotti.get(i);
  		String numeroLotto =  tmpVect.get(0).getStringValue();
  		lottoType[i] = LottoType.Factory.newInstance();
  		HashMap<String,Object> hm = this.getLotto(codgar,numeroLotto,genere);
  		lottoType[i] = (LottoType) hm.get(LOTTO_MAP_KEY);
  		String uuidLotto = (String) hm.get(UUID_MAP_KEY);
  		HashMap<String,String> temp = new HashMap<String,String>();
  		temp.put(NGARA_MAP_KEY, numeroLotto);
  		temp.put(UUID_MAP_KEY, uuidLotto);
  		uuidLotti.add(temp);
  	  }
	  garaType.setLOTTIArray(lottoType);
	}

	//Categorie
	CategorieMerceologicheType categoria = CategorieMerceologicheType.Factory.newInstance();
	categoria.addCATEGORIA(new BigInteger("999"));
	garaType.setCATEGORIE(categoria);

    garaDocument.setGara(garaType);

    ByteArrayOutputStream baosGaraType = new ByteArrayOutputStream();
    garaDocument.save(baosGaraType);
    xml = baosGaraType.toString();
    baosGaraType.close();

    ArrayList validationErrors = new ArrayList();
    XmlOptions validationOptions = new XmlOptions();
    validationOptions.setErrorListener(validationErrors);
    boolean isValid = garaDocument.validate(validationOptions);
    if (!isValid) {
        String listaErroriValidazione = "";
        Iterator iter = validationErrors.iterator();
        while (iter.hasNext()) {
          listaErroriValidazione += iter.next() + "\n";
        }
        logger.error("La generazione dei dati di gara per la richiesta CIG non rispetta il formato previsto: "
            + xml
            + "\n"
            + listaErroriValidazione);
        throw new GestoreException(
            "La generazione dei dati di gara per la richiesta CIG non rispetta il formato previsto: ",
            "errors.inviadatirichiestacig.validazione",
            new Object[] { listaErroriValidazione }, null);
      }



	if (logger.isDebugEnabled())
	          logger.debug("getXMLDatiGaraCig: fine metodo");

	HashMap<String,Object> hm = new HashMap<String,Object>();
	hm.put(XML_MAP_KEY, xml);
	hm.put(UUID_MAP_KEY, uuid);
	hm.put(LISTA_LOTTI_MAP_KEY, uuidLotti);
    return hm;

  }


  /**
   *
   * @throws Throwable
   */
  public void consultaDatiGara() {

    if (logger.isDebugEnabled())
      	logger.debug("consultaDatiGara: inizio metodo");
    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre....OP?
    if (WebUtilities.isAppNotReady()) return;


    //variabili per tracciatura eventi
    int livEvento = 1;
    String errMsgEvento = "";
    String codiceEvento="";
    String descrEv = "";
    String oggEvento="";

    String url = ConfigManager.getValore(PROP_INVIO_DATI_CIG_WS_URL);
    if (url == null || "".equals(url)) {
      logger.error("L'indirizzo per la connessione al web service Vigilanza CIG non e' definito");
      return;

    }

    EldasoftSimogWS servizio = null;
    try {
		EldasoftSimogWSServiceLocator locator = new EldasoftSimogWSServiceLocator();
        locator.setEldasoftSimogWSEndpointAddress(url);
        locator.getEngine().setOption(MessageContext.HTTP_TRANSPORT_VERSION, HTTPConstants.HEADER_PROTOCOL_V11);
        servizio = locator.getEldasoftSimogWS();
    } catch (ServiceException e) {
      logger.error("Errore durante la connessione a Vigilanza CIG " + e.getMessage(),e);
    }


        //Consultazione degli IDGara
        EsitoConsultaIDGARA esitoConsultaIdGara = new EsitoConsultaIDGARA();
	    String selectRichiesteSenzaIdGara =
	    	"select t.codgar,t.uuid from TORN t where t.uuid is not null and t.numavcp is null and not exists (select g.ngara from gare1 g where g.codgar1=t.codgar and g.uuid =t.uuid) order by t.codgar";
	    List listaRichiesteSenzaIdGara = null;

	    try {
          listaRichiesteSenzaIdGara = this.sqlManager.getListVector(selectRichiesteSenzaIdGara, null);
        } catch (SQLException e) {
          logger.error("Errore durante la lettura delle gare per cui non è definito il valore del campo TORN.NUMAVCP " + e.getMessage(),e);
        }

	    if(listaRichiesteSenzaIdGara != null && listaRichiesteSenzaIdGara.size() > 0){
	      String codgar = null;
	      String uuid = null;
	      Vector tmpVect = null;
	      String ngara = null;

	      for(int i=0; i < listaRichiesteSenzaIdGara.size(); i++){
 				tmpVect = (Vector) listaRichiesteSenzaIdGara.get(i);
  				codgar = ((JdbcParametro) tmpVect.get(0)).getStringValue();
  				uuid = ((JdbcParametro) tmpVect.get(1)).getStringValue();

  				if("$".equals(codgar.substring(0,1))){
  				  ngara = codgar.substring(1);

  				}
  				try{

  				  esitoConsultaIdGara = servizio.consultaIDGARA(uuid);
  	        	  if(esitoConsultaIdGara.isEsito()){
  	        	    String esitoWS = esitoConsultaIdGara.getIdgara();
  	        	    String updStr = "update torn set numavcp = ? where codgar = ?";

  	        		sqlManager.update(updStr,new Object[]{esitoWS,codgar});

  	        		codiceEvento="GA_CONSULTA_IDGARA_BATCH";
  	        		descrEv="Aggiornamento numero gara ANAC in seguito a invio richiesta CIG (processo batch)";
  	        		try {
  	                  LogEvento logEvento = new LogEvento();
  	                  logEvento.setCodApplicazione("PG");
  	                  if("$".equals(codgar.substring(0,1)))
  	                    oggEvento=ngara;
  	                  else
  	                    oggEvento=codgar;
  	                  logEvento.setOggEvento(oggEvento);
  	                  logEvento.setLivEvento(livEvento);
  	                  logEvento.setCodEvento(codiceEvento);
  	                  logEvento.setDescr(descrEv);
  	                  errMsgEvento = "Aggiornato Numero gara ANAC per la gara "+oggEvento;
  	                  logEvento.setErrmsg(errMsgEvento);
  	                  LogEventiUtils.insertLogEventi(logEvento);
  	                } catch (Exception le) {
  	                  logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
  	                }

  	        		if (logger.isInfoEnabled())
    	              logger.info("consultaDatiGara:"+errMsgEvento);
  	        	  }else{
  	        	    String esitoWS = "Numero gara ANAC gara " + codgar + " : " + esitoConsultaIdGara.getMessaggio();
                    logger.error(esitoWS);
                  }

  				}catch(SQLException e){
                  logger.error("Errore durante l'aggiornamento del NUMACPV della gara " + codgar + " " +  e.getMessage(),e);
                } catch (RemoteException e) {
                  logger.error("Errore durante l'aggiornamento del NUMACPV della gara " + codgar + " " +  e.getMessage(),e);
                }
	        }
		}

        //Consultazione dei Codici CIG
        EsitoConsultaCIG esitoConsultaCig = new EsitoConsultaCIG();
	    String selectRichiesteSenzaCig =
	    	"select g.codgar1,g.ngara,g1.uuid from GARE g,GARE1 g1  where g.NGARA =g1.NGARA" +
	    	" and codcig is null and uuid is not null order by g.codgar1,g.ngara";
	    List listaRichiesteSenzaCig = null;
	    try {
        listaRichiesteSenzaCig = this.sqlManager.getListVector(selectRichiesteSenzaCig, new Object[]{});
        } catch (SQLException e) {
          logger.error("Errore durante la lettura dei lotti per cui non è definito il valore del campo GARE.CODCIG " + e.getMessage(),e);
        }
		if(listaRichiesteSenzaCig != null && listaRichiesteSenzaCig.size() > 0){
			for(int i=0; i < listaRichiesteSenzaCig.size(); i++){
 				Vector tmpVect = (Vector) listaRichiesteSenzaCig.get(i);
  				String codgar = ((JdbcParametro) tmpVect.get(0)).getStringValue();
  				String ngara = ((JdbcParametro) tmpVect.get(1)).getStringValue();
  				String uuidLotto = ((JdbcParametro) tmpVect.get(2)).getStringValue();
  				try{
  				  esitoConsultaCig = servizio.consultaCIG(uuidLotto);
	        	  if(esitoConsultaCig.isEsito()){
	        		String esitoWS = esitoConsultaCig.getCig();
	            	String updStr = "update gare set codcig = ? where codgar1 = ?" +
	            			" and ngara = ?";

	            	  sqlManager.update(updStr,new Object[]{esitoWS,codgar,ngara});

	            	  codiceEvento="GA_CONSULTA_CIG_BATCH";
                      descrEv="Aggiornamento codice CIG in seguito a invio richiesta CIG o smartCIG (processo batch)";
                      try {
                        LogEvento logEvento = new LogEvento();
                        logEvento.setCodApplicazione("PG");
                        if("$".equals(codgar.substring(0,1))){
                          oggEvento=ngara;
                          errMsgEvento="Aggiornato il codice Cig della gara "+ngara;
                        }else{
                          oggEvento=codgar;
                          errMsgEvento="Aggiornato il codice Cig del lotto "+ngara+" della gara "+codgar;
                        }
                        logEvento.setOggEvento(oggEvento);
                        logEvento.setLivEvento(livEvento);
                        logEvento.setCodEvento(codiceEvento);
                        logEvento.setDescr(descrEv);
                        logEvento.setErrmsg(errMsgEvento);
                        LogEventiUtils.insertLogEventi(logEvento);
                      } catch (Exception le) {
                        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
                      }

	            	if (logger.isInfoEnabled())
	            		logger.info("consultaCig:" + errMsgEvento);
  	        	  }else{
                    String esitoWS = "Codice CIG del lotto " + ngara + " : " + esitoConsultaCig.getMessaggio();
                    logger.error(esitoWS);

                  }
  				}catch(SQLException e){
                  logger.error("Errore durante l'aggiornamento del codice CIG del lotto " + ngara + " " +  e.getMessage(),e);
                } catch (RemoteException e) {
                  logger.error("Errore durante l'aggiornamento del codice CIG del lotto " + ngara + " " +  e.getMessage(),e);
                }

			}

		}




    if (logger.isDebugEnabled())
        logger.debug("consultaDatiGara: fine metodo");

  }

  /***
   * Il metodo effettua i controlli bloccanti e quelli non bloccanti sui dati della gara per la richiesta
   * del codice CIG
   *
   * @param codgar
   * @param numeroLotto
   * @param genere
   * @param sessione
   * @return HashMap
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String,Object> controllaDatiRichiestaCIG(String codgar, String numeroLotto, String genere, HttpSession sessione) throws SQLException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaCIG: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();

    String selectDatiGara = null;

    if("1".equals(genere) || "3".equals(genere)){
      //controllo che vi sia almeno un lotto
      int numeroLotti = this.getNumeroLotti(codgar);
      if(numeroLotti <= 0){
        erroriBloccanti.add(CTR_NESSUN_LOTTO);
      }

      //Gare a lotti
      selectDatiGara = "select "
          + " torn.destor, "   //0
          + " torn.codrup, "   //1
          + " torn.tipgar, "   //2
          + " torn.modrea, "   //3
          + " torn.tipgen, "   //4
          + " torn.codnuts, "  //5
          + " torn.cenint"     //6
          + " from torn where codgar = ?";
    }else{
      //gara a lotto unico
      selectDatiGara = "select "
          + " gare.not_gar, "  //0
          + " torn.codrup, "   //1
          + " gare.tipgarg, "  //2
          + " torn.modrea, "   //3
          + " torn.tipgen, "   //4
          + " torn.codnuts, "  //5
          + " torn.cenint,"    //6
          + " gare.ngara, "    //7
          + " gare.impapp "    //8
          + " from torn, gare where torn.codgar = gare.codgar1 "
          + " and torn.codgar = ?";
    }

    String ngara=null;
    List datiTORNGARE = sqlManager.getVector(selectDatiGara,new Object[] { codgar });
    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {
      String profilo = (String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO);
        ArrayList erroriTemp=null;
        if("2".equals(genere)){
            ngara = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,7).getValue();
        }

        //Inizio - Controlli bloccanti

        String oggetto = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
        //Oggetto della gara
        if (oggetto == null || "".equals(oggetto)) {
            if("2".equals(genere)){
              erroriBloccanti.add(CTR_GARA_OGGETTO);
            }else{
              erroriBloccanti.add(CTR_GARA_OGGETTO);
            }
        }

        //Oggetto dei lotti
        if("1".equals(genere) || "3".equals(genere)){
          erroriBloccanti.addAll(this.getControlloNOTGAR(codgar));
        }

        //Controllo codice fiscale stazione appaltante
        String cenint =  (String) SqlManager.getValueFromVectorParam(datiTORNGARE,6).getValue();
        if (cenint != null) {

          String cfein = (String) sqlManager.getObject("select cfein from uffint where codein =?", new Object[]{cenint});

          if(cfein==null || "".equals(cfein)){
            erroriBloccanti.add(CTR_SA_CF);
          }else{
            if (!UtilityFiscali.isValidPartitaIVA(cfein) & !UtilityFiscali.isValidCodiceFiscale(cfein)){
              erroriBloccanti.add(CTR_SA_CF_NON_VALIDO);
            }
          }
        }else{
          erroriBloccanti.add(CTR_SA);
        }

        String codRUP = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
        //RUP
        if (codRUP != null) {
            erroriBloccanti.addAll(this.controlloDatiTecnico(codRUP,true));
        }else{
          erroriBloccanti.add(CTR_GARA_RUP);
        }

        //Importo Gara
        if("2".equals(genere)){
          Object importo = SqlManager.getValueFromVectorParam(
              datiTORNGARE, 8).getValue();
          if(importo==null)
            erroriBloccanti.add(CTR_GARA_IMPORTO);
        }else{
          // Importo lotti
          erroriBloccanti.addAll(this.getControlloImportoGara(codgar, ngara, genere));
        }


        //Controllo su tipgar
        Long tipo = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 2).getValue();
        if (tipo == null) {
          erroriBloccanti.add(CTR_GARE_TORN_TIPGAR);
        }else{
          String count = inviaVigilanzaManager.getFromTab2("A1z11", tipo, false);
          if(count == null || "".equals(count)){
            erroriBloccanti.add(CTR_GARE_TORN_MAP_TIPGAR_A1z11);
          }
        }



        //Fine - Controlli bloccanti

        //Inizio - Controlli non bloccanti

        //CONTROLLO MODREA E CODNUTS
        String modrea = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,3).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,5).getValue();

        String controllo[] = this.controlloModreaCodnuts(modrea, codnuts, genere, profilo);
        if("NOK".equals(controllo[0])){
          erroriNonBloccanti.add(CTR_MODREA);
        }
        if("NOK".equals(controllo[1])){
          erroriNonBloccanti.add(CTR_DONUTS);
        }

        Long tipgen = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 4).getValue();

        //Controllo su TIPLAV
        if((new Long(1)).equals(tipgen)){
          erroriNonBloccanti.addAll(this.getControlloDatiTornGare(codgar, ngara, genere, "TIPLAV", profilo));
        }

        //Controllo OGGCONT
        if((new Long(2)).equals(tipgen)){
          erroriNonBloccanti.addAll(this.getControlloDatiTornGare(codgar, ngara, genere, "OGGCONT", profilo));
        }

        //Controllo CPV
        erroriNonBloccanti.addAll(this.getControlloGarcpv(codgar, ngara, genere, profilo));

        //controllo cupprg
        erroriNonBloccanti.addAll(this.getControlloCUPCUI(codgar, ngara, genere, profilo, "CUPPRG"));

        //controllo codcui
        erroriNonBloccanti.addAll(this.getControlloCUPCUI(codgar, ngara, genere, profilo, "CODCUI"));



      //Fine - Controlli non bloccanti
    }

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);

    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaCIG: fine metodo");
    return response;
  }

  /**
   * Si effettua il conteggio dei numero dei lotti
   *
   * @param codgar
   * @return int
   * @throws SQLException
   */
  public int getNumeroLotti(String codgar) throws SQLException {
    String queryCount = "select ngara from gare where codgar1 = ? and (genere is null or genere != 3)";
    List count = sqlManager.getListVector(queryCount,
        new Object[] { codgar });
    return count.size();
  }

  /**
   * Si controlla la valorizzazione dell'oggetto dei lotti
   *
   * @param codgar
   * @return ArrayList<String>
   * @throws SQLException
   */
  public ArrayList<String> getControlloNOTGAR(String codgar) throws SQLException{
    ArrayList<String> errors = new ArrayList<String>();
    String MessageError = CTR_LOTTI_OGGETTO;
    boolean isFirst = true;
    String selectGARE = "select codiga, not_gar "
        + " from gare where codgar1 = ? and ngara!=codgar1 "
        + " order by ngara";

    List datiGARE = sqlManager.getListVector(selectGARE,
        new Object[] { codgar });

    if (datiGARE != null && datiGARE.size() > 0) {
      String codiga =null;
      String title=null;
      for (int i = 0; i < datiGARE.size(); i++) {

        codiga = (String) SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 0).getValue();
        title = (String) SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 1).getValue();

        if (title == null) {
          if(!isFirst){MessageError = MessageError + ", ";}
          MessageError = MessageError + codiga;
          isFirst = false;
        }
      }
      if(!isFirst){
        errors.add(MessageError);
      }
    }
    return errors;
  }

/**
 * Si controlla che sia valorizzato il RUP, se lo è si controlla la valorizzazione del nome e del
 * codice fiscale. Se il tecnico è italiano si controlla la validità del codice fiscal e della
 * partita iva(se valorizzata)
 *
 * @param codRUP
 * @param controlloPIVA
 * @return ArrayList<String>
 * @throws SQLException
 */
  public ArrayList<String> controlloDatiTecnico(String codRUP, boolean controlloPIVA) throws SQLException {
    ArrayList<String> errors = new ArrayList<String>();
    String selectDatiTecnico = "select nomtec,cftec, pivatec, naztei " +
            "from tecni where codtec = ?";
    List datiTecnico = sqlManager.getVector(selectDatiTecnico, new Object[] {codRUP});
    if (datiTecnico != null && datiTecnico.size() > 0) {
        String cognomenome = (String) SqlManager.getValueFromVectorParam(datiTecnico,0).getValue();
        //cognomenome
        if (cognomenome == null || "".equals(cognomenome)) {
          errors.add(CTR_RUP_INTESTAZIONE);
        }

        Long nazione = (Long) SqlManager.getValueFromVectorParam(datiTecnico,3).getValue();

        String codicefiscale = (String) SqlManager.getValueFromVectorParam(datiTecnico,1).getValue();
        //codicefiscale
        if (codicefiscale != null) {
          if((nazione==null || nazione.longValue()==1) && !UtilityFiscali.isValidCodiceFiscale(codicefiscale, true)){
            errors.add(CTR_RUP_CF_NON_VALIDO);
          }
        } else {
          errors.add(CTR_RUP_CF);
        }

        if(controlloPIVA && (nazione == null || nazione.longValue()==1)){
          //Partita iva
          String partitaIva = (String) SqlManager.getValueFromVectorParam(datiTecnico,2).getValue();
          if (partitaIva != null && !UtilityFiscali.isValidPartitaIVA(partitaIva,true)) {
            errors.add(CTR_RUP_PIVA_NON_VALIDA);
          }
        }
    }
    return errors;

  }

  /**
   * Si controlla che sia valorizzato l'importo di gara
   *
   * @param codgar
   * @param ngara
   * @param genere
   * @return ArrayList<String>
   * @throws SQLException
   */
  public ArrayList<String> getControlloImportoGara(String codgar, String ngara, String genere) throws SQLException{
    ArrayList<String> errors = new ArrayList<String>();
    String MessageError = CTR_LOTTI_IMPORTO;
    String selectGARE = "select codiga, "
        + " impapp from gare where codgar1 = ? "
        + " and codgar1!=ngara order by ngara";

    List datiGARE = sqlManager.getListVector(selectGARE,
        new Object[] { codgar });

    if (datiGARE != null && datiGARE.size() > 0) {
      String codiga=null;
      Object importo=null;
      boolean isFirst = true;
      for (int i = 0; i < datiGARE.size(); i++) {
        codiga = (String) SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 0).getValue();
        importo = SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 1).getValue();

        if (importo == null) {
          if(!isFirst){
            MessageError = MessageError + ", ";
          }
          MessageError = MessageError + codiga;
          isFirst = false;
        }
      }
      if(!isFirst){
        errors.add(MessageError);
      }
    }
    return errors;
  }

  /**
   * Si controlla se è valorizzato il campo (TIPLAV o OGGCONT) fornito come parametro
   *
   * @param codgar
   * @param ngara
   * @param genere
   * @param campo
   * @param profilo
   * @return ArrayList<String>
   * @throws SQLException
   */
  public ArrayList<String> getControlloDatiTornGare(String codgar, String ngara, String genere, String campo, String profilo) throws SQLException{
    ArrayList<String> errors = new ArrayList<String>();
    String MessageError = "";
    boolean campoVisibile = true;
    boolean sezioneVisibile = true;
    String entita="GARE";
    if("3".equals(genere))
      entita="TORN";
    if (!geneManager.getProfili().checkProtec(profilo, "COLS", "VIS","GARE." + entita + "." +campo))
      campoVisibile=false;

    if(campoVisibile){
      if("1".equals(genere) && !geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.DATIGEN.ALT"))
        sezioneVisibile=false;
      else if("2".equals(genere) && !geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.DATIGEN.ALT"))
        sezioneVisibile=false;
      else if("3".equals(genere) && !geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-OFFUNICA-scheda.DATIGEN.ALT"))
        sezioneVisibile=false;
    }

    if(campoVisibile && sezioneVisibile){
      if("1".equals(genere)){
        if("TIPLAV".equals(campo))
          MessageError = CTR_TIPLAV_LOTTI;
        else
          MessageError = CTR_OGGCONT_LOTTI;
      }else{
        if("TIPLAV".equals(campo))
          MessageError = CTR_TIPLAV;
        else
          MessageError = CTR_OGGCONT;
      }

      String selectTiplav = null;
      if("3".equals(genere))
        selectTiplav="select " + campo + " from torn where codgar=?";
      else if("2".equals(genere))
        selectTiplav="select " + campo + " from gare where ngara=?";
      else
        selectTiplav="select " + campo + ", codiga from gare where codgar1=? order by ngara";

      Object par[]= new Object[1];
      if("2".equals(genere))
        par[0]=ngara;
      else
        par[0]=codgar;

      List dati = sqlManager.getListVector(selectTiplav,par);

      if (dati != null && dati.size() > 0) {
        String codiga = null;
        Long tiplav = null;
        boolean isFirst = true;
        for (int i = 0; i < dati.size(); i++) {
          tiplav = (Long)SqlManager.getValueFromVectorParam(
              dati.get(i), 0).getValue();

          if (tiplav == null) {

            if("2".equals(genere) || "3".equals(genere) )
              errors.add(MessageError);
            else{
              codiga = (String) SqlManager.getValueFromVectorParam(
                  dati.get(i), 1).getValue();
              if(!isFirst){
                MessageError = MessageError + ", ";
              }
              MessageError = MessageError + codiga;
              isFirst = false;
            }
          }
        }
        if(!isFirst){
          errors.add(MessageError);
        }
      }
    }
    return errors;
  }

  /**
   * Viene controllato se è presente il codice CPV, e nel caso lo sia, se è di terzo livello
   *
   * @param codgar
   * @param ngara
   * @param genere
   * @param profilo
   * @return
   * @throws SQLException
   */
  public ArrayList<String> getControlloGarcpv(String codgar, String ngara, String genere, String profilo) throws SQLException{
    ArrayList<String> errors = new ArrayList<String>();
    boolean sezioneVisibile=true;
    if("1".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.ALTRIDATI.CPVPR") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.ALTRIDATI") ))
      sezioneVisibile=false;
    else if("2".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.ALTRIDATI.CPVPR") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.ALTRIDATI")))
      sezioneVisibile=false;
    else if("3".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.DATIGENOFFUNICA.CPVPR") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.DATIGENOFFUNICA")))
      sezioneVisibile=false;

    if(sezioneVisibile){
      String MessageError1 = "";
      if("1".equals(genere) || "3".equals(genere)){
        MessageError1 = CTR_CPV_LOTTI;
      }else{
        MessageError1 = CTR_CPV;
      }
      String MessageError2 = "";
      if("1".equals(genere) || "3".equals(genere)){
        MessageError2 = CTR_CPV_NON_VALIDO_LOTTI;
      }else{
        MessageError2 = CTR_CPV_NON_VALIDO;
      }
      String selectGarcpv = null;
      if ("2".equals(genere))
        selectGarcpv="select codcpv, tipcpv from garcpv where ngara=? ";
      else if("3".equals(genere))
        selectGarcpv="select codcpv, tipcpv, codiga from gare g LEFT JOIN garcpv gc on g.ngara=gc.ngara where codgar1=? and g.codgar1 != g.ngara order by g.ngara";
      else
        selectGarcpv="select codcpv, tipcpv, codiga from gare g LEFT JOIN garcpv gc on g.ngara=gc.ngara where codgar1=? order by g.ngara";

      Object par[]= new Object[1];
      if("2".equals(genere))
        par[0]=ngara;
      else
        par[0]=codgar;

      List datiGARE = sqlManager.getListVector(selectGarcpv,par);

      if (datiGARE != null && datiGARE.size() > 0) {
        String codiga=null;
        String codcpv = null;
        String tipcpv = null;
        boolean isFirst1 = true;
        boolean isFirst2 = true;
        for (int i = 0; i < datiGARE.size(); i++) {

          codcpv = (String)SqlManager.getValueFromVectorParam(
              datiGARE.get(i), 0).getValue();

          tipcpv = (String)SqlManager.getValueFromVectorParam(
              datiGARE.get(i), 1).getValue();

          if ((codcpv == null || "".equals(codcpv))) {
            if("2".equals(genere))
              errors.add(MessageError1);
            else{
              codiga = (String) SqlManager.getValueFromVectorParam(
                  datiGARE.get(i), 2).getValue();
              if(!isFirst1){
                MessageError1 = MessageError1 + ", ";
              }
              MessageError1 = MessageError1 + codiga;
              isFirst1 = false;
            }

          }else if(codcpv != null && !"".equals(codcpv) && "1".equals(tipcpv)){
            if(codcpv.length()>=4 && codcpv.charAt(3)=='0'){

              Long isFoglia = null;
              //controllo se il cpv (che non è di terzo livello) è foglia, se non lo è allora il controllo fallisce
              if(codcpv.length()>=4 && codcpv.charAt(2)!='0'){
                String query = "select count(*) from tabcpv a where cpvcod4 = ? and not exists(select * from tabcpv b where "+
                sqlManager.getDBFunction("substr",  new String[] {"a.cpvcod4","1","3"}) + " = " + sqlManager.getDBFunction("substr",  new String[] {"b.cpvcod4","1","3"})+
                " and a.cpvcod4<>b.cpvcod4)";
                isFoglia = (Long) sqlManager.getObject(query, new Object[] { codcpv });
              }else{
                String query = "select count(*) from tabcpv a where cpvcod4 = ? and not exists(select * from tabcpv b where "+
                sqlManager.getDBFunction("substr",  new String[] {"a.cpvcod4","1","2"}) + " = " + sqlManager.getDBFunction("substr",  new String[] {"b.cpvcod4","1","2"})+
                " and a.cpvcod4<>b.cpvcod4)";
                isFoglia = (Long) sqlManager.getObject(query, new Object[] { codcpv });
              }
              if(isFoglia.intValue()==0){
                if("2".equals(genere))
                  errors.add(MessageError2);
                else{
                  codiga = (String) SqlManager.getValueFromVectorParam(
                      datiGARE.get(i), 2).getValue();
                  if(!isFirst2){
                    MessageError2 = MessageError2 + ", ";
                  }
                  MessageError2 = MessageError2 + codiga;
                  isFirst2 = false;
                }
              }
            }
          }
        }
        if(!isFirst1){
          errors.add(MessageError1);
        }
        if(!isFirst2){
          errors.add(MessageError2);
        }
      }else{
        MessageError1=CTR_CPV;
        errors.add(MessageError1);
      }
    }
    return errors;
  }


  /**
   * Controlli sui campi CODCUI e CUPPRG
   * @param codgar
   * @param ngara
   * @param genere
   * @param profilo
   * @param campo
   * @return ArrayList<String>
   * @throws SQLException
   */
  public ArrayList<String> getControlloCUPCUI(String codgar, String ngara, String genere, String profilo, String campo) throws SQLException{
    ArrayList<String> errors = new ArrayList<String>();
    boolean sezioneVisibile=true;

    String sezione=null;
    if("CODCUI".equals(campo))
      sezione="CUI";
    else
      sezione="CUP";

    if("1".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.ALTRIDATI." + sezione) || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.ALTRIDATI") ))
      sezioneVisibile=false;
    else if("2".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.ALTRIDATI." + sezione) || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.ALTRIDATI")))
      sezioneVisibile=false;
    else if("3".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.DATIGENOFFUNICA." + sezione) || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.DATIGENOFFUNICA")))
      sezioneVisibile=false;

    if(sezioneVisibile){
      String MessageError1 = "";
      if("1".equals(genere) || "3".equals(genere)){
        if("CODCUI".equals(campo))
          MessageError1 = CTR_CUI_LOTTI;
        else
          MessageError1 = CTR_CUPPRG_LOTTI;
      }else{
        if("CODCUI".equals(campo))
          MessageError1 = CTR_CUI;
        else
          MessageError1 = CTR_CUPPRG;
      }

      String select = null;
      String entita=null;
      if("CODCUI".equals(campo))
        entita="GARE1";
      else
        entita="GARE";

      if ("2".equals(genere))
        select="select " + campo + " from " + entita + " where ngara=? ";
      else if ("1".equals(genere)){
        if("CODCUI".equals(campo))
          select="select CODCUI, codiga from gare1 g1, gare g where g1.ngara=g.ngara and g1.codgar1=?  order by codiga";
        else
          select="select CUPPRG, codiga from gare where codgar1=? order by codiga ";
      }else{
        if("CODCUI".equals(campo))
          select="select CODCUI, codiga from gare1 g1, gare g where g1.ngara=g.ngara and g1.codgar1=? and g1.ngara!=g1.codgar1 order by codiga ";
        else
          select="select CUPPRG, codiga from gare where codgar1=? and ngara!=codgar1 order by codiga";

      }


      Object par[]= new Object[1];
      if("2".equals(genere))
        par[0]=ngara;
      else
        par[0]=codgar;

      List datiGARE = sqlManager.getListVector(select,par);

      if (datiGARE != null && datiGARE.size() > 0) {
        String valoreCampo=null;
        String codiga=null;
        boolean isFirst = true;
        for (int i = 0; i < datiGARE.size(); i++) {

          valoreCampo = (String)SqlManager.getValueFromVectorParam(
              datiGARE.get(i), 0).getValue();


          if ((valoreCampo == null || "".equals(valoreCampo))) {
            if("2".equals(genere))
              errors.add(MessageError1);
            else{
              codiga = (String) SqlManager.getValueFromVectorParam(
                  datiGARE.get(i), 1).getValue();
              if(!isFirst){
                MessageError1 = MessageError1 + ", ";
              }
              MessageError1 = MessageError1 + codiga;
              isFirst = false;
            }

          }
        }
        if(!isFirst){
          if("1".equals(genere) || "3".equals(genere)){
            if("CODCUI".equals(campo))
              MessageError1+=". Procedendo tali lotti verranno intesi come non inseriti nella programmazione";
            else
              MessageError1+=". Procedendo tali lotti verranno intesi come esenti CUP";

          }
          errors.add(MessageError1);
        }

      }
    }
    return errors;
  }

  /***
   * Il metodo effettua i controlli bloccanti sui dati della gara per la richiesta
   * del codice  Smart CIG
   *
   * @param codgar
   * @param numeroLotto
   * @return HashMap
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String,Object> controllaDatiRichiestaSmartCIG(String codgar, String numeroLotto) throws SQLException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaSmartCIG: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();

    String  selectDatiGara = "select "
          + " gare.not_gar, "  //0
          + " torn.codrup, "   //1
          + " gare.tipgarg, "  //2
          + " gare.impapp, "    //3
          + " torn.cenint "    //4
          + " from torn, gare where torn.codgar = gare.codgar1 "
          + " and torn.codgar = ?";


    List datiTORNGARE = sqlManager.getVector(selectDatiGara,new Object[] { codgar });
    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {
        ArrayList erroriTemp=null;

        String oggetto = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
        //Oggetto della gara
        if (oggetto == null || "".equals(oggetto)) {
          erroriBloccanti.add(CTR_GARA_OGGETTO);
        }

        //Controllo codice fiscale stazione appaltante
        String cenint =  (String) SqlManager.getValueFromVectorParam(datiTORNGARE,4).getValue();
        if (cenint != null) {

          String cfein = (String) sqlManager.getObject("select cfein from uffint where codein =?", new Object[]{cenint});

          if(cfein==null || "".equals(cfein)){
            erroriBloccanti.add(CTR_SA_CF);
          }else{
            if (!UtilityFiscali.isValidPartitaIVA(cfein) & !UtilityFiscali.isValidCodiceFiscale(cfein)){
              erroriBloccanti.add(CTR_SA_CF_NON_VALIDO);
            }
          }

        }else{
          erroriBloccanti.add(CTR_SA);
        }

        String codRUP = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
        //RUP
        if (codRUP != null) {
            erroriBloccanti.addAll(this.controlloDatiTecnico(codRUP,true));
        }else{
          erroriBloccanti.add(CTR_GARA_RUP);
        }

        // Importo Gara
        Object importo = SqlManager.getValueFromVectorParam(
            datiTORNGARE, 3).getValue();
        if(importo==null)
          erroriBloccanti.add(CTR_GARA_IMPORTO);

        //Controllo su tipgar
        Long tipo = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 2).getValue();
        if (tipo == null) {
          erroriBloccanti.add(CTR_GARE_TORN_TIPGAR);
        }else{
          String tipoProc = this.inviaVigilanzaManager.getFromTab2("A1z05", tipo, true);
          if(tipoProc == null || "".equals(tipoProc)){
            erroriBloccanti.add(CTR_GARE_TORN_MAP_TIPGAR_A1z05);
          }
        }


    }

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);

    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaSmartCIG: fine metodo");
    return response;
  }



  /**
   * Restituisce XML contenente i dati della gara
   *  per la richiesta CIG
   * @param codgar
   * @param ngara
   * @return String
   * @throws SQLException
   * @throws IOException
   */
  private HashMap<String,String> getXMLDatiGaraSmartCig(String codgar, String ngara)
    throws SQLException,IOException, Exception {

    if (logger.isDebugEnabled())
            logger.debug("getXMLDatiGaraSmartCig: inizio metodo");

    String xml = null;

    SmartCIGDocument smartCigDocument = SmartCIGDocument.Factory.newInstance();
    smartCigDocument.documentProperties().setEncoding("UTF-8");
    SmartCIGType smartCigType= smartCigDocument.addNewSmartCIG();

    //UUID;
    String uuid = this.getIdRichiestaGara(codgar,UUID_RICHIESTA);
    //sqlManager.update("update gare1 set uuid=? where ngara=?", new Object[]{uuid,numeroLotto});
    smartCigType.setUUID(uuid);


    String selectDatiGara = "select "
        + " gare.not_gar, "     //0
        + " gare.codcig, "      //1
        + " torn.tipgen, "      //2
        + " torn.isadesione, "  //3
        + " torn.codcigaq, "    //4
        + " gare.cupprg, "      //5
        + " torn.codrup, "      //6
        + " gare.tipgarg, "     //7
        + " uffint.cfein, "     //8
        + " uffint.iscuc, "     //9
        + " uffint.cfanac, "    //10
        + " torn.altrisog "     //11
        + " from torn, gare,uffint where torn.codgar = gare.codgar1 "
        + " and torn.codgar = ? and torn.cenint = uffint.codein";


    List datiGara = sqlManager.getVector(selectDatiGara,new Object[] { codgar });
    if (datiGara != null && datiGara.size() > 0) {
      //Codice CIG
      String cig=(String) SqlManager.getValueFromVectorParam(datiGara,1).getValue();
      if(cig!=null && !"".equals(cig))
        smartCigType.setCIG(cig);

      //Fattispecie - non valorizzare???

      // Importo Gara
      String selectImporto="select valmax from v_gare_importi where codgar=? and ngara is not null";
      Double importoGara = null;
      Object importoGaraObject = sqlManager.getObject(selectImporto, new Object[] { codgar });
      if (importoGaraObject != null) {
        if (importoGaraObject instanceof Long)
          importoGara = new Double(((Long) importoGaraObject));
        else if (importoGaraObject instanceof Double)
          importoGara = new Double((Double) importoGaraObject);
      }
      smartCigType.setIMPORTO(importoGara.doubleValue());

      //Oggetto
      String oggetto = (String) SqlManager.getValueFromVectorParam(datiGara,0).getValue();
      if (oggetto.length()>1024)
        oggetto=oggetto.substring(0, 1024);
      smartCigType.setOGGETTO(oggetto);

      //tipoContratto (Lavori,Forniture,Servizi)
      Long tipoContratto = (Long) SqlManager.getValueFromVectorParam(datiGara,2).getValue();
      if (tipoContratto != null) {
          switch (tipoContratto.intValue()) {
          case 1: //lavori
            smartCigType.setTIPOCONTRATTO(W3Z05Type.L);
              break;
          case 2: //forniture
            smartCigType.setTIPOCONTRATTO(W3Z05Type.F);
              break;
          case 3: //servizi
            smartCigType.setTIPOCONTRATTO(W3Z05Type.S);
              break;
          }

      }

      //Cig Accordo quadro - solo nel caso di adesione ad accordo quadro
      String isAdesione = (String) SqlManager.getValueFromVectorParam(datiGara,3).getValue();
      if (isAdesione != null && "1".equals(isAdesione)){
        String cigAccordoQuadro = (String) SqlManager.getValueFromVectorParam(datiGara,4).getValue();
        if (cigAccordoQuadro != null) {
          smartCigType.setCIGACCQUADRO(cigAccordoQuadro);
        }
      }

      //CUP
      String cup = (String) SqlManager.getValueFromVectorParam(datiGara,5).getValue();
      if(cup!=null && !"".equals(cup))
        smartCigType.setCUP(cup);

      //Motivi comuni - non valorizzare???

      //Motivo richiesta -  non valorizzare???

      //Codice fiscale stazione appaltante
      String cfein = null;
      Long altrisog= (Long) SqlManager.getValueFromVectorParam(datiGara,11).getValue();
      String iscuc = (String) SqlManager.getValueFromVectorParam(datiGara,9).getValue();
      if("1".equals(iscuc) && (new Long(2).equals(altrisog) || new Long(3).equals(altrisog))){
        cfein = (String) SqlManager.getValueFromVectorParam(datiGara,10).getValue();
        if(cfein==null || "".equals(cfein))
          cfein = (String) SqlManager.getValueFromVectorParam(datiGara,8).getValue();
      }else
        cfein = (String) SqlManager.getValueFromVectorParam(datiGara,8).getValue();

      smartCigType.setCFSTAZIONEAPPALTANTE(cfein);

      //RUP
      String codRUP = (String) SqlManager.getValueFromVectorParam(datiGara,6).getValue();
      if (codRUP != null) {
          TecnicoType tecnicoType = TecnicoType.Factory.newInstance();
          tecnicoType = this.getTecnico(codRUP);
          smartCigType.setRUP(tecnicoType);
      }

      //Scelta contraente
      Long tipgarg=(Long) SqlManager.getValueFromVectorParam(datiGara,7).getValue();
      String proceduraScelta= inviaVigilanzaManager.getFromTab2("A1z05", tipgarg,true);
      smartCigType.setSCELTACONTRAENTE(W3Z20Type.Enum.forString(proceduraScelta));


      //Categorie
      CategorieMerceologicheSmartCIGType categoria = CategorieMerceologicheSmartCIGType.Factory.newInstance();
      categoria.addCATEGORIA(new BigInteger("999"));
      smartCigType.setCATEGORIE(categoria);
    }

    ByteArrayOutputStream baosSmartCigType = new ByteArrayOutputStream();
    smartCigDocument.save(baosSmartCigType);
    xml = baosSmartCigType.toString();
    baosSmartCigType.close();

    ArrayList validationErrors = new ArrayList();
    XmlOptions validationOptions = new XmlOptions();
    validationOptions.setErrorListener(validationErrors);
    boolean isValid = smartCigDocument.validate(validationOptions);
    if (!isValid) {
        String listaErroriValidazione = "";
        Iterator iter = validationErrors.iterator();
        while (iter.hasNext()) {
          listaErroriValidazione += iter.next() + "\n";
        }
        logger.error("La generazione dei dati di gara per la richiesta Smart CIG non rispetta il formato previsto: "
            + xml
            + "\n"
            + listaErroriValidazione);
        throw new GestoreException(
            "La generazione dei dati di gara per la richiesta Samrt CIG non rispetta il formato previsto: ",
            "errors.inviadatirichiestacig.validazione",
            new Object[] { listaErroriValidazione }, null);
      }



    if (logger.isDebugEnabled())
              logger.debug("getXMLDatiGaraSmartCig: fine metodo");

    HashMap<String,String> hm = new HashMap<String,String>();
    hm.put(XML_MAP_KEY, xml);
    hm.put(UUID_MAP_KEY, uuid);
    return hm;

  }

  /**
  *
  * @param codgar
  * @param numeroLotto
  * @param username
  * @param password
  * @return
  * @throws Exception
  * @throws Throwable
  */

 @SuppressWarnings("unchecked")
 public String inviaDatiRichiestaSmartCig(String codgar,String numeroLotto,String username, String password) throws Exception, Throwable {

     if (logger.isDebugEnabled())
         logger.debug("inviaDatiRichiestaSmartCig: inizio metodo");

     String esitoWS =  null;
     String url = this.getUrl();

       try {
         EldasoftSimogWS servizio = this.getWs(url);

               EsitoInserisciSmartCIG esitoSimogWS = new EsitoInserisciSmartCIG();
               HashMap<String,String> hm = this.getXMLDatiGaraSmartCig(codgar,numeroLotto);
               String xml = hm.get(XML_MAP_KEY);
               String uuid = hm.get(UUID_MAP_KEY);
               if (xml != null) {
                 logger.debug("inviaDatiRichiestaSmartCig: XML = \n" + xml);
                 esitoSimogWS = servizio.inserisciSmartCIG(username, password, xml);

                 if(esitoSimogWS.isEsito()){
                   if(esitoSimogWS.getOperazioniDML()!=null ){
                       String tipoInformazione = null;
                       String tipoOperazione =  null;
                       esitoWS = "";

                        for(int i=0; i < esitoSimogWS.getOperazioniDML().length; i++){
                           esitoSimogWS.getOperazioniDML(i);
                           tipoInformazione=esitoSimogWS.getOperazioniDML(i).getTipoInformazione().toString();
                           tipoOperazione = esitoSimogWS.getOperazioniDML(i).getTipoOperazione().toString();
                           String strEsitoWS = "";

                           if("SMARTCIG".equals(tipoInformazione)){
                             if("INS".equals(tipoOperazione))
                                 strEsitoWS = "E' stata inserito lo Smart cig per la gara "+numeroLotto;
                             if("UPD".equals(tipoOperazione))
                                 strEsitoWS = "E' stata aggiornato lo Smart cig per la gara "+numeroLotto;
                             if("".equals(tipoOperazione))
                                 strEsitoWS = "Non è stata eseguita nessuna operazione per la gara "+numeroLotto;

                           }
                           esitoWS=esitoWS+strEsitoWS+"\n";

                        }//for

                   }else{
                       esitoWS="Non sono state effettuate modifiche"+"\n"+"ai dati della gara corrente"+"\n";
                   }

                   sqlManager.update("update torn set uuid=? where codgar=?",new Object[]{uuid,codgar});
                   sqlManager.update("update gare1 set uuid=? where ngara=?", new Object[]{uuid,numeroLotto});

                   //flagEsito = "OK";
                 }else{
                   esitoWS = esitoSimogWS.getMessaggio();
                   //flagEsito = "KO";
                 }//if esito

               }//if xml

               // if (flagEsito=="KO" )
               //     throw new Exception(esitoWS);
           } catch (RemoteException e) {
             throw new GestoreException(
                 "Si e' verificato un errore durante la connessione al web service!",
                 "errors.RichiestaCig.ws.error", e);
           } catch (ServiceException e) {
             throw new GestoreException(
                 "Si e' verificato un errore durante la connessione al web service!",
                 "errors.RichiestaCig.ws.error", e);
           } catch (Throwable t) {
               throw t;
           }




     if (logger.isDebugEnabled())
         logger.debug("inviaDatiRichiestaSmartCig: fine metodo");
     if("\n".equals(esitoWS))
       esitoWS=null;

   return esitoWS;

 }

 /**
  * Vengono effettuati i controlli sui campi MODREA e CODNUTS, ma solo se da profilo sono visibili.
  * Viene restituito un vettore con l'esito dei controlli ("OK" o "NOK") con indici:
  * 0 MODREA
  * 1 CODNUTS
  *
  * @param modrea
  * @param codnuts
  * @param genere
  * @param profilo
  * @return String[]
  */
 public String[] controlloModreaCodnuts(String modrea, String codnuts, String genere, String profilo){
   boolean campoVisibile=true;
   boolean sezioneVisibile=true;
   String res[]= new String[]{"OK","OK"};
   if (!geneManager.getProfili().checkProtec(profilo, "COLS", "VIS","GARE.TORN.MODREA"))
     campoVisibile=false;

   if("1".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-scheda.ALTRIDATI.COMPBANDO") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.TORN-scheda.ALTRIDATI") ))
     sezioneVisibile=false;
   else if("2".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.ALTRIDATI.COMPBANDO") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.ALTRIDATI")))
     sezioneVisibile=false;
   else if("3".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-OFFUNICA-scheda.ALTRIDATI.COMPBANDO") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.TORN-OFFUNICA-scheda.ALTRIDATI")))
     sezioneVisibile=false;

   if(campoVisibile && sezioneVisibile && (modrea==null || "".equals(modrea))){
     res[0]="NOK";
   }

   campoVisibile=true;
   if (!geneManager.getProfili().checkProtec(profilo, "COLS", "VIS","GARE.TORN.CODNUTS"))
     campoVisibile=false;
   if(campoVisibile && sezioneVisibile && (codnuts==null || "".equals(codnuts))){
     res[1]="NOK";
   }
   return res;
 }
 
 /**
  * Vengono effettuati i controlli sui campi codnuts e locint, ma solo se da profilo risultano visibili.
  * Si verificando che siano presenti almeno uno dei due tra NUTS o ISTAT.
  * Viene restituito un vettore,in quanto estendibile ad altri campi della sezione, con l'esito dei controlli ("OK" o "NOK") con indici:
  * 0 CODNUTS
  * 1 LOCINT
  *
  * @param codnuts
  * @param locint
  * @param genere
  * @param profilo
  * @return String[]
  */
 public String[] controlloNutsIstat(String codnuts, String locint, String genere, String profilo){
   boolean campoVisibile=true;
   boolean sezioneVisibile=true;
   boolean visNuts=false;
   boolean visIstat=false;
   String res[]= new String[]{"OK","OK"};
   if (!geneManager.getProfili().checkProtec(profilo, "COLS", "VIS","GARE.GARE.LOCINT"))
     campoVisibile=false;

   if("1".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.ALTRIDATI.LOC") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.ALTRIDATI")))
     sezioneVisibile=false;
   else if("2".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.ALTRIDATI.LOC") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.GARE-scheda.ALTRIDATI")))
     sezioneVisibile=false;
   else if("3".equals(genere) && (!geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-OFFUNICA-scheda.ALTRIDATI.LOC") || !geneManager.getProfili().checkProtec(profilo, "PAGE", "VIS","GARE.TORN-OFFUNICA-scheda.ALTRIDATI")))
     sezioneVisibile=false;
   if(campoVisibile && sezioneVisibile) {
	   visIstat=true;  
   }
   campoVisibile=true;
   if (!geneManager.getProfili().checkProtec(profilo, "COLS", "VIS","GARE.TORN.CODNUTS"))
     campoVisibile=false;
   if(campoVisibile){
	   visNuts=true;
   }
   
   codnuts = StringUtils.stripToEmpty(codnuts);
   locint = StringUtils.stripToEmpty(locint);
   
   if(visNuts && visIstat) {
	   if("".equals(codnuts) && "".equals(locint)) {
	     res[0]="NOK";
	     res[1]="NOK";
	   }
   }
   
   if(visNuts && !visIstat) {
	   if("".equals(codnuts)) {
	     res[0]="NOK";
	     res[1]="OK";
	   }
   }
   
   if(!visNuts && visIstat) {
	   if("".equals(locint)) {
	     res[0]="OK";
	     res[1]="NOK";
	   }
   }
   
   return res;
   
 }
 
}

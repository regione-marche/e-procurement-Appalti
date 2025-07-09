package it.eldasoft.sil.pg.ws.rest.durc.richiestaDURC;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.tasks.VerificaRegolaritaManager;
import it.eldasoft.sil.pg.ws.rest.durc.richiestaDURC.entity.RichiestaDURCResponse;
import it.eldasoft.sil.pg.ws.rest.durc.richiestaDURC.entity.RispostaCfRichiestaDurc;
import it.eldasoft.sil.pg.ws.rest.durc.richiestaDURC.entity.RispostaDurc;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

@Path("/")
public class WSRichiestaDURC {

  static Logger           logger = Logger.getLogger(WSRichiestaDURC.class);

  private static final String AUTHENTICATION_HEADER = "Authorization";
  private static final String SERVIZIO_ATTIVO = "richiesta.durc.ws.rest.servizioattivo";
  private static final String SERVIZIO_PROTETTO_AUTENTICAZIONE = "richiesta.durc.ws.rest.autenticazione";
  private static final String UTENTE_ACCESSO = "richiesta.durc.ws.rest.utente";
  private static final String PASSWORD_ACCESSO = "richiesta.durc.ws.rest.password";

  private static final String MESSAGGIO_SERVIZIO_NON_ATTIVO     = "Il servizio non e' attivo";
  private static final String MESSAGGIO_NO_BASIC_AUTHENTICATION = "Informazioni di autenticazione non presenti";
  private static final String MESSAGGIO_NO_LOGIN                = "Il servizio non e' stato configurato con le credenziali di accesso: contattare l'amministratore. Per impostare le credenziali di accesso e' necessario accedere al pannello di amministrazione dell'applicativo Appalti";
  private static final String MESSAGGIO_UTENTE_NON_ABILITATO    = "L'utente indicato non e' abilitato ad utilizzare il servizio";


  @POST
  @Path("RispostaDurc")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response richiestaDURC(String in, @Context HttpServletRequest request) throws Exception
  {
    TransactionStatus statusTrans = null;
    boolean doCommit = true;
    int livEvento = 1;
    String errMsgEvento = "";
    Integer status = 200;
    RichiestaDURCResponse richiestaDURCResponse = new RichiestaDURCResponse();
    VerificaRegolaritaManager ws = (VerificaRegolaritaManager) UtilitySpring.getBean("verificaRegolaritaManager", request.getSession().getServletContext(), VerificaRegolaritaManager.class);
    RispostaCfRichiestaDurc respCF = null;
    RispostaDurc rispostaDURC = null;
    try {
      ObjectMapper mapper = JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
      rispostaDURC = mapper.readValue(in, RispostaDurc.class);
      if(controllaRispostaDurc(rispostaDURC)) {
        richiestaDURCResponse.setEsito(1);
        respCF = rispostaDURC.getRispostaCfRichiestaDurc();
        if(gestioneServizio(richiestaDURCResponse, request)) {

          statusTrans = ws.getSqlManager().startTransaction();
          String query = "UPDATE IMPDURC SET "
              + "NPROTO = ?, "
              + "ESITO_RICHIESTA_DURC = ?, "
              + "DATESITO = ?, "
              + "TIPESI = ? ";
          if(respCF.getDocumentoPDF() != null) {
            query += ",NOTESITO = ?, "
                + "DATSCA = ?, "
                + "ANNULLATO = ?, "
                + "DATANNULLAMENTO = ? ";
          } else if("ERR".equalsIgnoreCase(respCF.getEsitoRichiestaDurc())) {
            query += ",NOTESITO = ?";
          }
          query += "WHERE ID_RICHIESTA = ? "
              + "AND CODIMP = ?";

          Long idImpDurc = null;
          Vector<?> datiImpresa = ws.getSqlManager().getVector("select codimp from impr where cfimp = ?", new Object[]{respCF.getCodiceFiscale()});
          if(datiImpresa!=null && !datiImpresa.isEmpty()){
            String codiceImpresa = (String) SqlManager.getValueFromVectorParam(datiImpresa, 0).getValue();

            Long conteggio = (Long)ws.getSqlManager().getObject("select count(id) from IMPDURC where ID_RICHIESTA=? and CODIMP=?", new Object[]{rispostaDURC.getIdRichiestaApplicazione(), codiceImpresa});
            if(conteggio!= null && conteggio.longValue()==0)
              throw new GestoreException("Non è stata trovata la richiesta per idRichiesta: "+rispostaDURC.getIdRichiestaApplicazione()+" e codice impresa: "+codiceImpresa, null, new Exception());

            idImpDurc = (Long) ws.getSqlManager().getObject("select id from IMPDURC WHERE ID_RICHIESTA = ? AND CODIMP = ?", new Object[]{rispostaDURC.getIdRichiestaApplicazione(), codiceImpresa});
            String tipesi = "";
            String situazdoci="";
            switch (respCF.getEsitoRichiestaDurc()) {
            case "0":
              tipesi="1";
              situazdoci="3";
              break;
            case "1":
              tipesi="2";
              situazdoci="5";
              break;
            case "4":
            case "5":
            case "6":
              tipesi="3";
              situazdoci="2";
              break;
            case "3":
            case "7":
            case "ERR":
              tipesi="4";
              situazdoci="1";
              if("ERR".equalsIgnoreCase(respCF.getEsitoRichiestaDurc()))
                respCF.setEsitoRichiestaDurc("20");
              break;
            case "8":
              tipesi="5";
              situazdoci="6";
              break;
            default:
              break;
            }

            Timestamp dataEsito = null;
            Timestamp dataScadenza = null;
            if(respCF.getDataEsito() != null && !"".equalsIgnoreCase(respCF.getDataEsito())) {
              dataEsito = new Timestamp(UtilityDate.convertiData(respCF.getDataEsito(), UtilityDate.FORMATO_STANDARD_XSD).getTime());
            }
            if(respCF.getDocumentoPDF() != null) {
              if(respCF.getDataFineValidita()!=null && !"".equalsIgnoreCase(respCF.getDataFineValidita())) {
                dataScadenza = new Timestamp(UtilityDate.convertiData(respCF.getDataFineValidita(), UtilityDate.FORMATO_STANDARD_XSD).getTime());
              }
              Timestamp dataAnnullamento = null;
              if(respCF.getDataAnnullamento()!=null && !"".equalsIgnoreCase(respCF.getDataAnnullamento())) {
                dataAnnullamento = new Timestamp(UtilityDate.convertiData(respCF.getDataAnnullamento(), UtilityDate.FORMATO_STANDARD_XSD).getTime());
              }
              ws.getSqlManager().update(query, new Object[] {respCF.getProtocollo(), respCF.getEsitoRichiestaDurc(), dataEsito, tipesi,respCF.getMotivoAnnullamento(),dataScadenza, respCF.isAnnullato()==null?null:respCF.isAnnullato()?1:0,dataAnnullamento,rispostaDURC.getIdRichiestaApplicazione(),codiceImpresa});
            } else if("ERR".equalsIgnoreCase(respCF.getEsitoRichiestaDurc())) {
              ws.getSqlManager().update(query, new Object[] {respCF.getProtocollo(), respCF.getEsitoRichiestaDurc(), dataEsito, tipesi,"Errore generico dell'INPS",rispostaDURC.getIdRichiestaApplicazione(),codiceImpresa});
            } else {
              ws.getSqlManager().update(query, new Object[] {respCF.getProtocollo(), respCF.getEsitoRichiestaDurc(), dataEsito, tipesi,rispostaDURC.getIdRichiestaApplicazione(),codiceImpresa});
            }
            Vector<?> datiIMPRDOCG = ws.getSqlManager().getVector("select codgar,ngara,norddoci from IMPRDOCG where codimp = ? and descrizione = ?", new Object[]{codiceImpresa, "DURC"});
            if(datiIMPRDOCG!=null && !datiIMPRDOCG.isEmpty()){
              String codiceGara = (String) SqlManager.getValueFromVectorParam(datiIMPRDOCG, 0).getValue();
              String numeroGara = (String) SqlManager.getValueFromVectorParam(datiIMPRDOCG, 1).getValue();
              Long nordDoci = SqlManager.getValueFromVectorParam(datiIMPRDOCG, 2).longValue();

              if(respCF.getDocumentoPDF() != null) {
                String insertWDocdig = "insert into W_DOCDIG (idprg,iddocdig,digent,digkey1,digkey2,digkey4,dignomdoc,digogg) values (?,?,?,?,?,?,?,?)";
                String insertIMPRDOCG = "UPDATE IMPRDOCG SET IDPRG = ?, IDDOCDG = ?, DATARILASCIO = ?, DATASCADENZA = ?, SITUAZDOCI = ? where codimp = ? and descrizione = ?";

                Long maxIDDOCDIG = (Long) ws.getSqlManager().getObject( "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?", new Object[] { "PG" });
                long newIDDOCDIG = 1;
                if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0){
                  newIDDOCDIG = maxIDDOCDIG.longValue() + 1;
                }
                LobHandler lobHandler = new DefaultLobHandler();
                String nome = "documento_durc.pdf";
                ws.getSqlManager().update(insertWDocdig, new Object[] {"PG",newIDDOCDIG,"IMPRDOCG",codiceGara,nordDoci,idImpDurc,nome,new SqlLobValue(respCF.getDocumentoPDF(), lobHandler)});
                ObjectStorageUtils.addFile("PG", newIDDOCDIG);
                ws.getSqlManager().update(insertIMPRDOCG, new Object[] {"PG",newIDDOCDIG, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), dataScadenza, situazdoci, codiceImpresa, "DURC"});
              }  
            }
          } else {
            throw new GestoreException("Non è stata trovata l'impresa per codice fiscale: "+respCF.getCodiceFiscale(), null, new Exception());
          }
        }
      } else {
        throw new GestoreException("Il formato della richiesta non rispetta le specifiche, controllare i dati in input.", null, new Exception());
      }
    } catch (Exception e) {
      status = 500;
      livEvento = 3;
      errMsgEvento = "Errore ws in richiesta durc: "+e.getMessage();
      doCommit = false;
      richiestaDURCResponse.setEsito(0);
      richiestaDURCResponse.setMessaggio(errMsgEvento);
    } finally {
      if (statusTrans != null) {
        try {
          if (doCommit) {
            ws.getSqlManager().commitTransaction(statusTrans);
          } else {
            ws.getSqlManager().rollbackTransaction(statusTrans);
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
        logEvento.setCodEvento("GA_WSDURC_RES_RICHIESTA_DURC");
        logEvento.setDescr("Response Richiesta DURC idRichiesta: "+rispostaDURC.getIdRichiestaApplicazione()+" CodiceFiscale: "+respCF.getCodiceFiscale());
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      }catch(Exception e){
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi");
      }
    }
    return Response.status(status).entity(richiestaDURCResponse).build();
  }


  private boolean controllaRispostaDurc(RispostaDurc rispostaDURC) {
    boolean passed = false;
    if(rispostaDURC != null) {
      if(rispostaDURC.getIdRichiestaApplicazione()!=null && !"".equalsIgnoreCase(rispostaDURC.getIdRichiestaApplicazione())) {
        if(rispostaDURC.getRispostaCfRichiestaDurc()!=null) {
          RispostaCfRichiestaDurc risposta = rispostaDURC.getRispostaCfRichiestaDurc();
          if(risposta.getId()!=null) {
            if(risposta.getCodiceFiscale()!=null && (risposta.getCodiceFiscale().length()==16 || risposta.getCodiceFiscale().length()==11)) {
              if(risposta.getEsitoRichiestaDurc()!=null && risposta.getEsitoRichiestaDurc().length()<=3) {
                if(controllaDatiNonObbligatori(risposta.getProtocollo(), 50)) {
                  if(controllaDatiNonObbligatori(risposta.getDenominazione(), 255)) {
                    if(controllaDatiNonObbligatori(risposta.getSedeLegale(), 255)) {
                      if(controllaDataNonObbligatoria(risposta.getDataRichiesta())) {
                        if(controllaDataNonObbligatoria(risposta.getDataFineValidita())) {
                          passed = true;
                        }
                      }
                    }
                  }
                }
              }
            } 
          }
        }
      }
    }

    return passed;
  }

  private boolean controllaDatiNonObbligatori(String dato, int maxLength){
    boolean passed = false;
    if(dato==null || "".equalsIgnoreCase(dato) || dato.length()<=maxLength) {
      passed = true;
    }
    return passed;
  }
  
  private boolean controllaDataNonObbligatoria(String data){
    boolean passed = false;
    
    if(data!= null) {
		if(data.length()>19) {
			data= data.substring(0,19);
		}
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    format.setLenient(false);

    ParsePosition pos = new ParsePosition(0);

    format.parse(data, pos);
    if (pos.getErrorIndex() >= 0) {
      passed = false;
    } else if (pos.getIndex() != data.length()) {
      passed = false;
    } else {
      passed = true;
    }

    return passed;
  }

  private boolean gestioneServizio(RichiestaDURCResponse json, HttpServletRequest request) {

    boolean _ok = false;

    try {
      // Verifico se il servizio è attivo. Solo se il valore indicato e' pari a
      // "1" si considera il servizio attivo. In tutti gli altri casi il
      // servizio deve apparire inattivo.
      String servizio_attivo = ConfigManager.getValore(SERVIZIO_ATTIVO);
      if ("1".equals(servizio_attivo)) {
        // Verifico se il servizio deve essere protetto da credenziali.
        String servizio_protetto_autenticazione = ConfigManager.getValore(SERVIZIO_PROTETTO_AUTENTICAZIONE);
        if ("0".equals(servizio_protetto_autenticazione)) {
          // Il servizio esposto e' impostato per non richiedere le credenziali
          // di accesso in formato basicAuthenticazion. Si procedere senza
          // ulteriore verifica.
          _ok = true;
        } else {
          // Il servizio richiede autenticazione
          String auth = request.getHeader(AUTHENTICATION_HEADER);
          if (auth == null || "".equals(auth.trim())) {
            // La richiesta proveniente dal client non contiene alcun header di
            // autenticazione.
            _ok = false;
            json.setEsito(0);
            json.setMessaggio(MESSAGGIO_NO_BASIC_AUTHENTICATION);
          } else {
            // Verifica delle credenziali indicate nell'header di
            // autenticazione.
            final String usernameAndPasswordEncoded = auth.replaceFirst("Basic" + " ", "");
            byte[] usernameAndPasswordDecoded = Base64.decodeBase64(usernameAndPasswordEncoded);
            String usernameAndPassword = new String(usernameAndPasswordDecoded, "UTF-8");
            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            final String username = tokenizer.nextToken();
            final String password = tokenizer.nextToken();

            // Verifica utente per la connessione al servizio
            String utente_accesso = ConfigManager.getValore(UTENTE_ACCESSO);
            String password_accesso = ConfigManager.getValore(PASSWORD_ACCESSO);
            if (utente_accesso != null) {
              ICriptazioneByte valoreICriptazioneByte = null;
              valoreICriptazioneByte = FactoryCriptazioneByte.getInstance(
                  ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password_accesso.getBytes(),
                  ICriptazioneByte.FORMATO_DATO_CIFRATO);
              password_accesso = new String(valoreICriptazioneByte.getDatoNonCifrato());
            }

            // Controllo se il servizio e' stato configurato con le credenziali
            // di autenticazione.
            if (utente_accesso != null
                && !"".equals(utente_accesso.trim())
                && password_accesso != null
                && !"".equals(password_accesso.trim())) {

              // Verifica se le credenziali indicate dal client con l'header di
              // autenticazione corrispondono alle credenziali di accesso
              // impostate dall'amministratore per usufruire del servizio.
              if (!utente_accesso.equals(username) || !password_accesso.equals(password)) {
                // Le credenziali indicate nell'header dal client NON
                // CORRISPONDONO alle credenziali di accesso al servizio
                _ok = false;
                json.setEsito(0);
                json.setMessaggio(MESSAGGIO_UTENTE_NON_ABILITATO);
              } else {
                _ok = true;
              }

            } else {
              // Il servizio non e' stato configurato con le credenziali di
              // autenticazione.
              _ok = false;
              json.setEsito(0);
              json.setMessaggio(MESSAGGIO_NO_LOGIN);
            }
          }
        }
      } else {
        // Il servizio non e' attivo
        _ok = false;
        json.setEsito(0);
        json.setMessaggio(MESSAGGIO_SERVIZIO_NON_ATTIVO);
      }
    } catch (Exception e) {
      json.setEsito(0);
      json.setMessaggio(e.getMessage());
    }

    return _ok;

  }

}

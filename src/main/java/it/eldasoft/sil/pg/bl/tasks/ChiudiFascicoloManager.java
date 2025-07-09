package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.web.struts.WSDMChiudiFascicoloAction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloChiudiInType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloChiudiResType;
import it.maggioli.eldasoft.ws.dm.WSDMOperatoreType;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author Manuel Bridda
 */
public class ChiudiFascicoloManager {

  static Logger                                 logger = Logger.getLogger(ChiudiFascicoloManager.class);


  private ArchiviazioneDocumentiManager         archiviazioneDocumentiManager;

  private SqlManager                            sqlManager;

  private AnalizzaArchiviazioneDocumentiManager analizzaArchiviazioneDocumentiManager;

  private GestioneWSDMManager                   gestioneWSDMManager;

  private TabellatiManager                      tabellatiManager;

  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setAnalizzaArchiviazioneDocumentiManager(AnalizzaArchiviazioneDocumentiManager analizzaArchiviazioneDocumentiManager) {
    this.analizzaArchiviazioneDocumentiManager = analizzaArchiviazioneDocumentiManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public JSONObject check(String beforeDate, String codicefascicolo, boolean task) throws SQLException, GestoreException {

    JSONObject result = new JSONObject();

    String afterDate = "01/01/1900";
    String warnings = "";
    String errors = "";
    String newLine="<br>";
    if(task){
      newLine="\n";
    }

    List procedure = sqlManager.getListVector("select key1, entita from wsfascicolo where codice=?", new Object[]{codicefascicolo});
    if (procedure != null && !procedure.isEmpty()) {
      for (Object procedura : procedure) {
        String key = SqlManager.getValueFromVectorParam(procedura, 0).getStringValue();
        String entita = SqlManager.getValueFromVectorParam(procedura, 1).getStringValue();
        JSONObject data;
        if ("G1STIPULA".equals(entita)) {
          String codstipula = (String) sqlManager.getObject("select codstipula from g1stipula where id=?", new Object[]{new Long(key)});
          List<?> datiStipula = sqlManager.getListVector(analizzaArchiviazioneDocumentiManager.getQueryDocStipula(beforeDate, afterDate, " and id=? "), new Object[]{new Long(key)});
          if (datiStipula != null && !datiStipula.isEmpty()) {
            data = archiviazioneDocumentiManager.getListaDocumenti("", "", key, "true", null, false, false);
            if (data != null) {
              int recordDaArchiviareDocumentale = (int) data.get("documentiDaArchiviareDocumentale");
              if (recordDaArchiviareDocumentale > 0) {
                warnings += newLine+"- La stipula " + codstipula + " presenta dei documenti non ancora archiviati";
              }
            }
          } else {
            errors += newLine+"- La stipula " + key + " non è ancora conclusa";
          }
        } else if ("TORN".equals(entita) || "GARE".equals(entita)) {
          List<?> datiGara = sqlManager.getListVector(analizzaArchiviazioneDocumentiManager.getQueryDocGara(beforeDate, afterDate, " and vgg.codice=? "), new Object[]{key});
          if (datiGara != null && !datiGara.isEmpty()) {
            String codgar = SqlManager.getValueFromVectorParam(datiGara.get(0), 0).getStringValue();
            String genere = SqlManager.getValueFromVectorParam(datiGara.get(0), 1).getStringValue();
            String codice = SqlManager.getValueFromVectorParam(datiGara.get(0), 2).getStringValue();
            data = archiviazioneDocumentiManager.getListaDocumenti(codgar, genere, codice, "false", null, false, false);
            if (data != null) {
              int recordDaArchiviareDocumentale = (int) data.get("documentiDaArchiviareDocumentale");
              if (recordDaArchiviareDocumentale > 0) {
                warnings += newLine+"- La procedura " + key + " presenta dei documenti non ancora archiviati";
              }
            }
          } else {
            errors += newLine+"- La procedura " + key + " non è ancora conclusa";
          }
        }
      }
    }
    if (StringUtils.isNotBlank(errors)) {
      result.put("esito", "KO");
      result.put("errors", (task? "": "Impossibile procedere alla chiusura del fascicolo:") + errors);
    } else {
      if (StringUtils.isNotBlank(warnings)) {
        result.put("esito", "WARN");
        result.put("warnings", (task? "": "Sono presenti le seguenti segnalazioni non bloccanti:") + warnings);
      } else {
        result.put("esito", "OK");
      }
    }
    return result;
  }

  public void analizzaEChiudi() {
    if (!WebUtilities.isAppNotReady()) {
      if (logger.isDebugEnabled())
        logger.debug("Avvio chiusura fascicoli conclusi");
      List<String> errMsgs = new ArrayList<>();
      try {
        List<String> fascicoliElaborati = new ArrayList<>();
        String valoreTabellato = tabellatiManager.getDescrSupplementare("A1z36", "1");
        int profondita;
        try {
          profondita = Integer.parseInt(valoreTabellato);
        }catch(Exception e){
          errMsgs.add("il valore impostato per la profondità dell'analisi dello stato di archiviazione dei documenti non è valido");
          throw e;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -profondita);
        Date date = calendar.getTime();
        String beforeDate = UtilityDate.convertiData(date, UtilityDate.FORMATO_GG_MM_AAAA);
        List fascicoloAperti = sqlManager.getListVector("select ws.entita, ws.key1, vgg.codgar, ws.codice from wsfascicolo ws left join v_gare_genere vgg on vgg.codice=ws.key1 \n" +
                "   where (ws.ischiuso is null or ws.ischiuso <> '1') and ws.entita in ('GARE','TORN','G1STIPULA')", new Object[]{});
        String codicefascicolo = null;
        for (Object fascicolo : fascicoloAperti) {
          try {
            codicefascicolo = SqlManager.getValueFromVectorParam(fascicolo, 3).stringValue();
            if (!fascicoliElaborati.contains(codicefascicolo)) {
              fascicoliElaborati.add(codicefascicolo);
              String entita = SqlManager.getValueFromVectorParam(fascicolo, 0).stringValue();
              String key1 = SqlManager.getValueFromVectorParam(fascicolo, 1).stringValue();
              Long idconfiL;
              if ("G1STIPULA".equals(entita)) {
                idconfiL = gestioneWSDMManager.getWsdmConfigurazioneFromIdStipula(new Long(key1), "PG");
              } else {
                String codgar = SqlManager.getValueFromVectorParam(fascicolo, 2).stringValue();
                idconfiL = gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(codgar, "PG");
              }
              String idconfi = Long.toString(idconfiL);
              boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
              if (isIntegrazioneWSDMAttivaValida) {
                WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
                if (configurazione.isEsito()) {
                  String tipoWSDM = configurazione.getRemotewsdm();
                  if ("PALEO".equals(tipoWSDM)) {
                    String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE + idconfi);
                    if ("1".equals(wsdmLoginComune)) {
                      String[] datiLoginComune = gestioneWSDMManager.getWSDMLoginComune(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
                      String username = datiLoginComune[0];
                      String password = datiLoginComune[1];
                      String ruolo = datiLoginComune[2];
                      String nome = datiLoginComune[3];
                      String cognome = datiLoginComune[4];
                      String codiceuo = datiLoginComune[5];
                      String idutente = datiLoginComune[6];
                      String idutenteunop = datiLoginComune[7];

                      JSONObject result =  check(beforeDate,codicefascicolo,true);
                      if("OK".equals(result.getString("esito"))) {
                        WSDMOperatoreType wsdmOperatore = new WSDMOperatoreType();
                        wsdmOperatore.setNome(nome);
                        wsdmOperatore.setCognome(cognome);
                        wsdmOperatore.setRuolo(ruolo);
                        wsdmOperatore.setCodiceUO(codiceuo);

                        WSDMFascicoloChiudiInType wsdmFascicoloChiudiIn = new WSDMFascicoloChiudiInType();
                        wsdmFascicoloChiudiIn.setCodiceFascicolo(codicefascicolo);
                        wsdmFascicoloChiudiIn.setCustode(wsdmOperatore);

                        WSDMFascicoloChiudiResType wsdmFascicoloRes = gestioneWSDMManager.WSDMFascicoloChiudi(username, password,
                                ruolo, nome, cognome, codiceuo, idutente, idutenteunop, wsdmFascicoloChiudiIn, GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, "", "", idconfi);

                        if (wsdmFascicoloRes.isEsito()) {
                          TransactionStatus status = null;
                          try {
                            status =sqlManager.startTransaction();
                            sqlManager.update("update wsfascicolo set ischiuso='1' where codice=?", new Object[]{codicefascicolo});
                            sqlManager.commitTransaction(status);
                            errMsgs.add("Chiusura del fascicolo " + codicefascicolo + " conclusa con successo.");
                          } catch (Exception e) {
                            sqlManager.rollbackTransaction(status);
                            logger.error(e);
                            errMsgs.add("Chiusura del fascicolo " + codicefascicolo + " fallita: " + e);
                          }
                        }else {
                          String messaggio = wsdmFascicoloRes.getMessaggio();
                          logger.error(messaggio);
                          errMsgs.add("Chiusura del fascicolo " + codicefascicolo + " fallita: " + messaggio);
                        }
                      }else {
                        if ("KO".equals(result.getString("esito")))
                          ;//errMsgs.add("Chiusura del fascicolo "+codicefascicolo+" bloccata:"+result.getString("errors")); //non ci interessa loggare le procedure non concluse
                        else
                          errMsgs.add("Chiusura del fascicolo "+codicefascicolo+" bloccata:"+result.getString("warnings"));
                      }
                    }else{
                      errMsgs.add("Chiusura del fascicolo "+codicefascicolo+" fallita: non sono state impostate le credenziali comuni per la configurazione "+idconfi);
                    }
                  }
                }
              }
            }
          } catch(Exception e){
            logger.error(e);
            errMsgs.add("Chiusura del fascicolo " + codicefascicolo + " fallita: " +e);
          }
        }
      } catch (Exception e) {
        errMsgs.add("Chiusura dei fascicoli fallita: " +e);
      } finally {
        if (!errMsgs.isEmpty()) {
          try {
            LogEvento logEvento = new LogEvento();
            logEvento.setCodApplicazione("PG");
            logEvento.setLivEvento(1);
            logEvento.setCodEvento("GA_WSDM_CHIUDI_FASCICOLO_TASK");
            logEvento.setDescr("Chiusura fascicoli mediante task");
            logEvento.setErrmsg(String.join("\n", errMsgs));
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
          }
        }
      }
    }
  }
}



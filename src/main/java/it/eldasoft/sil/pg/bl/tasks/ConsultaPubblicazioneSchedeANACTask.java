package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.QuartzConfigManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.npa.ScambioDatiPCPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Task per la consultazione dell'avvenuta pubblicazione delle schede ANAC
 */
public class ConsultaPubblicazioneSchedeANACTask {

  private final int MINUTI_DELAY_TASK = 20;//TODO
  private static final String JSON_CFUTENTE = "cfUtente";
  private static final String JSON_LOA = "loa";
  private static final String JSON_PROVIDER = "idp";

  static Logger                         logger = Logger.getLogger(ConsultaPubblicazioneSchedeANACTask.class);

  private ScambioDatiPCPManager scambioDatiPCPManager;

  private SqlManager                    sqlManager;

  public void setScambioDatiPCPManager(ScambioDatiPCPManager scambioDatiPCPManager) {
    this.scambioDatiPCPManager = scambioDatiPCPManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void consultaPubblicazioneSchedeANAC() throws GestoreException {
    if (!WebUtilities.isAppNotReady()) {

      Calendar cal = Calendar.getInstance();
      int hour = cal.get(Calendar.HOUR_OF_DAY);
      int minute = cal.get(Calendar.MINUTE);
      double delay = Math.random() * MINUTI_DELAY_TASK;

      if (!UtilityDate.isGiornoLavorativo(cal) || hour > 18 || (hour > 17 && (minute + delay) > 30) || hour < 8 || (hour < 9 && (minute + delay) < 30)) {
        return;
      }

      // Creazione di un TimerTask per eseguire il codice dopo il ritardo
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          //questa porzione di codice mi serve per gestire la w_quartzlock
          String jobClass = StringUtils.substringBefore(ConsultaPubblicazioneSchedeANACTask.class.getName(), "$$");
          String jobMethod = "TimerTask.run";
          QuartzConfigManager manager = null;
          try {
            manager = (QuartzConfigManager) UtilitySpring.getBean("quartzConfigManager", SpringAppContext.getServletContext(),
                    QuartzConfigManager.class);
          }catch (IllegalStateException e){
            //errore dovuto al riavvio della webapp mentre un job è schedulato
            return;
          }
          String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

          boolean locked = false;
          try {
            String server = InetAddress.getLocalHost().getHostName();
            String node = System.getProperty("jvmRoute");
            // tentare inserimento lock esclusivo
            locked = manager.insertQuartzLock(codapp, jobClass, jobMethod, server, node);
            if (!locked) {
              return;
            }
          } catch (UnknownHostException e) {
            logger.error("Impossibile procedere all'esecuzione del task a causa di problemi di reperimento del nome server");
          }

          // INIZIO GESTIONE
          if (logger.isDebugEnabled()) logger.debug("Avvio task consulta pubblicazione schede ANAC");

          int counterElaborati = 0;
          int counterScaduti = 0;
          int counterErrori = 0;
          int livEvento = 1;
          String errMsg = "";

          try {
            List<Vector> schedeDaProcessare = sqlManager.getListVector("select ga.cfrup,ga.provider,ga.loa,ga.expirationdate,ga.expirationtime,gs.idform,gs.idschedanparet,gs.statonpa," +
                    "gn.ispubbnaz,gn.eform, gs.ispubpvl, gs.ispubted from g1npascheda gs, g1npaautoupdate ga, g1npaform gf, g1cf_npa gn " +
                    "where gs.id_autoupdate=ga.id and gs.inviabile='1' and gs.idform=gf.id and gf.idcfnpa=gn.id", null);

            if (schedeDaProcessare != null && !schedeDaProcessare.isEmpty()) {
              for (int i = 0; i < schedeDaProcessare.size(); i++) {
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put(ScambioDatiPCPManager.ID_FORM, SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 5).longValue());
                Timestamp expirationDate = SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 3).dataValue();
                String expirationTime = SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 4).getStringValue();

                java.util.Date dataScadenza = new java.util.Date(expirationDate.getTime());
                String oraScadenza = expirationTime.substring(0, 2);
                String minutiScadenza = expirationTime.substring(3);
                Calendar scadenza = Calendar.getInstance();
                scadenza.setTime(dataScadenza);
                scadenza.add(Calendar.HOUR, Integer.parseInt(oraScadenza));
                scadenza.add(Calendar.MINUTE, Integer.parseInt(minutiScadenza));

                Long statonpa = (Long) SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 7).longValue();
                String isPubbNaz = SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 8).getStringValue();
                String eform = SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 9).getStringValue();
                String isPubPVL = SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 10).getStringValue();
                String isPubTED = SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 11).getStringValue();


                boolean visualizzaRecuperaEstremiPubblicazione = (StringUtils.isNotEmpty(eform) && !"1".equals(isPubTED)) || ("1".equals(isPubbNaz) && !"1".equals(isPubPVL));

                if (cal.compareTo(scadenza) > 0 || (statonpa >= 60 && !visualizzaRecuperaEstremiPubblicazione)) {
                  parameters.put(ScambioDatiPCPManager.AUTOUPDATE, "stop");
                  scambioDatiPCPManager.serviceHandleAutoupdate(parameters);
                  counterScaduti++;
                  continue;
                }


                parameters.put("isTask", "true");
                parameters.put(JSON_CFUTENTE, SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 0).toString());
                parameters.put(JSON_PROVIDER, SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 1).toString());
                parameters.put(JSON_LOA, SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 2).longValue().toString());
                try {
                  Client client = ClientBuilder.newClient();
                  client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
                  parameters.put(ScambioDatiPCPManager.CLIENT, client);
                  JSONObject result = null;
                  if(statonpa == 60)//se la scheda è pubblicata, consulto le pubblicazioni PVL/TED
                    result = scambioDatiPCPManager.serviceConsultaAvviso(parameters);
                  else {//altrimenti verifico l'avvenuta pubblicazione
                    String tipoOperazione = "AV_PUBB";
                    String tipoRicerca = "ULTIMO_ESITO";

                    if (SqlManager.getValueFromVectorParam(schedeDaProcessare.get(i), 6).longValue() != null) {// si tratta di scheda di rettifica
                      tipoOperazione = "AV_RETT";
                      tipoRicerca = "TUTTI_ESITI";
                    }
                    parameters.put(ScambioDatiPCPManager.TIPO_OPERAZIONE, tipoOperazione);
                    parameters.put(ScambioDatiPCPManager.TIPO_RICERCA, tipoRicerca);
                    result = scambioDatiPCPManager.serviceEsitoOperazione(parameters);
                  }
                  client.close();
                  result.put("timestamp", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(System.currentTimeMillis())));
                  scambioDatiPCPManager.updateUltimaResponse(result.toString(), parameters.get(ScambioDatiPCPManager.ID_FORM));
                  counterElaborati++;
                } catch (Exception e) {
                  counterErrori++;
                  logger.error("Errore durante l'esecuzione del task consulta pubblicazione schede ANAC: scheda con idform=" + parameters.get(ScambioDatiPCPManager.ID_FORM) + "\n" + e);
                }
              }
            }
          } catch (Exception ex) {
            errMsg = "Errore durante l'esecuzione del task consulta pubblicazione schede ANAC: " + ex + "\n";
            logger.error(errMsg);
            livEvento = 3;
          } finally {
            try {
              if (counterErrori > 0)
                livEvento = 3;
              LogEvento logEvento = new LogEvento();
              logEvento.setCodApplicazione("PG");
              logEvento.setLivEvento(livEvento);
              logEvento.setCodEvento("NPA_TASK_CONSULTA_PUBBLICAZIONI");
              logEvento.setDescr("Lettura esiti pubblicazione delle schede ANAC");
              logEvento.setErrmsg(errMsg + "Schedulazioni processate: " + counterElaborati + "\nSchedulazioni scadute: " + counterScaduti + "\nSchedulazioni con errori: " + counterErrori);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
            }
          }
          //pulisco la quartzlock se raggiungo questo punto
          manager.deleteQuartzLock(codapp, jobClass, jobMethod);
        }
      }, (long) (delay * 60 * 1000)); // Pianificazione del TimerTask con ritardo in millisecondi

      if (logger.isDebugEnabled())
        logger.debug("Fine task consulta pubblicazione schede ANAC");
    }
  }

}

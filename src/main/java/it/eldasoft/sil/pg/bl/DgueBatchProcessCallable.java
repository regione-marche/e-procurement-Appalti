package it.eldasoft.sil.pg.bl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ws.security.util.Base64;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.sil.pg.bl.utils.EstrazioneContenutoFileFirmatoMarcato;
import it.eldasoft.sil.pg.db.domain.DgueBatch;
import it.eldasoft.sil.pg.db.domain.DgueBatchDoc;
import it.eldasoft.sil.pg.db.domain.DgueBatchStatus;
import it.eldasoft.sil.pg.db.domain.DgueElabSub;
import it.eldasoft.sil.pg.db.domain.DgueElaborazione;
import it.eldasoft.sil.pg.db.domain.DgueElaborazioneStatus;
import it.eldasoft.sil.pg.ws.rest.dgue.MDgueClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * Questa classe permette l'esecuzione in parallelo degli elementi legati alla tabella DGUE_BATCH
 * @author gabriele.nencini
 *
 */
public class DgueBatchProcessCallable implements Callable<String> {
  
  private final Logger logger = Logger.getLogger(DgueBatchProcessCallable.class);
  private final DgueBatch db;
  private final DgueManager dm;
  private final FileAllegatoManager fam;
  private final GenChiaviManager gcm;
  private final Map<Boolean,String> conversionOfEldaBoolean;
  private final Set<String> setOfElaboration;
  private final MDgueClient client;
  
  public DgueBatchProcessCallable(DgueBatch db, DgueManager dm, FileAllegatoManager fam, GenChiaviManager gcm, String infoEndpoint, String statusEndpoint) {
    this.db = db;
    this.dm = dm;
    this.fam = fam;
    this.gcm = gcm;
    client = new MDgueClient(infoEndpoint,statusEndpoint);
    conversionOfEldaBoolean = new HashMap<Boolean,String>();
    conversionOfEldaBoolean.put(Boolean.TRUE, "1");
    conversionOfEldaBoolean.put(Boolean.FALSE, "2");
    setOfElaboration = new TreeSet<String>();
  }

  /**
   * Questo metodo esegue le seguenti operazioni
   * <ul>
   * <li>Si verifica se il servizio M-DGUE sia raggiungibile ed operativo.</li>
   * <li>Si estraggono le occorenze nella <code>DGUE_BATCH_DOC</code></li>
   * <li>Per ciascuna di esse:
   *    <ul>
   *        <li>si estrae il contenuto del documento</li>
   *        <li>si invoca il servizio di estrazione informazioni</li>
   *        <li>Il risultato viene salvato nella DGUE_ELABORAZIONI</li>
   *    </ul>
   *    </li>
   * <li>Si aggiorna il record corrispondente della tabella DITG</li>
   * </ul>
   */
  @Override
  public String call() throws Exception {
    long start = System.currentTimeMillis();
    try {
      if(!client.isActiveService()) {
        logger.debug("Skip elaborazione.");
        return "NON-ESEGUITO";
      }
      
      // 2 prendo il riferimento dalla DGUE_BATCH e vado a prendere tutti record della tabella DGUE_BATCH_DOC
      this.db.setStato(DgueBatchStatus.IN_ELABORAZIONE);
      this.dm.updateDgueBatchStatus(this.db);
      if(logger.isTraceEnabled())
        logger.trace("["+db.getId()+"] -> update status to "+db.getStato().name());
      
      Integer stato = Integer.valueOf(2);
      //tabellato A1013 per la busta
      if(this.db.getBusta().intValue()==1) {
        //busta ammnistrativa
        this.dm.updateDitgStatodgueamm(this.db,stato);
      } else {
        //busta prequalifica
        this.dm.updateDitgStatodguepreq(this.db,stato);
      }
      
      DgueBatchDoc element = null;
      element = new DgueBatchDoc();
      element.setStato(DgueBatchStatus.DA_ELABORARE);
      element.setIdBatch(db);
      List<DgueBatchDoc> listaDoc = this.dm.getListDgueBatchDocByDgueBatchAndStatus(element);
      // 3 per ciascun record della tabella DGUE_BATCH_DOC eseguo le seguenti:
      byte[] doc = null;
      for (DgueBatchDoc dbd : listaDoc) {
        //imposto il dato "in elaborazione"
        dbd.setStato(DgueBatchStatus.IN_ELABORAZIONE);
        dm.updateDgueBatchDocStatus(dbd);
        // 3.1 estraggo il contenuto del file
        BlobFile f = fam.getFileAllegato(dbd.getIdprg(), dbd.getIddocdig());
        if (f == null) {
          logger.debug("File not found with idprg=" + dbd.getIdprg() + " and iddocdig=" + dbd.getIddocdig());
          dbd.setStato(DgueBatchStatus.ELABORATO_ERRORE);
          dm.updateDgueBatchDocStatus(dbd);
          setOfElaboration.add(DgueElaborazioneStatus.ELABORATO_ERRORE.name());
          insertElaborationError(dbd,"File non trovato");
          continue;
        }

        doc = EstrazioneContenutoFileFirmatoMarcato.estraiContenutoFile(f.getStream(), f.getNome());
        // 3.2 invoco il m-dgue-ms
        if(doc!=null) {
          JSONObject resp = client.processDgueInfoRequest(f.getNome(), Base64.encode(doc));
          if(logger.isTraceEnabled())
            logger.trace("Getting response: "+resp);
          
          // 3.3 gestisco la risposta dal ws
          if(resp.isNullObject() || resp.has("error")) {
            logger.debug("Errore dal ws per batch_doc con id: "+dbd.getId());
            dbd.setStato(DgueBatchStatus.ELABORATO_ERRORE);
            dm.updateDgueBatchDocStatus(dbd);
            setOfElaboration.add(DgueElaborazioneStatus.ELABORATO_ERRORE.name());
            insertElaborationError(dbd,resp.getString("error"));
            continue;
          }
          
          JSONObject response = resp.getJSONObject("response");
          if(logger.isTraceEnabled())
            logger.trace("Getting response.response: "+response);
          JSONObject info = response.getJSONObject("info");
          if(logger.isDebugEnabled()) {
            logger.debug("Getting response.info: "+info);
            logger.debug("Getting response.info.isNullObject: "+info.isNullObject());
          }
          if(!info.isEmpty()) {
            // 3.4 elaboro la risposta dal ws
            DgueElaborazione del = new DgueElaborazione();
            del.setDgueBatch(this.db);
            del.setDgueBatchDoc(dbd);
            del.setCodgar(this.db.getCodgar());
            del.setCodimp(this.db.getCodimp());
            del.setDignomdoc(dbd.getDignomdoc());
            boolean esclusione = info.optBoolean("exclusionCriterion");
            del.setEsclusione(conversionOfEldaBoolean.get(esclusione));
            del.setId(Long.valueOf(this.gcm.getNextId(DgueElaborazione.TABELLA)));
            del.setInterno(conversionOfEldaBoolean.get(info.optBoolean("owner")));
            del.setNgara(this.db.getNgara());
            del.setNomeoe(info.getString("economicOperatorName"));
            if(info.get("economicOperatorCode") instanceof  String) //il null del JSON è un JSONObject
              del.setPiva(info.getString("economicOperatorCode"));
            if(info.get("economicOperatorCF") instanceof  String)
              del.setCf(info.getString("economicOperatorCF"));
            boolean isGruppo = info.optBoolean("isGruppo");
            del.setIsgruppo(conversionOfEldaBoolean.get(isGruppo));
            if(isGruppo) {
              if(info.get("rtipartName") instanceof  String)
                del.setComponenti(info.getString("rtipartName"));
              if(info.get("rtigroupName") instanceof  String)
                del.setNomegruppo(info.getString("rtigroupName"));
            }
            boolean isConsorzio = info.optBoolean("isConsorzio");
            del.setIsconsorzio(conversionOfEldaBoolean.get(isConsorzio));
            if(isConsorzio)
              del.setConsorziate(info.getString("setConsorzioInfo"));
            del.setRuolo(info.getString("economicOperatorRole"));
            del.setStato(DgueElaborazioneStatus.ELABORATO);
            if(esclusione) {
              del.setStato(DgueElaborazioneStatus.ELABORATO_WARNING);
              setOfElaboration.add(DgueElaborazioneStatus.ELABORATO_WARNING.name());
            }
            if(logger.isDebugEnabled())
              logger.debug("I will insert "+del);
            dm.insertSingleDgueElaborazione(del);
            if(info.has("otherOe") ) {
              JSONArray arr = info.getJSONArray("otherOe");
              for(int i=0;i<arr.size();i++) {
                JSONObject obj = arr.getJSONObject(i);
                DgueElabSub elabsub= new DgueElabSub();
                elabsub.setId(Long.valueOf(this.gcm.getNextId(DgueElabSub.TABELLA)));
                elabsub.setDgueElaborazione(del);
                elabsub.setDenominazione(obj.getString("name"));
                elabsub.setCf(obj.getString("identifier"));
                elabsub.setRuolo(obj.getLong("role"));
                elabsub.setAttivita(obj.getString("activity"));
                if(elabsub.getRuolo()==1)
                  elabsub.setPrestazione(obj.getString("performance"));
                  try {
                  elabsub.setQuota(obj.getDouble("quote"));
                  }
                  catch(JSONException jex) {
                    logger.debug(jex.getMessage());
                  }
                if(logger.isDebugEnabled())
                  logger.debug("I will insert "+elabsub);
                dm.insertSingleDgueElabSub(elabsub);
              }
            }
            dbd.setStato(DgueBatchStatus.ELABORATO);
          } else {
            dbd.setStato(DgueBatchStatus.ELABORATO_ERRORE);
            setOfElaboration.add(DgueElaborazioneStatus.ELABORATO_ERRORE.name());
            insertElaborationError(dbd,"Il servizio non ha fornito i dati richiesti");
          }
        } else {
          dbd.setStato(DgueBatchStatus.ELABORATO_ERRORE);
          insertElaborationError(dbd,"Il file analizzato è vuoto");
          setOfElaboration.add(DgueElaborazioneStatus.ELABORATO_ERRORE.name());
        }
        dm.updateDgueBatchDocStatus(dbd);
        doc=null; //prevent bugs on processa again a document
      }
      // 4 imposto il dgue_batch in completato
      // 4.1 gestire gli eventuali errori
      this.db.setStato(DgueBatchStatus.ELABORATO);
    } catch (Exception e) {
      logger.error("Errore generico durante la elaborazione.",e);
      setOfElaboration.add(DgueElaborazioneStatus.ELABORATO_ERRORE.name());
      this.db.setStato(DgueBatchStatus.ELABORATO_ERRORE);
    }
    try {
      Integer stato = Integer.valueOf(3);
      if(setOfElaboration.contains(DgueElaborazioneStatus.ELABORATO_ERRORE.name())) {
        stato = Integer.valueOf(4);
      } else if(setOfElaboration.contains(DgueElaborazioneStatus.ELABORATO_WARNING.name())) {
        stato = Integer.valueOf(5);
      }
      if(this.db.getBusta().intValue()==1) {
        //busta ammnistrativa
        this.dm.updateDitgStatodgueamm(this.db,stato);
      } else {
        //busta prequalifica
        this.dm.updateDitgStatodguepreq(this.db,stato);
      }
      
      this.dm.updateDgueBatchStatus(this.db);
      if(logger.isTraceEnabled())
        logger.trace("["+db.getId()+"] -> update status to "+db.getStato().name());
      logger.debug("Execution time: "+(System.currentTimeMillis() - start)+" del batch on id:"+this.db.getId());
    } catch (Exception e) {
      logger.error("Errore nel salvataggio dello stato finale nella ditg.",e);
    }
    return "ESEGUITO";
  }

  /**
   * Inserisce a DB (DGUE_ELEABORAZIONI) una elaborazione con errore per quel BATCH_DOC
   * @param dbd 
   */
  private void insertElaborationError(DgueBatchDoc dbd, String errorMsg) {
    DgueElaborazione del = new DgueElaborazione();
    del.setDgueBatch(this.db);
    del.setDgueBatchDoc(dbd);
    del.setCodgar(this.db.getCodgar());
    del.setCodimp(this.db.getCodimp());
    del.setDignomdoc(dbd.getDignomdoc());
    if(errorMsg!=null && errorMsg.length()>250)
      errorMsg=errorMsg.substring(0,250);
    del.setErrorMsg(errorMsg);
    //del.setEsclusione(conversionOfEldaBoolean.get(Boolean.FALSE));
    del.setId(Long.valueOf(this.gcm.getNextId(DgueElaborazione.TABELLA)));
    del.setInterno(conversionOfEldaBoolean.get(Boolean.FALSE));
    del.setNgara(this.db.getNgara());
    del.setStato(DgueElaborazioneStatus.ELABORATO_ERRORE);
    dm.insertSingleDgueElaborazione(del);
  }

}

package it.eldasoft.sil.pg.bl.tasks;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.sil.pg.bl.DgueBatchProcessCallable;
import it.eldasoft.sil.pg.bl.DgueManager;
import it.eldasoft.sil.pg.db.domain.DgueBatch;
import it.eldasoft.sil.pg.db.domain.DgueBatchStatus;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Questa classe serve ad eseguire il task di processamento dei file DGUE
 * @author gabriele.nencini
 *
 */
public class DgueProcessamentoAsyncTask {
  
  private final Logger logger = Logger.getLogger(DgueProcessamentoAsyncTask.class);
  private DgueManager dgueManager;
  private GenChiaviManager gcm;
  private FileAllegatoManager fam;
  private PropsConfigManager pcm;
  
  /**
   * @param pcm the pcm to set
   */
  public void setPcm(PropsConfigManager pcm) {
    this.pcm = pcm;
  }

  /**
   * @param gcm the gcm to set
   */
  public void setGcm(GenChiaviManager gcm) {
    this.gcm = gcm;
  }

  /**
   * @param fam the fam to set
   */
  public void setFam(FileAllegatoManager fam) {
    this.fam = fam;
  }

  /**
   * @param dgueManager the dgueManager to set
   */
  public void setDgueManager(DgueManager dgueManager) {
    this.dgueManager = dgueManager;
  }
  
  /**
   * Questo metodo verifica la presenza delle properties 
   * <code><ul><li>integrazioneMDgue.url.info</li><li>integrazioneMDgue.url.status</li></ul></code>
   * e dopo di che verifica la resenza di record da elaborare nella tabella DGUE_BATCH.
   * <br>Se presenti lancia un {@link ExecutorService} con una {@link DgueBatchProcessCallable} per ciascun record. 
   */
  public void processDataDgue() {
    long start = System.currentTimeMillis();
    try {
      if(WebUtilities.isAppNotReady()) {
        logger.debug("AppNotReady skip run");
        return;
      }
      logger.debug("Esecuzione inizio");
      String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
      PropsConfig prop = pcm.getProperty(codapp, "integrazioneMDgue.url.info");
      if(prop == null || StringUtils.isBlank(prop.getValore())) {
        logger.trace("Configurazione integrazioneMDgue.url.info mancante.");
        return;
      }
      String infoEndpoint = prop.getValore();
      
      prop = pcm.getProperty(codapp, "integrazioneMDgue.url.status");
      if(prop == null || StringUtils.isBlank(prop.getValore())) {
        logger.trace("Configurazione integrazioneMDgue.url.status mancante.");
        return;
      }
      String statusEndpoint = prop.getValore();
      // 1 estrazione delle richieste da elaborare dalla tabella DGUE_BATCH 
      List<DgueBatch> lista = this.dgueManager.getListDgueBatchByStatus(DgueBatchStatus.DA_ELABORARE);
      if(lista == null) {
        logger.debug("Found null list from table");        
      } else {
        if(lista.size()>0) {
          int maxParallelism = Math.min(Runtime.getRuntime().availableProcessors(), lista.size());
          if(logger.isDebugEnabled())
            logger.debug("Set parallelism max to "+maxParallelism);
          ExecutorService exS = Executors.newFixedThreadPool(maxParallelism);
          for(final DgueBatch db : lista) {
            exS.submit(new DgueBatchProcessCallable(db,dgueManager,fam,gcm,infoEndpoint,statusEndpoint));
          }
          exS.shutdown();
          logger.debug("ShutDown called.");
          logger.debug("Lanciate "+(lista.size())+" richieste DGUE di elaborazione.");
        }
      }
    } catch (Exception e) {
      logger.error("Errore nel task per il processamento dei DGUE.",e);
    } finally {
      if(logger.isDebugEnabled())
        logger.debug("Execution time: "+(System.currentTimeMillis() - start));
    }
  }
  
  
}

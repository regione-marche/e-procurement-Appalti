/*
 * Created on 13/06/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class AcquisisciRegistrazioniInErroreManager {

  static Logger           logger = Logger.getLogger(AcquisisciRegistrazioniPortaleManager.class);

  private SqlManager      sqlManager;

  private FileAllegatoManager fileAllegatoManager;

  private PgManager       pgManager;


  /**
   *
   * @param fileAllegatoDao
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   *
   * @param pgManager
   */
  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }


  /**
   *
   * @throws GestoreException
   */
  public void acquisisciRegistrazioniInErrore() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'integrazione con il Portale Alice (OP114)
    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();
    if(!GeneManager.checkOP(context, "OP114")) return;

    if (logger.isDebugEnabled())
      logger.debug("acquisisciRegistrazioniInErrore: inizio metodo");

    //Definisco i parametri che devono essere adoperati per il metodo registrazione
    Long idcom= null;
    Map messaggio= new HashMap();
    String select="select IDCOM from w_invcom where idprg = ? and comstato = ? and comtipo = ? and comdatastato > ? order by IDCOM";
    
    String idprg="PA";
    String comstato= "7";
    String comtipo="FS1";
    boolean errore = false;

    List listaIDCOM = null;
    try {
      final Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -30);
      java.sql.Date startData = new java.sql.Date(cal.getTimeInMillis());
      
      listaIDCOM = sqlManager.getListVector(select,
            new Object[] { idprg, comstato,comtipo,startData});

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
    }
    if (listaIDCOM != null && listaIDCOM.size() > 0) {
      //Se non è attiva la codifica automatica per IMPR e TEIM non si può procedere
      if(!this.pgManager.getCodificaAutomaticaPerPortaleAttiva()){
        logger.error("Errore durante l'acquisizione della richiesta di registrazione da portale Appalti: " + this.pgManager.messaggioCodAutNonPresente);
        return;
      }
      for (int i = 0; i < listaIDCOM.size(); i++) {
        idcom = SqlManager.getValueFromVectorParam(listaIDCOM.get(i), 0).longValue();
        try{
          errore=pgManager.insertRegistrazionePortale(fileAllegatoManager, idcom, messaggio,comstato);
        }catch(GestoreException e){
          logger.error("Errore durante l'acquisizione della richiesta di registrazione da portale Appalti: IDCOM=" + idcom.toString() + ", " + e.getMessage(), e );
          if(messaggio.get("tipo")!= null && "DUPL-BACKOFFICE".equals(messaggio.get("tipo"))){
            logger.error(messaggio.get("valore"));
          }

        }
      }
    }
    if (logger.isDebugEnabled())
      logger.debug("acquisisciRegistrazioniInErrore: fine metodo");

  }



}

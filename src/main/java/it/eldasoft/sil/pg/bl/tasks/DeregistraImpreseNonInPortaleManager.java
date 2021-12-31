/*
 * Created on 30/03/12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class DeregistraImpreseNonInPortaleManager {

  static Logger           logger = Logger.getLogger(DeregistraImpreseNonInPortaleManager.class);

  private SqlManager      sqlManager;



  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   *
   * @throws GestoreException
   */
  public void deregistraImpreseNonInPortale() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'integrazione con il Portale Alice (OP114)
    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();
    if(!GeneManager.checkOP(context, CostantiGenerali.OPZIONE_GESTIONE_PORTALE)) return;

    if (logger.isDebugEnabled())
      logger.debug("deregistraImpreseNonInPortale: inizio metodo");

    //Definisco i parametri che devono essere adoperati per il metodo registrazione
    String select="select usernome, userkey1, iduser from w_puser where userent='IMPR'";

    List listaW_PUSER = null;
    String usernome=null;
    String userkey1 = null;
    Long iduser = null;
    boolean esisteImpresa=true;

    try {
      listaW_PUSER = sqlManager.getListVector(select, null);

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella W_PUSER ", null, e);
    }
    if (listaW_PUSER != null && listaW_PUSER.size() > 0) {
      PortaleAliceProxy proxy = new PortaleAliceProxy();
      //indirizzo del servizio letto da properties
      String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
      proxy.setEndpoint(endPoint);

      for (int i = 0; i < listaW_PUSER.size(); i++) {
        usernome = SqlManager.getValueFromVectorParam(listaW_PUSER.get(i), 0).stringValue();
        userkey1 = SqlManager.getValueFromVectorParam(listaW_PUSER.get(i), 1).stringValue();
        iduser = SqlManager.getValueFromVectorParam(listaW_PUSER.get(i), 2).longValue();

        try {
          esisteImpresa = proxy.esisteImpresa(usernome);
        } catch (RemoteException e) {
          logger.error("Errore durante la deregistrazione delle imprese non presenti nel portale: USERNOME=" + usernome + ", " +  e.getMessage(),e );
        }
        if(!esisteImpresa){
          try {
            sqlManager.update("delete from w_puser where iduser = ? ", new Object[]{iduser});
            sqlManager.update("update impr set DAESTERN=null, DINVREG=null, DELAREG=null where codimp = ? ", new Object[]{userkey1});

          } catch (SQLException e1) {
            logger.error("Errore durante la deregistrazione delle imprese non presenti nel portale: IDUSER=" + iduser.toString() + ", "  + e1.getMessage(),e1 );
          }
        }

      }
    }



    if (logger.isDebugEnabled())
      logger.debug("deregistraImpreseNonInPortale: fine metodo");

  }



}

/*
 * Created on 16/04/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class AggiornaAdempimentiNonValidiManager {

  static Logger           logger = Logger.getLogger(AggiornaAdempimentiNonValidiManager.class);

  private SqlManager      sqlManager;

  private PgManager pgManager;

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * Set PgManager
   *
   * @param pgManager
   */
  public void setPgManager(PgManager pgManager) {
      this.pgManager = pgManager;
  }

  /**
   *Si effettua il controllo di validità degli adempimenti e nel caso di non validità
   *si popola il campo anticorlotti.testolog se questo non è valorizzato
   *
   */
  public void controlloLottiAdempimentiNonAbilitati() throws GestoreException{

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato
    if (WebUtilities.isAppNotReady()) return;

    if (logger.isDebugEnabled())
      logger.debug("controlloLottiAdempimentiNonAbilitati: inizio metodo");

    Long id= null;
    String ufficioIntestatario = null;
    String select = "select lotti.id,a.codein from anticorlotti lotti, anticor a  where lotti.idanticor=a.id and lotti.inviabile=? and lotti.testolog is null order by lotti.id";
    List listaId = null;
    try {
      listaId = sqlManager.getListVector(select,new Object[]{"2"} );

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella Anticorlotti ", null, e);
    }
    if (listaId != null && listaId.size() > 0) {
      try {
        for (int i = 0; i < listaId.size(); i++) {
          id = SqlManager.getValueFromVectorParam(listaId.get(i), 0).longValue();
          ufficioIntestatario = SqlManager.getValueFromVectorParam(listaId.get(i), 1).getStringValue();
          HashMap esitoControlli = null;
          String msg = null;

            //Poichè gli adempimenti sono filtrati in base al codice
            esitoControlli = this.pgManager.controlloDatiAVCP(id,true, false,ufficioIntestatario);
            if(esitoControlli!=null){
              Boolean controlloOk = (Boolean)esitoControlli.get("esito");
              if(!controlloOk.booleanValue()){
                msg = (String)esitoControlli.get("msg");
                this.sqlManager.update("update anticorlotti set testolog=? where id=?", new Object[]{msg, id});
              }
            }
        }
      } catch (GestoreException e) {
        logger.error("Errore nell'esecuzione dei controlli di validità dei dati del lotto con id= " + id.toString(), e);
      } catch (SQLException e) {
        logger.error("Errore nell'esecuzione dei controlli di validità dei dati del lotto con id= " + id.toString(), e);
      }
    }
    if (logger.isDebugEnabled())
      logger.debug("controlloLottiAdempimentiNonAbilitati: fine metodo");
  }

}



/*
 * Created on 16/12/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

public class InitNuovaComunicazioneManager {

  static Logger           logger = Logger.getLogger(InitNuovaComunicazioneManager.class);

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
  public List getListaModelliComunicazioni(Long genere) throws GestoreException {


    //Definisco i parametri che devono essere adoperati per il metodo registrazione
    String select="select numpro,modtit,moddesc from w_confcom where genere = 1 or genere =? order by numord";
    if(genere.longValue()==10 || genere.longValue()==20 || genere.longValue() ==4 || genere.longValue() == 5 || genere.longValue() == 6 || genere.longValue() == 7 || genere.longValue() == 51 || genere.longValue() == 11)
      select="select numpro,modtit,moddesc from w_confcom where genere =? order by numord";

    List listaW_CONFCOM = null;
    try {
      listaW_CONFCOM = sqlManager.getListVector(select,
            new Object[] {genere });

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella W_CONFCOM ", null, e);
    }

    if (logger.isDebugEnabled())
      logger.debug("getListaModelliComunicazioni: fine metodo");

    return listaW_CONFCOM;

  }



}

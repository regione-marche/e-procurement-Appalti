/*
 * Created on 26/05/2020
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.NsoOrdiniManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

public class GestoreVariazioneOrdineNso extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "NSO_ORDINI";
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
    this.setStopProcess(true);

    NsoOrdiniManager nsoOrdiniManager = (NsoOrdiniManager) UtilitySpring.getBean("nsoOrdiniManager", this.getServletContext(), NsoOrdiniManager.class);

      // Esecuzione della copia dell'ordine
      try {
        nsoOrdiniManager.variazioneOrdine(status, impl.getLong("ORIGINE"), this.getRequest());

      } catch (GestoreException e) {
        this.getRequest().setAttribute("RISULTATO", "KO");
        throw e;
      }
      try {
        this.getSqlManager().commitTransaction(status);
      } catch (SQLException e) {
        throw new GestoreException("Errore durante il commit", "variazioneOrdineNso", e);
      }

    this.getRequest().setAttribute("RISULTATO", "OK");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

}
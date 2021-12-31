/*
 * Created on 26/mar/2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore dell'entita' CATAPP presente nella pagina dell'appalto
 *
 * @author Luca.Giacomazzo
 */
public class GestoreCATG extends AbstractGestoreChiaveNumerica {

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "NGARA" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "NCATG";
  }

  @Override
  public String getEntita() {
    return "CATG";
  }

  /**
   * Funzione che gestisce le operazioni di update, insert, delete il dettaglio
   * della "Categoria prevalente" della gara a lotto unico e del
   *
   * @param request
   * @param status
   * @param impl
   * @throws GestoreException
   */
  public static void gestisciEntitaDaGare(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer impl,
      DataColumn valoreChiave) throws GestoreException {
    // Gestione della categoria prevalente solo se esiste la colonna con la
    // categoria d'iscrizione
    if (impl.isColumn("CATIGA")) {
      // Creo il gestore dell'entita' CATG
      AbstractGestoreEntita gestore = new GestoreCATG();
      gestore.setRequest(request);
      // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita,
      // invece
      // di creare la classe GestoreCATG come apposito gestore di entita',
      // perche'
      // essa non avrebbe avuto alcuna logica di business

      DataColumnContainer newImpl = new DataColumnContainer(impl.getColumns(
          "CATG", 0));
      newImpl.removeColumns(new String[] { "CATG.NUMCLA_CAT_PRE_LAVORI", "CATG.NUMCLA_CAT_PRE_FORNITURE", "CATG.NUMCLA_CAT_PRE_SERVIZI", "CATG.NUMCLA_CAT_PRE_LAVORI150","CATG.NUMCLA_CAT_PRE_SERVIZIPROFESSIONALI" });
      if (newImpl.getColumn("CATG.NGARA").getValue().getValue() == null)
        newImpl.getColumn("CATG.NGARA").setValue(
            valoreChiave.getValue());
      // Se il campo CATG.NCATG e' null, allora bisogna effettuare l'insert
      if (newImpl.getLong("CATG.NCATG") == null) {
        gestore.inserisci(status, newImpl);
      } else
        gestore.update(status, newImpl);
    }
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}
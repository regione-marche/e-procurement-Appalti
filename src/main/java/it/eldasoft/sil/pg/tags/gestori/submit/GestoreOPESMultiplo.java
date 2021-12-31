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
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore delle occorrenze dell'entita OPES presenti piu' volte nella pagina
 * categorie-gara.jsp la quale contiene le categorie ulteriori d'iscrizione
 * delle imprese nell'appalto
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Luca.Giacomazzo
 */
public class GestoreOPESMultiplo extends AbstractGestoreEntita {

  /**
   * @param entita
   * @param campoNumerico
   * @param altreChiavi
   */
  public GestoreOPESMultiplo() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "OPES";
  }

  /**
   * Funzione che gestisce le operazioni di update, insert, delete dei dettagli
   * della "Ulteriore Categoria <n>" dell'appalto
   *
   * @param request
   * @param status
   * @param dataColumnContainer
   * @throws GestoreException
   */
  public static void gestisciEntitaDaGare(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      DataColumn valoreChiave) throws GestoreException {
    // Gestione delle ulteriori categorie solo se esiste la colonna con la
    // NUMERO_CATEGORIE
    if (dataColumnContainer.isColumn("NUMERO_CATEGORIE")) {

      // Creo il gestore dell'entita' ULTAPP
      DefaultGestoreEntitaChiaveNumerica gestoreOPES = new DefaultGestoreEntitaChiaveNumerica(
          "OPES", "NOPEGA", new String[] { "NGARA3" }, request);
      // Osservazione: si e' deciso di usare la classe
      // DefaultGestoreEntitaChiaveNumerica,
      // invece di creare la classe GestoreOPES come apposito gestore di
      // entita', perche' essa non avrebbe avuto alcuna logica di business

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entita' OPES
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          dataColumnContainer.getColumns("OPES", 0));

      int numeroCategorieUlteriori = dataColumnContainer.getLong(
          "NUMERO_CATEGORIE").intValue();

      for (int i = 1; i <= numeroCategorieUlteriori; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn("DEL_ULTERIORE_CATEGORIA")
          && "1".equals(newDataColumnContainer.getString("DEL_ULTERIORE_CATEGORIA"));

        boolean updateOccorrenza = newDataColumnContainer.isColumn("MOD_ULTERIORE_CATEGORIA")
        	&& ("1".equals(newDataColumnContainer.getString("MOD_ULTERIORE_CATEGORIA"))
        			|| "2".equals(newDataColumnContainer.getString("MOD_ULTERIORE_CATEGORIA")));

        // L'i-esima sezione dinamica delle categorie ulteriori risulta essere
        // modifica (e quindi da salvare) anche quando in fase di creazione di
        // una nuova gara, si inizializza la gara stessa prelevando dati da un
        // appalto. In questo caso, infatti, il campo MOD_ULTERIORE_CATEGORIA
        // viene inizializzato con il valore '2'

        // Rimozione dei campi fittizii 'OPES.DEL_ULTERIORE_CATEGORIA',
        // 'OPES.MOD_ULTERIORE_CATEGORIA', 'NUMCLU_CAT_PRE_LAVORI',
        // 'NUMCLU_CAT_PRE_FORNITURE', 'NUMCLU_CAT_PRE_SERVIZI'
        newDataColumnContainer.removeColumns(new String[]{
            "OPES.DEL_ULTERIORE_CATEGORIA", "OPES.MOD_ULTERIORE_CATEGORIA",
            "OPES.NUMCLU_CAT_PRE_LAVORI", "OPES.NUMCLU_CAT_PRE_FORNITURE",
            "OPES.NUMCLU_CAT_PRE_SERVIZI", "OPES.NUMCLU_CAT_PRE_LAVORI150",
            "OPES.NUMCLU_CAT_PRE_SERVIZIPROFESSIONALI", "OPES.PERCEN_CATEG"});

        if (deleteOccorrenza) {
          // Se è stata eliminata e il campo NOPEGA e' diverso da null
          // eseguo l'effettiva eliminazione del record
          if (newDataColumnContainer.getLong("NOPEGA") != null)
            gestoreOPES.elimina(status, newDataColumnContainer);
          // altrimenti e' stato eliminato una nuova categoria ulteriore
        } else {
          if (updateOccorrenza) {
            if (newDataColumnContainer.getColumn("NGARA3").getValue().getValue() == null)
              newDataColumnContainer.getColumn("NGARA3").setValue(
                  valoreChiave.getValue());
            if (newDataColumnContainer.getLong("NOPEGA") == null)
              gestoreOPES.inserisci(status, newDataColumnContainer);
            else
              gestoreOPES.update(status, newDataColumnContainer);
          }
        }
      }
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

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

}
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
package it.eldasoft.sil.pl.struts.gestori;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore delle occorrenze dell'entita ULTAPP presenti piu' volte nella pagina
 * categorie-appalto.jsp la quale contiene le categorie ulteriori d'iscrizione
 * delle imprese nell'appalto
 * 
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 * 
 * @author Luca.Giacomazzo
 */
public class GestoreULTAPPMultiplo extends AbstractGestoreEntita {

  /**
   * @param entita
   * @param campoNumerico
   * @param altreChiavi
   */
  public GestoreULTAPPMultiplo() {
    super(false);
  }

  public String getEntita() {
    return "ULTAPP";
  }

  /**
   * Funzione che gestisce le operazioni di update, insert, delete dei dettagli
   * della "Ulteriore Categoria <n>" dell'appalto
   * 
   * @param request
   * @param status
   * @param impl
   * @throws GestoreException
   */
  public static void gestisciEntitaDaAppa(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    // Gestione delle ulteriori categorie solo se esiste la colonna con la
    // NUMERO_CATEGORIE
    if (impl.isColumn("NUMERO_CATEGORIE")) {

      // Creo il gestore dell'entita' ULTAPP
      DefaultGestoreEntitaChiaveNumerica gestoreULTAPP = new DefaultGestoreEntitaChiaveNumerica(
          "ULTAPP", "NOPEGA", new String[] { "CODLAV", "NAPPAL" }, request);
      // Osservazione: si e' deciso di usare la classe
      // DefaultGestoreEntitaChiaveNumerica,
      // invece di creare la classe GestoreULTAPP come apposito gestore di
      // entita',
      // perche' essa non avrebbe avuto alcuna logica di business

      // Colonne da gestire a meno dei campi chiave: "CODLAV", "NAPPAL",
      // "NOPEGA"
      String colonne[] = { "ULTAPP.CATOFF", "ULTAPP.IMPAPO_", "ULTAPP.NUMCLU",
          "ULTAPP.ISCOFF", "ULTAPP.ACONTEC", "ULTAPP.QUAOBB", "ULTAPP.INCMAN",
          "ULTAPP.DESCOP","ULTAPP.IMPNCO_","ULTAPP.PSUBTO2_","ULTAPP.ISUBTO2_" };
      // Campi non gestiti: "IMPNCO", "ISUBTO", "PSUBTO"

      int numeroCategorieUlteriori = impl.getLong("NUMERO_CATEGORIE").intValue();

      for (int i = 1; i <= numeroCategorieUlteriori; i++) {
        DataColumnContainer newImpl = new DataColumnContainer("");
        newImpl.addColumn("ULTAPP.CODLAV", impl.getColumn("CODLAV" + i));
        //newImpl.addColumn("ULTAPP.NAPPAL", impl.getColumn("NAPPAL" + i));
        newImpl.addColumn("ULTAPP.NAPPAL", impl.getColumn("APPA.NAPPAL"));
        newImpl.getColumn("ULTAPP.CODLAV").setChiave(true);
        newImpl.getColumn("ULTAPP.NAPPAL").setChiave(true);

        if (impl.isColumn("ULTAPP.DEL_ULTERIORE_CATEGORIA_" + i)
            && "1".equals(impl.getString("ULTAPP.DEL_ULTERIORE_CATEGORIA_" + i))) {
          // Se è stata eliminata e il campo NOPEGA e' diverso da null
          // eseguo l'effettiva eliminazione del record
          if (impl.getLong("NOPEGA" + i) != null) {
            newImpl.addColumn("ULTAPP.NOPEGA", impl.getColumn("NOPEGA" + i));
            gestoreULTAPP.elimina(status, newImpl);
          } // altrimenti e' stata eliminata una nuova categoria ulteriore
        } else {
          // Aggiungo tutte le colonne
          String nomeColonna = null;
          for (int k = 0; k < colonne.length; k++) {
            nomeColonna = colonne[k];
            // si toglie l'underscore alla fine se è presente (cado di IMPAPO,
            // che per compatibilità con GARE è stato inserito nel nome del
            // campo nella JSP)
            if (nomeColonna.endsWith("_"))
              nomeColonna = nomeColonna.substring(0, nomeColonna.length() - 1);
            newImpl.addColumn(nomeColonna, impl.getColumn(colonne[k] + i));
          }

          if(impl.isColumn("ULTAPP.MOD_ULTERIORE_CATEGORIA_" + i)
              && "1".equals(impl.getString("ULTAPP.MOD_ULTERIORE_CATEGORIA_" + i))){
            if (impl.getLong("NOPEGA" + i) == null){
              // Nel caso di inserimento di un nuovo appalto (successivo al
              // primo) il campo NAPPAL<i> non è valorizzato, in quanto viene
              // valorizzato nel momento dell'inserimento dell'appalto stesso: 
              // questo comporta la valorizzazione di tale campo prima di
              // inserire l'occorrenza in ULTAPP
              if(newImpl.getLong("ULTAPP.NAPPAL") == null) {
                newImpl.setValue("ULTAPP.NAPPAL", impl.getLong("NAPPAL"));
              }
              gestoreULTAPP.inserisci(status, newImpl);
            } else {
              newImpl.addColumn("ULTAPP.NOPEGA", impl.getColumn("NOPEGA" + i));
              newImpl.getColumn("ULTAPP.NOPEGA").setChiave(true);
              gestoreULTAPP.update(status, newImpl);
            }
          }
        }
      }
    }
  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

}
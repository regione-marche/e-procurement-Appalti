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
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore dell'entita' CATAPP presente nella pagina dell'appalto 
 * 
 * @author Luca.Giacomazzo
 */
public class GestoreCATAPP extends AbstractGestoreChiaveNumerica {

  public String[] getAltriCampiChiave() {
    return new String[]{"NAPPAL", "CODLAV"};
  }

  public String getCampoNumericoChiave() {
    return "NCATG";
  }

  public String getEntita() {
    return "CATAPP";
  }

  /**
   * Funzione che gestisce le operazioni di update, insert, delete il dettaglio
   * della "Categoria prevalente" dell'appalto
   * 
   * @param request
   * @param status
   * @param impl
   * @throws GestoreException
   */
  public static void gestisciEntitaDaAppa(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer impl)
    throws GestoreException {
    // Gestione della categoria prevalente solo se esiste la colonna con la 
    // categoria d'iscrizione
    if (impl.isColumn("CATIGA")) {
      // Creo il gestore dell'entita' CATAPP
      AbstractGestoreEntita gestore = new DefaultGestoreEntita("CATAPP", request);
      // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita, invece
      // di creare la classe GestoreCATAPP come apposito gestore di entita', perche'
      // essa non avrebbe avuto alcuna logica di business
      
      // Colonne da gestire a meno dei campi chiave: "CODLAV", "NAPPAL", "NCATG"
      String colonne[] = {"CATIGA", "IMPBASG", "NUMCLA", "INCMAN", "IMPIGA","IMPNCO","PSUBTO2","ISUBTO2"}; 

      
      DataColumnContainer newImpl = new DataColumnContainer("");
      newImpl.addColumn("CATAPP.CODLAV", impl.getColumn("CODLAV"));
      newImpl.addColumn("CATAPP.NAPPAL", impl.getColumn("NAPPAL"));
      newImpl.getColumn("CATAPP.CODLAV").setChiave(true);

      if (impl.isModifiedTable("CATAPP", 2)) {
        // Aggiungo le altre colonne
        for (int k = 0; k < colonne.length; k++)
          newImpl.addColumn("CATAPP." + colonne[k], impl.getColumn(colonne[k]));

        // Se il campo CATAPP.NCATG e' null, allora bisogna effettuare l'insert
        if(impl.getLong("NCATG") == null){
          newImpl.addColumn("CATAPP.NCATG", new Long(1));
          gestore.inserisci(status, newImpl);
        } else {
          newImpl.addColumn("CATAPP.NCATG", impl.getColumn("NCATG"));
          gestore.update(status, newImpl);
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

}
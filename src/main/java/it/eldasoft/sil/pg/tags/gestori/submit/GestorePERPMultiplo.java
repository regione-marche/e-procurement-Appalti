/*
 * Created on 21/nov/2008
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
 * Gestore delle occorrenze dell'entita PERP presenti piu' volte nella pagina
 * perp-interno-scheda-multipla.jsp
 * 
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 * 
 * @author Luca.Giacomazzo
 */
public class GestorePERPMultiplo extends AbstractGestoreEntita {

  public GestorePERPMultiplo() {
    super(false);
  }

  public String getEntita() {
    return "PERP";
  }

  /**
   * Gestisce le operazioni di update, insert, delete dei dettagli delle
   * "Persone presenti alla seduta di gara"
   * 
   * @param request
   * @param status
   * @param dataColumnContainer
   * @throws GestoreException
   */
  public static void gestisciEntitaDaGARSED(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      DataColumn valoreChiave, DataColumn numeroSedutaGara) throws GestoreException {
    // Gestione delle pubblicazioni bando solo se esiste la colonna con la
    // NUMERO_PERSONE
    if (dataColumnContainer.isColumn("NUMERO_PERP")) {

      // Creo il gestore dell'entita' PERP
      DefaultGestoreEntitaChiaveNumerica gestorePERP =
          new DefaultGestoreEntitaChiaveNumerica("PERP", "NUMPER",
              new String[]{"NGARA", "NUMSED"}, request);
      // Osservazione: si e' deciso di usare la classe DefaultGestoreEntitaChiaveNumerica,
      // invece di creare la classe GestorePERP come apposito gestore di
      // entita', perche' essa non avrebbe avuto alcuna logica di business

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entita' PERP
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          dataColumnContainer.getColumns("PERP", 0));

      int numeroPersonePresenti = dataColumnContainer.getLong(
          "NUMERO_PERP").intValue();

      // Prima ciclo per cancellare le occorrenze, dopo per inserire/aggiornare
      // i dati nella PERP. Questa evita di inserire un componente della
      // commissione prima che venga cancellato
      for (int i = 1; i <= numeroPersonePresenti; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza =
          newDataColumnContainer.isColumn("DEL_PERP") &&
          "1".equals(newDataColumnContainer.getString("DEL_PERP"));

        // Rimozione dei campi fittizi 'PERP.DEL_PERP' e 'PERP.MOD_PERP'
        newDataColumnContainer.removeColumns(new String[]{"PERP.DEL_PERP",
            "PERP.MOD_PERP"});

        if(deleteOccorrenza){
          // Se è stata eliminata e il campo NUMPER e' diverso da null
          // eseguo l'effettiva eliminazione del record
          if(newDataColumnContainer.getLong("NUMPER") != null)
            gestorePERP.elimina(status, newDataColumnContainer);
          // altrimenti e' stato eliminato un nuovo componente della commissione
        }
      }
      for(int i = 1; i <= numeroPersonePresenti; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean updateOccorrenza = newDataColumnContainer.isColumn("MOD_PERP")
          && "1".equals(newDataColumnContainer.getString("MOD_PERP"));
        
        // Rimozione dei campi fittizi 'PERP.DEL_PERP' e 'PERP.MOD_PERP'
        newDataColumnContainer.removeColumns(new String[]{"PERP.DEL_PERP",
            "PERP.MOD_PERP"});

        if(updateOccorrenza) {
          newDataColumnContainer.getColumn("NGARA").setValue(
              valoreChiave.getValue());
          if(newDataColumnContainer.getLong("NUMPER") == null){
            newDataColumnContainer.setValue("PERP.NUMSED",
                numeroSedutaGara.getValue().getValue());
            gestorePERP.inserisci(status, newDataColumnContainer);
          } else
            gestorePERP.update(status, newDataColumnContainer);
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
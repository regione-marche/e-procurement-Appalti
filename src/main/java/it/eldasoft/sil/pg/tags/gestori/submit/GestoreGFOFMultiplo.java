/*
 * Created on 19/nov/2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore delle occorrenze dell'entita GFOF presenti piu' volte nella pagina
 * gfof-interno-scheda-multipla.jsp
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Luca.Giacomazzo
 */
public class GestoreGFOFMultiplo extends AbstractGestoreEntita {

  public GestoreGFOFMultiplo() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "GFOF";
  }

  /**
   * Gestisce le operazioni di update, insert, delete dei dettagli dei
   * "Componente commissione <n>" della gara/tornata
   *
   * @param request
   * @param status
   * @param dataColumnContainer
   * @param servletContext
   * @throws GestoreException
   */
  public static void gestisciEntitaDaGARE(HttpServletRequest request,
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      DataColumn valoreChiave,ServletContext  servletContext) throws GestoreException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", servletContext, SqlManager.class);
    GenChiaviManager genchiaviManager = (GenChiaviManager) UtilitySpring.getBean(
        "genChiaviManager", servletContext, GenChiaviManager.class);

    // Gestione delle pubblicazioni bando solo se esiste la colonna con la
    // NUMERO_GFOF
    if (dataColumnContainer.isColumn("NUMERO_GFOF")) {

      String ngara= valoreChiave.getValue().stringValue();

      // Creo il gestore dell'entita' GFOF
      DefaultGestoreEntita gestoreGFOF = new DefaultGestoreEntita("GFOF", request);
      // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita,
      // invece di creare la classe GestoreGFOF come apposito gestore di
      // entita', perche' essa non avrebbe avuto alcuna logica di business

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entita' GFOF
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          dataColumnContainer.getColumns("GFOF", 0));

      int numeroComponentiCommissione = dataColumnContainer.getLong(
          "NUMERO_GFOF").intValue();

      // Prima ciclo per cancellare le occorrenze, dopo per inserire/aggiornare
      // i dati nella GFOF. Questa evita di inserire un componente della
      // commissione prima che venga cancellato
      for (int i = 1; i <= numeroComponentiCommissione; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn("DEL_GFOF")
          && "1".equals(newDataColumnContainer.getString("DEL_GFOF"));
        
        // Rimozione dei campi fittizi 'GFOF.DEL_GFOF', 'GFOF.MOD_GFOF'
        newDataColumnContainer.removeColumns(new String[]{"GFOF.DEL_GFOF", "GFOF.MOD_GFOF"});

        if(deleteOccorrenza){
          Long id = (Long) newDataColumnContainer.getColumn("ID").getValue().getValue();
          String selectValutazioneOepv = "select count(*) from G1CRIVALCOM where idgfof = ?";
          try {
            Long count = (Long) sqlManager.getObject(selectValutazioneOepv, new Object[]{id});
            if(count.intValue()>0){
              throw new GestoreException("Non è consentito cancellare un membro della commissione che ha espresso valutazioni sui criteri OEPV","cancCommValOEPV");
            }
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nella lettura delle valutazioni Oepv", null, e);
          }
          
          // Se è stata eliminata e il campo NGARA2 e' diverso da null
          // eseguo l'effettiva eliminazione del record
          if(newDataColumnContainer.getString("NGARA2") != null)
            gestoreGFOF.elimina(status, newDataColumnContainer);
          // altrimenti e' stato eliminato un nuovo componente della commissione
        }
      }
      for(int i = 1; i <= numeroComponentiCommissione; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn("DEL_GFOF")
          && "1".equals(newDataColumnContainer.getString("DEL_GFOF"));

        boolean updateOccorrenza = newDataColumnContainer.isColumn("MOD_GFOF")
          && "1".equals(newDataColumnContainer.getString("MOD_GFOF"));

        // Rimozione dei campi fittizi 'GFOF.DEL_GFOF', 'GFOF.MOD_GFOF'
        newDataColumnContainer.removeColumns(new String[]{"GFOF.DEL_GFOF", "GFOF.MOD_GFOF"});

        if(! deleteOccorrenza && updateOccorrenza) {
          if(newDataColumnContainer.getColumn("ID").getValue().getValue() == null){
            newDataColumnContainer.getColumn("NGARA2").setValue(
                valoreChiave.getValue());

            Long id = new Long(genchiaviManager.getNextId("GFOF"));
            newDataColumnContainer.setValue("GFOF.ID",id);
            gestoreGFOF.inserisci(status, newDataColumnContainer);
          } else {
            newDataColumnContainer.getColumn("NGARA2").setValue(
                valoreChiave.getValue());
            Long id = (Long) newDataColumnContainer.getColumn("ID").getValue().getValue();
            String selectValutazioneOepv = "select count(*) from G1CRIVALCOM where idgfof = ?";
            try {
              Long count = (Long) sqlManager.getObject(selectValutazioneOepv, new Object[]{id});
              if(count.intValue()>0){
                if(newDataColumnContainer.getColumn("CODFOF").isModified()) {
                  throw new GestoreException("Non è consentito modificare il riferimento all'anagrafica di un membro della commissione che ha espresso valutazioni sui criteri di valutazione","cancCommValOEPV");
                }
                if(newDataColumnContainer.getColumn("ESPGIU").isModified()){
                  throw new GestoreException("Non è consentito modificare il riferimento all'anagrafica di un membro della commissione che ha espresso valutazioni sui criteri di valutazione","cancCommValOEPV");
                }
              }
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nella lettura delle valutazioni Oepv", null, e);
            }
            
            gestoreGFOF.update(status, newDataColumnContainer);
          }
        }
        //Aggiornamento Numero presenze in commissione per il nominativo
        if(dataColumnContainer.isColumn("GFOF.DATAACCETTAZIONE_"+i) && dataColumnContainer.isModifiedColumn("GFOF.DATAACCETTAZIONE_"+i)){
          String origDataAccettazione = dataColumnContainer.getColumn("GFOF.DATAACCETTAZIONE_"+i).getOriginalValue().getStringValue();
          origDataAccettazione = UtilityStringhe.convertiNullInStringaVuota(origDataAccettazione);
          if("".equals(origDataAccettazione) && dataColumnContainer.getColumn("GFOF.DATAACCETTAZIONE_"+i).getValue() != null){
            String codtec = dataColumnContainer.getColumn("GFOF.CODFOF_"+i).getValue().getStringValue();
            String incarico = dataColumnContainer.getColumn("GFOF.INCFOF_"+i).getValue().getStringValue();
            Long ruolo = new Long(incarico);
            String selectNominativo = "select id,idalbo from commnomin where codtec = ? and dataab is not null ";
            try {
              Vector datiNominativo = sqlManager.getVector(selectNominativo, new Object[] { codtec });
              if(datiNominativo !=null && datiNominativo.size()>0){
                Long idnomin = (Long)((JdbcParametro) datiNominativo.get(0)).getValue();
                Long idalbo = (Long)((JdbcParametro) datiNominativo.get(1)).getValue();
                //decremento il numero presenze
                String updateNumeroPresenzeCommissione = "update commruoli set inviti = (inviti - 1)" +
                        " where idalbo = ? and idnomin = ? and ruolo= ? and inviti > 0";
                sqlManager.update(updateNumeroPresenzeCommissione, new Object[] { idalbo, idnomin, ruolo });
              }
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nell' aggiornamento del numero presenze in commissione nella gara " + ngara, null, e);
            }
          }

        }

      }


      //Cancellazione delle righe di GARSEDPRES non associate a GFOF
      String delete="delete from garsedpres where ngara=? and codfof not in (select codfof from gfof where ngara2=?)";
      try {
        sqlManager.update(delete, new Object[] { ngara, ngara });
      } catch (SQLException e) {
        throw new GestoreException(
                "Errore nella cancellazione dei componenti della commissione di gara presenti nelle sedute "
                        + "della gara " + ngara, null, e);
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
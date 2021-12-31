/*
 * Created on 03/08/10
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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard delle occorrenze dell'entita GARALTSOG per la pagina
 * popup-dettaglio-soggetti-aggregati.jsp
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Marcello Caminiti
 */
public class GestorePopupDettaglioSoggettiAggregati extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "GARALTSOG";
  }

  public GestorePopupDettaglioSoggettiAggregati() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupDettaglioSoggettiAggregati(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {


    String numeroGara=this.getRequest().getParameter("ngara");
    String codiceGara=this.getRequest().getParameter("codgar");

    String[] listaSoggettiSelezionati = this.getRequest().getParameterValues("keys");
    int numeroSoggetti = 0;
    String numSoggetti = this.getRequest().getParameter("numeroSoggetti");
    if(numSoggetti != null && numSoggetti.length() > 0)
      numeroSoggetti =  UtilityNumeri.convertiIntero(numSoggetti).intValue();

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    String codein = null;
    int id=0;
    String codiceSoggettoSelezionato = null;
    Long idSoggetto = null;
    boolean soggettoTrovato = false;
    for (int i = 1; i <= numeroSoggetti; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
          impl.getColumnsBySuffix("_" + i, false));
      codein = dataColumnContainerDiRiga.getString("CODEIN_FIT");
      idSoggetto = dataColumnContainerDiRiga.getLong("GARALTSOG.ID");

      soggettoTrovato = false;

      if(listaSoggettiSelezionati!= null && listaSoggettiSelezionati.length > 0) {

        for (int j = 0; j < listaSoggettiSelezionati.length; j++) {
          codiceSoggettoSelezionato = listaSoggettiSelezionati[j];
          if(codiceSoggettoSelezionato.equals(codein)){
            soggettoTrovato = true;
            break;
          }

        }
      }

      if(soggettoTrovato && (idSoggetto==null || "".equals(idSoggetto))){
        //Inserimento in GARALTSOG se id non valorizzato
        try {
          id = genChiaviManager.getNextId("GARALTSOG");
          this.getSqlManager().update(
              "insert into GARALTSOG(id,ngara,cenint) values(?,?,?)",
              new Object[] { new Long(id),numeroGara,codein});
        }catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento dei dati in GARALTSOG",null, e);
        }
      }else if(!soggettoTrovato && idSoggetto!=null){
        //Eliminazione dalla GARALTSOG
        try {
          this.getSqlManager().update(
              "delete from GARALTSOG where id=?",
              new Object[] { new Long(idSoggetto)});
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento dei dati in GARALTSOG",null, e);
        }

      }
    }
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}
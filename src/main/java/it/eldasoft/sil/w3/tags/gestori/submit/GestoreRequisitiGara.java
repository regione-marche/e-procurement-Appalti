/*
 * Created on 30/08/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per i requisiti della gara
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Mirco Franzoni
 */
public class GestoreRequisitiGara extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "W3GARAREQ";
  }

  public GestoreRequisitiGara() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreRequisitiGara(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }



  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

	  Long numgara = impl.getLong("W3GARAREQ.NUMGARA");
	  Long numreq = impl.getLong("W3GARAREQ.NUMREQ");

	  this.getGeneManager().deleteTabelle(new String[] { "W3GARAREQCIG" },
	        "NUMGARA = ? AND NUMREQ = ?", new Object[] { numgara, numreq });

	  this.getGeneManager().deleteTabelle(new String[] { "W3GARAREQDOC" },
	        "NUMGARA = ? AND NUMREQ = ?", new Object[] { numgara, numreq });

	  this.getGeneManager().deleteTabelle(new String[] { "W3GARAREQ" },
		        "NUMGARA = ? AND NUMREQ = ?", new Object[] { numgara, numreq });
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

  	int numeroRequisiti = 0;
    String numRequisiti = this.getRequest().getParameter("numeroRequisiti");
    if (numRequisiti != null && numRequisiti.length() > 0)
    	numeroRequisiti =  UtilityNumeri.convertiIntero(numRequisiti).intValue();

    String esclusione = null;
    String offerta = null;
    String avvalimento = null;
    String bando = null;
    String riservatezza = null;

    for (int i = 1; i <= numeroRequisiti; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
      		impl.getColumnsBySuffix("_" + i, false));

      try {
        if (dataColumnContainerDiRiga.isModifiedTable("W3GARAREQ")) {
        	esclusione = dataColumnContainerDiRiga.getString("W3GARAREQ.FLAG_ESCLUSIONE");
        	offerta = dataColumnContainerDiRiga.getString("W3GARAREQ.FLAG_COMPROVA_OFFERTA");
        	avvalimento = dataColumnContainerDiRiga.getString("W3GARAREQ.FLAG_AVVALIMENTO");
        	bando = dataColumnContainerDiRiga.getString("W3GARAREQ.FLAG_BANDO_TIPO");
        	riservatezza = dataColumnContainerDiRiga.getString("W3GARAREQ.FLAG_RISERVATEZZA");
        	
        	dataColumnContainerDiRiga.setValue("W3GARAREQ.FLAG_ESCLUSIONE", esclusione);
        	dataColumnContainerDiRiga.setValue("W3GARAREQ.FLAG_COMPROVA_OFFERTA", offerta);
        	dataColumnContainerDiRiga.setValue("W3GARAREQ.FLAG_AVVALIMENTO", avvalimento);
        	dataColumnContainerDiRiga.setValue("W3GARAREQ.FLAG_BANDO_TIPO", bando);
        	dataColumnContainerDiRiga.setValue("W3GARAREQ.FLAG_RISERVATEZZA", riservatezza);
        	
            dataColumnContainerDiRiga.update("W3GARAREQ", sqlManager);
        }
      } catch (SQLException e) {
           throw new GestoreException("Errore nell'aggiornamento dei dati in W3GARAREQ",null, e);
      }
    }


  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}
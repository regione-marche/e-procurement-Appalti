/*
 * Created on 30/apr/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.utils.spring.UtilitySpring;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit per il salvataggio dei dati di GARE nella fase 'Inviti'
 * del wizard delle fasi di ricezione offerte. In particolare se la gara e' a
 * lotto unico, si possono salvare dei campi di TORN
 *
 * @author Luca.Giacomazzo
 */
public class GestoreFasiRicezione_Inviti extends GestoreDocumentazioneGara {

	public GestoreFasiRicezione_Inviti() {
		super(true);
	}

	public GestoreFasiRicezione_Inviti(boolean isGestoreStandard) {
		super(isGestoreStandard);
	}

	@Override
  public String getEntita() {
		return "GARE";
	}


	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
		boolean aggiornaFASGAR = false;

		if(datiForm.isModifiedTable("DOCUMGARA"))
		  aggiornaFASGAR = true;

		try{
    		//Gestione della documentazione
    		super.preUpdate(status, datiForm);


    		// Salvataggio dei campi di TORN se modificati (solo per gare a lotto unico)
    		//TODO e per le gare a lotti con offerta unica???
    		if(datiForm.isModifiedTable("TORN")){
    			aggiornaFASGAR = true;
    			DataColumn[] arrayDataColumn = datiForm.getColumns("TORN", 2);
    			if(arrayDataColumn != null && arrayDataColumn.length > 0){
    				//Gestione dell'inserimento di una ditta in una gara
    				DataColumnContainer dataColumnContainerTorn = new DataColumnContainer(arrayDataColumn);

    		    DefaultGestoreEntita gestoreTORN = new DefaultGestoreEntita("TORN",
    		        this.getRequest());
    		    String codiceTornata = datiForm.getString("GARE.CODGAR1");
    		    dataColumnContainerTorn.addColumn("TORN.CODGAR", codiceTornata);
    		    dataColumnContainerTorn.getColumn("TORN.CODGAR").setObjectOriginalValue(codiceTornata);
    		    dataColumnContainerTorn.getColumn("TORN.CODGAR").setChiave(true);
    		    gestoreTORN.update(status, dataColumnContainerTorn);
    			}
    		}

    		if(datiForm.isModifiedTable("GARESTATI")){
    			aggiornaFASGAR = true;

    			DefaultGestoreEntita gestoreGARESTATI = new DefaultGestoreEntita(
    					"GARESTATI", this.getRequest());

    			// Aggiornamento dei campi di GARESTATI presenti nella jsp
    			String codiceTornata = datiForm.getString("GARE.CODGAR1");
    			String numeroGara = datiForm.getString("GARE.NGARA");
    			Long faseGara = new Long(GestioneFasiRicezioneFunction.FASE_ELENCO_DITTE_INVITATE/10);
    			Long numeroFaseAttiva = new Long(GestioneFasiRicezioneFunction.FASE_INVITI);

    			DataColumnContainer dataColumnContainerGARESTATI = new DataColumnContainer(
    					datiForm.getColumns("GARESTATI", 2));
    			dataColumnContainerGARESTATI.addColumn("GARESTATI.CODGAR", codiceTornata);
    			dataColumnContainerGARESTATI.getColumn("GARESTATI.CODGAR").setObjectOriginalValue(codiceTornata);
    			dataColumnContainerGARESTATI.getColumn("GARESTATI.CODGAR").setChiave(true);

    			dataColumnContainerGARESTATI.addColumn("GARESTATI.NGARA", numeroGara);
    			dataColumnContainerGARESTATI.getColumn("GARESTATI.NGARA").setObjectOriginalValue(numeroGara);
    			dataColumnContainerGARESTATI.getColumn("GARESTATI.NGARA").setChiave(true);

    			dataColumnContainerGARESTATI.addColumn("GARESTATI.FASGAR", faseGara);
    			dataColumnContainerGARESTATI.getColumn("GARESTATI.FASGAR").setObjectOriginalValue(faseGara);
    			dataColumnContainerGARESTATI.getColumn("GARESTATI.FASGAR").setChiave(true);

    			dataColumnContainerGARESTATI.addColumn("GARESTATI.STEPGAR", numeroFaseAttiva);
    			dataColumnContainerGARESTATI.getColumn("GARESTATI.STEPGAR").setObjectOriginalValue(numeroFaseAttiva);
    			dataColumnContainerGARESTATI.getColumn("GARESTATI.STEPGAR").setChiave(false);

    			if(this.geneManager.countOccorrenze("GARESTATI", "CODGAR=? and NGARA=? and FASGAR=?",
    					new Object[]{codiceTornata, numeroGara, faseGara}) > 0)
    				gestoreGARESTATI.update(status, dataColumnContainerGARESTATI);
    			else
    				gestoreGARESTATI.inserisci(status, dataColumnContainerGARESTATI);
    		}

    		// Il salvataggio dei campi dell'entita' GARE viene demandato al gestore standard
    		if(! aggiornaFASGAR)
    			aggiornaFASGAR = datiForm.isModifiedTable("GARE", 2);

    		if(aggiornaFASGAR){
    			// Aggiornamento GARE.FASGAR e GARE.STEPGAR, solo se non si è già passati
    	    // alle fasi di gara, ovvero solo se FASGAR.GARE < 2. In questo caso il
    			// FASGAR viene settato a -3
    	    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
    	        this.getServletContext(), PgManager.class);
    	    Long numeroFaseAttiva = new Long(GestioneFasiRicezioneFunction.FASE_INVITI);
    	    String numeroGara = datiForm.getString("GARE.NGARA");
    	    pgManager.aggiornaFaseGara(numeroFaseAttiva, numeroGara, true);
    		}
		}catch(GestoreException e){
		  this.getRequest().setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA,"1");
		  throw e;
		}

	}

	@Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

}
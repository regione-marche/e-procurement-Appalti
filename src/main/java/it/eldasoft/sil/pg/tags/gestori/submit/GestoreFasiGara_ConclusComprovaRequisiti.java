/*
 * Created on 07/mag/10
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
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit per salvare i dati della scheda 'Conclusione comprova
 * requisiti' del wizard fasi di gara
 *
 * @author Luca.Giacomazzo
 */
public class GestoreFasiGara_ConclusComprovaRequisiti extends
		AbstractGestoreEntita {

	public GestoreFasiGara_ConclusComprovaRequisiti() {
		super();
	}

	public GestoreFasiGara_ConclusComprovaRequisiti(boolean isGestoreStandard) {
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

		String codiceTornata = datiForm.getString("GARE.CODGAR1");
        String numeroGara = datiForm.getString("GARE.NGARA");

		if(datiForm.isModifiedTable("GARESTATI")){
			aggiornaFASGAR = true;

			DefaultGestoreEntita gestoreGARESTATI = new DefaultGestoreEntita(
					"GARESTATI", this.getRequest());

			// Aggiornamento dei campi di GARESTATI presenti nella jsp

			Long faseGara = new Long(GestioneFasiGaraFunction.FASE_ESITO_CONTROLLO_SORTEGGIATE/10);
			Long numeroFaseAttiva = new Long(GestioneFasiGaraFunction.FASE_CONCLUSIONE_COMPROVA_REQUISITI);

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
			// Aggiornamento GARE.FASGAR e GARE.STEPGAR
    	    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
    	        this.getServletContext(), PgManager.class);
    	    Long numeroFaseAttiva = new Long(
    	    		GestioneFasiGaraFunction.FASE_CONCLUSIONE_COMPROVA_REQUISITI);

    	    Long bustalotti = null;
    	    try{
    	      bustalotti = (Long) this.sqlManager.getObject("select bustalotti from gare where codgar1=? and ngara=codgar1", new Object[]{codiceTornata});
    	      //Non si deve aggiornare la fase se fasgar>=5 quando bustalotti=1
              if((bustalotti!=null && bustalotti.longValue()==1) || bustalotti == null){
                Long faseGaraAttuale = (Long)this.sqlManager.getObject("select fasgar from gare where ngara=?", new Object[]{numeroGara});
                if(faseGaraAttuale!=null &&  faseGaraAttuale.longValue()>=5)
                  aggiornaFASGAR=false;
              }
    	    }catch (SQLException e) {
    	      throw new GestoreException(
                  "Errore nell'aggiornamento della fase di gare della tabella GARE",
                  "updateGARE_FASGAR", e);
    	    }
            if(aggiornaFASGAR){
              //Nel caso di bustalotti=2 si deve aggiornare la fase della gara con quella del max delle fasi dei lotti, se non nulla
              Long valoreFasgarAggiornamento =  numeroFaseAttiva;
              if(bustalotti!=null){
                valoreFasgarAggiornamento = pgManager.getStepAttivo(numeroFaseAttiva, codiceTornata);
              }
              pgManager.aggiornaFaseGara(valoreFasgarAggiornamento, numeroGara, false);
            }
		}
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

}
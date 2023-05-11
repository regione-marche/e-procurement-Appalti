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
 * Gestore di submit per salvare i dati della scheda 'Chiusura verifica
 * documentazione amministrativa' del wizard fasi di gara
 *
 * @author Luca.Giacomazzo
 */
public class GestoreFasiGara_ChiusuraVerificaDocAmm extends
		AbstractGestoreEntita {

	/**
	 *
	 */
	public GestoreFasiGara_ChiusuraVerificaDocAmm() {
		super();
	}

	/**
	 * @param isGestoreStandard
	 */
	public GestoreFasiGara_ChiusuraVerificaDocAmm(boolean isGestoreStandard) {
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

		Long stepWizard = datiForm.getLong("WIZARD_PAGINA_ATTIVA");
        Long numeroFaseAttiva = null;
        Long faseGara = null;

        if(stepWizard!= null && stepWizard.longValue() == GestioneFasiGaraFunction.FASE_CHIUSURA_VALUTAZIONE_TECNICA){
          faseGara = new Long(GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA/10);
          numeroFaseAttiva = new Long(GestioneFasiGaraFunction.FASE_CHIUSURA_VALUTAZIONE_TECNICA);
        }else{
          faseGara = new Long(GestioneFasiGaraFunction.FASE_SORTEGGIO_CONTROLLO_REQUISITI/10);
          numeroFaseAttiva = new Long(GestioneFasiGaraFunction.FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA);
        }

        String codiceTornata = datiForm.getString("GARE.CODGAR1");
        if(datiForm.isModifiedTable("TORN")){
			aggiornaFASGAR = true;

			DefaultGestoreEntita gestoreTORN = new DefaultGestoreEntita("TORN",
	        this.getRequest());

			// Aggiornamento dei campi di TORN presenti nella jsp
			DataColumnContainer dataColumnContainerTORN = new DataColumnContainer(
					datiForm.getColumns("TORN", 2));
			dataColumnContainerTORN.addColumn("TORN.CODGAR", codiceTornata);
			dataColumnContainerTORN.getColumn("TORN.CODGAR").setObjectOriginalValue(codiceTornata);
			dataColumnContainerTORN.getColumn("TORN.CODGAR").setChiave(true);
			gestoreTORN.update(status, dataColumnContainerTORN);
		}



		if(datiForm.isModifiedTable("GARESTATI")){
			aggiornaFASGAR = true;


			DefaultGestoreEntita gestoreGARESTATI = new DefaultGestoreEntita(
					"GARESTATI", this.getRequest());

			// Aggiornamento dei campi di GARESTATI presenti nella jsp
			String numeroGara = datiForm.getString("GARE.NGARA");

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
    	    try {
        	    String numeroGara = datiForm.getString("GARE.NGARA");
        	    Long bustalotti = (Long) this.sqlManager.getObject("select bustalotti from gare where codgar1=? and ngara=codgar1", new Object[]{codiceTornata});
        	    if(!(stepWizard!= null && stepWizard.longValue() == GestioneFasiGaraFunction.FASE_CHIUSURA_VALUTAZIONE_TECNICA)){
        	      numeroFaseAttiva = new Long(GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR);

        	      //Nel caso di step Chiusura verifica doc. amm. non si deve aggiornare la fase se fasgar>=5 quando bustalotti=1
        	      if(stepWizard!= null && stepWizard.longValue() == GestioneFasiGaraFunction.FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA){
                    if((bustalotti!=null && bustalotti.longValue()==1) || bustalotti == null){
                      String ngara = datiForm.getString("GARE.NGARA");
                      Long faseGaraAttuale = (Long)this.sqlManager.getObject("select fasgar from gare where ngara=?", new Object[]{ngara});
                      if(faseGaraAttuale!=null &&  faseGaraAttuale.longValue()>=5)
                        aggiornaFASGAR=false;
                    }
            	 }

        	      if(aggiornaFASGAR){
                    Long valoreFasgarAggiornamento =  numeroFaseAttiva;
                    if(bustalotti!=null){
                      valoreFasgarAggiornamento = pgManager.getStepAttivo(numeroFaseAttiva, codiceTornata);
                    }
                    pgManager.aggiornaFaseGara(valoreFasgarAggiornamento, numeroGara, false);
                    //Se si aggiorna il FASGAR a 30 o 20, si deve impostare lo stepgar sempre a 35
                    if(valoreFasgarAggiornamento == numeroFaseAttiva){
                      this.sqlManager.update("update GARE set STEPGAR = ? where NGARA = ? ", new Object[] { new Long(35), numeroGara });
                    }
        	      }
        	    }else{
        	      //Nel caso di bustalotti=1 numeroGara contiene il valore di ngara del lotto, quindi
        	      Long valoreFasgarAggiornamento = new Long(numeroFaseAttiva.longValue());
        	      if(bustalotti!=null){
        	        valoreFasgarAggiornamento = pgManager.getStepAttivo(numeroFaseAttiva, codiceTornata);
        	        if(bustalotti.longValue()==1){
        	          pgManager.aggiornaFaseGara(valoreFasgarAggiornamento, codiceTornata, false);
        	          valoreFasgarAggiornamento = numeroFaseAttiva;
        	        }
        	      }
        	      pgManager.aggiornaFaseGara(valoreFasgarAggiornamento, numeroGara, false);

        	    }
    	    } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nell'aggiornamento della fase di gare della tabella GARE",
                  "updateGARE_FASGAR", e);
            }
		}
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

}
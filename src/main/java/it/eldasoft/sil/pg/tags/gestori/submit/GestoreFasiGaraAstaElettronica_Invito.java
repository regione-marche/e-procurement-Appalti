/*
 * Created on 04/10/16
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit per il salvataggio dei dati della pagina "Invito" delle fasi
 * di gara per l'asta elettronica
 *
 * @author M.C.
 */
public class GestoreFasiGaraAstaElettronica_Invito extends GestoreDocumentazioneGara {

	public GestoreFasiGaraAstaElettronica_Invito() {
		super(true);
	}

	public GestoreFasiGaraAstaElettronica_Invito(boolean isGestoreStandard) {
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

	  String ngara = datiForm.getString("GARE.NGARA");
	  try{
    	//Gestione della documentazione
		super.preUpdate(status, datiForm);

	    // Aggiornamento GARE1
		if(datiForm.isModifiedTable("GARE1")){
		  String aenproti = datiForm.getString("GARE1.AENPROTI");
		  this.sqlManager.update("update gare1 set aenproti=? where ngara=?", new Object[]{aenproti,ngara});
    	}

		//Aggiornamento AEFASI
		AbstractGestoreChiaveIDAutoincrementante gestoreAEFASI = new DefaultGestoreEntitaChiaveIDAutoincrementante("AEFASI", "ID",this.getRequest());
		this.gestisciAggiornamentiRecordSchedaMultiplaFasi(status, datiForm,gestoreAEFASI, "AEFASI");
		List listaId = this.sqlManager.getListVector("select id from aefasi where ngara=? order by dataoraini", new Object[]{ngara});
		if(listaId!=null && listaId.size()>0){
		  Long id=null;
		  for(int i=0;i<listaId.size();i++){
		    id= SqlManager.getValueFromVectorParam(listaId.get(i), 0).longValue();
		    this.sqlManager.update("update aefasi set numfase=? where id=?", new Object[]{new Long(i+1), id});
		  }
		}
		this.sqlManager.update("update gare set stepgar=? where ngara=?", new Object[]{new Long(GestioneFasiGaraFunction.FASE_ASTA_ELETTRONICA),ngara});

	  } catch (SQLException e) {
	   throw new GestoreException(
           "Errore nell'aggiornamento dei dati della pagina 'Invito' dello step 'Asta elettronica' delle fasi di gara per la gara "
               + ngara , null, e);
      }

	}

	@Override
    public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}


  private void gestisciAggiornamentiRecordSchedaMultiplaFasi(
	      TransactionStatus status, DataColumnContainer dataColumnContainer,
	      AbstractGestoreChiaveIDAutoincrementante gestore, String suffisso)
	      throws GestoreException {

	    ////////////////////////////////////////////////////////////////
	    // ATTENZIONE: METODO CON UTILIZZO ID MAX + 1 (TRADIZIONALE)!!!!
	    ////////////////////////////////////////////////////////////////

	    String nomeCampoNumeroRecord = "NUMERO_" + suffisso;
	    String nomeCampoDelete = "DEL_" + suffisso;
	    String nomeCampoMod = "MOD_" + suffisso;

	    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
	    // numero di occorrenze
	    if (dataColumnContainer.isColumn(nomeCampoNumeroRecord)) {

	      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
	      // dell'entità definita per il gestore
	      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
	          dataColumnContainer.getColumns(gestore.getEntita(), 0));

	      int numeroRecord = dataColumnContainer.getLong(nomeCampoNumeroRecord).intValue();

	      for (int i = 1; i <= numeroRecord; i++) {
	        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
	            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

	        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
	            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
	        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
	            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

	        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
	        // tutti gli eventuali campi passati come argomento)
	        newDataColumnContainer.removeColumns(new String[] {
	            gestore.getEntita() + "." + nomeCampoDelete,
	            gestore.getEntita() + "." + nomeCampoMod});

	        if (deleteOccorrenza) {
	          // Se è stata richiesta l'eliminazione e il campo chiave numerica e'
	          // diverso da null eseguo l'effettiva eliminazione del record
	          if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null)
	            gestore.elimina(status, newDataColumnContainer);
	          // altrimenti e' stato eliminato un nuovo record non ancora inserito
	          // ma predisposto nel form per l'inserimento
	        } else {
	          if (updateOccorrenza) {
	            //Valorizzazione dei campi DATAORAINI e DATAORAFINE
	            String dataOraIniString = newDataColumnContainer.getString("AEFASI.DATAORAINI_FIT");
	            String dataOraFineString = newDataColumnContainer.getString("AEFASI.DATAORAFINE_FIT");
	            Date data = UtilityDate.convertiData(dataOraIniString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
	            newDataColumnContainer.setValue("AEFASI.DATAORAINI", data);
	            data = UtilityDate.convertiData(dataOraFineString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                newDataColumnContainer.setValue("AEFASI.DATAORAFINE", data);
                newDataColumnContainer.removeColumns(new String[] {"AEFASI.DATAORAINI_FIT","AEFASI.DATAORAFINE_FIT"});
                if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) == null)
	              gestore.inserisci(status, newDataColumnContainer);
	            else
	              gestore.update(status, newDataColumnContainer);
	          }
	        }
	      }
	    }
	  }

}
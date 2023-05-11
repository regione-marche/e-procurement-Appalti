package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore che si occupa di inserire, aggiornare e cancellare una lista di ruoli 
 * relativi al nominativo per l'albo nominativi di commissione
 * 
 * @author roberto.marcon
 * 
 */
public class GestoreCOMMNOMIN extends AbstractGestoreChiaveIDAutoincrementante {

    @Override
    public String getCampoNumericoChiave() {
	return "ID";
    }

    @Override
    public String getEntita() {
	return "COMMNOMIN";
    }

    @Override
    public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
	Long id = datiForm.getLong("COMMNOMIN.ID");
	int numeroRecord = datiForm.getLong("NUMERO_COMMR").intValue();

	for (int i = 1; i <= numeroRecord; i++) {
	    if (datiForm.isColumn("COMMRUOLI.MOD_COMMR_" + i) && "1".equals(datiForm.getString("COMMRUOLI.MOD_COMMR_" + i))) {
		datiForm.addColumn("COMMRUOLI.IDNOMIN_" + i, JdbcParametro.TIPO_NUMERICO, id);
	    }
	}

	// Gestione delle sezioni 'Atti autorizzativi'
	AbstractGestoreChiaveIDAutoincrementante gestoreCOMMRUOLI = new DefaultGestoreEntitaChiaveIDAutoincrementante("COMMRUOLI", "ID",
		this.getRequest());
	this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm, gestoreCOMMRUOLI, "COMMR",
		new DataColumn[] { datiForm.getColumn("COMMNOMIN.ID") }, null);
    }

    @Override
    public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    }

    @Override
    public void postDelete(DataColumnContainer datiForm) throws GestoreException {

    }

    @Override
    public void postInsert(DataColumnContainer datiForm) throws GestoreException {

    }

    @Override
    public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
	// Gestione delle sezioni 'Atti autorizzativi'
	AbstractGestoreChiaveIDAutoincrementante gestoreCOMMRUOLI = new DefaultGestoreEntitaChiaveIDAutoincrementante("COMMRUOLI", "ID",
		this.getRequest());
	this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm, gestoreCOMMRUOLI, "COMMR",
		new DataColumn[] { datiForm.getColumn("COMMNOMIN.ID") }, null);
    }

    @Override
    public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    }

}

/*
 * Created on 29-07-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

/**
 * Gestore per archiviazione di una gara per albo fornitori
 *
 * (questa classe e' la copia della classe GestorePopupArchiviaGara)
 * 
 * @author Luca.Giacomazzo
 */
public class GestorePopupArchiviaAlboFornitore extends AbstractGestoreEntita {

	static Logger      logger     = Logger.getLogger(GestorePopupArchiviaAlboFornitore.class);

	public String getEntita() {
		return "GAREALBO";
	}

	public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	public void postDelete(DataColumnContainer datiForm) throws GestoreException {
	}

	public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	public void postInsert(DataColumnContainer datiForm) throws GestoreException {
	}

	public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
		this.getRequest().setAttribute("RISULTATO", "ERRORE");
	}

	public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
		this.getRequest().setAttribute("RISULTATO", "OPERAZIONEESEGUITA");
	}

}
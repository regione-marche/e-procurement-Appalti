/*
 * Created on 17/giu/09
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

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreGAREFasiGara extends AbstractGestoreEntita {

	/** Logger */
	static Logger            logger           = Logger.getLogger(GestoreGAREFasiGara.class);


	@Override
  public String getEntita() {
		return "GARE";
	}

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {


	  AbstractGestoreEntita gestoreDITG = new DefaultGestoreEntita("DITG", this.getRequest());

	    gestoreDITG.update(status, datiForm);
	    if(datiForm.isColumn("GARE1.NOTPROV")){
	        AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
	        gestoreGARE1.update(status, datiForm);
	    }


	}

}

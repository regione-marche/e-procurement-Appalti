/*
 * Created on 28/11/2014
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
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.springframework.transaction.TransactionStatus;

public class GestoreW3LOTTTIPI extends AbstractGestoreChiaveNumerica {

	@Override
  public String[] getAltriCampiChiave() {
		return new String[] { "NUMGARA", "NUMLOTT" };
	}

	@Override
  public String getCampoNumericoChiave() {
		return "NUMTIPI";
	}

	@Override
  public String getEntita() {
		return "W3LOTTTIPI";
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
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}

}

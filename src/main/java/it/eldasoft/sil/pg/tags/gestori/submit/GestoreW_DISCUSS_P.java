/*
 * Created on 29/03/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.Timestamp;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit dell'entita' W_DISCUSS
 * 
 */
public class GestoreW_DISCUSS_P extends AbstractGestoreChiaveNumerica {

  public String getCampoNumericoChiave() {
    return "DISCID_P";
  }

  public String[] getAltriCampiChiave() {
    return null;
  }

  public String getEntita() {
    return "W_DISCUSS_P";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    Long discid_p = datiForm.getLong("W_DISCUSS_P.DISCID_P");

    this.getGeneManager().deleteTabelle(new String[] { "W_DISCUSS", "W_DISCDEST", "W_DISCALL", "W_DISCREAD" }, "DISCID_P = ?", new Object[] { discid_p });

  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    super.preInsert(status, datiForm);
    datiForm.setValue("W_DISCUSS_P.DISCMESSINS", new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
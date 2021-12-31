/*
 * Created on: 30-mar-2016
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import org.springframework.transaction.TransactionStatus;


import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

/**
 * Gestore di submit dell'entita' CONFOPECO
 * 
 * @author Francesco.DiMattei
 */
public class GestoreCONFOPECO extends AbstractGestoreChiaveNumerica {

  public String getCampoNumericoChiave() {
    return "NUMCONFOP";
  }

  public String[] getAltriCampiChiave() {
    return null;
  }
  
  public String getEntita() {
    return "CONFOPECO";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    super.preInsert(status, datiForm);
  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
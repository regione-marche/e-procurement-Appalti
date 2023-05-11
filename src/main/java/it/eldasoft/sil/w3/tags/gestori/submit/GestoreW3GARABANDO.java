/*
 * Created on 20/ott/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.gestori.submit;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

public class GestoreW3GARABANDO extends AbstractGestoreChiaveNumerica {

  static Logger logger = Logger.getLogger(GestoreW3GARABANDO.class);

  public String[] getAltriCampiChiave() {
    return null;
  }

  public String getCampoNumericoChiave() {
    return "NUMGARA";
  }

  public String getEntita() {
    return "W3GARA";
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    this.gestioneAllegati(status, datiForm);
    
  }
  
  private void gestioneAllegati(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneAllegati: inizio metodo");

    GestoreW3GARADOC gestoreW3GARADOC = new GestoreW3GARADOC();
    AbstractGestoreChiaveNumerica gestore = new DefaultGestoreEntitaChiaveNumerica(
        "W3GARADOC", "NUMDOC", new String[] { "NUMGARA" }, this.getRequest());
    gestoreW3GARADOC.setForm(this.getForm());

    gestoreW3GARADOC.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestore, "W3GARADOC",
        new DataColumn[] { datiForm.getColumn("W3GARA.NUMGARA") }, null);


    if (logger.isDebugEnabled())
      logger.debug("gestioneAllegati: fine metodo");

  }
  
  
}

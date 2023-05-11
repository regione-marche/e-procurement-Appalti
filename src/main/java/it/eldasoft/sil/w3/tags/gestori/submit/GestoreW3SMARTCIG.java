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

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreW3SMARTCIG extends AbstractGestoreChiaveIDAutoincrementante {

  static Logger logger = Logger.getLogger(GestoreW3SMARTCIG.class);

  @Override
  public String getCampoNumericoChiave() {
    return "CODRICH";
  }

  @Override
  public String getEntita() {
    return "W3SMARTCIG";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreW3SMARTCIG: inizio metodo ");

    // Memorizzazione identificativo utente
    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    datiForm.addColumn("W3SMARTCIG.SYSCON", new Long(profilo.getId()));

    super.preInsert(status, datiForm);

    this.gestioneW3SMARTCIGMERC(status, datiForm);

    if (logger.isDebugEnabled()) logger.debug("GestoreW3SMARTCIG: fine metodo ");

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreW3SMARTCIG: inizio metodo ");

    GeneManager geneManager = this.getGeneManager();
    Long numgara = datiForm.getLong("W3SMARTCIG.CODRICH");
    geneManager.deleteTabelle(new String[] { "W3SMARTCIGMERC"}, "CODRICH = ?", new Object[] { numgara });

    if (logger.isDebugEnabled()) logger.debug("GestoreW3SMARTCIG: fine metodo ");

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreW3SMARTCIG: inizio metodo ");

    if (datiForm.isModifiedTable("W3SMARTCIG")) {
      String cig = datiForm.getString("W3SMARTCIG.CIG");
      if (cig != null && cig.trim().length() > 0) {
        datiForm.setValue("W3SMARTCIG.STATO", new Long(3));
      }
    }

    this.gestioneW3SMARTCIGMERC(status, datiForm);
    
    if (logger.isDebugEnabled()) logger.debug("GestoreW3SMARTCIG: fine metodo ");

  }

  private void gestioneW3SMARTCIGMERC(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("gestioneW3SMARTCIGMERC: inizio metodo ");

    AbstractGestoreChiaveNumerica gestoreMultiploW3SMARTCIGMERC = new DefaultGestoreEntitaChiaveNumerica("W3SMARTCIGMERC", "NUMMERC",
        new String[] { "CODRICH" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm, gestoreMultiploW3SMARTCIGMERC, "W3SMARTCIGMERC",
        new DataColumn[] { datiForm.getColumn("W3SMARTCIG.CODRICH") }, null);

    if (logger.isDebugEnabled()) logger.debug("gestioneW3SMARTCIGMERC: fine metodo ");

  }

}

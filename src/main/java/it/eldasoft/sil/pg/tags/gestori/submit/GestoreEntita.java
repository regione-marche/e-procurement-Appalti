/*
 * Created on 1 dic 2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;


public class GestoreEntita extends AbstractGestoreEntita {

  private String           entitaDefault;

  public GestoreEntita(String entita, HttpServletRequest request) {
    super();
    this.entitaDefault = entita;
    this.setRequest(request);
  }

  public GestoreEntita(String entita, SqlManager sqlManager, GeneManager geneManager) {
   this.entitaDefault = entita;
    this.sqlManager=sqlManager;
   this.geneManager=geneManager;
  }

  @Override
  public String getEntita() {
    return this.entitaDefault;
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }



}

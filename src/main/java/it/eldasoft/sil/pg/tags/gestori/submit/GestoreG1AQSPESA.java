/*
 * Created on 21/04/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import org.springframework.transaction.TransactionStatus;


public class GestoreG1AQSPESA extends AbstractGestoreEntita{

  @Override
  public String getEntita() {
    // TODO Auto-generated method stub
    return "G1AQSPESA";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {


  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void preDelete(TransactionStatus arg0, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void preInsert(TransactionStatus arg0, DataColumnContainer datiForm) throws GestoreException {
    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    int id = genChiaviManager.getNextId("G1AQSPESA");

    datiForm.getColumn("G1AQSPESA.ID").setChiave(true);
    datiForm.setValue("G1AQSPESA.ID", new Long(id));

    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String ncont = UtilityStruts.getParametroString(this.getRequest(),"ncont");
    datiForm.addColumn("G1AQSPESA.NGARA", JdbcParametro.TIPO_TESTO, ngara);
    datiForm.addColumn("G1AQSPESA.NCONT", JdbcParametro.TIPO_NUMERICO, ncont);
  }

  @Override
  public void preUpdate(TransactionStatus arg0, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

}

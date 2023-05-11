package it.eldasoft.sil.pg.tags.gestori.submit;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;


public class GestoreW_DISCREAD extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "W_DISCREAD";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {


  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {


  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {


  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {


  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {


  }

}

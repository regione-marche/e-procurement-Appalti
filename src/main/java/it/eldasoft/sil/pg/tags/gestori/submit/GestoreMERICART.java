package it.eldasoft.sil.pg.tags.gestori.submit;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;


public class GestoreMERICART extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "MERICART";
  }

  @Override
  public void postDelete(DataColumnContainer arg0) throws GestoreException {
    

  }

  @Override
  public void postInsert(DataColumnContainer arg0) throws GestoreException {
    

  }

  @Override
  public void postUpdate(DataColumnContainer arg0) throws GestoreException {
   

  }

  @Override
  public void preDelete(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    

  }

  @Override
  public void preInsert(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    

  }

  @Override
  public void preUpdate(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    

  }

}

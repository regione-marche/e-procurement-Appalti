package it.eldasoft.sil.pg.tags.gestori.submit;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;


public class GestoreW_INVCOMDES extends AbstractGestoreChiaveNumerica {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW_INVCOMDES.class);
  
  public String[] getAltriCampiChiave() {
    return new String[] { "IDPRG" , "IDCOM" };
  }

  public String getCampoNumericoChiave() {
    return "IDCOMDES";
  }

  public String getEntita() {
    return "W_INVCOMDES";
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


  }
  
  

}

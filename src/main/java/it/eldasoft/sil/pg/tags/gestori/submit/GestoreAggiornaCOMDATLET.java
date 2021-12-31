package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreAggiornaCOMDATLET extends AbstractGestoreChiaveNumerica {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreAggiornaCOMDATLET.class);

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "IDPRG" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "IDCOM";
  }

  @Override
  public String getEntita() {
    return "W_INVCOM";
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
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    datiForm.getColumn("W_INVCOM.COMDATLET").setValue(new JdbcParametro(JdbcParametro.TIPO_DATA, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime())));
  }

}

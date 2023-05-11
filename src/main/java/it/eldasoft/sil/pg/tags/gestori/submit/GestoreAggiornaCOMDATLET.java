package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

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

  //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 3;
    String codEvento = "GA_LETTURA_FS12";
    String descrEvento="Comunicazione in ingresso contrassegnata come letta";
    String errMsgEvento = null;
    String idprg="";
    Long idcom = null;
    String operatore="";
    String chiave = "";
    try {
      idprg=datiForm.getString("W_INVCOM.IDPRG");
      idcom = datiForm.getLong("W_INVCOM.IDCOM");
      operatore = datiForm.getString("W_INVCOM.COMKEY1");
      chiave = datiForm.getString("W_INVCOM.COMKEY2");
      descrEvento +=" (id: " + idprg + " - " + idcom.toString() +", cod.operatore: " + operatore + ")";
      datiForm.getColumn("W_INVCOM.COMDATLET").setValue(new JdbcParametro(JdbcParametro.TIPO_DATA, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime())));
      ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      datiForm.getColumn("W_INVCOM.COMSYSLET").setValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, profilo.getId()));
      livEvento = 1;
    }catch(GestoreException e) {
      errMsgEvento = e.getMessage();
      throw e;
    }finally {

      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(chiave);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr);
      }
    }
  }

}

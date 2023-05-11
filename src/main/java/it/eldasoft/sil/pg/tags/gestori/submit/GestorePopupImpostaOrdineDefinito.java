/*
 * Created on 17/06/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la popup Imposta ordine definito
 *
 * @author Marcello.Caminiti
 */
public class GestorePopupImpostaOrdineDefinito extends AbstractGestoreEntita {

  public GestorePopupImpostaOrdineDefinito() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "GARECONT";
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

    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String ncont = UtilityStruts.getParametroString(this.getRequest(),"ncont");
    Date dataOdierna = UtilityDate.getDataOdiernaAsDate();

    try {
      this.getSqlManager().update(
          "update garecont set stato=?, datdef=? where NGARA = ? and ncont=?",
          new Object[] { new Long(3), new Timestamp(dataOdierna.getTime()), ngara, new Long(ncont)});

    } catch (SQLException e) {
      this.getRequest().setAttribute("operazioneErrore", "1");
      throw new GestoreException(
          "Errore durante l'aggiornamento dello stato dell'ordine " + ngara ,null,  e);
    }

    this.getRequest().setAttribute("operazioneEseguita", "1");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}

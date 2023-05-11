/*
 * Created on 26/10/22
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

public class GestoreStipulaLotti extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestoreStipulaLotti() {
    super(false);
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
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    //Gestione CPV

    if(datiForm.isColumn("GARE.NGARA")) {
      GestoreGARE gg= new GestoreGARE();
      gg.setRequest(this.getRequest());
      /*
      if (datiForm.isModifiedColumn("GARCPV.CODCPV")) {
        datiForm.setValue("GARCPV.NGARA", ngara);
      }
      */
      gg.updateGARCPV(status,datiForm);
    }
  }


}



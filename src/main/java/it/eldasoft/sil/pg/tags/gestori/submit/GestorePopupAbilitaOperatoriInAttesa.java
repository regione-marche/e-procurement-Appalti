/*
 * Created on 30/04/2018
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

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per l'abilitazione massiva degli operatori in attesa di verifica domanda
 *
 * @author M. Caminiti
 */
public class GestorePopupAbilitaOperatoriInAttesa extends AbstractGestoreEntita {

  public GestorePopupAbilitaOperatoriInAttesa() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
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
    // lettura dei parametri di input
    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    Timestamp datalimite = datiForm.getData("DATLIMITE");
    Timestamp dataAbilitazione = datiForm.getData("DATABILITAZ");


    try {
      Long operatoriInAttesa=(Long)this.sqlManager.getObject("select count(ngara5) from ditg where ngara5=? and abilitaz=6 and dricind<=?", new Object[]{ngara,datalimite});
      if(operatoriInAttesa!=null && operatoriInAttesa.longValue()>0){
        this.sqlManager.update("update ditg set abilitaz=?, dabilitaz=? where ngara5=? and abilitaz=6 and dricind<=?", new Object[]{1,dataAbilitazione,ngara,datalimite});
      }
      this.getRequest().setAttribute("operatoriInAttesa", operatoriInAttesa);
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'aggiornamento del campo GARE.CODCIG", null,  e);
    }
    this.getRequest().setAttribute("esito", "1");

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}

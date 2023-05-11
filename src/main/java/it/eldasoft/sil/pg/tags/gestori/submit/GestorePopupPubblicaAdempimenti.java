/*
 * Created on 30/08/13
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

import org.springframework.transaction.TransactionStatus;
/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * di aggiornamento del campo pubblica per la pagina anticor-pg-appalti.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupPubblicaAdempimenti extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  public GestorePopupPubblicaAdempimenti() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupPubblicaAdempimenti(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

    String idString = UtilityStruts.getParametroString(this.getRequest(),"id");
    Long id = new Long(idString);
    String operazione = UtilityStruts.getParametroString(this.getRequest(),"operazione");

    try {
      this.sqlManager.update("update anticorlotti set pubblica=? where id=?", new Object[]{operazione, id});
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nell'aggiornamento dello valore di ANTICORLOTTI.PUBBLICA della riga con id =" + idString, null, e);
    }


    //Se tutto è andato bene setto nel request il parametro operazioneEseguita = 1
    this.getRequest().setAttribute("operazioneEseguita", "1");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }




  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }




}
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
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;

import org.springframework.transaction.TransactionStatus;
/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * di rettifica/aggiornamento invio per la pagina anticor-pg-datigen.jsp
 * e la funzione di conrollo dati per la pagina anticor-pg-appalti.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRettificaAdempimenti extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "ANTICOR";
  }

  public GestorePopupRettificaAdempimenti() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupRettificaAdempimenti(boolean isGestoreStandard) {
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
    String operazione = UtilityStruts.getParametroString(this.getRequest(),"operazione");
    String entita = UtilityStruts.getParametroString(this.getRequest(),"entitaAdempimenti");
    String completato="2";
    if("APPROVAZIONE".equals(operazione)){
      completato="1";
      //In questo caso la chiamata del gestore non avviene da ANTICOR, ma dalle figlie, quindi si deve ricavare ANTICOR.ID
      if("ANTICORLOTTI".equals(entita)){
        try {
          Long id=(Long)this.sqlManager.getObject("select idanticor from anticorlotti where id=?", new Object[]{new Long(idString)});
          idString = id.toString();
        } catch (NumberFormatException e) {
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella lettura del'id dell'adempimento " + idString, null, e);
        } catch (SQLException e) {
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella lettura del'id dell'adempimento " + idString, null, e);
        }
      }else if("ANTICORPARTECIP".equals(entita)){
        try {
          Long id=(Long)this.sqlManager.getObject("select idanticor from anticorlotti a, anticorpartecip b where a.id= b.idanticorlotti and b.id= ?", new Object[]{new Long(idString)});
          idString = id.toString();
        } catch (NumberFormatException e) {
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella lettura del'id dell'adempimento " + idString, null, e);
        } catch (SQLException e) {
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella lettura del'id dell'adempimento " + idString, null, e);
        }
      }
    }
    Long id = new Long(idString);

    try {
      Date oggi = UtilityDate.getDataOdiernaAsDate();
      this.sqlManager.update("update anticor set completato=?, esportato=?, pubblicato=?, datappr=? where id=?", new Object[]{completato,"2","2",oggi, id});
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nell'aggiornamento dei dati dell'adempimento " + idString, null, e);
    }

    //Se tutto è andato bene setto nel request il parametro rettificaEseguita = 1
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
/*
 * Created on 25/02/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;
/**
 * Gestore non standard che si occupa di gestire la funzione
 * Riporta nell'anno di riferimento per la pagina anticor-pg-appalti.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRiportaAppalto extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  public GestorePopupRiportaAppalto() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupRiportaAppalto(boolean isGestoreStandard) {
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
    String update="update anticorlotti set daannoprec=?";
    Object parametri[]=null;
    try {
      Vector datiAnticor = this.sqlManager.getVector("select cig,annorif, codein, inviabile from anticor a, anticorlotti a1 where a.id = a1.idanticor and a1.id=?", new Object[]{id});
      if(datiAnticor!=null && datiAnticor.size()>0){
        String cig = SqlManager.getValueFromVectorParam(
            datiAnticor, 0).getStringValue();
        Long annorif = SqlManager.getValueFromVectorParam(
            datiAnticor, 1).longValue();
        String codein = SqlManager.getValueFromVectorParam(
            datiAnticor, 2).getStringValue();
        Long annorifPrec= new Long(annorif.longValue()-1);
        String inviabile = SqlManager.getValueFromVectorParam(
            datiAnticor, 3).getStringValue();
        if("1".equals(operazione)){
          if("1".equals(inviabile)){
            update += ", pubblica=?";
            parametri= new Object[]{"3","1", id};
          }else{
            parametri= new Object[]{"3", id};
          }

        }else{
          Vector datiAnticorlotti = this.sqlManager.getVector("select datainizio, dataultimazione, impsommeliq from anticor a, anticorlotti a1 where " +
              "a.id = a1.idanticor and codein =? and annorif=? and cig=? and esportato=? and pubblica=?", new Object[]{codein,annorifPrec,cig,"1","1"});
          if(datiAnticorlotti!=null && datiAnticorlotti.size()>0){
            Date datainizio = SqlManager.getValueFromVectorParam(datiAnticorlotti, 0).dataValue();
            Date dataultimazione = SqlManager.getValueFromVectorParam(datiAnticorlotti, 1).dataValue();
            Double impsommeliq = SqlManager.getValueFromVectorParam(datiAnticorlotti, 2).doubleValue();

            update += ", datainizio=?, dataultimazione=?, impsommeliq=?, pubblica=?";
            parametri= new Object[]{"1", datainizio, dataultimazione, impsommeliq, "2", id};
          }
        }

      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nella lettura dei dati del lotto dell'anno precedente", null, e);
    }

    update+=" where id=?";

    try {
      this.sqlManager.update(update, parametri);
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nell'aggiornamento del lotto con id =" + idString, null, e);
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
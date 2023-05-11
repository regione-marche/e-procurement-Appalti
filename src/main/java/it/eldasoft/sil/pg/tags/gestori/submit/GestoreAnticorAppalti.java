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
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per le pagine anticor-pg-appalti.jsp
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Marcello Caminiti
 */
public class GestoreAnticorAppalti extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  public GestoreAnticorAppalti() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreAnticorAppalti(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }



  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    if(impl.isColumn("ANTICORPARTECIP.ID")){
      Long id = impl.getLong("ANTICORPARTECIP.ID");
      //Cancellazione di ANTICORDITTE
      String delete="delete from anticorditte where idanticorpartecip =?";
      try {
        this.sqlManager.update(delete,  new Object[] {id});
      } catch (SQLException e) {
        throw new GestoreException("Errore nella cancellazione di ANTICORDITTE",null, e);
      }

      Long idAnticorlotti=null;
      try {
       idAnticorlotti= (Long)this.sqlManager.getObject("select idanticorlotti from anticorpartecip where id=?", new Object[]{id});
      }catch (SQLException e) {
        throw new GestoreException("Errore nella leettura di ANTICORPARTECIP.IDNATICORLOTTI",null, e);
      }

      //Cancellazione di ANTICORPARTECIP
      delete="delete from anticorpartecip where id =?";
      try {
        this.sqlManager.update(delete,  new Object[] {id});
      } catch (SQLException e) {
        throw new GestoreException("Errore nella cancellazione di ANTICORPARTECIP",null, e);
      }
      //Si deve aggiornare lo stato del lotto
      PgManager pgManager =(PgManager) UtilitySpring.getBean(
          "pgManager", this.getServletContext(), PgManager.class);

      String ufficioIntestatario=null;
      HttpSession session = this.getRequest().getSession();
      if ( session != null) {
        ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
      }

      HashMap<String,Object> esitoControlli = null;
      String inviabile = null;
      String pubblica = null;
      try {
        esitoControlli = pgManager.controlloDatiAVCP(idAnticorlotti,true,false,ufficioIntestatario);
        if (esitoControlli != null) {
          String msg = null;
          Boolean controlloOk = (Boolean)esitoControlli.get("esito");
          if (controlloOk.booleanValue()) {
            inviabile = "1";
            pubblica = "1";
          } else {
            msg = (String)esitoControlli.get("msg");
            inviabile = "2";
            pubblica = "2";
          }
          this.sqlManager.update("update anticorlotti set inviabile=?, pubblica=?, testolog=? where id=?",  new Object[] {inviabile,pubblica,msg,idAnticorlotti});
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dei dati del lotto con id " + id.toString(), null, e);
      }
    } else if(impl.isColumn("ANTICORLOTTI.ID")) {
      Long id = impl.getLong("ANTICORLOTTI.ID");
      try {
        //Cancellazione di ANTICORDITTE
        String delete="delete from anticorditte where idanticorpartecip in (select p.id from anticorpartecip p, anticorlotti l where p.idanticorlotti=l.id and p.id=?)";
        this.sqlManager.update(delete,  new Object[] {id});

        //Cancellazione di ANTICORPARTECIP
        delete="delete from anticorpartecip where idanticorlotti in (select id from anticorlotti where id=?)";
        this.sqlManager.update(delete,  new Object[] {id });

        //Cancellazione di ANTICORLOTTI
        delete="delete from anticorlotti where id=?";

        this.sqlManager.update(delete,  new Object[] {id });
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Errore nella cancellazione di ANTICORLOTTI e tabelle figlie", null, e);
      }
    }

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    String ufficioIntestatario = null;
    HttpSession session = this.getRequest().getSession();
    if (session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

  	int numeroAppalti = 0;
    String numAppalti = this.getRequest().getParameter("numeroAppalti");
    if (numAppalti != null && numAppalti.length() > 0)
      numeroAppalti =  UtilityNumeri.convertiIntero(numAppalti).intValue();

    HashMap<String,Object> esitoControlli = null;
    Boolean controlloOk = null;

    PgManager pgManager =(PgManager) UtilitySpring.getBean(
        "pgManager", this.getServletContext(), PgManager.class);
    Timestamp datainizio=null;
    Timestamp dataultimazione=null;
    //Double impsommeliq=null;
    String codiceCig = null;
    Long id = null;
    //Long stato=null;

    for (int i = 1; i <= numeroAppalti; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
      		impl.getColumnsBySuffix("_" + i, false));

      try {
        if (dataColumnContainerDiRiga.isModifiedTable("ANTICORLOTTI")) {
          datainizio = dataColumnContainerDiRiga.getData("ANTICORLOTTI.DATAINIZIO");
          dataultimazione = dataColumnContainerDiRiga.getData("ANTICORLOTTI.DATAULTIMAZIONE");
          //impsommeliq = dataColumnContainerDiRiga.getDouble("ANTICORLOTTI.IMPSOMMELIQ");
          codiceCig = dataColumnContainerDiRiga.getString("CIG_FIT");
          id = dataColumnContainerDiRiga.getLong("ANTICORLOTTI.ID");
          //stato=dataColumnContainerDiRiga.getLong("ANTICORLOTTI.STATO");

          esitoControlli = pgManager.controlloDatiAVCP(id,true,true,ufficioIntestatario);
          String msg = null;
          if (esitoControlli != null)
            controlloOk = (Boolean)esitoControlli.get("esito");

          //if(((datainizio==null || dataultimazione==null || impsommeliq==null) && (new Long(2)).equals(stato))  || !controlloOk.booleanValue()) {
          if ((datainizio == null && dataultimazione != null ) || (datainizio != null && dataultimazione != null && datainizio.after(dataultimazione)) || !controlloOk.booleanValue()) {
            dataColumnContainerDiRiga.setValue("ANTICORLOTTI.INVIABILE", "2");
            msg = (String)esitoControlli.get("msg");
            //Per potere mettere il valore a pubblica=1 si deve rieseguire il controllo dei della validità dei dati.
            //Se il controllo non viene superato allora si deve dare un messaggio e riportare PUBBLICA a 2
            if(dataColumnContainerDiRiga.isColumn("ANTICORLOTTI.PUBBLICA") && ((dataColumnContainerDiRiga.isModifiedColumn("ANTICORLOTTI.PUBBLICA") && "1".equals(dataColumnContainerDiRiga.getString("ANTICORLOTTI.PUBBLICA")))
                || "1".equals(dataColumnContainerDiRiga.getString("ANTICORLOTTI.PUBBLICA")))){
                dataColumnContainerDiRiga.setValue("ANTICORLOTTI.PUBBLICA", "2");
                UtilityStruts.addMessage(this.getRequest(), "warning",
                    "warnings.adempimenti.pubblica.NOK",
                    new Object[] { codiceCig });
            }
          }else
            dataColumnContainerDiRiga.setValue("ANTICORLOTTI.INVIABILE", "1");

          dataColumnContainerDiRiga.setValue("ANTICORLOTTI.TESTOLOG", msg);
          dataColumnContainerDiRiga.update("ANTICORLOTTI", sqlManager);
        }
      } catch (SQLException e) {
           throw new GestoreException("Errore nell'aggiornamento dei dati in ANTICORLOTTI",null, e);
      }
    }


  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}
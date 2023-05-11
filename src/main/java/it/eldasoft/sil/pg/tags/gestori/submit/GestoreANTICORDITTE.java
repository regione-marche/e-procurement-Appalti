/*
 * Created on 05-09-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la pagina di dettaglio dei partecipanti ad un lotto dell'adempimento (ANTICORDITTE).
 *
 * @author Marcello Caminiti
 */
public class GestoreANTICORDITTE extends AbstractGestoreChiaveNumerica {

  @Override
  public String[] getAltriCampiChiave() {
    return null;
  }

  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  public GestoreANTICORDITTE() {
    super(false);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    //Long idAnticorPartecip=datiForm.getLong("ANTICORPARTECIP.ID");

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

    DefaultGestoreEntita gestore = null;
    Long tipo=datiForm.getLong("ANTICORPARTECIP.TIPO");

    if((new Long(1)).equals(tipo)){
      String ragsoc = datiForm.getString("ANTICORDITTE.RAGSOC");
      datiForm.addColumn("ANTICORPARTECIP.RAGSOC", JdbcParametro.TIPO_TESTO, ragsoc);
    }
    //Salvataggio ANTICORPARTECIP
    datiForm.setValue("ANTICORPARTECIP.ID", new Long(genChiaviManager.getNextId("ANTICORPARTECIP")));
    gestore = new DefaultGestoreEntita("ANTICORPARTECIP", this.getRequest());
    gestore.inserisci(status, datiForm);

    Long id = datiForm.getLong("ANTICORPARTECIP.ID");

    //Salvataggio ANTICORDITTE
    if((new Long(1)).equals(tipo)){
      datiForm.addColumn("ANTICORDITTE.IDANTICORPARTECIP", JdbcParametro.TIPO_NUMERICO, id);
      //Salvataggio dei dati di ANTICORDITTE prima di effettuare i controlli
      datiForm.setValue("ANTICORDITTE.ID", new Long(genChiaviManager.getNextId("ANTICORDITTE")));
      gestore = new DefaultGestoreEntita("ANTICORDITTE",  this.getRequest());

      //Nel caso in cui prima si siano valorizzati i campi della sezione dinamica nella jsp e poi si
      //decide di adoperare una impresa singola, si devono togliere da datiForm i campi di appoggio adoperati
      //nella gestione delle sezioni dinamiche
      if(datiForm.isColumn("NUMERO_ANTICORDITTE")){
        int numeroRecord = datiForm.getLong("NUMERO_ANTICORDITTE").intValue();
          for (int i = 1; i <= numeroRecord; i++) {
            if(datiForm.isColumn("MOD_ANTICORDITTE_" + i))
              datiForm.removeColumns(new String[] {
                  "ANTICORDITTE.MOD_ANTICORDITTE_" +i, "ANTICORDITTE.DEL_ANTICORDITTE_" +i});
          }
      }

      gestore.inserisci(status, datiForm);
    }else{
      //Si deve valorizzare il campo ANTICORPARTECIP.IDANTICOR dei record delle sezioni multiple
      int numeroRecord = datiForm.getLong("NUMERO_ANTICORDITTE").intValue();
      for (int i = 1; i <= numeroRecord; i++) {
        if(datiForm.isColumn("MOD_ANTICORDITTE_" + i) && "1".equals(datiForm.getString("MOD_ANTICORDITTE_" + i))){
          datiForm.addColumn("ANTICORDITTE.IDANTICORPARTECIP_" + i, JdbcParametro.TIPO_NUMERICO, id);
        }
      }
      //Salvataggio dei dati delle sezioni dinamiche prima di effettuare i controlli
      AbstractGestoreChiaveIDAutoincrementante gestoreMultiploANTICORDITTE = new DefaultGestoreEntitaChiaveIDAutoincrementante(
          "ANTICORDITTE", "ID", this.getRequest());

      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreMultiploANTICORDITTE, "ANTICORDITTE", new DataColumn[] {datiForm.getColumn("ANTICORPARTECIP.ID")}, null);

    }

    //Controlli sui dati per la conformità a AVCP
    PgManager pgManager =(PgManager) UtilitySpring.getBean(
        "pgManager", this.getServletContext(), PgManager.class);

    HashMap<String,Object> esitoControlli = null;
    Long idAnticorLotti = datiForm.getLong("ANTICORPARTECIP.IDANTICORLOTTI");
    try {
      esitoControlli=pgManager.controlloDatiAVCP(idAnticorLotti,true,false,ufficioIntestatario);
      if(esitoControlli!=null){
        String inviabile= null;
        String pubblica=null;
        String msg = null;
        Boolean controlloOk = (Boolean)esitoControlli.get("esito");
        if(controlloOk.booleanValue()){
          inviabile="1";
          pubblica="1";
        }else{
          inviabile="2";
          pubblica="2";
          msg = (String)esitoControlli.get("msg");
        }


        //Aggiornamento di PUBBLICA e INVIABILE
        this.sqlManager.update("update anticorlotti set inviabile=?, pubblica=?, testolog=? where id=?", new Object[]{inviabile, pubblica, msg, idAnticorLotti});

      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella verifica dei dati del lotto " + idAnticorLotti.toString(), null, e);
    }


    this.getRequest().setAttribute("tipo", tipo);
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    //Salvataggio ANTICORPARTECIP
    String tipo = UtilityStruts.getParametroString(this.getRequest(),"tipo");
    if("1".equals(tipo)){
      String ragsoc = datiForm.getString("ANTICORDITTE.RAGSOC");
      datiForm.addColumn("ANTICORPARTECIP.RAGSOC", JdbcParametro.TIPO_TESTO, ragsoc);
    }

    DefaultGestoreEntita gestore = new DefaultGestoreEntita("ANTICORPARTECIP", this.getRequest());
    gestore.update(status, datiForm);

    Long idAnticorPartecip=datiForm.getLong("ANTICORPARTECIP.ID");


    if("1".equals(tipo)){
      //Salvataggio dei dati di ANTICORDITTE prima di effettuare i controlli
      gestore = new DefaultGestoreEntita("ANTICORDITTE", this.getRequest());
      Long idAnticorditte = datiForm.getLong("ANTICORDITTE.ID");
      if(idAnticorditte!=null){
        gestore.update(status, datiForm);
      }else{
        GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
            this.getServletContext(), GenChiaviManager.class);
        datiForm.setValue("ANTICORDITTE.ID", new Long(genChiaviManager.getNextId("ANTICORDITTE")));
        datiForm.addColumn("ANTICORDITTE.IDANTICORPARTECIP", JdbcParametro.TIPO_NUMERICO, idAnticorPartecip);
        gestore.inserisci(status, datiForm);
      }

    }else{
      //Salvataggio dei dati delle sezioni dinamiche prima di effettuare i controlli
      AbstractGestoreChiaveIDAutoincrementante gestoreMultiploANTICORDITTE = new DefaultGestoreEntitaChiaveIDAutoincrementante(
          "ANTICORDITTE", "ID", this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreMultiploANTICORDITTE, "ANTICORDITTE", new DataColumn[] {datiForm.getColumn("ANTICORPARTECIP.ID")}, null);

    }

    //Si devono ricavare i dati del lotto
    Long idAnticorLotti;
    try {
      idAnticorLotti = (Long)this.sqlManager.getObject("select idanticorlotti from anticorpartecip where id=?", new Object[]{idAnticorPartecip});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura del campo idanticorlotti di anticorpartecip con id=" + idAnticorPartecip.toString(), null, e);
    }
    String inviabileOriginale;
    try {
      inviabileOriginale = (String)this.sqlManager.getObject("select inviabile from anticorlotti where id=?", new Object[]{idAnticorLotti});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura del campo inviabile di anticorlotti con id=" + idAnticorLotti.toString(), null, e);
    }
    String pubblica;
    try {
      pubblica = (String)this.sqlManager.getObject("select pubblica from anticorlotti where id=?", new Object[]{idAnticorLotti});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura del campo pubblica di anticorlotti con id=" + idAnticorLotti.toString(), null, e);
    }
    String inviabile=null;

    PgManager pgManager =(PgManager) UtilitySpring.getBean(
        "pgManager", this.getServletContext(), PgManager.class);

    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

    //Controlli sui dati per la conformità a AVCP
    HashMap<String,Object> esitoControlli = null;
    try {
      esitoControlli = pgManager.controlloDatiAVCP(idAnticorLotti,true,false,ufficioIntestatario);
      if (esitoControlli!=null) {
        Boolean controlloOk = (Boolean)esitoControlli.get("esito");
        String msg = null;
        if (controlloOk.booleanValue()) {
          inviabile="1";
          if (!"1".equals(inviabileOriginale)) {
            pubblica = "1";
            UtilityStruts.addMessage(this.getRequest(), "info", "info.adempimenti.verificaDati.OK",null);
          }
        } else {
          msg = (String)esitoControlli.get("msg");
          inviabile="2";
          if("1".equals(inviabileOriginale)){
            pubblica = "2";
            UtilityStruts.addMessage(this.getRequest(),"warning","warnings.adempimenti.verificaDati.NOK",
                new Object[] { ". Consultare il dettaglio nella pagina 'Dati generali' del lotto." });
          }
        }

        //Aggiornamento di PUBBLICA e INVIABILE
        this.sqlManager.update("update anticorlotti set inviabile=?, pubblica=?, testolog=? where id=?", new Object[]{inviabile,pubblica,msg,idAnticorLotti});
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella verifica dei dati del lotto " + idAnticorLotti.toString(), null, e);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }



}


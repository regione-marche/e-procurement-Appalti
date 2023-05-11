/*
 * Created on 04-09-2013
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standarg per la pagina di dettaglio del lotto dell'adempimento (ANTICORLOTTI).
 *
 * @author Marcello Caminiti
 */
public class GestoreANTICORLOTTI extends AbstractGestoreChiaveIDAutoincrementante {

  private GenChiaviManager genChiaviManager = null;
	
  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    //sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
    //    this.getServletContext(), SqlManager.class);
    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
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

    super.preInsert(status, datiForm);

    //Controllo unicità del codice CIG
    String ufficioIntestatario = null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = (String) session.getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
    }

    String codCig = datiForm.getString("CIG");
    
    // Gestione del codice CIG fittizio
    if (datiForm.isColumn("ESENTE_CIG")) {
  	  String esenteCig = datiForm.getString("ESENTE_CIG");
  	  if ("1".equals(esenteCig)) {
  		  int nextId = this.genChiaviManager.getNextId("GARE.CODCIG");
  		  String codCigFittizio = "#".concat(StringUtils.leftPad(""+nextId, 9, "0"));
  		  datiForm.setValue("ANTICORLOTTI.CIG", codCigFittizio);
  		  codCig = codCigFittizio;
  	  }
    }
    
    Long idAnticor = datiForm.getLong("ANTICORLOTTI.IDANTICOR");
    try {
      Long annorif = (Long) this.sqlManager.getObject(
          "select annorif from anticor where id=?",
          new Object[] { idAnticor });
      // Si deve controllare che in anticorlotti non esista già un lotto con
      // uguale CIG per l'anno di riferimento
      Long count = null;
      String codiceErrore = "CIG.dupl";
      String select = "select count(lotti.id) from anticor a, anticorlotti lotti where idanticor=a.id and annorif=? and upper(cig)=?";
      if (ufficioIntestatario != null && !"".equals(ufficioIntestatario)) {
        select += " and codein='" + ufficioIntestatario + "'";
        codiceErrore = "CIG.duplUff";
      }
      count = (Long) this.sqlManager.getObject(select, new Object[] {
          annorif, codCig.toUpperCase() });
      if (count != null && count.longValue() > 0) {
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Esiste già un lotto con codice CIG="
            + codCig
            + " per l'anno di riferimento "
            + annorif.toString(), codiceErrore, new Object[] { codCig,
            annorif.toString()}, new Exception());
      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nella lettura di ANTICOR.ANNORIF", null, e);
    }

    //DefaultGestoreEntitaChiaveNumerica gestore = new DefaultGestoreEntitaChiaveNumerica(
    //    "ANTICORLOTTI", "ID",null, this.getRequest());
    datiForm.setValue("ANTICORLOTTI.INVIABILE", "2");
    datiForm.setValue("ANTICORLOTTI.PUBBLICA", "2");
    datiForm.setValue("ANTICORLOTTI.DAANNOPREC", "2");
    //gestore.inserisci(status, datiForm);
   
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long id = datiForm.getLong("ANTICORLOTTI.ID");

    PgManager pgManager =(PgManager) UtilitySpring.getBean(
        "pgManager", this.getServletContext(), PgManager.class);

    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO)));
    }

    //Controlli sui dati per la conformità a AVCP
    HashMap<String, Object> esitoControlli = null;
    try {
      esitoControlli = pgManager.controlloDatiAVCP(id,true,false,ufficioIntestatario);
      if (esitoControlli != null) {
        Boolean controlloOk = (Boolean)esitoControlli.get("esito");
        if (controlloOk.booleanValue()) {
          this.sqlManager.update("update anticorlotti set PUBBLICA=?,INVIABILE=?,TESTOLOG=null where ID=?" , new Object[]{"1","1", id});
          UtilityStruts.addMessage(this.getRequest(), "info", "info.adempimenti.verificaDati.OK",null);
        } else {
          String msg = (String)esitoControlli.get("msg");
          this.sqlManager.update("update anticorlotti set TESTOLOG=? where ID=?" , new Object[]{msg,id});
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella verifica dei dati del lotto " + id.toString(), null, e);
    }
  }

  @Override
  public void afterUpdateEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long id = datiForm.getLong("ANTICORLOTTI.ID");
    String inviabile=null;
    String inviabileOriginale= datiForm.getString("ANTICORLOTTI.INVIABILE");

    PgManager pgManager =(PgManager) UtilitySpring.getBean(
        "pgManager", this.getServletContext(), PgManager.class);

    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

    //Controlli sui dati per la conformità a AVCP
    HashMap<String, Object> esitoControlli = null;
    try {
      esitoControlli = pgManager.controlloDatiAVCP(id,true,false,ufficioIntestatario);
      if (esitoControlli != null) {
        Boolean controlloOk = (Boolean) esitoControlli.get("esito");
        if (controlloOk.booleanValue()) {
          inviabile="1";
          if (!"1".equals(inviabileOriginale)) {
            datiForm.setValue("ANTICORLOTTI.PUBBLICA", "1");
            UtilityStruts.addMessage(this.getRequest(), "info", "info.adempimenti.verificaDati.OK", null);
          }
          datiForm.setValue("ANTICORLOTTI.TESTOLOG", null);
        } else {
          inviabile="2";
          if ("1".equals(inviabileOriginale)) {
            datiForm.setValue("ANTICORLOTTI.PUBBLICA", "2");
            UtilityStruts.addMessage(this.getRequest(),"warning","warnings.adempimenti.verificaDati.NOK",
                  new Object[] { ". Consultare il dettaglio riportato sotto." });
          }
          String msg = (String)esitoControlli.get("msg");
          datiForm.setValue("ANTICORLOTTI.TESTOLOG", msg);
        }
        datiForm.setValue("ANTICORLOTTI.INVIABILE", inviabile);

        //Aggiornamento di PUBBLICA e INVIABILE
        //gestore.update(status, datiForm);
        datiForm.update("ANTICORLOTTI", sqlManager);

      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella verifica dei dati del lotto " + id.toString(), null, e);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

}

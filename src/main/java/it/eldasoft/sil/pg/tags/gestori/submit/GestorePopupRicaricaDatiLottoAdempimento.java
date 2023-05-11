/*
 * Created on 14-12-2013
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

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la popup per ricaricare i dati di un lotto di un Adempimento presente in bko.
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRicaricaDatiLottoAdempimento extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  public GestorePopupRicaricaDatiLottoAdempimento() {
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

    String idAnticorLottiString = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
    "idAnticorLotti"));

    String idAnticorString = UtilityStruts.getParametroString(this.getRequest(),
      "idAnticor");

    String idLottoRicaricaDati = StringUtils.stripToNull((UtilityStruts.getParametroString(this.getRequest(),
    "idLotto")));

    Long idAnticor = new Long(idAnticorString);
    Long idAnticorLotti = new Long(idAnticorLottiString);
    String daannoprec = null;

    String ufficioIntestatario = null;
    HttpSession session = this.getRequest().getSession();
    if (session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

    //Si deve controllare che esista il lotto nella view, controllando il codice CIG
    //e nel caso sia abilitata la gestione degli Uffici Intestatari si deve filtrare per il codice della stazione appaltante
    Long numLotti = null;
    try {
      String select="select count(*) from v_dati_lotti where lotto=?";
      if (ufficioIntestatario != null)
        select += " and codiceprop='" + ufficioIntestatario + "'" ;
      numLotti = (Long) this.sqlManager.getObject(select, new Object[] {idLottoRicaricaDati });
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nella lettura del LOTTO della view V_DATI_LOTTI con lotto=" + idLottoRicaricaDati, null, e);
    }

    if (numLotti.longValue() == 0) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Il lotto con codice " + idLottoRicaricaDati + " non è più presente nel sistema", "lottoNonPresente", new Object[]{idLottoRicaricaDati},new Exception());
    } else {
      //Prima di cancellare determino il valore di daannoprec
      try {
        daannoprec = (String) this.sqlManager.getObject(
            "select daannoprec from anticorlotti where id=?",
            new Object[] { idAnticorLotti });
      } catch (SQLException e) {
    	  this.getRequest().setAttribute("erroreOperazione", "1");
    	  throw new GestoreException("Errore nella lettura di ANTICORLOTTI.DANNOPREC",
    			  null, e);
      }

      try {
    	  //Cancellazione di ANTICORDITTE
	      String delete = "delete from anticorditte where idanticorpartecip in (select p.id from anticorpartecip p, anticorlotti l where p.idanticorlotti=l.id and p.id=?)";
	      this.sqlManager.update(delete,  new Object[] {new Long(idAnticorLottiString) });
	
	      //Cancellazione di ANTICORPARTECIP
	      delete = "delete from anticorpartecip where idanticorlotti in (select id from anticorlotti where id=?)";
	      this.sqlManager.update(delete,  new Object[] {new Long(idAnticorLottiString) });
	
	      //Cancellazione di ANTICORLOTTI
	      delete = "delete from anticorlotti where id=?";
	      this.sqlManager.update(delete,  new Object[] {new Long(idAnticorLottiString) });
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Errore nella cancellazione di ANTICORLOTTI e tabelle figlie", null, e);
      }
    }

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    try {
      //Voglio che sia ricaricato il lotto prelevando i dati sempre dalla view v_dati_lotti, anche se si tratta di un lotto proveniente
      //dall'anno precedente, ed inoltre per quelli che hanno impostato daannoprec='3', dopo l'aggiornamento deve rimanere a '3' tale valore
      //Per caricare i dati dalla view non mi serve che ci sia specificato l'anno di riferimento
      pgManager.insertLottiAdempimento(null, null, idAnticor, idAnticorLotti, idLottoRicaricaDati, ufficioIntestatario, "1",daannoprec,false,null);
    } catch(GestoreException e ) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw e;
    }
    this.getRequest().setAttribute("operazioneEseguita", "1");
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

}

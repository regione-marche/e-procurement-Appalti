/*
 * Created on 15-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pl.struts.gestori;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pl.bl.PlManager;
import it.eldasoft.utils.spring.UtilitySpring;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore che implementa le funzionalita di aggiornamento dati della tabella
 * CONT
 * 
 * @author Marco.Franceschin
 */
public class GestoreCONT extends AbstractGestoreChiaveNumerica {

  public String[] getAltriCampiChiave() {
    return new String[] { "CODLAV", "NAPPAL" };
  }

  public String getCampoNumericoChiave() {
    return "NPROAT";
  }

  public String getEntita() {
    return "CONT";
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    super.preInsert(status, impl);
    PlManager manager = (PlManager) UtilitySpring.getBean(
        "plManager", this.getServletContext(), PlManager.class);
    // Se non è settato il PERCAL di appa allora eseguo l'update sull'eventuale
    // appalto (se è stato modificato l'importo netto di contratto)
    if (impl.isColumn("CONT.NIMPCO")
        && impl.getColumn("CONT.NIMPCO").isModified()) {
      manager.aggPERCAL(impl, "UPD");
    }
  }
  
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    PlManager manager = (PlManager) UtilitySpring.getBean(
        "plManager", this.getServletContext(), PlManager.class);
    // Se non è settato il PERCAL di appa allora eseguo l'update sull'eventuale
    // appalto (se è stato modificato l'importo netto di contratto)
    if (impl.isColumn("CONT.NIMPCO")
        && impl.getColumn("CONT.NIMPCO").isModified()) {
      manager.aggPERCAL(impl, "UPD");
    }
    // Alla modifica della data atto del contratto (CONT.DAATTO) devo aggiornare
    // l'importo netto delle
    // categorie permanenti
    if (impl.isColumn("CONT.DAATTO")
        && impl.isModifiedColumn("CONT.DAATTO")
        && impl.getObject("CONT.DAATTO") != null) {
      if (manager.getUltimoContratto(impl.getString("CONT.CODLAV"),
          impl.getLong("CONT.NAPPAL"), "daatto is not null", null) <= impl.getLong(
          "CONT.NPROAT").longValue()) {
        manager.setImportoNettoCategorie(impl, impl.getString("CONT.CODLAV"),
            impl.getLong("CONT.NAPPAL"), impl.getDouble("CONT.NIMPCO"), true);
      }
    }
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    PlManager manager = (PlManager) UtilitySpring.getBean(
        "plManager", this.getServletContext(), PlManager.class);
    manager.deleteContratto(impl.getString("CONT.CODLAV"),
        impl.getLong("CONT.NAPPAL"), impl.getLong("CONT.NPROAT"));
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

}
/*
 * Created on 20/ott/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.gestori.submit;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreW3GARA extends AbstractGestoreChiaveIDAutoincrementante {

  static Logger logger = Logger.getLogger(GestoreW3GARA.class);


  @Override
  public String getCampoNumericoChiave() {
    return "NUMGARA";
  }

  @Override
  public String getEntita() {
    return "W3GARA";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreW3GARA: inizio metodo ");

    // Memorizzazione identificativo utente
//    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
//    datiForm.addColumn("W3GARA.SYSCON", new Long(profilo.getId()));

    // Memorizzazione UUID
    UUID uuid = UUID.randomUUID();
    datiForm.addColumn("W3GARA.GARA_UUID", uuid.toString());

    super.preInsert(status, datiForm);

    this.gestioneW3GARAMERC(status, datiForm);

    if (logger.isDebugEnabled()) logger.debug("GestoreW3GARA: fine metodo ");

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

	  String avcpass = datiForm.getString("W3GARA.ESCLUSO_AVCPASS");
	    
	  if (avcpass.equals("1")) {
		  UtilityStruts.addMessage(this.getRequest(), "info", "info.requisiti.AVCPASS", new Object[] { });
	  } else {
		  GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager = (GestioneServiziIDGARACIGManager) UtilitySpring.getBean("gestioneServiziIDGARACIGManager",
		          getServletContext(), GestioneServiziIDGARACIGManager.class);
		  Long numgara = datiForm.getLong("W3GARA.NUMGARA");
		  gestioneServiziIDGARACIGManager.gestioneRequisiti(numgara);
	  }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	  if (datiForm.isColumn("W3GARA.ESCLUSO_AVCPASS")) {
		  DataColumn avcpass = datiForm.getColumn("W3GARA.ESCLUSO_AVCPASS");
		  if (avcpass.isModified()) {
			  if (avcpass.getValue() != null && avcpass.getValue().stringValue().equals("1"))
			  {
				  UtilityStruts.addMessage(this.getRequest(), "info", "info.requisiti.AVCPASS", new Object[] { });
			  } 
			  GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager = (GestioneServiziIDGARACIGManager) UtilitySpring.getBean("gestioneServiziIDGARACIGManager",
		          getServletContext(), GestioneServiziIDGARACIGManager.class);
			  Long numgara = datiForm.getLong("W3GARA.NUMGARA");
			  gestioneServiziIDGARACIGManager.gestioneRequisiti(numgara);
		  }
	  }
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreW3GARA: inizio metodo ");

    GeneManager geneManager = this.getGeneManager();
    Long numgara = datiForm.getLong("W3GARA.NUMGARA");
    geneManager.deleteTabelle(new String[] { "W3GARAREQ", "W3GARAREQCIG", "W3GARAREQDOC", "W3GARADOC", "W3LOTT", "W3LOTTCATE", "W3LOTTCUP",
        "W3LOTTTIPI", "W3GARAMERC", "W3COND" }, "NUMGARA = ?", new Object[] { numgara });

    if (logger.isDebugEnabled()) logger.debug("GestoreW3GARA: fine metodo ");

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreW3GARA: inizio metodo ");

    if (datiForm.isModifiedTable("W3GARA") || datiForm.isModifiedTable("W3GARAMERC")) {
      String idgara = datiForm.getString("W3GARA.ID_GARA");
      if (idgara != null && idgara.trim().length() > 0) {
        datiForm.setValue("W3GARA.STATO_SIMOG", new Long(3));
      }
    }

    this.gestioneW3GARAMERC(status, datiForm);
    
    if (logger.isDebugEnabled()) logger.debug("GestoreW3GARA: fine metodo ");

  }

  private void gestioneW3GARAMERC(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("gestioneW3GARAMERC: inizio metodo ");

    AbstractGestoreChiaveNumerica gestoreMultiploW3GARAMERC = new DefaultGestoreEntitaChiaveNumerica("W3GARAMERC", "NUMMERC",
        new String[] { "NUMGARA" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm, gestoreMultiploW3GARAMERC, "W3GARAMERC",
        new DataColumn[] { datiForm.getColumn("W3GARA.NUMGARA") }, null);

    if (logger.isDebugEnabled()) logger.debug("gestioneW3GARAMERC: fine metodo ");

  }

}

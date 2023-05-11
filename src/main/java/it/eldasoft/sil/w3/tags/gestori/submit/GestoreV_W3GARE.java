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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreV_W3GARE extends AbstractGestoreChiaveNumerica {

  static Logger logger = Logger.getLogger(GestoreV_W3GARE.class);

  public GestoreV_W3GARE() {
	    super(false);
  }
  
  @Override
  public String[] getAltriCampiChiave() {
    return null;
  }

  @Override
  public String getCampoNumericoChiave() {
    return "CODICE";
  }

  @Override
  public String getEntita() {
    return "V_W3GARE";
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
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreV_W3GARE: inizio metodo ");

    GeneManager geneManager = this.getGeneManager();
    if (datiForm.isColumn("W3GARA.NUMGARA")) {
    	Long numgara = datiForm.getLong("W3GARA.NUMGARA");
    	geneManager.deleteTabelle(new String[] { "W3GARA", "W3GARAREQ", "W3GARAREQCIG", "W3GARAREQDOC", "W3GARADOC", "W3LOTT", "W3LOTTCATE", "W3LOTTCUP",
    	        "W3LOTTTIPI", "W3GARAMERC" }, "NUMGARA = ?", new Object[] { numgara });
    } else {
    	Long numgara = datiForm.getLong("W3SMARTCIG.CODRICH");
    	geneManager.deleteTabelle(new String[] { "W3SMARTCIG", "W3SMARTCIGMERC"}, "CODRICH = ?", new Object[] { numgara });
    }
    
    if (logger.isDebugEnabled()) logger.debug("GestoreV_W3GARE: fine metodo ");

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

}

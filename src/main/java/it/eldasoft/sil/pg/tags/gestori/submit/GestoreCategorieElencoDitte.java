/*
 * Created on 25/ago/2010
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
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di salvataggio delle categorie d'iscrizione per gli elenchi per
 * operatori economici
 *
 * @author Luca.Giacomazzo
 */
public class GestoreCategorieElencoDitte extends AbstractGestoreChiaveNumerica {

  public GestoreCategorieElencoDitte() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "OPES";
  }

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "NGARA3" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "NOPEGA";
  }

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	  //Long nopega = datiForm.getLong("OPES.NOPEGA");
      String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
      String categoria = datiForm.getString("V_CAIS_TIT.CAISIM");

      PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
          this.getServletContext(), PgManager.class);

      try {
        pgManager.cancellaCategoriaConGerarchia(ngara,categoria,null,"OPES") ;
        return;
      } catch (SQLException e) {
        throw new GestoreException("Errore nella cancellazione della categoria",null,e);
      }
	}

	@Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {


	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	  super.preInsert(status, datiForm);
	  //Si deve controllare che non vi siano altre occorrenze di OPES per l'elenco
	  //con lo stesso codice
	  String ngara = datiForm.getString("OPES.NGARA3");
	  String catoff = datiForm.getString("OPES.CATOFF");
	  String descop = datiForm.getString("OPES.DESCOP");
	  Long nopega = datiForm.getLong("OPES.NOPEGA");
	  try {
        Long numOccorrenze = (Long)this.sqlManager.getObject("select count(NGARA3) from opes where ngara3=? and catoff=?", new Object[]{ngara,catoff});
        if (numOccorrenze!= null && numOccorrenze.longValue()>0){
          throw new GestoreException("La categoria selezionata è già presente nell'elenco","catDuplicata");
        }
        this.sqlManager.update("insert into opes(ngara3,nopega,catoff,descop) values(?,?,?,?)", new Object[]{ngara,nopega,catoff,descop});
        this.getRequest().setAttribute("key",nopega);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento della categoria",null,e);
      }
      //Long nopega = datiForm.getLong("OPES.NOPEGA");

	}

	@Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  String descop = datiForm.getString("OPES.DESCOP");
	  String ngara = datiForm.getString("OPES.NGARA3");
      String catoff = datiForm.getString("OPES.CATOFF");
      Long nopega = datiForm.getLong("OPES.NOPEGA");
	  try {
	      if(datiForm.isModifiedColumn("OPES.CATOFF")){
	        Long numOccorrenze = (Long)this.sqlManager.getObject("select count(NGARA3) from opes where ngara3=? and catoff=? and nopega <> ?", new Object[]{ngara,catoff,nopega});
	        if (numOccorrenze!= null && numOccorrenze.longValue()>0){
	          throw new GestoreException("La categoria selezionata è già presente nell'elenco","catDuplicata");
	        }
	       }
	       this.sqlManager.update("update opes set catoff=?, descop=? where ngara3=? and nopega=?", new Object[]{catoff,descop,ngara,nopega});
	  } catch (SQLException e) {
	     throw new GestoreException("Errore nell'aggiornamento della categoria",null,e);
	  }


	}

	@Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

}
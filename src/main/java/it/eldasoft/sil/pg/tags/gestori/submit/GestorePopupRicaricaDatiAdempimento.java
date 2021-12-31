/*
 * Created on 21-02-2014
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la popup per ricaricare i dati di un adempimento.
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRicaricaDatiAdempimento extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  public GestorePopupRicaricaDatiAdempimento() {
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

	String CIGDuplicatiLotti = StringUtils.stripToNull(
    		UtilityStruts.getParametroString(this.getRequest(), "CIGDuplicatiLotti"));
	String CIGDuplicatiInDB = StringUtils.stripToNull(
    		UtilityStruts.getParametroString(this.getRequest(), "CIGDuplicatiInDB"));
	String operazione = StringUtils.stripToNull(
    		UtilityStruts.getParametroString(this.getRequest(), "operazione"));

	if ((CIGDuplicatiLotti == null && CIGDuplicatiInDB == null) ||
			((CIGDuplicatiLotti != null || CIGDuplicatiInDB != null))) {

	    String id = StringUtils.stripToNull(
	    		UtilityStruts.getParametroString(this.getRequest(), "id"));
	    Long idAnticor = new Long(id);

	    String ufficioIntestatario = null;
	    HttpSession session = this.getRequest().getSession();
	    if (session != null) {
	      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute(
	    		  CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO)));
	    }

	    if (ufficioIntestatario == null)
	      ufficioIntestatario = "*";

	    Long annorif = null;

	    try {
	    	annorif = (Long) this.sqlManager.getObject(
	    			"select annorif from anticor where id=?",
	    				new Object[] { idAnticor });
	    } catch (SQLException e) {
	      this.getRequest().setAttribute("erroreOperazione", "1");
	      throw new GestoreException("Errore nella lettura di ANTICOR.ANNORIF", null, e);
	    }

	    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager", this.getServletContext(), PgManager.class);

	    if ("ricaricaTuttiLotti".equals(operazione)) {
		    try {
		      //Cancellazione di ANTICORLOTTI
		      String delete = "delete from anticorlotti where idanticor=? and ((daannoprec ='1' or daannoprec ='3') " +
		      		"or (daannoprec ='2' and lottoinbo='1')) ";
		      if (CIGDuplicatiLotti != null) {
		        String elencoCig = StringUtils.replace("'".concat(StringUtils.replace(CIGDuplicatiLotti, ",", "','")).concat("'"), " ", "");

		        delete = "delete from anticorlotti where idanticor=? and ((daannoprec ='1' or daannoprec ='3') or (daannoprec ='2' and lottoinbo='1') or " +
		        		"(lottoinbo='2' and cig in (" + elencoCig + ")))";
		      }

		      this.sqlManager.update(delete,  new Object[] {idAnticor });

		    } catch (SQLException e) {
		      this.getRequest().setAttribute("erroreOperazione", "1");
		      throw new GestoreException("Errore nella cancellazione di ANTICORLOTTI e tabelle figlie", null, e);
		    }

		    try {
		      pgManager.insertLottiAdempimento(annorif, null, idAnticor, null, null, ufficioIntestatario, "1",null,true,null);
		    } catch(GestoreException e ) {
		      this.getRequest().setAttribute("erroreOperazione", "1");
		      throw e;
		    }

		    //Import lotti adempimento lotti anno precedente
		    try {
		      pgManager.insertLottiAdempimentoAnnoPrecedente(annorif, ufficioIntestatario, idAnticor);
		      /*
		      HashMap ret= pgManager.insertLottiAdempimentoAnnoPrecedente(annorif, ufficioIntestatario, idAnticor,true);
		      if(ret!=null && ret.get("erroreDuplicazioneCig")!=null){
		        boolean erroreDuplicazioneCig = ((Boolean)ret.get("erroreDuplicazioneCig")).booleanValue();
		        if(erroreDuplicazioneCig){
		          this.getRequest().setAttribute("erroreOperazione", "1");
		          throw new GestoreException("Duplicazione codice CIG fra lotti", "ricaricaAdempimento.cigDupl", new Exception());
		        }
		      }
		      */
		    } catch(GestoreException e ) {
		        this.getRequest().setAttribute("erroreOperazione", "1");
		        throw e;
		    }

		    this.getRequest().setAttribute("operazioneEseguita", "1");
	    } else if ("aggiungiLotti".equals(operazione)) {
	        java.util.Date dataInizio = null;
	        if (annorif.intValue() == 2013) {
	          dataInizio = UtilityDate.convertiData("01/12/2012",
	              UtilityDate.FORMATO_GG_MM_AAAA);
	        } else {
	          dataInizio = UtilityDate.convertiData("01/01/" + annorif.toString(),
	              UtilityDate.FORMATO_GG_MM_AAAA);
	        }
	        java.util.Date dataFine = UtilityDate.convertiData("31/12/" + annorif.toString(),
	            UtilityDate.FORMATO_GG_MM_AAAA);

	        List<?> listaCIG = null;
	        try {
	        	// Delete dei lotti creati a mano (lottoinbo = '2') con cig uguale a quello dei lotti in db
	        	if (CIGDuplicatiLotti != null) {
				  String delete = "delete from anticorlotti where idanticor=? and lottoinbo='2' ";
				  String elencoCig = StringUtils.replace("'".concat(StringUtils.replace(CIGDuplicatiLotti, ",", "','")).concat("'"), " ", "");
				  delete += " and CIG in (" + elencoCig + ")";
				  this.sqlManager.update(delete, new Object[] { idAnticor });
				}

				String select = "select CIG from V_DATI_LOTTI where CIG is not null and CIG not in " +
	        			"(select CIG from ANTICORLOTTI where IDANTICOR=? and CIG is not null) ";
	        	String where = " and (((datpub >= ? and  datpub <= ? "
			         + "or (datpub is null and (dinvit >= ? and  dinvit <= ?))) "
			         + "or (esineg is not null and datneg >= ? and  datneg <= ?) "
			         + "or (dattoa >= ? and  dattoa <= ?)"
			         + "or (dattoa is not null and  ((datainizio >=? and  datainizio <= ?) or (dataultimazione >=? and dataultimazione<=?)))))";

	        	Object[] parametri = new Object[] { idAnticor, dataInizio, dataFine, dataInizio, dataFine, dataInizio, dataFine,
						dataInizio, dataFine, dataInizio, dataFine, dataInizio, dataFine };

	        	if (ufficioIntestatario != null && !"*".equals(ufficioIntestatario)) {
	               	where += " and codiceprop='" + ufficioIntestatario + "'";
	            }

	        	listaCIG = this.sqlManager.getListVector(select + where, parametri );
	        } catch (SQLException e) {
	            //this.getRequest().setAttribute("erroreOperazione", "1");
	            throw new GestoreException(
	                "Errore nella lettura dei dati dei lotti per popolare ANTICORLOTTI ", null, e);
	        }
	    	if (listaCIG != null && listaCIG.size() > 0) {
	    		for (int i=0; i < listaCIG.size(); i++) {
	    		    try {
	    		    	String codiceCig = SqlManager.getValueFromVectorParam(listaCIG.get(i), 0).getStringValue();
	    		    	pgManager.insertLottiAdempimento(annorif, codiceCig, idAnticor, null, null, ufficioIntestatario, "1", null, true, null);
	    		    } catch(GestoreException e ) {
	    		    	this.getRequest().setAttribute("erroreOperazione", "1");
	    		    	throw e;
	    		    }
	    		}
	    	}
	    this.getRequest().setAttribute("operazioneEseguita", "1");
      } else {

      }
	}
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    String operazione = StringUtils.stripToNull(
		UtilityStruts.getParametroString(this.getRequest(), "operazione"));

	if (StringUtils.isNotEmpty(operazione)) {
	  this.getRequest().setAttribute("calcoloCigDuplicati", "false");
	}
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

}

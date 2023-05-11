/*
 * Created on 30-05-2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Gestore preload per la pagina ditg-schedaPopup-insertDaRda.jsp.
 *
 * @author Cristian Febas
 */

public class GestoreInitInsDittaRda extends AbstractGestorePreload {

  private GestioneWSERPManager gestioneWSERPManager = null;
  
  public GestoreInitInsDittaRda(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {

  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",page, SqlManager.class);
    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
            page, GestioneWSERPManager.class);
    
    
    String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");
    codgar = StringUtils.stripToEmpty(codgar);
    
    ProfiloUtente profilo = (ProfiloUtente) page.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    Long syscon = new Long(profilo.getId());
    String profiloAttivo = (String) page.getSession().getAttribute("profiloAttivo");
    String servizio = page.getRequest().getParameter("servizio");
    if(servizio==null || "".equals(servizio))
      servizio ="WSERP";

	try {
		String[]  credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);
	    String username = credenziali[0];
	    String password = credenziali[1];
	    WSERPRdaType erpSearch = new WSERPRdaType();
	    //imposto il num Rda come filtro ed esercizio obbligatorio,per ora metto fisso
	    Vector<?> datiRda = sqlManager.getVector(
                "select numrda,esercizio from GARERDA where CODGAR = ?", new Object[] { codgar });
	      if (datiRda != null && datiRda.size() > 0) {
	    	  String numeroRda = (String) SqlManager.getValueFromVectorParam(datiRda, 0).getValue();
	    	  String tipoRdaErp = (String) SqlManager.getValueFromVectorParam(datiRda, 1).getValue();
	    	  erpSearch.setCodiceRda(numeroRda);
	    	  erpSearch.setTipoRdaErp(tipoRdaErp);
	    	  WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpDettaglioRda(username, password, servizio, erpSearch);
	    	  WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
	    	  if(rdaArray!=null) {
	    		  WSERPRdaType rda = rdaArray[0];
	    		  String codimp = null;
	    		  String nomimp = null;
	    		  String cfimp = null;
	    		  String pivimp = null;
	    		  Vector<?> datiImpr = null;
	    		  String fornitoreIndividuato = rda.getFornitore();
	    		  fornitoreIndividuato = StringUtils.stripToEmpty(fornitoreIndividuato);
	    		  String cfFornitore = rda.getCfFornitore();
	    		  cfFornitore = StringUtils.stripToEmpty(cfFornitore);
	    		  String pivaFornitore = rda.getPivaFornitore();
	    		  pivaFornitore = StringUtils.stripToEmpty(pivaFornitore);

	    		  if(!"".equals(fornitoreIndividuato)) {
		    		  datiImpr = sqlManager.getVector(
		    	                "select codimp,nomimp,cfimp,pivimp from IMPR where cgenimp = ?", new Object[] { fornitoreIndividuato });
		    	      if ((datiImpr != null && datiImpr.size() > 0)) {
		    	    	  codimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
		    	    	  nomimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
		    	    	  cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 2).getValue();
		    	    	  pivimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 3).getValue();
		    	    	  
		    	      }else {
		    	    	  ;//cerco con cfimp
			    		  datiImpr = sqlManager.getVector(
			    	                "select codimp,nomimp,cfimp,pivimp from IMPR where cfimp = ?", new Object[] { cfFornitore });
			    	      if ((datiImpr != null && datiImpr.size() > 0)) {
			    	    	  codimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
			    	    	  nomimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
			    	    	  cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 2).getValue();
			    	    	  pivimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 3).getValue();
			    	      }
		    	    	  
		    	      }
	    		  }else {
	    			  ;//cerco con cfimp
		    		  datiImpr = sqlManager.getVector(
		    	                "select codimp,nomimp,cfimp,pivimp from IMPR where cfimp = ?", new Object[] { cfFornitore });
		    	      if ((datiImpr != null && datiImpr.size() > 0)) {
		    	    	  codimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
		    	    	  nomimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
		    	    	  cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 2).getValue();
		    	    	  pivimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 3).getValue();
		    	      }
	    		  }
	    		  
	    		  if(codimp!=null) {
	    	    	  Long countFornitore = (Long) sqlManager.getObject(
		    	                "select count(*) from DITG where codgar5 = ? and ngara5 = ? and dittao = ?", new Object[] { codgar,ngara,codimp });
	    	    	  if(Long.valueOf(0).equals(countFornitore)) {
			    		  page.setAttribute("dittao", codimp, PageContext.REQUEST_SCOPE);
			    		  page.setAttribute("nomimo", nomimp, PageContext.REQUEST_SCOPE);
			    		  page.setAttribute("cfFornitore", cfimp, PageContext.REQUEST_SCOPE);
			    		  page.setAttribute("pivaFornitore", pivimp, PageContext.REQUEST_SCOPE);
	    	    		  
	    	    	  }else {
		    	    	  page.setAttribute("isFornitoreRda", "false", PageContext.REQUEST_SCOPE);
		    	    	  page.setAttribute("msgFornitoreRda", "Il fornitore presente in rda risulta gia' inserito", PageContext.REQUEST_SCOPE);
	    	    	  }
	    			  
	    		  }else {
	    	    	  page.setAttribute("isFornitoreRda", "false", PageContext.REQUEST_SCOPE);
	    	    	  page.setAttribute("msgFornitoreRda", "Il fornitore non risulta presente in rda oppure non corrisponde ad una anagrafica in banca dati", PageContext.REQUEST_SCOPE);
	    		  }
	    		  
	    	  }//rdaArray not null
	    	  
	      }//datiRda not null
		
		
	} catch (GestoreException e) {
		throw new JspException("Errore durante l'estrazione dei dati del fornitore", e);
	} catch (SQLException e) {
		 throw new JspException("Errore durante l'estrazione dei dati del fornitore", e);
	}

  }

}

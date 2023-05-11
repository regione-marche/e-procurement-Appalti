/*
 * Created on 08-10-2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.xml.rpc.ServiceException;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;
import it.maggioli.eldasoft.ws.erp.WSERP_ServiceLocator;

/**
 * Funzione che estrae i dati delle rda (o delle rda) collegate alla gara
 * @author Cristian Febas
 */
public class GetDatiRdaGaraFunction extends AbstractFunzioneTag {
	
  private static final String PROP_WSERP_URL            = "wserp.erp.url";

  public  GetDatiRdaGaraFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    
    GestioneWSERPManager gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager", pageContext, GestioneWSERPManager.class);

    String ngara = (String) params[1];
    String isRda = "0";
    Long genere = null;
    String codiceGara = null;
    List<?> listaRdaGara = null;
   
    try {
    	
        Vector<?> datiGara= sqlManager.getVector("select genere,codgar  from v_gare_genere where codice=?", new Object[]{ngara});
        if (datiGara != null) {
          genere = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
          codiceGara = SqlManager.getValueFromVectorParam(datiGara, 1).getStringValue();
        }
        if(Long.valueOf(300).equals(genere)) {
        	//si tratta di un lotto della gara
    	    listaRdaGara = sqlManager.getListVector(
    	            "select numrda,esercizio from garerda where codgar = ? and ngara = ?", new Object[] { codiceGara,ngara });
        }else {
    	    listaRdaGara = sqlManager.getListVector(
    	            "select numrda,esercizio from garerda where codgar = ?", new Object[] { codiceGara });
        }
	    
	    if(listaRdaGara.size()==1) {
	    	isRda = "1";
	    	Vector vect = (Vector) listaRdaGara.get(0);
	    	String codicerda = (String) SqlManager.getValueFromVectorParam(listaRdaGara.get(0), 0).getValue();
	    	String tiporda = (String) SqlManager.getValueFromVectorParam(listaRdaGara.get(0), 1).getValue();
	    	
	        String url = ConfigManager.getValore(PROP_WSERP_URL);
	        ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
	            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
	        Long syscon = new Long(profilo.getId());
	        String[] credenziali = gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");

	        String utente = credenziali[0];
	        String password = credenziali[1];
	        WSERP_ServiceLocator wserp_ServiceLocator = new WSERP_ServiceLocator();
	        wserp_ServiceLocator.setWSERPImplPortEndpointAddress(url);
	    	
	        WSERPRdaType input = new WSERPRdaType();

	        input.setTipoRdaErp(tiporda);
	        input.setCodiceRda(codicerda);

	        WSERPRdaResType output = wserp_ServiceLocator.getWSERPImplPort().WSERPDettaglioRda(utente, password, input);
	        
	        WSERPRdaType[] rdaArray = output.getRdaArray();
	        if(rdaArray!=null && rdaArray.length==1) {
	        	WSERPRdaType rda = rdaArray[0];
	        	String tipoContrattoRda = rda.getTipologia();
	        	this.getRequest().setAttribute("tipoContrattoRda", tipoContrattoRda);
	        }
	 
	    }

		
	} catch (SQLException e) {
		throw new JspException("Errore durante la lettura dei dati delle rda della gara", e);		
	} catch (GestoreException e) {
		throw new JspException("Errore durante il recupero delle credenziali per il s.o.", e);		
	} catch (RemoteException e) {
		throw new JspException("Errore nell'invocazione del ws remoto erp", e);		
	} catch (ServiceException e) {
		throw new JspException("Errore nell'invocazione del servizio erp", e);
	}
    
    return isRda;
  }
}

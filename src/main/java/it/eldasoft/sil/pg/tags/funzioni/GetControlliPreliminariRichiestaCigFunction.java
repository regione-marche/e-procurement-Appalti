/*
 * Created on 19/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AnagraficaSimogManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Controlli preliminari
 * ....
 * @author ristian.Febas
 */
public class GetControlliPreliminariRichiestaCigFunction extends AbstractFunzioneTag {

  public GetControlliPreliminariRichiestaCigFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String ngara = (String) params[1];
    String codiceGara = (String) params[2];
    String genereGara = (String) params[3];
    String idStipula = (String) params[4];
    
    Double valmax = null;
    String tipoGara = null;
    String idGara =  null;
    Double impstipula = null;

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    AnagraficaSimogManager anagraficaSimogManager = (AnagraficaSimogManager) UtilitySpring.getBean("anagraficaSimogManager", pageContext, AnagraficaSimogManager.class);

    ArrayList<String> controlliPreliminari = new ArrayList<String>();
    
    try {
    	
    	if(idStipula != null) {
    		
    		//Alla sua attivazione, viene visualizzato un messaggio di avviso bloccante se per la variante non 
    		//c'è un'anagrafica simog collegata (NGARAVAR.G1STIPULA nullo oppure non 
    		//c'è occ. in W3GARA o W3SMARTCIG associata) e il codice CIG collegato è valorizzato (CIGVAR.G1STIPULA valorizzato).
    		
    		Long numgara = null;
        	if (!"".equals(codiceGara) && codiceGara!=null) {
        		numgara = (Long) sqlManager.getObject("select numgara from w3gara where codgar = ? and stato_simog <> 6", new Object[]{codiceGara});
        		if(numgara == null) {
        			numgara = (Long) sqlManager.getObject("select codrich from w3smartcig where codgar = ? and stato <> 6", new Object[]{codiceGara});
        		}
        	}
        	impstipula = (Double) sqlManager.getObject("select impstipula from g1stipula where id = ?", new Object[]{idStipula});
        	String cigvar = (String) sqlManager.getObject("select cigvar from g1stipula where id = ?", new Object[]{idStipula});
        	if(("".equals(codiceGara) || codiceGara==null || numgara==null) && (cigvar != null))
    		controlliPreliminari.add("Il CIG collegato della stipula risulta valorizzato");
    	}
    	if(codiceGara!=null && !"".equals(codiceGara)) {
    		
    	genereGara = StringUtils.stripToEmpty(genereGara);
    	
    	Boolean isAnagraficaModificabile = false;
    	Long numgara = (Long) sqlManager.getObject("select numgara from w3gara where codgar = ? and stato_simog <> 6", new Object[]{codiceGara});
    	if(numgara == null) {
    		numgara = (Long) sqlManager.getObject("select codrich from w3smartcig where codgar = ? and stato <> 6", new Object[]{codiceGara});
    		if(numgara!=null) {
    			//la gara deve essere in uno stato modificabile
    			isAnagraficaModificabile =anagraficaSimogManager.isW3SMARTCIG_Modificabile(codiceGara);
    		}else {
	            // Verifico la presenza NUMAVCP e di eventuali CIG nei lotti
	    		String numavcp  = (String) sqlManager.getObject("select numavcp from torn where codgar = ?", new Object[]{codiceGara});
            	if(numavcp!=null) {
            		controlliPreliminari.add("Il numero ANAC della gara risulta valorizzato");
            	}
	            if("2".equals(genereGara)){
	            	String codcig  = (String) sqlManager.getObject("select codcig from torn,gare where codgar=codgar1 and codgar1 = ?", new Object[]{codiceGara});
	            	if(codcig!=null) {
	            		controlliPreliminari.add("Il CIG della gara risulta valorizzato");
	            	}
	            }else{
	                // cig lotti
	            	controlliPreliminari.addAll(anagraficaSimogManager.getControlloCigGara(codiceGara, ngara, genereGara));
	            }
	            
	    		pageContext.setAttribute("controlliPreliminari", controlliPreliminari);
    			
    		}
    	}else {
            //la gara deve essere in uno stato modificabile
            Long stato_simog = (Long) sqlManager.getObject(
         	          "select stato_simog from w3gara where codgar = ? and stato_simog <> 6", new Object[] {codiceGara});
            isAnagraficaModificabile = anagraficaSimogManager.isSTATO_SIMOGModificabile(stato_simog);
    	}
    	if(numgara != null) {
    		Vector<?> datiW3 =sqlManager.getVector("select tipo_gara,id_gara from v_w3gare where codgar = ? and stato_simog <> 6", new Object[]{codiceGara});
            if (datiW3 != null && datiW3.size() > 0){
            	tipoGara = SqlManager.getValueFromVectorParam(datiW3, 0).stringValue();
                idGara = SqlManager.getValueFromVectorParam(datiW3, 1).stringValue();
            }
    	}else {
	    	genereGara =  StringUtils.stripToEmpty(genereGara);
	    	if("2".equals(genereGara)){
		    	Object valmaxObj = (Object) sqlManager.getObject("select valmax from v_gare_importi where codgar = ?", new Object[]{codiceGara});
		        if (valmaxObj != null) {
		            if (valmaxObj instanceof Long)
		              valmax = new Double(((Long) valmaxObj));
		            else if (valmaxObj instanceof Double)
		              valmax = new Double((Double) valmaxObj);
		          }
	    	}
	    	
	    	if(idStipula != null) {
	    		if(impstipula!= null && impstipula > 40000){
	    			pageContext.setAttribute("isSmartCig", "false", PageContext.REQUEST_SCOPE);
	    		}
	    	}
	    	else{
	    		if(!"2".equals(genereGara) || (valmax!= null && valmax > 40000)){
	    		pageContext.setAttribute("isSmartCig", "false", PageContext.REQUEST_SCOPE);
	    	}}
    	}
    	
    	if(numgara != null) {
    		String numavcp  = (String) sqlManager.getObject("select numavcp from torn where codgar = ?", new Object[]{codiceGara});
    		Long ccodcig  = (Long) sqlManager.getObject("select count(*) from gare where codgar1 = ? and codcig is not null", new Object[]{codiceGara});
    	
    		if (numavcp != null || ccodcig > 0) {
    			Boolean isgaraanacocodcig = true;
    			pageContext.setAttribute("isgaraanacocodcig", isgaraanacocodcig, PageContext.REQUEST_SCOPE);
    		}
    	
    	}
    	tipoGara = StringUtils.stripToEmpty(tipoGara);
    	idGara = StringUtils.stripToEmpty(idGara);
    	pageContext.setAttribute("isAnagraficaGara", tipoGara, PageContext.REQUEST_SCOPE);
    	pageContext.setAttribute("idGara", idGara, PageContext.REQUEST_SCOPE);
    	pageContext.setAttribute("isAnagraficaModificabile", isAnagraficaModificabile, PageContext.REQUEST_SCOPE);
	    	
    	}
		
	} catch (SQLException e) {
        throw new JspException(
                "Errore nella lettura del RUP e delle collaborazioni associate", e);
	} catch (GestoreException ge) {
		throw new JspException("Errore nella determinazione dello stato dell'anagrafica", ge);
	}
    
    pageContext.setAttribute("controlliPreliminari", controlliPreliminari, PageContext.REQUEST_SCOPE);

    return null;
  }

}
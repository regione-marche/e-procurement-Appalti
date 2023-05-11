/*
 * Created on 21-09-2011
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina scheda delle categorie degli elenchi
 * 
 * @author Marcello.Caminiti
 */
public class GestoreInizializzazioneCategorieElencoDitte extends AbstractGestorePreload {

  public GestoreInizializzazioneCategorieElencoDitte(BodyTagSupportGene tag) {
    super(tag);
  }

  
  public void doBeforeBodyProcessing(PageContext pageContext,
      String modoAperturaScheda) throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	          pageContext, SqlManager.class);
        
	  HashMap key = UtilityTags.stringParamsToHashMap(
              (String) pageContext.getAttribute(
                  UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),
              null);
	  
    if (!"NUOVO".equals(modoAperturaScheda)) {
        String codiceCategoria = null;
        String select="";
        try {
            if(key.get("V_CAIS_TIT.CAISIM")!=null)
              codiceCategoria = ((JdbcParametro) key.get("V_CAIS_TIT.CAISIM")).getStringValue();
            else{
        	  String ngara= ((JdbcParametro) key.get("OPES.NGARA3")).getStringValue();
        	  String nopega = ((JdbcParametro) key.get("OPES.NOPEGA")).getStringValue();
        	  select="select catoff from opes where ngara3= ? and nopega = ?";
        	  codiceCategoria = (String)sqlManager.getObject(select, new Object[]{ngara,nopega});
            }
      	  
            select="select DESCAT,TIPLAVG,TITCAT from V_CAIS_TIT where CAISIM = ?";
    	
    		Vector datiV_CAIS_TIT = sqlManager.getVector(select,
    	            new Object[] { codiceCategoria });
    		
    		 if(datiV_CAIS_TIT != null && datiV_CAIS_TIT.size() > 0){
    			 
    			 String descat= ((JdbcParametro) datiV_CAIS_TIT.get(0)).getStringValue();
    			 
    			 Long tiplavg = ((JdbcParametro) datiV_CAIS_TIT.get(1)).longValue();
    			 
    			 String titcat = ((JdbcParametro) datiV_CAIS_TIT.get(2)).getStringValue();
    			 
    			 pageContext.setAttribute("descat",descat,
 	                    PageContext.REQUEST_SCOPE);
    			 
    			 pageContext.setAttribute("tiplavg",tiplavg,
                     PageContext.REQUEST_SCOPE);
    			 
    			 pageContext.setAttribute("titcat",titcat,
                     PageContext.REQUEST_SCOPE);
    		 }
    		
    		
    	}catch (SQLException e) {
            throw new JspException(
                    "Errore durante le inizializzazioni della categoria", e);
        } catch (GestoreException e) {
          throw new JspException(
              "Errore durante le inizializzazioni della categoria", e);
      }
    	
    }

  }

  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }
  
  
}
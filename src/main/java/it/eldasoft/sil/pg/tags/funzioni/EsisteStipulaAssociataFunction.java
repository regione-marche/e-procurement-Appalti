/*
 * Created on 04-01-2022
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione che controlla se alla gara è stata associata una stipula
 *
 * @author Peruzzo Riccardo
 */
public class EsisteStipulaAssociataFunction extends AbstractFunzioneTag {

  public EsisteStipulaAssociataFunction() {
    super(3 , new Class[] { PageContext.class, String.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esisteStipulaGara= "false";
    
    String contestoSimog = (String)params[1];
    String codice = (String)params[2];

    String selOccGarecont = "select gc.ngara, gc.ncont"
    		+ " from gare ga, garecont gc"
    		+ " where gc.codimp = ga.ditta"
    		+ " and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))"
    		+ " and ga.ngara = ? and ga.ditta = ? ";
    
    String selOccStipula = "select codstipula from g1stipula where (ngara = ?) and ncont = ?";
    
    String selDitta = "select ditta from gare where ngara = ?";
    		
    String selectGara = "select count(*) from g1stipula where (ngara in (select ngara from gare where codgar1 = ? "
    		+ "and ngara <> codgar1)) or (ngara=?)";
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
    	
        if("GARA".equals(contestoSimog)) {
        	//Lotto singolo
        	if(codice.indexOf("$")>=0) {
          		//Verifico se il lotto ha una stipula collegata
        		String ngara = codice.substring(1);
           	    Long contStipule = (Long)sqlManager.getObject(selectGara,new Object[]{ngara,ngara});
           	    if(contStipule>new Long(0)) {
           	        esisteStipulaGara= "true";
           	    }
            }
        	else {
        	//Estraggo i lotti
        	//Verifico se ha una stipula collegata
        		String ngara = codice;
           	    Long contStipule = (Long)sqlManager.getObject(selectGara,new Object[]{ngara,ngara});
           	    if(contStipule>new Long(0)) {
           	        esisteStipulaGara= "true";
           	    }
            }
        }else {
      	  if("LOTTO".equals(contestoSimog)) {
      		//Verifico se il lotto ha una stipula collegata
          	String ditta = (String)sqlManager.getObject(selDitta,new Object[]{codice});
          	codice = StringUtils.stripToEmpty(codice);
          	ditta = StringUtils.stripToEmpty(ditta);
          	if(!"".equals(codice) && !"".equals(ditta)) {
        		Vector<?> datiOccGarecont = sqlManager.getVector(selOccGarecont, new Object[] {codice,ditta});
        		if (datiOccGarecont != null && datiOccGarecont.size() > 0) {
       	          String ngaraGarecont = (String) SqlManager.getValueFromVectorParam(datiOccGarecont, 0).getValue();
       	          Long ncontGarecont = (Long) SqlManager.getValueFromVectorParam(datiOccGarecont, 1).getValue();
       	          String codStipula = (String)sqlManager.getObject(selOccStipula,new Object[]{ngaraGarecont,ncontGarecont});
       	          codStipula = StringUtils.stripToEmpty(codStipula);
       	          if(!"".equals(codStipula)) {
       	        	esisteStipulaGara= "true";
       	          }
        		}
        	}
      		  
      	  }
        }
          
      
    } catch (SQLException e) {
      throw new JspException("Errore nella verifica della presenza della stipula per la gara)",e);
    }
    return esisteStipulaGara;
  }

}

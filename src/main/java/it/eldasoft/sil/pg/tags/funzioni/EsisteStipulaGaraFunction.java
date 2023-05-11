/*
 * Created on 31-05-2021
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione che controlla se alla gara è stata associata una stipula
 *
 * @author Cristian Febas
 */
public class EsisteStipulaGaraFunction extends AbstractFunzioneTag {

  public EsisteStipulaGaraFunction() {
    super(3 , new Class[] { PageContext.class, String.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esisteStipulaGara= "false";
    String ngara = (String) params[1];
    String ditta = (String) params[2];

    String selOccGarecont = "select gc.ngara, gc.ncont, ga.codcig"
    		+ " from gare ga, garecont gc"
    		+ " where gc.codimp = ga.ditta"
    		+ " and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))"
    		+ " and ga.ngara = ? and ga.ditta = ? ";
    
    String selOccStipula = "select codstipula from g1stipula where ngara = ? and ncont = ?";
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
    	
    	ngara = StringUtils.stripToEmpty(ngara);
    	ditta = StringUtils.stripToEmpty(ditta);
    	if(!"".equals(ngara) && !"".equals(ditta)) {
    		Vector<?> datiOccGarecont = sqlManager.getVector(selOccGarecont, new Object[] {ngara,ditta});
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
          
      
    } catch (SQLException e) {
      throw new JspException("Errore nella verifica della presenza della stipula per la gara)",e);
    }
    return esisteStipulaGara;
  }

}

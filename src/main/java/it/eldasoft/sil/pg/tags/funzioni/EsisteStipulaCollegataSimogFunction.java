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
public class EsisteStipulaCollegataSimogFunction extends AbstractFunzioneTag {

  public EsisteStipulaCollegataSimogFunction() {
    super(3 , new Class[] { PageContext.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esisteStipulaCollegataSimog= "false";
    
    String idStipula = (String)params[1];
    
    String selNgaravar = "select ngaravar from g1stipula where id = ?";
    
    String selV_W3GARE = "select count(*) from V_W3GARE where codice_gara = ?";
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
    	
    	String ngaravar = (String)sqlManager.getObject(selNgaravar,new Object[]{idStipula});
    	Long countV_W3GARE = new Long(0);
    	
    	if (!"".equals(ngaravar) && ngaravar != null) {
    		countV_W3GARE = (Long)sqlManager.getObject(selV_W3GARE,new Object[]{ngaravar});
    		if (countV_W3GARE>0)
    			esisteStipulaCollegataSimog= "true";
    	}
    	
    } catch (SQLException e) {
      throw new JspException("Errore nella verifica dell'anagrafica Simog della gara collegata alla stipula)",e);
    }
    return esisteStipulaCollegataSimog;
  }

}

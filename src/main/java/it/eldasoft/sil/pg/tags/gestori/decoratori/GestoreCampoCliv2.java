/*
 * Created on 27/Oct/21
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato.SqlSelect;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per il campo TORN.CLIV2
 *
 * @author Peruzzo Riccardo
 */
public class GestoreCampoCliv2 extends AbstractGestoreCampoTabellato {
	
  public GestoreCampoCliv2() {
    super(false, "N12");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {

	 
	SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
				this.getPageContext(), SqlManager.class); 
	PageContext page= this.getPageContext();
	HashMap<?, ?> datiRiga = (HashMap<?, ?>) this.getPageContext().getAttribute("datiRiga",
		    PageContext.REQUEST_SCOPE);

 	SqlSelect sqlSelect = null;

	if (datiRiga!=null) {

    	String uffint = (String) page.getSession().getAttribute("uffint");
    	uffint = StringUtils.stripToEmpty(uffint);

    	String selectUtenti="";

    	if (uffint != null && !"".equals(uffint)) {
    		selectUtenti = "select u.syscon,"
                + sqlManager.getDBFunction("concat",  new String[] {sqlManager.getDBFunction("concat",  new String[] {"u.sysute" , "' - '" }) , sqlManager.getDBFunction("inttostr",  new String[] {"u.syscon"})})
                + " as descr" 	
    			+ " from usrsys u"
    			+ " join usr_ein e on e.syscon=u.syscon"
    			+ " where e.codein = ? and (u.sysdisab is null or u.sysdisab = '0')"
    			+ " UNION"
    			+ " select u.syscon,"
    			+ sqlManager.getDBFunction("concat",  new String[] {sqlManager.getDBFunction("concat",  new String[] {"u.sysute" , "' - '" }) , sqlManager.getDBFunction("inttostr",  new String[] {"u.syscon"})})
                + " as descr"
    			+ " from usrsys u where u.syspwbou LIKE '%ou89%' and (u.sysdisab is null or u.sysdisab = '0')"			
       			+ " order by descr";
    		sqlSelect = new SqlSelect(selectUtenti, new Object[] {uffint});
    	}
    	else {
    		selectUtenti = "select u.syscon,"
                    + sqlManager.getDBFunction("concat",  new String[] {sqlManager.getDBFunction("concat",  new String[] {"u.sysute" , "' - '" }) , sqlManager.getDBFunction("inttostr",  new String[] {"u.syscon"})})
                    + " as descr" 	
    				+ " from usrsys u"
    				+ " where u.sysdisab is null or u.sysdisab = '0'"
    				+ " order by descr";
    		sqlSelect = new SqlSelect(selectUtenti);
    	}

    }

    return sqlSelect;
  }

}

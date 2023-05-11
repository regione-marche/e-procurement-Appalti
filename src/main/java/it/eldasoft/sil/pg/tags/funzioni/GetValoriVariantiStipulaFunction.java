/*
 * Created on 19/08/2021
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
import java.sql.SQLException;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

public class GetValoriVariantiStipulaFunction extends AbstractFunzioneTag {

  public GetValoriVariantiStipulaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
	String codice = (String) params[1];
	codice = StringUtils.stripToEmpty(codice);
    String ncontStr = (String) params[2];
    ncontStr = StringUtils.stripToEmpty(ncontStr);
    Long ncont = null;
    if(!"".equals(ncontStr)) {
    	ncont = Long.valueOf(ncontStr);	
    }

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    List<?> listaVariantiStipula = null;
    String ret = "0";

    try {
      if(!"".equals(codice)){
    	Long countVariantiStipula = (Long) sqlManager.getObject("select count(*) from g1stipula where ngara=? and ncont=? and id_padre is not null", new Object[] {codice,ncont});
    	if(Long.valueOf(0)< countVariantiStipula) {
            String selectVariantiStipula = "select id, ngara, codstipula, oggetto, impstipula, tiatto, nrepat, daatto, u1.sysute, u2.sysute, stato" +
            		" from g1stipula" +
            		" left join usrsys u1 on g1stipula.syscon=u1.syscon" +
            		" left join usrsys u2 on g1stipula.assegnatario=u2.syscon"+
            		" where ngara=? and ncont=? and id_padre is not null";
            listaVariantiStipula = sqlManager.getListVector(selectVariantiStipula, new Object[] {codice,ncont});
            ret = "1";
    	}
      }
      pageContext.setAttribute("listaVariantiStipula", listaVariantiStipula, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della tabella G1STIPULA", e);
    }

    return ret;
  }

}

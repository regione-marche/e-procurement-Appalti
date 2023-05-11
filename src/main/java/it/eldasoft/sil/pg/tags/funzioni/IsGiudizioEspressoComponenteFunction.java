/*
 * Created on 14/set/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;


public class IsGiudizioEspressoComponenteFunction extends AbstractFunzioneTag {

  public IsGiudizioEspressoComponenteFunction() {
    super(2, new Class[] { PageContext.class, JdbcParametro.class });
  }

  @Override
  public String function(PageContext context, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", context, SqlManager.class);
    String res = "false";
    
    Long idComponente = null;
    JdbcParametro idComponentePar = (JdbcParametro) params[1];
    if(idComponentePar != null){
      idComponente = (Long) idComponentePar.getValue();
      
      try {
        Long count = (Long) sqlManager.getObject("select count(*) from g1crivalcom where idgfof = ?", new Object[]{idComponente});
        if(count != null && count.intValue()>0){
          res = "true";
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura delle valutazioni di gara", e);
      }
    }
    return res;
  }

}

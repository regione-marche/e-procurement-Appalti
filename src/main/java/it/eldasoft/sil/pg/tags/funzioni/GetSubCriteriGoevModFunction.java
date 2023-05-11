/*
 * Created on 25/set/2018
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
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;


public class GetSubCriteriGoevModFunction extends AbstractFunzioneTag {

  public GetSubCriteriGoevModFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String key = (String) params[0];
    String id = key.substring(key.indexOf(":")+1);
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    List<?> subcriteriGoevmod = null;

    try {
      if(id!=null){
        Long idcrimod = null;
        Long necvan = null;
        String selectPadre = "select idcrimod, necvan from goevmod where id = ?";
        Vector<JdbcParametro> datiPadre = sqlManager.getVector(selectPadre, new Object[] { id });
        if(datiPadre!=null && datiPadre.size()>0){
          idcrimod = (Long) (datiPadre.get(0)).getValue();
          necvan = (Long) (datiPadre.get(1)).getValue();
        }
        
        String selectGOEVMOD = "select maxpun, despar, livpar, necvan1, id, norpar1 " +
                "from goevmod where goevmod.idcrimod = ? and goevmod.necvan1 = ? and livpar = 2 order by norpar1";
        subcriteriGoevmod = sqlManager.getListVector(selectGOEVMOD, new Object[] { idcrimod, necvan });
      }
      pageContext.setAttribute("subcriteriGoevmod", subcriteriGoevmod, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della tabella GOEVMOD", e);
    }

    return null;
  }

}
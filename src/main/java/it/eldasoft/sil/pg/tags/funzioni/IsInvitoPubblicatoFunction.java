/*
 * Created on 25/giu/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class IsInvitoPubblicatoFunction extends AbstractFunzioneTag {

  public IsInvitoPubblicatoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
    // TODO Auto-generated constructor stub
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String codiceGara = (String)params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    try {
      Long count = (Long) sqlManager.getObject("select count(*) from pubbli where (tippub = 13 or tippub = 23) and codgar9 = ?", new Object[]{codiceGara});
      if(count != null && count.intValue()>0){
        return "true";
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle pubblicazioni di gara", e);
    }
    return "false";
  }

}

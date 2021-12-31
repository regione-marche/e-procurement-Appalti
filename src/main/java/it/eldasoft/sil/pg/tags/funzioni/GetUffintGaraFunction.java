/*
 * Created on 17/feb/2020
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


public class GetUffintGaraFunction extends AbstractFunzioneTag{

  public GetUffintGaraFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String codgar = (String) params[1];
    
    try {
      String select = "select cenint from torn where codgar = ?";
      String cenint = (String)sqlManager.getObject(select, new Object[]{codgar});
      
      return cenint;

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del codice dell'ufficio intestatario", e);
    }
  }
  
  
}

/*
 * Created on 12/06/14
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

public class GetCfDRPFunction extends AbstractFunzioneTag {

  public GetCfDRPFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];

    String cftec = "";
    if (codgar != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {

        cftec = (String) sqlManager.getObject("select cftec from torn left join tecni on codtec=coddrp where codgar = ?",
                new Object[] {codgar});
      } catch (SQLException e) {
        throw new JspException("Errore durante il recupero del codice fiscale del soggetto incaricato dell'invio delle schede ANAC", e);
      }
    }
    return cftec;
  }

}
/*
 * Created on 27/09/21
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;
import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;


public class EsisteDocumentoMDGUEFunction extends AbstractFunzioneTag {

  public EsisteDocumentoMDGUEFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String gruppo = (String) params[2];
    String esisteDocumentoDGUE = "false";
    if (codgar != null && gruppo !=null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String select = "select count(*) from documgara where codgar = ? and gruppo =? and idstampa='DGUE' and (isarchi ='2' or isarchi is null)";
        Long conteggio = (Long) sqlManager.getObject(select, new Object[] {codgar, new Long(gruppo)});

        if (conteggio != null && conteggio.longValue() > 0) {
          esisteDocumentoDGUE = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il conteggio dei documenti DGUE", e);
      }
    }

    return esisteDocumentoDGUE;
  }

}
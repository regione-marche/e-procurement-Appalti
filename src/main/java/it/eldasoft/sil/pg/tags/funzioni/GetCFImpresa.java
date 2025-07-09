/*
 * Created on 21/08/24
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
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

/**
 * Funzione che recupera il codice fiscale di un'impresa
 *
 * @author Manuel Bridda
 */
public class GetCFImpresa extends AbstractFunzioneTag {

  public GetCFImpresa() {
    super(2, new Class[] { PageContext.class, String.class});
  }


  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codimp = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    if (StringUtils.isNotBlank(codimp)) {
      try {

        return (String)sqlManager.getObject("select cfimp from impr where codimp=?", new Object[]{codimp});

        }catch (SQLException e) {
        throw new JspException("Errore durante la verifica il recupero del codice fiscale dell'impresa "+codimp, e);
      }
    }
    return "";
  }

}
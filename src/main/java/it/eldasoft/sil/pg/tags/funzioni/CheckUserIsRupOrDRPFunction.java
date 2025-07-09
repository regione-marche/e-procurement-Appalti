/*
 * Created on 12-10-2012
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

public class CheckUserIsRupOrDRPFunction extends AbstractFunzioneTag {

  public CheckUserIsRupOrDRPFunction() {
    super(2 , new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
          throws JspException {

    String codgar = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            pageContext, SqlManager.class);

    ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String cf=profiloUtente.getCodiceFiscale();
    if(StringUtils.isNotEmpty(cf)){
      try {
        Long count = (Long) sqlManager.getObject("select count(*) from torn join tecni on (coddrp=codtec or codrup=codtec) where cftec=? and codgar=?",
                new Object[]{cf,codgar});
        if(count>0)
          return "true";
      } catch (SQLException e) {
        throw new JspException("Errore nella verifica dell'abilitazione dell'utente come RUP o DRP",e);
      }
    }
    return "false";
  }

}

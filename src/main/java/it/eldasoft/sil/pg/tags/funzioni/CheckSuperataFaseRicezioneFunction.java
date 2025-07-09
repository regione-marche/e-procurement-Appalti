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

public class CheckSuperataFaseRicezioneFunction extends AbstractFunzioneTag {

  public CheckSuperataFaseRicezioneFunction() {
    super(2 , new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
          throws JspException {

    String codgar = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            pageContext, SqlManager.class);

      try {
        Long count = (Long) sqlManager.getObject("select count(*) from gare g join v_gare_genere v on g.codgar1=v.codgar join torn t on g.codgar1=t.codgar where (g.fasgar is null or g.fasgar <=1) and (v.genere in (1,2) or (v.genere=3 and g.codgar1=g.ngara)) and t.gartel='1' and g.esineg is null and g.codgar1=?",
                new Object[]{codgar});
        if(count>0)
          return "false";
      } catch (SQLException e) {
        throw new JspException("Errore nella lettura del fasgar della tornata",e);
      }
    return "true";
  }

}

/*
 * Created on 11/07/17
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ControlloImportiNulliDaCriteriFunction extends AbstractFunzioneTag {

  public ControlloImportiNulliDaCriteriFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];

    String esitoControllo = "ok";

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      String select="select count(c.id) from g1cridef c, goev g where c.ngara=? and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = 2";
      try {
        Long totaleCriteri = (Long) sqlManager.getObject(select, new Object[] {ngara});
        select="select count(c.id) from g1cridef c, goev g where c.ngara=? and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = 2 and c.formato=100";
        Long totaleFormato100 = (Long) sqlManager.getObject(select, new Object[] {ngara});
        if(totaleCriteri!=null && totaleCriteri.equals(totaleFormato100)){
          select="select count(ngara5) from ditg where ngara5=? and impoff is null and (fasgar is null or fasgar > 6)";
          Long conteggioDitte = (Long) sqlManager.getObject(select, new Object[] {ngara});
          if(conteggioDitte!=null && conteggioDitte.longValue()>0)
            esitoControllo = "nok";
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo sulla valorizzazione degli importi offerti dalle ditte", e);
      }
    }

    return esitoControllo;
  }

}
/*
 * Created on 06-Giu-2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetIsRupFunction extends AbstractFunzioneTag {

  public GetIsRupFunction() {
    super(1, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

      Long abilitazioni = null;

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      
      ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      
      String selectQuery = "";

      try {
        selectQuery =  "select count(*) from w9loader_appalto_usr where syscon = ?";
        abilitazioni = (Long) sqlManager.getObject(selectQuery, new Object[] {profilo.getId()});
      } catch (SQLException s) {
        throw new JspException(
            "Errore durante la verifica dell'abilitiazione", s);
       }
       if (abilitazioni == null) abilitazioni = new Long(0);
       
       pageContext.setAttribute("isAbilitato", abilitazioni>0,
           PageContext.REQUEST_SCOPE);
      return null;
  }

}
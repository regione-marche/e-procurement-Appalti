/*
 * Created on 06/07/2015
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class IsAlboCommissioneCollegatoFunction extends AbstractFunzioneTag {

  public IsAlboCommissioneCollegatoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String codiceGara = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);

    String tipologiaGara = null;

    String isAlboCommissioneCollegato = "false";

    if (codiceGara != null) {
      try {
        if(pageContext.getRequest().getParameter("tipologiaGara") != null){
          tipologiaGara = pageContext.getRequest().getParameter("tipologiaGara");
        }else{
          tipologiaGara = (String) pageContext.getAttribute("tipologiaGara", PageContext.REQUEST_SCOPE);
        }
        if(tipologiaGara != null && "3".equals(tipologiaGara)){
          ;
        }else{
          try {
            codiceGara = (String) sqlManager.getObject("select codgar1 from gare where ngara = ? ", new Object[] {codiceGara});
          } catch (SQLException e) {
            throw new JspException("Errore durante la lettura del codice gara", e);          }
        }

        String selectTORN = "select idcommalbo from torn where codgar = ?";
        Long idCommAlbo = (Long) sqlManager.getObject(selectTORN, new Object[] {codiceGara});

        if (idCommAlbo != null) {
          isAlboCommissioneCollegato = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante la verifica del collegamento ad albo per commissione", e);
      }
    }

    return isAlboCommissioneCollegato;
  }

}
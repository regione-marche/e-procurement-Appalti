/*
 * Created on 14/nov/08
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
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetTitleW3LOTTFunction extends AbstractFunzioneTag {

  public GetTitleW3LOTTFunction() {
    super(1, new Class[] { PageContext.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    HashMap keyParent = UtilityTags.stringParamsToHashMap(
        (String) pageContext.getAttribute(
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),
        null);

    Long numgara = new Long(
        ((JdbcParametro) keyParent.get("W3LOTT.NUMGARA")).getStringValue());

    Long numlott = new Long(
        ((JdbcParametro) keyParent.get("W3LOTT.NUMLOTT")).getStringValue());
    
    String selectW3LOTT = "select cig, oggetto from w3lott where numgara = ? and numlott = ?";
    String descrizione = "";

    try {

      List<?> datiW3LOTT = sqlManager.getListVector(selectW3LOTT,
          new Object[] { numgara, numlott });

      if (datiW3LOTT != null && datiW3LOTT.size() > 0) {
        String cig = (String) SqlManager.getValueFromVectorParam(
            datiW3LOTT.get(0), 0).getValue();
        String oggetto = (String) SqlManager.getValueFromVectorParam(
            datiW3LOTT.get(0), 1).getValue();

        if (cig != null) descrizione += cig + " - ";

        if (oggetto != null) {
          if (oggetto.length() <= 80) {
            descrizione += oggetto;
          } else {
            descrizione += oggetto.substring(0, 80) + "...";
          }
        }

      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore nella selezione del titolo per la scheda del contratto", e);
    }

    return descrizione;

  }

}

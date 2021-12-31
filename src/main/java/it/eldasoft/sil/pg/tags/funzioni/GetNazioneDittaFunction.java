/*
 * Created on 15-04-2019
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae la nazione della ditta aggiudicataria
 * @author Cristian Febas
 */
public class GetNazioneDittaFunction extends AbstractFunzioneTag {

  public GetNazioneDittaFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ditta = (String) GeneralTagsFunction.cast("string", params[1]);
    String nazione="";

    if (ditta != null && ditta.length()>0){
      String select="select nazimp from impr where codimp=?";
      try {
        Long nazimp = (Long) sqlManager.getObject(select, new Object[] { ditta });
        if(nazimp!=null){
          nazione = nazimp.toString();
        }else{
          nazione = "1";
        }
      } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura della nazione della ditta aggiudicataria della gara ", e);
      }
    }

    return nazione;

  }
}

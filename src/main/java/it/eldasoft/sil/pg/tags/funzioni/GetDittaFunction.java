/*
 * Created on 17-12-2014
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
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che estrae il campo DITTA di GARE
 * @author Marcello Caminiti
 */
public class GetDittaFunction extends AbstractFunzioneTag {

  public GetDittaFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) GeneralTagsFunction.cast("string", params[1]);
    String dittaDef="";
    String dittaProv="";

    if (ngara != null && ngara.length()>0){
      String select="select ditta,dittap from gare where ngara=?";
      try {
        Vector datiAggiudicatarie = sqlManager.getVector(select, new Object[] { ngara });
        if(datiAggiudicatarie!=null && datiAggiudicatarie.size()>0){
          dittaDef= SqlManager.getValueFromVectorParam(datiAggiudicatarie, 0).getStringValue();
          dittaProv = SqlManager.getValueFromVectorParam(datiAggiudicatarie, 1).getStringValue();
        }
      } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura della ditta aggiudicataria della gara ", e);
      }
    }
    this.getRequest().setAttribute("dittaProv", dittaProv);
    return dittaDef;
  }
}

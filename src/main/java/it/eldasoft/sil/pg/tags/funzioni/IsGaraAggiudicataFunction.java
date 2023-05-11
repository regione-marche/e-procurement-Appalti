/*
 * Created on 07/03/22
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina se una gara è aggiudicata
 *
 * @author Peruzzo Riccardo
 */
public class IsGaraAggiudicataFunction extends AbstractFunzioneTag {

  public IsGaraAggiudicataFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }


  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String garaAggiudicata = "false";

    if (ngara != null) {

      try {

        Long conteggio = (Long)sqlManager.getObject("select count(codgar1) from gare where ngara=? and ditta<>''", new Object[]{ngara});
        if(conteggio>0) {
        	garaAggiudicata="true";
        }

        }catch (SQLException e) {
        throw new JspException("Errore durante la verifica che la gara " + ngara + " sia aggiudicata", e);
      }
    }
    return garaAggiudicata;
  }

}
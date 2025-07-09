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
import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

/**
 * Funzione che determina se una gara è abilitata a FVOE
 *
 * @author Manuel Bridda
 */
public class CheckVisualizzaFVOEFunction extends AbstractFunzioneTag {

  public CheckVisualizzaFVOEFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }


  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String visualizza = "false";

    if (codgar != null) {
      try {
        Long conteggio = (Long)sqlManager.getObject(GestoreG1NPAFORM.composeQueryCountSchede(GestoreG1NPAFORM.CONFERMATE_ADORS2), new Object[]{codgar});
        if(conteggio>0)
          visualizza="true";
        }catch (SQLException e) {
        throw new JspException("Errore durante la verifica che la gara " + codgar + " sia abilitata alla consultazione del fascicolo degli operatori economici (FVOE)", e);
      }
    }
    return visualizza;
  }

}
/*
 * Created on 20-06-2014
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

/**
 * Funzione che estrae il campo MERUOLO di G_PERMESSI a partire
 * dall'id della ricerca di mercato e dall'id dell'utente corrente
 *
 * @author Marcello Caminiti
 */
public class GetMeruoloDaIdmericFunction extends AbstractFunzioneTag {

  public GetMeruoloDaIdmericFunction() {
    super(3, new Class[] { PageContext.class,String.class,Integer.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    // Id della ricerca di mercato
    String idRiceraMercato = (String) params[1];

    // Id utente
    Integer idUtente = (Integer) params[2];

    String result="";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      String select="select meruolo from g_permessi where IDMERIC=? and syscon = ?";
      Long meruolo = (Long)sqlManager.getObject(select,	new Object[] { new Long(idRiceraMercato),
    	    idUtente});

      if(meruolo!= null)
        result = meruolo.toString();
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del campo MERUOLO della tabella G_PERMESSI ", e);
    }

    return result;
  }
}

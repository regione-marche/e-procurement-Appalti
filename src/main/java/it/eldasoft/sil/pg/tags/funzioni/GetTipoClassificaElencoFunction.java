/*
 * Created on 28-06-2013
 *
 /*
 * Created on 17-apr-2009
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
 * Funzione che estrae il campo TIPOCLASS di GAREALBO a partire da NGARA.
 *
 * @author Marcello caminiti
 */

public class GetTipoClassificaElencoFunction extends AbstractFunzioneTag {

  public GetTipoClassificaElencoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codiceElenco = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    Long tipoclass = null;
    String tipoclassString ="";
    try {
      tipoclass = (Long)sqlManager.getObject(
          "select tipoclass from garealbo where ngara = ? ",
          new Object[] { codiceElenco });
      if(tipoclass!=null){
        tipoclassString = String.valueOf(tipoclass);
      }
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura del tipo classifica dell'elenco" + codiceElenco,
          s);
    }

    return tipoclassString;

  }

}

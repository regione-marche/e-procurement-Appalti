/*
 * Created on 12-12-2011
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che i campi valiscr e tipologia della tabella GAREALBO per un
 * elenco
 * 
 * @author Marcello Caminiti
 */
public class isVisibleDataScadenzaIscrizFunction extends AbstractFunzioneTag {

  public isVisibleDataScadenzaIscrizFunction() {
    super(2 , new Class[] { PageContext.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    Long   valiscr = null;
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ret = "false";
    try {
      Vector<?> datiGarealbo = sqlManager.getVector("select valiscr, tipologia from garealbo where ngara=? " +
      		"and codgar=?", new Object[]{ngara, "$" + ngara});
      if (datiGarealbo != null && datiGarealbo.size() > 0) {
        valiscr = (Long)((JdbcParametro) datiGarealbo.get(0)).getValue();
        Long tipologia = (Long)((JdbcParametro) datiGarealbo.get(1)).getValue();
        if (valiscr != null && valiscr.longValue() > 0 && tipologia != null && tipologia.longValue() != 3)
          ret = "true";
      }
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura dei dati della tabella GAREALBO", s);
    }
    
    if (valiscr == null)
      valiscr = new Long(0);
    pageContext.setAttribute("valiscr", valiscr);
    return ret;
  }

}

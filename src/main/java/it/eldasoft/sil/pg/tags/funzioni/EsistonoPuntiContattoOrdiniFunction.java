/*
 * Created on 11/60/14
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
 * Funzione che controlla se esistono punti di contatto per gli ordini della
 * ricerca di mercato
 *
 * @author Marcello Caminiti
 */
public class EsistonoPuntiContattoOrdiniFunction extends AbstractFunzioneTag {

  public EsistonoPuntiContattoOrdiniFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String chiave = (String) params[1];

    String select="select count(idric) from v_oda,garecont where idric=? and v_oda.ngara is not null " +
    		"and v_oda.ngara= garecont.ngara and ncont=1 and (pcoese is not null or pcofat is not null)";

    String ret="no";
    try {
      Long conteggio = (Long) sqlManager.getObject(select, new Object[] { new Long(chiave) });

      if (conteggio != null && conteggio.longValue()>0)
        ret = "si";

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante i controlli per determinare se esistono punti di contatto per gli ordini della ricerca di mercato:" + chiave, e);
    }

    return ret;

  }

}

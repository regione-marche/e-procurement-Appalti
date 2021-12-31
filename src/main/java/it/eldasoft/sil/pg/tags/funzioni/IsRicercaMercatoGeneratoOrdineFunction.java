/*
 * Created on 12/06/14
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
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class IsRicercaMercatoGeneratoOrdineFunction extends AbstractFunzioneTag {

  public IsRicercaMercatoGeneratoOrdineFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String meric_id_s = (String) params[1];
    Long meric_id = null;
    if (meric_id_s != null && meric_id_s != "") {
      meric_id = new Long((String) params[1]);
    }

    String isRicercaMercatoGeneratoOrdine = "false";

    if (meric_id != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        Long conteggio = (Long) sqlManager.getObject("select count(*) from mericart where idric = ? and ngara is not null", new Object[] { meric_id });
        if (conteggio != null && conteggio.longValue() > 0) isRicercaMercatoGeneratoOrdine = "true";
      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo se per la ricerca di mercato esistono prodotti ordinati", e);
      }
    }
    
    return isRicercaMercatoGeneratoOrdine;
  }

}
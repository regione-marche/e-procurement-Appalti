/*
 * Created on 09/12/2013
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
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetValoriMEALLARTCATFunction extends AbstractFunzioneTag {

  public GetValoriMEALLARTCATFunction() {
    super(3, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String id = (String) params[1];
    String tipo = (String) params[2];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    List<?> datiMEALLARTCAT = null;

    try {
      if(id!=null){
        String selectMEALLARTCAT = "select meallartcat.id, meallartcat.idartcat, "
            + " meallartcat.idprg, meallartcat.iddocdig, w_docdig.dignomdoc, meallartcat.tipoall "
            + " from meallartcat, w_docdig "
            + " where meallartcat.idartcat = ? and meallartcat.tipoall = ?"
            + " and meallartcat.idprg = w_docdig.idprg "
            + " and meallartcat.iddocdig = w_docdig.iddocdig";

        datiMEALLARTCAT = sqlManager.getListVector(selectMEALLARTCAT, new Object[] { Long.valueOf(id),Long.valueOf(tipo) });
      }
      pageContext.setAttribute("datiMEALLARTCAT", datiMEALLARTCAT, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della lista dei documenti allegati", e);
    }

    return null;
  }

}

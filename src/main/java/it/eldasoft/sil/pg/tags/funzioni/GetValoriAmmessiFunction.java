/*
 * Created on 13/01/2015
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

public class GetValoriAmmessiFunction extends AbstractFunzioneTag {

  public GetValoriAmmessiFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String idcridef = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    List<?> datiG1CRIREG = null;

    try {
      if(idcridef!=null){
        String selectGARCOMPREQ = "select puntuale, valmin, valmax, coeffi, idcridef, id " +
        		"from g1crireg where g1crireg.idcridef = ? order by id";

        datiG1CRIREG = sqlManager.getListVector(selectGARCOMPREQ, new Object[] { idcridef });
      }
      pageContext.setAttribute("datiG1CRIREG", datiG1CRIREG, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della tabella GARCOMPREQ", e);
    }

    return null;
  }

}

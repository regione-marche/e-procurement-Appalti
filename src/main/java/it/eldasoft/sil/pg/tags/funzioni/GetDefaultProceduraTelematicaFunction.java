package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetDefaultProceduraTelematicaFunction extends AbstractFunzioneTag {

  public GetDefaultProceduraTelematicaFunction() {
    super(1, new Class[] { PageContext.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    try {

      String tab1desc = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] {
          "A1099", new Long(1) });

      if (tab1desc != null) {
        pageContext.setAttribute("defaultProceduraTelematica", tab1desc.substring(0, 1), PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione del default per la procedura telematica", e);
    }
    return null;
  }
}

package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GestioneMultiplaQuotaDerFunction extends AbstractFunzioneTag {

  public GestioneMultiplaQuotaDerFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String chiave = (String) params[1];
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String select="select ID, NGARA, MOTDEROGA from G1QUOTADER where NGARA = ? order by id";

    try {
      List listaNDer = sqlManager.getListVector(
          select, new Object[]{chiave});

      if (listaNDer != null && listaNDer.size() > 0)
        pageContext.setAttribute("listaNDer", listaNDer,PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i motivi della tabella G1QUOTADER con chiave" + chiave, e);
    }

    return null;
  }

}

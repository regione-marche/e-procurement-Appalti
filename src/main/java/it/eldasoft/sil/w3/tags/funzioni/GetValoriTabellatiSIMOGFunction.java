package it.eldasoft.sil.w3.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetValoriTabellatiSIMOGFunction extends AbstractFunzioneTag {

  public GetValoriTabellatiSIMOGFunction() {
    super(1, new Class[] { PageContext.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      List listaW3z13 = sqlManager.getListVector(
          "select tab2d1, tab2d2 from tab2 where tab2cod = ? order by tab2tip",
          new Object[] { "W3z13" });

      List listaW3z14 = sqlManager.getListVector(
          "select tab2d1, tab2d2 from tab2 where tab2cod = ? order by tab2tip",
          new Object[] { "W3z14" });

      pageContext.setAttribute("listaW3z13", listaW3z13,
          PageContext.REQUEST_SCOPE);

      pageContext.setAttribute("listaW3z14", listaW3z14,
          PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione dei dati dei tabellati", e);
    }
    return null;
  }

}

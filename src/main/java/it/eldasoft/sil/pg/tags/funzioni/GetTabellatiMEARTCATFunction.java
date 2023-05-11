package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetTabellatiMEARTCATFunction extends AbstractFunzioneTag {

  public GetTabellatiMEARTCATFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    try {

      List<?> listaTipo = sqlManager.getListVector("select tab1tip, tab1desc from tab1 where tab1cod = ? order by tab1tip",
          new Object[] { "ME001" });

      List<?> listaStato = sqlManager.getListVector("select tab1tip, tab1desc from tab1 where tab1cod = ? order by tab1tip",
          new Object[] { "ME002" });
      
      List<?> listaUnitaMisura = sqlManager.getListVector("select tab1tip, tab1desc from tab1 where tab1cod = ? order by tab1tip",
          new Object[] { "ME007" });

      pageContext.setAttribute("listaTipo", listaTipo, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("listaStato", listaStato, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("listaUnitaMisura", listaUnitaMisura, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione dei dati tabellati relativi agli articoli", e);
    }
    return null;
  }

}

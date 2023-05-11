package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetDefaultModalitaPresentazioneOffertaFunction extends AbstractFunzioneTag {

  public GetDefaultModalitaPresentazioneOffertaFunction() {
    super(1, new Class[] { PageContext.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    try {

      String tab1desc = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] {
          "A1099", new Long(2) });

      if (tab1desc != null) {
        pageContext.setAttribute("defaultModalitaPresentazione", tab1desc.substring(0, 1), PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione del default per la modalità di presentazione  dell'offerta", e);
    }
    return null;
  }
}

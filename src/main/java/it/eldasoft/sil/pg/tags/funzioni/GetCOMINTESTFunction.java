package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetCOMINTESTFunction extends AbstractFunzioneTag {

  public GetCOMINTESTFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String comintest = null;
    try {
      comintest = (String) sqlManager.getObject(
          "select comintest from w_invcom where idprg = ? and idcom = ?",
          new Object[] { idprg, idcom });
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura dell'abilitazione dell' intestazione della comunicazione",
          s);
    }

    if (comintest != null && comintest.length() > 0) {
      return comintest;
    } else {
      return "";
    }
  }

}

package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetCOMSTATOFunction extends AbstractFunzioneTag {

  public GetCOMSTATOFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String comstato = null;
    Long compub = null;
    try {
      Vector datiComunicazione = sqlManager.getVector(
          "select comstato,compub from w_invcom where idprg = ? and idcom = ?",
          new Object[] { idprg, idcom });
      if(datiComunicazione!=null && datiComunicazione.size()>0){
        comstato = (String) ((JdbcParametro) datiComunicazione.get(0)).getValue();
        compub = (Long) ((JdbcParametro) datiComunicazione.get(1)).getValue();
      }
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura dello stato della comunicazione",
          s);
    }

    pageContext.setAttribute("compub", compub, PageContext.REQUEST_SCOPE);

    if (comstato != null && comstato.length() > 0) {
      return comstato;
    } else {
      return "";
    }
  }

}

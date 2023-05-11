package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetDatiComunicazioneOriginaleFunction extends AbstractFunzioneTag {

  public GetDatiComunicazioneOriginaleFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String commsgogg = null;
    String commsgtes = null;
    try {
      Vector datiComunicazione = sqlManager.getVector(
          "select commsgogg, commsgtes from w_invcom where idprg = ? and idcom = ?",
          new Object[] { idprg, idcom });
      if(datiComunicazione!=null && datiComunicazione.size()>0){
        commsgogg = (String) ((JdbcParametro) datiComunicazione.get(0)).getValue();
        commsgtes = (String) ((JdbcParametro) datiComunicazione.get(1)).getValue();
      }
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura dello stato della comunicazione",
          s);
    }
    
    pageContext.setAttribute("commsgoggOrigin", commsgogg, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("commsgtesOrigin", commsgtes, PageContext.REQUEST_SCOPE);

    return "";
  }

}

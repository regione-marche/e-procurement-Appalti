package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetCOMKEYGaraLottoFunction extends AbstractFunzioneTag {

  public GetCOMKEYGaraLottoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String comkey1 = null;
    try {
      //estraggo il coment
      Vector<JdbcParametro> result = sqlManager.getVector("select comkey1,coment from w_invcom where idprg = ? and idcom = ?", new Object[] { idprg, idcom });
      //controllo il coment
      
      if("NSO_ORDINI".equalsIgnoreCase(StringUtils.trimToEmpty(result.get(1).getStringValue()))){
        comkey1 = (String) sqlManager.getObject("SELECT ngara FROM nso_ordini WHERE id=?",new Object[] { StringUtils.trimToEmpty(result.get(0).getStringValue())});
      } else {
        comkey1 = StringUtils.trimToEmpty(result.get(0).getStringValue()); //BACKWARD compatibility
      }
      
      /*
      comkey1 = (String) sqlManager.getObject(
          "select comkey1 from w_invcom where idprg = ? and idcom = ?",
          new Object[] { idprg, idcom });
         */
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura del codice del lotto",
          s);
    }

    if (comkey1 != null && comkey1.length() > 0) {
      return comkey1;
    } else {
      return "";
    }
  }

}

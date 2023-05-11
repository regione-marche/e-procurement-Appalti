package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class MostraStatoPECFunction extends AbstractFunzioneTag {

  private static final Logger LOGGER = Logger.getLogger(MostraStatoPECFunction.class);

  private static final String QUERY =
        "SELECT COUNT(*) "
      + "FROM w_invcomdes "
      + "WHERE idprg = ? AND idcom = ? AND desesitopec IS NOT NULL";

  public MostraStatoPECFunction() {
    super(3, new Class<?>[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("MostraStatoPECFunzione: inizio metodo");

    final SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    final String idprg = (String) params[1];
    final int idcom = Integer.parseInt((String) params[2]);

    String result = "true";

    try {
      final Long size = (Long) sqlManager.getObject(QUERY, new Object[] { idprg, idcom });
      if (size.compareTo(0L) == 0) {
        result = "false";
      }
    } catch (SQLException e) {
      LOGGER.error("Errore durante l'esecuzione della query '" + QUERY + "'", e);
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("MostraStatoPECFunzione: fine metodo");

    return result;
  }

}

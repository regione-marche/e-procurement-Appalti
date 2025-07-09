package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

public class MostraFiltroEMLFunction extends AbstractFunzioneTag {

  private static final Logger LOGGER = Logger.getLogger(MostraFiltroEMLFunction.class);

  private static final String QUERY =
        "SELECT COUNT(*) from W_INVCOMDES ii, W_INVCOM i where i.idprg=ii.idprg and i.idcom=ii.idcom and i.comkey1=? and desesitopec is not null";

  public MostraFiltroEMLFunction() {
    super(2, new Class<?>[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("MostraFiltroEMLFunction: inizio metodo");

    final SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    final String codiceGara = (String) params[1];

    String result = "false";

    try {
      final Long size = (Long) sqlManager.getObject(QUERY, new Object[] { codiceGara});
      if (size>0) {
        result = "true";
      }
    } catch (SQLException e) {
      LOGGER.error("Errore durante l'esecuzione della query '" + QUERY + "'", e);
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("MostraFiltroEMLFunction: fine metodo");

    return result;
  }

}

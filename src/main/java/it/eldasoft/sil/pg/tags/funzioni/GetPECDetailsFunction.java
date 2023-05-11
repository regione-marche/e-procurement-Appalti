package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetPECDetailsFunction extends AbstractFunzioneTag {

  private static final Logger LOGGER = Logger.getLogger(GetPECDetailsFunction.class);

  private static final String SENT_QUERY =
        "SELECT COUNT(*) "
      + "FROM w_invcomdes "
      + "WHERE idprg = ? AND idcom = ? AND comtipma = 1 AND desesitopec IS NOT NULL";

  private static final String ERROR_QUERY =
        "SELECT COUNT(*) "
      + "FROM w_invcomdes "
      + "WHERE idprg = ? AND idcom = ? AND comtipma = 1 AND desesitopec IN ('5', '6', '7')";

  private static final String RECEIVED_QUERY =
        "SELECT COUNT(*) "
      + "FROM w_invcomdes "
      + "WHERE idprg = ? AND idcom = ? AND comtipma = 1 AND desesitopec LIKE '4'";

  public GetPECDetailsFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GetPECDetailsFunction: inizio metodo");

    final SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    final String idprg = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    final Long idcom = Long.parseLong((String) params[1]);
    final Object[] queryParams = new Object[] { idprg, idcom };

    String result = "";

    try {
      final Long sent = (Long) sqlManager.getObject(SENT_QUERY, queryParams);
      result += sent.toString() + ";";

      final Long error = (Long) sqlManager.getObject(ERROR_QUERY, queryParams);
      result += error.toString() + ";";

      final Long received = (Long) sqlManager.getObject(RECEIVED_QUERY, queryParams);
      result += received.toString();
    } catch (SQLException e) {
      LOGGER.error("Errore nel reperimento dei dettagli sulle PEC inviate", e);
      throw new JspException();
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GetPECDetailsFunction: fine metodo");

    return result;
  }

}

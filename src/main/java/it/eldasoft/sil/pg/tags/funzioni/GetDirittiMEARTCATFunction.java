package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetDirittiMEARTCATFunction extends AbstractFunzioneTag {

  public GetDirittiMEARTCATFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      String id = (String) params[1];

      if (id != null && !"".equals(id.trim())) try {

        // Cancellabile
        boolean isMEARTCATCancellabile = true;
        Long countPRODOTTI = (Long) sqlManager.getObject("select count(*) from meiscrizprod where idartcat = ?", new Object[] { new Long(id) });
        if (countPRODOTTI != null && countPRODOTTI.longValue() > 0) {
          isMEARTCATCancellabile = false;
        }
        pageContext.setAttribute("isMEARTCATCancellabile", isMEARTCATCancellabile, PageContext.REQUEST_SCOPE);

      } catch (SQLException e) {
        throw new JspException("Errore nella lettura dei diritti relativi all'articolo", e);
      }
    }
    return null;
  }
}

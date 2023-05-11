package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetDiscOperatoreFunction extends AbstractFunzioneTag {

  public GetDiscOperatoreFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      if (params[1] != null) {
        Long discid_p = new Long((String) params[1]);

        try {
          Long operatore = (Long) sqlManager.getObject("select discmessope from w_discuss_p where discid_p = ?", new Object[] { discid_p });
          pageContext.setAttribute("operatore", operatore, PageContext.REQUEST_SCOPE);

        } catch (SQLException e) {
          throw new JspException("Errore nella lettura dell'operatore della discussione", e);
        }
      }
    }

    return null;

  }
}
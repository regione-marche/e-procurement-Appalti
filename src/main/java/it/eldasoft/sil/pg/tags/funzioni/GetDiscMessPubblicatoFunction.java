package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetDiscMessPubblicatoFunction extends AbstractFunzioneTag {

  public GetDiscMessPubblicatoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      String pubblicato = "false";

      if (params[1] != null && params[2] != null) {
        Long discid_p = new Long((String) params[1]);
        Long discid = new Long((String) params[2]);

        try {
          // Operatore
          String discmesspubbl = (String) sqlManager.getObject("select discmesspubbl from w_discuss where discid_p = ? and discid = ?",
              new Object[] { discid_p, discid });
          if (discmesspubbl != null && "1".equals(discmesspubbl)) {
            pubblicato = "true";
          }

        } catch (SQLException e) {
          throw new JspException("Errore nella lettura dell'operatore della discussione", e);
        }
      }

      pageContext.setAttribute("pubblicato", pubblicato, PageContext.REQUEST_SCOPE);
    }

    return null;

  }
}

package it.eldasoft.sil.w3.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestioneW3GARAMERCFunction extends AbstractFunzioneTag {

  public GestioneW3GARAMERCFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      Long numgara = new Long((String) params[0]);

      try {
        List<?> datiW3GARAMERC = sqlManager.getListVector("select numgara, nummerc, categoria from w3garamerc where numgara = ?",
            new Object[] { numgara });

        if (datiW3GARAMERC != null && datiW3GARAMERC.size() > 0) {
          pageContext.setAttribute("datiW3GARAMERC", datiW3GARAMERC, PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException("Errore nella lettura della lista delle categorie merceologiche", e);
      }

    }
    return null;

  }

}

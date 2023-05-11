package it.eldasoft.sil.w3.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestioneW3CONDFunction extends AbstractFunzioneTag {

  public GestioneW3CONDFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(
        pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      String[] parametri = ((String) params[0]).split(";");

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      Long numgara = new Long(parametri[0]);
      Long numlott = new Long(parametri[1]);

      try {
        List datiW3COND = sqlManager.getListVector(
            "select numgara, numlott, num_cond, id_condizione from w3cond where numgara = ? and numlott = ?",
            new Object[] { numgara, numlott });

        if (datiW3COND != null && datiW3COND.size() > 0) {
          pageContext.setAttribute("datiW3COND", datiW3COND,
              PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException(
            "Errore nella lettura della lista delle condizioni negoziate", e);
      }

    }
    return null;

  }

}

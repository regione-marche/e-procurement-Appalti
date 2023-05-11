package it.eldasoft.sil.w3.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GestioneW3LOTTDEROGHEFunction extends AbstractFunzioneTag {

  public GestioneW3LOTTDEROGHEFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
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
        List datiW3LOTTDEROGHE = sqlManager.getListVector(
            "select numgara, numlott, idderoga, codderoga from W3LOTTDEROGHE" +
            " where numgara = ? and numlott = ?",
            new Object[] { numgara, numlott });

        if (datiW3LOTTDEROGHE != null && datiW3LOTTDEROGHE.size() > 0) {
          pageContext.setAttribute("datiW3LOTTDEROGHE", datiW3LOTTDEROGHE, PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException(
            "Errore nella lettura della lista dei motivi deroga associati al CIG", e);
      }

    }
    return null;

  }

}

package it.eldasoft.sil.w3.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GestioneW3GARAREQCIGFunction extends AbstractFunzioneTag {

  public GestioneW3GARAREQCIGFunction() {
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
      Long numreq = new Long(parametri[1]);

      try {
        List datiW3GARAREQCIG = sqlManager.getListVector(
            "select c.numgara, c.numreq, c.numcig, c.cig, l.oggetto from w3garareqcig c,w3lott l" +
            " where c.numgara = ? and c.numreq = ? and c.cig = l.cig",
            new Object[] { numgara, numreq });

        if (datiW3GARAREQCIG != null && datiW3GARAREQCIG.size() > 0) {
          pageContext.setAttribute("datiW3GARAREQCIG", datiW3GARAREQCIG, PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException(
            "Errore nella lettura della lista dei CIG dei requisiti", e);
      }

    }
    return null;

  }

}

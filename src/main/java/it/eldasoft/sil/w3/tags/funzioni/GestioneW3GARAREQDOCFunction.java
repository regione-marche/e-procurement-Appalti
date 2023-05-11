package it.eldasoft.sil.w3.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GestioneW3GARAREQDOCFunction extends AbstractFunzioneTag {

  public GestioneW3GARAREQDOCFunction() {
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
        List datiW3GARAREQDOC = sqlManager.getListVector(
            "select numgara, numreq, numdoc, codice_tipo_doc, descrizione," +
            "emettitore, fax, telefono, mail, mail_pec " +
            "from w3garareqdoc where numgara = ? and numreq = ?",
            new Object[] { numgara, numreq });

        if (datiW3GARAREQDOC != null && datiW3GARAREQDOC.size() > 0) {
          pageContext.setAttribute("datiW3GARAREQDOC", datiW3GARAREQDOC, PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException(
            "Errore nella lettura della lista dei documenti dei requisiti", e);
      }

    }
    return null;

  }

}

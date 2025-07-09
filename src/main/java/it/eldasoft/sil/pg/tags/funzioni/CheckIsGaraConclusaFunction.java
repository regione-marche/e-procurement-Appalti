package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che determina se una gara è conclusa.
 *
 * @author Alvise Gorinati
 */
public class CheckIsGaraConclusaFunction extends AbstractFunzioneTag {

  // === Costanti ==============================================================

  private static final String QUERY =
          "SELECT count(1) FROM v_gare_statoesito WHERE codgar = ? AND codstato = 3";

  // === Costruttori ===========================================================

  public CheckIsGaraConclusaFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  // === Metodi ================================================================

  public String function(PageContext pageContext, Object[] params) throws JspException {
    String codgar = (String) params[1];

    SqlManager sqlManager = (SqlManager)UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String annullata = "false";

    if (codgar != null) {
      try {
        Long conteggio = (Long)sqlManager.getObject(QUERY, new Object[] { codgar });

        if (conteggio > 0) {
          annullata = "true";
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante la verifica che la gara " + codgar + " sia conclusa", e);
      }
    }

    return annullata;
  }

}

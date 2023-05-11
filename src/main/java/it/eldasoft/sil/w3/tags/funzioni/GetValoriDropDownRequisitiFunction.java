package it.eldasoft.sil.w3.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetValoriDropDownRequisitiFunction extends AbstractFunzioneTag {

  public GetValoriDropDownRequisitiFunction() {
    super(1, new Class[] { PageContext.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      List<?> listaValoriRequisiti = sqlManager.getListVector(
          "select codice_dettaglio,descrizione from w3tabreq order by codice_dettaglio",
          new Object[] { });

      pageContext.setAttribute("listaValoriRequisiti", listaValoriRequisiti,
          PageContext.REQUEST_SCOPE);


    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione dei dati dei tabellati", e);
    }
    return null;
  }

}

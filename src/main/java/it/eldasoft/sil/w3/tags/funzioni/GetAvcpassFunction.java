package it.eldasoft.sil.w3.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetAvcpassFunction extends AbstractFunzioneTag {

  public GetAvcpassFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String codice = params[1].toString();
    if (codice == null || codice.equals("")) {
      codice = (String) UtilityTags.getParametro(pageContext,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    }
    if (codice == null || codice.equals("")) {
      codice = (String) UtilityTags.getParametro(pageContext,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
    }
    String avcpass = "";
    try {
      DataColumnContainer key = new DataColumnContainer(codice);
      Long numgara = (key.getColumnsBySuffix("NUMGARA", true))[0].getValue().longValue();
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      avcpass = (String)sqlManager.getObject("select ESCLUSO_AVCPASS from W3GARA where NUMGARA = ?", new Object[] { numgara });
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre il campo ESCLUSO_AVCPASS", e);
    } catch (GestoreException e) {
      throw new JspException("Errore nell'estrarre il campo ESCLUSO_AVCPASS", e);
    }
    return avcpass;
  }

}

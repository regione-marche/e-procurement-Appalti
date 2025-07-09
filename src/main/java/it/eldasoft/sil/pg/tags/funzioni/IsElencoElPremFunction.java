package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;
import java.util.Vector;

public class IsElencoElPremFunction extends AbstractFunzioneTag {
  public IsElencoElPremFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
    pageContext, SqlManager.class);

    String codiceGara = null;
    if (params != null && params.length > 0)
        codiceGara = (String) params[0];

    if (StringUtils.isEmpty(codiceGara)) {
      codiceGara = UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
      if (StringUtils.isNotEmpty(codiceGara))
        codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);

      if (StringUtils.isEmpty(codiceGara)) {
        codiceGara = UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
        if (StringUtils.isNotEmpty(codiceGara))
          codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);
      }
    }
    try {
      Vector<JdbcParametro> datiElenco = sqlManager.getVector("select iselprem,iselpremob from garealbo where ngara=(select elencoe from gare where ngara=?)", new Object[]{codiceGara});
      if (datiElenco != null) {
        String isElencoElPrem = datiElenco.get(0).getStringValue();
        String isElencoElPremOB = datiElenco.get(1).getStringValue();
        pageContext.setAttribute("isElencoElPrem", "1".equals(isElencoElPrem) ? "true" : "false");
        pageContext.setAttribute("isElencoElPremOB", "1".equals(isElencoElPremOB) ? "true" : "false");
      }

      Vector<JdbcParametro> datiGare1 = sqlManager.getVector("select elpremval," + sqlManager.getDBFunction("datetimetostring",  new String[] {"DELPREMCONT"}) + ",elpremsinf,elpremsint from gare1 where ngara=?", new Object[]{codiceGara});
      String isELPREMVAL = (String) datiGare1.get(0).getValue();
      String delpremcont = (String) datiGare1.get(1).getValue();
      Double elpremsinf = datiGare1.get(2).getValue() != null ? ((Number) datiGare1.get(2).getValue()).doubleValue() : null;
      Double elpremsint = datiGare1.get(3).getValue() != null ? ((Number) datiGare1.get(3).getValue()).doubleValue() : null;
      pageContext.setAttribute("isElpremval", "1".equals(isELPREMVAL) ? "true" : "false");
      pageContext.setAttribute("delpremcont", delpremcont);
      pageContext.setAttribute("elpremsinf", elpremsinf);
      pageContext.setAttribute("elpremsint", elpremsint);

      Long countElementiPremiali = (Long) sqlManager.getObject("select count(*) from elpremgare where ngara=?", new Object[]{codiceGara});
      pageContext.setAttribute("inseritiElementiPremiali", countElementiPremiali != null && countElementiPremiali > 0 ? "true" : "false");

    } catch (SQLException e) {
      throw new JspException("Errore nella lettura dalla base dati: " + (e != null ? e.getMessage() : ""), e);
    }
    return null;
  }
}

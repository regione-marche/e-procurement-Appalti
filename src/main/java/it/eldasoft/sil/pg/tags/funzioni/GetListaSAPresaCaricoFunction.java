package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;
import java.util.List;

public class GetListaSAPresaCaricoFunction extends AbstractFunzioneTag {

  public GetListaSAPresaCaricoFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
          throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            pageContext, SqlManager.class);
    try {
      String codgar = (String) params[0];
      List<?> listaSA = sqlManager.getListVector(
              "select codein,nomein, CAST(1 as NUMERIC(1)) as SortOrder from uffint where codein in (select cenint from torn where codgar=?) " +
                      "union all " +
                      "select codein,nomein, CAST(2 as NUMERIC(1)) as SortOrder from uffint where codein in (select cenint from garaltsog where ngara in (select ngara from gare,torn where codgar1=? and codgar1=codgar and altrisog in (2,3)) AND codein NOT IN ( SELECT cenint FROM torn WHERE codgar = ?)) " +
                      "order by SortOrder,nomein",
              new Object[] {codgar,codgar,codgar});
      pageContext.setAttribute("bloccaListaSA", listaSA!=null && listaSA.size()==1, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("listaSA", listaSA, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException(
              "Errore nell'estrazione dei dati per la popolazione della/e dropdown list",
              e);
    }
    return null;
  }
}

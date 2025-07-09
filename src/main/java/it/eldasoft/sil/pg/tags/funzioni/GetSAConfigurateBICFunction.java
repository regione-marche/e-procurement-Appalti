package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.List;
import java.util.Vector;

public class GetSAConfigurateBICFunction extends AbstractFunzioneTag {
  public GetSAConfigurateBICFunction() {
    super(1, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      String sql = "select u.codein, u.nomein, u.cfein from uffint u, w_config w where w.codapp='PG' and w.valore is not null and w.chiave like " +
              sqlManager.getDBFunction("concat", new String[] {sqlManager.getDBFunction("concat", new String[] {"'lfs.ws.url/'","u.codein"}),"'%'"})
              +" order by u.codein";
      this.getRequest().setAttribute("listaSAConfigurate", sqlManager.getListVector(sql, null));
      
    } catch (Exception e) {
      throw new JspException("Errore nella lettura delle configurazione per l'integrazione BIC" ,
          e);
    }

    return null;

  }
}

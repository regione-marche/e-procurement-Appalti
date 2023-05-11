package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetValoreCampoParametrazioneFunction extends AbstractFunzioneTag {

  public GetValoreCampoParametrazioneFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  /*
   * La funzione restituisce il valore del campo relativo alla riparametrazione
   * sia in formato String che in formato Long(in questo caso come attributo del request)
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    String nomeCampo = (String) params[2];
    String ret=null;

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    Long dato = null;
    try {
      dato = (Long) sqlManager.getObject("select " + nomeCampo + " from gare1 where ngara=?", new Object[] { ngara });
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura del campo GARE1." + nomeCampo , s);
    }

    if (dato != null) {
      ret = dato.toString();
    }

    pageContext.setAttribute(nomeCampo, dato, PageContext.REQUEST_SCOPE);
    return ret;
  }

}

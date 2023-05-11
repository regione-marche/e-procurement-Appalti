package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetSTATOCGFunction extends AbstractFunzioneTag {

  public GetSTATOCGFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ret=null;
    try {
      Long statocg = (Long) sqlManager.getObject("select statocg from gare1  where ngara = ?", new Object[] { ngara });
      if(statocg!=null)
        ret= String.valueOf(statocg);
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura dello stato valutazione su app", s);
    }


    return ret;
  }

}

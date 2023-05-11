package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetCOMDATINSFunction extends AbstractFunzioneTag {

  public GetCOMDATINSFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    Date datins = null;
    String comdatins = null;
    try {
      datins = (Date) sqlManager.getObject("select comdatins from w_invcom where idprg = ? and idcom = ?", new Object[] { idprg, idcom });
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura della data inserimento della comunicazione", s);
    }

    if (datins != null) {
      comdatins = UtilityDate.convertiData(datins, UtilityDate.FORMATO_AAAAMMGG);
      pageContext.setAttribute("comdatins", comdatins, PageContext.REQUEST_SCOPE);
      return comdatins;
    } else {
      return "";
    }
  }

}

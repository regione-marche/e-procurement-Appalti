package it.eldasoft.sil.w3.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetDescFromCpvFunction extends AbstractFunzioneTag {

  public GetDescFromCpvFunction() {
    super(2,
        new Class[] { PageContext.class, JdbcParametro.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String codice = "";
    if (params[1] != null) {
      codice = params[1].toString();
    }
    String descrizioneDaCpv = "";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    try {
      if (codice != null && !codice.equals("")) {
        List listaDescrizioneFromCpv = sqlManager.getListVector("select CPVDESC "
              + "from TABCPV "
              + "where CPVCOD4 = ?", new Object[] { codice });
        
        
        if (listaDescrizioneFromCpv != null && listaDescrizioneFromCpv.size() > 0) {
          descrizioneDaCpv = ((List) listaDescrizioneFromCpv.get(0)).get(0).toString();
        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre la descrizione del cpv", e);
    }

    return descrizioneDaCpv;
  }

}

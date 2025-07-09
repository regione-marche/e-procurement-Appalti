package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;
import java.util.Vector;

public class GetDatiFS12RettificaFunction extends
    AbstractFunzioneTag {

  public GetDatiFS12RettificaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            pageContext, SqlManager.class);

    String isRettificaCodificata = "false";
    String hasRettificaRisposta = "false";

    try {
      Vector data = sqlManager.getVector(
              "select i.commodello, x.comstato from w_invcom i left join w_invcom x on ((i.commodello=13 and x.comtipo='FS11B') or (i.commodello=18 and x.comtipo='FS11C')) and x.idprg=i.idprg and i.comkey1=x.comkey1 and (i.comkey3=x.comkey2 or (i.comkey3 is null and i.comkey2=x.comkey2)) " +
                      "where i.idprg=? and i.idcom=?",
              new Object[]{idprg, idcom});

      if((Long.valueOf(13).equals(SqlManager.getValueFromVectorParam(data,0).getValue())  || Long.valueOf(18).equals(SqlManager.getValueFromVectorParam(data,0).getValue()))
              && "5".equals(SqlManager.getValueFromVectorParam(data,1).getStringValue()))
        isRettificaCodificata = "true";

      if(Long.valueOf(10).equals(SqlManager.getValueFromVectorParam(data,0).getValue())  || Long.valueOf(15).equals(SqlManager.getValueFromVectorParam(data,0).getValue())){
        Long countRisposte = (Long) sqlManager.getObject("select count(1) from w_invcom where idprgris=? and idcomris=?", new Object[]{idprg, idcom});
        if(countRisposte > 0)
          hasRettificaRisposta = "true";
      }

    } catch (SQLException s) {
      throw new JspException(
              "Errore durante la lettura dello stato della comunicazione di rettifica", s);
    }

    this.getRequest().setAttribute("isRettificaCodificata",isRettificaCodificata);
    this.getRequest().setAttribute("hasRettificaRisposta", hasRettificaRisposta);
    return null;
  }

  }

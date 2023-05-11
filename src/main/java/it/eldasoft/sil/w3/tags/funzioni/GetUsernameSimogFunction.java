package it.eldasoft.sil.w3.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetUsernameSimogFunction extends AbstractFunzioneTag {

  public GetUsernameSimogFunction() {
    super(3, new Class[] { PageContext.class, Object.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String simogwsuser = null;
    try {
    	Long numgara = null;
    	String codrup = null;
    	String entita = null;
    	if (params[1] != null && !params[1].equals("")) {
    		numgara = new Long(params[1].toString());
    		entita = (String)pageContext.getRequest().getAttribute("entita");
    	}
    	if (params[2] != null) {
    		codrup = params[2].toString();
    	}
    	SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
    	if (numgara != null) {
    		if (entita == null || entita.equals("") || entita.equals("W3GARA") || entita.equals("W3GARAPUBBLICA") || entita.equals("W3LOTT") || entita.equals("W3GARAREQ")) {
    			simogwsuser = (String)sqlManager.getObject("select CFTEC from W3GARA left join TECNI on W3GARA.RUP_CODTEC=TECNI.CODTEC where NUMGARA = ?", new Object[] { numgara });
        		codrup = (String)sqlManager.getObject("select RUP_CODTEC from W3GARA where NUMGARA = ?", new Object[] { numgara });	
    		} else if ("W3SMARTCIG".equals(entita)){
    			simogwsuser = (String)sqlManager.getObject("select CFTEC from W3SMARTCIG left join TECNI on W3SMARTCIG.RUP=TECNI.CODTEC where CODRICH = ?", new Object[] { numgara });
        		codrup = (String)sqlManager.getObject("select RUP from W3SMARTCIG where CODRICH = ?", new Object[] { numgara });
    		}
    	} else if (codrup != null) {
    		simogwsuser = (String)sqlManager.getObject("select CFTEC from TECNI where CODTEC = ?", new Object[] { codrup });
    	}
    	pageContext.setAttribute("codrup", codrup);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre il CFTEC dalla tabella TECNI", e);
    } 
    return simogwsuser;
  }

}

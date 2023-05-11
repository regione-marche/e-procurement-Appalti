
package it.eldasoft.sil.pg.tags.funzioni;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;


public class EsisteAnagraficaSimogFunction extends AbstractFunzioneTag {

  public EsisteAnagraficaSimogFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codiceGara = (String) params[1];
    String esisteAnagrafica = "false";
    if ( codiceGara!=null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
       String select = "select tipo_gara, tab1desc from v_w3gare, tab1 where codgar=? and tab1cod='A1171' and tab1tip=stato_gara and stato_simog <> 6";
       Vector<?> datiSimog = sqlManager.getVector(select, new Object[] {codiceGara});
       if (datiSimog != null && datiSimog.size() > 0) {
         String tipo = SqlManager.getValueFromVectorParam(datiSimog, 0).getStringValue();
         String stato = SqlManager.getValueFromVectorParam(datiSimog, 1).getStringValue();
         if(tipo !=null ){
           esisteAnagrafica = "true";
           pageContext.setAttribute("tipoSimog", tipo,PageContext.REQUEST_SCOPE);
           pageContext.setAttribute("statoSimog", stato, PageContext.REQUEST_SCOPE);
           String tipoDesc ="";
           if("G".equals(tipo))
             tipoDesc ="Creata anagrafica gara SIMOG per richiesta CIG";
           else if("S".equals(tipo))
             tipoDesc ="Creata anagrafica gara SIMOG per richiesta Smartcig";
           pageContext.setAttribute("tipoSimogDesc", tipoDesc, PageContext.REQUEST_SCOPE);
         }
       }

      } catch (SQLException e) {
        throw new JspException("Errore durante la verifica dell'anagrafica simog per la gara " + codiceGara, e);
      }
    }

    return esisteAnagrafica;
  }

}
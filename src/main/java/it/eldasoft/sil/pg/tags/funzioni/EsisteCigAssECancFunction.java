
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;


public class EsisteCigAssECancFunction extends AbstractFunzioneTag {

  public EsisteCigAssECancFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String esisteCigAssECanc = "false";
    String codgar = (String) params[1];
    String genere = (String) params[2];
    String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    
    if(codgar != null && !"".equals(codgar)) {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    
    try {

      String selectConta = "select count(*) from ( " 
    	  + " select numgara from w3gara where codgar=? and stato_simog=6 "
    	  + " union all "
    	  + " select l.numgara from w3lott l,gare g where g.codgar1=? and g.ngara=l.ngara and l.stato_simog=6 "
          + " union all "
          + " select codrich from w3smartcig where codgar=? and stato=6 )";
      if(!"ORA".equals(db)){
    	  selectConta += " as conta ";
      }
      Long conta = (Long)sqlManager.getObject(selectConta, new Object[] { codgar,codgar,codgar });
      if(conta>0){
    	  esisteCigAssECanc = "true";
      }
      
    } catch (SQLException e) {
        throw new JspException("Errore durante la conta delle richieste CIG in stato annullato per la gara " + codgar, e);
    }
    }

    return esisteCigAssECanc;
  }

}
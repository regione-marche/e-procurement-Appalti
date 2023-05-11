/*
 * Created on 18-nov-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il campo NOMIMO di DITG. Questa function viene definita
 * perchè, nella scheda v_gcap_dpre.jsp non è possibile sapere la ragione
 * sociale della ditta
 * 
 * @author Marcello Caminiti
 */
public class GetNOMIMOFunction extends AbstractFunzioneTag {

  public GetNOMIMOFunction() {
    super(4, new Class[] { PageContext.class,String.class,String.class,String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
	  
    // Numero gara
    String numeroGara = (String) params[1];
    
    // Codice della gara
    String codiceGara = (String) params[2];
    
    // ditta
    String ditta = (String) params[3];
    
    String result="";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      //se il codice della gara e la ditta sono nulli si determinano a partire dalla gara
    	if ((codiceGara == null || "".equals(codiceGara)) || (ditta == null || "".equals(ditta))){
    		String select="select codgar1,ditta from gare where ngara = ?";
    		Vector datiGARE = sqlManager.getVector(select,
					new Object[] { numeroGara });

			if (datiGARE != null && datiGARE.size() > 0) {
				codiceGara = ((String) ((JdbcParametro) datiGARE.get(0)).getValue());
				ditta = ((String) ((JdbcParametro) datiGARE.get(1)).getValue());
			}
    		
    	}
    	
    	
      String nomimo = (String) sqlManager.getObject(
          "select NOMIMO from DITG where CODGAR5 = ? and NGARA5 = ? and DITTAO = ?", new Object[]{codiceGara,numeroGara,ditta});
      if(nomimo != null)
    	  result = nomimo;
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della ragione sociale della ditta ", e);
    }

    return result;
  }
}

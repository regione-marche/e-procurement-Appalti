package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 20/01/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per il calcolo del numero degli eventuali avvalimenti di una ditta in gara
 *
 * @author Cristian Febas
 */
public class GetNumeroAvvalimentiDittaFunction extends AbstractFunzioneTag{
	public GetNumeroAvvalimentiDittaFunction() {
	    super(3, new Class[]{PageContext.class, String.class, String.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      String ngara = (String) params[1];
      String dittao = (String) params[2];
      String numAvvalimentiDitta = "0";

      String selectNumAvvDitta = "select count(*) from ditgavval where ngara = ? and dittao = ? ";

      try {
        Long nAvvalimentiDitta = (Long) sqlManager.getObject(selectNumAvvDitta, new Object[]{ngara,dittao});
  	    if (nAvvalimentiDitta!= null){
  	        numAvvalimentiDitta = nAvvalimentiDitta.toString();
  	    }

	  } catch (SQLException e) {
	          throw new JspException(
	              "Errore durante la lettura nel numero degli avvalimenti della ditta !", e);
	  }

	    return numAvvalimentiDitta;
	}

}

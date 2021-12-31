package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 29/06/15
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
 * Funzione per calcolare la presenza ed il numero Albi Componenti Commissione
 *
 * @author C.F.
 */
public class GetNumAlbiComponentiCommissioneFunction extends AbstractFunzioneTag{
	public GetNumAlbiComponentiCommissioneFunction() {
	    super(1, new Class[]{PageContext.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {

		  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
		      "sqlManager", pageContext, SqlManager.class);

		  String select = "select count(*) from commalbo";
		  Long NumAlbiComponentiCommissione = null;
		  String numACCstr = "0";

          try {
            NumAlbiComponentiCommissione = (Long) sqlManager.getObject(select,null);
		  } catch (SQLException e) {
		    throw new JspException("Errore nel conteggio degli albi componenti commissione", e);
		  }
		  if(NumAlbiComponentiCommissione != null){
		    numACCstr = NumAlbiComponentiCommissione.toString();
		  }
          return numACCstr;
		}
}

package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 06/06/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per caricare la lista degli scaglioni ANAC
 *
 * @author Marcello Caminiti
 */
public class GetListaScaglioniAnacFunction extends AbstractFunzioneTag{
	public GetListaScaglioniAnacFunction() {
	    super(3, new Class[]{PageContext.class, String.class, String.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {

	    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean(
		      "pgManagerEst1", pageContext, PgManagerEst1.class);

	    String tabellato = (String) params[1];
	    String nomeLista=(String) params[2];
		try {
		  List listaScaglioni = pgManagerEst1.getScaglioni(tabellato);
	      this.getRequest().setAttribute(nomeLista, listaScaglioni);
		} catch (SQLException e) {
		    throw new JspException("Errore nell'estrarre la lista degli scaglioni del tabellato A1z01", e);
		}

		return null;
	}
}

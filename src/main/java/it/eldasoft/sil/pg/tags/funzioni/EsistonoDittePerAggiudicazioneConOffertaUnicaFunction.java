/*
 * Created on 24/nov/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Function per le gare a lotti con offerta unica per determinare se per almeno
 * una ditta, nei diversi lotti, e' stato valorizzato il campi DITG.STAGGI (che
 * sta a significare che in almeno un lotto e' stato eseguito almeno il calcolo
 * soglia anomalia)
 * 
 * @author Luca.Giacomazzo
 */
public class EsistonoDittePerAggiudicazioneConOffertaUnicaFunction extends
		AbstractFunzioneTag {

	public EsistonoDittePerAggiudicazioneConOffertaUnicaFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}

	public String function(PageContext pageContext, Object[] params)
			throws JspException {
		
		String esito = "false";
    String codiceGara = (String) params[1];

    GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
    		"geneManager", pageContext, GeneManager.class);

    long numeroOccorrenze = geneManager.countOccorrenze("DITG",
    		"DITG.CODGAR5 = ? and DITG.STAGGI > 1",	new Object[]{codiceGara});
    if(numeroOccorrenze > 0)
    	esito = "true";

    return esito;
	}

}

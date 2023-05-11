/*
 * Created on 12/ago/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che ritorna true se la gara in input contiene almeno una ditta selezionata mediante elenco, false altrimenti.
 *
 * @author Stefano.Sabbadin
 */
public class IsGaraConDitteDaElencoFunction extends AbstractFunzioneTag {

  public IsGaraConDitteDaElencoFunction() {
    super(2, new Class[] {PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    Boolean risultato = false;
    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager", pageContext, GeneManager.class);

    if (ngara != null) {
      long numDitteDaElenco = geneManager.countOccorrenze("DITG", "NGARA5=?", new Object[] {ngara });
      risultato = numDitteDaElenco > 0;
    }
    return risultato.toString();
  }

}

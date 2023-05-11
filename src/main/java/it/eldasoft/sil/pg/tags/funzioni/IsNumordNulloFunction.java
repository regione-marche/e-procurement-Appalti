/*
 * Created on 04/may/2015
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
 * Funzione che ritorna true se per una tipologia di documentazione di una gara 
 * il campo 'Numord' nullo, false altrimenti.
 * 
 * @author Francesco.DiMattei
 */
public class IsNumordNulloFunction extends AbstractFunzioneTag {

  public IsNumordNulloFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    Long gruppo = new Long((String) params[2]); 
    Boolean risultato = false;
    long numordNullo = 0;
    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager", pageContext, GeneManager.class);

    if (codgar != null) {
      if (gruppo != null) {
        numordNullo = geneManager.countOccorrenze("DOCUMGARA", "CODGAR=? AND GRUPPO = ? AND NUMORD is NULL", new Object[] {codgar, gruppo});
      }
      risultato = numordNullo > 0;
    }
    return risultato.toString();
  }

}

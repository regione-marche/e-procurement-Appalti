/*
 * Created on 12/mag/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per caricare la property per la paginazione della lista "Operazioni
 * di ritiro plichi"
 * 
 * @author Marcello Caminiti
 */
public class GetPaginazioneListaFunction extends AbstractFunzioneTag {

  private static final String PROP_PAGINAZIONE = "it.eldasoft.sil.pg.ritiro.paginazione";

  public GetPaginazioneListaFunction() {
    super(1, new Class[] { PageContext.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String property = ConfigManager.getValore(PROP_PAGINAZIONE);
    Integer elementiPerPagina = new Integer(20);

    if (property != null && !property.equals("")){
      elementiPerPagina = Integer.valueOf(property);
    }

    pageContext.setAttribute("elementiPerPagina", elementiPerPagina,
        PageContext.REQUEST_SCOPE);

    return null;
  }
}
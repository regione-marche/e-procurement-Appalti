/*
 * Created on 18-06-2015
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
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per l'inizializzazione della lista 'Cauzione provvisoria ditte'
 *
 * @author Cristian Febas
 */
public class GestioneListaCauzioneDitteFunction extends AbstractFunzioneTag {



  public GestioneListaCauzioneDitteFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String updateLista = UtilityTags.getParametro(pageContext,
    		UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, updateLista,
        PageContext.REQUEST_SCOPE);

    return null;
  }



}
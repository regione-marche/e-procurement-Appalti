/*
 * Created on 07/11/14
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
 * Viene letto il valore del campo radio button "filtroDocumentazione",
 * ed in base al suo valore viene valorizzata la variabile "tipoDoc"
 *
 * @author Marcello Caminiti
 */
public class InizializzazionePaginaCriteriFunction extends AbstractFunzioneTag {

  public InizializzazionePaginaCriteriFunction(){
    super(1, new Class[]{PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String paginaCriterio = UtilityTags.getParametro(pageContext,"paginaCriterio");
    if(paginaCriterio == null || "".equals(paginaCriterio)){
      pageContext.setAttribute("tipoCriterio", new Long(1), PageContext.REQUEST_SCOPE);

    }else if("1".equals(paginaCriterio)){
      pageContext.setAttribute("tipoCriterio", new Long(1), PageContext.REQUEST_SCOPE);
    }else if("2".equals(paginaCriterio)){
      pageContext.setAttribute("tipoCriterio", new Long(2), PageContext.REQUEST_SCOPE);
    }else if("3".equals(paginaCriterio)){
      pageContext.setAttribute("tipoCriterio", new Long(3), PageContext.REQUEST_SCOPE);
    }

    return null;
  }

}
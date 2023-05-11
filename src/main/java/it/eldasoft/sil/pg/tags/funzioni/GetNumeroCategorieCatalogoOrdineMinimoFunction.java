/*
 * Created on 17/09/14
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che esegue il conteggio di occorrenze di OPES
 * con ordine minimo valorizzato per l'elenco
 *
 * @author Marcello Caminiti
 */
public class GetNumeroCategorieCatalogoOrdineMinimoFunction extends AbstractFunzioneTag {

  public GetNumeroCategorieCatalogoOrdineMinimoFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codiceElenco = (String) GeneralTagsFunction.cast("string", params[1]);
    String select="";
    String ret="0";

    select="select count(ngara3) from opes where ngara3=? and ordmin is not null";

    try {
      Long conteggio = (Long)sqlManager.getObject(select, new Object[]{codiceElenco});
      if(conteggio!= null && conteggio.longValue()>0)
        ret=conteggio.toString();
    } catch (SQLException e) {
      throw new JspException(
          "Errore nel conteggio di occorrenze di OPES con ordine minimo valorizzato per l'elenco " + codiceElenco, e);
    }


    return ret;

  }

}

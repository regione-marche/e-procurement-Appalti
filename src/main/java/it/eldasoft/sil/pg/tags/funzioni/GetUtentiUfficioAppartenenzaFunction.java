/*
 * Created on 05/feb/2015
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetUtentiUfficioAppartenenzaFunction extends AbstractFunzioneTag {

  public GetUtentiUfficioAppartenenzaFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);

      String su = profilo.getUfficioAppartenenza();
      Long syscon = new Long(profilo.getId());


      List<?> listaUtentiUfficioAppartenenza = sqlManager.getListVector(
          "select syscon,syslogin,sysute from usrsys where sysuffapp = " +
          "(select sysuffapp from usrsys where syscon= ? )" +
          " order by syscon",
          new Object[] { syscon });
      listaUtentiUfficioAppartenenza.add(null);

      pageContext.setAttribute("listaUtentiUfficioAppartenenza", listaUtentiUfficioAppartenenza, PageContext.REQUEST_SCOPE);


      String updateLista = UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
       if (updateLista == null || updateLista.length() == 0){
         updateLista = "0";
       }
       pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, updateLista,PageContext.REQUEST_SCOPE);


    } catch (SQLException e) {
      throw new JspException(
          "Errore nell'estrazione dei dati per la popolazione della/e dropdown list",
          e);
    }
    return null;
  }

}

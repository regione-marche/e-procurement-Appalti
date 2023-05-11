/*
 * Created on 18-08-2015
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione inizializza la pagina gare-popup-sorteggioDitteInviti.jsp
 *
 *
 */
public class InizializzaziPaginaSorteggioDitteInvitiFunction extends AbstractFunzioneTag {

  public InizializzaziPaginaSorteggioDitteInvitiFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    // Codice della gara
    String ngara = (String) params[1];
    String codgar = (String) params[2];
    String result="1";
    try {
      Long numope = (Long)sqlManager.getObject("select numope from torn where codgar=?", new Object[]{codgar});
      pageContext.setAttribute("numope", numope, PageContext.REQUEST_SCOPE);

      //Numero di ditte in gara su cui effettuare il sorteggio
      Long numDitte=(Long)sqlManager.getObject("select count(dittao) from ditg where ngara5=? and (fasgar is null or fasgar >= -3) and (acquisizione is null or acquisizione <> 8)", new Object[]{ngara});
      pageContext.setAttribute("numDitte", numDitte, PageContext.REQUEST_SCOPE);
      if(numDitte.longValue()==0){
        result ="-1";
      }else{

        //Esistono ditte già invitate
        Long numDitteInvitate=(Long)sqlManager.getObject("select count(dittao) from ditg where ngara5=? and (fasgar is null or fasgar >= -3) and sortinv=?", new Object[]{ngara,"1"});
        if(numDitteInvitate!=null && numDitteInvitate.longValue()>0)
          result ="-2";
      }
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei dati della gara " + ngara + " per inizializzare la funzione di sorteggio ditte per invito", e);
    }


    return result;
  }

}

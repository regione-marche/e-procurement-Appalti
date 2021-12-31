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
 * Funzione inizializza la pagina gare-popup-sorteggioDitteVerificaRequisiti.jsp
 *
 * @author Cristian Febas
 */
public class InizializzaziPaginaSorteggioDitteVerificaRequisitiFunction extends AbstractFunzioneTag {

  public InizializzaziPaginaSorteggioDitteVerificaRequisitiFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    // Codice della gara
    String ngara = (String) params[1];
    String percentualeIniziale = (String) params[2];
    String result="1";
    try {
      //Numero di ditte in gara su cui effettuare il sorteggio
      Long numDitte=(Long)sqlManager.getObject("select count(dittao) from ditg where ngara5=? and (fasgar is null or fasgar >=5)", new Object[]{ngara});
      pageContext.setAttribute("numDitte", numDitte, PageContext.REQUEST_SCOPE);
      if(numDitte.longValue()==0){
        pageContext.setAttribute("numDitteSorteggiate", new Long(0), PageContext.REQUEST_SCOPE);
        result ="-1";
      }else{

        //Calcolo della percentuale del numero ditte da sorteggiare
        Long percentuale = new Long(percentualeIniziale);
        long numditteSorteggio = (long)Math.ceil(numDitte.longValue() * percentuale.longValue() * 0.01);
        pageContext.setAttribute("numDitteSorteggiate", new Long(numditteSorteggio), PageContext.REQUEST_SCOPE);

        //Esistono ditte già sorteggiate
        Long numDitteSorteggiate=(Long)sqlManager.getObject("select count(dittao) from ditg where ngara5=? and (fasgar is null or fasgar >=2) and estimp=?", new Object[]{ngara,"1"});
        if(numDitteSorteggiate!=null && numDitteSorteggiate.longValue()>0)
          result ="-2";
      }
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei dati della gara " + ngara + " per inizializzare la funzione di sorteggio ditte per verifica requisiti", e);
    }


    return result;
  }

}

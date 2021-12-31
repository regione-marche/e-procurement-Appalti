/*
 * Created on 067/10/11
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
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene costruito il filtro sulle comunicazioni soccorso istruttorio
 *
 * @author Marcello Caminiti
 */
public class GetFiltroSoccorsoIstruttorioFunction extends AbstractFunzioneTag {

  public GetFiltroSoccorsoIstruttorioFunction(){
    super(2, new Class[]{PageContext.class,String.class});
  }


  /**
   * Viene costruito il filtro per le comunicazioni da leggere.
   * Il secondo parametro indica il tipo di operazione (count\sel).
   * Il secondo parametro indica il tipo di profilo, i valori sono:
   *          1 - Gare
   *          2 - Elenchi
   *          3 - Cataloghi
   *          4 - Ricerche di mercato
   *          5 - Avvisi
   *          6 - Protocollo
   *          7 - Affidamenti
   *          8 -
   *          9 - Ordini NSO
   */
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result="";
    String profilo = (String) params[1];

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    result += pgManagerEst1.getFiltroComunicazioniSoccorsoIstruttorio(profilo);

   return result;
  }

}
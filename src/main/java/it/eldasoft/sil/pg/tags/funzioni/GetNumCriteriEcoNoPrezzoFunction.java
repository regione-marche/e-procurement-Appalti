/*
 * Created on 28/11/2017
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
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

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per estrarre il numero di criteri economici non di tipo prezzo.
 *
 * @author M. Caminiti
 */
public class GetNumCriteriEcoNoPrezzoFunction extends AbstractFunzioneTag {

  public GetNumCriteriEcoNoPrezzoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);

    String gara = (String) params[1];
    Long conteggio = null;

    try {
      conteggio = pgManagerEst1.getNumCriteriEcoNoPrezzo(gara);
      if(conteggio==null )
        conteggio=new Long(0);
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio dei criteri economici non di tipo prezzo per la gara " + gara, e);
    }

    return conteggio.toString();
  }

}

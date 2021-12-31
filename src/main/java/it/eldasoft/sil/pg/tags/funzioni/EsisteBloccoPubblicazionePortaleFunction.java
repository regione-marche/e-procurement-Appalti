/*
 * Created on 11/10/10
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

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se è stata eseguita la pubblicazione su portale Alice
 *
 * @author Marcello Caminiti
 */
public class EsisteBloccoPubblicazionePortaleFunction extends AbstractFunzioneTag {

  public EsisteBloccoPubblicazionePortaleFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String chiaveGara = (String) params[1];
    String tipologiaPubblicazione= (String) params[2];
    String tuttiLotti = (String) params[3];
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String pubblicazioneSuPortale = "FALSE";
    try {
      boolean controlloTuttiLotti=false;
      if("true".equals(tuttiLotti))
        controlloTuttiLotti=true;
      pubblicazioneSuPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(chiaveGara, tipologiaPubblicazione, controlloTuttiLotti);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante i controlli per determinare se la gara è pubblicata sul portale web", e);
    }
    return pubblicazioneSuPortale;

  }

}

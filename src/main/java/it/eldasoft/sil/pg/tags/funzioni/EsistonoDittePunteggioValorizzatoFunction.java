/*
 * Created on 11/07/17
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

public class EsistonoDittePunteggioValorizzatoFunction extends AbstractFunzioneTag {

  public EsistonoDittePunteggioValorizzatoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String tipo = (String) params[2];
    String esito = "no";

    if (ngara != null) {
      PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);
      if("1".equals(tipo))
        tipo="TEC";
      else
        tipo="ECO";
      try {
        boolean controlloPunteggio= pgManagerEst1.esistonoDittePunteggioValorizzato(ngara, tipo);
        if(controlloPunteggio)
          esito = "si";
      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo dei punteggi delle ditte", e);
      }
    }

    return esito;
  }

}
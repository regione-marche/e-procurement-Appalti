/*
 * Created on 26/06/18
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

public class EsistonoComunicazioniFunction extends AbstractFunzioneTag {

  public EsistonoComunicazioniFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String tipoOfferta = (String) params[2];

    String esistonoComunicazioni = "false";

    if (ngara != null) {
      PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);

      try {
       if(pgManagerEst1.esistonoComunicazioni(ngara, tipoOfferta))
          esistonoComunicazioni = "true";
      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo della presenza di comunicazioni per la gara", e);
      }
    }

    return esistonoComunicazioni;
  }

}
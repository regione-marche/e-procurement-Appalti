/*
 * Created on 12/06/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

public class EsistonoDitteAmmissioneNullaFunction extends AbstractFunzioneTag {

  public EsistonoDitteAmmissioneNullaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String step = (String) params[2];
    Long numope = null;

    String esistonoDitteAmmissioneNulla = "false";

    if (ngara != null && step!=null && !"".equals(step)) {
      PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);
      try {
        HashMap<String, Object> ret = pgManagerEst1.esistonoDitteConAmmissionePariA(ngara, step, null, true, false);
        if (ret != null) {
          Boolean esitoControllo = (Boolean)ret.get("esistonoDitteAmmissione");
          esistonoDitteAmmissioneNulla = esitoControllo.toString();
          if(ret.containsKey("numope"))
            numope = (Long)ret.get("numope");
          pageContext.setAttribute("numope",numope,PageContext.REQUEST_SCOPE);
        }
      } catch (Exception e) {
        throw new JspException("Errore durante il controllo del campo ammissione delle ditte della gara", e);
      }
    }

    return esistonoDitteAmmissioneNulla;
  }

}